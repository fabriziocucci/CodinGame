import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

class Player {

	private static enum EntrancePoint {
		
		TOP, LEFT, RIGHT;

		public static EntrancePoint fromString(String entrancePointAsString) {
			switch (entrancePointAsString) {
				case "TOP": return TOP;
				case "LEFT": return LEFT;
				case "RIGHT": return RIGHT;
				default: throw new IllegalArgumentException("Unable to parse string.");
			}
		}
		
	}
	
	private static enum ExitPoint {
		
		BOTTOM {
			
			@Override
			public int getNextHorizontalPosition(int x) {
				return x;
			}
			
			@Override
			public int getNextVerticalPosition(int y) {
				return y + 1;
			}
			
		}, 
		
		LEFT {
			
			@Override
			public int getNextHorizontalPosition(int x) {
				return x - 1;
			}
			
			@Override
			public int getNextVerticalPosition(int y) {
				return y;
			}
			
		},
		
		RIGHT {
			
			@Override
			public int getNextHorizontalPosition(int x) {
				return x + 1;
			}
			
			@Override
			public int getNextVerticalPosition(int y) {
				return y;
			}
			
		};
		
		public abstract int getNextHorizontalPosition(int x);
		
		public abstract int getNextVerticalPosition(int y);
		
	}
	
	private static enum RoomType {
		
		TYPE_0() {
			
			@Override
			protected void fillEntranceToExitMap() {
				
			}
			
		},
		
		TYPE_1() {
			
			@Override
			protected void fillEntranceToExitMap() {
				Map<EntrancePoint, ExitPoint> entranceToExit = getEntranceToExitMap();
				entranceToExit.put(EntrancePoint.TOP, ExitPoint.BOTTOM);
				entranceToExit.put(EntrancePoint.LEFT, ExitPoint.BOTTOM);
				entranceToExit.put(EntrancePoint.RIGHT, ExitPoint.BOTTOM);
			}
			
		},
		
		TYPE_2() {
			
			@Override
			protected void fillEntranceToExitMap() {
				Map<EntrancePoint, ExitPoint> entranceToExit = getEntranceToExitMap();
				entranceToExit.put(EntrancePoint.LEFT, ExitPoint.RIGHT);
				entranceToExit.put(EntrancePoint.RIGHT, ExitPoint.LEFT);
			}
			
		},
		
		TYPE_3() {
			
			@Override
			protected void fillEntranceToExitMap() {
				Map<EntrancePoint, ExitPoint> entranceToExit = getEntranceToExitMap();
				entranceToExit.put(EntrancePoint.TOP, ExitPoint.BOTTOM);
			}
			
		},
		
		TYPE_4() {
			
			@Override
			protected void fillEntranceToExitMap() {
				Map<EntrancePoint, ExitPoint> entranceToExit = getEntranceToExitMap();
				entranceToExit.put(EntrancePoint.TOP, ExitPoint.LEFT);
				entranceToExit.put(EntrancePoint.RIGHT, ExitPoint.BOTTOM);
			}
			
		},
		
		TYPE_5() {
			
			@Override
			protected void fillEntranceToExitMap() {
				Map<EntrancePoint, ExitPoint> entranceToExit = getEntranceToExitMap();
				entranceToExit.put(EntrancePoint.TOP, ExitPoint.RIGHT);
				entranceToExit.put(EntrancePoint.LEFT, ExitPoint.BOTTOM);
			}
			
		},
		
		TYPE_6() {
			
			@Override
			protected void fillEntranceToExitMap() {
				Map<EntrancePoint, ExitPoint> entranceToExit = getEntranceToExitMap();
				entranceToExit.put(EntrancePoint.LEFT, ExitPoint.RIGHT);
				entranceToExit.put(EntrancePoint.RIGHT, ExitPoint.LEFT);
			}
			
		},
		
		TYPE_7() {
			
			@Override
			protected void fillEntranceToExitMap() {
				Map<EntrancePoint, ExitPoint> entranceToExit = getEntranceToExitMap();
				entranceToExit.put(EntrancePoint.TOP, ExitPoint.BOTTOM);
				entranceToExit.put(EntrancePoint.RIGHT, ExitPoint.BOTTOM);
			}
			
		},
		
		TYPE_8() {
			
			@Override
			protected void fillEntranceToExitMap() {
				Map<EntrancePoint, ExitPoint> entranceToExit = getEntranceToExitMap();
				entranceToExit.put(EntrancePoint.LEFT, ExitPoint.BOTTOM);
				entranceToExit.put(EntrancePoint.RIGHT, ExitPoint.BOTTOM);
			}
			
		},
		
		TYPE_9() {
			
			@Override
			protected void fillEntranceToExitMap() {
				Map<EntrancePoint, ExitPoint> entranceToExit = getEntranceToExitMap();
				entranceToExit.put(EntrancePoint.TOP, ExitPoint.BOTTOM);
				entranceToExit.put(EntrancePoint.LEFT, ExitPoint.BOTTOM);
			}
			
		},
		
