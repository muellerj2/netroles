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
import ch.ethz.sn.visone3.roles.blocks.factories.WeakStructuralRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.impl.algorithms.Equivalences;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

class EquivalenceWeakStructuralRolesFactory
    implements RoleOperatorBuilderFactory<ConstMapping.OfInt>,
    WeakStructuralRoleOperatorBuilderFactory<ConstMapping.OfInt> {

  @Override
  public <T> RoleOperatorBuilder<T, ConstMapping.OfInt> of(NetworkView<? extends T, ? extends T> positionView) {
    return of(positionView, null);
  }

  @Override
  public <T> RoleOperatorBuilder<T, OfInt> of(TransposableNetworkView<? extends T, ? extends T> positionView) {
    return of(positionView, null);
  }

  @Override
  public <T> RoleOperatorBuilder<T, OfInt> of(NetworkView<? extends T, ? extends T> oneDirection,
      NetworkView<? extends T, ? extends T> otherDirectionNonfinal) {
    if (oneDirection == otherDirectionNonfinal) {
      otherDirectionNonfinal = null;
    }
    final NetworkView<? extends T, ? extends T> otherDirection = otherDirectionNonfinal;
    final int numNodes = oneDirection.countNodes();

    if (otherDirection != null && numNodes != otherDirection.countNodes()) {
      throw new IllegalArgumentException("mismatched in node numbers between directions");
    }

    return new AbstractNoBiPredicateRoleOperatorBuilder<T, ConstMapping.OfInt>() {
      @Override
      RoleOperator<ConstMapping.OfInt> makeConcrete() {
        return new EquivalenceConstantRoleOperatorBase() {

          @Override
          public ConstMapping.OfInt relativeRefining(ConstMapping.OfInt in,
              ConstMapping.OfInt toRefine) {
            return Equivalences.refiningWeakStructuralEquivalence(numNodes, oneDirection,
                otherDirection, toRefine);
          }

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakStructuralEquivalence(numNodes, oneDirection, otherDirection);
          }

        };
      }

      @Override
      RoleOperator<ConstMapping.OfInt> makeConcrete(Comparator<? super T> comparator) {
        return new EquivalenceConstantRoleOperatorBase() {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakStructuralEquivalence(numNodes, oneDirection, otherDirection,
                comparator);
          }

        };
      }

      @Override
      RoleOperator<ConstMapping.OfInt> makeConcrete(PartialComparator<? super T> comparator) {
        return new EquivalenceConstantRoleOperatorBase() {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakStructuralEquivalence(numNodes, oneDirection, otherDirection,
                comparator);
          }

        };
      }
    };
  }

  @Override
  public <T> RoleOperatorBuilder<T, OfInt> of(TransposableNetworkView<? extends T, ? extends T> oneDirection,
      TransposableNetworkView<? extends T, ? extends T> otherDirectionNonfinal) {
    if (oneDirection == otherDirectionNonfinal) {
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
    return new AbstractNoBiPredicateRoleOperatorBuilder<T, ConstMapping.OfInt>() {
      @Override
      RoleOperator<ConstMapping.OfInt> makeConcrete() {
        return new EquivalenceConstantRoleOperatorBase() {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakStructuralEquivalence(numNodes, oneDirection, otherDirection);
          }

        };
      }

      @Override
      RoleOperator<ConstMapping.OfInt> makeConcrete(Comparator<? super T> comparator) {
        return new EquivalenceConstantRoleOperatorBase() {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakStructuralEquivalence(numNodes, oneDirection, otherDirection,
                comparator);
          }

        };
      }

      @Override
      RoleOperator<ConstMapping.OfInt> makeConcrete(PartialComparator<? super T> comparator) {
        return new EquivalenceConstantRoleOperatorBase() {

          @Override
          public ConstMapping.OfInt relative(ConstMapping.OfInt in) {
            return Equivalences.weakStructuralEquivalence(numNodes, oneDirection, otherDirection,
                comparator);
          }

        };
      }
    };
  }

  @Override
  public RoleOperatorBuilderFactory<OfInt> unidirectional() {
    return this;
  }

}
