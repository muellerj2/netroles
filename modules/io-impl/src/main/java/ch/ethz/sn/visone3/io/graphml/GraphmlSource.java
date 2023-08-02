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

package ch.ethz.sn.visone3.io.graphml;

import ch.ethz.sn.visone3.io.Source;
import ch.ethz.sn.visone3.io.SourceFormat;
import ch.ethz.sn.visone3.io.impl.SourceFormatImpl;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.networks.DyadType;
import ch.ethz.sn.visone3.networks.Network;
import ch.ethz.sn.visone3.networks.NetworkBuilder;
import ch.ethz.sn.visone3.networks.NetworkProvider;
import ch.ethz.sn.visone3.networks.Networks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Read GraphML Graphs.
 *
 * <p>Supported features: <ul> <li>Reads <b>one flat</b> graph, no nested objects in node nodes.
 * <li>Directed or undirected <b>not mixed</b> edges. If the input contains a directed edge all
 * edges will be directed. Undirected edges will be present in both directions. <li><b>Simple</b> no
 * multi edges, use a weight for multiplicity. </ul>
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;graphml ...&gt;
 *   &lt;key .../&gt;
 *   ...
 *   &lt;graph id="..." edgedefault="..." ...&gt;
 *     &lt;node id="..."&gt;
 *       &lt;data key="..."&gt;...&lt;/data&gt;
 *       ...
 *     &lt;/node&gt;
 *     ...
 *     &lt;edge id="..." source="..." target="..."&gt;
 *       &lt;data key="..."&gt;...&lt;/data&gt;
 *       ...
 *     &lt;/edge&gt;
 *     ...
 *     &lt;data key="..."&gt;...&lt;/data&gt;
 *     ...
 *   &lt;/graph&gt;
 *   &lt;data key="..."&gt;...&lt;/data&gt;
 *   ...
 * &lt;/graphml&gt;
 * </pre>
 */
public class GraphmlSource implements Source<Object>, AutoCloseable {
  private static final Logger LOG = LoggerFactory.getLogger(GraphmlSource.class);
  public static final String MULTIPLICITY = "_multiplicity";
  public static final String NESTED = "_nested";
  private final InputStream inputStream;

  public GraphmlSource(final InputStream inputStream) {
    this.inputStream = inputStream;
  }

  @Override
  public boolean isAutoconfig() {
    return true;
  }

  private XMLReader setupReader() throws ParserConfigurationException, SAXException {
    final SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    factory.setXIncludeAware(false);
    factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    final SAXParser saxParser = factory.newSAXParser();
    saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
    return saxParser.getXMLReader();
  }
  
  @Override
  public SourceFormat parse() throws IOException {
    try {
      final GraphmlHandler graphmlHandler = new GraphmlHandler();
      final XMLReader p = setupReader();
      p.setContentHandler(graphmlHandler);
      p.parse(new InputSource(inputStream));

      // create incidence structure
      final NetworkBuilder builder = NetworkProvider.getInstance().builder(
        DyadType.oneMode(graphmlHandler.isGlobalDirected()));
      for (int i = 0; i < graphmlHandler.sources.size(); i++) {
        builder.addEdge(graphmlHandler.sources.getInt(i), graphmlHandler.targets.getInt(i));
      }
      final Network incidence = builder.build();
      final Map<String, Object> graphDefault = new HashMap<>(); // TODO source?
      final Map<String, Mapping<?>> monadic = new HashMap<>();
      final Map<String, Mapping<?>> dyadic = new HashMap<>();

      // extract and pad attributes to correct dimensions
      for (final GraphmlHandler.Key<?> key : graphmlHandler.keys.values()) {
        switch (key.getElmntType()) {
          case EDGE:
            dyadic.put(key.getName(), key.mapping(incidence.countDyadicIndices()));
            break;
          case NODE:
            monadic.put(key.getName(), key.mapping(incidence.asRelation().countUnionDomain()));
            break;
          case GRAPH:
            graphDefault.put(key.getName(), key.mapping(1).get(0));
            break;
          default:
            LOG.warn("ignore data key type: " + key.getElmntType());
        }
      }

      // verify
      // Networks.source(incidence, monadic, dyadic);
      for (Map.Entry<String, Mapping<?>> e : monadic.entrySet()) {
        Networks.requireVertexMapping(incidence, e.getValue());
      }
      for (Map.Entry<String, Mapping<?>> e : dyadic.entrySet()) {
        Networks.requireLinkMapping(incidence, e.getValue());
      }
      return new SourceFormatImpl(incidence, monadic, dyadic, graphmlHandler.getNodeIds());
    } catch (final SAXException ex) {
      throw new IOException("xml parse error", ex);
    } catch (final ParserConfigurationException ex) {
      throw new IOException("xml parser configuration error", ex);
    }
  }

  @Override
  public void close() throws IOException {
    inputStream.close();
  }
}
