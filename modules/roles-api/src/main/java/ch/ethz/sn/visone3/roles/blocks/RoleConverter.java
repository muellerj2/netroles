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
 * Converts some object of type {@code T} to a role structure representation of type {@code U}.
 *
 * @param <T>
 *          the input type
 * @param <U>
 *          the type of the resulting role structure
 */
public interface RoleConverter<T, U> extends Operator<T, U> {

  /**
   * Applies the conversion to the given input object.
   * 
   * @param in
   *          the input object
   * @return the resulting role structure
   */
  public U convert(T in);

  /**
   * Determines the coarsest role structure that refines both the conversion of the given input
   * object and the second given role structure.
   * 
   * @param in
   *          the input object to convert
   * @param toRefine
   *          the role structure to refine
   * @return the coarsest role structure that refines both the conversion of {@code in} and the
   *         structure {@code toRefine}.
   */
  public U convertRefining(T in, U toRefine);

  /**
   * Determines the finest role structure that coarsens both the conversion of the given input
   * object and the second given role structure.
   * 
   * @param in
   *          the input object to convert
   * @param toCoarsen
   *          the role structure to coarsen
   * @return the finest role structure that coarsens both the conversion of {@code in} and the
   *         structure {@code toRefine}.
   */
  public U convertCoarsening(T in, U toCoarsen);

  default U apply(T in) {
    return convert(in);
  }
}
