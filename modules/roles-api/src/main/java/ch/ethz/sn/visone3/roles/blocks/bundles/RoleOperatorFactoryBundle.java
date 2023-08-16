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

package ch.ethz.sn.visone3.roles.blocks.bundles;

import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.factories.BasicRoleOperatorFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.GenericRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.RoleOperatorBuilderFactory;

/**
 * Bundle of factories for producing role operators according to some common
 * substitution mechanism or notion of role.
 * 
 * <p>
 * This bundle also offers a factory for generic, heavily user-customizable role
 * operators, where the user-supplied comparator can be dependent on the input
 * role structure. This greatly extends the possibilities to define customized
 * role notions that do not conform to a classical role notion when additional
 * refinements of tie substitution are independent of the input role structure.
 * 
 * @param <U> role structure type.
 * @param <V> factory type for operator builders supporting the notions of
 *            regular, equitable, weak, and weakly equitable roles.
 * @param <W> factory type for builders of generic, heavily user-customizable
 *            operators.
 * @param <X> factory type for basic operators on the role structure.
 * @param <Z> factory type for operator builders supporting the notion of weak
 *            structural roles.
 */
public interface RoleOperatorFactoryBundle<U, V extends RoleOperatorBuilderFactory<U>, //
    W extends GenericRoleOperatorBuilderFactory<U>, X extends BasicRoleOperatorFactory<U>, Z>
    extends FactoryBundle<U, U, RoleOperator<U>, V, RoleOperatorBuilderFactory<U>, Z>,
    GenericFactoryBundle<U, U, RoleOperator<U>, W> {

  /**
   * Returns a factory to construct common kinds of transformations on role structures.
   * 
   * @return the factory
   */
  X basic();
}
