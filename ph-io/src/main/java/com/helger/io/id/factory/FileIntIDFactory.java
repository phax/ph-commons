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
package com.helger.io.id.factory;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.ELockType;
import com.helger.annotation.concurrent.MustBeLocked;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.id.factory.AbstractPersistingIntIDFactory;
import com.helger.base.id.factory.IIntIDFactory;
import com.helger.base.string.StringParser;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.io.file.EFileIOErrorCode;
import com.helger.io.file.EFileIOOperation;
import com.helger.io.file.FileHelper;
import com.helger.io.file.FileIOError;
import com.helger.io.file.FileOperationManager;
import com.helger.io.file.SimpleFileIO;

import jakarta.annotation.Nonnull;

/**
 * {@link File} based persisting {@link IIntIDFactory} implementation.
 *
 * @author Philip Helger
 */
@ThreadSafe
public class FileIntIDFactory extends AbstractPersistingIntIDFactory
{
  /** The charset to use for writing the file */
  public static final Charset CHARSET_TO_USE = StandardCharsets.ISO_8859_1;

  /** The default number of values to reserve with a single IO action */
  public static final int DEFAULT_RESERVE_COUNT = 20;

  private final File m_aFile;
  private final File m_aPrevFile;
  private final File m_aNewFile;

  public FileIntIDFactory (@Nonnull final File aFile)
  {
    this (aFile, DEFAULT_RESERVE_COUNT);
  }

  public FileIntIDFactory (@Nonnull final File aFile, @Nonnegative final int nReserveCount)
  {
    super (nReserveCount);
    ValueEnforcer.notNull (aFile, "File");

    m_aFile = aFile;
    m_aPrevFile = new File (aFile.getParentFile (), aFile.getName () + ".prev");
    m_aNewFile = new File (aFile.getParentFile (), aFile.getName () + ".new");

    if (!FileHelper.canReadAndWriteFile (m_aFile))
      throw new IllegalArgumentException ("Cannot read and/or write the file " + m_aFile + "!");
    if (!FileHelper.canReadAndWriteFile (m_aPrevFile))
      throw new IllegalArgumentException ("Cannot read and/or write the file " + m_aPrevFile + "!");
    if (!FileHelper.canReadAndWriteFile (m_aNewFile))
      throw new IllegalArgumentException ("Cannot read and/or write the file " + m_aNewFile + "!");

    if (m_aNewFile.exists ())
      throw new IllegalStateException ("The temporary ID file '" +
                                       m_aNewFile.getAbsolutePath () +
                                       "' already exists! Please use the file with the highest number. Please resolve this conflict manually.");
    if (m_aPrevFile.exists ())
      throw new IllegalStateException ("The temporary ID file '" +
                                       m_aPrevFile.getAbsolutePath () +
                                       "' already exists! If the ID file '" +
                                       m_aFile.getAbsolutePath () +
                                       "' exists and contains a higher number, you may consider deleting this file. Please resolve this conflict manually.");
  }

  /**
   * @return The {@link File} to write to, as provided in the constructor. Never
   *         <code>null</code>.
   */
  @Nonnull
  public final File getFile ()
  {
    return m_aFile;
  }

  /*
   * Note: this method must only be called from within a locked section!
   */
  @Override
  @MustBeLocked (ELockType.WRITE)
  protected final int readAndUpdateIDCounter (@Nonnegative final int nReserveCount)
  {
    // Read the old content
    final String sContent = SimpleFileIO.getFileAsString (m_aFile, CHARSET_TO_USE);
    final int nRead = sContent != null ? StringParser.parseInt (sContent.trim (), 0) : 0;

    // Write the new content to the new file
    // This will fail, if the disk is full
    SimpleFileIO.writeFile (m_aNewFile, Integer.toString (nRead + nReserveCount), CHARSET_TO_USE);

    FileIOError aIOError;
    boolean bRenamedToPrev = false;
    if (m_aFile.exists ())
    {
      // Rename the existing file to the prev file
      aIOError = FileOperationManager.INSTANCE.renameFile (m_aFile, m_aPrevFile);
      bRenamedToPrev = true;
    }
    else
      aIOError = new FileIOError (EFileIOOperation.RENAME_FILE, EFileIOErrorCode.NO_ERROR);
    if (aIOError.isSuccess ())
    {
      // Rename the new file to the destination file
      aIOError = FileOperationManager.INSTANCE.renameFile (m_aNewFile, m_aFile);
      if (aIOError.isSuccess ())
      {
        // Finally delete the prev file (may not be existing for fresh
        // instances)
        aIOError = FileOperationManager.INSTANCE.deleteFileIfExisting (m_aPrevFile);
      }
      else
      {
        // 2nd rename failed
        // -> Revert original rename to stay as consistent as possible
        if (bRenamedToPrev)
          FileOperationManager.INSTANCE.renameFile (m_aPrevFile, m_aFile);
      }
    }
    if (aIOError.isFailure ())
      throw new IllegalStateException ("Error on rename(existing-old)/rename(new-existing)/delete(old): " + aIOError);

    return nRead;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final FileIntIDFactory rhs = (FileIntIDFactory) o;
    return m_aFile.equals (rhs.m_aFile);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aFile).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("file", m_aFile).getToString ();
  }
}
