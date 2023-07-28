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

package ch.ethz.sn.visone3.lang.impl.iterators;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

/**
 * Iterator over an array of doubles.
 */
public final class DoubleArrayIterator implements PrimitiveIterator.OfDouble {
  private final double[] array;
  private final int end;
  private int index;

  /**
   * Constructs an iterator over the specified array.
   * 
   * @param array
   *          the array.
   */
  public DoubleArrayIterator(final double[] array) {
    this(array, 0, array.length);
  }

  /**
   * Constructs an iterator over the specified range in the array.
   * 
   * @param array
   *          the array.
   * @param begin
   *          the first index in the array to iterate over.
   * @param end
   *          the last index (exclusive) in the array to iterate over.
   */
  public DoubleArrayIterator(final double[] array, final int begin, final int end) {
    this.array = array;
    index = begin;
    this.end = end;
  }

  @Override
  public boolean hasNext() {
    return index < end;
  }

  @Override
  public double nextDouble() {
    if (index < end) {
      return array[index++];
    }
    throw new NoSuchElementException();
  }
}
