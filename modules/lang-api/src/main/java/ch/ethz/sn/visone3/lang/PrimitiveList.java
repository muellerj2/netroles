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

import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Growable and writable list, see {@link Mapping} for a constant-size and
 * {@link ConstMapping} for read-only interfaces (which instances of this
 * interface can be converted to).
 *
 * @param <T> value type.
 */
public interface PrimitiveList<T> extends Mapping<T>, List<T> {

  @Override
  Stream<T> stream();

  /**
   * Sets the capacity of the list to the specified capacity. This might shrink or grow the internal
   * data structure.
   * 
   * <p>
   * Note that inappropriate calls to this method can defeat the memory management strategy of the
   * underlying implementation and thus severely degrade performance.
   * 
   * @param newCapacity
   *          the new capacity.
   * @throws IllegalArgumentException
   *           if the specified new capacity is less than the current size of the list.
   */
  void ensureCapacity(int newCapacity);

  /**
   * Sets the size of the list to the specified size. If this is less than its previous size, then
   * redundant elements at the end of the list are removed; if this is greater than the previous
   * size, then {@code value} is assigned to the newly added elements.
   * 
   * @param value
   *          the value to assign to any newly added elements at the end of the list.
   * @param newSize
   *          the new size of the list.
   * @throws IllegalArgumentException
   *           if the specified new size is negative.
   */
  void setSize(T value, int newSize);

  /**
   * Removes the element at the specified index, shifting all following elements by one.
   * 
   * @param index
   *          the index of the element to remove.
   */
  default void removeIndex(final int index) {
    removeRange(index, index + 1);
  }

  /**
   * Removes the element at the specified index, shifting all following elements by one.
   * 
   * @param index
   *          the index of the element to remove.
   */
  default void removeIndex(final Indexed index) {
    removeIndex(index.getIndex());
  }

  /**
   * Removes the elements in the specified range between {@code begin} (inclusive) and {@code end}
   * (exclusive), shifting all following elements.
   * 
   * @param begin
   *          the index of the first element to remove.
   * @param end
   *          the index following the one of the last element to remove.
   */
  void removeRange(int begin, int end);

  @Override
  int size();

  @Override
  boolean isEmpty();

  @Override
  void clear();

  /**
   * List specifically for integers.
   */
  interface OfInt extends PrimitiveList<Integer>, Mapping.OfInt {

    /**
     * {@inheritDoc} Provides direct access to data.
     *
     * @return a direct reference to the underlying array.
     */
    @Override
    int[] array();

    /**
     * Returns the internal array wrapped by this list, providing direct access to data.
     * 
     * <p>
     * Note that this array can be larger than the number of elements if the list's capacity is
     * greater than its size. If this is undesired, use {@link #array()} instead, which resizes the
     * array if size and capacity differ.
     * 
     * @return a direct reference to the underlying array.
     */
    int[] arrayQuick();

    @Override
    default Stream<Integer> stream() {
      return intStream().boxed();
    }

    @Override
    IntStream intStream();

    /**
     * Appends the specified integer to the end of the list.
     * 
     * @param value the integer.
     * @see #add(Object) for the boxed version.
     */
    void addInt(int value);

    /**
     * Removes the element at the specified index from the list, shifting all the following
     * elements, and returns the removed element.
     * 
     * @param index
     *          the index.
     * @return the value that was located at the specified index.
     */
    int removeInt(int index);

    @Override
    int setInt(int index, int value);

    /**
     * {@inheritDoc}
     * 
     * @deprecated use {@link #getInt(int)} in order to skip unnecessary boxing/unboxing when type
     *             is known.
     */
    @Override
    @Deprecated
    Integer get(final int index);

    @Override
    int getInt(int index);
  }

  /**
   * List specifically for longs.
   */
  interface OfLong extends PrimitiveList<Long>, Mapping.OfLong {

    /**
     * {@inheritDoc} Provides direct access to data.
     *
     * @return a direct reference to the underlying array.
     */
    @Override
    long[] array();

    /**
     * Returns the internal array wrapped by this list, providing direct access to data.
     * 
     * <p>
     * Note that this array can be larger than the number of elements if the list's capacity is
     * greater than its size. If this is undesired, use {@link #array()} instead, which resizes the
     * array if size and capacity differ.
     * 
     * @return a direct reference to the underlying array.
     */
    long[] arrayQuick();

    @Override
    default Stream<Long> stream() {
      return longStream().boxed();
    }

    @Override
    LongStream longStream();

    /**
     * Appends the specified long value to the end of the list.
     * 
     * @param value the long value.
     * @see #add(Object) for the boxed version.
     */
    void addLong(long value);

    @Override
    long setLong(int index, long value);

    /**
     * {@inheritDoc}
     * 
     * @deprecated use {@link #getLong(int)} in order to skip unnecessary boxing/unboxing when type
     *             is known.
     */
    @Override
    @Deprecated
    Long get(final int index);

    @Override
    long getLong(int index);
  }

  /**
   * List specifically for doubles.
   */
  interface OfDouble extends PrimitiveList<Double>, Mapping.OfDouble {

    /**
     * {@inheritDoc} Provides direct access to data.
     *
     * @return a direct reference to the underlying array.
     */
    @Override
    double[] array();

    /**
     * Returns the internal array wrapped by this list, providing direct access to data.
     * 
     * <p>
     * Note that this array can be larger than the number of elements if the list's capacity is
     * greater than its size. If this is undesired, use {@link #array()} instead, which resizes the
     * array if size and capacity differ.
     * 
     * @return a direct reference to the underlying array.
     */
    double[] arrayQuick();

    @Override
    default Stream<Double> stream() {
      return doubleStream().boxed();
    }

    @Override
    DoubleStream doubleStream();

    /**
     * Appends the specified double value to the end of the list.
     * 
     * @param value the double value.
     * @see #add(Object) for the boxed version.
     */
    void addDouble(double value);

    @Override
    double setDouble(int index, double value);

    /**
     * {@inheritDoc}
     * 
     * @deprecated use {@link #getDouble(int)} in order to skip unnecessary boxing/unboxing when
     *             type is known.
     */
    @Override
    @Deprecated
    Double get(final int index);

    @Override
    double getDouble(int index);
  }
}
