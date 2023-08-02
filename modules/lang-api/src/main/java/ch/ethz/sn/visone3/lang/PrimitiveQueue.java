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
 * FiFo queue. {@link #push(Object)} items to one side, {@link #peek()} or {@link #pop()} from the
 * other.
 */
public interface PrimitiveQueue<T> {
  /**
   * Returns the number of elements in the queue.
   * 
   * @return the number of elements
   */
  int size();

  /**
   * Adds a new last element to the queue.
   * 
   * @param value
   *          the value of the new element
   */
  void push(T value);

  /**
   * Peeks at the first element in the queue without removing it.
   * 
   * @return the value of the first element
   */
  T peek();

  /**
   * Returns and removes the first element in the queue.
   * 
   * @return the value of the first element
   */
  T pop();

  /**
   * FiFo queue specifically for integers.
   */
  interface OfInt extends PrimitiveQueue<Integer> {
    /**
     * {@inheritDoc}
     * 
     * @deprecated use {@link #push(int)} in order to skip unnecessary boxing/unboxing when type is
     *             known
     */
    @Override
    default void push(final Integer value) {
      push(value.intValue());
    }

    /**
     * Adds a new last element to the queue.
     * 
     * @param value
     *          the value of the new element
     */
    void push(int value);

    /**
     * {@inheritDoc}
     * 
     * @deprecated use {@link #peekInt()} in order to skip unnecessary boxing/unboxing when type is
     *             known
     */
    @Override
    @Deprecated
    default Integer peek() {
      return peekInt();
    }

    /**
     * Peeks at the first element in the queue without removing it.
     * 
     * @return the value of the first element
     */
    int peekInt();

    /**
     * {@inheritDoc}
     * 
     * @deprecated use {@link #popInt()} in order to skip unnecessary boxing/unboxing when type is
     *             known
     */
    @Override
    @Deprecated
    default Integer pop() {
      return popInt();
    }

    /**
     * Returns and removes the first element in the queue.
     * 
     * @return the value of the first element
     */
    int popInt();
  }
}
