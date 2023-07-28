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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Indexed;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveList;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class MappingsTest {

  private static void checkMappingInternal(ConstMapping<?> mapping, Class<?> type,
      Class<?> componentType, Object[] values) {
    assertTrue(type.isAssignableFrom(mapping.getClass()));
    assertEquals(componentType, mapping.getComponentType());
    assertEquals(values.length, mapping.size());
    assertArrayEquals(values, mapping.stream().toArray());
    assertEquals(Arrays.asList(values).hashCode(), mapping.hashCode());
    assertNotNull(mapping.toString());

    int pos = 0;
    for (Object x : mapping) {
      int thisPos = pos;
      assertEquals(values[pos], x);
      assertEquals(values[pos], mapping.get(pos));
      assertEquals(values[pos], mapping.get(new Indexed() {
        @Override
        public int getIndex() {
          return thisPos;
        }
      }));
      ++pos;
    }
    Iterator<?> iterator = mapping.iterator();
    for (int i = 0; i < values.length; ++i) {
      assertTrue(iterator.hasNext());
      assertEquals(values[i], iterator.next());
    }
    assertFalse(iterator.hasNext());
    assertThrows(NoSuchElementException.class, () -> iterator.next());

    assertThrows(IndexOutOfBoundsException.class, () -> mapping.get(-5));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping.get(() -> -5));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping.get(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping.get(() -> -1));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping.get(mapping.size()));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping.get(() -> mapping.size()));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping.get(mapping.size() + 2));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping.get(() -> mapping.size() + 2));
  }

  private static <T extends ConstMapping.OfInt> T checkIntMapping(ConstMapping<?> mapping,
      Class<T> type, Object[] values) {
    checkMappingInternal(mapping, type, int.class, values);
    @SuppressWarnings("unchecked")
    T mapping2 = (T) mapping;
    int[] array = mapping2.toUnboxedArray();
    assertArrayEquals(values, Arrays.stream(array).boxed().toArray());
    assertArrayEquals(values, mapping2.intStream().boxed().toArray());
    assertArrayEquals(values, mapping2.stream().toArray());

    int pos = 0;
    for (int x : mapping2) {
      int thisPos = pos;
      assertEquals(values[pos], x);
      assertEquals(values[pos], mapping2.getInt(pos));
      assertEquals(values[pos], mapping2.getInt(new Indexed() {
        @Override
        public int getIndex() {
          return thisPos;
        }
      }));
      ++pos;
    }
    PrimitiveIterator.OfInt iterator = mapping2.iterator();
    for (int i = 0; i < values.length; ++i) {
      assertEquals(values[i], iterator.nextInt());
    }
    assertThrows(NoSuchElementException.class, () -> iterator.next());

    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getInt(-5));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getInt(() -> -5));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getInt(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getInt(() -> -1));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getInt(mapping2.size()));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getInt(() -> mapping2.size()));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getInt(mapping2.size() + 2));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getInt(() -> mapping2.size() + 2));
    return mapping2;
  }

  private static <T extends ConstMapping.OfLong> T checkLongMapping(ConstMapping<?> mapping,
      Class<T> type, Object[] values) {
    checkMappingInternal(mapping, type, long.class, values);
    @SuppressWarnings("unchecked")
    T mapping2 = (T) mapping;
    long[] array = mapping2.toUnboxedArray();
    assertArrayEquals(values, Arrays.stream(array).boxed().toArray());
    assertArrayEquals(values, mapping2.longStream().boxed().toArray());
    assertArrayEquals(values, mapping2.stream().toArray());

    int pos = 0;
    for (long x : mapping2) {
      int thisPos = pos;
      assertEquals(values[pos], x);
      assertEquals(values[pos], mapping2.getLong(pos));
      assertEquals(values[pos], mapping2.getLong(new Indexed() {
        @Override
        public int getIndex() {
          return thisPos;
        }
      }));
      ++pos;
    }
    PrimitiveIterator.OfLong iterator = mapping2.iterator();
    for (int i = 0; i < values.length; ++i) {
      assertEquals(values[i], iterator.nextLong());
    }
    assertThrows(NoSuchElementException.class, () -> iterator.next());

    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getLong(-5));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getLong(() -> -5));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getLong(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getLong(() -> -1));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getLong(mapping2.size()));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getLong(() -> mapping2.size()));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getLong(mapping2.size() + 2));
    assertThrows(IndexOutOfBoundsException.class,
        () -> mapping2.getLong(() -> mapping2.size() + 2));
    return mapping2;
  }

  private static <T extends ConstMapping.OfDouble> T checkDoubleMapping(ConstMapping<?> mapping,
      Class<T> type, Object[] values) {
    checkMappingInternal(mapping, type, double.class, values);
    @SuppressWarnings("unchecked")
    T mapping2 = (T) mapping;
    double[] array = mapping2.toUnboxedArray();
    assertArrayEquals(values, Arrays.stream(array).boxed().toArray());
    assertArrayEquals(values, mapping2.doubleStream().boxed().toArray());
    assertArrayEquals(values, mapping2.stream().toArray());

    int pos = 0;
    for (double x : mapping2) {
      int thisPos = pos;
      assertEquals(values[pos], x);
      assertEquals(values[pos], mapping2.getDouble(pos));
      assertEquals(values[pos], mapping2.getDouble(new Indexed() {
        @Override
        public int getIndex() {
          return thisPos;
        }
      }));
      ++pos;
    }
    PrimitiveIterator.OfDouble iterator = mapping2.iterator();
    for (int i = 0; i < values.length; ++i) {
      assertEquals(values[i], iterator.nextDouble());
    }
    assertThrows(NoSuchElementException.class, () -> iterator.next());

    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getDouble(-5));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getDouble(() -> -5));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getDouble(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getDouble(() -> -1));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getDouble(mapping2.size()));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getDouble(() -> mapping2.size()));
    assertThrows(IndexOutOfBoundsException.class, () -> mapping2.getDouble(mapping2.size() + 2));
    assertThrows(IndexOutOfBoundsException.class,
        () -> mapping2.getDouble(() -> mapping2.size() + 2));
    return mapping2;
  }

  private static <U, T extends ConstMapping<U>> T checkMapping(T mapping, Class<?> type,
      Class<?> componentType, Object[] values) {
    checkMappingInternal(mapping, type, componentType, values);
    Object[] array = (Object[]) mapping.toUnboxedArray();
    assertArrayEquals(values, array);
    assertArrayEquals(values, mapping.stream().toArray());
    return mapping;
  }

  @SuppressWarnings("deprecation")
  private void checkIntListSpecific(PrimitiveList.OfInt list, Object[] values) {
    Object[] values2 = Arrays.copyOf(values, values.length);
    if (values.length == 0) {
      assertTrue(list.isEmpty());
      assertEquals(0, list.size());
      values2 = new Object[] { 5 };
      list.addInt(5);
    }
    assertFalse(list.isEmpty());
    assertArrayEquals(values2, list.stream().toArray());

    assertThrows(IndexOutOfBoundsException.class, () -> list.setInt(-1, 0));
    assertThrows(IndexOutOfBoundsException.class, () -> list.setInt(list.size(), 0));
    assertThrows(IndexOutOfBoundsException.class, () -> list.set(-1, 0));
    assertThrows(IndexOutOfBoundsException.class, () -> list.set(list.size(), 0));
    Integer val = (Integer) values2[0];
    assertEquals(val, list.setInt(0, val + 1));
    values2[0] = val + 1;
    assertArrayEquals(values2, list.stream().toArray());
    assertEquals(val + 1, list.getInt(0));
    assertEquals(val + 1, list.get(0));
    assertEquals(val + 1, list.set(0, val + 2));
    values2[0] = val + 2;
    assertArrayEquals(values2, list.stream().toArray());
    assertEquals(val + 2, list.getInt(0));
    assertEquals(val + 2, list.get(0));

    list.addInt(3);
    assertTrue(list.add(7));
    assertEquals(values2.length + 2, list.size());
    assertArrayEquals(Stream.concat(Arrays.stream(values2), Stream.of(3, 7)).toArray(),
        list.stream().toArray());
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeIndex(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeIndex(list.size()));
    assertThrows(IndexOutOfBoundsException.class, () -> list.remove(list.size()));
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeRange(-1, 0));
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeRange(0, list.size() + 1));
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeRange(-1, list.size() + 1));
    assertThrows(IllegalArgumentException.class, () -> list.removeRange(3, 2));
    list.removeRange(2, 2);
    assertArrayEquals(Stream.concat(Arrays.stream(values2), Stream.of(3, 7)).toArray(),
        list.stream().toArray());
    assertTrue(list.add(7));
    assertEquals(val + 2, list.remove(0));
    assertEquals(values2.length + 2, list.size());
    assertArrayEquals(
        Stream.concat(Arrays.stream(values2, 1, values2.length), Stream.of(3, 7, 7)).toArray(),
        list.stream().toArray());
    list.removeRange(list.size() - 2, list.size());
    assertEquals(values2.length, list.size());
    assertEquals(3, list.removeInt(list.size() - 1));
    assertArrayEquals(Arrays.stream(values2, 1, values2.length).toArray(), list.stream().toArray());
    list.clear();
    assertTrue(list.isEmpty());
    for (Object add : values2) {
      list.add((Integer) add);
    }
    assertArrayEquals(values2, list.stream().toArray());
    for (int i = 0; i < 200; ++i) {
      list.addInt(i + 15);
    }
    assertEquals(values2.length + 200, list.size());
    assertNotNull(list.toString());
    assertArrayEquals(Stream
        .concat(Arrays.stream(values2), IntStream.range(0, 200).map(x -> x + 15).boxed()).toArray(),
        list.stream().toArray());
    assertThrows(IllegalArgumentException.class, () -> list.ensureCapacity(100));
    list.setSize(0, 100 + values2.length);
    assertArrayEquals(Stream
        .concat(Arrays.stream(values2), IntStream.range(0, 100).map(x -> x + 15).boxed()).toArray(),
        list.stream().toArray());
    assertNotNull(list.toString());
    list.setSize(10, 101 + values2.length);
    assertArrayEquals(Stream.concat(
        Stream.concat(Arrays.stream(values2), IntStream.range(0, 100).map(x -> x + 15).boxed()),
        Stream.of(10)).toArray(), list.stream().toArray());
    list.setSize(10, 100 + values2.length);
    assertArrayEquals(Stream
        .concat(Arrays.stream(values2), IntStream.range(0, 100).map(x -> x + 15).boxed()).toArray(),
        list.stream().toArray());
    assertTrue(list.arrayQuick().length >= 100 + values2.length);
    list.ensureCapacity(100 + values2.length);
    assertEquals(values2.length + 100, list.arrayQuick().length);
    list.setSize(35, 200 + values2.length);
    assertArrayEquals(Stream.concat(
        Stream.concat(Arrays.stream(values2), IntStream.range(0, 100).map(x -> x + 15).boxed()),
        IntStream.range(0, 100).map(x -> 35).boxed()).toArray(), list.stream().toArray());
    list.setSize(35, 100 + values2.length);
    assertArrayEquals(IntStream.concat(Arrays.stream(values2).mapToInt(x -> (Integer) x),
        IntStream.range(0, 100).map(x -> x + 15)).toArray(), list.array());

    list.clear();
    assertTrue(list.isEmpty());
    assertEquals(0, list.size());
    list.addInt(val + 5);
    list.addInt(val + 2);
    assertEquals(val + 5, list.arrayQuick()[0]);
    assertEquals(val + 2, list.arrayQuick()[1]);
    assertArrayEquals(new int[] { val + 5, val + 2 }, list.array());
    assertArrayEquals(new int[] { val + 5, val + 2 }, list.array());

    assertTrue(list.equals(list));
    assertFalse(list.equals(Arrays.asList(val + 5, val + 2)));
    assertNotEquals(list, Mappings.newIntListFrom(val + 2, val + 5));
    assertEquals(list, Mappings.newIntListFrom(val + 5, val + 2));
    assertNotEquals(list, Mappings.newIntListFrom(val + 5));
    assertNotEquals(list, Mappings.newIntListFrom(val + 5, val + 2, 0));
  }

  private void checkIntList(ConstMapping<?> list, Object[] values) {
    checkIntListSpecific(checkIntMapping(list, PrimitiveList.OfInt.class, values), values);
  }

  @SuppressWarnings("deprecation")
  private void checkLongListSpecific(PrimitiveList.OfLong list, Object[] values) {
    Object[] values2 = Arrays.copyOf(values, values.length);
    if (values.length == 0) {
      assertTrue(list.isEmpty());
      assertEquals(0, list.size());
      values2 = new Object[] { 20L };
      list.addLong(20L);
    }
    assertFalse(list.isEmpty());
    assertArrayEquals(values2, list.stream().toArray());

    assertThrows(IndexOutOfBoundsException.class, () -> list.setLong(-1, 0L));
    assertThrows(IndexOutOfBoundsException.class, () -> list.setLong(list.size(), 0L));
    assertThrows(IndexOutOfBoundsException.class, () -> list.set(-1, 0L));
    assertThrows(IndexOutOfBoundsException.class, () -> list.set(list.size(), 0L));
    Long val = (Long) values2[0];
    assertEquals(val, list.setLong(0, val + 1));
    values2[0] = val + 1;
    assertArrayEquals(values2, list.stream().toArray());
    assertEquals(val + 1, list.getLong(0));
    assertEquals(val + 1, list.get(0));
    assertEquals(val + 1, list.set(0, val + 2));
    values2[0] = val + 2;
    assertArrayEquals(values2, list.stream().toArray());
    assertEquals(val + 2, list.getLong(0));
    assertEquals(val + 2, list.get(0));

    list.addLong(3L);
    assertTrue(list.add(7L));
    assertEquals(values2.length + 2, list.size());
    assertArrayEquals(Stream.concat(Arrays.stream(values2), Stream.of(3L, 7L)).toArray(),
        list.stream().toArray());
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeIndex(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeIndex(list.size()));
    assertThrows(IndexOutOfBoundsException.class, () -> list.remove(list.size()));
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeRange(-1, 0));
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeRange(0, list.size() + 1));
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeRange(-1, list.size() + 1));
    assertThrows(IllegalArgumentException.class, () -> list.removeRange(3, 2));
    list.removeRange(2, 2);
    assertArrayEquals(Stream.concat(Arrays.stream(values2), Stream.of(3L, 7L)).toArray(),
        list.stream().toArray());
    assertEquals(val + 2, list.remove(0));
    assertEquals(values2.length + 1, list.size());
    assertArrayEquals(
        Stream.concat(Arrays.stream(values2, 1, values2.length), Stream.of(3L, 7L)).toArray(),
        list.stream().toArray());
    list.removeRange(list.size() - 2, list.size());
    assertEquals(values2.length - 1, list.size());
    assertArrayEquals(Arrays.stream(values2, 1, values2.length).toArray(), list.stream().toArray());
    list.clear();
    assertTrue(list.isEmpty());
    for (Object add : values2) {
      list.add((Long) add);
    }
    assertArrayEquals(values2, list.stream().toArray());
    for (int i = 0; i < 200; ++i) {
      list.addLong(i + 15L);
    }
    assertEquals(values2.length + 200, list.size());
    assertNotNull(list.toString());
    assertArrayEquals(Stream
        .concat(Arrays.stream(values2), IntStream.range(0, 200).mapToLong(x -> x + 15L).boxed())
        .toArray(), list.stream().toArray());
    assertThrows(IllegalArgumentException.class, () -> list.ensureCapacity(100));
    list.setSize(0L, 100 + values2.length);
    assertArrayEquals(Stream
        .concat(Arrays.stream(values2), IntStream.range(0, 100).mapToLong(x -> x + 15L).boxed())
        .toArray(), list.stream().toArray());
    assertNotNull(list.toString());
    list.setSize(10L, 101 + values2.length);
    assertArrayEquals(
        Stream.concat(Stream.concat(Arrays.stream(values2),
            IntStream.range(0, 100).mapToLong(x -> x + 15L).boxed()), Stream.of(10L)).toArray(),
        list.stream().toArray());
    list.setSize(10L, 100 + values2.length);
    assertArrayEquals(Stream
        .concat(Arrays.stream(values2), IntStream.range(0, 100).mapToLong(x -> x + 15L).boxed())
        .toArray(), list.stream().toArray());
    assertTrue(list.arrayQuick().length >= 100 + values2.length);
    list.ensureCapacity(100 + values2.length);
    assertEquals(values2.length + 100, list.arrayQuick().length);
    list.setSize(35L, 200 + values2.length);
    assertArrayEquals(Stream.concat(
        Stream.concat(Arrays.stream(values2),
            IntStream.range(0, 100).mapToLong(x -> x + 15L).boxed()),
        IntStream.range(0, 100).mapToLong(x -> 35L).boxed()).toArray(), list.stream().toArray());
    list.setSize(35L, 100 + values2.length);
    assertArrayEquals(LongStream.concat(Arrays.stream(values2).mapToLong(x -> (Long) x),
        IntStream.range(0, 100).mapToLong(x -> x + 15L)).toArray(), list.array());

    list.clear();
    assertTrue(list.isEmpty());
    assertEquals(0, list.size());
    list.addLong(val + 5);
    list.addLong(val + 2);
    assertEquals(val + 5, list.arrayQuick()[0]);
    assertEquals(val + 2, list.arrayQuick()[1]);
    assertArrayEquals(new long[] { val + 5, val + 2 }, list.array());
    assertArrayEquals(new long[] { val + 5, val + 2 }, list.array());

    assertTrue(list.equals(list));
    assertFalse(list.equals(Arrays.asList(val + 5, val + 2)));
    assertNotEquals(list, Mappings.newLongListFrom(val + 2, val + 5));
    assertEquals(list, Mappings.newLongListFrom(val + 5, val + 2));
    assertNotEquals(list, Mappings.newLongListFrom(val + 5));
    assertNotEquals(list, Mappings.newLongListFrom(val + 5, val + 2, 0L));
  }

  private void checkLongList(ConstMapping<?> list, Object[] values) {
    checkLongListSpecific(checkLongMapping(list, PrimitiveList.OfLong.class, values), values);
  }

  @SuppressWarnings("deprecation")
  private void checkDoubleListSpecific(PrimitiveList.OfDouble list, Object[] values) {
    Object[] values2 = Arrays.copyOf(values, values.length);
    if (values.length == 0) {
      assertTrue(list.isEmpty());
      assertEquals(0, list.size());
      values2 = new Object[] { 0.5 };
      list.addDouble(0.5);
    }
    assertFalse(list.isEmpty());
    assertArrayEquals(values2, list.stream().toArray());

    assertThrows(IndexOutOfBoundsException.class, () -> list.setDouble(-1, 0.0));
    assertThrows(IndexOutOfBoundsException.class, () -> list.setDouble(list.size(), 0.0));
    assertThrows(IndexOutOfBoundsException.class, () -> list.set(-1, 0.0));
    assertThrows(IndexOutOfBoundsException.class, () -> list.set(list.size(), 0.0));
    Double val = (Double) values2[0];
    assertEquals(val, list.setDouble(0, val + 1));
    values2[0] = val + 1;
    assertArrayEquals(values2, list.stream().toArray());
    assertEquals(val + 1, list.getDouble(0));
    assertEquals(val + 1, list.get(0));
    assertEquals(val + 1, list.set(0, val + 2));
    values2[0] = val + 2;
    assertArrayEquals(values2, list.stream().toArray());
    assertEquals(val + 2, list.getDouble(0));
    assertEquals(val + 2, list.get(0));

    list.addDouble(2.5);
    assertTrue(list.add(6.5));
    assertEquals(values2.length + 2, list.size());
    assertArrayEquals(Stream.concat(Arrays.stream(values2), Stream.of(2.5, 6.5)).toArray(),
        list.stream().toArray());
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeIndex(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeIndex(list.size()));
    assertThrows(IndexOutOfBoundsException.class, () -> list.remove(list.size()));
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeRange(-1, 0));
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeRange(0, list.size() + 1));
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeRange(-1, list.size() + 1));
    assertThrows(IllegalArgumentException.class, () -> list.removeRange(3, 2));
    list.removeRange(2, 2);
    assertArrayEquals(Stream.concat(Arrays.stream(values2), Stream.of(2.5, 6.5)).toArray(),
        list.stream().toArray());
    assertEquals(val + 2, list.remove(0));
    assertEquals(values2.length + 1, list.size());
    assertArrayEquals(
        Stream.concat(Arrays.stream(values2, 1, values2.length), Stream.of(2.5, 6.5)).toArray(),
        list.stream().toArray());
    list.removeRange(list.size() - 2, list.size());
    assertEquals(values2.length - 1, list.size());
    assertArrayEquals(Arrays.stream(values2, 1, values2.length).toArray(), list.stream().toArray());
    list.clear();
    assertTrue(list.isEmpty());
    for (Object add : values2) {
      list.add((Double) add);
    }
    assertArrayEquals(values2, list.stream().toArray());
    for (int i = 0; i < 200; ++i) {
      list.addDouble(i + 0.5);
    }
    assertEquals(values2.length + 200, list.size());
    assertArrayEquals(
        Stream.concat(Arrays.stream(values2),
            IntStream.range(0, 200).mapToDouble(x -> x + 0.5).boxed()).toArray(),
        list.stream().toArray());
    assertNotNull(list.toString());
    assertThrows(IllegalArgumentException.class, () -> list.ensureCapacity(100));
    list.setSize(0.0, 100 + values2.length);
    assertArrayEquals(
        Stream.concat(Arrays.stream(values2),
            IntStream.range(0, 100).mapToDouble(x -> x + 0.5).boxed()).toArray(),
        list.stream().toArray());
    assertNotNull(list.toString());
    list.setSize(10.0, 101 + values2.length);
    assertArrayEquals(
        Stream
            .concat(Stream.concat(Arrays.stream(values2),
                IntStream.range(0, 100).mapToDouble(x -> x + 0.5).boxed()), Stream.of(10.0))
            .toArray(),
        list.stream().toArray());
    list.setSize(10.0, 100 + values2.length);
    assertArrayEquals(
        Stream.concat(Arrays.stream(values2),
            IntStream.range(0, 100).mapToDouble(x -> x + 0.5).boxed()).toArray(),
        list.stream().toArray());
    assertTrue(list.arrayQuick().length >= 100 + values2.length);
    list.ensureCapacity(100 + values2.length);
    assertEquals(values2.length + 100, list.arrayQuick().length);
    list.setSize(3.5, 200 + values2.length);
    assertArrayEquals(Stream.concat(
        Stream.concat(Arrays.stream(values2),
            IntStream.range(0, 100).mapToDouble(x -> x + 0.5).boxed()),
        IntStream.range(0, 100).mapToDouble(x -> 3.5).boxed()).toArray(), list.stream().toArray());
    list.setSize(3.5, 100 + values2.length);
    assertArrayEquals(DoubleStream.concat(Arrays.stream(values2).mapToDouble(x -> (Double) x),
        IntStream.range(0, 100).mapToDouble(x -> x + 0.5)).toArray(), list.array());

    list.clear();
    assertTrue(list.isEmpty());
    assertEquals(0, list.size());
    list.addDouble(val + 5);
    list.addDouble(val + 2);
    assertEquals(val + 5, list.arrayQuick()[0]);
    assertEquals(val + 2, list.arrayQuick()[1]);
    assertArrayEquals(new double[] { val + 5, val + 2 }, list.array());
    assertArrayEquals(new double[] { val + 5, val + 2 }, list.array());

    assertTrue(list.equals(list));
    assertFalse(list.equals(Arrays.asList(val + 5, val + 2)));
    assertNotEquals(list, Mappings.newDoubleListFrom(val + 2, val + 5));
    assertEquals(list, Mappings.newDoubleListFrom(val + 5, val + 2));
    assertNotEquals(list, Mappings.newDoubleListFrom(val + 5));
    assertNotEquals(list, Mappings.newDoubleListFrom(val + 5, val + 2, 0.0));
  }

  private void checkDoubleList(ConstMapping<?> list, Object[] values) {
    checkDoubleListSpecific(checkDoubleMapping(list, PrimitiveList.OfDouble.class, values), values);
  }

  private void checkStringListSpecific(PrimitiveList<String> list, Object[] values) {
    Object[] values2 = Arrays.copyOf(values, values.length);
    if (values.length == 0) {
      assertTrue(list.isEmpty());
      assertEquals(0, list.size());
      values2 = new Object[] { "woof" };
      list.add("woof");
    }
    assertFalse(list.isEmpty());
    assertArrayEquals(values2, list.stream().toArray());

    assertThrows(IndexOutOfBoundsException.class, () -> list.set(-1, "bah"));
    assertThrows(IndexOutOfBoundsException.class, () -> list.set(list.size(), "bah"));
    String val = (String) values2[0];
    assertEquals(val, list.set(0, val + 1));
    values2[0] = val + 1;
    assertArrayEquals(values2, list.stream().toArray());
    assertEquals(val + 1, list.get(0));
    assertEquals(val + 1, list.set(0, val + 2));
    values2[0] = val + 2;
    assertArrayEquals(values2, list.stream().toArray());
    assertEquals(val + 2, list.get(0));

    list.add("neigh");
    assertTrue(list.add("ribbit"));
    assertEquals(values2.length + 2, list.size());
    assertArrayEquals(Stream.concat(Arrays.stream(values2), Stream.of("neigh", "ribbit")).toArray(),
        list.stream().toArray());
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeIndex(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeIndex(list.size()));
    assertThrows(IndexOutOfBoundsException.class, () -> list.remove(list.size()));
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeRange(-1, 0));
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeRange(0, list.size() + 1));
    assertThrows(IndexOutOfBoundsException.class, () -> list.removeRange(-1, list.size() + 1));
    assertThrows(IllegalArgumentException.class, () -> list.removeRange(3, 2));
    list.removeRange(2, 2);
    assertArrayEquals(Stream.concat(Arrays.stream(values2), Stream.of("neigh", "ribbit")).toArray(),
        list.stream().toArray());
    assertEquals(val + 2, list.remove(0));
    assertEquals(values2.length + 1, list.size());
    assertArrayEquals(Stream
        .concat(Arrays.stream(values2, 1, values2.length), Stream.of("neigh", "ribbit")).toArray(),
        list.stream().toArray());
    list.removeRange(list.size() - 2, list.size());
    assertEquals(values2.length - 1, list.size());
    assertArrayEquals(Arrays.stream(values2, 1, values2.length).toArray(), list.stream().toArray());
    list.clear();
    assertTrue(list.isEmpty());
    for (Object add : values2) {
      list.add((String) add);
    }
    assertArrayEquals(values2, list.stream().toArray());
    for (int i = 0; i < 200; ++i) {
      list.add(String.valueOf(i + 0.5));
    }
    assertEquals(values2.length + 200, list.size());
    assertNotNull(list.toString());
    assertArrayEquals(
        Stream.concat(Arrays.stream(values2),
            IntStream.range(0, 200).mapToObj(x -> String.valueOf(x + 0.5))).toArray(),
        list.stream().toArray());
    assertThrows(IllegalArgumentException.class, () -> list.ensureCapacity(100));
    list.setSize("bah", 100 + values2.length);
    assertNotNull(list.toString());
    assertArrayEquals(
        Stream.concat(Arrays.stream(values2),
            IntStream.range(0, 100).mapToObj(x -> String.valueOf(x + 0.5))).toArray(),
        list.stream().toArray());
    list.setSize("neigh", 101 + values2.length);
    assertArrayEquals(Stream
        .concat(Stream.concat(Arrays.stream(values2),
            IntStream.range(0, 100).mapToObj(x -> String.valueOf(x + 0.5))), Stream.of("neigh"))
        .toArray(), list.stream().toArray());
    list.setSize("neigh", 100 + values2.length);
    assertArrayEquals(
        Stream.concat(Arrays.stream(values2),
            IntStream.range(0, 100).mapToObj(x -> String.valueOf(x + 0.5))).toArray(),
        list.stream().toArray());
    list.ensureCapacity(100 + values2.length);
    list.setSize("bah", 200 + values2.length);
    assertArrayEquals(Stream.concat(
        Stream.concat(Arrays.stream(values2),
            IntStream.range(0, 100).mapToObj(x -> String.valueOf(x + 0.5))),
        IntStream.range(0, 100).mapToObj(x -> "bah")).toArray(), list.stream().toArray());
    list.setSize("bah", 100 + values2.length);
    assertArrayEquals(
        Stream.concat(Arrays.stream(values2),
            IntStream.range(0, 100).mapToObj(x -> String.valueOf(x + 0.5))).toArray(),
        list.toArray());

    list.clear();
    assertTrue(list.isEmpty());
    assertEquals(0, list.size());
    list.add(val + 5);
    list.add(val + 2);
    list.add(null);
    assertArrayEquals(new String[] { val + 5, val + 2, null }, list.toArray());
    assertArrayEquals(new String[] { val + 5, val + 2, null }, list.toArray(new String[0]));
    assertArrayEquals(new String[] { val + 5, val + 2, null }, list.toArray(new Object[0]));

    assertTrue(list.equals(list));
    assertTrue(list.equals(Arrays.asList(val + 5, val + 2, null)));
    assertNotEquals(Mappings.newListFrom(String.class, val + 2, null, val + 5), list);
    assertEquals(Mappings.newListFrom(String.class, val + 5, val + 2, null), list);
    assertNotEquals(Mappings.newListFrom(String.class, val + 5, null), list);
    assertNotEquals(Mappings.newListFrom(String.class, val + 5, val + 2, null, "moo"), list);
    assertNotEquals(Mappings.newListFrom(String.class, val + 5, val + 2), list);
  }

  private void checkStringList(ConstMapping<String> mapping, Object[] values) {
    checkMapping(mapping, PrimitiveList.class, String.class, values);
    checkStringListSpecific((PrimitiveList<String>) mapping, values);
  }

  @Test
  public void testIntList() {
    checkIntList(Mappings.newIntList(), new Object[] {});
    checkIntList(Mappings.newIntList(25), new Object[] {});
    checkIntList(Mappings.newIntList(4, 5), new Object[] { 4, 4, 4, 4, 4 });
    checkIntList(Mappings.newIntList(new int[] { 1, 5, 6, 2, 3 }), new Object[] { 1, 5, 6, 2, 3 });
    checkIntList(Mappings.newIntListFrom(1, 5, 6, 2, 3), new Object[] { 1, 5, 6, 2, 3 });
    checkIntList(Mappings.newIntListOfSize(3), new Object[] { 0, 0, 0 });
    checkIntList(Mappings.newList(int.class), new Object[] {});
    checkIntList(Mappings.newList(int.class, 25), new Object[] {});
    checkIntList(Mappings.newList(int.class, 4, 5), new Object[] { 4, 4, 4, 4, 4 });
    checkIntList(Mappings.newListFromArray(int.class, new int[] { 1, 5, 6, 2, 3 }),
        new Object[] { 1, 5, 6, 2, 3 });
    checkIntList(Mappings.newListOfSize(int.class, 3), new Object[] { 0, 0, 0 });
    checkIntList(Mappings.newListAutoboxing(int.class), new Object[] {});
    checkIntList(Mappings.newListAutoboxing(int.class, 25), new Object[] {});
    checkIntList(Mappings.newListAutoboxing(int.class, 4, 5), new Object[] { 4, 4, 4, 4, 4 });
    checkIntList(Mappings.newListOfSizeAutoboxing(int.class, 3), new Object[] { 0, 0, 0 });

    checkMapping(Mappings.newList(Integer.class), PrimitiveList.class, Integer.class,
        new Object[] {});
    checkMapping(Mappings.newList(Integer.class, 25), PrimitiveList.class, Integer.class,
        new Object[] {});
    checkMapping(Mappings.newList(Integer.class, 4, 5), PrimitiveList.class, Integer.class,
        new Object[] { 4, 4, 4, 4, 4 });
    checkMapping(Mappings.newListFromArray(Integer.class, new Integer[] { 1, 5, 6, 2, 3 }),
        PrimitiveList.class, Integer.class, new Object[] { 1, 5, 6, 2, 3 });
    checkMapping(Mappings.newListOfSize(Integer.class, 3), PrimitiveList.class, Integer.class,
        new Object[] { null, null, null });
    checkMapping(Mappings.newListAutoboxing(Integer.class), PrimitiveList.class, Integer.class,
        new Object[] {});
    checkMapping(Mappings.newListAutoboxing(Integer.class, 25), PrimitiveList.class, Integer.class,
        new Object[] {});
    checkMapping(Mappings.newListAutoboxing(Integer.class, 4, 5), PrimitiveList.class,
        Integer.class, new Object[] { 4, 4, 4, 4, 4 });
    checkMapping(Mappings.newListOfSizeAutoboxing(Integer.class, 3), PrimitiveList.class,
        Integer.class, new Object[] { null, null, null });
  }

  @Test
  public void testLongList() {
    checkLongList(Mappings.newLongList(), new Object[] {});
    checkLongList(Mappings.newLongList(25), new Object[] {});
    checkLongList(Mappings.newLongList(4L, 5), new Object[] { 4L, 4L, 4L, 4L, 4L });
    checkLongList(Mappings.newLongList(new long[] { 1L, 5L, 6L, 2L, 3L }),
        new Object[] { 1L, 5L, 6L, 2L, 3L });
    checkLongList(Mappings.newLongListFrom(1, 5, 6, 2, 3), new Object[] { 1L, 5L, 6L, 2L, 3L });
    checkLongList(Mappings.newLongListOfSize(3), new Object[] { 0L, 0L, 0L });
    checkLongList(Mappings.newList(long.class), new Object[] {});
    checkLongList(Mappings.newList(long.class, 25), new Object[] {});
    checkLongList(Mappings.newList(long.class, 4L, 5), new Object[] { 4L, 4L, 4L, 4L, 4L });
    checkLongList(Mappings.newListFromArray(long.class, new long[] { 1L, 5L, 6L, 2L, 3L }),
        new Object[] { 1L, 5L, 6L, 2L, 3L });
    checkLongList(Mappings.newListOfSize(long.class, 3), new Object[] { 0L, 0L, 0L });
    checkLongList(Mappings.newListAutoboxing(long.class), new Object[] {});
    checkLongList(Mappings.newListAutoboxing(long.class, 25), new Object[] {});
    checkLongList(Mappings.newListAutoboxing(long.class, 4L, 5),
        new Object[] { 4L, 4L, 4L, 4L, 4L });
    checkLongList(Mappings.newListOfSizeAutoboxing(long.class, 3), new Object[] { 0L, 0L, 0L });

    checkMapping(Mappings.newList(Long.class), PrimitiveList.class, Long.class, new Object[] {});
    checkMapping(Mappings.newList(Long.class, 25), PrimitiveList.class, Long.class,
        new Object[] {});
    checkMapping(Mappings.newList(Long.class, 4L, 5), PrimitiveList.class, Long.class,
        new Object[] { 4L, 4L, 4L, 4L, 4L });
    checkMapping(Mappings.newListFromArray(Long.class, new Long[] { 1L, 5L, 6L, 2L, 3L }),
        PrimitiveList.class, Long.class, new Object[] { 1L, 5L, 6L, 2L, 3L });
    checkMapping(Mappings.newListOfSize(Long.class, 3), PrimitiveList.class, Long.class,
        new Object[] { null, null, null });
    checkMapping(Mappings.newListAutoboxing(Long.class), PrimitiveList.class, Long.class,
        new Object[] {});
    checkMapping(Mappings.newListAutoboxing(Long.class, 25), PrimitiveList.class, Long.class,
        new Object[] {});
    checkMapping(Mappings.newListAutoboxing(Long.class, 4L, 5), PrimitiveList.class, Long.class,
        new Object[] { 4L, 4L, 4L, 4L, 4L });
    checkMapping(Mappings.newListOfSizeAutoboxing(Long.class, 3), PrimitiveList.class, Long.class,
        new Object[] { null, null, null });
  }

  @Test
  public void testDoubleList() {
    checkDoubleList(Mappings.newDoubleList(), new Object[] {});
    checkDoubleList(Mappings.newDoubleList(25), new Object[] {});
    checkDoubleList(Mappings.newDoubleList(4., 5), new Object[] { 4., 4., 4., 4., 4. });
    checkDoubleList(Mappings.newDoubleList(new double[] { 1., 5., 6., 2., 3. }),
        new Object[] { 1., 5., 6., 2., 3. });
    checkDoubleList(Mappings.newDoubleListFrom(1, 5, 6, 2, 3), new Object[] { 1., 5., 6., 2., 3. });
    checkDoubleList(Mappings.newDoubleListOfSize(3), new Object[] { 0., 0., 0. });
    checkDoubleList(Mappings.newList(double.class), new Object[] {});
    checkDoubleList(Mappings.newList(double.class, 25), new Object[] {});
    checkDoubleList(Mappings.newList(double.class, 4., 5), new Object[] { 4., 4., 4., 4., 4. });
    checkDoubleList(Mappings.newListFromArray(double.class, new double[] { 1., 5., 6., 2., 3. }),
        new Object[] { 1., 5., 6., 2., 3. });
    checkDoubleList(Mappings.newListOfSize(double.class, 3), new Object[] { 0., 0., 0. });
    checkDoubleList(Mappings.newListAutoboxing(double.class), new Object[] {});
    checkDoubleList(Mappings.newListAutoboxing(double.class, 25), new Object[] {});
    checkDoubleList(Mappings.newListAutoboxing(double.class, 4., 5),
        new Object[] { 4., 4., 4., 4., 4. });
    checkDoubleList(Mappings.newListOfSizeAutoboxing(double.class, 3), new Object[] { 0., 0., 0. });

    checkMapping(Mappings.newList(Double.class), PrimitiveList.class, Double.class,
        new Object[] {});
    checkMapping(Mappings.newList(Double.class, 25), PrimitiveList.class, Double.class,
        new Object[] {});
    checkMapping(Mappings.newList(Double.class, 4., 5), PrimitiveList.class, Double.class,
        new Object[] { 4., 4., 4., 4., 4. });
    checkMapping(Mappings.newListFromArray(Double.class, new Double[] { 1., 5., 6., 2., 3. }),
        PrimitiveList.class, Double.class, new Object[] { 1., 5., 6., 2., 3. });
    checkMapping(Mappings.newListOfSize(Double.class, 3), PrimitiveList.class, Double.class,
        new Object[] { null, null, null });
    checkMapping(Mappings.newListAutoboxing(Double.class), PrimitiveList.class, Double.class,
        new Object[] {});
    checkMapping(Mappings.newListAutoboxing(Double.class, 25), PrimitiveList.class, Double.class,
        new Object[] {});
    checkMapping(Mappings.newListAutoboxing(Double.class, 4., 5), PrimitiveList.class, Double.class,
        new Object[] { 4., 4., 4., 4., 4. });
    checkMapping(Mappings.newListOfSizeAutoboxing(Double.class, 3), PrimitiveList.class,
        Double.class, new Object[] { null, null, null });
  }

  @SuppressWarnings({ "unlikely-arg-type", "unchecked" })
  @Test
  public void testList() {

    checkStringList(Mappings.newList(String.class), new Object[] {});
    checkStringList(Mappings.newList(String.class, 25), new Object[] {});
    checkStringList(Mappings.newList(String.class, "meow", 5),
        new Object[] { "meow", "meow", "meow", "meow", "meow" });
    checkStringList(
        Mappings.newListFrom(String.class, "bark", "moo", "meow", "quack", null, "bark"),
        new Object[] { "bark", "moo", "meow", "quack", null, "bark" });
    checkStringList(
        (ConstMapping<String>) (ConstMapping<?>) Mappings.newList(
            (Class<Object>) (Class<?>) String.class,
            new Object[] { "bark", "moo", "meow", "quack", "bark" }),
        new Object[] { "bark", "moo", "meow", "quack", "bark" });
    checkStringList(
        Mappings.newListFromArray(String.class,
            new String[] { "bark", "moo", "meow", "quack", "bark" }),
        new Object[] { "bark", "moo", "meow", "quack", "bark" });
    checkStringList(Mappings.newListOfSize(String.class, 3), new Object[] { null, null, null });
    checkStringList(Mappings.newListAutoboxing(String.class), new Object[] {});
    checkStringList(Mappings.newListAutoboxing(String.class, 25), new Object[] {});
    checkStringList(Mappings.newListAutoboxing(String.class, "meow", 5),
        new Object[] { "meow", "meow", "meow", "meow", "meow" });
    checkStringList(Mappings.newListOfSizeAutoboxing(String.class, 4),
        new Object[] { null, null, null, null });
    assertFalse(Mappings.newList(String.class).equals(Mappings.newIntList()));
    assertFalse(Mappings.newList(String.class).equals(Collections.EMPTY_SET));
  }

  @Test
  public void testNewListErrorOnUnboxing() {
    assertThrows(UnsupportedOperationException.class, () -> Mappings.newList(short.class));
    assertThrows(UnsupportedOperationException.class, () -> Mappings.newList(short.class, 20));
    assertThrows(UnsupportedOperationException.class,
        () -> Mappings.newList(short.class, (short) 4, (short) 5));
    assertThrows(IllegalArgumentException.class,
        () -> Mappings.newList(short.class, new Short[] { (short) 3, (short) 5 }));
    assertThrows(UnsupportedOperationException.class, () -> Mappings.newListOfSize(short.class, 5));
    assertThrows(UnsupportedOperationException.class,
        () -> Mappings.newListFromArray(short.class, new short[] { (short) 3, (short) 5 }));
    assertThrows(IllegalArgumentException.class,
        () -> Mappings.newListFrom(short.class, (short) 3, (short) 5));
  }

  @Test
  public void testNewListErrorOnIntArray() {
    assertThrows(IllegalArgumentException.class,
        () -> Mappings.newList(int.class, new Integer[] { 3, 5 }));
  }

  @Test
  public void testNewListAutoboxing() {

    checkMapping(Mappings.newListAutoboxing(short.class), PrimitiveList.class, Short.class,
        new Object[] {});
    checkMapping(Mappings.newListAutoboxing(short.class, 25), PrimitiveList.class, Short.class,
        new Object[] {});
    checkMapping(Mappings.newListAutoboxing(short.class, (short) 4, 5), PrimitiveList.class,
        Short.class, new Object[] { (short) 4, (short) 4, (short) 4, (short) 4, (short) 4 });
    checkMapping(Mappings.newListOfSizeAutoboxing(short.class, 3), PrimitiveList.class, Short.class,
        new Object[] { null, null, null });
  }

  @Test
  public void testIntRange() {
    ConstMapping.OfInt rangeMap = checkIntMapping(Mappings.intRange(3, 8), ConstMapping.OfInt.class,
        new Object[] { 3, 4, 5, 6, 7 });
    assertFalse(rangeMap instanceof Mapping<?>);

    checkIntMapping(Mappings.intRange(8, 3), ConstMapping.OfInt.class,
        new Object[] { 8, 7, 6, 5, 4 });
  }

  @Test
  public void testRepeated() {
    ConstMapping.OfInt mapping = checkIntMapping(Mappings.repeated(14, 6), ConstMapping.OfInt.class,
        new Object[] { 14, 14, 14, 14, 14, 14 });
    assertFalse(mapping instanceof Mapping<?>);
    PrimitiveIterator.OfInt iterator = mapping.iterator();
    for (int i = 0; i < 6; ++i) {
      assertTrue(iterator.hasNext());
      iterator.next();
    }
    assertFalse(iterator.hasNext());
    assertThrows(NoSuchElementException.class, () -> iterator.next());
  }

  @SuppressWarnings("unlikely-arg-type")
  @Test
  public void testArrayMappingViews() {
    ConstMapping.OfInt unmodifiableMapping = checkIntMapping(
        Mappings.wrapUnmodifiableInt(new int[] { 1, 5, 6, 2, 3 }),
        ConstMapping.OfInt.class, new Object[] { 1, 5, 6, 2, 3 });
    ConstMapping.OfInt unmodifiableMapping2 = checkIntMapping(
        Mappings.wrapUnmodifiable(new int[] { 3, 1, 5, 6, 2, 3, 7, 9 }, 1, 6),
        ConstMapping.OfInt.class, new Object[] { 1, 5, 6, 2, 3 });
    assertEquals(unmodifiableMapping, unmodifiableMapping2);
    int[] modifiableArray = new int[] { 1, 5, 6, 2, 3 };
    Mapping.OfInt modifiableIntMapping = Mappings.wrapModifiableInt(modifiableArray);
    assertEquals(unmodifiableMapping, modifiableIntMapping);
    assertEquals(unmodifiableMapping, unmodifiableMapping);
    assertFalse(unmodifiableMapping.equals(Mappings.newListFrom(Integer.class, 1, 5, 6, 2, 3)));
    modifiableIntMapping.setInt(2, 7);
    assertNotEquals(unmodifiableMapping, modifiableIntMapping);
    checkIntMapping(modifiableIntMapping, Mapping.OfInt.class, new Object[] { 1, 5, 7, 2, 3 });
    modifiableIntMapping.set(3, 5);
    checkIntMapping(modifiableIntMapping, Mapping.OfInt.class, new Object[] { 1, 5, 7, 5, 3 });
    assertThrows(IndexOutOfBoundsException.class, () -> modifiableIntMapping.setInt(5, 2));
    checkIntMapping(modifiableIntMapping, Mapping.OfInt.class, new Object[] { 1, 5, 7, 5, 3 });
    assertThrows(IndexOutOfBoundsException.class, () -> modifiableIntMapping.setInt(-1, 2));
    checkIntMapping(modifiableIntMapping, Mapping.OfInt.class, new Object[] { 1, 5, 7, 5, 3 });
    assertThrows(IndexOutOfBoundsException.class, () -> modifiableIntMapping.set(5, 2));
    checkIntMapping(modifiableIntMapping, Mapping.OfInt.class, new Object[] { 1, 5, 7, 5, 3 });
    assertThrows(IndexOutOfBoundsException.class, () -> modifiableIntMapping.set(-1, 2));
    checkIntMapping(modifiableIntMapping, Mapping.OfInt.class, new Object[] { 1, 5, 7, 5, 3 });
    assertArrayEquals(new Object[] { 1, 5, 7, 5, 3 },
        IntStream.of(modifiableArray).boxed().toArray());
    assertEquals(modifiableArray, modifiableIntMapping.array());
    assertNotEquals(unmodifiableMapping, Mappings.wrapUnmodifiableInt(new int[] { 1, 5, 6, 2 }));
    assertNotEquals(unmodifiableMapping,
        Mappings.wrapUnmodifiableInt(new int[] { 1, 5, 6, 2, 3, 4 }));
  }

  @Test
  public void testSafeCastConstMapping() {
    ConstMapping<?> map = Mappings.newIntList();
    ConstMapping<? extends Integer> resMap = Mappings.cast(Integer.class, map);
    assertEquals(map, resMap);
    ConstMapping<? extends Object> resMap2 = Mappings.cast(Object.class, map);
    assertEquals(map, resMap2);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      ConstMapping<? extends String> resMap3 = Mappings.cast(String.class, map);
    });
    ConstMapping<? extends Object> resMap4 = Mappings.cast(Number.class, map);
    assertEquals(map, resMap4);

    ConstMapping<?> map2 = Mappings.newList(Number.class);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      ConstMapping<? extends Integer> resMap5 = Mappings.cast(Integer.class, map2);
    });

    assertNull(Mappings.cast(Integer.class, (ConstMapping<?>) null));
  }

  @Test
  public void testSafeCastMapping() {
    Mapping<?> map = Mappings.newIntList();
    Mapping<? extends Integer> resMap = Mappings.cast(Integer.class, map);
    assertEquals(map, resMap);
    Mapping<? extends Object> resMap2 = Mappings.cast(Object.class, map);
    assertEquals(map, resMap2);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      Mapping<? extends String> resMap3 = Mappings.cast(String.class, map);
    });
    Mapping<? extends Object> resMap4 = Mappings.cast(Number.class, map);
    assertEquals(map, resMap4);

    Mapping<?> map2 = Mappings.newList(Number.class);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      Mapping<? extends Integer> resMap5 = Mappings.cast(Integer.class, map2);
    });

    assertNull(Mappings.cast(Integer.class, (Mapping<?>) null));
  }

  @Test
  public void testSafeCastList() {
    PrimitiveList<?> map = Mappings.newIntList();
    PrimitiveList<? extends Integer> resMap = Mappings.cast(Integer.class, map);
    assertEquals(map, resMap);
    PrimitiveList<? extends Object> resMap2 = Mappings.cast(Object.class, map);
    assertEquals(map, resMap2);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      PrimitiveList<? extends String> resMap3 = Mappings.cast(String.class, map);
    });
    PrimitiveList<? extends Object> resMap4 = Mappings.cast(Number.class, map);
    assertEquals(map, resMap4);

    PrimitiveList<?> map2 = Mappings.newList(Number.class);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      PrimitiveList<? extends Integer> resMap5 = Mappings.cast(Integer.class, map2);
    });

    assertNull(Mappings.cast(Integer.class, (PrimitiveList<?>) null));
  }

  @Test
  public void testSafeExactCastConstMapping() {
    ConstMapping<?> map = Mappings.newIntList();
    ConstMapping<Integer> resMap = Mappings.castExact(Integer.class, map);
    assertEquals(map, resMap);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      ConstMapping<Number> resMap2 = Mappings.castExact(Number.class, map);
    });
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      ConstMapping<String> resMap3 = Mappings.castExact(String.class, map);
    });
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      ConstMapping<Double> resMap4 = Mappings.castExact(Double.class, map);
    });
    ConstMapping<?> map2 = Mappings.newList(Number.class);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      ConstMapping<Integer> resMap5 = Mappings.castExact(Integer.class, map2);
    });
    assertNull(Mappings.castExact(Integer.class, (ConstMapping<?>) null));
  }

  @Test
  public void testSafeExactCastMapping() {
    Mapping<?> map = Mappings.newIntList();
    Mapping<Integer> resMap = Mappings.castExact(Integer.class, map);
    assertEquals(map, resMap);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      Mapping<Number> resMap2 = Mappings.castExact(Number.class, map);
    });
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      Mapping<String> resMap3 = Mappings.castExact(String.class, map);
    });
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      Mapping<Double> resMap4 = Mappings.castExact(Double.class, map);
    });
    Mapping<?> map2 = Mappings.newList(Number.class);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      Mapping<Integer> resMap5 = Mappings.castExact(Integer.class, map2);
    });
    assertNull(Mappings.castExact(Integer.class, (Mapping<?>) null));
  }

  @Test
  public void testSafeExactCastList() {
    PrimitiveList<?> map = Mappings.newIntList();
    PrimitiveList<Integer> resMap = Mappings.castExact(Integer.class, map);
    assertEquals(map, resMap);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      PrimitiveList<Number> resMap2 = Mappings.castExact(Number.class, map);
    });
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      PrimitiveList<String> resMap3 = Mappings.castExact(String.class, map);
    });
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      PrimitiveList<Double> resMap4 = Mappings.castExact(Double.class, map);
    });
    PrimitiveList<?> map2 = Mappings.newList(Number.class);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      PrimitiveList<Integer> resMap5 = Mappings.castExact(Integer.class, map2);
    });
    assertNull(Mappings.castExact(Integer.class, (PrimitiveList<?>) null));
  }

  @Test
  public void testSafeSuperCastConstMapping() {
    ConstMapping<?> map = Mappings.newIntList();
    ConstMapping<? super Integer> resMap = Mappings.castSuper(Integer.class, map);
    assertEquals(map, resMap);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      ConstMapping<? super Number> resMap2 = Mappings.castSuper(Number.class, map);
    });
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      ConstMapping<? super String> resMap3 = Mappings.castSuper(String.class, map);
    });
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      ConstMapping<? super Double> resMap4 = Mappings.castSuper(Double.class, map);
    });
    ConstMapping<?> map2 = Mappings.newList(Number.class);
    ConstMapping<? super Integer> resMap5 = Mappings.castSuper(Integer.class, map2);
    assertEquals(map2, resMap5);
    ConstMapping<? super Double> resMap6 = Mappings.castSuper(Double.class, map2);
    assertEquals(map2, resMap6);
    ConstMapping<? super Number> resMap7 = Mappings.castSuper(Number.class, map2);
    assertEquals(map2, resMap7);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      ConstMapping<? super Object> resMap8 = Mappings.castSuper(Object.class, map);
    });
    assertNull(Mappings.castSuper(Integer.class, (ConstMapping<?>) null));
  }

  @Test
  public void testSafeSuperCastMapping() {
    Mapping<?> map = Mappings.newIntList();
    Mapping<? super Integer> resMap = Mappings.castSuper(Integer.class, map);
    assertEquals(map, resMap);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      Mapping<? super Number> resMap2 = Mappings.castSuper(Number.class, map);
    });
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      Mapping<? super String> resMap3 = Mappings.castSuper(String.class, map);
    });
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      Mapping<? super Double> resMap4 = Mappings.castSuper(Double.class, map);
    });
    Mapping<?> map2 = Mappings.newList(Number.class);
    Mapping<? super Integer> resMap5 = Mappings.castSuper(Integer.class, map2);
    assertEquals(map2, resMap5);
    Mapping<? super Double> resMap6 = Mappings.castSuper(Double.class, map2);
    assertEquals(map2, resMap6);
    Mapping<? super Number> resMap7 = Mappings.castSuper(Number.class, map2);
    assertEquals(map2, resMap7);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      Mapping<? super Object> resMap8 = Mappings.castSuper(Object.class, map);
    });
    assertNull(Mappings.castSuper(Integer.class, (Mapping<?>) null));
  }

  @Test
  public void testSafeSuperCastList() {
    PrimitiveList<?> map = Mappings.newIntList();
    PrimitiveList<? super Integer> resMap = Mappings.castSuper(Integer.class, map);
    assertEquals(map, resMap);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      PrimitiveList<? super Number> resMap2 = Mappings.castSuper(Number.class, map);
    });
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      PrimitiveList<? super String> resMap3 = Mappings.castSuper(String.class, map);
    });
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      PrimitiveList<? super Double> resMap4 = Mappings.castSuper(Double.class, map);
    });
    PrimitiveList<?> map2 = Mappings.newList(Number.class);
    PrimitiveList<? super Integer> resMap5 = Mappings.castSuper(Integer.class, map2);
    assertEquals(map2, resMap5);
    PrimitiveList<? super Double> resMap6 = Mappings.castSuper(Double.class, map2);
    assertEquals(map2, resMap6);
    PrimitiveList<? super Number> resMap7 = Mappings.castSuper(Number.class, map2);
    assertEquals(map2, resMap7);
    assertThrows(ClassCastException.class, () -> {
      @SuppressWarnings("unused")
      PrimitiveList<? super Object> resMap8 = Mappings.castSuper(Object.class, map);
    });
    assertNull(Mappings.castSuper(Integer.class, (PrimitiveList<?>) null));
  }

}
