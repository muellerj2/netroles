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

package ch.ethz.sn.visone3.roles.impl.algorithms;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.function.BiPredicate;
import java.util.stream.StreamSupport;

import ch.ethz.sn.visone3.algorithms.AlgoProvider;
import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.IntPair;
import ch.ethz.sn.visone3.lang.LongSet;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.Pair;
import ch.ethz.sn.visone3.lang.PrimitiveCollections;
import ch.ethz.sn.visone3.lang.PrimitiveContainers;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.NetworkBuilder;
import ch.ethz.sn.visone3.networks.NetworkProvider;
import ch.ethz.sn.visone3.roles.position.NetworkView;
import ch.ethz.sn.visone3.roles.position.TransposableNetworkView;
import ch.ethz.sn.visone3.roles.util.PartialComparator;

/**
 * Provides algorithms to compute standard role equivalences and their
 * extensions, plus methods for some common operations on equivalences.
 */
public class Equivalences {
  private Equivalences() {
  }

  private static final String EQUIVALENCE_TO_REFINE = "equivalenceToRefine";
  private static final String EQUIVALENCE_RELATIVE_TO = "equivalenceRelativeTo";

  /**
   * Computes the maximum relative regular equivalence. Space: O(n), Runtime: O(n+m) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @return an array representing the maximum relative regular equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static Mapping.OfInt relativeRegularEquivalence(int n,
      NetworkView<?, ?> positionView, ConstMapping.OfInt equivalenceRelativeTo) {
    checkEquivalenceFormat(equivalenceRelativeTo, EQUIVALENCE_RELATIVE_TO);

    final int[] resEquivalence = new int[n];
    final int[] counts = new int[2 * n];
    counts[0] = n;

    final EquivalenceAlgorithmState state = new EquivalenceAlgorithmState(resEquivalence, counts,
        2 * n, 1);

    refiningRegularEquivalenceImpl(n, positionView, equivalenceRelativeTo, state);

    normalizePartition(state.getEquivalence(), state.getColorsMap());

    return Mappings.wrapModifiableInt(state.getEquivalence());
  }

  /**
   * Computes the maximum relative equivalence refining another equivalence. Space: O(n+m), Runtime:
   * O(n+m) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param equivalenceToRefine
   *          array that represents the equivalence the output equivalence should be the relative
   *          regular interior of. Here, two vertices are equivalent to each other if they have the
   *          same associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @return an array representing the maximum relative equivalence that refines
   *         equivalenceToRefine: vertices that have the same associated number in the array (based
   *         on their IDs) are structurally equivalent. The algorithm guarantees the following
   *         properties of the output array: <code>p[0] = 0</code> and
   *         <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static Mapping.OfInt refiningRelativeRegularEquivalence(int n,
      NetworkView<?, ?> positionView, ConstMapping.OfInt equivalenceRelativeTo,
      ConstMapping.OfInt equivalenceToRefine) {
    checkEquivalenceFormat(equivalenceRelativeTo, EQUIVALENCE_RELATIVE_TO);
    checkEquivalenceFormat(equivalenceToRefine, EQUIVALENCE_TO_REFINE);

    final int[] resEquivalence = equivalenceToRefine.intStream().toArray();
    final int[] counts = new int[2 * n];
    int maxColor = 0;
    for (final int c : equivalenceToRefine) {
      maxColor = Math.max(c, maxColor);
      ++counts[c];
    }

    final EquivalenceAlgorithmState state = new EquivalenceAlgorithmState(resEquivalence, counts,
        2 * n, maxColor + 1);
    refiningRegularEquivalenceImpl(n, positionView, equivalenceRelativeTo, state);

    normalizePartition(state.getEquivalence(), state.getColorsMap());

    return Mappings.wrapModifiableInt(state.getEquivalence());
  }

  private static <T> void refiningRegularEquivalenceImpl(final int n,
      NetworkView<?, T> positionView, ConstMapping.OfInt equivalenceRelativeTo,
      final EquivalenceAlgorithmState state) {

    // This color management ensures that the algorithm
    // needs at most 2n colors at once (colors 0 to 2n-1).

    // first, do some preprocessing on the equivalence
    // that the output equivalence should be relative to:
    // order the vertices by their color
    final int[] verticesOrderedByColor = PrimitiveCollections.countingSort(equivalenceRelativeTo)
        .array();

    // preprocessing of original equivalence complete

    // main loop of the algorithm
    // iterate over all vertices ordered by their color in the equivalence
    // that the output should be regular relative to.
    // assign a new free color to the neighbors of vertices of the same
    // color (if they did not get assigned a new color already)
    // if we enter a block of vertices of different color, clean up
    // all internal data structures and garbage-collect unused colors
    for (int iv = 0; iv < n; ++iv) {
      // split the neighbors of iv/v from the ones that are not known
      // to have a neighbor
      // of color "color"
      final int v = verticesOrderedByColor[iv];
      for (final T r : positionView.inverseTies(v)) {
        final int w = positionView.inverseTieTarget(v, r);
        final int oldcolor = state.getColor(w);

        // get the new color to assign to w
        int newcolor = state.getColorMappedToValue(oldcolor);
        // if no such color has been fixed yet,
        // allocate that color and mark old
        // color as processed
        if (newcolor == -1) {

          newcolor = state.allocateColor();

          // put oldcolor in the processed list
          state.getProcessedList().addInt(oldcolor);

          // save mapping from old color to new color
          state.setColorMappedToValue(oldcolor, newcolor);

          // trick that ensures that vertices are not split
          // based on the number of neighbors of a color
          state.setColorMappedToValue(newcolor, newcolor);
        }
        // update the color of neighbor w
        state.reassignColor(w, newcolor);
      }

      // if all vertices of the current color have been processed
      // do clean-up on processed colors
      // and garbage-collect unused colors
      if (iv == n - 1 || equivalenceRelativeTo.getInt(v) != equivalenceRelativeTo
          .getInt(verticesOrderedByColor[iv + 1])) {
        for (int i = 0; i < state.getProcessedList().size(); ++i) {
          final int oldcolor = state.getProcessedList().getInt(i);
          // if color unused -> free it
          if (state.getCount(oldcolor) == 0) {
            state.freeColor(oldcolor);
          }
          // unset the color mapping
          // don't forget about doing the
          // same for the new color
          // due to the trick used above
          final int newcolor = state.getColorMappedToValue(oldcolor);
          state.setColorMappedToValue(oldcolor, -1);
          state.setColorMappedToValue(newcolor, -1);
        }
        // empty processed list
        state.getProcessedList().clear();
      }
    }
  }

  private static <T> void refiningRegularEquivalenceComparatorImpl(final NetworkView<?, T> positionView,
      final ConstMapping.OfInt equivalenceRelativeTo,
      Comparator<? super T> comparatorExtension, final EquivalenceAlgorithmState state) {

    // This color management ensures that the algorithm
    // needs at most 2n colors at once (colors 0 to 2n-1).

    // first, do some preprocessing on the equivalence
    // that the output equivalence should be relative to:
    // order the vertices by their color
    final int[] verticesOrderedByColor = PrimitiveCollections.countingSort(equivalenceRelativeTo)
        .array();
    final int n = positionView.countNodes();

    int maxNumberVerticesForColor = 0;
    for (int begin = 0; begin < n;) {
      int expectedcolor = equivalenceRelativeTo.getInt(verticesOrderedByColor[begin]);
      int end = begin + 1;
      for (int iv = end; iv < n; end = ++iv) {
        final int v = verticesOrderedByColor[iv];
        if (equivalenceRelativeTo.getInt(v) != expectedcolor) {
          break;
        }
      }
      maxNumberVerticesForColor = Math.max(maxNumberVerticesForColor, end - begin);
      begin = end;
    }

    @SuppressWarnings("unchecked")
    T[][] sortedRelationships = (T[][]) new Object[n][];
    int[] positions = new int[n];

    PriorityQueue<Pair<Integer, T>> heap = new PriorityQueue<>(
        (lhs, rhs) -> comparatorExtension.compare(lhs.getSecond(), rhs.getSecond()));
    // preprocessing of original equivalence complete

    // main loop of the algorithm
    // iterate over all vertices ordered by their color in the equivalence
    // that the output should be regular relative to.
    // assign a new free color to the neighbors of vertices of the same
    // color (if they did not get assigned a new color already)
    // if we have processed all edges of one class
    // for all vertices of one color, clean up
    // all internal data structures and garbage-collect unused colors
    int begin = 0;
    while (begin < n) {
      int end = begin + 1;
      int expectedcolor = equivalenceRelativeTo.getInt(verticesOrderedByColor[begin]);
      for (int iv = begin; iv < n; end = ++iv) {
        final int v = verticesOrderedByColor[iv];
        if (equivalenceRelativeTo.getInt(v) != expectedcolor) {
          break;
        }
        @SuppressWarnings("unchecked")
        T[] sortedForV = (T[]) StreamSupport
            .stream(positionView.inverseTies(v).spliterator(), false).sorted(comparatorExtension)
            .toArray();
        sortedRelationships[v] = sortedForV;
        positions[v] = 0;
        if (sortedRelationships[v].length > 0) {
          heap.add(new Pair<>(v, sortedRelationships[v][0]));
        }
      }

      T expected = !heap.isEmpty() ? heap.peek().getSecond() : null;
      // while we still have some unprocessed edges for vertices of this
      // color
      while (!heap.isEmpty()) {

        final Pair<Integer, T> p = heap.poll();
        T r = p.getSecond();
        int v = p.getFirst();
        int pos = positions[v];

        while (r != null && comparatorExtension.compare(expected, r) == 0) {
          final int w = positionView.inverseTieTarget(v, r);
          final int oldcolor = state.getColor(w);

          // get the new color to assign to w
          int newcolor = state.getColorMappedToValue(oldcolor);
          // if no such color has been fixed yet,
          // allocate that color and mark old
          // color as processed
          if (newcolor == -1) {

            newcolor = state.allocateColor();

            // put oldcolor in the processed list
            state.getProcessedList().addInt(oldcolor);

            // save mapping from old color to new color
            state.setColorMappedToValue(oldcolor, newcolor);

            // trick that ensures that vertices are not split
            // based on the number of neighbors of a color
            state.setColorMappedToValue(newcolor, newcolor);
          }
          // update the color of neighbor w
          state.reassignColor(w, newcolor);

          // to next relationship
          ++pos;
          r = pos < sortedRelationships[v].length ? sortedRelationships[v][pos] : null;
        }
        // is a relationship of another class left for this vertex?
        // then store it for later processing and reinsert this vertex
        // into the heap
        if (r != null) {
          positions[v] = pos;
          heap.add(new Pair<>(p.getFirst(), r));
        }

        // if all edges of a class for the current vertex color have
        // been processed
        // do clean-up on processed colors
        // and garbage-collect unused colors
        T next = !heap.isEmpty() ? heap.peek().getSecond() : null;
        if (next == null || comparatorExtension.compare(expected, next) != 0) {
          expected = next;
          for (int i = 0; i < state.getProcessedList().size(); ++i) {
            final int oldcolor = state.getProcessedList().getInt(i);
            // if color unused -> free it
            if (state.getCount(oldcolor) == 0) {
              state.freeColor(oldcolor);
            }
            // unset the color mapping
            // don't forget about doing the
            // same for the new color
            // due to the trick used above
            final int newcolor = state.getColorMappedToValue(oldcolor);
            state.setColorMappedToValue(oldcolor, -1);
            state.setColorMappedToValue(newcolor, -1);
          }
          // empty processed list
          state.getProcessedList().clear();
        }
      }
      // move on to next color
      begin = end;
    }
  }

  /**
   * Computes the maximum relative regular equivalence with ordinal data on links. Note that
   * different edge classes must not compare equal. For now, the implementation is restricted to
   * weakly ordered data. Space: O(n), Runtime: O(n + m log n) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param comparator
   *          compares the relationships.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum relative regular equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array: 
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt relativeRegularEquivalence(final int n,
      NetworkView<? extends V, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, Comparator<? super V> comparator) {
    checkEquivalenceFormat(equivalenceRelativeTo, EQUIVALENCE_RELATIVE_TO);

    final int[] resEquivalence = new int[n];
    final int[] counts = new int[2 * n];
    counts[0] = n;

    final EquivalenceAlgorithmState state = new EquivalenceAlgorithmState(resEquivalence, counts,
        2 * n, 1);

    refiningRegularEquivalenceImpl(n, positionView, equivalenceRelativeTo, comparator, state);

    normalizePartition(state.getEquivalence(), state.getColorsMap());

    return Mappings.wrapModifiableInt(state.getEquivalence());
  }

  /**
   * Computes the maximum relative equivalence refining another equivalence for a network with with
   * ordinal data on links. Note that different edge classes must not compare equal. For now, the
   * implementation is restricted to weakly ordered data. Space: O(n + m), Runtime: O(n + m log n)
   * worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param equivalenceToRefine
   *          array that represents the equivalence the output equivalence should be the relative
   *          regular interior of. Here, two vertices are equivalent to each other if they have the
   *          same associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param comparator
   *          Compares the relationships.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum relative equivalence that refines
   *         equivalenceToRefine: vertices that have the same associated number in the array (based
   *         on their IDs) are structurally equivalent. The algorithm guarantees the following
   *         properties of the output array: <code>p[0] = 0</code> and
   *         <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt refiningRelativeRegularEquivalence(int n,
      NetworkView<? extends V, ? extends V> positionView,
      final ConstMapping.OfInt equivalenceRelativeTo, final ConstMapping.OfInt equivalenceToRefine,
      Comparator<? super V> comparator) {
    checkEquivalenceFormat(equivalenceRelativeTo, EQUIVALENCE_RELATIVE_TO);
    checkEquivalenceFormat(equivalenceToRefine, EQUIVALENCE_TO_REFINE);

    final int[] resEquivalence = equivalenceToRefine.intStream().toArray();
    final int[] counts = new int[2 * n];
    int maxColor = 0;
    for (final int c : equivalenceToRefine) {
      maxColor = Math.max(c, maxColor);
      ++counts[c];
    }

    final EquivalenceAlgorithmState state = new EquivalenceAlgorithmState(resEquivalence, counts,
        2 * n, maxColor + 1);

    refiningRegularEquivalenceImpl(n, positionView, equivalenceRelativeTo, comparator, state);

    normalizePartition(state.getEquivalence(), state.getColorsMap());

    return Mappings.wrapModifiableInt(state.getEquivalence());
  }

  private static <V, T extends V, U extends V> void refiningRegularEquivalenceImpl(final int n,
      final NetworkView<T, U> positionView, ConstMapping.OfInt equivalenceRelativeTo,
      Comparator<? super V> comparator, final EquivalenceAlgorithmState state) {

    // Idea:
    // If two vertices have the same class, then the maximum edge class for
    // edges of a color has to be the same in both neighborhoods; all other
    // edges can be ignored
    // so we filter out all non-maximal edges for each color and
    // neighborhood and then reduce to the implementation for categorical
    // data after removing non-maximal edges
    // (for now, this only works for weakly ordered edge classes, but there
    // is nothing stopping us from also supporting partially ordered edge
    // classes, even if the resulting complexity its worse)

    final boolean[] includedEdges = new boolean[positionView.maxUniqueTieIndex()];
    @SuppressWarnings("unchecked")
    T[] maximalRelationships = (T[]) new Object[n];

    for (int i = 0; i < n; ++i) {

      for (T r : positionView.ties(i)) {
        int color = equivalenceRelativeTo.getInt(positionView.tieTarget(i, r));
        T oldMaxRelationshipColor = maximalRelationships[color];
        if (oldMaxRelationshipColor == null) {
          state.getProcessedList().addInt(color);
          maximalRelationships[color] = r;
        } else if (comparator.compare(oldMaxRelationshipColor, r) < 0) {
          maximalRelationships[color] = r;
        }
      }

      for (T r : positionView.ties(i)) {
        int color = equivalenceRelativeTo.getInt(positionView.tieTarget(i, r));

        if (comparator.compare(r, maximalRelationships[color]) == 0) {
          includedEdges[positionView.uniqueTieIndex(i, r)] = true;
        }
      }

      for (int j = 0; j < state.getProcessedList().size(); ++j) {
        int color = state.getProcessedList().getInt(j);
        maximalRelationships[color] = null;
      }
      state.getProcessedList().clear();
    }

    refiningRegularEquivalenceComparatorImpl(
        // only those methods are implemented that are actually needed by the called
        // algorithm
        new NetworkView<T, U>() {

          @Override
          public int countNodes() {
            return n;
          }

          @Override
          public Iterable<? extends T> ties(int node) {
            throw new UnsupportedOperationException();
          }

          private <W> Iterable<W> fromIterator(Iterator<W> it) {
            return () -> it;
          }

          @Override
          public Iterable<? extends U> inverseTies(int node) {
            return fromIterator(
                StreamSupport.stream(positionView.inverseTies(node).spliterator(), false)
                    .filter((r) -> includedEdges[positionView.uniqueInverseTieIndex(node, r)])
                    .iterator());
          }

          @Override
          public int tieTarget(int node, T tie) {
            throw new UnsupportedOperationException();
          }

          @Override
          public int inverseTieTarget(int node, U inverseTie) {
            return positionView.inverseTieTarget(node, inverseTie);
          }

          @Override
          public int tieIndex(int node, T tie) {
            throw new UnsupportedOperationException();
          }

          @Override
          public int inverseTieIndex(int node, U inverseTie) {
            return positionView.inverseTieIndex(node, inverseTie);
          }

          @Override
          public int countTies(int node) {
            throw new UnsupportedOperationException();
          }

          @Override
          public int countInverseTies(int node) {
            throw new UnsupportedOperationException();
          }

          @Override
          public int uniqueTieIndex(int node, T tie) {
            return tieIndex(node, tie);
          }

          @Override
          public int uniqueInverseTieIndex(int node, U inverseTie) {
            return inverseTieIndex(node, inverseTie);
          }

          @Override
          public int countAllTies() {
            throw new UnsupportedOperationException();
          }

          @Override
          public int maxUniqueTieIndex() {
            throw new UnsupportedOperationException();
          }
        }, equivalenceRelativeTo, comparator, state);
  }

  /**
   * Computes the maximum relative exact equivalence. Space: O(n+m), Runtime: O(n+m) worst case.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @return an array representing the maximum relative exact equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static Mapping.OfInt relativeExactEquivalence(final int n,
      NetworkView<?, ?> positionView, final ConstMapping.OfInt equivalenceRelativeTo) {
    checkEquivalenceFormat(equivalenceRelativeTo, EQUIVALENCE_RELATIVE_TO);

    final int[] resEquivalence = new int[n];
    final int m = positionView.countAllTies();
    final int[] counts = new int[2 * m + n];
    counts[0] = n;

    final EquivalenceAlgorithmState state = new EquivalenceAlgorithmState(resEquivalence, counts,
        2 * m + n, 1);

    refiningExactEquivalenceImpl(n, positionView, equivalenceRelativeTo, state);

    normalizePartition(state.getEquivalence(), state.getColorsMap());

    return Mappings.wrapModifiableInt(state.getEquivalence());
  }

  /**
   * Computes the maximum relative exact equivalence that refines equivalenceToRefine. Space:
   * O(n+m), Runtime: O(n+m) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param equivalenceToRefine
   *          array that represents the equivalence the output equivalence should be the relative
   *          regular interior of. Here, two vertices are equivalent to each other if they have the
   *          same associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @return an array representing the maximum relative exact equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static Mapping.OfInt refiningRelativeExactEquivalence(final int n,
      NetworkView<?, ?> positionView, ConstMapping.OfInt equivalenceRelativeTo,
      ConstMapping.OfInt equivalenceToRefine) {
    checkEquivalenceFormat(equivalenceRelativeTo, EQUIVALENCE_RELATIVE_TO);
    checkEquivalenceFormat(equivalenceToRefine, EQUIVALENCE_TO_REFINE);

    final int[] resEquivalence = equivalenceToRefine.intStream().toArray();
    final int m = positionView.countAllTies();
    final int[] counts = new int[2 * m + n];

    int maxColor = 0;
    for (final int c : equivalenceToRefine) {
      maxColor = Math.max(c, maxColor);
      ++counts[c];
    }
    final EquivalenceAlgorithmState state = new EquivalenceAlgorithmState(resEquivalence, counts,
        2 * m + n, maxColor + 1);

    refiningExactEquivalenceImpl(n, positionView, equivalenceRelativeTo, state);

    normalizePartition(state.getEquivalence(), state.getColorsMap());

    return Mappings.wrapModifiableInt(state.getEquivalence());
  }

  /**
   * Computes the maximum relative regular equivalence. Space: O(n), Runtime: O(nm) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @return an array representing the maximum relative regular equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static Mapping.OfInt relativeRegularEquivalence(int n,
      TransposableNetworkView<?, ?> positionView,
      ConstMapping.OfInt equivalenceRelativeTo) {

    return relativeRegularEquivalence(n, positionView, equivalenceRelativeTo,
        MiscUtils.alwaysTrue());
  }

  /**
   * Computes the maximum relative equivalence refining another equivalence. Space: O(n), Runtime:
   * O(nm) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param equivalenceToRefine
   *          array that represents the equivalence the output equivalence should be the relative
   *          regular interior of. Here, two vertices are equivalent to each other if they have the
   *          same associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @return an array representing the maximum relative equivalence that refines
   *         equivalenceToRefine: vertices that have the same associated number in the array (based
   *         on their IDs) are structurally equivalent. The algorithm guarantees the following
   *         properties of the output array: <code>p[0] = 0</code> and
   *         <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static Mapping.OfInt refiningRelativeRegularEquivalence(int n,
      TransposableNetworkView<?, ?> positionView, ConstMapping.OfInt equivalenceRelativeTo,
      ConstMapping.OfInt equivalenceToRefine) {
    return refiningRelativeRegularEquivalence(n, positionView, equivalenceRelativeTo,
        equivalenceToRefine, MiscUtils.alwaysTrue());
  }

  /**
   * Computes the maximum relative regular equivalence with ordinal data on links. Space: O(n),
   * Runtime: O(nm) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param comparator
   *          compares the relationships.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum relative regular equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt relativeRegularEquivalence(final int n,
      final TransposableNetworkView<? extends V, ? extends V> positionView,
      final ConstMapping.OfInt equivalenceRelativeTo, Comparator<? super V> comparator) {

    return relativeRegularEquivalence(n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum relative equivalence refining another equivalence for a network with with
   * ordinal data on links. Space: O(n + m), Runtime: O(nm) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param equivalenceToRefine
   *          array that represents the equivalence the output equivalence should be the relative
   *          regular interior of. Here, two vertices are equivalent to each other if they have the
   *          same associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param comparator
   *          compares the relationships.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum relative equivalence that refines
   *         equivalenceToRefine: vertices that have the same associated number in the array (based
   *         on their IDs) are structurally equivalent. The algorithm guarantees the following
   *         properties of the output array: <code>p[0] = 0</code> and
   *         <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt refiningRelativeRegularEquivalence(final int n,
      final TransposableNetworkView<? extends V, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, ConstMapping.OfInt equivalenceToRefine,
      Comparator<? super V> comparator) {
    return refiningRelativeRegularEquivalence(n, positionView, equivalenceRelativeTo,
        equivalenceToRefine, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum relative regular equivalence with ordinal data on links. Space: O(n),
   * Runtime: O(nm) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param comparator
   *          compares the relationships.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum relative regular equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt relativeRegularEquivalence(final int n,
      final TransposableNetworkView<? extends V, ? extends V> positionView,
      final ConstMapping.OfInt equivalenceRelativeTo, PartialComparator<? super V> comparator) {

    return relativeRegularEquivalence(n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum relative equivalence refining another equivalence for a network with with
   * ordinal data on links. Space: O(n + m), Runtime: O(nm) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param equivalenceToRefine
   *          array that represents the equivalence the output equivalence should be the relative
   *          regular interior of. Here, two vertices are equivalent to each other if they have the
   *          same associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param comparator
   *          compares the relationships.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum relative equivalence that refines
   *         equivalenceToRefine: vertices that have the same associated number in the array (based
   *         on their IDs) are structurally equivalent. The algorithm guarantees the following
   *         properties of the output array: <code>p[0] = 0</code> and
   *         <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt refiningRelativeRegularEquivalence(final int n,
      final TransposableNetworkView<? extends V, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, ConstMapping.OfInt equivalenceToRefine,
      PartialComparator<? super V> comparator) {
    return refiningRelativeRegularEquivalence(n, positionView, equivalenceRelativeTo,
        equivalenceToRefine, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum relative regular equivalence with ordinal data on links. Space: O(n),
   * Runtime: O(nm) worst case.
   *
   * <p>
   * This method is deliberately set private, as its input does not guarantee that the result is an
   * equivalence, and it is only used to implement other public methods.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param comparator
   *          compares the relationships.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum relative regular equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  private static <V> Mapping.OfInt relativeRegularEquivalence(final int n,
      final TransposableNetworkView<? extends V, ? extends V> positionView,
      final ConstMapping.OfInt equivalenceRelativeTo,
      BiPredicate<? super V, ? super V> comparator) {
    checkEquivalenceFormat(equivalenceRelativeTo, EQUIVALENCE_RELATIVE_TO);

    final int[] resEquivalence = new int[n];

    refiningRegularEquivalenceImpl(n, positionView, equivalenceRelativeTo, resEquivalence,
        comparator);
    final int[] colorsMap = new int[n];
    Arrays.fill(colorsMap, -1);
    normalizePartition(resEquivalence, colorsMap);

    return Mappings.wrapModifiableInt(resEquivalence);
  }

  /**
   * Computes the maximum relative equivalence refining another equivalence for a network with with
   * ordinal data on links. Space: O(n + m), Runtime: O(nm) worst case.
   *
   * <p>
   * This method is deliberately set private, as its input does not guarantee that the result is an
   * equivalence, and it is only used to implement other public methods.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param equivalenceToRefine
   *          array that represents the equivalence the output equivalence should be the relative
   *          regular interior of. Here, two vertices are equivalent to each other if they have the
   *          same associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param comparator
   *          compares the relationships.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum relative equivalence that refines
   *         equivalenceToRefine: vertices that have the same associated number in the array (based
   *         on their IDs) are structurally equivalent. The algorithm guarantees the following
   *         properties of the output array: <code>p[0] = 0</code> and
   *         <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  private static <V> Mapping.OfInt refiningRelativeRegularEquivalence(final int n,
      final TransposableNetworkView<? extends V, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, ConstMapping.OfInt equivalenceToRefine,
      BiPredicate<? super V, ? super V> comparator) {
    checkEquivalenceFormat(equivalenceRelativeTo, EQUIVALENCE_RELATIVE_TO);
    checkEquivalenceFormat(equivalenceToRefine, EQUIVALENCE_TO_REFINE);

    final int[] resEquivalence = equivalenceToRefine.intStream().toArray();

    refiningRegularEquivalenceImpl(n, positionView, equivalenceRelativeTo, resEquivalence,
        comparator);

    final int[] colorsMap = new int[resEquivalence.length];
    Arrays.fill(colorsMap, -1);
    normalizePartition(resEquivalence, colorsMap);

    return Mappings.wrapModifiableInt(resEquivalence);
  }

  private static <T> void refiningRegularEquivalenceImpl(final int n,
      final TransposableNetworkView<T, ?> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, final int[] resultAndToRefine,
      BiPredicate<? super T, ? super T> comparator) {

    if (resultAndToRefine.length == 0) {
      return;
    }

    int[] verticesSortedByOldClass = PrimitiveCollections.countingSort(resultAndToRefine);

    int freeClass = 1;
    int[] representative = new int[n];
    int currentOldClass = resultAndToRefine[verticesSortedByOldClass[0]];
    int firstRepresentativeInOldClass = 0;
    resultAndToRefine[verticesSortedByOldClass[0]] = 0;
    for (int pos = 1; pos < n; ++pos) {
      int i = verticesSortedByOldClass[pos];
      if (currentOldClass != resultAndToRefine[i]) {
        currentOldClass = resultAndToRefine[i];
        firstRepresentativeInOldClass = freeClass;
      }
      boolean classAssigned = false;
      for (int eqClass = firstRepresentativeInOldClass; eqClass < freeClass; ++eqClass) {
        int j = representative[eqClass];
        int degi = positionView.countTies(i, j, i);
        int degj = positionView.countTies(i, j, j);
        boolean potentiallyEquivalent = (degi == 0) || (degj > 0);

        if (potentiallyEquivalent) {
          for (T ri : positionView.ties(i, j, i)) {
            boolean matched = false;
            int ritarget = positionView.tieTarget(i, j, i, ri);
            for (T rj : positionView.ties(i, j, j)) {
              int rjtarget = positionView.tieTarget(i, j, j, rj);
              if (equivalenceRelativeTo.getInt(ritarget) == equivalenceRelativeTo
                  .getInt(rjtarget)) {
                if (comparator.test(ri, rj)) {
                  matched = true;
                  break;
                }
              }
            }
            if (!matched) {
              potentiallyEquivalent = false;
              break;
            }
          }
        }
        if (potentiallyEquivalent) {
          degi = positionView.countTies(j, i, i);
          degj = positionView.countTies(j, i, j);
          potentiallyEquivalent = (degi == 0) || (degj > 0);
        }
        if (potentiallyEquivalent) {
          for (T rj : positionView.ties(j, i, j)) {
            boolean matched = false;
            int rjtarget = positionView.tieTarget(j, i, j, rj);
            for (T ri : positionView.ties(j, i, i)) {
              int ritarget = positionView.tieTarget(j, i, i, ri);
              if (equivalenceRelativeTo.getInt(ritarget) == equivalenceRelativeTo
                  .getInt(rjtarget)) {
                if (comparator.test(rj, ri)) {
                  matched = true;
                  break;
                }
              }
            }
            if (!matched) {
              potentiallyEquivalent = false;
              break;
            }
          }
        }
        if (potentiallyEquivalent) {
          resultAndToRefine[i] = eqClass;
          classAssigned = true;
          break;
        }
      }
      if (!classAssigned) {
        representative[freeClass] = i;
        resultAndToRefine[i] = freeClass++;
      }
    }

  }

  private static <T> void refiningExactEquivalenceImpl(final int n,
      final NetworkView<?, T> positionView, ConstMapping.OfInt equivalenceRelativeTo,
      final EquivalenceAlgorithmState state) {

    // This color management ensures that the algorithm
    // needs at most 2n colors at once (colors 0 to 2n-1).

    // first, do some preprocessing on the equivalence
    // that the output equivalence should be relative to:
    // order the vertices by their color
    final int[] verticesOrderedByColor = PrimitiveCollections.countingSort(equivalenceRelativeTo)
        .array();

    // preprocessing of original equivalence complete

    // main loop of the algorithm
    // iterate over all vertices ordered by their color in the equivalence
    // that the output should be regular relative to.
    // assign a new free color to the neighbors of vertices of the same
    // color (if they did not get assigned a new color already)
    // if we enter a block of vertices of different color, clean up
    // all internal data structures and garbage-collect unused colors
    for (int iv = 0; iv < n; ++iv) {
      // split the neighbors of iv/v from the ones that are not known
      // to have a neighbor
      // of color "color"
      final int v = verticesOrderedByColor[iv];
      for (final T r : positionView.inverseTies(v)) {
        final int w = positionView.inverseTieTarget(v, r);
        final int oldcolor = state.getColor(w);

        // get the new color to assign to w
        int newcolor = state.getColorMappedToValue(oldcolor);
        // if no such color has been fixed yet,
        // allocate that color and mark old
        // color as processed
        if (newcolor == -1) {

          newcolor = state.allocateColor();

          // put oldcolor in the processed list
          state.getProcessedList().addInt(oldcolor);

          // save mapping from old color to new color
          state.setColorMappedToValue(oldcolor, newcolor);
        }
        // update the color of neighbor w
        state.reassignColor(w, newcolor);
      }

      // if all vertices of the current color have been processed
      // do clean-up on processed colors
      // and garbage-collect unused colors
      if (iv == n - 1 || equivalenceRelativeTo.getInt(v) != equivalenceRelativeTo
          .getInt(verticesOrderedByColor[iv + 1])) {
        for (int i = 0; i < state.getProcessedList().size(); ++i) {
          final int oldcolor = state.getProcessedList().getInt(i);
          // if color unused -> free it
          if (state.getCount(oldcolor) == 0) {
            state.freeColor(oldcolor);
          }
          // unset the color mapping
          state.setColorMappedToValue(oldcolor, -1);
        }
        // empty processed list
        state.getProcessedList().clear();
      }
    }
  }

  /**
   * Computes the maximum relative exact equivalence for a network with categorical or ordinal data
   * on links. Space: O(n+m), Runtime: O(n+m) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * 
   * @return an array representing the maximum relative exact equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt relativeExactEquivalence(final int n,
      NetworkView<? extends V, ? extends V> positionView,
      final ConstMapping.OfInt equivalenceRelativeTo, Comparator<? super V> comparator) {
    checkEquivalenceFormat(equivalenceRelativeTo, EQUIVALENCE_RELATIVE_TO);

    final int[] resEquivalence = new int[n];
    final int m = positionView.countAllTies();
    final int[] counts = new int[2 * m + n];
    counts[0] = n;

    final EquivalenceAlgorithmState state = new EquivalenceAlgorithmState(resEquivalence, counts,
        2 * m + n, 1);

    refiningExactEquivalenceImpl(n, positionView, equivalenceRelativeTo, comparator, state);

    normalizePartition(state.getEquivalence(), state.getColorsMap());

    return Mappings.wrapModifiableInt(state.getEquivalence());
  }

  /**
   * Computes the maximum relative exact equivalence that refines equivalenceToRefine for a network
   * with categorical or ordinal data on links. Space: O(n+m), Runtime: O(n + m log n) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param equivalenceToRefine
   *          array that represents the equivalence the output equivalence should be the relative
   *          regular interior of. Here, two vertices are equivalent to each other if they have the
   *          same associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum relative exact equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt refiningRelativeExactEquivalence(final int n,
      final NetworkView<? extends V, ? extends V> positionView,
      final ConstMapping.OfInt equivalenceRelativeTo, final ConstMapping.OfInt equivalenceToRefine,
      Comparator<? super V> comparator) {
    checkEquivalenceFormat(equivalenceRelativeTo, EQUIVALENCE_RELATIVE_TO);
    checkEquivalenceFormat(equivalenceToRefine, EQUIVALENCE_TO_REFINE);

    final int[] resEquivalence = equivalenceToRefine.intStream().toArray();
    final int m = positionView.countAllTies();
    final int[] counts = new int[2 * m + n];

    int maxColor = 0;
    for (final int c : equivalenceToRefine) {
      maxColor = Math.max(c, maxColor);
      ++counts[c];
    }
    final EquivalenceAlgorithmState state = new EquivalenceAlgorithmState(resEquivalence, counts,
        2 * m + n, maxColor + 1);

    refiningExactEquivalenceImpl(n, positionView, equivalenceRelativeTo, comparator, state);

    normalizePartition(state.getEquivalence(), state.getColorsMap());

    return Mappings.wrapModifiableInt(state.getEquivalence());
  }

  private static <T> void refiningExactEquivalenceImpl(final int n,
      final NetworkView<?, T> positionView, final ConstMapping.OfInt equivalenceRelativeTo,
      Comparator<? super T> comparator, final EquivalenceAlgorithmState state) {

    // first, do some preprocessing on the equivalence
    // that the output equivalence should be relative to:
    // order the vertices by their color
    final int[] verticesOrderedByColor = PrimitiveCollections.countingSort(equivalenceRelativeTo)
        .array();

    @SuppressWarnings("unchecked")
    final T[][] sortedRelationships = (T[][]) new Object[n][];
    final int[] positions = new int[n];

    PriorityQueue<Pair<Integer, T>> heap = new PriorityQueue<>(
        (lhs, rhs) -> comparator.compare(lhs.getSecond(), rhs.getSecond()));

    // preprocessing of original equivalence complete

    // main loop of the algorithm
    // iterate over all vertices ordered by their color in the equivalence
    // that the output should be regular relative to.
    // assign a new free color to the neighbors of vertices of the same
    // color (if they did not get assigned a new color already)
    // if we have processed all edges of one class
    // for all vertices of one color, clean up
    // all internal data structures and garbage-collect unused colors
    int begin = 0;
    while (begin < n) {
      int end = begin + 1;
      int expectedcolor = equivalenceRelativeTo.getInt(verticesOrderedByColor[begin]);
      for (int iv = begin; iv < n; end = ++iv) {
        final int v = verticesOrderedByColor[iv];
        if (equivalenceRelativeTo.getInt(v) != expectedcolor) {
          break;
        }
        @SuppressWarnings("unchecked")
        T[] sortedForV = (T[]) StreamSupport
            .stream(positionView.inverseTies(v).spliterator(), false).sorted(comparator).toArray();
        sortedRelationships[v] = sortedForV;
        positions[v] = 0;
        if (sortedRelationships[v].length > 0) {
          T r = sortedRelationships[v][0];
          heap.add(new Pair<>(v, r));
        }
      }
      T expected = !heap.isEmpty() ? heap.peek().getSecond() : null;
      // while we still have some unprocessed edges for vertices of this
      // color
      while (!heap.isEmpty()) {

        final Pair<Integer, T> p = heap.poll();
        T r = p.getSecond();
        int v = p.getFirst();
        int pos = positions[v];
        while (r != null && comparator.compare(expected, r) == 0) {
          final int w = positionView.inverseTieTarget(v, r);
          final int oldcolor = state.getColor(w);

          // get the new color to assign to w
          int newcolor = state.getColorMappedToValue(oldcolor);
          // if no such color has been fixed yet,
          // allocate that color and mark old
          // color as processed
          if (newcolor == -1) {

            newcolor = state.allocateColor();

            // put oldcolor in the processed list
            state.getProcessedList().addInt(oldcolor);

            // save mapping from old color to new color
            state.setColorMappedToValue(oldcolor, newcolor);
          }
          // update the color of neighbor w
          state.reassignColor(w, newcolor);

          // to next relationship
          ++pos;
          r = pos < sortedRelationships[v].length ? sortedRelationships[v][pos] : null;
        }
        // is a relationship of another class left for this vertex?
        // then store it for later processing and reinsert this vertex
        // into the heap
        if (r != null) {
          positions[v] = pos;
          heap.add(new Pair<>(p.getFirst(), r));
        }

        // if all edges of a class for the current vertex color have
        // been processed
        // do clean-up on processed colors
        // and garbage-collect unused colors
        T next = !heap.isEmpty() ? heap.peek().getSecond() : null;
        if (heap.isEmpty() || comparator.compare(expected, next) != 0) {
          expected = next;
          for (int i = 0; i < state.getProcessedList().size(); ++i) {
            final int oldcolor = state.getProcessedList().getInt(i);
            // if color unused -> free it
            if (state.getCount(oldcolor) == 0) {
              state.freeColor(oldcolor);
            }
            // unset the color mapping
            state.setColorMappedToValue(oldcolor, -1);
          }
          // empty processed list
          state.getProcessedList().clear();
        }
      }
      // move on to next color
      begin = end;
    }
  }

  /**
   * Computes the maximum relative exact equivalence. Space: O(n+m), Runtime: O(n+m) worst case. FsF
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @return an array representing the maximum relative exact equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static Mapping.OfInt relativeExactEquivalence(final int n,
      TransposableNetworkView<?, ?> positionView,
      final ConstMapping.OfInt equivalenceRelativeTo) {
    return relativeExactEquivalence(n, positionView, equivalenceRelativeTo, MiscUtils.alwaysTrue());
  }

  /**
   * Computes the maximum relative exact equivalence that refines equivalenceToRefine. Space:
   * O(n+m), Runtime: O(n+m) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param equivalenceToRefine
   *          array that represents the equivalence the output equivalence should be the relative
   *          regular interior of. Here, two vertices are equivalent to each other if they have the
   *          same associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @return an array representing the maximum relative exact equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static Mapping.OfInt refiningRelativeExactEquivalence(final int n,
      TransposableNetworkView<?, ?> positionView, ConstMapping.OfInt equivalenceRelativeTo,
      ConstMapping.OfInt equivalenceToRefine) {
    return refiningRelativeExactEquivalence(n, positionView, equivalenceRelativeTo,
        equivalenceToRefine, MiscUtils.alwaysTrue());
  }

  /**
   * Computes the maximum relative exact equivalence for a network with categorical or ordinal data
   * on links. Space: O(n+m), Runtime: O(n+m) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * 
   * @return an array representing the maximum relative exact equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt relativeExactEquivalence(final int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, Comparator<? super V> comparator) {
    return relativeExactEquivalence(n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum relative exact equivalence that refines equivalenceToRefine for a network
   * with categorical or ordinal data on links. Space: O(n+m), Runtime: O(n + m log n) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param equivalenceToRefine
   *          array that represents the equivalence the output equivalence should be the relative
   *          regular interior of. Here, two vertices are equivalent to each other if they have the
   *          same associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum relative exact equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt refiningRelativeExactEquivalence(final int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      final ConstMapping.OfInt equivalenceRelativeTo, final ConstMapping.OfInt equivalenceToRefine,
      Comparator<? super V> comparator) {
    return refiningRelativeExactEquivalence(n, positionView, equivalenceRelativeTo,
        equivalenceToRefine, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum relative exact equivalence for a network with categorical or ordinal data
   * on links. Space: O(n+m), Runtime: O(n+m) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * 
   * @return an array representing the maximum relative exact equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt relativeExactEquivalence(final int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, PartialComparator<? super V> comparator) {
    return relativeExactEquivalence(n, positionView, equivalenceRelativeTo,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum relative exact equivalence that refines equivalenceToRefine for a network
   * with categorical or ordinal data on links. Space: O(n+m), Runtime: O(n + m log n) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param equivalenceToRefine
   *          array that represents the equivalence the output equivalence should be the relative
   *          regular interior of. Here, two vertices are equivalent to each other if they have the
   *          same associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum relative exact equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt refiningRelativeExactEquivalence(final int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      final ConstMapping.OfInt equivalenceRelativeTo, final ConstMapping.OfInt equivalenceToRefine,
      PartialComparator<? super V> comparator) {
    return refiningRelativeExactEquivalence(n, positionView, equivalenceRelativeTo,
        equivalenceToRefine, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum relative exact equivalence for a network with categorical or ordinal data
   * on links. Space: O(n+m), Runtime: O(n+m) worst case.
   *
   * <p>
   * This method is deliberately set private, as its input does not guarantee that the result is an
   * equivalence, and it is only used to implement other public methods.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * 
   * @return an array representing the maximum relative exact equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  private static <V> Mapping.OfInt relativeExactEquivalence(final int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      ConstMapping.OfInt equivalenceRelativeTo, BiPredicate<? super V, ? super V> comparator) {
    checkEquivalenceFormat(equivalenceRelativeTo, EQUIVALENCE_RELATIVE_TO);

    final int[] resEquivalence = new int[n];

    refiningExactEquivalenceImpl(n, positionView, equivalenceRelativeTo, resEquivalence,
        comparator);
    final int[] colorsMap = new int[n];
    Arrays.fill(colorsMap, -1);
    normalizePartition(resEquivalence, colorsMap);

    return Mappings.wrapModifiableInt(resEquivalence);
  }

  /**
   * Computes the maximum relative exact equivalence that refines equivalenceToRefine for a network
   * with categorical or ordinal data on links. Space: O(n+m), Runtime: O(n + m log n) worst case.
   *
   * <p>
   * This method is deliberately set private, as its input does not guarantee that the result is an
   * equivalence, and it is only used to implement other public methods.
   * 
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceRelativeTo
   *          array that represents the equivalence the output equivalence should be regular
   *          relative to. Here, two vertices are equivalent to each other if they have the same
   *          associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param equivalenceToRefine
   *          array that represents the equivalence the output equivalence should be the relative
   *          regular interior of. Here, two vertices are equivalent to each other if they have the
   *          same associated integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum relative exact equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  private static <V> Mapping.OfInt refiningRelativeExactEquivalence(final int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      final ConstMapping.OfInt equivalenceRelativeTo, final ConstMapping.OfInt equivalenceToRefine,
      BiPredicate<? super V, ? super V> comparator) {
    checkEquivalenceFormat(equivalenceRelativeTo, EQUIVALENCE_RELATIVE_TO);
    checkEquivalenceFormat(equivalenceToRefine, EQUIVALENCE_TO_REFINE);

    final int[] resEquivalence = equivalenceToRefine.intStream().toArray();

    refiningExactEquivalenceImpl(n, positionView, equivalenceRelativeTo, resEquivalence,
        comparator);
    final int[] colorsMap = new int[n];
    Arrays.fill(colorsMap, -1);
    normalizePartition(resEquivalence, colorsMap);

    return Mappings.wrapModifiableInt(resEquivalence);
  }

  private static <T> void refiningExactEquivalenceImpl(final int n,
      TransposableNetworkView<T, ?> positionView, ConstMapping.OfInt equivalenceRelativeTo,
      final int[] resultAndToRefine, BiPredicate<? super T, ? super T> comparator) {

    if (resultAndToRefine.length == 0) {
      return;
    }

    int[] verticesSortedByOldClass = PrimitiveCollections.countingSort(resultAndToRefine);

    int freeClass = 1;
    int[] representative = new int[n];
    int currentOldClass = verticesSortedByOldClass[0];
    int firstRepresentativeInOldClass = 0;
    resultAndToRefine[verticesSortedByOldClass[0]] = 0;
    for (int pos = 1; pos < n; ++pos) {
      int i = verticesSortedByOldClass[pos];
      if (currentOldClass != resultAndToRefine[i]) {
        currentOldClass = resultAndToRefine[i];
        firstRepresentativeInOldClass = freeClass;
      }
      boolean classAssigned = false;
      for (int eqClass = firstRepresentativeInOldClass; eqClass < freeClass; ++eqClass) {
        int j = representative[eqClass];
        int degi = positionView.countTies(i, j, i);
        int degj = positionView.countTies(i, j, j);
        boolean potentiallyEquivalent = degi <= degj;

        if (potentiallyEquivalent) {
          NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
          builder.ensureNode(2 * degi - 1);
          int ipos = 0;
          // there is some room for improvement here by sorting according to equivalenceRelativeTo
          for (T ri : positionView.ties(i, j, i)) {
            boolean matched = false;
            int ritarget = positionView.tieTarget(i, j, i, ri);
            int jpos = degi;
            for (T rj : positionView.ties(i, j, j)) {
              int rjtarget = positionView.tieTarget(i, j, j, rj);
              if (equivalenceRelativeTo.getInt(ritarget) == equivalenceRelativeTo
                  .getInt(rjtarget)) {
                if (comparator.test(ri, rj)) {
                  matched = true;
                  builder.addEdge(ipos, jpos);
                }
              }
              ++jpos;
            }
            if (!matched) {
              potentiallyEquivalent = false;
              break;
            }
            ++ipos;
          }
          potentiallyEquivalent = potentiallyEquivalent
              && BipartiteMatching.maximumMatchingSize(builder.build().asUndirectedGraph(),
                  Mappings.intRange(0, degi)) == degi;
        }
        if (potentiallyEquivalent) {
          degi = positionView.countTies(j, i, i);
          degj = positionView.countTies(j, i, j);
          potentiallyEquivalent = degi == degj;
        }
        if (potentiallyEquivalent) {
          NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
          builder.ensureNode(2 * degi - 1);
          int jpos = 0;
          // there is some room for improvement here by sorting according to equivalenceRelativeTo
          for (T rj : positionView.ties(j, i, j)) {
            int rjtarget = positionView.tieTarget(j, i, j, rj);
            boolean matched = false;
            int ipos = degj;
            for (T ri : positionView.ties(j, i, i)) {
              int ritarget = positionView.tieTarget(j, i, i, ri);
              if (equivalenceRelativeTo.getInt(ritarget) == equivalenceRelativeTo
                  .getInt(rjtarget)) {
                if (comparator.test(rj, ri)) {
                  matched = true;
                  builder.addEdge(jpos, ipos);
                }
              }
              ++ipos;
            }
            if (!matched) {
              potentiallyEquivalent = false;
              break;
            }
            ++jpos;
          }
          potentiallyEquivalent = potentiallyEquivalent
              && BipartiteMatching.maximumMatchingSize(builder.build().asUndirectedGraph(),
                  Mappings.intRange(0, degj)) == degj;
        }
        if (potentiallyEquivalent) {
          resultAndToRefine[i] = eqClass;
          classAssigned = true;
          break;
        }
      }
      if (!classAssigned) {
        representative[freeClass] = i;
        resultAndToRefine[i] = freeClass++;
      }
    }

  }

  /**
   * Computes the maximum strong structural equivalence. Space: O(n), Runtime: O(n+m) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @return an array representing the maximum relative regular equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static Mapping.OfInt strongStructuralEquivalence(int n,
      NetworkView<?, ?> positionView) {

    final int[] counts = new int[2 * n];
    counts[0] = n;

    final EquivalenceAlgorithmState state = new EquivalenceAlgorithmState(new int[n], counts, 2 * n,
        1);

    strongStructuralEquivalenceImpl(n, positionView, state);

    normalizePartition(state.getEquivalence(), state.getColorsMap());

    return Mappings.wrapModifiableInt(state.getEquivalence());
  }

  /**
   * Computes the maximum strong structural equivalence that refines a given equivalence. Space:
   * O(n), Runtime: O(n+m) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceToRefine
   *          array that represents the equivalence the output equivalence should be a refinement
   *          of. Here, two vertices are equivalent to each other if they have the same associated
   *          integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @return an array representing the maximum relative regular equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static Mapping.OfInt refiningStrongStructuralEquivalence(int n,
      NetworkView<?, ?> positionView, final ConstMapping.OfInt equivalenceToRefine) {

    checkEquivalenceFormat(equivalenceToRefine, EQUIVALENCE_TO_REFINE);

    final int[] resEquivalence = equivalenceToRefine.intStream().toArray();
    final int[] counts = new int[2 * n];
    int maxColor = 0;
    for (final int c : equivalenceToRefine) {
      maxColor = Math.max(c, maxColor);
      ++counts[c];
    }

    final EquivalenceAlgorithmState state = new EquivalenceAlgorithmState(resEquivalence, counts,
        2 * n, maxColor + 1);

    strongStructuralEquivalenceImpl(n, positionView, state);

    normalizePartition(state.getEquivalence(), state.getColorsMap());

    return Mappings.wrapModifiableInt(state.getEquivalence());
  }

  private static <T> void strongStructuralEquivalenceImpl(final int n,
      NetworkView<?, T> positionView, final EquivalenceAlgorithmState state) {
    strongStructuralEquivalenceImpl(n, positionView, state, false);
  }

  private static <T> void strongStructuralEquivalenceImpl(final int n,
      NetworkView<?, T> positionView, final EquivalenceAlgorithmState state,
      boolean skipLoops) {
    // main loop of the algorithm
    // iterate over all vertices
    // assign a new free color to the neighbors of vertices of the same
    // color (if they did not get assigned a new color already)
    // if we enter a block of vertices of different color, clean up
    // all internal data structures and garbage-collect unused colors
    for (int v = 0; v < n; ++v) {

      // split the neighbors of iv/v from the ones that are not known
      // to have a neighbor
      // of color "color"
      for (T r : positionView.inverseTies(v)) {
        final int w = positionView.inverseTieTarget(v, r);

        // skip loops if requested (used to implement weak structural equivalence)
        if (skipLoops && w == v) {
          continue;
        }

        final int oldcolor = state.getColor(w);

        // get the new color to assign to w
        int newcolor = state.getColorMappedToValue(oldcolor);
        // if no such color has been fixed yet,
        // allocate that color
        if (newcolor == -1) {

          newcolor = state.allocateColor();

          // store mapping from old color to new color
          state.setColorMappedToValue(oldcolor, newcolor);

          // put oldcolor in the processed list
          state.processedList.addInt(oldcolor);

          // trick to deal with backwards and forward edges

        }
        // update the color of neighbor w
        state.reassignColor(w, newcolor);
      }

      // do clean-up on processed colors
      // and garbage-collect unused colors
      for (int i = 0; i < state.getProcessedList().size(); ++i) {
        final int oldcolor = state.getProcessedList().getInt(i);
        // if color unused -> free it
        if (state.getCount(oldcolor) == 0) {
          state.freeColor(oldcolor);
        }
        // unset the color mapping
        state.setColorMappedToValue(oldcolor, -1);
      }
      // empty processed list
      state.getProcessedList().clear();
    }
  }

  /**
   * Computes the maximum strong structural equivalence for a network with weakly-ordered edges.
   * Space: O(n), Runtime: O(n+m log n) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum relative regular equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt strongStructuralEquivalence(int n,
      NetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {

    final int[] counts = new int[2 * n];
    counts[0] = n;

    final EquivalenceAlgorithmState state = new EquivalenceAlgorithmState(new int[n], counts, 2 * n,
        1);

    strongStructuralEquivalenceImpl(n, positionView, comparator, state);

    normalizePartition(state.getEquivalence(), state.getColorsMap());

    return Mappings.wrapModifiableInt(state.getEquivalence());
  }

  /**
   * Computes the maximum strong structural equivalence that refines a given equivalence for a
   * network with weakly-ordered edges. Space: O(n), Runtime: O(n+m log n) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param equivalenceToRefine
   *          array that represents the equivalence the output equivalence should be a refinement
   *          of. Here, two vertices are equivalent to each other if they have the same associated
   *          integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum relative regular equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt refiningStrongStructuralEquivalence(final int n,
      final NetworkView<? extends V, ? extends V> positionView,
      final ConstMapping.OfInt equivalenceToRefine, final Comparator<? super V> comparator) {

    checkEquivalenceFormat(equivalenceToRefine, EQUIVALENCE_TO_REFINE);

    final int[] resEquivalence = equivalenceToRefine.intStream().toArray();
    final int[] counts = new int[2 * n];
    int maxColor = 0;
    for (final int c : equivalenceToRefine) {
      maxColor = Math.max(c, maxColor);
      ++counts[c];
    }

    final EquivalenceAlgorithmState state = new EquivalenceAlgorithmState(resEquivalence, counts,
        2 * n, maxColor + 1);

    strongStructuralEquivalenceImpl(n, positionView, comparator, state);

    normalizePartition(state.getEquivalence(), state.getColorsMap());

    return Mappings.wrapModifiableInt(state.getEquivalence());
  }

  private static <T> void strongStructuralEquivalenceImpl(final int n,
      NetworkView<?, T> positionView, Comparator<? super T> comparator,
      final EquivalenceAlgorithmState state) {
    // main loop of the algorithm
    // iterate over all vertices
    // assign a new free color to the neighbors of vertices of the same
    // color (if they did not get assigned a new color already)
    // if we enter a block of vertices of different color, clean up
    // all internal data structures and garbage-collect unused colors
    for (int v = 0; v < n; ++v) {
      // split the neighbors of iv/v from non-neighbors with same color
      // for each set of edges

      @SuppressWarnings("unchecked")
      T[] vrels = (T[]) StreamSupport.stream(positionView.inverseTies(v).spliterator(), false)
          .sorted(comparator).toArray();

      if (vrels.length > 0) {
        T r = vrels[0];
        T rep = r;
        int pos = 0;
        do {

          {
            final int w = positionView.inverseTieTarget(v, r);
            final int oldcolor = state.getColor(w);

            // get the new color to assign to w
            int newcolor = state.getColorMappedToValue(oldcolor);
            // if no such color has been fixed yet,
            // allocate that color
            if (newcolor == -1) {

              newcolor = state.allocateColor();

              // store mapping from old color to new color
              state.setColorMappedToValue(oldcolor, newcolor);

              // put oldcolor in the processed list
              state.processedList.addInt(oldcolor);

              // trick to deal with backwards and forward edges

            }
            // update the color of neighbor w
            state.reassignColor(w, newcolor);
          }

          ++pos;
          r = pos < vrels.length ? vrels[pos] : null;

          // when all edges of one type are processed
          if (r == null || comparator.compare(rep, r) != 0) {
            // do clean-up on processed colors
            // and garbage-collect unused colors
            for (int i = 0; i < state.getProcessedList().size(); ++i) {
              final int oldcolor = state.getProcessedList().getInt(i);
              // if color unused -> free it
              if (state.getCount(oldcolor) == 0) {
                state.freeColor(oldcolor);
              }
              // unset the color mapping
              state.setColorMappedToValue(oldcolor, -1);
            }
            // empty processed list
            state.getProcessedList().clear();
            rep = r;
          }
        } while (r != null);
      }
    }
  }

  /**
   * Computes the maximum strong structural equivalence for a network with partially-ordered edges.
   * Space: O(m * max deg + n), Runtime: O(m * max deg + n) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @param <T>
   *          type for ties in forward direction.
   * @param <U>
   *          type for ties in backward direction.
   * @return an array representing the maximum relative regular equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V, T extends V, U extends V> Mapping.OfInt strongStructuralEquivalence(int n,
      NetworkView<T, U> positionView, PartialComparator<? super V> comparator) {

    if (n == 0) {
      return Mappings.wrapModifiableInt();
    }

    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.DIRECTED);
    builder.ensureNode(n - 1);

    PrimitiveList.OfInt isolates = Mappings.newIntList();

    BiPredicate<? super V, ? super V> pred = MiscUtils.lessEqualPredicate(comparator);

    NeighborhoodInclusion.structuralPreorder(n, positionView::ties, positionView::inverseTies,
        positionView::tieTarget, positionView::inverseTieTarget,
        new NeighborhoodInclusion.StructuralPreorderVisitor<T, U>() {
          @Override
          public void startNode(int i) {
          }

          @Override
          public void finishNode(int i) {
          }

          @Override
          public void isolate(int i) {
            isolates.addInt(i);
          }

          @Override
          public boolean canMatch(int source, T edge, int middle, U matchedby) {
            return pred.test(edge, matchedby);
          }

          @Override
          public void addDomination(int i, int j) {
            builder.addEdge(i, j);
          }
        });

    Mapping.OfInt components = AlgoProvider.getInstance().connectedness()
        .strongComponents(builder.build().asDirectedGraph());

    int[] partition = components.array();

    int representativeClass = -1;
    for (int isolate : isolates) {
      if (representativeClass == -1) {
        representativeClass = partition[isolate];
      }
      partition[isolate] = representativeClass;
    }

    int[] map = new int[n];
    Arrays.fill(map, -1);
    normalizePartition(partition, map);

    return Mappings.wrapModifiableInt(partition);
  }

  /**
   * Computes the maximum strong structural equivalence for a network. Space: O(n), Runtime: O(m * n
   * log(n)) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @return an array representing the maximum relative regular equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static Mapping.OfInt strongStructuralEquivalence(int n,
      TransposableNetworkView<?, ?> positionView) {

    return strongStructuralEquivalenceImpl(n, positionView, MiscUtils.alwaysTrue());
  }

  /**
   * Computes the maximum strong structural equivalence for a network with weakly-ordered edges.
   * Space: O(n), Runtime: O(m * n log(n)) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum relative regular equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt strongStructuralEquivalence(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView, Comparator<? super V> comparator) {

    return strongStructuralEquivalenceImpl(n, positionView,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum strong structural equivalence for a network with partially-ordered edges.
   * Space: O(n), Runtime: O(m * n log(n)) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionView
   *          Network as viewed from the position of the individual nodes
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum relative regular equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt strongStructuralEquivalence(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView, PartialComparator<? super V> comparator) {

    return strongStructuralEquivalenceImpl(n, positionView,
        MiscUtils.lessEqualPredicate(comparator));
  }

  private static <T> Mapping.OfInt strongStructuralEquivalenceImpl(final int n,
      final TransposableNetworkView<T, ?> positionView,
      final BiPredicate<? super T, ? super T> comparator) {

    int freeClass = 1;
    int[] representative = new int[n];

    int[] result = new int[n];
    for (int i = 1; i < n; ++i) {
      boolean classAssigned = false;
      for (int eqClass = 0; eqClass < freeClass; ++eqClass) {
        int j = representative[eqClass];
        boolean potentiallyEquivalent = positionView.countTies(i, j, i) <= positionView.countTies(i,
            j, j) && positionView.countTies(j, i, j) <= positionView.countTies(j, i, i);
        int thisI = i;
        int thisJ = j;
        if (potentiallyEquivalent) {
          @SuppressWarnings("unchecked")
          T[] tiesOfI = (T[]) StreamSupport.stream(positionView.ties(i, j, i).spliterator(), false)
              .sorted(
                  Comparator.comparingInt(ri -> positionView.tieTarget(thisI, thisJ, thisI, ri)))
              .toArray();
          @SuppressWarnings("unchecked")
          T[] tiesOfJ = (T[]) StreamSupport.stream(positionView.ties(i, j, j).spliterator(), false)
              .sorted(
                  Comparator.comparingInt(rj -> positionView.tieTarget(thisI, thisJ, thisJ, rj)))
              .toArray();
          int posi = 0;
          for (int posj = 0; posi < tiesOfI.length && posj < tiesOfJ.length; ++posi, ++posj) {
            T ri = tiesOfI[posi];
            T rj = tiesOfJ[posj];
            int targeti = positionView.tieTarget(i, j, i, ri);
            int targetj = positionView.tieTarget(i, j, j, rj);
            while (targetj < targeti && ++posj < tiesOfJ.length) {
              rj = tiesOfJ[posj];
              targetj = positionView.tieTarget(i, j, j, rj);
            }
            if (targeti != targetj || !comparator.test(ri, rj)) {
              potentiallyEquivalent = false;
              break;
            }
          }
          potentiallyEquivalent = potentiallyEquivalent && posi == tiesOfI.length;
        }
        if (potentiallyEquivalent) {
          @SuppressWarnings("unchecked")
          T[] tiesOfI = (T[]) StreamSupport.stream(positionView.ties(j, i, i).spliterator(), false)
              .sorted(
                  Comparator.comparingInt(ri -> positionView.tieTarget(thisJ, thisI, thisI, ri)))
              .toArray();
          @SuppressWarnings("unchecked")
          T[] tiesOfJ = (T[]) StreamSupport.stream(positionView.ties(j, i, j).spliterator(), false)
              .sorted(
                  Comparator.comparingInt(rj -> positionView.tieTarget(thisJ, thisI, thisJ, rj)))
              .toArray();
          int posj = 0;
          for (int posi = 0; posi < tiesOfI.length && posj < tiesOfJ.length; ++posi, ++posj) {
            T ri = tiesOfI[posi];
            T rj = tiesOfJ[posj];
            int targeti = positionView.tieTarget(j, i, i, ri);
            int targetj = positionView.tieTarget(j, i, j, rj);
            while (targeti < targetj && ++posi < tiesOfI.length) {
              ri = tiesOfI[posi];
              targeti = positionView.tieTarget(j, i, i, ri);
            }
            if (targeti != targetj || !comparator.test(rj, ri)) {
              potentiallyEquivalent = false;
              break;
            }
          }
          potentiallyEquivalent = potentiallyEquivalent && posj == tiesOfJ.length;
        }
        if (potentiallyEquivalent) {
          result[i] = eqClass;
          classAssigned = true;
          break;
        }
      }
      if (!classAssigned) {
        representative[freeClass] = i;
        result[i] = freeClass++;
      }
    }
    return Mappings.wrapModifiableInt(result);
  }

  /**
   * Computes the maximum weak structural equivalence of a network. Space: O(n), Runtime: O(n+m)
   * worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionViewFirstDirection
   *          Network as viewed from the position of the individual nodes in one direction
   * @param positionViewOtherDirection
   *          Network as viewed from the position of the individual nodes in the other direction
   * @return an array representing the maximum relative regular equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static Mapping.OfInt weakStructuralEquivalence(int n,
      NetworkView<?, ?> positionViewFirstDirection,
      NetworkView<?, ?> positionViewOtherDirection) {
    final int[] resEquivalence = new int[n];

    final int[] counts = new int[2 * n];
    counts[0] = n;

    final EquivalenceAlgorithmState state = new EquivalenceAlgorithmState(resEquivalence, counts,
        2 * n, 1);

    weakStructuralEquivalenceImpl(n, positionViewFirstDirection, positionViewOtherDirection, state);

    normalizePartition(state.getEquivalence(), state.getColorsMap());

    return Mappings.wrapModifiableInt(state.getEquivalence());
  }

  /**
   * Computes the maximum weak structural equivalence that refines a given equivalence. Space: O(n),
   * Runtime: O(n+m) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionViewFirstDirection
   *          Network as viewed from the position of the individual nodes in one direction
   * @param positionViewOtherDirection
   *          Network as viewed from the position of the individual nodes in the other direction
   * @param equivalenceToRefine
   *          array that represents the equivalence the output equivalence should be a refinement
   *          of. Here, two vertices are equivalent to each other if they have the same associated
   *          integer. The algorithm expects the following equivalence format:
   *          <code>p[0] = 0</code> and 
   *          <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * @return an array representing the maximum relative regular equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static Mapping.OfInt refiningWeakStructuralEquivalence(int n,
      NetworkView<?, ?> positionViewFirstDirection,
      NetworkView<?, ?> positionViewOtherDirection,
      ConstMapping.OfInt equivalenceToRefine) {

    checkEquivalenceFormat(equivalenceToRefine, EQUIVALENCE_TO_REFINE);

    final int[] resEquivalence = equivalenceToRefine.intStream().toArray();
    final int[] counts = new int[2 * n];
    int maxColor = 0;
    for (final int c : equivalenceToRefine) {
      maxColor = Math.max(c, maxColor);
      ++counts[c];
    }

    final EquivalenceAlgorithmState state = new EquivalenceAlgorithmState(resEquivalence, counts,
        2 * n, maxColor + 1);

    weakStructuralEquivalenceImpl(n, positionViewFirstDirection, positionViewOtherDirection, state);

    normalizePartition(state.getEquivalence(), state.getColorsMap());

    return Mappings.wrapModifiableInt(state.getEquivalence());
  }

  private static <T, U> void weakStructuralEquivalenceImpl(final int n,
      final NetworkView<?, T> positionViewFirstDirection,
      final NetworkView<?, U> positionViewOtherDirection,
      final EquivalenceAlgorithmState state) {

    int[] prevEquivalence = Arrays.copyOf(state.getEquivalence(), state.getEquivalence().length);

    // first, refine based on strong structural equivalence (skipping loops)
    strongStructuralEquivalenceImpl(n, positionViewFirstDirection, state, true);
    if (positionViewOtherDirection != null) {
      strongStructuralEquivalenceImpl(n, positionViewOtherDirection, state, true);
    }

    // now merge singletons, if they had not been separate in the original
    // equivalence
    final boolean[] mergedVertices = mergeSingletons(state, prevEquivalence);

    // and next, split based on adjacent structural equivalence
    adjacentStructuralEquivalenceImpl(n, positionViewFirstDirection, state, mergedVertices);
    if (positionViewOtherDirection != null) {
      adjacentStructuralEquivalenceImpl(n, positionViewOtherDirection, state, mergedVertices);
    }
  }

  /**
   * Internal function to compute the coarsest refining adjacent structural equivalence. Space:
   * O(n), Runtime: O(n+m) worst case.
   *
   * The result is returned in the state parameter.
   *
   * @param n
   *          number of nodes.
   * @param positionView
   *          network as viewed from the position of the individual nodes.
   * @param state
   *          represents the state of the equivalence that will be refined. Note that this state has
   *          to be internally consistent (processed list empty, counts correct, exactly those
   *          colors allocated that are in use), will be modified and used to return the result. The
   *          state has to be constructed such that the algorithm can use up to 2*n colors.
   * @param doSplit
   *          specifies which vertices are allowed to be split up/affected by the algorithm
   */
  private static <T> void adjacentStructuralEquivalenceImpl(final int n,
      NetworkView<?, T> positionView, final EquivalenceAlgorithmState state,
      final boolean[] doSplit) {

    // used to collect loops
    int[] loops = new int[n];
    int lastLoop = 0;

    // main loop of the algorithm
    // iterate over all vertices
    // assign a new free color to the neighbors of vertices of the same
    // color (if they did not get assigned a new color already)
    // if we enter a block of vertices of different color, clean up
    // all internal data structures and garbage-collect unused colors
    for (int v = 0; v < n; ++v) {

      // split the neighbors of iv/v from the ones that are not known
      // to have a neighbor
      // of color "color"
      for (final T r : positionView.inverseTies(v)) {

        int w = positionView.inverseTieTarget(v, r);

        // collect loops
        if (w == v) {
          loops[lastLoop++] = v;
          continue;
        }

        // do not split off those neighbors that are marked as not
        // to be split off
        if (!doSplit[w]) {
          continue;
        }

        final int oldcolor = state.getColor(w);

        // get the new color to assign to w
        int newcolor = state.getColorMappedToValue(oldcolor);
        // if no such color has been fixed yet,
        // allocate that color
        if (newcolor == -1) {

          newcolor = state.allocateColor();

          // store mapping from old color to new color
          state.setColorMappedToValue(oldcolor, newcolor);
          state.setColorMappedToValue(newcolor, newcolor);

          // put oldcolor in the processed list
          state.processedList.addInt(oldcolor);
        }
        // update the color of neighbor w
        state.reassignColor(w, newcolor);
      }
      // also treat iv/v like a neighbor in this split here
      if (doSplit[v]) {

        final int oldcolor = state.getColor(v);

        // get the new color to assign to w
        int newcolor = state.getColorMappedToValue(oldcolor);
        // if no such color has been fixed yet,
        // allocate that color
        if (newcolor == -1) {

          newcolor = state.allocateColor();

          // store mapping from old color to new color
          state.setColorMappedToValue(oldcolor, newcolor);
          state.setColorMappedToValue(newcolor, newcolor);

          // put oldcolor in the processed list
          state.processedList.addInt(oldcolor);
        }
        // update the color of neighbor w
        state.reassignColor(v, newcolor);
      }

      // do clean-up on processed colors
      // and garbage-collect unused colors
      for (int i = 0; i < state.getProcessedList().size(); ++i) {
        final int oldcolor = state.getProcessedList().getInt(i);
        // if color unused -> free it
        if (state.getCount(oldcolor) == 0) {
          state.freeColor(oldcolor);
        }
        // unset the color mapping
        final int newcolor = state.getColorMappedToValue(oldcolor);
        state.setColorMappedToValue(oldcolor, -1);
        state.setColorMappedToValue(newcolor, -1);
      }
      // empty processed list
      state.getProcessedList().clear();
    }

    // finally, handle loops
    // also split those that were marked as not to be split by reciprocity-aware splitting
    for (int pos = 0; pos < lastLoop; ++pos) {
      int v = loops[pos];

      final int oldcolor = state.getColor(v);

      // get the new color to assign to w
      int newcolor = state.getColorMappedToValue(oldcolor);
      // if no such color has been fixed yet,
      // allocate that color
      if (newcolor == -1) {

        newcolor = state.allocateColor();

        // store mapping from old color to new color
        state.setColorMappedToValue(oldcolor, newcolor);
        state.setColorMappedToValue(newcolor, newcolor);

        // put oldcolor in the processed list
        state.processedList.addInt(oldcolor);
      }
      // update the color of neighbor v
      state.reassignColor(v, newcolor);
    }

    // do clean-up on processed colors
    // and garbage-collect unused colors
    for (int i = 0; i < state.getProcessedList().size(); ++i) {
      final int oldcolor = state.getProcessedList().getInt(i);
      // if color unused -> free it
      if (state.getCount(oldcolor) == 0) {
        state.freeColor(oldcolor);
      }
      // unset the color mapping
      final int newcolor = state.getColorMappedToValue(oldcolor);
      state.setColorMappedToValue(oldcolor, -1);
      state.setColorMappedToValue(newcolor, -1);
    }
    // empty processed list
    state.getProcessedList().clear();
  }

