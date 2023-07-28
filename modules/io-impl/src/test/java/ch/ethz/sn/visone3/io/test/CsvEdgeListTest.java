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

package ch.ethz.sn.visone3.io.test;

import static ch.ethz.sn.visone3.test.NetworkAsserts.assertNetwork;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import java.util.Arrays;
import java.util.stream.Collectors;

public class CsvEdgeListTest {
  private static final String SOURCE = "source";
  private static final String TARGET = "target";
  private static final String VALUE0 = "v0";
  private static final String VALUE1 = "v1";
  private final String[] header = new String[] { SOURCE, TARGET, VALUE0, VALUE1 };
  private final Integer z = null;
  private final Integer[][] adj0 = new Integer[][] { { z, 1 }, //
      { 2, 3 } };
  private final Integer[][] adj1 = new Integer[][] { { z, 2 }, //
      { 0, 2 } };
  private final int[][] data = new int[][] { { 0, 1, 1, 2 }, //
      { 1, 0, 2, 0 }, //
      { 1, 1, 3, 2 } };

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

      final Mapping<Integer> value0 = Mappings.castExact(Integer.class,
          source.dyadic().get(VALUE0));
      assertNetwork(adj0, incidence, value0);

      final Mapping<Integer> value1 = Mappings.castExact(Integer.class,
          source.dyadic().get(VALUE1));
      assertNetwork(adj1, incidence, value1);

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
}
