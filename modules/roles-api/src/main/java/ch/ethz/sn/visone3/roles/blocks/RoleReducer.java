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

package ch.ethz.sn.visone3.roles.blocks;

/**
 * Specifies a mechanism to combine role structures into a single one.
 *
 * @param <T>
 *          the role structure type
 */
public interface RoleReducer<T> extends Reducer<T> {

  /**
   * Combines two role structures into one.
   * 
   * @param first
   *          first object
   * @param second
   *          second object
   * @return combination of first and second
   */
  @Override
  T combine(T first, T second);

  /**
   * Determines the coarsest role structure that refines the first and the combination of the two
   * last given role structures.
   * 
   * @param base
   *          a role structure that has to be refined
   * @param first
   *          first role structure for the combination
   * @param second
   *          second role structure for the combination
   * @return the coarsest structure that refines {@code base} and the combination of {@code first}
   *         and {@code second}.
   */
  T refiningCombine(T base, T first, T second);

  /**
   * Determines the finest role structure that coarsens the first and the combination of the two
   * last given role structure.
   * 
   * @param base
   *          a role structure that has to be coarsened
   * @param first
   *          first role structure for the combination
   * @param second
   *          second role structure for the combination
   * @return the finest structure that coarsens {@code base} and the combination of {@code first}
   *         and {@code second}.
   */
  T coarseningCombine(T base, T first, T second);

  /**
   * Determines the coarsest role structure that refines the base and the combined role structures.
   * 
   * @param base
   *          the base role structure
   * @param combined
   *          the combined role structure
   * @return the coarsest structure that refines {@code base} and {@code combined}.
   */
  T refine(T base, T combined);

  /**
   * Determines the finest role structure that coarsens the base and the combined role structures.
   * 
   * @param base
   *          the base role structure
   * @param combined
   *          the combined role structure
   * @return the finest structure that coarsens {@code base} and {@code combined}.
   */
  T coarsen(T base, T combined);
}
