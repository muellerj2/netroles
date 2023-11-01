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
/**
 * Provides basic utility functions and containers, dealing especially with
 * (unboxed) primitive types.
 * 
 * <ul>
 * <li>Mappings are fixed-size lists and store values of primitive types
 * compactly (i.e., unboxed). There are read-only
 * ({@link ch.ethz.sn.visone3.lang.ConstMapping}) and writable
 * {@link ch.ethz.sn.visone3.lang.Mapping} mappings, as well as those with
 * variable size {@link ch.ethz.sn.visone3.lang.PrimitiveList} that function
 * similar to {@link java.util.List}. New Mappings can be created using methods
 * of the {@link Mappings} class.
 * <li>Algorithms for permuting, mapping or counting sort on mappings or
 * (primitive) arrays are available in the {@link PrimitiveCollections}
 * class.</li>
 * <li>The {@link ch.ethz.sn.visone3.lang.PrimitiveCollector} interface
 * describes primitive versions of the {@link java.util.stream.Collector}
 * interface in the Java stream API. The
 * {@link ch.ethz.sn.visone3.lang.PrimitiveCollectors} class provides collectors
 * for use with {@link java.util.stream.IntStream},
 * {@link java.util.stream.LongStream} and
 * {@link java.util.stream.DoubleStream}.</li>
 * <li>Maps, sets, queues and heaps for some primitive data types can be
 * obtained from the {@link ch.ethz.sn.visone3.lang.PrimitiveContainers}
 * class.</li>
 * <li>{@link ch.ethz.sn.visone3.lang.Pair} and
 * {@link ch.ethz.sn.visone3.lang.IntPair} can be used to represents pairs of
 * objects and integers.</li>
 * <li>{@link ch.ethz.sn.visone3.lang.ClassUtils} provides utilities to work
 * with class types.</li>
 * </ul>
 */
package ch.ethz.sn.visone3.lang;
