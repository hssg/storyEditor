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
package diskworld.grid;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import diskworld.Disk;

/**
 * @author Jan
 * 
 */
public class Cell implements Serializable  {

	private Set<Disk> intersectingDisks;
	private int[] index;

	/**
	 * Creates a CellDisks object
	 * 
	 * @param colIndex
	 *            x index of cell
	 * @param rowIndex
	 *            y index of cell
	 */
	public Cell(int colIndex, int rowIndex) {
		intersectingDisks = new HashSet<Disk>();
		index = new int[] { colIndex, rowIndex };
	}

	/**
	 * Access to the set of intersecting disks
	 * 
	 * @return set of disks
	 */
	public Set<Disk> getIntersectingDisks() {
		return intersectingDisks;
	}

	/**
	 * Access to the index
	 * 
	 * @return array of length 2 of form {colIndex, rowIndex}
	 */
	public int[] getIndex() {
		return index;
	}
}
