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

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

import ch.ethz.sn.visone3.lang.PrimitiveIterable.OfInt;
import ch.ethz.sn.visone3.roles.structures.RelationBase;
import ch.ethz.sn.visone3.roles.structures.Relations;

/**
 * Dense matrix representation of a binary relation or ranking, but the content
 * is lazily computed and only stored when it is requested for the first time.
 */
public class LazyCachedBinaryRelationMatrixImpl implements BinaryRelationOrRanking {

  private boolean[] mat_;
  private boolean[] evaluated_;
  private final int size_;
  private final BiIntPredicate lazyEvaluator_;
  private int unevaluatedDyads_;
  private int relationships_;
  private int hashcode_;
  private boolean hasHashcode_;

  /**
   * Constructs the representation of a relation.
   * 
   * @param size          the size of the relation's domain.
   * @param lazyEvaluator function that is called to lazily compute the content of
   *                      this relation.
   */
  public LazyCachedBinaryRelationMatrixImpl(int size, BiIntPredicate lazyEvaluator) {
    this.size_ = size;
    this.lazyEvaluator_ = lazyEvaluator;
    this.unevaluatedDyads_ = 1;
    this.hasHashcode_ = false;
  }

  private void constructMatrix() {
    this.mat_ = new boolean[size_ * size_];
    this.evaluated_ = new boolean[size_ * size_];
    unevaluatedDyads_ = size_ * size_;
    relationships_ = 0;
  }

  private void checkMatrixConstructed() {
    if (mat_ == null) {
      constructMatrix();
    }
  }

  private int indexGreaterThan(int i, int j) {
    return i * size_ + j;
  }

  private boolean initializeValueAt(int i, int j, int index) {
    boolean value = lazyEvaluator_.testInt(i, j);
    mat_[index] = value;
    evaluated_[index] = true;
    --unevaluatedDyads_;
    if (value) {
      ++relationships_;
    }
    return value;
  }

  private boolean evaluateGreaterThan(int i, int j) {
    int index = indexGreaterThan(i, j);
    if (evaluated_[index]) {
      return mat_[index];
    }
    return initializeValueAt(i, j, index);
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
    checkMatrixConstructed();
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
    checkMatrixConstructed();
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
    checkMatrixConstructed();
    return () -> new RelatedIterator(i, this::evaluateLessThan);
  }

  @Override
  public OfInt iterateInRelationFrom(int i) {
    checkMatrixConstructed();
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
    checkMatrixConstructed();
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
    if (unevaluatedDyads_ > 0) {
      checkMatrixConstructed();
      int n = domainSize();
      for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
          int index = indexGreaterThan(i, j);
          if (!evaluated_[index]) {
            initializeValueAt(i, j, index);
          }
        }
      }
    }
    return relationships_;
  }

  @Override
  public int hashCode() {
    if (!hasHashcode_) {
      hashcode_ = Relations.hashCode(this);
      hasHashcode_ = true;
    }
    return hashcode_;
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
