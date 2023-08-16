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
import java.util.function.Function;

import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.builders.GenericRoleOperatorBuilder;
import ch.ethz.sn.visone3.roles.blocks.factories.VariableGenericRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.impl.algorithms.MiscRankedRoles;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

class RelationalGenericRolesFactory
    extends VariableBuilderFactoryBase<VariableGenericRoleOperatorBuilderFactory<BinaryRelation>>
    implements VariableGenericRoleOperatorBuilderFactory<BinaryRelation> {

  @Override
  public <T> GenericRoleOperatorBuilder<T, BinaryRelation> of(int numNodes,
      NetworkView<? extends T, ? extends T> positionView) {
    final int p = getPValue();
    return new AbstractGenericRoleOperatorBuilder<T, BinaryRelation>() {

      @Override
      RoleOperator<BinaryRelation> makeConcrete() {
        if (p >= numNodes) {
          return new RelationalTraitsAdjustableOperator(traits) {

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

          return new RelationalTraitsAdjustableOperator(traits) {

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
          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, positionView,
                  (ri, rj) -> true);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, positionView, toRefine,
                  (ri, rj) -> true);
            }
          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcreteWeak(
          Function<? super BinaryRelation, Comparator<? super T>> comparator) {
        if (p >= numNodes) {
          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakRolesRelation(numNodes, positionView,
                  comparator.apply(in));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine,
                  comparator.apply(in));
            }
          };
        } else if (p == 1) {
          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakExactRolesRelation(numNodes, positionView,
                  comparator.apply(in));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine,
                  comparator.apply(in));
            }
          };
        } else {
          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              Comparator<? super T> concreteComparator = comparator.apply(in);
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, positionView,
                  (ri, rj) -> concreteComparator.compare(ri, rj) <= 0);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              Comparator<? super T> concreteComparator = comparator.apply(in);
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, positionView, toRefine,
                  (ri, rj) -> concreteComparator.compare(ri, rj) <= 0);
            }
          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcretePartial(
          Function<? super BinaryRelation, PartialComparator<? super T>> comparator) {
        if (p >= numNodes) {
          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakRolesRelation(numNodes, positionView,
                  comparator.apply(in));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine,
                  comparator.apply(in));
            }
          };
        } else if (p == 1) {

          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakExactRolesRelation(numNodes, positionView,
                  comparator.apply(in));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine,
                  comparator.apply(in));
            }
          };
        } else {
          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              PartialComparator<? super T> concreteComparator = comparator.apply(in);
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, positionView, (ri, rj) -> {
                PartialComparator.ComparisonResult result = concreteComparator.compare(ri, rj);
                return result == PartialComparator.ComparisonResult.LESS
                    || result == PartialComparator.ComparisonResult.EQUAL;
              });
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              PartialComparator<? super T> concreteComparator = comparator.apply(in);
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, positionView, toRefine,
                  (ri, rj) -> {
                    PartialComparator.ComparisonResult result = concreteComparator.compare(ri, rj);
                    return result == PartialComparator.ComparisonResult.LESS
                        || result == PartialComparator.ComparisonResult.EQUAL;
                  });
            }
          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcretePred(
          Function<? super BinaryRelation, BiPredicate<? super T, ? super T>> comparator) {
        if (p >= numNodes) {
          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakRolesRelation(numNodes, positionView,
                  comparator.apply(in));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine,
                  comparator.apply(in));
            }
          };
        } else if (p == 1) {
          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakExactRolesRelation(numNodes, positionView,
                  comparator.apply(in));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine,
                  comparator.apply(in));
            }
          };
        } else {

          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, positionView,
                  comparator.apply(in));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, positionView, toRefine,
                  comparator.apply(in));
            }
          };
        }
      }
    };
  }

  @Override
  public <T> GenericRoleOperatorBuilder<T, BinaryRelation> of(int numNodes,
      TransposableNetworkView<? extends T, ? extends T> positionView) {
    if (positionView instanceof NetworkView<?, ?>) {
      return of(numNodes, (NetworkView<? extends T, ? extends T>) positionView);
    }
    final int p = getPValue();
    return new AbstractGenericRoleOperatorBuilder<T, BinaryRelation>() {

      @Override
      RoleOperator<BinaryRelation> makeConcrete() {
        if (p >= numNodes) {
          return new RelationalTraitsAdjustableOperator(traits) {

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

          return new RelationalTraitsAdjustableOperator(traits) {

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
          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, positionView,
                  (ri, rj) -> true);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, positionView, toRefine,
                  (ri, rj) -> true);
            }
          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcreteWeak(
          Function<? super BinaryRelation, Comparator<? super T>> comparator) {
        if (p >= numNodes) {
          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakRolesRelation(numNodes, positionView,
                  comparator.apply(in));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine,
                  comparator.apply(in));
            }
          };
        } else if (p == 1) {
          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakExactRolesRelation(numNodes, positionView,
                  comparator.apply(in));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine,
                  comparator.apply(in));
            }
          };
        } else {
          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              Comparator<? super T> concreteComparator = comparator.apply(in);
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, positionView,
                  (ri, rj) -> concreteComparator.compare(ri, rj) <= 0);
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              Comparator<? super T> concreteComparator = comparator.apply(in);
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, positionView, toRefine,
                  (ri, rj) -> concreteComparator.compare(ri, rj) <= 0);
            }
          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcretePartial(
          Function<? super BinaryRelation, PartialComparator<? super T>> comparator) {
        if (p >= numNodes) {
          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakRolesRelation(numNodes, positionView,
                  comparator.apply(in));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine,
                  comparator.apply(in));
            }
          };
        } else if (p == 1) {

          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakExactRolesRelation(numNodes, positionView,
                  comparator.apply(in));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine,
                  comparator.apply(in));
            }
          };
        } else {
          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              PartialComparator<? super T> concreteComparator = comparator.apply(in);
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, positionView, (ri, rj) -> {
                PartialComparator.ComparisonResult result = concreteComparator.compare(ri, rj);
                return result == PartialComparator.ComparisonResult.LESS
                    || result == PartialComparator.ComparisonResult.EQUAL;
              });
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              PartialComparator<? super T> concreteComparator = comparator.apply(in);
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, positionView, toRefine,
                  (ri, rj) -> {
                    PartialComparator.ComparisonResult result = concreteComparator.compare(ri, rj);
                    return result == PartialComparator.ComparisonResult.LESS
                        || result == PartialComparator.ComparisonResult.EQUAL;
                  });
            }
          };
        }
      }

      @Override
      RoleOperator<BinaryRelation> makeConcretePred(
          Function<? super BinaryRelation, BiPredicate<? super T, ? super T>> comparator) {
        if (p >= numNodes) {
          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakRolesRelation(numNodes, positionView,
                  comparator.apply(in));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine,
                  comparator.apply(in));
            }
          };
        } else if (p == 1) {
          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.weakExactRolesRelation(numNodes, positionView,
                  comparator.apply(in));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine,
                  comparator.apply(in));
            }
          };
        } else {

          return new RelationalTraitsAdjustableOperator(traits) {

            @Override
            public BinaryRelation relative(BinaryRelation in) {
              return MiscRankedRoles.pMatchingWeakRoles(p, numNodes, positionView,
                  comparator.apply(in));
            }

            @Override
            public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
              return MiscRankedRoles.pMatchingRefiningWeakRoles(p, numNodes, positionView, toRefine,
                  comparator.apply(in));
            }
          };
        }
      }
    };
  }

}
