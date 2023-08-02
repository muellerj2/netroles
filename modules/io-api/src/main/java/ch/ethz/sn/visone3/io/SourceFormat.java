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

import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.networks.Network;

import java.util.Collections;
import java.util.Map;

/**
 * Data source abstraction. A source can provide monadic and/or dyadic data. The incidence structure
 * of all dyadic mappings is the same and mandatory if there is any dyadic data.
 */
public interface SourceFormat {
  /**
   * Returns the incidence structure underlying the dyadic data.
   * 
   * @return the incidence structure.
   */
  default Network incidence() {
    return null;
  }

  /**
   * Returns the global values in the data.
   * 
   * @return map of the global values.
   */
  default Map<String, Object> global() {
    return Collections.emptyMap();
  }

  /**
   * Returns the monadic (node) data.
   * 
   * @return map of the monadic variables to values.
   */
  default Map<String, Mapping<?>> monadic() {
    return Collections.emptyMap();
  }

  /**
   * Returns the map from data source node ids to node indices.
   * 
   * @return map from the node ids in the data source to node indices in this format.
   */
  Map<?, Integer> nodeIds();

  /**
   * Returns the dyadic (link) data.
   * 
   * @return map of the dyadic variables to values.
   */
  default Map<String, Mapping<?>> dyadic() {
    return Collections.emptyMap();
  }
}
