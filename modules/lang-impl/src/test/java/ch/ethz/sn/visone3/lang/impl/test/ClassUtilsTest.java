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

package ch.ethz.sn.visone3.lang.impl.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.ethz.sn.visone3.lang.ClassUtils;

import org.junit.jupiter.api.Test;

class ClassUtilsTest {

  @Test
  void testWrappedToPrimitive() {
    assertEquals(boolean.class, ClassUtils.wrappedToPrimitive(Boolean.class));
    assertEquals(byte.class, ClassUtils.wrappedToPrimitive(Byte.class));
    assertEquals(char.class, ClassUtils.wrappedToPrimitive(Character.class));
    assertEquals(short.class, ClassUtils.wrappedToPrimitive(Short.class));
    assertEquals(int.class, ClassUtils.wrappedToPrimitive(Integer.class));
    assertEquals(long.class, ClassUtils.wrappedToPrimitive(Long.class));
    assertEquals(float.class, ClassUtils.wrappedToPrimitive(Float.class));
    assertEquals(double.class, ClassUtils.wrappedToPrimitive(Double.class));
    assertEquals(void.class, ClassUtils.wrappedToPrimitive(Void.class));
    assertThrows(IllegalArgumentException.class, () -> {
      ClassUtils.wrappedToPrimitive(Object.class);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      ClassUtils.wrappedToPrimitive(String.class);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      ClassUtils.wrappedToPrimitive(Number.class);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      ClassUtils.wrappedToPrimitive(int.class);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      ClassUtils.wrappedToPrimitive(Comparable.class);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      ClassUtils.wrappedToPrimitive(Character.UnicodeScript.class);
    });
  }

  @Test
  void testPrimitiveToWrapped() {
    assertEquals(Boolean.class, ClassUtils.primitiveToWrapped(boolean.class));
    assertEquals(Byte.class, ClassUtils.primitiveToWrapped(byte.class));
    assertEquals(Character.class, ClassUtils.primitiveToWrapped(char.class));
    assertEquals(Short.class, ClassUtils.primitiveToWrapped(short.class));
    assertEquals(Integer.class, ClassUtils.primitiveToWrapped(int.class));
    assertEquals(Long.class, ClassUtils.primitiveToWrapped(long.class));
    assertEquals(Float.class, ClassUtils.primitiveToWrapped(float.class));
    assertEquals(Double.class, ClassUtils.primitiveToWrapped(double.class));
    assertEquals(Void.class, ClassUtils.primitiveToWrapped(void.class));
    assertThrows(IllegalArgumentException.class, () -> {
      ClassUtils.primitiveToWrapped(Object.class);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      ClassUtils.primitiveToWrapped(String.class);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      ClassUtils.primitiveToWrapped(Number.class);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      ClassUtils.primitiveToWrapped(Integer.class);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      ClassUtils.primitiveToWrapped(Comparable.class);
    });
    assertThrows(IllegalArgumentException.class, () -> {
      ClassUtils.primitiveToWrapped(Character.UnicodeScript.class);
    });
  }

  @Test
  void testWrap() {
    assertEquals(Boolean.class, ClassUtils.wrap(boolean.class));
    assertEquals(Byte.class, ClassUtils.wrap(byte.class));
    assertEquals(Character.class, ClassUtils.wrap(char.class));
    assertEquals(Short.class, ClassUtils.wrap(short.class));
    assertEquals(Integer.class, ClassUtils.wrap(int.class));
    assertEquals(Long.class, ClassUtils.wrap(long.class));
    assertEquals(Float.class, ClassUtils.wrap(float.class));
    assertEquals(Double.class, ClassUtils.wrap(double.class));
    assertEquals(Void.class, ClassUtils.wrap(void.class));
    assertEquals(Object.class, ClassUtils.wrap(Object.class));
    assertEquals(String.class, ClassUtils.wrap(String.class));
    assertEquals(Number.class, ClassUtils.wrap(Number.class));
    assertEquals(Integer.class, ClassUtils.wrap(Integer.class));
    assertEquals(Comparable.class, ClassUtils.wrap(Comparable.class));
    assertEquals(Character.UnicodeScript.class, ClassUtils.wrap(Character.UnicodeScript.class));
  }

  @Test
  void testUnwrap() {
    assertEquals(boolean.class, ClassUtils.unwrap(Boolean.class));
    assertEquals(byte.class, ClassUtils.unwrap(Byte.class));
    assertEquals(char.class, ClassUtils.unwrap(Character.class));
    assertEquals(short.class, ClassUtils.unwrap(Short.class));
    assertEquals(int.class, ClassUtils.unwrap(Integer.class));
    assertEquals(long.class, ClassUtils.unwrap(Long.class));
    assertEquals(float.class, ClassUtils.unwrap(Float.class));
    assertEquals(double.class, ClassUtils.unwrap(Double.class));
    assertEquals(void.class, ClassUtils.unwrap(Void.class));
    assertEquals(Object.class, ClassUtils.unwrap(Object.class));
    assertEquals(String.class, ClassUtils.unwrap(String.class));
    assertEquals(Number.class, ClassUtils.unwrap(Number.class));
    assertEquals(int.class, ClassUtils.unwrap(int.class));
    assertEquals(Comparable.class, ClassUtils.unwrap(Comparable.class));
    assertEquals(Character.UnicodeScript.class, ClassUtils.unwrap(Character.UnicodeScript.class));
  }

}
