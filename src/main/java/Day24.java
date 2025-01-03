import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

// Enum representing different types of gates: AND, OR, XOR.
enum GateType {
	AND {
		@Override
		Integer apply(Integer a, Integer b) {
			return a & b; // AND operation
		}
	},
	OR {
		@Override
		Integer apply(Integer a, Integer b) {
			return a | b; // OR operation
		}
	},
	XOR {
		@Override
		Integer apply(Integer a, Integer b) {
			return a ^ b; // XOR operation
		}
	};

	abstract Integer apply(Integer a, Integer b); // Abstract method to apply the gate operation.
}

public class Day24 {
	public static void main(String[] args) throws Exception {
		new Day24Puzzle().solve(); // Solve the puzzle when the main method is called.
	}
}

class Day24Puzzle {
	final Map<String, Integer> initialValues = new HashMap<>(); // Map to store the initial values of wires
	final Map<String, Wire> wires = new HashMap<>(); // Map to store the wires

	// Constructor that initializes the puzzle from a file provided by the user
	Day24Puzzle() throws Exception {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the file path: "); // Prompt the user to enter the file path
		String filePath = scanner.nextLine(); // Read the file path

		var parts = Files.readString(Paths.get(filePath)).split("\n\n"); // Read file and split into two parts
		part0(parts[0]); // Initialize the initial values (part 0)
		part1(parts[1]); // Initialize the gates (part 1)
	}

	// Initializes the initial values (wires with predefined values)
	private void part0(String lines) {
		for (String initLine : lines.split("\n")) {
			var initSplit = initLine.split(": ");
			String name = initSplit[0]; // Wire name
			initialValues.put(name, Integer.parseInt(initSplit[1])); // Store the initial value for the wire
		}
	}

	// Initializes the gates and connects them to the wires
	private void part1(String lines) {
		for (String gateLine : lines.split("\n")) {
			var split = gateLine.split(" ");
			var nameA = split[0]; // Name of the first input wire
			var type = GateType.valueOf(split[1]); // Gate type (AND, OR, XOR)
			var nameB = split[2]; // Name of the second input wire
			var name = split[4]; // Name of the output wire

			// Create or retrieve the wires from the map
			var inputA = wires.computeIfAbsent(nameA, k -> new Wire(nameA));
			var inputB = wires.computeIfAbsent(nameB, k -> new Wire(nameB));
			var output = wires.computeIfAbsent(name, k -> new Wire(name));

			// Create a new gate and associate it with the input and output wires
			var gate = new Gate(type, inputA, inputB, output);
			inputA.gates().add(gate); // Add gate to inputA
			inputB.gates().add(gate); // Add gate to inputB
		}
	}

	// Computes the value of the output wire 'z' as a long
	long getValue() {
		long value = 0L; // Value to store the result
		long bit = 1L; // The bit position to set
		int i = 0;
		do {
			var wire = wires.get(("z" + "%02d").formatted(i)); // Get wire 'z' with index i
			if (wire == null) { // If the wire does not exist, break the loop
				break;
			}
			if (wire.getValue() == 1) { // If wire value is 1, set the corresponding bit
				value |= bit;
			}
			++i; // Move to the next wire
			bit <<= 1; // Move to the next bit position
		} while (true);
		return value; // Return the computed value
	}

	// Traces the connections between wires and gates to detect swapped values
	void traceAll() {
		Set<String> swaps = new HashSet<>();
		int i = 0;
		do {
			++i; // Increment the iteration counter
		} while (cell(i, swaps)); // Keep tracing until no more swaps are found
		// Output the sorted list of swapped wire names
		System.out.println(swaps.stream().sorted().collect(Collectors.joining(",")));
	}

	// Helper method to check and trace the connections for a particular wire
	boolean cell(int i, Set<String> swaps) {
		var a = wires.get(("x" + "%02d").formatted(i)); // Get the wire 'x' with index i
		if (a == null) { // If the wire does not exist, return false
			return false;
		}

		// Find the first XOR gate connected to wire 'a'
		var xorGate = a.gates().stream().filter(g -> g.type() == GateType.XOR).findFirst().orElseThrow();
		var xorOutput = xorGate.output(); // Get the output of the XOR gate

		// Find the first AND gate connected to wire 'a'
		var andGate = a.gates().stream().filter(g -> g.type() == GateType.AND).findFirst().orElseThrow();
		var andOutput = andGate.output(); // Get the output of the AND gate

		// Check if the XOR gate's output is not connected to another XOR gate and the AND gate's output is connected to a XOR gate
		if (xorOutput.gates().stream().noneMatch(g -> g.type() == GateType.XOR) && andOutput.gates().stream().anyMatch(g -> g.type() == GateType.XOR)) {
			swaps.add(xorOutput.name()); // Mark XOR output as swapped
			swaps.add(andOutput.name()); // Mark AND output as swapped
		}

		// Check if the output of the XOR gate is not 'z' and needs to be swapped
		var outputName = xorOutput.gates().stream().filter(g -> g.type() == GateType.XOR).findFirst().map(g -> g.output().name()).orElse(null);
		if (outputName != null && !outputName.equals("z%02d".formatted(i))) {
			swaps.add(outputName); // Add swapped output name to the set
			swaps.add("z%02d".formatted(i)); // Mark the wire 'z' as swapped
		}
		return true; // Continue tracing
	}

	// Solves the puzzle by initializing the wires and printing the results
	void solve() {
		initialValues.forEach((k, v) -> wires.get(k).setValue(v)); // Set initial values for the wires
		System.out.println(getValue()); // Print the final value of 'z'
		traceAll(); // Trace the connections and print any swapped wires
	}
}

// Represents the state of a wire (either 0 or 1)
class State {
	Integer value;

	// Set the value of the wire (0 or 1)
	void setValue(int value) {
		if (this.value != null) { // Ensure the value is not already set
			throw new IllegalStateException(); // Throw an exception if the value is already set
		}
		this.value = value; // Set the wire's value
	}
}

// Represents a wire in the circuit with a name, state, and associated gates
record Wire(String name, State state, Set<Gate> gates) {
	Wire(String name) {
		this(name, new State(), new HashSet<>()); // Initialize a new wire with default state and empty gate set
	}

	// Returns the value of the wire (either 0 or 1)
	Integer getValue() {
		return state.value;
	}

	// Set the value of the wire and trigger the connected gates
	void setValue(int value) {
		state.setValue(value); // Set the wire's value
		for (var gate : gates) { // Trigger all the gates connected to this wire
			gate.trigger();
		}
	}

	@Override
	public String toString() {
		return name; // Return the wire's name when printed
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true; // If the objects are the same, return true
		if (o == null || getClass() != o.getClass()) return false; // Check for null or class mismatch
		Wire wire = (Wire) o; // Cast to Wire object
		return Objects.equals(name, wire.name); // Compare wire names
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name); // Return the hash code of the wire's name
	}
}

// Represents a logic gate (AND, OR, XOR) that takes two input wires and produces an output wire
record Gate(GateType type, Wire inputA, Wire inputB, Wire output) {
	// Trigger the gate operation when both inputs are available
	void trigger() {
		if (inputA.getValue() != null && inputB.getValue() != null) {
			output.setValue(type.apply(inputA.getValue(), inputB.getValue())); // Apply the gate operation and set the output
		}
	}
}
