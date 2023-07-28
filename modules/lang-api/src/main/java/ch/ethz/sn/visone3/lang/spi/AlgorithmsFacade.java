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
package ch.ethz.sn.visone3.lang.spi;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.PrimitiveCollections;

/**
 * Provides implementations of several common algorithms. The methods should follow the contract of
 * the corresponding methods in {@link PrimitiveCollections}.
 *
 */
public interface AlgorithmsFacade {

  int[] countingSort(final int[] array);

  int[] countingSort(final int[] array, final int valueOffset, final int universeSize,
      final int begin, final int end);

  int[] countingSort(final int[] array, final int valueOffset, final int[] start, final int begin,
      final int end);

  Mapping.OfInt countingSort(ConstMapping.OfInt list);

  Mapping.OfInt countingSort(ConstMapping.OfInt list, final int valueOffset, final int universeSize,
      final int begin, final int end);

  Mapping.OfInt countingSort(ConstMapping.OfInt list, final int valueOffset,
      final Mapping.OfInt start, final int begin, final int end);

  Mapping.OfInt compose(ConstMapping.OfInt outer, ConstMapping.OfInt inner, int missingValue);

  Mapping.OfLong compose(ConstMapping.OfLong outer, ConstMapping.OfInt inner, long missingValue);

  Mapping.OfDouble compose(ConstMapping.OfDouble outer, ConstMapping.OfInt inner,
      double missingValue);

  <T> Mapping<T> compose(ConstMapping<T> outer, ConstMapping.OfInt inner, T missingValue);

  int[] compose(int[] outer, int[] inner, int missingValue);

  long[] compose(long[] outer, int[] inner, long missingValue);

  double[] compose(double[] outer, int[] inner, double missingValue);

  <T> T[] compose(T[] outer, int[] inner, T missingValue);

  Mapping.OfInt compose(ConstMapping.OfInt outer, ConstMapping.OfInt inner);

  Mapping.OfLong compose(ConstMapping.OfLong outer, ConstMapping.OfInt inner);

  Mapping.OfDouble compose(ConstMapping.OfDouble outer, ConstMapping.OfInt inner);

  <T> Mapping<T> compose(ConstMapping<T> outer, ConstMapping.OfInt inner);

  int[] compose(int[] outer, int[] inner);

  long[] compose(long[] outer, int[] inner);

  double[] compose(double[] outer, int[] inner);

  <T> T[] compose(T[] outer, int[] inner);
}
