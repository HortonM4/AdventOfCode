import java.io.*;
import java.util.*;

public class SetDifferenceCalculator {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter the file path for the combined set file: ");
		String filePath = scanner.nextLine();

		System.out.println("Processing file: " + filePath);

		List<Integer> leftList = new ArrayList<>();
		List<Integer> rightList = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

			String line;

			while ((line = reader.readLine()) != null) {
				System.out.println("Read line: " + line); // Log each line
				// Split the line into two numbers
				String[] numbers = line.trim().split("\\s+");
				if (numbers.length != 2) {
					System.err.println("Invalid line format: " + line);
					continue;
				}

				int num1 = Integer.parseInt(numbers[0]);
				int num2 = Integer.parseInt(numbers[1]);

				leftList.add(num1);
				rightList.add(num2);
			}

			System.out.println("Left List: " + leftList);
			System.out.println("Right List: " + rightList);

			// Sort both lists
			Collections.sort(leftList);
			Collections.sort(rightList);

			System.out.println("Sorted Left List: " + leftList);
			System.out.println("Sorted Right List: " + rightList);

			// Calculate the total difference
			long totalDifference = 0;
			for (int i = 0; i < leftList.size(); i++) {
				int diff = Math.abs(leftList.get(i) - rightList.get(i));
				System.out.println("Pair: (" + leftList.get(i) + ", " + rightList.get(i) + "), Difference: " + diff);
				totalDifference += diff;
			}

			System.out.println("The total distance between the lists is: " + totalDifference);

			// Calculate the similarity score
			long similarityScore = 0;
			Map<Integer, Integer> rightListFrequency = new HashMap<>();

			// Count the frequency of each number in the right list
			for (int num : rightList) {
				rightListFrequency.put(num, rightListFrequency.getOrDefault(num, 0) + 1);
			}

			// Calculate similarity score based on frequencies
			for (int num : leftList) {
				int frequency = rightListFrequency.getOrDefault(num, 0);
				similarityScore += num * frequency;
				System.out.println("Number: " + num + ", Frequency in Right List: " + frequency + ", Contribution to Similarity Score: " + (num * frequency));
			}

			System.out.println("The similarity score between the lists is: " + similarityScore);

		} catch (IOException e) {
			System.err.println("Error reading file: " + e.getMessage());
		} catch (NumberFormatException e) {
			System.err.println("Invalid number format in file: " + e.getMessage());
		}
	}
}
