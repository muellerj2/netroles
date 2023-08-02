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

package ch.ethz.sn.visone3.io.graphml;

import java.util.Objects;
import java.util.function.Function;

class DataType<T> {

  public static final DataType<Integer> BOOLEAN = new DataType<>("BOOLEAN", int.class,
      (value) -> Boolean.parseBoolean(value) ? 1 : 0);
  public static final DataType<Integer> INT = new DataType<>("INT", int.class, Integer::parseInt);
  public static final DataType<Double> DOUBLE = new DataType<>("DOUBLE", double.class,
      Double::parseDouble);
  public static final DataType<String> STRING = new DataType<>("STRING", String.class,
      String::valueOf);

  private static final DataType<?>[] list = { BOOLEAN, INT, DOUBLE, STRING };

  private final String name;
  private final Class<T> componentType;
  private final Function<String, T> convert;

  private DataType(final String name, final Class<T> componentType,
      final Function<String, T> convert) {
    this.name = name;
    this.componentType = componentType;
    this.convert = convert;
  }

  static DataType<?> getByGraphMlName(final String name) {
    Objects.requireNonNull(name);
    String upper = name.toUpperCase();
    for (DataType<?> val : list) {
      if (upper.equals(val.toString())) {
        return val;
      }
    }
    throw new IllegalArgumentException("unknown GraphML type name `" + name + "'");
  }

  public T convert(final String value) {
    return convert.apply(value);
  }

  public Class<T> getComponentType() {
    return componentType;
  }

  @Override
  public String toString() {
    return name;
  }
}
