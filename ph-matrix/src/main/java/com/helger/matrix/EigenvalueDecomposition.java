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
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.math.MathHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Eigenvalues and eigenvectors of a real matrix.
 * <P>
 * If A is symmetric, then A = V*D*V' where the eigenvalue matrix D is diagonal
 * and the eigenvector matrix V is orthogonal. I.e. A =
 * V.times(D.times(V.transpose())) and V.times(V.transpose()) equals the
 * identity matrix.
 * <P>
 * If A is not symmetric, then the eigenvalue matrix D is block diagonal with
 * the real eigenvalues in 1-by-1 blocks and any complex eigenvalues, lambda +
 * i*mu, in 2-by-2 blocks, [lambda, mu; -mu, lambda]. The columns of V represent
 * the eigenvectors in the sense that A*V = V*D, i.e. A.times(V) equals
 * V.times(D). The matrix V may be badly conditioned, or even singular, so the
 * validity of the equation A = V*D*inverse(V) depends upon V.cond().
 **/
public class EigenvalueDecomposition implements Serializable
{
  private static final double EPSILON = Math.pow (2.0, -52.0);

  /**
   * Row and column dimension (square matrix).
   *
   * @serial matrix dimension.
   */
  private final int m_nDim;

  /**
   * Symmetry flag.
   *
   * @serial internal symmetry flag.
   */
  private final boolean m_bIsSymmetric;

  /**
   * Arrays for internal storage of eigenvalues.
   *
   * @serial internal storage of eigenvalues.
   */
  private final double [] m_aEVd;
  private final double [] m_aEVe;

  /**
   * Array for internal storage of eigenvectors.
   *
   * @serial internal storage of eigenvectors.
   */
  private final double [] [] m_aEigenVector;

  /**
   * Array for internal storage of nonsymmetric Hessenberg form.
   *
   * @serial internal storage of nonsymmetric Hessenberg form.
   */
  private double [] [] m_aHessenBerg;

  /**
   * Working storage for nonsymmetric algorithm.
   *
   * @serial working storage for nonsymmetric algorithm.
   */
  private double [] m_aOrt;

  /**
   * Symmetric Householder reduction to tridiagonal form.
   */
  private void _symmetricTred2 ()
  {
    // This is derived from the Algol procedures tred2 by
    // Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
    // Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
    // Fortran subroutine in EISPACK.
    for (int j = 0; j < m_nDim; j++)
    {
      m_aEVd[j] = m_aEigenVector[m_nDim - 1][j];
    }

    // Householder reduction to tridiagonal form.

    for (int i = m_nDim - 1; i > 0; i--)
    {
      // Scale to avoid under/overflow.
      double scale = 0.0;
      double h = 0.0;
      for (int k = 0; k < i; k++)
        scale += MathHelper.abs (m_aEVd[k]);
      if (scale == 0.0)
      {
        m_aEVe[i] = m_aEVd[i - 1];
        for (int j = 0; j < i; j++)
        {
          m_aEVd[j] = m_aEigenVector[i - 1][j];
          m_aEigenVector[i][j] = 0.0;
          m_aEigenVector[j][i] = 0.0;
        }
      }
      else
      {
        // Generate Householder vector.

        for (int k = 0; k < i; k++)
        {
          m_aEVd[k] /= scale;
          h += m_aEVd[k] * m_aEVd[k];
        }
        double f = m_aEVd[i - 1];
        double g = Math.sqrt (h);
        if (f > 0)
        {
          g = -g;
        }
        m_aEVe[i] = scale * g;
        h -= f * g;
        m_aEVd[i - 1] = f - g;
        for (int j = 0; j < i; j++)
        {
          m_aEVe[j] = 0.0;
        }

        // Apply similarity transformation to remaining columns.
        for (int j = 0; j < i; j++)
        {
          f = m_aEVd[j];
          m_aEigenVector[j][i] = f;
          g = m_aEVe[j] + m_aEigenVector[j][j] * f;
          for (int k = j + 1; k <= i - 1; k++)
          {
            g += m_aEigenVector[k][j] * m_aEVd[k];
            m_aEVe[k] += m_aEigenVector[k][j] * f;
          }
          m_aEVe[j] = g;
        }
        f = 0.0;
        for (int j = 0; j < i; j++)
        {
          m_aEVe[j] /= h;
          f += m_aEVe[j] * m_aEVd[j];
        }
        final double hh = f / (h + h);
        for (int j = 0; j < i; j++)
        {
          m_aEVe[j] -= hh * m_aEVd[j];
        }
        for (int j = 0; j < i; j++)
        {
          f = m_aEVd[j];
          g = m_aEVe[j];
          for (int k = j; k <= i - 1; k++)
          {
            m_aEigenVector[k][j] -= (f * m_aEVe[k] + g * m_aEVd[k]);
          }
          m_aEVd[j] = m_aEigenVector[i - 1][j];
          m_aEigenVector[i][j] = 0.0;
        }
      }
      m_aEVd[i] = h;
    }

    // Accumulate transformations.
    for (int i = 0; i < m_nDim - 1; i++)
    {
      m_aEigenVector[m_nDim - 1][i] = m_aEigenVector[i][i];
      m_aEigenVector[i][i] = 1.0;
      final double h = m_aEVd[i + 1];
      if (h != 0.0)
      {
        for (int k = 0; k <= i; k++)
        {
          m_aEVd[k] = m_aEigenVector[k][i + 1] / h;
        }
        for (int j = 0; j <= i; j++)
        {
          double g = 0.0;
          for (int k = 0; k <= i; k++)
          {
            g += m_aEigenVector[k][i + 1] * m_aEigenVector[k][j];
          }
          for (int k = 0; k <= i; k++)
          {
            m_aEigenVector[k][j] -= g * m_aEVd[k];
          }
        }
      }
      for (int k = 0; k <= i; k++)
      {
        m_aEigenVector[k][i + 1] = 0.0;
      }
    }
    for (int j = 0; j < m_nDim; j++)
    {
      m_aEVd[j] = m_aEigenVector[m_nDim - 1][j];
      m_aEigenVector[m_nDim - 1][j] = 0.0;
    }
    m_aEigenVector[m_nDim - 1][m_nDim - 1] = 1.0;
    m_aEVe[0] = 0.0;
  }

