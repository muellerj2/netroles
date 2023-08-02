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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

import ch.ethz.sn.visone3.lang.spi.AlgorithmsFacade;
import ch.ethz.sn.visone3.lang.spi.LangProvider;

/**
 * Provides some common algorithms on primitive mappings and arrays.
 *
 */
public final class PrimitiveCollections {
  private static final double EPS = Double.MIN_VALUE;

  private static AlgorithmsFacade facade() {
    return LangProvider.getInstance().algos();
  }

  private PrimitiveCollections() {
  }

  /**
   * Index returning counting sort. Searches the universe size, runs on the complete array. To get
   * the sorted sequence apply {@link #permute(Object[], int[])}.
   * 
   * @param array
   *          integer array to sort
   * @return integer array storing the sort permutation.
   *
   * @see #countingSort(int[], int, int, int, int) for the full description.
   */
  public static int[] countingSort(final int[] array) {
    return facade().countingSort(array);
  }

  /**
   * Index returning counting sort. Assumes non-negative values, runs on the complete array.To get
   * the sorted sequence apply {@link #permute(Object[], int[])}.
   *
   *
   * @param array
   *          integer array to sort
   * @param universeSize
   *          size of the maximum integer range {0, ..., universeSize - 1}. Should be larger than
   *          {@code Arrays.stream(array).max()}.
   * @return integer array storing the sort permutation.
   * 
   * @see #countingSort(int[], int, int, int, int) for the full description.
   */
  public static int[] countingSort(final int[] array, final int universeSize) {
    return countingSort(array, 0, universeSize, 0, array.length);
  }

  /**
   * Index returning counting sort. Assumes {@code array[i] + valueOffset} is non-negative, runs on
   * the complete array.
   *
   * @param array
   *          integer array to sort
   * @param valueOffset
   *          integer added to each value in {@code array}.
   * @param start
   *          output array where start[i] will contain the index of the first element with value
   *          {@code i - valueOffset} after this method returns. The array should have size larger
   *          than {@code Arrays.stream(array).max() + valueOffset} and be filled with zeros.
   * @return integer array storing the sort permutation.
   * 
   * @see #countingSort(int[], int, int[], int, int) for the full description.
   */
  public static int[] countingSort(final int[] array, final int valueOffset, final int[] start) {
    return countingSort(array, valueOffset, start, 0, array.length);
  }

  /**
   * Index returning counting sort. To get the sorted sequence apply
   * {@link #permute(Object[], int[])}.
   *
   * @param array
   *          integer array.
   * @param valueOffset
   *          integer added to each value in {@code array}.
   * @param universeSize
   *          should be larger than {@code Arrays.stream(array).max() + valueOffset}.
   * @param begin
   *          start index (inclusive).
   * @param end
   *          end index (exclusive).
   * @return integer array storing the sort permutation.
   */
  public static int[] countingSort(final int[] array, final int valueOffset, final int universeSize,
      final int begin, final int end) {
    return facade().countingSort(array, valueOffset, universeSize, begin, end);
  }

  /**
   * Index returning counting sort. To get the sorted sequence apply
   * {@link #permute(Object[], int[])}.
   *
   * @param array
   *          integer array.
   * @param valueOffset
   *          integer added to each value in {@code array}.
   * @param start
   *          output array where start[i] will contain the index of the first element with value
   *          {@code i - valueOffset} after this method returns. The array should have size larger
   *          than {@code Arrays.stream(array).max() + valueOffset} and be filled with zeros.
   * @param begin
   *          start index (inclusive).
   * @param end
   *          end index (exclusive).
   * @return integer array storing the sort permutation.
   */
  public static int[] countingSort(final int[] array, final int valueOffset, final int[] start,
      final int begin, final int end) {
    return facade().countingSort(array, valueOffset, start, begin, end);
  }

  /**
   * Index returning counting sort. Searches the universe size, runs on the complete mapping. To get
   * the sorted sequence apply {@link #permute(Object[], int[])}.
   *
   * @param list
   *          integer mapping to sort
   * @return integer array storing the sort permutation.
   *
   * @see #countingSort(ch.ethz.sn.visone3.lang.ConstMapping.OfInt, int, int, int, int) for the full
   *      description.
   */
  public static Mapping.OfInt countingSort(final ConstMapping.OfInt list) {
    return facade().countingSort(list);
  }

