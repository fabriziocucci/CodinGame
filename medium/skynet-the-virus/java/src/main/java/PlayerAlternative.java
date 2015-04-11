import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

class PlayerAlternative {

	private static class Node {
		
		private final int id;
		private final Set<Node> adjacentNodes;
		
		private boolean isGateway = false;
		
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
		
		public boolean isGateway() {
			return isGateway;
		}

		public void markAsGateway() {
			this.isGateway = true;
		}
		
		public void addAdjacentNode(Node adjacentNode) {
			adjacentNodes.add(adjacentNode);
		}
		
		public void removeAdjacentNode(Node adjacentNode) {
			adjacentNodes.remove(adjacentNode);
		}
		
		public int getDegreeWithoutGateways() {
			return (int) adjacentNodes.stream().filter(n -> !n.isGateway()).count();
		}
		
		public int getSumOfAdjacentNodesDegree() {
			return adjacentNodes.stream().mapToInt(Node::getDegreeWithoutGateways).sum();
		}
		
		public Node getAdjacentNodeWithMaxDegree() {
			return adjacentNodes.stream().filter(n -> !n.isGateway()).max((n1, n2) -> n1.getDegreeWithoutGateways() - n2.getDegreeWithoutGateways()).get();
		}
		
	}
	
	private static class Link {
		
		private final Node node1;
		private final Node node2;
		
		private Link(Node node1, Node node2) {
			this.node1 = node1;
			this.node2 = node2;
		}
		
		public Node getNode1() {
			return node1;
		}
		
		public Node getNode2() {
			return node2;
		}
		
	}
	
	private static class Path {
		
		private final Node currentNode;
		private final Path previousPath;
		
		private Path(Node currentNode, Path previousPath) {
			this.currentNode = currentNode;
			this.previousPath = previousPath;
		}		
		
		public Node getNode() {
			return currentNode;
		}
		
		public Path getPreviousPath() {
			return previousPath;
		}
		
		public Path reverse() {
		
			LinkedList<Path> stack = new LinkedList<>();
			
			Path currentPath = this;
			while (currentPath != null) {
				stack.push(new Path(currentPath.getNode(), stack.peek()));
				currentPath = currentPath.getPreviousPath();
			}
			
			return stack.peek();
		}
		
	}
	
    public static void main(String args[]) {
        
    	Scanner in = new Scanner(System.in);
        
    	int N = in.nextInt(); // the total number of nodes in the level, including the gateways
        int L = in.nextInt(); // the number of links
        int E = in.nextInt(); // the number of exit gateways
        
        Node[] graph = new Node[N];
        for (int i = 0; i < L; i++) {
        	
        	int N1 = in.nextInt(); // N1 and N2 defines a link between these nodes
            if (graph[N1] == null) graph[N1] = new Node(N1); 
            
            int N2 = in.nextInt(); // N1 and N2 defines a link between these nodes
            if (graph[N2] == null) graph[N2] = new Node(N2);
            
            graph[N1].addAdjacentNode(graph[N2]);
            graph[N2].addAdjacentNode(graph[N1]);
        
        }
        
        for (int i = 0; i < E; i++) {
            int EI = in.nextInt(); // the index of a gateway node
            graph[EI].markAsGateway();
        }
        
        PriorityQueue<Node> minHeap = new PriorityQueue<>(N, (n1, n2) -> {
        	
        	int n1DegreeWithoutGateways = n1.getDegreeWithoutGateways();
        	int n2DegreeWithoutGateways = n2.getDegreeWithoutGateways();
        	
        	if (n1DegreeWithoutGateways == n2DegreeWithoutGateways) {
        		return n2.getSumOfAdjacentNodesDegree() - n1.getSumOfAdjacentNodesDegree();
        	}
        	
        	return n1DegreeWithoutGateways - n2DegreeWithoutGateways;
        });
        
        minHeap.addAll(Arrays.asList(graph));
        
        // game loop
        while (true) {
            
        	int SI = in.nextInt(); // The index of the node on which the Skynet agent is positioned this turn

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
        	Link link = findLinkToSevere(graph[SI], graph, minHeap);
            System.out.println(link.getNode1().getId() + " " + link.getNode2().getId()); // Example: 0 1 are the indices of the nodes you wish to sever the link between
            removeSeveredLink(link, graph, minHeap);
        }
        
    }

	private static Link findLinkToSevere(Node skynetAgentNode, Node[] graph, PriorityQueue<Node> minHeap) {
		return getLinkToGateway(skynetAgentNode).orElseGet(() -> getBestLink(minHeap));
	}

	private static Optional<Link> getLinkToGateway(Node skynetAgentNode) {
		for (Node adjacentNode : skynetAgentNode.getAdjacentNodes()) {
			if (adjacentNode.isGateway()) {
				return Optional.of(new Link(skynetAgentNode, adjacentNode));
			}
		}
		return Optional.empty();
	}
    
	private static Link getBestLink(PriorityQueue<Node> minHeap) {
		Node minNode; 
		do {
			minNode = minHeap.poll();
		} while (minNode.isGateway() || minNode.getDegreeWithoutGateways() == 1);
		Node adjacentNodeWithMaxDegree = minNode.getAdjacentNodeWithMaxDegree();
		minHeap.remove(adjacentNodeWithMaxDegree);
		return new Link(minNode, adjacentNodeWithMaxDegree);
	}
	
	private static void removeSeveredLink(Link link, Node[] graph, PriorityQueue<Node> minHeap) {
		link.getNode1().removeAdjacentNode(link.getNode2());
		link.getNode2().removeAdjacentNode(link.getNode1());
		
		minHeap.offer(link.getNode1());
		minHeap.offer(link.getNode2());
	}
	
//	private static Link findLinkToSevere(Node skynetAgentNode, Node[] graph) {
//		Path shortestPathToGateway = findShortestPathToGateway(skynetAgentNode);
//		if (shortestPathToGateway.getPreviousPath().getPreviousPath() != null) {
//			return new Link(shortestPathToGateway.getPreviousPath().getNode(), shortestPathToGateway.getPreviousPath().getPreviousPath().getNode());
//		} else {
//			return new Link(shortestPathToGateway.getNode(), shortestPathToGateway.getPreviousPath().getNode());
//		}
//	}
//	
//	private static Path findShortestPathToGateway(Node skynetAgentNode) {
//		
//		Set<Node> visited = new HashSet<>();
//		Queue<Path> queue = new LinkedList<>();
//		queue.offer(new Path(skynetAgentNode, null));
//		
//		while (!queue.isEmpty()) {
//			Path currentPath = queue.poll();
//			visited.add(currentPath.getNode());
//			if (currentPath.getNode().isGateway()) {
//				return currentPath;
//			}
//			for (Node adjacentNode : currentPath.getNode().getAdjacentNodes()) {
//				if (!visited.contains(adjacentNode)) {
//					queue.offer(new Path(adjacentNode, currentPath));
//				}
//			}
//		}
//		
//		throw new IllegalStateException("The Skynet agent is unable to reach any gateway, we win!");
//	}
//	
//	private static void removeSeveredLink(Link link, Node[] graph) {
//		link.getNode1().removeAdjacentNode(link.getNode2());
//		link.getNode2().removeAdjacentNode(link.getNode1());
//	}
	
}