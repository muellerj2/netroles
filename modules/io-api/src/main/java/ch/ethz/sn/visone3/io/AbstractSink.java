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

package ch.ethz.sn.visone3.io;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.networks.Network;

/**
 * Convenience base class for sinks, implementing some methods by throwing
 * UnsupportedOperationException.
 *
 */
public abstract class AbstractSink implements Sink {

  @Override
  public void incidence(final Network network) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void global(final String name, final Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> void node(final String name, final T def, final ConstMapping<T> monadic) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> void link(final String name, final T def, final ConstMapping<T> dyadic) {
    throw new UnsupportedOperationException();
  }
}
