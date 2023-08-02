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
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.lang.impl.iterators.IntArrayIterator;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.PrimitiveIterator;
import java.util.RandomAccess;
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

final class IntArrayList extends AbstractList<Integer>
    implements PrimitiveList.OfInt, RandomAccess {
  private static final long serialVersionUID = 3100503410988216890L;
  private int size;
  private int[] array;

  public static final Supplier<IntArrayList> COLLECTION_SUPPLIER = IntArrayList::new;
  public static final ObjIntConsumer<IntArrayList> COLLECTION_ACCUMULATOR = //
      PrimitiveList.OfInt::addInt;
  public static final BiConsumer<IntArrayList, IntArrayList> COLLECTION_COMBINER = (acc,
      values) -> acc.forEach((IntConsumer) values::addInt);
  /**
   * Value set if the generic interface gets a {@code null}.
   */
  public static final int NULL = Integer.MIN_VALUE;

  IntArrayList() {
    this(PrimitiveListSettings.INITIAL_CAPACITY);
  }

  IntArrayList(final int capacity) {
    array = new int[capacity];
  }

  IntArrayList(final int value, final int size) {
    array = new int[size];
    this.size = size;
    Arrays.fill(array, value);
  }

  IntArrayList(final int[] values) {
    array = values;
    size = values.length;
  }

  IntArrayList(final int size, boolean dummy) {
    array = new int[size];
    this.size = size;
  }

  @Override
  public Integer get(final int index) {
    return getInt(index);
  }

  @Override
  public int[] array() {
    if (array.length > size) {
      ensureCapacity(size);
    }
    return array;
  }

  @Override
  public int[] arrayQuick() {
    return array;
  }

  @Override
  public IntStream intStream() {
    return Arrays.stream(array, 0, size);
  }

  @Override
  public void ensureCapacity(final int newSize) {
    if (newSize < size) {
      throw new IllegalArgumentException("cannot decrease capacity below size");
    }
    final int[] tmp = new int[newSize];
    System.arraycopy(array, 0, tmp, 0, size);
    array = tmp;
  }

  @Override
  public void setSize(final Integer value, final int newSize) {
    if (newSize > size) {
      if (newSize > array.length) {
        ensureCapacity(Math.max((int) (1 + PrimitiveListSettings.GROWTH * size), newSize));
      }
      Arrays.fill(array, size, newSize, unboxWithNull(value));
    }
    size = newSize;
  }

  private int unboxWithNull(final Integer value) {
    if (value != null) {
      return value.intValue();
    }
    return NULL;
  }

  @Override
  public Integer set(final int index, final Integer element) {
    if (index >= size) {
      throw new IndexOutOfBoundsException(index + " >= " + size);
    }
    return setInt(index, unboxWithNull(element));
  }

  @Override
  public boolean add(final Integer value) {
    addInt(unboxWithNull(value));
    return true;
  }

  @Override
  public void addInt(final int value) {
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
    // remove region (move end..size forward)
    System.arraycopy(array, end, array, begin, size - end);
    size -= length;
    if (Math.max(size,
        PrimitiveListSettings.INITIAL_CAPACITY) < (PrimitiveListSettings.SHRINK * array.length)) {
      ensureCapacity(size);
    }
  }

  @Override
  public int setInt(final int index, final int value) {
    if (index < size) {
      int old = array[index];
      array[index] = value;
      return old;
    } else {
      throw new ArrayIndexOutOfBoundsException(index + " >= " + size);
    }
  }

  @Override
  public int getInt(final int index) {
    if (index >= size) {
      throw new ArrayIndexOutOfBoundsException(index + " >= " + size);
    }
    return array[index];
  }

  @Override
  public int removeInt(final int index) {
    final int value = getInt(index);
    removeIndex(index);
    return value;
  }

  @Override
  public Integer remove(final int index) {
    return removeInt(index);
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
  public PrimitiveIterator.OfInt iterator() {
    return new IntArrayIterator(array, 0, size);
  }

  @Override
  public int hashCode() {
    // cannot use Arrays.hash() since it does not support ranges
    int hashCode = 1;
    for (final int i : this) {
      hashCode = 31 * hashCode + Integer.hashCode(i);
    }
    return hashCode;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof ConstMapping.OfInt)) {
      return false;
    }
    final PrimitiveIterator.OfInt e1 = iterator();
    final PrimitiveIterator.OfInt e2 = ((ConstMapping.OfInt) other).iterator();
    while (e1.hasNext() && e2.hasNext()) {
      if (e1.nextInt() != e2.nextInt()) {
        return false;
      }
    }
    return !(e1.hasNext() || e2.hasNext());
  }

  @Override
  public int[] toUnboxedArray() {
    return Arrays.copyOf(array, size);
  }
}
