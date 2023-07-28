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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.ethz.sn.visone3.lang.IntDoubleHeap;
import ch.ethz.sn.visone3.lang.PrimitiveContainers;
import ch.ethz.sn.visone3.lang.PrimitiveQueue;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

class IntDataStructuresTest {

  @SuppressWarnings("deprecation")
  @Test
  void testQueue() {
    PrimitiveQueue.OfInt queue = PrimitiveContainers.intQueue();
    assertThrows(NoSuchElementException.class, () -> queue.peekInt());
    assertThrows(NoSuchElementException.class, () -> queue.peek());
    assertThrows(NoSuchElementException.class, () -> queue.popInt());
    assertThrows(NoSuchElementException.class, () -> queue.pop());
    assertEquals(0, queue.size());

    queue.push(25);
    queue.push(36);
    queue.push(4);
    queue.push(10);
    assertEquals(4, queue.size());
    assertEquals(25, queue.peekInt());
    assertEquals(25, queue.popInt());
    assertEquals(3, queue.size());
    assertEquals(36, queue.popInt());
    assertEquals(4, queue.popInt());
    assertEquals(1, queue.size());

    queue.push(4);
    queue.push(80);
    queue.push(15);
    assertEquals(10, queue.popInt());
    assertEquals(4, queue.peekInt());
    assertEquals(4, queue.popInt());
    assertEquals(80, queue.popInt());
    assertEquals(1, queue.size());
    assertEquals(15, queue.popInt());
    assertEquals(0, queue.size());
    assertThrows(NoSuchElementException.class, () -> queue.peekInt());
    assertThrows(NoSuchElementException.class, () -> queue.peek());
    assertThrows(NoSuchElementException.class, () -> queue.popInt());
    assertThrows(NoSuchElementException.class, () -> queue.pop());

    queue.push(20);
    assertEquals(1, queue.size());
    assertEquals(20, queue.popInt());
  }

  @Test
  void testMinHeap() {
    IntDoubleHeap heap = PrimitiveContainers.fixedUniverseIntDoubleMinHeap(10);
    assertTrue(heap.isEmpty());
    assertEquals(0, heap.size());
    heap.upall(2.0);
    assertTrue(heap.contains(3));
    assertFalse(heap.isEmpty());
    assertEquals(10, heap.size());
    heap.upsert(4, 1.0);
    heap.upsert(7, 1.5);
    heap.upsert(8, 1.7);
    heap.upsert(5, 1.8);
    heap.upsert(3, 1.8);
    heap.upsert(8, 3.0);
    assertTrue(heap.contains(8));
    assertEquals(10, heap.size());
    assertEquals(4, heap.pop());
    heap.upsert(2, 0.0);
    heap.upsert(5, 0.5);
    assertThrows(NoSuchElementException.class, () -> heap.value(4));
    assertEquals(1.5, heap.value(7));
    int lastVal = -1;
    int count = 0;
    while (!heap.isEmpty()) {
      if (lastVal == -1) {
        assertEquals(2, heap.peek());
        assertEquals(2, heap.pop());
        assertFalse(heap.contains(4));
        assertFalse(heap.contains(2));
        assertTrue(heap.contains(7));
        assertTrue(heap.contains(8));
        lastVal = 2;
      } else if (lastVal == 2) {
        assertEquals(5, heap.pop());
        lastVal = 5;
      } else if (lastVal == 7) {
        assertEquals(3, heap.pop());
        lastVal = 3;
        heap.upsert(5, 2.2);
      } else {
        lastVal = heap.pop();
      }
      ++count;
    }
    assertEquals(10, count);
    assertEquals(8, lastVal);
    assertEquals(0, heap.size());
    assertThrows(NoSuchElementException.class, () -> heap.pop());
    assertThrows(NoSuchElementException.class, () -> heap.peek());
    assertFalse(heap.contains(3));
    heap.upsert(2, -1.0);
    heap.upsert(5, -2.0);
    heap.upsert(3, -1.5);
    heap.upsert(8, -1.3);
    double[] values = { -2.0, -1.5, -1.3, -1.0 };
    int[] elems = { 5, 3, 8, 2 };
    for (int i = 0; i < elems.length; ++i) {
      assertFalse(heap.isEmpty());
      for (int j = i; j < elems.length; ++j) {
        assertEquals(values[j], heap.value(elems[j]));
      }
      assertEquals(elems[i], heap.peek());
      assertEquals(elems[i], heap.pop());
      assertEquals(elems.length - i - 1, heap.size());
    }
  }

}