  /**
   * Symmetric tridiagonal QL algorithm.
   */
  private void _symmetricTql2 ()
  {
    // This is derived from the Algol procedures tql2, by
    // Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
    // Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
    // Fortran subroutine in EISPACK.
    for (int i = 1; i < m_nDim; i++)
      m_aEVe[i - 1] = m_aEVe[i];
    m_aEVe[m_nDim - 1] = 0.0;

    double f = 0.0;
    double tst1 = 0.0;
    for (int l = 0; l < m_nDim; l++)
    {
      // Find small subdiagonal element
      tst1 = Math.max (tst1, MathHelper.abs (m_aEVd[l]) + MathHelper.abs (m_aEVe[l]));
      int m = l;
      while (m < m_nDim)
      {
        if (MathHelper.abs (m_aEVe[m]) <= EPSILON * tst1)
          break;
        m++;
      }

      // If m == l, d[l] is an eigenvalue,
      // otherwise, iterate.

      if (m > l)
      {
        do
        {
          // Compute implicit shift
          double g = m_aEVd[l];
          double p = (m_aEVd[l + 1] - g) / (2.0 * m_aEVe[l]);
          double r = MathHelper.hypot (p, 1.0);
          if (p < 0)
          {
            r = -r;
          }
          m_aEVd[l] = m_aEVe[l] / (p + r);
          m_aEVd[l + 1] = m_aEVe[l] * (p + r);
          final double dl1 = m_aEVd[l + 1];
          double h = g - m_aEVd[l];
          for (int i = l + 2; i < m_nDim; i++)
          {
            m_aEVd[i] -= h;
          }
          f += h;

          // Implicit QL transformation.
          p = m_aEVd[m];
          double c = 1.0;
          double c2 = c;
          double c3 = c;
          final double el1 = m_aEVe[l + 1];
          double s = 0.0;
          double s2 = 0.0;
          for (int i = m - 1; i >= l; i--)
          {
            c3 = c2;
            c2 = c;
            s2 = s;
            g = c * m_aEVe[i];
            h = c * p;
            r = MathHelper.hypot (p, m_aEVe[i]);
            m_aEVe[i + 1] = s * r;
            s = m_aEVe[i] / r;
            c = p / r;
            p = c * m_aEVd[i] - s * g;
            m_aEVd[i + 1] = h + s * (c * g + s * m_aEVd[i]);

            // Accumulate transformation.

            for (int k = 0; k < m_nDim; k++)
            {
              h = m_aEigenVector[k][i + 1];
              m_aEigenVector[k][i + 1] = s * m_aEigenVector[k][i] + c * h;
              m_aEigenVector[k][i] = c * m_aEigenVector[k][i] - s * h;
            }
          }
          p = -s * s2 * c3 * el1 * m_aEVe[l] / dl1;
          m_aEVe[l] = s * p;
          m_aEVd[l] = c * p;

          // Check for convergence.

        } while (MathHelper.abs (m_aEVe[l]) > EPSILON * tst1);
      }
      m_aEVd[l] = m_aEVd[l] + f;
      m_aEVe[l] = 0.0;
    }

    // Sort eigenvalues and corresponding vectors.
    for (int i = 0; i < m_nDim - 1; i++)
    {
      int k = i;
      double p = m_aEVd[i];
      for (int j = i + 1; j < m_nDim; j++)
      {
        if (m_aEVd[j] < p)
        {
          k = j;
          p = m_aEVd[j];
        }
      }
      if (k != i)
      {
        m_aEVd[k] = m_aEVd[i];
        m_aEVd[i] = p;
        for (int j = 0; j < m_nDim; j++)
        {
          p = m_aEigenVector[j][i];
          m_aEigenVector[j][i] = m_aEigenVector[j][k];
          m_aEigenVector[j][k] = p;
        }
      }
    }
  }

