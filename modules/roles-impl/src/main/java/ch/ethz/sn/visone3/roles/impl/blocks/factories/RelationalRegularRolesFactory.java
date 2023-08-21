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

package ch.ethz.sn.visone3.roles.impl.blocks.factories;

import java.util.Comparator;
import java.util.function.BiPredicate;

import ch.ethz.sn.visone3.lang.Iterators;
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.builders.RoleOperatorBuilder;
import ch.ethz.sn.visone3.roles.blocks.factories.VariableRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.impl.algorithms.EquitableRankedRoles;
import ch.ethz.sn.visone3.roles.impl.algorithms.MiscRankedRoles;
import ch.ethz.sn.visone3.roles.impl.algorithms.RegularRankedRoles;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

class RelationalRegularRolesFactory
    extends VariableBuilderFactoryBase<VariableRoleOperatorBuilderFactory<BinaryRelation>>
    implements VariableRoleOperatorBuilderFactory<BinaryRelation> {

  private static class Wrapper<T> {
    Wrapper(T tie, int source, int target, int node) {
      this.tie = tie;
      this.source = source;
      this.target = target;
      this.node = node;
    }

    T tie;
    int source;
    int target;
    int node;
  }

  private <T, U> TransposableNetworkView<Wrapper<T>, Wrapper<U>> wrap(
      TransposableNetworkView<T, U> positionView) {
    return new TransposableNetworkView<Wrapper<T>, Wrapper<U>>() {

      @Override
      public int countNodes() {
        return positionView.countNodes();
      }

      @Override
      public Iterable<? extends Wrapper<T>> ties(int lhsComparison, int rhsComparison, int node) {
        return Iterators.map(positionView.ties(lhsComparison, rhsComparison, node),
            tie -> new Wrapper<>(tie, lhsComparison, rhsComparison, node));
      }

      @Override
      public int tieTarget(int lhsComparison, int rhsComparison, int node, Wrapper<T> tie) {
        return positionView.tieTarget(lhsComparison, rhsComparison, node, tie.tie);
      }

      @Override
      public int tieIndex(int lhsComparison, int rhsComparison, int node, Wrapper<T> tie) {
        return positionView.tieIndex(lhsComparison, rhsComparison, node, tie.tie);
      }

      @Override
      public int countTies(int lhsComparison, int rhsComparison, int node) {
        return positionView.countTies(lhsComparison, rhsComparison, node);
      }
    };
  }

  @Override
  public <T> RoleOperatorBuilder<T, BinaryRelation> of(NetworkView<? extends T, ? extends T> positionView) {
    final int p = getPValue();
    final int numNodes = positionView.countNodes();
    return new AbstractRoleOperatorBuilder<T, BinaryRelation>() {

      @Override
      RoleOperator<BinaryRelation> makeConcrete() {
        if (p >= numNodes) {
          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return RegularRankedRoles.rankedRegularRoles(numNodes, positionView, in);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return RegularRankedRoles.refiningRankedRegularRoles(numNodes, positionView, in,
                  toRefine);
            }

            @Override
            public BinaryRelation interior(BinaryRelation in) {
              return RegularRankedRoles.computeRankedRolesInterior(numNodes, positionView, in);
            }

            @Override
            public BinaryRelation closure(BinaryRelation in) {
              return RegularRankedRoles.computeRankedRolesClosure(numNodes, positionView, in);
            }
          };
        } else if (p == 1) {
          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return EquitableRankedRoles.rankedExactRoles(numNodes, positionView, in);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return EquitableRankedRoles.refiningRankedExactRoles(numNodes, positionView, in,
                  toRefine);
            }

          };
        } else {

          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return callWrapped(positionView, in);
            }

            private <V extends T> BinaryRelation callWrapped(
                TransposableNetworkView<V, ?> positionView, BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, wrap(positionView),
                  (ri, rj) -> in.contains(
                      positionView.tieTarget(ri.source, ri.target, ri.node, ri.tie),
                      positionView.tieTarget(rj.source, rj.target, rj.node, rj.tie)));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return callRefiningWrapped(positionView, in, toRefine);
            }

            private <V extends T> BinaryRelation callRefiningWrapped(
                TransposableNetworkView<V, ?> positionView, BinaryRelation in,
                BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, wrap(positionView),
                  toRefine, (ri, rj) -> in.contains(
                      positionView.tieTarget(ri.source, ri.target, ri.node, ri.tie),
                      positionView.tieTarget(rj.source, rj.target, rj.node, rj.tie)));
            }

          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(Comparator<? super T> comparator) {
        if (p >= numNodes) {
          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return RegularRankedRoles.rankedRegularRoles(numNodes, positionView, in, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return RegularRankedRoles.refiningRankedRegularRoles(numNodes, positionView, in,
                  toRefine, comparator);
            }

            @Override
            public BinaryRelation closure(BinaryRelation in) {
              return RegularRankedRoles.computeRankedRolesClosure(numNodes, positionView, in,
                  comparator);
            }
          };
        } else if (p == 1) {
          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return EquitableRankedRoles.rankedExactRoles(numNodes, positionView, in, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return EquitableRankedRoles.refiningRankedExactRoles(numNodes, positionView, in,
                  toRefine, comparator);
            }

          };
        } else {

          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return callWrapped(positionView, in);
            }

            private <V extends T> BinaryRelation callWrapped(
                TransposableNetworkView<V, ?> positionView, BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, wrap(positionView),
                  (ri, rj) -> in.contains(
                      positionView.tieTarget(ri.source, ri.target, ri.node, ri.tie),
                      positionView.tieTarget(rj.source, rj.target, rj.node, rj.tie))
                      && comparator.compare(ri.tie, rj.tie) <= 0);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return callRefiningWrapped(positionView, in, toRefine);
            }

            private <V extends T> BinaryRelation callRefiningWrapped(
                TransposableNetworkView<V, ?> positionView, BinaryRelation in,
                BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, wrap(positionView),
                  toRefine, (ri, rj) -> in.contains(
                      positionView.tieTarget(ri.source, ri.target, ri.node, ri.tie),
                      positionView.tieTarget(rj.source, rj.target, rj.node, rj.tie))
                      && comparator.compare(ri.tie, rj.tie) <= 0);
            }
          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(PartialComparator<? super T> comparator) {
        if (p >= numNodes) {
          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return RegularRankedRoles.rankedRegularRoles(numNodes, positionView, in, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return RegularRankedRoles.refiningRankedRegularRoles(numNodes, positionView, in,
                  toRefine, comparator);
            }
          };
        } else if (p == 1) {
          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return EquitableRankedRoles.rankedExactRoles(numNodes, positionView, in, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return EquitableRankedRoles.refiningRankedExactRoles(numNodes, positionView, in,
                  toRefine, comparator);
            }

          };
        } else {

          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return callWrapped(positionView, in);
            }

            private <V extends T> BinaryRelation callWrapped(
                TransposableNetworkView<V, ?> positionView, BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, wrap(positionView),
                  (ri, rj) -> {
                    if (!in.contains(positionView.tieTarget(ri.source, ri.target, ri.node, ri.tie),
                        positionView.tieTarget(rj.source, rj.target, rj.node, rj.tie))) {
                      return false;
                    }
                    PartialComparator.ComparisonResult result = comparator.compare(ri.tie, rj.tie);
                    return result == PartialComparator.ComparisonResult.LESS
                        || result == PartialComparator.ComparisonResult.EQUAL;
                  });
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return callRefiningWrapped(positionView, in, toRefine);
            }

            private <V extends T> BinaryRelation callRefiningWrapped(
                TransposableNetworkView<V, ?> positionView, BinaryRelation in,
                BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, wrap(positionView),
                  toRefine, (ri, rj) -> {
                    if (!in.contains(positionView.tieTarget(ri.source, ri.target, ri.node, ri.tie),
                        positionView.tieTarget(rj.source, rj.target, rj.node, rj.tie))) {
                      return false;
                    }
                    PartialComparator.ComparisonResult result = comparator.compare(ri.tie, rj.tie);
                    return result == PartialComparator.ComparisonResult.LESS
                        || result == PartialComparator.ComparisonResult.EQUAL;
                  });
            }
          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(BiPredicate<? super T, ? super T> comparator) {
        if (p >= numNodes) {
          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return RegularRankedRoles.rankedRegularRoles(numNodes, positionView, in, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return RegularRankedRoles.refiningRankedRegularRoles(numNodes, positionView, in,
                  toRefine, comparator);
            }
          };
        } else if (p == 1) {
          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return EquitableRankedRoles.rankedExactRoles(numNodes, positionView, in, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return EquitableRankedRoles.refiningRankedExactRoles(numNodes, positionView, in,
                  toRefine, comparator);
            }

          };
        } else {

          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return callWrapped(positionView, in);
            }

            private <V extends T> BinaryRelation callWrapped(
                TransposableNetworkView<V, ?> positionView, BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, wrap(positionView),
                  (ri, rj) -> in.contains(
                      positionView.tieTarget(ri.source, ri.target, ri.node, ri.tie),
                      positionView.tieTarget(rj.source, rj.target, rj.node, rj.tie))
                      && comparator.test(ri.tie, rj.tie));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return callRefiningWrapped(positionView, in, toRefine);
            }

            private <V extends T> BinaryRelation callRefiningWrapped(
                TransposableNetworkView<V, ?> positionView, BinaryRelation in,
                BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, wrap(positionView),
                  toRefine, (ri, rj) -> in.contains(
                      positionView.tieTarget(ri.source, ri.target, ri.node, ri.tie),
                      positionView.tieTarget(rj.source, rj.target, rj.node, rj.tie))
                      && comparator.test(ri.tie, rj.tie));
            }
          };
        }
      }
    };
  }

  @Override
  public <T> RoleOperatorBuilder<T, BinaryRelation> of(TransposableNetworkView<? extends T, ? extends T> positionView) {
    if (positionView instanceof NetworkView<?, ?>) {
      return of((NetworkView<? extends T, ? extends T>) positionView);
    }
    final int p = getPValue();
    final int numNodes = positionView.countNodes();
    return new AbstractRoleOperatorBuilder<T, BinaryRelation>() {

      @Override
      RoleOperator<BinaryRelation> makeConcrete() {
        if (p >= numNodes) {
          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return RegularRankedRoles.rankedRegularRoles(numNodes, positionView, in);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return RegularRankedRoles.refiningRankedRegularRoles(numNodes, positionView, in,
                  toRefine);
            }

          };
        } else if (p == 1) {
          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return EquitableRankedRoles.rankedExactRoles(numNodes, positionView, in);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return EquitableRankedRoles.refiningRankedExactRoles(numNodes, positionView, in,
                  toRefine);
            }

          };
        } else {

          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return callWrapped(positionView, in);
            }

            private <V extends T> BinaryRelation callWrapped(
                TransposableNetworkView<V, ?> positionView, BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, wrap(positionView),
                  (ri, rj) -> in.contains(
                      positionView.tieTarget(ri.source, ri.target, ri.node, ri.tie),
                      positionView.tieTarget(rj.source, rj.target, rj.node, rj.tie)));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return callRefiningWrapped(positionView, in, toRefine);
            }

            private <V extends T> BinaryRelation callRefiningWrapped(
                TransposableNetworkView<V, ?> positionView, BinaryRelation in,
                BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, wrap(positionView),
                  toRefine, (ri, rj) -> in.contains(
                      positionView.tieTarget(ri.source, ri.target, ri.node, ri.tie),
                      positionView.tieTarget(rj.source, rj.target, rj.node, rj.tie)));
            }

          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(Comparator<? super T> comparator) {
        if (p >= numNodes) {
          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return RegularRankedRoles.rankedRegularRoles(numNodes, positionView, in, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return RegularRankedRoles.refiningRankedRegularRoles(numNodes, positionView, in,
                  toRefine, comparator);
            }

          };
        } else if (p == 1) {
          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return EquitableRankedRoles.rankedExactRoles(numNodes, positionView, in, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return EquitableRankedRoles.refiningRankedExactRoles(numNodes, positionView, in,
                  toRefine, comparator);
            }

          };
        } else {

          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return callWrapped(positionView, in);
            }

            private <V extends T> BinaryRelation callWrapped(
                TransposableNetworkView<V, ?> positionView, BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, wrap(positionView),
                  (ri, rj) -> in.contains(
                      positionView.tieTarget(ri.source, ri.target, ri.node, ri.tie),
                      positionView.tieTarget(rj.source, rj.target, rj.node, rj.tie))
                      && comparator.compare(ri.tie, rj.tie) <= 0);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return callRefiningWrapped(positionView, in, toRefine);
            }

            private <V extends T> BinaryRelation callRefiningWrapped(
                TransposableNetworkView<V, ?> positionView, BinaryRelation in,
                BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, wrap(positionView),
                  toRefine, (ri, rj) -> in.contains(
                      positionView.tieTarget(ri.source, ri.target, ri.node, ri.tie),
                      positionView.tieTarget(rj.source, rj.target, rj.node, rj.tie))
                      && comparator.compare(ri.tie, rj.tie) <= 0);
            }
          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(PartialComparator<? super T> comparator) {
        if (p >= numNodes) {
          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return RegularRankedRoles.rankedRegularRoles(numNodes, positionView, in, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return RegularRankedRoles.refiningRankedRegularRoles(numNodes, positionView, in,
                  toRefine, comparator);
            }
          };
        } else if (p == 1) {
          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return EquitableRankedRoles.rankedExactRoles(numNodes, positionView, in, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return EquitableRankedRoles.refiningRankedExactRoles(numNodes, positionView, in,
                  toRefine, comparator);
            }

          };
        } else {

          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return callWrapped(positionView, in);
            }

            private <V extends T> BinaryRelation callWrapped(
                TransposableNetworkView<V, ?> positionView, BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, wrap(positionView),
                  (ri, rj) -> {
                    if (!in.contains(positionView.tieTarget(ri.source, ri.target, ri.node, ri.tie),
                        positionView.tieTarget(rj.source, rj.target, rj.node, rj.tie))) {
                      return false;
                    }
                    PartialComparator.ComparisonResult result = comparator.compare(ri.tie, rj.tie);
                    return result == PartialComparator.ComparisonResult.LESS
                        || result == PartialComparator.ComparisonResult.EQUAL;
                  });
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return callRefiningWrapped(positionView, in, toRefine);
            }

            private <V extends T> BinaryRelation callRefiningWrapped(
                TransposableNetworkView<V, ?> positionView, BinaryRelation in,
                BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, wrap(positionView),
                  toRefine, (ri, rj) -> {
                    if (!in.contains(positionView.tieTarget(ri.source, ri.target, ri.node, ri.tie),
                        positionView.tieTarget(rj.source, rj.target, rj.node, rj.tie))) {
                      return false;
                    }
                    PartialComparator.ComparisonResult result = comparator.compare(ri.tie, rj.tie);
                    return result == PartialComparator.ComparisonResult.LESS
                        || result == PartialComparator.ComparisonResult.EQUAL;
                  });
            }
          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(BiPredicate<? super T, ? super T> comparator) {
        if (p >= numNodes) {
          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return RegularRankedRoles.rankedRegularRoles(numNodes, positionView, in, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return RegularRankedRoles.refiningRankedRegularRoles(numNodes, positionView, in,
                  toRefine, comparator);
            }
          };
        } else if (p == 1) {
          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return EquitableRankedRoles.rankedExactRoles(numNodes, positionView, in, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return EquitableRankedRoles.refiningRankedExactRoles(numNodes, positionView, in,
                  toRefine, comparator);
            }

          };
        } else {

          return new RelationalIsotoneRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return callWrapped(positionView, in);
            }

            private <V extends T> BinaryRelation callWrapped(
                TransposableNetworkView<V, ?> positionView, BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, wrap(positionView),
                  (ri, rj) -> in.contains(
                      positionView.tieTarget(ri.source, ri.target, ri.node, ri.tie),
                      positionView.tieTarget(rj.source, rj.target, rj.node, rj.tie))
                      && comparator.test(ri.tie, rj.tie));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return callRefiningWrapped(positionView, in, toRefine);
            }

            private <V extends T> BinaryRelation callRefiningWrapped(
                TransposableNetworkView<V, ?> positionView, BinaryRelation in,
                BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, wrap(positionView),
                  toRefine, (ri, rj) -> in.contains(
                      positionView.tieTarget(ri.source, ri.target, ri.node, ri.tie),
                      positionView.tieTarget(rj.source, rj.target, rj.node, rj.tie))
                      && comparator.test(ri.tie, rj.tie));
            }
          };
        }
      }
    };

  }

}
