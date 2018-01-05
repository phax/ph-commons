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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.text.NumberFormat;
import java.util.Arrays;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.math.MathHelper;
import com.helger.commons.random.RandomHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.system.SystemHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Jama = Java Matrix class.
 * <P>
 * The Java Matrix Class provides the fundamental operations of numerical linear
 * algebra. Various constructors create Matrices from two dimensional arrays of
 * double precision floating point numbers. Various "gets" and "sets" provide
 * access to submatrices and matrix elements. Several methods implement basic
 * matrix arithmetic, including matrix addition and multiplication, matrix
 * norms, and element-by-element array operations. Methods for reading and
 * printing matrices are also included. All the operations in this version of
 * the Matrix Class involve real matrices. Complex matrices may be handled in a
 * future version.
 * </P>
 * <P>
 * Five fundamental matrix decompositions, which consist of pairs or triples of
 * matrices, permutation vectors, and the like, produce results in five
 * decomposition classes. These decompositions are accessed by the Matrix class
 * to compute solutions of simultaneous linear equations, determinants, inverses
 * and other matrix functions. The five decompositions are:
 * </P>
 * <UL>
 * <LI>Cholesky Decomposition of symmetric, positive definite matrices.
 * <LI>LU Decomposition of rectangular matrices.
 * <LI>QR Decomposition of rectangular matrices.
 * <LI>Singular Value Decomposition of rectangular matrices.
 * <LI>Eigenvalue Decomposition of both symmetric and nonsymmetric square
 * matrices.
 * </UL>
 * <DL>
 * <DT><B>Example of use:</B></DT>
 * <DD>Solve a linear system A x = b and compute the residual norm, ||b - A x||.
 *
 * <PRE>
 * double [] [] vals = { { 1., 2., 3 }, { 4., 5., 6. }, { 7., 8., 10. } };
 * Matrix A = new Matrix (vals);
 * Matrix b = Matrix.random (3, 1);
 * Matrix x = A.solve (b);
 * Matrix r = A.times (x).minus (b);
 * double rnorm = r.normInf ();
 * </PRE>
 *
 * </DD>
 * </DL>
 *
 * @author The MathWorks, Inc. and the National Institute of Standards and
 *         Technology.
 * @version 5 August 1998
 */
public class MatrixInt implements Serializable, ICloneable <MatrixInt>
{
  /**
   * Array for internal storage of elements.
   *
   * @serial internal array storage.
   */
  private final int [] [] m_aData;

  /**
   * Row dimensions.
   *
   * @serial row dimension.
   */
  private final int m_nRows;

  /**
   * Column dimensions.
   *
   * @serial column dimension.
   */
  private final int m_nCols;

  /**
   * Construct an nRows-by-nCols matrix of zeros.
   *
   * @param nRows
   *        Number of rows.
   * @param nCols
   *        Number of columns.
   */
  public MatrixInt (@Nonnegative final int nRows, @Nonnegative final int nCols)
  {
    ValueEnforcer.isGT0 (nRows, "Rows");
    ValueEnforcer.isGT0 (nCols, "Cols");
    m_nRows = nRows;
    m_nCols = nCols;
    m_aData = new int [nRows] [nCols];
  }

  /**
   * Construct an nRows-by-nCols constant matrix.
   *
   * @param nRows
   *        Number of rows.
   * @param nCols
   *        Number of columns.
   * @param nValue
   *        Fill the matrix with this scalar value.
   */
  public MatrixInt (@Nonnegative final int nRows, @Nonnegative final int nCols, final int nValue)
  {
    this (nRows, nCols);
    for (int nRow = 0; nRow < nRows; nRow++)
      Arrays.fill (m_aData[nRow], nValue);
  }

  /**
   * Construct a matrix from a 2-D array.
   *
   * @param aOther
   *        Two-dimensional array of doubles.
   * @exception IllegalArgumentException
   *            All rows must have the same length
   * @see #constructWithCopy
   */
  @SuppressFBWarnings ("EI_EXPOSE_REP")
  MatrixInt (@Nonnull final int [] [] aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");
    ValueEnforcer.isGT0 (aOther.length, "rows");
    ValueEnforcer.isGT0 (aOther[0].length, "cols");

    m_nRows = aOther.length;
    m_nCols = aOther[0].length;
    for (int nRow = 0; nRow < m_nRows; nRow++)
      if (aOther[nRow].length != m_nCols)
        throw new IllegalArgumentException ("All rows must have the same length.");
    m_aData = aOther;
  }

  /**
   * Construct a matrix quickly without checking arguments.
   *
   * @param aOther
   *        Two-dimensional array of doubles. Is directly reused!
   * @param nRows
   *        Number of rows.
   * @param nCols
   *        Number of columns.
   */
  @SuppressFBWarnings ("EI_EXPOSE_REP")
  MatrixInt (@Nonnull final int [] [] aOther, @Nonnegative final int nRows, @Nonnegative final int nCols)
  {
    ValueEnforcer.notNull (aOther, "Other");
    ValueEnforcer.isGT0 (nRows, "rows");
    ValueEnforcer.isGT0 (nCols, "cols");
    ValueEnforcer.isTrue (aOther.length >= nRows, "array is too short");
    for (int nRow = 0; nRow < nRows; nRow++)
      ValueEnforcer.isTrue (aOther[nRow].length >= nCols, "All rows must have the same length.");

    m_aData = aOther;
    m_nRows = nRows;
    m_nCols = nCols;
  }

