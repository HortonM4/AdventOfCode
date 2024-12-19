import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day14 {

	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter the file path: ");
		String filePath = scanner.nextLine();
		scanner.close();

		List<String> lines = Files.readAllLines(Paths.get(filePath));

		List<Robot> robots = new ArrayList<>();
		for (String line : lines) {
			String[] parts = line.split(" ");
			robots.add(new Robot(parsePosition(parts[0]), parsePosition(parts[1])));
		}

		System.out.println("Part 1: " + calculateSafetyFactorAfter100(robots));

		int clusterTime = find3x3ClusterTime(robots);
		System.out.println("Part 2 (3x3 Cluster Time): " + clusterTime);
	}

	private static int calculateSafetyFactorAfter100(List<Robot> robots) {
		HashMap<Position, Integer> robotPositions = new HashMap<>();
		for (Robot robot : robots) {
			Position finalPos = robot.getPositionAfterTime(100);
			robotPositions.put(finalPos, robotPositions.getOrDefault(finalPos, 0) + 1);
		}
		return calculateSafetyFactor(robotPositions);
	}

	private static Position parsePosition(String positionString) {
		String[] parts = positionString.substring(2).split(",");
		return new Position(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
	}

	private static int calculateSafetyFactor(HashMap<Position, Integer> robotPositions) {
		int topLeft = 0, topRight = 0, bottomLeft = 0, bottomRight = 0;

		for (Position position : robotPositions.keySet()) {
			if (position.getX() < 50 && position.getY() < 51) topLeft += robotPositions.get(position);
			else if (position.getX() > 50 && position.getY() < 51) topRight += robotPositions.get(position);
			else if (position.getX() < 50 && position.getY() > 51) bottomLeft += robotPositions.get(position);
			else if (position.getX() > 50 && position.getY() > 51) bottomRight += robotPositions.get(position);
		}

		return Math.max(1, topLeft) * Math.max(1, topRight) * Math.max(1, bottomLeft) * Math.max(1, bottomRight);
	}

	private static int find3x3ClusterTime(List<Robot> robots) {
		for (int time = 0; time <= 10000; time++) {
			Set<Position> positions = new HashSet<>();
			for (Robot robot : robots) {
				positions.add(robot.getPositionAfterTime(time));
			}

			if (is3x3Cluster(positions)) {
				return time;
			}
		}
		return -1;
	}

	private static boolean is3x3Cluster(Set<Position> positions) {
		for (Position p : positions) {
			int clusterCount = 0;
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					int nx = (p.getX() + dx + 101) % 101;
					int ny = (p.getY() + dy + 103) % 103;

					if (positions.contains(new Position(nx, ny))) {
						clusterCount++;
					}
				}
			}
			if (clusterCount == 9) {
				return true;
			}
		}
		return false;
	}

	static class Robot {
		Position position;
		Position velocity;

		public Robot(Position position, Position velocity) {
			this.position = position;
			this.velocity = velocity;
		}

		public Position getPositionAfterTime(int time) {
			int newX = (position.getX() + velocity.getX() * time) % 101;
			if (newX < 0) newX += 101;
			int newY = (position.getY() + velocity.getY() * time) % 103;
			if (newY < 0) newY += 103;
			return new Position(newX, newY);
		}
	}

	static class Position {
		private final int x;
		private final int y;

		public Position(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Position position = (Position) o;
			return x == position.x && y == position.y;
		}

		@Override
		public int hashCode() {
			return 31 * x + y;
		}
	}
}