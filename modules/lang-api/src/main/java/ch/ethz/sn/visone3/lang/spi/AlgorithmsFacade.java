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
package ch.ethz.sn.visone3.lang.spi;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.PrimitiveCollections;

/**
 * Provides implementations of several common algorithms. The methods should
 * follow the contract of the corresponding methods in
 * {@link PrimitiveCollections}.
 *
 */
public interface AlgorithmsFacade {

  /**
   * Index returning counting sort. Searches the universe size, runs on the
   * complete array.
   * 
   * @param array integer array to sort.
   * @return integer array storing the sort permutation.
   *
   * @see #countingSort(int[], int, int, int, int) for the full description.
   */
  int[] countingSort(final int[] array);

  /**
   * Index returning counting sort..
   *
   * @param array        integer array.
   * @param valueOffset  integer added to each value in {@code array}.
   * @param universeSize should be larger than
   *                     {@code Arrays.stream(array).max() + valueOffset}.
   * @param begin        start index (inclusive).
   * @param end          end index (exclusive).
   * @return integer array storing the sort permutation.
   */
  int[] countingSort(final int[] array, final int valueOffset, final int universeSize, final int begin, final int end);

  /**
   * Index returning counting sort.
   *
   * @param array       integer array.
   * @param valueOffset integer added to each value in {@code array}.
   * @param start       output array where start[i] will contain the index of the
   *                    first element with value {@code i - valueOffset} after
   *                    this method returns. The array should have size larger
   *                    than {@code Arrays.stream(array).max() + valueOffset} and
   *                    be filled with zeros.
   * @param begin       start index (inclusive).
   * @param end         end index (exclusive).
   * @return integer array storing the sort permutation.
   */
  int[] countingSort(final int[] array, final int valueOffset, final int[] start, final int begin, final int end);

  /**
   * Index returning counting sort. Searches the universe size, runs on the
   * complete mapping.
   *
   * @param list integer mapping to sort.
   * @return integer array storing the sort permutation.
   *
   * @see #countingSort(ch.ethz.sn.visone3.lang.ConstMapping.OfInt, int, int, int,
   *      int) for the full description.
   */
  Mapping.OfInt countingSort(ConstMapping.OfInt list);

  /**
   * Index returning counting sort.
   *
   * @param list         integer mapping.
   * @param valueOffset  integer added to each value in {@code list}.
   * @param universeSize should be larger than
   *                     {@code list.intStream().max() + valueOffset}.
   * @param begin        start index (inclusive).
   * @param end          end index (exclusive).
   * @return integer mapping storing the sort permutation.
   */
  Mapping.OfInt countingSort(ConstMapping.OfInt list, final int valueOffset, final int universeSize, final int begin,
      final int end);

  /**
   * Index returning counting sort.
   *
   * @param list        integer mapping.
   * @param valueOffset integer added to each value in {@code list}.
   * @param start       output mapping where start[i] will contain the index of
   *                    the first element with value {@code i - valueOffset} after
   *                    this method returns. The mapping should have size larger
   *                    than {@code list.intStream().max() + valueOffset} and be
   *                    filled with zeros.
   * @param begin       start index (inclusive).
   * @param end         end index (exclusive).
   * @return integer mapping storing the sort permutation.
   */
  Mapping.OfInt countingSort(ConstMapping.OfInt list, final int valueOffset, final Mapping.OfInt start, final int begin,
      final int end);

  /**
   * Applies the outer mapping to the indices in the inner mapping, or
   * {@code missingValue} if a provided index is negative.
   *
   * @param outer        the outer mapping.
   * @param inner        the inner mapping with indices.
   * @param missingValue the missing value to use for negative indices.
   * @return New mapping where {@code i}-th element is
   *         {@code outer.get(inner.getInt(i))} if {@code inner.getInt(i)} is
   *         non-negative, else {@code missingValue}.
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfDouble,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt, double)
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfLong,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt, long)
   * @see #compose(ConstMapping, ch.ethz.sn.visone3.lang.ConstMapping.OfInt,
   *      Object)
   */
  Mapping.OfInt compose(ConstMapping.OfInt outer, ConstMapping.OfInt inner, int missingValue);

