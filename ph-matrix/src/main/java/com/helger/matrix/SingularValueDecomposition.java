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
import java.util.Arrays;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.math.MathHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Singular Value Decomposition.
 * <P>
 * For an m-by-n matrix A with m &gt;= n, the singular value decomposition is an
 * m-by-n orthogonal matrix U, an n-by-n diagonal matrix S, and an n-by-n
 * orthogonal matrix V so that A = U*S*V'.
 * </P>
 * <P>
 * The singular values, sigma[k] = S[k][k], are ordered so that sigma[0] &gt;=
 * sigma[1] &gt;= ... &gt;= sigma[n-1].
 * </P>
 * <P>
 * The singular value decomposition always exists, so the constructor will never
 * fail. The matrix condition number and the effective numerical rank can be
 * computed from this decomposition.
 * </P>
 */
public class SingularValueDecomposition implements Serializable
{
  private static final double EPSILON = Math.pow (2.0, -52.0);
  private static final double TINY = Math.pow (2.0, -966.0);

  /**
   * Arrays for internal storage of U and V.
   *
   * @serial internal storage of U.
   * @serial internal storage of V.
   */
  private final double [] [] m_aU;
  private final double [] [] m_aV;

  /**
   * Array for internal storage of singular values.
   *
   * @serial internal storage of singular values.
   */
  private final double [] m_aData;

