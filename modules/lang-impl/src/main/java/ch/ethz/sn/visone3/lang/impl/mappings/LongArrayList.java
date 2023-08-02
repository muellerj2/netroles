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

import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.lang.impl.iterators.LongArrayIterator;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.PrimitiveIterator;
import java.util.RandomAccess;
import java.util.function.BiConsumer;
import java.util.function.LongConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;
import java.util.stream.LongStream;

final class LongArrayList extends AbstractList<Long> implements PrimitiveList.OfLong, RandomAccess {
  private static final long serialVersionUID = 850447995748072041L;
  private int size;
  private long[] array;

  public static final Supplier<LongArrayList> COLLECTION_SUPPLIER = LongArrayList::new;
  public static final ObjLongConsumer<LongArrayList> COLLECTION_ACCUMULATOR = //
      LongArrayList::addLong;
  public static final BiConsumer<LongArrayList, LongArrayList> COLLECTION_COMBINER = (acc,
      values) -> acc.forEach((LongConsumer) values::addLong);

  LongArrayList() {
    this(PrimitiveListSettings.INITIAL_CAPACITY);
  }

  LongArrayList(final int capacity) {
    array = new long[capacity];
  }

  LongArrayList(final long value, final int size) {
    array = new long[size];
    this.size = size;
    Arrays.fill(array, value);
  }

  LongArrayList(final long[] values) {
    array = values;
    size = values.length;
  }

  LongArrayList(final int size, boolean dummy) {
    array = new long[size];
    this.size = size;
  }

  @Override
  public Long get(final int index) {
    return getLong(index);
  }

  @Override
  public long[] array() {
    if (array.length > size) {
      ensureCapacity(size);
    }
    return array;
  }

  @Override
  public long[] arrayQuick() {
    return array;
  }

  @Override
  public LongStream longStream() {
    return Arrays.stream(array, 0, size);
  }

  @Override
  public void ensureCapacity(final int newSize) {
    if (newSize < size) {
      throw new IllegalArgumentException("cannot decrease capacity below size");
    }
    final long[] tmp = new long[newSize];
    System.arraycopy(array, 0, tmp, 0, size);
    array = tmp;
  }

  @Override
  public void setSize(final Long value, final int newSize) {
    if (newSize > size) {
      if (newSize > array.length) {
        ensureCapacity(Math.max((int) (1 + PrimitiveListSettings.GROWTH * size), newSize));
      }
      Arrays.fill(array, size, newSize, unboxWithNull(value));
    }
    size = newSize;
  }

  private long unboxWithNull(final Long value) {
    if (value != null) {
      return value;
    }
    return Long.MIN_VALUE;
  }

  @Override
  public Long set(final int index, final Long element) {
    if (index >= size) {
      throw new IndexOutOfBoundsException();
    }
    return setLong(index, unboxWithNull(element));
  }

  @Override
  public boolean add(final Long value) {
    addLong(unboxWithNull(value));
    return true;
  }

  @Override
  public void addLong(final long value) {
    if (size == array.length) {
      ensureCapacity((int) (1 + PrimitiveListSettings.GROWTH * size));
    }
    array[size] = value;
    ++size;
  }

  @Override
  public void removeRange(final int begin, final int end) {
    if (end < begin) {
      throw new IllegalArgumentException("negative size index range");
    }
    final int length = end - begin;
    if (length > 0 && (begin < 0 || end > size)) {
      throw new IndexOutOfBoundsException("index range out of bounds");
    }
    System.arraycopy(array, end, array, begin, size - end);
    size -= length;
    if (Math.max(size,
        PrimitiveListSettings.INITIAL_CAPACITY) < (PrimitiveListSettings.SHRINK * array.length)) {
      ensureCapacity(size + 1);
    }
  }

  @Override
  public Long remove(final int index) {
    long value = getLong(index);
    removeIndex(index);
    return value;
  }

  @Override
  public long setLong(final int index, final long value) {
    if (index < size) {
      long old = array[index];
      array[index] = value;
      return old;
    } else {
      throw new ArrayIndexOutOfBoundsException(index + " < " + size);
    }
  }

  @Override
  public long getLong(final int index) {
    if (index >= size) {
      throw new ArrayIndexOutOfBoundsException();
    }
    return array[index];
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public void clear() {
    size = 0;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("{size=").append(size).append(",[");
    FormatterUtility.limited(sb, iterator(), 80);
    sb.append("]}");
    return sb.toString();
  }

  @Override
  public PrimitiveIterator.OfLong iterator() {
    return new LongArrayIterator(array, 0, size);
  }

  @Override
  public int hashCode() {
    int hashCode = 1;
    for (final long i : this) {
      hashCode = (int) (31 * hashCode + Long.hashCode(i));
    }
    return hashCode;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof PrimitiveList.OfLong)) {
      return false;
    }
    final PrimitiveIterator.OfLong e1 = iterator();
    final PrimitiveIterator.OfLong e2 = ((PrimitiveList.OfLong) other).iterator();
    while (e1.hasNext() && e2.hasNext()) {
      if (e1.nextLong() != e2.nextLong()) {
        return false;
      }
    }
    return !(e1.hasNext() || e2.hasNext());
  }

  @Override
  public long[] toUnboxedArray() {
    return Arrays.copyOf(array, size);
  }
}
