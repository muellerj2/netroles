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

package ch.ethz.sn.visone3.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implements a service providing I/O for specific file types.
 */
public interface IoService {
  /**
   * Checks if this service supports this file type.
   * 
   * @param fileType
   *          the file type.
   * @return true if the service supports the file type, false otherwise.
   */
  boolean supportFileType(String fileType);

  /**
   * Constructs a new source reading from the specified input stream.
   * 
   * @param in
   *          the input stream.
   * @return the source.
   * @throws IOException
   *           if some I/O error occurs.
   */
  Source<?> newSource(InputStream in) throws IOException;

  /**
   * Constructs a new source reading from the specified file.
   * 
   * @param in
   *          the file.
   * @return the source.
   * @throws IOException
   *           if some I/O error occurs.
   */
  default Source<?> newSource(final File in) throws IOException {
    return newSource(new BufferedInputStream(new FileInputStream(in)));
  }

  /**
   * Constructs a new source reading from the file at the specified path.
   * 
   * @param in
   *          the path to some file.
   * @return the source.
   * @throws IOException
   *           if some I/O error occurs.
   */
  default Source<?> newSource(final Path in) throws IOException {
    return newSource(Files.newInputStream(in));
  }

  /**
   * Constructs a new sink writing to the specified output stream.
   * 
   * @param out
   *          the output stream.
   * @return the sink.
   * @throws IOException
   *           if some I/O error occurs.
   */
  Sink newSink(OutputStream out) throws IOException;
}