  /**
   * Nonsymmetric reduction to Hessenberg form.
   */
  private void _nonsymetricOrthes ()
  {
    // This is derived from the Algol procedures orthes and ortran,
    // by Martin and Wilkinson, Handbook for Auto. Comp.,
    // Vol.ii-Linear Algebra, and the corresponding
    // Fortran subroutines in EISPACK.

    final int low = 0;
    final int high = m_nDim - 1;

    for (int m = low + 1; m <= high - 1; m++)
    {
      // Scale column.
      double scale = 0.0;
      for (int i = m; i <= high; i++)
        scale += MathHelper.abs (m_aHessenBerg[i][m - 1]);
      if (scale != 0.0)
      {
        // Compute Householder transformation.
        double h = 0.0;
        for (int i = high; i >= m; i--)
        {
          m_aOrt[i] = m_aHessenBerg[i][m - 1] / scale;
          h += m_aOrt[i] * m_aOrt[i];
        }
        double g = Math.sqrt (h);
        if (m_aOrt[m] > 0)
        {
          g = -g;
        }
        h -= m_aOrt[m] * g;
        m_aOrt[m] = m_aOrt[m] - g;

        // Apply Householder similarity transformation
        // H = (I-u*u'/h)*H*(I-u*u')/h)

        for (int j = m; j < m_nDim; j++)
        {
          double f = 0.0;
          for (int i = high; i >= m; i--)
          {
            f += m_aOrt[i] * m_aHessenBerg[i][j];
          }
          f /= h;
          for (int i = m; i <= high; i++)
          {
            m_aHessenBerg[i][j] -= f * m_aOrt[i];
          }
        }

        for (int i = 0; i <= high; i++)
        {
          double f = 0.0;
          for (int j = high; j >= m; j--)
          {
            f += m_aOrt[j] * m_aHessenBerg[i][j];
          }
          f /= h;
          for (int j = m; j <= high; j++)
          {
            m_aHessenBerg[i][j] -= f * m_aOrt[j];
          }
        }
        m_aOrt[m] = scale * m_aOrt[m];
        m_aHessenBerg[m][m - 1] = scale * g;
      }
    }

    // Accumulate transformations (Algol's ortran).
    for (int i = 0; i < m_nDim; i++)
    {
      final double [] aRow = m_aEigenVector[i];
      for (int j = 0; j < m_nDim; j++)
      {
        aRow[j] = (i == j ? 1d : 0d);
      }
    }

    for (int m = high - 1; m >= low + 1; m--)
    {
      if (m_aHessenBerg[m][m - 1] != 0.0)
      {
        for (int i = m + 1; i <= high; i++)
        {
          m_aOrt[i] = m_aHessenBerg[i][m - 1];
        }
        for (int j = m; j <= high; j++)
        {
          double g = 0.0;
          for (int i = m; i <= high; i++)
          {
            g += m_aOrt[i] * m_aEigenVector[i][j];
          }
          // Double division avoids possible underflow
          g = (g / m_aOrt[m]) / m_aHessenBerg[m][m - 1];
          for (int i = m; i <= high; i++)
          {
            m_aEigenVector[i][j] += g * m_aOrt[i];
          }
        }
      }
    }
  }

