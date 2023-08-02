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

package ch.ethz.sn.visone3.lang.impl.containers;

import ch.ethz.sn.visone3.lang.IntDoubleHeap;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Doubly linked heap on a fixed universe.
 *
 * <ul>
 * <li>Consumes constant space in the universeSize.</li>
 * <li>Logarithmic (in number of elements) {@link #upsert(int, double)} and {@link #pop()}.</li>
 * </ul>
 */
final class FixedUniverseMinHeap implements IntDoubleHeap {
  private static final int ROOT = 1;
  private final int[] pos; // element to position in heap
  private final int[] heap; // heap position to element (1-based)
  private final double[] value; // element to value
  private int size;

  public FixedUniverseMinHeap(final int universeSize) {
    size = 0;
    pos = new int[universeSize];
    heap = new int[universeSize + 1];
    value = new double[universeSize];
    clear();
  }

  @Override
  public void upsert(final int element, final double value) {
    if (pos[element] == 0) {
      // first time we see this element
      ++size;
      pos[element] = size;
      heap[size] = element;
    }
    if (value > this.value[element]) {
      this.value[element] = value;
      shiftDown(element);
    } else if (value < this.value[element]) {
      this.value[element] = value;
      shiftUp(element);
    }
  }

  @Override
  public int peek() {
    if (size <= 0) {
      throw new NoSuchElementException();
    }
    return heap[ROOT];
  }

  @Override
  public int pop() {
    if (size <= 0) {
      throw new NoSuchElementException();
    }
    // take out root element
    final int min = heap[ROOT];
    pos[min] = 0;
    value[min] = Double.POSITIVE_INFINITY;
    if (size > 1) {
      // move bottom most element to root
      final int elmnt = heap[size];
      heap[ROOT] = elmnt;
      pos[elmnt] = ROOT;
      --size;
      // heapify
      shiftDown(elmnt);
    } else {
      // popped last element from heap
      size = 0;
    }
    return min;
  }

  @Override
  public boolean contains(final int element) {
    return pos[element] != 0;
  }

  private void shiftUp(final int element) {
    final double val = value[element];
    // shift up
    int currPos = pos[element];
    int parentPos = currPos >> 1;
    while (parentPos >= ROOT && val < value[heap[parentPos]]) {
      // move parent down
      pos[heap[parentPos]] = currPos;
      heap[currPos] = heap[parentPos];
      // move pointer up
      currPos = parentPos;
      parentPos = currPos >> 1;
    }
    pos[element] = currPos;
    heap[currPos] = element;
  }

  private void shiftDown(final int element) {
    final double val = value[element];
    int currPos = pos[element];
    int childPos = currPos << 1;
    while (childPos <= size) {
      // find smaller child
      if (childPos < size && value[heap[childPos]] > value[heap[childPos + 1]]) {
        ++childPos;
      }
      // if it is smaller than the parent
      if (val > value[heap[childPos]]) {
        // move the child upwards
        pos[heap[childPos]] = currPos;
        heap[currPos] = heap[childPos];
        // and the pointer downwards
        currPos = childPos;
        childPos = currPos << 1;
      } else {
        childPos = size + 1; // break
      }
    }
    // place the element
    pos[element] = currPos;
    heap[currPos] = element;
  }

  @Override
  public double value(final int element) {
    if (pos[element] == 0) {
      throw new NoSuchElementException("element not in heap");
    }
    return value[element];
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public void clear() {
    size = 0;
    Arrays.fill(pos, 0);
    Arrays.fill(value, Double.POSITIVE_INFINITY);
  }

  @Override
  public void upall(final double value) {
    size = pos.length;
    for (int i = 0; i < size; i++) {
      pos[i] = ROOT + i;
      heap[ROOT + i] = i;
    }
    Arrays.fill(this.value, value);
  }
}
