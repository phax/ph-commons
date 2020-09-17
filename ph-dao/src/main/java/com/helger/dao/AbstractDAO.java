/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.dao;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ELockType;
import com.helger.commons.annotation.MustBeLocked;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.callback.CallbackList;
import com.helger.commons.collection.NonBlockingStack;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.io.file.FileIOError;
import com.helger.commons.io.file.FileOperationManager;
import com.helger.commons.string.ToStringGenerator;

/**
 * Base implementation of {@link IDAO}
 *
 * @author Philip Helger
 */
@ThreadSafe
public abstract class AbstractDAO implements IDAO
{
  /** By default auto-save is enabled */
  public static final boolean DEFAULT_AUTO_SAVE_ENABLED = true;
  /**
   * The default extension for an old file that was not yet deleted. Mainly used
   * in the WAL DAO
   */
  public static final String FILENAME_EXTENSION_PREV = ".prev";
  /**
   * The default extension for a file that is newly created. Mainly used in the
   * WAL DAO
   */
  public static final String FILENAME_EXTENSION_NEW = ".new";

  private static final CallbackList <IDAOReadExceptionCallback> s_aExceptionHandlersRead = new CallbackList <> ();
  private static final CallbackList <IDAOWriteExceptionCallback> s_aExceptionHandlersWrite = new CallbackList <> ();

  protected static final SimpleReadWriteLock s_aRWLock = new SimpleReadWriteLock ();
  private static final AtomicBoolean SILENT_MODE = new AtomicBoolean (GlobalDebug.DEFAULT_SILENT_MODE);

  protected final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  @GuardedBy ("m_aRWLock")
  private final NonBlockingStack <Boolean> m_aAutoSaveStack = new NonBlockingStack <> ();
  @GuardedBy ("m_aRWLock")
  private boolean m_bPendingChanges = false;
  @GuardedBy ("m_aRWLock")
  private boolean m_bAutoSaveEnabled = DEFAULT_AUTO_SAVE_ENABLED;

  /**
   * @return <code>true</code> if logging is disabled, <code>false</code> if it
   *         is enabled.
   * @since 9.2.0
   */
  public static boolean isSilentMode ()
  {
    return SILENT_MODE.get ();
  }

  /**
   * Enable or disable certain regular log messages.
   *
   * @param bSilentMode
   *        <code>true</code> to disable logging, <code>false</code> to enable
   *        logging
   * @return The previous value of the silent mode.
   * @since 9.2.0
   */
  public static boolean setSilentMode (final boolean bSilentMode)
  {
    return SILENT_MODE.getAndSet (bSilentMode);
  }

  protected AbstractDAO ()
  {}

  /**
   * @return The static mutable exception handler list that is invoked for read
   *         exceptions. Never <code>null</code>. The list applies to ALL DAOs
   *         since it is static.
   */
  @Nonnull
  @ReturnsMutableObject
  public static final CallbackList <IDAOReadExceptionCallback> exceptionHandlersRead ()
  {
    return s_aExceptionHandlersRead;
  }

  /**
   * @return The static mutable exception handler list that is invoked for write
   *         exceptions. Never <code>null</code>. The list applies to ALL DAOs
   *         since it is static.
   */
  @Nonnull
  @ReturnsMutableObject
  public static final CallbackList <IDAOWriteExceptionCallback> exceptionHandlersWrite ()
  {
    return s_aExceptionHandlersWrite;
  }

  /**
   * @return <code>true</code> if auto save is enabled, <code>false</code>
   *         otherwise.
   */
  @MustBeLocked (ELockType.READ)
  protected final boolean internalIsAutoSaveEnabled ()
  {
    return m_bAutoSaveEnabled;
  }

  /**
   * @return <code>true</code> if auto save is enabled, <code>false</code>
   *         otherwise.
   */
  public final boolean isAutoSaveEnabled ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_bAutoSaveEnabled);
  }

  @MustBeLocked (ELockType.WRITE)
  public final void internalSetPendingChanges (final boolean bPendingChanges)
  {
    m_bPendingChanges = bPendingChanges;
  }

  /**
   * @return <code>true</code> if unsaved changes are present
   */
  @MustBeLocked (ELockType.READ)
  public final boolean internalHasPendingChanges ()
  {
    return m_bPendingChanges;
  }

  /**
   * @return <code>true</code> if unsaved changes are present
   */
  public final boolean hasPendingChanges ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_bPendingChanges);
  }

  public final void beginWithoutAutoSave ()
  {
    m_aRWLock.writeLocked ( () -> {
      // Save old auto save state
      m_aAutoSaveStack.push (Boolean.valueOf (m_bAutoSaveEnabled));
      m_bAutoSaveEnabled = false;
    });
  }

  public final void endWithoutAutoSave ()
  {
    // Restore previous auto save state
    final boolean bPreviouslyAutoSaveEnabled = m_aRWLock.writeLockedBoolean ( () -> {
      final boolean bPreviously = m_aAutoSaveStack.pop ().booleanValue ();
      m_bAutoSaveEnabled = bPreviously;
      return bPreviously;
    });

    if (bPreviouslyAutoSaveEnabled)
    {
      // And in case something was changed - writeLocked itself
      writeToFileOnPendingChanges ();
    }
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("autoSaveStack", m_aAutoSaveStack)
                                       .append ("pendingChanges", m_bPendingChanges)
                                       .append ("autoSaveEnabled", m_bAutoSaveEnabled)
                                       .getToString ();
  }

  /**
   * Check the access to the passed file using the specified mode. If the access
   * is not provided, a {@link DAOException} is thrown. If everything is good,
   * this method returns without a comment.
   *
   * @param aFile
   *        The file to check. May not be <code>null</code>.
   * @param eMode
   *        The access mode. May not be <code>null</code>.
   * @throws DAOException
   *         If the requested access mode cannot be provided.
   */
  protected static void checkFileAccess (@Nonnull final File aFile, @Nonnull final EMode eMode) throws DAOException
  {
    ValueEnforcer.notNull (aFile, "File");
    ValueEnforcer.notNull (eMode, "Mode");

    final String sFilename = aFile.toString ();
    if (aFile.exists ())
    {
      // file exist -> must be a file!
      if (!aFile.isFile ())
        throw new DAOException ("The passed filename '" +
                                sFilename +
                                "' is not a file - maybe a directory or a symlink? Path is '" +
                                aFile.getAbsolutePath () +
                                "'");

      switch (eMode)
      {
        case READ:
          // Check for read-rights
          if (!aFile.canRead ())
            throw new DAOException ("Read access rights from '" + aFile.getAbsolutePath () + "' are missing.");
          break;
        case WRITE:
          // Check for write-rights
          if (!aFile.canWrite ())
            throw new DAOException ("Write access rights to '" + aFile.getAbsolutePath () + "' are missing");
          break;
      }
    }
    else
    {
      // Ensure the parent directory is present
      final File aParentDir = aFile.getParentFile ();
      if (aParentDir != null)
      {
        final FileIOError aError = FileOperationManager.INSTANCE.createDirRecursiveIfNotExisting (aParentDir);
        if (aError.isFailure ())
          throw new DAOException ("Failed to create parent directory '" +
                                  aParentDir +
                                  "' of '" +
                                  aFile.getAbsolutePath () +
                                  "': " +
                                  aError);
      }
    }
  }
}
