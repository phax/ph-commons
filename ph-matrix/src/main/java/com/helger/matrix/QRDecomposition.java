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
import com.helger.commons.math.MathHelper;

/**
 * QR Decomposition.
 * <P>
 * For an m-by-n matrix A with m &ge; n, the QR decomposition is an m-by-n
 * orthogonal matrix Q and an n-by-n upper triangular matrix R so that A = Q*R.
 * </P>
 * <P>
 * The QR decompostion always exists, even if the matrix does not have full
 * rank, so the constructor will never fail. The primary use of the QR
 * decomposition is in the least squares solution of nonsquare systems of
 * simultaneous linear equations. This will fail if isFullRank() returns false.
 * </P>
 */
public class QRDecomposition implements Serializable
{
  /**
   * Array for internal storage of decomposition.
   *
   * @serial internal array storage.
   */
  private final double [] [] m_aQR;

  /**
   * Row dimension.
   *
   * @serial row dimension.
   */
  private final int m_nRows;

  /**
   * Column dimension.
   *
   * @serial column dimension.
   */
  private final int m_nCols;

  /**
   * Array for internal storage of diagonal of R.
   *
   * @serial diagonal of R.
   */
  private final double [] m_aRdiag;

  /**
   * QR Decomposition, computed by Householder reflections. Structure to access
   * R and the Householder vectors and compute Q.
   *
   * @param aMatrix
   *        Rectangular matrix
   */
  public QRDecomposition (@Nonnull final Matrix aMatrix)
  {
    // Initialize.
    m_aQR = aMatrix.getArrayCopy ();
    m_nRows = aMatrix.getRowDimension ();
    m_nCols = aMatrix.getColumnDimension ();
    m_aRdiag = new double [m_nCols];

    // Main loop.
    for (int k = 0; k < m_nCols; k++)
    {
      // Compute 2-norm of k-th column without under/overflow.
      double nrm = 0;
      for (int i = k; i < m_nRows; i++)
        nrm = MathHelper.hypot (nrm, m_aQR[i][k]);

      if (nrm != 0.0)
      {
        // Form k-th Householder vector.
        if (m_aQR[k][k] < 0)
        {
          nrm = -nrm;
        }
        for (int i = k; i < m_nRows; i++)
        {
          m_aQR[i][k] /= nrm;
        }
        m_aQR[k][k] += 1.0;

        // Apply transformation to remaining columns.
        for (int j = k + 1; j < m_nCols; j++)
        {
          double s = 0.0;
          for (int i = k; i < m_nRows; i++)
          {
            s += m_aQR[i][k] * m_aQR[i][j];
          }
          s = -s / m_aQR[k][k];
          for (int i = k; i < m_nRows; i++)
          {
            m_aQR[i][j] += s * m_aQR[i][k];
          }
        }
      }
      m_aRdiag[k] = -nrm;
    }
  }

  /**
   * Is the matrix full rank?
   *
   * @return true if R, and hence A, has full rank.
   */
  public boolean isFullRank ()
  {
    for (int j = 0; j < m_nCols; j++)
      if (m_aRdiag[j] == 0)
        return false;
    return true;
  }

  /**
   * Return the Householder vectors
   *
   * @return Lower trapezoidal matrix whose columns define the reflections
   */
  @Nonnull
  @ReturnsMutableCopy
  public Matrix getH ()
  {
    final Matrix aNewMatrix = new Matrix (m_nRows, m_nCols);
    final double [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final double [] aSrcRow = m_aQR[nRow];
      final double [] aDstRow = aNewArray[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
      {
        aDstRow[nCol] = (nRow >= nCol ? aSrcRow[nCol] : 0d);
      }
    }
    return aNewMatrix;
  }

  /**
   * Return the upper triangular factor
   *
   * @return R
   */
  @Nonnull
  @ReturnsMutableCopy
  public Matrix getR ()
  {
    final Matrix aNewMatrix = new Matrix (m_nCols, m_nCols);
    final double [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = 0; nRow < m_nCols; nRow++)
    {
      final double [] aDstRow = aNewArray[nRow];
      for (int j = 0; j < m_nCols; j++)
      {
        if (nRow < j)
          aDstRow[j] = m_aQR[nRow][j];
        else
          if (nRow == j)
            aDstRow[j] = m_aRdiag[nRow];
          else
            aDstRow[j] = 0.0;
      }
    }
    return aNewMatrix;
  }

  /**
   * Generate and return the (economy-sized) orthogonal factor
   *
   * @return Q
   */
  @Nonnull
  @ReturnsMutableCopy
  public Matrix getQ ()
  {
    final Matrix aNewMatrix = new Matrix (m_nRows, m_nCols);
    final double [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int k = m_nCols - 1; k >= 0; k--)
    {
      final double [] aQRk = m_aQR[k];
      for (int nRow = 0; nRow < m_nRows; nRow++)
        aNewArray[nRow][k] = 0.0;
      aNewArray[k][k] = 1.0;
      for (int j = k; j < m_nCols; j++)
      {
        if (aQRk[k] != 0)
        {
          double s = 0.0;
          for (int i = k; i < m_nRows; i++)
            s += m_aQR[i][k] * aNewArray[i][j];
          s = -s / aQRk[k];
          for (int i = k; i < m_nRows; i++)
          {
            aNewArray[i][j] += s * m_aQR[i][k];
          }
        }
      }
    }
    return aNewMatrix;
  }

  /**
   * Least squares solution of A*X = B
   *
   * @param aMatrix
   *        A Matrix with as many rows as A and any number of columns.
   * @return X that minimizes the two norm of Q*R*X-B.
   * @exception IllegalArgumentException
   *            Matrix row dimensions must agree.
   * @exception RuntimeException
   *            Matrix is rank deficient.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Matrix solve (@Nonnull final Matrix aMatrix)
  {
    if (aMatrix.getRowDimension () != m_nRows)
      throw new IllegalArgumentException ("Matrix row dimensions must agree.");
    if (!isFullRank ())
      throw new IllegalStateException ("Matrix is rank deficient.");

    // Copy right hand side
    final int nCols = aMatrix.getColumnDimension ();
    final double [] [] aArray = aMatrix.getArrayCopy ();

    // Compute Y = transpose(Q)*B
    for (int k = 0; k < m_nCols; k++)
    {
      final double [] aQRk = m_aQR[k];
      for (int j = 0; j < nCols; j++)
      {
        double s = 0.0;
        for (int i = k; i < m_nRows; i++)
          s += m_aQR[i][k] * aArray[i][j];
        s = -s / aQRk[k];
        for (int i = k; i < m_nRows; i++)
          aArray[i][j] += s * m_aQR[i][k];
      }
    }
    // Solve R*X = Y;
    for (int k = m_nCols - 1; k >= 0; k--)
    {
      final double [] aArrayk = aArray[k];
      for (int j = 0; j < nCols; j++)
        aArrayk[j] /= m_aRdiag[k];
      for (int i = 0; i < k; i++)
      {
        final double [] aSrcRow = m_aQR[i];
        final double [] aDstRow = aArray[i];
        for (int j = 0; j < nCols; j++)
          aDstRow[j] -= aArrayk[j] * aSrcRow[k];
      }
    }
    return new Matrix (aArray, m_nCols, nCols).getMatrix (0, m_nCols - 1, 0, nCols - 1);
  }
}
