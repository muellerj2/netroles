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

package ch.ethz.sn.visone3.roles.impl.blocks.factories.dist;

import ch.ethz.sn.visone3.roles.blocks.OperatorTraits;
import ch.ethz.sn.visone3.roles.blocks.builders.GenericDistanceOperatorBuilder;
import ch.ethz.sn.visone3.roles.impl.blocks.factories.IsotoneOperatorTraits;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

abstract class AbstractGenericDistanceOperatorBuilder<T, U> extends
    AbstractDistanceOperatorBuilderBase<T, U, GenericDistanceOperatorBuilder<T, U>, Function<? super U, Comparator<? super T>>, Function<? super U, PartialComparator<? super T>>, Function<? super U, BiPredicate<? super T, ? super T>>, Function<? super U, ToIntBiFunction<? super T, ? super T>>, Function<? super U, ToIntFunction<? super T>>>
    implements GenericDistanceOperatorBuilder<T, U> {

  OperatorTraits traits = new IsotoneOperatorTraits();

  @Override
  public GenericDistanceOperatorBuilder<T, U> traits(OperatorTraits traits) {
    this.traits = traits;
    return this;
  }

}
