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

package ch.ethz.sn.visone3.roles.lattice;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.ConstMapping.OfInt;
import ch.ethz.sn.visone3.roles.blocks.RoleOperator;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.Ranking;

import java.util.function.Predicate;

/**
 * Provides enumerators on stable role structures with respect to role extension and
 * restriction.
 *
 */
public class StableRolesEnumeration {

  private StableRolesEnumeration() {
  }

  /**
   * Factory to create enumerators for stable role structures.
   * 
   * @param <T>
   *          the type of role structure
   */
  public interface Factory<T> {

    /**
     * Enumerates stable role structures under role extension.
     * 
     * @param roleOp
     *          the (isotone) role operator whose stable role structures are to be enumerated
     * @param initial
     *          the role structure to start the search from; any enumerated stable role structures
     *          are coarsenings of this one
     * @return an iterable that allows to enumerate the stable role structures under role extension
     */
    default Iterable<T> stableRolesUnderExtension(RoleOperator<T> roleOp, T initial) {
      return stableRolesUnderExtension(roleOp, initial, x -> false);
    }

    /**
     * Enumerates stable role structures under role extension.
     * 
     * @param roleOp
     *          the (isotone) role operator whose stable role structures are to be enumerated
     * @param initial
     *          the role structure to start the search from; any enumerated stable role structures
     *          are coarsenings of this one
     * @param skipElement
     *          predicate that says whether this element and all succeeding lattice elements should
     *          be skipped during the enumeration
     * @return an iterable that allows to enumerate the stable role structures under role extension
     */
    Iterable<T> stableRolesUnderExtension(RoleOperator<T> roleOp, T initial,
        Predicate<T> skipElement);

    /**
     * Enumerates stable role structures under role restriction.
     * 
     * @param roleOp
     *          the (isotone) role operator whose stable role structures are to be enumerated
     * @param initial
     *          the role structure to start the search from; any enumerated stable role
     *          structures are refinements of this one
     * @return an iterable that allows to enumerate the stable role structures under role
     *         restriction
     */
    default Iterable<T> stableRolesUnderRestriction(RoleOperator<T> roleOp, T initial) {
      return stableRolesUnderRestriction(roleOp, initial, x -> false);
    }

    /**
     * Enumerates stable role structures under role restriction.
     * 
     * @param roleOp
     *          the (isotone) role operator whose stable role structures are to be enumerated
     * @param initial
     *          the role structure to start the search from; any enumerated stable role
     *          structures are refinements of this one
     * @param skipElement
     *          predicate that says whether this element and all preceding lattice elements should
     *          be skipped during the enumeration
     * @return an iterable that allows to enumerate the stable role structures under role
     *         restriction
     */
    Iterable<T> stableRolesUnderRestriction(RoleOperator<T> roleOp, T initial,
        Predicate<T> skipElement);
  }

  public static final Factory<BinaryRelation> BINARYRELATION = new Factory<BinaryRelation>() {

    @Override
    public Iterable<BinaryRelation> stableRolesUnderExtension(RoleOperator<BinaryRelation> roleOp,
        BinaryRelation initial, Predicate<BinaryRelation> skipElement) {
      return LatticeEnumerator.enumerateLattice(roleOp::closure, () -> initial,
          SuccessorAndPredecessorIterators::successorRelations, skipElement);
    }

    @Override
    public Iterable<BinaryRelation> stableRolesUnderRestriction(RoleOperator<BinaryRelation> roleOp,
        BinaryRelation initial, Predicate<BinaryRelation> skipElement) {
      return LatticeEnumerator.enumerateLattice(roleOp::interior, () -> initial,
          SuccessorAndPredecessorIterators::predecessorRelations, skipElement);
    }

  };

  public static final Factory<Ranking> RANKING = new Factory<Ranking>() {

    @Override
    public Iterable<Ranking> stableRolesUnderExtension(RoleOperator<Ranking> roleOp,
        Ranking initial, Predicate<Ranking> skipElement) {
      return LatticeEnumerator.enumerateLattice(roleOp::closure, () -> initial,
          SuccessorAndPredecessorIterators::successorRankings, skipElement);
    }

    @Override
    public Iterable<Ranking> stableRolesUnderRestriction(RoleOperator<Ranking> roleOp,
        Ranking initial, Predicate<Ranking> skipElement) {
      return LatticeEnumerator.enumerateLattice(roleOp::interior, () -> initial,
          SuccessorAndPredecessorIterators::predecessorRankings, skipElement);
    }
  };

  public static final Factory<ConstMapping.OfInt> EQUIVALENCE = new Factory<ConstMapping.OfInt>() {

    @Override
    public Iterable<OfInt> stableRolesUnderExtension(RoleOperator<ConstMapping.OfInt> roleOp,
        ConstMapping.OfInt initial, Predicate<ConstMapping.OfInt> skipElement) {
      return LatticeEnumerator.enumerateLattice(roleOp::closure, () -> initial,
          SuccessorAndPredecessorIterators::successorEquivalences, skipElement);
    }

    @Override
    public Iterable<OfInt> stableRolesUnderRestriction(RoleOperator<ConstMapping.OfInt> roleOp,
        ConstMapping.OfInt initial, Predicate<ConstMapping.OfInt> skipElement) {
      return LatticeEnumerator.enumerateLattice(roleOp::interior, () -> initial,
          SuccessorAndPredecessorIterators::predecessorEquivalences, skipElement);
    }
  };
}
