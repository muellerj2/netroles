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

package ch.ethz.sn.visone3.networks.impl;

/**
 * Utility class to produce some human readable numbers.
 */
public final class Format {
  public static final String SI_PREFIX = " KMGTPE";
  public static final String UNIT_BYTE = "B";
  private static final long[] DURATION_SCALE = new long[] { 31_536_000_000L, 604_800_000,
      86_400_000, 3_600_000, 60_000, 1_000 };
  private static final String[] DURATION_UNIT = new String[] { "yrs", "wks", "days", "hrs", "min",
      "sec" };

  private Format() {
  }

  /**
   * Formats memory amount with SI prefixes.
   * 
   * @param fmt
   *          the format string; first format argument is the number of bytes (transformed to
   *          prefix), second argument is the unit name.
   * @param bytes
   *          the number of bytes.
   * @return the formatted string.
   */
  public static String formatBytes(final String fmt, final long bytes) {
    if (bytes < 1000) {
      return String.format(fmt, (double) bytes, UNIT_BYTE);
    }
    final int z = (int) (Math.log(bytes) / Math.log(1000));
    return String.format(fmt, (double) bytes / Math.pow(1000, z), SI_PREFIX.charAt(z) + UNIT_BYTE);
  }

  /**
   * Formats memory amount with binary prefixes.
   * 
   * @param fmt
   *          the format string; first format argument is the number of bytes (transformed to
   *          prefix), second argument is the unit name.
   * @param bytes
   *          the number of bytes.
   * @return the formatted string.
   */
  public static String formatBinaryBytes(final String fmt, final long bytes) {
    if (bytes < 1024) {
      return String.format(fmt, (double) bytes, UNIT_BYTE);
    }
    final int z = (63 - Long.numberOfLeadingZeros(bytes)) / 10;
    return String.format(fmt, (double) bytes / (1L << (z * 10)), SI_PREFIX.charAt(z) + "iB");
  }

  /**
   * Formats durations.
   * 
   * @param fmt
   *          the format string; first format argument is the number of bytes (transformed to
   *          prefix), second argument is the unit name.
   * @param duration
   *          the duration.
   * @return the formatted string.
   */
  public static String formatMillis(final String fmt, final long duration) {
    for (int i = 0; i < DURATION_SCALE.length; i++) {
      if (Math.abs(duration) >= DURATION_SCALE[i]) {
        return String.format(fmt, (double) duration / DURATION_SCALE[i], DURATION_UNIT[i]);
      }
    }
    return String.format(fmt, (double) duration, "msec");
  }
}
