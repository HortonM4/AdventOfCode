import java.io.*;
import java.util.*;

public class WordSearchSolver {

	// Directions for 8 possible ways: {row offset, col offset}
	private static final int[][] DIRECTIONS = {
			{0, 1},   // Right
			{0, -1},  // Left
			{1, 0},   // Down
			{-1, 0},  // Up
			{1, 1},   // Diagonal Down-Right
			{1, -1},  // Diagonal Down-Left
			{-1, 1},  // Diagonal Up-Right
			{-1, -1}  // Diagonal Up-Left
	};
	private static final String TARGET_WORD = "XMAS";

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

			// Count occurrences of the target word
			int totalOccurrences = countWordOccurrences(charGrid, TARGET_WORD);

			System.out.println("The word \"" + TARGET_WORD + "\" appears " + totalOccurrences + " times in the grid.");
		} catch (IOException e) {
			System.err.println("Error reading the file: " + e.getMessage());
		}
	}

	/**
	 * Reads the word search grid from a file.
	 * @param filePath The path to the input file.
	 * @return A list of strings representing the rows of the grid.
	 * @throws IOException If there is an error reading the file.
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
	 * @param grid The list of strings representing the grid.
	 * @return A 2D character array representing the grid.
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
	 * Counts the occurrences of the target word in the grid.
	 * @param grid The 2D character array representing the word search grid.
	 * @param word The word to search for.
	 * @return The total number of occurrences of the word in all 8 directions.
	 */
	private static int countWordOccurrences(char[][] grid, String word) {
		int rowCount = grid.length;
		int colCount = grid[0].length;
		int wordLength = word.length();
		int totalOccurrences = 0;

		// Iterate through each cell in the grid
		for (int row = 0; row < rowCount; row++) {
			for (int col = 0; col < colCount; col++) {
				// Check all 8 directions
				for (int[] direction : DIRECTIONS) {
					if (isWordInDirection(grid, word, row, col, direction[0], direction[1])) {
						totalOccurrences++;
					}
				}
			}
		}

		return totalOccurrences;
	}

	/**
	 * Checks if the target word exists starting from a given position in a specific direction.
	 * @param grid The 2D character array grid.
	 * @param word The target word to search for.
	 * @param startRow The starting row index.
	 * @param startCol The starting column index.
	 * @param rowStep The row increment for the direction.
	 * @param colStep The column increment for the direction.
	 * @return True if the word is found in the specified direction, otherwise false.
	 */
	private static boolean isWordInDirection(char[][] grid, String word, int startRow, int startCol, int rowStep, int colStep) {
		int rowCount = grid.length;
		int colCount = grid[0].length;
		int wordLength = word.length();

		// Check if the word can fit in the given direction
		for (int i = 0; i < wordLength; i++) {
			int currentRow = startRow + i * rowStep;
			int currentCol = startCol + i * colStep;

			// Check boundaries
			if (currentRow < 0 || currentRow >= rowCount || currentCol < 0 || currentCol >= colCount) {
				return false;
			}

			// Compare the character
			if (grid[currentRow][currentCol] != word.charAt(i)) {
				return false;
			}
		}

		return true;
	}
}
