/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import com.helger.annotation.Nonnull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.FileOperations;
import com.helger.commons.string.StringHelper;

/**
 * TestMatrix tests the functionality of the Jama MatrixInt class and associated
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
public final class MatrixIntTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MatrixIntTest.class);
  private static final File FILE_JAMA_TEST_MATRIX_OUT = new File ("Jamaout");
  private static final double EPSILON = Math.pow (2.0, -52.0);
  private static final DecimalFormatSymbols DFS = DecimalFormatSymbols.getInstance (Locale.US);

  @Test
  public void testMain ()
  {
    // Uncomment this to test IO in a different locale.
    // Locale.setDefault(Locale.GERMAN);
    int warningCount = 0;
    int tmp;
    final int [] columnwise = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
    final int [] rowwise = { 1, 4, 7, 10, 2, 5, 8, 11, 3, 6, 9, 12 };
    final int [] [] avals = { { 1, 4, 7, 10 }, { 2, 5, 8, 11 }, { 3, 6, 9, 12 } };
    final int [] [] tvals = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 }, { 10, 11, 12 } };
    final int [] [] subavals = { { 5, 8, 11 }, { 6, 9, 12 } };
    final int [] [] rvals = { { 1, 4, 7 }, { 2, 5, 8, 11 }, { 3, 6, 9, 12 } };
    final int [] [] ivals = { { 1, 0, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 1, 0 } };
    final int [] [] square = { { 166, 188, 210 }, { 188, 214, 240 }, { 210, 240, 270 } };
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
    /* index ranges for sub MatrixInt */
    final int ib = 1;
    final int ie = 2;
    final int jb = 1;
    final int je = 3;
    final int [] rowindexset = { 1, 2 };
    final int [] badrowindexset = { 1, 3 };
    final int [] columnindexset = { 1, 2, 3 };
    final int [] badcolumnindexset = { 1, 2, 4 };
    final int columnsummax = 33;
    final int rowsummax = 30;
    final int sumofdiagonals = 15;
    final int sumofsquares = 650;

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
      new MatrixInt (columnwise, invalidld);
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
      final MatrixInt a = new MatrixInt (rvals);
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
      final MatrixInt a = MatrixInt.constructWithCopy (rvals);
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

    MatrixInt a = new MatrixInt (columnwise, validld);
    MatrixInt b = new MatrixInt (avals);
    tmp = b.get (0, 0);
    avals[0][0] = 0;
    MatrixInt C = b.minus (a);
    avals[0][0] = tmp;
    b = MatrixInt.constructWithCopy (avals);
    tmp = b.get (0, 0);
    avals[0][0] = 0;
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
    final MatrixInt I = new MatrixInt (ivals);
    try
    {
      _check (I, MatrixInt.identity (3, 4));
      _try_success ("identity... ", "");
    }
    catch (final RuntimeException e)
    {
      fail ("identity MatrixInt not successfully created");
    }

    /**
     * Access Methods: getColumnDimension() getRowDimension() getArray()
     * getArrayCopy() getColumnPackedCopy() getRowPackedCopy() get(int,int)
     * getMatrix(int,int,int,int) getMatrix(int,int,int[])
     * getMatrix(int[],int,int) getMatrix(int[],int[]) set(int,int,double)
     * setMatrix(int,int,int,int,MatrixInt) setMatrix(int,int,int[],MatrixInt)
     * setMatrix(int[],int,int,MatrixInt) setMatrix(int[],int[],MatrixInt)
     **/

    _print ("\nTesting access methods...\n");

    /**
     * Various get methods:
     **/

    b = new MatrixInt (avals);
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
    b = new MatrixInt (avals);
    int [] [] barray = b.internalGetArray ();
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
    int [] bpacked = b.getColumnPackedCopy ();
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
        fail ("MatrixInt entry (i,j) not successfully retreived");
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
    final MatrixInt SUB = new MatrixInt (subavals);
    MatrixInt M;
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
      b.set (b.getRowDimension (), b.getColumnDimension () - 1, 0);
      fail ("OutOfBoundsException expected but not thrown");
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      try
      {
        b.set (b.getRowDimension () - 1, b.getColumnDimension (), 0);
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
      b.set (ib, jb, 0);
      tmp = b.get (ib, jb);
      try
      {
        _check (tmp, 0);
        _try_success ("set(int,int,double)... ", "");
      }
      catch (final RuntimeException e)
      {
        fail ("MatrixInt element not successfully set");
      }
    }
    catch (final ArrayIndexOutOfBoundsException e1)
    {
      fail ("Unexpected ArrayIndexOutOfBoundsException");
    }
    M = new MatrixInt (2, 3, 0);
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
        _try_success ("setMatrix(int,int,int,int,MatrixInt)... ArrayIndexOutOfBoundsException... ", "");
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
        _try_success ("setMatrix(int,int,int,int,MatrixInt)... ", "");
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
        _try_success ("setMatrix(int,int,int[],MatrixInt)... ArrayIndexOutOfBoundsException... ", "");
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
        _try_success ("setMatrix(int,int,int[],MatrixInt)... ", "");
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
        _try_success ("setMatrix(int[],int,int,MatrixInt)... ArrayIndexOutOfBoundsException... ", "");
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
        _try_success ("setMatrix(int[],int,int,MatrixInt)... ", "");
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
        _try_success ("setMatrix(int[],int[],MatrixInt)... ArrayIndexOutOfBoundsException... ", "");
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
        _try_success ("setMatrix(int[],int[],MatrixInt)... ", "");
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
    MatrixInt S = new MatrixInt (columnwise, nonconformld);
    MatrixInt r = MatrixInt.random (a.getRowDimension (), a.getColumnDimension ());
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
    if (a.minus (r).norm1 () != 0)
    {
      fail ("(difference of identical Matrices is nonzero,\nSubsequent use of minus should be suspect)");
    }
    else
    {
      _try_success ("minus... ", "");
    }
    a = r.getClone ();
    a.minusEquals (r);
    final MatrixInt Z = new MatrixInt (a.getRowDimension (), a.getColumnDimension ());
    try
    {
      a.minusEquals (S);
      fail ("nonconformance not raised");
    }
    catch (final IllegalArgumentException e)
    {
      _try_success ("minusEquals conformance _check... ", "");
    }
    if (a.minus (Z).norm1 () != 0)
    {
      fail ("(difference of identical Matrices is nonzero,\nSubsequent use of minus should be suspect)");
    }
    else
    {
      _try_success ("minusEquals... ", "");
    }

    a = r.getClone ();
    b = MatrixInt.random (a.getRowDimension (), a.getColumnDimension ());
    C = a.minus (b);
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
      _check (C.plus (b), a);
      _try_success ("plus... ", "");
    }
    catch (final RuntimeException e)
    {
      fail ("(C = a - b, but C + b != a)");
    }
    C = a.minus (b);
    C.plusEquals (b);
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
      _check (C, a);
      _try_success ("plusEquals... ", "");
    }
    catch (final RuntimeException e)
    {
      fail ("(C = a - b, but C = C + b != a)");
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
    final MatrixInt O = new MatrixInt (a.getRowDimension (), a.getColumnDimension (), 1);
    C = a.arrayLeftDivide (r);
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
      _check (C, O);
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
    C = a.arrayRightDivide (r);
    try
    {
      _check (C, O);
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
    b = MatrixInt.random (a.getRowDimension (), a.getColumnDimension ());
    try
    {
      S = a.arrayTimes (S);
      fail ("nonconformance not raised");
    }
    catch (final IllegalArgumentException e)
    {
      _try_success ("arrayTimes conformance _check... ", "");
    }
    C = a.arrayTimes (b);
    try
    {
      _check (C.arrayRightDivideEquals (b), a);
      _try_success ("arrayTimes... ", "");
    }
    catch (final RuntimeException e)
    {
      fail ("(a = r, C = a.*b, but C./b != a)");
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
      final DecimalFormat fmt = new DecimalFormat ("0", DFS);

      try (final PrintWriter aPW = FileHelper.getPrintWriter (FILE_JAMA_TEST_MATRIX_OUT, StandardCharsets.UTF_8))
      {
        a.print (aPW, fmt, 10);
      }
      try (final Reader aReader = FileHelper.getReader (FILE_JAMA_TEST_MATRIX_OUT, StandardCharsets.UTF_8))
      {
        r = MatrixInt.read (aReader);
      }
      if (a.minus (r).norm1 () < .001)
      {
        _try_success ("print()/read()...", "");
      }
      else
      {
        fail ("MatrixInt read from file does not match MatrixInt printed to file");
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
        LOGGER.error ("oops", e);
        warningCount = _try_warning (warningCount,
                                     "print()/read()...",
                                     "Formatting error... will try JDK1.1 reformulation...");
        final DecimalFormat fmt = new DecimalFormat ("0", DFS);
        try (final PrintWriter aPW = FileHelper.getPrintWriter (FILE_JAMA_TEST_MATRIX_OUT, StandardCharsets.UTF_8))
        {
          a.print (aPW, fmt, 10);
        }
        try (final Reader aReader = FileHelper.getBufferedReader (FILE_JAMA_TEST_MATRIX_OUT, StandardCharsets.UTF_8))
        {
          r = MatrixInt.read (aReader);
        }
        if (a.minus (r).norm1 () < .001)
        {
          _try_success ("print()/read()...", "");
        }
        else
        {
          fail ("MatrixInt read from file does not match MatrixInt printed to file");
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

    r = MatrixInt.random (a.getRowDimension (), a.getColumnDimension ());
    final String tmpname = "TMPMATRIX.serial";
    try
    {
      try (final ObjectOutputStream out = new ObjectOutputStream (new FileOutputStream (tmpname)))
      {
        out.writeObject (r);
      }
      try (final ObjectInputStream sin = new ObjectInputStream (new FileInputStream (tmpname)))
      {
        a = (MatrixInt) sin.readObject ();
      }

      try
      {
        _check (a, r);
        _try_success ("writeObject(MatrixInt)/readObject(MatrixInt)...", "");
      }
      catch (final RuntimeException e)
      {
        fail ("MatrixInt not serialized correctly");
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
    a = new MatrixInt (columnwise, 3);
    MatrixInt T = new MatrixInt (tvals);
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
    assertNotNull (a.transpose ());
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
    final MatrixInt SQ = new MatrixInt (square);
    try
    {
      _check (a.times (a.transpose ()), SQ);
      _try_success ("times(MatrixInt)...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect MatrixInt-MatrixInt product calculation");
    }
    try
    {
      _check (a.times (0), Z);
      _try_success ("times(double)...", "");
    }
    catch (final RuntimeException e)
    {
      fail ("incorrect MatrixInt-scalar product calculation");
    }

    a = new MatrixInt (columnwise, 4);
    assertNotNull (a);
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

  private static void _check (final int x, final int y)
  {
    if (x != y)
      throw new IllegalArgumentException ("The difference x-y is !=0: x = " + x + "  y = " + y);
  }

  /** Check norm of difference of "vectors". **/

  private static void _check (final int [] x, final int [] y)
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

  private static void _check (@Nonnull final int [] [] x, @Nonnull final int [] [] y)
  {
    final MatrixInt a = new MatrixInt (x);
    final MatrixInt b = new MatrixInt (y);
    _check (a, b);
  }

  /** Check norm of difference of Matrices. **/

  private static void _check (@Nonnull final MatrixInt x, @Nonnull final MatrixInt y)
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
    LOGGER.info (StringHelper.trimEnd (s, '\n'));
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
