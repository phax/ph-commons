/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.annotation.ELockType;
import com.helger.commons.annotation.MustBeLocked;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.callback.CallbackList;
import com.helger.commons.collection.NonBlockingStack;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.debug.GlobalDebug;
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

  private static final CallbackList <IDAOReadExceptionCallback> s_aExceptionHandlersRead = new CallbackList <> ();
  private static final CallbackList <IDAOWriteExceptionCallback> s_aExceptionHandlersWrite = new CallbackList <> ();

  protected static final SimpleReadWriteLock s_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("s_aRWLock")
  private static boolean s_bSilentMode = false;

  protected final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  @GuardedBy ("m_aRWLock")
  private final NonBlockingStack <Boolean> m_aAutoSaveStack = new NonBlockingStack <> ();
  @GuardedBy ("m_aRWLock")
  private boolean m_bPendingChanges = false;
  @GuardedBy ("m_aRWLock")
  private boolean m_bAutoSaveEnabled = DEFAULT_AUTO_SAVE_ENABLED;

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
    return s_aRWLock.writeLocked ( () -> {
      final boolean bOld = s_bSilentMode;
      s_bSilentMode = bSilentMode;
      return bOld;
    });
  }

  /**
   * @return <code>true</code> if logging is disabled, <code>false</code> if it is
   *         enabled.
   * @since 9.2.0
   */
  public static boolean isSilentMode ()
  {
    return s_aRWLock.readLocked ( () -> s_bSilentMode);
  }

  protected AbstractDAO ()
  {}

  protected static final boolean isDebugLogging ()
  {
    return GlobalDebug.isDebugMode () && !isSilentMode ();
  }

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
    return m_aRWLock.readLocked ( () -> m_bAutoSaveEnabled);
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
    return m_aRWLock.readLocked ( () -> m_bPendingChanges);
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
    final boolean bPreviouslyAutoSaveEnabled = m_aRWLock.writeLocked ( () -> {
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
}
