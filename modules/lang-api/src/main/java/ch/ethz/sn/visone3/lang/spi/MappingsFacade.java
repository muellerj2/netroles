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
package ch.ethz.sn.visone3.lang.spi;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.PrimitiveCollector;
import ch.ethz.sn.visone3.lang.PrimitiveList;

/**
 * Provides methods to manipulate and create mappings.
 */
public interface MappingsFacade {

  /**
   * Create a new list of (unboxed) integers.
   *
   * @return A primitive array list instance for integers
   */
  PrimitiveList.OfInt newIntList();

  /**
   * Create a new list of (unboxed) integers with specified initial capacity.
   *
   * @param capacity
   *          Initial capacity of the list
   * @return A primitive array list instance for integers
   */
  PrimitiveList.OfInt newIntList(int capacity);

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
  PrimitiveList.OfInt newIntList(int value, int size);

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
  PrimitiveList.OfInt newIntList(int[] values);

  /**
   * Create a new list of (unboxed) integers, filled with the specified number of zeros.
   *
   * @param size
   *          Initial size
   * @return A primitive array list instance for integers
   */
  PrimitiveList.OfInt newIntListOfSize(int size);

  /**
   * Create a new list of (unboxed) longs.
   *
   * @return A primitive array list instance for longs
   */
  PrimitiveList.OfLong newLongList();

  /**
   * Create a new list of (unboxed) longs with specified initial capacity.
   *
   * @param capacity
   *          Initial capacity of the list
   * @return A primitive array list instance for longs
   */
  PrimitiveList.OfLong newLongList(int capacity);

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
  PrimitiveList.OfLong newLongList(long value, int size);

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
  PrimitiveList.OfLong newLongList(long[] values);

  /**
   * Create a new list of (unboxed) longs, filled with the specified number of zeros.
   *
   * @param size
   *          Initial size
   * @return A primitive array list instance for longs
   */
  PrimitiveList.OfLong newLongListOfSize(int size);

  /**
   * Create a new list of (unboxed) doubles.
   *
   * @return A primitive array list instance for doubles
   */
  PrimitiveList.OfDouble newDoubleList();

  /**
   * Create a new list of (unboxed) doubles with specified initial capacity.
   *
   * @param capacity
   *          Initial capacity of the list
   * @return A primitive array list instance for doubles
   */
  PrimitiveList.OfDouble newDoubleList(int capacity);

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
  PrimitiveList.OfDouble newDoubleList(double value, int size);

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
  PrimitiveList.OfDouble newDoubleList(double[] values);

  /**
   * Create a new list of (unboxed) doubles, filled with the specified number of zeros.
   *
   * @param size
   *          Initial size
   * @return A primitive array list instance for doubles
   */
  PrimitiveList.OfDouble newDoubleListOfSize(int size);

  /**
   * Create an empty primitive list for the given component type.
   *
   * @param componentType
   *          Component type of the list
   * @return A primitive array list instance if {@code componentType} is a reference type or a
   *         supported primitive type.
   * @throws UnsupportedOperationException
   *           if the component type is a primitive type for which no specialized primitive list is
   *           available (i.e., throws for all primitive types but int, boolean and double)
   */
  <T> PrimitiveList<T> newList(Class<T> componentType);

  /**
   * Create an empty primitive list with specified capacity for the given component type.
   *
   * @param componentType
   *          Component type of the list
   * @param capacity
   *          Initial capacity
   * @return A primitive array list instance if {@code componentType} is a reference type or a
   *         supported primitive type.
   * @throws UnsupportedOperationException
   *           if the component type is a primitive type for which no specialized primitive list is
   *           available (i.e., throws for all primitive types but int, boolean and double)
   */
  <T> PrimitiveList<T> newList(Class<T> componentType, int capacity);

  /**
   * Create a new primitive list for the given component type, filled with the specified number of a
   * given initial value.
   *
   * @param componentType
   *          Component type of the list
   * @return A primitive array list instance if {@code componentType} is a reference type or a
   *         supported primitive type.
   * @throws UnsupportedOperationException
   *           if the component type is a primitive type for which no specialized primitive list is
   *           available (i.e., throws for all primitive types but int, boolean and double)
   */
  <T> PrimitiveList<T> newList(Class<T> componentType, T value, int size);

