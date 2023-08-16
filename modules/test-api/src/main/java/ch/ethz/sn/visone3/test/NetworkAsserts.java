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

package ch.ethz.sn.visone3.test;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Iterators;
import ch.ethz.sn.visone3.lang.PrimitiveIterable;
import ch.ethz.sn.visone3.networks.DirectedGraph;
import ch.ethz.sn.visone3.networks.Edge;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.Relation;
import ch.ethz.sn.visone3.networks.Relationship;
import ch.ethz.sn.visone3.networks.UndirectedGraph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Provides utility methods to assert network structure in unit tests.
 */
public final class NetworkAsserts {
  private static final Logger LOG = LoggerFactory.getLogger(NetworkAsserts.class);

  private NetworkAsserts() {
  }

  /**
   * Convert to boxed types replacing {@link Double#NaN} with {@code null}.
   * 
   * @param matrix unboxed matrix.
   * @return matrix of boxed doubles where {@link Double#NaN} was replaced by
   *         {@code null}.
   */
  public static Double[][] boxed(final double[][] matrix) {
    final Double[][] box = new Double[matrix.length][];
    for (int r = 0; r < matrix.length; r++) {
      final int l = matrix[r].length;
      box[r] = new Double[l];
      for (int c = 0; c < l; c++) {
        final double v = matrix[r][c];
        if (!Double.isNaN(v)) {
          box[r][c] = Double.valueOf(v);
        }
      }
    }
    return box;
  }

  /**
   * Mirror a diagonal matrix and return the full matrix.
   * 
   * @param diag the diagonal matrix.
   * @param ctor constructor for new array.
   * @param <T> array element type.
   * @return square matrix derived from the diagonal matrix.
   */
  public static <T> T[][] diagonal2square(final T[][] diag, final IntFunction<T[][]> ctor) {
    final int n = diag.length;
    final T[][] square = ctor.apply(n);
    for (int r = 0; r < square.length; r++) {
      for (int c = 0; c < r; c++) {
        square[r][c] = diag[r][c];
        square[c][r] = diag[r][c];
      }
      square[r][r] = diag[r][r];
    }
    return square;
  }

  /**
   * Assert every perspective of a network against an adjacency matrix.
   * 
   * @param expected expected adjacency matrix of network.
   * @param actual   actual network structure.
   * @param map      actual weights on network links.
   * @param <T>      link weight type.
   */
  public static <T> void assertNetwork(final T[][] expected, final Network actual,
      final ConstMapping<T> map) {
    assertRelation(expected, actual, map);
    if (actual.isDirected()) {
      assertDirectedGraph(expected, actual, map);
    } else {
      assertUndirectedGraph(expected, actual, map);
    }
    assertMatrix(expected, actual, map);
  }

  /**
   * Assert the directed graph perspective against an adjacency matrix.
   * 
   * @param expected expected adjacency matrix of network.
   * @param actual   actual network structure.
   * @param map      actual weights on network links.
   * @param <T>      link weight type.
   */
  public static <T> void assertDirectedGraph(final T[][] expected, final Network actual,
      final ConstMapping<T> map) {
    LOG.warn("assertDirectedGraph");
    final Relation r = actual.asRelation();
    final DirectedGraph g = actual.asDirectedGraph();
    assertEquals(r.countLeftDomain(), g.countVertices(), "countVertices()");
    assertEquals(r.countRelationships(), g.countEdges(), "countEdges()");
    assertEquals(g.countEdges(), count(g.getEdges()), "count(getEdges())");
    for (final int i : g.getVertices()) {
      assertEquals(r.countRelationshipsFrom(i), g.getOutDegree(i), format("outDegree(%d)", i));
      assertEquals(g.getOutDegree(i), count(g.getOutEdges(i)), format("count(getOutEdges(%d))", i));
      assertEquals(r.countRelationshipsTo(i), g.getInDegree(i), format("inDegree(%d)", i));
      assertEquals(g.getInDegree(i), count(g.getInEdges(i)), format("count(getInEdges(%d))", i));

      for (final Edge e : g.getOutEdges(i)) {
        assertEquals(i, e.getSource(), format("source(%s) == %d", e, i));
        assertEquals(expected[e.getSource()][e.getTarget()], map.get(e.getIndex()),
            format("value((%d,%d),[%d]=%s) == %s", e.getSource(), e.getTarget(), e.getIndex(),
                map.get(e.getIndex()), expected[e.getSource()][e.getTarget()]));
      }
      for (final Edge e : g.getInEdges(i)) {
        assertEquals(i, e.getTarget(), format("target(%s) == %d", e, i));
        assertEquals(expected[e.getSource()][e.getTarget()], map.get(e.getIndex()),
            format("value((%d,%d),[%d]=%s) == %s", e.getSource(), e.getTarget(), e.getIndex(),
                map.get(e.getIndex()), expected[e.getSource()][e.getTarget()]));
      }
    }
  }

