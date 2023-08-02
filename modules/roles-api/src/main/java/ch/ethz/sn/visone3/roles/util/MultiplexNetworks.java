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
package ch.ethz.sn.visone3.roles.util;

import ch.ethz.sn.visone3.lang.IntPair;
import ch.ethz.sn.visone3.lang.LongMap;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.Pair;
import ch.ethz.sn.visone3.lang.PrimitiveContainers;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.networks.Direction;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.NetworkBuilder;
import ch.ethz.sn.visone3.networks.NetworkProvider;
import ch.ethz.sn.visone3.networks.Relation;
import ch.ethz.sn.visone3.networks.Relationship;
import ch.ethz.sn.visone3.progress.ProgressProvider;
import ch.ethz.sn.visone3.progress.ProgressSource;

import java.util.function.IntBinaryOperator;

/**
 * Constructs a composite network relation from several given relations in a multiplex network.
 *
 */
public class MultiplexNetworks {

  private MultiplexNetworks() {
  }

  private interface DirectedView<T> {
    public Iterable<T> getForwardLinks(int i);

    public int getLinkTarget(T link);

    public int getLinkIndex(T link);

    public int countNodes();

    public int countLinks();
  }

  private static DirectedView<Relationship> viewFromNetwork(Network network, Direction dir) {
    Relation rel = network.asRelation();
    if (dir == Direction.INCOMING) {
      return new DirectedView<Relationship>() {
        @Override
        public Iterable<Relationship> getForwardLinks(int i) {
          return rel.getRelationshipsTo(i);
        }

        @Override
        public int getLinkTarget(Relationship link) {
          return link.getLeft();
        }

        @Override
        public int getLinkIndex(Relationship link) {
          return link.getIndex();
        }

        @Override
        public int countLinks() {
          return rel.countRelationships();
        }

        @Override
        public int countNodes() {
          return rel.countUnionDomain();
        }
      };
    } else {
      return new DirectedView<Relationship>() {

        @Override
        public Iterable<Relationship> getForwardLinks(int i) {
          return rel.getRelationshipsFrom(i);
        }

        @Override
        public int getLinkTarget(Relationship link) {
          return link.getRight();
        }

        @Override
        public int getLinkIndex(Relationship link) {
          return link.getIndex();
        }

        @Override
        public int countLinks() {
          return rel.countRelationships();
        }

        @Override
        public int countNodes() {
          return rel.countUnionDomain();
        }
      };
    }
  }

