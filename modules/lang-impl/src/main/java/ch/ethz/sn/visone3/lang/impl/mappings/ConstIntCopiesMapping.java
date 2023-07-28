package ch.ethz.sn.visone3.lang.impl.mappings;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
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
import java.util.stream.IntStream;

final class ConstIntCopiesMapping extends IntMappingBase {

  private static final long serialVersionUID = 8042736811098454068L;

  private final int value;
  private final int size;

  public ConstIntCopiesMapping(int value, int size) {
    this.value = value;
    this.size = size;
  }

  @Override
  public int getInt(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
    return value;
  }

  @Override
  public IntStream intStream() {
    int val = value;
    return IntStream.range(0, size).map(i -> val);
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public PrimitiveIterator.OfInt iterator() {
    return new PrimitiveIterator.OfInt() {
      int pos = 0;
      final int size = ConstIntCopiesMapping.this.size;
      final int value = ConstIntCopiesMapping.this.value;

      @Override
      public boolean hasNext() {
        return pos < size;
      }

      @Override
      public int nextInt() {
        if (pos < size) {
          ++pos;
          return value;
        }
        throw new NoSuchElementException();
      }
    };
  }

  @Override
  public int[] toUnboxedArray() {
    int[] array = new int[size];
    Arrays.fill(array, value);
    return array;
  }

}
