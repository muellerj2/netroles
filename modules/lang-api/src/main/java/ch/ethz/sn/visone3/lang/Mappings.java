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

package ch.ethz.sn.visone3.lang;

import ch.ethz.sn.visone3.lang.spi.LangProvider;
import ch.ethz.sn.visone3.lang.spi.MappingsFacade;

/**
 * Provides static methods to create and cast some common representations for mappings and primitive
 * lists.
 *
 */
public final class Mappings {

  private Mappings() {
  }

  private static MappingsFacade facade() {
    return LangProvider.getInstance().mappings();
  }

  /**
   * Create a new list of (unboxed) integers.
   *
   * @return A primitive array list instance for integers
   */
  public static PrimitiveList.OfInt newIntList() {
    return facade().newIntList();
  }

  /**
   * Create a new list of (unboxed) integers with specified initial capacity.
   *
   * @param capacity
   *          Initial capacity of the list
   * @return A primitive array list instance for integers
   */
  public static PrimitiveList.OfInt newIntList(int capacity) {
    return facade().newIntList(capacity);
  }

  /**
   * Create a new list of (unboxed) integers, filled with the specified number of a given initial
   * value.
   *
   * @param value
   *          Initial value
   * @param size
   *          Initial size
   * @return A primitive array list instance for integers
   */
  public static PrimitiveList.OfInt newIntList(int value, int size) {
    return facade().newIntList(value, size);
  }

  /**
   * Create a new list of (unboxed) integers from the given values.
   * 
   * <p>
   * Note: The list is allowed to reuse the storage of the passed array.
   *
   * @param values
   *          Initial values
   * @return A primitive array list instance for integers
   */
  public static PrimitiveList.OfInt newIntList(int[] values) {
    return facade().newIntList(values);
  }

  /**
   * Create a new list of (unboxed) integers, filled with the specified number of zeros.
   *
   * @param size
   *          Initial size
   * @return A primitive array list instance for integers
   */
  public static PrimitiveList.OfInt newIntListOfSize(int size) {
    return facade().newIntListOfSize(size);
  }

  /**
   * Create a new list of (unboxed) longs.
   *
   * @return A primitive array list instance for longs
   */
  public static PrimitiveList.OfLong newLongList() {
    return facade().newLongList();
  }

  /**
   * Create a new list of (unboxed) longs with specified initial capacity.
   *
   * @param capacity
   *          Initial capacity of the list
   * @return A primitive array list instance for longs
   */
  public static PrimitiveList.OfLong newLongList(int capacity) {
    return facade().newLongList(capacity);
  }

  /**
   * Create a new list of (unboxed) longs, filled with the specified number of a given initial
   * value.
   *
   * @param value
   *          Initial value
   * @param size
   *          Initial size
   * @return A primitive array list instance for longs
   */
  public static PrimitiveList.OfLong newLongList(long value, int size) {
    return facade().newLongList(value, size);
  }

  /**
   * Create a new list of (unboxed) longs from the given values.
   * 
   * <p>
   * Note: The list is allowed to reuse the storage of the passed array.
   *
   * @param values
   *          Initial values
   * @return A primitive array list instance for longs
   */
  public static PrimitiveList.OfLong newLongList(long[] values) {
    return facade().newLongList(values);
  }

  /**
   * Create a new list of (unboxed) longs, filled with the specified number of zeros.
   *
   * @param size
   *          Initial size
   * @return A primitive array list instance for longs
   */
  public static PrimitiveList.OfLong newLongListOfSize(int size) {
    return facade().newLongListOfSize(size);
  }

  /**
   * Create a new list of (unboxed) doubles.
   *
   * @return A primitive array list instance for doubles
   */
  public static PrimitiveList.OfDouble newDoubleList() {
    return facade().newDoubleList();
  }

  /**
   * Create a new list of (unboxed) doubles with specified initial capacity.
   *
   * @param capacity
   *          Initial capacity of the list
   * @return A primitive array list instance for doubles
   */
  public static PrimitiveList.OfDouble newDoubleList(int capacity) {
    return facade().newDoubleList(capacity);
  }

  /**
   * Create a new list of (unboxed) doubles, filled with the specified number of a given initial
   * value.
   *
   * @param value
   *          Initial value
   * @param size
   *          Initial size
   * @return A primitive array list instance for doubles
   */
  public static PrimitiveList.OfDouble newDoubleList(double value, int size) {
    return facade().newDoubleList(value, size);
  }

