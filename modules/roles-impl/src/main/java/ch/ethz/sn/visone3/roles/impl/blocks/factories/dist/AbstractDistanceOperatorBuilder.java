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

import ch.ethz.sn.visone3.roles.blocks.Operator;
import ch.ethz.sn.visone3.roles.blocks.builders.DistanceOperatorBuilder;
import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

abstract class AbstractDistanceOperatorBuilder<T, U> extends
    AbstractDistanceOperatorBuilderBase<T, U, DistanceOperatorBuilder<T, U>, Comparator<? super T>, PartialComparator<? super T>, BiPredicate<? super T, ? super T>, ToIntBiFunction<? super T, ? super T>, ToIntFunction<? super T>>
    implements DistanceOperatorBuilder<T, U> {

  abstract Operator<U, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator);

  abstract Operator<U, IntDistanceMatrix> makeConcrete(
      PartialComparator<? super T> comparator);

  abstract Operator<U, IntDistanceMatrix> makeConcrete(
      BiPredicate<? super T, ? super T> comparator);

  abstract Operator<U, IntDistanceMatrix> makeConcrete(ToIntFunction<? super T> penalty);

  abstract Operator<U, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator,
      ToIntFunction<? super T> penalty);

  abstract Operator<U, IntDistanceMatrix> makeConcrete(
      PartialComparator<? super T> comparator, ToIntFunction<? super T> penalty);

  abstract Operator<U, IntDistanceMatrix> makeConcrete(
      BiPredicate<? super T, ? super T> comparator, ToIntFunction<? super T> penalty);

  abstract Operator<U, IntDistanceMatrix> makeConcrete(
      ToIntBiFunction<? super T, ? super T> substitutionCost);

  abstract Operator<U, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost);

  abstract Operator<U, IntDistanceMatrix> makeConcrete(
      PartialComparator<? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost);

  abstract Operator<U, IntDistanceMatrix> makeConcrete(
      BiPredicate<? super T, ? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost);

  @Override
  Operator<U, IntDistanceMatrix> makeConcreteWeak(Comparator<? super T> comparator) {
    return makeConcrete(comparator);
  }

  @Override
  Operator<U, IntDistanceMatrix> makeConcretePartial(
      PartialComparator<? super T> comparator) {
    return makeConcrete(comparator);
  }

  @Override
  Operator<U, IntDistanceMatrix> makeConcretePredicate(
      BiPredicate<? super T, ? super T> comparator) {
    return makeConcrete(comparator);
  }

  @Override
  Operator<U, IntDistanceMatrix> makeConcreteFail(ToIntFunction<? super T> penalty) {
    return makeConcrete(penalty);
  }

  @Override
  Operator<U, IntDistanceMatrix> makeConcreteFailWeak(Comparator<? super T> comparator,
      ToIntFunction<? super T> penalty) {
    return makeConcrete(comparator, penalty);
  }

  @Override
  Operator<U, IntDistanceMatrix> makeConcreteFailPartial(
      PartialComparator<? super T> comparator, ToIntFunction<? super T> penalty) {
    return makeConcrete(comparator, penalty);
  }

  @Override
  Operator<U, IntDistanceMatrix> makeConcreteFailPredicate(
      BiPredicate<? super T, ? super T> comparator, ToIntFunction<? super T> penalty) {
    return makeConcrete(comparator, penalty);
  }

  @Override
  Operator<U, IntDistanceMatrix> makeConcreteSubst(
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    return makeConcrete(substitutionCost);
  }

  @Override
  Operator<U, IntDistanceMatrix> makeConcreteSubstWeak(Comparator<? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    return makeConcrete(comparator, substitutionCost);
  }

  @Override
  Operator<U, IntDistanceMatrix> makeConcreteSubstPartial(
      PartialComparator<? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    return makeConcrete(comparator, substitutionCost);
  }

  @Override
  Operator<U, IntDistanceMatrix> makeConcreteSubstPredicate(
      BiPredicate<? super T, ? super T> comparator,
      ToIntBiFunction<? super T, ? super T> substitutionCost) {
    return makeConcrete(comparator, substitutionCost);
  }

}
