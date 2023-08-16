/*
 * This file is part of netroles.
 *
 * netroles is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * netroles is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with netroles.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.ethz.sn.visone3.roles.distances;

/**
 * Represents a (not necessarily symmetric) integer distance matrix on a
 * quadratic domain of a particular size.
 */
public interface IntDistanceMatrix {

  /**
   * Returns the size of the domain.
   * 
   * @return the size.
   */
  public int getDomainSize();

  /**
   * Returns the distance from {@code i} to {@code j}.
   * 
   * @param i the first element.
   * @param j the second element.
   * @return the distance from {@code i} to {@code j}.
   */
  public int getDistance(int i, int j);

  /**
   * Produces a representation of the pairwise distances as a two-dimensional
   * integer array.
   * 
   * @return a two-dimensional integer array containing pairwise distances.
   */
  public int[][] asMatrix();
}
