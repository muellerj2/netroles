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
 * Simple immutable pair class.
 *
 * @param <T>
 *          type of first element
 * @param <U>
 *          type of second element
 */
public class Pair<T, U> {
  private final T first;
  private final U second;

  /**
   * Constructs a new pair.
   * 
   * @param first
   *          the first element
   * @param second
   *          the second element
   */
  public Pair(final T first, final U second) {
    this.first = first;
    this.second = second;
  }

  /**
   * Returns the first element.
   * 
   * @return the first element
   */
  public T getFirst() {
    return first;
  }

  /**
   * Returns the second element.
   * 
   * @return the second element
   */
  public U getSecond() {
    return second;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Pair)) {
      return false;
    }
    final Pair<?, ?> other = (Pair<?, ?>) obj;
    return (first == null ? other.first == null : first.equals(other.first))
        && (second == null ? other.second == null : second.equals(other.second));
  }

  @Override
  public int hashCode() {
    return 23 * (first != null ? first.hashCode() : 0) + (second != null ? second.hashCode() : 0);
  }
}
