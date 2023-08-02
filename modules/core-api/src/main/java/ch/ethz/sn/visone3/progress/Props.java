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

package ch.ethz.sn.visone3.progress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class Props {
  private static final Logger LOG = LoggerFactory.getLogger(Props.class);

  private Props() {
  }

  /**
   * Load a properties file form the class path.
   *
   * @param clazz Class determining the name of the file.
   * @return Properties loaded from {@code clazz.getCanonicalName() + ".properties"}.
   */
  public static Properties load(final Class<?> clazz) {
    final Properties props = new Properties();
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    final String name = clazz.getCanonicalName() + ".properties"; // name with dots
    final InputStream in = cl.getResourceAsStream(name);
    if (in != null) {
      try {
        props.load(in);
      } catch (IOException ioe) {
        LOG.info("loading props {}", ioe);
      }
    }
    return props;
  }
}
