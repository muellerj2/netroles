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

package ch.ethz.sn.visone3.roles.blocks.builders;

import ch.ethz.sn.visone3.roles.blocks.Operator;
import ch.ethz.sn.visone3.roles.blocks.OperatorTraits;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * This builder constructs an operator producing producing role structures based on network
 * structure and a user-specified substitution mechanism which can depend on the input role
 * structure.
 * 
 * @param <T>
 *          type for ties
 * @param <U>
 *          type for role structure
 */
public interface GenericOperatorBuilder<T, U, V extends Operator<U, ?>, //
    W extends GenericOperatorBuilder<T, U, V, W>>
    extends OperatorBuilderBase<T, V, W, Function<? super U, Comparator<? super T>>, //
        Function<? super U, PartialComparator<? super T>>, //
        Function<? super U, BiPredicate<? super T, ? super T>>> {

  /**
   * Defines the traits of the operator to construct.
   * 
   * @param traits
   *          the traits
   * @return this builder
   */
  W traits(OperatorTraits traits);
}
