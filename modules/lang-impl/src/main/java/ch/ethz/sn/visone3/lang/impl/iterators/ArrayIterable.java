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

package ch.ethz.sn.visone3.lang.impl.iterators;

import java.util.Iterator;

/**
 * Iterable over a generic array.
 * 
 * @param <T>
 *          value type of the array.
 */
public final class ArrayIterable<T> implements Iterable<T> {
  private final T[] array;
  private final int begin;
  private final int end;

  public ArrayIterable(final T[] array) {
    this(array, 0, array.length);
  }

  /**
   * Construct an iterable over a range.
   *
   * @param begin
   *          Start index (inclusive).
   * @param end
   *          End index (exclusive).
   */
  public ArrayIterable(final T[] array, final int begin, final int end) {
    this.array = array;
    this.begin = begin;
    this.end = end;
  }

  @Override
  public Iterator<T> iterator() {
    return new ArrayIterator<>(array, begin, end);
  }
}
