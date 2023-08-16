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
package ch.ethz.sn.visone3.roles.blocks.factories;

import ch.ethz.sn.visone3.roles.blocks.builders.RoleOperatorBuilder;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;

/**
 * This class defines the factory interface for builders of role operators based
 * on the notion of weak structural equivalence. Since the notion of weak
 * structural equivalence is only guaranteed to produce equivalences and
 * rankings if both directions in a network are considered, the factory for weak
 * structural equivalence asks to be supplied with views on the network covering
 * both directions.
 * 
 * <p>
 * If desired, it is possible to only supply a single direction, and doing so is
 * unproblematic in the undirected case, this is unproblematic, as both
 * directions appear the same. If consideration of a single direction is not
 * sufficient, however, some post-conditions might not be satisfied (e.g., the
 * result might not be transitive).
 * 
 * @param <U> the role structure type.
 */
public interface WeakStructuralRoleOperatorBuilderFactory<U> {

  /**
   * Returns an operator builder for the weak structural role notion for the supplied two position
   * views for opposite directions.
   * 
   * <p>
   * Both directions need to be supplied in some way, as otherwise the weak structural role notion
   * does not yield transitive structures, like equivalences and rankings.
   * 
   * <p>
   * This factory method can also be called supplying only one position view and passing null for
   * the other; to yield correct results, however, this position view should represent both
   * directions in some way (which is the case, e.g., if the underlying network is undirected).
   * 
   * @param <T>
   *          common tie type
   * @param numNodes
   *          number of nodes in the underlying network
   * @param oneDirection
   *          position view in one direction
   * @param otherDirection
   *          position view in the other direction; can be null if the first position view already
   *          represents the other direction in some way
   * @return an operator builder
   */
  <T> RoleOperatorBuilder<T, U> of(int numNodes,
      NetworkView<? extends T, ? extends T> oneDirection,
      NetworkView<? extends T, ? extends T> otherDirection);

  /**
   * Returns an operator builder for the weak structural role notion for the supplied two position
   * views for opposite directions.
   * 
   * <p>
   * Both directions need to be supplied in some way, as otherwise the weak structural role notion
   * does not yield transitive structures, like equivalences and rankings.
   * 
   * <p>
   * This factory method can also be called supplying only one position view and passing null for
   * the other; to yield correct results, however, this position view should represent both
   * directions in some way (which is the case, e.g., if the underlying network is undirected).
   * 
   * @param <T>
   *          common tie type
   * @param numNodes
   *          number of nodes in the underlying network
   * @param oneDirection
   *          position view in one direction
   * @param otherDirection
   *          position view in the other direction; can be null if the first position view already
   *          represents the other direction in some way
   * @return an operator builder
   */
  <T> RoleOperatorBuilder<T, U> of(int numNodes,
      TransposableNetworkView<? extends T, ? extends T> oneDirection,
      TransposableNetworkView<? extends T, ? extends T> otherDirection);

  /**
   * Returns an instance of the usual unidirectional factory instance, which can produce operators
   * from the supply of a network view in a single direction.
   * 
   * <p>
   * Note that providing the network view only in a single direction can yield incorrect outputs
   * with broken invariants, since the result of the weak structural role notion is not necessarily
   * transitive if only one direction is supplied. This is irrelevant, though, if the network view
   * encodes both directions in some sense (e.g., if the underlying network is undirected).
   * 
   * @return the unidirectional factory
   */
  RoleOperatorBuilderFactory<U> unidirectional();
}
