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

package ch.ethz.sn.visone3.networks.impl;

/**
 * Magic number, capacity guesses for collections.
 */
public final class Magic {
  public static final int CAP_DEGREE = 10;
  public static final int CAP_NODES = 1_000;
  public static final int CAP_EDGES = 10_000;
  public static final double EPS = Double.MIN_VALUE;

  private Magic() {
  }

}
