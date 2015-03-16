import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

class Player {

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
		
		public void addAdjacentNode(Node adjacentNode) {
			adjacentNodes.add(adjacentNode);
		}
		
		public void removeAdjacentNode(Node adjacentNode) {
			adjacentNodes.remove(adjacentNode);
		}
		
		public int getDegree() {
			return adjacentNodes.size();
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
		
		private final int length;
		private final Node node;
		private final Optional<Path> subpath;
		
		private Path(Node node) {
			this.node = node;
			this.subpath = Optional.empty();
			this.length = 0;
		}
		
		private Path(Node node, Path subpath) {
			this.node = Objects.requireNonNull(node);
			this.subpath = Optional.of(subpath);
			this.length = subpath.getLength() + 1;
		}		
		
		public int getLength() {
			return length;
		}
		
		public Node getNode() {
			return node;
		}
		
		public Optional<Path> getSubpath() {
			return subpath;
		}
		
		public Path reverse() {
		
			LinkedList<Path> stack = new LinkedList<>();
			stack.push(new Path(this.getNode()));
			
			Optional<Path> currentPath = this.getSubpath();
			while (currentPath.isPresent()) {
				stack.push(new Path(currentPath.get().getNode(), stack.peek()));
				currentPath = currentPath.get().getSubpath();
			}
			
			return stack.peek();
		}
		
	}
	
	private static class PathToGateway {
		
		public static final Comparator<PathToGateway> COMPARE_BY_ASCENDING_PATH_LENGTH_THEN_DESCENDING_GATEWAY_DEGREE = Comparator.comparing(PathToGateway::getPathLength).thenComparing(Comparator.comparing(PathToGateway::getGatewayDegree).reversed());
		
		private final Path path;
		private final Node gateway;
		
		private PathToGateway(Path path, Node gateway) {
			this.path = Objects.requireNonNull(path);
			this.gateway = Objects.requireNonNull(gateway);
		}
		
		public int getPathLength() {
			return path.getLength();
		}
		
		public int getGatewayDegree() {
			return gateway.getDegree();
		}
		
		public Link getFirstLink() {
			return new Link(path.getNode(), path.getSubpath().get().getNode());
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
        
        Node[] gateways = new Node[E];
        for (int i = 0; i < E; i++) {
            int EI = in.nextInt(); // the index of a gateway node
            gateways[i] = graph[EI];
        }
        
        // game loop
        while (true) {
            
        	int SI = in.nextInt(); // The index of the node on which the Skynet agent is positioned this turn

        	Link link = findLinkToSevere(graph[SI], gateways);
            severeLink(link);
            removeLink(link);
        }
        
    }
	
	private static Link findLinkToSevere(Node skynetAgentNode, Node[] gateways) {
		PathToGateway potentialPathToGateway = findPotentialPathToGateway(skynetAgentNode, gateways);
		return potentialPathToGateway.getFirstLink();
	}
	
	private static PathToGateway findPotentialPathToGateway(Node skynetAgentNode, Node[] gateways) {
		return findShortestPathToGateways(skynetAgentNode, gateways).min(PathToGateway.COMPARE_BY_ASCENDING_PATH_LENGTH_THEN_DESCENDING_GATEWAY_DEGREE).orElseThrow(() -> new IllegalStateException("Skynet agent is screwed but it doesn't know, we win!"));
	}
	
	private static Stream<PathToGateway> findShortestPathToGateways(Node skynetAgentNode, Node[] gateways) {
		return Arrays.stream(gateways).map(gateway -> findShortestPathToGateway(skynetAgentNode, gateway)).filter(Optional::isPresent).map(Optional::get);
	}
	
	private static Optional<PathToGateway> findShortestPathToGateway(Node skynetAgentNode, Node gateway) {
		
		Set<Node> visitedNodes = new HashSet<>();
		Queue<Path> queue = new LinkedList<>();
		queue.offer(new Path(skynetAgentNode));
		
		while (!queue.isEmpty()) {
			Path currentPath = queue.poll();
			visitedNodes.add(currentPath.getNode());
			for (Node adjacentNode : currentPath.getNode().getAdjacentNodes()) {
				if (!visitedNodes.contains(adjacentNode)) {
					if (adjacentNode.equals(gateway)) {
						Path pathFromSkynetAgentToGateway = new Path(adjacentNode, currentPath).reverse();
						return Optional.of(new PathToGateway(pathFromSkynetAgentToGateway, gateway));
					} else {
						queue.offer(new Path(adjacentNode, currentPath));						
					}
				}
			}
		}
		
		return Optional.empty();
	}
	
	private static void severeLink(Link link) {
		System.out.println(link.getNode1().getId() + " " + link.getNode2().getId());
	}
	
	private static void removeLink(Link link) {
		link.getNode1().removeAdjacentNode(link.getNode2());
		link.getNode2().removeAdjacentNode(link.getNode1());
	}
	
}