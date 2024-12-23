import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

// Main class to run the program
public class Day20 {

	public static void main(String[] args) throws Exception {
		// Prompt the user to enter the file path
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the file path: ");
		String filePath = scanner.nextLine();

		// Initialize and solve the puzzle
		new Day20Puzzle(filePath).solve();
	}
}

// Class representing the puzzle logic
class Day20Puzzle {
	private final Day20Grid grid;

	// Constructor to initialize the grid from the input file
	Day20Puzzle(String filePath) throws Exception {
		try (var reader = new BufferedReader(new FileReader(filePath))) {
			// Parse the grid from the lines in the file
			grid = Day20Grid.from(reader.lines());
		}
	}

	// Method to solve the puzzle
	void solve() {
		// Generate the distance map for the grid
		var map = grid.distanceMap();

		// Print results for different cheat distance thresholds
		System.out.println(grid.countGoodCheats(map, 2));
		System.out.println(grid.countGoodCheats(map, 20));
	}
}

// Class representing a coordinate on the grid
record Day20Coordinate(int x, int y) implements Comparable<Day20Coordinate> {

	// Override compareTo to define natural ordering based on y and then x
	@Override
	public int compareTo(Day20Coordinate o) {
		return y == o.y ? Integer.compare(x, o.x) : Integer.compare(y, o.y);
	}

	// Get immediate (orthogonal) neighbors of the coordinate
	Collection<Day20Coordinate> neighbours() {
		return Set.of(
				new Day20Coordinate(x, y - 1), // Up
				new Day20Coordinate(x + 1, y), // Right
				new Day20Coordinate(x, y + 1), // Down
				new Day20Coordinate(x - 1, y)  // Left
		);
	}

	// Get neighbors within a specified cheat distance (diagonal moves allowed)
	Collection<Day20Coordinate> neighbours(int cheatDistance) {
		Collection<Day20Coordinate> neighbours = new TreeSet<>();
		for (int i = 0; i < cheatDistance; i++) {
			// Generate the diagonal neighbors within the cheat distance
			neighbours.add(new Day20Coordinate(x + i, y - cheatDistance + i));
			neighbours.add(new Day20Coordinate(x + cheatDistance - i, y + i));
			neighbours.add(new Day20Coordinate(x - i, y + cheatDistance - i));
			neighbours.add(new Day20Coordinate(x - cheatDistance + i, y - i));
		}
		return neighbours;
	}
}

// Class representing the grid
record Day20Grid(int width, int height, Set<Day20Coordinate> walls, Day20Coordinate start, Day20Coordinate finish) {

	// Static method to create a grid from input lines
	static Day20Grid from(Stream<String> lines) {
		Set<Day20Coordinate> walls = new HashSet<>();
		Day20Coordinate start = null;
		Day20Coordinate end = null;
		int y = 0;
		int x = 0;

		// Parse each line to identify walls, start, and end points
		for (String line : lines.toList()) {
			x = 0;
			for (char c : line.toCharArray()) {
				switch (c) {
					case 'S' -> start = new Day20Coordinate(x, y); // Start point
					case 'E' -> end = new Day20Coordinate(x, y);   // End point
					case '#' -> walls.add(new Day20Coordinate(x, y)); // Wall
					case '.' -> { /* Open space */ }
					default -> throw new IllegalArgumentException(); // Invalid character
				}
				x++;
			}
			++y;
		}
		// Ensure start and end points are present and return the grid
		return new Day20Grid(x, y, walls, Objects.requireNonNull(start), Objects.requireNonNull(end));
	}

	// Method to calculate the shortest distance map from the start
	Map<Day20Coordinate, Integer> distanceMap() {
		Map<Day20Coordinate, Integer> distanceMap = new HashMap<>();
		Day20Coordinate pos = start;
		int distance = 0;

		// Perform a breadth-first traversal to calculate distances
		do {
			distanceMap.put(pos, distance++);
			pos = pos.neighbours().stream()
					.filter(c -> !walls.contains(c)) // Avoid walls
					.filter(c -> !distanceMap.containsKey(c)) // Avoid already visited
					.findFirst().orElse(null);
		} while (pos != null);

		return distanceMap;
	}

	// Method to count "good cheats" with a distance threshold
	long countGoodCheats(Map<Day20Coordinate, Integer> distances, int maxCheatDuration) {
		Map<List<Day20Coordinate>, Integer> cheats = new HashMap<>();

		// Iterate over each coordinate and its distance
		for (Map.Entry<Day20Coordinate, Integer> e : distances.entrySet()) {
			Day20Coordinate cheatStart = e.getKey();
			int distance = e.getValue();

			// Check potential cheats with varying durations
			for (int cheatDuration = 2; cheatDuration <= maxCheatDuration; cheatDuration++) {
				for (var cheatEnd : cheatStart.neighbours(cheatDuration)) {
					Integer newDistance = distances.get(cheatEnd);
					if (newDistance != null) {
						// Calculate the cheat gain and store it
						cheats.put(List.of(cheatStart, cheatEnd), newDistance - distance - cheatDuration);
					}
				}
			}
		}

		// Count the number of "good cheats" where the gain is >= 100
		return cheats.entrySet().stream().filter(e -> e.getValue() >= 100).count();
	}
}