  /**
   * Assert the undirected graph perspective against an adjacency matrix.
   * 
   * @param expected expected adjacency matrix of network.
   * @param actual   actual network structure.
   * @param map      actual weights on network links.
   * @param <T>      link weight type.
   */
  public static <T> void assertUndirectedGraph(final T[][] expected, final Network actual,
      final ConstMapping<T> map) {
    if (actual.isTwoMode()) {
      return;
    }
    LOG.warn("assertUndirectedGraph");
    final UndirectedGraph g = actual.asUndirectedGraph();
    int loops = 0;
    int relations = 0;
    final int[] relationsFrom = new int[g.countVertices()];
    for (int r = 0; r < expected.length; r++) {
      for (int c = 0; c < expected[r].length; c++) {
        if (!Objects.equals(expected[r][c], expected[c][r])) {
          throw new IllegalArgumentException("illegal expected");
        }
        if (expected[r][c] != null) {
          if (r == c) {
            ++loops;
          }
          ++relations;
          ++relationsFrom[r];
        }
      }
    }
    assertEquals(expected.length, g.countVertices(), "countVertices()");
    assertEquals((relations + loops) / 2, g.countEdges(), "countEdges()");
    assertEquals(g.countEdges(), count(g.getEdges()), "count(getEdges())");
    for (final int i : g.getVertices()) {
      assertEquals(relationsFrom[i], g.getDegree(i), format("(%d)", i));
      for (final Edge e : g.getEdges(i)) {
        assertEquals(expected[i][e.getTarget()], map.get(e.getIndex()),
            format("(%d,%d)", i, e.getTarget()));
      }
    }
  }

