import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// Enum to represent possible headings (directions) the robot can face
// Each direction has a corresponding character and a movement function
enum Heading {
	EAST('>', Location::east), // Represents moving right
	SOUTH('v', Location::south), // Represents moving down
	WEST('<', Location::west), // Represents moving left
	NORTH('^', Location::north); // Represents moving up

	final char c; // The character representing the direction (e.g., '>' for EAST)
	final Function<Location, Location> mover; // Function to calculate the next position in this direction

	// Constructor for each heading
	Heading(char c, Function<Location, Location> mover) {
		this.c = c;
		this.mover = mover;
	}

	// Method to find a Heading from its character representation
	static Heading from(char c) {
		return Arrays.stream(values()) // Stream through all Heading values
				.filter(t -> t.c == c) // Find the one matching the character
				.findFirst()
				.orElseThrow(); // Throw an exception if no match is found
	}
}

public class Day15 {
	public static void main(String[] args) throws Exception {
		// Prompt the user for the file path containing the warehouse map
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the file path for the warehouse map: ");
		String filePath = scanner.nextLine();
		scanner.close();

		// Solve the problem for two configurations: normal and wide mode
		new Challenge(false, filePath).solve(); // Normal mode
		new Challenge(true, filePath).solve();  // Wide mode
	}
}

// Class to encapsulate the entire challenge logic
class Challenge {
	final Place initialGrid; // The initial state of the warehouse
	final String filePath; // Path to the input file

	// Constructor to initialize the challenge
	Challenge(boolean wide, String filePath) throws Exception {
		this.filePath = filePath;
		// Parse the warehouse map from the file
		initialGrid = Place.from(Files.readString(Paths.get(filePath)), wide);
	}

	// Method to solve the challenge
	void solve() {
		var grid = initialGrid; // Start with the initial grid
		while (true) {
			var newGrid = grid.move(); // Attempt to move
			if (newGrid == null) {
				break; // Exit if no further moves are possible
			}
			grid = newGrid; // Update the grid state
		}
		System.out.println(grid.gpsSum()); // Output the GPS sum of box locations
	}
}

// Class to represent a specific location in the grid
record Location(int x, int y) {
	// Methods to calculate adjacent locations in each direction
	Location north() { return new Location(x, y - 1); }
	Location south() { return new Location(x, y + 1); }
	Location west() { return new Location(x - 1, y); }
	Location east() { return new Location(x + 1, y); }

	// Generic movement based on a Heading
	Location move(Heading heading) {
		return heading.mover.apply(this);
	}
}

