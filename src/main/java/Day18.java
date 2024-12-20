import java.io.BufferedReader;  // Importing classes to read files
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;  // Used for dynamic arrays
import java.util.HashSet;  // Used for storing unique elements
import java.util.List;
import java.util.PriorityQueue;  // Used for sorting and retrieving elements with the highest priority
import java.util.Scanner;  // Used for reading user input

public class Day18 {

	// Constants defining the size of the grid
	private static final int W = 71;  // Width of the grid
	private static final int H = 71;  // Height of the grid
	private static final int N = 1024;  // Number of corrupted points to be considered initially

	public static void main(String[] args) throws IOException {
		// Start tracking time for performance measurement
		long start = System.currentTimeMillis();

		// Prompting user to enter the file path where the corrupted coordinates are stored
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the file path: ");
		String filePath = scanner.nextLine();

		// Reading the file containing corrupted positions and storing them in a list
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		List<Pos> corrupt = new ArrayList<>();  // List to store the corrupted positions
		String s;
		// Read each line from the file
		while ((s = br.readLine()) != null) {
			String[] line = s.split(",");  // Split the line into x and y coordinates
			corrupt.add(new Pos(Integer.parseInt(line[0]), Integer.parseInt(line[1])));
		}

		// HashSet to store the positions where bytes have fallen (corrupted positions)
		HashSet<Pos> fallen = new HashSet<>();
		// Initially, mark the first N corrupted positions as fallen
		for (int i = 0; i < N; i++) {
			fallen.add(corrupt.get(i));
		}
		// Find the minimum number of steps to reach the destination after the first N corruptions
		System.out.println(steps(fallen));

		// Now, we process the rest of the corrupted positions and find the first one where no path exists
		for (int i = N; i < corrupt.size(); i++) {
			fallen.add(corrupt.get(i));  // Add the current corrupted position to the fallen set
			// Call the 'steps' method to calculate the number of steps to reach the end point
			int res = steps(fallen);
			// If no valid path is found (-1), print the current corrupted position and stop
			if (res == -1) {
				System.out.println(corrupt.get(i).col + "," + corrupt.get(i).row);
				break;
			}
		}

		// End time measurement and display the duration of the program
		long end = System.currentTimeMillis();
		System.out.println((end - start) + " ms");
	}

	// Method to calculate the minimum steps to reach the destination, considering the fallen corrupted positions
	private static int steps(HashSet<Pos> fallen) {
		// Starting point (0, 0) - top-left corner
		Pos start = new Pos(0, 0);
		// End point (H-1, W-1) - bottom-right corner
		Pos end = new Pos(H - 1, W - 1);

		// Priority queue used to explore positions in the grid by the least number of steps (Dijkstra's algorithm)
		PriorityQueue<State> queue = new PriorityQueue<>();
		// HashSet to keep track of visited positions to avoid re-exploring them
		HashSet<Pos> visited = new HashSet<>();
		// Mark the start position as visited and add it to the queue with 0 steps
		visited.add(start);
		queue.add(new State(start, 0));

		// Explore the grid until there are no more positions to explore
		while (!queue.isEmpty()) {
			// Retrieve and remove the position with the least number of steps from the queue
			State curr = queue.poll();
			// If the current position is the destination, return the number of steps taken
			if (curr.pos.equals(end)) {
				return curr.steps;
			}
			// Try all 4 possible directions (North, East, South, West)
			for (Direction dir : Direction.values()) {
				// Calculate the next position by applying the direction
				Pos stepped = new Pos(curr.pos.col + dir.c, curr.pos.row + dir.r);
				// Check if the next position is out of bounds (invalid)
				if (stepped.col < 0 || stepped.col >= H || stepped.row < 0 || stepped.row >= W) {
					continue;
				}
				// If the next position is not corrupted and hasn't been visited yet
				if (!fallen.contains(stepped) && !visited.contains(stepped)) {
					visited.add(stepped);  // Mark the position as visited
					queue.add(new State(stepped, curr.steps + 1));  // Add the new state to the queue
				}
			}
		}
		// If no valid path is found, return -1
		return -1;
	}

	// Record class representing the state of the search, including a position and the number of steps taken
	record State(Pos pos, int steps) implements Comparable<State> {
		// Comparison method to ensure the queue orders states by the number of steps (for Dijkstra's algorithm)
		@Override
		public int compareTo(State o) {
			return steps - o.steps;  // Compare based on the number of steps
		}
	}

	// Record class representing a position (x, y) in the grid
	record Pos(long col, long row) {
	}

	// Enum to represent the four possible directions (North, East, South, West)
	enum Direction {
		NORTH(-1, 0),  // Move up (decrease row)
		EAST(0, 1),    // Move right (increase column)
		SOUTH(1, 0),   // Move down (increase row)
		WEST(0, -1);   // Move left (decrease column)

		final int r;  // Row change
		final int c;  // Column change

		// Constructor to initialize the direction with its row and column changes
		Direction(int r, int c) {
			this.r = r;
			this.c = c;
		}
	}
}