  /**
   * Assert the relation perspective against an adjacency matrix.
   * 
   * @param expected expected adjacency matrix of network.
   * @param actual   actual network structure.
   * @param map      actual weights on network links.
   * @param <T>      link weight type.
   */
  public static <T> void assertRelation(final T[][] expected, final Network actual,
      final ConstMapping<T> map) {
    LOG.warn("assertRelation");
    final Relation r = actual.asRelation();
    if (expected.length == 0) {
      // an empty network
      assertEquals(0, r.countLeftDomain(), "countLeftDomain");
      assertEquals(0, r.countRightDomain(), "countRightDomain");
      assertEquals(0, r.countRelationships(), "countRelationships");
    } else {
      // domain counts
      assertEquals(expected.length, r.countLeftDomain(), "countLeftDomain");
      assertEquals(expected[0].length, r.countRightDomain(), "countRightDomain");
      // domain iterators
      assertArrayEquals(IntStream.range(0, r.countLeftDomain()).toArray(), array(r.getLeftDomain()),
          "getLeftDomain()");
      assertArrayEquals(IntStream.range(0, r.countLeftDomain()).toArray(),
          r.getLeftDomainStream().toArray(), "getLeftDomainStream()");
      if (!r.isTwoMode()) {
        assertEquals(r.countLeftDomain(), r.countUnionDomain(), "countUnionDomain()");
        assertArrayEquals(array(r.getLeftDomain()), array(r.getRightDomain()), "getRightDomain()");
        assertArrayEquals(r.getLeftDomainStream().toArray(), r.getRightDomainStream().toArray(),
            "getRightDomainStream()");
        assertArrayEquals(array(r.getLeftDomain()), array(r.getUnionDomain()), "getUnionDomain()");
        assertArrayEquals(r.getLeftDomainStream().toArray(), r.getUnionDomainStream().toArray(),
            "getUnionDomainStream()");
      } else {
        assertEquals(r.countLeftDomain() + r.countRightDomain(), r.countUnionDomain(),
            "countUnionDomain()");
        assertArrayEquals(
            IntStream.range(0, r.countRightDomain()).map(i -> i + r.countLeftDomain()).toArray(),
            array(r.getRightDomain()), "getRightDomain()");
        assertArrayEquals(
            IntStream.range(0, r.countRightDomain()).map(i -> i + r.countLeftDomain()).toArray(),
            r.getRightDomainStream().toArray(), "getRightDomainStream()");
        assertArrayEquals(array(Iterators.concat(r.getLeftDomain(), r.getRightDomain())),
            array(r.getUnionDomain()), "getUnionDomain()");
        assertArrayEquals(
            IntStream.concat(r.getLeftDomainStream(), r.getRightDomainStream()).toArray(),
            r.getUnionDomainStream().toArray(), "getUnionDomainStream()");

      }

      // right domain accessors (if different from left)
      if (r.isTwoMode()) {
        assert2ModeRelation(expected, map, r);
      } else {
        assert1ModeRelation(expected, map, r);
      }

      // global domain accessors
      final int expectedRelation = Arrays.stream(expected)
          .mapToInt(t -> (int) Arrays.stream(t).filter(Objects::nonNull).count()).sum();
      assertEquals(expectedRelation, r.countRelationships(), "countRelationships()");
      assertEquals(expectedRelation, count(r.getRelationships()), "count(getRelationships())");
      for (final Relationship e : r.getRelationships()) {
        assertEquals(expected[e.getLeft()][e.getRight0()], map.get(e.getIndex()), format("%s", e));
      }
    }
  }

  private static <T> void assert1ModeRelation(final T[][] expected, final ConstMapping<T> map,
      final Relation r) {
    // 1-mode left (and right) domain accessors
    for (final int i : r.getUnionDomain()) {
      final int rowNonZeros = (int) Arrays.stream(expected[i]).filter(Objects::nonNull).count();
      final int colNonZeros = (int) Arrays.stream(expected).map(row -> row[i])
          .filter(Objects::nonNull).count();
      // first more specific (better for debugging)
      for (final Relationship e : r.getRelationshipsFrom(i)) {
        assertEquals(expected[i][e.getRight0()], map.get(e.getIndex()),
            format("relationshipsFrom(%d) %s", i, e));
      }
      for (final Relationship e : r.getRelationshipsTo(i)) {
        assertEquals(expected[e.getLeft()][i], map.get(e.getIndex()),
            format("relationshipsTo(%d) %s", i, e));
      }
      assertEquals(rowNonZeros, r.countRelationshipsFrom(i), format("countRelationsFrom(%d)", i));
      assertEquals(colNonZeros, r.countRelationshipsTo(i), format("countRelationsTo(%d)", i));
      assertEquals(rowNonZeros, count(r.getRelationshipsFrom(i)),
          format("count(getRelationsFrom(%d))", i));
      assertEquals(colNonZeros, count(r.getRelationshipsTo(i)),
          format("count(getRelationsTo(%d))", i));
      assertEquals(colNonZeros, count(r.getRelationshipsTo0(i)),
          format("count(getRelationsTo0(%d))", i));
      assertEquals(colNonZeros, r.countRelationshipsTo0(i),
          format("count(countRelationsTo0(%d))", i));
    }
  }