  /**
   * Applies the outer mapping to the indices in the inner mapping, or
   * {@code missingValue} if a provided index is negative.
   *
   * @param outer        the outer mapping.
   * @param inner        the inner mapping with indices.
   * @param missingValue the missing value to use for negative indices.
   * @return New mapping where {@code i}-th element is
   *         {@code outer.get(inner.getInt(i))} if {@code inner.getInt(i)} is
   *         non-negative, else {@code missingValue}.
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfDouble,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt, double)
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfInt,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt, int)
   * @see #compose(ConstMapping, ch.ethz.sn.visone3.lang.ConstMapping.OfInt,
   *      Object)
   */
  Mapping.OfLong compose(ConstMapping.OfLong outer, ConstMapping.OfInt inner, long missingValue);

  /**
   * Applies the outer mapping to the indices in the inner mapping, or
   * {@code missingValue} if a provided index is negative.
   *
   * @param outer        the outer mapping.
   * @param inner        the inner mapping with indices.
   * @param missingValue the missing value to use for negative indices.
   * @return New mapping where {@code i}-th element is
   *         {@code outer.get(inner.getInt(i))} if {@code inner.getInt(i)} is
   *         non-negative, else {@code missingValue}.
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfInt,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt, int)
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfLong,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt, long)
   * @see #compose(ConstMapping, ch.ethz.sn.visone3.lang.ConstMapping.OfInt,
   *      Object)
   */
  Mapping.OfDouble compose(ConstMapping.OfDouble outer, ConstMapping.OfInt inner, double missingValue);

  /**
   * Applies the outer mapping to the indices in the inner mapping, or
   * {@code missingValue} if a provided index is negative.
   *
   * @param outer        the outer mapping.
   * @param inner        the inner mapping with indices.
   * @param <T>          type of values in the outer mapping.
   * @param missingValue the missing value to use for negative indices.
   * @return New mapping where {@code i}-th element is
   *         {@code outer.get(inner.getInt(i))} if {@code inner.getInt(i)} is
   *         non-negative, else {@code missingValue}.
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfInt,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt, int)
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfDouble,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt, double)
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfLong,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt, long)
   */
  <T> Mapping<T> compose(ConstMapping<T> outer, ConstMapping.OfInt inner, T missingValue);

  /**
   * Applies the given outer array to the indices in the inner one, or
   * {@code missingValue} if a provided index is negative.
   * 
   * @param outer        the outer array.
   * @param inner        the inner array with indices.
   * @param missingValue the missing value to use for negative indices.
   *
   * @return New array where {@code i}-th element is {@code outer[inner[i]]} if
   *         {@code inner[i]} is non-negative, else {@code missingValue}.
   * @see #compose(double[], int[], double)
   * @see #compose(long[], int[], long)
   * @see #compose(Object[], int[], Object)
   */
  int[] compose(int[] outer, int[] inner, int missingValue);

  /**
   * Applies the given outer array to the indices in the inner one, or
   * {@code missingValue} if a provided index is negative.
   * 
   * @param outer        the outer array.
   * @param inner        the inner array with indices.
   * @param missingValue the missing value to use for negative indices.
   *
   * @return New array where {@code i}-th element is {@code outer[inner[i]]} if
   *         {@code inner[i]} is non-negative, else {@code missingValue}.
   * @see #compose(int[], int[], int)
   * @see #compose(double[], int[], double)
   * @see #compose(Object[], int[], Object)
   */
  long[] compose(long[] outer, int[] inner, long missingValue);

  /**
   * Applies the given outer array to the indices in the inner one, or
   * {@code missingValue} if a provided index is negative.
   * 
   * @param outer        the outer array.
   * @param inner        the inner array with indices.
   * @param missingValue the missing value to use for negative indices.
   *
   * @return New array where {@code i}-th element is {@code outer[inner[i]]} if
   *         {@code inner[i]} is non-negative, else {@code missingValue}.
   * @see #compose(int[], int[], int)
   * @see #compose(long[], int[], long)
   * @see #compose(Object[], int[], Object)
   */
  double[] compose(double[] outer, int[] inner, double missingValue);

