package multiagent.lab2.environment;

import multiagent.lab2.Percept;
import multiagent.lab2.spelunker.behaviour.GameCycleBehaviour;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class EnvironmentState {
	private Random gameRand;
	private int tick;
	private Coordinate gold;
	private Set<Coordinate> pits;
	private Coordinate wumpus;
	private GameplayState gameplayState;

	private Coordinate spelunkerPosition;

	/**
	 * Shows spelunkers current direction (0 - N, 1 - E, 2 - S, 3 - W)
	 */
	private int spelunkerRotation;
	private boolean spelunkerHitAWall;

	public EnvironmentState() {
		gameplayState = new GameplayState();
		gameRand = new Random();

		wumpus = Coordinate.asRandom(gameRand, 4);

		pits = new HashSet<>();
		while (pits.size() < 3) {
			pits.add(Coordinate.asRandom(gameRand, 4));
		}

		while (gold == null) {
			Coordinate newGold = Coordinate.asRandom(gameRand, 4);
			if (!pits.contains(newGold) && !wumpus.equals(newGold)) {
				gold = newGold;
			}
		}

		do {
			spelunkerPosition = Coordinate.asRandom(gameRand, 4);
		} while (pits.contains(spelunkerPosition) || wumpus.equals(spelunkerPosition));
		spelunkerRotation = 0;

		tick = 0;
	}

	public String getStatePercept() {
		if (!gameplayState.isGameOver()) {
			StringBuilder builder = new StringBuilder(GameCycleBehaviour.GamePerceptType.PERCEPT.getTextFormat());
			builder.append("(");
			if (gameplayState.isWumpusKilled()) {
				builder.append(Percept.SCREAM.getPunctuatedInterpretation());
			} else if (spelunkerPosition.isNextTo(wumpus)) {
				builder.append(Percept.STENCH.getPunctuatedInterpretation());
			}
			for (Coordinate pit : pits) {
				if (spelunkerPosition.equals(pit)) {
					gameplayState.setPlayerFell(true);
				} else if (spelunkerPosition.isNextTo(pit)) {
					builder.append(Percept.BREEZE.getPunctuatedInterpretation());
					break;
				}
			}
			if (spelunkerPosition.equals(gold)) {
				builder.append(Percept.GLITTER.getPunctuatedInterpretation());
			}
			if (spelunkerHitAWall) {
				builder.append(Percept.BUMP.getPunctuatedInterpretation());
				spelunkerHitAWall = false;
			}
			builder.append(tick).append(")");
			return builder.toString();
		} else {
			return gameplayState.getEndGamePredicate();
		}
	}

	public void performShot() {
		if (spelunkerRotation % 2 == 0 &&
			spelunkerPosition.getX() == wumpus.getX()) {
			if ((
				spelunkerRotation == 0 &&
					spelunkerPosition.getY() > wumpus.getY()
			) || (
				spelunkerRotation == 2 &&
					spelunkerPosition.getY() < wumpus.getY()
			)) {
				wumpus = null;
				gameplayState.setWumpusKilled(true);
			}
		} else if (spelunkerRotation % 2 == 1 &&
			spelunkerPosition.getY() == wumpus.getY()) {
			if ((
				spelunkerRotation == 1 &&
					spelunkerPosition.getX() < wumpus.getX()
			) || (
				spelunkerRotation == 3 &&
					spelunkerPosition.getX() > wumpus.getX()
			)) {
				wumpus = null;
				gameplayState.setWumpusKilled(true);
			}
		}
		if (!gameplayState.isWumpusKilled()) {
			moveCoordinate(wumpus, gameRand.nextInt(4));
			if (spelunkerPosition.equals(wumpus)) {
				gameplayState.setPlayerKilled(true);
			}
		}
		tick++;
	}

	private void moveCoordinate(Coordinate coordinate, int direction) {
		int newX = coordinate.getX();
		int newY = coordinate.getY();
		switch (direction) {
			case 0:
				newY--;
				break;
			case 1:
				newX++;
				break;
			case 2:
				newY++;
				break;
			case 3:
				newX--;
				break;
		}
		if (newX >= 0 && newX <= 3) {
			coordinate.setX(newX);
		}
		if (newY >= 0 && newY <= 3) {
			coordinate.setY(newY);
		}
	}

	public void performTurn(String direction) {
		if ("left".equals(direction)) {
			if (spelunkerRotation == 0) {
				spelunkerRotation = 3;
			} else {
				spelunkerRotation--;
			}
		} else if ("right".equals(direction)) {
			spelunkerRotation = (spelunkerRotation + 1) % 4;
		}
		tick++;
	}

	public void performGrab() {
		if (spelunkerPosition.equals(gold)) {
			gold = null;
			gameplayState.setGoldTaken(true);
		}
		tick++;
	}

	public void performForward() {
		Coordinate oldPosition = spelunkerPosition.getClone();
		moveCoordinate(spelunkerPosition, spelunkerRotation);
		if (spelunkerPosition.equals(oldPosition)) {
			spelunkerHitAWall = true;
		}
		if (spelunkerPosition.equals(wumpus)) {
			gameplayState.setPlayerKilled(true);
		}
		for (Coordinate pit : pits) {
			if (pit.equals(spelunkerPosition)) {
				gameplayState.setPlayerFell(true);
				break;
			}
		}
		tick++;
	}

	public boolean isGameOver() {
		return gameplayState.isGameOver();
	}

	public void performClimb() {
		gameplayState.setPlayerClimbed(true);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Actual map:\n");
		builder.append(spelunkerPosition.toString()).append("(").append(spelunkerRotation).append(")\n");
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (spelunkerPosition.equals(new Coordinate(j, i))) {
					builder.append("*-");
				} else {
					builder.append("+-");
				}
			}
			builder.append("+\n");
			for (int j = 0; j < 4; j++) {
				Coordinate c = new Coordinate(j, i);
				builder.append("|");
				if (c.equals(wumpus) && pits.contains(c)) {
					builder.append("B");
				} else if (c.equals(wumpus)) {
					builder.append("W");
				} else if (pits.contains(c)) {
					builder.append("P");
				} else {
					builder.append(" ");
				}
			}
			builder.append("|\n");
		}
		for (int i = 0; i < 4; i++) {
			builder.append("+-");
		}
		builder.append("+\n");
		return builder.toString();
	}
}
