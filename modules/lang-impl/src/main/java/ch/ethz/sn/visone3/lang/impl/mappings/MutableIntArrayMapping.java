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
package ch.ethz.sn.visone3.lang.impl.mappings;

import ch.ethz.sn.visone3.lang.Mapping;

final class MutableIntArrayMapping extends ConstIntArrayMapping implements Mapping.OfInt {
  private static final long serialVersionUID = -934936080580088616L;

  public MutableIntArrayMapping(final int[] array) {
    super(array);
  }

  @Override
  public Integer set(int index, Integer value) {
    int size = this.size();
    if (index >= size) {
      throw new IndexOutOfBoundsException(index + " >= " + size);
    }
    return setInt(index, unboxWithNull(value));
  }

  @Override
  public int setInt(int index, int value) {
    int size = this.size();
    if (index < size) {
      int old = array[index];
      array[index] = value;
      return old;
    } else {
      throw new IndexOutOfBoundsException(index + " >= " + size);
    }
  }

  @Override
  public int[] array() {
    return array;
  }

  private int unboxWithNull(final Integer value) {
    if (value != null) {
      return value.intValue();
    }
    return IntArrayList.NULL;
  }

}