  /**
   * Index returning counting sort. Assumes non-negative values, runs on the complete array. To get
   * the sorted sequence apply {@link #permute(Object[], int[])}.
   *
   * @param list
   *          integer mapping to sort
   * @param universeSize
   *          size of the maximum integer range {0, ..., universeSize - 1}. Should be larger than
   *          {@code Arrays.stream(array).max()}.
   * @return integer array storing the sort permutation.
   * @see #countingSort(ch.ethz.sn.visone3.lang.ConstMapping.OfInt, int, int, int, int) for the full
   *      description.
   */
  public static Mapping.OfInt countingSort(final ConstMapping.OfInt list, final int universeSize) {
    return countingSort(list, 0, universeSize, 0, list.size());
  }

  /**
   * Index returning counting sort. Assumes {@code list.getInt(i) + valueOffset} is non-negative,
   * runs on the complete array. To get the sorted sequence apply {@link #permute(Object[], int[])}.
   *
   * @param list
   *          integer mapping to sort
   * @param valueOffset
   *          integer added to each value in {@code list}.
   * @param start
   *          output array where start[i] will contain the index of the first element with value
   *          {@code i - valueOffset} after this method returns. The array should have size larger
   *          than {@code list.intStream().max() + valueOffset} and be filled with zeros.
   * @return integer array storing the sort permutation.
   * @see #countingSort(ch.ethz.sn.visone3.lang.ConstMapping.OfInt, int,
   *      ch.ethz.sn.visone3.lang.Mapping.OfInt, int, int) for the full description.
   */
  public static Mapping.OfInt countingSort(final ConstMapping.OfInt list, final int valueOffset,
      final Mapping.OfInt start) {
    return countingSort(list, valueOffset, start, 0, list.size());
  }

  /**
   * Index returning counting sort. To get the sorted sequence apply
   * {@link #permute(Object[], int[])}.
   *
   * @param list
   *          integer mapping.
   * @param valueOffset
   *          integer added to each value in {@code list}.
   * @param universeSize
   *          should be larger than {@code list.intStream().max() + valueOffset}.
   * @param begin
   *          start index (inclusive).
   * @param end
   *          end index (exclusive).
   * @return integer mapping storing the sort permutation.
   */
  public static Mapping.OfInt countingSort(final ConstMapping.OfInt list, final int valueOffset,
      final int universeSize, final int begin, final int end) {
    return facade().countingSort(list, valueOffset, universeSize, begin, end);
  }

  /**
   * Index returning counting sort. To get the sorted sequence apply
   * {@link #permute(Object[], int[])}.
   *
   * @param list
   *          integer mapping.
   * @param valueOffset
   *          integer added to each value in {@code list}.
   * @param start
   *          output mapping where start[i] will contain the index of the first element with value
   *          {@code i - valueOffset} after this method returns. The mapping should have size larger
   *          than {@code list.intStream().max() + valueOffset} and be filled with zeros.
   * @param begin
   *          start index (inclusive).
   * @param end
   *          end index (exclusive).
   * @return integer mapping storing the sort permutation.
   */
  public static Mapping.OfInt countingSort(ConstMapping.OfInt list, final int valueOffset,
      final Mapping.OfInt start, final int begin, final int end) {
    return facade().countingSort(list, valueOffset, start, begin, end);
  }

  /***
   * Applies a permutation to an array.
   * 
   * @param array
   *          the array
   * @param permutation
   *          the permutation to apply
   * @return New array where {@code i}-th element is {@code array[permutation[i]]}.
   * @see #permute(Object[], int[]) for the full description.
   */
  public static int[] permute(final int[] array, final int[] permutation) {
    if (array.length != permutation.length) {
      throw new IllegalArgumentException();
    }
    return facade().compose(array, permutation);
  }

  /**
   * Applies a permutation to an array.
   * 
   * @param array
   *          the array
   * @param permutation
   *          the permutation to apply
   * @return New array where {@code i}-th element is {@code array[permutation[i]]}.
   * @see #permute(Object[], int[]) for the full description.
   */
  public static double[] permute(final double[] array, final int[] permutation) {
    if (array.length != permutation.length) {
      throw new IllegalArgumentException();
    }
    return facade().compose(array, permutation);
  }

