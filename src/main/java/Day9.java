import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Day9 {
	public static void main(String[] args) throws FileNotFoundException {
		// Use Scanner to read input file
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the file path: ");
		String filePath = scanner.nextLine();  // Accept file path as input

		// Read file content
		String input = new Scanner(new File(filePath)).nextLine();
		int[] filesystem = new int[input.length()];
		for (int i = 0; i < filesystem.length; i++) {
			filesystem[i] = input.charAt(i) - '0';
		}

		// Call the methods to solve the parts
		solvePartOne(filesystem);
		solvePartTwo(filesystem);

		scanner.close();  // Close the scanner
	}

	private static void solvePartOne(int[] filesystem) {
		long checksum = 0;
		int currIndex = 0;

		int left = 0;
		int right = filesystem.length - 1;
		int spaceNeeded = filesystem[right];
		while (left < right) {
			for (int i = 0; i < filesystem[left]; i++) {
				checksum += (long) (left / 2) * currIndex;
				currIndex++;
			}
			left++;

			for (int i = 0; i < filesystem[left]; i++) {
				if (spaceNeeded == 0) {
					right -= 2;
					if (right <= left) {
						break;
					}
					spaceNeeded = filesystem[right];
				}
				checksum += (long) (right / 2) * currIndex;
				currIndex++;
				spaceNeeded--;
			}
			left++;
		}
		for (int i = 0; i < spaceNeeded; i++) {
			checksum += (long) (right / 2) * currIndex;
			currIndex++;
		}

		System.out.println("Part 1 Checksum: " + checksum);
	}

	private static void solvePartTwo(int[] filesystem) {
		long checksum = 0L;

		int[] openStartIndex = new int[filesystem.length];
		openStartIndex[0] = 0;
		for (int i = 1; i < filesystem.length; i++) {
			openStartIndex[i] = openStartIndex[i - 1] + filesystem[i - 1];
		}

		for (int right = filesystem.length - 1; right >= 0; right -= 2) {
			boolean found = false;
			for (int left = 1; left < right; left += 2) {
				if (filesystem[left] >= filesystem[right]) {
					for (int i = 0; i < filesystem[right]; i++) {
						checksum += (long) (right / 2) * (openStartIndex[left] + i);
					}
					filesystem[left] -= filesystem[right];
					openStartIndex[left] += filesystem[right];
					found = true;
					break;
				}
			}
			if (!found) {
				for (int i = 0; i < filesystem[right]; i++) {
					checksum += (long) (right / 2) * (openStartIndex[right] + i);
				}
			}
		}

		System.out.println("Part 2 Checksum: " + checksum);
	}
}
