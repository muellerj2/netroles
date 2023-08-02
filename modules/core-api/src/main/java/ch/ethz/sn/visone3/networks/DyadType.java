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
 * Represents the type of dyads in a network.
 *
 */
public enum DyadType {
  DIRECTED(true, false), UNDIRECTED(false, false), TWO_MODE(false, true);
  private final boolean isDirected;
  private final boolean isTwoMode;

  /**
   * Returns the appropriate {@code DyadType} for one-mode networks based on the specified
   * directedness of the dyads.
   * 
   * @param directed
   *          true if the dyads are directed, false otherwise
   * @return the appropriate {@code DyadType} for one-mode networks.
   */
  public static DyadType oneMode(final boolean directed) {
    if (directed) {
      return DIRECTED;
    }
    return UNDIRECTED;
  }

  DyadType(final boolean isDirected, final boolean isTwoMode) {
    this.isDirected = isDirected;
    this.isTwoMode = isTwoMode;
  }

  /**
   * Returns true if the dyads are directed.
   * 
   * @return true if directed, false otherwise.
   */
  public boolean isDirected() {
    return isDirected;
  }

  /**
   * Returns true if the dyads represent links between two modes.
   * 
   * @return true if two-mode, false otherwise.
   */
  public boolean isTwoMode() {
    return isTwoMode;
  }
}
