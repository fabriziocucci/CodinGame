import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

class Solution {
	
	private static class WordPointsCalculator {
		
		private static final int[] CHAR_TO_SCORE = new int['z' - 'a' + 1];
		
		static {
			CHAR_TO_SCORE[getIndexFromChar('a')] = 1;
			CHAR_TO_SCORE[getIndexFromChar('b')] = 3;
			CHAR_TO_SCORE[getIndexFromChar('c')] = 3;
			CHAR_TO_SCORE[getIndexFromChar('d')] = 2;
			CHAR_TO_SCORE[getIndexFromChar('e')] = 1;
			CHAR_TO_SCORE[getIndexFromChar('f')] = 4;
			CHAR_TO_SCORE[getIndexFromChar('g')] = 2;
			CHAR_TO_SCORE[getIndexFromChar('h')] = 4;
			CHAR_TO_SCORE[getIndexFromChar('i')] = 1;
			CHAR_TO_SCORE[getIndexFromChar('j')] = 8;
			CHAR_TO_SCORE[getIndexFromChar('k')] = 5;
			CHAR_TO_SCORE[getIndexFromChar('l')] = 1;
			CHAR_TO_SCORE[getIndexFromChar('m')] = 3;
			CHAR_TO_SCORE[getIndexFromChar('n')] = 1;
			CHAR_TO_SCORE[getIndexFromChar('o')] = 1;
			CHAR_TO_SCORE[getIndexFromChar('p')] = 3;
			CHAR_TO_SCORE[getIndexFromChar('q')] = 10;
			CHAR_TO_SCORE[getIndexFromChar('r')] = 1;
			CHAR_TO_SCORE[getIndexFromChar('s')] = 1;
			CHAR_TO_SCORE[getIndexFromChar('t')] = 1;
			CHAR_TO_SCORE[getIndexFromChar('u')] = 1;
			CHAR_TO_SCORE[getIndexFromChar('v')] = 4;
			CHAR_TO_SCORE[getIndexFromChar('w')] = 4;
			CHAR_TO_SCORE[getIndexFromChar('x')] = 8;
			CHAR_TO_SCORE[getIndexFromChar('y')] = 4;
			CHAR_TO_SCORE[getIndexFromChar('z')] = 10;
		}
		
		private WordPointsCalculator() { }
		
		public static int calculateScore(char character) {
			return CHAR_TO_SCORE[getIndexFromChar(character)];
		}
		
		public static int calculateScore(String word) {
			return word.chars().map(character -> calculateScore((char)character)).sum();
		}
		
		private static int getIndexFromChar(char character) {
			return character - 'a';
		}
		
	}
	
	private static class WordValidator {
		
		private final int[] lettersFrequencies;
		
		public WordValidator(String letters) {
			this.lettersFrequencies = new int['z' - 'a' + 1];
			for (char letter : letters.toCharArray()) {
				this.lettersFrequencies[letter - 'a']++;
			}
		}
		
		private boolean isValidWord(String word) {
			return hasValidLength(word) && hasOnlyValidLetters(word, Arrays.copyOf(lettersFrequencies, lettersFrequencies.length));
		}
	    
		private static boolean hasValidLength(String word) {
			return word.length() <= 7;
		}
		
		private static boolean hasOnlyValidLetters(String word, int[] lettersFrequencies) {
			for (char letter : word.toCharArray()) {
				if (lettersFrequencies[letter - 'a'] > 0) {
					lettersFrequencies[letter - 'a']--;
				} else {
					return false;
				}
			}
			return true;
		}
		
	}
	
	private static class WordWithPoints {
		
		private static final Comparator<WordWithPoints> COMPARING_BY_DESCENDING_POINTS_THEN_ASCENDING_INDEX = Comparator.comparing(WordWithPoints::getPoints).reversed().thenComparing(Comparator.comparing(WordWithPoints::getIndex));
		
		private final String word;
		private final int points;
		private final int index;
		
		private WordWithPoints(String word, int points, int index) {
			this.word = word;
			this.points = points;
			this.index = index;
		}
			
		public String getWord() {
			return word;
		}
		
		public int getPoints() {
			return points;
		}
		
		public int getIndex() {
			return index;
		}
		
	}
	
    public static void main(String args[]) {
        
    	Scanner in = new Scanner(System.in);
        int N = in.nextInt();
        in.nextLine();
        
        PriorityQueue<WordWithPoints> dictionary = new PriorityQueue<>(WordWithPoints.COMPARING_BY_DESCENDING_POINTS_THEN_ASCENDING_INDEX);
        for (int i = 0; i < N; i++) {
            String W = in.nextLine();
            dictionary.offer(new WordWithPoints(W, WordPointsCalculator.calculateScore(W), i));
        }
        
        String LETTERS = in.nextLine();
        WordValidator wordValidator = new WordValidator(LETTERS);

        System.out.println(findWordThatScoresTheMost(dictionary, wordValidator));
    }

	private static String findWordThatScoresTheMost(PriorityQueue<WordWithPoints> dictionary, WordValidator wordValidator) {
		
		while (!dictionary.isEmpty()) {
			WordWithPoints wordWithPoints = dictionary.poll();
			if (wordValidator.isValidWord(wordWithPoints.getWord())) {
				return wordWithPoints.getWord();
			}
		}
		
		throw new IllegalStateException("There should be at least one legal word!");
	}
	
}