  /**
   * Construct a matrix from a one-dimensional packed array
   *
   * @param aVals
   *        One-dimensional array of doubles, packed by columns (ala Fortran).
   * @param nRows
   *        Number of rows.
   * @exception IllegalArgumentException
   *            Array length must be a multiple of nRows.
   */
  public MatrixInt (@Nonnull final int [] aVals, @Nonnegative final int nRows)
  {
    m_nRows = nRows;
    m_nCols = (nRows != 0 ? aVals.length / nRows : 0);
    if (nRows * m_nCols != aVals.length)
      throw new IllegalArgumentException ("Array length must be a multiple of nRows.");

    m_aData = new int [nRows] [m_nCols];
    for (int nRow = 0; nRow < nRows; nRow++)
    {
      final int [] aRow = m_aData[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        aRow[nCol] = aVals[nRow + nCol * nRows];
    }
  }

  /**
   * Construct a matrix from a copy of a 2-D array.
   *
   * @param aArray
   *        Two-dimensional array of doubles.
   * @return The new matrix
   * @exception IllegalArgumentException
   *            All rows must have the same length
   */
  @Nonnull
  public static MatrixInt constructWithCopy (@Nonnull final int [] [] aArray)
  {
    final int nRows = aArray.length;
    final int nCols = aArray[0].length;
    final MatrixInt aCopy = new MatrixInt (nRows, nCols);
    final int [] [] aCopyArray = aCopy.internalGetArray ();
    for (int nRow = 0; nRow < nRows; nRow++)
    {
      final int [] aSrcRow = aArray[nRow];
      final int [] aDstRow = aCopyArray[nRow];
      if (aSrcRow.length != nCols)
        throw new IllegalArgumentException ("All rows must have the same length.");
      System.arraycopy (aSrcRow, 0, aDstRow, 0, aSrcRow.length);
    }
    return aCopy;
  }

  /**
   * @return a deep copy of a matrix
   */
  @Nonnull
  @ReturnsMutableCopy
  public MatrixInt getClone ()
  {
    final MatrixInt aCopy = new MatrixInt (m_nRows, m_nCols);
    final int [] [] aCopyArray = aCopy.m_aData;
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aSrcRow = m_aData[nRow];
      final int [] aDstRow = aCopyArray[nRow];
      System.arraycopy (aSrcRow, 0, aDstRow, 0, aSrcRow.length);
    }
    return aCopy;
  }

  /**
   * Access the internal two-dimensional array.
   *
   * @return Pointer to the two-dimensional array of matrix elements.
   */
  @SuppressFBWarnings ("EI_EXPOSE_REP")
  @Nonnull
  public int [] [] internalGetArray ()
  {
    return m_aData;
  }