  /**
   * Merges singletons in the equivalence relation represented by state, if they were equivalent in
   * equivalenceToRefine
   *
   * @param state
   *          represents the state of the equivalence that will be refined. Note that this state has
   *          to be internally consistent (processed list empty, counts correct, exactly those
   *          colors allocated that are in use), will be modified and used to return the result.
   * @param equivalenceToRefine
   *          singletons in the equivalence in state are only merged if they are equivalent in
   *          equivalenceToRefine
   * @return remembers for each vertex if it was a singleton or not
   */
  private static boolean[] mergeSingletons(final EquivalenceAlgorithmState state,
      final int[] equivalenceToRefine) {
    final int n = state.getEquivalence().length;
    final boolean[] merged = new boolean[n];

    // for each vertex in a singleton, merge it with any other singletons
    // found previously where the contained vertices are equivalent to the
    // current one in equivalenceToRefine
    for (int i = 0; i < n; ++i) {
      final int colorInState = state.getColor(i);
      if (state.getCount(colorInState) == 1) {
        merged[i] = true;
        final int colorInequivalenceToRefine = equivalenceToRefine[i];
        final int newColor = state.getColorMappedToValue(colorInequivalenceToRefine);
        if (newColor == -1) {
          state.setColorMappedToValue(colorInequivalenceToRefine, colorInState);
          state.getProcessedList().addInt(colorInequivalenceToRefine);
        } else {
          state.reassignColor(i, newColor);
          state.freeColor(colorInState);
        }
      }
    }

    // clean up temporary state
    for (int i = 0; i < state.getProcessedList().size(); ++i) {
      state.setColorMappedToValue(state.getProcessedList().getInt(i), -1);
    }
    state.getProcessedList().clear();

    return merged;
  }

