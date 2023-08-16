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

package ch.ethz.sn.visone3.roles.blocks.builders;

import java.util.Comparator;
import java.util.function.BiPredicate;

import ch.ethz.sn.visone3.roles.util.PartialComparator;

/**
 * This interface represents a builder to construct operators based on the
 * network structure and configurable with a comparator among represented ties
 * to refine the substitution among ties.
 * 
 * @param <T> type of represented ties.
 * @param <U> type of the produced operator.
 * @param <V> full type of this role operator.
 */
public interface OperatorBuilder<T, U, V extends OperatorBuilder<T, U, V>>
    extends OperatorBuilderBase<T, U, V, //
        Comparator<? super T>, //
        PartialComparator<? super T>, //
        BiPredicate<? super T, ? super T>> {

  /**
   * Sets the weakly ordered comparator for the underlying substitution mechanism.
   * 
   * <p>
   * A tie can be substituted by another tie if and only if the former is less
   * than or equal to the latter.
   * 
   * @param comp the weakly ordered comparator.
   * @return this builder.
   */
  default V comp(Comparator<? super T> comp) {
    return compWeak(comp);
  }

  /**
   * Sets the partially ordered comparator for the underlying substitution
   * mechanism.
   * 
   * <p>
   * A tie can be substituted by another tie if and only if the former is less
   * than or equal to the latter.
   * 
   * @param comp the partially ordered comparator.
   * @return this builder.
   */
  default V comp(PartialComparator<? super T> comp) {
    return compPartial(comp);
  }

  /**
   * Sets a bipredicate used for comparison of ties in the underlying substitution
   * mechanism.
   * 
   * <p>
   * A tie {@code t} can be substituted by another tie {@code u} if and only if
   * the predicate produces true for arguments {@code (t, u)}.
   * 
   * @param comp the bipredicate.
   * @return this builder.
   */
  default V comp(BiPredicate<? super T, ? super T> comp) {
    return compPredicate(comp);
  }

}
