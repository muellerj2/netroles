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
import ch.ethz.sn.visone3.roles.util.PartialComparator;

import java.util.Comparator;
import java.util.function.BiPredicate;

abstract class AbstractRoleOperatorBuilder<T, U> extends
    AbstractOperatorBuilderBase<T, RoleOperator<U>, RoleOperatorBuilder<T, U>, Comparator<? super T>, PartialComparator<? super T>, BiPredicate<? super T, ? super T>>
    implements RoleOperatorBuilder<T, U> {

  abstract RoleOperator<U> makeConcrete();

  abstract RoleOperator<U> makeConcrete(Comparator<? super T> comparator);

  abstract RoleOperator<U> makeConcrete(PartialComparator<? super T> comparator);

  abstract RoleOperator<U> makeConcrete(BiPredicate<? super T, ? super T> comparator);

  @Override
  public RoleOperator<U> make() {
    if (weakComp != null) {
      return makeConcrete(weakComp);
    } else if (partialComp != null) {
      return makeConcrete(partialComp);
    } else if (biPred != null) {
      return makeConcrete(biPred);
    } else {
      return makeConcrete();
    }
  }

}