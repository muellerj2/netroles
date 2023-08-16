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
 * Loader for the registered relation builder service.
 */
public class RelationBuilderLoader {

  private RelationBuilderLoader() {

  }

  /**
   * Returns the registered relation builder service.
   * 
   * @return the registered relation builder service.
   * @throws NoSuchElementException if no relation builder service is registered.
   */
  public static RelationBuilderService getService() {
    return ServiceLoader.load(RelationBuilderService.class).iterator().next();
  }
}