  /**
   * Create a new list of (unboxed) doubles from the given values.
   * 
   * <p>
   * Note: The list is allowed to reuse the storage of the passed array.
   *
   * @param values
   *          Initial values
   * @return A primitive array list instance for doubles
   */
  public static PrimitiveList.OfDouble newDoubleList(double[] values) {
    return facade().newDoubleList(values);
  }

  /**
   * Create a new list of (unboxed) doubles, filled with the specified number of zeros.
   *
   * @param size
   *          Initial size
   * @return A primitive array list instance for doubles
   */
  public static PrimitiveList.OfDouble newDoubleListOfSize(int size) {
    return facade().newDoubleListOfSize(size);
  }

  /**
   * Create an empty primitive list for the given component type.
   *
   * @param componentType class object representing the value/component type of
   *                      the list.
   * @param <T>           value type.
   * @return A primitive array list instance if {@code componentType} is a
   *         reference type or a supported primitive type.
   * @throws UnsupportedOperationException if the component type is a primitive
   *                                       type for which no specialized primitive
   *                                       list is available (i.e., throws for all
   *                                       primitive types but int, boolean and
   *                                       double)
   */
  public static <T> PrimitiveList<T> newList(Class<T> componentType) {
    return facade().newList(componentType);
  }

  /**
   * Create an empty primitive list with specified capacity for the given
   * component type.
   *
   * @param componentType class object representing the value/component type of
   *                      the list.
   * @param capacity      initial capacity.
   * @param <T>           value type.
   * @return A primitive array list instance if {@code componentType} is a
   *         reference type or a supported primitive type.
   * @throws UnsupportedOperationException if the component type is a primitive
   *                                       type for which no specialized primitive
   *                                       list is available (i.e., throws for all
   *                                       primitive types but int, boolean and
   *                                       double)
   */
  public static <T> PrimitiveList<T> newList(Class<T> componentType, int capacity) {
    return facade().newList(componentType, capacity);
  }

  /**
   * Create a new primitive list for the given component type, filled with the
   * specified number of a given initial value.
   *
   * @param componentType class object representing the value/component type of
   *                      the list.
   * @param value         initial value.
   * @param size          initial size of the list with all elements initialized
   *                      to {@code value}.
   * @param <T>           value type.
   * @return A primitive array list instance if {@code componentType} is a
   *         reference type or a supported primitive type.
   * @throws UnsupportedOperationException if the component type is a primitive
   *                                       type for which no specialized primitive
   *                                       list is available (i.e., throws for all
   *                                       primitive types but int, boolean and
   *                                       double)
   */
  public static <T> PrimitiveList<T> newList(Class<T> componentType, T value, int size) {
    return facade().newList(componentType, value, size);
  }

  /**
   * Create a new primitive list for the given component type from the given
   * values.
   * 
   * <p>
   * Note: The list is allowed to reuse the storage of the passed array.
   *
   * @param componentType class object representing the value/component type of
   *                      the list.
   * @param values        initial values.
   * @param <T>           value type.
   * @return A primitive array list instance if {@code componentType} is a
   *         reference type or a supported primitive type.
   * @throws UnsupportedOperationException if the component type is a primitive
   *                                       type (because automatic unboxing is not
   *                                       provided)
   */
  public static <T> PrimitiveList<T> newList(Class<T> componentType, T[] values) {
    return facade().newList(componentType, values);
  }

  /**
   * Create a new primitive list for the given component type from the given
   * values in the {@code array} object.
   * 
   * <p>
   * Note: The list is allowed to reuse the storage of the passed array.
   *
   * @param componentType class object representing the value/component type of
   *                      the list.
   * @param array         An array object containing the initial values.
   * @param <T>           value type.
   * @return A primitive array list instance if {@code componentType} is a
   *         reference type or a supported primitive type.
   * @throws UnsupportedOperationException if the component type is a primitive
   *                                       type for which no specialized primitive
   *                                       list is available (i.e., throws for all
   *                                       primitive types but int, boolean and
   *                                       double)
   */
  public static <T> PrimitiveList<T> newListFromArray(Class<T> componentType, Object array) {
    return facade().newListFromArray(componentType, array);
  }

