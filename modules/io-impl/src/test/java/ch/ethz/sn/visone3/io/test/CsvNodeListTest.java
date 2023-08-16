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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.ethz.sn.visone3.io.IoProvider;
import ch.ethz.sn.visone3.io.IoService;
import ch.ethz.sn.visone3.io.Sink;
import ch.ethz.sn.visone3.io.Source;
import ch.ethz.sn.visone3.io.SourceFormat;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvNodeListTest {
  private static final String NODE = "node";
  private static final String VALUE0 = "v0";
  private static final String VALUE1 = "v1";
  private final String[] header = new String[] { VALUE0, NODE, VALUE1 };
  private final int[] attr1 = new int[] {  2, 1, 3  };
  private final int[] attr2 = new int[] {  0, 1, 2  };
  private final int[] ids = new int[] { 0, 2, 1 };
  
  private final int[] attr1_reordered = new int[] { 2, 3, 1 };
  private final int[] attr2_reordered = new int[] { 0, 2, 1 };
  private final int[] ids_reordered = new int[] { 0, 1, 2 };
  private final int[][] data = new int[][] { //
      { 2, 0, 0 }, //
      { 1, 2, 1 }, //
      { 3, 1, 2 } };

  private String data(final char delimiter) {
    final String del = String.valueOf(delimiter);
    final StringBuilder sb = new StringBuilder();
    sb.append(String.join(del, header)).append("\n");
    for (final int[] row : data) {
      sb.append(Arrays.stream(row).mapToObj(Integer::toString).collect(Collectors.joining(del)))
          .append("\n");
    }
    return sb.toString();
  }

  @Test
  public void testRead() throws Exception {
    final String data = data(',');

    // test source

    final IoService csv = IoProvider.getService("nodelist.csv");
    try (final Source<?> in = csv.newSource(new ByteArrayInputStream(data.getBytes()))) {
      in.monad(NODE, Source.Range.INT);
      in.noderange(VALUE0, Source.Range.INT);
      in.noderange(VALUE1, Source.Range.INT);
      final SourceFormat source = in.parse();

      assertNull(source.incidence());

      final Mapping.OfInt value0 = (Mapping.OfInt) source.monadic().get(VALUE0);
      assertArrayEquals(attr1, value0.array());

      final Mapping.OfInt value1 = (Mapping.OfInt) source.monadic().get(VALUE1);
      assertArrayEquals(attr2, value1.array());

      final Mapping.OfInt idMap = (Mapping.OfInt) source.monadic().get("id");
      assertArrayEquals(ids, idMap.array());
      assertEquals(ids.length, source.nodeIds().size());
      for (Map.Entry<?, Integer> idToIndex : source.nodeIds().entrySet()) {
        assertEquals(Integer.toString(ids[idToIndex.getValue()]), idToIndex.getKey());
      }

      // test sink
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      try (final Sink sink = csv.newSink(out)) {
        sink.node(VALUE0, value0);
        sink.node(VALUE1, value1);
      }

      assertEquals("node,v0,v1\n0,2,0\n1,1,1\n2,3,2\n", new String(out.toByteArray()));
    }
  }

  @Test
  public void testReadFailures() throws Exception {

    // test source
    final IoService csv = IoProvider.getService("nodelist.csv");
    try (final Source<?> in = csv.newSource(new ByteArrayInputStream("v0\n0\n".getBytes()))) {
      in.noderange(VALUE0, Source.Range.INT);
      assertThrows(IOException.class, in::parse);
    }
    try (final Source<?> in = csv.newSource(new ByteArrayInputStream(data(',').getBytes()))) {
      in.monad("source", Source.Range.STRING);
      in.noderange(VALUE0, Source.Range.INT);
      assertThrows(IOException.class, in::parse);
    }
  }
  


  @Test
  public void testReadReordered() throws Exception {
    final String data = data(',');

    // test source

    final IoService csv = IoProvider.getService("nodelist.csv");
    try (final Source<?> in = csv.newSource(new ByteArrayInputStream(data.getBytes()))) {
      in.monad(NODE, Source.Range.INT);
      in.mergeNodes(Mappings.newListFrom(String.class, "0", "1", "2"));
      in.noderange(VALUE0, Source.Range.INT);
      in.noderange(VALUE1, Source.Range.INT);
      final SourceFormat source = in.parse();

      assertNull(source.incidence());

      final Mapping.OfInt value0 = (Mapping.OfInt) source.monadic().get(VALUE0);
      assertArrayEquals(attr1_reordered, value0.array());

      final Mapping.OfInt value1 = (Mapping.OfInt) source.monadic().get(VALUE1);
      assertArrayEquals(attr2_reordered, value1.array());

      final Mapping.OfInt idMap = (Mapping.OfInt) source.monadic().get("id");
      assertArrayEquals(ids_reordered, idMap.array());

      // test sink
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      try (final Sink sink = csv.newSink(out)) {
        sink.node(VALUE0, value0);
        sink.node(VALUE1, value1);
      }

      assertEquals("node,v0,v1\n0,2,0\n1,3,2\n2,1,1\n", new String(out.toByteArray()));
    }
  }
  
  @Test
  public void testEmptyWrite() throws IOException {

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (final Sink sink = IoProvider.getService("nodelist.csv").newSink(out)) {
    }
    assertEquals("node\n", new String(out.toByteArray()));
  }
}