  /**
   * Applies the given outer array to the indices in the inner one, or
   * {@code missingValue} if a provided index is negative.
   * 
   * @param outer        the outer array.
   * @param inner        the inner array with indices.
   * @param missingValue the missing value to use for negative indices.
   * @param <T>          type of values in the outer array.
   * @return New array where {@code i}-th element is {@code outer[inner[i]]} if
   *         {@code inner[i]} is non-negative, else {@code missingValue}.
   * @see #compose(int[], int[], int)
   * @see #compose(double[], int[], double)
   * @see #compose(long[], int[], long)
   */
  <T> T[] compose(T[] outer, int[] inner, T missingValue);

  /**
   * Applies the outer mapping to the indices in the inner mapping.
   *
   * @param outer the outer mapping.
   * @param inner the inner mapping.
   * @return New mapping where {@code i}-th element is
   *         {@code outer.get(inner.getInt(i))}
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfDouble,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt)
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfLong,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt)
   * @see #compose(ConstMapping, ch.ethz.sn.visone3.lang.ConstMapping.OfInt)
   */
  Mapping.OfInt compose(ConstMapping.OfInt outer, ConstMapping.OfInt inner);

  /**
   * Applies the outer mapping to the indices in the inner mapping.
   *
   * @param outer the outer mapping.
   * @param inner the inner mapping.
   * @return New mapping where {@code i}-th element is
   *         {@code outer.get(inner.getInt(i))}
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfInt,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt)
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfDouble,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt)
   * @see #compose(ConstMapping, ch.ethz.sn.visone3.lang.ConstMapping.OfInt)
   */
  Mapping.OfLong compose(ConstMapping.OfLong outer, ConstMapping.OfInt inner);

  /**
   * Applies the outer mapping to the indices in the inner mapping.
   *
   * @param outer the outer mapping.
   * @param inner the inner mapping.
   * @return New mapping where {@code i}-th element is
   *         {@code outer.get(inner.getInt(i))}
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfInt,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt)
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfLong,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt)
   * @see #compose(ConstMapping, ch.ethz.sn.visone3.lang.ConstMapping.OfInt)
   */
  Mapping.OfDouble compose(ConstMapping.OfDouble outer, ConstMapping.OfInt inner);

  /**
   * Applies the outer mapping to the indices in the inner mapping.
   *
   * @param outer the outer mapping.
   * @param inner the inner mapping.
   * @param <T>   type of values in the outer mapping.
   * @return New mapping where {@code i}-th element is
   *         {@code outer.get(inner.getInt(i))}
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfInt,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt)
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfLong,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt)
   * @see #compose(ch.ethz.sn.visone3.lang.ConstMapping.OfDouble,
   *      ch.ethz.sn.visone3.lang.ConstMapping.OfInt)
   */
  <T> Mapping<T> compose(ConstMapping<T> outer, ConstMapping.OfInt inner);

  /**
   * Applies the given outer array to the indices in the inner one.
   * 
   * @param outer the outer array.
   * @param inner the inner array with indices.
   *
   * @return New array where {@code i}-th element is {@code outer[inner[i]]}.
   * @see #compose(double[], int[])
   * @see #compose(long[], int[])
   * @see #compose(Object[], int[])
   */
  int[] compose(int[] outer, int[] inner);

  /**
   * Applies the given outer array to the indices in the inner one.
   * 
   * @param outer the outer array.
   * @param inner the inner array with indices.
   *
   * @return New array where {@code i}-th element is {@code outer[inner[i]]}.
   * @see #compose(int[], int[])
   * @see #compose(double[], int[])
   * @see #compose(Object[], int[])
   */
  long[] compose(long[] outer, int[] inner);

  /**
   * Applies the given outer array to the indices in the inner one.
   * 
   * @param outer the outer array.
   * @param inner the inner array with indices.
   *
   * @return New array where {@code i}-th element is {@code outer[inner[i]]}.
   * @see #compose(int[], int[])
   * @see #compose(long[], int[])
   * @see #compose(Object[], int[])
   */
  double[] compose(double[] outer, int[] inner);

  /**
   * Applies the given outer array to the indices in the inner one.
   * 
   * @param outer the outer array.
   * @param inner the inner array with indices.
   * @param <T>   type of values in the outer array.
   * 
   * @return New array where {@code i}-th element is {@code outer[inner[i]]}.
   * @see #compose(int[], int[])
   * @see #compose(double[], int[])
   * @see #compose(long[], int[])
   */
  <T> T[] compose(T[] outer, int[] inner);
}
