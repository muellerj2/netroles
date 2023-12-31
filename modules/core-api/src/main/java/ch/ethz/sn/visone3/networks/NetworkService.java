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
package ch.ethz.sn.visone3.networks;

/**
 * Interface that a service providing builders for networks must implement.
 *
 */
public interface NetworkService {

  /**
   * The name of this service.
   * 
   * @return the service name
   */
  String getName();

  /**
   * Returns whether this service provides a builder to represent network data compatible with this
   * dyad type.
   * 
   * @param type
   *          the dyad type
   * @return true if the service provides a builder for this type, false otherwise.
   */
  boolean supports(DyadType type);

  /**
   * Produces a new builder for the specified dyad type.
   * 
   * @param type
   *          the dyad type.
   * @return the builder compatible with this dyad type.
   */
  NetworkBuilder createBuilder(DyadType type);
}