  /**
   * Applies a permutation to an array.
   * 
   * @param array
   *          the array
   * @param permutation
   *          the permutation to apply
   * @return New array where {@code i}-th element is {@code array[permutation[i]]}.
   * @see #permute(Object[], int[]) for the full description.
   */
  public static long[] permute(final long[] array, final int[] permutation) {
    if (array.length != permutation.length) {
      throw new IllegalArgumentException();
    }
    return facade().compose(array, permutation);
  }

  /**
   * Applies a permutation to an array.
   * 
   * @param array
   *          the array
   * @param permutation
   *          the permutation to apply
   * @return New array where {@code i}-th element is {@code array[permutation[i]]}.
   * @see #countingSort(int[]) and siblings on how to get a permutation.
   */
  public static <T> T[] permute(final T[] array, final int[] permutation) {
    if (array.length != permutation.length) {
      throw new IllegalArgumentException();
    }
    return facade().compose(array, permutation);
  }

  /**
   * Applies a permutation to a given mapping.
   * 
   * @param list
   *          the mapping
   * @param permutation
   *          the permutation to apply
   * @return New mapping where {@code i}-th element is {@code list.get(permutation.getInt(i))}.
   * @see #permute(ConstMapping, ch.ethz.sn.visone3.lang.ConstMapping.OfInt) for the full
   *      description.
   */
  public static Mapping.OfInt permute(final ConstMapping.OfInt list,
      final ConstMapping.OfInt permutation) {
    if (list.size() != permutation.size()) {
      throw new IllegalArgumentException();
    }
    return facade().compose(list, permutation);
  }

  /**
   * Applies a permutation to a given mapping.
   * 
   * @param list
   *          the mapping
   * @param permutation
   *          the permutation to apply
   * @return New mapping where {@code i}-th element is {@code list.get(permutation.getInt(i))}.
   * @see #permute(ConstMapping, ch.ethz.sn.visone3.lang.ConstMapping.OfInt) for the full
   *      description.
   */
  public static ConstMapping.OfDouble permute(final ConstMapping.OfDouble list,
      final ConstMapping.OfInt permutation) {
    if (list.size() != permutation.size()) {
      throw new IllegalArgumentException();
    }
    return facade().compose(list, permutation);
  }

  /**
   * Applies a permutation to a given mapping.
   * 
   * @param list
   *          the mapping
   * @param permutation
   *          the permutation to apply
   * @return New mapping where {@code i}-th element is {@code list.get(permutation.getInt(i))}.
   * @see #permute(ConstMapping, ch.ethz.sn.visone3.lang.ConstMapping.OfInt) for the full
   *      description.
   */
  public static ConstMapping.OfLong permute(final ConstMapping.OfLong list,
      final ConstMapping.OfInt permutation) {
    if (list.size() != permutation.size()) {
      throw new IllegalArgumentException();
    }
    return facade().compose(list, permutation);
  }

  /**
   * Applies a permutation to a given mapping.
   * 
   * @param list
   *          the mapping
   * @param permutation
   *          the permutation to apply
   * @return New mapping where {@code i}-th element is {@code list.get(permutation.getInt(i))}.
   * @see #countingSort(ch.ethz.sn.visone3.lang.ConstMapping.OfInt) and siblings on how to get a
   *      permutation.
   */
  public static <T> Mapping<T> permute(final ConstMapping<T> list,
      final ConstMapping.OfInt permutation) {
    if (list.size() != permutation.size()) {
      throw new IllegalArgumentException();
    }
    return facade().compose(list, permutation);
  }

  /**
   * Applies a function to each element of an array and returns the resulting array.
   * 
   * @param array
   *          the array
   * @param mapper
   *          the function to apply to each array element
   * @return new array obtained by applying the {@code mapper} to each array element
   * @see #map(double[], DoubleUnaryOperator)
   * @see #map(Object[], Function)
   */
  public static int[] map(final int[] array, IntUnaryOperator mapper) {
    final int[] tmp = new int[array.length];
    for (int i = 0; i < array.length; i++) {
      tmp[i] = mapper.applyAsInt(array[i]);
    }
    return tmp;
  }

  /**
   * Applies a function to each element of an array and returns the resulting array.
   * 
   * @param array
   *          the array
   * @param mapper
   *          the function to apply to each array element
   * @return new array obtained by applying the {@code mapper} to each array element
   * @see #map(int[], IntUnaryOperator)
   * @see #map(Object[], Function)
   */
  public static double[] map(final double[] array, DoubleUnaryOperator mapper) {
    final double[] tmp = new double[array.length];
    for (int i = 0; i < array.length; i++) {
      tmp[i] = mapper.applyAsDouble(array[i]);
    }
    return tmp;
  }

