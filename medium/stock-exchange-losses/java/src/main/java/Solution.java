import java.util.Arrays;
import java.util.Scanner;

class Solution {

    public static void main(String args[]) {
        
    	Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        in.nextLine();
        String vs = in.nextLine();
        
        int[] stockValues = parseStockValues(vs);
        System.out.println(findMaximalLoss(stockValues));
        
    }
    
    private static int[] parseStockValues(String stockValuesAsString) {
    	return Arrays.stream(stockValuesAsString.split("\\s")).mapToInt(Integer::parseInt).toArray();
    }
    
    private static int findMaximalLoss(int[] stockValues) {
    	int maximalLoss = 0, lastPeakIndex = 0;
		for (int i = lastPeakIndex + 1; i < stockValues.length; i++) {
			int lossOrGainFromLastPeak = stockValues[i] - stockValues[lastPeakIndex];
			if (lossOrGainFromLastPeak < 0 && lossOrGainFromLastPeak < maximalLoss) {
				maximalLoss = lossOrGainFromLastPeak;
			} else if (lossOrGainFromLastPeak > 0) {
				lastPeakIndex = i;
			}
		}
    	return maximalLoss;
    }
    
}