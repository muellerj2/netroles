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

import ch.ethz.sn.visone3.roles.blocks.OperatorTraits;
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.builders.GenericRoleOperatorBuilder;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;

abstract class AbstractGenericRoleOperatorBuilder<T, U> extends
    AbstractOperatorBuilderBase<T, RoleOperator<U>, GenericRoleOperatorBuilder<T, U>, Function<? super U, Comparator<? super T>>, Function<? super U, PartialComparator<? super T>>, Function<? super U, BiPredicate<? super T, ? super T>>>
    implements GenericRoleOperatorBuilder<T, U> {

  OperatorTraits traits = new IsotoneOperatorTraits();

  @Override
  public GenericRoleOperatorBuilder<T, U> traits(OperatorTraits traits) {
    this.traits = traits;
    return this;
  }

  abstract RoleOperator<U> makeConcrete();

  abstract RoleOperator<U> makeConcreteWeak(Function<? super U, Comparator<? super T>> comparator);

  abstract RoleOperator<U> makeConcretePartial(
      Function<? super U, PartialComparator<? super T>> comparator);

  abstract RoleOperator<U> makeConcretePred(
      Function<? super U, BiPredicate<? super T, ? super T>> comparator);

  @Override
  public RoleOperator<U> make() {
    if (weakComp != null) {
      return makeConcreteWeak(weakComp);
    } else if (partialComp != null) {
      return makeConcretePartial(partialComp);
    } else if (biPred != null) {
      return makeConcretePred(biPred);
    } else {
      return makeConcrete();
    }
  }

}