  /**
   * Applies a function to each element of an array and returns the resulting array.
   * 
   * @param array
   *          the array
   * @param mapper
   *          the function to apply to each array element
   * @return new array obtained by applying the {@code mapper} to each array element
   * @see #map(double[], DoubleUnaryOperator)
   * @see #map(int[], IntUnaryOperator)
   */
  public static <T> T[] map(final T[] array, Function<? super T, ? extends T> mapper) {
    @SuppressWarnings("unchecked")
    final T[] tmp = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length);
    for (int i = 0; i < array.length; i++) {
      tmp[i] = mapper.apply(array[i]);
    }
    return tmp;
  }

  /**
   * Applies the given array to a list of indices, or {@code missingValue} if a provided index is
   * negative.
   * 
   * @param array
   *          the array
   * @param indices
   *          the list of indices to apply
   * @param missingValue
   *          the missing value to use for negative indices
   *
   * @return New array where {@code i}-th element is {@code array[indices[i]]} if {@code indices[i]}
   *         is non-negative, else {@code missingValue}.
   * @see #compose(double[], int[], double)
   * @see #compose(long[], int[], long)
   * @see #compose(Object[], int[], Object)
   */
  public static int[] compose(final int[] array, final int[] indices, final int missingValue) {
    return facade().compose(array, indices, missingValue);
  }

  /**
   * Applies the given array to a list of indices, or {@code missingValue} if a provided index is
   * negative.
   * 
   * @param array
   *          the array
   * @param indices
   *          the list of indices to apply
   * @param missingValue
   *          the missing value to use for negative indices
   *
   * @return New array where {@code i}-th element is {@code array[indices[i]]} if {@code indices[i]}
   *         is non-negative, else {@code missingValue}.
   * @see #compose(int[], int[], int)
   * @see #compose(long[], int[], long)
   * @see #compose(Object[], int[], Object)
   */
  public static double[] compose(final double[] array, final int[] indices,
      final double missingValue) {
    return facade().compose(array, indices, missingValue);
  }

  /**
   * Applies the given array to a list of indices, or {@code missingValue} if a provided index is
   * negative.
   * 
   * @param array
   *          the array
   * @param indices
   *          the list of indices to apply
   * @param missingValue
   *          the missing value to use for negative indices
   *
   * @return New array where {@code i}-th element is {@code array[indices[i]]} if {@code indices[i]}
   *         is non-negative, else {@code missingValue}.
   * @see #compose(double[], int[], double)
   * @see #compose(int[], int[], int)
   * @see #compose(Object[], int[], Object)
   */
  public static long[] compose(final long[] array, final int[] indices, final long missingValue) {
    return facade().compose(array, indices, missingValue);
  }

  /**
   * Applies the given array to a list of indices, or {@code missingValue} if a provided index is
   * negative.
   * 
   * @param array
   *          the array
   * @param indices
   *          the list of indices to apply
   * @param missingValue
   *          the missing value to use for negative indices
   *
   * @return New array where {@code i}-th element is {@code array[indices[i]]} if {@code indices[i]}
   *         is non-negative, else {@code missingValue}.
   * @see #countingSort(int[], int, int, int, int) and siblings on how to get a list of indices.
   */
  public static <T> T[] compose(final T[] array, final int[] indices, final T missingValue) {
    return facade().compose(array, indices, missingValue);
  }

  /**
   * Applies the given mapping to a list of indices, or {@code missingValue} if a provided index is
   * negative.
   *
   * @param list
   *          the mapping
   * @param indices
   *          the list of indices to apply
   * @param missingValue
   *          the missing value to use for negative indices
   * @return New mapping where {@code i}-th element is {@code list.get(indices.getInt(i))} if
   *         {@code indices[i]} is non-negative, else {@code missingValue}.
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfDouble,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt, double)
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfLong,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt, long)
   * @see #compose(ConstMapping, ch.ethz.sn.visone3.lang.ConstMapping.OfInt, Object)
   */
  public static Mapping.OfInt compose(final ConstMapping.OfInt list,
      final ConstMapping.OfInt indices, final int missingValue) {
    return facade().compose(list, indices, missingValue);
  }

  /**
   * Applies the given mapping to a list of indices, or {@code missingValue} if a provided index is
   * negative.
   *
   * @param list
   *          the mapping
   * @param indices
   *          the list of indices to apply
   * @param missingValue
   *          the missing value to use for negative indices
   * @return New mapping where {@code i}-th element is {@code list.get(indices.getInt(i))} if
   *         {@code indices[i]} is non-negative, else {@code missingValue}.
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfInt,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt, int)
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfLong,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt, long)
   * @see #compose(ConstMapping, ch.ethz.sn.visone3.lang.ConstMapping.OfInt, Object)
   */
  public static Mapping.OfDouble compose(final ConstMapping.OfDouble list,
      final ConstMapping.OfInt indices, final double missingValue) {
    return facade().compose(list, indices, missingValue);
  }

  /**
   * Applies the given mapping to a list of indices, or {@code missingValue} if a provided index is
   * negative.
   *
   * @param list
   *          the mapping
   * @param indices
   *          the list of indices to apply
   * @param missingValue
   *          the missing value to use for negative indices
   * @return New mapping where {@code i}-th element is {@code list.get(indices.getInt(i))} if
   *         {@code indices[i]} is non-negative, else {@code missingValue}.
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfDouble,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt, double)
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfInt,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt, int)
   * @see #compose(ConstMapping, ch.ethz.sn.visone3.lang.ConstMapping.OfInt, Object)
   */
  public static Mapping.OfLong compose(final ConstMapping.OfLong list,
      final ConstMapping.OfInt indices, final long missingValue) {
    return facade().compose(list, indices, missingValue);
  }

  /**
   * Applies the given mapping to a list of indices, or {@code missingValue} if a provided index is
   * negative.
   *
   * @param list
   *          the mapping
   * @param indices
   *          the list of indices to apply
   * @param missingValue
   *          the missing value to use for negative indices
   * @return New mapping where {@code i}-th element is {@code list.get(indices.getInt(i))} if
   *         {@code indices[i]} is non-negative, else {@code missingValue}.
   * @see #countingSort(ch.ethz.sn.visone3.lang.ConstMapping.OfInt, int, int, int, int) and siblings
   *      on how to get a list of indices.
   */
  public static <T> Mapping<T> compose(final ConstMapping<T> list, final ConstMapping.OfInt indices,
      final T missingValue) {
    return facade().compose(list, indices, missingValue);
  }

  /**
   * Reverse the order of elements in place.
   * 
   * @param array
   *          the array to reverse
   */
  public static void reverse(final int[] array) {
    for (int l = array.length / 2 - 1; l >= 0; l--) {
      final int r = array.length - 1 - l;
      final int t = array[r];
      array[r] = array[l];
      array[l] = t;
    }
  }

  /**
   * Reverse the order of elements in place.
   * 
   * @param array
   *          the array to reverse
   */
  public static void reverse(final double[] array) {
    for (int l = array.length / 2 - 1; l >= 0; l--) {
      final int r = array.length - 1 - l;
      final double t = array[r];
      array[r] = array[l];
      array[l] = t;
    }
  }

  /**
   * Sorts the primitive list.
   * 
   * @param list
   *          the list
   */
  public static void sort(final PrimitiveList.OfInt list) {
    Arrays.sort(list.array(), 0, list.size());
  }

  /**
   * Returns an integer array with the indices where the value changes. Like uniq (the linux tool)
   * this requires sorted input to return only the distinct groups.
   * 
   * @param array
   *          the array
   * @return integer array of indices where the value changes (including array.length)
   */
  public static int[] group(final double[] array) {
    final PrimitiveList.OfInt group = Mappings.newIntList();
    if (array.length > 0) {
      group.addInt(0);
    }
    for (int i = 1; i < array.length; i++) {
      if (Math.abs(array[i - 1] - array[i]) >= EPS) {
        group.addInt(i);
      }
    }
    group.addInt(array.length);
    return group.array();
  }

  /**
   * Returns an integer array with the indices where the value changes. Like uniq (the linux tool)
   * this requires sorted input to return only the distinct groups.
   * 
   * @param array
   *          the array
   * @return integer array of indices where the value changes (including array.length)
   */
  public static int[] group(final int[] array) {
    final PrimitiveList.OfInt group = Mappings.newIntList();
    if (array.length > 0) {
      group.addInt(0);
    }
    for (int i = 1; i < array.length; i++) {
      if (array[i - 1] != array[i]) {
        group.addInt(i);
      }
    }
    group.addInt(array.length);
    return group.array();
  }

}
