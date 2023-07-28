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
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveList;

import java.lang.reflect.Array;

final class PrimitiveComposition {

  private PrimitiveComposition() {
  }

  public static Mapping.OfInt compose(ConstMapping.OfInt outer, ConstMapping.OfInt inner,
      int missingValue) {
    final int size = inner.size();
    final int[] result = new int[size];
    for (int i = 0; i < size; i++) {
      int pos = inner.getInt(i);
      result[i] = pos >= 0 ? outer.getInt(pos) : missingValue;
    }
    return Mappings.wrapModifiableInt(result);
  }

  public static Mapping.OfLong compose(ConstMapping.OfLong outer, ConstMapping.OfInt inner,
      long missingValue) {
    final int size = inner.size();
    final long[] result = new long[size];
    for (int i = 0; i < size; i++) {
      int pos = inner.getInt(i);
      result[i] = pos >= 0 ? outer.getLong(pos) : missingValue;
    }
    return Mappings.newLongList(result);
  }

  public static Mapping.OfDouble compose(ConstMapping.OfDouble outer, ConstMapping.OfInt inner,
      double missingValue) {
    final int size = inner.size();
    final double[] result = new double[size];
    for (int i = 0; i < size; i++) {
      int pos = inner.getInt(i);
      result[i] = pos >= 0 ? outer.getDouble(pos) : missingValue;
    }
    return Mappings.newDoubleList(result);
  }

  public static <T> Mapping<T> compose(ConstMapping<T> outer, ConstMapping.OfInt inner,
      T missingValue) {
    final int size = inner.size();
    final PrimitiveList<T> result = Mappings.newList(outer.getComponentType(), size);
    for (int i = 0; i < size; i++) {
      int pos = inner.getInt(i);
      result.add(pos >= 0 ? outer.get(pos) : missingValue);
    }
    return result;
  }

  public static int[] compose(int[] outer, int[] inner, int missingValue) {
    final int size = inner.length;
    final int[] result = new int[size];
    for (int i = 0; i < size; i++) {
      int pos = inner[i];
      result[i] = pos >= 0 ? outer[pos] : missingValue;
    }
    return result;
  }

  public static long[] compose(long[] outer, int[] inner, long missingValue) {
    final int size = inner.length;
    final long[] result = new long[size];
    for (int i = 0; i < size; i++) {
      int pos = inner[i];
      result[i] = pos >= 0 ? outer[pos] : missingValue;
    }
    return result;
  }

  public static double[] compose(double[] outer, int[] inner, double missingValue) {
    final int size = inner.length;
    final double[] result = new double[size];
    for (int i = 0; i < size; i++) {
      int pos = inner[i];
      result[i] = pos >= 0 ? outer[pos] : missingValue;
    }
    return result;
  }

  public static <T> T[] compose(T[] outer, int[] inner, T missingValue) {
    final int size = inner.length;
    @SuppressWarnings("unchecked")
    final T[] result = (T[]) Array.newInstance(outer.getClass().getComponentType(), size);
    for (int i = 0; i < size; i++) {
      int pos = inner[i];
      result[i] = pos >= 0 ? outer[pos] : missingValue;
    }
    return result;
  }

  public static Mapping.OfInt compose(ConstMapping.OfInt outer, ConstMapping.OfInt inner) {
    final int size = inner.size();
    final int[] result = new int[size];
    for (int i = 0; i < size; i++) {
      result[i] = outer.getInt(inner.getInt(i));
    }
    return Mappings.wrapModifiableInt(result);
  }

  public static Mapping.OfLong compose(ConstMapping.OfLong outer, ConstMapping.OfInt inner) {
    final int size = inner.size();
    final long[] result = new long[size];
    for (int i = 0; i < size; i++) {
      result[i] = outer.getLong(inner.getInt(i));
    }
    return Mappings.newLongList(result);
  }

  public static Mapping.OfDouble compose(ConstMapping.OfDouble outer, ConstMapping.OfInt inner) {
    final int size = inner.size();
    final double[] result = new double[size];
    for (int i = 0; i < size; i++) {
      result[i] = outer.getDouble(inner.getInt(i));
    }
    return Mappings.newDoubleList(result);
  }

  @SuppressWarnings("unchecked")
  public static <T> Mapping<T> compose(ConstMapping<T> outer, ConstMapping.OfInt inner) {
    if (outer instanceof ConstMapping.OfInt) {
      return (Mapping<T>) compose((ConstMapping.OfInt) outer, inner);
    } else if (outer instanceof ConstMapping.OfLong) {
      return (Mapping<T>) compose((ConstMapping.OfLong) outer, inner);
    } else if (outer instanceof ConstMapping.OfDouble) {
      return (Mapping<T>) compose((ConstMapping.OfDouble) outer, inner);
    }
    final int size = inner.size();
    final PrimitiveList<T> result = Mappings.newList(outer.getComponentType(), size);
    for (int i = 0; i < size; i++) {
      result.add(outer.get(inner.getInt(i)));
    }
    return result;
  }

  public static int[] compose(int[] outer, int[] inner) {
    final int size = inner.length;
    final int[] result = new int[size];
    for (int i = 0; i < size; i++) {
      result[i] = outer[inner[i]];
    }
    return result;
  }

  public static long[] compose(long[] outer, int[] inner) {
    final int size = inner.length;
    final long[] result = new long[size];
    for (int i = 0; i < size; i++) {
      result[i] = outer[inner[i]];
    }
    return result;
  }

  public static double[] compose(double[] outer, int[] inner) {
    final int size = inner.length;
    final double[] result = new double[size];
    for (int i = 0; i < size; i++) {
      result[i] = outer[inner[i]];
    }
    return result;
  }

  public static <T> T[] compose(T[] outer, int[] inner) {
    final int size = inner.length;
    @SuppressWarnings("unchecked")
    final T[] result = (T[]) Array.newInstance(outer.getClass().getComponentType(), size);
    for (int i = 0; i < size; i++) {
      result[i] = outer[inner[i]];
    }
    return result;
  }

}
