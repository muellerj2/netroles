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

package ch.ethz.sn.visone3.lang.impl.mappings;

import ch.ethz.sn.visone3.lang.ConstMapping;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

/**
 * Cache less mapping over a range of integer.
 *
 * @see java.util.stream.IntStream#range(int, int) Note: Stream are not {@link Iterable} or
 *      {@link ConstMapping}
 */
final class IntRangeMapping extends IntMappingBase {

  private static final long serialVersionUID = 8789526523901580247L;
  private final int begin;
  private final int end;
  private final int delta;

  public IntRangeMapping(final int begin, final int end) {
    delta = end < begin ? -1 : 1;
    this.begin = begin;
    this.end = end;
  }

  @Override
  public PrimitiveIterator.OfInt iterator() {
    return new Itr();
  }

  private final class Itr implements PrimitiveIterator.OfInt {
    private int index = begin;

    @Override
    public boolean hasNext() {
      return index != end;
    }

    @Override
    public int nextInt() {
      if (hasNext()) {
        int result = index;
        index += delta;
        return result;
      }
      throw new NoSuchElementException();
    }
  }

  @Override
  public int size() {
    return delta * (end - begin);
  }

  @Override
  public int getInt(int index) {
    int size = size();
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
    }
    return begin + index * delta;
  }

  @Override
  public IntStream intStream() {
    IntStream stream = IntStream.range(delta * begin, delta * end);
    if (delta < 0) {
      stream = stream.map(i -> -i);
    }
    return stream;
  }

  @Override
  public int[] toUnboxedArray() {
    return intStream().toArray();
  }
}
