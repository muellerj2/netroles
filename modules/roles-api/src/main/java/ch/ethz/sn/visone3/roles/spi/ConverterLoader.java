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
package ch.ethz.sn.visone3.roles.spi;

import ch.ethz.sn.visone3.roles.blocks.RoleConverter;

import java.util.ServiceLoader;

public class ConverterLoader {

  private ConverterLoader() {
    loader = ServiceLoader.load(ConverterService.class);
  }

  private static final ConverterLoader INSTANCE = new ConverterLoader();
  private ServiceLoader<ConverterService> loader;

  public static ConverterLoader getInstance() {
    return INSTANCE;
  }

  public <T, U> RoleConverter<T, U> tryGetConverter(Class<T> sourceType, Class<U> destinationType,
      Object argument) {
    for (ConverterService service : loader) {
      RoleConverter<T, U> result = service.getConverter(sourceType, destinationType, argument);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  public <T, U> RoleConverter<T, U> getConverter(Class<T> sourceType, Class<U> destinationType,
      Object argument) {
    RoleConverter<T, U> result = tryGetConverter(sourceType, destinationType, argument);
    if (result != null) {
      return result;
    }
    throw new UnsupportedOperationException(
        "No converter for types " + sourceType + " -> " + destinationType + " (argument " + argument
            + ") provided by any service");
  }

}