  /**
   * Create a new primitive list for the given component type from the given values.
   * 
   * <p>
   * Note: The list is allowed to reuse the storage of the passed array.
   *
   * @param componentType
   *          Component type of the list
   * @param values
   *          Initial values
   * @return A primitive array list instance if {@code componentType} is a reference type or a
   *         supported primitive type.
   * @throws UnsupportedOperationException
   *           if the component type is a primitive type (because automatic unboxing is not
   *           provided)
   */
  <T> PrimitiveList<T> newList(Class<T> componentType, T[] values);

  /**
   * Create a new primitive list for the given component type from the given values in the
   * {@code array} object.
   * 
   * <p>
   * Note: The list is allowed to reuse the storage of the passed array.
   *
   * @param componentType
   *          Component type of the list
   * @param array
   *          An array object containing the initial values
   * @return A primitive array list instance if {@code componentType} is a reference type or a
   *         supported primitive type.
   * @throws UnsupportedOperationException
   *           if the component type is a primitive type for which no specialized primitive list is
   *           available (i.e., throws for all primitive types but int, boolean and double)
   */
  <T> PrimitiveList<T> newListFromArray(Class<T> componentType, Object array);

  /**
   * Create a new primitive list for the given component type, filled with the specified number of
   * the component type's default value.
   *
   * @param componentType
   *          Component type of the list
   * @return A primitive array list instance if {@code componentType} is a reference type or a
   *         supported primitive type.
   * @throws UnsupportedOperationException
   *           if the component type is a primitive type for which no specialized primitive list is
   *           available (i.e., throws for all primitive types but int, boolean and double)
   */
  <T> PrimitiveList<T> newListOfSize(Class<T> componentType, int size);

  /**
   * Create an empty primitive list for the given component type, or its wrapper type if the
   * component type is primitive and no specialized primitive list is available for the specified
   * primitive component type.
   *
   * @param componentType
   *          Component type of the list
   * @return A primitive array list instance
   */
  <T> PrimitiveList<T> newListAutoboxing(Class<T> componentType);

  /**
   * Create an empty primitive list with the specified initial capacity for the given component
   * type, or its wrapper type if the component type is primitive and no specialized primitive list
   * is available for the specified primitive component type.
   *
   * @param componentType
   *          Component type of the list
   * @return A primitive array list instance
   */
  <T> PrimitiveList<T> newListAutoboxing(Class<T> componentType, int capacity);

  /**
   * Create a new primitive list for the given component type, or its wrapper type if the component
   * type is primitive and no specialized primitive list is available for the specified primitive
   * component type. The primitive list is filled with the specified number of the given initial
   * value.
   *
   * @param componentType
   *          Component type of the list
   * @param value
   *          Initial value
   * @param size
   *          Initial size
   * @return A primitive array list instance
   */
  <T> PrimitiveList<T> newListAutoboxing(Class<T> componentType, T value, int size);

  /**
   * Create a new primitive list for the given component type, or its wrapper type if the component
   * type is primitive and no specialized primitive list is available for the specified primitive
   * component type. The primitive list is filled with the specified number of the list's component
   * type's default value, i.e.:
   * <ul>
   * <li>If {@code componentType} is a supported primitive type, fill it with zeros, that type's
   * default value</li>
   * <li>If {@code componentType} is a reference type, fill it with nulls, a reference type's
   * default value</li>
   * <li>If {@code componentType} is an unsupported primitive type, fill it with nulls, the wrapper
   * type's default value
   * </ul>
   * 
   * @param componentType
   *          Component type of the list
   * @param size
   *          Initial size
   * @return A primitive array list instance
   */
  <T> PrimitiveList<T> newListOfSizeAutoboxing(Class<T> componentType, int size);

  /**
   * Create a constant mapping that represents an integer range.
   * 
   * @param begin
   *          Start of integer range
   * @param end
   *          End (exlusive) of integer range
   * @return An integer constant mapping representing the range
   */
  ConstMapping.OfInt intRange(int begin, int end);

  /**
   * Create a mutable wrapper around an array of integer values.
   * 
   * @param values
   *          The values
   * @return A mutable integer mapping that provides a view on the supplied array
   */
  Mapping.OfInt wrapModifiableInt(int... values);

