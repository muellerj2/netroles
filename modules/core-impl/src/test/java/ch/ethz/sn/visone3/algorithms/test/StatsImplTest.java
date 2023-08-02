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

package ch.ethz.sn.visone3.algorithms.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.ethz.sn.visone3.algorithms.AlgoProvider;
import ch.ethz.sn.visone3.algorithms.Stats;
import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveList;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.IntStream;

public class StatsImplTest {
  private static final Stats STATS = AlgoProvider.getInstance().stats();

  private static <T, A, R> R myCollect(List<T> list, Collector<T, A, R> collector) {
    A temp1 = collector.supplier().get();
    A temp2 = collector.supplier().get();
    int size = list.size();
    for (int i = 0; i < size; ++i) {
      collector.accumulator().accept(i % 2 == 0 ? temp1 : temp2, list.get(i));
    }
    return collector.finisher().apply(collector.combiner().apply(temp1, temp2));
  }
  @Test
  public void testHistCategorical() {
    final PrimitiveList<String> strings = Mappings.newList(String.class);
    strings.add("a");
    strings.add("b");
    strings.add("a");
    strings.add("a");
    strings.add("c");
    final Stats.Hist<String> hist = STATS.hist(strings);
    assertEquals("{N=3[a=3,b=1,c=1]}", hist.toString());
    assertEquals(0, hist.getMaxCountBin());
    assertEquals("a", hist.getMaxCountValue());
    assertEquals(1, hist.getCount(2));
    assertEquals("b", hist.getValue(1));

    final Stats.Hist<String> expectedHist = new Stats.Hist<>(new String[] { "a", "b", "c" },
        new int[] { 3, 1, 1 });
    final Stats.Hist<String> unexpectedHist = new Stats.Hist<>(new String[] { "d", "b", "c" },
        new int[] { 3, 1, 1 });
    final Stats.Hist<String> unexpectedHist2 = new Stats.Hist<>(new String[] { "a", "b", "c" },
        new int[] { 2, 1, 1 });
    assertEquals(expectedHist, hist);
    assertEquals(expectedHist.hashCode(), hist.hashCode());
    assertNotEquals(unexpectedHist, hist);
    assertNotEquals(unexpectedHist2, hist);

    assertEquals(expectedHist, strings.stream().collect(STATS.histcollector()));
    assertEquals(expectedHist, myCollect(strings, STATS.histcollector()));

    final Stats.Hist<String> nullHist = new Stats.Hist<>(new String[0], new int[0]);
    final Stats.Hist<String> nullHist2 = new Stats.Hist<>(new String[0], new int[0]);
    assertNotEquals(nullHist, hist);
    assertNotEquals(hist, nullHist);
    assertEquals(nullHist, nullHist2);
    assertEquals("{N=0[]}", nullHist.toString());
    assertFalse(nullHist.equals(new Object()));
    assertThrows(IllegalArgumentException.class,
        () -> new Stats.Hist<>(new String[] { "a" }, new int[0]));
  }

  @Test
  public void testHistInt() {
    int[] ints = { 5, 3, 3, 3, 3, 4, 1, 5, 5, 6, 6 };
    Stats.Hist<Integer> hist = STATS.hist(ints);
    assertEquals("{N=6[1=1,2=0,3=4,4=1,5=3,6=2]}", hist.toString());
    assertEquals("{N=9[0=0,1=1,2=0,3=4,4=1,5=3,6=2,7=0,8=0]}", STATS.hist(ints, 0, 8).toString());
    assertEquals("{N=4[2=1,3=4,4=1,5=5]}", STATS.hist(ints, 2, 5).toString());
  }

  @Test
  public void testHistDouble() {
    double[] doubles = { 5.2, 3.2, 3.3, 3.2, 3.2, 4.4, 1.25, 5.6, 6.25, 4.3, 6, 6 };
    Stats.Hist<Double> hist = STATS.hist(Mappings.newDoubleListFrom(doubles), 5);
    assertEquals("{N=5[1.75=1,2.75=3,3.75=1,4.75=3,5.75=4]}", hist.toString());
  }