  // Complex scalar division.

  private double m_dCdivr;
  private double m_dCdivi;

  private void _cdiv (final double xr, final double xi, final double yr, final double yi)
  {
    if (MathHelper.abs (yr) > MathHelper.abs (yi))
    {
      final double r = yr == 0 ? 0 : yi / yr;
      final double d = yr + r * yi;
      if (d == 0)
      {
        m_dCdivr = 0;
        m_dCdivi = 0;
      }
      else
      {
        m_dCdivr = (xr + r * xi) / d;
        m_dCdivi = (xi - r * xr) / d;
      }
    }
    else
    {
      final double r = yi == 0 ? 0 : yr / yi;
      final double d = yi + r * yr;
      if (d == 0)
      {
        m_dCdivr = 0;
        m_dCdivi = 0;
      }
      else
      {
        m_dCdivr = (r * xr + xi) / d;
        m_dCdivi = (r * xi - xr) / d;
      }
    }
  }

  /**
   * Nonsymmetric reduction from Hessenberg to real Schur form.
   */
  private void _nonsymetricHqr2 ()
  {
    // This is derived from the Algol procedure hqr2,
    // by Martin and Wilkinson, Handbook for Auto. Comp.,
    // Vol.ii-Linear Algebra, and the corresponding
    // Fortran subroutine in EISPACK.

    // Initialize

    final int nn = m_nDim;
    int n = nn - 1;
    final int low = 0;
    final int high = nn - 1;
    double exshift = 0;
    double p = 0;
    double q = 0;
    double r = 0;
    double s = 0;
    double z = 0;
    double t;
    double w;
    double x;
    double y;

    // Store roots isolated by balanc and compute matrix norm
    double norm = 0.0;
    for (int i = 0; i < nn; i++)
    {
      if (i < low || i > high)
      {
        m_aEVd[i] = m_aHessenBerg[i][i];
        m_aEVe[i] = 0.0;
      }
      for (int j = Math.max (i - 1, 0); j < nn; j++)
      {
        norm += MathHelper.abs (m_aHessenBerg[i][j]);
      }
    }

    // Outer loop over eigenvalue index
    int iter = 0;
    while (n >= low)
    {
      // Look for single small sub-diagonal element
      int l = n;
      while (l > low)
      {
        s = MathHelper.abs (m_aHessenBerg[l - 1][l - 1]) + MathHelper.abs (m_aHessenBerg[l][l]);
        if (s == 0.0)
          s = norm;
        if (MathHelper.abs (m_aHessenBerg[l][l - 1]) < EPSILON * s)
          break;
        l--;
      }

      // Check for convergence
      // One root found
      if (l == n)
      {
        m_aHessenBerg[n][n] = m_aHessenBerg[n][n] + exshift;
        m_aEVd[n] = m_aHessenBerg[n][n];
        m_aEVe[n] = 0.0;
        n--;
        iter = 0;
        // Two roots found
      }
      else
        if (l == n - 1)
        {
          w = m_aHessenBerg[n][n - 1] * m_aHessenBerg[n - 1][n];
          p = (m_aHessenBerg[n - 1][n - 1] - m_aHessenBerg[n][n]) / 2.0;
          q = p * p + w;
          z = Math.sqrt (MathHelper.abs (q));
          m_aHessenBerg[n][n] = m_aHessenBerg[n][n] + exshift;
          m_aHessenBerg[n - 1][n - 1] = m_aHessenBerg[n - 1][n - 1] + exshift;
          x = m_aHessenBerg[n][n];

          // Real pair

          if (q >= 0)
          {
            if (p >= 0)
              z = p + z;
            else
              z = p - z;
            m_aEVd[n - 1] = x + z;
            m_aEVd[n] = m_aEVd[n - 1];
            if (z != 0.0)
              m_aEVd[n] = x - w / z;
            m_aEVe[n - 1] = 0.0;
            m_aEVe[n] = 0.0;
            x = m_aHessenBerg[n][n - 1];
            s = MathHelper.abs (x) + MathHelper.abs (z);
            p = x / s;
            q = z / s;
            r = Math.sqrt (p * p + q * q);
            p /= r;
            q /= r;

            // Row modification
            for (int j = n - 1; j < nn; j++)
            {
              z = m_aHessenBerg[n - 1][j];
              m_aHessenBerg[n - 1][j] = q * z + p * m_aHessenBerg[n][j];
              m_aHessenBerg[n][j] = q * m_aHessenBerg[n][j] - p * z;
            }

            // Column modification
            for (int i = 0; i <= n; i++)
            {
              z = m_aHessenBerg[i][n - 1];
              m_aHessenBerg[i][n - 1] = q * z + p * m_aHessenBerg[i][n];
              m_aHessenBerg[i][n] = q * m_aHessenBerg[i][n] - p * z;
            }

            // Accumulate transformations
            for (int i = low; i <= high; i++)
            {
              z = m_aEigenVector[i][n - 1];
              m_aEigenVector[i][n - 1] = q * z + p * m_aEigenVector[i][n];
              m_aEigenVector[i][n] = q * m_aEigenVector[i][n] - p * z;
            }

            // Complex pair
          }
          else
          {
            m_aEVd[n - 1] = x + p;
            m_aEVd[n] = x + p;
            m_aEVe[n - 1] = z;
            m_aEVe[n] = -z;
          }
          n -= 2;
          iter = 0;

          // No convergence yet
        }
        else
        {
          // Form shift
          x = m_aHessenBerg[n][n];
          y = 0.0;
          w = 0.0;
          if (l < n)
          {
            y = m_aHessenBerg[n - 1][n - 1];
            w = m_aHessenBerg[n][n - 1] * m_aHessenBerg[n - 1][n];
          }

          // Wilkinson's original ad hoc shift
          if (iter == 10)
          {
            exshift += x;
            for (int i = low; i <= n; i++)
            {
              m_aHessenBerg[i][i] -= x;
            }
            s = MathHelper.abs (m_aHessenBerg[n][n - 1]) + MathHelper.abs (m_aHessenBerg[n - 1][n - 2]);
            x = 0.75 * s;
            y = x;
            w = -0.4375 * s * s;
          }

          // MATLAB's new ad hoc shift
          if (iter == 30)
          {
            s = (y - x) / 2.0;
            s = s * s + w;
            if (s > 0)
            {
              s = Math.sqrt (s);
              if (y < x)
                s = -s;
              s = x - w / ((y - x) / 2.0 + s);
              for (int i = low; i <= n; i++)
              {
                m_aHessenBerg[i][i] -= s;
              }
              exshift += s;
              x = 0.964;
              y = x;
              w = x;
            }
          }

          iter++; // (Could check iteration count here.)

          // Look for two consecutive small sub-diagonal elements
          int m = n - 2;
          while (m >= l)
          {
            z = m_aHessenBerg[m][m];
            r = x - z;
            s = y - z;
            p = (r * s - w) / m_aHessenBerg[m + 1][m] + m_aHessenBerg[m][m + 1];
            q = m_aHessenBerg[m + 1][m + 1] - z - r - s;
            r = m_aHessenBerg[m + 2][m + 1];
            s = MathHelper.abs (p) + MathHelper.abs (q) + MathHelper.abs (r);
            p /= s;
            q /= s;
            r /= s;
            if (m == l)
              break;
            if (MathHelper.abs (m_aHessenBerg[m][m - 1]) *
                (MathHelper.abs (q) + MathHelper.abs (r)) < EPSILON *
                                                            (MathHelper.abs (p) *
                                                             (MathHelper.abs (m_aHessenBerg[m - 1][m - 1]) +
                                                              MathHelper.abs (z) +
                                                              MathHelper.abs (m_aHessenBerg[m + 1][m + 1]))))
            {
              break;
            }
            m--;
          }

          for (int i = m + 2; i <= n; i++)
          {
            m_aHessenBerg[i][i - 2] = 0.0;
            if (i > m + 2)
            {
              m_aHessenBerg[i][i - 3] = 0.0;
            }
          }

          // Double QR step involving rows l:n and columns m:n

          for (int k = m; k <= n - 1; k++)
          {
            final boolean notlast = (k != n - 1);
            if (k != m)
            {
              p = m_aHessenBerg[k][k - 1];
              q = m_aHessenBerg[k + 1][k - 1];
              r = (notlast ? m_aHessenBerg[k + 2][k - 1] : 0.0);
              x = MathHelper.abs (p) + MathHelper.abs (q) + MathHelper.abs (r);
              if (x == 0.0)
              {
                continue;
              }
              p /= x;
              q /= x;
              r /= x;
            }

            s = Math.sqrt (p * p + q * q + r * r);
            if (p < 0)
            {
              s = -s;
            }
            if (s != 0)
            {
              if (k != m)
              {
                m_aHessenBerg[k][k - 1] = -s * x;
              }
              else
                if (l != m)
                {
                  m_aHessenBerg[k][k - 1] = -m_aHessenBerg[k][k - 1];
                }
              p += s;
              x = p / s;
              y = q / s;
              z = r / s;
              q /= p;
              r /= p;

              // Row modification

              for (int j = k; j < nn; j++)
              {
                p = m_aHessenBerg[k][j] + q * m_aHessenBerg[k + 1][j];
                if (notlast)
                {
                  p += r * m_aHessenBerg[k + 2][j];
                  m_aHessenBerg[k + 2][j] = m_aHessenBerg[k + 2][j] - p * z;
                }
                m_aHessenBerg[k][j] = m_aHessenBerg[k][j] - p * x;
                m_aHessenBerg[k + 1][j] = m_aHessenBerg[k + 1][j] - p * y;
              }

              // Column modification

              for (int i = 0; i <= Math.min (n, k + 3); i++)
              {
                p = x * m_aHessenBerg[i][k] + y * m_aHessenBerg[i][k + 1];
                if (notlast)
                {
                  p += z * m_aHessenBerg[i][k + 2];
                  m_aHessenBerg[i][k + 2] = m_aHessenBerg[i][k + 2] - p * r;
                }
                m_aHessenBerg[i][k] = m_aHessenBerg[i][k] - p;
                m_aHessenBerg[i][k + 1] = m_aHessenBerg[i][k + 1] - p * q;
              }

              // Accumulate transformations

              for (int i = low; i <= high; i++)
              {
                p = x * m_aEigenVector[i][k] + y * m_aEigenVector[i][k + 1];
                if (notlast)
                {
                  p += z * m_aEigenVector[i][k + 2];
                  m_aEigenVector[i][k + 2] = m_aEigenVector[i][k + 2] - p * r;
                }
                m_aEigenVector[i][k] = m_aEigenVector[i][k] - p;
                m_aEigenVector[i][k + 1] = m_aEigenVector[i][k + 1] - p * q;
              }
            } // (s != 0)
          } // k loop
        } // check convergence
    } // while (n >= low)

    // Backsubstitute to find vectors of upper triangular form

    if (norm == 0.0)
    {
      return;
    }

    for (n = nn - 1; n >= 0; n--)
    {
      p = m_aEVd[n];
      q = m_aEVe[n];

      // Real vector

      if (q == 0)
      {
        int l = n;
        m_aHessenBerg[n][n] = 1.0;
        for (int i = n - 1; i >= 0; i--)
        {
          w = m_aHessenBerg[i][i] - p;
          r = 0.0;
          for (int j = l; j <= n; j++)
          {
            r += m_aHessenBerg[i][j] * m_aHessenBerg[j][n];
          }
          if (m_aEVe[i] < 0.0)
          {
            z = w;
            s = r;
          }
          else
          {
            l = i;
            if (m_aEVe[i] == 0.0)
            {
              if (w != 0.0)
              {
                m_aHessenBerg[i][n] = -r / w;
              }
              else
              {
                m_aHessenBerg[i][n] = -r / (EPSILON * norm);
              }

              // Solve real equations

            }
            else
            {
              x = m_aHessenBerg[i][i + 1];
              y = m_aHessenBerg[i + 1][i];
              q = (m_aEVd[i] - p) * (m_aEVd[i] - p) + m_aEVe[i] * m_aEVe[i];
              t = (x * s - z * r) / q;
              m_aHessenBerg[i][n] = t;
              if (MathHelper.abs (x) > MathHelper.abs (z))
              {
                m_aHessenBerg[i + 1][n] = (-r - w * t) / x;
              }
              else
              {
                m_aHessenBerg[i + 1][n] = (-s - y * t) / z;
              }
            }

            // Overflow control

            t = MathHelper.abs (m_aHessenBerg[i][n]);
            if ((EPSILON * t) * t > 1)
            {
              for (int j = i; j <= n; j++)
              {
                m_aHessenBerg[j][n] = m_aHessenBerg[j][n] / t;
              }
            }
          }
        }

        // Complex vector

      }
      else
        if (q < 0)
        {
          int l = n - 1;

          // Last vector component imaginary so matrix is triangular

          if (MathHelper.abs (m_aHessenBerg[n][n - 1]) > MathHelper.abs (m_aHessenBerg[n - 1][n]))
          {
            m_aHessenBerg[n - 1][n - 1] = q / m_aHessenBerg[n][n - 1];
            m_aHessenBerg[n - 1][n] = -(m_aHessenBerg[n][n] - p) / m_aHessenBerg[n][n - 1];
          }
          else
          {
            _cdiv (0.0, -m_aHessenBerg[n - 1][n], m_aHessenBerg[n - 1][n - 1] - p, q);
            m_aHessenBerg[n - 1][n - 1] = m_dCdivr;
            m_aHessenBerg[n - 1][n] = m_dCdivi;
          }
          m_aHessenBerg[n][n - 1] = 0.0;
          m_aHessenBerg[n][n] = 1.0;
          for (int i = n - 2; i >= 0; i--)
          {
            double ra = 0.0;
            double sa = 0.0;
            double vr;
            double vi;
            for (int j = l; j <= n; j++)
            {
              ra += m_aHessenBerg[i][j] * m_aHessenBerg[j][n - 1];
              sa += m_aHessenBerg[i][j] * m_aHessenBerg[j][n];
            }
            w = m_aHessenBerg[i][i] - p;

            if (m_aEVe[i] < 0.0)
            {
              z = w;
              r = ra;
              s = sa;
            }
            else
            {
              l = i;
              if (m_aEVe[i] == 0)
              {
                _cdiv (-ra, -sa, w, q);
                m_aHessenBerg[i][n - 1] = m_dCdivr;
                m_aHessenBerg[i][n] = m_dCdivi;
              }
              else
              {

                // Solve complex equations

                x = m_aHessenBerg[i][i + 1];
                y = m_aHessenBerg[i + 1][i];
                vr = (m_aEVd[i] - p) * (m_aEVd[i] - p) + m_aEVe[i] * m_aEVe[i] - q * q;
                vi = (m_aEVd[i] - p) * 2.0 * q;
                if (vr == 0.0 && vi == 0.0)
                {
                  vr = EPSILON *
                       norm *
                       (MathHelper.abs (w) +
                        MathHelper.abs (q) +
                        MathHelper.abs (x) +
                        MathHelper.abs (y) +
                        MathHelper.abs (z));
                }
                _cdiv (x * r - z * ra + q * sa, x * s - z * sa - q * ra, vr, vi);
                m_aHessenBerg[i][n - 1] = m_dCdivr;
                m_aHessenBerg[i][n] = m_dCdivi;
                if (MathHelper.abs (x) > (MathHelper.abs (z) + MathHelper.abs (q)))
                {
                  m_aHessenBerg[i + 1][n - 1] = (-ra - w * m_aHessenBerg[i][n - 1] + q * m_aHessenBerg[i][n]) / x;
                  m_aHessenBerg[i + 1][n] = (-sa - w * m_aHessenBerg[i][n] - q * m_aHessenBerg[i][n - 1]) / x;
                }
                else
                {
                  _cdiv (-r - y * m_aHessenBerg[i][n - 1], -s - y * m_aHessenBerg[i][n], z, q);
                  m_aHessenBerg[i + 1][n - 1] = m_dCdivr;
                  m_aHessenBerg[i + 1][n] = m_dCdivi;
                }
              }

              // Overflow control

              t = Math.max (MathHelper.abs (m_aHessenBerg[i][n - 1]), MathHelper.abs (m_aHessenBerg[i][n]));
              if ((EPSILON * t) * t > 1)
              {
                for (int j = i; j <= n; j++)
                {
                  m_aHessenBerg[j][n - 1] = m_aHessenBerg[j][n - 1] / t;
                  m_aHessenBerg[j][n] = m_aHessenBerg[j][n] / t;
                }
              }
            }
          }
        }
    }

    // Vectors of isolated roots

    for (int i = 0; i < nn; i++)
    {
      if (i < low || i > high)
      {
        for (int j = i; j < nn; j++)
        {
          m_aEigenVector[i][j] = m_aHessenBerg[i][j];
        }
      }
    }

    // Back transformation to get eigenvectors of original matrix

    for (int j = nn - 1; j >= low; j--)
    {
      for (int i = low; i <= high; i++)
      {
        z = 0.0;
        for (int k = low; k <= Math.min (j, high); k++)
        {
          z += m_aEigenVector[i][k] * m_aHessenBerg[k][j];
        }
        m_aEigenVector[i][j] = z;
      }
    }
  }

