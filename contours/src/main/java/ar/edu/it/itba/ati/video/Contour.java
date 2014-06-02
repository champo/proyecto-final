package ar.edu.it.itba.ati.video;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Contour implements Iterable<Point> {

	enum State {
		STABLE,
		MISSING
	}

	// Difference vectors. 8 neighbours.
	private static final int dx[] = {-1, 1, 0,  0, 1,  1, -1, -1};
	private static final int dy[] = { 0, 0, 1, -1, 1, -1, -1,  1};

	private static final double mu = 0.7;
	private static int windowSize;

	private Set<Point> points;
	private Set<Point> lin;
	public final int color;

	private Set<Point> internalPoints;

	private long expectedSize = 0;

	private Point lastCentroid;

	private State state = State.MISSING;

	private int cyclesLost;

	public Contour(final int color, final Rectangle rect) {
                this.color = color;
		points = new HashSet<Point>();
		lin = new HashSet<Point>();
		for (int i = 0; i < rect.width; i++) {
			points.add(new Point(rect.x + i, rect.y));
		}
		for (int j = 0; j < rect.height; j++) {
			points.add(new Point(rect.x + rect.width, rect.y + j));
		}
		for (int i = rect.width; i > 0; i--) {
			points.add(new Point(rect.x + i, rect.y + rect.height));
		}
		for (int j = rect.height; j > 0; j--) {
			points.add(new Point(rect.x, rect.y + j));
		}
		for (int i = 1; i < rect.width - 1; i++) {
			lin.add(new Point(rect.x + i, rect.y + 1));
		}
		for (int j = 1; j < rect.height - 1; j++) {
			lin.add(new Point(rect.x + rect.width - 1, rect.y + j));
		}
		for (int i = rect.width - 1; i > 1; i--) {
			lin.add(new Point(rect.x + i, rect.y + rect.height - 1));
		}
		for (int j = rect.height - 1; j > 1; j--) {
			lin.add(new Point(rect.x + 1, rect.y + j));
		}
		expectedSize = lin.size();
	}

	public static Contour aroundPoint(final int color, final Point point) {
		return new Contour(color, new Rectangle(point.x - windowSize, point.y - windowSize, 2 * windowSize, 2 * windowSize));
	}

	public static void setWindowSize(final int newSize) {
		windowSize = newSize;
	}

	public int minX() {

		int min = Integer.MAX_VALUE;
		for (final Point p : points) {
			min = min(min, p.x);
		}
		return min;
	}

	public int maxX() {

		int max = Integer.MIN_VALUE;
		for (final Point p : points) {
			max = max(max, p.x);
		}
		return max;
	}

	public int minY() {

		int min = Integer.MAX_VALUE;
		for (final Point p : points) {
			min = min(min, p.y);
		}
		return min;
	}

	public int maxY() {

		int max = Integer.MIN_VALUE;
		for (final Point p : points) {
			max = max(max, p.y);
		}
		return max;
	}

	private static int max(final int a, final int b) {
		return a > b ? a : b;
	}
	private static int min(final int a, final int b) {
		return a < b ? a : b;
	}

	@Override
	public Iterator<Point> iterator() {
		return internalPoints.iterator();
	}

	public boolean contains(final int i, final int j) {
		return internalPoints.contains(new Point(i, j));
	}

	public Set<Point> getLin() {
		return lin;
	}

	public Set<Point> getLout() {
		return points;
	}

	public void setInternalPoints(final Set<Point> internalPoints) {
		this.internalPoints = internalPoints;

		lastCentroid = new Point(centroidX(), centroidY());
	}

	public void addPoint(final Point p) {
		internalPoints.add(p);
	}

	public void removePoint(final Point p) {
		internalPoints.remove(p);
	}

	public int centroidY() {
		double cumm = 0;
		for (final Point p : internalPoints) {
			cumm += p.y;
		}
		return (int) (cumm/internalPoints.size());
	}

	public int centroidX() {
		double cumm = 0;
		for (final Point p : internalPoints) {
			cumm += p.x;
		}
		return (int) (cumm/internalPoints.size());
	}

	public boolean mutationFinished() {

		checkIfMissing();

		if (state == State.STABLE) {
			expectedSize = (long)(0.95 * expectedSize + 0.05 * currentSize());

			lastCentroid = new Point(centroidX(), centroidY());
		} else {
			cyclesLost++;
		}

		return checkConnectivity();
	}

	private void checkIfMissing() {

		if (currentSize() < mu * averageSize()) {

			if (state == State.STABLE) {
				System.out.println("A countor is missing OH NOES");
				state = State.MISSING;
			}

		} else {

			if (state != State.STABLE) {
				cyclesLost = 0;
				System.out.println("Got it back!");
				state = State.STABLE;
			}
		}
	}

	public boolean checkConnectivity() {

		if (state == State.STABLE) {

			int minX = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxY = Integer.MIN_VALUE;
			for (final Point x : points) {
				minX = Math.min(minX, x.x);
				maxX = Math.max(maxX, x.x);
				minY = Math.min(minY, x.y);
				maxY = Math.max(maxY, x.y);
			}

			// Create a map with a 1 when the given coordinate contains a point in LIN. Reduce coordinates to save memory.
			final int map[][] = new int[maxX - minX + 1][maxY - minY + 1];
			for (final Point x : points) {
				map[x.x-minX][x.y-minY] = 1;
			}

			// Sets of connected points found.
			final List<Set<Point>> sets = new LinkedList<Set<Point>>();

			// BFS
			for (final Point x : points) {
				if (map[x.x-minX][x.y-minY] == 1) {

					// New set of connected points (we're going to erase the 1s as we find the connected points)

					// Take them from the queue and create a new set to store the subset of connected points
					final Deque<Point> q = new LinkedList<Point>();
					final Set<Point> s = new HashSet<Point>();
					s.add(x);

					map[x.x-minX][x.y-minY] = 0;
					q.push(x);
					while (!(q.size() == 0)) {
						final Point y = q.pop();
						for (int d = 0; d < 8; d++) {
							// Neighbour's coordinates, reduced
							final int vx = y.x + dx[d] - minX;
							final int vy = y.y + dy[d] - minY;
							if (vx >= 0 && vx <= maxX - minX && vy >= 0 && vy <= maxY - minY && map[vx][vy] == 1) {
								map[vx][vy] = 0;
								final Point v = new Point(vx + minX, vy + minY);
								q.push(v);
								s.add(v);
							}
						}
					}
					if (s.size() > 30) {
						sets.add(s);
					}
				}
			}
			if (sets.size() > 1) {
				// Split
				final List<Integer> weightedSizes = new LinkedList<Integer>();

				final int centroidX = centroidX();
				final int centroidY = centroidY();

				for (final Set<Point> s : sets) {
					int nc_x = 0;
					int nc_y = 0;
					for (final Point p : s) {
						nc_x += Math.abs(p.x);
						nc_y += Math.abs(p.y);
					}
					nc_x /= s.size();
					nc_y /= s.size();
					weightedSizes.add(Math.abs(nc_x - centroidX) + Math.abs(nc_y - centroidY));
				}
				Set<Point> best = null;
				int bestValue = Integer.MAX_VALUE;
				for (int i = 0; i < weightedSizes.size(); i++) {
					if (weightedSizes.get(i) < bestValue) {
						bestValue = weightedSizes.get(i);
						best = sets.get(i);
					}
				}
				// BFS to update lin, lout, and internal points
				points = best;
				for (final Point p : best) {
					map[p.x-minX][p.y-minY] = 1;
				}
				final Set<Point> newLin = new HashSet<Point>();
				final Set<Point> newInternals = new HashSet<Point>();
				final Deque<Point> q = new LinkedList<Point>();
				// First, pick the points in lin that are inside the new lout
				for (final Point p : lin) {
					boolean found = false;
					for (int d = 0; !found && d < 4; d++) {
						final int vx = p.x + dx[d] - minX;
						final int vy = p.y + dy[d] - minY;
						if (vx >= 0 && vx <= maxX - minX && vy >= 0 && vy <= maxY - minY && map[vx][vy] == 1) {
							found = true;
						}
					}
					if (found) {
						newLin.add(p);
						q.add(p);
					}
				}
				// Now, update internal points.
				while (!(q.size() == 0)) {
					final Point p = q.pop();
					newInternals.add(p);
					for (int d = 0; d < 4; d++) {
						final int vx = p.x - minX + dx[d];
						final int vy = p.y - minY + dy[d];
						if (vx >= 0 && vx <= maxX - minX && vy >= 0 && vy <= maxY - minY && map[vx][vy] == 0) {
							map[vx][vy] = 1;
							q.push(new Point(vx + minX, vy + minY));
						}
					}
				}
				lin = newLin;
				internalPoints = newInternals;

				// Reset expected size after split
				expectedSize = currentSize();
				return true;
			}
		}
		return false;
	}

	public long averageSize() {
		return expectedSize;
	}

	public long currentSize() {
		return lin.size();
	}

	public State getState() {
		return state;
	}

	public Point getLastCentroid() {
		return lastCentroid;
	}

	public int cyclesLost() {
		return cyclesLost;
	}

	public Set<Point> getInternalPoints() {
		return internalPoints;
	}
}
