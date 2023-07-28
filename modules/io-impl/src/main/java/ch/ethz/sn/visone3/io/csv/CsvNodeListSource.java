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

package ch.ethz.sn.visone3.io.csv;

import ch.ethz.sn.visone3.io.Source;
import ch.ethz.sn.visone3.io.SourceFormat;
import ch.ethz.sn.visone3.io.impl.IdMapper;
import ch.ethz.sn.visone3.io.impl.RangedList;
import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.progress.ProgressProvider;
import ch.ethz.sn.visone3.progress.ProgressSource;

import com.univocity.parsers.csv.CsvParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class CsvNodeListSource implements SourceFormat, Source<String> {
  private static final Logger LOG = LoggerFactory.getLogger(CsvNodeListSource.class);
  private final InputStream in;
  private final boolean header;
  private final Map<String, Range<?>> name2range;
  private String nameNode;
  private Range<?> rangeNodeId;
  private Map<String, Mapping<?>> monadic;
  private IdMapper<String> ids = IdMapper.continous(String.class);

  public CsvNodeListSource(final InputStream in, final boolean header) {
    this.in = in;
    this.header = header;
    name2range = new HashMap<>();
  }

  @Override
  public boolean isAutoconfig() {
    return false;
  }

  @Override
  public void mergeNodes(final ConstMapping<String> ids) {
    this.ids = IdMapper.fixed(ids); // TODO hand in mapper?
  }

  @Override
  public void monad(final String varName, final Range<?> range) {
    nameNode = varName;
    rangeNodeId = range;
  }

  @Override
  public void noderange(final String varName, final Range<?> range) {
    name2range.put(varName, range);
  }

  /**
   * Parses the CSV input.
   */
  @Override
  public SourceFormat parse() {
    final CsvParser parser = new CsvParser(UnivocitySettings.SETTINGS);
    parser.beginParsing(in);

    // parse header
    String[] row = parser.parseNext();
    final ArrayList<String> header = new ArrayList<>();
    if (this.header) {
      Collections.addAll(header, row);
      row = parser.parseNext();
    } else {
      IntStream.range(0, row.length).mapToObj(String::valueOf).forEach(header::add);
    }

    // find source and target columns
    final int nodeCol = header.indexOf(nameNode);
    if (nodeCol < 0) {
      throw new IllegalStateException("could not find node column\n" + "known: "
          + String.join(", ", header) + "\n" + "searching: " + nameNode);
    }
    if (name2range.remove(nameNode) != null) {
      LOG.warn("removed node range");
    }

    // create ranges and lists
    final RangedList<?>[] rangedMappings = new RangedList[row.length];
    final Range<?>[] ranges = new Range[row.length];
    final PrimitiveList<?>[] mappings = new PrimitiveList[row.length];
    for (int i = 0; i < ranges.length; i++) {
      rangedMappings[i] = new RangedList<>(name2range.get(header.get(i)));
    }
    LOG.info("ranges: {}", Arrays.toString(ranges));

    // read
    try (ProgressSource p = ProgressProvider.getMonitor().newSource()) {
      p.updateProgress("read node csv");
      int ignored = 0;
      do {
        final int v = ids.map(row[nodeCol]);
        if (v >= 0) {
          // add monadic attributes
          p.updateProgress(v);
          for (int i = 0; i < row.length; i++) {
            if (ranges[i] != null) {
              if (v >= mappings[i].size()) {
                mappings[i].setSize(null, v + 1);
              }
              rangedMappings[i].setListAt(v, row[i]);
            }
          }
        } else {
          ignored++;
        }
      } while ((row = parser.parseNext()) != null);
      LOG.info("{} ids read", ids.size());
      LOG.info("{} rows ignored (merging or empty)", ignored);

      // fill the monadic mappings map
      p.updateProgress(1, 3);
      monadic = new LinkedHashMap<>();
      for (int i = 0; i < ranges.length; i++) {
        if (ranges[i] != null) {
          PrimitiveList<?> mapping = rangedMappings[i].getList();
          mapping.setSize(null, ids.size());
          monadic.put(header.get(i), mapping);
        }
      }
      // save the original node ids
      final RangedList<?> nodeIds = new RangedList<>(rangeNodeId);
      for (final Map.Entry<String, Integer> e : ids.entrySet()) {
        nodeIds.setListAt(e.getValue().intValue(), e.getKey());
      }
      monadic.put("id", nodeIds.getList());
    }
    return this;
  }

  @Override
  public Map<String, Mapping<?>> monadic() {
    return Collections.unmodifiableMap(monadic);
  }

  @Override
  public Map<String, Integer> nodeIds() {
    return ids.getMapping();
  }

  @Override
  public void close() throws IOException {
    in.close();
  }
}
