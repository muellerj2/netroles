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

package ch.ethz.sn.visone3.roles.impl.blocks.factories;

import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.builders.RoleOperatorBuilder;

import java.util.function.BiPredicate;

abstract class AbstractNoBiPredicateRoleOperatorBuilder<T, U> extends
    AbstractRoleOperatorBuilder<T, U> {

  @Override
  public RoleOperatorBuilder<T, U> compPredicate(BiPredicate<? super T, ? super T> comparator) {
    throw new UnsupportedOperationException("setting bi-predicates as comparators is not supported for rankings and equivalences");
  }

  @Override
  public final RoleOperator<U> makeConcrete(BiPredicate<? super T, ? super T> comparator) {
    throw new UnsupportedOperationException("setting bi-predicates as comparators is not supported for rankings and equivalences");
  }
}