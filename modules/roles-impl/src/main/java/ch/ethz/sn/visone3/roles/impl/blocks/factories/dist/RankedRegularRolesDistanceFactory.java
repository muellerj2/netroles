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
import ch.ethz.sn.visone3.roles.blocks.factories.VariableDistanceBuilderFactory;
import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;
import ch.ethz.sn.visone3.roles.impl.algorithms.RoleDistanceAlgorithms;
import ch.ethz.sn.visone3.roles.impl.blocks.factories.VariableBuilderFactoryBase;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

public class RankedRegularRolesDistanceFactory
    extends VariableBuilderFactoryBase<VariableDistanceBuilderFactory<Ranking>>
    implements VariableDistanceBuilderFactory<Ranking> {

  @Override
  public <T> DistanceOperatorBuilder<T, Ranking> of(int numNodes,
      NetworkView<? extends T, ? extends T> positionView) {
    final int p = getPValue();
    return new AbstractDistanceOperatorBuilder<T, Ranking>() {

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete() {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, x -> 1);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in);
        } else {

          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          Comparator<? super T> comparator) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator, x -> 1);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in,
                  comparator);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          PartialComparator<? super T> comparator) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator, x -> 1);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in,
                  comparator);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          BiPredicate<? super T, ? super T> comparator) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator, x -> 1);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in,
                  comparator);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          ToIntFunction<? super T> penalty) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, penalty);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, penalty);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in, penalty);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator,
          ToIntFunction<? super T> penalty) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator, penalty);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator, penalty);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in, comparator,
                  penalty);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          PartialComparator<? super T> comparator, ToIntFunction<? super T> penalty) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator, penalty);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator, penalty);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in, comparator,
                  penalty);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          BiPredicate<? super T, ? super T> comparator, ToIntFunction<? super T> penalty) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator, penalty);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator, penalty);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in, comparator,
                  penalty);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, substitutionCost);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, substitutionCost);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in,
                  substitutionCost);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator,
                  substitutionCost);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator,
                  substitutionCost);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in, comparator,
                  substitutionCost);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          PartialComparator<? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator,
                  substitutionCost);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator,
                  substitutionCost);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in, comparator,
                  substitutionCost);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          BiPredicate<? super T, ? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator,
                  substitutionCost);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator,
                  substitutionCost);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in, comparator,
                  substitutionCost);
        }
      }
    };
  }

  @Override
  public <T> DistanceOperatorBuilder<T, Ranking> of(int numNodes,
      TransposableNetworkView<? extends T, ? extends T> positionView) {
    if (positionView instanceof NetworkView) {
      return of(numNodes, (NetworkView<? extends T, ? extends T>) positionView);
    }

    final int p = getPValue();
    return new AbstractDistanceOperatorBuilder<T, Ranking>() {

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete() {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, x -> 1);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in);
        } else {

          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          Comparator<? super T> comparator) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator, x -> 1);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in,
                  comparator);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          PartialComparator<? super T> comparator) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator, x -> 1);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in,
                  comparator);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          BiPredicate<? super T, ? super T> comparator) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator, x -> 1);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in,
                  comparator);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          ToIntFunction<? super T> penalty) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, penalty);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, penalty);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in, penalty);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator,
          ToIntFunction<? super T> penalty) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator, penalty);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator, penalty);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in, comparator,
                  penalty);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          PartialComparator<? super T> comparator, ToIntFunction<? super T> penalty) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator, penalty);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator, penalty);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in, comparator,
                  penalty);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          BiPredicate<? super T, ? super T> comparator, ToIntFunction<? super T> penalty) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator, penalty);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator, penalty);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in, comparator,
                  penalty);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, substitutionCost);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, substitutionCost);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in,
                  substitutionCost);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(Comparator<? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator,
                  substitutionCost);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator,
                  substitutionCost);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in, comparator,
                  substitutionCost);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          PartialComparator<? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator,
                  substitutionCost);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator,
                  substitutionCost);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in, comparator,
                  substitutionCost);
        }
      }

      @Override
      Operator<Ranking, IntDistanceMatrix> makeConcrete(
          BiPredicate<? super T, ? super T> comparator,
          ToIntBiFunction<? super T, ? super T> substitutionCost) {
        if (p >= numNodes) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .regularRolesDistanceRelativeTo(numNodes, positionView, in, comparator,
                  substitutionCost);
        } else if (p == 1) {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .exactRolesDistanceRelativeTo(numNodes, positionView, in, comparator,
                  substitutionCost);
        } else {
          return (IsotoneDistanceOperatorBase<Ranking>) in -> RoleDistanceAlgorithms
              .pApproximateRegularRolesDistanceRelativeTo(p, numNodes, positionView, in, comparator,
                  substitutionCost);
        }
      }
    };
  }

}
