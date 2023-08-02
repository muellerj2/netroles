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

package ch.ethz.sn.visone3.networks;

import ch.ethz.sn.visone3.lang.Indexed;

/**
 * Valued relationship between two elements.
 */
public interface Relationship extends Indexed {
  /**
   * The left element of a relationship.
   * 
   * @return left element
   */
  int getLeft();

  /**
   * The right element of a relationship.
   * 
   * @return right element
   */
  int getRight();

  /**
   * The right element (zero-based domain index) of a relationship.
   * 
   * @return (zero-indexed) right element
   */
  int getRight0();
}
