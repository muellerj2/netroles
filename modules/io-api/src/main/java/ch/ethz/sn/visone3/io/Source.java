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

package ch.ethz.sn.visone3.io;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.networks.DyadType;

import java.io.IOException;
import java.util.function.Function;

/**
 * Specifies which and how parts of a data container are read.
 *
 * @param <H>
 *          Hint type.
 */
public interface Source<H> extends AutoCloseable {
  /**
   * Typed conversion function.
   *
   * @param <T>
   *          data type.
   */
  interface Range<T> extends Function<String, T> {

    /**
     * Returns the type of the values produced by this converter.
     * 
     * @return the class object representing the value type.
     */
    Class<T> componentType();

    /**
     * Returns the default value for this converter.
     * 
     * @return the default value.
     */
    T defaultValue();

    Range<Integer> INT = Range.of(int.class, 0, Integer::parseInt);
    Range<Double> DOUBLE = Range.of(double.class, 0., Double::parseDouble);
    Range<String> STRING = Range.of(String.class, null, String::valueOf);

    /**
     * Constructs a new typed converter from string to the specified value type.
     * 
     * @param <T>
     *          the type of the produced value.
     * @param clazz
     *          the class object for the value type.
     * @param defaultValue
     *          the default value of the value type.
     * @param conv
     *          the actual conversion function.
     * @return the typed converter.
     */
    static <T> Range<T> of(final Class<T> clazz, T defaultValue, final Function<String, T> conv) {
      return new Range<T>() {
        @Override
        public Class<T> componentType() {
          return clazz;
        }

        @Override
        public T defaultValue() {
          return defaultValue;
        }

        @Override
        public T apply(final String str) {
          return conv.apply(str);
        }

      };
    }
  }

  /**
   * Tells whether the source can be parsed without any configuration.
   * 
   * @return True if no prior configuration is necessary, false otherwise.
   */
  boolean isAutoconfig();

  /**
   * Sets the mapping from node indices to node ids that is used when reading from the source.
   * 
   * @param ids
   *          mapping from node indices to node ids
   */
  default void mergeNodes(final ConstMapping<String> ids) {
    throw new UnsupportedOperationException("merging nodes not supported");
  }

  /**
   * Sets the mapping from affiliation indices to affiliation ids that is used when reading from the
   * source.
   * 
   * @param ids
   *          mapping from affiliation indices to affiliation ids
   */
  default void mergeAffiliations(final ConstMapping<String> ids) {
    throw new UnsupportedOperationException("merging affiliations not supported");
  }

  /**
   * Sets the node id attribute and the associated typed converter for monadic sources.
   * 
   * @param varName
   *          the variable name.
   * @param range
   *          the typed converter.
   */
  default void monad(final String varName, final Range<?> range) {
    throw new UnsupportedOperationException("reading monadic variables not supported");
  }

  /**
   * Specifies the two incidence keys for dyadic sources.
   *
   * @param type
   *          Type of dyads.
   * @param sourceVarName
   *          source key.
   * @param targetVarName
   *          target key.
   * @param range
   *          Type of node id.
   */
  default void dyad(final DyadType type, final String sourceVarName, final String targetVarName,
      final Range<?> range) {
    throw new UnsupportedOperationException("reading networks not supported");
  }

  /**
   * Sets the typed converter to use for reading a dyadic variable.
   * 
   * @param varName
   *          the variable name
   * @param range
   *          the typed converter
   */
  default void linkrange(final String varName, final Range<?> range) {
    throw new UnsupportedOperationException("reading link weights not supported");
  }

  /**
   * Sets the typed converter to use for reading a monadic variable.
   * 
   * @param varName
   *          the variable name
   * @param range
   *          the typed converter
   */
  default void noderange(final String varName, final Range<?> range) {
    throw new UnsupportedOperationException("reading node weights not supported");
  }

  /**
   * Passes a (source-specific) hint to the source.
   * 
   * @param key
   *          the source-specific key of the hint
   * @param value
   *          the value of the hint
   */
  default void hint(final H key, final Object value) {
  }

  /**
   * Processes the source and builds incidence and attributes. Actual IO might happen earlier to
   * perform the auto configuration.
   * 
   * @return the result of the parse.
   */
  SourceFormat parse() throws IOException;

  /**
   * Closes the source.
   * 
   * @throws IOException
   *           Forwards exceptions of underlying IO.
   * @implNote Overrides the thrown exception type.
   */
  @Override
  void close() throws IOException;
}