  /**
   * Create a new primitive list for the given component type, filled with the
   * specified number of the component type's default value.
   *
   * @param componentType class object representing the value/component type of
   *                      the list.
   * @param size          initial size.
   * @param <T>           value type.
   * @return A primitive array list instance of the specified size filled with the
   *         value type's default file if {@code componentType} is a reference
   *         type or a supported primitive type.
   * @throws UnsupportedOperationException if the component type is a primitive
   *                                       type for which no specialized primitive
   *                                       list is available (i.e., throws for all
   *                                       primitive types but int, boolean and
   *                                       double)
   */
  public static <T> PrimitiveList<T> newListOfSize(Class<T> componentType, int size) {
    return facade().newListOfSize(componentType, size);
  }

  /**
   * Create an empty primitive list for the given component type, or its wrapper
   * type if the component type is primitive and no specialized primitive list is
   * available for the specified primitive component type.
   *
   * @param componentType class object representing the value/component type of
   *                      the list.
   * @param <T>           value type.
   * @return A primitive array list instance.
   */
  public static <T> PrimitiveList<T> newListAutoboxing(Class<T> componentType) {
    return facade().newListAutoboxing(componentType);
  }

  /**
   * Create an empty primitive list with the specified initial capacity for the
   * given component type, or its wrapper type if the component type is primitive
   * and no specialized primitive list is available for the specified primitive
   * component type.
   *
   * @param componentType class object representing the value/component type of
   *                      the list.
   * @param capacity      initial capacity of the list.
   * @param <T>           value type.
   * @return A primitive array list instance.
   */
  public static <T> PrimitiveList<T> newListAutoboxing(Class<T> componentType, int capacity) {
    return facade().newListAutoboxing(componentType, capacity);
  }

  /**
   * Create a new primitive list for the given component type, or its wrapper type
   * if the component type is primitive and no specialized primitive list is
   * available for the specified primitive component type. The primitive list is
   * filled with the specified number of the given initial value.
   *
   * @param componentType class object representing the value/component type of
   *                      the list.
   * @param value         initial value.
   * @param size          initial size.
   * @param <T>           value type.
   * @return A primitive array list instance of the specified size with all
   *         elements initialized to {@code value}.
   */
  public static <T> PrimitiveList<T> newListAutoboxing(Class<T> componentType, T value, int size) {
    return facade().newListAutoboxing(componentType, value, size);
  }

  /**
   * Create a new primitive list for the given component type, or its wrapper type
   * if the component type is primitive and no specialized primitive list is
   * available for the specified primitive component type. The primitive list is
   * filled with the specified number of the list's component type's default
   * value, i.e.:
   * <ul>
   * <li>If {@code componentType} is a supported primitive type, fill it with
   * zeros, that type's default value</li>
   * <li>If {@code componentType} is a reference type, fill it with nulls, a
   * reference type's default value</li>
   * <li>If {@code componentType} is an unsupported primitive type, fill it with
   * nulls, the wrapper type's default value
   * </ul>
   * 
   * @param componentType class object representing the value/component type of
   *                      the list.
   * @param size          initial size
   * @param <T>           value type.
   * @return A primitive array list instance of the specified size with elements
   *         initialized as described.
   */
  public static <T> PrimitiveList<T> newListOfSizeAutoboxing(Class<T> componentType, int size) {
    return facade().newListOfSizeAutoboxing(componentType, size);
  }

  /**
   * Create a new list of (unboxed) integers from the given values.
   * 
   * <p>
   * Note: The list is allowed to reuse the storage of the passed array.
   *
   * @param array
   *          Initial values
   * @return A primitive array list instance for integers
   */
  public static PrimitiveList.OfInt newIntListFrom(final int... array) {
    return newIntList(array);
  }

  /**
   * Create a new list of (unboxed) doubles from the given values.
   * 
   * <p>
   * Note: The list is allowed to reuse the storage of the passed array.
   *
   * @param array
   *          Initial values
   * @return A primitive array list instance for integers
   */
  public static PrimitiveList.OfDouble newDoubleListFrom(final double... array) {
    return newDoubleList(array);
  }

  /**
   * Create a new list of (unboxed) longs from the given values.
   * 
   * <p>
   * Note: The list is allowed to reuse the storage of the passed array.
   *
   * @param array
   *          Initial values
   * @return A primitive array list instance for longs
   */
  public static PrimitiveList.OfLong newLongListFrom(final long... array) {
    return newLongList(array);
  }

