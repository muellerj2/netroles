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
import ch.ethz.sn.visone3.lang.impl.iterators.ArrayIterator;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import java.util.stream.Stream;

final class GenericArrayList<T> extends AbstractList<T> implements PrimitiveList<T>, RandomAccess {

  private static final long serialVersionUID = 3100503410988216890L;
  private final Class<T> componentType;
  private int size;
  private T[] array;

  private static <T> void requireReferenceType(Class<T> componentType) {
    if (componentType.isPrimitive()) {
      throw new IllegalArgumentException("primitive type not allowed");
    }
  }

  GenericArrayList(final Class<T> componentType) {
    this(componentType, PrimitiveListSettings.INITIAL_CAPACITY);
  }

  @SuppressWarnings("unchecked")
  GenericArrayList(final Class<T> componentType, final T[] values) {
    requireReferenceType(componentType);
    this.componentType = componentType;
    array = values;
    if (componentType != values.getClass().getComponentType()) {
      array = (T[]) Array.newInstance(componentType, values.length);
      System.arraycopy(values, 0, array, 0, values.length);
    }
    size = values.length;
  }

  @SuppressWarnings("unchecked")
  GenericArrayList(final Class<T> componentType, final int capacity) {
    requireReferenceType(componentType);
    array = (T[]) Array.newInstance(componentType, capacity);
    this.componentType = componentType;
  }

  @SuppressWarnings("unchecked")
  GenericArrayList(final Class<T> componentType, T value, final int size) {
    requireReferenceType(componentType);
    array = (T[]) Array.newInstance(componentType, size);
    this.componentType = componentType;
    this.size = size;
    Arrays.fill(array, value);
  }

  @Override
  public Stream<T> stream() {
    return Arrays.stream(array, 0, size);
  }

  @Override
  public Class<T> getComponentType() {
    return componentType;
  }

  @Override
  public T get(final int index) {
    if (index >= size) {
      throw new ArrayIndexOutOfBoundsException();
    }
    return array[index];
  }

  @Override
  public void ensureCapacity(final int newSize) {
    if (newSize < size) {
      throw new IllegalArgumentException("cannot decrease capacity below size");
    }
    @SuppressWarnings("unchecked")
    final T[] tmp = (T[]) Array.newInstance(componentType, newSize);
    System.arraycopy(array, 0, tmp, 0, size);
    array = tmp;
  }

  @Override
  public void setSize(final T value, final int newSize) {
    if (newSize > size) {
      if (newSize > array.length) {
        ensureCapacity(Math.max((int) (1 + PrimitiveListSettings.GROWTH * size), newSize));
      }
      Arrays.fill(array, size, newSize, value);
    }
    size = newSize;
  }

  @Override
  public T set(final int index, final T element) {
    if (index >= size) {
      throw new IndexOutOfBoundsException();
    }
    T old = get(index);
    array[index] = element;
    return old;
  }

  @Override
  public boolean add(final T value) {
    if (size == array.length) {
      ensureCapacity((int) (1 + PrimitiveListSettings.GROWTH * size));
    }
    array[size] = value;
    ++size;
    return true;
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
  public T remove(final int index) {
    T value = get(index);
    removeIndex(index);
    return value;
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
  public Iterator<T> iterator() {
    return new ArrayIterator<>(array, 0, size);
  }

  @Override
  public int hashCode() {
    int hashCode = 1;
    for (final T i : this) {
      hashCode = 31 * hashCode + (i == null ? 0 : i.hashCode());
    }
    return hashCode;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof List)) {
      return false;
    }
    // this list cannot store primitive values, but the other list does ->
    // values are actually not equal
    if (other instanceof PrimitiveList
        && ((PrimitiveList<?>) other).getComponentType().isPrimitive()) {
      return false;
    }

    final Iterator<T> e1 = iterator();
    final Iterator<?> e2 = ((List<?>) other).iterator();
    while (e1.hasNext() && e2.hasNext()) {
      T v1 = e1.next();
      Object v2 = e2.next();
      if (v1 == null) {
        if (v2 != null) {
          return false;
        }
      } else if (!v1.equals(v2)) {
        return false;
      }
    }
    return !(e1.hasNext() || e2.hasNext());
  }

  @Override
  public T[] toUnboxedArray() {
    return Arrays.copyOf(array, size);
  }
}
