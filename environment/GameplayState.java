package multiagent.lab2.environment;

import multiagent.lab2.spelunker.behaviour.GameCycleBehaviour;

public class GameplayState {
	private boolean goldTaken;
	private boolean wumpusKilled;
	private boolean playerKilled;
	private boolean playerFell;
	private boolean playerClimbed;

	public String getEndGamePredicate() {
		if (playerClimbed && (wumpusKilled || goldTaken)) {
			StringBuilder builder = new StringBuilder(GameCycleBehaviour.GamePerceptType.WIN.getTextFormat());
			builder.append("(");
			if (wumpusKilled) {
				builder.append("W");
				if (goldTaken) {
					builder.append(",");
				}
			}
			if (goldTaken) {
				builder.append("G");
			}
			builder.append(")");
			return builder.toString();
		} else if (playerFell || playerKilled || playerClimbed) {
			StringBuilder builder = new StringBuilder(GameCycleBehaviour.GamePerceptType.LOSS.getTextFormat());
			builder.append("(");
			if (playerKilled) {
				builder.append("W");
			} else if (playerFell) {
				builder.append("P");
			} else if (playerClimbed) {
				builder.append("F");
			}
			builder.append(")");
			return builder.toString();
		} else {
			return "";
		}
	}

	public boolean isGameOver() {
		return playerFell || playerKilled || playerClimbed;
	}

	public boolean isGoldTaken() {
		return goldTaken;
	}

	public void setGoldTaken(boolean goldTaken) {
		this.goldTaken = goldTaken;
	}

	public boolean isWumpusKilled() {
		return wumpusKilled;
	}

	public void setWumpusKilled(boolean wumpusKilled) {
		this.wumpusKilled = wumpusKilled;
	}

	public boolean isPlayerKilled() {
		return playerKilled;
	}

	public void setPlayerKilled(boolean playerKilled) {
		this.playerKilled = playerKilled;
	}

	public boolean isPlayerFell() {
		return playerFell;
	}

	public void setPlayerFell(boolean playerFell) {
		this.playerFell = playerFell;
	}

	public boolean isPlayerClimbed() {
		return playerClimbed;
	}

	public void setPlayerClimbed(boolean playerClimbed) {
		this.playerClimbed = playerClimbed;
	}
}
