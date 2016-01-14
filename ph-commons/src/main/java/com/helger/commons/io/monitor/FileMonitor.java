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
package com.helger.commons.io.monitor;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * A polling file monitor implementation. Use
 * {@link FileMonitorManager#createFileMonitor(IFileMonitorCallback)} to use
 * this class effectively. All files that have the same callback (
 * {@link IFileMonitorCallback}) can be encapsulated in the same
 * {@link FileMonitor}.
 *
 * @author Philip Helger
 */
public class FileMonitor
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (FileMonitor.class);

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();

  /**
   * A listener object that is notified on file creation and deletion.
   */
  private final IFileMonitorCallback m_aListener;

  /**
   * Map from filename to File being monitored.
   */
  @GuardedBy ("m_aRWLock")
  private final Map <String, FileMonitorAgent> m_aMonitorMap = new HashMap <> ();

  /**
   * File objects to be removed from the monitor map.
   */
  private final Stack <File> m_aDeleteStack = new Stack <> ();

  /**
   * File objects to be added to the monitor map.
   */
  private final Stack <File> m_aAddStack = new Stack <> ();

  /**
   * A flag used to determine if adding files to be monitored should be
   * recursive.
   */
  private boolean m_bRecursive;

  public FileMonitor (@Nonnull final IFileMonitorCallback aListener)
  {
    m_aListener = ValueEnforcer.notNull (aListener, "Listener");
  }

  /**
   * @return The listener as passed in the constructor. Never <code>null</code>.
   */
  @Nonnull
  public IFileMonitorCallback getListener ()
  {
    return m_aListener;
  }

  /**
   * Access method to get the recursive setting when adding files for
   * monitoring.
   *
   * @return <code>true</code> if monitoring is enabled for children.
   */
  public boolean isRecursive ()
  {
    return m_bRecursive;
  }

  /**
   * Access method to set the recursive setting when adding files for
   * monitoring.
   *
   * @param bRecursive
   *        <code>true</code> if monitoring should be enabled for children,
   *        <code>false</code> otherwise.
   * @return this
   */
  @Nonnull
  public FileMonitor setRecursive (final boolean bRecursive)
  {
    m_bRecursive = bRecursive;
    return this;
  }

  /**
   * Adds a file to be monitored.
   *
   * @param aFile
   *        The File to add.
   * @param bRecursive
   *        Scan recursively?
   * @return {@link EChange}
   */
  @Nonnull
  private EChange _recursiveAddFile (@Nonnull final File aFile, final boolean bRecursive)
  {
    final String sKey = aFile.getAbsolutePath ();

    // Check if already contained
    if (m_aRWLock.readLocked ( () -> m_aMonitorMap.containsKey (sKey)))
      return EChange.UNCHANGED;

    final EChange eChange = m_aRWLock.writeLocked ( () -> {
      // Try again in write lock
      if (m_aMonitorMap.containsKey (sKey))
        return EChange.UNCHANGED;

      // Add monitored item
      m_aMonitorMap.put (sKey, new FileMonitorAgent (this, aFile));
      return EChange.CHANGED;
    });

    if (eChange.isChanged () && bRecursive)
    {
      // Traverse the children depth first
      final File [] aChildren = aFile.listFiles ();
      if (aChildren != null)
        for (final File aChild : aChildren)
          _recursiveAddFile (aChild, true);
    }

    return eChange;
  }

  @Nonnegative
  int getMonitoredFileCount ()
  {
    return m_aRWLock.readLocked ( () -> m_aMonitorMap.size ());
  }

  /**
   * Adds a file to be monitored.
   *
   * @param aFile
   *        The File to monitor.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange addMonitoredFile (@Nonnull final File aFile)
  {
    // Not recursive, because the direct children are added anyway
    if (_recursiveAddFile (aFile, false).isUnchanged ())
      return EChange.UNCHANGED;

    // Traverse the direct children anyway
    final File [] aChildren = aFile.listFiles ();
    if (aChildren != null)
      for (final File aChild : aChildren)
        _recursiveAddFile (aChild, m_bRecursive);

    s_aLogger.info ("Added " +
                    (m_bRecursive ? "recursive " : "") +
                    "monitoring for file changes in " +
                    aFile.getAbsolutePath () +
                    " - monitoring " +
                    getMonitoredFileCount () +
                    " files and directories in total");
    return EChange.CHANGED;
  }

  /**
   * Removes a file from being monitored.
   *
   * @param aFile
   *        The File to remove from monitoring.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange removeMonitoredFile (@Nonnull final File aFile)
  {
    ValueEnforcer.notNull (aFile, "File");

    final String sKey = aFile.getAbsolutePath ();

    if (m_aRWLock.writeLocked ( () -> m_aMonitorMap.remove (sKey) == null))
    {
      // File not monitored
      return EChange.UNCHANGED;
    }

    final File aParent = aFile.getParentFile ();
    if (aParent != null)
    {
      // Not the root
      final String sParentKey = aParent.getAbsolutePath ();
      final FileMonitorAgent aParentAgent = m_aRWLock.readLocked ( () -> m_aMonitorMap.get (sParentKey));
      if (aParentAgent != null)
        aParentAgent.resetChildrenList ();
    }

    s_aLogger.info ("Removed " +
                    (m_bRecursive ? "recursive " : "") +
                    "monitoring for file changes in " +
                    aFile.getAbsolutePath () +
                    " - monitoring " +
                    getMonitoredFileCount () +
                    " files and directories in total");
    return EChange.CHANGED;
  }

  /**
   * Called upon file creation by {@link FileMonitorAgent}.
   *
   * @param aFile
   *        The File to add. Never <code>null</code>.
   */
  void onFileCreated (@Nonnull final File aFile)
  {
    try
    {
      m_aListener.onFileCreated (new FileChangeEvent (aFile));
    }
    catch (final Throwable t)
    {
      s_aLogger.error ("Failed to invoke onFileCreated listener " + m_aListener + " on file " + aFile, t);
    }

    m_aAddStack.push (aFile);
  }

  /**
   * Called upon file deletion by {@link FileMonitorAgent}.
   *
   * @param aFile
   *        The File to be removed from being monitored. Never <code>null</code>
   *        .
   */
  void onFileDeleted (@Nonnull final File aFile)
  {
    try
    {
      m_aListener.onFileDeleted (new FileChangeEvent (aFile));
    }
    catch (final Throwable t)
    {
      s_aLogger.error ("Failed to invoke onFileDeleted listener " + m_aListener + " for file " + aFile, t);
    }

    m_aDeleteStack.push (aFile);
  }

  /**
   * Called on modification by {@link FileMonitorAgent}.
   *
   * @param aFile
   *        The File that was modified. Never <code>null</code>.
   */
  void onFileChanged (@Nonnull final File aFile)
  {
    try
    {
      m_aListener.onFileChanged (new FileChangeEvent (aFile));
    }
    catch (final Throwable t)
    {
      s_aLogger.error ("Failed to invoke onFileChanged listener " + m_aListener + " for file " + aFile, t);
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  Collection <FileMonitorAgent> getAllAgents ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newList (m_aMonitorMap.values ()));
  }

  void applyPendingDeletes ()
  {
    // Remove listener for all deleted files
    while (!m_aDeleteStack.isEmpty ())
      removeMonitoredFile (m_aDeleteStack.pop ());
  }

  void applyPendingAdds ()
  {
    // Add listener for all added files
    while (!m_aAddStack.isEmpty ())
      addMonitoredFile (m_aAddStack.pop ());
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("listener", m_aListener).append ("recursive", m_bRecursive).toString ();
  }
}
