import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

// Enum to represent the four cardinal directions and their movement logic
enum Day16Heading {
	EAST(Day16Coordinate::east),
	SOUTH(Day16Coordinate::south),
	WEST(Day16Coordinate::west),
	NORTH(Day16Coordinate::north);

	// Function that defines how a coordinate moves in this direction
	final Function<Day16Coordinate, Day16Coordinate> mover;

	Day16Heading(Function<Day16Coordinate, Day16Coordinate> mover) {
		this.mover = mover;
	}

	// Returns possible orthogonal directions for rotation
	Collection<Day16Heading> rotate() {
		return switch (this) {
			case EAST, WEST -> Set.of(NORTH, SOUTH); // Horizontal directions rotate to vertical
			case NORTH, SOUTH -> Set.of(EAST, WEST); // Vertical directions rotate to horizontal
		};
	}
}

// Main entry point for the program
public class Day16 {
	public static void main(String[] args) throws Exception {
		new Day16Puzzle().solve(); // Initialize and solve the puzzle
	}
}

// Class to represent the puzzle logic
class Day16Puzzle {
	final Day16Grid grid; // The grid representation of the maze

	Day16Puzzle() throws IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the file path: "); // Prompt user for file path
		String filePath = scanner.nextLine();
		scanner.close();

		// Read the maze grid from the file
		List<String> lines = Files.readAllLines(Path.of(filePath));
		grid = Day16Grid.from(lines.stream()); // Convert the lines to a grid
	}

	// Solves the puzzle and prints results
	void solve() {
		BestPaths result = grid.shortestPath(); // Find the shortest path in the grid
		System.out.println("Lowest Score: " + result.lowestScore());
		System.out.println("Number of Viewing Spots: " + result.viewingSpots());
	}
}

// Record to represent a coordinate in the grid
record Day16Coordinate(int x, int y) implements Comparable<Day16Coordinate> {
	// Movement methods to return new coordinates in each direction
	Day16Coordinate north() { return new Day16Coordinate(x, y - 1); }
	Day16Coordinate south() { return new Day16Coordinate(x, y + 1); }
	Day16Coordinate west() { return new Day16Coordinate(x - 1, y); }
	Day16Coordinate east() { return new Day16Coordinate(x + 1, y); }

	// Moves to a new coordinate based on a given heading
	Day16Coordinate move(Day16Heading heading) {
		return heading.mover.apply(this);
	}

	// Comparison method for sorting coordinates
	@Override
	public int compareTo(Day16Coordinate o) {
		return y == o.y ? Integer.compare(x, o.x) : Integer.compare(y, o.y);
	}
}

// Record to represent a reindeer, which has a position and a heading
record Reindeer(Day16Coordinate position, Day16Heading heading) implements Comparable<Reindeer> {
	@Override
	public int compareTo(Reindeer o) {
		// Compare first by position, then by heading
		return position.equals(o.position) ? Integer.compare(heading.ordinal(), o.heading.ordinal()) : position.compareTo(o.position);
	}
}

// Record to store the results of finding the best paths
record BestPaths(int lowestScore, int viewingSpots) {
}

// Record to represent the grid, including walls, start, and end points
record Day16Grid(Set<Day16Coordinate> walls, Day16Coordinate start, Day16Coordinate end) {
	// Parses a stream of lines into a grid
	static Day16Grid from(Stream<String> lines) {
		Set<Day16Coordinate> walls = new HashSet<>();
		Day16Coordinate start = null;
		Day16Coordinate end = null;
		int y = 0;

		// Parse each character in the input lines
		for (String line : lines.toList()) {
			int x = 0;
			for (char c : line.toCharArray()) {
				switch (c) {
					case '#' -> walls.add(new Day16Coordinate(x, y)); // Wall
					case 'S' -> start = new Day16Coordinate(x, y); // Start point
					case 'E' -> end = new Day16Coordinate(x, y); // End point
				}
				x++;
			}
			y++;
		}

		// Ensure start and end points exist and return the grid
		return new Day16Grid(Set.copyOf(walls), Objects.requireNonNull(start), Objects.requireNonNull(end));
	}

	// Finds the neighbors (valid moves) for a given node
	Collection<ND> neighbours(ND nd) {
		Collection<ND> neighbours = new HashSet<>();
		Reindeer reindeer = nd.node;
		Day16Heading currentHeading = reindeer.heading();

		// Add rotated directions as possible neighbors
		for (Day16Heading heading : currentHeading.rotate()) {
			neighbours.add(new ND(new Reindeer(reindeer.position(), heading), nd.distance + 1000, nd));
		}

		// Add forward movement if the path is not blocked by a wall
		var nextPosition = reindeer.position().move(currentHeading);
		if (!walls.contains(nextPosition)) {
			neighbours.add(new ND(new Reindeer(nextPosition, currentHeading), nd.distance + 1, nd));
		}
		return neighbours;
	}

	// Implements the shortest path algorithm
	BestPaths shortestPath() {
		int shortest = -1;
		var queue = new PriorityQueue<ND>(); // Priority queue to prioritize shorter paths
		Set<Day16Coordinate> viewingSpots = new HashSet<>();
		Set<Reindeer> visited = new HashSet<>();

		// Start from the initial position
		queue.add(new ND(new Reindeer(start, Day16Heading.EAST), 0, null));

		while (!queue.isEmpty()) {
			var current = queue.remove();

			// Check if the end position is reached
			if (current.node.position().equals(end)) {
				if (shortest == -1) {
					shortest = current.distance; // Record the shortest path distance
				}
				if (shortest == current.distance) {
					viewingSpots.addAll(current.path()); // Record viewing spots on the path
				}
			}

			visited.add(current.node); // Mark the current node as visited

			// Add neighbors to the queue if they haven't been visited
			for (var next : neighbours(current)) {
				if (!visited.contains(next.node)) {
					queue.add(next);
				}
			}
		}

		return new BestPaths(shortest, viewingSpots.size()); // Return the results
	}

	// Record to represent a node in the shortest path search
	record ND(Reindeer node, int distance, ND previous) implements Comparable<ND> {
		@Override
		public int compareTo(ND o) {
			// Compare nodes by distance, breaking ties by node details
			return distance == o.distance ? node.compareTo(o.node) : Integer.compare(distance, o.distance);
		}

		// Reconstruct the path from the start to this node
		Collection<Day16Coordinate> path() {
			Collection<Day16Coordinate> path = new HashSet<>();
			var nd = this;
			while (nd != null) {
				path.add(nd.node.position());
				nd = nd.previous;
			}
			return path;
		}
	}
}
