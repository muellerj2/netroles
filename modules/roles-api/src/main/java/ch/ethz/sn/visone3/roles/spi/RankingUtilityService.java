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
package ch.ethz.sn.visone3.roles.spi;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.Ranking;

public interface RankingUtilityService {

  Ranking universal(int size);

  Ranking identity(int size);

  Ranking fromEquivalence(ConstMapping.OfInt equivalence);

  Ranking infimum(Ranking r1, Ranking r2, boolean lazy);

  Ranking supremum(Ranking r1, Ranking r2, boolean lazy);

  Ranking closeTransitively(BinaryRelation relation);
}
