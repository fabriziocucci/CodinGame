import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

class Solution {

	private static class Graph {
	
		private final Map<Integer, Node> idToNode;

		private Graph() {
			this.idToNode = new HashMap<>();
		}
		
		public Node getNode(int nodeId) {
			return idToNode.get(nodeId);
		}
		
		public Set<Node> getLeaves() {
			return idToNode.values().stream().filter(Node::isLeaf).collect(Collectors.toSet());
		}
		
		public void addDirectedEdge(int from, int to) {
			Node fromNode = idToNode.computeIfAbsent(from, Node::new);
			Node toNode = idToNode.computeIfAbsent(to, Node::new);
			fromNode.addDirectedEdge(toNode);
		}
		
		public Graph reverse() {
			Graph reversedGraph = new Graph();
			for (Map.Entry<Integer, Node> entry : this.idToNode.entrySet()) {
				for (Node adjacentNode : entry.getValue().getAdjacentNodes()) {
					reversedGraph.addDirectedEdge(adjacentNode.getId(), entry.getKey().intValue());
				}
			}
			return reversedGraph;
		}
		
	}
	
	private static class Node {
		
		private final int id;
		private final Set<Node> adjacentNodes;
		
		private Node(int id) {
			this.id = id;
			this.adjacentNodes = new HashSet<>();
		}
		
		public int getId() {
			return id;
		}
		
		public Set<Node> getAdjacentNodes() {
			return adjacentNodes;
		}
		
		public boolean isLeaf() {
			return adjacentNodes.size() == 0;
		}
		
		public void addDirectedEdge(Node toNode) {
			adjacentNodes.add(toNode);
		}
		
	}
	
	private static class Path {
		
		private final Node terminalNode;
		private final int length;
		
		private Path(Node terminalNode, int length) {
			this.terminalNode = terminalNode;
			this.length = length;
		}
		
		public Node getTerminalNode() {
			return terminalNode;
		}
		
		public int getLength() {
			return length;
		}
		
	}
	
    public static void main(String args[]) {
    	
        Scanner in = new Scanner(System.in);
    	int n = in.nextInt(); // the number of relationships of influence
        
        Graph graph = new Graph();
        for (int i = 0; i < n; i++) {
            int x = in.nextInt(); // a relationship of influence between two people (x influences y)
            int y = in.nextInt();
            graph.addDirectedEdge(x, y);
        }
    
        System.out.println(findLongestSuccessionOfInfluences(graph)); // The number of people involved in the longest succession of influences
    }

	private static int findLongestSuccessionOfInfluences(Graph graph) {		
		return findLongestPathFromLeaves(graph.reverse(), graph.getLeaves());
	}
    
	private static int findLongestPathFromLeaves(Graph graph, Set<Node> leaves) {
    	return leaves.stream().mapToInt(leaf -> findLongestPathFromLeaf(graph, leaf.getId())).max().orElseThrow(() -> new IllegalStateException("Unable to find longest path from leaves."));
    }
	
    private static int findLongestPathFromLeaf(Graph graph, int leafId) {
    	
    	Path currentPath = new Path(graph.getNode(leafId), 1);
    	
    	Queue<Path> queue = new LinkedList<>();
    	queue.offer(currentPath);
    	
    	while (!queue.isEmpty()) {
    		currentPath = queue.poll();
    		for (Node node : currentPath.getTerminalNode().getAdjacentNodes()) {
    			queue.offer(new Path(node, currentPath.getLength() + 1));
    		}
    	}
    	
    	return currentPath.getLength();
    }
    
}