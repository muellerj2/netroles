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

package ch.ethz.sn.visone3.networks;

import ch.ethz.sn.visone3.lang.ClassUtils;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveList;

/**
 * Provides utilities to create networks from matrices.
 */
public final class MatrixSource {
  private MatrixSource() {
  }

  /**
   * Generates a weighted network from a given adjacency matrix. Non-null matrix entries are
   * translated into edges with corresponding weight. For one-mode, undirected networks are returned
   * if the adjacency matrix is given in lower triangular form, else directed networks are returned.
   * Note that this means that returned networks with zero or one node are always undirected.
   * 
   * @param adj
   *          Adjacency matrix. If one-mode is requested and the adjacency matrix is in lower
   *          triangular form, the result is undirected, directed otherwise.
   * @param twomode
   *          Two-mode network flag. If true, the result is a two-mode network, otherwise it is a
   *          one-mode directed or undirected network.
   * @param <T>
   *          Type of value. If this is a wrapper class the corresponding primitive mapping is
   *          returned.
   * @return A weighted network.
   * @throws IllegalArgumentException
   *           if the adjacency matrix does not have the required form.
   */
  public static <T> WeightedNetwork<? extends T, ? extends Mapping<? extends T>> fromAdjacency(
      final T[][] adj, boolean twomode) {
    if (twomode) {
      return fromAdjacency(adj, DyadType.TWO_MODE);
    }
    return fromAdjacencyNocheck(adj, guessOneModeLayout(adj));
  }

  /**
   * Generates a weighted network from a given adjacency matrix according to the given dyad type.
   * Non-null matrix entries are translated into edges with corresponding weight. For one-mode,
   * undirected networks, the adjacency matrix is given in lower triangular form, otherwise it must
   * be in rectangular form with the appropriate number of rows and columns.
   * 
   * @param adj
   *          Adjacency matrix. If one-mode is requested and the adjacency matrix is in lower
   *          triangular form, the result is undirected, directed otherwise.
   * @param type
   *          Dyad type of the returned network.
   * @param <T>
   *          Type of value. If this is a wrapper class the corresponding primitive mapping is
   *          returned.
   * @return A weighted network.
   * @throws IllegalArgumentException
   *           if the adjacency matrix does not have the required form.
   */
  public static <T> WeightedNetwork<? extends T, ? extends Mapping<? extends T>> fromAdjacency(
      final T[][] adj, DyadType type) {
    checkMatrixLayout(adj, type);
    return fromAdjacencyNocheck(adj, type);
  }

  private static final int DIAGONAL_FLAG = 1;
  private static final int SQUARED_FLAG = 2;

  private static <T> int determineMatrixLayout(T[][] adj) {
    final int nrows = adj.length;
    boolean diag = true;
    boolean squared = true;
    for (int i = 0; i < nrows; i++) {
      diag &= adj[i].length == i + 1;
      squared &= adj[i].length == nrows;
      if (!diag && i > 0 && adj[i - 1].length != adj[i].length) {
        throw new IllegalArgumentException("non-rectangular matrix");
      }
    }
    return (diag ? DIAGONAL_FLAG : 0) | (squared ? SQUARED_FLAG : 0);
  }

  private static <T> void checkMatrixLayout(T[][] adj, DyadType type) {
    int flags = determineMatrixLayout(adj);

    switch (type) {
      case TWO_MODE:
        if ((flags & DIAGONAL_FLAG) != 0) {
          throw new IllegalArgumentException("non-rectangular matrix");
        }
        break;
      case UNDIRECTED:
        if ((flags & DIAGONAL_FLAG) == 0) {
          throw new IllegalArgumentException("non-triangular matrix");
        }
        break;
      case DIRECTED:
        if ((flags & DIAGONAL_FLAG) != 0) {
          throw new IllegalArgumentException("non-rectangular matrix");
        }
        if ((flags & SQUARED_FLAG) == 0) {
          throw new IllegalArgumentException("non-square matrix");
        }
        break;
      default:
        throw new UnsupportedOperationException("unsupported dyad type");
    }
  }

  private static <T> DyadType guessOneModeLayout(T[][] adj) {
    int flags = determineMatrixLayout(adj);
    boolean diag = (flags & DIAGONAL_FLAG) != 0;
    if (!diag && (flags & SQUARED_FLAG) == 0) {
      throw new IllegalArgumentException("non-square matrix");
    }
    return DyadType.oneMode(!diag);
  }

  private static <T> WeightedNetwork<? extends T, ? extends Mapping<? extends T>> //
      fromAdjacencyNocheck(final T[][] adj, DyadType type) {
    final int nrows = adj.length;
    final int ncols = nrows > 0 ? adj[nrows - 1].length : 0;
    final NetworkBuilder builder = NetworkProvider.getInstance().builder(type);
    final boolean diag = !type.isTwoMode() && !type.isDirected();

    // ensure nodes/affiliations
    for (int i = 0; i < nrows; i++) {
      builder.ensureNode(i);
    }
    if (type.isTwoMode()) {
      for (int i = 0; i < ncols; i++) {
        builder.ensureAffiliation(i);
      }
    }
    // add links
    @SuppressWarnings("unchecked")
    PrimitiveList<? extends T> map = Mappings.newListAutoboxing(ClassUtils
        .unwrap((Class<? extends T>) adj.getClass().getComponentType().getComponentType()));

    // strictly speaking, this generic cast is wrong, however, we get away with it
    // since all elements inserted into the primitive list must have a type
    // compatible with the underlying component type because they originate from
    // an array of the same component type
    @SuppressWarnings("unchecked")
    PrimitiveList<T> castMap = (PrimitiveList<T>) map;
    for (int s = 0; s < nrows; s++) {
      for (int t = 0; t <= (diag ? s : ncols - 1); t++) {
        final T v = adj[s][t];
        if (v != null) {
          builder.addEdge(s, t);
          castMap.add(v);
        }
      }
    }
    return new WeightedNetwork<>(builder.build(), map);
  }

