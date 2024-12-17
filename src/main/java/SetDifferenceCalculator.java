import java.io.*;
import java.util.*;

public class SetDifferenceCalculator {
	public static void main(String[] args) {
		// Create a Scanner object to read input from the console
		Scanner scanner = new Scanner(System.in);

		// Prompt the user to provide the path of the input file
		System.out.print("Enter the file path for the combined set file: ");
		String filePath = scanner.nextLine();

		// Display the file path being processed
		System.out.println("Processing file: " + filePath);

		// Create two lists to hold numbers from the two columns in the file
		List<Integer> leftList = new ArrayList<>();
		List<Integer> rightList = new ArrayList<>();

		// Try-with-resources to automatically close resources (BufferedReader)
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

			String line; // Temporary variable to hold each line from the file

			// Read the file line by line
			while ((line = reader.readLine()) != null) {
				System.out.println("Read line: " + line); // Log each line for debugging

				// Split the line into two numbers using whitespace as the delimiter
				String[] numbers = line.trim().split("\\s+");

				// Validate the line format (it should contain exactly two numbers)
				if (numbers.length != 2) {
					System.err.println("Invalid line format: " + line);
					continue; // Skip invalid lines
				}

				// Parse the two numbers from the split strings
				int num1 = Integer.parseInt(numbers[0]);
				int num2 = Integer.parseInt(numbers[1]);

				// Add the numbers to their respective lists
				leftList.add(num1);
				rightList.add(num2);
			}

			// Log the unsorted lists
			System.out.println("Left List: " + leftList);
			System.out.println("Right List: " + rightList);

			// Sort both lists to ensure the numbers are in ascending order
			Collections.sort(leftList);
			Collections.sort(rightList);

			// Log the sorted lists
			System.out.println("Sorted Left List: " + leftList);
			System.out.println("Sorted Right List: " + rightList);

			// Variable to hold the total difference between corresponding numbers
			long totalDifference = 0;

			// Loop through both lists to calculate the absolute difference
			for (int i = 0; i < leftList.size(); i++) {
				int diff = Math.abs(leftList.get(i) - rightList.get(i)); // Calculate absolute difference
				System.out.println("Pair: (" + leftList.get(i) + ", " + rightList.get(i) + "), Difference: " + diff);
				totalDifference += diff; // Accumulate the difference
			}

			// Display the total "distance" (sum of differences)
			System.out.println("The total distance between the lists is: " + totalDifference);

			// Variable to hold the similarity score
			long similarityScore = 0;

			// Map to store the frequency of numbers in the right list
			Map<Integer, Integer> rightListFrequency = new HashMap<>();

			// Populate the frequency map for the right list
			for (int num : rightList) {
				rightListFrequency.put(num, rightListFrequency.getOrDefault(num, 0) + 1);
			}

			// Calculate the similarity score by checking the presence of numbers in the left list
			for (int num : leftList) {
				int frequency = rightListFrequency.getOrDefault(num, 0); // Get frequency of the number in the right list
				similarityScore += num * frequency; // Calculate contribution to the similarity score
				System.out.println("Number: " + num + ", Frequency in Right List: " + frequency +
						", Contribution to Similarity Score: " + (num * frequency));
			}

			// Display the calculated similarity score
			System.out.println("The similarity score between the lists is: " + similarityScore);

		} catch (IOException e) {
			// Handle file input/output errors (e.g., file not found, read failure)
			System.err.println("Error reading file: " + e.getMessage());
		} catch (NumberFormatException e) {
			// Handle invalid number formats in the file
			System.err.println("Invalid number format in file: " + e.getMessage());
		}
	}
}
