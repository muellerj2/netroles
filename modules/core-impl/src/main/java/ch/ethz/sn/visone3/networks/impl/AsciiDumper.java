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

package ch.ethz.sn.visone3.networks.impl;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.networks.DirectedGraph;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.Relation;
import ch.ethz.sn.visone3.networks.Relationship;
import ch.ethz.sn.visone3.networks.UndirectedGraph;

import java.util.Iterator;
import java.util.function.Function;

public final class AsciiDumper {
  private static final int WIDTH_HINT = 80;
  private static final int CELL_WIDTH = 4;
  private static final String CELL_FORMAT = "%" + CELL_WIDTH + "s";

  private AsciiDumper() {
  }

  /**
   * Produces a multi-line dump of a network.
   * 
   * @param net
   *          the network.
   * @return the dump string.
   */
  public static String multiLine(final Network net) {
    final Relation rel = net.asRelation();
    final StringBuilder sb = new StringBuilder();
    sb.append("++++\n");
    sb.append("Implementation=").append(rel.getClass().getSimpleName()).append('\n');
    sb.append("Relation=").append(singleLine(net.asRelation())).append('\n');
    if (net.isDirected()) {
      sb.append("DirectedGraph=").append(singleLine(net.asDirectedGraph())).append('\n');
    } else {
      sb.append("UndirectedGraph=").append(singleLine(net.asUndirectedGraph())).append('\n');
    }
    if (rel.countRightDomain() <= WIDTH_HINT / CELL_WIDTH
        && rel.countLeftDomain() <= WIDTH_HINT / CELL_WIDTH) {
      sb.append(matrix(rel, r -> String.valueOf(r.getIndex())));
    }
    sb.append("++++\n");
    return sb.toString();
  }

  /**
   * Produces a dump of a relation in matrix format.
   * 
   * @param rel
   *          the relation.
   * @param toString
   *          the converter of relationships to strings.
   * @return the dump string.
   */
  @SuppressWarnings("unused")
  public static String matrix(final Relation rel, final Function<Relationship, String> toString) {
    final StringBuilder sb = new StringBuilder();
    // top left cell
    sb.append(String.format(CELL_FORMAT, rel.countLeftDomain() + "x" + rel.countRightDomain()));
    sb.append(" |");
    // top row
    for (final int i : rel.getRightDomain()) {
      sb.append(String.format(CELL_FORMAT, i));
    }
    // separator line
    final StringBuilder line = new StringBuilder();
    for (int i = 0; i < CELL_WIDTH; i++) {
      line.append("-");
    }
    sb.append('\n').append(line).append("-+");
    for (final int ignored : rel.getRightDomain()) {
      sb.append(line);
    }
    for (final int r : rel.getLeftDomain()) {
      // first column
      sb.append('\n').append(String.format(CELL_FORMAT, r)).append(" |");
      // cells
      for (final int c : rel.getRightDomain()) {
        final Relationship v = rel.getRelationship(r, c);
        sb.append(String.format(CELL_FORMAT, v != null ? toString.apply(v) : "."));
      }
    }
    return sb.append('\n').toString();
  }

  /**
   * Produces a single-line dump of a relation.
   * 
   * @param rel
   *          the relation.
   * @return the dump string
   */
  public static String singleLine(final Relation rel) {
    return singleLine(rel, WIDTH_HINT);
  }

  /**
   * Produces a single-line dump of a relation with specified maximum length.
   * 
   * @param rel
   *          the relation.
   * @param widthHint
   *          the maximum length.
   * @return the dump string
   */
  public static String singleLine(final Relation rel, final int widthHint) {
    final StringBuilder sb = new StringBuilder();
    sb.append(String.format("{Relation,I=%s,D=%dx%d,|R|=%d,[", rel.getClass().getSimpleName(),
        rel.countLeftDomain(), rel.countRightDomain(), rel.countRelationships()));
    limited(sb, rel.getRelationships().iterator(), widthHint);
    sb.append("]}");
    return sb.toString();
  }

