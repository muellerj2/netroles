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
package ch.ethz.sn.visone3.lang.impl.algorithms;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class CountingSort {
  private static final Logger LOG = LoggerFactory.getLogger(CountingSort.class);

  private CountingSort() {
  }

  /**
   * Index returning counting sort. Searches the universe size, runs on the complete array.
   *
   * @see #countingSort(int[], int, int, int, int) for the full description.
   */
  public static int[] countingSort(final int[] array) {
    // find min/max
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    for (final int i : array) {
      if (i < min) {
        min = i;
      }
      if (max < i) {
        max = i;
      }
    }
    if ((max - min) > array.length) {
      LOG.warn("size < universeSize");
    }
    // non-negatives
    return countingSort(array, -min, (max - min) + 1, 0, array.length);
  }

  /**
   * Index returning counting sort. To get the sorted sequence, permute the sequence using the
   * returned index array.
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
    return countingSort(array, valueOffset, new int[universeSize], begin, end);
  }

  /**
   * Index returning counting sort. To get the sorted sequence, permute the sequence using the
   * returned index array.
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
    // count
    for (int i = begin; i < end; i++) {
      ++start[array[i] + valueOffset];
    }
    // accumulate
    for (int i = 1; i < start.length; i++) {
      start[i] += start[i - 1];
    }
    // write indices
    final int[] pi = new int[end - begin];
    for (int j = end - 1; j >= begin; j--) {
      final int v = array[j] + valueOffset;
      final int pos = --start[v];
      pi[pos] = j - begin;
    }
    return pi;
  }

  /**
   * Index returning counting sort. Searches the universe size, runs on the complete mapping.
   *
   * @see #countingSort(ch.ethz.sn.visone3.lang.ConstMapping.OfInt, int, int, int, int) for the
   *      full description.
   */
  public static Mapping.OfInt countingSort(final ConstMapping.OfInt list) {
    // find min/max
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    for (final int i : list) {
      if (i < min) {
        min = i;
      }
      if (max < i) {
        max = i;
      }
    }
    if ((max - min) > list.size()) {
      LOG.warn("size < universeSize");
    }
    // non-negatives
    return countingSort(list, -min, (max - min) + 1, 0, list.size());
  }

  /**
   * Index returning counting sort. To get the sorted sequence, permute the sequence using the
   * returned index mapping.
   *
   * @param list
   *          integer mapping.
   * @param valueOffset
   *          integer added to each value in {@code array}.
   * @param universeSize
   *          should be larger than {@code Arrays.stream(array).max() + valueOffset}.
   * @param begin
   *          start index (inclusive).
   * @param end
   *          end index (exclusive).
   * @return integer mapping storing the sort permutation.
   */
  public static Mapping.OfInt countingSort(final ConstMapping.OfInt list, final int valueOffset,
      final int universeSize, final int begin, final int end) {
    int[] counts = new int[universeSize];
    // count
    for (int i = begin; i < end; i++) {
      int v = list.getInt(i) + valueOffset;
      ++counts[v];
    }
    // accumulate
    int prev = counts[0];
    for (int i = 1; i < universeSize; i++) {
      prev = counts[i] += prev;
    }
    // write indices
    final int[] pi = new int[end - begin];
    for (int j = end - 1; j >= begin; j--) {
      final int v = list.getInt(j) + valueOffset;
      final int pos = --counts[v];
      pi[pos] = j - begin;
    }
    return Mappings.wrapModifiableInt(pi);
  }

  /**
   * Index returning counting sort. To get the sorted sequence, permute the sequence using the
   * returned index array.
   *
   * @param list
   *          integer mapping.
   * @param valueOffset
   *          integer added to each value in {@code array}.
   * @param start
   *          output mapping where start[i] will contain the index of the first element with value
   *          {@code i - valueOffset} after this method returns. The mapping should have size larger
   *          than {@code Arrays.stream(array).max() + valueOffset} and be filled with zeros.
   * @param begin
   *          start index (inclusive).
   * @param end
   *          end index (exclusive).
   * @return integer mapping storing the sort permutation.
   */
  public static Mapping.OfInt countingSort(ConstMapping.OfInt list, final int valueOffset,
      final Mapping.OfInt start, final int begin, final int end) {
    // count
    for (int i = begin; i < end; i++) {
      int v = list.getInt(i) + valueOffset;
      start.setInt(v, start.getInt(v) + 1);
    }
    // accumulate
    int prev = start.getInt(0);
    int universeSize = start.size();
    for (int i = 1; i < universeSize; i++) {
      int val = start.getInt(i) + prev;
      start.setInt(i, val);
      prev = val;
    }
    // write indices
    final int[] pi = new int[end - begin];
    for (int j = end - 1; j >= begin; j--) {
      final int v = list.getInt(j) + valueOffset;
      final int pos = start.getInt(v) - 1;
      start.setInt(v, pos);
      pi[pos] = j - begin;
    }
    return Mappings.wrapModifiableInt(pi);
  }

}