  /**
   * Computes the maximum weak structural equivalence for a network with weakly-ordered edges.
   * Space: O(m * max deg + n), Runtime: O(m * max deg + n log n) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionViewFirstDirection
   *          Network as viewed from the position of the individual nodes in one direction
   * @param positionViewOtherDirection
   *          Network as viewed from the position of the individual nodes in the other direction
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum weak structural equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt weakStructuralEquivalence(final int n,
      NetworkView<? extends V, ? extends V> positionViewFirstDirection,
      NetworkView<? extends V, ? extends V> positionViewOtherDirection,
      Comparator<? super V> comparator) {
    return weakStructuralEquivalenceImpl(n, positionViewFirstDirection, positionViewOtherDirection,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum weak structural equivalence for a network with partially-ordered edges.
   * Space: O(m * max deg + n * n), Runtime: O(m * max deg + n * n) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionViewFirstDirection
   *          Network as viewed from the position of the individual nodes in one direction
   * @param positionViewOtherDirection
   *          Network as viewed from the position of the individual nodes in the other direction
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum weak structural equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt weakStructuralEquivalence(final int n,
      NetworkView<? extends V, ? extends V> positionViewFirstDirection,
      NetworkView<? extends V, ? extends V> positionViewOtherDirection,
      PartialComparator<? super V> comparator) {
    return weakStructuralEquivalenceImpl(n, positionViewFirstDirection, positionViewOtherDirection,
        MiscUtils.lessEqualPredicate(comparator));
  }

  private static <V, T extends V, U extends V, W extends V, X extends V> Mapping.OfInt weakStructuralEquivalenceImpl(
      int n,
      NetworkView<T, U> positionViewOneDirection,
      NetworkView<W, X> positionViewOtherDirection,
      BiPredicate<? super V, ? super V> comparator) {

    if (n == 0) {
      return Mappings.wrapModifiableInt();
    }

    NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.DIRECTED);
    builder.ensureNode(n - 1);

    PrimitiveList.OfInt isolates = Mappings.newIntList();
    boolean[] isOtherIsolate = new boolean[n];
    boolean[] isFirstIsolate = new boolean[n];
    for (int i = 0; i < n; ++i) {
      if (positionViewOneDirection.countTies(i) == 0) {
        isFirstIsolate[i] = true;
      }
    }
    LongSet set = PrimitiveContainers.longHashSet();
    if (positionViewOtherDirection != null) {

      @SuppressWarnings({ "unchecked" })
      PrimitiveList<Pair<Integer, W>> otherSingleLoops = Mappings
          .newList((Class<Pair<Integer, W>>) (Class<?>) Pair.class);
      NeighborhoodInclusion.VicinalPreorderVisitor<W, X> otherVisitor = new NeighborhoodInclusion.VicinalPreorderVisitor<W, X>() {

        @Override
        public void startNode(int i) {
        }

        @Override
        public void finishNode(int i) {
        }

        @Override
        public void isolate(int i) {
          isOtherIsolate[i] = true;
        }

        @Override
        public boolean canMatch(int source, W edge, int middle, X matchedby) {
          return comparator.test(edge, matchedby);
        }

        @Override
        public boolean canMatchLoop(int firstSource, W firstEdge, int secondSource, W secondEdge) {
          return comparator.test(firstEdge, secondEdge);
        }

        @Override
        public void addDomination(int i, int j) {
          set.add(IntPair.tuple(i, j));
          if (isFirstIsolate[i] && isFirstIsolate[j]) {
            builder.addEdge(i, j);
          }
        }

        @Override
        public void visitLoop(int i, W loop, boolean onlyIncidentEdge) {
          if (onlyIncidentEdge) {
            otherSingleLoops.add(new Pair<>(i, loop));
          }
        }
      };

      NeighborhoodInclusion.vicinalPreorder(n, positionViewOtherDirection::ties,
          positionViewOtherDirection::inverseTies, positionViewOtherDirection::tieTarget,
          positionViewOtherDirection::inverseTieTarget, otherVisitor);
      for (Pair<Integer, W> ri : otherSingleLoops) {
        for (Pair<Integer, W> rj : otherSingleLoops) {
          if (comparator.test(ri.getSecond(), rj.getSecond())) {
            set.add(IntPair.tuple(ri.getFirst(), rj.getFirst()));
          }
        }
      }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    PrimitiveList<Pair<Integer, T>> singleLoops = (PrimitiveList<Pair<Integer, T>>) Mappings
        .newList((Class) Pair.class);

    NeighborhoodInclusion.VicinalPreorderVisitor<T, U> visitor = new NeighborhoodInclusion.VicinalPreorderVisitor<T, U>() {

      @Override
      public void startNode(int i) {
      }

      @Override
      public void finishNode(int i) {
      }

      @Override
      public void isolate(int i) {
        if (positionViewOtherDirection == null || isOtherIsolate[i]) {
          isolates.addInt(i);
        }
      }

      @Override
      public boolean canMatch(int source, T edge, int middle, U matchedby) {
        return comparator.test(edge, matchedby);
      }

      @Override
      public boolean canMatchLoop(int firstSource, T firstEdge, int secondSource, T secondEdge) {
        return comparator.test(firstEdge, secondEdge);
      }

      @Override
      public void addDomination(int i, int j) {
        if (positionViewOtherDirection == null || set.contains(IntPair.tuple(i, j))
            || (isOtherIsolate[i] && isOtherIsolate[j])) {
          builder.addEdge(i, j);
        }
      }

      @Override
      public void visitLoop(int i, T loop, boolean onlyIncidentEdge) {
        if (onlyIncidentEdge) {
          singleLoops.add(new Pair<>(i, loop));
        }
      }
    };

    NeighborhoodInclusion.vicinalPreorder(n, positionViewOneDirection::ties,
        positionViewOneDirection::inverseTies, positionViewOneDirection::tieTarget,
        positionViewOneDirection::inverseTieTarget, visitor);
    for (Pair<Integer, T> ri : singleLoops) {
      for (Pair<Integer, T> rj : singleLoops) {
        if (comparator.test(ri.getSecond(), rj.getSecond())) {
          int i = ri.getFirst(), j = rj.getFirst();
          if (positionViewOtherDirection == null || set.contains(IntPair.tuple(i, j))
              || (isOtherIsolate[i] && isOtherIsolate[j]))
            builder.addEdge(i, j);
        }
      }
    }

    Mapping.OfInt components = AlgoProvider.getInstance().connectedness()
        .strongComponents(builder.build().asDirectedGraph());

    int[] partition = components.array();

    int representativeClass = -1;
    for (int isolate : isolates) {
      if (representativeClass == -1) {
        representativeClass = partition[isolate];
      }
      partition[isolate] = representativeClass;
    }

    int[] map = new int[n];
    Arrays.fill(map, -1);
    normalizePartition(partition, map);

    return Mappings.wrapModifiableInt(partition);
  }

  /**
   * Computes the maximum weak structural equivalence for a network. Space: O(n), Runtime: O(m * n
   * log (n)) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionViewFirstDirection
   *          Network as viewed from the position of the individual nodes in one direction
   * @param positionViewOtherDirection
   *          Network as viewed from the position of the individual nodes in the other direction
   * @return an array representing the maximum weak structural equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static Mapping.OfInt weakStructuralEquivalence(final int n,
      TransposableNetworkView<?, ?> positionViewFirstDirection,
      TransposableNetworkView<?, ?> positionViewOtherDirection) {
    return weakStructuralEquivalenceImpl(n, positionViewFirstDirection, positionViewOtherDirection,
        MiscUtils.alwaysTrue());
  }

  /**
   * Computes the maximum weak structural equivalence for a network with weakly-ordered edges.
   * Space: O(n), Runtime: O(m * n log (n)) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionViewFirstDirection
   *          Network as viewed from the position of the individual nodes in one direction
   * @param positionViewOtherDirection
   *          Network as viewed from the position of the individual nodes in the other direction
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum weak structural equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt weakStructuralEquivalence(final int n,
      TransposableNetworkView<? extends V, ? extends V> positionViewFirstDirection,
      TransposableNetworkView<? extends V, ? extends V> positionViewOtherDirection,
      Comparator<? super V> comparator) {
    return weakStructuralEquivalenceImpl(n, positionViewFirstDirection, positionViewOtherDirection,
        MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum weak structural equivalence for a network with partially-ordered edges.
   * Space: O(n), Runtime: O(m * n log (n)) worst case.
   *
   * @param n
   *          Number of nodes
   * @param positionViewFirstDirection
   *          Network as viewed from the position of the individual nodes in one direction
   * @param positionViewOtherDirection
   *          Network as viewed from the position of the individual nodes in the other direction
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum weak structural equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt weakStructuralEquivalence(final int n,
      TransposableNetworkView<? extends V, ? extends V> positionViewFirstDirection,
      TransposableNetworkView<? extends V, ? extends V> positionViewOtherDirection,
      PartialComparator<? super V> comparator) {
    return weakStructuralEquivalenceImpl(n, positionViewFirstDirection, positionViewOtherDirection,
        MiscUtils.lessEqualPredicate(comparator));
  }

  private static <T> int transposedTieTarget(
      TransposableNetworkView<T, ?> positionViewFirstDirection, final int lhsComparison,
      final int rhsComparison, final int node, T rj) {
    int target = positionViewFirstDirection.tieTarget(lhsComparison, rhsComparison, node, rj);
    if (rhsComparison == node) {
      if (target == node) {
        return lhsComparison;
      } else if (target == lhsComparison) {
        return node;
      }
    }
    return target;
  }

  private static <S, T extends S, U, V extends S, W> Mapping.OfInt weakStructuralEquivalenceImpl(
      final int n,
      final TransposableNetworkView<T, U> positionViewFirstDirection,
      final TransposableNetworkView<V, W> positionViewOtherDirection,
      final BiPredicate<? super S, ? super S> comparator) {

    int freeClass = 1;
    int[] representative = new int[n];

    int[] result = new int[n];
    for (int i = 1; i < n; ++i) {
      boolean classAssigned = false;
      for (int eqClass = 0; eqClass < freeClass; ++eqClass) {
        int j = representative[eqClass];
        boolean potentiallyEquivalent = positionViewFirstDirection.countTies(i, j,
            i) <= positionViewFirstDirection.countTies(i, j, j)
            && positionViewFirstDirection.countTies(j, i, j) <= positionViewFirstDirection
                .countTies(j, i, i);
        if (potentiallyEquivalent && positionViewOtherDirection != null) {
          potentiallyEquivalent = positionViewOtherDirection.countTies(i, j,
              i) <= positionViewOtherDirection
                .countTies(i, j, j)
            && positionViewOtherDirection.countTies(j, i, j) <= positionViewOtherDirection
                .countTies(j, i, i);
        }
        int thisI = i;
        int thisJ = j;
        if (potentiallyEquivalent) {
          @SuppressWarnings("unchecked")
          T[] tiesOfI = (T[]) StreamSupport
              .stream(positionViewFirstDirection.ties(i, j, i).spliterator(), false)
              .sorted(
                  Comparator.comparingInt(
                      ri -> positionViewFirstDirection.tieTarget(thisI, thisJ, thisI, ri)))
              .toArray();
          @SuppressWarnings("unchecked")
          T[] tiesOfJ = (T[]) StreamSupport
              .stream(positionViewFirstDirection.ties(i, j, j).spliterator(), false)
              .sorted(
                  Comparator.comparingInt(
                      rj -> transposedTieTarget(positionViewFirstDirection, thisI, thisJ, thisJ,
                          rj)))
              .toArray();
          int posi = 0;
          for (int posj = 0; posi < tiesOfI.length && posj < tiesOfJ.length; ++posi, ++posj) {
            T ri = tiesOfI[posi];
            T rj = tiesOfJ[posj];
            int targeti = positionViewFirstDirection.tieTarget(i, j, i, ri);
            int targetj = transposedTieTarget(positionViewFirstDirection, i, j, j, rj);
            while (targetj < targeti && ++posj < tiesOfJ.length) {
              rj = tiesOfJ[posj];
              targetj = transposedTieTarget(positionViewFirstDirection, i, j, j, rj);
            }
            if (targeti != targetj || !comparator.test(ri, rj)) {
              potentiallyEquivalent = false;
              break;
            }
          }
          potentiallyEquivalent = potentiallyEquivalent && posi == tiesOfI.length;
        }
        if (potentiallyEquivalent) {
          @SuppressWarnings("unchecked")
          T[] tiesOfI = (T[]) StreamSupport
              .stream(positionViewFirstDirection.ties(j, i, i).spliterator(), false)
              .sorted(
                  Comparator.comparingInt(
                      ri -> transposedTieTarget(positionViewFirstDirection, thisJ, thisI, thisI,
                          ri)))
              .toArray();
          @SuppressWarnings("unchecked")
          T[] tiesOfJ = (T[]) StreamSupport
              .stream(positionViewFirstDirection.ties(j, i, j).spliterator(), false)
              .sorted(
                  Comparator.comparingInt(
                      rj -> positionViewFirstDirection.tieTarget(thisJ, thisI, thisJ, rj)))
              .toArray();
          int posj = 0;
          for (int posi = 0; posi < tiesOfI.length && posj < tiesOfJ.length; ++posi, ++posj) {
            T ri = tiesOfI[posi];
            T rj = tiesOfJ[posj];
            int targeti = transposedTieTarget(positionViewFirstDirection, j, i, i, ri);
            int targetj = positionViewFirstDirection.tieTarget(j, i, j, rj);
            while (targeti < targetj && ++posi < tiesOfI.length) {
              ri = tiesOfI[posi];
              targeti = transposedTieTarget(positionViewFirstDirection, j, i, i, ri);
            }
            if (targeti != targetj || !comparator.test(rj, ri)) {
              potentiallyEquivalent = false;
              break;
            }
          }
          potentiallyEquivalent = potentiallyEquivalent && posj == tiesOfJ.length;
        }
        if (potentiallyEquivalent && positionViewOtherDirection != null) {

          @SuppressWarnings("unchecked")
          V[] tiesOfI = (V[]) StreamSupport
              .stream(positionViewOtherDirection.ties(i, j, i).spliterator(), false)
              .sorted(Comparator.comparingInt(
                  ri -> positionViewOtherDirection.tieTarget(thisI, thisJ, thisI, ri)))
              .toArray();
          @SuppressWarnings("unchecked")
          V[] tiesOfJ = (V[]) StreamSupport
              .stream(positionViewOtherDirection.ties(i, j, j).spliterator(), false)
              .sorted(Comparator.comparingInt(
                  rj -> transposedTieTarget(positionViewOtherDirection, thisI, thisJ, thisJ, rj)))
              .toArray();
          int posi = 0;
          for (int posj = 0; posi < tiesOfI.length && posj < tiesOfJ.length; ++posi, ++posj) {
            V ri = tiesOfI[posi];
            V rj = tiesOfJ[posj];
            int targeti = positionViewOtherDirection.tieTarget(i, j, i, ri);
            int targetj = transposedTieTarget(positionViewOtherDirection, i, j, j, rj);
            while (targetj < targeti && ++posj < tiesOfJ.length) {
              rj = tiesOfJ[posj];
              targetj = transposedTieTarget(positionViewOtherDirection, i, j, j, rj);
            }
            if (targeti != targetj || !comparator.test(ri, rj)) {
              potentiallyEquivalent = false;
              break;
            }
          }
          potentiallyEquivalent = potentiallyEquivalent && posi == tiesOfI.length;
        }

        if (potentiallyEquivalent && positionViewOtherDirection != null) {
          @SuppressWarnings("unchecked")
          V[] tiesOfI = (V[]) StreamSupport
              .stream(positionViewOtherDirection.ties(j, i, i).spliterator(), false)
              .sorted(Comparator.comparingInt(
                  ri -> transposedTieTarget(positionViewOtherDirection, thisJ, thisI, thisI, ri)))
              .toArray();
          @SuppressWarnings("unchecked")
          V[] tiesOfJ = (V[]) StreamSupport
              .stream(positionViewOtherDirection.ties(j, i, j).spliterator(), false)
              .sorted(Comparator.comparingInt(
                  rj -> positionViewOtherDirection.tieTarget(thisJ, thisI, thisJ, rj)))
              .toArray();
          int posj = 0;
          for (int posi = 0; posi < tiesOfI.length && posj < tiesOfJ.length; ++posi, ++posj) {
            V ri = tiesOfI[posi];
            V rj = tiesOfJ[posj];
            int targeti = transposedTieTarget(positionViewOtherDirection, j, i, i, ri);
            int targetj = positionViewOtherDirection.tieTarget(j, i, j, rj);
            while (targeti < targetj && ++posi < tiesOfI.length) {
              ri = tiesOfI[posi];
              targeti = transposedTieTarget(positionViewOtherDirection, j, i, i, ri);
            }
            if (targeti != targetj || !comparator.test(rj, ri)) {
              potentiallyEquivalent = false;
              break;
            }
          }
          potentiallyEquivalent = potentiallyEquivalent && posj == tiesOfJ.length;
        }
        if (potentiallyEquivalent) {
          result[i] = eqClass;
          classAssigned = true;
          break;
        }
      }
      if (!classAssigned) {
        representative[freeClass] = i;
        result[i] = freeClass++;
      }
    }
    return Mappings.wrapModifiableInt(result);
  }

  /**
   * Computes the maximum weak equivalence for an unweighted network.
   * Space: O(n), Runtime: O(n) worst case.
   *
   * @param n
   *          number of nodes.
   * @param positionView
   *          network as viewed from the position of the individual nodes.
   * @param <T>
   *          type of represented ties in forward direction.
   * @return an array representing the maximum weak equivalence: vertices that have the same
   *         associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <T> Mapping.OfInt weakEquivalence(final int n,
      NetworkView<T, ?> positionView) {
    if (n == 0) {
      return Mappings.wrapModifiableInt();
    }
    int isolateClass = positionView.ties(0).iterator().hasNext() ? 1 : 0;

    int[] result = new int[n];
    for (int i = 1; i < n; ++i) {
      result[i] = positionView.ties(i).iterator().hasNext() ? 1 - isolateClass : isolateClass;
    }
    return Mappings.wrapModifiableInt(result);
  }

  /**
   * Computes the maximum weak equivalence for a network with weakly ordered ties.
   * Space: O(n), Runtime: O(n) worst case.
   *
   * @param n
   *          number of nodes.
   * @param positionView
   *          network as viewed from the position of the individual nodes.
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @param <T>
   *          type of represented ties in forward direction.
   * @return an array representing the maximum weak equivalence: vertices that have the same
   *         associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V, T extends V> Mapping.OfInt weakEquivalence(final int n,
      final NetworkView<T, ? extends V> positionView, Comparator<? super V> comparator) {
    if (n == 0) {
      return Mappings.wrapModifiableInt();
    }
    int firstFreeClass = 0;
    Map<T, Integer> relToClass = new TreeMap<>(comparator);
    int[] result = new int[n];
    for (int i = 0; i < n; ++i) {
      T max = null;
      for (T r : positionView.ties(i)) {
        if (max == null || comparator.compare(max, r) < 0) {
          max = r;
        }
      }
      Integer eqClass = relToClass.get(max);
      if (eqClass != null) {
        result[i] = eqClass;
      } else {
        result[i] = firstFreeClass;
        relToClass.put(max, firstFreeClass++);
      }
    }
    return Mappings.wrapModifiableInt(result);
  }

  /**
   * Computes the maximum weak equivalence for an unweighted network.
   * Space: O(n), Runtime: O(n) worst case.
   *
   * @param n
   *          number of nodes.
   * @param positionView
   *          network as viewed from the position of the individual nodes.
   * @return an array representing the maximum weak equivalence: vertices that have the same
   *         associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static Mapping.OfInt weakEquivalence(int n,
      TransposableNetworkView<?, ?> positionView) {

    return weakEquivalenceImpl(n, positionView, MiscUtils.alwaysTrue());
  }

  /**
   * Computes the maximum weak equivalence for a network with weakly ordered ties.
   * Space: O(n), Runtime: O(n) worst case.
   *
   * @param n
   *          number of nodes.
   * @param positionView
   *          network as viewed from the position of the individual nodes.
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum weak equivalence: vertices that have the same
   *         associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt weakEquivalence(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {

    return weakEquivalenceImpl(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum weak equivalence for a network with partially ordered ties.
   * Space: O(n), Runtime: O(n) worst case.
   *
   * @param n
   *          number of nodes.
   * @param positionView
   *          network as viewed from the position of the individual nodes.
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum weak equivalence: vertices that have the same
   *         associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt weakEquivalence(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator) {

    return weakEquivalenceImpl(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  private static <T> Mapping.OfInt weakEquivalenceImpl(final int n,
      final TransposableNetworkView<T, ?> positionView,
      final BiPredicate<? super T, ? super T> comparator) {

    int freeClass = 1;
    int[] representative = new int[n];

    int[] result = new int[n];
    for (int i = 1; i < n; ++i) {
      boolean classAssigned = false;
      for (int eqClass = 0; eqClass < freeClass; ++eqClass) {
        int j = representative[eqClass];
        boolean potentiallyEquivalent = true;
        for (T ri : positionView.ties(i, j, i)) {
          boolean matched = false;
          for (T rj : positionView.ties(i, j, j)) {
            if (comparator.test(ri, rj)) {
              matched = true;
              break;
            }
          }
          if (!matched) {
            potentiallyEquivalent = false;
            break;
          }
        }
        if (potentiallyEquivalent) {
          for (T rj : positionView.ties(j, i, j)) {
            boolean matched = false;
            for (T ri : positionView.ties(j, i, i)) {
              if (comparator.test(rj, ri)) {
                matched = true;
                break;
              }
            }
            if (!matched) {
              potentiallyEquivalent = false;
              break;
            }
          }
        }
        if (potentiallyEquivalent) {
          result[i] = eqClass;
          classAssigned = true;
          break;
        }
      }
      if (!classAssigned) {
        representative[freeClass] = i;
        result[i] = freeClass++;
      }
    }
    return Mappings.wrapModifiableInt(result);
  }

  /**
   * Computes the maximum weak equivalence for an unweighted network.
   * Space: O(n), Runtime: O(n) worst case.
   *
   * @param n
   *          number of nodes.
   * @param positionView
   *          network as viewed from the position of the individual nodes.
   * @param <T>
   *          type of represented ties in forward direction.
   * @return an array representing the maximum weak exact equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <T> Mapping.OfInt weakExactEquivalence(final int n,
      final NetworkView<T, ?> positionView) {
    int[] degreeClassAssignment = new int[n];
    Arrays.fill(degreeClassAssignment, -1);
    int[] resultEquivalence = new int[n];
    int firstFreeClass = 0;

    for (int i = 0; i < n; ++i) {
      int deg = positionView.countTies(i);
      int eqClass = degreeClassAssignment[deg];
      if (eqClass < 0) {
        degreeClassAssignment[deg] = eqClass = firstFreeClass++;
      }
      resultEquivalence[i] = eqClass;
    }
    return Mappings.wrapModifiableInt(resultEquivalence);
  }

  /**
   * Computes the maximum weak equivalence for a network with weakly ordered ties.
   * Space: O(n), Runtime: O(n) worst case.
   *
   * @param n
   *          number of nodes.
   * @param positionView
   *          network as viewed from the position of the individual nodes.
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum weak exact equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt weakExactEquivalence(int n,
      NetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    int m = positionView.countAllTies();
    final int[] counts = new int[2 * m + n];
    counts[0] = n;

    int[] result = new int[n];
    final EquivalenceAlgorithmState state = new EquivalenceAlgorithmState(result, counts, 2 * m + n,
        1);

    weakExactEquivalenceImpl(n, positionView, comparator, state);

    normalizePartition(state.getEquivalence(), state.getColorsMap());

    return Mappings.wrapModifiableInt(result);
  }

  private static <T> void weakExactEquivalenceImpl(final int n,
      final NetworkView<T, ?> positionView, Comparator<? super T> comparator,
      EquivalenceAlgorithmState state) {

    // The color management ensures that the algorithm
    // needs at most 2m+n colors at once.

    // preprocessing: order neighborhoods by comparator order
    // and determine the least elements in neighborhoods

    @SuppressWarnings("unchecked")
    T[][] sortedRelationships = (T[][]) new Object[n][];

    for (int i = 0; i < n; ++i) {
      @SuppressWarnings("unchecked")
      T[] relationshipsForI = (T[]) StreamSupport.stream(positionView.ties(i).spliterator(), false)
          .sorted(comparator).toArray();
      sortedRelationships[i] = relationshipsForI;
    }

    int[] positions = new int[n];

    PriorityQueue<Pair<Integer, T>> heap = new PriorityQueue<>(n,
        (lhs, rhs) -> comparator.compare(lhs.getSecond(), rhs.getSecond()));

    for (int i = 0; i < n; ++i) {
      if (sortedRelationships[i].length > 0) {
        heap.add(new Pair<>(i, sortedRelationships[i][0]));
      }
    }

    // main loop of the algorithm
    // assign a new colors to vertices per edge of current weight class
    // if we have processed all edges of one class, clean up
    // all internal data structures and garbage-collect unused colors

    T expected = !heap.isEmpty() ? heap.peek().getSecond() : null;
    // while we still have some unprocessed relationships for vertices
    while (!heap.isEmpty()) {

      final Pair<Integer, T> p = heap.poll();
      T r = p.getSecond();
      int v = p.getFirst();
      int pos = positions[v];

      do {
        final int oldcolor = state.getColor(v);

        // get the new color to assign to v
        int newcolor = state.getColorMappedToValue(oldcolor);
        // if no such color has been fixed yet,
        // allocate that color and mark old
        // color as processed
        if (newcolor == -1) {

          newcolor = state.allocateColor();

          // put oldcolor in the processed list
          state.getProcessedList().addInt(oldcolor);

          // save mapping from old color to new color
          state.setColorMappedToValue(oldcolor, newcolor);
        }
        // update the color of v
        state.reassignColor(v, newcolor);

        // to next relationship
        ++pos;
        r = pos < sortedRelationships[v].length ? sortedRelationships[v][pos] : null;
      } while (r != null && comparator.compare(r, expected) == 0);

      // is a relationship of another class left for this vertex?
      // then store it for later processing and reinsert this vertex
      // into the heap
      if (r != null) {
        heap.add(new Pair<>(p.getFirst(), r));
        positions[v] = pos;
      }

      // if all edges of a type have been processed
      // do clean-up on processed colors
      // and garbage-collect unused colors
      T next = !heap.isEmpty() ? heap.peek().getSecond() : null;
      if (heap.isEmpty() || comparator.compare(next, expected) != 0) {
        expected = next;
        for (int i = 0; i < state.getProcessedList().size(); ++i) {
          final int oldcolor = state.getProcessedList().getInt(i);
          // if color unused -> free it
          if (state.getCount(oldcolor) == 0) {
            state.freeColor(oldcolor);
          }
          // unset the color mapping
          state.setColorMappedToValue(oldcolor, -1);
        }
        // empty processed list
        state.getProcessedList().clear();
      }
    }

  }

  /**
   * Computes the maximum weak equivalence for an unweighted network.
   * Space: O(n), Runtime: O(n) worst case.
   *
   * @param n
   *          number of nodes.
   * @param positionView
   *          network as viewed from the position of the individual nodes.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum weak exact equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt weakExactEquivalence(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView) {
    return weakExactEquivalence(n, positionView, MiscUtils.alwaysTrue());
  }

  /**
   * Computes the maximum weak equivalence for a network with weakly ordered ties.
   * Space: O(n), Runtime: O(n) worst case.
   *
   * @param n
   *          number of nodes.
   * @param positionView
   *          network as viewed from the position of the individual nodes.
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum weak exact equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt weakExactEquivalence(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      Comparator<? super V> comparator) {
    return weakExactEquivalence(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  /**
   * Computes the maximum weak equivalence for a network with partially ordered ties.
   * Space: O(n), Runtime: O(n) worst case.
   *
   * @param n
   *          number of nodes.
   * @param positionView
   *          network as viewed from the position of the individual nodes.
   * @param comparator
   *          compares the ties.
   * @param <V>
   *          base type of represented ties.
   * @return an array representing the maximum weak exact equivalence: vertices that have the
   *         same associated number in the array (based on their IDs) are structurally equivalent.
   *         The algorithm guarantees the following properties of the output array:
   *         <code>p[0] = 0</code> and <code>p[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   */
  public static <V> Mapping.OfInt weakExactEquivalence(int n,
      TransposableNetworkView<? extends V, ? extends V> positionView,
      PartialComparator<? super V> comparator) {
    return weakExactEquivalence(n, positionView, MiscUtils.lessEqualPredicate(comparator));
  }

