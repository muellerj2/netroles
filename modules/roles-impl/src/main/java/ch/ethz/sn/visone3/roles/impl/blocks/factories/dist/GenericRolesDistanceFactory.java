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
package ch.ethz.sn.visone3.roles.impl.blocks.factories.dist;

import ch.ethz.sn.visone3.roles.blocks.Operator;
import ch.ethz.sn.visone3.roles.blocks.builders.GenericDistanceOperatorBuilder;
import ch.ethz.sn.visone3.roles.blocks.factories.GenericDistanceBuilderFactory;
import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;
import ch.ethz.sn.visone3.roles.impl.algorithms.RoleDistanceAlgorithms;
import ch.ethz.sn.visone3.roles.impl.blocks.factories.VariableBuilderFactoryBase;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

public class GenericRolesDistanceFactory<U>
    extends VariableBuilderFactoryBase<GenericDistanceBuilderFactory<U>>
    implements GenericDistanceBuilderFactory<U> {

  @Override
  public <T> GenericDistanceOperatorBuilder<T, U> of(int numNodes,
      NetworkView<? extends T, ? extends T> positionView) {
    final int p = getPValue();
    return new AbstractGenericDistanceOperatorBuilder<T, U>() {

      @Override
      Operator<U, IntDistanceMatrix> makeConcrete() {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {

            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView, x -> 1);
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {

            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView, x -> 1);
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {

            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView);
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcreteWeak(
          Function<? super U, Comparator<? super T>> comparator) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), x -> 1);
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcretePartial(
          Function<? super U, PartialComparator<? super T>> comparator) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), x -> 1);
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcretePredicate(
          Function<? super U, BiPredicate<? super T, ? super T>> comparator) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), x -> 1);
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcreteFail(
          Function<? super U, ToIntFunction<? super T>> penalty) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  penalty.apply(in));
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  penalty.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, penalty.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcreteFailWeak(
          Function<? super U, Comparator<? super T>> comparator,
          Function<? super U, ToIntFunction<? super T>> penalty) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), penalty.apply(in));
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), penalty.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in), penalty.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcreteFailPartial(
          Function<? super U, PartialComparator<? super T>> comparator,
          Function<? super U, ToIntFunction<? super T>> penalty) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), penalty.apply(in));
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), penalty.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in), penalty.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcreteFailPredicate(
          Function<? super U, BiPredicate<? super T, ? super T>> comparator,
          Function<? super U, ToIntFunction<? super T>> penalty) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), penalty.apply(in));
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), penalty.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in), penalty.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcreteSubst(
          Function<? super U, ToIntBiFunction<? super T, ? super T>> substitutionCost) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  substitutionCost.apply(in));
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  substitutionCost.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, substitutionCost.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcreteSubstWeak(
          Function<? super U, Comparator<? super T>> comparator,
          Function<? super U, ToIntBiFunction<? super T, ? super T>> substitutionCost) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), substitutionCost.apply(in));
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), substitutionCost.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in), substitutionCost.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcreteSubstPartial(
          Function<? super U, PartialComparator<? super T>> comparator,
          Function<? super U, ToIntBiFunction<? super T, ? super T>> substitutionCost) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), substitutionCost.apply(in));
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), substitutionCost.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in), substitutionCost.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcreteSubstPredicate(
          Function<? super U, BiPredicate<? super T, ? super T>> comparator,
          Function<? super U, ToIntBiFunction<? super T, ? super T>> substitutionCost) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), substitutionCost.apply(in));
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), substitutionCost.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in), substitutionCost.apply(in));
            }
          };
        }
      }
    };
  }

  @Override
  public <T> GenericDistanceOperatorBuilder<T, U> of(int numNodes,
      TransposableNetworkView<? extends T, ? extends T> positionView) {
    if (positionView instanceof NetworkView) {
      return of(numNodes, (NetworkView<? extends T, ? extends T>) positionView);
    }

    final int p = getPValue();
    return new AbstractGenericDistanceOperatorBuilder<T, U>() {

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
      Operator<U, IntDistanceMatrix> makeConcreteWeak(
          Function<? super U, Comparator<? super T>> comparator) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), x -> 1);
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcretePartial(
          Function<? super U, PartialComparator<? super T>> comparator) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), x -> 1);
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcretePredicate(
          Function<? super U, BiPredicate<? super T, ? super T>> comparator) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), x -> 1);
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcreteFail(
          Function<? super U, ToIntFunction<? super T>> penalty) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  penalty.apply(in));
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  penalty.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, penalty.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcreteFailWeak(
          Function<? super U, Comparator<? super T>> comparator,
          Function<? super U, ToIntFunction<? super T>> penalty) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), penalty.apply(in));
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), penalty.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in), penalty.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcreteFailPartial(
          Function<? super U, PartialComparator<? super T>> comparator,
          Function<? super U, ToIntFunction<? super T>> penalty) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), penalty.apply(in));
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), penalty.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in), penalty.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcreteFailPredicate(
          Function<? super U, BiPredicate<? super T, ? super T>> comparator,
          Function<? super U, ToIntFunction<? super T>> penalty) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), penalty.apply(in));
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), penalty.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in), penalty.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcreteSubst(
          Function<? super U, ToIntBiFunction<? super T, ? super T>> substitutionCost) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  substitutionCost.apply(in));
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  substitutionCost.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, substitutionCost.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcreteSubstWeak(
          Function<? super U, Comparator<? super T>> comparator,
          Function<? super U, ToIntBiFunction<? super T, ? super T>> substitutionCost) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), substitutionCost.apply(in));
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), substitutionCost.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in), substitutionCost.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcreteSubstPartial(
          Function<? super U, PartialComparator<? super T>> comparator,
          Function<? super U, ToIntBiFunction<? super T, ? super T>> substitutionCost) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), substitutionCost.apply(in));
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), substitutionCost.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in), substitutionCost.apply(in));
            }
          };
        }
      }

      @Override
      Operator<U, IntDistanceMatrix> makeConcreteSubstPredicate(
          Function<? super U, BiPredicate<? super T, ? super T>> comparator,
          Function<? super U, ToIntBiFunction<? super T, ? super T>> substitutionCost) {
        if (p >= numNodes) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), substitutionCost.apply(in));
            }
          };
        } else if (p == 1) {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.weakExactEquivalenceDistance(numNodes, positionView,
                  comparator.apply(in), substitutionCost.apply(in));
            }
          };
        } else {
          return new TraitsAdjustableOperator<U, IntDistanceMatrix>(traits) {
            @Override
            public IntDistanceMatrix apply(U in) {
              return RoleDistanceAlgorithms.pApproximateWeakEquivalenceDistance(p, numNodes,
                  positionView, comparator.apply(in), substitutionCost.apply(in));
            }
          };
        }
      }
    };
  }
}