  /**
   * Row dimensions.
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
   * Construct the singular value decomposition Structure to access U, S and V.
   *
   * @param aMatrix
   *        Rectangular matrix
   */
  public SingularValueDecomposition (@Nonnull final Matrix aMatrix)
  {
    // Derived from LINPACK code.
    // Initialize.
    final double [] [] aArray = aMatrix.getArrayCopy ();
    m_nRows = aMatrix.getRowDimension ();
    m_nCols = aMatrix.getColumnDimension ();

    /*
     * Apparently the failing cases are only a proper subset of (m<n), so let's
     * not throw error. Correct fix to come later? if (m<n) { throw new
     * IllegalArgumentException("Jama SVD only works for m >= n"); }
     */
    final int nu = Math.min (m_nRows, m_nCols);
    m_aData = new double [Math.min (m_nRows + 1, m_nCols)];
    m_aU = new double [m_nRows] [nu];
    m_aV = new double [m_nCols] [m_nCols];
    final double [] e = new double [m_nCols];
    final double [] work = new double [m_nRows];
    final boolean wantu = true;
    final boolean wantv = true;

    // Reduce A to bidiagonal form, storing the diagonal elements
    // in s and the super-diagonal elements in e.
    final int nct = Math.min (m_nRows - 1, m_nCols);
    final int nrt = Math.max (0, Math.min (m_nCols - 2, m_nRows));
    for (int k = 0; k < Math.max (nct, nrt); k++)
    {
      if (k < nct)
      {
        // Compute the transformation for the k-th column and
        // place the k-th diagonal in s[k].
        // Compute 2-norm of k-th column without under/overflow.
        m_aData[k] = 0;
        for (int i = k; i < m_nRows; i++)
          m_aData[k] = MathHelper.hypot (m_aData[k], aArray[i][k]);
        if (m_aData[k] != 0.0)
        {
          if (aArray[k][k] < 0.0)
            m_aData[k] = -m_aData[k];
          for (int i = k; i < m_nRows; i++)
            aArray[i][k] /= m_aData[k];
          aArray[k][k] += 1.0;
        }
        m_aData[k] = -m_aData[k];
      }
      for (int j = k + 1; j < m_nCols; j++)
      {
        if (k < nct && m_aData[k] != 0.0)
        {
          // Apply the transformation.
          double t = 0;
          for (int i = k; i < m_nRows; i++)
            t += aArray[i][k] * aArray[i][j];
          t = -t / aArray[k][k];
          for (int i = k; i < m_nRows; i++)
            aArray[i][j] += t * aArray[i][k];
        }

        // Place the k-th row of A into e for the
        // subsequent calculation of the row transformation.
        e[j] = aArray[k][j];
      }
      if (wantu && k < nct)
      {
        // Place the transformation in U for subsequent back
        // multiplication.

        for (int i = k; i < m_nRows; i++)
          m_aU[i][k] = aArray[i][k];
      }
      if (k < nrt)
      {
        // Compute the k-th row transformation and place the
        // k-th super-diagonal in e[k].
        // Compute 2-norm without under/overflow.
        e[k] = 0;
        for (int i = k + 1; i < m_nCols; i++)
          e[k] = MathHelper.hypot (e[k], e[i]);
        if (e[k] != 0.0)
        {
          if (e[k + 1] < 0.0)
            e[k] = -e[k];
          for (int i = k + 1; i < m_nCols; i++)
            e[i] /= e[k];
          e[k + 1] += 1.0;
        }
        e[k] = -e[k];
        if ((k + 1 < m_nRows) && (e[k] != 0.0))
        {
          // Apply the transformation.
          for (int i = k + 1; i < m_nRows; i++)
            work[i] = 0.0;

          for (int j = k + 1; j < m_nCols; j++)
            for (int i = k + 1; i < m_nRows; i++)
              work[i] += e[j] * aArray[i][j];

          for (int j = k + 1; j < m_nCols; j++)
          {
            final double t = -e[j] / e[k + 1];
            for (int i = k + 1; i < m_nRows; i++)
              aArray[i][j] += t * work[i];
          }
        }
        if (wantv)
        {
          // Place the transformation in V for subsequent
          // back multiplication.
          for (int i = k + 1; i < m_nCols; i++)
            m_aV[i][k] = e[i];
        }
      }
    }

    // Set up the final bidiagonal matrix or order p.
    int p = Math.min (m_nCols, m_nRows + 1);
    if (nct < m_nCols)
      m_aData[nct] = aArray[nct][nct];
    if (m_nRows < p)
      m_aData[p - 1] = 0.0;
    if (nrt + 1 < p)
      e[nrt] = aArray[nrt][p - 1];
    e[p - 1] = 0.0;

    // If required, generate U.
    if (wantu)
    {
      for (int j = nct; j < nu; j++)
      {
        for (int i = 0; i < m_nRows; i++)
          m_aU[i][j] = 0.0;
        m_aU[j][j] = 1.0;
      }
      for (int k = nct - 1; k >= 0; k--)
      {
        if (m_aData[k] != 0.0)
        {
          for (int j = k + 1; j < nu; j++)
          {
            double t = 0;
            for (int i = k; i < m_nRows; i++)
              t += m_aU[i][k] * m_aU[i][j];
            t = -t / m_aU[k][k];
            for (int i = k; i < m_nRows; i++)
              m_aU[i][j] += t * m_aU[i][k];
          }
          for (int i = k; i < m_nRows; i++)
            m_aU[i][k] = -m_aU[i][k];
          m_aU[k][k] = 1.0 + m_aU[k][k];
          for (int i = 0; i < k - 1; i++)
            m_aU[i][k] = 0.0;
        }
        else
        {
          for (int i = 0; i < m_nRows; i++)
            m_aU[i][k] = 0.0;
          m_aU[k][k] = 1.0;
        }
      }
    }

    // If required, generate V.
    if (wantv)
    {
      for (int k = m_nCols - 1; k >= 0; k--)
      {
        if ((k < nrt) && (e[k] != 0.0))
        {
          for (int j = k + 1; j < nu; j++)
          {
            double t = 0;
            for (int i = k + 1; i < m_nCols; i++)
              t += m_aV[i][k] * m_aV[i][j];
            t = -t / m_aV[k + 1][k];
            for (int i = k + 1; i < m_nCols; i++)
              m_aV[i][j] += t * m_aV[i][k];
          }
        }
        for (int i = 0; i < m_nCols; i++)
          m_aV[i][k] = 0.0;
        m_aV[k][k] = 1.0;
      }
    }

    // Main iteration loop for the singular values.
    final int pp = p - 1;
    while (p > 0)
    {
      int k;
      int kase;

      // Here is where a test for too many iterations would go.

      // This section of the program inspects for
      // negligible elements in the s and e arrays. On
      // completion the variables kase and k are set as follows.

      // kase = 1 if s(p) and e[k-1] are negligible and k<p
      // kase = 2 if s(k) is negligible and k<p
      // kase = 3 if e[k-1] is negligible, k<p, and
      // s(k), ..., s(p) are not negligible (qr step).
      // kase = 4 if e(p-1) is negligible (convergence).

      for (k = p - 2; k >= -1; k--)
      {
        if (k == -1)
          break;
        if (MathHelper.abs (e[k]) <= TINY + EPSILON * (MathHelper.abs (m_aData[k]) + MathHelper.abs (m_aData[k + 1])))
        {
          e[k] = 0.0;
          break;
        }
      }
      if (k == p - 2)
        kase = 4;
      else
      {
        int ks;
        for (ks = p - 1; ks >= k; ks--)
        {
          if (ks == k)
            break;
          final double t = (ks != p ? MathHelper.abs (e[ks]) : 0.) + (ks != k + 1 ? MathHelper.abs (e[ks - 1]) : 0.);
          if (MathHelper.abs (m_aData[ks]) <= TINY + EPSILON * t)
          {
            m_aData[ks] = 0.0;
            break;
          }
        }
        if (ks == k)
          kase = 3;
        else
          if (ks == p - 1)
            kase = 1;
          else
          {
            kase = 2;
            k = ks;
          }
      }
      k++;

      // Perform the task indicated by kase.

      switch (kase)
      {
        // Deflate negligible s(p).
        case 1:
        {
          double f = e[p - 2];
          e[p - 2] = 0.0;
          for (int j = p - 2; j >= k; j--)
          {
            double t = MathHelper.hypot (m_aData[j], f);
            final double cs = m_aData[j] / t;
            final double sn = f / t;
            m_aData[j] = t;
            if (j != k)
            {
              f = -sn * e[j - 1];
              e[j - 1] *= cs;
            }
            if (wantv)
            {
              for (int i = 0; i < m_nCols; i++)
              {
                final double q = m_aV[i][p - 1];
                t = cs * m_aV[i][j] + sn * q;
                m_aV[i][p - 1] = -sn * m_aV[i][j] + cs * q;
                m_aV[i][j] = t;
              }
            }
          }
          break;
        }

        // Split at negligible s(k).
        case 2:
        {
          double f = e[k - 1];
          e[k - 1] = 0.0;
          for (int j = k; j < p; j++)
          {
            double t = MathHelper.hypot (m_aData[j], f);
            final double cs = m_aData[j] / t;
            final double sn = f / t;
            m_aData[j] = t;
            f = -sn * e[j];
            e[j] = cs * e[j];
            if (wantu)
            {
              for (int i = 0; i < m_nRows; i++)
              {
                final double q = m_aU[i][k - 1];
                t = cs * m_aU[i][j] + sn * q;
                m_aU[i][k - 1] = -sn * m_aU[i][j] + cs * q;
                m_aU[i][j] = t;
              }
            }
          }
          break;
        }

        // Perform one qr step.
        case 3:
        {

          // Calculate the shift.
          final double scale = MathHelper.getMaxDouble (MathHelper.abs (m_aData[p - 1]),
                                                        MathHelper.abs (m_aData[p - 2]),
                                                        MathHelper.abs (e[p - 2]),
                                                        MathHelper.abs (m_aData[k]),
                                                        MathHelper.abs (e[k]));
          final double sp = m_aData[p - 1] / scale;
          final double spm1 = m_aData[p - 2] / scale;
          final double epm1 = e[p - 2] / scale;
          final double sk = m_aData[k] / scale;
          final double ek = e[k] / scale;
          final double b = ((spm1 + sp) * (spm1 - sp) + epm1 * epm1) / 2.0;
          final double c = (sp * epm1) * (sp * epm1);
          double shift = 0.0;
          if ((b != 0.0) || (c != 0.0))
          {
            shift = Math.sqrt (b * b + c);
            if (b < 0.0)
              shift = -shift;
            shift = c / (b + shift);
          }
          double f = (sk + sp) * (sk - sp) + shift;
          double g = sk * ek;

          // Chase zeros.
          for (int j = k; j < p - 1; j++)
          {
            double t = MathHelper.hypot (f, g);
            double cs = f / t;
            double sn = g / t;
            if (j != k)
              e[j - 1] = t;
            f = cs * m_aData[j] + sn * e[j];
            e[j] = cs * e[j] - sn * m_aData[j];
            g = sn * m_aData[j + 1];
            m_aData[j + 1] = cs * m_aData[j + 1];
            if (wantv)
            {
              for (int i = 0; i < m_nCols; i++)
              {
                t = cs * m_aV[i][j] + sn * m_aV[i][j + 1];
                m_aV[i][j + 1] = -sn * m_aV[i][j] + cs * m_aV[i][j + 1];
                m_aV[i][j] = t;
              }
            }
            t = MathHelper.hypot (f, g);
            cs = f / t;
            sn = g / t;
            m_aData[j] = t;
            f = cs * e[j] + sn * m_aData[j + 1];
            m_aData[j + 1] = -sn * e[j] + cs * m_aData[j + 1];
            g = sn * e[j + 1];
            e[j + 1] = cs * e[j + 1];
            if (wantu && (j < m_nRows - 1))
            {
              for (int i = 0; i < m_nRows; i++)
              {
                final double q = m_aU[i][j + 1];
                t = cs * m_aU[i][j] + sn * q;
                m_aU[i][j + 1] = -sn * m_aU[i][j] + cs * q;
                m_aU[i][j] = t;
              }
            }
          }
          e[p - 2] = f;
          break;
        }

        // Convergence.
        case 4:
        {
          // Make the singular values positive.
          if (m_aData[k] <= 0.0)
          {
            m_aData[k] = m_aData[k] < 0.0 ? -m_aData[k] : 0.0;
            if (wantv)
            {
              for (int i = 0; i <= pp; i++)
                m_aV[i][k] = -m_aV[i][k];
            }
          }

          // Order the singular values.
          while (k < pp)
          {
            if (m_aData[k] >= m_aData[k + 1])
              break;
            double t = m_aData[k];
            m_aData[k] = m_aData[k + 1];
            m_aData[k + 1] = t;
            if (wantv && (k < m_nCols - 1))
            {
              for (int i = 0; i < m_nCols; i++)
              {
                t = m_aV[i][k + 1];
                m_aV[i][k + 1] = m_aV[i][k];
                m_aV[i][k] = t;
              }
            }
            if (wantu && (k < m_nRows - 1))
            {
              for (int i = 0; i < m_nRows; i++)
              {
                t = m_aU[i][k + 1];
                m_aU[i][k + 1] = m_aU[i][k];
                m_aU[i][k] = t;
              }
            }
            k++;
          }
          p--;
          break;
        }
        default:
          throw new IllegalStateException ();
      }
    }
  }

