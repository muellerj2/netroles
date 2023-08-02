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

package ch.ethz.sn.visone3.lang.impl.mappings;

import ch.ethz.sn.visone3.lang.ClassUtils;
import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.lang.Mapping;
import ch.ethz.sn.visone3.lang.PrimitiveCollector;
import ch.ethz.sn.visone3.lang.PrimitiveList;
import ch.ethz.sn.visone3.lang.spi.MappingsFacade;

/**
 * Provides methods to create representations for mappings and primitive lists.
 *
 */
public final class MappingsFacadeImpl implements MappingsFacade {

  public MappingsFacadeImpl() {
  }

  @Override
  public PrimitiveList.OfInt newIntList() {
    return new IntArrayList();
  }

  @Override
  public PrimitiveList.OfInt newIntList(int capacity) {
    return new IntArrayList(capacity);
  }

  @Override
  public PrimitiveList.OfInt newIntList(int value, int size) {
    return new IntArrayList(value, size);
  }

  @Override
  public PrimitiveList.OfInt newIntList(int[] values) {
    return new IntArrayList(values);
  }

  @Override
  public PrimitiveList.OfInt newIntListOfSize(int size) {
    return new IntArrayList(size, false);
  }

  @Override
  public PrimitiveList.OfLong newLongList() {
    return new LongArrayList();
  }

  @Override
  public PrimitiveList.OfLong newLongList(int capacity) {
    return new LongArrayList(capacity);
  }

  @Override
  public PrimitiveList.OfLong newLongList(long value, int size) {
    return new LongArrayList(value, size);
  }

  @Override
  public PrimitiveList.OfLong newLongList(long[] values) {
    return new LongArrayList(values);
  }

  @Override
  public PrimitiveList.OfLong newLongListOfSize(int size) {
    return new LongArrayList(size, false);
  }

  @Override
  public PrimitiveList.OfDouble newDoubleList() {
    return new DoubleArrayList();
  }

  @Override
  public PrimitiveList.OfDouble newDoubleList(int capacity) {
    return new DoubleArrayList(capacity);
  }

  @Override
  public PrimitiveList.OfDouble newDoubleList(double value, int size) {
    return new DoubleArrayList(value, size);
  }

  @Override
  public PrimitiveList.OfDouble newDoubleList(double[] values) {
    return new DoubleArrayList(values);
  }

  @Override
  public PrimitiveList.OfDouble newDoubleListOfSize(int size) {
    return new DoubleArrayList(size, false);
  }

  /**
   * Create an empty primitive list for the given primitive component type.
   *
   * @param componentType
   *          component type of the primitive list
   * @return A primitive array list instance if {@code componentType} is a supported primitive type,
   *         else null.
   */
  @SuppressWarnings("unchecked")
  private <T> PrimitiveList<T> newPrimitiveList(Class<T> componentType) {
    if (componentType == int.class) {
      return (PrimitiveList<T>) newIntList();
    } else if (componentType == long.class) {
      return (PrimitiveList<T>) newLongList();
    } else if (componentType == double.class) {
      return (PrimitiveList<T>) newDoubleList();
    } else {
      return null;
    }
  }

  /**
   * Create an empty primitive list with given initial capacity for the given primitive component
   * type.
   *
   * @param componentType
   *          component type of the primitive list
   * @param capacity
   *          Initial capacity
   * @return A primitive array list instance if {@code componentType} is a supported primitive type,
   *         else null.
   */
  @SuppressWarnings("unchecked")
  private <T> PrimitiveList<T> newPrimitiveList(Class<T> componentType, int capacity) {
    if (componentType == int.class) {
      return (PrimitiveList<T>) newIntList(capacity);
    } else if (componentType == long.class) {
      return (PrimitiveList<T>) newLongList(capacity);
    } else if (componentType == double.class) {
      return (PrimitiveList<T>) newDoubleList(capacity);
    } else {
      return null;
    }
  }

