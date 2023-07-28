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

package ch.ethz.sn.visone3.algorithms.impl;

import ch.ethz.sn.visone3.algorithms.Stats;
import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveList;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.IntStream;

/**
 * Aggregation functions.
 */
public final class StatsImpl implements Stats {

  StatsImpl() {
  }

  @Override
  public int[] hist0(final ConstMapping.OfInt ints) {
    return hist0(() -> (PrimitiveIterator.OfInt) ints.iterator());
  }

  @Override
  public int[] hist0(final int[] ints) {
    return hist0(() -> Arrays.stream(ints).iterator());
  }

  @Override
  public int[] hist0(int[] ints, int bins) {
    return hist0(() -> Arrays.stream(ints).iterator(), bins);
  }

  @Override
  public int[] hist0(final Supplier<PrimitiveIterator.OfInt> ints) {
    final PrimitiveList.OfInt num = Mappings.newIntList();
    for (final PrimitiveIterator.OfInt it = ints.get(); it.hasNext();) {
      final int i = it.nextInt();
      if (i >= num.size()) {
        num.setSize(0, i + 1);
      }
      ++num.arrayQuick()[i];
    }
    return num.array();
  }

  @Override
  public int[] hist0(Supplier<PrimitiveIterator.OfInt> ints, int bins) {
    final PrimitiveList.OfInt num = Mappings.newIntList(0, bins);
    for (final PrimitiveIterator.OfInt it = ints.get(); it.hasNext();) {
      ++num.arrayQuick()[it.nextInt()];
    }
    return num.array();
  }

  @Override
  public Stats.Hist<Integer> hist(final int[] ints) {
    final Range<Integer> range = minMax(ints);
    return hist(ints, range.min, range.max);
  }

  @Override
  public Stats.Hist<Integer> hist(final int[] ints, final int min, final int max) {
    final PrimitiveList.OfInt count = Mappings.newIntList(0, max - min + 1);
    for (int i : ints) {
      if (i < min) {
        i = min;
      }
      if (i > max) {
        i = max;
      }
      ++count.arrayQuick()[i - min];
    }
    return new Stats.Hist<>(IntStream.rangeClosed(min, max).boxed().toArray(Integer[]::new),
        count.array());
  }

  @Override
  public Stats.Hist<Double> hist(final ConstMapping.OfDouble array, final int bins) {
    // range
    final Range<Double> minMax = minMax(array);
    final double min = minMax.min;
    final double max = minMax.max;
    final double range = max - min;
    // histogram
    final PrimitiveList.OfInt n = Mappings.newIntList(0, bins);
    for (final double v : array) {
      final int i = v == max ? bins - 1 : (int) (bins * (v - min) / range);
      ++n.arrayQuick()[i];
    }
    final int[] count = n.array();
    final Double[] value = new Double[bins];
    final double binWidth = range / bins;
    for (int i = 0; i < bins; i++) {
      value[i] = min + (i + 0.5) * binWidth;
    }
    return new Hist<>(value, count);
  }

  /**
   * Categorical histogram.
   *
   * @param array
   *          input values.
   * @return Array with number of occurrences.
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T> Stats.Hist<T> hist(final ConstMapping<T> array) {
    final Map<T, Integer> counts = new HashMap<>();
    for (final T v : array) {
      counts.put(v, 1 + counts.getOrDefault(v, 0));
    }
    return new Hist<>(
        counts.keySet().toArray((T[]) Array.newInstance(array.getComponentType(), counts.size())),
        counts.values().stream().mapToInt(Integer::intValue).toArray());
  }

  @Override
  public <T> int argmax(final T[] array, final Comparator<T> comp) {
    int maxi = 0;
    T max = array[maxi];
    for (int i = 1; i < array.length; i++) {
      if (comp.compare(max, array[i]) < 0) {
        maxi = i;
        max = array[maxi];
      }
    }
    return maxi;
  }

  @Override
  public int argmax(final int[] array) {
    int maxi = 0;
    int max = array[maxi];
    for (int i = 1; i < array.length; i++) {
      if (max < array[i]) {
        maxi = i;
        max = array[maxi];
      }
    }
    return maxi;
  }

  @Override
  public <T> int index(final T[] array, final T element) {
    for (int i = 1; i < array.length; i++) {
      if (Objects.equals(element, array[i])) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public <T> Stats.Range<T> minMax(final Supplier<? extends Iterator<T>> values,
      final Comparator<T> comp) {
    final Iterator<T> itr = values.get();
    if (!itr.hasNext()) {
      return new Range<>(null, null, comp);
    }
    T min = itr.next();
    T max = min;
    while (itr.hasNext()) {
      final T i = itr.next();
      if (comp.compare(i, min) < 0) {
        min = i;
      }
      if (comp.compare(i, max) > 0) {
        max = i;
      }
    }
    return new Range<>(min, max, comp);
  }

  @Override
  public Range<Integer> minMax(final int[] values) {
    Integer min = values[0];
    Integer max = values[0];
    for (final int i : values) {
      if (i < min) {
        min = i;
      }
      if (i > max) {
        max = i;
      }
    }
    return new Range<>(min, max, Integer::compareTo);
  }

  @Override
  public Range<Double> minMax(final ConstMapping.OfDouble array) {
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < array.size(); i++) {
      final double v = array.getDouble(i);
      if (v < min) {
        min = v;
      }
      if (max < v) {
        max = v;
      }
    }
    return new Range<>(min, max, Double::compareTo);
  }

  private static class HashHist<T> {
    Map<T, Integer> hist;

    public HashHist() {
      hist = new HashMap<>();
    }

    public void accept(final T value) {
      final int c = hist.getOrDefault(value, 0);
      hist.put(value, c + 1);
    }

    public HashHist<T> combine(final HashHist<T> other) {
      for (final Map.Entry<T, Integer> e : other.hist.entrySet()) {
        final int c = hist.getOrDefault(e.getKey(), 0);
        hist.put(e.getKey(), c + e.getValue());
      }
      return this;
    }

    public Hist<T> toHist() {
      @SuppressWarnings("unchecked")
      T[] values = (T[]) hist.keySet().toArray();
      int[] count = hist.values().stream().mapToInt(Integer::intValue).toArray();
      return new Hist<>(values, count);
    }
  }

  @Override
  public <T> Collector<T, HashHist<T>, Hist<T>> histcollector() {
    return Collector.of(HashHist::new, HashHist::accept, HashHist::combine, HashHist::toHist);
  }
}
