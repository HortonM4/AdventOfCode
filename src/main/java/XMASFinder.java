import java.io.*;
import java.util.*;

public class XMASFinder {

	private static final String MAS = "MAS"; // The word to search
	private static final int MAS_LENGTH = MAS.length(); // Length of the word
	private static final int[][] TOP_LEFT_TO_BOTTOM_RIGHT = {{-1, -1}, {0, 0}, {1, 1}}; // ↘ diagonal
	private static final int[][] TOP_RIGHT_TO_BOTTOM_LEFT = {{-1, 1}, {0, 0}, {1, -1}}; // ↙ diagonal

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the file path containing the word search grid: ");
		String filePath = scanner.nextLine();

		try {
			// Read the grid from the input file
			List<String> grid = readGridFromFile(filePath);
			int rowCount = grid.size();
			int colCount = grid.get(0).length();

			System.out.println("Grid successfully loaded. Rows: " + rowCount + ", Columns: " + colCount);

			// Convert the grid into a 2D character array
			char[][] charGrid = convertToCharGrid(grid);

			// Count occurrences of X-MAS patterns
			int totalOccurrences = countXMASPatterns(charGrid);

			System.out.println("The X-MAS pattern appears " + totalOccurrences + " times in the grid.");
		} catch (IOException e) {
			System.err.println("Error reading the file: " + e.getMessage());
		}
	}

	/**
	 * Reads the word search grid from a file.
	 */
	private static List<String> readGridFromFile(String filePath) throws IOException {
		List<String> grid = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				grid.add(line.trim());
			}
		}
		return grid;
	}

	/**
	 * Converts a list of strings into a 2D character array.
	 */
	private static char[][] convertToCharGrid(List<String> grid) {
		int rows = grid.size();
		int cols = grid.get(0).length();
		char[][] charGrid = new char[rows][cols];

		for (int i = 0; i < rows; i++) {
			charGrid[i] = grid.get(i).toCharArray();
		}
		return charGrid;
	}

	/**
	 * Counts the occurrences of the X-MAS pattern in the grid.
	 */
	private static int countXMASPatterns(char[][] grid) {
		int rowCount = grid.length;
		int colCount = grid[0].length;
		int totalOccurrences = 0;

		// Iterate through each cell in the grid
		for (int row = 0; row < rowCount; row++) {
			for (int col = 0; col < colCount; col++) {
				// Check for X-MAS centered at (row, col)
				if (isXMASAt(grid, row, col)) {
					totalOccurrences++;
				}
			}
		}
		return totalOccurrences;
	}

	/**
	 * Checks if an X-MAS pattern is centered at a given position.
	 */
	private static boolean isXMASAt(char[][] grid, int centerRow, int centerCol) {
		return checkMAS(grid, centerRow, centerCol, TOP_LEFT_TO_BOTTOM_RIGHT) &&
				checkMAS(grid, centerRow, centerCol, TOP_RIGHT_TO_BOTTOM_LEFT);
	}

	/**
	 * Checks if the "MAS" sequence appears in the specified direction.
	 * @param grid The grid.
	 * @param centerRow The center row of the X.
	 * @param centerCol The center column of the X.
	 * @param direction The direction offsets to check.
	 * @return True if "MAS" appears in this direction, else false.
	 */
	private static boolean checkMAS(char[][] grid, int centerRow, int centerCol, int[][] direction) {
		return checkWordInDirection(grid, centerRow, centerCol, direction, MAS) ||
				checkWordInDirection(grid, centerRow, centerCol, direction, new StringBuilder(MAS).reverse().toString());
	}

	/**
	 * Checks if a word appears starting from the center in a given direction.
	 */
	private static boolean checkWordInDirection(char[][] grid, int centerRow, int centerCol, int[][] direction, String word) {
		for (int i = 0; i < word.length(); i++) {
			int row = centerRow + direction[i][0];
			int col = centerCol + direction[i][1];

			// Check boundaries
			if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length) {
				return false;
			}

			// Compare the character
			if (grid[row][col] != word.charAt(i)) {
				return false;
			}
		}
		return true;
	}
}
