import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day17 {
	// The main method that starts the program execution
	public static void main(String[] args) throws Exception {
		// Create an instance of Day17Puzzle and call the solve method
		new Day17Puzzle().solve();
	}
}

// A class to represent the puzzle solution
class Day17Puzzle {
	// Declare a final variable that holds an instance of the Computer class
	final Computer computer;

	// Constructor for the Day17Puzzle class
	// It asks the user for a file path and loads the program from that file
	Day17Puzzle() throws Exception {
		// Use Scanner to get file path from user input
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the file path: "); // Prompt the user
		String filePath = scanner.nextLine(); // Read the input file path

		// Read the content of the file and store it in a string
		String program = readFile(filePath);

		// Create an instance of the Computer class by parsing the program string
		computer = Computer.from(program);
	}

	// Method to solve the puzzle
	void solve() {
		// Run the program and print the output as a comma-separated string
		System.out.println(computer.run().stream().map(String::valueOf).collect(Collectors.joining(",")));

		// Print the result of the self-replicator calculation
		System.out.println(computer.selfReplicator());
	}

	// Method to read file content and return it as a string
	private String readFile(String filePath) throws FileNotFoundException {
		// Create a Scanner object to read the file at the provided file path
		Scanner fileScanner = new Scanner(new File(filePath));

		// StringBuilder to build the file content as a string
		StringBuilder content = new StringBuilder();

		// Read each line of the file and append it to content
		while (fileScanner.hasNextLine()) {
			content.append(fileScanner.nextLine()).append("\n");
		}

		// Close the file scanner after reading the entire file
		fileScanner.close();

		// Return the entire content of the file as a string
		return content.toString();
	}
}

// A record class that represents the Computer
// It stores the initial values of registers and the program instructions
record Computer(int initialA, int initialB, int initialC, int[] instructions) {

	// Static method to create a Computer instance from a program string
	static Computer from(String program) {
		// Split the input program into two parts: register values and instructions
		var parts = program.split("\n\n");

		// Use a regular expression to match the initial values of registers A, B, and C
		var matcher = Pattern.compile(".*: (\\d+)\n.*: (\\d+)\n.*: (\\d+)").matcher(parts[0]);

		// If the regular expression does not match the expected format, throw an exception
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid program format");
		}

		// Create a new Computer instance with parsed register values and instructions
		return new Computer(
				Integer.parseInt(matcher.group(1)), // Parse register A value
				Integer.parseInt(matcher.group(2)), // Parse register B value
				Integer.parseInt(matcher.group(3)), // Parse register C value
				Arrays.stream(parts[1].split(":")[1].replaceAll("\\s", "").split(","))
						.mapToInt(Integer::parseInt) // Parse the program instructions
						.toArray());
	}

	// Helper method that returns a value based on an opcode and its operands
	long combo(int o, long a, long b, long c) {
		return switch (o) { // Using switch expression for cleaner code
			case 0, 1, 2, 3 -> o; // Return the opcode for these cases
			case 4 -> a;          // Return value of register A
			case 5 -> b;          // Return value of register B
			case 6 -> c;          // Return value of register C
			default -> throw new IllegalStateException("Unexpected opcode: " + o); // Handle unexpected opcodes
		};
	}

	// Method to run the program with the initial value of register A
	List<Integer> run() {
		return run(initialA);
	}

	// Method to run the program with a custom value for register A
	List<Integer> run(final long newA) {
		List<Integer> output = new ArrayList<>(); // List to store the program output
		int ip = 0; // Instruction pointer, starts at 0
		long a = newA; // Initialize register A with the provided value
		long b = initialB; // Initialize register B with the stored value
		long c = initialC; // Initialize register C with the stored value

		// Loop through the instructions until the end of the program
		while (ip < instructions.length) {
			int i = instructions[ip++]; // Get the instruction
			int o = instructions[ip++]; // Get the operand for the instruction

			// Process the instruction based on its type
			switch (i) {
				case 0 -> a = a >> combo(o, a, b, c); // Shift register A right by the operand
				case 1 -> b = b ^ o; // XOR register B with the operand
				case 2 -> b = combo(o, a, b, c) & 7; // Store the result of combo and apply modulo 8 to B
				case 3 -> { // Conditional jump if A is not zero
					if (a != 0) {
						ip = o; // Jump to the operand value as the new instruction pointer
					}
				}
				case 4 -> b = b ^ c; // XOR register B with register C
				case 5 -> output.add((int) combo(o, a, b, c) & 7); // Store the result in the output list
				case 6 -> b = a >> combo(o, a, b, c); // Shift register A right and store in B
				case 7 -> c = a >> combo(o, a, b, c); // Shift register A right and store in C
			}
		}
		// Return the output list after executing the program
		return output;
	}

	// Method to find the self-replicator value based on the program instructions
	long selfReplicator() {
		SortedSet<Long> candidates = new TreeSet<>(); // Set to store potential candidates
		candidates.add(0L); // Start with an initial candidate value of 0

		// Loop through each instruction in reverse order
		for (int i = 0; i < instructions.length; i++) {
			// Get the last instruction in the current part of the program
			var lastInstruction = instructions[instructions.length - i - 1];

			// Create a new set to store new candidates
			SortedSet<Long> newCandidates = new TreeSet<>();

			// Check each candidate from the previous step
			for (var old : candidates) {
				long candidate = old << 3; // Left shift the candidate by 3 bits

				// Try all 8 possible candidates by incrementing the value
				for (int j = 0; j < 8; j++) {
					// If the first output matches the last instruction, add the candidate to newCandidates
					if (run(candidate).get(0) == lastInstruction) {
						newCandidates.add(candidate);
					}
					candidate++; // Increment the candidate value for the next check
				}
			}
			// Update the set of candidates with the new candidates found in this step
			candidates = newCandidates;
		}
		// Return the smallest candidate that satisfies the condition
		return candidates.first();
	}
}
