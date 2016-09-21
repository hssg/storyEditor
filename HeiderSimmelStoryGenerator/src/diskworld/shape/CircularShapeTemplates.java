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
package diskworld.shape;

public class CircularShapeTemplates {

	public static ShapeTemplate getConeTemplate(final double centerAngle, final double openingAngle, final double minRangeRelativeToRadius, final double maxRangeAbsolute) {
		return new ShapeTemplate() {
			@Override
			public Shape getShape(double centerx, double centery, double radius, double angle) {
				return new RingSegmentShape(centerx, centery, minRangeRelativeToRadius * radius, maxRangeAbsolute, centerAngle - openingAngle * 0.5 + angle, openingAngle);
			}
		};
	}

	public static ShapeTemplate getRingTemplate(final double minRangeRelativeToRadius, final double maxRangeAbsolute) {
		return new ShapeTemplate() {
			@Override
			public Shape getShape(double centerx, double centery, double radius, double angle) {
				return new RingShape(centerx, centery, minRangeRelativeToRadius * radius, maxRangeAbsolute);
			}
		};
	}

	public static ShapeTemplate getAbsoluteCircleTemplate(final double maxRangeAbsolute) {
		return new ShapeTemplate() {
			@Override
			public Shape getShape(double centerx, double centery, double radius, double angle) {
				return new CircleShape(centerx, centery, maxRangeAbsolute);
			}
		};
	}

	public static ShapeTemplate getRelativeCircleTemplate(final double maxRangeRelative) {
		return new ShapeTemplate() {
			@Override
			public Shape getShape(double centerx, double centery, double radius, double angle) {
				return new CircleShape(centerx, centery, maxRangeRelative * radius);
			}
		};
	}

}
