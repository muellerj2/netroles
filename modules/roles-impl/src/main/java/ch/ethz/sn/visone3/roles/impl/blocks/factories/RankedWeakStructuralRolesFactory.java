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

import ch.ethz.sn.visone3.roles.blocks.Operators;
import ch.ethz.sn.visone3.roles.blocks.Reducers;
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.builders.RoleOperatorBuilder;
import ch.ethz.sn.visone3.roles.blocks.factories.RoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.WeakStructuralRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.impl.algorithms.MiscRankedRoles;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

class RankedWeakStructuralRolesFactory
    implements RoleOperatorBuilderFactory<Ranking>,
    WeakStructuralRoleOperatorBuilderFactory<Ranking> {

  @Override
  public <T> RoleOperatorBuilder<T, Ranking> of(NetworkView<? extends T, ? extends T> positionView) {
    return of(positionView, null);
  }

  @Override
  public <T> RoleOperatorBuilder<T, Ranking> of(TransposableNetworkView<? extends T, ? extends T> positionView) {
    return of(positionView, null);
  }

  @Override
  public <T> RoleOperatorBuilder<T, Ranking> of(NetworkView<? extends T, ? extends T> oneDirection,
      NetworkView<? extends T, ? extends T> otherDirectionNonfinal) {

    if (oneDirection == otherDirectionNonfinal) {
      otherDirectionNonfinal = null;
    }
    final NetworkView<? extends T, ? extends T> otherDirection = otherDirectionNonfinal;
    final int numNodes = oneDirection.countNodes();

    if (otherDirection != null && numNodes != otherDirection.countNodes()) {
      throw new IllegalArgumentException("mismatched in node numbers between directions");
    }

    return new AbstractNoBiPredicateRoleOperatorBuilder<T, Ranking>() {

      @Override
      RoleOperator<Ranking> makeConcrete() {
        RoleOperator<Ranking> result = new RankedConstantRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakStructuralRolesRanking(numNodes, oneDirection);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningWeakStructuralRoles(numNodes, oneDirection, toRefine);
          }
        };
        if (otherDirection != null) {
          result = Operators.parallel(Reducers.RANKING.meet(), result,
              new RankedConstantRoleOperatorBase() {

                @Override
                public Ranking relative(Ranking in) {
                  return MiscRankedRoles.weakStructuralRolesRanking(numNodes, otherDirection);
                }

                @Override
                public Ranking relativeRefining(Ranking in, Ranking toRefine) {
                  return MiscRankedRoles.refiningWeakStructuralRoles(numNodes, otherDirection,
                      toRefine);
                }
              });
        }
        return result;
      }

      @Override
      RoleOperator<Ranking> makeConcrete(Comparator<? super T> comparator) {
        RoleOperator<Ranking> result = new RankedConstantRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakStructuralRolesRanking(numNodes, oneDirection, comparator);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningWeakStructuralRoles(numNodes, oneDirection, toRefine,
                comparator);
          }

        };

        if (otherDirection != null) {
          result = Operators.parallel(Reducers.RANKING.meet(), result,
              new RankedConstantRoleOperatorBase() {

                @Override
                public Ranking relative(Ranking in) {
                  return MiscRankedRoles.weakStructuralRolesRanking(numNodes, otherDirection,
                      comparator);
                }

                @Override
                public Ranking relativeRefining(Ranking in, Ranking toRefine) {
                  return MiscRankedRoles.refiningWeakStructuralRoles(numNodes, otherDirection,
                      toRefine, comparator);
                }

              });
        }
        return result;
      }

      @Override
      RoleOperator<Ranking> makeConcrete(PartialComparator<? super T> comparator) {
        RoleOperator<Ranking> result = new RankedConstantRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakStructuralRolesRanking(numNodes, oneDirection, comparator);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningWeakStructuralRoles(numNodes, oneDirection, toRefine,
                comparator);
          }
        };

        if (otherDirection != null) {
          result = Operators.parallel(Reducers.RANKING.meet(), result,
              new RankedConstantRoleOperatorBase() {

                @Override
                public Ranking relative(Ranking in) {
                  return MiscRankedRoles.weakStructuralRolesRanking(numNodes, otherDirection,
                      comparator);
                }

                @Override
                public Ranking relativeRefining(Ranking in, Ranking toRefine) {
                  return MiscRankedRoles.refiningWeakStructuralRoles(numNodes, otherDirection,
                      toRefine, comparator);
                }

              });
        }
        return result;
      }

    };
  }

  @Override
  public <T> RoleOperatorBuilder<T, Ranking> of(TransposableNetworkView<? extends T, ? extends T> oneDirection,
      TransposableNetworkView<? extends T, ? extends T> otherDirectionNonfinal) {
    if (otherDirectionNonfinal == oneDirection) {
      otherDirectionNonfinal = null;
    }
    final TransposableNetworkView<? extends T, ? extends T> otherDirection = otherDirectionNonfinal;

    if (oneDirection instanceof NetworkView<?, ?>
        && (otherDirection == null || otherDirection instanceof NetworkView<?, ?>)) {
      return of((NetworkView<? extends T, ? extends T>) oneDirection,
          (NetworkView<? extends T, ? extends T>) otherDirection);
    }

    final int numNodes = oneDirection.countNodes();

    if (otherDirection != null && numNodes != otherDirection.countNodes()) {
      throw new IllegalArgumentException("mismatched in node numbers between directions");
    }

    return new AbstractNoBiPredicateRoleOperatorBuilder<T, Ranking>() {

      @Override
      RoleOperator<Ranking> makeConcrete() {
        RoleOperator<Ranking> result = new RankedConstantRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakStructuralRolesRanking(numNodes, oneDirection);
          }

        };
        if (otherDirection != null) {
          result = Operators.parallel(Reducers.RANKING.meet(), result,
              new RankedConstantRoleOperatorBase() {

                @Override
                public Ranking relative(Ranking in) {
                  return MiscRankedRoles.weakStructuralRolesRanking(numNodes, otherDirection);
                }

              });
        }
        return result;
      }

      @Override
      RoleOperator<Ranking> makeConcrete(Comparator<? super T> comparator) {
        RoleOperator<Ranking> result = new RankedConstantRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakStructuralRolesRanking(numNodes, oneDirection, comparator);
          }

        };

        if (otherDirection != null) {
          result = Operators.parallel(Reducers.RANKING.meet(), result,
              new RankedConstantRoleOperatorBase() {

                @Override
                public Ranking relative(Ranking in) {
                  return MiscRankedRoles.weakStructuralRolesRanking(numNodes, otherDirection,
                      comparator);
                }

              });
        }
        return result;
      }

      @Override
      RoleOperator<Ranking> makeConcrete(PartialComparator<? super T> comparator) {
        RoleOperator<Ranking> result = new RankedConstantRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakStructuralRolesRanking(numNodes, oneDirection, comparator);
          }

        };

        if (otherDirection != null) {
          result = Operators.parallel(Reducers.RANKING.meet(), result,
              new RankedConstantRoleOperatorBase() {

                @Override
                public Ranking relative(Ranking in) {
                  return MiscRankedRoles.weakStructuralRolesRanking(numNodes, otherDirection,
                      comparator);
                }

              });
        }
        return result;
      }

    };
  }

  @Override
  public RoleOperatorBuilderFactory<Ranking> unidirectional() {
    return this;
  }

}
