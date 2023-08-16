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

package ch.ethz.sn.visone3.io.json;

import ch.ethz.sn.visone3.io.IoService;
import ch.ethz.sn.visone3.io.Sink;
import ch.ethz.sn.visone3.io.Source;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Service class making JSON source and sink available to others.
 */
public class JsonIoService implements IoService {
  private static final String[] SUPPORTED_TYPES = new String[] {"json"};

  @Override
  public boolean supportFileType(final String fileType) {
    return Arrays.binarySearch(SUPPORTED_TYPES, fileType) >= 0;
  }

  @Override
  public Source<String> newSource(final InputStream in) throws IOException {
    return new JsonSource(in);
  }

  @Override
  public Sink newSink(final OutputStream out) throws IOException {
    return new JsonSink(out);
  }
}
