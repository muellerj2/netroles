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
package ch.ethz.sn.visone3.lang;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Writable mapping, see {@link ConstMapping} for an immutable version.
 *
 * @param <W>
 *          value type.
 */
public interface Mapping<W> extends ConstMapping<W> {
  /**
   * Check and if constrains are fulfilled set value at index {@code index}.
   *
   * @param index
   *          the index
   * @param value
   *          the value
   * @return the previous value set at index
   */
  W set(int index, W value);

  /**
   * Check and if constrains are fulfilled set value at indexed argument {@code index}.
   *
   * @param index
   *          the indexed argument
   * @param value
   *          the value
   * @return the previous value set at the indexed argument
   */
  default W set(Indexed index, W value) {
    return set(index.getIndex(), value);
  }

  /**
   * Mutable mapping interface specifically for integers.
   */
  interface OfInt extends Mapping<Integer>, ConstMapping.OfInt {
    /**
     * Sets the value at index {@code index}.
     * 
     * @param index
     *          the index
     * @param value
     *          the value
     * @return the previous value set at index
     */
    int setInt(final int index, final int value);

    /**
     * Sets the value at indexed argument {@code index}.
     * 
     * @param index
     *          the indexed argument
     * @param value
     *          the value
     * @return the previous value set at the indexed argument
     */
    default int setInt(final Indexed index, final int value) {
      return setInt(index.getIndex(), value);
    }

    // Mapping.OfInt copy();

    @Override
    IntStream intStream();

    @Override
    @Deprecated
    /**
     * {@inheritDoc}
     * 
     * @deprecated use {@link #setInt(Indexed, int)} in order to skip unnecessary boxing/unboxing
     *             when type is known
     */
    default Integer set(Indexed index, Integer value) {
      return set(index.getIndex(), value);
    }

    /**
     * Returns an array of the elements wrapped by this mapping.
     * 
     * @return the array
     */
    int[] array();
  }

  /**
   * Mutable mapping interface specifically for longs.
   */
  interface OfLong extends Mapping<Long>, ConstMapping.OfLong {
    /**
     * Sets the value at index {@code index}.
     * 
     * @param index
     *          the index
     * @param value
     *          the value
     * @return the previous value set at index
     */
    long setLong(final int index, final long value);

    /**
     * Sets the value at indexed argument {@code index}.
     * 
     * @param index
     *          the indexed argument
     * @param value
     *          the value
     * @return the previous value set at the indexed argument
     */
    default long setLong(final Indexed index, final long value) {
      return setLong(index.getIndex(), value);
    }

    @Override
    @Deprecated
    /**
     * {@inheritDoc}
     * 
     * @deprecated use {@link #setLong(Indexed, long)} in order to skip unnecessary boxing/unboxing
     *             when type is known
     */
    default Long set(Indexed index, Long value) {
      return set(index.getIndex(), value);
    }

    // Mapping.OfLong copy();

    @Override
    LongStream longStream();

    /**
     * Returns an array of the elements wrapped by this mapping.
     * 
     * @return the array
     */
    long[] array();
  }

  /**
   * Mutable mapping interface specifically for doubles.
   */
  interface OfDouble extends Mapping<Double>, ConstMapping.OfDouble {
    /**
     * Sets the value at index {@code index}.
     * 
     * @param index
     *          the index
     * @param value
     *          the value
     * @return the previous value set at index
     */
    double setDouble(final int index, final double value);

    /**
     * Sets the value at indexed argument {@code index}.
     * 
     * @param index
     *          the indexed argument
     * @param value
     *          the value
     * @return the previous value set at the indexed argument
     */
    default double setDouble(final Indexed index, final double value) {
      return setDouble(index.getIndex(), value);
    }

    @Override
    @Deprecated
    /**
     * {@inheritDoc}
     * 
     * @deprecated use {@link #setInt(Indexed, double)} in order to skip unnecessary boxing/unboxing
     *             when type is known
     */
    default Double set(Indexed index, Double value) {
      return set(index.getIndex(), value);
    }

    // Mapping.OfDouble copy();

    @Override
    DoubleStream doubleStream();

    /**
     * Returns an array of the elements wrapped by this mapping.
     * 
     * @return the array
     */
    double[] array();
  }
}