		TYPE_10() {
			
			@Override
			protected void fillEntranceToExitMap() {
				Map<EntrancePoint, ExitPoint> entranceToExit = getEntranceToExitMap();
				entranceToExit.put(EntrancePoint.TOP, ExitPoint.LEFT);
			}
			
		},
		
		TYPE_11() {
			
			@Override
			protected void fillEntranceToExitMap() {
				Map<EntrancePoint, ExitPoint> entranceToExit = getEntranceToExitMap();
				entranceToExit.put(EntrancePoint.TOP, ExitPoint.RIGHT);
			}
			
		},
		
		TYPE_12() {
			
			@Override
			protected void fillEntranceToExitMap() {
				Map<EntrancePoint, ExitPoint> entranceToExit = getEntranceToExitMap();
				entranceToExit.put(EntrancePoint.RIGHT, ExitPoint.BOTTOM);
			}
			
		},
		
		TYPE_13() {
			
			@Override
			protected void fillEntranceToExitMap() {
				Map<EntrancePoint, ExitPoint> entranceToExit = getEntranceToExitMap();
				entranceToExit.put(EntrancePoint.LEFT, ExitPoint.BOTTOM);
			}
			
		};
		
		private final Map<EntrancePoint, ExitPoint> entranceToExitMap;
		
		private RoomType() {
			this.entranceToExitMap = new HashMap<>();
			fillEntranceToExitMap();
		}
		
		protected abstract void fillEntranceToExitMap();
		
		public Map<EntrancePoint, ExitPoint> getEntranceToExitMap() {
			return entranceToExitMap;
		}
		
		public Optional<ExitPoint> getExitPoint(EntrancePoint entrancePoint) {
			return Optional.of(entranceToExitMap.get(entrancePoint));
		}
		
		public static RoomType fromString(String roomTypeAsString) {
			switch (roomTypeAsString) {
				case "0" : return TYPE_0;
				case "1" : return TYPE_1;
				case "2" : return TYPE_2;
				case "3" : return TYPE_3;
				case "4" : return TYPE_4;
				case "5" : return TYPE_5;
				case "6" : return TYPE_6;
				case "7" : return TYPE_7;
				case "8" : return TYPE_8;
				case "9" : return TYPE_9;
				case "10" : return TYPE_10;
				case "11" : return TYPE_11;
				case "12" : return TYPE_12;
				case "13" : return TYPE_13;
				default: throw new IllegalArgumentException("Unable to parse string.");
			}
		}
		
	}
	
    public static void main(String args[]) {
    	
        Scanner in = new Scanner(System.in);
        int W = in.nextInt(); // number of columns.
        int H = in.nextInt(); // number of rows.
        in.nextLine();
        
        RoomType[][] rooms = new RoomType[H][W];
        for (int i = 0; i < H; i++) {
            String LINE = in.nextLine(); // represents a line in the grid and contains W integers. Each integer represents one room of a given type.
            rooms[i] = parseRoomsLine(LINE); 
        }
        
        int EX = in.nextInt(); // the coordinate along the X axis of the exit (not useful for this first mission, but must be read).
        in.nextLine();

        // game loop
        while (true) {
            
        	int XI = in.nextInt();
            int YI = in.nextInt();
            String POS = in.next();
            in.nextLine();

            System.out.println(getNextPosition(XI, YI, rooms[YI][XI], EntrancePoint.fromString(POS))); // One line containing the X Y coordinates of the room in which you believe Indy will be on the next turn.
        
        }
    }

	private static RoomType[] parseRoomsLine(String line) {
		String[] roomsLineAsArray = line.split("\\s");
		RoomType[] roomsLine = new RoomType[roomsLineAsArray.length];
		for (int i = 0; i < roomsLineAsArray.length; i++) {
			roomsLine[i] = RoomType.fromString(roomsLineAsArray[i]);
		}
		return roomsLine;
	}
	
	private static String getNextPosition(int x, int y, RoomType room, EntrancePoint entrancePoint) {
		return getNextHorizontalPosition(x, room, entrancePoint) + " " + getNextVerticalPosition(y, room, entrancePoint);
	}
	
	private static int getNextHorizontalPosition(int x, RoomType room, EntrancePoint entrancePoint) {
		return room.getExitPoint(entrancePoint).map(exitPoint -> exitPoint.getNextHorizontalPosition(x)).orElseThrow(() -> new IllegalStateException("Indiana is horizontally screwed!"));
	}
	
	private static int getNextVerticalPosition(int y, RoomType room, EntrancePoint entrancePoint) {
		return room.getExitPoint(entrancePoint).map(exitPoint -> exitPoint.getNextVerticalPosition(y)).orElseThrow(() -> new IllegalStateException("Indiana is vertically screwed!"));
	}

}