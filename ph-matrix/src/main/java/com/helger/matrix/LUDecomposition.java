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
 * LU Decomposition.
 * <p>
 * For an m-by-n matrix A with m &gt;= n, the LU decomposition is an m-by-n unit
 * lower triangular matrix L, an n-by-n upper triangular matrix U, and a
 * permutation vector piv of length m so that A(piv,:) = L*U. If m &lt; n, then
 * L is m-by-m and U is m-by-n.
 * </p>
 * <p>
 * The LU decompostion with pivoting always exists, even if the matrix is
 * singular, so the constructor will never fail. The primary use of the LU
 * decomposition is in the solution of square systems of simultaneous linear
 * equations. This will fail if isNonsingular() returns false.
 * </p>
 */
public class LUDecomposition implements Serializable
{
  /**
   * Array for internal storage of decomposition.
   *
   * @serial internal array storage.
   */
  private final double [] [] m_aLU;

  /**
   * Row and column dimensions, and pivot sign.
   *
   * @serial column dimension.
   * @serial row dimension.
   * @serial pivot sign.
   */
  private final int m_nRows;
  private final int m_nCols;

  private final int m_nPivSign;

  /**
   * Internal storage of pivot vector.
   *
   * @serial pivot vector.
   */
  private final int [] m_aPivot;

  /**
   * LU Decomposition Structure to access L, U and piv.
   *
   * @param aMatrix
   *        Rectangular matrix
   */

  public LUDecomposition (@Nonnull final Matrix aMatrix)
  {
    // Use a "left-looking", dot-product, Crout/Doolittle algorithm.
    m_aLU = aMatrix.getArrayCopy ();
    m_nRows = aMatrix.getRowDimension ();
    m_nCols = aMatrix.getColumnDimension ();
    m_aPivot = new int [m_nRows];
    for (int i = 0; i < m_nRows; i++)
      m_aPivot[i] = i;
    int nPivSign = 1;
    double [] aLUrowi;
    final double [] aLUcolj = new double [m_nRows];

    // Outer loop.
    for (int j = 0; j < m_nCols; j++)
    {
      // Make a copy of the j-th column to localize references.
      for (int i = 0; i < m_nRows; i++)
        aLUcolj[i] = m_aLU[i][j];

      // Apply previous transformations.
      for (int i = 0; i < m_nRows; i++)
      {
        aLUrowi = m_aLU[i];

        // Most of the time is spent in the following dot product.
        final int kmax = Math.min (i, j);
        double s = 0.0;
        for (int k = 0; k < kmax; k++)
          s += aLUrowi[k] * aLUcolj[k];

        aLUrowi[j] = aLUcolj[i] -= s;
      }

      // Find pivot and exchange if necessary.
      int p = j;
      for (int i = j + 1; i < m_nRows; i++)
        if (MathHelper.abs (aLUcolj[i]) > MathHelper.abs (aLUcolj[p]))
          p = i;
      final double [] aLUj = m_aLU[j];
      if (p != j)
      {
        final double [] aLUp = m_aLU[p];
        for (int k = 0; k < m_nCols; k++)
        {
          final double t = aLUp[k];
          aLUp[k] = aLUj[k];
          aLUj[k] = t;
        }
        final int k = m_aPivot[p];
        m_aPivot[p] = m_aPivot[j];
        m_aPivot[j] = k;
        nPivSign = -nPivSign;
      }

      // Compute multipliers.
      if (j < m_nRows && aLUj[j] != 0.0)
        for (int i = j + 1; i < m_nRows; i++)
          m_aLU[i][j] /= aLUj[j];
    }
    m_nPivSign = nPivSign;
  }

  /*
   * ------------------------ Temporary, experimental code.
   * ------------------------ *\ \** LU Decomposition, computed by Gaussian
   * elimination. <P> This constructor computes L and U with the "daxpy"-based
   * elimination algorithm used in LINPACK and MATLAB. In Java, we suspect the
   * dot-product, Crout algorithm will be faster. We have temporarily included
   * this constructor until timing experiments confirm this suspicion. <P>
   * @param A Rectangular matrix
   * @param linpackflag Use Gaussian elimination. Actual value ignored.
   * @return Structure to access L, U and piv.\ public LUDecomposition (Matrix
   * A, int linpackflag) { // Initialize. LU = A.getArrayCopy(); m =
   * A.getRowDimension(); n = A.getColumnDimension(); piv = new int[m]; for (int
   * i = 0; i < m; i++) { piv[i] = i; } pivsign = 1; // Main loop. for (int k =
   * 0; k < n; k++) { // Find pivot. int p = k; for (int i = k+1; i < m; i++) {
   * if (MathHelper.abs(LU[i][k]) > MathHelper.abs(LU[p][k])) { p = i; } } //
   * Exchange if necessary. if (p != k) { for (int j = 0; j < n; j++) { double t
   * = LU[p][j]; LU[p][j] = LU[k][j]; LU[k][j] = t; } int t = piv[p]; piv[p] =
   * piv[k]; piv[k] = t; pivsign = -pivsign; } // Compute multipliers and
   * eliminate k-th column. if (LU[k][k] != 0.0) { for (int i = k+1; i < m; i++)
   * { LU[i][k] /= LU[k][k]; for (int j = k+1; j < n; j++) { LU[i][j] -=
   * LU[i][k]*LU[k][j]; } } } } } \* ------------------------ End of temporary
   * code. ------------------------
   */

