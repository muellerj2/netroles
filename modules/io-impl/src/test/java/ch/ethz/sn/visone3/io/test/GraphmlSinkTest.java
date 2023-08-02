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

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.ethz.sn.visone3.io.SourceFormat;
import ch.ethz.sn.visone3.io.graphml.GraphmlSink;
import ch.ethz.sn.visone3.io.graphml.GraphmlSource;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.WeightedNetwork;
import ch.ethz.sn.visone3.networks.MatrixSource.OfDouble;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 *
 */
public class GraphmlSinkTest {

  private final double z = Double.NaN;
  private final double[][] adj = new double[][] {
    {z, 1, z}, //
    {1, z, 2}, //
    {z, 2, z}, //
  };
  private final String file2 = "ch/ethz/sn/visone3/io/testOutputEmptyValues.graphml";

  @Test
  public void testWriteOneModeDefaultValues() throws Exception {
    final String file1 = "ch/ethz/sn/visone3/io/testOutputDefault.graphml";
    final WeightedNetwork<Double, Mapping.OfDouble> network = OfDouble.fromAdjacency(adj,
        DyadType.DIRECTED);
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (GraphmlSink graphmlSink = new GraphmlSink(out)) {
      graphmlSink.hint(GraphmlSink.Hint.PARSE_INFO.name(), "true");
      // graphmlSink.setKeyIdGenerator((key) -> "k_" + key);
      graphmlSink.incidence(network.getNetwork());
      graphmlSink.global("type", "map");
      graphmlSink.link("straight", 0, null);
      graphmlSink.link("weight", network.getWeight());
      graphmlSink.link("id", Mappings.newIntListFrom(1, 2, 3, 4));
      graphmlSink.node("color", "blue", null);
      graphmlSink.node("id", Mappings.newIntListFrom(1, 2, 3));
    }
    final String string1 = out.toString();
    final String expected = resourceAsStrign(file1);
    assertEquals(expected, string1);
  }

  @Test
  public void testWriteOneModeOverwriteDef() throws Exception {
    final String file1 = "ch/ethz/sn/visone3/io/testOutputDefault.graphml";
    final WeightedNetwork<Double, Mapping.OfDouble> network = OfDouble.fromAdjacency(adj,
        DyadType.DIRECTED);
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (GraphmlSink graphmlSink = new GraphmlSink(out)) {
      graphmlSink.hint(GraphmlSink.Hint.PARSE_INFO.name(), "true");
      // graphmlSink.setKeyIdGenerator((key) -> "k_" + key);
      graphmlSink.incidence(network.getNetwork());
      graphmlSink.global("type", "map");
      graphmlSink.link("straight", 0, null);
      graphmlSink.link("id", 4, null);
      graphmlSink.link("weight", network.getWeight());
      graphmlSink.link("id", Mappings.newIntListFrom(1, 2, 3, 4));
      graphmlSink.node("color", "blue", null);
      graphmlSink.node("id", Mappings.newIntListFrom(1, 2, 3));
    }
    final String string1 = out.toString();
    final String expected = resourceAsStrign(file1);
    assertEquals(expected, string1);
  }

  @Test
  public void testWriteOneModeMissingValues() throws Exception {
    final WeightedNetwork<Double, Mapping.OfDouble> network = OfDouble.fromAdjacency(adj,
        DyadType.DIRECTED);
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (GraphmlSink graphmlSink = new GraphmlSink(out)) {
      graphmlSink.hint(GraphmlSink.Hint.PARSE_INFO.name(), "true");
      // graphmlSink.setKeyIdGenerator((key) -> "k_" + key);
      graphmlSink.incidence(network.getNetwork());
      graphmlSink.link("name", "", Mappings.newListFrom(String.class, "", "", "Oh hi this won't work...", "pewpwew"));
      graphmlSink.node("someOtherID", Integer.MIN_VALUE, Mappings.newIntListFrom(Integer.MIN_VALUE, 123, 321));
      graphmlSink.node("weight", Double.NaN, Mappings.newDoubleListFrom(Double.NaN, 666.99, 42.00));
    }
    final String string1 = out.toString();
    final String expected = resourceAsStrign(file2);
    assertEquals(expected, string1);
  }

  @Test
  @Disabled // default value not matching -> is because default values aren't read as such
  public void testReadAndWriteOneMode() throws Exception {
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (
      final GraphmlSource conf = new GraphmlSource(cl.getResourceAsStream(file2));
      final GraphmlSink graphmlSink = new GraphmlSink(out)
    ) {
      final SourceFormat source = conf.parse();
      graphmlSink.hint(GraphmlSink.Hint.PARSE_INFO.name(), "true");
      // graphmlSink.setKeyIdGenerator((key) -> "k_" + key);
      graphmlSink.incidence(source.incidence());
      source.dyadic().forEach(graphmlSink::link);
      source.monadic().forEach(graphmlSink::node);
    }
    final String string1 = out.toString();
    final String expected = resourceAsStrign(file2);
    assertEquals(expected, string1);
  }

//  @Ignore // too much time
//  @Test
//  public void writeReadBigOneMode() throws Exception {
//    final GnpImpl source;
//    final Network network;
//    System.out.println("Starting random gen");
//    source = new GnpImpl();
//    source.setNumVertices(1000);
//    source.setDirected(true);
//    source.setEdgeProbability(0.2);
//    network = source.generate();
//    System.out.println("Start writing");
//    final long startTime = System.currentTimeMillis();
//    final Path path = Files.createTempDirectory("visone3test");
//    final ByteArrayOutputStream out = new ByteArrayOutputStream();
//    try (GraphmlSink graphmlSink = new GraphmlSink(out)) {
//      graphmlSink.incidence(network);
//    }
//    final long endTime = System.currentTimeMillis();
//    final long total = endTime - startTime;
//    System.out.println("Write time: " + total);
//    final String readWriteFile = path.toString() + "bigNetwork.graphmlz";
//    try (OutputStream outputStream = new GZIPOutputStream(new FileOutputStream(readWriteFile))) {
//      out.writeTo(outputStream);
//    }
//    final long startReadTime = System.currentTimeMillis();
//    try (GraphmlSource in = new GraphmlSource(new GZIPInputStream(new FileInputStream(readWriteFile)))) {
//      //
//    }
//    final long endReadTime = System.currentTimeMillis();
//    final long ReadTotal = endReadTime - startReadTime;
//    System.out.println("Read time: " + ReadTotal);
//    Files.delete(path);
//  }

  private String resourceAsStrign(final String url) throws IOException {
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(cl.getResourceAsStream(url)))) {
      String sep = System.lineSeparator();
      return br.lines().collect(Collectors.joining(sep)) + sep;
    }
  }
}
