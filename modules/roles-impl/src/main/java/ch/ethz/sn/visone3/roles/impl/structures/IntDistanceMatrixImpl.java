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

import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;

public class IntDistanceMatrixImpl implements IntDistanceMatrix {

  private int[][] mat_;

  public IntDistanceMatrixImpl(int[][] mat) {
    this.mat_ = mat;
  }

  @Override
  public int getDomainSize() {
    return mat_.length;
  }

  @Override
  public int getDistance(int i, int j) {
    return mat_[i][j];
  }

  @Override
  public int[][] asMatrix() {
    return mat_;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof IntDistanceMatrix)) {
      return false;
    }
    return distancesEqual(this, (IntDistanceMatrix) obj);
  }

  @Override
  public int hashCode() {
    return hashCode(this);
  }

  @Override
  public String toString() {
    return toString(this);
  }

  static boolean distancesEqual(IntDistanceMatrix matrix1, IntDistanceMatrix matrix2) {
    final int size = matrix1.getDomainSize();
    if (size != matrix2.getDomainSize()) {
      return false;
    }
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (matrix1.getDistance(i, j) != matrix2.getDistance(i, j)) {
          return false;
        }
      }
    }
    return true;
  }

  static int hashCode(IntDistanceMatrix matrix) {
    final int size = matrix.getDomainSize();
    int hash = 1;
    for (int i = 0; i < size; ++i) {
      int innerHash = 1;
      for (int j = 0; j < size; ++j) {
        innerHash = 31 * innerHash + matrix.getDistance(i, j);
      }
      hash = 31 * hash + innerHash;
    }
    return hash;
  }

  static String toString(IntDistanceMatrix matrix) {
    StringBuilder builder = new StringBuilder();
    final int size = matrix.getDomainSize();
    builder.append('{');
    builder.append(size);
    builder.append(", [");
    if (size > 0) {
      builder.append('[');
    }
    for (int i = 0; i < size; ++i) {
      if (i > 0) {
        builder.append("], [");
      }
      for (int j = 0; j < size; ++j) {
        if (j > 0) {
          builder.append(",");
        }
        builder.append(matrix.getDistance(i, j));
      }
    }
    if (size > 0) {
      builder.append(']');
    }
    builder.append("]}");
    return builder.toString();
  }
}
