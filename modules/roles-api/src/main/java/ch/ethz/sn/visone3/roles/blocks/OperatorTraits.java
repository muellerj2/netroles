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
 * Describes some basic properties of operators.
 *
 */
public interface OperatorTraits {

  /**
   * Describes whether an operator is isotone with respect to some natural ordering; i.e., if we
   * have inputs {@code A1, A2, ..., An} and {@code B1, B2, ..., Bn} such that {@code Ai <= Bi} for
   * all {@code 1 <= i <= n}, then {@code op(A1, A2, ..., An) <= op(B1, B2, ...,
   * Bn)}.
   * 
   * @return true if operator is guaranteed to be isotone, false otherwise
   */
  boolean isIsotone();

  /**
   * Describes whether an operator is non-increasing with respect to some natural ordering; i.e., if
   * we have inputs {@code A1, A2, ..., An}, then {@code op(A1, A2, ..., An) <= Ai} for all
   * {@code 1 <= i <= n}.
   * 
   * @return true if operator is guaranteed to be non-increasing, false otherwise
   */
  boolean isNonincreasing();

  /**
   * Describes whether an operator is non-decreasing with respect to some natural ordering; i.e., if
   * we have inputs {@code A1, A2, ..., An}, then {@code Ai <= op(A1, A2, ..., An} } for all
   * {@code 1 <= i <= n}.
   * 
   * @return true if operator is guaranteed to be non-decreasing, false otherwise
   */
  boolean isNondecreasing();

  /**
   * Describes whether an operator is constant, i.e., {@code op(A1, ..., An) = op(B1, ..., Bn)} for
   * all possible inputs {@code A1, A2, ..., An} and {@code B1, B2, ..., Bn}.
   * 
   * @return true if operator is constant, false otherwise
   */
  boolean isConstant();
}
