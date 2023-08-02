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

import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;

import java.util.Arrays;
import java.util.function.IntBinaryOperator;

public class LazyIntDistanceMatrixImpl implements IntDistanceMatrix {

  private int[][] mat_;
  private final int size_;
  private IntBinaryOperator lazyEvaluator_;
  private int unevaluatedCount_;

  public LazyIntDistanceMatrixImpl(int size, IntBinaryOperator lazyEvaluator) {
    size_ = size;
    lazyEvaluator_ = lazyEvaluator;
  }

  @Override
  public int getDomainSize() {
    return size_;
  }

  @Override
  public int getDistance(int i, int j) {
    if (mat_ == null) {
      mat_ = new int[size_][size_];
      for (int[] row : mat_) {
        Arrays.fill(row, Integer.MIN_VALUE);
      }
      unevaluatedCount_ = size_ * size_;
    }
    if (mat_[i][j] == Integer.MIN_VALUE) {
      mat_[i][j] = lazyEvaluator_.applyAsInt(i, j);
      --unevaluatedCount_;
    }
    return mat_[i][j];
  }

  @Override
  public int[][] asMatrix() {
    if (mat_ == null) {
      final int n = size_;
      mat_ = new int[n][n];
      for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
          mat_[i][j] = lazyEvaluator_.applyAsInt(i, j);
        }
      }
      unevaluatedCount_ = 0;
    } else if (unevaluatedCount_ != 0) {
      final int n = size_;
      for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
          if (mat_[i][j] == Integer.MIN_VALUE) {
            mat_[i][j] = lazyEvaluator_.applyAsInt(i, j);
          }
        }
      }
      unevaluatedCount_ = 0;
    }
    return mat_;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof IntDistanceMatrix)) {
      return false;
    }
    return IntDistanceMatrixImpl.distancesEqual(this, (IntDistanceMatrix) obj);
  }

  @Override
  public int hashCode() {
    return IntDistanceMatrixImpl.hashCode(this);
  }

  @Override
  public String toString() {
    return IntDistanceMatrixImpl.toString(this);
  }
}
