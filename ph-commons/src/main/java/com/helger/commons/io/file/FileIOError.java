/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.io.file;

import java.io.File;
import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.ISuccessIndicator;
import com.helger.commons.string.ToStringGenerator;

/**
 * Represents an error with an additional error object.
 *
 * @author Philip Helger
 */
@Immutable
public final class FileIOError implements ISuccessIndicator, Serializable
{
  private final EFileIOOperation m_eOperation;
  private final EFileIOErrorCode m_eCode;
  private final File m_aFile1;
  private final File m_aFile2;
  private final Exception m_aException;

  public FileIOError (@Nonnull final EFileIOOperation eOperation, @Nonnull final EFileIOErrorCode eCode)
  {
    m_eOperation = ValueEnforcer.notNull (eOperation, "Operation");
    m_eCode = ValueEnforcer.notNull (eCode, "ErrorCode");
    m_aFile1 = null;
    m_aFile2 = null;
    m_aException = null;
  }

  public FileIOError (@Nonnull final EFileIOOperation eOperation,
                      @Nonnull final EFileIOErrorCode eCode,
                      @Nonnull final File aFile1)
  {
    m_eOperation = ValueEnforcer.notNull (eOperation, "Operation");
    m_eCode = ValueEnforcer.notNull (eCode, "ErrorCode");
    m_aFile1 = ValueEnforcer.notNull (aFile1, "File1");
    m_aFile2 = null;
    m_aException = null;
  }

  public FileIOError (@Nonnull final EFileIOOperation eOperation,
                      @Nonnull final EFileIOErrorCode eCode,
                      @Nonnull final File aFile1,
                      @Nonnull final File aFile2)
  {
    m_eOperation = ValueEnforcer.notNull (eOperation, "Operation");
    m_eCode = ValueEnforcer.notNull (eCode, "ErrorCode");
    m_aFile1 = ValueEnforcer.notNull (aFile1, "File1");
    m_aFile2 = ValueEnforcer.notNull (aFile2, "File2");
    m_aException = null;
  }

  public FileIOError (@Nonnull final EFileIOOperation eOperation,
                      @Nonnull final EFileIOErrorCode eCode,
                      @Nonnull final Exception aException)
  {
    m_eOperation = ValueEnforcer.notNull (eOperation, "Operation");
    m_eCode = ValueEnforcer.notNull (eCode, "ErrorCode");
    m_aFile1 = null;
    m_aFile2 = null;
    m_aException = ValueEnforcer.notNull (aException, "Exception");
  }

  /**
   * @return The operation passed in the constructor. Never <code>null</code>.
   */
  @Nonnull
  public EFileIOOperation getOperation ()
  {
    return m_eOperation;
  }

  /**
   * @return The error code passed in the constructor. Never <code>null</code>.
   */
  @Nonnull
  public EFileIOErrorCode getErrorCode ()
  {
    return m_eCode;
  }

  public boolean isSuccess ()
  {
    return m_eCode.isSuccess ();
  }

  /**
   * @return The first file relevant to this error. May be <code>null</code>.
   */
  @Nullable
  public File getFile1 ()
  {
    return m_aFile1;
  }

  /**
   * @return <code>true</code> if the first file is present, <code>false</code>
   *         if not.
   */
  public boolean hasFile1 ()
  {
    return m_aFile1 != null;
  }

  /**
   * @return The second file relevant to this error. May be <code>null</code>.
   *         This field can only be present, if {@link #getFile1()} is present.
   */
  @Nullable
  public File getFile2 ()
  {
    return m_aFile2;
  }

  /**
   * @return <code>true</code> if the second file is present, <code>false</code>
   *         if not.
   */
  public boolean hasFile2 ()
  {
    return m_aFile2 != null;
  }

  /**
   * @return The exception passed in the constructor. May be <code>null</code>.
   */
  @Nullable
  public Exception getException ()
  {
    return m_aException;
  }

  /**
   * @return <code>true</code> if an exception is present, <code>false</code> if
   *         not.
   */
  public boolean hasException ()
  {
    return m_aException != null;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final FileIOError rhs = (FileIOError) o;
    return m_eOperation == rhs.m_eOperation &&
           m_eCode == rhs.m_eCode &&
           EqualsHelper.equals (m_aFile1, rhs.m_aFile1) &&
           EqualsHelper.equals (m_aFile2, rhs.m_aFile2) &&
           EqualsHelper.equals (m_aException, rhs.m_aException);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_eOperation)
                                       .append (m_eCode)
                                       .append (m_aFile1)
                                       .append (m_aFile2)
                                       .append (m_aException)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("operation", m_eOperation)
                                       .append ("ecode", m_eCode)
                                       .appendIfNotNull ("file1", m_aFile1)
                                       .appendIfNotNull ("file2", m_aFile2)
                                       .appendIfNotNull ("exception", m_aException)
                                       .toString ();
  }
}
