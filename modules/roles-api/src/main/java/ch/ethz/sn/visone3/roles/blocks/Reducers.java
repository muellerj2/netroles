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

package ch.ethz.sn.visone3.roles.blocks;

import ch.ethz.sn.visone3.lang.ConstMapping;
import ch.ethz.sn.visone3.roles.distances.IntDistanceMatrix;
import ch.ethz.sn.visone3.roles.spi.ReducerFactoryLoader;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.Ranking;

/**
 * Provides some combiners for common representations of role structures.
 *
 */
public class Reducers {

  private Reducers() {
  }

  /**
   * Factory to produce common reducers on a lattice.
   *
   * @param <T>
   *          the type of objects underlying the lattice
   */
  public interface Factory<T> {

    /**
     * Produces a reducer that represents the join in the lattice underlying {@code <T>}.
     * 
     * @return the reducer
     */
    RoleReducer<T> join();

    /**
     * Produces a reducer that represents the meet in the lattice underlying {@code <T>}.
     * 
     * @return the reducer
     */
    RoleReducer<T> meet();
  }

  /**
   * Factory for reducers on the lattice of equivalences.
   */
  public static final Factory<ConstMapping.OfInt> EQUIVALENCE = ReducerFactoryLoader.getInstance()
      .getFactory(ConstMapping.OfInt.class);

  /**
   * Factory for reducers on the lattice of rankings.
   */
  public static final Factory<Ranking> RANKING = ReducerFactoryLoader.getInstance()
      .getFactory(Ranking.class);

  /**
   * Factory for reducers on the lattice of binary relations.
   */
  public static final Factory<BinaryRelation> BINARYRELATION = ReducerFactoryLoader.getInstance()
      .getFactory(BinaryRelation.class);

  /**
   * Factory interface to produce common reducers on integer distance matrices.
   */
  public interface DistanceFactory {
    /**
     * Produces a reducer that represents addition on integer matrices.
     * 
     * @return the reducer
     */
    Reducer<IntDistanceMatrix> add();
  }

  /**
   * Factory for reducers on integer matrices.
   */
  public static final DistanceFactory DISTANCE = ReducerFactoryLoader.getInstance()
      .getDistanceFactory();

}
