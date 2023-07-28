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
import ch.ethz.sn.visone3.roles.blocks.factories.EquitableLooseRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.impl.algorithms.EquitableRankedRoles;
import ch.ethz.sn.visone3.roles.impl.algorithms.RegularRankedRoles;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

import java.util.Comparator;

public class RankedRegularRolesFactory
    extends ExactLooseBuilderFactoryBase<EquitableLooseRoleOperatorBuilderFactory<Ranking>>
    implements EquitableLooseRoleOperatorBuilderFactory<Ranking> {

  @Override
  public <T> RoleOperatorBuilder<T, Ranking> of(int numNodes,
      NetworkView<? extends T, ? extends T> positionView) {
    final boolean exact = isExact();
    return new AbstractNoBiPredicateRoleOperatorBuilder<T, Ranking>() {

      @Override
      RoleOperator<Ranking> makeConcrete() {
        return !exact ? new RankedIsotoneRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
              return RegularRankedRoles.rankedRegularRoles(numNodes, positionView, in);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
              return RegularRankedRoles.refiningRankedRegularRoles(numNodes, positionView, in,
                  toRefine);
          }

          @Override
          public Ranking interior(Ranking in) {
              return RegularRankedRoles.computeRankedRolesInterior(numNodes, positionView, in);
          }

          @Override
          public Ranking closure(Ranking in) {
            return RegularRankedRoles.computeRankedRolesClosure(numNodes, positionView, in);
          }
        } : new RankedIsotoneRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return EquitableRankedRoles.rankedExactRoles(numNodes, positionView, in);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return EquitableRankedRoles.refiningRankedExactRoles(numNodes, positionView, in, toRefine);
          }
        };
      }

      @Override
      RoleOperator<Ranking> makeConcrete(Comparator<? super T> comparator) {
        return !exact ? new RankedIsotoneRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return RegularRankedRoles.rankedRegularRoles(numNodes, positionView, in, comparator);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return RegularRankedRoles.refiningRankedRegularRoles(numNodes, positionView, in,
                toRefine, comparator);
          }

          @Override
          public Ranking closure(Ranking in) {
            return RegularRankedRoles.computeRankedRolesClosure(numNodes, positionView, in,
                comparator);
          }
        } : new RankedIsotoneRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return EquitableRankedRoles.rankedExactRoles(numNodes, positionView, in, comparator);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return EquitableRankedRoles.refiningRankedExactRoles(numNodes, positionView, in,
                toRefine, comparator);
          }

        };
      }

      @Override
      RoleOperator<Ranking> makeConcrete(PartialComparator<? super T> comparator) {
        return !exact ? new RankedIsotoneRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return RegularRankedRoles.rankedRegularRoles(numNodes, positionView, in, comparator);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return RegularRankedRoles.refiningRankedRegularRoles(numNodes, positionView, in,
                toRefine, comparator);
          }
        } : new RankedIsotoneRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return EquitableRankedRoles.rankedExactRoles(numNodes, positionView, in, comparator);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return EquitableRankedRoles.refiningRankedExactRoles(numNodes, positionView, in,
                toRefine, comparator);
          }

        };
      }
    };
  }

  @Override
  public <T> RoleOperatorBuilder<T, Ranking> of(int numNodes,
      TransposableNetworkView<? extends T, ? extends T> positionView) {
    if (positionView instanceof NetworkView<?, ?>) {
      return of(numNodes, (NetworkView<? extends T, ? extends T>) positionView);
    }

    final boolean exact = isExact();
    return new AbstractNoBiPredicateRoleOperatorBuilder<T, Ranking>() {

      @Override
      RoleOperator<Ranking> makeConcrete() {
        return !exact ? new RankedIsotoneRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return RegularRankedRoles.rankedRegularRoles(numNodes, positionView, in);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return RegularRankedRoles.refiningRankedRegularRoles(numNodes, positionView, in,
                toRefine);
          }

        } : new RankedIsotoneRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return EquitableRankedRoles.rankedExactRoles(numNodes, positionView, in);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return EquitableRankedRoles.refiningRankedExactRoles(numNodes, positionView, in, toRefine);
          }

        };
      }

      @Override
      RoleOperator<Ranking> makeConcrete(Comparator<? super T> comparator) {
        return !exact ? new RankedIsotoneRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return RegularRankedRoles.rankedRegularRoles(numNodes, positionView, in, comparator);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return RegularRankedRoles.refiningRankedRegularRoles(numNodes, positionView, in,
                toRefine, comparator);
          }

        } : new RankedIsotoneRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return EquitableRankedRoles.rankedExactRoles(numNodes, positionView, in, comparator);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return EquitableRankedRoles.refiningRankedExactRoles(numNodes, positionView, in, toRefine,
                comparator);
          }

        };
      }

      @Override
      RoleOperator<Ranking> makeConcrete(PartialComparator<? super T> comparator) {
        return !exact ? new RankedIsotoneRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return RegularRankedRoles.rankedRegularRoles(numNodes, positionView, in, comparator);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return RegularRankedRoles.refiningRankedRegularRoles(numNodes, positionView, in,
                toRefine, comparator);
          }
        } : new RankedIsotoneRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return EquitableRankedRoles.rankedExactRoles(numNodes, positionView, in, comparator);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return EquitableRankedRoles.refiningRankedExactRoles(numNodes, positionView, in, toRefine,
                comparator);
          }

        };
      }

    };
  }

}
