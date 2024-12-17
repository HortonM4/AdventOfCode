import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day05 {

	/**
	 * Custom comparator for page numbers based on their ordering rules.
	 * This comparator determines if one page should come before another according to the given rules.
	 */
	static class PageComparator implements Comparator<Integer> {
		private final Map<Integer, Set<Integer>> map;

		public PageComparator(Map<Integer, Set<Integer>> map) {
			this.map = map;
		}

		@Override
		public int compare(Integer i1, Integer i2) {
			if (map.get(i1).contains(i2)) {
				// i1 must be before i2, so return 1 to indicate i1 is 'greater'
				return 1;
			} else {
				// i1 can be before or after i2 based on other rules, so we return -1 to indicate i1 is 'less'
				return -1;
			}
		}
	}

	// An empty set used as a default when checking for rules in the map
	private static final Set<Integer> emptySet = new LinkedHashSet<>();

	/**
	 * Checks if the given list of pages respects the ordering rules.
	 *
	 * @param map The map containing the ordering rules.
	 * @param pages The list of pages to check.
	 * @return true if the pages are in valid order, false otherwise.
	 */
	private static boolean valid(Map<Integer, Set<Integer>> map, List<Integer> pages) {
		for (int i = 0; i < pages.size()-1; i++) {
			if (!map.getOrDefault(pages.get(i), emptySet).contains(pages.get(i+1))) {
				return false; // The current page does not come before the next one according to rules
			}
		}
		return true;
	}

	/**
	 * Calculates the middle page number for Part 1 if the page order is valid, otherwise returns 0.
	 *
	 * @param map The map of ordering rules.
	 * @param pages The list of pages to evaluate.
	 * @return The middle page number or 0 if not valid.
	 */
	public static int partOneMiddle(Map<Integer, Set<Integer>> map, List<Integer> pages) {
		if (valid(map, pages)) {
			return pages.get(pages.size()/2); // Middle element for odd-numbered list
		} else {
			return 0;
		}
	}

	/**
	 * Calculates the middle page number for Part 2 if the page order is invalid.
	 * Sorts the pages according to the rules and returns the middle number.
	 *
	 * @param map The map of ordering rules.
	 * @param pages The list of pages to evaluate.
	 * @return The middle page number after sorting if invalid, otherwise 0.
	 */
	public static int partTwoMiddle(Map<Integer, Set<Integer>> map, List<Integer> pages) {
		if (valid(map, pages)) {
			return 0; // If valid, no sorting needed, return 0 as per requirement
		} else {
			pages.sort(new PageComparator(map)); // Sort pages based on custom rules
			return pages.get(pages.size()/2); // Middle element after sorting
		}
	}

	public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);

		// Prompt user to enter the file path for input
		System.out.print("Please enter the path to the input file: ");
		String filePath = scanner.nextLine();

		// Read the entire content of the file as a string
		String input = Files.readString(Path.of(filePath));

		scanner.close(); // Close the scanner to free system resources

		// Split the input into sections: rules and pages
		String[] section = input.split("\n\n");
		String[] ruleSection = section[0].split("\n"); // Each line contains a rule
		String[] pageSection = section[1].split("\n"); // Each line contains a page update

		// Map to store page ordering rules
		Map<Integer, Set<Integer>> map = new HashMap<>();
		for (String rule : ruleSection) {
			String[] R = rule.split("\\|");
			int from = Integer.parseInt(R[0]); // Parse the page that must come first
			int to = Integer.parseInt(R[1]); // Parse the page that must come after
			Set<Integer> set = map.getOrDefault(from, new LinkedHashSet<>());
			set.add(to); // Add the rule: 'from' must come before 'to'
			map.put(from, set);
		}

		int partOneSum = 0; // Sum for part one
		int partTwoSum = 0; // Sum for part two

		// Process each page update
		for (String pageString : pageSection) {
			List<Integer> pages = Arrays.stream(pageString.split(","))
					.mapToInt(Integer::parseInt)
					.boxed() // Convert int to Integer for list
					.collect(Collectors.toList());

			partOneSum += partOneMiddle(map, pages);
			partTwoSum += partTwoMiddle(map, pages);
		}

		// Output results
		System.out.println("Day 5 part 1: " + partOneSum);
		System.out.println("Day 5 part 2: " + partTwoSum);
	}
}