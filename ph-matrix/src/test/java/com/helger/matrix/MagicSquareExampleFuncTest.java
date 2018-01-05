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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import javax.annotation.Nonnegative;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.string.StringHelper;

/** Example of use of Matrix Class, featuring magic squares. **/

public final class MagicSquareExampleFuncTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger ("dummy");

  /**
   * @param n
   *        Dimension
   * @return magic square test matrix.
   */
  public static Matrix magic (@Nonnegative final int n)
  {
    if (n <= 0)
      throw new IllegalStateException ();

    final double [] [] M = new double [n] [n];

    // Odd order

    if ((n & 1) == 1)
    {
      final int a = (n + 1) / 2;
      final int b = (n + 1);
      for (int j = 0; j < n; j++)
        for (int i = 0; i < n; i++)
          M[i][j] = n * ((i + j + a) % n) + ((i + 2 * j + b) % n) + 1;
      // Doubly Even Order
    }
    else
      if ((n % 4) == 0)
      {
        for (int j = 0; j < n; j++)
          for (int i = 0; i < n; i++)
            if (((i + 1) / 2) % 2 == ((j + 1) / 2) % 2)
              M[i][j] = n * n - n * i - j;
            else
              M[i][j] = n * i + j + 1;
        // Singly Even Order
      }
      else
      {
        final int p = n / 2;
        final int k = (n - 2) / 4;
        final Matrix A = magic (p);
        for (int j = 0; j < p; j++)
        {
          for (int i = 0; i < p; i++)
          {
            final double aij = A.get (i, j);
            M[i][j] = aij;
            M[i][j + p] = aij + 2 * p * p;
            M[i + p][j] = aij + 3 * p * p;
            M[i + p][j + p] = aij + p * p;
          }
        }
        for (int i = 0; i < p; i++)
        {
          for (int j = 0; j < k; j++)
          {
            final double t = M[i][j];
            M[i][j] = M[i + p][j];
            M[i + p][j] = t;
          }
          for (int j = n - k + 1; j < n; j++)
          {
            final double t = M[i][j];
            M[i][j] = M[i + p][j];
            M[i + p][j] = t;
          }
        }
        double t = M[k][0];
        M[k][0] = M[k + p][0];
        M[k + p][0] = t;
        t = M[k][k];
        M[k][k] = M[k + p][k];
        M[k + p][k] = t;
      }
    return new Matrix (M);
  }

  /* Format double with Fw.d. **/
  public static String fixedWidthDoubletoString (final double x, final int w, final int d)
  {
    final DecimalFormat fmt = (DecimalFormat) NumberFormat.getNumberInstance (CGlobal.DEFAULT_LOCALE);
    fmt.setMaximumFractionDigits (d);
    fmt.setMinimumFractionDigits (d);
    fmt.setGroupingUsed (false);
    final String s = fmt.format (x);
    return StringHelper.getWithLeading (s, w, ' ');
  }

  /* Format integer with Iw. **/
  public static String fixedWidthIntegertoString (final int n, final int w)
  {
    final String s = Integer.toString (n);
    return StringHelper.getWithLeading (s, w, ' ');
  }

  @Test
  public void testMain ()
  {
    final StringBuilder aSB = new StringBuilder ();
    /*
     * | Tests LU, QR, SVD and symmetric Eig decompositions. | | n = order of
     * magic square. | trace = diagonal sum, should be the magic sum, (n^3 +
     * n)/2. | max_eig = maximum eigenvalue of (A + A')/2, should equal trace. |
     * rank = linear algebraic rank, | should equal n if n is odd, be less than
     * n if n is even. | cond = L_2 condition number, ratio of singular values.
     * | lu_res = test of LU factorization, norm1(L*U-A(p,:))/(n*eps). | qr_res
     * = test of QR factorization, norm1(Q*R-A)/(n*eps).
     */

    aSB.append ("\n    Test of Matrix Class, using magic squares.\n");
    aSB.append ("    See MagicSquareExample.main() for an explanation.\n");
    aSB.append ("\n      n     trace       max_eig   rank        cond      lu_res      qr_res\n\n");

    final Date start_time = new Date ();
    final double eps = Math.pow (2.0, -52.0);
    for (int n = 3; n <= 32; n++)
    {
      aSB.append (fixedWidthIntegertoString (n, 7));

      final Matrix M = magic (n);

      final int t = (int) M.trace ();
      aSB.append (fixedWidthIntegertoString (t, 10));

      final EigenvalueDecomposition E = new EigenvalueDecomposition (M.plus (M.transpose ()).times (0.5));
      final double [] d = E.directGetRealEigenvalues ();
      aSB.append (fixedWidthDoubletoString (d[n - 1], 14, 3));

      final int r = M.rank ();
      aSB.append (fixedWidthIntegertoString (r, 7));

      final double c = M.cond ();
      aSB.append (c < 1 / eps ? fixedWidthDoubletoString (c, 12, 3) : "         Inf");

      final LUDecomposition LU = new LUDecomposition (M);
      final Matrix L = LU.getL ();
      final Matrix U = LU.getU ();
      final int [] p = LU.getPivot ();
      Matrix R = L.times (U).minus (M.getMatrix (p, 0, n - 1));
      double res = R.norm1 () / (n * eps);
      aSB.append (fixedWidthDoubletoString (res, 12, 3));

      final QRDecomposition QR = new QRDecomposition (M);
      final Matrix Q = QR.getQ ();
      R = QR.getR ();
      R = Q.times (R).minus (M);
      res = R.norm1 () / (n * eps);
      aSB.append (fixedWidthDoubletoString (res, 12, 3));

      aSB.append ('\n');
    }
    final Date stop_time = new Date ();
    final double etime = (stop_time.getTime () - start_time.getTime ()) / 1000.;
    aSB.append ("\nElapsed Time = " + fixedWidthDoubletoString (etime, 12, 3) + " seconds\n");
    aSB.append ("Adios\n");
    s_aLogger.info (aSB.toString ());
  }
}