  /**
   * Is the matrix nonsingular?
   *
   * @return true if U, and hence A, is nonsingular.
   */
  public boolean isNonsingular ()
  {
    for (int j = 0; j < m_nCols; j++)
      if (m_aLU[j][j] == 0)
        return false;
    return true;
  }

  /**
   * Return lower triangular factor
   *
   * @return L
   */
  @Nonnull
  @ReturnsMutableCopy
  public Matrix getL ()
  {
    final Matrix aNewMatrix = new Matrix (m_nRows, m_nCols);
    final double [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final double [] aSrcRow = m_aLU[nRow];
      final double [] aDstRow = aNewArray[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        if (nRow > nCol)
          aDstRow[nCol] = aSrcRow[nCol];
        else
          if (nRow == nCol)
            aDstRow[nCol] = 1.0;
          else
            aDstRow[nCol] = 0.0;
    }
    return aNewMatrix;
  }

  /**
   * Return upper triangular factor
   *
   * @return U
   */
  @Nonnull
  public Matrix getU ()
  {
    final Matrix aNewMatrix = new Matrix (m_nCols, m_nCols);
    final double [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = 0; nRow < m_nCols; nRow++)
    {
      final double [] aSrcRow = m_aLU[nRow];
      final double [] aDstRow = aNewArray[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        if (nRow <= nCol)
          aDstRow[nCol] = aSrcRow[nCol];
        else
          aDstRow[nCol] = 0.0;
    }
    return aNewMatrix;
  }

  /**
   * Return pivot permutation vector
   *
   * @return piv
   */
  @Nonnull
  public int [] getPivot ()
  {
    final int [] p = new int [m_nRows];
    for (int i = 0; i < m_nRows; i++)
      p[i] = m_aPivot[i];
    return p;
  }

  /**
   * Return pivot permutation vector as a one-dimensional double array
   *
   * @return (double) piv
   */
  @Nonnull
  public double [] getDoublePivot ()
  {
    final double [] vals = new double [m_nRows];
    for (int i = 0; i < m_nRows; i++)
      vals[i] = m_aPivot[i];
    return vals;
  }

  /**
   * Determinant
   *
   * @return det(A)
   * @exception IllegalArgumentException
   *            Matrix must be square
   */
  public double det ()
  {
    if (m_nRows != m_nCols)
      throw new IllegalArgumentException ("Matrix must be square.");
    double d = m_nPivSign;
    for (int j = 0; j < m_nCols; j++)
      d *= m_aLU[j][j];
    return d;
  }

  /**
   * Solve A*X = B
   *
   * @param aMatrix
   *        A Matrix with as many rows as A and any number of columns.
   * @return X so that L*U*X = B(piv,:)
   * @exception IllegalArgumentException
   *            Matrix row dimensions must agree.
   * @exception RuntimeException
   *            Matrix is singular.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Matrix solve (@Nonnull final Matrix aMatrix)
  {
    if (aMatrix.getRowDimension () != m_nRows)
      throw new IllegalArgumentException ("Matrix row dimensions must agree.");
    if (!isNonsingular ())
      throw new IllegalStateException ("Matrix is singular.");

    // Copy right hand side with pivoting
    final int nCols = aMatrix.getColumnDimension ();
    final Matrix aNewMatrix = aMatrix.getMatrix (m_aPivot, 0, nCols - 1);
    final double [] [] aNewArray = aNewMatrix.internalGetArray ();

    // Solve L*Y = B(piv,:)
    for (int k = 0; k < m_nCols; k++)
    {
      final double [] aNewk = aNewArray[k];
      for (int i = k + 1; i < m_nCols; i++)
      {
        final double [] aLUi = m_aLU[i];
        final double [] aNewi = aNewArray[i];
        for (int j = 0; j < nCols; j++)
          aNewi[j] -= aNewk[j] * aLUi[k];
      }
    }
    // Solve U*X = Y;
    for (int k = m_nCols - 1; k >= 0; k--)
    {
      final double [] aLUk = m_aLU[k];
      final double [] aNewk = aNewArray[k];
      for (int j = 0; j < nCols; j++)
        aNewk[j] /= aLUk[k];
      for (int i = 0; i < k; i++)
      {
        final double [] aLUi = m_aLU[i];
        final double [] aNewi = aNewArray[i];
        for (int j = 0; j < nCols; j++)
          aNewi[j] -= aNewk[j] * aLUi[k];
      }
    }
    return aNewMatrix;
  }
}