  private static <T> Mapping.OfInt weakExactEquivalence(final int n,
      final TransposableNetworkView<T, ?> positionView,
      BiPredicate<? super T, ? super T> comparator) {

    int freeClass = 1;
    int[] representative = new int[n];
    int[] result = new int[n];
    for (int i = 1; i < n; ++i) {
      boolean classAssigned = false;
      for (int eqClass = 0; eqClass < freeClass; ++eqClass) {
        int j = representative[eqClass];
        int degi = positionView.countTies(i, j, i);
        boolean potentiallyEquivalent = degi == positionView.countTies(i, j, j);

        if (potentiallyEquivalent) {
          NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
          builder.ensureNode(2 * degi - 1);
          int ipos = 0;
          for (T ri : positionView.ties(i, j, i)) {
            boolean matched = false;
            int jpos = degi;
            for (T rj : positionView.ties(i, j, j)) {
              if (comparator.test(ri, rj)) {
                matched = true;
                builder.addEdge(ipos, jpos);
              }
              ++jpos;
            }
            if (!matched) {
              potentiallyEquivalent = false;
              break;
            }
            ++ipos;
          }
          potentiallyEquivalent = potentiallyEquivalent
              && BipartiteMatching.maximumMatchingSize(builder.build().asUndirectedGraph(),
                  Mappings.intRange(0, degi)) == degi;
        }
        if (potentiallyEquivalent) {
          degi = positionView.countTies(j, i, i);
          potentiallyEquivalent = degi == positionView.countTies(j, i, j);
        }
        if (potentiallyEquivalent) {
          NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
          builder.ensureNode(2 * degi - 1);
          int jpos = 0;
          for (T rj : positionView.ties(j, i, j)) {
            boolean matched = false;
            int ipos = degi;
            for (T ri : positionView.ties(j, i, i)) {
              if (comparator.test(rj, ri)) {
                matched = true;
                builder.addEdge(jpos, ipos);
              }
              ++ipos;
            }
            if (!matched) {
              potentiallyEquivalent = false;
              break;
            }
            ++jpos;
          }
          potentiallyEquivalent = potentiallyEquivalent
              && BipartiteMatching.maximumMatchingSize(builder.build().asUndirectedGraph(),
                  Mappings.intRange(0, degi)) == degi;
        }
        if (potentiallyEquivalent) {
          result[i] = eqClass;
          classAssigned = true;
          break;
        }
      }
      if (!classAssigned) {
        representative[freeClass] = i;
        result[i] = freeClass++;
      }
    }
    return Mappings.wrapModifiableInt(result);
  }

