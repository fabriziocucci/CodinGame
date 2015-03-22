import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

class Solution {
	
	private static class Map {
		
		private final int rowCount;
		private final int columnCount;
		private final MapElement[][] mapElements;
		
		private MapElement startingPoint;
		private MapElement teleporter1;
		private MapElement teleporter2;
		
		private Map(int rowCount, int columnCount, MapElement[][] mapElements) {
			this.rowCount = rowCount;
			this.columnCount = columnCount;
			this.mapElements = mapElements;
			initializeStartingPointAndTeleporters();
		}		
		
		private void initializeStartingPointAndTeleporters() {
			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
					if (mapElements[rowIndex][columnIndex].getMapElementType().equals(MapElementType.STARTING_POINT)) {
						this.startingPoint = mapElements[rowIndex][columnIndex];
					} else if (mapElements[rowIndex][columnIndex].getMapElementType().equals(MapElementType.TELEPORTER)) {
						if (teleporter1 == null) {
							this.teleporter1 = mapElements[rowIndex][columnIndex];
						} else if (teleporter2 == null) {
							this.teleporter2 = mapElements[rowIndex][columnIndex];
						}
					}
				}
			}
		}
		
		public MapElement getStartingPoint() {
			return startingPoint;
		}

		public boolean tryMove(Bender bender) {
			return getNextMapElement(bender.getRowIndex(), bender.getColumnIndex(), bender.getDirection()).tryMove(this, bender);
		}
		
		public MapElement getNextMapElement(int rowIndex, int columnIndex, Direction direction) {
			switch (direction) {
				case NORTH: return mapElements[rowIndex - 1][columnIndex];
				case SOUTH: return mapElements[rowIndex + 1][columnIndex];
				case WEST: return mapElements[rowIndex][columnIndex - 1];
				case EAST: return mapElements[rowIndex][columnIndex + 1];
				default: throw new IllegalArgumentException("Unable to switch on the provided direction!");
			}
		}

		public MapElement getTeleportingPoint(MapElement mapElement) {
			if (mapElement.equals(teleporter1)) {
				return teleporter2;
			}
			if (mapElement.equals(teleporter2)) {
				return teleporter1;
			}
			throw new IllegalArgumentException("There is no teleporting point from the map element provided as argument.");
		}

		public static MapElement[] parseRowFromString(int rowIndex, String rowAsString) {
			MapElement[] row = new MapElement[rowAsString.length()];
			for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
				row[columnIndex] = MapElement.parseFromChar(rowIndex, columnIndex, rowAsString.charAt(columnIndex));
			}
			return row;
		}

	}
	
	private static class MapElement {
		
		private final int rowIndex;
		private final int columnIndex;
		private MapElementType mapElementType;

		private MapElement(int rowIndex, int columnIndex, MapElementType mapElementType) {
			this.rowIndex = rowIndex;
			this.columnIndex = columnIndex;
			this.mapElementType = mapElementType;
		}
		
		public boolean tryMove(Map map, Bender bender) {
			return mapElementType.tryMove(map, bender, this);
		}

		public int getRowIndex() {
			return rowIndex;
		}
		
		public int getColumnIndex() {
			return columnIndex;
		}
		
		public MapElementType getMapElementType() {
			return mapElementType;
		}
		
		public void destroy() {
			if (mapElementType.equals(MapElementType.BREAKABLE_OBSTACLE)) {
				mapElementType = MapElementType.BLANK;
			} else {
				throw new UnsupportedOperationException("Only breakable obstacle can be destroyed.");
			}
		}
		
		public static MapElement parseFromChar(int rowIndex, int columnIndex, char mapElementAsChar) {
			return new MapElement(rowIndex, columnIndex, MapElementType.parseFromChar(mapElementAsChar));
		}
		
	}
	
	private static enum MapElementType {
		
		STARTING_POINT {

			@Override
			public boolean tryMove(Map map, Bender bender, MapElement mapElement) {
				throw new IllegalStateException("Bender should never return to the starting point!");
			}
			
		},
		
		SUICIDE_BOOTH {

			@Override
			public boolean tryMove(Map map, Bender bender, MapElement mapElement) {
				bender.updatePosition(mapElement);
				bender.commitSuicide();
				return true;
			}
			
		},
		
		UNBREAKABLE_OBSTACLE {

			@Override
			public boolean tryMove(Map map, Bender bender, MapElement mapElement) {
				return false;
			}
			
		},
		
		BREAKABLE_OBSTACLE {

			@Override
			public boolean tryMove(Map map, Bender bender, MapElement mapElement) {
				if (bender.isBreakerModeEnabled()) {
					bender.destroyObstacle(mapElement);
					bender.updatePosition(mapElement);
					return true;
				} else {
					return false;
				}
			}
			
		},
		
		SOUTH_PATH_MODIFIER {
			
			@Override
			public boolean tryMove(Map map, Bender bender, MapElement mapElement) {
				bender.acquirePathModifier(Direction.SOUTH);
				bender.updatePosition(mapElement);
				return true;
			}
			
		},
		
		EAST_PATH_MODIFIER {
			
			@Override
			public boolean tryMove(Map map, Bender bender, MapElement mapElement) {
				bender.acquirePathModifier(Direction.EAST);
				bender.updatePosition(mapElement);
				return true;
			}
			
		},
		
		NORTH_PATH_MODIFIER {

			@Override
			public boolean tryMove(Map map, Bender bender, MapElement mapElement) {
				bender.acquirePathModifier(Direction.NORTH);
				bender.updatePosition(mapElement);
				return true;
			}
			
		},
		
		
		WEST_PATH_MODIFIER {

			@Override
			public boolean tryMove(Map map, Bender bender, MapElement mapElement) {
				bender.acquirePathModifier(Direction.WEST);
				bender.updatePosition(mapElement);
				return true;
			}
			
		},
		
		CIRCUIT_INVERTER {

			@Override
			public boolean tryMove(Map map, Bender bender, MapElement mapElement) {
				bender.changeReverseMode();
				bender.updatePosition(mapElement);
				return true;
			}
			
		},
		
		BEER {

			@Override
			public boolean tryMove(Map map, Bender bender, MapElement mapElement) {
				bender.changeBreakerMode();
				bender.updatePosition(mapElement);
				return true;
			}
			
		},
		
		TELEPORTER {

			@Override
			public boolean tryMove(Map map, Bender bender, MapElement mapElement) {
				bender.updatePosition(map.getTeleportingPoint(mapElement));
				return true;
			}
			
		},
		
		BLANK {

			@Override
			public boolean tryMove(Map map, Bender bender, MapElement mapElement) {
				bender.updatePosition(mapElement);
				return true;
			}
			
		};
		
		public static MapElementType parseFromChar(char mapElementTypeAsChar) {
			switch (mapElementTypeAsChar) {
				case '@': return STARTING_POINT;
				case '$': return SUICIDE_BOOTH;
				case '#': return UNBREAKABLE_OBSTACLE;
				case 'X': return BREAKABLE_OBSTACLE;
				case 'N': return NORTH_PATH_MODIFIER;
				case 'S': return SOUTH_PATH_MODIFIER;
				case 'W': return WEST_PATH_MODIFIER;
				case 'E': return EAST_PATH_MODIFIER;
				case 'I': return CIRCUIT_INVERTER;
				case 'B': return BEER;
				case 'T': return TELEPORTER;
				case ' ': return BLANK;
				default: throw new IllegalArgumentException("Unable to parse string.");
			}
		}
		
		public abstract boolean tryMove(Map map, Bender bender, MapElement mapElement);

	}
	
	private static class Bender {
		
		private class BenderState {
			
			private int rowIndex;
			private int columnIndex;
			private Direction direction;
			
			private boolean isDead;
			private boolean isInLoop;
			private boolean isReverseModeEnabled;
			private boolean isBreakerModeEnabled;
			
			private Optional<Direction> pathModifier = Optional.empty();
			
			public BenderState copy() {
				BenderState benderState = new BenderState();
				benderState.rowIndex = this.rowIndex;
				benderState.columnIndex = this.columnIndex;
				benderState.direction = this.direction;
				benderState.isDead = this.isDead;
				benderState.isInLoop = this.isInLoop;
				benderState.isReverseModeEnabled = this.isReverseModeEnabled;
				benderState.isBreakerModeEnabled = this.isBreakerModeEnabled;
				benderState.pathModifier = this.pathModifier;
				return benderState;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + columnIndex;
				result = prime * result + rowIndex;
				result = prime * result + ((direction == null) ? 0 : direction.hashCode());
				result = prime * result + (isBreakerModeEnabled ? 1231 : 1237);
				result = prime * result + (isReverseModeEnabled ? 1231 : 1237);
				return result;
			}

			@Override
			public boolean equals(Object that) {
				if (this == that)
					return true;
				if (that == null)
					return false;
				if (getClass() != that.getClass())
					return false;
				BenderState other = (BenderState) that;
				if (rowIndex != other.rowIndex)
					return false;
				if (columnIndex != other.columnIndex)
					return false;
				if (direction != other.direction)
					return false;
				if (isReverseModeEnabled != other.isReverseModeEnabled)
					return false;
				if (isBreakerModeEnabled != other.isBreakerModeEnabled)
					return false;
				return true;
			}

		}
		
		private final BenderState benderState;
		private final Set<BenderState> previousStates;
		
		private Bender(int rowIndex, int columnIndex) {
			this.benderState = new BenderState();
			this.previousStates = new HashSet<>();
			initializeBenderState(rowIndex, columnIndex);
		}
		
		private void initializeBenderState(int rowIndex, int columnIndex) {
			this.benderState.rowIndex = rowIndex;
			this.benderState.columnIndex = columnIndex;
			this.benderState.direction = Direction.SOUTH;
			this.benderState.isDead = false;
			this.benderState.isInLoop = false;
			this.benderState.isReverseModeEnabled = false;
			this.benderState.isBreakerModeEnabled = false;
			this.benderState.pathModifier = Optional.empty();
		}
		
		public int getRowIndex() {
			return this.benderState.rowIndex;
		}
		
		public int getColumnIndex() {
			return this.benderState.columnIndex;
		}
		
		public Direction getDirection() {
			return this.benderState.direction;
		}
		
		public boolean isDead() {
			return this.benderState.isDead;
		}
		
		public boolean isInLoop() {
			return this.benderState.isInLoop;
		}
		
		public boolean isBreakerModeEnabled() {
			return this.benderState.isBreakerModeEnabled;
		}
		
		public void commitSuicide() {
			this.benderState.isDead = true;
		}
		
		public void changeReverseMode() {
			this.benderState.isReverseModeEnabled = !this.benderState.isReverseModeEnabled;
		}
		
		public void changeBreakerMode() {
			this.benderState.isBreakerModeEnabled = !this.benderState.isBreakerModeEnabled;
		}
		
		public void acquirePathModifier(Direction direction) {
			this.benderState.pathModifier = Optional.of(direction);
		}
		
		public void destroyObstacle(MapElement obstacle) {
			obstacle.destroy();
			this.previousStates.clear();
		}
		
		public void updatePosition(MapElement mapElement) {
			this.benderState.rowIndex = mapElement.getRowIndex();
			this.benderState.columnIndex = mapElement.getColumnIndex();
		}
		
		public void move(Map map) {
			consumePathModifierIfPresent();
			if (!map.tryMove(this)) {
				tryMoveInDifferentDirections(map);
			}
			checkForLoop();
		}
		
		private void consumePathModifierIfPresent() {
			if (this.benderState.pathModifier.isPresent()) {
				this.benderState.direction = this.benderState.pathModifier.get();
				this.benderState.pathModifier = Optional.empty();
			}
		}
		
		private void tryMoveInDifferentDirections(Map map) {
			for (Direction direction : getDirectionsByPriority()) {
				changeDirection(direction);
				if (map.tryMove(this)) {
					return;
				}
			}
			throw new IllegalStateException("It seems that the poor Bender is trapped.");
		}

		private void changeDirection(Direction direction) {
			this.benderState.direction = direction;
		}
		
		private Collection<Direction> getDirectionsByPriority() {
			if (this.benderState.isReverseModeEnabled) {
				return Direction.getDirectionsByAscendingPriority();
			} else {
				return Direction.getDirectionsByDescendingPriority();
			}
		}
		
		private void checkForLoop() {
			if (!this.previousStates.add(this.benderState.copy())) {
				this.benderState.isInLoop = true;
			}
		}

	}
	
	private static enum Direction {
		
		SOUTH(3), EAST(2), NORTH(1), WEST(0);
		
		private static final Collection<Direction> DIRECTIONS_BY_ASCENDING_PRIORITY = Arrays.stream(Direction.values()).sorted(Comparator.comparingInt(Direction::getPriority)).collect(Collectors.toList());
		private static final Collection<Direction> DIRECTIONS_BY_DESCENDING_PRIORITY = Arrays.stream(Direction.values()).sorted(Comparator.comparingInt(Direction::getPriority).reversed()).collect(Collectors.toList());
		
		private final int priority;
		
		private Direction(int priority) {
			this.priority = priority;
		}
		
		public int getPriority() {
			return priority;
		}
		
		public static Collection<Direction> getDirectionsByAscendingPriority() {
			return DIRECTIONS_BY_ASCENDING_PRIORITY;
		}
		
		public static Collection<Direction> getDirectionsByDescendingPriority() {
			return DIRECTIONS_BY_DESCENDING_PRIORITY;
		}
		
	}
	
    public static void main(String args[]) {
    	
        Scanner in = new Scanner(System.in);
        int L = in.nextInt();
        int C = in.nextInt();
        in.nextLine();
        
        MapElement[][] mapElements = new MapElement[L][C];
        for (int i = 0; i < L; i++) {
            String rowAsString = in.nextLine();
            mapElements[i] = Map.parseRowFromString(i, rowAsString);
        }

        Map map = new Map(L, C, mapElements);
        Bender bender = new Bender(map.getStartingPoint().getRowIndex(), map.getStartingPoint().getColumnIndex());
        
        StringBuilder moves = new StringBuilder();
        while (!bender.isDead() && !bender.isInLoop()) {
        	bender.move(map);
        	moves.append(bender.getDirection()).append('\n');
        }
        moves.setLength(moves.length() - 1);
        
        System.out.println(bender.isInLoop() ? "LOOP" : moves.toString());
    }

}