  /*
   * ------------------------ Public Methods ------------------------
   */

  /**
   * Return the left singular vectors
   *
   * @return U
   */
  @Nonnull
  @ReturnsMutableCopy
  public Matrix getU ()
  {
    return new Matrix (m_aU, m_nRows, Math.min (m_nRows + 1, m_nCols));
  }

  /**
   * Return the right singular vectors
   *
   * @return V
   */
  @Nonnull
  @ReturnsMutableCopy
  public Matrix getV ()
  {
    return new Matrix (m_aV, m_nCols, m_nCols);
  }

  /**
   * Return the one-dimensional array of singular values
   *
   * @return diagonal of S.
   */
  @Nonnull
  @SuppressFBWarnings ("EI_EXPOSE_REP")
  public double [] getSingularValues ()
  {
    return m_aData;
  }

  /**
   * Return the diagonal matrix of singular values
   *
   * @return S
   */
  @Nonnull
  @ReturnsMutableCopy
  public Matrix getS ()
  {
    final Matrix aNewMatrix = new Matrix (m_nCols, m_nCols);
    final double [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int i = 0; i < m_nCols; i++)
    {
      Arrays.fill (aNewArray[i], 0.0);
      aNewArray[i][i] = m_aData[i];
    }
    return aNewMatrix;
  }

  /**
   * Two norm
   *
   * @return max(S)
   */
  public double norm2 ()
  {
    return m_aData[0];
  }

  /**
   * Two norm condition number
   *
   * @return max(S)/min(S)
   */
  public double cond ()
  {
    return m_aData[0] / m_aData[Math.min (m_nRows, m_nCols) - 1];
  }

  /**
   * Effective numerical matrix rank
   *
   * @return Number of nonnegligible singular values.
   */
  public int rank ()
  {
    final double tol = Math.max (m_nRows, m_nCols) * m_aData[0] * EPSILON;
    int r = 0;
    for (final double element : m_aData)
      if (element > tol)
        r++;
    return r;
  }
}
