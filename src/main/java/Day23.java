import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day23 {
	public static void main(String[] args) throws Exception {
		// Entry point of the program
		new Day23Puzzle().solve();
	}
}

class Day23Puzzle {
	private final Set<Set<String>> computerPairs; // Stores all computer pairs from the input
	private final Set<String> computers; // Set of all unique computers

	Day23Puzzle() throws Exception {
		// Scanner to allow user to input the file path dynamically
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the file path: "); // Prompt for input
		String filePath = scanner.nextLine(); // Read the file path

		// Read input file and process it into computer pairs and computers set
		try (var input = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
			// Create a set of pairs from the input file
			computerPairs = input.lines()
					.map(Day23Puzzle::pair) // Convert each line to a pair of computers
					.collect(Collectors.toSet());

			// Create a set of all unique computers
			computers = computerPairs.stream()
					.flatMap(Set::stream) // Flatten the pairs to individual computers
					.collect(Collectors.toSet());
		}
	}

	// Helper method to convert a line (e.g., "A-B") into a pair (Set) of computers
	static Set<String> pair(String line) {
		return Arrays.stream(line.split("-")) // Split the line by "-"
				.collect(Collectors.toSet()); // Collect as a set
	}

	// Counts the number of triplets in the given set that contain a computer starting with "t"
	long countTripletsWithT(Set<Set<String>> triplets) {
		return triplets.stream()
				.filter(s -> s.stream().anyMatch(t -> t.startsWith("t"))) // Check if any computer starts with "t"
				.count(); // Count such triplets
	}

	// Expands the given set of cliques by adding one more computer to each clique
	Set<Set<String>> embiggen(Set<Set<String>> cliques) {
		Set<Set<String>> embiggened = new HashSet<>();
		for (var clique : cliques) {
			// Find all computers not in the current clique
			var others = new HashSet<>(computers);
			others.removeAll(clique);

			// Check if each "other" can form a valid pair with all computers in the current clique
			for (var other : others) {
				if (clique.stream().allMatch(s -> computerPairs.contains(Set.of(s, other)))) {
					// Create a new, larger clique and add it to the result
					var biggerClique = new HashSet<>(clique);
					biggerClique.add(other);
					embiggened.add(biggerClique);
				}
			}
		}
		return embiggened; // Return the set of expanded cliques
	}

	// Solves the puzzle and prints the results
	void solve() {
		// Start with the initial set of pairs as cliques
		var cliques = embiggen(computerPairs);

		// Count and print the triplets containing a computer starting with "t"
		System.out.println(countTripletsWithT(cliques));

		// Expand cliques iteratively until no more expansion is possible
		for (var bigger = embiggen(cliques); !bigger.isEmpty(); bigger = embiggen(bigger)) {
			cliques = bigger; // Update cliques with the expanded set
		}

		// Print the largest clique as a comma-separated string of computer names
		System.out.println(cliques.iterator()
				.next()
				.stream()
				.sorted(String::compareTo)
				.collect(Collectors.joining(",")));
	}
}
