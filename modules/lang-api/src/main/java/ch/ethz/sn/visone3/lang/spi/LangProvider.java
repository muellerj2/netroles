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
package ch.ethz.sn.visone3.lang.spi;

import java.util.ServiceLoader;

/**
 * Connects the implementation to the API. Loads a connected service and forwards all requests to
 * this service.
 *
 */
public class LangProvider implements LangService {

  private static final class Helper {
    private static final LangProvider INSTANCE = new LangProvider();
  }

  /**
   * Gets the singleton.
   * 
   * @return the singleton.
   */
  public static LangProvider getInstance() {
    return Helper.INSTANCE;
  }

  private LangService impl;

  private LangProvider() {
    impl = getService();
  }

  private static LangService getService() {
    return ServiceLoader.load(LangService.class).iterator().next();
  }

  @Override
  public String getName() {
    return impl.getName();
  }

  @Override
  public MappingsFacade mappings() {
    return impl.mappings();
  }

  @Override
  public AlgorithmsFacade algos() {
    return impl.algos();
  }

  @Override
  public ContainersFacade containers() {
    return impl.containers();
  }

  @Override
  public IteratorFacade iterators() {
    return impl.iterators();
  }

}
