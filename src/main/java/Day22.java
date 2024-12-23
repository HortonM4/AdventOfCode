import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day22 {
	public static void main(String[] args) throws Exception {
		// Use Scanner to take file path input from the user
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the file path: ");
		String filePath = scanner.nextLine();

		// Initialize and solve the puzzle
		new Day22Puzzle(filePath).solve();
	}
}

class Day22Puzzle {
	final Market market;

	// Constructor reads secrets from the user-provided file path
	Day22Puzzle(String filePath) throws Exception {
		try (var input = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
			market = new Market(input.lines());
		}
	}

	void solve() {
		System.out.println(market.iterateAllBuyers());
	}
}

record Pair(long sumSecrets, int bestPrice) {
}

class Market {
	static final int ITERATIONS = 2000;
	static final int SEQUENCE_LENGTH = 4;
	static final int B24 = 0xffffff;
	private final List<Integer> secrets;

	Market(Stream<String> secrets) {
		this.secrets = secrets.map(Integer::parseInt).toList();
	}

	void iterateBuyer(final int initialSecret, BiConsumer<String, Integer> priceWatcher, Consumer<Integer> secretWatcher) {
		int secret = initialSecret;
		List<Integer> deltas = new ArrayList<>();
		Set<String> visited = new HashSet<>();
		for (int round = 0; round < ITERATIONS; round++) {
			int oldPrice = secret % 10;
			secret = (secret << 6 ^ secret) & B24;
			secret = (secret >> 5 ^ secret) & B24;
			secret = (secret << 11 ^ secret) & B24;
			int newPrice = secret % 10;
			if (deltas.size() >= SEQUENCE_LENGTH) {
				deltas.remove(0);
			}
			deltas.add(newPrice - oldPrice);
			if (deltas.size() >= SEQUENCE_LENGTH) {
				var pattern = deltas.stream().map(String::valueOf).collect(Collectors.joining());
				if (!visited.contains(pattern)) {
					visited.add(pattern);
					priceWatcher.accept(pattern, newPrice);
				}
			}
		}
		secretWatcher.accept(secret);
	}

	Pair iterateAllBuyers() {
		Map<String, Integer> prices = new HashMap<>();
		class SumHolder {
			long sum;

			void add(int i) {
				sum += i;
			}
		}
		var sumHolder = new SumHolder();
		secrets.forEach(initialSecret -> iterateBuyer(
				initialSecret,
				((pattern, price) -> prices.compute(pattern, (k, v) -> v == null ? price : v + price)),
				sumHolder::add
		));
		return new Pair(sumHolder.sum, prices.values().stream().max(Integer::compareTo).orElseThrow());
	}
}
