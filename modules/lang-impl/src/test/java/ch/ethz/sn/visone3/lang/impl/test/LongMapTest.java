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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.ethz.sn.visone3.lang.LongMap;
import ch.ethz.sn.visone3.lang.LongSet;
import ch.ethz.sn.visone3.lang.PrimitiveContainers;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Random;

public class LongMapTest {
  private static final Logger LOG = LoggerFactory.getLogger(LongMapTest.class);

  @Test
  public void testHashSet() {
    final Random rand = new Random();
    final LongSet hash = PrimitiveContainers.longHashSet();
    final int n = 100_000;
    final long[] nums = rand.longs(n).toArray();
    // add all initial
    for (int i = 0; i < n; i++) {
      final long l = nums[i];
      hash.add(l);
      assertTrue(hash.size() <= i + 1,
          String.format("size %d after %d elements", hash.size(), i + 1));
      assertTrue(hash.contains(l));
    }
    System.out.printf("size %d after %d elements%n", hash.size(), n);
    // ensure some duplicate hits
    final int h = n / 10;
    for (int i = 0; i < h; i++) {
      final long l = nums[i];
      hash.add(l);
      assertTrue(hash.contains(l));
    }
    System.out.printf("size %d after %d hits%n", hash.size(), h);
    for (final long l : nums) {
      assertTrue(hash.contains(l));
      hash.remove(l);
      assertFalse(hash.contains(l));
    }
    System.out.printf("size %d after removing%n", hash.size());
    // add again (test rehash from realy empty)
    hash.add(42);
  }

  @Test
  public void testTreeSet() {
    final Random rand = new Random();
    final LongSet hash = PrimitiveContainers.longTreeSet();
    final int n = 100_000;
    final long[] nums = rand.longs(n).toArray();
    // add all initial
    for (int i = 0; i < n; i++) {
      final long l = nums[i];
      hash.add(l);
      assertTrue(hash.size() <= i + 1,
          String.format("size %d after %d elements", hash.size(), i + 1));
      assertTrue(hash.contains(l));
    }
    System.out.printf("size %d after %d elements%n", hash.size(), n);
    // ensure some duplicate hits
    final int h = n / 10;
    for (int i = 0; i < h; i++) {
      final long l = nums[i];
      hash.add(l);
      assertTrue(hash.contains(l));
    }
    System.out.printf("size %d after %d hits%n", hash.size(), h);
    assertThrows(UnsupportedOperationException.class, () -> hash.remove(nums[0]));
  }

  @Test
  public void testHashMapDefaultCapacity() {
    final Random rand = new Random(0);
    final LongMap<Integer> hash = PrimitiveContainers.longHashMap();
    assertNotNull(hash.toString());
    final int n = 1_000;
    final long[] nums = rand.longs(n).toArray();
    // add all initial
    for (int i = 0; i < n; i++) {
      long l = nums[i] % 100;
      if (l >= 10) {
        l += 10;
      }
      LOG.info("::: put({}, {})", l, i);
      int oldSize = hash.size();
      Integer oldValue = hash.put(l, i);
      // StringBuilder sb = new StringBuilder();
      // tree.dump(sb);
      // System.out.println(sb.toString());
      assertTrue(hash.size() <= i + 1,
          String.format("size %d after %d elements", hash.size(), i + 1));
      assertTrue(oldValue != null || hash.size() == oldSize + 1);
      assertTrue(oldValue == null || oldValue < i);
      assertNotNull(hash.get(l));
      assertEquals(i, hash.get(l));
      assertEquals(i, hash.getOrDefault(l, 5000));
      assertTrue(hash.contains(l));
      assertNotNull(hash.toString());
    }
    assertFalse(hash.contains(13));
    assertFalse(hash.contains(-110));
    assertFalse(hash.contains(111));
    assertNull(hash.get(14));
    assertEquals(5000, hash.getOrDefault(16, 5000));
    assertNull(hash.remove(18));
  }

