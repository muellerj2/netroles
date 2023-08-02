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

package ch.ethz.sn.visone3.algorithms;

import ch.ethz.sn.visone3.lang.ConstMapping;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Provides methods for descriptive statistics.
 *
 */
public interface Stats {

  /**
   * Integer histogram assuming non-negative values including zero.
   *
   * @param ints
   *          input values.
   * @return Array with number of occurrences.
   */
  int[] hist0(final ConstMapping.OfInt ints);

  /**
   * Integer histogram assuming non-negative values including zero.
   *
   * @param ints
   *          input values.
   * @return Array with number of occurrences.
   */
  int[] hist0(final int[] ints);

  /**
   * Integer histogram assuming non-negative values including zero.
   *
   * @param ints
   *          input values.
   * @param bins
   *          number of bins to use.
   * @return Array with number of occurrences.
   */
  int[] hist0(final int[] ints, int bins);

  /**
   * Integer histogram assuming non-negative values including zero.
   *
   * @param ints input values.
   * @return Array with number of occurrences.
   */
  int[] hist0(final Supplier<PrimitiveIterator.OfInt> ints);

  /**
   * Integer histogram assuming non-negative values including zero.
   *
   * @param ints
   *          input values.
   * @param bins
   *          number of bins to use.
   * @return Array with number of occurrences.
   */
  int[] hist0(final Supplier<PrimitiveIterator.OfInt> ints, int bins);

  /**
   * Integer histogram.
   *
   * @param ints
   *          input values.
   * @return histogram of integer values.
   */
  Hist<Integer> hist(final int[] ints);

  /**
   * Integer histogram.
   *
   * @param ints
   *          input values.
   * @param min
   *          minimum reported integer in the histogram
   * @param max
   *          maximum reported integer in the histogram
   * @return histogram of integer values.
   */
  Hist<Integer> hist(final int[] ints, final int min, final int max);

  /**
   * Double histogram.
   *
   * @param array
   *          double values.
   * @param bins
   *          number of bins to use for the histogram.
   * @return histogram of double values.
   */
  Hist<Double> hist(final ConstMapping.OfDouble array, final int bins);

  /**
   * Categorical histogram.
   *
   * @param array
   *          input values.
   * @return histogram of categorical values.
   */
  <T> Hist<T> hist(final ConstMapping<T> array);

  /**
   * Returns the index of the first global maximum in the array.
   * 
   * @param array
   *          the array to search.
   * @param comp
   *          the ordering of the values in the array.
   * @return index of the first global maximum.
   */
  <T> int argmax(final T[] array, final Comparator<T> comp);

  /**
   * Returns the index of the first global maximum in the array.
   * 
   * @param array
   *          the integer array to search.
   * @return index of the first global maximum.
   */
  int argmax(final int[] array);

  /**
   * Returns the first index at which this element is found in this array, or -1 if it is not
   * contained.
   * 
   * @param <T>
   *          element type.
   * @param array
   *          the array to search through.
   * @param element
   *          the element to find.
   * @return the first index at which this element can be found, or -1 if the element is not in the
   *         array.
   */
  <T> int index(final T[] array, final T element);

  /**
   * Determines the range (minimum and maximum) of values.
   *
   * @param values
   *          input values.
   * @return Range of values.
   */
  <T> Range<T> minMax(final Supplier<? extends Iterator<T>> values, final Comparator<T> comp);

  /**
   * Determines the range (minimum and maximum) of values.
   *
   * @param values
   *          input values.
   * @return Range of values.
   */
  Range<Integer> minMax(final int[] values);

  /**
   * Determines the range (minimum and maximum) of values.
   *
   * @param values
   *          input values.
   * @return Range of values.
   */
  Range<Double> minMax(final ConstMapping.OfDouble values);

  /**
   * Represents a (closed) range of values.
   * 
   * @param <T>
   *          the value type
   */
  public class Range<T> {
    public final T min;
    public final T max;
    final Comparator<T> less;

    /**
     * Constructs a new range object.
     * 
     * @param min
     *          the minimum value of the range.
     * @param max
     *          the maximum value of the range.
     * @param less
     *          the ordering underlying the choice of minimum and maximum.
     */
    public Range(final T min, final T max, final Comparator<T> less) {
      this.min = min;
      this.max = max;
      this.less = less;
    }

    @Override
    public int hashCode() {
      return Objects.hash(min, max);
    }

    @Override
    public boolean equals(Object rhs) {
      if (!(rhs instanceof Range)) {
        return false;
      }
      Range<?> rhsRange = (Range<?>) rhs;
      return Objects.equals(min, rhsRange.min) && Objects.equals(max, rhsRange.max);
    }
  }

  /**
   * Histogram helper for non-integer types.
   */
  class Hist<T> {
    public final T[] value;
    public final int[] count;

    /**
     * Constructs a new histogram representation.
     * 
     * @param value
     *          the representative values in the bins.
     * @param count
     *          the number of values in the bins.
     */
    public Hist(final T[] value, final int[] count) {
      if (value.length != count.length) {
        throw new IllegalArgumentException();
      }
      this.value = value;
      this.count = count;
    }

    /**
     * Returns the representative value for the specified bin.
     * 
     * @param bin
     *          the bin.
     * @return the representative value.
     */
    public T getValue(final int bin) {
      return value[bin];
    }

    /**
     * Returns the count for the specified bin.
     * 
     * @param bin
     *          the bin.
     * @return the count.
     */
    public int getCount(final int bin) {
      return count[bin];
    }

    /**
     * Returns the (first) bin with the maximum count in the histogram.
     * 
     * @return the bin.
     */
    public int getMaxCountBin() {
      return AlgoProvider.getInstance().stats().argmax(count);
    }

    /**
     * Returns the representative value of the (first) bin with the maximum count in the histogram.
     * 
     * @return the representative value.
     */
    public T getMaxCountValue() {
      return value[getMaxCountBin()];
    }

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("{N=").append(value.length).append("[");
      if (value.length > 0) {
        sb.append(value[0]).append('=').append(count[0]);
      }
      for (int i = 1; i < value.length; i++) {
        sb.append(',').append(value[i]).append('=').append(count[i]);
      }
      return sb.append("]}").toString();
    }

    @Override
    public boolean equals(Object rhs) {
      if (!(rhs instanceof Hist)) {
        return false;
      }
      Hist<?> other = (Hist<?>) rhs;
      return Arrays.equals(value, other.value) && Arrays.equals(count, other.count);
    }

    @Override
    public int hashCode() {
      return 23 * Arrays.hashCode(value) + Arrays.hashCode(count);
    }
  }

  /**
   * Produces a collector for constructing a categorical histogram from a stream.
   * 
   * @param <T>
   *          the type of the values.
   * @return the collector.
   */
  <T> Collector<T, ?, Hist<T>> histcollector();

}
