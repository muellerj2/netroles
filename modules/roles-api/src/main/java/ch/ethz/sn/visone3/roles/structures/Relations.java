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
package ch.ethz.sn.visone3.roles.structures;

/**
 * This class provides some utility methods for binary relations and rankings.
 *
 */
public class Relations {

  private Relations() {

  }

  /**
   * Determines whether two relations have the same domains and contain the same
   * pairs.
   * 
   * @param lhs the first relation.
   * @param rhs the second relation
   * @return true if the two relations have the same domains and contain the same
   *         pairs, otherwise false.
   */
  public static boolean equals(RelationBase lhs, RelationBase rhs) {
    if (lhs == rhs) {
      return true;
    }
    if (lhs == null || rhs == null) {
      return false;
    }
    int n = lhs.domainSize();
    if (n != rhs.domainSize()) {
      return false;
    }
    for (int i = 0; i < n; ++i) {
      for (int j = 0; j < n; ++j) {
        if (lhs.contains(i, j) != rhs.contains(i, j)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Determines the hash code of a binary relation.
   * 
   * @param relation the relation.
   * @return the hash code.
   */
  public static int hashCode(RelationBase relation) {
    final int existing = Boolean.hashCode(true);
    final int nonexisting = Boolean.hashCode(false);
    int hash = 0;
    int n = relation.domainSize();
    for (int i = 0; i < n; ++i) {
      for (int j = 0; j < n; ++j) {
        hash = 31 * hash + (relation.contains(i, j) ? existing : nonexisting);
      }
    }
    return hash;
  }

  /**
   * Produces a string representation of a relation.
   * 
   * @param relation the relation.
   * @return the string representation.
   */
  public static String toString(RelationBase relation) {
    StringBuilder builder = new StringBuilder();
    final int size = relation.domainSize();
    builder.append("{domain size=");
    builder.append(size);
    builder.append(", [");
    if (size > 0) {
      builder.append('[');
    }
    for (int i = 0; i < size; ++i) {
      if (i > 0) {
        builder.append("], [");
      }
      for (int j = 0; j < size; ++j) {
        if (j > 0) {
          builder.append(",");
        }
        builder.append(relation.contains(i, j) ? '1' : '0');
      }
    }
    if (size > 0) {
      builder.append(']');
    }
    builder.append("]}");
    return builder.toString();
  }
}
