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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.id.IHasIntID;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.state.ISuccessIndicator;

/**
 * Represents a set of predefined error codes that can occur in file operations.
 *
 * @author Philip Helger
 */
public enum EFileIOErrorCode implements ISuccessIndicator,IHasIntID
{
  /** Generic error code: no error. */
  NO_ERROR (0),
  /** Generic error code: operation failed but we don't know why. */
  OPERATION_FAILED (1),
  /** Generic error code: a SecurityException occurred. */
  SECURITY_ERROR (2),
  /** A source file or directory does not exist. */
  SOURCE_DOES_NOT_EXIST (3),
  /** A destination file or directory already exists. */
  TARGET_ALREADY_EXISTS (4),
  /** Source and destination object are identical. */
  SOURCE_EQUALS_TARGET (5),
  /** Avoid recursive dependencies. */
  TARGET_IS_CHILD_OF_SOURCE (6),
  /** We stumble across an object that is neither a file nor a directory. */
  OBJECT_CANNOT_BE_HANDLED (7),
  /** An object's parent could not be retrieved. */
  OBJECT_HAS_NO_PARENT (8),
  /** The source object cannot be read. */
  SOURCE_NOT_READABLE (9),
  /** The source parent object is read-only (not writable). */
  SOURCE_PARENT_NOT_WRITABLE (10),
  /** The target parent object is read-only (not writable). */
  TARGET_PARENT_NOT_WRITABLE (11);

  private final int m_nID;

  private EFileIOErrorCode (@Nonnegative final int nID)
  {
    m_nID = nID;
  }

  /**
   * An internal ID for easier referencing. {@link #NO_ERROR} has ID 0. All
   * errors have positive numbers.
   */
  @Nonnegative
  public int getID ()
  {
    return m_nID;
  }

  /**
   * Use this error code to create a new {@link FileIOError} instance.
   *
   * @param eOperation
   *        The operation carried out. May not be <code>null</code>.
   * @param aFile
   *        The file on which operation was carried out. May not be
   *        <code>null</code>.
   * @return The new {@link FileIOError} and never <code>null</code>.
   */
  @Nonnull
  public FileIOError getAsIOError (@Nonnull final EFileIOOperation eOperation, @Nonnull final File aFile)
  {
    return new FileIOError (eOperation, this, aFile);
  }

  /**
   * Use this error code to create a new {@link FileIOError} instance.
   *
   * @param eOperation
   *        The operation carried out. May not be <code>null</code>.
   * @param aFile1
   *        The first file on which operation was carried out. May not be
   *        <code>null</code>.
   * @param aFile2
   *        The second file on which operation was carried out. May not be
   *        <code>null</code>.
   * @return The new {@link FileIOError} and never <code>null</code>.
   */
  @Nonnull
  public FileIOError getAsIOError (@Nonnull final EFileIOOperation eOperation,
                                   @Nonnull final File aFile1,
                                   @Nonnull final File aFile2)
  {
    if (eOperation.getParamCount () < 2)
      throw new IllegalStateException ("The operation " + eOperation + " expects only one parameter!");
    return new FileIOError (eOperation, this, aFile1, aFile2);
  }

  /**
   * @return <code>true</code> if this is {@link #NO_ERROR}, <code>false</code>
   *         otherwise.
   */
  public boolean isSuccess ()
  {
    return this == NO_ERROR;
  }

  /**
   * Static method to create a {@link FileIOError} for a
   * {@link SecurityException}.
   *
   * @param eOperation
   *        The performed operation. May not be <code>null</code>.
   * @param ex
   *        The occurred {@link SecurityException}. Never <code>null</code>.
   * @return The non-<code>null</code> {@link FileIOError}.
   */
  @Nonnull
  public static FileIOError getAsIOError (@Nonnull final EFileIOOperation eOperation,
                                          @Nonnull final SecurityException ex)
  {
    return new FileIOError (eOperation, EFileIOErrorCode.SECURITY_ERROR, ex);
  }

  @Nullable
  public static EFileIOErrorCode getFromIDOrNull (final int nID)
  {
    return EnumHelper.getFromIDOrNull (EFileIOErrorCode.class, nID);
  }
}
