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

package ch.ethz.sn.visone3.lang;

/**
 * Primitive data type heap. Objects are identified by {@code int} id.
 */
public interface IntDoubleHeap {
  /**
   * Updates an element identifier's associated value or adds the element identifier to the heap.
   * 
   * @param element
   *          the element identifier.
   * @param value
   *          the element identifier's value.
   */
  void upsert(final int element, final double value);

  /**
   * Returns the current top element of the heap.
   * 
   * @return current top element.
   */
  int peek();

  /**
   * Returns and removes the current top element of the heap.
   * 
   * @return current top element. Call {@code value(peek())} before popping if value is needed.
   */
  int pop();

  /**
   * Checks if the heap contains this element identifier.
   * 
   * @param element
   *          the element identifier.
   * @return true if the heap contains the element identifier.
   */
  boolean contains(final int element);

  /**
   * Returns the value associated with an element identifier.
   * 
   * @param element
   *          element identifier.
   * @return current value.
   */
  double value(final int element);

  /**
   * Returns the number of elements in the heap.
   * 
   * @return the number of elements.
   */
  int size();

  /**
   * Returns whether the heap contains no elements.
   * 
   * @return true if the heap contains no elements (i.e., if {@link #size()} is {@code 0}).
   */
  boolean isEmpty();

  /**
   * Removes all elements from the heap.
   */
  void clear();

  /**
   * Updates all elements' associated value.
   * 
   * @param value
   *          the associated value.
   */
  void upall(double value);
}
