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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.annotation.Nonnull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.FileOperations;
import com.helger.commons.string.StringHelper;

/**
 * TestMatrix tests the functionality of the Jama Matrix class and associated
 * decompositions.
 * <P>
 * Run the test from the command line using <BLOCKQUOTE>
 *
 * <PRE>
 * <CODE>
 *  java Jama.test.TestMatrix
 * </CODE>
 * </PRE>
 *
 * </BLOCKQUOTE> Detailed output is provided indicating the functionality being
 * tested and whether the functionality is correctly implemented. Exception
 * handling is also tested.
 * <P>
 * The test is designed to run to completion and give a summary of any
 * implementation errors encountered. The final output should be: <BLOCKQUOTE>
 *
 * <PRE>
 * <CODE>
 *       TestMatrix completed.
 *       Total errors reported: n1
 *       Total warning reported: n2
 * </CODE>
 * </PRE>
 *
 * </BLOCKQUOTE> If the test does not run to completion, this indicates that
 * there is a substantial problem within the implementation that was not
 * anticipated in the test design. The stopping point should give an indication
 * of where the problem exists.
 **/
public final class MatrixTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (MatrixTest.class);
  private static final File FILE_JAMA_TEST_MATRIX_OUT = new File ("Jamaout");
  private static final double EPSILON = Math.pow (2.0, -52.0);
  private static final DecimalFormatSymbols DFS = DecimalFormatSymbols.getInstance (Locale.US);

  @Test
  @SuppressWarnings ("unused")
  public void testMain ()
  {
    // Uncomment this to test IO in a different locale.
    // Locale.setDefault(Locale.GERMAN);
    int warningCount = 0;
    double tmp;
    final double [] columnwise = { 1., 2., 3., 4., 5., 6., 7., 8., 9., 10., 11., 12. };
    final double [] rowwise = { 1., 4., 7., 10., 2., 5., 8., 11., 3., 6., 9., 12. };
    final double [] [] avals = { { 1., 4., 7., 10. }, { 2., 5., 8., 11. }, { 3., 6., 9., 12. } };
    final double [] [] rankdef = avals;
    final double [] [] tvals = { { 1., 2., 3. }, { 4., 5., 6. }, { 7., 8., 9. }, { 10., 11., 12. } };
    final double [] [] subavals = { { 5., 8., 11. }, { 6., 9., 12. } };
    final double [] [] rvals = { { 1., 4., 7. }, { 2., 5., 8., 11. }, { 3., 6., 9., 12. } };
    final double [] [] pvals = { { 4., 1., 1. }, { 1., 2., 3. }, { 1., 3., 6. } };
    final double [] [] ivals = { { 1., 0., 0., 0. }, { 0., 1., 0., 0. }, { 0., 0., 1., 0. } };
    final double [] [] evals = { { 0., 1., 0., 0. },
                                 { 1., 0., 2.e-7, 0. },
                                 { 0., -2.e-7, 0., 1. },
                                 { 0., 0., 1., 0. } };
    final double [] [] square = { { 166., 188., 210. }, { 188., 214., 240. }, { 210., 240., 270. } };
    final double [] [] sqSolution = { { 13. }, { 15. } };
    final double [] [] condmat = { { 1., 3. }, { 7., 9. } };
    final double [] [] badeigs = { { 0, 0, 0, 0, 0 },
                                   { 0, 0, 0, 0, 1 },
                                   { 0, 0, 0, 1, 0 },
                                   { 1, 1, 0, 0, 1 },
                                   { 1, 0, 1, 0, 1 } };
    final int rows = 3;
    final int cols = 4;
    /*
     * should trigger bad shape for construction with val
     */
    final int invalidld = 5;
    /*
     * (raggedr,raggedc) should be out of bounds in ragged array
     */
    final int raggedr = 0;
    final int raggedc = 4;
    /* leading dimension of intended test Matrices */
    final int validld = 3;
    /*
     * leading dimension which is valid, but nonconforming
     */
    final int nonconformld = 4;
    /* index ranges for sub Matrix */
    final int ib = 1;
    final int ie = 2;
    final int jb = 1;
    final int je = 3;
    final int [] rowindexset = { 1, 2 };
    final int [] badrowindexset = { 1, 3 };
    final int [] columnindexset = { 1, 2, 3 };
    final int [] badcolumnindexset = { 1, 2, 4 };
    final double columnsummax = 33;
    final double rowsummax = 30;
    final double sumofdiagonals = 15;
    final double sumofsquares = 650;

    /**
     * Constructors and constructor-like methods: double[], int double[][] int,
     * int int, int, double int, int, double[][] constructWithCopy(double[][])
     * random(int,int) identity(int)
     **/

    _print ("\nTesting constructors and constructor-like methods...\n");
    try
    {
      /**
       * _check that exception is thrown in packed constructor with invalid
       * length
       **/
      new Matrix (columnwise, invalidld);
      fail ("exception not thrown for invalid input");
    }
    catch (final IllegalArgumentException e)
    {
      _try_success ("Catch invalid length in packed constructor... ", e.getMessage ());
    }
    try
    {
      /**
       * _check that exception is thrown in default constructor if input array
       * is 'ragged'
       **/
      final Matrix a = new Matrix (rvals);
      tmp = a.get (raggedr, raggedc);
    }
    catch (final IllegalArgumentException e)
    {
      _try_success ("Catch ragged input to default constructor... ", e.getMessage ());
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      fail ("exception not thrown in construction...ArrayIndexOutOfBoundsException thrown later");
    }
    try
    {
      /**
       * _check that exception is thrown in constructWithCopy if input array is
       * 'ragged'
       **/
      final Matrix a = Matrix.constructWithCopy (rvals);
      tmp = a.get (raggedr, raggedc);
    }
    catch (final IllegalArgumentException e)
    {
      _try_success ("Catch ragged input to constructWithCopy... ", e.getMessage ());
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      fail ("exception not thrown in construction...ArrayIndexOutOfBoundsException thrown later");
    }

    Matrix a = new Matrix (columnwise, validld);
    Matrix b = new Matrix (avals);
    tmp = b.get (0, 0);
    avals[0][0] = 0.0;
    Matrix c = b.minus (a);
    avals[0][0] = tmp;
    b = Matrix.constructWithCopy (avals);
    tmp = b.get (0, 0);
    avals[0][0] = 0.0;
    if ((tmp - b.get (0, 0)) != 0.0)
    {
      /** _check that constructWithCopy behaves properly **/
      fail ("copy not effected... data visible outside");
    }
    else
    {
      _try_success ("constructWithCopy... ", "");
    }
    avals[0][0] = columnwise[0];
    final Matrix I = new Matrix (ivals);
    try
    {
      _check (I, Matrix.identity (3, 4));
      _try_success ("identity... ", "");
    }
    catch (final RuntimeException e)
    {
      fail ("identity Matrix not successfully created");
    }

    /**
     * Access Methods: getColumnDimension() getRowDimension() getArray()
     * getArrayCopy() getColumnPackedCopy() getRowPackedCopy() get(int,int)
     * getMatrix(int,int,int,int) getMatrix(int,int,int[])
     * getMatrix(int[],int,int) getMatrix(int[],int[]) set(int,int,double)
     * setMatrix(int,int,int,int,Matrix) setMatrix(int,int,int[],Matrix)
     * setMatrix(int[],int,int,Matrix) setMatrix(int[],int[],Matrix)
     **/

    _print ("\nTesting access methods...\n");

    /**
     * Various get methods:
     **/

    b = new Matrix (avals);
    if (b.getRowDimension () != rows)
    {
      fail ();
    }
    else
    {
      _try_success ("getRowDimension... ", "");
    }
    if (b.getColumnDimension () != cols)
    {
      fail ();
    }
    else
    {
      _try_success ("getColumnDimension... ", "");
    }
    b = new Matrix (avals);
    double [] [] barray = b.internalGetArray ();
    if (barray != avals)
    {
      fail ();
    }
    else
    {
      _try_success ("getArray... ", "");
    }
    barray = b.getArrayCopy ();
    if (barray == avals)
    {
      fail ("data not (deep) copied");
    }
    try
    {
      _check (barray, avals);
      _try_success ("getArrayCopy... ", "");
    }
    catch (final RuntimeException e)
    {
      fail ("data not successfully (deep) copied");
    }
    double [] bpacked = b.getColumnPackedCopy ();
    try
    {
      _check (bpacked, columnwise);
      _try_success ("getColumnPackedCopy... ", "");
    }
    catch (final RuntimeException e)
    {
      fail ("data not successfully (deep) copied by columns");
    }
    bpacked = b.getRowPackedCopy ();
    try
    {
      _check (bpacked, rowwise);
      _try_success ("getRowPackedCopy... ", "");
    }
    catch (final RuntimeException e)
    {
      fail ("data not successfully (deep) copied by rows");
    }
    try
    {
      tmp = b.get (b.getRowDimension (), b.getColumnDimension () - 1);
      fail ("OutOfBoundsException expected but not thrown");
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      try
      {
        tmp = b.get (b.getRowDimension () - 1, b.getColumnDimension ());
        fail ("OutOfBoundsException expected but not thrown");
      }
      catch (final ArrayIndexOutOfBoundsException e1)
      {
        _try_success ("get(int,int)... OutofBoundsException... ", "");
      }
    }
    catch (final IllegalArgumentException e1)
    {
      fail ("OutOfBoundsException expected but not thrown");
    }
    try
    {
      if (b.get (b.getRowDimension () - 1,
                 b.getColumnDimension () - 1) != avals[b.getRowDimension () - 1][b.getColumnDimension () - 1])
      {
        fail ("Matrix entry (i,j) not successfully retreived");
      }
      else
      {
        _try_success ("get(int,int)... ", "");
      }
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      fail ("Unexpected ArrayIndexOutOfBoundsException");
    }
    final Matrix SUB = new Matrix (subavals);
    Matrix M;
    try
    {
      M = b.getMatrix (ib, ie + b.getRowDimension () + 1, jb, je);
      fail ("ArrayIndexOutOfBoundsException expected but not thrown");
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      try
      {
        M = b.getMatrix (ib, ie, jb, je + b.getColumnDimension () + 1);
        fail ("ArrayIndexOutOfBoundsException expected but not thrown");
      }
      catch (final ArrayIndexOutOfBoundsException e1)
      {
        _try_success ("getMatrix(int,int,int,int)... ArrayIndexOutOfBoundsException... ", "");
      }
    }
    catch (final IllegalArgumentException e1)
    {
      fail ("ArrayIndexOutOfBoundsException expected but not thrown");
    }
    try
    {
      M = b.getMatrix (ib, ie, jb, je);
      try
      {
        _check (SUB, M);
        _try_success ("getMatrix(int,int,int,int)... ", "");
      }
      catch (final RuntimeException e)
      {
        fail ("submatrix not successfully retreived");
      }
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      fail ("Unexpected ArrayIndexOutOfBoundsException");
    }

    try
    {
      M = b.getMatrix (ib, ie, badcolumnindexset);
      fail ("ArrayIndexOutOfBoundsException expected but not thrown");
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      try
      {
        M = b.getMatrix (ib, ie + b.getRowDimension () + 1, columnindexset);
        fail ("ArrayIndexOutOfBoundsException expected but not thrown");
      }
      catch (final ArrayIndexOutOfBoundsException e1)
      {
        _try_success ("getMatrix(int,int,int[])... ArrayIndexOutOfBoundsException... ", "");
      }
    }
    catch (final IllegalArgumentException e1)
    {
      fail ("ArrayIndexOutOfBoundsException expected but not thrown");
    }
    try
    {
      M = b.getMatrix (ib, ie, columnindexset);
      try
      {
        _check (SUB, M);
        _try_success ("getMatrix(int,int,int[])... ", "");
      }
      catch (final RuntimeException e)
      {
        fail ("submatrix not successfully retreived");
      }
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      fail ("Unexpected ArrayIndexOutOfBoundsException");
    }
    try
    {
      M = b.getMatrix (badrowindexset, jb, je);
      fail ("ArrayIndexOutOfBoundsException expected but not thrown");
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      try
      {
        M = b.getMatrix (rowindexset, jb, je + b.getColumnDimension () + 1);
        fail ("ArrayIndexOutOfBoundsException expected but not thrown");
      }
      catch (final ArrayIndexOutOfBoundsException e1)
      {
        _try_success ("getMatrix(int[],int,int)... ArrayIndexOutOfBoundsException... ", "");
      }
    }
    catch (final IllegalArgumentException e1)
    {
      fail ("ArrayIndexOutOfBoundsException expected but not thrown");
    }
    try
    {
      M = b.getMatrix (rowindexset, jb, je);
      try
      {
        _check (SUB, M);
        _try_success ("getMatrix(int[],int,int)... ", "");
      }
      catch (final RuntimeException e)
      {
        fail ("submatrix not successfully retreived");
      }
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      fail ("Unexpected ArrayIndexOutOfBoundsException");
    }
    try
    {
      M = b.getMatrix (badrowindexset, columnindexset);
      fail ("ArrayIndexOutOfBoundsException expected but not thrown");
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      try
      {
        M = b.getMatrix (rowindexset, badcolumnindexset);
        fail ("ArrayIndexOutOfBoundsException expected but not thrown");
      }
      catch (final ArrayIndexOutOfBoundsException e1)
      {
        _try_success ("getMatrix(int[],int[])... ArrayIndexOutOfBoundsException... ", "");
      }
    }
    catch (final IllegalArgumentException e1)
    {
      fail ("ArrayIndexOutOfBoundsException expected but not thrown");
    }
    try
    {
      M = b.getMatrix (rowindexset, columnindexset);
      try
      {
        _check (SUB, M);
        _try_success ("getMatrix(int[],int[])... ", "");
      }
      catch (final RuntimeException e)
      {
        fail ("submatrix not successfully retreived");
      }
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      fail ("Unexpected ArrayIndexOutOfBoundsException");
    }

    /**
     * Various set methods:
     **/

    try
    {
      b.set (b.getRowDimension (), b.getColumnDimension () - 1, 0.);
      fail ("OutOfBoundsException expected but not thrown");
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      try
      {
        b.set (b.getRowDimension () - 1, b.getColumnDimension (), 0.);
        fail ("OutOfBoundsException expected but not thrown");
      }
      catch (final ArrayIndexOutOfBoundsException e1)
      {
        _try_success ("set(int,int,double)... OutofBoundsException... ", "");
      }
    }
    catch (final IllegalArgumentException e1)
    {
      fail ("OutOfBoundsException expected but not thrown");
    }
    try
    {
      b.set (ib, jb, 0.);
      tmp = b.get (ib, jb);
      try
      {
        _check (tmp, 0.);
        _try_success ("set(int,int,double)... ", "");
      }
      catch (final RuntimeException e)
      {
        fail ("Matrix element not successfully set");
      }
    }
    catch (final ArrayIndexOutOfBoundsException e1)
    {
      fail ("Unexpected ArrayIndexOutOfBoundsException");
    }
    M = new Matrix (2, 3, 0.);
    try
    {
      b.setMatrix (ib, ie + b.getRowDimension () + 1, jb, je, M);
      fail ("ArrayIndexOutOfBoundsException expected but not thrown");
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      try
      {
        b.setMatrix (ib, ie, jb, je + b.getColumnDimension () + 1, M);
        fail ("ArrayIndexOutOfBoundsException expected but not thrown");
      }
      catch (final ArrayIndexOutOfBoundsException e1)
      {
        _try_success ("setMatrix(int,int,int,int,Matrix)... ArrayIndexOutOfBoundsException... ", "");
      }
    }
    catch (final IllegalArgumentException e1)
    {
      fail ("ArrayIndexOutOfBoundsException expected but not thrown");
    }
    try
    {
      b.setMatrix (ib, ie, jb, je, M);
      try
      {
        _check (M.minus (b.getMatrix (ib, ie, jb, je)), M);
        _try_success ("setMatrix(int,int,int,int,Matrix)... ", "");
      }
      catch (final RuntimeException e)
      {
        fail ("submatrix not successfully set");
      }
      b.setMatrix (ib, ie, jb, je, SUB);
    }
    catch (final ArrayIndexOutOfBoundsException e1)
    {
      fail ("Unexpected ArrayIndexOutOfBoundsException");
    }
    try
    {
      b.setMatrix (ib, ie + b.getRowDimension () + 1, columnindexset, M);
      fail ("ArrayIndexOutOfBoundsException expected but not thrown");
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      try
      {
        b.setMatrix (ib, ie, badcolumnindexset, M);
        fail ("ArrayIndexOutOfBoundsException expected but not thrown");
      }
      catch (final ArrayIndexOutOfBoundsException e1)
      {
        _try_success ("setMatrix(int,int,int[],Matrix)... ArrayIndexOutOfBoundsException... ", "");
      }
    }
    catch (final IllegalArgumentException e1)
    {
      fail ("ArrayIndexOutOfBoundsException expected but not thrown");
    }
    try
    {
      b.setMatrix (ib, ie, columnindexset, M);
      try
      {
        _check (M.minus (b.getMatrix (ib, ie, columnindexset)), M);
        _try_success ("setMatrix(int,int,int[],Matrix)... ", "");
      }
      catch (final RuntimeException e)
      {
        fail ("submatrix not successfully set");
      }
      b.setMatrix (ib, ie, jb, je, SUB);
    }
    catch (final ArrayIndexOutOfBoundsException e1)
    {
      fail ("Unexpected ArrayIndexOutOfBoundsException");
    }
    try
    {
      b.setMatrix (rowindexset, jb, je + b.getColumnDimension () + 1, M);
      fail ("ArrayIndexOutOfBoundsException expected but not thrown");
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      try
      {
        b.setMatrix (badrowindexset, jb, je, M);
        fail ("ArrayIndexOutOfBoundsException expected but not thrown");
      }
      catch (final ArrayIndexOutOfBoundsException e1)
      {
        _try_success ("setMatrix(int[],int,int,Matrix)... ArrayIndexOutOfBoundsException... ", "");
      }
    }
    catch (final IllegalArgumentException e1)
    {
      fail ("ArrayIndexOutOfBoundsException expected but not thrown");
    }
    try
    {
      b.setMatrix (rowindexset, jb, je, M);
      try
      {
        _check (M.minus (b.getMatrix (rowindexset, jb, je)), M);
        _try_success ("setMatrix(int[],int,int,Matrix)... ", "");
      }
      catch (final RuntimeException e)
      {
        fail ("submatrix not successfully set");
      }
      b.setMatrix (ib, ie, jb, je, SUB);
    }
    catch (final ArrayIndexOutOfBoundsException e1)
    {
      fail ("Unexpected ArrayIndexOutOfBoundsException");
    }
    try
    {
      b.setMatrix (rowindexset, badcolumnindexset, M);
      fail ("ArrayIndexOutOfBoundsException expected but not thrown");
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      try
      {
        b.setMatrix (badrowindexset, columnindexset, M);
        fail ("ArrayIndexOutOfBoundsException expected but not thrown");
      }
      catch (final ArrayIndexOutOfBoundsException e1)
      {
        _try_success ("setMatrix(int[],int[],Matrix)... ArrayIndexOutOfBoundsException... ", "");
      }
    }
    catch (final IllegalArgumentException e1)
    {
      fail ("ArrayIndexOutOfBoundsException expected but not thrown");
    }
    try
    {
      b.setMatrix (rowindexset, columnindexset, M);
      try
      {
        _check (M.minus (b.getMatrix (rowindexset, columnindexset)), M);
        _try_success ("setMatrix(int[],int[],Matrix)... ", "");
      }
      catch (final RuntimeException e)
      {
        fail ("submatrix not successfully set");
      }
    }
    catch (final ArrayIndexOutOfBoundsException e1)
    {
      fail ("Unexpected ArrayIndexOutOfBoundsException");
    }

    /**
     * Array-like methods: minus minusEquals plus plusEquals arrayLeftDivide
     * arrayLeftDivideEquals arrayRightDivide arrayRightDivideEquals arrayTimes
     * arrayTimesEquals uminus
     **/

    _print ("\nTesting array-like methods...\n");
    Matrix S = new Matrix (columnwise, nonconformld);
    Matrix r = Matrix.random (a.getRowDimension (), a.getColumnDimension ());
    a = r;
    try
    {
      S = a.minus (S);
      fail ("nonconformance not raised");
    }
    catch (final IllegalArgumentException e)
    {
      _try_success ("minus conformance _check... ", "");
    }
    if (a.minus (r).norm1 () != 0.)
    {
      fail ("(difference of identical Matrices is nonzero,\nSubsequent use of minus should be suspect)");
    }
    else
    {
      _try_success ("minus... ", "");
    }
    a = r.getClone ();
    a.minusEquals (r);
    final Matrix Z = new Matrix (a.getRowDimension (), a.getColumnDimension ());
    try
    {
      a.minusEquals (S);
      fail ("nonconformance not raised");
    }
    catch (final IllegalArgumentException e)
    {
      _try_success ("minusEquals conformance _check... ", "");
    }
    if (a.minus (Z).norm1 () != 0.)
    {
      fail ("(difference of identical Matrices is nonzero,\nSubsequent use of minus should be suspect)");
    }
    else
    {
      _try_success ("minusEquals... ", "");
    }

    a = r.getClone ();
    b = Matrix.random (a.getRowDimension (), a.getColumnDimension ());
    c = a.minus (b);
    try
    {
      S = a.plus (S);
      fail ("nonconformance not raised");
    }
    catch (final IllegalArgumentException e)
    {
      _try_success ("plus conformance _check... ", "");
    }
    try
    {
      _check (c.plus (b), a);
      _try_success ("plus... ", "");
    }
    catch (final RuntimeException e)
    {
      fail ("(c = a - b, but c + b != a)");
    }
    c = a.minus (b);
    c.plusEquals (b);
    try
    {
      a.plusEquals (S);
      fail ("nonconformance not raised");
    }
    catch (final IllegalArgumentException e)
    {
      _try_success ("plusEquals conformance _check... ", "");
    }
    try
    {
      _check (c, a);
      _try_success ("plusEquals... ", "");
    }
    catch (final RuntimeException e)
    {
      fail ("(c = a - b, but c = c + b != a)");
    }
    a = r.uminus ();
    try
    {
      _check (a.plus (r), Z);
      _try_success ("uminus... ", "");
    }
    catch (final RuntimeException e)
    {
      fail ("(-a + a != zeros)");
    }
    a = r.getClone ();
    Matrix O = new Matrix (a.getRowDimension (), a.getColumnDimension (), 1.0);
    c = a.arrayLeftDivide (r);
    try
    {
      S = a.arrayLeftDivide (S);
      fail ("nonconformance not raised");
    }
    catch (final IllegalArgumentException e)
    {
      _try_success ("arrayLeftDivide conformance _check... ", "");
    }
    try
    {
      _check (c, O);
      _try_success ("arrayLeftDivide... ", "");
    }
    catch (final RuntimeException e)
    {
      fail ("(M.\\M != ones)");
    }
    try
    {
      a.arrayLeftDivideEquals (S);
      fail ("nonconformance not raised");
    }
    catch (final IllegalArgumentException e)
    {
      _try_success ("arrayLeftDivideEquals conformance _check... ", "");
    }
    a.arrayLeftDivideEquals (r);
    try
    {
      _check (a, O);
      _try_success ("arrayLeftDivideEquals... ", "");
    }
    catch (final RuntimeException e)
    {
      fail ("(M.\\M != ones)");
    }
    a = r.getClone ();
    try
    {
      a.arrayRightDivide (S);
      fail ("nonconformance not raised");
    }
    catch (final IllegalArgumentException e)
    {
      _try_success ("arrayRightDivide conformance _check... ", "");
    }
    c = a.arrayRightDivide (r);
    try
    {
      _check (c, O);
      _try_success ("arrayRightDivide... ", "");
    }
    catch (final RuntimeException e)
    {
      fail ("(M./M != ones)");
    }
    try
    {
      a.arrayRightDivideEquals (S);
      fail ("nonconformance not raised");
    }
    catch (final IllegalArgumentException e)
    {
      _try_success ("arrayRightDivideEquals conformance _check... ", "");
    }
    a.arrayRightDivideEquals (r);
    try
    {
      _check (a, O);
      _try_success ("arrayRightDivideEquals... ", "");
    }
    catch (final RuntimeException e)
    {
      fail ("(M./M != ones)");
    }
    a = r.getClone ();
    b = Matrix.random (a.getRowDimension (), a.getColumnDimension ());
    try
    {
      S = a.arrayTimes (S);
      fail ("nonconformance not raised");
    }
    catch (final IllegalArgumentException e)
    {
      _try_success ("arrayTimes conformance _check... ", "");
    }
    c = a.arrayTimes (b);
    try
    {
      _check (c.arrayRightDivideEquals (b), a);
      _try_success ("arrayTimes... ", "");
    }
    catch (final RuntimeException e)
    {
      fail ("(a = r, c = a.*b, but c./b != a)");
    }
    try
    {
      a.arrayTimesEquals (S);
      fail ("nonconformance not raised");
    }
    catch (final IllegalArgumentException e)
    {
      _try_success ("arrayTimesEquals conformance _check... ", "");
    }
    a.arrayTimesEquals (b);
    try
    {
      _check (a.arrayRightDivideEquals (b), r);
      _try_success ("arrayTimesEquals... ", "");
    }
    catch (final RuntimeException e)
    {
      fail ("(a = r, a = a.*b, but a./b != r)");
    }

    /**
     * I/O methods: read print serializable: writeObject readObject
     **/
    _print ("\nTesting I/O methods...\n");
    try
    {
      final DecimalFormat fmt = new DecimalFormat ("0.0000E00", DFS);

      try (final PrintWriter aPW = FileHelper.getPrintWriter (FILE_JAMA_TEST_MATRIX_OUT, StandardCharsets.UTF_8))
      {
        a.print (aPW, fmt, 10);
      }
      try (final Reader aReader = FileHelper.getBufferedReader (FILE_JAMA_TEST_MATRIX_OUT, StandardCharsets.UTF_8))
      {
        r = Matrix.read (aReader);
      }
      if (a.minus (r).norm1 () < .001)
      {
        _try_success ("print()/read()...", "");
      }
      else
      {
        fail ("Matrix read from file does not match Matrix printed to file");
      }
    }
    catch (final IOException ioe)
    {
      warningCount = _try_warning (warningCount,
                                   "print()/read()...",
                                   "unexpected I/O error, unable to run print/read test;  _check write permission in current directory and retry");
    }
    catch (final Exception e)
    {
      try
      {
        s_aLogger.error ("oops", e);
        warningCount = _try_warning (warningCount,
                                     "print()/read()...",
                                     "Formatting error... will try JDK1.1 reformulation...");
        final DecimalFormat fmt = new DecimalFormat ("0.0000", DFS);
        try (final PrintWriter aPW = FileHelper.getPrintWriter (FILE_JAMA_TEST_MATRIX_OUT, StandardCharsets.UTF_8))
        {
          a.print (aPW, fmt, 10);
        }
        try (final Reader aReader = FileHelper.getBufferedReader (FILE_JAMA_TEST_MATRIX_OUT, StandardCharsets.UTF_8))
        {
          r = Matrix.read (aReader);
        }
        if (a.minus (r).norm1 () < .001)
        {
          _try_success ("print()/read()...", "");
        }
        else
        {
          fail ("Matrix read from file does not match Matrix printed to file");
        }
      }
      catch (final IOException ioe)
      {
        warningCount = _try_warning (warningCount,
                                     "print()/read()...",
                                     "unexpected I/O error, unable to run print/read test;  _check write permission in current directory and retry");
      }
    }
    finally
    {
      FileOperations.deleteFile (FILE_JAMA_TEST_MATRIX_OUT);
    }

    r = Matrix.random (a.getRowDimension (), a.getColumnDimension ());
    final String tmpname = "TMPMATRIX.serial";
    try
    {
      try (final ObjectOutputStream out = new ObjectOutputStream (new FileOutputStream (tmpname)))
      {
        out.writeObject (r);
      }
      try (final ObjectInputStream sin = new ObjectInputStream (new FileInputStream (tmpname)))
      {
        a = (Matrix) sin.readObject ();
      }

      try
      {
        _check (a, r);
        _try_success ("writeObject(Matrix)/readObject(Matrix)...", "");
      }
      catch (final RuntimeException e)
      {
        fail ("Matrix not serialized correctly");
      }
    }
    catch (final IOException ioe)
    {
      warningCount = _try_warning (warningCount,
                                   "writeObject()/readObject()...",
                                   "unexpected I/O error, unable to run serialization test;  _check write permission in current directory and retry");
    }
    catch (final Exception e)
    {
      fail ("unexpected error in serialization test");
    }
    finally
    {
      FileOperations.deleteFile (new File (tmpname));
    }

    /**
     * LA methods: transpose times cond rank det trace norm1 norm2 normF normInf
     * solve solveTranspose inverse chol eig lu qr svd
     **/

    _print ("\nTesting linear algebra methods...\n");
    a = new Matrix (columnwise, 3);
    Matrix T = new Matrix (tvals);
    T = a.transpose ();
    try
    {
      _check (a.transpose (), T);
      _try_success ("transpose...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("transpose unsuccessful");
    }
    a.transpose ();
    try
    {
      _check (a.norm1 (), columnsummax);
      _try_success ("norm1...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect norm calculation");
    }
    try
    {
      _check (a.normInf (), rowsummax);
      _try_success ("normInf()...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect norm calculation");
    }
    try
    {
      _check (a.normF (), Math.sqrt (sumofsquares));
      _try_success ("normF...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect norm calculation");
    }
    try
    {
      _check (a.trace (), sumofdiagonals);
      _try_success ("trace()...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect trace calculation");
    }
    try
    {
      _check (a.getMatrix (0, a.getRowDimension () - 1, 0, a.getRowDimension () - 1).det (), 0.);
      _try_success ("det()...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect determinant calculation");
    }
    Matrix SQ = new Matrix (square);
    try
    {
      _check (a.times (a.transpose ()), SQ);
      _try_success ("times(Matrix)...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect Matrix-Matrix product calculation");
    }
    try
    {
      _check (a.times (0.), Z);
      _try_success ("times(double)...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect Matrix-scalar product calculation");
    }

    a = new Matrix (columnwise, 4);
    final QRDecomposition QR = a.qr ();
    r = QR.getR ();
    try
    {
      _check (a, QR.getQ ().times (r));
      _try_success ("QRDecomposition...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect QR decomposition calculation");
    }
    SingularValueDecomposition SVD = a.svd ();
    try
    {
      _check (a, SVD.getU ().times (SVD.getS ().times (SVD.getV ().transpose ())));
      _try_success ("SingularValueDecomposition...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect singular value decomposition calculation");
    }
    final Matrix DEF = new Matrix (rankdef);
    try
    {
      _check (DEF.rank (), Math.min (DEF.getRowDimension (), DEF.getColumnDimension ()) - 1);
      _try_success ("rank()...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect rank calculation");
    }
    b = new Matrix (condmat);
    SVD = b.svd ();
    final double [] singularvalues = SVD.getSingularValues ();
    try
    {
      _check (b.cond (),
              singularvalues[0] / singularvalues[Math.min (b.getRowDimension (), b.getColumnDimension ()) - 1]);
      _try_success ("cond()...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect condition number calculation");
    }
    final int n = a.getColumnDimension ();
    a = a.getMatrix (0, n - 1, 0, n - 1);
    a.set (0, 0, 0.);
    final LUDecomposition LU = a.lu ();
    try
    {
      _check (a.getMatrix (LU.getPivot (), 0, n - 1), LU.getL ().times (LU.getU ()));
      _try_success ("LUDecomposition...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect LU decomposition calculation");
    }
    Matrix x = a.inverse ();
    try
    {
      _check (a.times (x), Matrix.identity (3, 3));
      _try_success ("inverse()...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect inverse calculation");
    }
    O = new Matrix (SUB.getRowDimension (), 1, 1.0);
    final Matrix SOL = new Matrix (sqSolution);
    SQ = SUB.getMatrix (0, SUB.getRowDimension () - 1, 0, SUB.getRowDimension () - 1);
    try
    {
      _check (SQ.solve (SOL), O);
      _try_success ("solve()...", "");
    }
    catch (final IllegalArgumentException e1)
    {
      fail (e1.getMessage ());
    }
    catch (final RuntimeException e)
    {
      fail (e.getMessage ());
    }
    a = new Matrix (pvals);
    final CholeskyDecomposition Chol = a.chol ();
    final Matrix L = Chol.getL ();
    try
    {
      _check (a, L.times (L.transpose ()));
      _try_success ("CholeskyDecomposition...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect Cholesky decomposition calculation");
    }
    x = Chol.solve (Matrix.identity (3, 3));
    try
    {
      _check (a.times (x), Matrix.identity (3, 3));
      _try_success ("CholeskyDecomposition solve()...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect Choleskydecomposition solve calculation");
    }
    EigenvalueDecomposition Eig = a.eig ();
    Matrix D = Eig.getD ();
    Matrix V = Eig.getV ();
    try
    {
      _check (a.times (V), V.times (D));
      _try_success ("EigenvalueDecomposition (symmetric)...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect symmetric Eigenvalue decomposition calculation");
    }
    a = new Matrix (evals);
    Eig = a.eig ();
    D = Eig.getD ();
    V = Eig.getV ();
    try
    {
      _check (a.times (V), V.times (D));
      _try_success ("EigenvalueDecomposition (nonsymmetric)...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect nonsymmetric Eigenvalue decomposition calculation");
    }

    try
    {
      _print ("\nTesting Eigenvalue; If this hangs, we've failed\n");
      final Matrix bA = new Matrix (badeigs);
      final EigenvalueDecomposition bEig = bA.eig ();
      assertNotNull (bEig);
      _try_success ("EigenvalueDecomposition (hang)...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect termination");
    }

    _print ("\nTestMatrix completed.\n");
    _print ("Total warnings reported: " + warningCount + "\n");

    assertEquals (0, warningCount);
  }

  /** private utility routines **/

  /** Check magnitude of difference of scalars. **/

  private static void _check (final double x, final double y)
  {
    if (x == 0 && Math.abs (y) < 10 * EPSILON)
      return;
    if (y == 0 && Math.abs (x) < 10 * EPSILON)
      return;
    if (Math.abs (x - y) > 10 * EPSILON * Math.max (Math.abs (x), Math.abs (y)))
    {
      throw new IllegalArgumentException ("The difference x-y is too large: x = " +
                                          Double.toString (x) +
                                          "  y = " +
                                          Double.toString (y));
    }
  }

  /** Check norm of difference of "vectors". **/

  private static void _check (final double [] x, final double [] y)
  {
    if (x.length == y.length)
    {
      for (int i = 0; i < x.length; i++)
        _check (x[i], y[i]);
    }
    else
    {
      throw new IllegalArgumentException ("Attempt to compare vectors of different lengths");
    }
  }

  /** Check norm of difference of arrays. **/

  private static void _check (@Nonnull final double [] [] x, @Nonnull final double [] [] y)
  {
    final Matrix a = new Matrix (x);
    final Matrix b = new Matrix (y);
    _check (a, b);
  }

  /** Check norm of difference of Matrices. **/

  private static void _check (@Nonnull final Matrix x, @Nonnull final Matrix y)
  {
    if (x.norm1 () == 0. && y.norm1 () < 10 * EPSILON)
      return;
    if (y.norm1 () == 0. && x.norm1 () < 10 * EPSILON)
      return;
    if (x.minus (y).norm1 () > 1000 * EPSILON * Math.max (x.norm1 (), y.norm1 ()))
      throw new IllegalArgumentException ("The norm of (x-y) is too large: " + Double.toString (x.minus (y).norm1 ()));
  }

  /** Shorten spelling of print. **/

  private static void _print (final String s)
  {
    s_aLogger.info (StringHelper.trimEnd (s, '\n'));
  }

  /** Print appropriate messages for successful outcome try **/

  private static void _try_success (final String s, final String e)
  {
    _print (">    " + s + "success\n");
    if (StringHelper.hasText (e))
      _print (">      Message: " + e + "\n");
  }

  /** Print appropriate messages for unsuccessful outcome try **/

  private static int _try_warning (final int count, final String s, final String e)
  {
    _print (">    " + s + "*** warning ***\n>      Message: " + e + "\n");
    return count + 1;
  }
}
