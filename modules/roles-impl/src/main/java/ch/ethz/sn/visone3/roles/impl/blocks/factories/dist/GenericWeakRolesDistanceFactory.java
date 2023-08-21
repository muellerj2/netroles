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

import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

import ch.ethz.sn.visone3.roles.blocks.Operator;
import ch.ethz.sn.visone3.roles.blocks.builders.DistanceOperatorBuilder;
import ch.ethz.sn.visone3.roles.blocks.factories.VariableDistanceBuilderFactory;
import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;
import ch.ethz.sn.visone3.roles.impl.algorithms.RoleDistanceAlgorithms;
import ch.ethz.sn.visone3.roles.impl.blocks.factories.VariableBuilderFactoryBase;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

class GenericWeakRolesDistanceFactory<U>
    extends VariableBuilderFactoryBase<VariableDistanceBuilderFactory<U>>
    implements VariableDistanceBuilderFactory<U> {

  @Override
  public <T> DistanceOperatorBuilder<T, U> of(NetworkView<? extends T, ? extends T> positionView) {
    final int p = getPValue();
    final int numNodes = positionView.countNodes();
    return new AbstractDistanceOperatorBuilder<T, U>() {

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete() {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, x -> 1);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, x -> 1);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          PartialComparator<? super T> comparator) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, x -> 1);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          BiPredicate<? super T, ? super T> comparator) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, x -> 1);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(ToIntFunction<? super T> penalty) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, penalty);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, penalty);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, penalty);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator,
          ToIntFunction<? super T> penalty) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, penalty);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator, penalty);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator, penalty);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          PartialComparator<? super T> comparator, ToIntFunction<? super T> penalty) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, penalty);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator, penalty);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator, penalty);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          BiPredicate<? super T, ? super T> comparator, ToIntFunction<? super T> penalty) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, penalty);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator, penalty);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator, penalty);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, substitutionCost);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, substitutionCost);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, substitutionCost);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, substitutionCost);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator, substitutionCost);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator,
                  substitutionCost);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          PartialComparator<? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, substitutionCost);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator, substitutionCost);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator,
                  substitutionCost);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          BiPredicate<? super T, ? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, substitutionCost);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator, substitutionCost);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator,
                  substitutionCost);
        }
      }
    };
  }

  @Override
  public <T> DistanceOperatorBuilder<T, U> of(TransposableNetworkView<? extends T, ? extends T> positionView) {
    if (positionView instanceof NetworkView) {
      return of((NetworkView<? extends T, ? extends T>) positionView);
    }

    final int p = getPValue();
    final int numNodes = positionView.countNodes();
    return new AbstractDistanceOperatorBuilder<T, U>() {

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete() {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, x -> 1);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, x -> 1);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          PartialComparator<? super T> comparator) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, x -> 1);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          BiPredicate<? super T, ? super T> comparator) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, x -> 1);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(ToIntFunction<? super T> penalty) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, penalty);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, penalty);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, penalty);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator,
          ToIntFunction<? super T> penalty) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, penalty);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator, penalty);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator, penalty);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          PartialComparator<? super T> comparator, ToIntFunction<? super T> penalty) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, penalty);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator, penalty);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator, penalty);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          BiPredicate<? super T, ? super T> comparator, ToIntFunction<? super T> penalty) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, penalty);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator, penalty);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator, penalty);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, substitutionCost);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, substitutionCost);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, substitutionCost);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, substitutionCost);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator, substitutionCost);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator,
                  substitutionCost);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          PartialComparator<? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, substitutionCost);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator, substitutionCost);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator,
                  substitutionCost);
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete(
          BiPredicate<? super T, ? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        if (p >= numNodes) {
        return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakEquivalenceDistance(numNodes, positionView, comparator, substitutionCost);
        } else if (p == 1) {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .weakExactEquivalenceDistance(numNodes, positionView, comparator, substitutionCost);
        } else {
          return (ConstantDistanceOperatorBase<U>) in -> RoleDistanceAlgorithms
              .pApproximateWeakEquivalenceDistance(p, numNodes, positionView, comparator,
                  substitutionCost);
        }
      }
    };
  }

}