  /**
   * Normalizes a partition representation p such that <code>p[0] = 0</code> and
   * <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   * 
   * @param p the partition
   * @return the partition in normalized form
   */
  public static int[] normalizePartition(final int[] p) {
    int maxColor = -1;
    for (int i = 0; i < p.length; ++i) {
      maxColor = Math.max(maxColor, p[i]);
    }
    int[] store = new int[maxColor + 1];
    Arrays.fill(store, -1);
    int[] copy = Arrays.copyOf(p, p.length);
    normalizePartition(copy, store);
    return copy;
  }

  /**
   * Normalizes a partition representation p such that <code>p[0] = 0</code> and
   * <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   *
   * @param p the partition
   * @return the partition in normalized form
   */
  public static Mapping.OfInt normalizePartition(ConstMapping.OfInt p) {
    int maxColor = -1;
    for (int i = 0; i < p.size(); ++i) {
      maxColor = Math.max(maxColor, p.getInt(i));
    }
    int[] store = new int[maxColor + 1];
    Arrays.fill(store, -1);
    int[] copy = p.intStream().toArray();
    normalizePartition(copy, store);
    return Mappings.wrapModifiableInt(copy);
  }

  /**
   * Normalizes a partition representation p such that <code>p[0] = 0</code> and
   * <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   *
   * @param p                 the partition
   * @param tempColorMapStore temporary storage, at least
   *                          <code>max_{0 &lt;= i &lt;= n} p[i]+1</code> memory,
   *                          all entries -1
   */
  static void normalizePartition(final int[] p, final int[] tempColorMapStore) {
    int firstunusedcolor = 0;
    for (int i = 0; i < p.length; ++i) {
      final int vertexcolor = p[i];
      int newcolor = tempColorMapStore[vertexcolor];
      if (newcolor == -1) {
        tempColorMapStore[vertexcolor] = newcolor = firstunusedcolor++;
      }
      p[i] = newcolor;
    }
  }

