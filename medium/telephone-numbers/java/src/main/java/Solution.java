import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Solution {

	private static class Trie {
		
		private class Node {
			
			private final int value;
			private final Map<Integer, Node> children;
			
			private Node(int value) {
				this.value = value;
				this.children = new HashMap<>();
				Trie.this.nodesCount++;
			}
			
			public void add(int[] telephoneNumber, int index) {
				if (index < telephoneNumber.length) {
					children.computeIfAbsent(telephoneNumber[index], key -> new Node(key)).add(telephoneNumber, index + 1);
				}
			}
			
		}
		
		private int nodesCount;
		private final Node root;

		private Trie() {
			this.nodesCount = 0;
			this.root = new Node(-1);
		}
		
		public int getNodesCount() {
			return nodesCount - 1;
		}
		
		public void add(int[] telephoneNumber) {
			root.add(telephoneNumber, 0);
		}
		
	}
	
	
	
    public static void main(String args[]) {
    	
        Scanner in = new Scanner(System.in);
        int N = in.nextInt();
        
        Trie trie = new Trie();
        for (int i = 0; i < N; i++) {
            String telephone = in.next();
            trie.add(parseTelephoneNumber(telephone));
            
        }

        System.out.println(trie.getNodesCount()); // The number of elements (referencing a number) stored in the structure.
    }
    
    private static int[] parseTelephoneNumber(String telephoneNumber) {
    	return telephoneNumber.chars().map(Character::getNumericValue).toArray();
    }
    
}