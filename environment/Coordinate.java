package multiagent.lab2.environment;

import java.util.Random;

public final class Coordinate {
	private int x;
	private int y;

	public static Coordinate asRandom(Random random, int boundary) {
		return new Coordinate(random.nextInt(boundary), random.nextInt(boundary));
	}

	public Coordinate() {
		this(0, 0);
	}

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isNextTo(Coordinate c) {
		if (c == null) {
			return false;
		}
		int xd = Math.abs(c.x - x);
		int yd = Math.abs(c.y - y);
		return (xd == 1 && yd == 0) || (xd == 0 && yd == 1);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Coordinate that = (Coordinate) o;

		if (x != that.x) return false;
		return y == that.y;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		return result;
	}

	public Coordinate getClone() {
		return new Coordinate(x, y);
	}

	@Override
	public String toString() {
		return "{" +
			"x=" + x +
			", y=" + y +
			'}';
	}
}
