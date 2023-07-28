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

package ch.ethz.sn.visone3.lang;

import java.lang.invoke.MethodType;

/**
 * Provides some utilities for class types.
 */
public final class ClassUtils {

  private ClassUtils() {
  }

  /**
   * Converts a primitive to its wrapped type.
   * 
   * @param <T>
   *          the type represented by the class object
   * @param clazz
   *          the class object of the primitive type
   * @return the class object representing the wrapped type
   * @throws IllegalArgumentException
   *           if the specified class does not describe a primitive type
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> primitiveToWrapped(Class<T> clazz) {
    if (!clazz.isPrimitive()) {
      throw new IllegalArgumentException(String.format("Class '%s' not primitive", clazz));
    }
    if (clazz == int.class) {
      return (Class<T>) Integer.class;
    } else if (clazz == double.class) {
      return (Class<T>) Double.class;
    } else if (clazz == long.class) {
      return (Class<T>) Long.class;
    } else if (clazz == boolean.class) {
      return (Class<T>) Boolean.class;
    } else if (clazz == float.class) {
      return (Class<T>) Float.class;
    } else if (clazz == byte.class) {
      return (Class<T>) Byte.class;
    } else if (clazz == char.class) {
      return (Class<T>) Character.class;
    } else if (clazz == short.class) {
      return (Class<T>) Short.class;
    } else if (clazz == void.class) {
      return (Class<T>) Void.class;
    } else { // fallback for unknown primitive types
      return (Class<T>) MethodType.methodType(clazz).wrap().returnType();
    }
  }

  /**
   * Converts a wrapped type to its primitive type.
   * 
   * @param <T>
   *          the wrapped type represented by the class object
   * @param clazz
   *          the class object of the wrapped type
   * @return the class object of the primitive type, or null if the class object does not refer to a
   *         wrapped type
   */
  @SuppressWarnings("unchecked")
  private static <T> Class<T> wrappedToPrimitiveInternal(Class<T> clazz) {
    if (clazz == Integer.class) {
      return (Class<T>) int.class;
    } else if (clazz == Double.class) {
      return (Class<T>) double.class;
    } else if (clazz == Long.class) {
      return (Class<T>) long.class;
    } else if (clazz == Boolean.class) {
      return (Class<T>) boolean.class;
    } else if (clazz == Float.class) {
      return (Class<T>) float.class;
    } else if (clazz == Byte.class) {
      return (Class<T>) byte.class;
    } else if (clazz == Character.class) {
      return (Class<T>) char.class;
    } else if (clazz == Short.class) {
      return (Class<T>) short.class;
    } else if (clazz == Void.class) {
      return (Class<T>) void.class;
    } else {
      Class<T> primitiveType = (Class<T>) MethodType.methodType(clazz).unwrap().returnType();
      return primitiveType.isPrimitive() ? primitiveType : null;
    }
  }

  /**
   * Converts a wrapped type to its primitive type.
   * 
   * @param <T>
   *          the wrapped type represented by the class object
   * @param clazz
   *          the class object of the wrapped type
   * @return the class object of the primitive type
   * @throws IllegalArgumentException
   *           if the class object does not refer to the wrapper class of a primitive type
   */
  public static <T> Class<T> wrappedToPrimitive(Class<T> clazz) {
    Class<T> primitive = !clazz.isPrimitive() ? wrappedToPrimitiveInternal(clazz) : null;
    if (primitive == null) {
      throw new IllegalArgumentException(String.format("Class '%s' not wrapper class", clazz));
    }
    return primitive;
  }

  /**
   * Wraps a primitive type if necessary.
   * 
   * @param <T>
   *          the type the class object is describing
   * @param clazz
   *          the class object
   * @return the class object itself if the class is not primitive, else the primitive type's
   *         wrapper class.
   */
  public static <T> Class<T> wrap(Class<T> clazz) {
    if (clazz.isPrimitive()) {
      return primitiveToWrapped(clazz);
    }
    return clazz;
  }

  /**
   * Unwraps a wrapped type if necessary.
   * 
   * @param <T>
   *          the type the class object is describing
   * @param type
   *          the class object
   * @return the class object itself if the class is not the wrapper class of a primitive type, else
   *         the primitive type.
   */
  public static <T> Class<T> unwrap(Class<T> type) {
    Class<T> primitive = wrappedToPrimitiveInternal(type);
    return primitive != null ? primitive : type;
  }

  // /**
  // * Tries to convert source objects to the target types by unboxing.
  // */
  // public static Object[] unboxIfNeeded(final Object[] array, final Class<?>[] targetTypes) {
  // for (int i = 0; i < array.length; ++i) {
  // final Class<?> targetType = targetTypes[i];
  // final Object sourceObject = array[i];
  // // TODO: handle all primitives
  // if (targetType == double[].class && sourceObject.getClass() == Double[].class) {
  // final Double[] boxedArray = (Double[]) sourceObject;
  // final double[] target = new double[boxedArray.length];
  // for (int j = 0; j < boxedArray.length; ++j) {
  // target[j] = boxedArray[j];
  // }
  // array[i] = target;
  // }
  // }
  // return array;
  // }
}
