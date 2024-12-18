import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Day10 {

	private static final int[] dx = {-1, 1, 0, 0}; // Direction arrays for up, down, left, right
	private static final int[] dy = {0, 0, -1, 1};

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the file path: ");
		String filePath = scanner.nextLine();

		try {
			// Read the map from the file
			List<String> map = readMap(filePath);

			int totalScore = 0; // Part 1: Sum of scores
			int totalRating = 0; // Part 2: Sum of ratings
			int rows = map.size();
			int cols = map.get(0).length();

			// Iterate through the grid to find trailheads
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					if (map.get(i).charAt(j) == '0') {
						totalScore += countReachableNines(i, j, map); // Part 1
						totalRating += countDistinctTrails(i, j, map); // Part 2
					}
				}
			}

			// Output the results
			System.out.println("Part 1: The sum of the scores of all trailheads is: " + totalScore);
			System.out.println("Part 2: The sum of the ratings of all trailheads is: " + totalRating);

		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		}
	}

	// Method to read the map from a file
	private static List<String> readMap(String filePath) throws FileNotFoundException {
		List<String> map = new ArrayList<>();
		Scanner fileScanner = new Scanner(new File(filePath));

		while (fileScanner.hasNextLine()) {
			map.add(fileScanner.nextLine());
		}

		return map;
	}

	// Part 1: Count reachable 9's from a given trailhead
	private static int countReachableNines(int startX, int startY, List<String> map) {
		int rows = map.size();
		int cols = map.get(0).length();
		boolean[][] visited = new boolean[rows][cols];
		Set<String> reachableNines = new HashSet<>();

		// Stack for DFS
		Stack<int[]> stack = new Stack<>();
		stack.push(new int[]{startX, startY});
		visited[startX][startY] = true;

		// Perform DFS
		while (!stack.isEmpty()) {
			int[] current = stack.pop();
			int x = current[0];
			int y = current[1];

			// If we reach a 9, add its position to the set
			if (map.get(x).charAt(y) == '9') {
				reachableNines.add(x + "," + y);
			}

			// Explore all 4 possible directions
			for (int i = 0; i < 4; i++) {
				int nx = x + dx[i];
				int ny = y + dy[i];

				// Check if the new position is within bounds and is not visited
				if (nx >= 0 && nx < rows && ny >= 0 && ny < cols && !visited[nx][ny]) {
					int currentHeight = map.get(x).charAt(y) - '0';
					int nextHeight = map.get(nx).charAt(ny) - '0';

					// Ensure the elevation increases by exactly 1
					if (nextHeight == currentHeight + 1) {
						visited[nx][ny] = true;
						stack.push(new int[]{nx, ny});
					}
				}
			}
		}

		// Return the count of unique reachable 9's
		return reachableNines.size();
	}

	// Part 2: Count distinct trails from a given trailhead
	private static int countDistinctTrails(int startX, int startY, List<String> map) {
		int rows = map.size();
		int cols = map.get(0).length();
		int[][] memo = new int[rows][cols]; // Memoization for distinct paths
		for (int[] row : memo) Arrays.fill(row, -1);

		return dfsDistinctTrails(startX, startY, map, memo);
	}

	// Recursive DFS to count distinct trails with memoization
	private static int dfsDistinctTrails(int x, int y, List<String> map, int[][] memo) {
		int rows = map.size();
		int cols = map.get(0).length();

		// Base case: If we reach height 9, this is one distinct trail
		if (map.get(x).charAt(y) == '9') {
			return 1;
		}

		// If already computed, return the stored result
		if (memo[x][y] != -1) {
			return memo[x][y];
		}

		int distinctTrails = 0;

		// Explore all 4 possible directions
		for (int i = 0; i < 4; i++) {
			int nx = x + dx[i];
			int ny = y + dy[i];

			// Check if the new position is within bounds
			if (nx >= 0 && nx < rows && ny >= 0 && ny < cols) {
				int currentHeight = map.get(x).charAt(y) - '0';
				int nextHeight = map.get(nx).charAt(ny) - '0';

				// Ensure the elevation increases by exactly 1
				if (nextHeight == currentHeight + 1) {
					distinctTrails += dfsDistinctTrails(nx, ny, map, memo);
				}
			}
		}

		// Store the result in memoization table
		memo[x][y] = distinctTrails;
		return distinctTrails;
	}
}
