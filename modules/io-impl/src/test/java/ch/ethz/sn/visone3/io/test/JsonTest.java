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
import static ch.ethz.sn.visone3.test.NetworkAsserts.boxed;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.ethz.sn.visone3.io.IoProvider;
import ch.ethz.sn.visone3.io.IoService;
import ch.ethz.sn.visone3.io.Sink;
import ch.ethz.sn.visone3.io.Source;
import ch.ethz.sn.visone3.io.SourceFormat;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.impl.AsciiDumper;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JsonTest {
  @Test
  public void testReadOneMode() throws IOException {
    final String FILE = "{\"vertices\": [" +
      "{\"g\":1}," +
      "{\"g\":2}," +
      "{\"g\":3}], \"edges\":[" +
      "{\"source\":0,\"target\":1,\"value\":1.0}," +
      "{\"source\":1,\"target\":2,\"value\":2.0}," +
      "{\"source\":2,\"target\":0,\"value\":0.0}]}";

    final double z = Double.NaN;
    final double[][] adj = new double[][]{
      {z, 1, z}, //
      {z, z, 2}, //
      {0, z, z} //
    };

    final IoService jsonService = IoProvider.getService("json");
    
    try (Source<?> conf = jsonService.newSource(new ByteArrayInputStream(FILE.getBytes()))) {
      final SourceFormat source = conf.parse();

      final Mapping<? extends Integer> g = Mappings.cast(Integer.class, source.monadic().get("g"));
      assertEquals(Arrays.asList(1, 2, 3), g.stream().collect(Collectors.toList()));

      final Network network = source.incidence();
      Mapping<?> value = source.dyadic().get("value");
      assertTrue(value instanceof PrimitiveList.OfDouble, "Expected double type for value mapping");
      assertNetwork(boxed(adj), network, (PrimitiveList.OfDouble) value);

      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      try (final Sink sink = jsonService.newSink(out)) {
        sink.incidence(network);
        sink.node("g", g);
        sink.link("value", value);
      }
      
      try (final Source<?> conf2 = jsonService.newSource(new ByteArrayInputStream(out.toByteArray()))) {
        SourceFormat source2 = conf2.parse();
        assertEquals(network, source2.incidence());
        assertArrayEquals(value.stream().toArray(),
            Mappings.cast(Number.class, source2.dyadic().get("value")).stream().map(Number::doubleValue).toArray());
        assertEquals(g, source2.monadic().get("g"));
      }
    }
  }

  @Test
  public void testAttributeTypeDetection() throws IOException {
    final String FILE = "{\"vertices\": [" +
      "{\"g\":1}," +
      "{\"g\":10000000000}," +
      "{\"g\":3}], \"edges\":[" +
      "{\"source\":0,\"target\":1,\"value\":1}," +
      "{\"source\":1,\"target\":2,\"value\":10000000000}," +
      "{\"source\":2,\"target\":0,\"value\":0}]," +
      "\"type\":\"directed\"}";

    final Long[][] adj = new Long[][]{
      {null, 1L, null}, //
      {null, null, 10_000_000_000L}, //
      {0L, null, null} //
    };
    
    final IoService jsonService = IoProvider.getService("json");

    try (Source<?> conf = jsonService.newSource(new ByteArrayInputStream(FILE.getBytes()))) {
      final SourceFormat source = conf.parse();

      final Mapping<? extends Long> g = Mappings.cast(Long.class, source.monadic().get("g"));
      System.out.println(AsciiDumper.singleLine(g));
      assertEquals(Arrays.asList(1L, 10_000_000_000L, 3L), g.stream().collect(Collectors.toList()));

      final Network network = source.incidence();
      System.out.println(AsciiDumper.multiLine(network));
      Mapping<?> value = source.dyadic().get("value");
      assertTrue(value instanceof PrimitiveList.OfLong, "Expected long type for mapping value");
      assertNetwork(adj, network, (PrimitiveList.OfLong)value);
      
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      try (final Sink sink = jsonService.newSink(out)) {
        sink.incidence(network);
        sink.node("g", g);
        sink.link("value", value);
      }
      
      try (final Source<?> conf2 = jsonService.newSource(new ByteArrayInputStream(out.toByteArray()))) {
        SourceFormat source2 = conf2.parse();
        assertEquals(network, source2.incidence());
        assertEquals(value, source2.dyadic().get("value"));
        assertEquals(g, source2.monadic().get("g"));
      }
    }
  }

  @Test
  public void testAttributesWithNull() throws IOException {
    final String FILE = "{\"vertices\": [" +
      "{\"g1\":1}," +
      "{\"g1\":2}," +
      "{\"g2\":3}], \"edges\":[" +
      "{\"source\":0,\"target\":1,\"w1\":1 ,\"w2\":1}," +
      "{\"source\":1,\"target\":2,\"w1\":2}," +
      "{\"source\":2,\"target\":0,\"w1\":0 ,\"w2\":0}]}";

    final Integer[][] adj = new Integer[][]{
      {null, 1, null}, //
      {null, null, 2}, //
      {0, null, null} //
    };

    final IoService jsonService = IoProvider.getService("json");
    
    try (Source<?> conf = jsonService.newSource(new ByteArrayInputStream(FILE.getBytes()))) {
      final SourceFormat source = conf.parse();

      final Mapping<? extends Integer> g1 = Mappings.cast(Integer.class, source.monadic().get("g1"));
      assertEquals(Arrays.asList(1, 2, null), g1.stream().collect(Collectors.toList()));
      final Mapping<? extends Integer> g2 = Mappings.cast(Integer.class, source.monadic().get("g2"));
      assertEquals(Arrays.asList(null, null, 3), g2.stream().collect(Collectors.toList()));

      final Network network = source.incidence();
      Mapping<?> w1 = source.dyadic().get("w1");
      assertTrue(w1 instanceof PrimitiveList.OfInt, "Expected int type for mapping w1");
      assertNetwork(adj, network, (PrimitiveList.OfInt) w1);
      Mapping<?> w2 = source.dyadic().get("w2");
      assertTrue(w2 instanceof PrimitiveList, "Expected generic type for mapping w2");
      assertFalse(w2.getComponentType().isPrimitive(), "Expected non-primitive component type for mapping w2");

      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      try (final Sink sink = jsonService.newSink(out)) {
        sink.incidence(network);
        sink.link("value", w2);
      }
      
      try (final Source<?> conf2 = jsonService.newSource(new ByteArrayInputStream(out.toByteArray()))) {
        SourceFormat source2 = conf2.parse();
        assertEquals(network, source2.incidence());
        assertEquals(w2, source2.dyadic().get("value"));
      }
    }
  }
}
