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
package ch.ethz.sn.visone3.roles.impl.blocks.reducers.eq;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.roles.blocks.Reducers;
import ch.ethz.sn.visone3.roles.blocks.Reducers.Factory;
import ch.ethz.sn.visone3.roles.blocks.RoleReducer;
import ch.ethz.sn.visone3.roles.spi.ReducerFactoryService;

public class EquivalenceReducerFactoryServiceProvider implements ReducerFactoryService {

  @SuppressWarnings("unchecked")
  @Override
  public <T> Factory<T> getFactory(Class<T> structureType) {
    if (ConstMapping.OfInt.class.equals(structureType)) {
      return (Factory<T>) new Reducers.Factory<ConstMapping.OfInt>() {

        @Override
        public RoleReducer<ConstMapping.OfInt> join() {
          return new EquivalenceJoin();
        }

        @Override
        public RoleReducer<ConstMapping.OfInt> meet() {
          return new EquivalenceMeet();
        }

      };
    }
    return null;
  }

}
