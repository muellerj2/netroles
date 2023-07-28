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
import ch.ethz.sn.visone3.roles.blocks.factories.VariableRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.impl.algorithms.MiscRankedRoles;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

import java.util.Comparator;
import java.util.function.BiPredicate;

public class RelationalWeakRolesFactory
    extends VariableBuilderFactoryBase<VariableRoleOperatorBuilderFactory<BinaryRelation>>
    implements VariableRoleOperatorBuilderFactory<BinaryRelation> {

  @Override
  public <T> RoleOperatorBuilder<T, BinaryRelation> of(int numNodes,
      NetworkView<? extends T, ? extends T> positionView) {
    final int p = getPValue();
    return new AbstractRoleOperatorBuilder<T, BinaryRelation>() {

      @Override
      RoleOperator<BinaryRelation> makeConcrete() {
        if (p >= numNodes) {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakRolesRelation(numNodes, positionView);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine);
            }
          };
        } else if (p == 1) {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakExactRolesRelation(numNodes, positionView);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine);
            }
          };
        } else {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, positionView, (i, j) -> true);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, positionView, toRefine,
                  (i, j) -> true);
            }
          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(Comparator<? super T> comparator) {
        int p = getPValue();
        if (p >= numNodes) {
          return new RelationalConstantRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakRolesRelation(numNodes, positionView, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine,
                  comparator);
            }

          };
        } else if (p == 1) {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakExactRolesRelation(numNodes, positionView, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine,
                  comparator);
            }
          };
        } else {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, positionView,
                  (i, j) -> comparator.compare(i, j) <= 0);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, positionView, toRefine,
                  (i, j) -> comparator.compare(i, j) <= 0);
            }
          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(PartialComparator<? super T> comparator) {
        int p = getPValue();
        if (p >= numNodes) {
          return new RelationalConstantRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakRolesRelation(numNodes, positionView, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine,
                  comparator);
            }

          };
        } else if (p == 1) {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakExactRolesRelation(numNodes, positionView, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine,
                  comparator);
            }
          };
        } else {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, positionView, (i, j) -> {
                PartialComparator.ComparisonResult result = comparator.compare(i, j);
                return result == PartialComparator.ComparisonResult.LESS
                    || result == PartialComparator.ComparisonResult.EQUAL;
              });
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, positionView, toRefine,
                  (i, j) -> {
                    PartialComparator.ComparisonResult result = comparator.compare(i, j);
                    return result == PartialComparator.ComparisonResult.LESS
                        || result == PartialComparator.ComparisonResult.EQUAL;
                  });
            }
          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(BiPredicate<? super T, ? super T> comparator) {
        int p = getPValue();
        if (p >= numNodes) {
          return new RelationalConstantRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakRolesRelation(numNodes, positionView, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine,
                  comparator);
            }

          };
        } else if (p == 1) {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakExactRolesRelation(numNodes, positionView, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine,
                  comparator);
            }
          };
        } else {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, positionView,
                  (i, j) -> comparator.test(i, j));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, positionView, toRefine,
                  (i, j) -> comparator.test(i, j));
            }
          };
        }
      }
    };
  }

  @Override
  public <T> RoleOperatorBuilder<T, BinaryRelation> of(int numNodes,
      TransposableNetworkView<? extends T, ? extends T> positionView) {
    if (positionView instanceof NetworkView<?, ?>) {
      return of(numNodes, (NetworkView<? extends T, ? extends T>) positionView);
    }
    final int p = getPValue();
    return new AbstractRoleOperatorBuilder<T, BinaryRelation>() {

      @Override
      RoleOperator<BinaryRelation> makeConcrete() {
        if (p >= numNodes) {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakRolesRelation(numNodes, positionView);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine);
            }
          };
        } else if (p == 1) {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakExactRolesRelation(numNodes, positionView);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine);
            }
          };
        } else {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, positionView, (i, j) -> true);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, positionView, toRefine,
                  (i, j) -> true);
            }
          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(Comparator<? super T> comparator) {
        int p = getPValue();
        if (p >= numNodes) {
          return new RelationalConstantRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakRolesRelation(numNodes, positionView, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine,
                  comparator);
            }

          };
        } else if (p == 1) {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakExactRolesRelation(numNodes, positionView, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine,
                  comparator);
            }
          };
        } else {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, positionView,
                  (i, j) -> comparator.compare(i, j) <= 0);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, positionView, toRefine,
                  (i, j) -> comparator.compare(i, j) <= 0);
            }
          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(PartialComparator<? super T> comparator) {
        int p = getPValue();
        if (p >= numNodes) {
          return new RelationalConstantRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakRolesRelation(numNodes, positionView, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine,
                  comparator);
            }

          };
        } else if (p == 1) {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakExactRolesRelation(numNodes, positionView, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine,
                  comparator);
            }
          };
        } else {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, positionView, (i, j) -> {
                PartialComparator.ComparisonResult result = comparator.compare(i, j);
                return result == PartialComparator.ComparisonResult.LESS
                    || result == PartialComparator.ComparisonResult.EQUAL;
              });
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, positionView, toRefine,
                  (i, j) -> {
                    PartialComparator.ComparisonResult result = comparator.compare(i, j);
                    return result == PartialComparator.ComparisonResult.LESS
                        || result == PartialComparator.ComparisonResult.EQUAL;
                  });
            }
          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(BiPredicate<? super T, ? super T> comparator) {
        int p = getPValue();
        if (p >= numNodes) {
          return new RelationalConstantRoleOperatorBase() {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakRolesRelation(numNodes, positionView, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine,
                  comparator);
            }

          };
        } else if (p == 1) {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakExactRolesRelation(numNodes, positionView, comparator);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine,
                  comparator);
            }
          };
        } else {
          return new RelationalConstantRoleOperatorBase() {
            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, positionView,
                  (i, j) -> comparator.test(i, j));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, positionView, toRefine,
                  (i, j) -> comparator.test(i, j));
            }
          };
        }
      }
    };
  }

}
