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

package ch.ethz.sn.visone3.lang.impl.containers;

import ch.ethz.sn.visone3.lang.Mappings;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.lang.PrimitiveQueue;

import java.util.NoSuchElementException;

/**
 * FiFo backed by an {@link PrimitiveList}.
 */
class IntQueue implements PrimitiveQueue.OfInt {
  private final PrimitiveList.OfInt queue = Mappings.newIntList(); // manually managed fifo
  private int queueHead = 0;

  @Override
  public void push(final int value) {

    // clean up head part if only half of the list size is used
    if (2 * queueHead > queue.size()) {
      queue.removeRange(0, queueHead);
      queueHead = 0;
    }
    // queue grows according to the list settings
    queue.add(value);
  }

  @Override
  public int peekInt() {
    if (queueHead >= queue.size()) {
      throw new NoSuchElementException();
    }
    return queue.getInt(queueHead);
  }

  @Override
  public int popInt() {
    if (queueHead >= queue.size()) {
      throw new NoSuchElementException();
    }
    // return and move head
    return queue.getInt(queueHead++);
  }

  @Override
  public int size() {
    return queue.size() - queueHead;
  }
}