  /**
   * Check for symmetry, then construct the eigenvalue decomposition Structure
   * to access D and V.
   *
   * @param aMatrix
   *        Square matrix
   */
  public EigenvalueDecomposition (@Nonnull final Matrix aMatrix)
  {
    final double [] [] aArray = aMatrix.internalGetArray ();
    m_nDim = aMatrix.getColumnDimension ();
    m_aEigenVector = new double [m_nDim] [m_nDim];
    m_aEVd = new double [m_nDim];
    m_aEVe = new double [m_nDim];

    boolean bIsSymmetric = true;
    for (int nRow = 0; nRow < m_nDim; nRow++)
    {
      final double [] aRow = aArray[nRow];
      for (int nCol = 0; nCol < m_nDim; nCol++)
        if (!EqualsHelper.equals (aRow[nCol], aArray[nCol][nRow]))
        {
          bIsSymmetric = false;
          break;
        }
    }
    m_bIsSymmetric = bIsSymmetric;

    if (m_bIsSymmetric)
    {
      for (int nRow = 0; nRow < m_nDim; nRow++)
      {
        final double [] aSrcRow = aArray[nRow];
        System.arraycopy (aSrcRow, 0, m_aEigenVector[nRow], 0, aSrcRow.length);
      }

      // Tridiagonalize.
      _symmetricTred2 ();

      // Diagonalize.
      _symmetricTql2 ();
    }
    else
    {
      m_aHessenBerg = new double [m_nDim] [m_nDim];
      m_aOrt = new double [m_nDim];

      for (int nRow = 0; nRow < m_nDim; nRow++)
      {
        final double [] aSrcRow = aArray[nRow];
        final double [] aDstRow = m_aHessenBerg[nRow];
        for (int nCol = 0; nCol < m_nDim; nCol++)
          aDstRow[nCol] = aSrcRow[nCol];
      }

      // Reduce to Hessenberg form.
      _nonsymetricOrthes ();

      // Reduce Hessenberg to real Schur form.
      _nonsymetricHqr2 ();
    }
  }

