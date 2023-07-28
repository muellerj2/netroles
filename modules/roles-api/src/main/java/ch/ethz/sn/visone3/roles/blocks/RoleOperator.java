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

package ch.ethz.sn.visone3.roles.blocks;

/**
 * This interface describes the fundamental operations supported by all commonly used notions of
 * roles: relative role ({@link #relative(Object)}), role restriction ({@link #restrict(Object)}),
 * role extension ({@link #extend(Object)}) as well as role interior ({@link #interior(Object)}) and
 * closure ({@link #closure(Object)}).
 * 
 * <p>
 * For more details on the nature and properties of these operations, particularly when relative
 * role is isotone, see the papers
 * 
 * <p>
 * Julian Müller, Ulrik Brandes (2019). The evolution of roles. ASONAM '19, August 27-30, 2019,
 * Vancouver, BC, Canada, pp. 406-413, ACM.
 * 
 * <p>
 * Julian Müller, Ulrik Brandes (2022). The evolution of roles. Social Networks 68:195-208, 2022.
 * 
 *
 * @param <T>
 *          A type encoding underlying representation of role structure between nodes in the
 *          network; usually denotes an equivalence
 *          ({@link ch.ethz.sn.visone3.lang.ConstMapping.OfInt}), a ranking
 *          ({@link ch.ethz.sn.visone3.roles.structures.Ranking}) or an arbitrary binary relation
 *          ({@link ch.ethz.sn.visone3.roles.structures.BinaryRelation})
 */
public interface RoleOperator<T> extends RoleConverter<T, T> {

  /**
   * Determines a role structure relative to some given structure between nodes.
   * 
   * @param in
   *          the input relation
   * @return the role relation relative to the given input relation {@code in}
   */
  T relative(T in);

  /**
   * Determines the coarsest role structure that refines both the role structure relative to a given
   * structure between vertices and a second given role structure.
   * 
   * @param in
   *          the role structure which the output role structure is relative to
   * @param toRefine
   *          a relation which the output role structure is refining
   * @return the coarsest role structure which refines both the structure {@code toRefine} and the
   *         role structure relative to the structure {@code in}
   */
  T relativeRefining(T in, T toRefine);

  /**
   * Determines the finest role structure that coarsens both the role structure relative to a given
   * structure and a second given role structure.
   * 
   * @param in
   *          the role structure which the output role structure is relative to
   * @param toCoarsen
   *          a role structure which the output role structure is coarsening
   * @return the finest role structure which coarsens both the role structure {@code toCoarsen} and
   *         the role structure relative to the structure {@code in}
   */
  T relativeCoarsening(T in, T toCoarsen);

  /**
   * Determines the role restriction of an input role structure.
   * 
   * @param in
   *          the input role structure
   * @return the role restriction of the given input role structure {@code in}
   */
  default T restrict(T in) {
    return isNonincreasing() ? relative(in) : relativeRefining(in, in);
  }

  /**
   * Determines the role extension of a role structure.
   * 
   * @param in
   *          the input role structure
   * @return the role extension of the given input role structure {@code in}
   */
  default T extend(T in) {
    return isNondecreasing() ? relative(in) : relativeCoarsening(in, in);
  }

  /**
   * Determines the closure for a role structure.
   * 
   * @param in
   *          the input role structure
   * @return the closure for {@code in}
   */
  default T closure(T in) {
    T prev;
    do {
      prev = in;
      in = extend(in);
    } while (!prev.equals(in));
    return in;
  }

  /**
   * Determines the interior of an input role structure.
   * 
   * @param in
   *          the input role structure
   * @return the interior for {@code in}
   */
  default T interior(T in) {
    T prev;
    do {
      prev = in;
      in = restrict(in);
    } while (!prev.equals(in));
    return in;
  }

  /**
   * Determines a role structure relative to a specified input structure.
   * 
   * <p>
   * Prefer to use {@link #relative(Object)} instead.
   * 
   * @param in
   *          the input role structure
   * @return the role structure relative to the input structure {@code in}
   * @see #relative(Object)
   */
  @Override
  default T apply(T in) {
    return relative(in);
  }

  /**
   * Determines a role structure relative to a specified input structure.
   * 
   * <p>
   * Prefer to use {@link #relative(Object)} instead.
   * 
   * @param in
   *          the input role structure
   * @return the role structure relative to the input structure {@code in}
   * @see #relative(Object)
   */
  @Override
  default T convert(T in) {
    return relative(in);
  }

  /**
   * Determines the coarsest role structure that refines both the role structure relative to a given
   * structure and a second given role structure.
   * 
   * <p>
   * Prefer to use {@link #relativeRefining(Object, Object)} instead.
   * 
   * @param in
   *          the role structure which the output role structure is relative to
   * @param toRefine
   *          a relation which the output role structure is refining
   * @return the coarsest role structure which refines both the structure {@code toRefine} and the
   *         role structure relative to the structure {@code in}
   */
  @Override
  default T convertRefining(T in, T toRefine) {
    return relativeRefining(in, toRefine);
  }

  /**
   * Determines the finest role structure that coarsens both the role structure relative to a given
   * structure and a second given role structure.
   * 
   * <p>
   * Prefer to use {@link #relativeCoarsening(Object, Object)} instead.
   * 
   * @param in
   *          the role structure which the output role structure is relative to
   * @param toCoarsen
   *          a role structure which the output role structure is coarsening
   * @return the finest role structure which coarsens both the role structure {@code toCoarsen} and
   *         the role structure relative to the structure {@code in}
   */
  @Override
  default T convertCoarsening(T in, T toCoarsen) {
    return relativeCoarsening(in, toCoarsen);
  }

}
