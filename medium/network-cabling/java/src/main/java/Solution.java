import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collector;

/**
 * WARNINING
 * 
 * According to CodingGame tests, using the Stream API with a custom {@link Collector} for calculating 
 * the {@link HousesSummaryStatistics} will lead to a sub-optimal solution from a performance prospective. 
 *
 */
class Solution {
	
	private static class House {
		
		private final int x;
		private final int y;

		private House(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
	}
	
	private static class HousesSummaryStatistics {
		
		private final int minX;
		private final int maxX;
		private final double avgY;
		
		private HousesSummaryStatistics(int minX, int maxX, double avgY) {
			this.minX = minX;
			this.maxX = maxX;
			this.avgY = avgY;
		}

		public int getMinX() {
			return minX;
		}

		public int getMaxX() {
			return maxX;
		}

		public double getAvgY() {
			return avgY;
		}
		
	}
	
    public static void main(String args[]) {
    	
    	Scanner in = new Scanner(System.in);
        int N = in.nextInt();
        
        List<House> houses = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            int X = in.nextInt();
            int Y = in.nextInt();
            houses.add(new House(X, Y));
        }
        
        System.out.println(calculateMinimumLength(houses));
    }
    
    private static long calculateMinimumLength(List<House> houses) {
    	HousesSummaryStatistics housesSummaryStatistics = calculateHousesSummaryStatiatics(houses);
    	return calculateHorizontalLength(housesSummaryStatistics) + calculateVerticalLength(houses, housesSummaryStatistics);
    }

    private static HousesSummaryStatistics calculateHousesSummaryStatiatics(List<House> houses) {
    	
    	int minX = Integer.MAX_VALUE;
    	int maxX = Integer.MIN_VALUE;
    	long sumY = 0;
    	
    	for (House house : houses) {
			if (house.getX() < minX) minX = house.getX();
			if (house.getX() > maxX) maxX = house.getX();
			sumY += house.getY();
		}
    	
    	double avgY = sumY / houses.size();
    	
    	return new HousesSummaryStatistics(minX, maxX, avgY);
    }
    
	private static long calculateHorizontalLength(HousesSummaryStatistics housesSummaryStatistics) {
		return Math.abs(housesSummaryStatistics.getMaxX() - housesSummaryStatistics.getMinX());
	}

	private static long calculateVerticalLength(List<House> houses, HousesSummaryStatistics housesSummaryStatistics) {
		int optimalY = calculateOptimalY(houses, housesSummaryStatistics.getAvgY());
		return calculateSumOfVerticalLengths(houses, optimalY);
	}

	private static int calculateOptimalY(List<House> houses, double avgY) {
		
		int optimalY = houses.get(0).getY();
    	double minDifferenceToAvg = Math.abs(optimalY - avgY);
    	
    	for (int i = 1; i < houses.size(); i++) {
    		double differenceToAvg = Math.abs(houses.get(i).getY() - avgY);
			if (differenceToAvg < minDifferenceToAvg) {
				minDifferenceToAvg = differenceToAvg;
				optimalY = houses.get(i).getY();
			}
		}
    	
    	return optimalY;
	}
    
	private static long calculateSumOfVerticalLengths(List<House> houses, int optimalY) {
		long sumOfDifferenceToOptimalY = 0;
    	for (House house : houses) {
    		sumOfDifferenceToOptimalY += Math.abs(house.getY() - optimalY);
		}
    	return sumOfDifferenceToOptimalY;
	}
	
}