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
 * This interface describes some operator that converts/transforms an object of type {@code T} to
 * type {@code U}.
 * 
 * @param <T>
 *          the input type
 * @param <U>
 *          the result type
 */
public interface Operator<T, U> extends OperatorTraits {

  /**
   * Applies the operator to some given input.
   * 
   * @param in
   *          the input to apply the operator to
   * @return the result of applying the operator
   */
  U apply(T in);

  /**
   * Releases any internal caches.
   */
  default void releaseCache() {
  }
}
