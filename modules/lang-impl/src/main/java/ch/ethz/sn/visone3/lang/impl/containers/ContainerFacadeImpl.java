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

import ch.ethz.sn.visone3.lang.IntDoubleHeap;
import ch.ethz.sn.visone3.lang.LongMap;
import ch.ethz.sn.visone3.lang.LongSet;
import ch.ethz.sn.visone3.lang.PrimitiveQueue;
import ch.ethz.sn.visone3.lang.spi.ContainersFacade;

/**
 * Facade to implemented containers.
 */
public class ContainerFacadeImpl implements ContainersFacade {

  @Override
  public <T> LongMap<T> longHashMap() {
    return new LongHashMap<>();
  }

  @Override
  public <T> LongMap<T> longHashMap(int initialCapacity) {
    return new LongHashMap<>(initialCapacity);
  }

  @Override
  public <T> LongMap<T> longTreeMap() {
    return new LongTreeMap<>();
  }

  @Override
  public <T> LongMap<T> longTreeMap(int minDegree) {
    return new LongTreeMap<>(minDegree);
  }

  @Override
  public LongSet longTreeSet() {
    return new LongSetImpl(LongTreeMap::new);
  }

  @Override
  public LongSet longHashSet() {
    return new LongSetImpl(LongHashMap::new);
  }

  @Override
  public PrimitiveQueue.OfInt intQueue() {
    return new IntQueue();
  }

  @Override
  public IntDoubleHeap fixedUniverseIntDoubleMinHeap(int size) {
    return new FixedUniverseMinHeap(size);
  }

}
