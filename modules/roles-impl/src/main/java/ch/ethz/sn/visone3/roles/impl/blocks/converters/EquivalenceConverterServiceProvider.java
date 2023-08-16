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
package ch.ethz.sn.visone3.roles.impl.blocks.converters;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.roles.blocks.RoleConverter;
import ch.ethz.sn.visone3.roles.spi.ConverterService;
import ch.ethz.sn.visone3.roles.structures.RelationBase;

/**
 * Service making conversion operations producing equivalence outputs available.
 */
public class EquivalenceConverterServiceProvider implements ConverterService {

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public <T, U> RoleConverter<T, U> getConverter(Class<T> source, Class<U> destination,
      Object argument) {
    if (ConstMapping.OfInt.class.equals(destination)) {
      if ("weak".equals(argument) && RelationBase.class.isAssignableFrom(source)) {
        return new EquivalenceFromWeakComponents();
      } else if (argument == null && RelationBase.class.isAssignableFrom(source)) {
        return new EquivalenceFromStrongComponents();
      }
    }
    return null;
  }

}
