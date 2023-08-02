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

import ch.ethz.sn.visone3.io.IoService;
import ch.ethz.sn.visone3.io.Sink;
import ch.ethz.sn.visone3.io.Source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class CsvIoService {
  private static final Logger LOG = LoggerFactory.getLogger(Edge.class);

  private static void error(final String fileType) {
    if (fileType.equals("csv")) {
      LOG.warn("unspecified csv type");
      LOG.warn("use " + String.join(",", Node.SUPPORTED_TYPES) + " for node lists");
      LOG.warn("use " + String.join(",", Edge.SUPPORTED_TYPES) + " for edge lists");
    }
  }

  public static class Node implements IoService {
    private static final String[] SUPPORTED_TYPES = new String[] {"nodelist.csv", "nodes.csv"};

    @Override
    public boolean supportFileType(final String fileType) {
      if (Arrays.binarySearch(SUPPORTED_TYPES, fileType) >= 0) {
        return true;
      }
      error(fileType);
      return false;
    }

    @Override
    public Source<String> newSource(final InputStream in) {
      return new CsvNodeListSource(in, true);
    }

    @Override
    public Sink newSink(final OutputStream out) {
      return new CsvNodeListSink(out, ',');
    }
  }

  public static class Edge implements IoService {
    private static final String[] SUPPORTED_TYPES = new String[] {"edgelist.csv", "edges.csv"};

    @Override
    public boolean supportFileType(final String fileType) {
      if (Arrays.binarySearch(SUPPORTED_TYPES, fileType) >= 0) {
        return true;
      }
      error(fileType);
      return false;
    }

    @Override
    public Source<String> newSource(final InputStream in) {
      return new CsvEdgeListSource(in, true);
    }

    @Override
    public Sink newSink(final OutputStream out) {
      return new CsvEdgeListSink(out, ',');
    }
  }
}