  /**
   * Copy the internal two-dimensional array.
   *
   * @return Two-dimensional array copy of matrix elements.
   */
  @Nonnull
  @ReturnsMutableCopy
  public int [] [] getArrayCopy ()
  {
    final int [] [] aArray = new int [m_nRows] [m_nCols];
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aSrcRow = m_aData[nRow];
      final int [] aDstRow = aArray[nRow];
      System.arraycopy (aSrcRow, 0, aDstRow, 0, m_nCols);
    }
    return aArray;
  }

  /**
   * Make a one-dimensional column packed copy of the internal array.
   *
   * @return Matrix elements packed in a one-dimensional array by columns.
   */
  @Nonnull
  public int [] getColumnPackedCopy ()
  {
    final int [] ret = new int [m_nRows * m_nCols];
    for (int nCol = 0; nCol < m_nCols; nCol++)
    {
      final int nRowIndex = nCol * m_nRows;
      for (int nRow = 0; nRow < m_nRows; nRow++)
        ret[nRow + nRowIndex] = m_aData[nRow][nCol];
    }
    return ret;
  }

  /**
   * Make a one-dimensional row packed copy of the internal array.
   *
   * @return Matrix elements packed in a one-dimensional array by rows.
   */
  @Nonnull
  public int [] getRowPackedCopy ()
  {
    final int [] ret = new int [m_nRows * m_nCols];
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aSrcRow = m_aData[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        ret[nRow * m_nCols + nCol] = aSrcRow[nCol];
    }
    return ret;
  }

  /**
   * Get row dimension.
   *
   * @return nRows, the number of rows.
   */
  @Nonnegative
  public int getRowDimension ()
  {
    return m_nRows;
  }

  /**
   * Get column dimension.
   *
   * @return nCols, the number of columns.
   */
  @Nonnegative
  public int getColumnDimension ()
  {
    return m_nCols;
  }

  /**
   * @return <code>true</code> if the matrix is symmetrical, meaning number of
   *         rows and columns is identical.
   */
  public boolean isSymmetrical ()
  {
    return m_nRows == m_nCols;
  }

  /**
   * Get a single element.
   *
   * @param nRow
   *        Row index.
   * @param nCol
   *        Column index.
   * @return A(nRow,nCol)
   */
  public int get (@Nonnegative final int nRow, @Nonnegative final int nCol)
  {
    return m_aData[nRow][nCol];
  }

  /**
   * Get a submatrix.
   *
   * @param nStartRowIndex
   *        Initial row index
   * @param nEndRowIndex
   *        Final row index
   * @param nStartColumnIndex
   *        Initial column index
   * @param nEndColumnIndex
   *        Final column index
   * @return Matrix(nStartRowIndex:nEndRowIndex,nStartColumnIndex:
   *         nEndColumnIndex )
   * @exception ArrayIndexOutOfBoundsException
   *            Submatrix indices
   */
  @Nonnull
  @ReturnsMutableCopy
  public MatrixInt getMatrix (@Nonnegative final int nStartRowIndex,
                              @Nonnegative final int nEndRowIndex,
                              @Nonnegative final int nStartColumnIndex,
                              @Nonnegative final int nEndColumnIndex)
  {
    final MatrixInt aNewMatrix = new MatrixInt (nEndRowIndex -
                                                nStartRowIndex +
                                                1,
                                                nEndColumnIndex - nStartColumnIndex + 1);
    final int [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = nStartRowIndex; nRow <= nEndRowIndex; nRow++)
    {
      final int [] aSrcRow = m_aData[nRow];
      final int [] aDstRow = aNewArray[nRow - nStartRowIndex];
      for (int nCol = nStartColumnIndex; nCol <= nEndColumnIndex; nCol++)
        aDstRow[nCol - nStartColumnIndex] = aSrcRow[nCol];
    }
    return aNewMatrix;
  }

  /**
   * Get a submatrix.
   *
   * @param aRows
   *        Array of row indices.
   * @param aCols
   *        Array of column indices.
   * @return Matrix(aRows(:),aCols(:))
   * @exception ArrayIndexOutOfBoundsException
   *            Submatrix indices
   */
  @Nonnull
  @ReturnsMutableCopy
  public MatrixInt getMatrix (@Nonnull final int [] aRows, @Nonnull final int [] aCols)
  {
    final MatrixInt aNewMatrix = new MatrixInt (aRows.length, aCols.length);
    final int [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = 0; nRow < aRows.length; nRow++)
    {
      final int nRowIndex = aRows[nRow];
      final int [] aSrcRow = m_aData[nRowIndex];
      final int [] aDstRow = aNewArray[nRow];
      for (int nCol = 0; nCol < aCols.length; nCol++)
        aDstRow[nCol] = aSrcRow[aCols[nCol]];
    }
    return aNewMatrix;
  }

  /**
   * Get a submatrix.
   *
   * @param nStartRowIndex
   *        Initial row index
   * @param nEndRowIndex
   *        Final row index
   * @param aCols
   *        Array of column indices.
   * @return Matrix(nStartRowIndex:nEndRowIndex,aCols(:))
   * @exception ArrayIndexOutOfBoundsException
   *            Submatrix indices
   */
  @Nonnull
  @ReturnsMutableCopy
  public MatrixInt getMatrix (@Nonnegative final int nStartRowIndex,
                              @Nonnegative final int nEndRowIndex,
                              @Nonnull final int [] aCols)
  {
    final MatrixInt aNewMatrix = new MatrixInt (nEndRowIndex - nStartRowIndex + 1, aCols.length);
    final int [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nCol = 0; nCol < aCols.length; nCol++)
    {
      final int nColIndex = aCols[nCol];
      for (int nRow = nStartRowIndex; nRow <= nEndRowIndex; nRow++)
        aNewArray[nRow - nStartRowIndex][nCol] = m_aData[nRow][nColIndex];
    }
    return aNewMatrix;
  }

  /**
   * Get a submatrix.
   *
   * @param aRows
   *        Array of row indices.
   * @param nStartColumnIndex
   *        Initial column index
   * @param nEndColumnIndex
   *        Final column index
   * @return Matrix(aRows(:),nStartColumnIndex:nEndColumnIndex)
   * @exception ArrayIndexOutOfBoundsException
   *            Submatrix indices
   */
  @Nonnull
  @ReturnsMutableCopy
  public MatrixInt getMatrix (@Nonnull final int [] aRows,
                              @Nonnegative final int nStartColumnIndex,
                              @Nonnegative final int nEndColumnIndex)
  {
    final MatrixInt aNewMatrix = new MatrixInt (aRows.length, nEndColumnIndex - nStartColumnIndex + 1);
    final int [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = 0; nRow < aRows.length; nRow++)
    {
      final int nRowIndex = aRows[nRow];
      final int [] aSrcRow = m_aData[nRowIndex];
      final int [] aDstRow = aNewArray[nRow];
      for (int nCol = nStartColumnIndex; nCol <= nEndColumnIndex; nCol++)
        aDstRow[nCol - nStartColumnIndex] = aSrcRow[nCol];
    }
    return aNewMatrix;
  }

  /**
   * Set a single element.
   *
   * @param nRow
   *        Row index.
   * @param nCol
   *        Column index.
   * @param nValue
   *        A(nRow,nCol).
   */
  public void set (@Nonnegative final int nRow, @Nonnegative final int nCol, final int nValue)
  {
    m_aData[nRow][nCol] = nValue;
  }

  /**
   * Set a submatrix.
   *
   * @param nStartRowIndex
   *        Initial row index
   * @param nEndRowIndex
   *        Final row index
   * @param nStartColumnIndex
   *        Initial column index
   * @param nEndColumnIndex
   *        Final column index
   * @param aMatrix
   *        Matrix(nStartRowIndex:nEndRowIndex,nStartColumnIndex:nEndColumnIndex
   *        )
   * @exception ArrayIndexOutOfBoundsException
   *            Submatrix indices
   */
  public void setMatrix (@Nonnegative final int nStartRowIndex,
                         @Nonnegative final int nEndRowIndex,
                         @Nonnegative final int nStartColumnIndex,
                         @Nonnegative final int nEndColumnIndex,
                         @Nonnull final MatrixInt aMatrix)
  {
    for (int nRow = nStartRowIndex; nRow <= nEndRowIndex; nRow++)
    {
      final int [] aRow = m_aData[nRow];
      for (int nCol = nStartColumnIndex; nCol <= nEndColumnIndex; nCol++)
        aRow[nCol] = aMatrix.get (nRow - nStartRowIndex, nCol - nStartColumnIndex);
    }
  }

  /**
   * Set a submatrix.
   *
   * @param aRows
   *        Array of row indices.
   * @param aCols
   *        Array of column indices.
   * @param aMatrix
   *        Matrix(aRows(:),aCols(:))
   * @exception ArrayIndexOutOfBoundsException
   *            Submatrix indices
   */
  public void setMatrix (@Nonnull final int [] aRows, @Nonnull final int [] aCols, @Nonnull final MatrixInt aMatrix)
  {
    for (int nRow = 0; nRow < aRows.length; nRow++)
    {
      final int nRowIndex = aRows[nRow];
      final int [] aRow = m_aData[nRowIndex];
      for (int nCol = 0; nCol < aCols.length; nCol++)
        aRow[aCols[nCol]] = aMatrix.get (nRow, nCol);
    }
  }

  /**
   * Set a submatrix.
   *
   * @param aRows
   *        Array of row indices.
   * @param nStartColumnIndex
   *        Initial column index
   * @param nEndColumnIndex
   *        Final column index
   * @param aMatrix
   *        Matrix(aRows(:),nStartColumnIndex:nEndColumnIndex
   * @exception ArrayIndexOutOfBoundsException
   *            Submatrix indices
   */
  public void setMatrix (@Nonnull final int [] aRows,
                         @Nonnegative final int nStartColumnIndex,
                         @Nonnegative final int nEndColumnIndex,
                         @Nonnull final MatrixInt aMatrix)
  {
    for (int nRow = 0; nRow < aRows.length; nRow++)
    {
      final int nRowIndex = aRows[nRow];
      final int [] aRow = m_aData[nRowIndex];
      for (int nCol = nStartColumnIndex; nCol <= nEndColumnIndex; nCol++)
        aRow[nCol] = aMatrix.get (nRow, nCol - nStartColumnIndex);
    }
  }

  /**
   * Set a submatrix.
   *
   * @param nStartRowIndex
   *        Initial row index
   * @param nEndRowIndex
   *        Final row index
   * @param aCols
   *        Array of column indices.
   * @param aMatrix
   *        Matrix(nStartRowIndex:nEndRowIndex,aCols(:))
   * @exception ArrayIndexOutOfBoundsException
   *            Submatrix indices
   */
  public void setMatrix (@Nonnegative final int nStartRowIndex,
                         @Nonnegative final int nEndRowIndex,
                         @Nonnull final int [] aCols,
                         @Nonnull final MatrixInt aMatrix)
  {
    for (int nRow = nStartRowIndex; nRow <= nEndRowIndex; nRow++)
    {
      final int [] aRow = m_aData[nRow];
      for (int nCol = 0; nCol < aCols.length; nCol++)
      {
        final int nColIndex = aCols[nCol];
        aRow[nColIndex] = aMatrix.get (nRow - nStartRowIndex, nCol);
      }
    }
  }

  /**
   * Matrix transpose.
   *
   * @return A'
   */
  @Nonnull
  @ReturnsMutableCopy
  public MatrixInt transpose ()
  {
    final MatrixInt aNewMatrix = new MatrixInt (m_nCols, m_nRows);
    final int [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aSrcRow = m_aData[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        aNewArray[nCol][nRow] = aSrcRow[nCol];
    }
    return aNewMatrix;
  }

  /**
   * One norm
   *
   * @return maximum column sum.
   */
  public int norm1 ()
  {
    int nRet = 0;
    for (int nCol = 0; nCol < m_nCols; nCol++)
    {
      int nSum = 0;
      for (int nRow = 0; nRow < m_nRows; nRow++)
        nSum += MathHelper.abs (m_aData[nRow][nCol]);
      nRet = Math.max (nRet, nSum);
    }
    return nRet;
  }

  /**
   * Infinity norm
   *
   * @return maximum row sum.
   */
  public int normInf ()
  {
    int ret = 0;
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aRow = m_aData[nRow];
      int nSum = 0;
      for (int nCol = 0; nCol < m_nCols; nCol++)
        nSum += MathHelper.abs (aRow[nCol]);
      ret = Math.max (ret, nSum);
    }
    return ret;
  }

  /**
   * Frobenius norm
   *
   * @return sqrt of sum of squares of all elements.
   */
  public double normF ()
  {
    double f = 0;
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aRow = m_aData[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        f = MathHelper.hypot (f, aRow[nCol]);
    }
    return f;
  }

  /**
   * Unary minus
   *
   * @return -A
   */
  @Nonnull
  @ReturnsMutableCopy
  public MatrixInt uminus ()
  {
    final MatrixInt aNewMatrix = new MatrixInt (m_nRows, m_nCols);
    final int [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aSrcRow = m_aData[nRow];
      final int [] aDstRow = aNewArray[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        aDstRow[nCol] = -aSrcRow[nCol];
    }
    return aNewMatrix;
  }

  /**
   * Check if size(A) == size(B)
   *
   * @param aMatrix
   *        MAtrix to check
   */
  private void _checkMatrixDimensions (@Nonnull final MatrixInt aMatrix)
  {
    if (aMatrix.m_nRows != m_nRows)
      throw new IllegalArgumentException ("Matrix row dimensions must agree.");
    if (aMatrix.m_nCols != m_nCols)
      throw new IllegalArgumentException ("Matrix column dimensions must agree.");
  }

  /**
   * C = A + B
   *
   * @param aMatrix
   *        another matrix
   * @return A + B
   */
  @Nonnull
  @ReturnsMutableCopy
  public MatrixInt plus (@Nonnull final MatrixInt aMatrix)
  {
    _checkMatrixDimensions (aMatrix);
    final MatrixInt aNewMatrix = new MatrixInt (m_nRows, m_nCols);
    final int [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aSrcRow1 = m_aData[nRow];
      final int [] aSrcRow2 = aMatrix.m_aData[nRow];
      final int [] aDstRow = aNewArray[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        aDstRow[nCol] = aSrcRow1[nCol] + aSrcRow2[nCol];
    }
    return aNewMatrix;
  }

  /**
   * A = A + B
   *
   * @param aMatrix
   *        another matrix
   * @return this
   */
  @Nonnull
  public MatrixInt plusEquals (@Nonnull final MatrixInt aMatrix)
  {
    _checkMatrixDimensions (aMatrix);
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aSrcRow = aMatrix.m_aData[nRow];
      final int [] aDstRow = m_aData[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        aDstRow[nCol] += aSrcRow[nCol];
    }
    return this;
  }

  /**
   * C = A - B
   *
   * @param aMatrix
   *        another matrix
   * @return A - B
   */
  @Nonnull
  @ReturnsMutableCopy
  public MatrixInt minus (@Nonnull final MatrixInt aMatrix)
  {
    _checkMatrixDimensions (aMatrix);
    final MatrixInt aNewMatrix = new MatrixInt (m_nRows, m_nCols);
    final int [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aSrcRow1 = m_aData[nRow];
      final int [] aSrcRow2 = aMatrix.m_aData[nRow];
      final int [] aDstRow = aNewArray[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        aDstRow[nCol] = aSrcRow1[nCol] - aSrcRow2[nCol];
    }
    return aNewMatrix;
  }

  /**
   * A = A - B
   *
   * @param aMatrix
   *        another matrix
   * @return this
   */
  @Nonnull
  public MatrixInt minusEquals (@Nonnull final MatrixInt aMatrix)
  {
    _checkMatrixDimensions (aMatrix);
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aSrcRow = aMatrix.m_aData[nRow];
      final int [] aDstRow = m_aData[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        aDstRow[nCol] -= aSrcRow[nCol];
    }
    return this;
  }

  /**
   * Element-by-element multiplication, C = A.*B
   *
   * @param aMatrix
   *        another matrix
   * @return A.*B
   */
  @Nonnull
  @ReturnsMutableCopy
  public MatrixInt arrayTimes (@Nonnull final MatrixInt aMatrix)
  {
    _checkMatrixDimensions (aMatrix);
    final MatrixInt aNewMatrix = new MatrixInt (m_nRows, m_nCols);
    final int [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aSrcRow1 = m_aData[nRow];
      final int [] aSrcRow2 = aMatrix.m_aData[nRow];
      final int [] aDstRow = aNewArray[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        aDstRow[nCol] = aSrcRow1[nCol] * aSrcRow2[nCol];
    }
    return aNewMatrix;
  }

  /**
   * Element-by-element multiplication in place, A = A.*B
   *
   * @param aMatrix
   *        another matrix
   * @return this
   */
  @Nonnull
  public MatrixInt arrayTimesEquals (@Nonnull final MatrixInt aMatrix)
  {
    _checkMatrixDimensions (aMatrix);
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aSrcRow = aMatrix.m_aData[nRow];
      final int [] aDstRow = m_aData[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        aDstRow[nCol] *= aSrcRow[nCol];
    }
    return this;
  }

  /**
   * Element-by-element right division, C = A./B
   *
   * @param aMatrix
   *        another matrix
   * @return A./B
   */
  @Nonnull
  @ReturnsMutableCopy
  public MatrixInt arrayRightDivide (@Nonnull final MatrixInt aMatrix)
  {
    _checkMatrixDimensions (aMatrix);
    final MatrixInt aNewMatrix = new MatrixInt (m_nRows, m_nCols);
    final int [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aSrcRow1 = m_aData[nRow];
      final int [] aSrcRow2 = aMatrix.m_aData[nRow];
      final int [] aDstRow = aNewArray[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        aDstRow[nCol] = aSrcRow1[nCol] / aSrcRow2[nCol];
    }
    return aNewMatrix;
  }

  /**
   * Element-by-element right division in place, A = A./B
   *
   * @param aMatrix
   *        another matrix
   * @return this
   */
  @Nonnull
  public MatrixInt arrayRightDivideEquals (@Nonnull final MatrixInt aMatrix)
  {
    _checkMatrixDimensions (aMatrix);
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aSrcRow = aMatrix.m_aData[nRow];
      final int [] aDstRow = m_aData[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        aDstRow[nCol] /= aSrcRow[nCol];
    }
    return this;
  }

  /**
   * Element-by-element left division, C = B.\A
   *
   * @param aMatrix
   *        another matrix
   * @return B.\A
   */
  @Nonnull
  @ReturnsMutableCopy
  public MatrixInt arrayLeftDivide (@Nonnull final MatrixInt aMatrix)
  {
    _checkMatrixDimensions (aMatrix);
    final MatrixInt aNewMatrix = new MatrixInt (m_nRows, m_nCols);
    final int [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aSrcRow1 = aMatrix.m_aData[nRow];
      final int [] aSrcRow2 = m_aData[nRow];
      final int [] aDstRow = aNewArray[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        aDstRow[nCol] = aSrcRow1[nCol] / aSrcRow2[nCol];
    }
    return aNewMatrix;
  }

  /**
   * Element-by-element left division in place, A = B.\A
   *
   * @param aMatrix
   *        another matrix
   * @return this
   */
  @Nonnull
  public MatrixInt arrayLeftDivideEquals (@Nonnull final MatrixInt aMatrix)
  {
    _checkMatrixDimensions (aMatrix);
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aSrcRow1 = aMatrix.m_aData[nRow];
      final int [] aSrcRow2 = m_aData[nRow];
      final int [] aDstRow = aSrcRow2;
      for (int nCol = 0; nCol < m_nCols; nCol++)
        aDstRow[nCol] = aSrcRow1[nCol] / aSrcRow2[nCol];
    }
    return this;
  }

  /**
   * Multiply a matrix by a scalar, C = s*A
   *
   * @param s
   *        scalar
   * @return s*A
   */
  @Nonnull
  @ReturnsMutableCopy
  public MatrixInt times (final int s)
  {
    final MatrixInt aNewMatrix = new MatrixInt (m_nRows, m_nCols);
    final int [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aSrcRow = m_aData[nRow];
      final int [] aDstRow = aNewArray[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        aDstRow[nCol] = s * aSrcRow[nCol];
    }
    return aNewMatrix;
  }

  /**
   * Multiply a matrix by a scalar in place, A = s*A
   *
   * @param s
   *        scalar
   * @return this
   */
  @Nonnull
  public MatrixInt timesEquals (final double s)
  {
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      final int [] aDstRow = m_aData[nRow];
      for (int nCol = 0; nCol < m_nCols; nCol++)
        aDstRow[nCol] *= s;
    }
    return this;
  }

  /**
   * Linear algebraic matrix multiplication, A * B
   *
   * @param aMatrix
   *        another matrix
   * @return Matrix product, A * B
   * @exception IllegalArgumentException
   *            Matrix inner dimensions must agree.
   */
  @Nonnull
  @ReturnsMutableCopy
  public MatrixInt times (@Nonnull final MatrixInt aMatrix)
  {
    if (aMatrix.m_nRows != m_nCols)
      throw new IllegalArgumentException ("Matrix inner dimensions must agree.");

    final MatrixInt aNewMatrix = new MatrixInt (m_nRows, aMatrix.m_nCols);
    final int [] [] aNewArray = aNewMatrix.internalGetArray ();
    final int [] aRhsCol = new int [m_nCols];
    for (int nCol = 0; nCol < aMatrix.m_nCols; nCol++)
    {
      for (int k = 0; k < m_nCols; k++)
        aRhsCol[k] = aMatrix.m_aData[k][nCol];

      for (int nRow = 0; nRow < m_nRows; nRow++)
      {
        final int [] aRow = m_aData[nRow];

        // Build and assign sum
        int nValue = 0;
        for (int k = 0; k < m_nCols; k++)
          nValue += aRow[k] * aRhsCol[k];
        aNewArray[nRow][nCol] = nValue;
      }
    }
    return aNewMatrix;
  }

  /**
   * Matrix trace.
   *
   * @return sum of the diagonal elements.
   */
  public int trace ()
  {
    int t = 0;
    final int nMin = Math.min (m_nRows, m_nCols);
    for (int nIndex = 0; nIndex < nMin; nIndex++)
      t += m_aData[nIndex][nIndex];
    return t;
  }

  /**
   * Generate matrix with random elements
   *
   * @param nRows
   *        Number of rows.
   * @param nCols
   *        Number of columns.
   * @return An nRows-by-nCols matrix with uniformly distributed random
   *         elements.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static MatrixInt random (@Nonnegative final int nRows, @Nonnegative final int nCols)
  {
    final MatrixInt aNewMatrix = new MatrixInt (nRows, nCols);
    final int [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = 0; nRow < nRows; nRow++)
    {
      final int [] aDstRow = aNewArray[nRow];
      for (int nCol = 0; nCol < nCols; nCol++)
        aDstRow[nCol] = 1 + RandomHelper.getRandom ().nextInt (100);
    }
    return aNewMatrix;
  }

  /**
   * Generate identity matrix
   *
   * @param nRows
   *        Number of rows.
   * @param nCols
   *        Number of columns.
   * @return An nRows-by-nCols matrix with ones on the diagonal and zeros
   *         elsewhere.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static MatrixInt identity (@Nonnegative final int nRows, @Nonnegative final int nCols)
  {
    final MatrixInt aNewMatrix = new MatrixInt (nRows, nCols);
    final int [] [] aNewArray = aNewMatrix.internalGetArray ();
    for (int nRow = 0; nRow < nRows; nRow++)
    {
      final int [] aRow = aNewArray[nRow];
      for (int nCol = 0; nCol < nCols; nCol++)
        aRow[nCol] = (nRow == nCol ? 1 : 0);
    }
    return aNewMatrix;
  }

  /**
   * Print the matrix to stdout. Line the elements up in columns with a
   * Fortran-like 'Fw.d' style format.
   *
   * @param aPS
   *        PrintStream to write to. May not be <code>null</code>.
   * @param nWidth
   *        Column width.
   * @param nFractionDigits
   *        Number of digits after the decimal.
   */
  @Deprecated
  public void print (@Nonnull final PrintStream aPS,
                     @Nonnegative final int nWidth,
                     @Nonnegative final int nFractionDigits)
  {
    print (new PrintWriter (new OutputStreamWriter (aPS, SystemHelper.getSystemCharset ()), true),
           nWidth,
           nFractionDigits);
  }

  /**
   * Print the matrix to the output stream. Line the elements up in columns with
   * a Fortran-like 'Fw.d' style format.
   *
   * @param aPW
   *        Output stream.
   * @param nWidth
   *        Column width.
   * @param nFractionDigits
   *        Number of digits after the decimal.
   */
  public void print (@Nonnull final PrintWriter aPW,
                     @Nonnegative final int nWidth,
                     @Nonnegative final int nFractionDigits)
  {
    final NumberFormat format = NumberFormat.getInstance (CGlobal.DEFAULT_LOCALE);
    format.setMinimumIntegerDigits (1);
    format.setMaximumFractionDigits (nFractionDigits);
    format.setMinimumFractionDigits (nFractionDigits);
    format.setGroupingUsed (false);
    print (aPW, format, nWidth + 2);
  }

  /**
   * Print the matrix to stdout. Line the elements up in columns. Use the format
   * object, and right justify within columns of width characters. Note that is
   * the matrix is to be read back in, you probably will want to use a
   * NumberFormat that is set to US Locale.
   *
   * @param aPS
   *        PrintStream to write to. May not be <code>null</code>.
   * @param aFormat
   *        A Formatting object for individual elements.
   * @param nWidth
   *        Field width for each column.
   * @see java.text.DecimalFormat#setDecimalFormatSymbols
   */
  @Deprecated
  public void print (@Nonnull final PrintStream aPS, @Nonnull final NumberFormat aFormat, @Nonnegative final int nWidth)
  {
    print (new PrintWriter (new OutputStreamWriter (aPS, SystemHelper.getSystemCharset ()), true), aFormat, nWidth);
  }

  // DecimalFormat is a little disappointing coming from Fortran or C's printf.
  // Since it doesn't pad on the left, the elements will come out different
  // widths. Consequently, we'll pass the desired column width in as an
  // argument and do the extra padding ourselves.

  /**
   * Print the matrix to the output stream. Line the elements up in columns. Use
   * the format object, and right justify within columns of width characters.
   * Note that is the matrix is to be read back in, you probably will want to
   * use a NumberFormat that is set to US Locale.
   *
   * @param aPW
   *        the output stream.
   * @param aFormat
   *        A formatting object to format the matrix elements
   * @param nWidth
   *        Column width.
   * @see java.text.DecimalFormat#setDecimalFormatSymbols
   */
  public void print (@Nonnull final PrintWriter aPW, @Nonnull final NumberFormat aFormat, @Nonnegative final int nWidth)
  {
    aPW.println (); // start on new line.
    for (int nRow = 0; nRow < m_nRows; nRow++)
    {
      for (int nCol = 0; nCol < m_nCols; nCol++)
      {
        // format the number
        final String s = aFormat.format (m_aData[nRow][nCol]);
        // At _least_ 1 space
        final int padding = Math.max (1, nWidth - s.length ());
        aPW.print (StringHelper.getRepeated (' ', padding));
        aPW.print (s);
      }
      aPW.println ();
    }
    // end with blank line.
    aPW.println ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MatrixInt rhs = (MatrixInt) o;
    if (m_nRows != rhs.m_nRows || m_nCols != rhs.m_nCols)
      return false;
    for (int nRow = 0; nRow < m_nRows; ++nRow)
      if (!Arrays.equals (m_aData[nRow], rhs.m_aData[nRow]))
        return false;
    return true;
  }

  @Override
  public int hashCode ()
  {
    final HashCodeGenerator aHC = new HashCodeGenerator (this).append (m_nRows).append (m_nCols);
    for (int nRow = 0; nRow < m_nRows; ++nRow)
      aHC.append (m_aData[nRow]);
    return aHC.getHashCode ();
  }

  /**
   * Read a matrix from a stream. The format is the same the print method, so
   * printed matrices can be read back in (provided they were printed using US
   * Locale). Elements are separated by whitespace, all the elements for each
   * row appear on a single line, the last row is followed by a blank line.
   *
   * @param aReader
   *        the input stream.
   * @return The read matrix
   * @throws IOException
   *         in case of an I/O error
   */
  @Nonnull
  public static MatrixInt read (@Nonnull @WillNotClose final Reader aReader) throws IOException
  {
    final StreamTokenizer aTokenizer = new StreamTokenizer (StreamHelper.getBuffered (aReader));

    // Although StreamTokenizer will parse numbers, it doesn't recognize
    // scientific notation (E or D); however, Double.valueOf does.
    // The strategy here is to disable StreamTokenizer's number parsing.
    // We'll only get whitespace delimited words, EOL's and EOF's.
    // These words should all be numbers, for Double.valueOf to parse.

    aTokenizer.resetSyntax ();
    aTokenizer.wordChars (0, 255);
    aTokenizer.whitespaceChars (0, ' ');
    aTokenizer.eolIsSignificant (true);

    // Ignore initial empty lines
    while (aTokenizer.nextToken () == StreamTokenizer.TT_EOL)
    {
      // Skip all new lines
    }
    if (aTokenizer.ttype == StreamTokenizer.TT_EOF)
      throw new IOException ("Unexpected EOF on matrix read.");

    // Read & store 1st row.
    final ICommonsList <Integer> vD = new CommonsArrayList<> ();
    do
    {
      vD.add (Integer.valueOf (Integer.parseInt (aTokenizer.sval)));
    } while (aTokenizer.nextToken () == StreamTokenizer.TT_WORD);

    // Now we've got the number of columns!
    final int nCols = vD.size ();
    int [] aRow = new int [nCols];
    for (int nCol = 0; nCol < nCols; nCol++)
    {
      // extract the elements of the 1st row.
      aRow[nCol] = vD.get (nCol).intValue ();
    }

    final ICommonsList <int []> v = new CommonsArrayList<> ();
    // Start storing rows instead of columns.
    v.add (aRow);
    while (aTokenizer.nextToken () == StreamTokenizer.TT_WORD)
    {
      // While non-empty lines
      aRow = new int [nCols];
      v.add (aRow);
      int nCol = 0;
      do
      {
        if (nCol >= nCols)
          throw new IOException ("Row " + v.size () + " is too long.");
        aRow[nCol++] = Integer.parseInt (aTokenizer.sval);
      } while (aTokenizer.nextToken () == StreamTokenizer.TT_WORD);
      if (nCol < nCols)
        throw new IOException ("Row " + v.size () + " is too short.");
    }
    // Now we've got the number of rows.
    final int nRows = v.size ();
    // copy the rows out of the vector
    return new MatrixInt (v.toArray (new int [nRows] []));
  }
}