  @Test
  public void testMinMax() {
    int[] values = { 3, 2, 6, 4, 3, 5, 1, -1, 5, 8, 4, 3 };
    Stats.Range<Integer> range = STATS.minMax(values);
    assertEquals(-1, range.min.intValue());
    assertEquals(8, range.max.intValue());

    ConstMapping.OfDouble doubleList = Mappings.newDoubleListFrom(3.2, 2.5, 6.3, 4.1, 3.6, 5.2, 1.,
        -1.5, 5.8, 8.25, 4.3, 3.2);
    Stats.Range<Double> doubleRange = STATS.minMax(doubleList);
    assertEquals(Double.valueOf(-1.5), doubleRange.min);
    assertEquals(Double.valueOf(8.25), doubleRange.max);

    Stats.Range<Byte> byteRange = STATS.minMax(
        () -> IntStream.of(values).mapToObj(x -> (byte) x).iterator(), Comparator.reverseOrder());
    assertEquals(Byte.valueOf((byte) 8), byteRange.min);
    assertEquals(Byte.valueOf((byte) -1), byteRange.max);

    Stats.Range<Integer> nullObjRange = STATS.minMax(
        () -> IntStream.of().mapToObj(Integer::valueOf).iterator(), Comparator.naturalOrder());
    assertNull(nullObjRange.min);
    assertNull(nullObjRange.max);

    assertEquals(new Stats.Range<>(-1, 8, Integer::compareTo), range);
    assertEquals(new Stats.Range<>(-1, 8, Integer::compareTo).hashCode(), range.hashCode());
    assertNotEquals(new Stats.Range<>(1, 8, Integer::compareTo), range);
    assertNotEquals(new Stats.Range<>(-1, 7, Integer::compareTo), range);
    assertNotEquals(nullObjRange, range);
    assertNotEquals(range, nullObjRange);
    assertFalse(range.equals(new Object()));
  }

  @Test
  public void testHist0() {
    int[] ints = {0, 3, 3, 3, 3, 4, 5, 5, 5, 6, 6};
    int[] bins = STATS.hist0(ints);
    int[] correct_bins = {1, 0, 0, 4, 1, 3, 2};
    assertArrayEquals(correct_bins, bins);

    assertArrayEquals(correct_bins, STATS.hist0(Mappings.wrapUnmodifiableInt(ints)));
  }


  @Test
  public void testHist0Unordered() {
    int[] ints = {3, 3, 3, 3, 4, 5, 5, 5, 6, 6, 0};
    int[] bins = STATS.hist0(ints);
    int[] correct_bins = {1, 0, 0, 4, 1, 3, 2};
    assertArrayEquals(correct_bins, bins);

    assertArrayEquals(correct_bins, STATS.hist0(Mappings.wrapUnmodifiableInt(ints)));
  }

  @Test
  public void testHist0Bins() {
    int[] ints = { 3, 3, 3, 2, 4, 5, 5, 5, 6, 6, 0 };
    int[] bins = STATS.hist0(ints, 7);
    int[] correct_bins = { 1, 0, 1, 3, 1, 3, 2 };
    assertArrayEquals(correct_bins, bins);
  }

  @Test
  public void testArgmax() {
    assertEquals(3, STATS.argmax(new int[] { 2, 5, -1, 8, 3, 4, -1, 7, 8, 2 }));
    assertEquals(2,
        STATS.argmax(new Integer[] { 2, 5, -1, 8, 3, 4, -1, 7, 8, 2 }, Comparator.reverseOrder()));
  }

  @Test
  public void testIndex() {
    assertEquals(5,
        STATS.index(new String[] { "a", "c", "b", "p", null, "d", "e", "p", "d", "a" }, "d"));
    assertEquals(-1,
        STATS.index(new String[] { "a", "c", "b", "p", null, "d", "e", "p", "d", "a" }, "x"));
    assertEquals(4,
        STATS.index(new String[] { "a", "c", "b", "p", null, "d", "e", "p", "d", "a" }, null));
  }
}
