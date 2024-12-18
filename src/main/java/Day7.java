import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day7 {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter the file name (e.g., Day6.txt): ");
		String filePath = scanner.nextLine();

		try {
			List<String> equations = readEquationsFromFile(filePath);
			long partOneResult = calculateCalibrationResultWithBasicOperators(equations);
			long partTwoResult = calculateCalibrationResultWithAllOperators(equations);

			System.out.println("Part One Total Calibration Result: " + partOneResult);
			System.out.println("Part Two Total Calibration Result: " + partTwoResult);
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + filePath);
		}
	}

	private static List<String> readEquationsFromFile(String filePath) throws FileNotFoundException {
		List<String> equations = new ArrayList<>();
		Scanner fileScanner = new Scanner(new File(filePath));

		while (fileScanner.hasNextLine()) {
			equations.add(fileScanner.nextLine());
		}

		fileScanner.close();
		return equations;
	}

	private static long calculateCalibrationResultWithBasicOperators(List<String> equations) {
		long total = 0;

		for (String equation : equations) {
			String[] parts = equation.split(":");
			long testValue = Long.parseLong(parts[0].trim());
			String[] numbers = parts[1].trim().split(" ");

			long[] nums = new long[numbers.length];
			for (int i = 0; i < numbers.length; i++) {
				nums[i] = Long.parseLong(numbers[i]);
			}

			if (canEvaluateWithBasicOperators(nums, testValue)) {
				total += testValue;
			}
		}

		return total;
	}

	private static long calculateCalibrationResultWithAllOperators(List<String> equations) {
		long total = 0;

		for (String equation : equations) {
			String[] parts = equation.split(":");
			long testValue = Long.parseLong(parts[0].trim());
			String[] numbers = parts[1].trim().split(" ");

			long[] nums = new long[numbers.length];
			for (int i = 0; i < numbers.length; i++) {
				nums[i] = Long.parseLong(numbers[i]);
			}

			if (canEvaluateWithAllOperators(nums, testValue)) {
				total += testValue;
			}
		}

		return total;
	}

	private static boolean canEvaluateWithBasicOperators(long[] nums, long testValue) {
		return evaluateWithOperators(nums, 0, nums[0], testValue, false);
	}

	private static boolean canEvaluateWithAllOperators(long[] nums, long testValue) {
		return evaluateWithOperators(nums, 0, nums[0], testValue, true);
	}

	private static boolean evaluateWithOperators(long[] nums, int index, long currentResult, long testValue, boolean allowConcatenation) {
		if (index == nums.length - 1) {
			return currentResult == testValue;
		}

		// Try addition
		if (evaluateWithOperators(nums, index + 1, currentResult + nums[index + 1], testValue, allowConcatenation)) {
			return true;
		}

		// Try multiplication
		if (evaluateWithOperators(nums, index + 1, currentResult * nums[index + 1], testValue, allowConcatenation)) {
			return true;
		}

		// Try concatenation if allowed
		if (allowConcatenation) {
			long concatenatedValue = Long.parseLong(currentResult + "" + nums[index + 1]);
			if (evaluateWithOperators(nums, index + 1, concatenatedValue, testValue, true)) {
				return true;
			}
		}

		return false;
	}
}
