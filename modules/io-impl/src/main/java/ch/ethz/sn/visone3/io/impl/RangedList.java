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

import ch.ethz.sn.visone3.io.Source.Range;
import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveList;

/**
 * Represents a list backed by a converting {@link Range}.
 * 
 * @param <T> target type of range.
 */
public class RangedList<T> {
  Range<T> range;
  PrimitiveList<T> list;

  /**
   * Constructs a new ranged list.
   * 
   * @param range the backing range.
   */
  public RangedList(Range<T> range) {
    this.range = range;
    this.list = this.range != null ? Mappings.newList(range.componentType()) : null;
  }

  /**
   * Constructs a new ranged list of the specified size.
   * 
   * @param range the backing range.
   * @param size  the (initial) size of the list.
   */
  public RangedList(Range<T> range, int size) {
    this.range = range;
    this.list = Mappings.newListOfSize(range.componentType(), size);
  }

  /**
   * Returns the list.
   * 
   * @return the list.
   */
  public PrimitiveList<T> getList() {
    return list;
  }

  /**
   * Sets the value of the list at the specified index.
   * 
   * @param index the index.
   * @param value the string value to convert to the target type and put at the
   *              specified index.
   */
  public void setListAt(int index, String value) {
    list.set(index, range.apply(value));
  }

  /**
   * Adds a value to the list.
   * 
   * @param value the string value to convert to the target type and add at the
   *              end of the list.
   */
  public void addToList(String value) {
    list.add(range.apply(value));
  }

  @Override
  public String toString() {
    return "RangedList [ " + (range != null ? range.toString() : "(null)") + ", "
        + (list != null ? list.toString() : "(null)") + "]";
  }
}