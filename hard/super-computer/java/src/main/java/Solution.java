import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

class Solution {
	
	 public static void main(String args[]) {
        
    	List<Calculation> calculations = readCalculations();
    	Collections.sort(calculations, Comparator.comparingInt(Calculation::getEndingDay));
    	Set<Calculation> intervalSchedulingSolution = findIntervalSchedulingSolution(calculations);
    	
        System.out.println(intervalSchedulingSolution.size());
    }
    
	private static List<Calculation> readCalculations() {
    	
    	Scanner in = new Scanner(System.in);
        int N = in.nextInt();
        
        List<Calculation> calculations = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            int J = in.nextInt();
            int D = in.nextInt();
            calculations.add(new Calculation(i, J, J + D));
        }
        
        return calculations;
    }
    
	private static Set<Calculation> findIntervalSchedulingSolution(List<Calculation> calculations) {
		
		Set<Calculation> solution = new HashSet<>();
		
		int lastEndingDay = 0;
    	for (Calculation calculation : calculations) {
    		if (calculation.getStartingDay() >= lastEndingDay) {
    			solution.add(calculation);
    			lastEndingDay = calculation.getEndingDay();
    		}
    	}
    	
    	return solution;
	}
	
	private static class Calculation {
		
		private final int id;
		private final int startingDay;
		private final int endingDay;
		
		public Calculation(int id, int startingDay, int endingDay) {
			this.id = id;
			this.startingDay = startingDay;
			this.endingDay = endingDay;
		}
		
		public int getStartingDay() {
			return startingDay;
		}
		
		public int getEndingDay() {
			return endingDay;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + id;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Calculation other = (Calculation) obj;
			if (id != other.id)
				return false;
			return true;
		}
		
	}
	
}