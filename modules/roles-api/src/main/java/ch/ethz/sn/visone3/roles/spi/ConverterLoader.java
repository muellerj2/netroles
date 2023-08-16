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
package ch.ethz.sn.visone3.roles.spi;

import java.util.ServiceLoader;

import ch.ethz.sn.visone3.roles.blocks.RoleConverter;

/**
 * Provides access to the services registering convertion operators between
 * different kinds of structures.
 */
public class ConverterLoader {

  private ConverterLoader() {
    loader = ServiceLoader.load(ConverterService.class);
  }

  private static final ConverterLoader INSTANCE = new ConverterLoader();
  private ServiceLoader<ConverterService> loader;

  /**
   * Gets the singleton instance of the loader.
   * 
   * @return the singleton instance.
   */
  public static ConverterLoader getInstance() {
    return INSTANCE;
  }

  /**
   * Tries to find a role converter between the supplied source and destination
   * type, or returns {@code null} if no such converter is offered by any
   * registered service.
   * 
   * @param <T>             the source type.
   * @param <U>             the destination role structure type.
   * @param sourceType      class object representing the source type.
   * @param destinationType class object representing the destination role
   *                        structure type.
   * @param argument        optional argument identifying the kind of conversion
   *                        operator between these two types.
   * @return a role converter compatible with these types and the optional
   *         argument if such a converter is offered by any registered service,
   *         otherwise {@code null}.
   */
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

  /**
   * Returns a role converter between the supplied source and destination type.
   * 
   * @param <T>             the source type.
   * @param <U>             the destination role structure type.
   * @param sourceType      class object representing the source type.
   * @param destinationType class object representing the destination role
   *                        structure type.
   * @param argument        optional argument identifying the kind of conversion
   *                        operator between these two types.
   * @return a role converter compatible with these types and the optional
   *         argument.
   * @throws UnsupportedOperationException if no registered service offers a role
   *                                       converter compatible with the source
   *                                       and destination types plus the
   *                                       operational argument.
   */
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
