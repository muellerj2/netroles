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

package ch.ethz.sn.visone3.io.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Provides utility methods for streams.
 */
public final class IoStreams {
  private IoStreams() {
  }

  /**
   * Produces a buffered writer wrapping the specified output stream.
   * @param stream the output stream.
   * @return a buffered writer wrapping the output stream.
   */
  public static BufferedWriter writer(final OutputStream stream) {
    return new BufferedWriter(new OutputStreamWriter(stream));
  }

  /**
   * Produces a buffered reader wrapping the specified input stream.
   * @param stream the input stream.
   * @return a buffered reader wrapping the input stream.
   */
  public static BufferedReader reader(final InputStream stream) {
    return new BufferedReader(new InputStreamReader(stream));
  }
}
