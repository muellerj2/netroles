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
import ch.ethz.sn.visone3.roles.blocks.factories.DistanceBuilderFactory;
import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;
import ch.ethz.sn.visone3.roles.impl.algorithms.RoleDistanceAlgorithms;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

class GenericWeakStructuralRolesDistanceFactory<U> implements DistanceBuilderFactory<U> {

  @Override
  public <T> DistanceOperatorBuilder<T, U> of(int numNodes,
      NetworkView<? extends T, ? extends T> positionView) {
    return new AbstractDistanceOperatorBuilder<T, U>() {

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete() {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, p -> 1);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator, p -> 1);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(PartialComparator<? super T> comparator) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator, p -> 1);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          BiPredicate<? super T, ? super T> comparator) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator, p -> 1);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(ToIntFunction<? super T> penalty) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, penalty);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator,
          ToIntFunction<? super T> penalty) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator, penalty);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(PartialComparator<? super T> comparator,
          ToIntFunction<? super T> penalty) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator, penalty);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(BiPredicate<? super T, ? super T> comparator,
          ToIntFunction<? super T> penalty) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator, penalty);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, substitutionCost);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator,
                substitutionCost);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(PartialComparator<? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator,
                substitutionCost);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(BiPredicate<? super T, ? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator,
                substitutionCost);
      }
    };
  }

  @Override
  public <T> DistanceOperatorBuilder<T, U> of(int numNodes,
      TransposableNetworkView<? extends T, ? extends T> positionView) {
    if (positionView instanceof NetworkView) {
      return of(numNodes, (NetworkView<? extends T, ? extends T>) positionView);
    }
    return new AbstractDistanceOperatorBuilder<T, U>() {

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete() {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, p -> 1);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator, p -> 1);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(PartialComparator<? super T> comparator) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator, p -> 1);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          BiPredicate<? super T, ? super T> comparator) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator, p -> 1);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(ToIntFunction<? super T> penalty) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, penalty);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator,
          ToIntFunction<? super T> penalty) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator, penalty);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(PartialComparator<? super T> comparator,
          ToIntFunction<? super T> penalty) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator, penalty);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(BiPredicate<? super T, ? super T> comparator,
          ToIntFunction<? super T> penalty) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator, penalty);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, substitutionCost);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator,
                substitutionCost);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(PartialComparator<? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator,
                substitutionCost);
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(BiPredicate<? super T, ? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
            .weakStructuralEquivalenceDistance(numNodes, positionView, comparator,
                substitutionCost);
      }
    };
  }

}
