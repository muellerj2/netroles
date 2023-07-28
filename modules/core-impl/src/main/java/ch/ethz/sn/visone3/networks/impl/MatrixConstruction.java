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

package ch.ethz.sn.visone3.networks.impl;

import ch.ethz.sn.visone3.lang.ClassUtils;
import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.Relation;
import ch.ethz.sn.visone3.networks.Relationship;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Common matrix construction code.
 *
 * @see Network#asMatrix(Object, Object, ch.ethz.sn.visone3.lang.ConstMapping)
 */
class MatrixConstruction {

  private MatrixConstruction() {
  }

  /**
   * Creates a copy of the adjacency matrix.
   *
   * <p>
   * The element at position {@code i,j} will be
   * <ul>
   * <li>the observed value for observed entries,</li>
   * <li>the default value for the diagonal for unobserved diagonal entries,</li>
   * <li>the general default value if unobserved.</li>
   * </ul>
   *
   * <p>
   * This provides very limited view on matrices. For more complex needs, and for directly filling
   * primitive matrices, use {@link Relation#getRelationships()} to walk over all observations and
   * fill you matrix structure.
   *
   * @param network
   *          The network for which to produce the adjacency matrix.
   * @param fill
   *          Default value for all values.
   * @param diagonal
   *          Default value for the diagonal.
   * @return new adjacency matrix.
   * @see Network#asMatrix(Object, Object, ConstMapping)
   */
  public static <T> T[][] toMatrix(final Network network, final T fill, final T diagonal,
      final ConstMapping<T> mapping) {
    final Relation r = network.asRelation();
    Class<?> componentType = ClassUtils.wrap(mapping.getComponentType());
    @SuppressWarnings("unchecked")
    final T[][] matrix = (T[][]) Array.newInstance(componentType, r.countLeftDomain(),
        r.countRightDomain());
    // fill with matrix with default value
    if (fill != null) {
      for (final T[] row : matrix) {
        Arrays.fill(row, fill);
      }
    }
    // fill diagonal with diagonal default value
    if (!network.isTwoMode() && diagonal != null) {
      for (int i = 0; i < matrix.length; i++) {
        matrix[i][i] = diagonal;
      }
    }
    // fill with relation values
    for (final Relationship i : r.getRelationships()) {
      matrix[i.getLeft()][i.getRight0()] = mapping.get(i.getIndex());
    }
    return matrix;
  }
}
