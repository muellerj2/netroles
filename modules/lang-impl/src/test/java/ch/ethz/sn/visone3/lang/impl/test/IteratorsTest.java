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

package ch.ethz.sn.visone3.lang.impl.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Iterators;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveIterable;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class IteratorsTest {

  @Test
  void testEmptyIntIterator() {
    PrimitiveIterator.OfInt emptyIt = Iterators.emptyIteratorInt();
    assertFalse(emptyIt.hasNext());
    assertThrows(NoSuchElementException.class, () -> emptyIt.next());
    for (int x : Iterators.emptyInt()) {
      fail("invalid value " + x + "in empty iterator");
    }
    PrimitiveIterator.OfInt emptyIt2 = Iterators.emptyInt().iterator();
    assertFalse(emptyIt2.hasNext());
    assertThrows(NoSuchElementException.class, () -> emptyIt2.next());
  }

  @Test
  void testSingletonIntIterator() {
    PrimitiveIterator.OfInt singletonIt = Iterators.singletonItrInt(25);
    assertTrue(singletonIt.hasNext());
    assertEquals(25, singletonIt.next());
    assertFalse(singletonIt.hasNext());
    assertThrows(NoSuchElementException.class, () -> singletonIt.next());
    PrimitiveIterable.OfInt singletonIterable = Iterators.singletonInt(30);
    boolean loop = false;
    for (int x : singletonIterable) {
      assertEquals(30, x);
      assertFalse(loop);
      loop = true;
    }
    PrimitiveIterator.OfInt singletonIt2 = singletonIterable.iterator();
    assertTrue(singletonIt2.hasNext());
    assertEquals(30, singletonIt2.next());
    assertFalse(singletonIt2.hasNext());
    assertThrows(NoSuchElementException.class, () -> singletonIt2.next());
  }

  @Test
  void testIntFilterIterator() {
    ConstMapping.OfInt mapping = Mappings.wrapUnmodifiableInt(5, 1, 0, 6, 8, 2, 3, 5, 6);
    PrimitiveIterator.OfInt filterIt = Iterators.filter(mapping.iterator(),
        (IntPredicate) x -> x % 2 == 0);
    int[] result = new int[] { 0, 6, 8, 2, 6 };
    for (int i = 0; i < result.length; ++i) {
      assertTrue(filterIt.hasNext());
      assertEquals(result[i], filterIt.next());
    }
    assertFalse(filterIt.hasNext());
    assertThrows(NoSuchElementException.class, () -> filterIt.next());
    PrimitiveIterator.OfInt filterIt2 = Iterators.filter(mapping.iterator(),
        (IntPredicate) x -> x % 2 == 0);
    for (int i = 0; i < result.length; ++i) {
      assertTrue(filterIt2.hasNext());
      assertEquals(result[i], filterIt2.nextInt());
    }

    PrimitiveIterable.OfInt filterIterable = Iterators.filter(mapping, (IntPredicate) x -> x >= 4);
    int[] result2 = new int[] { 5, 6, 8, 5, 6 };
    int pos = 0;
    for (int x : filterIterable) {
      assertEquals(result2[pos++], x);
    }
    PrimitiveIterator.OfInt filterIt3 = filterIterable.iterator();
    for (int i = 0; i < result2.length; ++i) {
      assertTrue(filterIt3.hasNext());
      assertEquals(result2[i], filterIt3.next());
    }
    assertFalse(filterIt3.hasNext());
    assertThrows(NoSuchElementException.class, () -> filterIt3.next());
    PrimitiveIterator.OfInt filterIt4 = filterIterable.iterator();
    for (int i = 0; i < result2.length; ++i) {
      assertTrue(filterIt4.hasNext());
      assertEquals(result2[i], filterIt4.nextInt());
    }
  }

  @Test
  void testGenericFilterIterator() {
    ConstMapping<String> mapping = Mappings.newListFrom(String.class, "a", "cb", "d", "xz", "uv",
        "g", "eh", "moo", "p");
    Iterator<String> filterIt = Iterators.filter(mapping.iterator(), x -> x.length() > 1);
    String[] result = new String[] { "cb", "xz", "uv", "eh", "moo" };
    for (int i = 0; i < result.length; ++i) {
      assertTrue(filterIt.hasNext());
      assertEquals(result[i], filterIt.next());
    }
    assertFalse(filterIt.hasNext());
    assertThrows(NoSuchElementException.class, () -> filterIt.next());

    Iterable<String> filterIterable = Iterators.filter(mapping, x -> x.charAt(0) >= 'n');
    String[] result2 = new String[] { "xz", "uv", "p" };
    int pos = 0;
    for (String x : filterIterable) {
      assertEquals(result2[pos++], x);
    }
    Iterator<String> filterIt2 = filterIterable.iterator();
    for (int i = 0; i < result2.length; ++i) {
      assertTrue(filterIt2.hasNext());
      assertEquals(result2[i], filterIt2.next());
    }
    assertFalse(filterIt2.hasNext());
    assertThrows(NoSuchElementException.class, () -> filterIt2.next());
  }

  @Test
  void testIntConcatIterator() {
    ConstMapping.OfInt mapping1 = Mappings.wrapUnmodifiableInt(2, 5, 4, 3, 6, 8);
    ConstMapping.OfInt mapping2 = Mappings.wrapUnmodifiableInt(6, 3, 2, 3);
    int[] result = IntStream.concat(mapping1.intStream(), mapping2.intStream()).toArray();
    PrimitiveIterator.OfInt concatIt = Iterators.concat(mapping1.iterator(), mapping2.iterator());
    for (int val : result) {
      assertTrue(concatIt.hasNext());
      assertEquals(val, concatIt.next());
    }
    assertFalse(concatIt.hasNext());
    assertThrows(NoSuchElementException.class, () -> concatIt.next());
    PrimitiveIterator.OfInt concatIt2 = Iterators.concat(Mappings.wrapModifiableInt().iterator(),
        mapping2.iterator());
    for (int val : mapping2.toUnboxedArray()) {
      assertTrue(concatIt2.hasNext());
      assertEquals(val, concatIt2.nextInt());
    }
    assertFalse(concatIt2.hasNext());
    assertThrows(NoSuchElementException.class, () -> concatIt2.nextInt());
    PrimitiveIterator.OfInt concatIt3 = Iterators.concat(mapping1.iterator(),
        Iterators.emptyIteratorInt());
    for (int val : mapping1.toUnboxedArray()) {
      assertTrue(concatIt3.hasNext());
      assertEquals(val, concatIt3.next());
    }
    assertFalse(concatIt3.hasNext());
    assertThrows(NoSuchElementException.class, () -> concatIt3.next());
    PrimitiveIterator.OfInt concatIt4 = Iterators.concat(Mappings.wrapModifiableInt().iterator(),
        Iterators.emptyIteratorInt());
    assertFalse(concatIt4.hasNext());
    assertThrows(NoSuchElementException.class, () -> concatIt4.next());

    PrimitiveIterable.OfInt concatIterable = Iterators.concat(mapping1, mapping2);
    int pos = 0;
    for (int val : concatIterable) {
      assertEquals(result[pos++], val);
    }
    PrimitiveIterator.OfInt concatIt5 = concatIterable.iterator();
    for (int val : result) {
      assertTrue(concatIt5.hasNext());
      assertEquals(val, concatIt5.nextInt());
    }
    assertFalse(concatIt.hasNext());
    assertThrows(NoSuchElementException.class, () -> concatIt5.next());
  }

  @Test
  void testGenericConcatIterator() {
    ConstMapping<String> mapping1 = Mappings.newListFrom(String.class, "bark", "woof", "moo",
        "bark");
    ConstMapping<String> mapping2 = Mappings.newListFrom(String.class, "neigh", "meow", "moo",
        "woof");
    String[] result = Stream.concat(mapping1.stream(), mapping2.stream()).toArray(String[]::new);
    Iterator<String> concatIt = Iterators.concat(mapping1.iterator(), mapping2.iterator());
    for (String val : result) {
      assertTrue(concatIt.hasNext());
      assertEquals(val, concatIt.next());
    }
    assertFalse(concatIt.hasNext());
    assertThrows(NoSuchElementException.class, () -> concatIt.next());
    Iterator<String> concatIt2 = Iterators.concat(Mappings.newList(String.class).iterator(),
        mapping2.iterator());
    for (String val : (String[]) mapping2.toUnboxedArray()) {
      assertTrue(concatIt2.hasNext());
      assertEquals(val, concatIt2.next());
    }
    assertFalse(concatIt2.hasNext());
    assertThrows(NoSuchElementException.class, () -> concatIt2.next());
    Iterator<String> concatIt3 = Iterators.concat(mapping1.iterator(),
        Mappings.newList(String.class).iterator());
    for (String val : (String[]) mapping1.toUnboxedArray()) {
      assertTrue(concatIt3.hasNext());
      assertEquals(val, concatIt3.next());
    }
    assertFalse(concatIt3.hasNext());
    assertThrows(NoSuchElementException.class, () -> concatIt3.next());
    Iterator<String> concatIt4 = Iterators.concat(Mappings.newList(String.class).iterator(),
        Collections.<String>emptyIterator());
    assertFalse(concatIt4.hasNext());
    assertThrows(NoSuchElementException.class, () -> concatIt4.next());

    Iterable<String> concatIterable = Iterators.concat(mapping1, mapping2);
    int pos = 0;
    for (String val : concatIterable) {
      assertEquals(result[pos++], val);
    }
    Iterator<String> concatIt5 = concatIterable.iterator();
    for (String val : result) {
      assertTrue(concatIt5.hasNext());
      assertEquals(val, concatIt5.next());
    }
    assertFalse(concatIt.hasNext());
    assertThrows(NoSuchElementException.class, () -> concatIt5.next());
  }

  @Test
  void testMapIterator() {
    ConstMapping<String> mapping = Mappings.newListFrom(String.class, "bark", "woof",
        "cock-a-doodle-doo", "moo", "quack", "ribbit");
    Iterator<Integer> mapIt = Iterators.map(mapping.iterator(), String::length);
    for (String val : mapping) {
      assertTrue(mapIt.hasNext());
      assertEquals(val.length(), mapIt.next());
    }
    assertFalse(mapIt.hasNext());
    assertThrows(NoSuchElementException.class, () -> mapIt.next());

    Iterator<Boolean> mapIt2 = Iterators.map(Collections.<String>emptyIterator(), String::isEmpty);
    assertFalse(mapIt2.hasNext());
    assertThrows(NoSuchElementException.class, () -> mapIt2.next());

    Iterable<Character> mapIterable = Iterators.map(mapping, x -> x.charAt(0));
    int pos = 0;
    for (char val : mapIterable) {
      assertEquals(mapping.get(pos++).charAt(0), val);
    }
    Iterator<Character> mapIt3 = mapIterable.iterator();
    for (String val : mapping) {
      assertTrue(mapIt3.hasNext());
      assertEquals(val.charAt(0), mapIt3.next());
    }
    assertFalse(mapIt3.hasNext());
    assertThrows(NoSuchElementException.class, () -> mapIt3.next());
  }
}