  /**
   * Create the directed composite network relation from a provided list of relations of a multiplex
   * network.
   * 
   * @param networks
   *          the provided list of networks plus the direction in which the links are to be
   *          considered.
   * @param compositeNodeIndexMap
   *          maps a network relation's sequence number in the list and the node index in this
   *          network relation to the index of the node in the composite network relation to
   *          produce.
   * @return the composite network relation plus for each provided network relation a mapping that
   *         maps the link indices in the composite network to the corresponding link indices in the
   *         respective provided network relations.
   */
  public static Pair<Network, Mapping.OfInt[]> multiplexDirected(
      Iterable<Pair<Network, Direction>> networks, IntBinaryOperator compositeNodeIndexMap) {

    final NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.DIRECTED);
    final LongMap<Integer> nodes2edgeid = PrimitiveContainers.longTreeMap();
    int maxEdgeId = -1;
    try (ProgressSource p = ProgressProvider.getMonitor().newSource()) {
      int limit = 0;
      int networksCount = 0;
      for (Pair<Network, Direction> net : networks) {
        limit += viewFromNetwork(net.getFirst(), net.getSecond()).countLinks();
        ++networksCount;
      }
      p.updateProgress(0, 2 * limit, "multiplex-dir");
      int k = 0;
      for (Pair<Network, Direction> net : networks) {
        DirectedView<Relationship> view = viewFromNetwork(net.getFirst(), net.getSecond());
        int n = view.countNodes();
        if (n > 0) {
          builder.ensureNode(n - 1);
        }
        for (int i = 0; i < n; ++i) {
          int left = compositeNodeIndexMap.applyAsInt(k, i);
          builder.ensureNode(left);
          for (final Relationship r : view.getForwardLinks(i)) {
            int right = compositeNodeIndexMap.applyAsInt(k, view.getLinkTarget(r));
            final long nodepair = IntPair.tuple(left, right);
            Integer edgeid = nodes2edgeid.get(nodepair);

            if (edgeid == null) {
              int id = builder.addEdge(left, right);
              nodes2edgeid.put(nodepair, id);
              maxEdgeId = Math.max(id, maxEdgeId);
            }
            p.increaseProgress();
          }
        }
        ++k;
      }

      PrimitiveList.OfInt[] oldedgeidsmap = new PrimitiveList.OfInt[networksCount];
      k = 0;
      for (Pair<Network, Direction> net : networks) {
        oldedgeidsmap[k] = Mappings.newIntList(-1, maxEdgeId + 1);

        DirectedView<Relationship> view = viewFromNetwork(net.getFirst(), net.getSecond());
        int n = view.countNodes();
        for (int i = 0; i < n; ++i) {
          int left = compositeNodeIndexMap.applyAsInt(k, i);
          for (final Relationship r : view.getForwardLinks(i)) {
            int right = compositeNodeIndexMap.applyAsInt(k, view.getLinkTarget(r));
            final long nodepair = IntPair.tuple(left, right);
            Integer edgeid = nodes2edgeid.get(nodepair);
            oldedgeidsmap[k].setInt(edgeid, view.getLinkIndex(r));
            p.increaseProgress();
          }
        }
        ++k;
      }
      return new Pair<>(builder.build(), oldedgeidsmap);
    }
  }

  /**
   * Create the undirected multiplex network from a sequence of provided networks.
   * 
   * @param networks
   *          the provided networks plus the link direction to consider
   * @param translateToNew
   *          is provided a network's sequence number and the node index and translates it to the
   *          multiplex network's node index
   * @return the multiplex network plus for each provided network a mapping that maps an int pair of
   *         multiplex network's link indices to the corresponding link indices in the provided
   *         network; for a link (a, b) in the undirected multiplex network with a <= b, the first
   *         element of the int pair refers to the original index of the link (a, b) and the second
   *         element to the original index of the link (b, a).
   */
  public static Pair<Network, Mapping.OfLong[]> multiplexUndirected(
      Iterable<Pair<Network, Direction>> networks, IntBinaryOperator translateToNew) {

    final NetworkBuilder builder = NetworkProvider.getInstance().builder(DyadType.UNDIRECTED);
    final LongMap<Integer> nodes2edgeid = PrimitiveContainers.longTreeMap();
    int maxEdgeId = -1;
    try (ProgressSource p = ProgressProvider.getMonitor().newSource()) {
      int limit = 0;
      int networksCount = 0;
      for (Pair<Network, Direction> net : networks) {
        limit += viewFromNetwork(net.getFirst(), net.getSecond()).countLinks();
        ++networksCount;
      }
      p.updateProgress(0, 2 * limit, "multiplex-undir");
      int k = 0;
      for (Pair<Network, Direction> net : networks) {
        DirectedView<Relationship> view = viewFromNetwork(net.getFirst(), net.getSecond());
        int n = view.countNodes();
        if (n > 0) {
          builder.ensureNode(n - 1);
        }
        for (int i = 0; i < n; ++i) {
          int left = translateToNew.applyAsInt(k, i);
          for (final Relationship r : view.getForwardLinks(i)) {
            int right = translateToNew.applyAsInt(k, view.getLinkTarget(r));
            final long nodepair = IntPair.set(left, right);
            Integer edgeid = nodes2edgeid.get(nodepair);

            if (edgeid == null) {
              int id = builder.addEdge(left, right);
              nodes2edgeid.put(nodepair, id);
              maxEdgeId = Math.max(id, maxEdgeId);
            }
            p.increaseProgress();
          }
        }
        ++k;
      }

      PrimitiveList.OfLong[] oldedgeidsmap = new PrimitiveList.OfLong[networksCount];
      k = 0;
      for (Pair<Network, Direction> net : networks) {
        oldedgeidsmap[k] = Mappings.newLongList(IntPair.tuple(-1, -1), maxEdgeId + 1);

        DirectedView<Relationship> view = viewFromNetwork(net.getFirst(), net.getSecond());
        int n = view.countNodes();
        for (int i = 0; i < n; ++i) {
          int left = translateToNew.applyAsInt(k, i);
          for (final Relationship r : view.getForwardLinks(i)) {
            int right = translateToNew.applyAsInt(k, view.getLinkTarget(r));
            final long nodepair = IntPair.set(left, right);
            Integer edgeid = nodes2edgeid.get(nodepair);

            long oldids = oldedgeidsmap[k].getLong(edgeid);

            int first = IntPair.first(oldids);
            int second = IntPair.second(oldids);

            int index = view.getLinkIndex(r);
            if (left <= right) {
              first = index;
            } else {
              second = index;
            }

            oldedgeidsmap[k].setLong(edgeid, IntPair.tuple(first, second));
            p.increaseProgress();
          }
        }
        ++k;
      }
      return new Pair<>(builder.build(), oldedgeidsmap);
    }
  }
}
