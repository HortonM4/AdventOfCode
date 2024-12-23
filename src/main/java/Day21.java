import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

// Enum representing the keys on the keypad with their neighbors and transition rules
enum Key {
	ACTIVATE('A', "<0,^3,<^,v>"),
	ZERO('0', "^2,>A"),
	ONE('1', "^4,>2"),
	TWO('2', "<1,^5,>3,v0"),
	THREE('3', "<2,^6,vA"),
	FOUR('4', "^7,>5,v1"),
	FIVE('5', "<4,^8,>6,v2"),
	SIX('6', "<5,^9,v3"),
	SEVEN('7', ">8,v4"),
	EIGHT('8', "<7,>9,v5"),
	NINE('9', "<8,v6"),
	UP('^', ">A,vv"),
	DOWN('v', "<<,^^,>>"),
	LEFT('<', ">v"),
	RIGHT('>', "^A,<v");

	// Initialize neighbors for each key during class loading
	static {
		Arrays.stream(values()).forEach(Key::init);
	}

	private final char symbol; // Symbol of the key
	private final String instructions; // Transition instructions
	private final Collection<Neighbour> neighbours = new HashSet<>(); // Neighbors of the key

	Key(char symbol, String instructions) {
		this.symbol = symbol;
		this.instructions = instructions;
	}

	// Retrieve a Key instance based on its symbol
	static Key from(char symbol) {
		return Arrays.stream(values()).filter(v -> v.symbol == symbol).findFirst().orElseThrow();
	}

	// Initialize the neighbors based on transition instructions
	private void init() {
		neighbours.addAll(Arrays.stream(instructions.split(",")).map(Neighbour::new).toList());
	}

	char symbol() {
		return symbol;
	}

	Collection<Neighbour> neighbours() {
		return neighbours;
	}
}

// Main class to run the program
public class Day21 {
	public static void main(String[] args) throws Exception {
		// Use Scanner to take file path input from the user
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the file path: ");
		String filePath = scanner.nextLine();

		// Create Puzzle instance with the provided file path
		var puzzle = new Day21Puzzle(filePath);

		// Solve the puzzle for different repeat values
		puzzle.solve(2); // Solve with 2 repeats
		puzzle.solve(25); // Solve with 25 repeats
	}
}

// Puzzle class to handle the input and solving logic
class Day21Puzzle {
	private final List<String> sequences; // Input sequences

	// Constructor to initialize sequences from the given file path
	Day21Puzzle(String filePath) throws Exception {
		try (var input = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
			sequences = input.lines().toList();
		}
	}

	// Solve the puzzle with a given number of repeats
	void solve(int repeats) {
		var keyPad = new KeyPad(repeats); // Initialize keypad with repeats
		// Calculate the total complexity of all sequences and print it
		System.out.println(sequences.stream().mapToLong(keyPad::complexity).sum());
	}
}

// KeyPad class to manage the logic of instructions and complexity
class KeyPad {
	private static final Map<String, String> INSTRUCTION_CACHE = new HashMap<>(); // Cache for instructions
	private final int repeats; // Number of iterations

	KeyPad(int repeats) {
		this.repeats = repeats;
	}

	// Get instructions for a given sequence
	String instructions(String sequence) {
		var cached = INSTRUCTION_CACHE.get(sequence);
		if (cached != null) {
			return cached; // Return cached instructions if available
		}
		Key state;
		var sb = new StringBuilder();
		Key previousState = Key.ACTIVATE; // Start from the ACTIVATE key
		for (char c : sequence.toCharArray()) {
			state = Key.from(c); // Determine the current key
			sb.append(path(previousState, state)); // Find the path to the current key
			previousState = state;
		}
		String instructions = sb.toString();
		INSTRUCTION_CACHE.put(sequence, instructions); // Cache the result
		return instructions;
	}

