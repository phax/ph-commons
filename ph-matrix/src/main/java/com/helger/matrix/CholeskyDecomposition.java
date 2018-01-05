/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.matrix;

import java.io.Serializable;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.equals.EqualsHelper;

/**
 * Cholesky Decomposition.
 * <P>
 * For a symmetric, positive definite matrix A, the Cholesky decomposition is an
 * lower triangular matrix L so that A = L*L'.
 * <P>
 * If the matrix is not symmetric or positive definite, the constructor returns
 * a partial decomposition and sets an internal flag that may be queried by the
 * isSPD() method.
 */
public class CholeskyDecomposition implements Serializable
{
  /**
   * Array for internal storage of decomposition.
   *
   * @serial internal array storage.
   */
  private final double [] [] m_aData;

  /**
   * Row and column dimension (square matrix).
   *
   * @serial matrix dimension.
   */
  private final int m_nDim;

  /**
   * Symmetric and positive definite flag.
   *
   * @serial is symmetric and positive definite flag.
   */
  private final boolean m_bIsSPD;

  /**
   * Cholesky algorithm for symmetric and positive definite matrix. Structure to
   * access L and isspd flag.
   *
   * @param aMatrix
   *        Square, symmetric matrix.
   */
  public CholeskyDecomposition (@Nonnull final Matrix aMatrix)
  {
    // Initialize.
    final double [] [] aArray = aMatrix.internalGetArray ();
    m_nDim = aMatrix.getRowDimension ();
    m_aData = new double [m_nDim] [m_nDim];
    boolean bIsSPD = (aMatrix.getColumnDimension () == m_nDim);
    // Main loop.
    for (int nRow = 0; nRow < m_nDim; nRow++)
    {
      final double [] aArrayJ = aArray[nRow];
      final double [] aRowJ = m_aData[nRow];
      double d = 0.0;
      for (int nCol = 0; nCol < nRow; nCol++)
      {
        final double [] aRowK = m_aData[nCol];
        double s = 0.0;
        for (int i = 0; i < nCol; i++)
          s += aRowK[i] * aRowJ[i];
        aRowJ[nCol] = s = (aArrayJ[nCol] - s) / aRowK[nCol];
        d += s * s;
        bIsSPD = bIsSPD && EqualsHelper.equals (aArray[nCol][nRow], aArrayJ[nCol]);
      }
      d = aArrayJ[nRow] - d;
      bIsSPD = bIsSPD && (d > 0.0);
      aRowJ[nRow] = Math.sqrt (Math.max (d, 0.0));
      for (int k = nRow + 1; k < m_nDim; k++)
        aRowJ[k] = 0.0;
    }
    m_bIsSPD = bIsSPD;
  }

  /*
   * ------------------------ Temporary, experimental code.
   * ------------------------ *\ \** Right Triangular Cholesky Decomposition.
   * <P> For a symmetric, positive definite matrix A, the Right Cholesky
   * decomposition is an upper triangular matrix R so that A = R'*R. This
   * constructor computes R with the Fortran inspired column oriented algorithm
   * used in LINPACK and MATLAB. In Java, we suspect a row oriented, lower
   * triangular decomposition is faster. We have temporarily included this
   * constructor here until timing experiments confirm this suspicion.\ \**
   * Array for internal storage of right triangular decomposition. **\ private
   * transient double[][] R; \** Cholesky algorithm for symmetric and positive
   * definite matrix.
   * @param A Square, symmetric matrix.
   * @param rightflag Actual value ignored.
   * @return Structure to access R and isspd flag.\ public CholeskyDecomposition
   * (Matrix Arg, int rightflag) { // Initialize. double[][] A = Arg.getArray();
   * n = Arg.getColumnDimension(); R = new double[n][n]; isspd =
   * (Arg.getColumnDimension() == n); // Main loop. for (int j = 0; j < n; j++)
   * { double d = 0.0; for (int k = 0; k < j; k++) { double s = A[k][j]; for
   * (int i = 0; i < k; i++) { s = s - R[i][k]*R[i][j]; } R[k][j] = s =
   * s/R[k][k]; d = d + s*s; isspd = isspd & (A[k][j] == A[j][k]); } d = A[j][j]
   * - d; isspd = isspd & (d > 0.0); R[j][j] = Math.sqrt(Math.max(d,0.0)); for
   * (int k = j+1; k < n; k++) { R[k][j] = 0.0; } } } \** Return upper
   * triangular factor.
   * @return R\ public Matrix getR () { return new Matrix(R,n,n); } \*
   * ------------------------ End of temporary code. ------------------------
   */

  /**
   * Is the matrix symmetric and positive definite?
   *
   * @return true if A is symmetric and positive definite.
   */
  public boolean isSPD ()
  {
    return m_bIsSPD;
  }

  /**
   * Return triangular factor.
   *
   * @return L
   */
  @Nonnull
  @ReturnsMutableCopy
  public Matrix getL ()
  {
    return new Matrix (m_aData, m_nDim, m_nDim);
  }

  /**
   * Solve A*X = B
   *
   * @param aMatrix
   *        A Matrix with as many rows as A and any number of columns.
   * @return X so that L*L'*X = B
   * @exception IllegalArgumentException
   *            Matrix row dimensions must agree.
   * @exception RuntimeException
   *            Matrix is not symmetric positive definite.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Matrix solve (@Nonnull final Matrix aMatrix)
  {
    if (aMatrix.getRowDimension () != m_nDim)
      throw new IllegalArgumentException ("Matrix row dimensions must agree.");
    if (!m_bIsSPD)
      throw new IllegalStateException ("Matrix is not symmetric positive definite.");

    // Copy right hand side.
    final double [] [] aArray = aMatrix.getArrayCopy ();
    final int nCols = aMatrix.getColumnDimension ();

    // Solve L*Y = B;
    for (int k = 0; k < m_nDim; k++)
    {
      final double [] aDataK = m_aData[k];
      final double [] aArrayK = aArray[k];
      for (int j = 0; j < nCols; j++)
      {
        for (int i = 0; i < k; i++)
        {
          aArrayK[j] -= aArray[i][j] * aDataK[i];
        }
        aArrayK[j] /= aDataK[k];
      }
    }

    // Solve L'*X = Y;
    for (int k = m_nDim - 1; k >= 0; k--)
    {
      final double [] aDataK = m_aData[k];
      final double [] aArrayK = aArray[k];
      for (int j = 0; j < nCols; j++)
      {
        for (int i = k + 1; i < m_nDim; i++)
        {
          aArrayK[j] -= aArray[i][j] * m_aData[i][k];
        }
        aArrayK[j] /= aDataK[k];
      }
    }

    return new Matrix (aArray, m_nDim, nCols);
  }
}
