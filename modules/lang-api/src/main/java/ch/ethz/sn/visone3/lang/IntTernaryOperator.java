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
 * Represents a operation of three arguments on integers.
 */
@FunctionalInterface
public interface IntTernaryOperator {

  /**
   * Applies this operator to the given operands.
   *
   * @param first  the first operand
   * @param second the second operand
   * @param third  the third operand
   * @return the operator result
   */
  int applyAsInt(int first, int second, int third);
}
