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

package ch.ethz.sn.visone3.io.test;

import static ch.ethz.sn.visone3.test.NetworkAsserts.assertNetwork;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.ethz.sn.visone3.io.Source;
import ch.ethz.sn.visone3.io.SourceFormat;
import ch.ethz.sn.visone3.io.graphml.GraphmlSource;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.impl.AsciiDumper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.GZIPInputStream;

public class GraphmlSourceTest {
  private final String[] files = new String[] {
    "ch/ethz/sn/visone3/io/network.visone2.graphmlz",
    "ch/ethz/sn/visone3/io/network.yEd.graphmlz",
  };
  private final String[] files2 = new String[] {
    "ch/ethz/sn/visone3/io/nested.yEd.graphml",
    "ch/ethz/sn/visone3/io/mixedTests.graphml",
    "ch/ethz/sn/visone3/io/keysBehindData.yEd.graphml",
  };

  private final Integer z = null;
  private final Integer[][] adj = new Integer[][] {
    {z, 1, z, z, z}, //
    {1, z, z, z, 1}, //
    {z, z, z, z, z}, //
    {z, 1, z, 1, z}, //
    {z, z, 1, 1, z}
  };
  private final Integer[][] adj2 = new Integer[][] {
    {1, 1, 1, 1}, //
    {2, z, 2, z}, //
    {1, 1, z, 1}, //
    {1, z, 2, z}
  };

  @Test
  public void testReadOneMode() throws IOException {
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    for (final String file : files) {
      try (GraphmlSource conf = new GraphmlSource(new GZIPInputStream(cl.getResourceAsStream(file)))) {
        final SourceFormat source = conf.parse();
        final Network incidence = source.incidence();
        System.out.println(AsciiDumper.multiLine(incidence));
        assertNetwork(adj, incidence, Mappings.newIntList(1, incidence.countDyadicIndices()));
      }
    }
  }

  @Test
  public void testNested1() throws IOException {
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    //test nested nodes
    final String graph = files2[0];
    try (InputStream in = cl.getResourceAsStream(graph);
        Source<?> source = new GraphmlSource(in)) {
      final SourceFormat sourceFormat = source.parse();
      final Mapping<? extends Integer> nested = Mappings.cast(Integer.class,
          sourceFormat.monadic().get(GraphmlSource.NESTED));
      System.out.println("nested = " + nested);
      // test nodes
      assertEquals(Arrays.asList(-1, 0, 1, 1, 0, 0, -1, -1),
        StreamSupport.stream(nested.spliterator(), false).collect(Collectors.toList()));
    }
  }

  @Test
  public void testNested2() throws IOException {
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    //test multi edges, directed and undirected
    final String graph2 = files2[1];
    try (final Source<?> config = new GraphmlSource(cl.getResourceAsStream(graph2))) {
      final SourceFormat source = config.parse();
      final Network incidence = source.incidence();
      final Mapping<Integer> multiplicity = Mappings.castExact(Integer.class,
          source.dyadic().get(GraphmlSource.MULTIPLICITY));

      System.out.println("monad --");
      for (Map.Entry<String, Mapping<?>> e : source.monadic().entrySet()) {
        System.out.println(e);
      }
      System.out.println("dyad --");
      for (Map.Entry<String, Mapping<?>> e : source.dyadic().entrySet()) {
        System.out.println(e);
      }

      // TODO test misspecification? multiplicity of a loop?
      assertNetwork(adj2, incidence, multiplicity);
      final Integer graphData = (Integer) source.global().get("graphData");
      System.out.println(graphData);
    }
  }

  @Test
  public void testUndirectedMultiplicity() throws IOException {
    final String file3 = "ch/ethz/sn/visone3/io/undirectedNetwork.byHand.graphml";
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    //test undirected edges
    try (InputStream in = cl.getResourceAsStream(file3);
        Source<?> source = new GraphmlSource(in)) {
      final SourceFormat sourceFormat = source.parse();

      System.out.println(source);

      final Mapping<? extends Integer> multiplicity = Mappings.cast(Integer.class, sourceFormat.dyadic().get(GraphmlSource.MULTIPLICITY));
      assertEquals(Arrays.asList(1, 1, 2),
        StreamSupport.stream(multiplicity.spliterator(), false).collect(Collectors.toList()));
    }
  }

  @Test
  public void testReadOneMode4() throws IOException {
    final String graph = "ch/ethz/sn/visone3/io/testOutputEmptyValues.graphml";
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    try (InputStream in = cl.getResourceAsStream(graph);
        Source<?> source = new GraphmlSource(in)) {
      final SourceFormat sourceFormat = source.parse();
      @SuppressWarnings("unused")
      final Mapping<? extends Integer> multiplicity = Mappings.cast(Integer.class, sourceFormat.dyadic().get(GraphmlSource.MULTIPLICITY));
    }
  }
}
