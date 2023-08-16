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

package ch.ethz.sn.visone3.lang;

/**
 * Efficient ordered and unordered (int,int)-tuples.
 */
public final class IntPair {
  private static final int INT_WIDTH = 32;
  private static final long INT_MASK = 0xffffffffL;

  /**
   * Build efficient integer 2-element set. The first element is ensured to be no
   * larger than the second.
   * 
   * @param first  one integer in the pair.
   * @param second the other integer in the pair.
   * @return long representation of the integer 2-element set
   */
  public static long set(final int first, final int second) {
    if (first < second) {
      return tuple(first, second);
    }
    return tuple(second, first);
  }

  /**
   * Build efficient ordered integer pair (2-tuple).
   * 
   * @param first  first integer in the pair.
   * @param second second integer in the pair.
   * 
   * @return long representation of the ordered integer pair
   */
  public static long tuple(final int first, final int second) {
    return ((long) first << INT_WIDTH) | (second & INT_MASK);
  }

  /**
   * Returns the first element of the pair's long representation.
   * 
   * @param pair the long representation of a pair.
   * @return first element of the pair.
   */
  public static int first(final long pair) {
    return (int) (pair >>> INT_WIDTH);
  }

  /**
   * Returns the second element of the pair's long representation.
   * 
   * @param pair the long representation of a pair.
   * @return second element of the pair.
   */
  public static int second(final long pair) {
    return (int) (pair & INT_MASK);
  }

  private final long pair;

  /**
   * Constructs an integer pair object.
   * 
   * @param first  the first element of the pair.
   * @param second the second element of the pair.
   */
  public IntPair(final int first, final int second) {
    pair = tuple(first, second);
  }

  /**
   * Returns the first element of the integer pair.
   * 
   * @return the first element.
   */
  public int getFirst() {
    return first(pair);
  }

  /**
   * Returns the second element of the integer pair.
   * 
   * @return the second element.
   */
  public int getSecond() {
    return second(pair);
  }

  @Override
  public boolean equals(final Object obj) {
    return obj instanceof IntPair && pair == ((IntPair) obj).pair;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(pair);
  }
}
