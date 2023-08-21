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

import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.builders.RoleOperatorBuilder;
import ch.ethz.sn.visone3.roles.blocks.factories.RoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.impl.algorithms.MiscRankedRoles;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

class RelationalStrongStructuralRolesFactory
    implements RoleOperatorBuilderFactory<BinaryRelation> {

  @Override
  public <T> RoleOperatorBuilder<T, BinaryRelation> of(NetworkView<? extends T, ? extends T> positionView) {
    final int numNodes = positionView.countNodes();
    return new AbstractRoleOperatorBuilder<T, BinaryRelation>() {

      @Override
      RoleOperator<BinaryRelation> makeConcrete() {
        return new RelationalConstantRoleOperatorBase() {

          @Override
          public BinaryRelation relative(BinaryRelation in) {
            return MiscRankedRoles.strongStructuralRolesRelation(numNodes, positionView);
          }

          @Override
          public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
            return MiscRankedRoles.refiningStrongStructuralRoles(numNodes, positionView, toRefine);
          }

        };
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(Comparator<? super T> comparator) {
        return new RelationalConstantRoleOperatorBase() {

          @Override
          public BinaryRelation relative(BinaryRelation in) {
            return MiscRankedRoles.strongStructuralRolesRelation(numNodes, positionView,
                comparator);
          }

          @Override
          public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
            return MiscRankedRoles.refiningStrongStructuralRoles(numNodes, positionView, toRefine,
                comparator);
          }

        };
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(PartialComparator<? super T> comparator) {
        return new RelationalConstantRoleOperatorBase() {

          @Override
          public BinaryRelation relative(BinaryRelation in) {
            return MiscRankedRoles.strongStructuralRolesRelation(numNodes, positionView,
                comparator);
          }

          @Override
          public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
            return MiscRankedRoles.refiningStrongStructuralRoles(numNodes, positionView, toRefine,
                comparator);
          }
        };
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(BiPredicate<? super T, ? super T> comparator) {
        return new RelationalConstantRoleOperatorBase() {

          @Override
          public BinaryRelation relative(BinaryRelation in) {
            return MiscRankedRoles.strongStructuralRolesRelation(numNodes, positionView,
                comparator);
          }

          @Override
          public BinaryRelation relativeRefining(BinaryRelation in, BinaryRelation toRefine) {
            return MiscRankedRoles.refiningStrongStructuralRoles(numNodes, positionView, toRefine,
                comparator);
          }
        };
      }
    };
  }

  @Override
  public <T> RoleOperatorBuilder<T, BinaryRelation> of(TransposableNetworkView<? extends T, ? extends T> positionView) {
    if (positionView instanceof NetworkView<?, ?>) {
      return of((NetworkView<? extends T, ? extends T>) positionView);
    }
    final int numNodes = positionView.countNodes();
    return new AbstractRoleOperatorBuilder<T, BinaryRelation>() {

      @Override
      RoleOperator<BinaryRelation> makeConcrete() {
        return new RelationalConstantRoleOperatorBase() {

          @Override
          public BinaryRelation relative(BinaryRelation in) {
            return MiscRankedRoles.strongStructuralRolesRelation(numNodes, positionView);
          }

        };
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(Comparator<? super T> comparator) {
        return new RelationalConstantRoleOperatorBase() {

          @Override
          public BinaryRelation relative(BinaryRelation in) {
            return MiscRankedRoles.strongStructuralRolesRelation(numNodes, positionView,
                comparator);
          }

        };
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(PartialComparator<? super T> comparator) {
        return new RelationalConstantRoleOperatorBase() {

          @Override
          public BinaryRelation relative(BinaryRelation in) {
            return MiscRankedRoles.strongStructuralRolesRelation(numNodes, positionView,
                comparator);
          }

        };
      }

      @Override
      RoleOperator<BinaryRelation> makeConcrete(BiPredicate<? super T, ? super T> comparator) {
        return new RelationalConstantRoleOperatorBase() {

          @Override
          public BinaryRelation relative(BinaryRelation in) {
            return MiscRankedRoles.strongStructuralRolesRelation(numNodes, positionView,
                comparator);
          }

        };
      }
    };
  }

}