  /**
   * Create an empty primitive list for the given primitive component type, filled with the
   * specified number of a given initial value.
   *
   * @param componentType
   *          component type of the primitive list
   * @param value
   *          Initial value
   * @param size
   *          Initial size
   * @return A primitive array list instance if {@code componentType} is a supported primitive type,
   *         else null.
   */
  @SuppressWarnings("unchecked")
  private <T> PrimitiveList<T> newPrimitiveList(Class<T> componentType, T value, int size) {
    if (componentType == int.class) {
      return (PrimitiveList<T>) newIntList((Integer) value, size);
    } else if (componentType == long.class) {
      return (PrimitiveList<T>) newLongList((Long) value, size);
    } else if (componentType == double.class) {
      return (PrimitiveList<T>) newDoubleList((Double) value, size);
    } else {
      return null;
    }
  }

  /**
   * Create an empty primitive list for the given primitive component type from the given values in
   * the {@code array} object.
   * 
   * <p>
   * Note: The list might reuse the storage of the passed array.
   *
   * @param componentType
   *          component type of the primitive list
   * @param array
   *          Array of initial values
   * @return A primitive array list instance if {@code componentType} is a supported primitive type,
   *         else null.
   */
  @SuppressWarnings("unchecked")
  private <T> PrimitiveList<T> newPrimitiveListFromArray(Class<T> componentType, Object array) {
    if (componentType == int.class) {
      return (PrimitiveList<T>) newIntList((int[]) array);
    } else if (componentType == long.class) {
      return (PrimitiveList<T>) newLongList((long[]) array);
    } else if (componentType == double.class) {
      return (PrimitiveList<T>) newDoubleList((double[]) array);
    } else {
      return null;
    }
  }

  /**
   * Create a new list for the given primitive type, filled with the specified number of the type's
   * default value.
   * 
   * @param componentType
   *          component type of the primitive list
   * @param size
   *          Initial size
   * @return A primitive array list instance
   */
  @SuppressWarnings("unchecked")
  private <T> PrimitiveList<T> newPrimitiveListOfSize(Class<T> componentType, int size) {
    if (componentType == int.class) {
      return (PrimitiveList<T>) newIntListOfSize(size);
    } else if (componentType == long.class) {
      return (PrimitiveList<T>) newLongListOfSize(size);
    } else if (componentType == double.class) {
      return (PrimitiveList<T>) newDoubleListOfSize(size);
    } else {
      return null;
    }
  }

  /**
   * Create an empty list for the given reference component type.
   * 
   * @param componentType
   *          Reference component type of the list
   * @return A primitive array list instance for the reference type
   */
  private static <T> PrimitiveList<T> newReferenceList(Class<T> componentType) {
    assert (!componentType.isPrimitive());
    return new GenericArrayList<>(componentType);
  }

  /**
   * Create an empty list with the specified capacity for the given reference component type.
   * 
   * @param componentType
   *          Reference component type of the list
   * @param capacity
   *          Initial capacity
   * @return A primitive array list instance for the reference type
   */
  private static <T> PrimitiveList<T> newReferenceList(Class<T> componentType, int capacity) {
    assert (!componentType.isPrimitive());
    return new GenericArrayList<>(componentType, capacity);
  }

  /**
   * Create a new list for the given reference component type, filled with the specified number of a
   * given initial value.
   * 
   * @param componentType
   *          Reference component type of the list
   * @param value
   *          Initial value
   * @param size
   *          Initial size
   * @return A primitive array list instance for the reference type
   */
  private static <T> PrimitiveList<T> newReferenceList(Class<T> componentType, T value, int size) {
    assert (!componentType.isPrimitive());
    return new GenericArrayList<>(componentType, value, size);
  }

  /**
   * Create a new list for the given reference component type, filled with the given values.
   * 
   * <p>
   * Note: The list might reuse the storage of the passed array.
   * 
   * @param componentType
   *          Reference component type of the list
   * @param values
   *          Initial values
   * @return A primitive array list instance for the reference type
   */
  private static <T> PrimitiveList<T> newReferenceList(Class<T> componentType, T[] values) {
    assert (!componentType.isPrimitive());
    return new GenericArrayList<>(componentType, values);
  }

