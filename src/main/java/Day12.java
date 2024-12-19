import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Day12 {
	public static void main(String[] args) throws IOException {
		// Create a new Puzzle instance and solve it
		new Puzzle().solve();
	}
}

class Puzzle {

	private final Grid grid;

	// Constructor to read the grid from the user's file path input
	Puzzle() throws IOException {
		// Create a scanner to read the file path from the console
		Scanner scanner = new Scanner(System.in);

		// Prompt the user for the file path where the garden map is located
		System.out.print("Enter the file path: ");
		String filePath = scanner.nextLine(); // Read the file path input by the user

		// Initialize the grid by reading the file content
		grid = Grid.from(filePath);
	}

	// Method to solve the puzzle and output the results for both parts
	void solve() {
		// Calculate and print the total price for part 1 (based on the perimeter of regions)
		var regions = grid.regions(); // Get the regions from the grid
		System.out.println("Total price (Part 1): " + regions.stream().mapToInt(Region::price).sum());

		// Calculate and print the total price for part 2 (based on better price using sides of the regions)
		System.out.println("Total price (Part 2): " + regions.stream().mapToLong(Region::betterPrice).sum());
	}
}

record Coordinate(int x, int y) implements Comparable<Coordinate> {

	// Get the neighboring coordinates (up, down, left, right)
	Collection<Coordinate> neighbours() {
		return Set.of(
				new Coordinate(x + 1, y),
				new Coordinate(x, y + 1),
				new Coordinate(x - 1, y),
				new Coordinate(x, y - 1)
		);
	}

	// Get the edges of the current coordinate (including the diagonal)
	Collection<Coordinate> edges() {
		return Set.of(
				new Coordinate(x, y),
				new Coordinate(x, y + 1),
				new Coordinate(x + 1, y),
				new Coordinate(x + 1, y + 1)
		);
	}

	// Check if two coordinates are neighbors (horizontally or vertically)
	boolean isNeighbor(Coordinate other) {
		return x == other.x || y == other.y;
	}

	// Compare two coordinates based on x and y values (for sorting purposes)
	@Override
	public int compareTo(Coordinate o) {
		return y == o.y ? Integer.compare(x, o.x) : Integer.compare(y, o.y);
	}
}

record Region(Map<Coordinate, Integer> plots) {

	// Calculate the area of the region (number of plots in the region)
	int area() {
		return plots.size();
	}

	// Calculate the perimeter of the region (sum of all fences)
	int perimeter() {
		return plots.values().stream().mapToInt(i -> i).sum();
	}

	// Calculate the price of the region (area * perimeter)
	int price() {
		return area() * perimeter();
	}

	// Calculate the edges of the region (based on sides between adjacent plots)
	long edges() {
		Map<Coordinate, SortedSet<Coordinate>> edges = new HashMap<>();
		for (Coordinate coordinate : plots.keySet()) {
			for (Coordinate edge : coordinate.edges()) {
				edges.computeIfAbsent(edge, c -> new TreeSet<>());
				edges.get(edge).add(coordinate);
			}
		}
		// Count the hidden edges and return the total number of edges
		long hidden = 2 * edges.values().stream().filter(s -> s.size() == 2 && !s.first().isNeighbor(s.last())).count();
		return edges.values().stream().filter(s -> (s.size() & 1) == 1).count() + hidden;
	}

	// Calculate the better price using the area and the edges of the region
	long betterPrice() {
		return area() * edges();
	}
}

record Grid(Map<Coordinate, Character> plots) {

	// Load the grid from a file based on the file path
	static Grid from(String filePath) throws IOException {
		Map<Coordinate, Character> plots = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			int y = 0;
			// Read each line and process each character in the line
			while ((line = reader.readLine()) != null) {
				int x = 0;
				for (char c : line.toCharArray()) {
					// Store each plot in the grid (coordinate -> plant type)
					plots.put(new Coordinate(x, y), c);
					x++;
				}
				y++;
			}
		}
		return new Grid(plots);
	}

	// Get the region of plots connected by the same plant type
	Region region(Coordinate coordinate, Set<Coordinate> visited) {
		var thisPlant = plots.get(coordinate); // Get the plant type at the given coordinate
		Map<Coordinate, Integer> bits = new HashMap<>();
		Deque<Coordinate> queue = new ArrayDeque<>();
		queue.addLast(coordinate); // Start from the initial coordinate

		// Perform a breadth-first search (BFS) to find all connected plots
		while (!queue.isEmpty()) {
			if (queue.size() > plots.size()) {
				throw new IllegalStateException(); // Avoid infinite loop
			}
			Coordinate c = queue.removeFirst();
			visited.add(c);
			int fences = 4; // Assume all sides are fences initially

			// Check all neighboring plots
			for (Coordinate neighbour : c.neighbours()) {
				var otherPlant = plots.get(neighbour);
				if (Objects.equals(otherPlant, thisPlant)) {
					--fences; // Reduce fence count if the plot is part of the same region
					if (!visited.contains(neighbour)) {
						if (!queue.contains(neighbour)) {
							queue.addLast(neighbour); // Add neighboring plot to queue if not visited
						}
					}
				}
			}
			bits.put(c, fences); // Store the fence count for the current plot
		}
		return new Region(bits); // Return the region with the plots
	}

	// Get all the regions in the grid
	List<Region> regions() {
		List<Region> regions = new ArrayList<>();
		Set<Coordinate> visited = new HashSet<>();
		// Iterate through all coordinates in the grid
		for (Coordinate coordinate : plots.keySet()) {
			if (!visited.contains(coordinate)) {
				// Add region for unvisited coordinates
				regions.add(region(coordinate, visited));
			}
		}
		return regions; // Return all the regions found
	}
}