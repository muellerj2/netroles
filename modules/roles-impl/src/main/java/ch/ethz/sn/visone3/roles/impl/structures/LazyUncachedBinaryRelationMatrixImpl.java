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
package ch.ethz.sn.visone3.roles.impl.structures;

import ch.ethz.sn.visone3.lang.PrimitiveIterable.OfInt;
import ch.ethz.sn.visone3.roles.structures.RelationBase;
import ch.ethz.sn.visone3.roles.structures.Relations;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

public class LazyUncachedBinaryRelationMatrixImpl implements BinaryRelationOrRanking {

  private final int size_;
  private final BiIntPredicate lazyEvaluator_;
  private int relationships_;

  public LazyUncachedBinaryRelationMatrixImpl(int size, BiIntPredicate lazyEvaluator) {
    this.size_ = size;
    this.lazyEvaluator_ = lazyEvaluator;
    this.relationships_ = -1;
  }

  private boolean evaluateGreaterThan(int i, int j) {
    return lazyEvaluator_.testInt(i, j);
  }

  private boolean evaluateLessThan(int i, int j) {
    return evaluateGreaterThan(j, i);
  }

  @Override
  public int domainSize() {
    return size_;
  }

  @Override
  public boolean contains(int i, int j) {
    return evaluateGreaterThan(i, j);
  }

  @Override
  public boolean isRandomAccess() {
    return true;
  }

  @Override
  public boolean isLazilyEvaluated() {
    return true;
  }

  private class RelatedIterator implements PrimitiveIterator.OfInt {

    private int pos = 0;
    private int src_;
    private int prev = -1;
    private BiIntPredicate evaluator_;

    public RelatedIterator(int src, BiIntPredicate indexer) {
      evaluator_ = indexer;
      src_ = src;
      moveNext();
    }

    private void moveNext() {
      while (pos < domainSize() && !evaluator_.testInt(src_, pos)) {
        ++pos;
      }
    }

    @Override
    public boolean hasNext() {
      return pos < domainSize();
    }

    @Override
    public int nextInt() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      prev = pos++;
      moveNext();
      return prev;
    }

    // @Override
    // public void remove() {
    // ranking_[indexer_.applyAsInt(src_, prev)] = false;
    // }

  }

  private int countGeneric(int i, BiIntPredicate indexer) {
    int n = 0;
    for (int j = 0; j < domainSize(); ++j) {
      if (indexer.testInt(i, j)) {
        ++n;
      }
    }
    return n;
  }

  @Override
  public OfInt iterateInRelationTo(int i) {
    return () -> new RelatedIterator(i, this::evaluateLessThan);
  }

  @Override
  public OfInt iterateInRelationFrom(int i) {
    return () -> new RelatedIterator(i, this::evaluateGreaterThan);
  }

  @Override
  public int countInRelationTo(int i) {
    return countGeneric(i, this::evaluateLessThan);
  }

  @Override
  public int countInRelationFrom(int i) {
    return countGeneric(i, this::evaluateGreaterThan);
  }

  @Override
  public int countSymmetricRelationPairs(int i) {
    int n = 0;
    for (int j = 0; j < domainSize(); ++j) {
      if (evaluateGreaterThan(i, j) && evaluateLessThan(i, j)) {
        ++n;
      }
    }
    return n;
  }

  @Override
  public int countRelationPairs() {
    if (relationships_ < 0) {
      int n = domainSize();
      relationships_ = 0;
      for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
          if (evaluateGreaterThan(i, j)) {
            ++relationships_;
          }
        }
      }
    }
    return relationships_;
  }

  @Override
  public int hashCode() {
    return Relations.hashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof RelationBase)) {
      return false;
    }
    return equals((RelationBase) obj);
  }

  @Override
  public String toString() {
    return Relations.toString(this);
  }

}