  /**
   * Produces a single-line dump of an undirected graph.
   * 
   * @param graph
   *          the undirected graph.
   * @return the dump string
   */
  public static String singleLine(final UndirectedGraph graph) {
    return singleLine(graph, WIDTH_HINT);
  }

  /**
   * Produces a single-line dump of an undirected graph with specified maximum length.
   * 
   * @param graph
   *          the undirected graph.
   * @param widthHint
   *          the maximum length.
   * @return the dump string
   */
  public static String singleLine(final UndirectedGraph graph, final int widthHint) {
    final StringBuilder sb = new StringBuilder();
    sb.append(String.format("{UndirectedGraph,I=%s,n=%d,|E|=%d,[", graph.getClass().getSimpleName(),
        graph.countVertices(), graph.countEdges()));
    limited(sb, graph.getEdges().iterator(), widthHint);
    sb.append("]}");
    return sb.toString();
  }

  /**
   * Produces a single-line dump of a directed graph.
   * 
   * @param graph
   *          the directed graph.
   * @return the dump string
   */
  public static String singleLine(final DirectedGraph graph) {
    return singleLine(graph, WIDTH_HINT);
  }

  /**
   * Produces a single-line dump of a directed graph with specified maximum length.
   * 
   * @param graph
   *          the directed graph.
   * @param widthHint
   *          the maximum length.
   * @return the dump string
   */
  public static String singleLine(final DirectedGraph graph, final int widthHint) {
    final StringBuilder sb = new StringBuilder();
    sb.append(String.format("{DirectedGraph,I=%s,n=%d,|E|=%d,[", graph.getClass().getSimpleName(),
        graph.countVertices(), graph.countEdges()));
    limited(sb, graph.getEdges().iterator(), widthHint);
    sb.append("]}");
    return sb.toString();
  }

  /**
   * Produces a single-line dump of a mapping.
   * 
   * @param map
   *          the mapping.
   * @return the dump string
   */
  public static <T> String singleLine(final ConstMapping<T> map) {
    return singleLine(map, WIDTH_HINT);
  }

  /**
   * Produces a single-line dump of a mapping with specified maximum length.
   * 
   * @param map
   *          the mapping.
   * @param widthHint
   *          the maximum length.
   * @return the dump string
   */
  public static <T> String singleLine(final ConstMapping<T> map, final int widthHint) {
    final StringBuilder sb = new StringBuilder();
    sb.append(String.format("{n=%d,[", map.size()));
    limited(sb, map.iterator(), widthHint);
    sb.append("]}");
    return sb.toString();
  }

  /**
   * Append till {@code sb} is over {@code widthHint} characters long.
   */
  private static <T> void limited(final StringBuilder sb, final Iterator<T> itr,
      final int widthHint) {
    final String fmt = "(... %3s omitted)";
    // append as long as it fits
    if (itr.hasNext()) {
      final String next = saveToString(itr.next());
      if (widthHint < 0 || sb.length() + next.length() < widthHint) {
        sb.append(next);
      }
    }

    // append as long as it fits
    while (itr.hasNext()) {
      final String next = saveToString(itr.next());
      if (widthHint >= 0 && sb.length() + next.length() >= widthHint) {
        break;
      }
      sb.append(',').append(next);
    }

    // count rest (but limit to not walk everything)
    int count = 0;
    final int max = 99;
    while (itr.hasNext() && count < max) {
      itr.next();
      count++;
    }
    if (count >= max) {
      sb.append(String.format(fmt, max + "+"));
    } else if (count > 0) {
      sb.append(String.format(fmt, count));
    }
  }

  private static <T> String saveToString(final T obj) {
    if (obj instanceof Double) {
      return String.format("%10.2e", (Double) obj);
    }
    return obj.toString().replaceAll("\n", "#");
  }
}