  /**
   * Argument validation: Checks that the equivalence conforms to the following
   * equivalence format: <code>p[0] = 0</code> and
   * <code>equivalence[i] &lt;= max_{1&lt;=j&lt;=i-1} p[j] + 1</code>
   *
   * @param equivalence  the equivalence to check
   * @param argumentName the name of the argument
   */
  private static void checkEquivalenceFormat(ConstMapping.OfInt equivalence,
      final String argumentName) {
    int upperLimit = 0;
    for (final int color : equivalence) {
      if (color > upperLimit) {
        throw new IllegalArgumentException(argumentName);
      }
      upperLimit = Math.max(upperLimit, color + 1);
    }
  }

  /**
   * Internal data structure used to represent the current state within algorithms computing
   * equivalences
   *
   * @author muellerj
   */
  private static class EquivalenceAlgorithmState {

    private final int[] equivalence;
    private final int[] counts;
    // to ensure O(n) memory consumption, the algorithms
    // keep track of the colors that are
    // actually currently in use.
    // therefore, they "allocates" and "frees" colors like
    // memory would be allocated and freed in
    // programming languages without garbage collection.
    // To see how many colors an algorithm needs at most
    // look up the documentation of the algorithm.
    private final FixedSizeSlotAllocator colorAlloc;
    private final FixedCapacityIntegerList processedList;
    private final int[] colorsMap;

