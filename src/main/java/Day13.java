import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Day13 {
	public static void main(String[] args) throws Exception {
		new ClawMachineGame().solve();
	}
}

class ClawMachineGame {
	final List<ClawMachine> clawMachines = new ArrayList<>();

	ClawMachineGame() throws Exception {
		Scanner scanner = new Scanner(System.in);

		// Get the file path from the user
		System.out.print("Enter the file path: ");
		String filePath = scanner.nextLine();

		// Read the input file based on the provided file path
		String input = Files.readString(Paths.get(filePath));

		// Parse the input into claw machines
		for (String instruction : input.split("\\n\\n")) {
			clawMachines.add(ClawMachine.from(instruction));
		}
	}

	void solve() {
		System.out.println("Part 1: " + clawMachines.stream().mapToLong(c -> c.tokens(0L)).sum());
		System.out.println("Part 2: " + clawMachines.stream().mapToLong(c -> c.tokens(10000000000000L)).sum());
	}
}

record Position(long x, long y) {
	static final Position ZERO = new Position(0L, 0L);

	static Position from(String x, String y) {
		return new Position(Long.parseLong(x), Long.parseLong(y));
	}

	Position mod(long d) {
		return new Position(x % d, y % d);
	}

	Position divide(long d) {
		return new Position(x / d, y / d);
	}
}

record ClawMachine(Position buttonA, Position buttonB, Position prize) {
	static final long BUTTON_A_TOKENS = 3;
	static final long BUTTON_B_TOKENS = 1;
	static final Pattern PATTERN = Pattern.compile("\\+(\\d+),.*\\+(\\d+)\\n.*\\+(\\d+),.*\\+(\\d+)\\n.*=(\\d+),.*=(\\d+)");

	static ClawMachine from(String instruction) {
		var matcher = PATTERN.matcher(instruction);
		if (!matcher.find()) {
			System.err.println(instruction);
			throw new IllegalArgumentException();
		}
		return new ClawMachine(
				Position.from(matcher.group(1), matcher.group(2)),
				Position.from(matcher.group(3), matcher.group(4)),
				Position.from(matcher.group(5), matcher.group(6))
		);
	}

	long determinant() {
		return buttonA.x() * buttonB.y() - buttonA.y() * buttonB.x();
	}

	long tokens(long prizeDistance) {
		long px = prize.x() + prizeDistance;
		long py = prize.y() + prizeDistance;
		Position multiple = new Position(buttonB.y() * px - buttonB.x() * py, -buttonA.y() * px + buttonA.x() * py);
		var d = determinant();
		if (multiple.mod(d).equals(Position.ZERO)) {
			var ab = multiple.divide(d);
			return BUTTON_A_TOKENS * ab.x() + BUTTON_B_TOKENS * ab.y();
		} else {
			return 0L;
		}
	}
}
