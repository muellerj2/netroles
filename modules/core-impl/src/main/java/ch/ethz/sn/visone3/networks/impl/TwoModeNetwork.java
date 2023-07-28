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

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.IntPair;
import ch.ethz.sn.visone3.lang.LongMap;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveCollections;
import ch.ethz.sn.visone3.lang.PrimitiveContainers;
import ch.ethz.sn.visone3.lang.PrimitiveIterable;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.networks.AdjacencyListEntry;
import ch.ethz.sn.visone3.networks.DirectedGraph;
import ch.ethz.sn.visone3.networks.Edge;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.NetworkBuilder;
import ch.ethz.sn.visone3.networks.Relation;
import ch.ethz.sn.visone3.networks.Relationship;
import ch.ethz.sn.visone3.networks.ReorderableDirectedGraph;
import ch.ethz.sn.visone3.networks.ReorderableNetwork;
import ch.ethz.sn.visone3.networks.ReorderableUndirectedGraph;
import ch.ethz.sn.visone3.networks.UndirectedGraph;
import ch.ethz.sn.visone3.progress.ProgressProvider;
import ch.ethz.sn.visone3.progress.ProgressSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.IntBinaryOperator;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

public class TwoModeNetwork
    implements Network, Relation, UndirectedGraph, Serializable {
  private static final Logger LOG = LoggerFactory.getLogger(TwoModeNetwork.class);
  private static final long serialVersionUID = 1931123468620173444L;
  private final int numNodes;
  protected final int[] accDegree;
  protected int[] neighbors;
  protected int[] edgeIds;

  private TwoModeNetwork(final int numNodes, final int[] accDegree, final int[] neighbors,
      final int[] edgeIds) {
    this.numNodes = numNodes;
    this.accDegree = accDegree;
    this.neighbors = neighbors;
    this.edgeIds = edgeIds;
  }

  private void assertLeft(final int index) {
    if (index < 0 || index >= countLeftDomain()) {
      throw new IllegalArgumentException("not in left domain: " + index);
    }
  }

  private void assertRight(final int index) {
    if (index < countLeftDomain() || index >= countUnionDomain()) {
      throw new IllegalArgumentException("not in right domain: " + index);
    }
  }

  protected Relationship itrFrom(final int edge, final int self, final int opposite) {
    // TODO test left and right from
    return new RelationshipImpl(edge, self, opposite, opposite - countLeftDomain());
  }

  private Relationship itrTo(final int edge, final int self, final int opposite) {
    return new RelationshipImpl(edge, opposite, self, self - countLeftDomain());
  }

  @Override
  public int countVertices() {
    return accDegree.length - 1;
  }

  @Override
  public IntStream getNeighborStream(final int vertex) {
    return Arrays.stream(neighbors, accDegree[vertex], accDegree[vertex + 1]);
  }

  @Override
  public int countEdges() {
    return countRelationships();
  }

  @Override
  public int countLoops() {
    return 0;
  }

  @Override
  public Iterable<Edge> getEdges(final int vertex) {
    return new NeighborIterable<>(vertex, accDegree[vertex], accDegree[vertex + 1],
        UndirectedEdgeImpl::new);
  }

  @Override
  public Iterable<Edge> getEdges() {
    return AllEdgeIterator::new;
  }

  @Override
  public int getDegree(final int vertex) {
    return accDegree[vertex + 1] - accDegree[vertex];
  }

  @Override
  public boolean isDirected() {
    return false;
  }

  @Override
  public DirectedGraph asDirectedGraph() {
    throw new UnsupportedOperationException();
  }

  @Override
  public UndirectedGraph asUndirectedGraph() {
    return this;
  }

  @Override
  public Relation asRelation() {
    return this;
  }

  @Override
  public NetworkBuilder builder() {
    return new Builder();
  }

  @Override
  public boolean equals(final Object rhs) {
    return (rhs instanceof Network) && structureEquals((Network) rhs);
  }

  @Override
  public int countDyadicIndices() {
    return countEdges();
  }

  @Override
  public int countRelationships() {
    return neighbors.length / 2;
  }

  @Override
  public Iterable<Relationship> getRelationshipsFrom(final int left) {
    assertLeft(left);
    return new NeighborIterable<>(left, accDegree[left], accDegree[left + 1], this::itrFrom);
  }

  @Override
  public Iterable<Relationship> getRelationshipsTo(final int right) {
    assertRight(right);
    return new NeighborIterable<>(right, accDegree[right], accDegree[right + 1], this::itrTo);
  }

  @Override
  public Iterable<Relationship> getRelationships(final int index) {
    if (index < countLeftDomain()) {
      return getRelationshipsFrom(index);
    }
    return getRelationshipsTo(index);
  }

  @Override
  public String toString() {
    return AsciiDumper.singleLine((Relation) this);
  }

  @Override
  public PrimitiveIterable.OfInt getLeftDomain() {
    return Mappings.intRange(0, countLeftDomain());
  }

  @Override
  public int countLeftDomain() {
    return numNodes;
  }

  @Override
  public PrimitiveIterable.OfInt getRightDomain() {
    return Mappings.intRange(countLeftDomain(), countUnionDomain());
  }

  @Override
  public int countRightDomain() {
    return countUnionDomain() - numNodes;
  }

  @Override
  public PrimitiveIterable.OfInt getUnionDomain() {
    return Mappings.intRange(0, countUnionDomain());
  }

  @Override
  public int countUnionDomain() {
    return accDegree.length - 1;
  }

  @Override
  public boolean isTwoMode() {
    return true;
  }

  @Override
  public int countMonadicIndices() {
    return accDegree.length - 1;
  }

  @Override
  public int countRelationshipsFrom(final int left) {
    return accDegree[left + 1] - accDegree[left];
  }

  @Override
  public int countRelationshipsTo(final int right) {
    return accDegree[right + 1] - accDegree[right];
  }

  @Override
  public IntStream getPartnersStream(final int elemnt) {
    return Arrays.stream(neighbors, accDegree[elemnt], accDegree[elemnt + 1]);
  }

  @Override
  public <T> T[][] asMatrix(T fill, T diagonal, ConstMapping<T> mapping) {
    return MatrixConstruction.toMatrix(this, fill, diagonal, mapping);
  }

  @Override
  public ReorderableNetwork reorderable() {
    return new Reorderable(numNodes, accDegree, neighbors, edgeIds, false);
  }

  @Override
  public boolean structureEquals(final Network network) {
    if (!(network instanceof TwoModeNetwork)) {
      return false;
    }
    final TwoModeNetwork other = (TwoModeNetwork) network;
    return numNodes == other.numNodes //
        && Arrays.equals(accDegree, other.accDegree) //
        && Arrays.equals(neighbors, other.neighbors) //
        && Arrays.equals(edgeIds, other.edgeIds);
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(numNodes) //
        + Arrays.hashCode(accDegree) //
        + Arrays.hashCode(neighbors) //
        + Arrays.hashCode(edgeIds);
  }

  // @Override
  // public IntStream getRelationsWithFrom(final int left) {
  // if (left >= countLeftDomain()) {
  // throw new IllegalArgumentException("Node is not part of the left domain");
  // }
  // return Arrays.stream(neighbors, accDegree[left], accDegree[left + 1]);
  // }
  //
  // @Override
  // public IntStream getRelationsWithTo(final int right) {
  // if (right < countLeftDomain()) {
  // throw new IllegalArgumentException("Node is not part of the right domain");
  // }
  // return Arrays.stream(neighbors, accDegree[right], accDegree[right + 1]);
  // }
  //
  // @Override
  // public IntStream getRelationsWithTo0(final int right) {
  // if (right >= countRightDomain()) {
  // throw new IllegalArgumentException("Node is not part of the right domain");
  // }
  // return Arrays.stream(neighbors, accDegree[right + countLeftDomain()], accDegree[right +
  // countLeftDomain() + 1]);
  // }

  /**
   * Convenience interface to reduce number of iterators.
   */
  @FunctionalInterface
  private interface ItrOutput<T> {
    T edge(int edge, int self, int opposite);
  }

  @FunctionalInterface
  private interface IntItrOutput {
    int edge(int edge, int self, int opposite);
  }

  public static class Builder implements NetworkBuilder {
    final LongMap<Integer> hash = PrimitiveContainers.longTreeMap();
    final PrimitiveList.OfInt degrees = Mappings.newIntList(Magic.CAP_NODES); // n
    final PrimitiveList.OfInt adegrees = Mappings.newIntList(Magic.CAP_NODES); // a
    final PrimitiveList.OfInt sortKey = Mappings.newIntList(Magic.CAP_EDGES); // 2m
    final PrimitiveList.OfInt neighbors = Mappings.newIntList(Magic.CAP_EDGES); // 2m
    final PrimitiveList.OfInt edgeIds = Mappings.newIntList(Magic.CAP_EDGES); // 2m
    int hits;
    int edgeCount;

    @Override
    public void ensureNode(final int node) {
      if (degrees.size() <= node) {
        degrees.setSize(0, node + 1);
      }
    }

    @Override
    public void ensureAffiliation(final int node) {
      if (adegrees.size() <= node) {
        adegrees.setSize(0, node + 1);
      }
    }

    @Override
    public int addEdge(final int source, final int target) {
      final long set = IntPair.set(source, -(target + 1));
      final int existing = hash.getOrDefault(set, -1);
      if (existing >= 0) {
        if (hits < 10) {
          LOG.warn("edge already present ({},{}) -> {}", source, target, existing);
        }
        ++hits;
        return -(existing + 1);
      }
      hash.put(set, edgeCount);

      // LOG.info("n{} -- a{}", source, target);
      ensureNode(source);
      ensureAffiliation(target);

      // add edges
      degrees.arrayQuick()[source]++;
      sortKey.addInt(source);
      neighbors.addInt(-(target + 1));
      edgeIds.addInt(edgeCount);
      // add
      adegrees.arrayQuick()[target]++;
      sortKey.addInt(-(target + 1));
      neighbors.addInt(source);
      edgeIds.addInt(edgeCount);

      return edgeCount++;
    }

    @Override
    public boolean acceptsDirected() {
      return false;
    }

    @Override
    public boolean acceptsTwoModes() {
      return true;
    }

    @Override
    public Network build() {
      if (hits > 0) {
        LOG.warn("{} duplicated edges", hits);
      }
      // adjust negative node ids
      final int numNodes = degrees.size();
      for (int i = 0; i < sortKey.size(); i++) {
        int value = sortKey.getInt(i);
        if (value < 0) {
          value = (-value) - 1;
          sortKey.setInt(i, numNodes + value);
        }
        value = neighbors.getInt(i);
        if (value < 0) {
          value = (-value) - 1;
          neighbors.setInt(i, numNodes + value);
        }
      }
      for (final int i : adegrees) {
        degrees.addInt(i);
      }

      // build incidence
      final int[] pi = PrimitiveCollections.countingSort(sortKey.array(), degrees.size());
      final int[] ns = PrimitiveCollections.permute(neighbors.array(), pi);
      final int[] es = PrimitiveCollections.permute(edgeIds.array(), pi);
      final int[] ad = new int[degrees.size() + 1];
      for (int i = 0; i < ad.length - 1; i++) {
        ad[i + 1] = ad[i] + degrees.getInt(i);
      }

      for (int i = 0; i < ad[numNodes]; i++) {
        if (ns[i] < numNodes) {
          throw new IllegalArgumentException("node linking node");
        }
      }
      for (int i = ad[numNodes]; i < ad[ad.length - 1]; i++) {
        if (ns[i] >= numNodes) {
          throw new IllegalArgumentException("affiliation linking affiliation");
        }
      }

      return new TwoModeNetwork(numNodes, ad, ns, es);
    }
  }

  private class NeighborIterable<T> implements Iterable<T> {
    final int vertex;
    final int begin;
    final int end;
    final ItrOutput<T> fac;

    NeighborIterable(final int vertex, final int begin, final int end, final ItrOutput<T> fac) {
      this.vertex = vertex;
      this.fac = fac;
      this.begin = begin;
      this.end = end;
    }

    @Override
    public Iterator<T> iterator() {
      return new Itr();
    }

    private class Itr implements Iterator<T> {
      int it = begin;

      @Override
      public boolean hasNext() {
        return it < end;
      }

      @Override
      public T next() {
        if (hasNext()) {
          final int edge = edgeIds[it];
          final T r = fac.edge(edge, vertex, neighbors[it]);
          it++;
          return r;
        }
        throw new NoSuchElementException();
      }
    }
  }

  private class AllEdgeIterator implements Iterator<Edge> {
    int index = -1;
    int source = 0;
    int target = 0;

    AllEdgeIterator() {
      loadNext();
    }

    void loadNext() {
      do {
        ++index;
        if (index < neighbors.length) {
          while (index >= accDegree[source + 1]) {
            ++source;
          }
          target = neighbors[index];
        }
      } while (index < neighbors.length && source > target);
    }

    @Override
    public boolean hasNext() {
      return index < neighbors.length;
    }

    @Override
    public Edge next() {
      if (index >= neighbors.length) {
        throw new NoSuchElementException();
      }
      final int id = edgeIds[index];
      final Edge edge = new UndirectedEdgeImpl(id, source, target);
      loadNext();
      return edge;
    }
  }

  private static final class AdjacencyListEntryImpl implements AdjacencyListEntry {
    private final int self;
    private final int opposite;
    private final int index;

    public AdjacencyListEntryImpl(int index, int self, int opposite) {
      this.index = index;
      this.self = self;
      this.opposite = opposite;
    }

    public int getListSource() {
      return self;
    }

    public Edge getEdge() {
      return new UndirectedEdgeImpl(index, self, opposite);
    }
  }

  private static class Reorderable extends TwoModeNetwork
      implements ReorderableNetwork, ReorderableUndirectedGraph {

    private static final long serialVersionUID = -4952744692742304194L;
    private boolean copyOnWrite = false;

    private Reorderable(final int numNodes, final int[] accDegree, final int[] neighbors,
        final int[] edgeIds, boolean copyOnWrite) {
      super(numNodes, accDegree, neighbors, edgeIds);
      this.copyOnWrite = copyOnWrite;
    }

    @Override
    public void swapNeighbors(int node, int n1, int n2) {
      if (!copyOnWrite) {
        neighbors = Arrays.copyOf(neighbors, neighbors.length);
        edgeIds = Arrays.copyOf(edgeIds, edgeIds.length);
        copyOnWrite = true;
      }
      final int offset = accDegree[node];
      int tmp = neighbors[offset + n1];
      neighbors[offset + n1] = neighbors[offset + n2];
      neighbors[offset + n2] = tmp;
      tmp = edgeIds[offset + n1];
      edgeIds[offset + n1] = edgeIds[offset + n2];
      edgeIds[offset + n2] = tmp;
    }

    @Override
    public void sortNeighborhoods(final IntBinaryOperator nodeComparator) {
      final int[] tmpNeighbors = new int[neighbors.length];
      final int[] tmpEdgeIds = new int[edgeIds.length];
      final int n = countVertices();
      try (ProgressSource p = ProgressProvider.getMonitor().newSource()) {
        for (int i = 0; i < n; i++) {
          p.updateProgress(i, n, "sort n " + i);
          final int begin = accDegree[i];
          final int end = accDegree[i + 1];
          final int[] pi = IntStream.range(begin, end).boxed()
              .sorted((lhs, rhs) -> nodeComparator.applyAsInt(neighbors[lhs], neighbors[rhs]))
              .mapToInt(Integer::intValue).toArray();
          for (int k = begin; k < end; k++) {
            tmpNeighbors[pi[k - begin]] = neighbors[k];
            tmpEdgeIds[pi[k - begin]] = edgeIds[k];
          }
        }
      }
      neighbors = tmpNeighbors;
      edgeIds = tmpEdgeIds;
      copyOnWrite = true;
    }

    @Override
    public void sortNeighborhoods(Comparator<AdjacencyListEntry> comparator) {
      final int[] tmpNeighbors = new int[neighbors.length];
      final int[] tmpEdgeIds = new int[edgeIds.length];
      final int n = countVertices();
      for (int i = 0; i < n; i++) {
        final int begin = accDegree[i];
        final int end = accDegree[i + 1];
        final int self = i;
        final int[] pi = IntStream.range(begin, end).boxed()
            .sorted((lhs, rhs) -> comparator.compare(
                new AdjacencyListEntryImpl(edgeIds[lhs], self, neighbors[lhs]),
                new AdjacencyListEntryImpl(edgeIds[rhs], self, neighbors[rhs])))
            .mapToInt(Integer::intValue).toArray();
        for (int k = begin; k < end; k++) {
          tmpNeighbors[pi[k - begin]] = neighbors[k];
          tmpEdgeIds[pi[k - begin]] = edgeIds[k];
        }
      }
      neighbors = tmpNeighbors;
      edgeIds = tmpEdgeIds;
      copyOnWrite = true;
    }

    @Override
    public void sortNeighborhoods(ToIntFunction<AdjacencyListEntry> valueProducer,
        int universeSize) {
      final int[] values = new int[neighbors.length];
      final int n = countVertices();
      for (int i = 0; i < n; ++i) {
        final int begin = accDegree[i];
        final int end = accDegree[i + 1];
        for (int j = begin; j < end; ++j) {
          values[j] = valueProducer
              .applyAsInt(new AdjacencyListEntryImpl(edgeIds[j], i, neighbors[j]));
        }
      }

      // TODO: Use counting sort if universeSize in O(n+m)
      final int[] tmpNeighbors = new int[neighbors.length];
      final int[] tmpEdgeIds = new int[edgeIds.length];
      for (int i = 0; i < n; ++i) {
        final int begin = accDegree[i];
        final int end = accDegree[i + 1];
        final int[] pi = IntStream.range(begin, end).boxed()
            .sorted((lhs, rhs) -> Integer.compare(values[lhs], values[rhs]))
            .mapToInt(Integer::intValue).toArray();
        for (int k = begin; k < end; k++) {
          tmpNeighbors[pi[k - begin]] = neighbors[k];
          tmpEdgeIds[pi[k - begin]] = edgeIds[k];
        }
      }
      neighbors = tmpNeighbors;
      edgeIds = tmpEdgeIds;
      copyOnWrite = true;
    }

    @Override
    public ReorderableNetwork reorderable() {
      return new Reorderable(countLeftDomain(), accDegree,
          Arrays.copyOf(neighbors, neighbors.length),
          Arrays.copyOf(edgeIds, edgeIds.length), true);
    }

    @Override
    public ReorderableDirectedGraph asDirectedGraph() {
      return (ReorderableDirectedGraph) super.asDirectedGraph();
    }

    @Override
    public ReorderableUndirectedGraph asUndirectedGraph() {
      return this;
    }
  }

}
