import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

enum Type {LOCK, KEY}

public class Day25 {
	public static void main(String[] args) throws Exception {
		new Day25Puzzle().solve();
	}
}

class Day25Puzzle {
	final List<Piece> pieces;

	// Constructor that reads the file path dynamically
	Day25Puzzle() throws Exception {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the file path: "); // Prompt the user to enter the file path
		String filePath = scanner.nextLine();  // Read the file path from the user

		// Read the file content from the specified path and process it
		pieces = Arrays.stream(Files.readString(Paths.get(filePath)).split("\n\n"))
				.map(Piece::from)
				.toList();
	}

	void solve() {
		System.out.println(fittingPieces());
	}

	long fittingPieces() {
		return IntStream.range(0, pieces.size())
				.mapToLong(i -> IntStream.range(i + 1, pieces.size())
						.filter(j -> pieces.get(i).fits(pieces.get(j)))
						.count())
				.sum();
	}
}

record Piece(Type type, int[] heights) {
	static Piece from(String blob) {
		var lines = blob.split("\n");
		var type = lines[0].startsWith("#") ? Type.LOCK : Type.KEY;
		var heights = new int[5];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (lines[i + 1].charAt(j) == '#') {
					heights[j]++;
				}
			}
		}
		return new Piece(type, heights);
	}

	boolean fits(Piece other) {
		if (type == other.type) {
			return false;
		}
		return IntStream.range(0, 5).allMatch(i -> heights[i] + other.heights[i] <= 5);
	}
}
