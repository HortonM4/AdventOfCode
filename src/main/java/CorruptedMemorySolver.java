import java.io.*;
import java.util.regex.*;

public class CorruptedMemorySolver {
	public static void main(String[] args) {
		// Prompt the user for the file path
		BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter the file path: ");

		try {
			// Read the file path input from the user
			String filePath = consoleReader.readLine();

			// Read the contents of the file into a single string
			String fileContent = readFile(filePath);

			// Process the content to calculate the total sum of valid mul(X,Y) instructions
			int totalSum = calculateSumWithControlInstructions(fileContent);

			// Display the final result
			System.out.println("The total sum of enabled mul instructions is: " + totalSum);
		} catch (IOException e) {
			System.err.println("Error reading file path: " + e.getMessage());
		}
	}

	/**
	 * Reads the entire content of a file into a single string.
	 * @param filePath Path to the input file
	 * @return The content of the file as a string
	 * @throws IOException If there's an error reading the file
	 */
	public static String readFile(String filePath) throws IOException {
		StringBuilder contentBuilder = new StringBuilder();
		try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = fileReader.readLine()) != null) {
				contentBuilder.append(line).append("\n");
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + filePath);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Error reading the file: " + e.getMessage());
			System.exit(1);
		}
		return contentBuilder.toString();
	}

	/**
	 * Parses the corrupted memory to find valid mul(X,Y) instructions and computes their sum.
	 * Handles do() and don't() instructions to control whether multiplications are enabled.
	 * @param input The corrupted memory string
	 * @return The sum of all enabled multiplications
	 */
	public static int calculateSumWithControlInstructions(String input) {
		int totalSum = 0;

		// Regex patterns for instructions
		String mulRegex = "mul\\((\\d{1,3}),(\\d{1,3})\\)"; // Match mul(X,Y)
		String controlRegex = "(do\\(\\))|(don't\\(\\))";   // Match do() and don't()

		// Compile the regex patterns
		Pattern mulPattern = Pattern.compile(mulRegex);
		Pattern controlPattern = Pattern.compile(controlRegex);

		// Flags to control mul instructions
		boolean isEnabled = true;

		// Use a matcher to iterate through all instructions in the input
		Matcher matcher = Pattern.compile(mulRegex + "|" + controlRegex).matcher(input);

		while (matcher.find()) {
			String matchedInstruction = matcher.group();

			// Check if the matched instruction is a control instruction
			if (matchedInstruction.equals("do()")) {
				isEnabled = true;
				System.out.println("do() encountered: mul instructions are ENABLED.");
			} else if (matchedInstruction.equals("don't()")) {
				isEnabled = false;
				System.out.println("don't() encountered: mul instructions are DISABLED.");
			}
			// Check if the matched instruction is a valid mul(X,Y)
			else {
				if (isEnabled) {
					// Extract the two numbers using the mul regex
					Matcher mulMatcher = mulPattern.matcher(matchedInstruction);
					if (mulMatcher.matches()) {
						int x = Integer.parseInt(mulMatcher.group(1));
						int y = Integer.parseInt(mulMatcher.group(2));
						int product = x * y;
						totalSum += product;

						// Log the multiplication for debugging
						System.out.println("Found mul(" + x + "," + y + ") -> Product: " + product);
					}
				} else {
					System.out.println("Skipping " + matchedInstruction + " as mul instructions are DISABLED.");
				}
			}
		}

		return totalSum;
	}
}
