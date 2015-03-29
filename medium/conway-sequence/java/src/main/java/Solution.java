import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class Solution {

    public static void main(String args[]) {
        
    	Scanner in = new Scanner(System.in);
        int R = in.nextInt();
        int L = in.nextInt();
        
        List<Integer> sequenceLine = getSequenceLine(R, L);
        printSequenceLine(sequenceLine);
    }


	private static List<Integer> getSequenceLine(int first, int lineNumber) {
		
		List<Integer> currentLine = new ArrayList<>();
		currentLine.add(Integer.valueOf(first));
		
		for (int i = 2; i <= lineNumber; i++) {
			currentLine = getNextSequenceLine(currentLine);
		}
		
		return currentLine;
	}

	private static List<Integer> getNextSequenceLine(List<Integer> currentLine) {
		
		List<Integer> nextLine = new ArrayList<>();
		
		int startIndex = 0, currentIndex = 1;
		while (currentIndex < currentLine.size()) {
			if (currentLine.get(startIndex) != currentLine.get(currentIndex)) {
				nextLine.add(currentIndex - startIndex);
				nextLine.add(currentLine.get(startIndex));
				startIndex = currentIndex;
			}
			currentIndex++;
		}
		
		nextLine.add(currentIndex - startIndex);
		nextLine.add(currentLine.get(startIndex));		
		
		return nextLine;
	}
	
	private static void printSequenceLine(List<Integer> sequenceLine) {
		System.out.println(sequenceLine.stream().map(i -> i.toString()).collect(Collectors.joining(" ")));
	}
	
}