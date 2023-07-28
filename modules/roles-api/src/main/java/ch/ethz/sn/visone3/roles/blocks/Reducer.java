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

package ch.ethz.sn.visone3.roles.blocks;

/**
 * Specifies a mechanism to combine two objects into a single result object.
 * 
 * @param <T>
 *          the object type
 */
public interface Reducer<T> extends OperatorTraits {

  /**
   * Describes whether a reducer is associative; i.e., if we have inputs {@code A1, A2, A3}, then
   * {@code op(op(A1, A2), A3) = op(A1, op(A2, A3))}.
   * 
   * @return true if operator is associative, false otherwise
   */
  boolean isAssociative();

  /**
   * Describes whether a reducer is commutative; i.e., if we have inputs {@code A1, A2}, then
   * {@code op(A1, A2) = op(A2, A1)}.
   * 
   * @return true if operator is commutative, false otherwise
   */
  boolean isCommutative();

  /**
   * Combines two objects by some method into a single result.
   * 
   * @param first
   *          first object
   * @param second
   *          second object
   * @return method result
   */
  T combine(T first, T second);

  /**
   * Combines two objects by some method into a single result in a potentially lazy way.
   * 
   * @param first
   *          first object
   * @param second
   *          second object
   * @return method result
   */
  default T combineLazily(T first, T second) {
    return combine(first, second);
  }

  /**
   * Releases any internal caches.
   */
  default void releaseCache() {
  }
}
