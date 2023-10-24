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
 * This package contains algorithms for enumerating stable role structures and
 * other fixed points on finite lattices, including the necessary auxiliary
 * methods to apply the algorithms on the lattices of equivalences, rankings and
 * binary relations.
 * 
 * <p>
 * The {@link ch.ethz.sn.visone3.roles.lattice.DepthFirstSearchEnumerator} and
 * {@link ch.ethz.sn.visone3.roles.lattice.BacktrackSearchEnumerator} implement
 * two algorithms to list fixed points of increasing or decreasing monotone
 * functions. Both of them perform better than brute-force
 * 
 * <p>
 * For role structures on equivalences, rankings and binary relations, you may
 * use the {@link ch.ethz.sn.visone3.roles.lattice.StableRolesEnumeration}
 * class, which applies the appropriate algorithm given the properties of the
 * underlying lattice.
 * 
 * <p>
 * If you want to choose the algorithm yourself or want to apply to different
 * kinds of lattices, you should consider the following advantages and
 * disadvantages:
 * <ul>
 * <li>The {@link ch.ethz.sn.visone3.roles.lattice.BacktrackSearchEnumerator}
 * class has to do little bookkeeping. Moreover, it often works well when the
 * lattice elements can be decomposed into several dimensions and the meet
 * operation (if the monotone function is increasing) or the join operation (if
 * decreasing) essentially acts component-wise in the dimensional decomposition.
 * In this case, it also distributes evaluations of the monotone functions more
 * equally between outputting fixed points.</li>
 * <li>The {@link ch.ethz.sn.visone3.roles.lattice.DepthFirstSearchEnumerator}
 * class performs more bookkeeping and does not distribute evaluations of
 * monotone functions as equally, but it works better when the meet or join
 * operation and the dimensional decomposition do not mesh as well. For example,
 * this is the case for the join of equivalences, since the join of all
 * equivalences having the same projection to some number of equivalences does
 * not have the same projection; rather, there might be an exponential number of
 * equivalences that are maximal among those having the same projection, and the
 * algorithm in
 * {@link ch.ethz.sn.visone3.roles.lattice.BacktrackSearchEnumerator} must test
 * all of these to ascertain whether there is a fixed point with this projection
 * or not.</li>
 * </ul>
 * 
 */
package ch.ethz.sn.visone3.roles.lattice;
