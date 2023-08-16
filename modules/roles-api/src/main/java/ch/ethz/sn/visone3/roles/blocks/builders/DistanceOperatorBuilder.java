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
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

import ch.ethz.sn.visone3.roles.blocks.Operator;
import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

/**
 * This builder constructs an operator producing pairwise distances of nodes
 * based on network position, role structure, and the chosen substitution
 * mechanism between ties. The builder provides settings for comparators, which
 * restrict and thus refine the set of substitutions between ties, as well as
 * cost functions, which assign costs for substitution pairs of ties or failing
 * to substitute a tie at all.
 * 
 * @param <T> type for ties.
 * @param <U> type for role structure.
 */
public interface DistanceOperatorBuilder<T, U> extends
    OperatorBuilder<T, Operator<U, IntDistanceMatrix>, DistanceOperatorBuilder<T, U>>,
    DistanceOperatorBuilderBase<T, U, DistanceOperatorBuilder<T, U>, Comparator<? super T>, //
        PartialComparator<? super T>, BiPredicate<? super T, ? super T>, //
        ToIntBiFunction<? super T, ? super T>, ToIntFunction<? super T>> {
}