  /**
   * Produces weighted networks from double matrices.
   */
  public static final class OfDouble {

    private OfDouble() {
    }

    /**
     * Generates a weighted network from a given adjacency matrix according to the given dyad type.
     * Non-NaN matrix entries are translated into edges with corresponding weight. For one-mode,
     * undirected networks, the adjacency matrix is given in lower triangular form, otherwise it
     * must be in rectangular form with the appropriate number of rows and columns.
     * 
     * @param adj
     *          Adjacency matrix. If one-mode is requested and the adjacency matrix is in lower
     *          triangular form, the result is undirected, directed otherwise.
     * @param type
     *          Dyad type of the returned network.
     * @return A weighted network.
     * @throws IllegalArgumentException
     *           if the adjacency matrix does not have the required form.
     */
    public static WeightedNetwork<Double, Mapping.OfDouble> fromAdjacency(final double[][] adj,
        DyadType type) {
      checkMatrixLayout(adj, type);
      return fromAdjacencyNocheck(adj, type);
    }

    /**
     * Generates a weighted network from a given adjacency matrix. Non-NaN matrix entries are
     * translated into edges with corresponding weight. For one-mode, undirected networks are
     * returned if the adjacency matrix is given in lower triangular form, else directed networks
     * are returned. Note that this means that returned networks with zero or one node are always
     * undirected.
     * 
     * @param adj
     *          Adjacency matrix. If one-mode is requested and the adjacency matrix is in lower
     *          triangular form, the result is undirected, directed otherwise.
     * @param twomode
     *          Two-mode network flag. If true, the result is a two-mode network, otherwise it is a
     *          one-mode directed or undirected network.
     * @return A weighted network.
     * @throws IllegalArgumentException
     *           if the adjacency matrix does not have the required form.
     */
    public static WeightedNetwork<Double, Mapping.OfDouble> fromAdjacency(final double[][] adj,
        boolean twomode) {
      if (twomode) {
        return fromAdjacency(adj, DyadType.TWO_MODE);
      }
      return fromAdjacencyNocheck(adj, guessOneModeLayout(adj));
    }

    private static int determineMatrixLayout(double[][] adj) {
      final int nrows = adj.length;
      boolean diag = true;
      boolean squared = true;
      for (int i = 0; i < nrows; i++) {
        diag &= adj[i].length == i + 1;
        squared &= adj[i].length == nrows;
        if (!diag && i > 0 && adj[i - 1].length != adj[i].length) {
          throw new IllegalArgumentException("non-rectangular matrix");
        }
      }
      return (diag ? DIAGONAL_FLAG : 0) | (squared ? SQUARED_FLAG : 0);
    }

    static void checkMatrixLayout(double[][] adj, DyadType type) {
      int flags = determineMatrixLayout(adj);

      switch (type) {
        case TWO_MODE:
          if ((flags & DIAGONAL_FLAG) != 0) {
            throw new IllegalArgumentException("non-rectangular matrix");
          }
          break;
        case UNDIRECTED:
          if ((flags & DIAGONAL_FLAG) == 0) {
            throw new IllegalArgumentException("non-triangular matrix");
          }
          break;
        case DIRECTED:
          if ((flags & DIAGONAL_FLAG) != 0) {
            throw new IllegalArgumentException("non-rectangular matrix");
          }
          if ((flags & SQUARED_FLAG) == 0) {
            throw new IllegalArgumentException("non-square matrix");
          }
          break;
        default:
          throw new UnsupportedOperationException("unsupported dyad type");
      }
    }

    static DyadType guessOneModeLayout(double[][] adj) {
      int flags = determineMatrixLayout(adj);
      boolean diag = (flags & DIAGONAL_FLAG) != 0;
      if (!diag && (flags & SQUARED_FLAG) == 0) {
        throw new IllegalArgumentException("non-square matrix");
      }
      return DyadType.oneMode(!diag);
    }

    static WeightedNetwork<Double, Mapping.OfDouble> fromAdjacencyNocheck(double[][] adj,
        DyadType type) {
      final int nrows = adj.length;
      final int ncols = nrows > 0 ? adj[nrows - 1].length : 0;
      final NetworkBuilder builder = NetworkProvider.getInstance().builder(type);
      final boolean diag = !type.isTwoMode() && !type.isDirected();

      // ensure nodes/affiliations
      for (int i = 0; i < nrows; i++) {
        builder.ensureNode(i);
      }
      if (type.isTwoMode()) {
        for (int i = 0; i < ncols; i++) {
          builder.ensureAffiliation(i);
        }
      }
      // add links
      PrimitiveList.OfDouble map = Mappings.newDoubleList();
      for (int s = 0; s < nrows; s++) {
        for (int t = 0; t <= (diag ? s : ncols - 1); t++) {
          final double v = adj[s][t];
          if (!Double.isNaN(v)) {
            builder.addEdge(s, t);
            map.addDouble(v);
          }
        }
      }
      return new WeightedNetwork<>(builder.build(), map);
    }
  }
}
