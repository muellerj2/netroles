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
package ch.ethz.sn.visone3.lang.impl.algorithms;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.spi.AlgorithmsFacade;

public final class AlgorithmsFacadeImpl implements AlgorithmsFacade {

  @Override
  public int[] countingSort(int[] array) {
    return CountingSort.countingSort(array);
  }

  @Override
  public int[] countingSort(int[] array, int valueOffset, int universeSize, int begin, int end) {
    return CountingSort.countingSort(array, valueOffset, universeSize, begin, end);
  }

  @Override
  public int[] countingSort(int[] array, int valueOffset, int[] start, int begin, int end) {
    return CountingSort.countingSort(array, valueOffset, start, begin, end);
  }

  @Override
  public Mapping.OfInt countingSort(ConstMapping.OfInt list) {
    return CountingSort.countingSort(list);
  }

  @Override
  public Mapping.OfInt countingSort(ConstMapping.OfInt list, int valueOffset, int universeSize,
      int begin, int end) {
    return CountingSort.countingSort(list, valueOffset, universeSize, begin, end);
  }

  @Override
  public Mapping.OfInt countingSort(ConstMapping.OfInt list, int valueOffset, Mapping.OfInt start,
      int begin, int end) {
    return CountingSort.countingSort(list, valueOffset, start, begin, end);
  }

  @Override
  public Mapping.OfInt compose(ConstMapping.OfInt outer, ConstMapping.OfInt inner,
      int missingValue) {
    return PrimitiveComposition.compose(outer, inner, missingValue);
  }

  @Override
  public Mapping.OfLong compose(ConstMapping.OfLong outer, ConstMapping.OfInt inner,
      long missingValue) {
    return PrimitiveComposition.compose(outer, inner, missingValue);
  }

  @Override
  public Mapping.OfDouble compose(ConstMapping.OfDouble outer, ConstMapping.OfInt inner,
      double missingValue) {
    return PrimitiveComposition.compose(outer, inner, missingValue);
  }

  @Override
  public <T> Mapping<T> compose(ConstMapping<T> outer, ConstMapping.OfInt inner, T missingValue) {
    return PrimitiveComposition.compose(outer, inner, missingValue);
  }

  @Override
  public int[] compose(int[] outer, int[] inner, int missingValue) {
    return PrimitiveComposition.compose(outer, inner, missingValue);
  }

  @Override
  public long[] compose(long[] outer, int[] inner, long missingValue) {
    return PrimitiveComposition.compose(outer, inner, missingValue);
  }

  @Override
  public double[] compose(double[] outer, int[] inner, double missingValue) {
    return PrimitiveComposition.compose(outer, inner, missingValue);
  }

  @Override
  public <T> T[] compose(T[] outer, int[] inner, T missingValue) {
    return PrimitiveComposition.compose(outer, inner, missingValue);
  }

  @Override
  public Mapping.OfInt compose(ConstMapping.OfInt outer, ConstMapping.OfInt inner) {
    return PrimitiveComposition.compose(outer, inner);
  }

  @Override
  public Mapping.OfLong compose(ConstMapping.OfLong outer, ConstMapping.OfInt inner) {
    return PrimitiveComposition.compose(outer, inner);
  }

  @Override
  public Mapping.OfDouble compose(ConstMapping.OfDouble outer, ConstMapping.OfInt inner) {
    return PrimitiveComposition.compose(outer, inner);
  }

  @Override
  public <T> Mapping<T> compose(ConstMapping<T> outer, ConstMapping.OfInt inner) {
    return PrimitiveComposition.compose(outer, inner);
  }

  @Override
  public int[] compose(int[] outer, int[] inner) {
    return PrimitiveComposition.compose(outer, inner);
  }

  @Override
  public long[] compose(long[] outer, int[] inner) {
    return PrimitiveComposition.compose(outer, inner);
  }

  @Override
  public double[] compose(double[] outer, int[] inner) {
    return PrimitiveComposition.compose(outer, inner);
  }

  @Override
  public <T> T[] compose(T[] outer, int[] inner) {
    return PrimitiveComposition.compose(outer, inner);
  }

}
