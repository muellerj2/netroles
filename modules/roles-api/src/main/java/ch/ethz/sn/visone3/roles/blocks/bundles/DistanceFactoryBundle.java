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

package ch.ethz.sn.visone3.roles.blocks.bundles;

import ch.ethz.sn.visone3.roles.blocks.Operator;
import ch.ethz.sn.visone3.roles.blocks.factories.DistanceBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.VariableDistanceBuilderFactory;
import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;

/**
 * Bundle of factories to produce distances based on common role types/substitution mechanisms on
 * the given role structure type.
 *
 * @param <T>
 *          the role structure type
 */
public interface DistanceFactoryBundle<T>
    extends FactoryBundle<T, IntDistanceMatrix, Operator<T, IntDistanceMatrix>, //
        VariableDistanceBuilderFactory<T>, DistanceBuilderFactory<T>, DistanceBuilderFactory<T>> {

}
