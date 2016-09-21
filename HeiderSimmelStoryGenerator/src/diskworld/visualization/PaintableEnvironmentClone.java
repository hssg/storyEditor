/*******************************************************************************
 *     DiskWorld - a simple 2D physics simulation environment, 
 *     Copyright (C) 2014  Jan Kneissler
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program in the file "License.txt".  
 *     If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package diskworld.visualization;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import diskworld.Disk;
import diskworld.DiskComplex;
import diskworld.Environment;
import diskworld.environment.Floor;
import diskworld.environment.Wall;
import diskworld.grid.Grid;
import diskworld.grid.GridWithDiskMap;
import diskworld.linalg2D.Line;
import diskworld.linalg2D.Point;
import diskworld.skeleton.PermanentEdge;
import diskworld.skeleton.TransientEdge;
import diskworld.skeleton.Vertex;

public class PaintableEnvironmentClone {

	private static final int TIME_POS_X = 10;
	private static final int TIME_POS_Y = 20;
	private static final int GRID_TEXT_POS_X = 14;
	private static final int GRID_TEXT_POS_Y = 48;
	private static final int GRID_TEXT_SIZE = 48;

	private final Floor floor;
	private final Collection<Wall> walls;
	private final Grid[] grids;
	private final int[][] numDisksInCell;
	private final List<PaintableDisk> disks;
	private final double time;
	private final List<Point> collisions, blockedCollisions;
	private final List<Line> permanentEdges;
	private final List<Line> transientEdges;
	private final List<double[]> coordinates;

	public PaintableEnvironmentClone(Environment environment) {
		this.floor = environment.getFloor().createClone();
		this.walls = new LinkedList<Wall>();
		this.walls.addAll(environment.getWalls()); // clone the list of walls, because it may change, the Wall objects themselves are immutable, however 
		this.grids = new Grid[] { environment.getFloorGrid(), environment.getDiskGrid() }; // no need for cloning, grids do not change
		this.numDisksInCell = countDisks(environment.getDiskGrid());
		disks = new LinkedList<PaintableDisk>();
		permanentEdges = new LinkedList<Line>();
		transientEdges = new LinkedList<Line>();
		coordinates = new LinkedList<double[]>();
		Map<Integer, List<PaintableDisk>> zLevelMap = new TreeMap<Integer, List<PaintableDisk>>();
		for (DiskComplex dc : environment.getDiskComplexes()) {
			coordinates.add(getCoordinateSystem(dc));
			for (Disk d : dc.getDisks()) {
				List<PaintableDisk> list = zLevelMap.get(d.getZLevel());
				if (list == null) {
					list = new LinkedList<PaintableDisk>();
					zLevelMap.put(d.getZLevel(), list);
				}
				list.add(new PaintableDisk(d));
				Vertex vertex = d.getSkeletonVertex();
				for (PermanentEdge e : vertex.getEdges()) {
					if (e.getVertex1() == vertex) {
						Disk d2 = e.getVertex2().getDisk();
						permanentEdges.add(new Line(d.getX(), d.getY(), d2.getX(), d2.getY()));
					}
				}
			}
			for (TransientEdge e : dc.getTransientEdges()) {
				Disk d1 = e.getVertex1().getDisk();
				Disk d2 = e.getVertex2().getDisk();
				transientEdges.add(new Line(d1.getX(), d1.getY(), d2.getX(), d2.getY()));
			}
		}
		// now add disks in increasing zLevel order
		for (List<PaintableDisk> list : zLevelMap.values()) {
			disks.addAll(list);
		}
		collisions = environment.getCollisionPoints(false);
		blockedCollisions = environment.getCollisionPoints(true);
		this.time = environment.getTime();
	}

	private double[] getCoordinateSystem(DiskComplex dc) {
		return new double[] { dc.getCenterx(), dc.getCentery(), dc.getAngle() };
	}

	private int[][] countDisks(GridWithDiskMap grid) {
		int nx = grid.getNumColumns();
		int ny = grid.getNumRows();
		int res[][] = new int[nx][ny];
		for (int i = 0; i < nx; i++) {
			for (int j = 0; j < ny; j++) {
				res[i][j] = grid.getCell(i, j).getIntersectingDisks().size();
			}
		}
		return res;
	}

	public void paint(Graphics2D g, VisualizationSettings settings) {
		if (settings.isOptionEnabled(VisualizationOptions.OPTION_FLOOR)) {
			paintFloor(g, settings);
		} else {
			clear(g, settings);
		}
		if (settings.paintShadows()) {
			for (PaintableDisk d : disks) {
				d.paintShadow(g, settings);
			}
		}
		if (settings.isOptionEnabled(VisualizationOptions.OPTION_WALLS)) {
			for (Wall w : walls) {
				paintWall(g, w, settings);
			}
		}
		if (settings.isOptionEnabled(VisualizationOptions.OPTION_DISKS)) {
			for (PaintableDisk d : disks) {
				d.paint(g, settings);
			}
		}
		if (settings.isOptionEnabled(VisualizationOptions.OPTION_SKELETON)) {
			paintSkeleton(g, settings);
		}
		for (PaintableDisk d : disks) {
			d.paintAdditionalItems(g, settings);
		}
		if (settings.isOptionEnabled(VisualizationOptions.OPTION_COLLISIONS)) {
			paintCollisions(g, settings);
		}

		if (settings.isOptionEnabled(VisualizationOptions.OPTION_TIME)) {
			paintTime(g, settings);
		}
		if (settings.isOptionEnabled(VisualizationOptions.OPTION_GRID)) {
			VisualizationOption option = settings.getOptions().getOption(VisualizationOptions.GROUP_GENERAL, VisualizationOptions.OPTION_GRID);
			int index = option.chosenVariantIndex();
			paintGrid(g, grids[index], settings);
			if (index == 1) {
				paintCounts(g, grids[index], numDisksInCell, settings);
			}
		}
		if (settings.isOptionEnabled(VisualizationOptions.OPTION_COORDINATES)) {
			for (double coordinateSys[] : coordinates) {
				paintCoordinateSystem(g, coordinateSys, settings);
			}
		}
	}

	private void paintCoordinateSystem(Graphics2D g, double[] coordinateSys, VisualizationSettings settings) {
		g.setColor(settings.getColorScheme().coordinatesColor);
		g.setStroke(new BasicStroke(6));
		double x = coordinateSys[0];
		double y = coordinateSys[1];
		double arrowLength = 20;
		drawArrow(x, y, coordinateSys[2], arrowLength, g, settings);
		drawArrow(x, y, coordinateSys[2] + Math.PI / 2, arrowLength, g, settings);
	}

	private void drawArrow(double x, double y, double angle, double length, Graphics2D g, VisualizationSettings settings) {
		double dx = Math.cos(angle) * length;
		double dy = Math.sin(angle) * length;
		int x0 = settings.mapX(x);
		int y0 = settings.mapY(y);
		int x1 = settings.mapX(x + dx);
		int y1 = settings.mapY(y + dy);
		double arrowFactor = 0.1;
		int x2 = settings.mapX(x + dx - dx * arrowFactor + dy * arrowFactor);
		int y2 = settings.mapY(y + dy - dy * arrowFactor - dx * arrowFactor);
		int x3 = settings.mapX(x + dx - dx * arrowFactor - dy * arrowFactor);
		int y3 = settings.mapY(y + dy - dy * arrowFactor + dx * arrowFactor);
		g.drawLine(x0, y0, x1, y1);
		g.drawLine(x1, y1, x2, y2);
		g.drawLine(x1, y1, x3, y3);
	}

	private void paintGrid(Graphics2D g, Grid grid, VisualizationSettings settings) {
		g.setStroke(new BasicStroke(5));
		g.setColor(settings.getColorScheme().gridColor);
		double dx = grid.getCellWidth();
		double dy = grid.getCellHeight();
		int nx = grid.getNumColumns();
		int ny = grid.getNumRows();
		int minx = settings.mapX(0);
		int maxx = settings.mapX(nx * dx);
		int miny = settings.mapX(ny * dy);
		int maxy = settings.mapX(0);
		for (int i = 0; i <= nx; i++) {
			int x = settings.mapX(i * dx);
			g.drawLine(x, miny, x, maxy);
		}
		for (int i = 0; i <= ny; i++) {
			int y = settings.mapY(i * dy);
			g.drawLine(minx, y, maxx, y);
		}
	}

	private void paintCounts(Graphics2D g, Grid grid, int[][] numDisks, VisualizationSettings settings) {
		g.setColor(settings.getColorScheme().gridTextColor);
		g.setFont(new Font("SansSerif", Font.BOLD, GRID_TEXT_SIZE));
		double dx = grid.getCellWidth();
		double dy = grid.getCellHeight();
		int nx = grid.getNumColumns();
		int ny = grid.getNumRows();
		for (int i = 0; i < nx; i++) {
			for (int j = 0; j < ny; j++) {
				int x = settings.mapX(i * dx);
				int y = settings.mapY((j + 1) * dy);
				g.drawString("" + numDisks[i][j], x + GRID_TEXT_POS_X, y + GRID_TEXT_POS_Y);
			}
		}
	}

	private void paintSkeleton(Graphics2D g, VisualizationSettings settings) {
		g.setStroke(new BasicStroke(5));
		g.setColor(settings.getColorScheme().permanentEdgesColor);
		for (Line e : permanentEdges) {
			paintEdge(g, e, settings);
		}
		g.setColor(settings.getColorScheme().transientEdgesColor);
		for (Line e : transientEdges) {
			paintEdge(g, e, settings);
		}
	}

	private void paintCollisions(Graphics2D g, VisualizationSettings settings) {
		g.setColor(settings.getColorScheme().collisionsColor);
		for (Point p : collisions) {
			int x = settings.mapX(p.x);
			int y = settings.mapY(p.y);
			g.fill(settings.getCollisionShape(x, y));
		}
		g.setColor(settings.getColorScheme().blockingCollisionsColor);
		for (Point p : blockedCollisions) {
			int x = settings.mapX(p.x);
			int y = settings.mapY(p.y);
			g.fill(settings.getCollisionShape(x, y));
		}
	}

	private void paintEdge(Graphics2D g, Line e, VisualizationSettings settings) {
		g.drawLine(settings.mapX(e.getX1()), settings.mapY(e.getY1()), settings.mapX(e.getX2()), settings.mapY(e.getY2()));
	}

	private void paintTime(Graphics2D g, VisualizationSettings settings) {
		g.setColor(settings.getTimeColor());
		String text = getTimeText(time, settings.getDayLength());
		g.drawString(text, TIME_POS_X, TIME_POS_Y);
	}

	public static String getTimeText(double time, double dayLength) {
		String text;
		if ((dayLength > 0.0) && (time > dayLength)) {
			text = String.format("%3.2f days", time/dayLength);			
		} else {
			if (time < 60.0) {
				text = String.format("%2.2f sec", time);
			} else if (time < 3600.0) {
				text = String.format("%2.2f min", time/60.0);
			} else {
				text = String.format("%2.2f hours", time/3600.0);
			}
		}
		return text;
	}
	
	private static void paintWall(Graphics2D g, Wall w, VisualizationSettings settings) {
		g.setColor(settings.getWallColor());
		double x1 = w.getX1();
		double y1 = w.getY1();
		double x2 = w.getX2();
		double y2 = w.getY2();
		double d = w.getHalfThickness();
		double dx = w.getHalfThicknessX();
		double dy = w.getHalfThicknessY();
		paintWallEnd(g, x1, y1, d, settings);
		paintWallEnd(g, x2, y2, d, settings);
		paintRectangle(g, x1 - dx, y1 - dy, x1 + dx, y1 + dy, x2 + dx, y2 + dy, x2 - dx, y2 - dy, settings);
	}

	private static void paintWallEnd(Graphics2D g, double cx, double cy, double r, VisualizationSettings settings) {
		int x1 = settings.mapX(cx - r);
		int y1 = settings.mapY(cy + r);
		int x2 = settings.mapX(cx + r);
		int y2 = settings.mapY(cy - r);
		g.fillArc(x1 - 1, y1 - 1, x2 - x1 + 1, y2 - y1 + 1, 0, 360);
	}

	private static void paintRectangle(Graphics2D g, double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, VisualizationSettings settings) {
		g.fillPolygon(new int[] { settings.mapX(x1), settings.mapX(x2), settings.mapX(x3), settings.mapX(x4) },
				new int[] { settings.mapY(y1), settings.mapY(y2), settings.mapY(y3), settings.mapY(y4) }, 4);
	}

	private void paintFloor(Graphics2D g, VisualizationSettings settings) {
		int x1 = settings.mapX(floor.getPosX(0));
		for (int i = 0; i < floor.getNumX(); i++) {
			int y1 = settings.mapY(floor.getPosY(0));
			int x2 = settings.mapX(floor.getPosX(i + 1));
			for (int j = 0; j < floor.getNumY(); j++) {
				int y2 = settings.mapY(floor.getPosY(j + 1));
				g.setColor(floor.getType(i, j).getDisplayColor());
				g.fillRect(x1, y2, x2 - x1, y1 - y2);
				y1 = y2;
			}
			x1 = x2;
		}
	}

	private void clear(Graphics2D g, VisualizationSettings settings) {
		int x1 = settings.mapX(floor.getPosX(0));
		int y1 = settings.mapY(floor.getPosY(0));
		int x2 = settings.mapX(floor.getPosX(floor.getNumX()));
		int y2 = settings.mapY(floor.getPosY(floor.getNumY()));
		g.setColor(settings.getColorScheme().getNoFloorColor());
		g.fillRect(x1, y2, x2 - x1, y1 - y2);
	}
}
