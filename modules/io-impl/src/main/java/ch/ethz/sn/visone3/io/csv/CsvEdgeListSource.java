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

package ch.ethz.sn.visone3.io.csv;

import ch.ethz.sn.visone3.io.Source;
import ch.ethz.sn.visone3.io.SourceFormat;
import ch.ethz.sn.visone3.io.impl.IdMapper;
import ch.ethz.sn.visone3.io.impl.RangedList;
import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.NetworkBuilder;
import ch.ethz.sn.visone3.networks.NetworkProvider;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * CSV network source. All the CSV settings (line separator, delimiter, quotes) are auto discovered
 * (thanks to univocity). Semantics of columns has to supplied by calling
 * {@link Source#dyad(DyadType, String, String, Range)} and {@link #linkrange(String, Range)}.
 * Columns without a range specification are not read.
 *
 * <p>
 * Example:
 * 
 * <pre>
 * final CsvEdgeListSource source = new CsvEdgeListSource(stream, true);
 *
 * // configure what to read
 * source.dyad(DyadType.DIRECTED, SOURCE, TARGET, Source.Range.INT);
 * source.range(VALUE, Source.Range.INT);
 *
 * // read
 * try (final InputStream in = new ByteArrayInputStream(data.getBytes())) {
 *   source.parse();
 * }
 * </pre>
 */
public class CsvEdgeListSource implements SourceFormat, Source<String> {
  private static final Logger LOG = LoggerFactory.getLogger(CsvEdgeListSource.class);
  // single monadic result with original node names
  /**
   * Name of the generated ID attribute.
   */
  public static final String ID = "id";
  private static final String BUILDER_UNINTIALIZED_MESSAGE = "first call #dyad()";
  private final InputStream in;
  private final boolean header;
  private final Map<String, Range<?>> name2range;
  private NetworkBuilder builder;
  private String nameSource;
  private String nameTarget;
  private Range<?> rangeNodeId;
  private Map<String, Mapping<?>> monadic;
  private Map<String, Mapping<?>> dyadic;
  private Network incidence;
  private IdMapper<String> nodeIds;
  private IdMapper<String> affiliationIds;

  /**
   * Constructs the source.
   * 
   * @param in     the stream to read from.
   * @param header true if the CSV data contains a header line, otherwise false.
   */
  public CsvEdgeListSource(final InputStream in, final boolean header) {
    this.in = in;
    this.header = header;
    name2range = new HashMap<>();
    nodeIds = IdMapper.continous(String.class);
  }

  @Override
  public void dyad(final DyadType type, final String sourceVarName, final String targetVarName,
      final Range<?> range) {
    builder = NetworkProvider.getInstance().builder(type);
    nameSource = sourceVarName;
    nameTarget = targetVarName;
    rangeNodeId = range;
    affiliationIds = builder.acceptsTwoModes() ? IdMapper.continous(String.class) : nodeIds;
  }

  @Override
  public void linkrange(final String varName, final Range<?> range) {
    name2range.put(varName, range);
  }

  @Override
  public SourceFormat parse() throws IOException {
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
    final int sourceCol = header.indexOf(nameSource);
    final int targetCol = header.indexOf(nameTarget);
    if (sourceCol < 0 || targetCol < 0) {
      throw new IOException("could not find source/target column\n" + "known: "
          + String.join(", ", header) + "\n" + "searching: " + nameSource + ", " + nameTarget);
    }
    if (name2range.remove(nameSource) != null) {
      LOG.warn("removed source range");
    }
    if (name2range.remove(nameTarget) != null) {
      LOG.warn("removed target range");
    }

    // create ranges and lists
    final RangedList<?>[] rangedMappings = new RangedList[row.length];
    for (int i = 0; i < rangedMappings.length; i++) {
      rangedMappings[i] = new RangedList<>(name2range.get(header.get(i)));
    }
    LOG.info("ranges: {}", Arrays.toString(rangedMappings));

    // read
    try (ProgressSource p = ProgressProvider.getMonitor().newSource()) {
      p.updateProgress("read edge csv");
      int ignoredId = 0;
      int ignoredEdge = 0;
      do {
        if (row.length != rangedMappings.length || sourceCol >= row.length || targetCol >= row.length) {
          throw new IOException("row too short");
        }
        // add edge
        final int s = nodeIds.map(row[sourceCol]);
        final int t = affiliationIds.map(row[targetCol]);
        if (s >= 0 && t >= 0) {
          final int e = builder.addEdge(s, t);
          if (e >= 0) {
            p.updateProgress(e);
            // first time around: add dyadic attributes
            for (int i = 0; i < rangedMappings.length; i++) {
              RangedList<?> rangedMapping = rangedMappings[i];
              if (rangedMapping.getList() != null) {
                if (rangedMapping.getList().size() != e) {
                  throw new IllegalStateException();
                }
                rangedMapping.addToList(row[i]);
              }
            }
          } else {
            if (ignoredEdge < 10) {
              LOG.warn("duplicate ({},{}): {}", s, t, Arrays.toString(row));
            }
            ignoredEdge++;
          }
        } else {
          ignoredId++;
        }
      } while ((row = parser.parseNext()) != null);
      LOG.info("{} ids read", nodeIds.size());
      LOG.info("{} rows ignored (unmerged or empty ids)", ignoredId);
      LOG.info("{} rows ignored (duplicates)", ignoredEdge);

      // network dimensions
      int numVertices = nodeIds.size();
      IntStream.range(0, numVertices).forEach(builder::ensureNode);
      if (builder.acceptsTwoModes()) {
        numVertices += affiliationIds.size();
        IntStream.range(0, affiliationIds.size()).forEach(builder::ensureAffiliation);
      }
      // build network
      incidence = builder.build();
      if (incidence.asRelation().countUnionDomain() != numVertices) {
        throw new IllegalStateException();
      }

      // add monadic mapping to original index names
      final RangedList<?> rangedIds = new RangedList<>(rangeNodeId, numVertices);
      LOG.info("domain {}, nodes {}, affiliations {}", numVertices, nodeIds.size(),
          affiliationIds.size());
      for (final Map.Entry<String, Integer> e : nodeIds.entrySet()) {
        rangedIds.setListAt(e.getValue().intValue(), e.getKey());
      }
      final int nodeIdsSize = nodeIds.size();
      if (builder.acceptsTwoModes()) {
        for (final Map.Entry<String, Integer> e : affiliationIds.entrySet()) {
          rangedIds.setListAt(nodeIdsSize + e.getValue().intValue(), e.getKey());
        }
      }
      monadic = Collections.singletonMap(ID, rangedIds.getList());

      // fill the dyadic mappings array
      dyadic = new HashMap<>();
      for (int i = 0; i < rangedMappings.length; i++) {
        PrimitiveList<?> mapping = rangedMappings[i].getList();
        if (mapping != null) {
          dyadic.put(header.get(i), mapping);
        }
      }
    }
    return this;
  }

  @Override
  public boolean isAutoconfig() {
    return false;
  }

  @Override
  public void mergeNodes(final ConstMapping<String> ids) {
    nodeIds = IdMapper.fixed(ids);
    Objects.requireNonNull(builder, BUILDER_UNINTIALIZED_MESSAGE);
    if (!builder.acceptsTwoModes()) {
      affiliationIds = nodeIds;
    }
  }

  @Override
  public void mergeAffiliations(final ConstMapping<String> ids) {
    Objects.requireNonNull(builder, BUILDER_UNINTIALIZED_MESSAGE);
    if (!builder.acceptsTwoModes()) {
      throw new IllegalStateException("not a 2-mode builder");
    }
    affiliationIds = IdMapper.fixed(ids);
  }

  @Override
  public Map<String, Mapping<?>> monadic() {
    return Collections.unmodifiableMap(monadic);
  }

  @Override
  public Map<String, Integer> nodeIds() {
    return nodeIds.getMapping();
  }

  @Override
  public Map<String, Mapping<?>> dyadic() {
    return Collections.unmodifiableMap(dyadic);
  }

  @Override
  public Network incidence() {
    return incidence;
  }

  @Override
  public void close() throws IOException {
    in.close();
  }
}