  /**
   * Create a new primitive list for the given component type from the given
   * values.
   * 
   * <p>
   * Note: The list is allowed to reuse the storage of the passed array.
   *
   * @param componentType class object representing the value/component type of
   *                      the list.
   * @param array         initial values.
   * @param <T>           value type.
   * @return A primitive array list instance if {@code componentType} is a
   *         reference type or a supported primitive type.
   * @throws UnsupportedOperationException if the component type is a primitive
   *                                       type (because automatic unboxing is not
   *                                       provided)
   */
  @SafeVarargs
  public static <T> PrimitiveList<T> newListFrom(Class<T> componentType, final T... array) {
    return newList(componentType, array);
  }

  /**
   * Create a constant mapping that represents an integer range.
   * 
   * @param begin
   *          Start of integer range
   * @param end
   *          End (exlusive) of integer range
   * @return An integer constant mapping representing the range
   */
  public static ConstMapping.OfInt intRange(int begin, int end) {
    return facade().intRange(begin, end);
  }

  /**
   * Create a mutable wrapper around an array of integer values.
   * 
   * @param values
   *          The values
   * @return A mutable integer mapping that provides a view on the supplied array
   */
  public static Mapping.OfInt wrapModifiableInt(int... values) {
    return facade().wrapModifiableInt(values);
  }

  /**
   * Create a constant wrapper of integer values.
   * 
   * @param values
   *          The values
   * @return A constant integer mapping that provides a view on the supplied array
   */
  public static ConstMapping.OfInt wrapUnmodifiableInt(int... values) {
    return facade().wrapUnmodifiableInt(values);
  }

  /**
   * Create a constant mapping view of an array range.
   * 
   * @param array
   *          The array
   * @param begin
   *          The start index of the array range
   * @param end
   *          The end index (exclusive) of the array range
   * @return A constant integer mapping providing a view on the supplied array range
   */
  public static ConstMapping.OfInt wrapUnmodifiable(int[] array, int begin, int end) {
    return facade().wrapUnmodifiable(array, begin, end);
  }

  /**
   * Create a constant mapping representing several copies of the same value.
   * 
   * @param value
   *          The value
   * @param count
   *          The number of copies
   * @return A constant integer mapping representing {@code count} copies of {@code value}
   */
  public static ConstMapping.OfInt repeated(int value, int count) {
    return facade().repeated(value, count);
  }

  /**
   * Casts a mapping based on its element type in a type-safe way, throwing if the cast is
   * impermissible. This cast is suitable for accessing elements of the mapping.
   * 
   * @param <T>
   *          the base class of the element type
   * @param clazz
   *          base class description to cast to
   * @param mapping
   *          the mapping to cast
   * @return the cast mapping
   * @throws ClassCastException
   *           if (wrapped) {@code <T>} is not a base class of the (wrapped) component type
   */
  public static <T> ConstMapping<? extends T> cast(Class<T> clazz, ConstMapping<?> mapping) {
    if (mapping == null) {
      return null;
    }
    Class<T> destClass = ClassUtils.wrap(clazz);
    Class<?> sourceClass = ClassUtils.wrap(mapping.getComponentType());
    if (destClass.isAssignableFrom(sourceClass)) {
      @SuppressWarnings("unchecked")
      ConstMapping<? extends T> result = (ConstMapping<? extends T>) mapping;
      return result;
    }
    throw new ClassCastException(
        "Cannot cast ch.ethz.sn.lang.ConstMapping<" + sourceClass.getTypeName()
            + "> to ch.ethz.sn.lang.ConstMapping<? extends " + destClass.getTypeName() + ">");
  }

  /**
   * Casts a mapping based on its element type in a type-safe way, throwing if the cast is
   * impermissible. This cast is suitable for accessing elements of the mapping.
   * 
   * @param <T>
   *          the base class of the element type
   * @param clazz
   *          base class description to cast to
   * @param mapping
   *          the mapping to cast
   * @return the cast mapping
   * @throws ClassCastException
   *           if (wrapped) {@code <T>} is not a base class of the (wrapped) component type
   */
  public static <T> Mapping<? extends T> cast(Class<T> clazz, Mapping<?> mapping) {
    if (mapping == null) {
      return null;
    }
    Class<T> destClass = ClassUtils.wrap(clazz);
    Class<?> sourceClass = ClassUtils.wrap(mapping.getComponentType());
    if (destClass.isAssignableFrom(sourceClass)) {
      @SuppressWarnings("unchecked")
      Mapping<? extends T> result = (Mapping<? extends T>) mapping;
      return result;
    }
    throw new ClassCastException("Cannot cast ch.ethz.sn.lang.Mapping<" + sourceClass.getTypeName()
        + "> to ch.ethz.sn.lang.Mapping<? extends " + destClass.getTypeName() + ">");
  }

