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

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

/**
 * Loader for basic methods on rankings and binary relations.
 */
public class RelationUtilityServiceLoader {

  private RelationUtilityServiceLoader() {

  }

  /**
   * Returns the registered service for basic methods on binary relations.
   * 
   * @return the service for basic methods on binary relations.
   * @throws NoSuchElementException if no service is registered.
   */
  public static BinaryRelationUtilityService getBinaryRelationService() {
    return ServiceLoader.load(BinaryRelationUtilityService.class).iterator().next();
  }

  /**
   * Returns the registered service for basic methods on rankings.
   * 
   * @return the service for basic methods on rankings.
   * @throws NoSuchElementException if no service is registered.
   */
  public static RankingUtilityService getRankingService() {
    return ServiceLoader.load(RankingUtilityService.class).iterator().next();
  }
}
