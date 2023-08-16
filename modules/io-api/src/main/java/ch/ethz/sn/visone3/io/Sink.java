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

package ch.ethz.sn.visone3.io;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.networks.Network;

import java.io.IOException;

/**
 * Specifies which and how parts of a data container are written to a stream.
 */
public interface Sink extends AutoCloseable {
  /**
   * Set the incidence structure to store.
   * 
   * @param network the incidence structure.
   */
  void incidence(Network network);

  /**
   * Set scalar values.
   * 
   * @param name  variable name.
   * @param value assigned value.
   */
  void global(String name, Object value);

  // this needs a different name because T == ConstMapping<T> would be ambiguous
  /**
   * Set default for monadic values.
   *
   * @param name variable name.
   * @param def  default value. If {@code null} clears any previous assignment. If
   *             the sink supports sparse storage, occurrences of this value are
   *             not stored.
   * @param <T>  type of the values.
   */
  default <T> void nodeDefault(final String name, final T def) {
    node(name, def, null);
  }

  /**
   * Set monadic values.
   *
   * @param name    variable name.
   * @param monadic node mapping. If {@code null} clears any previous assignment.
   * @param <T>     type of the values.
   */
  default <T> void node(final String name, final ConstMapping<T> monadic) {
    node(name, null, monadic);
  }

  /**
   * Set monadic values.
   *
   * @param name    variable name.
   * @param def     default value. If {@code null} clears any previous assignment.
   *                If the sink supports sparse storage, occurrences of this value
   *                are not stored.
   * @param monadic node mapping. If {@code null} clears any previous assignment.
   * @param <T>     type of the values.
   */
  <T> void node(String name, T def, ConstMapping<T> monadic);

  // this needs a different name because T == ConstMapping<T> would be ambiguous
  /**
   * Set default for dyadic values.
   *
   * @param name variable name.
   * @param def  default value. If {@code null} clears any previous assignment. If
   *             the sink supports sparse storage, occurrences of this value are
   *             not stored.
   * @param <T>  type of the values.
   */
  default <T> void linkDefault(final String name, final T def) {
    link(name, def, null);
  }

  /**
   * Set dyadic values.
   *
   * @param name   variable name.
   * @param dyadic link mapping. If {@code null} clears any previous assignment.
   * @param <T>    type of the values.
   */
  default <T> void link(final String name, final ConstMapping<T> dyadic) {
    link(name, null, dyadic);
  }

  /**
   * Set dyadic values.
   *
   * @param name   variable name.
   * @param def    default value. If {@code null} clears any previous assignment.
   *               If the sink supports sparse storage, occurrences of this value
   *               are not stored.
   * @param dyadic link mapping. If {@code null} clears any previous assignment.
   * @param <T>    type of the values.
   */
  <T> void link(String name, T def, ConstMapping<T> dyadic);

  /**
   * Sets additional hints used by the sink for outputting the described data.
   * 
   * @param key   name of the hint
   * @param value assigned value to this hint
   */
  default void hint(final String key, final String value) {
  }

  /**
   * Closes the sink.
   * 
   * @throws IOException forwards exceptions of underlying IO.
   */
  @Override
  void close() throws IOException;
}
