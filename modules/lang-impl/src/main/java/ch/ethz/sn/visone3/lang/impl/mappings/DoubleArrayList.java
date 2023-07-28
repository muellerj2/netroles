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

import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.lang.impl.iterators.DoubleArrayIterator;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.PrimitiveIterator;
import java.util.RandomAccess;
import java.util.function.BiConsumer;
import java.util.function.DoubleConsumer;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

final class DoubleArrayList extends AbstractList<Double>
    implements PrimitiveList.OfDouble, RandomAccess {
  /**
   * Value set if the generic interface gets a {@code null}.
   */
  public static final double NULL = Double.NaN;
  private static final long serialVersionUID = -8155350961224033587L;
  private int size;
  private double[] array;

  public static final Supplier<DoubleArrayList> COLLECTION_SUPPLIER = DoubleArrayList::new;
  public static final ObjDoubleConsumer<DoubleArrayList> COLLECTION_ACCUMULATOR = //
      DoubleArrayList::addDouble;
  public static final BiConsumer<DoubleArrayList, DoubleArrayList> COLLECTION_COMBINER = (acc,
      values) -> acc.forEach((DoubleConsumer) values::addDouble);

  DoubleArrayList() {
    this(PrimitiveListSettings.INITIAL_CAPACITY);
  }

  DoubleArrayList(final int capacity) {
    array = new double[capacity];
  }

  DoubleArrayList(final double value, final int size) {
    array = new double[size];
    this.size = size;
    Arrays.fill(array, value);
  }

  DoubleArrayList(final double[] values) {
    array = values;
    size = values.length;
  }

  DoubleArrayList(int size, boolean dummy) {
    array = new double[size];
    this.size = size;
  }

  @Override
  public Double get(final int index) {
    return getDouble(index);
  }

  @Override
  public double[] array() {
    if (array.length > size) {
      ensureCapacity(size);
    }
    return array;
  }

  @Override
  public double[] arrayQuick() {
    return array;
  }

  @Override
  public DoubleStream doubleStream() {
    return Arrays.stream(array, 0, size);
  }

  @Override
  public void ensureCapacity(final int newSize) {
    if (newSize < size) {
      throw new IllegalArgumentException("cannot decrease capacity below size");
    }
    final double[] tmp = new double[newSize];
    System.arraycopy(array, 0, tmp, 0, size);
    array = tmp;
  }

  @Override
  public void setSize(final Double value, final int newSize) {
    if (newSize > size) {
      if (newSize > array.length) {
        ensureCapacity(Math.max((int) (1 + PrimitiveListSettings.GROWTH * size), newSize));
      }
      Arrays.fill(array, size, newSize, unboxWithNull(value));
    }
    size = newSize;
  }

  private double unboxWithNull(final Double value) {
    if (value != null) {
      return value.doubleValue();
    }
    return NULL;
  }

  @Override
  public Double set(final int index, final Double element) {
    return setDouble(index, unboxWithNull(element));
  }

  @Override
  public boolean add(final Double value) {
    addDouble(unboxWithNull(value));
    return true;
  }

  @Override
  public void addDouble(final double value) {
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
      ensureCapacity(size);
    }
  }

  @Override
  public Double remove(final int index) {
    double value = getDouble(index);
    removeIndex(index);
    return value;
  }

  @Override
  public double setDouble(final int index, final double value) {
    if (index < size) {
      double old = array[index];
      array[index] = value;
      return old;
    } else {
      throw new IndexOutOfBoundsException(index + " >= " + size);
    }
  }

  @Override
  public double getDouble(final int index) {
    if (index >= size) {
      throw new IndexOutOfBoundsException();
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
  public PrimitiveIterator.OfDouble iterator() {
    return new DoubleArrayIterator(array, 0, size);
  }

  @Override
  public int hashCode() {
    int hashCode = 1;
    for (final double i : this) {
      hashCode = (int) (31 * hashCode + Double.hashCode(i));
    }
    return hashCode;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof PrimitiveList.OfDouble)) {
      return false;
    }
    final PrimitiveIterator.OfDouble e1 = iterator();
    final PrimitiveIterator.OfDouble e2 = ((PrimitiveList.OfDouble) other).iterator();
    while (e1.hasNext() && e2.hasNext()) {
      if (Double.doubleToRawLongBits(e1.nextDouble()) != Double
          .doubleToRawLongBits(e2.nextDouble())) {
        return false;
      }
    }
    return !(e1.hasNext() || e2.hasNext());
  }

  @Override
  public double[] toUnboxedArray() {
    return Arrays.copyOf(array, size);
  }
}