    /**
     * Constructs the state object.
     *
     * @param equivalence
     *          the current equivalence.
     * @param counts
     *          the current counts of colors in the equivalence
     * @param maxNumberColorsUsed
     *          the maximum number of colors the algorithms will need
     * @param nColorsAlreadyInUse
     *          the number of colors that are already in use in the equivalence. Assumption: Colors
     *          [0, nColorsAlreadyInUse) are the ones in use.
     */
    public EquivalenceAlgorithmState(final int[] equivalence, final int[] counts,
        final int maxNumberColorsUsed, final int nColorsAlreadyInUse) {
      this.equivalence = equivalence;
      this.counts = counts;
      colorAlloc = new FixedSizeSlotAllocator(maxNumberColorsUsed, nColorsAlreadyInUse);
      processedList = new FixedCapacityIntegerList(maxNumberColorsUsed);
      colorsMap = new int[maxNumberColorsUsed];
      Arrays.fill(colorsMap, -1);
    }

    /**
     * Returns the array representing the equivalence.
     *
     * @return the equivalence
     */
    public int[] getEquivalence() {
      return equivalence;
    }

    /**
     * Returns the counts for the different colors in the current equivalence.
     *
     * @return the array of counts of different colors
     */
    public int[] getCounts() {
      return counts;
    }

