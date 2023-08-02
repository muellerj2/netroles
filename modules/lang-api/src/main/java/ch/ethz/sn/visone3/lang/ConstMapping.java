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

import java.io.Serializable;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Mapping integers to values (it's an array). The mapping is not mutable through the provided
 * interface.
 *
 * @param <W>
 *          value type.
 */
public interface ConstMapping<W> extends Iterable<W>, Serializable {

  /**
   * Returns the component type of elements held in this mapping.
   * 
   * @return the component type
   */
  Class<W> getComponentType();

  /**
   * Returns the element indexed by the argument.
   * 
   * @param index
   *          the index
   * @return the element at this index
   */
  default W get(final Indexed index) {
    return get(index.getIndex());
  }

  /**
   * Returns the element at this index.
   * 
   * @param index
   *          the integer index
   * @return the element
   */
  W get(int index);

  /**
   * Returns the size of this mapping.
   * 
   * @return the size
   */
  int size();

  // ConstMapping<W> copy();

  /**
   * Constructs a stream on the elements in this mapping.
   * 
   * @return the stream
   */
  Stream<W> stream();

  /**
   * Returns an (unboxed) array containing the elements in this mapping.
   * 
   * @return the (unboxed) array
   */
  Object toUnboxedArray();

  /**
   * Immutable mapping interface specifically for integers.
   */
  interface OfInt extends ConstMapping<Integer>, PrimitiveIterable.OfInt {
    @Override
    default Class<Integer> getComponentType() {
      return int.class;
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated use {@link #getInt(Indexed)} in order to skip unnecessary boxing/unboxing when
     *             type is known
     */
    @Override
    @Deprecated
    default Integer get(final Indexed index) {
      return get(index.getIndex());
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated use {@link #getInt(int)} in order to skip unnecessary boxing/unboxing when type
     *             is known
     */
    @Override
    @Deprecated
    Integer get(int index);

    /**
     * Returns the element at this index.
     * 
     * @param index
     *          the integer index
     * @return the element
     */
    int getInt(final int index);

    /**
     * Returns the element indexed by the argument.
     * 
     * @param index
     *          the index
     * @return the element at this index
     */
    default int getInt(final Indexed index) {
      return getInt(index.getIndex());
    }

    /**
     * Constructs an {@link IntStream} on the elements in this mapping.
     * 
     * @return the stream
     */
    IntStream intStream();

    // @Override
    // default Stream<Integer> stream() {
    // return intStream().boxed();
    // }

    @Override
    int[] toUnboxedArray();
  }

  interface OfLong extends ConstMapping<Long>, PrimitiveIterable.OfLong {
    @Override
    default Class<Long> getComponentType() {
      return long.class;
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated use {@link #getLong(Indexed)} in order to skip unnecessary boxing/unboxing when
     *             type is known
     */
    @Override
    @Deprecated
    default Long get(final Indexed index) {
      return get(index.getIndex());
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated use {@link #getLong(int)} in order to skip unnecessary boxing/unboxing when type
     *             is known
     */
    @Override
    @Deprecated
    Long get(int index);

    /**
     * Returns the element at this index.
     * 
     * @param index
     *          the integer index
     * @return the element
     */
    long getLong(final int index);

    /**
     * Returns the element indexed by the argument.
     * 
     * @param index
     *          the index
     * @return the element at this index
     */
    default long getLong(final Indexed index) {
      return getLong(index.getIndex());
    }

    /**
     * Constructs a {@link LongStream} on the elements in this mapping.
     * 
     * @return the stream
     */
    LongStream longStream();

    @Override
    long[] toUnboxedArray();

    // @Override
    // default Stream<Long> stream() {
    // return longStream().boxed();
    // }
  }

  interface OfDouble extends ConstMapping<Double>, PrimitiveIterable.OfDouble {
    @Override
    default Class<Double> getComponentType() {
      return double.class;
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated use {@link #getDouble(Indexed)} in order to skip unnecessary boxing/unboxing when
     *             type is known
     */
    @Override
    @Deprecated
    default Double get(final Indexed index) {
      return get(index.getIndex());
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated use {@link #getDouble(int)} in order to skip unnecessary boxing/unboxing when
     *             type is known
     */
    @Override
    @Deprecated
    Double get(int index);

    /**
     * Returns the element at this index.
     * 
     * @param index
     *          the integer index
     * @return the element
     */
    double getDouble(final int index);

    /**
     * Returns the element indexed by the argument.
     * 
     * @param index
     *          the index
     * @return the element at this index
     */
    default double getDouble(final Indexed index) {
      return getDouble(index.getIndex());
    }

    /**
     * Constructs a {@link DoubleStream} on the elements in this mapping.
     * 
     * @return the stream
     */
    DoubleStream doubleStream();

    @Override
    double[] toUnboxedArray();

    // @Override
    // default Stream<Double> stream() {
    // return doubleStream().boxed();
    // }
  }
}
