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
package ch.ethz.sn.visone3.roles.test.spi;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.ethz.sn.visone3.roles.blocks.Converters;
import ch.ethz.sn.visone3.roles.blocks.DistanceOperators;
import ch.ethz.sn.visone3.roles.blocks.Reducers;
import ch.ethz.sn.visone3.roles.blocks.RoleOperators;
import ch.ethz.sn.visone3.roles.distances.DistanceMatrices;
import ch.ethz.sn.visone3.roles.spi.CompositionLoader;
import ch.ethz.sn.visone3.roles.structures.BinaryRelations;
import ch.ethz.sn.visone3.roles.structures.Rankings;
import ch.ethz.sn.visone3.roles.structures.RelationBuilders;

import org.junit.jupiter.api.Test;

public class SpiTest {

  @Test
  public void testCompositionSpi() {
    // TODO: test actual composition methods
    assertNotNull(CompositionLoader.getInstance().getService());
  }

  @Test
  public void testConverters() {
    assertNotNull(Converters.equivalenceAsRanking());
    assertNotNull(Converters.equivalenceAsRelation());
    assertNotNull(Converters.strongComponentsAsEquivalence());
    assertNotNull(Converters.weakComponentsAsEquivalence());
    assertNotNull(Converters.thresholdDistances((i, j) -> 1));
    assertNotNull(Converters.rankingAsRelation());
    assertThrows(UnsupportedOperationException.class,
        () -> Converters.convert(SpiTest.class, Converters.class, null));
  }

  @Test
  public void testRoleOperators() {
    assertNotNull(RoleOperators.BINARYRELATION);
    assertNotNull(RoleOperators.RANKING);
    assertNotNull(RoleOperators.EQUIVALENCE);
  }

  @Test
  public void testDistanceOperators() {
    assertNotNull(DistanceOperators.BASIC);
    assertNotNull(DistanceOperators.BINARYRELATION);
    assertNotNull(DistanceOperators.EQUIVALENCE);
    assertNotNull(DistanceOperators.GENERIC);
    assertNotNull(DistanceOperators.RANKING);
  }

  @Test
  public void testReducers() {
    assertNotNull(Reducers.BINARYRELATION);
    assertNotNull(Reducers.DISTANCE);
    assertNotNull(Reducers.EQUIVALENCE);
    assertNotNull(Reducers.RANKING);
  }

  @Test
  public void testCorrespondenceBuilders() {
    assertNotNull(RelationBuilders.denseRelationBuilder(5));
    assertNotNull(RelationBuilders.denseSafeRankingBuilder(4));
    assertNotNull(RelationBuilders.denseUnsafeRankingBuilder(6));
  }

  @Test
  public void testBinaryRelationUtilityService() {
    // TODO: test more methods
    assertNotNull(BinaryRelations.identity(4));
  }

  @Test
  public void testRankingUtilityService() {
    // TODO: test more methods
    assertNotNull(Rankings.identity(5));
  }

  @Test
  public void testDistanceMatrices() {
    assertNotNull(DistanceMatrices.fromMatrix(new int[4][4]));
  }
}