  /**
   * @return <code>true</code> if the input was symmetric, <code>false</code> if
   *         not
   */
  public boolean isSymmetric ()
  {
    return m_bIsSymmetric;
  }

  /**
   * Return the eigenvector matrix
   *
   * @return V
   */
  @Nonnull
  @ReturnsMutableCopy
  public Matrix getV ()
  {
    return new Matrix (m_aEigenVector, m_nDim, m_nDim);
  }

  /**
   * Return the real parts of the eigenvalues
   *
   * @return real(diag(D))
   */
  @SuppressFBWarnings ("EI_EXPOSE_REP")
  @Nonnull
  @ReturnsMutableObject ("took code as is")
  public double [] directGetRealEigenvalues ()
  {
    return m_aEVd;
  }

  /**
   * Return the imaginary parts of the eigenvalues
   *
   * @return imag(diag(D))
   */
  @SuppressFBWarnings ("EI_EXPOSE_REP")
  @Nonnull
  @ReturnsMutableObject ("took code as is")
  public double [] directGetImagEigenvalues ()
  {
    return m_aEVe;
  }

  /**
   * Return the block diagonal eigenvalue matrix
   *
   * @return D
   */
  @Nonnull
  @ReturnsMutableCopy
  public Matrix getD ()
  {
    final Matrix aNewMatrix = new Matrix (m_nDim, m_nDim);
    final double [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = 0; nRow < m_nDim; nRow++)
    {
      final double [] aDstRow = aNewArray[nRow];
      Arrays.fill (aDstRow, 0.0);
      aDstRow[nRow] = m_aEVd[nRow];

      final double dEVe = m_aEVe[nRow];
      if (dEVe > 0)
        aDstRow[nRow + 1] = dEVe;
      else
        if (dEVe < 0)
          aDstRow[nRow - 1] = dEVe;
    }
    return aNewMatrix;
  }
}
