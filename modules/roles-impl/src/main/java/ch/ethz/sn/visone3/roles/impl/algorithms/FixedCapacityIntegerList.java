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

package ch.ethz.sn.visone3.roles.impl.algorithms;

import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveIterable;

/**
 * This class implements a dynamic array of integers with a fixed maximum
 * capacity.
 *
 * The class does no explicit error checking.
 *
 * @author muellerj
 */
class FixedCapacityIntegerList implements PrimitiveIterable.OfInt {

  private final int[] data;
  private int _size;

  /**
   * Constructs a new empty dynamic array of fixed capacity.
   *
   * @param capacity
   *          the capacity of the array
   */
  public FixedCapacityIntegerList(final int capacity) {
    data = new int[capacity];
    _size = 0;
  }

  /**
   * Appends a new value at the end of the list.
   *
   * @param val
   *          value to append
   */
  public void addInt(final int val) {
    data[_size++] = val;
  }

  /**
   * Gets the value at index i in the array.
   *
   * @param i
   *          the index
   * @return the value at the specified index
   */
  public int getInt(final int i) {
    return data[i];
  }

  /**
   * Gets the size of the array.
   *
   * @return the size of the array
   */
  public int size() {
    return _size;
  }

  /**
   * Clears the array.
   */
  public void clear() {
    _size = 0;
  }

  /**
   * Gets the capacity of the array.
   *
   * @return the capacity of the array
   */
  public int capacity() {
    return data.length;
  }

  public boolean isEmpty() {
    return _size == 0;
  }

  public int poplast() {
    return data[--_size];
  }

  @Override
  public java.util.PrimitiveIterator.OfInt iterator() {
    return Mappings.wrapUnmodifiable(data, 0, _size).iterator();
  }

  public int[] arrayQuick() {
    return data;
  }

}