  @Test
  public void testHashMapParameterizedCapacity() {
    final Random rand = new Random(0);
    final LongMap<Integer> hash = PrimitiveContainers.longHashMap(5);
    assertNotNull(hash.toString());
    final int n = 1_000;
    final long[] nums = rand.longs(n).toArray();
    // add all initial
    for (int i = 0; i < n; i++) {
      long l = nums[i] % 100;
      if (l >= 10) {
        l += 10;
      }
      LOG.info("::: put({}, {})", l, i);
      int oldSize = hash.size();
      Integer oldValue = hash.put(l, i);
      // StringBuilder sb = new StringBuilder();
      // tree.dump(sb);
      // System.out.println(sb.toString());
      assertTrue(hash.size() <= i + 1,
          String.format("size %d after %d elements", hash.size(), i + 1));
      assertTrue(oldValue != null || hash.size() == oldSize + 1);
      assertTrue(oldValue == null || oldValue < i);
      assertNotNull(hash.get(l));
      assertEquals(i, hash.get(l));
      assertEquals(i, hash.getOrDefault(l, 5000));
      assertTrue(hash.contains(l));
      assertNotNull(hash.toString());
    }
    assertFalse(hash.contains(13));
    assertFalse(hash.contains(-110));
    assertFalse(hash.contains(111));
    assertNull(hash.get(14));
    assertEquals(5000, hash.getOrDefault(16, 5000));
    assertNull(hash.remove(18));
  }
  // @Test
  public void testPerf() {
    final Random rand = new Random(0);
    // final LongMap<Integer> hash = new LongHashMap<>();
    final LongMap<Integer> hash = PrimitiveContainers.longHashMap();
    final LongMap<Integer> tree = PrimitiveContainers.longTreeMap(64);
    final int n = 10_000;
    final int k = 1_000;
    final long[] nums = rand.longs(n).toArray();
    // add all initial
    final long time0 = System.currentTimeMillis();
    for (int i = 0; i < n; i++) {
      final long l = nums[i] % k;
      hash.put(l, i);
      hash.get(l);
    }
    final long time1 = System.currentTimeMillis();
    for (int i = 0; i < n; i++) {
      final long l = nums[i] % k;
      tree.put(l, i);
      tree.get(l);
    }
    final long time2 = System.currentTimeMillis();
    System.out.println("hash = " + Duration.ofMillis(time1 - time0));
    System.out.println("tree = " + Duration.ofMillis(time2 - time1));
  }

  @Test
  public void testTreeParameterizedMinDegree() {
    final Random rand = new Random(0);
    final LongMap<Integer> tree = PrimitiveContainers.longTreeMap(4);
    assertNotNull(tree.toString());
    final int n = 1_000;
    final long[] nums = rand.longs(n).toArray();
    // add all initial
    for (int i = 0; i < n; i++) {
      long l = nums[i] % 100;
      if (l >= 10) {
        l += 10;
      }
      LOG.info("::: put({}, {})", l, i);
      int oldSize = tree.size();
      Integer oldValue = tree.put(l, i);
      // StringBuilder sb = new StringBuilder();
      // tree.dump(sb);
      // System.out.println(sb.toString());
      assertTrue(tree.size() <= i + 1,
          String.format("size %d after %d elements", tree.size(), i + 1));
      assertTrue(oldValue != null || tree.size() == oldSize + 1);
      assertTrue(oldValue == null || oldValue < i);
      assertNotNull(tree.get(l));
      assertEquals(i, tree.get(l));
      assertEquals(i, tree.getOrDefault(l, 5000));
      assertTrue(tree.contains(l));
      assertNotNull(tree.toString());
    }
    assertFalse(tree.contains(13));
    assertFalse(tree.contains(-110));
    assertFalse(tree.contains(111));
    assertNull(tree.get(14));
    assertEquals(5000, tree.getOrDefault(16, 5000));
    assertThrows(UnsupportedOperationException.class, () -> tree.remove(18));
  }

  @Test
  public void testTreeDefaultMinDegree() {
    final Random rand = new Random(0);
    final LongMap<Integer> tree = PrimitiveContainers.longTreeMap();
    final int n = 1_000;
    final long[] nums = rand.longs(n).toArray();
    // add all initial
    for (int i = 0; i < n; i++) {
      long l = nums[i] % 100;
      if (l >= 10) {
        l += 10;
      }
      LOG.info("::: put({}, {})", l, i);
      int oldSize = tree.size();
      Integer oldValue = tree.put(l, i);
      // StringBuilder sb = new StringBuilder();
      // tree.dump(sb);
      // System.out.println(sb.toString());
      assertTrue(tree.size() <= i + 1,
          String.format("size %d after %d elements", tree.size(), i + 1));
      assertTrue(oldValue != null || tree.size() == oldSize + 1);
      assertTrue(oldValue == null || oldValue < i);
      assertNotNull(tree.get(l));
      assertEquals(i, tree.get(l));
      assertEquals(i, tree.getOrDefault(l, 5000));
      assertTrue(tree.contains(l));
      assertNotNull(tree.toString());
    }
    assertFalse(tree.contains(13));
    assertFalse(tree.contains(-110));
    assertFalse(tree.contains(111));
    assertNull(tree.get(14));
    assertEquals(5000, tree.getOrDefault(16, 5000));
    assertThrows(UnsupportedOperationException.class, () -> tree.remove(18));
  }
}
