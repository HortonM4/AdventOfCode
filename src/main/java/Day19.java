import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day19 {
	public static void main(String[] args) throws Exception {
		new Day19Puzzle().solve();
	}
}

class Day19Puzzle {
	// Set of all valid towel sizes (lengths) read from the input file
	final Set<String> towels;
	// List of all patterns to be checked against the valid towels
	final List<String> patterns;
	// Maximum length of a towel size
	final int maxLen;
	// Map to store solutions (number of ways to form a pattern) for previously seen patterns
	// This is used for memoization to avoid redundant calculations
	Map<String, Long> solutions = new HashMap<>();

	Day19Puzzle() throws Exception {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the file path: ");
		String filePath = scanner.nextLine();

		// Read the input file content as a string
		var input = Files.readString(Paths.get(filePath));
		// Split the input into two parts based on two empty lines
		var parts = input.split("\n\n");
		// Read the first part (comma separated list) into a set of valid towel sizes
		towels = Arrays.stream(parts[0].split(", ")).collect(Collectors.toSet());
		// Read the second part (list of patterns) into a list of strings
		patterns = Arrays.stream(parts[1].split("\n")).toList();
		// Find the maximum length of a towel size
		maxLen = towels.stream().mapToInt(String::length).max().orElseThrow();
	}

	// This function recursively calculates the number of ways to form a pattern using valid towels
	// It uses memoization to store solutions for previously seen patterns to avoid redundant calculations
	long solutions(String pattern) {
		if (pattern.isEmpty()) {
			// Empty pattern can be formed in 1 way
			return 1;
		}
		if (solutions.containsKey(pattern)) {
			// If the solution for this pattern is already calculated, return it from the map
			return solutions.get(pattern);
		}
		long total = 0;
		// Try all possible towel sizes (up to the maximum length and length of the pattern)
		for (int i = 1; i <= maxLen && i <= pattern.length(); i++) {
			String subPattern = pattern.substring(0, i);
			// Check if the current towel size is valid (present in the set of valid sizes)
			if (towels.contains(subPattern)) {
				// If the current towel size is valid, recursively calculate the number of ways to form the remaining pattern (after removing the current towel size)
				// Add the number of ways to form the remaining pattern to the total count
				total += solutions(pattern.substring(i));
			}
		}
		// Store the solution for the current pattern in the map before returning
		solutions.put(pattern, total);
		return total;
	}

	void solve() {
		// Count the number of patterns that can be formed with at least one valid combination of towels
		System.out.println(patterns.stream().filter(p -> solutions(p) > 0).count());
		// Calculate the total number of ways to form all the patterns
		System.out.println(patterns.stream().mapToLong(this::solutions).sum());
	}
}