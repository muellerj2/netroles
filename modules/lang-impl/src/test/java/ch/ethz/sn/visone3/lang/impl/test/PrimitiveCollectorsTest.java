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

package ch.ethz.sn.visone3.lang.impl.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.ethz.sn.visone3.lang.PrimitiveCollectors;
import ch.ethz.sn.visone3.lang.PrimitiveList;

import org.junit.jupiter.api.Test;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

class PrimitiveCollectorsTest {

  @Test
  void testToListCollectors() {
    assertArrayEquals(new int[] { 5, 32, 4, 1, 2, 5, 3, 3 }, PrimitiveCollectors
        .collect(IntStream.of(5, 32, 4, 1, 2, 5, 3, 3), PrimitiveCollectors.toIntList()).array());
    assertEquals(PrimitiveList.OfInt.class, PrimitiveCollectors.toIntList().resultType());
    assertArrayEquals(new long[] { 5, 32, 4, 1, 2, 5, 3, 3 }, PrimitiveCollectors
        .collect(LongStream.of(5L, 32L, 4L, 1L, 2L, 5L, 3L, 3L), PrimitiveCollectors.toLongList())
        .array());
    assertEquals(PrimitiveList.OfLong.class, PrimitiveCollectors.toLongList().resultType());
    assertArrayEquals(new double[] { 5., 32., 4., 1., 2., 5., 3., 3. },
        PrimitiveCollectors
            .collect(DoubleStream.of(5, 32, 4, 1, 2, 5, 3, 3), PrimitiveCollectors.toDoubleList())
            .array());
    assertEquals(PrimitiveList.OfDouble.class, PrimitiveCollectors.toDoubleList().resultType());
    assertArrayEquals(new String[] { "quack", "bark", "moo", "bark", "woof" },
        PrimitiveCollectors.collect(Stream.of("quack", "bark", "moo", "bark", "woof"),
            PrimitiveCollectors.toList(String.class)).toArray(new String[0]));
    assertEquals(PrimitiveList.class, PrimitiveCollectors.toList(String.class).resultType());
    assertArrayEquals(new Integer[] { 5, 32, 4, 1, 2, 5, 3, 3 },
        PrimitiveCollectors.collect(IntStream.of(5, 32, 4, 1, 2, 5, 3, 3).boxed(),
            PrimitiveCollectors.toList(int.class)).toArray(new Integer[0]));
    assertEquals(PrimitiveList.OfInt.class, PrimitiveCollectors.toList(int.class).resultType());
    assertArrayEquals(new Long[] { 5L, 32L, 4L, 1L, 2L, 5L, 3L, 3L }, PrimitiveCollectors
        .collect(Stream.of(5L, 32L, 4L, 1L, 2L, 5L, 3L, 3L), PrimitiveCollectors.toList(long.class))
        .toArray(new Long[0]));
    assertEquals(PrimitiveList.OfLong.class, PrimitiveCollectors.toList(long.class).resultType());
    assertArrayEquals(new Double[] { 5., 32., 4., 1., 2., 5., 3., 3. },
        PrimitiveCollectors.collect(Stream.of(5., 32., 4., 1., 2., 5., 3., 3.),
            PrimitiveCollectors.toList(double.class)).toArray(new Double[0]));
    assertEquals(PrimitiveList.OfDouble.class,
        PrimitiveCollectors.toList(double.class).resultType());
    assertThrows(UnsupportedOperationException.class,
        () -> PrimitiveCollectors.toList(short.class));
    assertArrayEquals(new String[] { "quack", "bark", "moo", "bark", "woof" },
        PrimitiveCollectors.collect(Stream.of("quack", "bark", "moo", "bark", "woof"),
            PrimitiveCollectors.toListAutoboxing(String.class)).toArray(new String[0]));
    assertEquals(PrimitiveList.class,
        PrimitiveCollectors.toListAutoboxing(String.class).resultType());
    assertArrayEquals(new Integer[] { 5, 32, 4, 1, 2, 5, 3, 3 },
        PrimitiveCollectors.collect(IntStream.of(5, 32, 4, 1, 2, 5, 3, 3).boxed(),
            PrimitiveCollectors.toList(int.class)).toArray(new Integer[0]));
    assertEquals(PrimitiveList.OfInt.class,
        PrimitiveCollectors.toListAutoboxing(int.class).resultType());
    assertArrayEquals(new Long[] { 5L, 32L, 4L, 1L, 2L, 5L, 3L, 3L },
        PrimitiveCollectors.collect(Stream.of(5L, 32L, 4L, 1L, 2L, 5L, 3L, 3L),
            PrimitiveCollectors.toListAutoboxing(long.class)).toArray(new Long[0]));
    assertEquals(PrimitiveList.OfLong.class,
        PrimitiveCollectors.toListAutoboxing(long.class).resultType());
    assertArrayEquals(new Double[] { 5., 32., 4., 1., 2., 5., 3., 3. },
        PrimitiveCollectors.collect(Stream.of(5., 32., 4., 1., 2., 5., 3., 3.),
            PrimitiveCollectors.toListAutoboxing(double.class)).toArray(new Double[0]));
    assertEquals(PrimitiveList.OfDouble.class,
        PrimitiveCollectors.toListAutoboxing(double.class).resultType());
    assertArrayEquals(new Short[] { 5, 32, 4, 1, 2, 5, 3, 3 },
        PrimitiveCollectors
            .collect(Stream.of((short) 5, (short) 32, (short) 4, (short) 1, (short) 2, (short) 5,
                (short) 3, (short) 3), PrimitiveCollectors.toListAutoboxing(short.class))
            .toArray(new Short[0]));
    assertEquals(PrimitiveList.class,
        PrimitiveCollectors.toListAutoboxing(short.class).resultType());
  }

}
