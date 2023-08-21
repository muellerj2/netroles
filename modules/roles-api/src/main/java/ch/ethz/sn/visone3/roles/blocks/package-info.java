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
/**
 * Role, distance and other operators for representing appropriate notions of
 * role.
 * 
 * <p>
 * This package is organized as follows:
 * <ul>
 * <li>The Operators class offers methods for composing operators in a
 * data-flow-like manner. The operators can be composed sequentially (series(),
 * compose*()) or in parallel working on the same initial input followed by a
 * reduction operation (parallel()).</li>
 * <li>The RoleOperators class provides builders to construct operators for
 * established notions of role on the lattices of equivalences, rankings and
 * binary relations. Moreover, it also offers basic operators working on these
 * lattices.</li>
 * <li>The DistanceOperators class provides builders to construct operators
 * producing distances based on established notions of role, as well as some
 * basic operators working on distances.</li>
 * <li>The Reducers class provides reduction operations for equivalences,
 * rankings, binary relations and distances. These are intended to be used in
 * conjunction with the Operators.parallel() methods.</li>
 * <li>The Converters class provides operators representing conversion
 * operations between the different kinds of role structure representations
 * (equivalence, ranking, binary relation) and distance matrices.
 * <li>
 * </ul>
 */
package ch.ethz.sn.visone3.roles.blocks;
