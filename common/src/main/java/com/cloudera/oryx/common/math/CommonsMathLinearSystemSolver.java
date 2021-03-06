/*
 * Copyright (c) 2013, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */

package com.cloudera.oryx.common.math;

import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RRQRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation based on {@link RRQRDecomposition} from Commons Math.
 * 
 * @author Sean Owen
 */
public final class CommonsMathLinearSystemSolver implements LinearSystemSolver {
  
  private static final Logger log = LoggerFactory.getLogger(CommonsMathLinearSystemSolver.class);
  
  @Override
  public Solver getSolver(RealMatrix M) {
    if (M == null) {
      return null;
    }
    RRQRDecomposition decomposition = new RRQRDecomposition(M, SINGULARITY_THRESHOLD);
    DecompositionSolver solver = decomposition.getSolver();
    if (solver.isNonSingular()) {
      return new CommonsMathSolver(solver);
    }
    // Otherwise try to report apparent rank
    int apparentRank = decomposition.getRank(0.01); // Better value?
    log.warn("{} x {} matrix is near-singular (threshold {}). Add more data or decrease the value of model.features, " +
             "to <= about {}",
             M.getRowDimension(), 
             M.getColumnDimension(), 
             SINGULARITY_THRESHOLD,
             apparentRank);
    throw new SingularMatrixSolverException(apparentRank, "Apparent rank: " + apparentRank);
  }  

  @Override
  public boolean isNonSingular(RealMatrix M) {
    QRDecomposition decomposition = new RRQRDecomposition(M, SINGULARITY_THRESHOLD);
    DecompositionSolver solver = decomposition.getSolver();
    return solver.isNonSingular();
  }  

}
