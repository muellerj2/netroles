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

package ch.ethz.sn.visone3.roles.impl.algorithms;

/**
 * This class provides a pool of colors that can be allocated and freed as needed.
 *
 * <p>
 * Note that the class methods do no error checking at all.
 *
 * @author muellerj
 */
class FixedSizeSlotAllocator {

  // the range [freeindex, freelist.length) contains all currently unused
  // colors in the pool
  private final int[] freelist;
  private int freeindex;

  /**
   * Constructs a fixed size allocator for slots in the range [0,nSlots). It assumes that at least
   * minSlotsInUse slots are always allocated. To this end, the slots in the range [0,minSlotsInUse)
   * are immediately allocated during construction.
   *
   * @param nSlots
   *          the total number of slots in the pool
   * @param minSlotsInUse
   *          minimum number of colors in use
   */
  public FixedSizeSlotAllocator(final int nSlots, final int minSlotsInUse) {
    freelist = new int[nSlots - minSlotsInUse];
    for (int i = 0; i < freelist.length; ++i) {
      freelist[i] = i + minSlotsInUse;
    }
    freeindex = 0;
  }

  /**
   * Allocates a free slot.
   *
   * @return the free slot
   */
  public int allocate() {
    return freelist[freeindex++];
  }

  /**
   * Releases a slot into the free slot pool.
   *
   * @param slot
   *          the slot to be freed
   */
  public void free(final int slot) {
    freelist[--freeindex] = slot;
  }
}