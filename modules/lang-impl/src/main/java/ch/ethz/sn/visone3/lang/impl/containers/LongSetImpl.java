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

package ch.ethz.sn.visone3.lang.impl.containers;

import ch.ethz.sn.visone3.lang.LongMap;
import ch.ethz.sn.visone3.lang.LongSet;

import java.util.function.Supplier;

/**
 * Set backed by a {@link LongMap}.
 */
class LongSetImpl implements LongSet {
  private static final Object NULL = new Object();
  private final LongMap<Object> map;

  public LongSetImpl(final Supplier<LongMap<Object>> map) {
    this.map = map.get();
  }

  @Override
  public void add(final long key) {
    map.put(key, NULL);
  }

  @Override
  public void remove(final long key) {
    map.remove(key);
  }

  @Override
  public boolean contains(final long key) {
    return map.contains(key);
  }

  @Override
  public int size() {
    return map.size();
  }
}
