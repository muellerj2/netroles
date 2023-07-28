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

import org.junit.jupiter.api.Test;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveCollections;
import ch.ethz.sn.visone3.lang.PrimitiveList;

class PrimitiveCollectionsTest {

  @Test
  void testCountingSort() {
    int[] array = { 1, 6, 3, 2, -1, 0, 2, 3, 2, 1, 5, 7, 3, 4, 2, 1, 5, 3, 9 };
    assertArrayEquals(
        new int[] { 4, 5, 0, 9, 15, 3, 6, 8, 14, 2, 7, 12, 17, 13, 10, 16, 1, 11, 18 },
        PrimitiveCollections.countingSort(array, 1, 11, 0, array.length));
    assertArrayEquals(new int[] { 2, 3, 7, 13, 1, 4, 6, 12, 0, 5, 10, 11, 8, 9 },
        PrimitiveCollections.countingSort(array, 1, 10, 2, array.length - 3));
    int[] startIndices = new int[9];
    assertArrayEquals(new int[] { 2, 3, 7, 13, 1, 4, 6, 12, 0, 5, 10, 11, 8, 9 },
        PrimitiveCollections.countingSort(array, 1, startIndices, 2, array.length - 3));
    assertArrayEquals(new int[] { 0, 1, 2, 4, 8, 11, 12, 13, 13 }, startIndices);
    assertArrayEquals(
        new int[] { 4, 5, 0, 9, 15, 3, 6, 8, 14, 2, 7, 12, 17, 13, 10, 16, 1, 11, 18 },
        PrimitiveCollections.countingSort(array));
    int[] startIndices2 = new int[11];
    assertArrayEquals(
        new int[] { 4, 5, 0, 9, 15, 3, 6, 8, 14, 2, 7, 12, 17, 13, 10, 16, 1, 11, 18 },
        PrimitiveCollections.countingSort(array, 1, startIndices2));
    assertArrayEquals(new int[] { 0, 1, 2, 5, 9, 13, 14, 16, 17, 18, 18 }, startIndices2);
    int[] array2 = { 1, 6, 3, 2, 0, 0, 2, 3, 2, 1, 5, 7, 3, 4, 2, 1, 5, 3, 9 };
    assertArrayEquals(
        new int[] { 4, 5, 0, 9, 15, 3, 6, 8, 14, 2, 7, 12, 17, 13, 10, 16, 1, 11, 18 },
        PrimitiveCollections.countingSort(array2, 10));

    ConstMapping.OfInt list = Mappings.wrapUnmodifiableInt(1, 6, 3, 2, -1, 0, 2, 3, 2, 1, 5, 7, 3,
        4, 2, 1, 5, 3, 9);
    ConstMapping.OfInt result = Mappings.wrapUnmodifiableInt(4, 5, 0, 9, 15, 3, 6, 8, 14, 2, 7, 12,
        17, 13, 10, 16, 1, 11, 18);
    ConstMapping.OfInt result2 = Mappings.wrapUnmodifiableInt(2, 3, 7, 13, 1, 4, 6, 12, 0, 5, 10,
        11, 8, 9);
    assertEquals(result, PrimitiveCollections.countingSort(list, 1, 11, 0, list.size()));
    assertEquals(result2, PrimitiveCollections.countingSort(list, 1, 10, 2, list.size() - 3));
    Mapping.OfInt startIndices3 = Mappings.wrapModifiableInt(new int[9]);
    assertEquals(result2,
        PrimitiveCollections.countingSort(list, 1, startIndices3, 2, list.size() - 3));
    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 4, 8, 11, 12, 13, 13), startIndices3);
    assertEquals(result, PrimitiveCollections.countingSort(list));
    Mapping.OfInt startIndices4 = Mappings.wrapModifiableInt(new int[11]);
    assertEquals(result, PrimitiveCollections.countingSort(list, 1, startIndices4));
    assertEquals(Mappings.wrapUnmodifiableInt(0, 1, 2, 5, 9, 13, 14, 16, 17, 18, 18),
        startIndices4);
    ConstMapping.OfInt list2 = Mappings.wrapUnmodifiableInt(1, 6, 3, 2, 0, 0, 2, 3, 2, 1, 5, 7, 3,
        4, 2, 1, 5, 3, 9);
    assertEquals(result, PrimitiveCollections.countingSort(list2, 10));
  }

  @Test
  void testPermute() {
    int[] permutation = new int[] { 1, 6, 3, 8, 2, 4, 7, 5, 0 };
    assertArrayEquals(new int[] { 4, 4, 6, 3, 2, 3, 5, 2, 0 },
        PrimitiveCollections.permute(new int[] { 0, 4, 2, 6, 3, 2, 4, 5, 3 }, permutation));
    assertThrows(IllegalArgumentException.class,
        () -> PrimitiveCollections.permute(new int[] { 0, 4, 2, 6, 3, 2, 4, 5 }, permutation));
    assertArrayEquals(new long[] { 4, 4, 6, 3, 2, 3, 5, 2, 0 },
        PrimitiveCollections.permute(new long[] { 0, 4, 2, 6, 3, 2, 4, 5, 3 }, permutation));
    assertThrows(IllegalArgumentException.class,
        () -> PrimitiveCollections.permute(new long[] { 0, 4, 2, 6, 3, 2, 4, 5 }, permutation));
    assertArrayEquals(new double[] { 4, 4, 6, 3, 2, 3, 5, 2, 0 },
        PrimitiveCollections.permute(new double[] { 0, 4, 2, 6, 3, 2, 4, 5, 3 }, permutation));
    assertThrows(IllegalArgumentException.class,
        () -> PrimitiveCollections.permute(new double[] { 0, 4, 2, 6, 3, 2, 4, 5 }, permutation));
    assertArrayEquals(new String[] { "4", "4", "6", "3", "2", "3", "5", "2", "0" },
        PrimitiveCollections.permute(new String[] { "0", "4", "2", "6", "3", "2", "4", "5", "3" },
            permutation));
    assertThrows(IllegalArgumentException.class, () -> PrimitiveCollections
        .permute(new String[] { "0", "4", "2", "6", "3", "2", "4", "5" }, permutation));

    ConstMapping.OfInt permMapping = Mappings.wrapUnmodifiableInt(1, 6, 3, 8, 2, 4, 7, 5, 0);
    assertEquals(Mappings.wrapUnmodifiableInt(4, 4, 6, 3, 2, 3, 5, 2, 0), PrimitiveCollections
        .permute(Mappings.wrapUnmodifiableInt(0, 4, 2, 6, 3, 2, 4, 5, 3), permMapping));
    assertThrows(IllegalArgumentException.class, () -> PrimitiveCollections
        .permute(Mappings.wrapUnmodifiableInt(0, 4, 2, 6, 3, 2, 4, 5), permMapping));
    assertEquals(Mappings.newLongListFrom(4, 4, 6, 3, 2, 3, 5, 2, 0), PrimitiveCollections
        .permute(Mappings.newLongListFrom(0, 4, 2, 6, 3, 2, 4, 5, 3), permMapping));
    assertThrows(IllegalArgumentException.class, () -> PrimitiveCollections
        .permute(Mappings.newLongListFrom(0, 4, 2, 6, 3, 2, 4, 5), permMapping));
    assertEquals(Mappings.newDoubleListFrom(4, 4, 6, 3, 2, 3, 5, 2, 0), PrimitiveCollections
        .permute(Mappings.newDoubleListFrom(0, 4, 2, 6, 3, 2, 4, 5, 3), permMapping));
    assertThrows(IllegalArgumentException.class, () -> PrimitiveCollections
        .permute(Mappings.newDoubleListFrom(0, 4, 2, 6, 3, 2, 4, 5), permMapping));
    assertEquals(Mappings.newListFrom(String.class, "4", "4", "6", "3", "2", "3", "5", "2", "0"),
        PrimitiveCollections.permute(
            Mappings.newListFrom(String.class, "0", "4", "2", "6", "3", "2", "4", "5", "3"),
            permMapping));
    assertThrows(IllegalArgumentException.class,
        () -> PrimitiveCollections.permute(
            Mappings.newListFrom(String.class, "0", "4", "2", "6", "3", "2", "4", "5"),
            permMapping));
  }

  @Test
  void testCompose() {

    int[] indices = new int[] { 1, 6, 3, -1, 8, 2, 4, 7, 5, 0, -1 };
    int[] lessIndices = new int[] { 1, 6, 8, 2, 4, 7, 5, 0 };

    assertArrayEquals(new int[] { 4, 4, 6, 11, 3, 2, 3, 5, 2, 0, 11 },
        PrimitiveCollections.compose(new int[] { 0, 4, 2, 6, 3, 2, 4, 5, 3 }, indices, 11));
    assertArrayEquals(new int[] { 4, 4, 3, 2, 3, 5, 2, 0 },
        PrimitiveCollections.compose(new int[] { 0, 4, 2, 6, 3, 2, 4, 5, 3 }, lessIndices, 11));
    assertArrayEquals(new long[] { 4, 4, 6, 11, 3, 2, 3, 5, 2, 0, 11 },
        PrimitiveCollections.compose(new long[] { 0, 4, 2, 6, 3, 2, 4, 5, 3 }, indices, 11L));
    assertArrayEquals(new long[] { 4, 4, 3, 2, 3, 5, 2, 0 },
        PrimitiveCollections.compose(new long[] { 0, 4, 2, 6, 3, 2, 4, 5, 3 }, lessIndices, 11L));
    assertArrayEquals(new double[] { 4, 4, 6, 11, 3, 2, 3, 5, 2, 0, 11 },
        PrimitiveCollections.compose(new double[] { 0, 4, 2, 6, 3, 2, 4, 5, 3 }, indices, 11.));
    assertArrayEquals(new double[] { 4, 4, 3, 2, 3, 5, 2, 0 },
        PrimitiveCollections.compose(new double[] { 0, 4, 2, 6, 3, 2, 4, 5, 3 }, lessIndices, 11.));
    assertArrayEquals(new String[] { "4", "4", "6", "11", "3", "2", "3", "5", "2", "0", "11" },
        PrimitiveCollections.compose(new String[] { "0", "4", "2", "6", "3", "2", "4", "5", "3" },
            indices, "11"));
    assertArrayEquals(new String[] { "4", "4", "3", "2", "3", "5", "2", "0" },
        PrimitiveCollections.compose(new String[] { "0", "4", "2", "6", "3", "2", "4", "5", "3" },
            lessIndices, "11"));

    ConstMapping.OfInt indicesMap = Mappings.wrapUnmodifiableInt(indices);
    ConstMapping.OfInt lessIndicesMap = Mappings.wrapUnmodifiableInt(lessIndices);
    assertEquals(Mappings.wrapModifiableInt(4, 4, 6, 11, 3, 2, 3, 5, 2, 0, 11), PrimitiveCollections
        .compose(Mappings.wrapModifiableInt(0, 4, 2, 6, 3, 2, 4, 5, 3), indicesMap, 11));
    assertEquals(Mappings.wrapModifiableInt(4, 4, 3, 2, 3, 5, 2, 0), PrimitiveCollections
        .compose(Mappings.wrapModifiableInt(0, 4, 2, 6, 3, 2, 4, 5, 3), lessIndicesMap, 11));
    assertEquals(Mappings.newLongListFrom(4, 4, 6, 11, 3, 2, 3, 5, 2, 0, 11), PrimitiveCollections
        .compose(Mappings.newLongListFrom(0, 4, 2, 6, 3, 2, 4, 5, 3), indicesMap, 11L));
    assertEquals(Mappings.newLongListFrom(4, 4, 3, 2, 3, 5, 2, 0), PrimitiveCollections
        .compose(Mappings.newLongListFrom(0, 4, 2, 6, 3, 2, 4, 5, 3), lessIndicesMap, 11L));
    assertEquals(Mappings.newDoubleListFrom(4, 4, 6, 11, 3, 2, 3, 5, 2, 0, 11), PrimitiveCollections
        .compose(Mappings.newDoubleListFrom(0, 4, 2, 6, 3, 2, 4, 5, 3), indicesMap, 11.));
    assertEquals(Mappings.newDoubleListFrom(4, 4, 3, 2, 3, 5, 2, 0), PrimitiveCollections
        .compose(Mappings.newDoubleListFrom(0, 4, 2, 6, 3, 2, 4, 5, 3), lessIndicesMap, 11.));
    assertEquals(
        Mappings.newListFrom(String.class, "4", "4", "6", "11", "3", "2", "3", "5", "2", "0", "11"),
        PrimitiveCollections.compose(
            Mappings.newListFrom(String.class, "0", "4", "2", "6", "3", "2", "4", "5", "3"),
            indicesMap, "11"));
    assertEquals(Mappings.newListFrom(String.class, "4", "4", "3", "2", "3", "5", "2", "0"),
        PrimitiveCollections.compose(
            Mappings.newListFrom(String.class, "0", "4", "2", "6", "3", "2", "4", "5", "3"),
            lessIndicesMap, "11"));
  }

  @Test
  void testOthers() {
    int[] array = { 2, 6, 3, 4, 1, 6, 3, 2 };
    PrimitiveCollections.reverse(array);
    assertArrayEquals(new int[] { 2, 3, 6, 1, 4, 3, 6, 2 }, array);
    double[] array2 = { 2, 6, 3, 4, 1, 6, 3, 2 };
    PrimitiveCollections.reverse(array2);
    assertArrayEquals(new double[] { 2, 3, 6, 1, 4, 3, 6, 2 }, array2);
    int[] array3 = { 2, 6, 3, 4, 1, 1, 6, 3, 2 };
    PrimitiveCollections.reverse(array3);
    assertArrayEquals(new int[] { 2, 3, 6, 1, 1, 4, 3, 6, 2 }, array3);
    double[] array4 = { 2, 6, 3, 4, 1, 1, 6, 3, 2 };
    PrimitiveCollections.reverse(array4);
    assertArrayEquals(new double[] { 2, 3, 6, 1, 1, 4, 3, 6, 2 }, array4);

    PrimitiveList.OfInt list = Mappings.newIntListFrom(6, 3, 2, 7, 3, 2, 5, 4, 3, 2, 8, 3, 5, 4);
    PrimitiveCollections.sort(list);
    assertEquals(Mappings.wrapUnmodifiableInt(2, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 6, 7, 8), list);

    assertArrayEquals(new int[] { 0 }, PrimitiveCollections.group(new int[] {}));
    assertArrayEquals(new int[] { 0, 2, 3, 6, 7, 9, 12 },
        PrimitiveCollections.group(new int[] { 2, 2, 5, 7, 7, 7, 8, 9, 9, 10, 10, 10 }));
    assertArrayEquals(new int[] { 0 }, PrimitiveCollections.group(new double[] {}));
    assertArrayEquals(new int[] { 0, 2, 3, 6, 7, 9, 12 }, PrimitiveCollections
        .group(new double[] { 2., 2., 2.5, 7., 7., 7., 8., 9., 9., 10., 10., 10. }));

    assertArrayEquals(new String[] {}, PrimitiveCollections.map(new String[] {}, x -> x + "t"));
    assertArrayEquals(new String[] { "quackt", "wooft", "quackt", "barkt" },
        PrimitiveCollections.map(new String[] { "quack", "woof", "quack", "bark" }, x -> x + "t"));
    assertArrayEquals(new int[] {}, PrimitiveCollections.map(new int[] {}, x -> x + 1));
    assertArrayEquals(new int[] { 1, 3, 2, 5, 4, 3 },
        PrimitiveCollections.map(new int[] { 0, 2, 1, 4, 3, 2 }, x -> x + 1));
    assertArrayEquals(new int[] { -1, 1, 0, 3, 2, 1 },
        PrimitiveCollections.map(new int[] { 0, 2, 1, 4, 3, 2 }, x -> x - 1));
    assertArrayEquals(new double[] {}, PrimitiveCollections.map(new double[] {}, x -> x + 1));
    assertArrayEquals(new double[] { 0.5, 2.5, 1.5, 4.5, 3.5, 2.5 },
        PrimitiveCollections.map(new double[] { 0, 2, 1, 4, 3, 2 }, x -> x + 0.5));
    assertArrayEquals(new double[] { -0.5, 1.5, 0.5, 3.5, 2.5, 1.5 },
        PrimitiveCollections.map(new double[] { 0, 2, 1, 4, 3, 2 }, x -> x - 0.5));
  }

}