  /**
   * Create a constant wrapper of integer values.
   * 
   * @param values
   *          The values
   * @return A constant integer mapping that provides a view on the supplied array
   */
  ConstMapping.OfInt wrapUnmodifiableInt(int... values);

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
  ConstMapping.OfInt wrapUnmodifiable(int[] array, int begin, int end);

  /**
   * Create a constant mapping representing several copies of the same value.
   * 
   * @param value
   *          The value
   * @param count
   *          The number of copies
   * @return A constant integer mapping representing {@code count} copies of {@code value}
   */
  ConstMapping.OfInt repeated(int value, int count);

  /**
   * Returns a {@link PrimitiveCollector.OfInt} that collects the input integers into a new
   * {@link PrimitiveList.OfInt}.
   * 
   * @return a {@link PrimitiveCollector.OfInt} which collects all the input integers into a
   *         {@link PrimitiveList.OfInt} in encounter order
   */
  PrimitiveCollector.OfInt<?, PrimitiveList.OfInt> toIntList();

  /**
   * Returns a {@link PrimitiveCollector.OfLong} that collects the input longs into a new
   * {@link PrimitiveList.OfLong}.
   * 
   * @return a {@link PrimitiveCollector.OfLong} which collects all the input longs into a
   *         {@link PrimitiveList.OfLong} in encounter order
   */
  PrimitiveCollector.OfLong<?, PrimitiveList.OfLong> toLongList();

  /**
   * Returns a {@link PrimitiveCollector.OfDouble} that collects the input integers into a new
   * {@link PrimitiveList.OfDouble}.
   * 
   * @return a {@link PrimitiveCollector.OfDouble} which collects all the input integers into a
   *         {@link PrimitiveList.OfDouble} in encounter order
   */
  PrimitiveCollector.OfDouble<?, PrimitiveList.OfDouble> toDoubleList();

  /**
   * Returns a {@link PrimitiveCollector} that collects the input elements into a new
   * {@link PrimitiveList} for the supplied component type.
   * 
   * <p>
   * If the supplied component type is one of the primitive types {@code int}, {@code long}, or
   * {@code double}, returns a {@link PrimitiveCollector.OfInt}, {@link PrimitiveCollector.OfLong}
   * or {@link PrimitiveCollector.OfDouble} that collect into a {@link PrimitiveList.OfInt},
   * {@link PrimitiveList.OfLong} or {@link PrimitiveList.OfDouble}, respectively. If the component
   * type is any other primitive type, throws an exception. For any other type, returns a
   * {@link PrimitiveCollector} that collects into a {@link PrimitiveList} for that component type.
   * 
   * <p>
   * Note that the list of supported primitive component types might be extended in the future.
   * 
   * @param componentType
   *          the component type of the {@link PrimitiveList} to collect into
   * @return a {@link PrimitiveCollector} which collects all the input elements into a
   *         {@link PrimitiveList} in encounter order
   * @throws UnsupportedOperationException
   *           if the component type is primitive and not one of the supported primitive component
   *           types
   */
  <T> PrimitiveCollector<T, ?, ? extends PrimitiveList<T>> toList(Class<T> componentType);

  /**
   * Returns a {@link PrimitiveCollector} that collects the input elements into a new
   * {@link PrimitiveList} for the supplied component type.
   * 
   * <p>
   * If the supplied component type is one of the primitive types {@code int}, {@code long}, or
   * {@code double}, returns a {@link PrimitiveCollector.OfInt}, {@link PrimitiveCollector.OfLong}
   * or {@link PrimitiveCollector.OfDouble} that collect into a {@link PrimitiveList.OfInt},
   * {@link PrimitiveList.OfLong} or {@link PrimitiveList.OfDouble}, respectively. If the component
   * type is any other primitive type, it produces a collector for the wrapper type. For any other
   * type, returns a {@link PrimitiveCollector} that collects into a {@link PrimitiveList} for that
   * component type.
   * 
   * <p>
   * Note that the list of primitive component types with specialized primitive lists and collectors
   * might be extended in the future.
   * 
   * @param componentType
   *          the component type of the {@link PrimitiveList} to collect into
   * @return a {@link PrimitiveCollector} which collects all the input elements into a
   *         {@link PrimitiveList} in encounter order
   */
  <T> PrimitiveCollector<T, ?, ? extends PrimitiveList<T>> toListAutoboxing(Class<T> componentType);
}
