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

import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.builders.RoleOperatorBuilder;
import ch.ethz.sn.visone3.roles.blocks.factories.RoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.impl.algorithms.MiscRankedRoles;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

class RankedStrongStructuralRolesFactory
    implements RoleOperatorBuilderFactory<Ranking> {

  @Override
  public <T> RoleOperatorBuilder<T, Ranking> of(NetworkView<? extends T, ? extends T> positionView) {
    final int numNodes = positionView.countNodes();
    return new AbstractNoBiPredicateRoleOperatorBuilder<T, Ranking>() {

      @Override
      RoleOperator<Ranking> makeConcrete() {
        return new RankedConstantRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.strongStructuralRolesRanking(numNodes, positionView);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningStrongStructuralRoles(numNodes, positionView, toRefine);
          }

        };
      }

      @Override
      RoleOperator<Ranking> makeConcrete(Comparator<? super T> comparator) {
        return new RankedConstantRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.strongStructuralRolesRanking(numNodes, positionView, comparator);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningStrongStructuralRoles(numNodes, positionView, toRefine,
                comparator);
          }

        };
      }

      @Override
      RoleOperator<Ranking> makeConcrete(PartialComparator<? super T> comparator) {
        return new RankedConstantRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.strongStructuralRolesRanking(numNodes, positionView, comparator);
          }

          @Override
          public Ranking relativeRefining(Ranking in, Ranking toRefine) {
            return MiscRankedRoles.refiningStrongStructuralRoles(numNodes, positionView, toRefine,
                comparator);
          }
        };
      }

    };
  }

  @Override
  public <T> RoleOperatorBuilder<T, Ranking> of(TransposableNetworkView<? extends T, ? extends T> positionView) {
    if (positionView instanceof NetworkView<?, ?>) {
      return of((NetworkView<? extends T, ? extends T>) positionView);
    }
    final int numNodes = positionView.countNodes();
    return new AbstractNoBiPredicateRoleOperatorBuilder<T, Ranking>() {

      @Override
      RoleOperator<Ranking> makeConcrete() {
        return new RankedConstantRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.strongStructuralRolesRanking(numNodes, positionView);
          }

        };
      }

      @Override
      RoleOperator<Ranking> makeConcrete(Comparator<? super T> comparator) {
        return new RankedConstantRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.strongStructuralRolesRanking(numNodes, positionView, comparator);
          }

        };
      }

      @Override
      RoleOperator<Ranking> makeConcrete(PartialComparator<? super T> comparator) {
        return new RankedConstantRoleOperatorBase() {

          @Override
          public Ranking relative(Ranking in) {
            return MiscRankedRoles.strongStructuralRolesRanking(numNodes, positionView, comparator);
          }

        };
      }

    };
  }

}