// Class to represent the warehouse state (robot, boxes, walls, etc.)
record Place(Location position, Set<Location> boxes, Set<Location> walls, List<Heading> headings,
			 int instructionPointer, boolean wide) {
	// Parse the warehouse map and instructions from the input string
	static Place from(String input, boolean wide) {
		var parts = input.split("\n\n"); // Split map and instructions
		int y = 0;
		Location position = null; // Robot's position
		Set<Location> boxes = new HashSet<>(); // Box locations
		Set<Location> walls = new HashSet<>(); // Wall locations

		// Parse the grid map
		for (String line : parts[0].split("\n")) {
			int x = 0;
			for (char c : line.toCharArray()) {
				Location coordinate = new Location(x, y);
				switch (c) {
					case '@' -> position = coordinate; // Robot's position
					case '#' -> walls.add(coordinate); // Wall
					case 'O' -> boxes.add(coordinate); // Box
				}
				// Handle wide mode by adding additional wall spaces
				if (wide && c == '#') {
					walls.add(coordinate.east());
				}
				x += wide ? 2 : 1; // Adjust x coordinate for wide mode
			}
			y++;
		}

		// Parse the movement instructions
		List<Heading> headings = new ArrayList<>();
		for (String line : parts[1].split("\n")) {
			for (char c : line.toCharArray()) {
				headings.add(Heading.from(c));
			}
		}
		return new Place(position, boxes, walls, headings, 0, wide);
	}

	// Check if a box is next to a wall
	boolean isBoxNextToWall(Location box, Heading heading) {
		return walls.contains(box.move(heading)) || wide && walls.contains(box.move(heading).east());
	}

	// Determine all boxes movable from a starting location
	Set<Location> movableBoxes(Location initialPos, Heading heading) {
		Set<Location> visited = new HashSet<>();
		Deque<Location> queue = new ArrayDeque<>(); // BFS queue
		if (!boxes.contains(initialPos)) {
			throw new IllegalStateException(); // Ensure the start is a box
		}
		queue.addLast(initialPos);
		while (!queue.isEmpty()) {
			var pos = queue.removeFirst();
			if (visited.contains(pos)) {
				continue; // Skip already visited locations
			}
			visited.add(pos);
			var newPos = pos.move(heading);
			if (boxes.contains(newPos)) {
				queue.addLast(newPos);
			}
			if (wide) {
				if (boxes.contains(newPos.west())) {
					queue.addLast(newPos.west());
				}
				if (boxes.contains(newPos.east())) {
					queue.addLast(newPos.east());
				}
			}
		}
		// Ensure no box in the group is blocked by a wall
		return visited.stream().noneMatch(c -> isBoxNextToWall(c, heading)) ? visited : Collections.emptySet();
	}

	// Check if the robot is next to a wall
	boolean isRobotNextToWall(Heading heading) {
		return walls.contains(position.move(heading));
	}

	// Find the box next to the robot in a given direction
	Location boxNextToRobot(Heading heading) {
		Location next = position.move(heading);
		if (boxes.contains(next)) {
			return next;
		}
		if (wide) {
			Location west = next.west();
			if (boxes.contains(west)) {
				return west;
			}
		}
		return null;
	}

	// Perform a single move based on the current instruction
	Place move() {
		if (instructionPointer >= headings.size()) {
			return null; // No more instructions to process
		}
		var heading = headings.get(instructionPointer);
		boolean robotCanMove;
		Set<Location> movableBoxes;

		// Determine if the robot or boxes can move
		if (isRobotNextToWall(heading)) {
			robotCanMove = false;
			movableBoxes = Collections.emptySet();
		} else {
			var possibleBox = boxNextToRobot(heading);
			if (possibleBox == null) {
				robotCanMove = true;
				movableBoxes = Collections.emptySet();
			} else {
				movableBoxes = movableBoxes(possibleBox, heading);
				robotCanMove = !movableBoxes.isEmpty();
			}
		}

		// Update the state of the boxes after the move
		Set<Location> newBoxes;
		if (movableBoxes.isEmpty()) {
			newBoxes = boxes;
		} else {
			newBoxes = new HashSet<>();
			newBoxes.addAll(boxes.stream().filter(Predicate.not(movableBoxes::contains)).collect(Collectors.toSet()));
			newBoxes.addAll(boxes.stream().filter(movableBoxes::contains).map(c -> c.move(heading)).collect(Collectors.toSet()));
		}

		// Return the updated state of the warehouse
		return new Place(robotCanMove ? position.move(heading) : position, newBoxes, walls, headings, instructionPointer + 1, wide);
	}

	// Calculate the GPS sum for all boxes
	long gpsSum() {
		return boxes.stream().mapToLong(c -> c.x() + 100L * c.y()).sum();
	}

	// Generate a string representation of the warehouse state
	@Override
	public String toString() {
		int width = walls.stream().mapToInt(Location::x).max().orElseThrow() + 1;
		int height = walls.stream().mapToInt(Location::y).max().orElseThrow() + 1;
		var sb = new StringBuilder();
		for (int y = 0; y < height; y++) {
			char[] chars = new char[width];
			for (int x = 0; x < width; x++) {
				var xy = new Location(x, y);
				char c;
				if (xy.equals(position)) {
					c = '@';
				} else if (walls.contains(xy)) {
					c = '#';
				} else if (boxes.contains(xy)) {
					c = wide ? '[' : 'O';
				} else if (wide && boxes.contains(xy.west())) {
					c = ']';
				} else {
					c = '.';
				}
				chars[x] = c;
			}
			sb.append(chars);
			sb.append('\n');
		}
		return sb.toString();
	}
}
