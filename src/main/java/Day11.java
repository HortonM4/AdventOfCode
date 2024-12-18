import java.io.*;
import java.util.*;

public class Day11 {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		// Prompt the user to enter the file path
		System.out.print("Enter the file path: ");
		String filePath = scanner.nextLine();

		// Map to store the count of stones with the same value
		Map<Long, Long> stoneCounts = new HashMap<>();

		// Read the initial arrangement of stones from the file
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line = br.readLine();
			if (line != null) {
				// Parse the numbers and populate the map with initial counts
				for (String num : line.split("\\s+")) {
					long stone = Long.parseLong(num);
					stoneCounts.put(stone, stoneCounts.getOrDefault(stone, 0L) + 1);
				}
			}
		} catch (IOException e) {
			System.err.println("Error reading the file: " + e.getMessage());
			return;
		}

		// Ask the user for the number of blinks to simulate
		System.out.print("Enter the number of blinks: ");
		int blinks = scanner.nextInt();

		// Simulate the blinking process for the specified number of times
		for (int i = 1; i <= blinks; i++) {
			stoneCounts = blink(stoneCounts); // Process one blink
			// Calculate the total number of stones after this blink
			long totalStones = stoneCounts.values().stream().mapToLong(Long::longValue).sum();
			System.out.println("After blink " + i + ": " + totalStones + " stones");
		}
	}

	/**
	 * Simulates one blink by applying the transformation rules to all stones.
	 * @param stoneCounts A map where the key is the stone value and the value is its count.
	 * @return A new map representing the stone counts after the blink.
	 */
	private static Map<Long, Long> blink(Map<Long, Long> stoneCounts) {
		Map<Long, Long> newCounts = new HashMap<>();

		for (Map.Entry<Long, Long> entry : stoneCounts.entrySet()) {
			long stone = entry.getKey(); // Current stone value
			long count = entry.getValue(); // Count of stones with this value

			if (stone == 0) {
				// Rule 1: If the stone value is 0, replace it with a stone of value 1
				newCounts.put(1L, newCounts.getOrDefault(1L, 0L) + count);
			} else if (hasEvenDigits(stone)) {
				// Rule 2: If the stone has an even number of digits, split it into two stones
				String numStr = String.valueOf(stone);
				int mid = numStr.length() / 2;
				long left = Long.parseLong(numStr.substring(0, mid)); // Left half
				long right = Long.parseLong(numStr.substring(mid));  // Right half

				// Add the left and right parts to the new stone map
				newCounts.put(left, newCounts.getOrDefault(left, 0L) + count);
				newCounts.put(right, newCounts.getOrDefault(right, 0L) + count);
			} else {
				// Rule 3: If none of the other rules apply, multiply the stone by 2024
				long newStone = stone * 2024;
				newCounts.put(newStone, newCounts.getOrDefault(newStone, 0L) + count);
			}
		}

		return newCounts; // Return the updated map of stone counts
	}

	/**
	 * Helper function to check if a number has an even number of digits.
	 * @param number The stone value to check.
	 * @return True if the number has an even number of digits, false otherwise.
	 */
	private static boolean hasEvenDigits(long number) {
		int digits = String.valueOf(number).length(); // Convert number to string to get the digit count
		return digits % 2 == 0; // Check if the digit count is even
	}
}
