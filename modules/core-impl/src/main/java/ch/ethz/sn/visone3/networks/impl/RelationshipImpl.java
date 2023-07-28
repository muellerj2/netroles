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
 * along with visone3.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.ethz.sn.visone3.networks.impl;

import ch.ethz.sn.visone3.networks.Relationship;

/**
 * Default relationship implementation.
 */
public class RelationshipImpl implements Relationship {
  private final int index;
  private final int left;
  private final int right;
  private final int right0;

  /**
   * Constructs a relationship representation.
   * 
   * @param index
   *          the (storage) index of the relationship.
   * @param left
   *          the left element of the relationship.
   * @param right
   *          the right element of the relationship.
   */
  public RelationshipImpl(final int index, final int left, final int right) {
    this.index = index;
    this.left = left;
    this.right = right;
    right0 = right;
  }

  /**
   * Constructs a relationship representation.
   * 
   * @param index
   *          the (storage) index of the relationship.
   * @param left
   *          the left element of the relationship.
   * @param right
   *          the right element of the relationship.
   * @param right0
   *          the 0-based right element of the relationship.
   */
  public RelationshipImpl(final int index, final int left, final int right, final int right0) {
    this.index = index;
    this.left = left;
    this.right = right;
    this.right0 = right0;
  }

  @Override
  public int getIndex() {
    return index;
  }

  @Override
  public int getLeft() {
    return left;
  }

  @Override
  public int getRight() {
    return right;
  }

  @Override
  public int getRight0() {
    return right0;
  }

  @Override
  public String toString() {
    return String.format("((%d,%d(%d)),[%d])", left, right, right0, index);
  }
}