  /**
   * Casts a list based on its element type in a type-safe way, throwing if the cast is
   * impermissible. This cast is suitable for accessing elements of the mapping.
   * 
   * @param <T>
   *          the base class of the element type
   * @param clazz
   *          base class description to cast to
   * @param mapping
   *          the list to cast
   * @return the cast list
   * @throws ClassCastException
   *           if (wrapped) {@code <T>} is not a base class of the (wrapped) component type
   */
  public static <T> PrimitiveList<? extends T> cast(Class<T> clazz, PrimitiveList<?> mapping) {
    if (mapping == null) {
      return null;
    }
    Class<T> destClass = ClassUtils.wrap(clazz);
    Class<?> sourceClass = ClassUtils.wrap(mapping.getComponentType());
    if (destClass.isAssignableFrom(sourceClass)) {
      @SuppressWarnings("unchecked")
      PrimitiveList<? extends T> result = (PrimitiveList<? extends T>) mapping;
      return result;
    }
    throw new ClassCastException(
        "Cannot cast ch.ethz.sn.lang.PrimitiveList<" + sourceClass.getTypeName()
            + "> to ch.ethz.sn.lang.PrimitiveList<? extends " + destClass.getTypeName() + ">");
  }

  /**
   * Casts a mapping based on its element type in a type-safe way, throwing if the cast is
   * impermissible. Since this casts to the element type exactly, it is suitable for accessing and
   * writing elements of the mapping.
   * 
   * @param <T>
   *          the class of the element type
   * @param clazz
   *          description of the class of the element type to cast to
   * @param mapping
   *          the mapping to cast
   * @return the cast mapping
   * @throws ClassCastException
   *           if (wrapped) {@code <T>} is not equal to the (wrapped) component type
   */
  public static <T> ConstMapping<T> castExact(Class<T> clazz, ConstMapping<?> mapping) {
    if (mapping == null) {
      return null;
    }
    Class<T> destClass = ClassUtils.wrap(clazz);
    Class<?> sourceClass = ClassUtils.wrap(mapping.getComponentType());
    if (destClass == sourceClass) {
      @SuppressWarnings("unchecked")
      ConstMapping<T> result = (ConstMapping<T>) mapping;
      return result;
    }
    throw new ClassCastException(
        "Cannot cast ch.ethz.sn.lang.ConstMapping<" + sourceClass.getTypeName()
            + "> to ch.ethz.sn.lang.ConstMapping<" + destClass.getTypeName() + ">");
  }

  /**
   * Casts a mapping based on its element type in a type-safe way, throwing if the cast is
   * impermissible. Since this casts to the element type exactly, it is suitable for accessing and
   * writing elements of the mapping.
   * 
   * @param <T>
   *          the class of the element type
   * @param clazz
   *          description of the class of the element type to cast to
   * @param mapping
   *          the mapping to cast
   * @return the cast mapping
   * @throws ClassCastException
   *           if (wrapped) {@code <T>} is not equal to the (wrapped) component type
   */
  public static <T> Mapping<T> castExact(Class<T> clazz, Mapping<?> mapping) {
    if (mapping == null) {
      return null;
    }
    Class<T> destClass = ClassUtils.wrap(clazz);
    Class<?> sourceClass = ClassUtils.wrap(mapping.getComponentType());
    if (destClass == sourceClass) {
      @SuppressWarnings("unchecked")
      Mapping<T> result = (Mapping<T>) mapping;
      return result;
    }
    throw new ClassCastException("Cannot cast ch.ethz.sn.lang.Mapping<" + sourceClass.getTypeName()
        + "> to ch.ethz.sn.lang.Mapping<" + destClass.getTypeName() + ">");
  }