  @Override
  public <T> PrimitiveList<T> newList(Class<T> componentType) {
    if (componentType.isPrimitive()) {
      PrimitiveList<T> list = newPrimitiveList(componentType);
      if (list == null) {
        throw new UnsupportedOperationException("unsupported primitive type");
      }
      return list;
    }
    return newReferenceList(componentType);
  }

  @Override
  public <T> PrimitiveList<T> newList(Class<T> componentType, int capacity) {
    if (componentType.isPrimitive()) {
      PrimitiveList<T> list = newPrimitiveList(componentType, capacity);
      if (list == null) {
        throw new UnsupportedOperationException("unsupported primitive type");
      }
      return list;
    }
    return newReferenceList(componentType, capacity);
  }

  @Override
  public <T> PrimitiveList<T> newList(Class<T> componentType, T value, int size) {
    if (componentType.isPrimitive()) {
      PrimitiveList<T> list = newPrimitiveList(componentType, value, size);
      if (list == null) {
        throw new UnsupportedOperationException("unsupported primitive type");
      }
      return list;
    }
    return newReferenceList(componentType, value, size);
  }

  @Override
  public <T> PrimitiveList<T> newList(Class<T> componentType, T[] values) {
    if (componentType.isPrimitive()) {
      throw new IllegalArgumentException(
          "cannot construct list of primitive type from reference array");
    }
    return newReferenceList(componentType, values);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> PrimitiveList<T> newListFromArray(Class<T> componentType, Object array) {
    if (componentType.isPrimitive()) {
      PrimitiveList<T> list = newPrimitiveListFromArray(componentType, array);
      if (list == null) {
        throw new UnsupportedOperationException("unsupported primitive type");
      }
      return list;
    }
    return newReferenceList(componentType, (T[]) array);
  }

  @Override
  public <T> PrimitiveList<T> newListOfSize(Class<T> componentType, int size) {
    if (componentType.isPrimitive()) {
      PrimitiveList<T> list = newPrimitiveListOfSize(componentType, size);
      if (list == null) {
        throw new UnsupportedOperationException("unsupported primitive type");
      }
      return list;
    }
    return newReferenceList(componentType, null, size);
  }

  @Override
  public <T> PrimitiveList<T> newListAutoboxing(Class<T> componentType) {
    if (componentType.isPrimitive()) {
      PrimitiveList<T> list = newPrimitiveList(componentType);
      if (list != null) {
        return list;
      }
      componentType = ClassUtils.wrap(componentType);
    }
    return newReferenceList(componentType);
  }

  @Override
  public <T> PrimitiveList<T> newListAutoboxing(Class<T> componentType, int capacity) {
    if (componentType.isPrimitive()) {
      PrimitiveList<T> list = newPrimitiveList(componentType, capacity);
      if (list != null) {
        return list;
      }
      componentType = ClassUtils.wrap(componentType);
    }
    return newReferenceList(componentType, capacity);
  }

  @Override
  public <T> PrimitiveList<T> newListAutoboxing(Class<T> componentType, T value, int size) {
    if (componentType.isPrimitive()) {
      PrimitiveList<T> list = newPrimitiveList(componentType, value, size);
      if (list != null) {
        return list;
      }
      componentType = ClassUtils.wrap(componentType);
    }
    return newReferenceList(componentType, value, size);
  }

  @Override
  public <T> PrimitiveList<T> newListOfSizeAutoboxing(Class<T> componentType, int size) {
    if (componentType.isPrimitive()) {
      PrimitiveList<T> list = newPrimitiveListOfSize(componentType, size);
      if (list != null) {
        return list;
      }
      componentType = ClassUtils.wrap(componentType);
    }
    return newReferenceList(componentType, null, size);
  }

  @Override
  public ConstMapping.OfInt intRange(int begin, int end) {
    return new IntRangeMapping(begin, end);
  }

  @Override
  public Mapping.OfInt wrapModifiableInt(int... values) {
    return new MutableIntArrayMapping(values);
  }

  @Override
  public ConstMapping.OfInt wrapUnmodifiableInt(int... values) {
    return new ConstIntArrayMapping(values);
  }

  @Override
  public ConstMapping.OfInt wrapUnmodifiable(int[] array, int begin, int end) {
    return new ConstIntArrayMapping(array, begin, end);
  }

  @Override
  public ConstMapping.OfInt repeated(int value, int count) {
    return new ConstIntCopiesMapping(value, count);
  }

  @Override
  public PrimitiveCollector.OfInt<?, PrimitiveList.OfInt> toIntList() {
    return PrimitiveCollector.OfInt.of(IntArrayList.COLLECTION_SUPPLIER,
        IntArrayList.COLLECTION_ACCUMULATOR, IntArrayList.COLLECTION_COMBINER, x -> x,
        PrimitiveList.OfInt.class);
  }

  @Override
  public PrimitiveCollector.OfLong<?, PrimitiveList.OfLong> toLongList() {
    return PrimitiveCollector.OfLong.of(LongArrayList.COLLECTION_SUPPLIER,
        LongArrayList.COLLECTION_ACCUMULATOR, LongArrayList.COLLECTION_COMBINER, x -> x,
        PrimitiveList.OfLong.class);
  }

  @Override
  public PrimitiveCollector.OfDouble<?, PrimitiveList.OfDouble> toDoubleList() {
    return PrimitiveCollector.OfDouble.of(DoubleArrayList.COLLECTION_SUPPLIER,
        DoubleArrayList.COLLECTION_ACCUMULATOR, DoubleArrayList.COLLECTION_COMBINER, x -> x,
        PrimitiveList.OfDouble.class);
  }

  @SuppressWarnings("unchecked")
  private static <T> PrimitiveCollector<T, ?, PrimitiveList<T>> toReferenceList(
      Class<T> componentType) {
    return PrimitiveCollector.of(() -> MappingsFacadeImpl.newReferenceList(componentType),
        PrimitiveList::add, PrimitiveList::addAll,
        (Class<PrimitiveList<T>>) (Class<?>) PrimitiveList.class);
  }

  @SuppressWarnings("unchecked")
  private <T> PrimitiveCollector<T, ?, ? extends PrimitiveList<T>> toListForPrimitive(
      Class<T> componentType) {
    if (componentType == int.class) {
      return (PrimitiveCollector<T, ?, ? extends PrimitiveList<T>>) toIntList();
    } else if (componentType == long.class) {
      return (PrimitiveCollector<T, ?, ? extends PrimitiveList<T>>) toLongList();
    } else if (componentType == double.class) {
      return (PrimitiveCollector<T, ?, ? extends PrimitiveList<T>>) toDoubleList();
    }
    return null;
  }

  @Override
  public <T> PrimitiveCollector<T, ?, ? extends PrimitiveList<T>> toList(Class<T> componentType) {
    if (componentType.isPrimitive()) {
      PrimitiveCollector<T, ?, ? extends PrimitiveList<T>> list = toListForPrimitive(componentType);
      if (list == null) {
        throw new UnsupportedOperationException("unsupported primitive type");
      }
      return list;
    }
    return toReferenceList(componentType);
  }

  @Override
  public <T> PrimitiveCollector<T, ?, ? extends PrimitiveList<T>> toListAutoboxing(
      Class<T> componentType) {
    if (componentType.isPrimitive()) {
      PrimitiveCollector<T, ?, ? extends PrimitiveList<T>> list = toListForPrimitive(componentType);
      if (list != null) {
        return list;
      }
      componentType = ClassUtils.wrap(componentType);
    }
    return toReferenceList(componentType);
  }
}
