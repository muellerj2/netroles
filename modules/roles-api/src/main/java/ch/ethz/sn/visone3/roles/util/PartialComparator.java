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
 * along with visone3.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.ethz.sn.visone3.roles.util;

import java.util.Comparator;

/**
 * A comparison function which imposes a preorder on a collection of objects.
 * 
 * @param <T>
 *          the type of objects that may be compared by this comparator
 */
public interface PartialComparator<T> {
  /**
   * Represents the four kinds of possible results when comparing objects under an imposed preorder.
   * 
   */
  public enum ComparisonResult {
    /**
     * The first object is considered less than the second object.
     */
    LESS,
    /**
     * The first object is considered equal to the second object.
     */
    EQUAL,
    /**
     * The first object is considered greater than the second object.
     */
    GREATER,
    /**
     * The two objects are incomparable.
     */
    INCOMPARABLE
  }

  /**
   * Determines the order of its two arguments.
   * 
   * @param lhs
   *          the first object to be compared
   * @param rhs
   *          the second object to be compared
   * @return {@code ComparisonResult.INCOMPARABLE} if the two objects cannot be ordered under the
   *         imposed preorder; otherwise, returns {@code ComparisonResult.LESS},
   *         {@code ComparisonResult.EQUAL}, or {@code ComparisonResult.GREATER} as the first
   *         argument is less than, equal to, or greater than the second argument
   */
  PartialComparator.ComparisonResult compare(T lhs, T rhs);

  /**
   * Represents a java.util.Comparator imposing a weak order as a PartialComparator object.
   * 
   * @param comparator
   *          the comparator
   * @return the representation as an instance of PartialComparator
   */
  public static <T> PartialComparator<T> fromComparator(Comparator<T> comparator) {
    return (lhs, rhs) -> {
      int result = comparator.compare(lhs, rhs);
      if (result == 0) {
        return ComparisonResult.EQUAL;
      } else if (result < 0) {
        return ComparisonResult.LESS;
      } else {
        return ComparisonResult.GREATER;
      }
    };
  }
}