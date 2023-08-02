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

import java.util.Iterator;

class FormatterUtility {

  private FormatterUtility() {
  }

  /**
   * Append till {@code sb} is over {@code widthHint} characters long.
   */
  static <T> void limited(final StringBuilder sb, final Iterator<T> itr, final int widthHint) {
    final String fmt = "(... %3s omitted)";
    // append as long as it fits
    if (itr.hasNext()) {
      final String next = String.valueOf(itr.next());
      if (widthHint < 0 || sb.length() + next.length() < widthHint) {
        sb.append(next);
      }
    }

    // append as long as it fits
    while (itr.hasNext()) {
      final String next = String.valueOf(itr.next());
      if (widthHint >= 0 && sb.length() + next.length() >= widthHint) {
        break;
      }
      sb.append(',').append(next);
    }

    // count rest (but limit to not walk everything)
    int count = 0;
    final int max = 99;
    while (itr.hasNext() && count < max) {
      itr.next();
      count++;
    }
    if (count >= max) {
      sb.append(String.format(fmt, max + "+"));
    } else if (count > 0) {
      sb.append(String.format(fmt, count));
    }
  }
}
