import java.util.*;
import java.io.*;

// This program reads a file containing rows of integers and determines
// how many rows are "safe". A row is considered safe if it is strictly
// increasing or decreasing within a defined difference or can be made safe
// by removing one element.
public class SafeRowChecker {

	public static void main(String[] args) {
		// Create a scanner to read user input
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the file path of the document:");
		String filePath = scanner.nextLine(); // Read the file path from the user

		// Use a try-with-resources block to ensure the BufferedReader is closed automatically
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line; // Holds each line read from the file
			int safeRowCount = 0; // Counter for the number of safe rows

			// Loop through each line in the file
			while ((line = br.readLine()) != null) {
				// Split the line into parts based on whitespace and parse as integers
				String[] parts = line.trim().split("\\s+");
				int[] levels = Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();

				// Check if the row is safe or can be made safe
				if (isSafeRow(levels) || canBeMadeSafe(levels)) {
					safeRowCount++; // Increment the safe row counter
				}
			}

			// Output the total number of safe rows
			System.out.println("Total safe rows: " + safeRowCount);
		} catch (IOException e) {
			// Handle any IO errors that occur during file reading
			System.out.println("Error reading the file: " + e.getMessage());
		}
	}

	// Determines if a row is safe by checking if it is strictly increasing or decreasing
	private static boolean isSafeRow(int[] levels) {
		if (levels.length < 2) {
			return true; // Rows with fewer than 2 levels are inherently safe
		}

		boolean increasing = true;  // Flag to check for strictly increasing sequence
		boolean decreasing = true;  // Flag to check for strictly decreasing sequence

		// Iterate through the row to check differences between adjacent elements
		for (int i = 1; i < levels.length; i++) {
			int diff = levels[i] - levels[i - 1]; // Calculate the difference between adjacent levels

			// If the difference is outside the range [1, 3], the row is not safe
			if (Math.abs(diff) < 1 || Math.abs(diff) > 3) {
				return false;
			}

			// Update flags based on the difference's direction
			if (diff < 0) {
				increasing = false; // Not strictly increasing
			} else if (diff > 0) {
				decreasing = false; // Not strictly decreasing
			}
		}

		// The row is safe if it is either strictly increasing or strictly decreasing
		return increasing || decreasing;
	}

	// Determines if a row can be made safe by removing one element
	private static boolean canBeMadeSafe(int[] levels) {
		// Iterate through each element in the row
		for (int i = 0; i < levels.length; i++) {
			// Create a new array excluding the current element
			int[] modifiedLevels = new int[levels.length - 1];
			for (int j = 0, k = 0; j < levels.length; j++) {
				if (j != i) {
					modifiedLevels[k++] = levels[j];
				}
			}

			// Check if the modified row is safe
			if (isSafeRow(modifiedLevels)) {
				return true; // The row can be made safe by removing this element
			}
		}
		return false; // The row cannot be made safe by removing any single element
	}
}
