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

package ch.ethz.sn.visone3.lang.impl.mappings;

import ch.ethz.sn.visone3.lang.impl.iterators.IntArrayIterator;

import java.util.Arrays;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

/**
 * Cache less iterator over an array of integers.
 *
 * @see java.util.Arrays#spliterator(int[], int, int)
 */
class ConstIntArrayMapping extends IntMappingBase {
  private static final long serialVersionUID = -2150209425500788425L;

  protected final int[] array;
  protected final int begin;
  protected final int end;

  public ConstIntArrayMapping(final int[] array) {
    this(array, 0, array.length);
  }

  public ConstIntArrayMapping(final int[] array, final int begin, final int end) {
    this.array = array;
    this.begin = begin;
    this.end = end;
  }

  @Override
  public PrimitiveIterator.OfInt iterator() {
    return new IntArrayIterator(array, begin, end);
  }

  @Override
  public int size() {
    return end - begin;
  }

  @Override
  public int getInt(int index) {
    int size = size();
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
    return array[begin + index];
  }

  @Override
  public IntStream intStream() {
    return Arrays.stream(array, begin, end);
  }

  @Override
  public int[] toUnboxedArray() {
    return Arrays.copyOfRange(array, begin, end);
  }
}
