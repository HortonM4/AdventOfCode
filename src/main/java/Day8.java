import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;

public class Day8 {

	// A record to represent the coordinates (y, x) of an antenna or an antinode
	private record Coords(int y, int x) {}

	// 2D array to store the map of the antenna layout
	private char[][] map;

	// A map to store antennas by their character type (e.g., 'A', 'B', etc.)
	private HashMap<Character, ArrayList<Coords>> antennas = new HashMap<>();

	// Part 1 - Finds and calculates unique antinode locations (doesn't include resonance)
	public void part1() {
		// Populate the antenna positions on the map
		populateAntennaPositions();

		// Calculate antinode positions for Part 1 (without resonance)
		Set<Coords> antinodes = new HashSet<>(calculateAntinodePositions(false));

		// Print the result for Part 1
		System.out.println("Unique Antinode Locations (Part 1): " + antinodes.size());
	}

	// Part 2 - Finds and calculates unique antinode locations (including resonance)
	public void part2() {
		// Calculate antinode positions for Part 2 (with resonance)
		Set<Coords> antinodes = new HashSet<>(calculateAntinodePositions(true));

		// Print the result for Part 2
		System.out.println("Unique Antinode Locations (Part 2): " + antinodes.size());
	}

	// Populates the antennas map with coordinates for each antenna type
	private void populateAntennaPositions() {
		// Loop through the map to find positions of each antenna
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[i].length; j++) {
				if (map[i][j] != '.') {  // If it's not an empty space ('.')
					// Add the coordinates of the antenna to the corresponding character's list
					ArrayList<Coords> coords = antennas.getOrDefault(map[i][j], new ArrayList<>());
					coords.add(new Coords(i, j));
					antennas.put(map[i][j], coords);  // Update the map of antennas
				}
			}
		}
	}

	// Calculates the antinode positions based on the antenna positions
	// Optionally, can include resonance (repeated antinodes) if `resonateHarmonics` is true
	private Set<Coords> calculateAntinodePositions(boolean resonateHarmonics) {
		Set<Coords> antinodes = new HashSet<>();  // Set to store unique antinode positions

		// Loop through each type of antenna
		for (ArrayList<Coords> value : antennas.values()) {
			if (value.size() > 1) {  // Only consider antenna types with more than one antenna
				for (int i = 0; i < value.size(); i++) {
					int y = value.get(i).y;  // y-coordinate of the first antenna
					int x = value.get(i).x;  // x-coordinate of the first antenna
					for (int j = i + 1; j < value.size(); j++) {
						int compareY = value.get(j).y;  // y-coordinate of the second antenna
						int compareX = value.get(j).x;  // x-coordinate of the second antenna

						// Calculate the differences in coordinates (y and x)
						int yDiff = y - compareY;
						int xDiff = x - compareX;

						// Calculate potential antinode positions based on the antenna distances
						int antinode1Y = y + yDiff;
						int antinode1X = x + xDiff;

						int antinode2Y = compareY - yDiff;
						int antinode2X = compareX - xDiff;

						// Add the first antinode and check if it stays within map bounds
						while (antinode1Y < map.length && antinode1Y >= 0 && antinode1X < map[y].length && antinode1X >= 0) {
							antinodes.add(new Coords(antinode1Y, antinode1X));  // Add the antinode location

							if (!resonateHarmonics) break;  // If resonance is not required, stop here

							// Move to the next possible antinode along the same direction
							antinode1Y += yDiff;
							antinode1X += xDiff;
						}

						// Add the second antinode and check if it stays within map bounds
						while (antinode2Y < map.length && antinode2Y >= 0 && antinode2X < map[y].length && antinode2X >= 0) {
							antinodes.add(new Coords(antinode2Y, antinode2X));  // Add the antinode location

							if (!resonateHarmonics) break;  // If resonance is not required, stop here

							// Move to the next possible antinode along the opposite direction
							antinode2Y -= yDiff;
							antinode2X -= xDiff;
						}

						// If resonance is enabled, include the original antennas as antinodes as well
						if (resonateHarmonics) {
							antinodes.add(new Coords(y, x));
							antinodes.add(new Coords(compareY, compareX));
						}
					}
				}
			}
		}

		return antinodes;  // Return the set of unique antinode locations
	}

	// Reads and parses the input map file, which contains the antenna layout
	public void parseInput() {
		// Use Scanner to prompt the user for the file path of the map
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the map file name (e.g., AntennaMap.txt): ");
		String filePath = scanner.nextLine();

		try {
			// Create a scanner to read the file content
			Scanner fileScanner = new Scanner(new File(filePath));
			ArrayList<String> lines = new ArrayList<>();

			// Read each line of the file and store it in a list
			while (fileScanner.hasNextLine()) {
				lines.add(fileScanner.nextLine());
			}

			// Initialize the map array with the appropriate dimensions
			map = new char[lines.size()][lines.get(0).length()];

			// Convert each line into a char array and populate the map
			for (int y = 0; y < lines.size(); y++) {
				map[y] = lines.get(y).toCharArray();
			}

			fileScanner.close();  // Close the file scanner
		} catch (FileNotFoundException e) {
			// Handle the exception if the file is not found
			System.out.println("File not found: " + e.getMessage());
		}
	}

	// Main method to run the program
	public static void main(String[] args) {
		Day8 day = new Day8();

		// Parse the input file (map of antennas)
		day.parseInput();

		// Run Part 1 to calculate unique antinode locations (without resonance)
		day.part1();

		// Run Part 2 to calculate unique antinode locations (with resonance)
		day.part2();
	}
}