  private static <T> void assert2ModeRelation(final T[][] expected, final ConstMapping<T> map,
      final Relation rel) {
    // left domain accessors
    for (final int l : rel.getLeftDomain()) {
      final int rowNonZeros = (int) Arrays.stream(expected[l]).filter(Objects::nonNull).count();
      assertEquals(rowNonZeros, rel.countRelationshipsFrom(l), format("countRelationsFrom(%d)", l));
      assertEquals(rowNonZeros, count(rel.getRelationshipsFrom(l)),
          format("count(getRelationsFrom(%d))", l));
      for (final Relationship e : rel.getRelationshipsFrom(l)) {
        assertEquals(expected[l][e.getRight0()], map.get(e.getIndex()),
            format("relationshipsFrom(%d) %s", l, e));
      }
    }
    // right domain accessors
    for (final int r : rel.getRightDomain()) {
      final int r0 = r - rel.countLeftDomain();
      final int colNonZeros = (int) Arrays.stream(expected).map(row -> row[r0])
          .filter(Objects::nonNull).count();
      // N-based
      assertEquals(colNonZeros, rel.countRelationshipsTo(r), format("countRelationsTo(%d)", r));
      assertEquals(colNonZeros, count(rel.getRelationshipsTo(r)),
          format("count(getRelationsTo(%d))", r));
      for (final Relationship e : rel.getRelationshipsTo(r)) {
        LOG.warn("{}, {}", e.getLeft(), r0);
        assertEquals(expected[e.getLeft()][r0], map.get(e.getIndex()),
            format("relationshipsTo(%d) %s", r, e));
      }
      // 0-based
      assertEquals(colNonZeros, count(rel.getRelationshipsTo0(r0)),
          format("count(getRelationsTo0(%d))", r0));
      assertEquals(colNonZeros, rel.countRelationshipsTo0(r0),
          format("count(countRelationsTo0(%d))", r0));
    }
  }

  /**
   * Assert the matrix perspective against an adjacency matrix.
   * 
   * @param expected expected adjacency matrix of network.
   * @param actual   actual network structure.
   * @param map      actual weights on network links.
   * @param <T>      link weight type.
   */
  public static <T> void assertMatrix(final T[][] expected, final Network actual,
      final ConstMapping<T> map) {
    final T zero = null;
    final T one = null;
    final T[][] m = actual.asMatrix(zero, one, map);
    if (expected.length == 0) {
      assertEquals(0, m.length);
    } else {
      assertEquals(expected.length, m.length);
      for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i].length, m[i].length);
      }
    }
    for (int i = 0; i < m.length; i++) {
      for (int j = 0; j < m[0].length; j++) {
        final T vexpected = expected[i][j];
        final T vactual = m[i][j];
        if (vexpected == null) {
          // if there was no input the matrix should assume 0/1 default values
          if (!actual.isTwoMode() && i == j) {
            assertEquals(one, vactual);
          } else {
            assertEquals(zero, vactual);
          }
        } else {
          assertEquals(vexpected, vactual, format("matrix(%d,%d)", i, j));
        }
      }
    }
  }

  @SafeVarargs
  private static <T> Set<T> unordered(final T... ints) {
    return new HashSet<>(Arrays.asList(ints));
  }

  private static <T, R> Set<R> unordered(final Iterable<T> ts, final Function<T, R> toInt) {
    return StreamSupport.stream(ts.spliterator(), false).map(toInt).collect(Collectors.toSet());
  }

  private static int[] array(final PrimitiveIterable.OfInt itr) {
    return StreamSupport.stream(itr.spliterator(), false).mapToInt(Integer::intValue).toArray();
  }

  private static int count(final Iterable<?> itr) {
    Iterator<?> iterator = itr.iterator();
    int count = 0;
    while (iterator.hasNext()) {
      iterator.next();
      ++count;
    }
    assertThrows(NoSuchElementException.class, () -> iterator.next());
    return count;
  }
}