    /**
     * Allocates a currently unused color.
     *
     * @return the allocated color
     */
    public int allocateColor() {
      return colorAlloc.allocate();
    }

    /**
     * Releases a color that is currently unused.
     *
     * @param color
     *          the freed color
     */
    public void freeColor(final int color) {
      colorAlloc.free(color);
    }

    /**
     * Gets a list of colors that were processed in the current iteration.
     *
     * @return the list
     */
    public FixedCapacityIntegerList getProcessedList() {
      return processedList;
    }

    /**
     * Assigns a new color to a vertex and updates the counts.
     *
     * @param v
     *          the vertex to which a new color should be assigned
     * @param newcolor
     *          the new color of the vertex
     */
    public void reassignColor(final int v, final int newcolor) {
      final int oldcolor = equivalence[v];
      equivalence[v] = newcolor;
      --counts[oldcolor];
      ++counts[newcolor];
    }

    /**
     * Gets the current color of a vertex.
     *
     * @param v
     *          the vertex
     * @return the color
     */
    public int getColor(final int v) {
      return equivalence[v];
    }

    /**
     * gets the number of vertices that have this color.
     *
     * @param color
     *          the color
     * @return the count
     */
    public int getCount(final int color) {
      return counts[color];
    }

    /**
     * Gets the value this color was mapped to by the algorithm. -1 means unassigned
     *
     * @param color
     *          the color
     * @return the mapped value
     */
    public int getColorMappedToValue(final int color) {
      return colorsMap[color];
    }

    /**
     * Sets the value this color is mapped to by the algorithm. -1 means unassigned
     *
     * @param color
     *          the color
     * @param value
     *          the new mapped value
     */
    public void setColorMappedToValue(final int color, final int value) {
      colorsMap[color] = value;
    }

    /**
     * Gets the array representing the mapping from colors to values.
     *
     * @return the array
     */
    public int[] getColorsMap() {
      return colorsMap;
    }
  }

  /**
   * Computes the greatest common refinement of two partitions.
   * @param p1 the first partition.
   * @param p2 the second partition.
   * @return the greatest common refinement of the specified partitions.
   */
  public static Mapping.OfInt infimum(final ConstMapping.OfInt p1, final ConstMapping.OfInt p2) {

    // first, do some preprocessing on the equivalence
    // and order the vertices by their color on p2
    final int[] verticesOrderedByColor = PrimitiveCollections
        .countingSort(p2.intStream().toArray());
    // determine the maximum color

    int n = p2.size();

    int maxColor = 0;
    final int[] counts = new int[2 * n];
    for (final int c : p1) {
      maxColor = Math.max(c, maxColor);
    }
    EquivalenceAlgorithmState state = new EquivalenceAlgorithmState(p1.intStream().toArray(),
        counts, 2 * n, maxColor + 1);

    // iterate over all vertices and process them block-wise
    // according to their color in p2
    for (int iv = 0; iv < n; ++iv) {
      int u = verticesOrderedByColor[iv];
      {

        int oldcolor = state.getColor(u);

        // get the new color to assign to u
        int newcolor = state.getColorMappedToValue(oldcolor);
        // if no such color has been fixed yet,
        // allocate that color and mark old
        // color as processed
        if (newcolor == -1) {

          newcolor = state.allocateColor();

          // put oldcolor in the processed list
          state.getProcessedList().addInt(oldcolor);

          // save mapping from old color to new color
          state.setColorMappedToValue(oldcolor, newcolor);
        }
        // update the color of neighbor u
        state.reassignColor(u, newcolor);

      }

      // if processing a block is finished
      // clean up and garbage collect colors
      if (iv == n - 1 || p2.getInt(u) != p2.getInt(verticesOrderedByColor[iv + 1])) {
        for (int i = 0; i < state.getProcessedList().size(); ++i) {
          final int oldcolor = state.getProcessedList().getInt(i);
          // if color unused -> free it
          if (state.getCount(oldcolor) == 0) {
            state.freeColor(oldcolor);
          }
          // unset the color mapping
          state.setColorMappedToValue(oldcolor, -1);
        }
        // empty processed list
        state.getProcessedList().clear();
      }
    }

    // now make it so that the partition array
    // corresponds to the precondition laid out above
    // for partitions
    normalizePartition(state.getEquivalence(), state.getColorsMap());

    return Mappings.wrapModifiableInt(state.getEquivalence());
  }

  /**
   * Computes the least common coarsening of two partitions.
   * @param p1 the first partition.
   * @param p2 the second partition.
   * @return the least common coarsening of the specified partitions.
   */
  public static Mapping.OfInt supremum(final ConstMapping.OfInt p1, final ConstMapping.OfInt p2) {
    int[] pcurr = p1.intStream().toArray();

    int n = pcurr.length;

    int p2max = Integer.MIN_VALUE;// Collections.max(Arrays.asList(ArrayUtils.toObject(p2arr)));
    int p1max = Integer.MIN_VALUE;// Collections.max(Arrays.asList(ArrayUtils.toObject(pcurr)));
    for (int i = 0; i < p2.size(); ++i) {
      p2max = Math.max(p2max, p2.getInt(i));
      p1max = Math.max(p1max, pcurr[i]);
    }

    UnionFind uf = new UnionFind(p1max + 1);

    int[] maincolors = new int[p2max + 1];
    Arrays.fill(maincolors, -1);

    for (int i = 0; i < n; ++i) {
      int p2color = p2.getInt(i);
      int pcurrcolor = pcurr[i];
      int pcurrrepcolor = maincolors[p2color];
      if (pcurrrepcolor == -1) {
        maincolors[p2color] = pcurrcolor;
      } else if (pcurrrepcolor != pcurrcolor) {
        uf.union(pcurrrepcolor, pcurrcolor);
      }
    }
    for (int i = 0; i < n; ++i) {
      pcurr[i] = uf.find(pcurr[i]);
    }

    int[] colorstoreducedcolors = new int[p1max + 1];
    Arrays.fill(colorstoreducedcolors, -1);
    normalizePartition(pcurr, colorstoreducedcolors);

    return Mappings.wrapModifiableInt(pcurr);
  }

}
