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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.ethz.sn.visone3.io.IoProvider;
import ch.ethz.sn.visone3.io.IoService;
import ch.ethz.sn.visone3.io.Sink;
import ch.ethz.sn.visone3.io.Source;
import ch.ethz.sn.visone3.io.SourceFormat;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.impl.AsciiDumper;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvEdgeListTest {
  private static final String SOURCE = "source";
  private static final String TARGET = "target";
  private static final String VALUE0 = "v0";
  private static final String VALUE1 = "v1";
  private final String[] header = new String[] { SOURCE, TARGET, VALUE0, VALUE1 };
  private final Integer z = null;
  private final int[] ids0 = new int[] { 0, 1 };
  private final int[] ids1 = new int[] { 0, 1, 1, 0 };
  private final Integer[][] adj0 = new Integer[][] { //
      { z, 1 }, //
      { 2, 3 } };
  private final Integer[][] adj1 = new Integer[][] { //
      { z, 2 }, //
      { 0, 2 } };
  private final int[][] data = new int[][] { //
      { 0, 1, 1, 2 }, //
      { 1, 0, 2, 0 }, //
      { 1, 1, 3, 2 } };

  private final Integer[][] adj2 = new Integer[][] { //
      { 1, z }, //
      { 3, 2 }, //
  };
  private final Integer[][] adj3 = new Integer[][] { //
      { 2, z }, //
      { 2, 0 }, //
  };
  private final Integer[][] adj4 = new Integer[][] { //
      { 3, 2 }, //
      { 1, z }, //
  };
  private final Integer[][] adj5 = new Integer[][] { //
      { 2, 0 }, //
      { 2, z }, //
  };
  private final Integer[][] adj6 = new Integer[][] { //
      { 2, 3 }, //
      { z, 1 }, //
  };
  private final Integer[][] adj7 = new Integer[][] { //
      { 0, 2 }, //
      { z, 2 }, //
  };

  private String data(final char delimiter) {
    final String del = String.valueOf(delimiter);
    final StringBuilder sb = new StringBuilder();
    sb.append(String.join(del, header)).append("\n");
    for (final int[] row : data) {
      sb.append(Arrays.stream(row).mapToObj(Integer::toString).collect(Collectors.joining(del))).append("\n");
    }
    return sb.toString();
  }

  @Test
  public void testReadOneMode() throws Exception {
    final String data = data(',');

    // test source

    final IoService csv = IoProvider.getService("edgelist.csv");
    try (final Source<?> in = csv.newSource(new ByteArrayInputStream(data.getBytes()))) {
      in.dyad(DyadType.DIRECTED, SOURCE, TARGET, Source.Range.INT);
      in.linkrange(VALUE0, Source.Range.INT);
      in.linkrange(VALUE1, Source.Range.INT);
      final SourceFormat source = in.parse();

      final Network incidence = source.incidence();
      System.out.println(AsciiDumper.multiLine(incidence));

      final Mapping<Integer> value0 = Mappings.castExact(Integer.class, source.dyadic().get(VALUE0));
      assertNetwork(adj0, incidence, value0);

      final Mapping<Integer> value1 = Mappings.castExact(Integer.class, source.dyadic().get(VALUE1));
      assertNetwork(adj1, incidence, value1);

      final Mapping.OfInt idMap = (Mapping.OfInt) source.monadic().get("id");
      assertArrayEquals(ids0, idMap.array());
      assertEquals(2, source.nodeIds().size());
      for (Map.Entry<?, Integer> idToIndex : source.nodeIds().entrySet()) {
        assertEquals(Integer.toString(ids0[idToIndex.getValue()]), idToIndex.getKey());
      }

      // test sink
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      try (final Sink sink = csv.newSink(out)) {
        sink.incidence(source.incidence());
        sink.link(VALUE0, null, source.dyadic().get(VALUE0));
        sink.link(VALUE1, null, source.dyadic().get(VALUE1));
      }

      assertEquals(data, new String(out.toByteArray()));
    }
  }

  @Test
  public void testReadTwoMode() throws Exception {
    final String data = data(',');

    // test source

    final IoService csv = IoProvider.getService("edgelist.csv");
    try (final Source<?> in = csv.newSource(new ByteArrayInputStream(data.getBytes()))) {
      in.dyad(DyadType.TWO_MODE, SOURCE, TARGET, Source.Range.INT);
      in.linkrange(VALUE0, Source.Range.INT);
      in.linkrange(VALUE1, Source.Range.INT);
      final SourceFormat source = in.parse();

      final Network incidence = source.incidence();
      System.out.println(AsciiDumper.multiLine(incidence));

      final Mapping<Integer> value0 = Mappings.castExact(Integer.class, source.dyadic().get(VALUE0));
      assertNetwork(adj2, incidence, value0);

      final Mapping<Integer> value1 = Mappings.castExact(Integer.class, source.dyadic().get(VALUE1));
      assertNetwork(adj3, incidence, value1);
      
      final Mapping.OfInt idMap = (Mapping.OfInt) source.monadic().get("id");
      assertArrayEquals(ids1, idMap.array());
      assertEquals(2, source.nodeIds().size());
      for (Map.Entry<?, Integer> idToIndex : source.nodeIds().entrySet()) {
        assertEquals(Integer.toString(ids1[idToIndex.getValue()]), idToIndex.getKey());
      }

      // test sink
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      try (final Sink sink = csv.newSink(out)) {
        sink.incidence(source.incidence());
        sink.link(VALUE0, null, source.dyadic().get(VALUE0));
        sink.link(VALUE1, null, source.dyadic().get(VALUE1));
      }

      assertEquals("source,target,v0,v1\n0,2,1,2\n1,3,2,0\n1,2,3,2\n", new String(out.toByteArray()));
    }
  }

  @Test
  public void testReadOneModeReordered() throws Exception {
    final String data = data(',');

    // test source

    final IoService csv = IoProvider.getService("edgelist.csv");
    try (final Source<?> in = csv.newSource(new ByteArrayInputStream(data.getBytes()))) {
      in.dyad(DyadType.DIRECTED, SOURCE, TARGET, Source.Range.INT);
      in.linkrange(VALUE0, Source.Range.INT);
      in.linkrange(VALUE1, Source.Range.INT);
      in.mergeNodes(Mappings.newListFrom(String.class, "1", "0"));
      final SourceFormat source = in.parse();

      final Network incidence = source.incidence();
      System.out.println(AsciiDumper.multiLine(incidence));

      final Mapping<Integer> value0 = Mappings.castExact(Integer.class, source.dyadic().get(VALUE0));
      assertNetwork(adj4, incidence, value0);

      final Mapping<Integer> value1 = Mappings.castExact(Integer.class, source.dyadic().get(VALUE1));
      assertNetwork(adj5, incidence, value1);

      // test sink
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      try (final Sink sink = csv.newSink(out)) {
        sink.incidence(source.incidence());
        sink.link(VALUE0, null, source.dyadic().get(VALUE0));
        sink.link(VALUE1, null, source.dyadic().get(VALUE1));
      }

      assertEquals("source,target,v0,v1\n0,1,2,0\n0,0,3,2\n1,0,1,2\n", new String(out.toByteArray()));
    }
  }

  @Test
  public void testReadTwoModeReordered() throws Exception {
    final String data = data(',');

    // test source

    final IoService csv = IoProvider.getService("edgelist.csv");
    try (final Source<?> in = csv.newSource(new ByteArrayInputStream(data.getBytes()))) {
      in.dyad(DyadType.TWO_MODE, SOURCE, TARGET, Source.Range.INT);
      in.mergeNodes(Mappings.newListFrom(String.class, "1", "0"));
      in.mergeAffiliations(Mappings.newListFrom(String.class, "0", "1"));
      in.linkrange(VALUE0, Source.Range.INT);
      in.linkrange(VALUE1, Source.Range.INT);
      final SourceFormat source = in.parse();

      final Network incidence = source.incidence();
      System.out.println(AsciiDumper.multiLine(incidence));

      final Mapping<Integer> value0 = Mappings.castExact(Integer.class, source.dyadic().get(VALUE0));
      assertNetwork(adj6, incidence, value0);

      final Mapping<Integer> value1 = Mappings.castExact(Integer.class, source.dyadic().get(VALUE1));
      assertNetwork(adj7, incidence, value1);

      // test sink
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      try (final Sink sink = csv.newSink(out)) {
        sink.incidence(source.incidence());
        sink.link(VALUE0, null, source.dyadic().get(VALUE0));
        sink.link(VALUE1, null, source.dyadic().get(VALUE1));
      }

      assertEquals("source,target,v0,v1\n0,2,2,0\n0,3,3,2\n1,3,1,2\n", new String(out.toByteArray()));
    }
  }

  @Test
  public void testReadFailures() throws Exception {
    final String data = data(',');

    final IoService csv = IoProvider.getService("edgelist.csv");
    try (final Source<?> in = csv.newSource(new ByteArrayInputStream(data.getBytes()))) {
      in.dyad(DyadType.DIRECTED, "no", TARGET, Source.Range.INT);
      in.linkrange(VALUE0, Source.Range.INT);
      in.linkrange(VALUE1, Source.Range.INT);
      assertThrows(IOException.class, in::parse);
    }
    try (final Source<?> in = csv.newSource(new ByteArrayInputStream(data.getBytes()))) {
      in.dyad(DyadType.DIRECTED, SOURCE, "no", Source.Range.INT);
      in.linkrange(VALUE0, Source.Range.INT);
      in.linkrange(VALUE1, Source.Range.INT);
      assertThrows(IOException.class, in::parse);
    }
    try (final Source<?> in = csv.newSource(new ByteArrayInputStream(data.getBytes()))) {
      in.dyad(DyadType.DIRECTED, "no", "no", Source.Range.INT);
      in.linkrange(VALUE0, Source.Range.INT);
      in.linkrange(VALUE1, Source.Range.INT);
      assertThrows(IOException.class, in::parse);
    }
    try (final Source<?> in = csv.newSource(new ByteArrayInputStream(data.getBytes()))) {
      in.dyad(DyadType.DIRECTED, SOURCE, TARGET, Source.Range.INT);
      assertThrows(IllegalStateException.class, () -> in.mergeAffiliations(Mappings.newList(String.class)));
    }
    try (final Source<?> in = csv.newSource(new ByteArrayInputStream((SOURCE + "," + TARGET + "\n0\n").getBytes()))) {
      in.dyad(DyadType.DIRECTED, SOURCE, TARGET, Source.Range.INT);
      assertThrows(IOException.class, in::parse);
    }
  }
}
