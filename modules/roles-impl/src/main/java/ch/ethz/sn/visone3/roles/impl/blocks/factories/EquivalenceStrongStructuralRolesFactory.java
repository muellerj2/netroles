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

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.ConstMapping.OfInt;
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.builders.RoleOperatorBuilder;
import ch.ethz.sn.visone3.roles.blocks.factories.RoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.impl.algorithms.Equivalences;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

class EquivalenceStrongStructuralRolesFactory
    implements RoleOperatorBuilderFactory<ConstMapping.OfInt> {

  @Override
  public <T> RoleOperatorBuilder<T, ConstMapping.OfInt> of(int numNodes,
      NetworkView<? extends T, ? extends T> positionView) {
    return new AbstractNoBiPredicateRoleOperatorBuilder<T, ConstMapping.OfInt>() {
      @Override
      RoleOperator<ConstMapping.OfInt> makeConcrete() {
        return new EquivalenceConstantRoleOperatorBase() {

          @Override
          public ConstMapping.OfInt relativeRefining(ConstMapping.OfInt in,
              ConstMapping.OfInt toRefine) {
            return Equivalences.refiningStrongStructuralEquivalence(numNodes, positionView,
                toRefine);
          }

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.strongStructuralEquivalence(numNodes, positionView);
          }

        };
      }

      @Override
      RoleOperator<ConstMapping.OfInt> makeConcrete(Comparator<? super T> comparator) {
        return new EquivalenceConstantRoleOperatorBase() {

          @Override
          public ConstMapping.OfInt relativeRefining(ConstMapping.OfInt in,
              ConstMapping.OfInt toRefine) {
            return Equivalences.refiningStrongStructuralEquivalence(numNodes, positionView,
                toRefine, comparator);
          }

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.strongStructuralEquivalence(numNodes, positionView, comparator);
          }

        };
      }

      @Override
      RoleOperator<ConstMapping.OfInt> makeConcrete(PartialComparator<? super T> comparator) {
        return new EquivalenceConstantRoleOperatorBase() {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.strongStructuralEquivalence(numNodes, positionView, comparator);
          }

        };
      }
    };
  }

  @Override
  public <T> RoleOperatorBuilder<T, OfInt> of(int numNodes,
      TransposableNetworkView<? extends T, ? extends T> positionView) {
    if (positionView instanceof NetworkView<?, ?>) {
      return of(numNodes, (NetworkView<? extends T, ? extends T>) positionView);
    }
    return new AbstractNoBiPredicateRoleOperatorBuilder<T, ConstMapping.OfInt>() {
      @Override
      RoleOperator<ConstMapping.OfInt> makeConcrete() {
        return new EquivalenceConstantRoleOperatorBase() {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.strongStructuralEquivalence(numNodes, positionView);
          }

        };
      }

      @Override
      RoleOperator<ConstMapping.OfInt> makeConcrete(Comparator<? super T> comparator) {
        return new EquivalenceConstantRoleOperatorBase() {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.strongStructuralEquivalence(numNodes, positionView, comparator);
          }

        };
      }

      @Override
      RoleOperator<ConstMapping.OfInt> makeConcrete(PartialComparator<? super T> comparator) {
        return new EquivalenceConstantRoleOperatorBase() {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.strongStructuralEquivalence(numNodes, positionView, comparator);
          }

        };
      }
    };
  }

}