	// Convert instructions into a map of sequences and counts
	Map<String, Long> sequenceMap(String instructions) {
		if (instructions.equals("A")) {
			return Map.of("A", 1L); // Special case for "A"
		}
		Map<String, Long> map = new HashMap<>();
		for (String part : instructions.split("A")) {
			map.compute(part + "A", (k, v) -> v == null ? 1L : v + 1L); // Count occurrences
		}
		return map;
	}

	// Perform one iteration of the sequence map
	Map<String, Long> iterate(Map<String, Long> sequenceMap) {
		Map<String, Long> map = new HashMap<>();
		sequenceMap.forEach((sequence, a) ->
				sequenceMap(instructions(sequence)).forEach((instruction, b) ->
						map.compute(instruction, (unused, c) -> c == null ? a * b : c + a * b)));
		return map;
	}

	// Repeat the instructions multiple times
	Map<String, Long> repeatInstructions(String sequence) {
		var instructions = instructions(sequence);
		Map<String, Long> sequenceMap = sequenceMap(instructions);
		for (int i = 0; i < repeats; i++) {
			sequenceMap = iterate(sequenceMap);
		}
		return sequenceMap;
	}

	// Calculate the complexity of a sequence
	long complexity(String sequence) {
		return length(sequence) * value(sequence); // Length * Numeric value
	}

	// Extract numeric value from the sequence
	private long value(String sequence) {
		return Long.parseLong(sequence.replaceAll("A", ""));
	}

	// Calculate the total length of all repeated instructions
	private long length(String sequence) {
		return repeatInstructions(sequence).entrySet().stream()
				.mapToLong(e -> e.getValue() * e.getKey().length())
				.sum();
	}

	// Find the shortest path from one key to another
	String path(Key from, Key to) {
		var queue = new PriorityQueue<ND>(); // Priority queue for Dijkstra's algorithm
		Set<Turn> visited = new HashSet<>(); // Set to track visited turns
		queue.add(new ND(from, 0, null, null)); // Start from the "from" key
		ND bestPath = null; // Best path found
		while (!queue.isEmpty()) {
			var current = queue.remove();
			if (current.key() == to) {
				bestPath = current; // Path to the destination key found
				break;
			}
			for (Neighbour neighbour : current.key().neighbours()) {
				var turn = new Turn(current.key(), current.direction(), neighbour.direction(), neighbour.key());
				if (!visited.contains(turn)) {
					visited.add(turn);
					queue.add(new ND(neighbour.key(), current.distance() + turn.cost(), neighbour.direction(), current));
				}
			}
		}
		// Reconstruct the path from the best ND object
		return Objects.requireNonNull(bestPath).path();
	}
}

// Represents a neighbor key and the direction to it
record Neighbour(Key direction, Key key) {
	Neighbour(String s) {
		this(Key.from(s.charAt(0)), Key.from(s.charAt(1)));
	}
}

// Represents a turn in the pathfinding process
record Turn(Key key, Key fromDirection, Key toDirection, Key toKey) {
	// Calculate the cost of the turn
	int cost() {
		if (fromDirection == null) {
			return switch (toDirection) {
				case LEFT -> 50;
				case UP, DOWN -> 100;
				case RIGHT -> 200;
				default -> throw new IllegalStateException();
			};
		}
		return fromDirection == toDirection ? 1 : 1000;
	}
}

// Represents a node in the pathfinding process
record ND(Key key, int distance, Key direction, ND previous) implements Comparable<ND> {

	@Override
	public int compareTo(ND o) {
		return distance != o.distance ? Integer.compare(distance, o.distance)
				: key != o.key ? key.compareTo(o.key)
				: direction == null ? (o.direction == null ? 0 : -1)
				: o.direction == null ? 1 : direction.compareTo(o.direction);
	}

	// Reconstruct the path as a string
	String path() {
		List<Key> l = new ArrayList<>();
		l.add(Key.ACTIVATE);
		var nd = this;
		while (nd.direction != null) {
			l.add(nd.direction);
			nd = nd.previous;
		}
		Collections.reverse(l);
		return l.stream().map(k -> String.valueOf(k.symbol())).collect(Collectors.joining());
	}
}
