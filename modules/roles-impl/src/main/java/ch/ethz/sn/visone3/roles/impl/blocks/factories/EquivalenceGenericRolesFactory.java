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

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.ConstMapping.OfInt;
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.blocks.builders.GenericRoleOperatorBuilder;
import ch.ethz.sn.visone3.roles.blocks.factories.EquitableLooseGenericRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.impl.algorithms.Equivalences;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

class EquivalenceGenericRolesFactory extends
    ExactLooseBuilderFactoryBase<EquitableLooseGenericRoleOperatorBuilderFactory<ConstMapping.OfInt>>
    implements EquitableLooseGenericRoleOperatorBuilderFactory<ConstMapping.OfInt> {

  @Override
  public <T> GenericRoleOperatorBuilder<T, ConstMapping.OfInt> of(int numNodes,
      NetworkView<? extends T, ? extends T> positionView) {
    boolean exact = isExact();
    return new AbstractGenericNoBiPredicateRoleOperatorBuilder<T, ConstMapping.OfInt>() {

      @Override
      RoleOperator<ConstMapping.OfInt> makeConcrete() {
        return !exact ? new EquivalenceTraitsAdjustableOperator(traits) {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakEquivalence(numNodes, positionView);
          }
        } : new EquivalenceTraitsAdjustableOperator(traits) {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakExactEquivalence(numNodes, positionView);
          }
        };
      }

      @Override
      RoleOperator<ConstMapping.OfInt> makeConcreteWeak(
          Function<? super OfInt, Comparator<? super T>> comparator) {
        return !exact ? new EquivalenceTraitsAdjustableOperator(traits) {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakEquivalence(numNodes, positionView, comparator.apply(in));
          }
        } : new EquivalenceTraitsAdjustableOperator(traits) {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakExactEquivalence(numNodes, positionView, comparator.apply(in));
          }
        };
      }

      @Override
      RoleOperator<ConstMapping.OfInt> makeConcretePartial(
          Function<? super OfInt, PartialComparator<? super T>> comparator) {
        return !exact ? new EquivalenceTraitsAdjustableOperator(traits) {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakEquivalence(numNodes, positionView, comparator.apply(in));
          }
        } : new EquivalenceTraitsAdjustableOperator(traits) {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakExactEquivalence(numNodes, positionView, comparator.apply(in));
          }
        };
      }
    };
  }

  @Override
  public <T> GenericRoleOperatorBuilder<T, ConstMapping.OfInt> of(int numNodes,
      TransposableNetworkView<? extends T, ? extends T> positionView) {
    if (positionView instanceof NetworkView<?, ?>) {
      return of(numNodes, (NetworkView<? extends T, ? extends T>) positionView);
    }
    boolean exact = isExact();
    return new AbstractGenericNoBiPredicateRoleOperatorBuilder<T, ConstMapping.OfInt>() {

      @Override
      RoleOperator<ConstMapping.OfInt> makeConcrete() {
        return !exact ? new EquivalenceTraitsAdjustableOperator(traits) {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakEquivalence(numNodes, positionView);
          }
        } : new EquivalenceTraitsAdjustableOperator(traits) {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakExactEquivalence(numNodes, positionView);
          }
        };
      }

      @Override
      RoleOperator<ConstMapping.OfInt> makeConcreteWeak(
          Function<? super OfInt, Comparator<? super T>> comparator) {
        return !exact ? new EquivalenceTraitsAdjustableOperator(traits) {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakEquivalence(numNodes, positionView, comparator.apply(in));
          }
        } : new EquivalenceTraitsAdjustableOperator(traits) {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakExactEquivalence(numNodes, positionView, comparator.apply(in));
          }
        };
      }

      @Override
      RoleOperator<ConstMapping.OfInt> makeConcretePartial(
          Function<? super OfInt, PartialComparator<? super T>> comparator) {
        return !exact ? new EquivalenceTraitsAdjustableOperator(traits) {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakEquivalence(numNodes, positionView, comparator.apply(in));
          }
        } : new EquivalenceTraitsAdjustableOperator(traits) {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakExactEquivalence(numNodes, positionView, comparator.apply(in));
          }
        };
      }
    };
  }

}
