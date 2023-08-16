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
import java.util.function.IntBinaryOperator;

import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveIterable.OfInt;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.roles.structures.RelationBase;
import ch.ethz.sn.visone3.roles.structures.RelationBuilder;
import ch.ethz.sn.visone3.roles.structures.Relations;

/**
 * Implementation of a binary relation or ranking backed by a dense matrix.
 */
public class BinaryRelationMatrixImpl
    implements BinaryRelationOrRanking, ReducibleRelationOrRanking {

  private boolean[] ranking_;
  private int size_;
  private int relationships_;
  private int hashCode_;
  private boolean hasHashCode_ = false;

  /**
   * Constructs a binary relation from a two-dimensional boolean matrix.
   * 
   * @param matrix the underlying matrix, such that the pair (i, j) is contained
   *               in the binary relation if {@code matrix[i][j]} is true.
   */
  public BinaryRelationMatrixImpl(boolean[][] matrix) {
    size_ = matrix.length;
    ranking_ = new boolean[size_ * size_];
    for (int i = 0; i < size_; ++i) {
      for (int j = 0; j < size_; ++j) {
        boolean value = matrix[i][j];
        ranking_[indexGreaterThan(i, j)] = value;
        if (value) {
          ++relationships_;
        }
      }
    }
  }

  private BinaryRelationMatrixImpl(int size, PrimitiveList.OfInt sources,
      PrimitiveList.OfInt targets) {
    size_ = size;
    ranking_ = new boolean[size_ * size_];
    for (int i = 0; i < sources.size(); ++i) {
      final int index = indexGreaterThan(sources.getInt(i), targets.getInt(i));
      if (!ranking_[index]) {
        ranking_[index] = true;
        ++relationships_;
      }
    }
  }

  private int indexGreaterThan(int i, int j) {
    return i * size_ + j;
  }

  private int indexLessThan(int i, int j) {
    return indexGreaterThan(j, i);
  }

  private class RelatedIterator implements PrimitiveIterator.OfInt {

    private int pos = 0;
    private int src_;
    private int prev = -1;
    private IntBinaryOperator indexer_;

    public RelatedIterator(int src, IntBinaryOperator indexer) {
      indexer_ = indexer;
      src_ = src;
      moveNext();
    }

    private void moveNext() {
      while (pos < domainSize() && !ranking_[indexer_.applyAsInt(src_, pos)]) {
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

  }

  private int countGeneric(int i, IntBinaryOperator indexer) {
    int n = 0;
    for (int j = 0; j < domainSize(); ++j) {
      if (ranking_[indexer.applyAsInt(i, j)]) {
        ++n;
      }
    }
    return n;
  }

  @Override
  public OfInt iterateInRelationTo(int i) {
    return () -> new RelatedIterator(i, this::indexLessThan);
  }

  @Override
  public OfInt iterateInRelationFrom(int i) {
    return () -> new RelatedIterator(i, this::indexGreaterThan);
  }

  @Override
  public int countInRelationTo(int i) {
    return countGeneric(i, this::indexLessThan);
  }

  @Override
  public int countInRelationFrom(int i) {
    return countGeneric(i, this::indexGreaterThan);
  }

  @Override
  public int countSymmetricRelationPairs(int i) {
    int n = 0;
    for (int j = 0; j < domainSize(); ++j) {
      if (ranking_[indexLessThan(i, j)] && ranking_[indexGreaterThan(i, j)]) {
        ++n;
      }
    }
    return n;
  }

  @Override
  public int countRelationPairs() {
    return relationships_;
  }

  @Override
  public int domainSize() {
    return size_;
  }

  @Override
  public boolean contains(int i, int j) {
    return ranking_[indexGreaterThan(i, j)];
  }

  public void remove(int i, int j) {
    ranking_[indexGreaterThan(i, j)] = false;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs instanceof RelationBase) {
      return equals((RelationBase) rhs);
    }
    return false;
  }

  @Override
  public int hashCode() {
    if (!hasHashCode_) {
      hashCode_ = Relations.hashCode(this);
      hasHashCode_ = true;
    }
    return hashCode_;
  }

  @Override
  public String toString() {
    return Relations.toString(this);
  }

  /**
   * Builder to construct a {@link BinaryRelationMatrixImpl} object.
   */
  public static class Builder implements RelationBuilder<ReducibleRelationOrRanking> {

    private PrimitiveList.OfInt sources_ = Mappings.newIntList();
    private PrimitiveList.OfInt targets_ = Mappings.newIntList();
    int domain = 0;

    /**
     * Constructs a new builder.
     */
    public Builder() {
    }

    /**
     * Constructs a new builder with the specified initial size of the binary
     * relation's underlying domain.
     * 
     * @param domainSize the initial size of the domain underlying the binary
     *                   relation.
     */
    public Builder(int domainSize) {
      ensureDomainSize(domainSize);
    }

    /**
     * Ensures that the underlying domain of the binary relation has at least the
     * specified size.
     * 
     * @param size the minimal size of the binary relation's underlying domain.
     */
    public void ensureDomainSize(int size) {
      domain = Math.max(domain, size);
    }

    @Override
    public void add(int i, int j) {
      ensureDomainSize(i + 1);
      ensureDomainSize(j + 1);
      sources_.addInt(i);
      targets_.addInt(j);
    }

    @Override
    public ReducibleRelationOrRanking build() {
      return new BinaryRelationMatrixImpl(domain, sources_, targets_);
    }
  }

  @Override
  public boolean isRandomAccess() {
    return true;
  }

  @Override
  public boolean isLazilyEvaluated() {
    return false;
  }
}
