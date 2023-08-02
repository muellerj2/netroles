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
import ch.ethz.sn.visone3.roles.blocks.bundles.RoleOperatorFactoryBundle;
import ch.ethz.sn.visone3.roles.blocks.factories.BasicCloseableRoleOperatorFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.BasicRoleOperatorFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.BasicSymmetrizableRoleOperatorFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.EquitableLooseGenericRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.EquitableLooseRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.RoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.VariableGenericRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.VariableRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.blocks.factories.WeakStructuralRoleOperatorBuilderFactory;
import ch.ethz.sn.visone3.roles.spi.RoleOperatorBundleLoader;
import ch.ethz.sn.visone3.roles.structures.BinaryRelation;
import ch.ethz.sn.visone3.roles.structures.Ranking;

/**
 * Provides factories to produce instances of {@link RoleOperator} for common role notions.
 *
 */
public class RoleOperators {

  private RoleOperators() {
  }

  /**
   * Factory bundle for role operators on the lattice of equivalences.
   */
  @SuppressWarnings("unchecked")
  public static final RoleOperatorFactoryBundle<ConstMapping.OfInt, //
      EquitableLooseRoleOperatorBuilderFactory<ConstMapping.OfInt>, //
      EquitableLooseGenericRoleOperatorBuilderFactory<ConstMapping.OfInt>, //
      BasicRoleOperatorFactory<ConstMapping.OfInt>, //
      WeakStructuralRoleOperatorBuilderFactory<ConstMapping.OfInt>> EQUIVALENCE = //
          RoleOperatorBundleLoader.getInstance().getBundle(ConstMapping.OfInt.class, //
              (Class<EquitableLooseRoleOperatorBuilderFactory<ConstMapping.OfInt>>) (Class<?>) //
              EquitableLooseRoleOperatorBuilderFactory.class, //
              (Class<EquitableLooseGenericRoleOperatorBuilderFactory<ConstMapping.OfInt>>) //
              (Class<?>) EquitableLooseGenericRoleOperatorBuilderFactory.class, //
              (Class<BasicRoleOperatorFactory<ConstMapping.OfInt>>) //
              (Class<?>) BasicRoleOperatorFactory.class, //
              (Class<WeakStructuralRoleOperatorBuilderFactory<ConstMapping.OfInt>>) //
              (Class<?>) WeakStructuralRoleOperatorBuilderFactory.class);
  /**
   * Factory bundle for role operators on the lattice of rankings.
   */
  @SuppressWarnings("unchecked")
  public static final RoleOperatorFactoryBundle<Ranking, //
      EquitableLooseRoleOperatorBuilderFactory<Ranking>, //
      EquitableLooseGenericRoleOperatorBuilderFactory<Ranking>, //
      BasicSymmetrizableRoleOperatorFactory<Ranking>, //
      WeakStructuralRoleOperatorBuilderFactory<Ranking>> RANKING = //
          RoleOperatorBundleLoader.getInstance().getBundle(Ranking.class,
              (Class<EquitableLooseRoleOperatorBuilderFactory<Ranking>>) //
              (Class<?>) EquitableLooseRoleOperatorBuilderFactory.class, //
              (Class<EquitableLooseGenericRoleOperatorBuilderFactory<Ranking>>) //
              (Class<?>) EquitableLooseGenericRoleOperatorBuilderFactory.class, //
              (Class<BasicSymmetrizableRoleOperatorFactory<Ranking>>) //
              (Class<?>) BasicSymmetrizableRoleOperatorFactory.class, //
              (Class<WeakStructuralRoleOperatorBuilderFactory<Ranking>>) //
              (Class<?>) WeakStructuralRoleOperatorBuilderFactory.class);
  /**
   * Factory bundle for role operators on the lattice of binary relations.
   */
  @SuppressWarnings("unchecked")
  public static final RoleOperatorFactoryBundle<BinaryRelation, //
      VariableRoleOperatorBuilderFactory<BinaryRelation>, //
      VariableGenericRoleOperatorBuilderFactory<BinaryRelation>, //
      BasicCloseableRoleOperatorFactory<BinaryRelation>, //
      RoleOperatorBuilderFactory<BinaryRelation>> BINARYRELATION = //
          RoleOperatorBundleLoader.getInstance().getBundle(BinaryRelation.class,
              (Class<VariableRoleOperatorBuilderFactory<BinaryRelation>>) //
              (Class<?>) VariableRoleOperatorBuilderFactory.class, //
              (Class<VariableGenericRoleOperatorBuilderFactory<BinaryRelation>>) //
              (Class<?>) VariableGenericRoleOperatorBuilderFactory.class, //
              (Class<BasicCloseableRoleOperatorFactory<BinaryRelation>>) //
              (Class<?>) BasicCloseableRoleOperatorFactory.class, //
              (Class<RoleOperatorBuilderFactory<BinaryRelation>>) //
              (Class<?>) RoleOperatorBuilderFactory.class);
}
