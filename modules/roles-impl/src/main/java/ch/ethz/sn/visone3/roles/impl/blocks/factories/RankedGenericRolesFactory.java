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
import java.util.function.Function;

import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.builders.GenericRoleOperatorBuilder;
import ch.ethz.sn.visone3.roles.blocks.factories.EquitableLooseGenericRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.impl.algorithms.MiscRankedRoles;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

class RankedGenericRolesFactory
    extends ExactLooseBuilderFactoryBase<EquitableLooseGenericRoleOperatorBuilderFactory<Ranking>>
    implements EquitableLooseGenericRoleOperatorBuilderFactory<Ranking> {

  @Override
  public <T> GenericRoleOperatorBuilder<T, Ranking> of(NetworkView<? extends T, ? extends T> positionView) {
    final boolean exact = isExact();
    final int numNodes = positionView.countNodes();
    return new AbstractGenericNoBiPredicateRoleOperatorBuilder<T, Ranking>() {

      @Override
      RoleOperator<Ranking> makeConcrete() {
        return !exact ? new RankedTraitsAdjustableOperator(traits) {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakRolesRanking(numNodes, positionView);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine);
          }
        } : new RankedTraitsAdjustableOperator(traits) {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakExactRolesRanking(numNodes, positionView);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine);
          }
        };
      }

      @Override
      RoleOperator<Ranking> makeConcreteWeak(
          Function<? super Ranking, Comparator<? super T>> comparator) {
        return !exact ? new RankedTraitsAdjustableOperator(traits) {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakRolesRanking(numNodes, positionView, comparator.apply(in));
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine,
                comparator.apply(in));
          }
        } : new RankedTraitsAdjustableOperator(traits) {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakExactRolesRanking(numNodes, positionView,
                comparator.apply(in));
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine,
                comparator.apply(in));
          }
        };
      }

      @Override
      RoleOperator<Ranking> makeConcretePartial(
          Function<? super Ranking, PartialComparator<? super T>> comparator) {
        return !exact ? new RankedTraitsAdjustableOperator(traits) {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakRolesRanking(numNodes, positionView, comparator.apply(in));
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine,
                comparator.apply(in));
          }
        } : new RankedTraitsAdjustableOperator(traits) {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakExactRolesRanking(numNodes, positionView,
                comparator.apply(in));
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine,
                comparator.apply(in));
          }
        };
      }
    };
  }

  @Override
  public <T> GenericRoleOperatorBuilder<T, Ranking> of(TransposableNetworkView<? extends T, ? extends T> positionView) {
    if (positionView instanceof NetworkView<?, ?>) {
      return of((NetworkView<? extends T, ? extends T>) positionView);
    }
    final boolean exact = isExact();
    final int numNodes = positionView.countNodes();
    return new AbstractGenericNoBiPredicateRoleOperatorBuilder<T, Ranking>() {

      @Override
      RoleOperator<Ranking> makeConcrete() {
        return !exact ? new RankedTraitsAdjustableOperator(traits) {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakRolesRanking(numNodes, positionView);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine);
          }
        } : new RankedTraitsAdjustableOperator(traits) {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakExactRolesRanking(numNodes, positionView);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine);
          }
        };
      }

      @Override
      RoleOperator<Ranking> makeConcreteWeak(
          Function<? super Ranking, Comparator<? super T>> comparator) {
        return !exact ? new RankedTraitsAdjustableOperator(traits) {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakRolesRanking(numNodes, positionView, comparator.apply(in));
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine,
                comparator.apply(in));
          }
        } : new RankedTraitsAdjustableOperator(traits) {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakExactRolesRanking(numNodes, positionView,
                comparator.apply(in));
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine,
                comparator.apply(in));
          }
        };
      }

      @Override
      RoleOperator<Ranking> makeConcretePartial(
          Function<? super Ranking, PartialComparator<? super T>> comparator) {
        return !exact ? new RankedTraitsAdjustableOperator(traits) {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakRolesRanking(numNodes, positionView, comparator.apply(in));
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningWeakRoles(numNodes, positionView, toRefine,
                comparator.apply(in));
          }
        } : new RankedTraitsAdjustableOperator(traits) {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.weakExactRolesRanking(numNodes, positionView,
                comparator.apply(in));
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningWeakExactRoles(numNodes, positionView, toRefine,
                comparator.apply(in));
          }
        };
      }
    };
  }

}