  /**
   * Casts a list based on its element type in a type-safe way, throwing if the cast is
   * impermissible. Since this casts to the element type exactly, it is suitable for accessing and
   * writing elements of the list.
   * 
   * @param <T>
   *          the class of the element type
   * @param clazz
   *          description of the class of the element type to cast to
   * @param mapping
   *          the list to cast
   * @return the cast list
   * @throws ClassCastException
   *           if (wrapped) {@code <T>} is not equal to the (wrapped) component type
   */
  public static <T> PrimitiveList<T> castExact(Class<T> clazz, PrimitiveList<?> mapping) {
    if (mapping == null) {
      return null;
    }
    Class<T> destClass = ClassUtils.wrap(clazz);
    Class<?> sourceClass = ClassUtils.wrap(mapping.getComponentType());
    if (destClass == sourceClass) {
      @SuppressWarnings("unchecked")
      PrimitiveList<T> result = (PrimitiveList<T>) mapping;
      return result;
    }
    throw new ClassCastException(
        "Cannot cast ch.ethz.sn.lang.PrimitiveList<" + sourceClass.getTypeName()
            + "> to ch.ethz.sn.lang.PrimitiveList<" + destClass.getTypeName() + ">");
  }

  /**
   * Casts a mapping based on its element type in a type-safe way, throwing if the cast is
   * impermissible. This cast is suitable for writing to the mapping.
   * 
   * @param <T>
   *          a subclass of the element type
   * @param clazz
   *          description of the subclass to cast to
   * @param mapping
   *          the mapping to cast
   * @return the cast mapping
   * @throws ClassCastException
   *           if (wrapped) {@code <T>} is not a subclass of the (wrapped) component type
   */
  public static <T> ConstMapping<? super T> castSuper(Class<T> clazz, ConstMapping<?> mapping) {
    if (mapping == null) {
      return null;
    }
    Class<T> destClass = ClassUtils.wrap(clazz);
    Class<?> sourceClass = ClassUtils.wrap(mapping.getComponentType());
    if (sourceClass.isAssignableFrom(destClass)) {
      @SuppressWarnings("unchecked")
      ConstMapping<? super T> result = (ConstMapping<? super T>) mapping;
      return result;
    }
    throw new ClassCastException(
        "Cannot cast ch.ethz.sn.lang.ConstMapping<" + sourceClass.getTypeName()
            + "> to ch.ethz.sn.lang.ConstMapping<? super " + destClass.getTypeName() + ">");
  }

  /**
   * Casts a mapping based on its element type in a type-safe way, throwing if the cast is
   * impermissible. This cast is suitable for writing to the mapping.
   * 
   * @param <T>
   *          a subclass of the element type
   * @param clazz
   *          description of the subclass to cast to
   * @param mapping
   *          the mapping to cast
   * @return the cast mapping
   * @throws ClassCastException
   *           if (wrapped) {@code <T>} is not a subclass of the (wrapped) component type
   */
  public static <T> Mapping<? super T> castSuper(Class<T> clazz, Mapping<?> mapping) {
    if (mapping == null) {
      return null;
    }
    Class<T> destClass = ClassUtils.wrap(clazz);
    Class<?> sourceClass = ClassUtils.wrap(mapping.getComponentType());
    if (sourceClass.isAssignableFrom(destClass)) {
      @SuppressWarnings("unchecked")
      Mapping<? super T> result = (Mapping<? super T>) mapping;
      return result;
    }
    throw new ClassCastException("Cannot cast ch.ethz.sn.lang.Mapping<" + sourceClass.getTypeName()
        + "> to ch.ethz.sn.lang.Mapping<? super " + destClass.getTypeName() + ">");
  }

  /**
   * Casts a list based on its element type in a type-safe way, throwing if the cast is
   * impermissible. This cast is suitable for writing to the list.
   * 
   * @param <T>
   *          a subclass of the element type
   * @param clazz
   *          description of the subclass to cast to
   * @param mapping
   *          the list to cast
   * @return the cast list
   * @throws ClassCastException
   *           if (wrapped) {@code <T>} is not a subclass of the (wrapped) component type
   */
  public static <T> PrimitiveList<? super T> castSuper(Class<T> clazz, PrimitiveList<?> mapping) {
    if (mapping == null) {
      return null;
    }
    Class<T> destClass = ClassUtils.wrap(clazz);
    Class<?> sourceClass = ClassUtils.wrap(mapping.getComponentType());
    if (sourceClass.isAssignableFrom(destClass)) {
      @SuppressWarnings("unchecked")
      PrimitiveList<? super T> result = (PrimitiveList<? super T>) mapping;
      return result;
    }
    throw new ClassCastException(
        "Cannot cast ch.ethz.sn.lang.PrimitiveList<" + sourceClass.getTypeName()
            + "> to ch.ethz.sn.lang.PrimitiveList<? super " + destClass.getTypeName() + ">");
  }

}
