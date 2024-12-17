import java.io.*;
import java.util.*;

public class GuardPatrol {
	public static void main(String[] args) {
		// Step 1: Get the input file from the user
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the grid file name (e.g., Day6.txt): ");
		String filePath = scanner.nextLine();

		// Step 2: Read the map from the file
		char[][] map = readMap(filePath);

		// Step 3: Find the initial position of the guard and direction
		int startX = -1, startY = -1;
		char initialDirection = ' ';
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (map[i][j] == '^' || map[i][j] == 'v' || map[i][j] == '<' || map[i][j] == '>') {
					startX = i;
					startY = j;
					initialDirection = map[i][j];
					map[i][j] = '.'; // Mark the starting position as an empty space
					break;
				}
			}
			if (startX != -1) break; // Exit outer loop once found
		}

		// Step 4: Simulate the guard's movement
		Set<String> visitedPositions = new HashSet<>();
		int x = startX, y = startY;
		char direction = initialDirection;
		visitedPositions.add(x + "," + y); // Mark the starting position as visited

		// Directions array: index 0 -> up ('^'), 1 -> right ('>'), 2 -> down ('v'), 3 -> left ('<')
		int[] dx = {-1, 0, 1, 0}; // Up, Right, Down, Left
		int[] dy = {0, 1, 0, -1}; // Up, Right, Down, Left
		int currentDirection = direction == '^' ? 0 : direction == '>' ? 1 : direction == 'v' ? 2 : 3;

		while (true) {
			int newX = x + dx[currentDirection];
			int newY = y + dy[currentDirection];

			// Check if the guard has left the map
			if (newX < 0 || newX >= map.length || newY < 0 || newY >= map[0].length) {
				break;
			}

			// If there is an obstacle in front, turn right 90 degrees
			if (map[newX][newY] == '#') {
				currentDirection = (currentDirection + 1) % 4;
			} else {
				// Otherwise, move forward
				x = newX;
				y = newY;
				visitedPositions.add(x + "," + y);
			}
		}

		// Output the number of distinct positions visited
		System.out.println("Total distinct positions visited: " + visitedPositions.size());
	}

	// Step 2: Function to read the map from the file
	private static char[][] readMap(String filePath) {
		List<String> lines = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			System.err.println("Error reading the file: " + e.getMessage());
		}

		// Convert the lines into a 2D character array (map)
		char[][] map = new char[lines.size()][lines.get(0).length()];
		for (int i = 0; i < lines.size(); i++) {
			map[i] = lines.get(i).toCharArray();
		}
		return map;
	}
}
