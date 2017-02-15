/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.commons.changelog;

import java.io.Serializable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.version.Version;

/**
 * This class represents a single change log with a list of entries and
 * releases.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class ChangeLog implements Serializable
{
  private final String m_sOriginalVersion;
  private final Version m_aVersion;
  private final String m_sComponent;
  private final ICommonsList <AbstractChangeLogEntry> m_aEntries = new CommonsArrayList <> ();

  /**
   * Constructor.
   *
   * @param sVersion
   *        The change log version string.
   * @param sComponent
   *        The name of the component the changelog belongs to.
   */
  public ChangeLog (@Nonnull @Nonempty final String sVersion, @Nonnull @Nonempty final String sComponent)
  {
    m_sOriginalVersion = ValueEnforcer.notEmpty (sVersion, "Version");
    m_aVersion = Version.parse (sVersion);
    m_sComponent = ValueEnforcer.notEmpty (sComponent, "Component");
  }

  /**
   * @return The original change log version. Never <code>null</code>.
   */
  @Nonnull
  @Nonempty
  public String getOriginalVersion ()
  {
    return m_sOriginalVersion;
  }

  /**
   * @return The change log version. Never <code>null</code>.
   */
  @Nonnull
  public Version getVersion ()
  {
    return m_aVersion;
  }

  /**
   * @return The name of the component, to which this change log belongs.
   *         Neither <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  public String getComponent ()
  {
    return m_sComponent;
  }

  /**
   * Add a new change log entry at the end.
   *
   * @param aEntry
   *        The entry to be added. May not be <code>null</code>.
   */
  public void addEntry (@Nonnull final ChangeLogEntry aEntry)
  {
    ValueEnforcer.notNull (aEntry, "Entry");
    m_aEntries.add (aEntry);
  }

  /**
   * Add a new change log entry at the specified index.
   *
   * @param nIndex
   *        The index to add the change log entry. May not be &lt; 0.
   * @param aEntry
   *        The entry to be added. May not be <code>null</code>.
   */
  public void addEntry (@Nonnegative final int nIndex, @Nonnull final ChangeLogEntry aEntry)
  {
    ValueEnforcer.notNull (aEntry, "Entry");
    m_aEntries.add (nIndex, aEntry);
  }

  /**
   * @return An copy of the list with all contained change log items. Never
   *         <code>null</code>. The elements my be of type
   *         {@link ChangeLogEntry} or {@link ChangeLogRelease}.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <AbstractChangeLogEntry> getAllBaseEntries ()
  {
    return m_aEntries.getClone ();
  }

  /**
   * @return A modifiable list of all change log entries. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <ChangeLogEntry> getAllEntries ()
  {
    return m_aEntries.getAllInstanceOf (ChangeLogEntry.class);
  }

  /**
   * Get all change log entries, that match the specified category.
   *
   * @param eCategory
   *        The category to search. May not be <code>null</code>.
   * @return An empty list, if no change log entry matched the specified
   *         category. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <ChangeLogEntry> getAllEntriesOfCategory (@Nonnull final EChangeLogCategory eCategory)
  {
    ValueEnforcer.notNull (eCategory, "Category");

    final ICommonsList <ChangeLogEntry> ret = new CommonsArrayList <> ();
    m_aEntries.findAllInstanceOf (ChangeLogEntry.class, e -> {
      if (e.getCategory ().equals (eCategory))
        ret.add (e);
    });
    return ret;
  }

  /**
   * Add a new release at the end.
   *
   * @param aRelease
   *        The release to be added. May not be <code>null</code>.
   */
  public void addRelease (@Nonnull final ChangeLogRelease aRelease)
  {
    ValueEnforcer.notNull (aRelease, "Release");
    m_aEntries.add (aRelease);
  }

  /**
   * Add a new release at the specified index.
   *
   * @param nIndex
   *        The index to add the release. May not be &lt; 0.
   * @param aRelease
   *        The release to be added. May not be <code>null</code>.
   */
  public void addRelease (@Nonnegative final int nIndex, @Nonnull final ChangeLogRelease aRelease)
  {
    ValueEnforcer.notNull (aRelease, "Release");
    m_aEntries.add (nIndex, aRelease);
  }

  /**
   * @return A list of all contained releases in this change log.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <ChangeLogRelease> getAllReleases ()
  {
    return m_aEntries.getAllInstanceOf (ChangeLogRelease.class);
  }

  /**
   * @return The release with the latest date. May be <code>null</code> if no
   *         release is contained.
   */
  @Nullable
  public ChangeLogRelease getLatestRelease ()
  {
    ChangeLogRelease aLatest = null;
    for (final AbstractChangeLogEntry aEntry : m_aEntries)
      if (aEntry instanceof ChangeLogRelease)
      {
        final ChangeLogRelease aRelease = (ChangeLogRelease) aEntry;
        if (aLatest == null || aRelease.getDate ().isAfter (aLatest.getDate ()))
          aLatest = aRelease;
      }
    return aLatest;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ChangeLog rhs = (ChangeLog) o;
    return m_sOriginalVersion.equals (rhs.m_sOriginalVersion) &&
           m_aVersion.equals (rhs.m_aVersion) &&
           m_sComponent.equals (rhs.m_sComponent) &&
           m_aEntries.equals (rhs.m_aEntries);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sOriginalVersion)
                                       .append (m_aVersion)
                                       .append (m_sComponent)
                                       .append (m_aEntries)
                                       .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("originalVersion", m_sOriginalVersion)
                                       .append ("version", m_aVersion)
                                       .append ("component", m_sComponent)
                                       .append ("entries", m_aEntries)
                                       .getToString ();
  }
}
