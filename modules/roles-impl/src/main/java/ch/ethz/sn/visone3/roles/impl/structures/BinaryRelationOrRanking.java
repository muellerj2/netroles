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
package ch.ethz.sn.visone3.roles.impl.structures;

import ch.ethz.sn.visone3.lang.PrimitiveIterable.OfInt;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.Ranking;
import ch.ethz.sn.visone3.roles.structures.RelationBase;
import ch.ethz.sn.visone3.roles.structures.Relations;

/**
 * Helper interface to handle the fact that binary relations and rankings cannot
 * be implicitly converted to each other while still providing common algorithms
 * that create rankings under some constraints and other arbitrary binary
 * relations otherwise.
 *
 */
public interface BinaryRelationOrRanking extends BinaryRelation, Ranking {

  default BinaryRelationOrRanking invert() {
    return new BinaryRelationOrRanking() {

      private int hashCode_;
      private boolean hasHashCode_ = false;
      
      @Override
      public OfInt iterateInRelationTo(int i) {
        return BinaryRelationOrRanking.this.iterateInRelationFrom(i);
      }

      @Override
      public OfInt iterateInRelationFrom(int i) {
        return BinaryRelationOrRanking.this.iterateInRelationTo(i);
      }

      @Override
      public int countInRelationTo(int i) {
        return BinaryRelationOrRanking.this.countInRelationFrom(i);
      }

      @Override
      public int countInRelationFrom(int i) {
        return BinaryRelationOrRanking.this.countInRelationTo(i);
      }

      @Override
      public int countSymmetricRelationPairs(int i) {
        return BinaryRelationOrRanking.this.countSymmetricRelationPairs(i);
      }
      
      @Override
      public int countRelationPairs() {
        return BinaryRelationOrRanking.this.countRelationPairs();
      }

      @Override
      public boolean contains(int i, int j) {
        return BinaryRelationOrRanking.this.contains(j, i);
      }

      @Override
      public int domainSize() {
        return BinaryRelationOrRanking.this.domainSize();
      }
      
      @Override
      public int hashCode() {
        if (hasHashCode_) {
          return hashCode_;
        }
        hashCode_ = Relations.hashCode(this);
        hasHashCode_ = true;
        return hashCode_;
      }
      
      @Override
      public boolean equals(Object rhs) {
        if (rhs instanceof RelationBase) {
          return equals((RelationBase)rhs);
        }
        return false;
      }

      @Override
      public String toString() {
        return Relations.toString(this);
      }

      @Override
      public boolean isRandomAccess() {
        return BinaryRelationOrRanking.this.isRandomAccess();
      }

      @Override
      public boolean isLazilyEvaluated() {
        return BinaryRelationOrRanking.this.isLazilyEvaluated();
      }
    };
  }

  @Override
  default boolean equals(RelationBase rhs) {
    return Relations.equals(this, rhs);
  }

  default BinaryRelation asBinaryRelation() {
    return this;
  }
}
