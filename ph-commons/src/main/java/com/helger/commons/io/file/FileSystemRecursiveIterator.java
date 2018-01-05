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
package com.helger.commons.io.file;

import java.io.File;
import java.util.NoSuchElementException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.functional.IPredicate;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * Recursively iterate the content of a file system directory. This is a depth
 * first iteration, because as soon as a directory is encountered, the children
 * of this directory are iterated.<br>
 * Note: the order of iteration is undefined and depends on the order returned
 * by {@link FileHelper#getDirectoryContent(File)}.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class FileSystemRecursiveIterator implements IIterableIterator <File>
{
  private final int m_nStartLevel;
  private int m_nLevel = 0;
  private final IPredicate <File> m_aRecursionFilter;
  private final ICommonsList <File> m_aFilesLeft;

  @Nonnegative
  private static int _getLevel (@Nonnull final File aFile)
  {
    return StringHelper.getCharCount (aFile.getAbsolutePath (), File.separatorChar);
  }

  /**
   * Constructor for recursively iterating a file system directory.
   *
   * @param sBaseDir
   *        The base directory to start with. May not be <code>null</code>.
   */
  public FileSystemRecursiveIterator (@Nonnull final String sBaseDir)
  {
    this (new File (sBaseDir), (IPredicate <File>) null);
  }

  /**
   * Constructor for recursively iterating a file system directory.
   *
   * @param aBaseDir
   *        The base directory to start with. May not be <code>null</code>.
   */
  public FileSystemRecursiveIterator (@Nonnull final File aBaseDir)
  {
    this (aBaseDir, (IPredicate <File>) null);
  }

  /**
   * Constructor for recursively iterating a file system directory.
   *
   * @param sBaseDir
   *        The base directory to start with. May not be <code>null</code>.
   * @param aRecursionFilter
   *        An optional filter that controls, into which sub-directories this
   *        iterator should descend to. May be <code>null</code>.
   */
  public FileSystemRecursiveIterator (@Nonnull final String sBaseDir,
                                      @Nullable final IPredicate <File> aRecursionFilter)
  {
    this (new File (sBaseDir), aRecursionFilter);
  }

  /**
   * Constructor for recursively iterating a file system directory.
   *
   * @param aBaseDir
   *        The base directory to start with. May not be <code>null</code>.
   * @param aRecursionFilter
   *        An optional filter that controls, into which sub-directories this
   *        iterator should descend to. May be <code>null</code>.
   */
  public FileSystemRecursiveIterator (@Nonnull final File aBaseDir, @Nullable final IPredicate <File> aRecursionFilter)
  {
    ValueEnforcer.notNull (aBaseDir, "BaseDirectory");
    m_nStartLevel = _getLevel (aBaseDir);
    m_aRecursionFilter = aRecursionFilter;
    m_aFilesLeft = FileHelper.getDirectoryContent (aBaseDir);
  }

  @Nonnegative
  public int getStartLevel ()
  {
    return m_nStartLevel;
  }

  @Nullable
  public IPredicate <File> getRecursionFilter ()
  {
    return m_aRecursionFilter;
  }

  public final boolean hasNext ()
  {
    return m_aFilesLeft.isNotEmpty ();
  }

  /**
   * Override this method to manually filter the directories, which are recursed
   * into.
   *
   * @param aDirectory
   *        The non-<code>null</code> directory
   * @return <code>true</code> if all children of this directory should be
   *         investigated
   */
  @OverrideOnDemand
  protected boolean recurseIntoDirectory (@Nonnull final File aDirectory)
  {
    return m_aRecursionFilter == null || m_aRecursionFilter.test (aDirectory);
  }

  @Nonnull
  public final File next ()
  {
    if (!hasNext ())
      throw new NoSuchElementException ();

    // Get and remove the first element
    final File aFile = m_aFilesLeft.removeFirst ();

    m_nLevel = _getLevel (aFile) - m_nStartLevel;
    if (aFile.isDirectory ())
      if (recurseIntoDirectory (aFile))
      {
        // insert all children of the current directory at the beginning of the
        // list
        m_aFilesLeft.addAll (0, FileHelper.getDirectoryContent (aFile));
      }
    return aFile;
  }

  /**
   * @return The nesting level of the last file retrieved by {@link #next()}.
   *         Always &ge; 0. The starting directory has level 0.
   */
  @Nonnegative
  public final int getLevel ()
  {
    return m_nLevel;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("FilesLeft", m_aFilesLeft).getToString ();
  }
}
