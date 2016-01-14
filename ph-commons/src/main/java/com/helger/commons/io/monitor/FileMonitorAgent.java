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
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.NonBlockingStack;
import com.helger.commons.string.ToStringGenerator;

/**
 * File monitor agent.
 *
 * @author commons-vfs
 * @author Philip Helger
 */
@NotThreadSafe
final class FileMonitorAgent
{
  private final FileMonitor m_aMonitor;
  private final File m_aFile;

  private boolean m_bExists;
  private long m_nTimestamp;
  private final Set <String> m_aChildren = new HashSet <String> ();

  /**
   * @param aMonitor
   *        Owning file monitor. May not be <code>null</code>.
   * @param aFile
   *        The file to be monitored. May not be <code>null</code>.
   */
  FileMonitorAgent (@Nonnull final FileMonitor aMonitor, @Nonnull final File aFile)
  {
    m_aMonitor = ValueEnforcer.notNull (aMonitor, "Monitor");
    m_aFile = ValueEnforcer.notNull (aFile, "File");
    m_bExists = aFile.exists ();
    if (m_bExists)
    {
      m_nTimestamp = aFile.lastModified ();
      resetChildrenList ();
    }
    else
    {
      m_nTimestamp = CGlobal.ILLEGAL_ULONG;
    }
  }

  void resetChildrenList ()
  {
    m_aChildren.clear ();
    final File [] aChildren = m_aFile.listFiles ();
    if (aChildren != null)
      for (final File aChild : aChildren)
        m_aChildren.add (aChild.getAbsolutePath ());
  }

  /**
   * Recursively fires create events for all children if recursive descent is
   * enabled. Otherwise the create event is only fired for the initial File.
   *
   * @param aCreatedFile
   *        The child to add.
   */
  private void _onFileCreateRecursive (@Nonnull final File aCreatedFile)
  {
    // Add
    m_aMonitor.onFileCreated (aCreatedFile);

    if (m_aMonitor.isRecursive ())
    {
      // Scan recursively
      final File [] aChildren = aCreatedFile.listFiles ();
      if (aChildren != null)
        for (final File aChild : aChildren)
          _onFileCreateRecursive (aChild);
    }
  }

  /**
   * Only checks for new children. If children are removed, they'll eventually
   * be checked.
   */
  private void _checkForNewChildren ()
  {
    // See which new children are not listed in the current children
    // map.
    final Set <String> aNewChildren = new HashSet <String> ();
    final NonBlockingStack <File> aNewCreatedChildren = new NonBlockingStack <File> ();

    final File [] aNewChildrenList = m_aFile.listFiles ();
    if (aNewChildrenList != null)
      for (final File aNewChild : aNewChildrenList)
      {
        final String sKey = aNewChild.getAbsolutePath ();
        aNewChildren.add (sKey);
        if (!m_aChildren.contains (sKey))
          aNewCreatedChildren.push (aNewChild);
      }

    m_aChildren.clear ();
    m_aChildren.addAll (aNewChildren);

    // If there were missing children
    while (!aNewCreatedChildren.isEmpty ())
      _onFileCreateRecursive (aNewCreatedChildren.pop ());
  }

  void checkForModifications ()
  {
    final boolean bExistsNow = m_aFile.exists ();
    if (m_bExists)
    {
      // File previously existed
      if (bExistsNow)
      {
        // File previously existed and still exists
        // Check the timestamp to see if it has been modified
        final long nNewTimestamp = m_aFile.lastModified ();
        if (m_nTimestamp != nNewTimestamp)
        {
          m_nTimestamp = nNewTimestamp;
          m_aMonitor.onFileChanged (m_aFile);
        }
      }
      else
      {
        // The file was deleted
        m_bExists = bExistsNow;
        m_nTimestamp = CGlobal.ILLEGAL_ULONG;

        // Mark it as deleted
        m_aMonitor.onFileDeleted (m_aFile);
      }
    }
    else
    {
      // File previously did not exist
      if (bExistsNow)
      {
        // File was created
        m_bExists = bExistsNow;
        m_nTimestamp = m_aFile.lastModified ();
        m_aMonitor.onFileCreated (m_aFile);
      }
    }

    _checkForNewChildren ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("file", m_aFile)
                                       .append ("exists", m_bExists)
                                       .append ("timestamp", m_nTimestamp)
                                       .append ("children", m_aChildren)
                                       .toString ();
  }
}
