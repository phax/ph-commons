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
package com.helger.commons.io.file.iterate;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.UnsupportedOperation;
import com.helger.commons.collection.iterate.FilterIterator;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.filter.FilterListAll;
import com.helger.commons.filter.IFilter;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.filter.IFileFilter;
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
  private final IFileFilter m_aRecursionFilter;
  private final List <File> m_aFilesLeft;

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
    this (new File (sBaseDir), (IFileFilter) null);
  }

  /**
   * Constructor for recursively iterating a file system directory.
   *
   * @param aBaseDir
   *        The base directory to start with. May not be <code>null</code>.
   */
  public FileSystemRecursiveIterator (@Nonnull final File aBaseDir)
  {
    this (aBaseDir, (IFileFilter) null);
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
  public FileSystemRecursiveIterator (@Nonnull final String sBaseDir, @Nullable final IFileFilter aRecursionFilter)
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
  public FileSystemRecursiveIterator (@Nonnull final File aBaseDir, @Nullable final IFileFilter aRecursionFilter)
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
  public IFileFilter getRecursionFilter ()
  {
    return m_aRecursionFilter;
  }

  @Nonnull
  public final Iterator <File> iterator ()
  {
    return this;
  }

  public final boolean hasNext ()
  {
    return !m_aFilesLeft.isEmpty ();
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
    final File aFile = m_aFilesLeft.remove (0);

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

  @UnsupportedOperation
  public final void remove ()
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("files", m_aFilesLeft).toString ();
  }

  /**
   * Create a new iterator that recursively descends into sub-directories
   * starting from the given base directory. Additionally a
   * {@link FilenameFilter} can be added, that determines, which results to be
   * returned and which not. The difference between the filter passed here and
   * the filter that can be specified in the constructor is the following: the
   * {@link IFilter} in the constructor defines into which sub-directories to
   * descend. The {@link FilenameFilter} passed to this method only defines
   * which elements should be returned and which not, independent of the
   * iterated files (like a "view").
   *
   * @param sBaseDir
   *        The base directory to start iterating. May not be <code>null</code>.
   * @param aFileFilter
   *        The file filter to be used. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static IIterableIterator <File> create (@Nonnull final String sBaseDir, @Nonnull final IFileFilter aFileFilter)
  {
    return create (new File (sBaseDir), aFileFilter);
  }

  /**
   * Create a new iterator that recursively descends into sub-directories
   * starting from the given base directory. Additionally a
   * {@link FilenameFilter} can be added, that determines, which results to be
   * returned and which not. The difference between the filter passed here and
   * the filter that can be specified in the constructor is the following: the
   * {@link IFilter} in the constructor defines into which sub-directories to
   * descend. The {@link FilenameFilter} passed to this method only defines
   * which elements should be returned and which not, independent of the
   * iterated files (like a "view").
   *
   * @param fBaseDir
   *        The base directory to start iterating. May not be <code>null</code>.
   * @param aFileFilter
   *        The file filter to be used. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static IIterableIterator <File> create (@Nonnull final File fBaseDir, @Nonnull final IFileFilter aFileFilter)
  {
    return new FilterIterator <File> (new FileSystemRecursiveIterator (fBaseDir), aFileFilter);
  }

  /**
   * Create a new iterator that recursively descends into sub-directories
   * starting from the given base directory. Additionally a list of
   * {@link FilenameFilter}s can be added, that determine, which results to be
   * returned and which not. The difference between the filter passed here and
   * the filter that can be specified in the constructor is the following: the
   * {@link IFilter} in the constructor defines into which sub-directories to
   * descend. The {@link FilenameFilter}s passed to this method only define
   * which elements should be returned and which not, independent of the
   * iterated files (like a "view").
   *
   * @param sBaseDir
   *        The base directory to start iterating. May not be <code>null</code>.
   * @param aFileFilters
   *        The file filter to be used. May neither be <code>null</code> nor
   *        empty.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static IIterableIterator <File> create (@Nonnull final String sBaseDir,
                                                 @Nonnull @Nonempty final IFileFilter... aFileFilters)
  {
    return create (new File (sBaseDir), aFileFilters);
  }

  /**
   * Create a new iterator that recursively descends into sub-directories
   * starting from the given base directory. Additionally a list of
   * {@link FilenameFilter}s can be added, that determine, which results to be
   * returned and which not. The difference between the filter passed here and
   * the filter that can be specified in the constructor is the following: the
   * {@link IFilter} in the constructor defines into which sub-directories to
   * descend. The {@link FilenameFilter}s passed to this method only define
   * which elements should be returned and which not, independent of the
   * iterated files (like a "view").
   *
   * @param fBaseDir
   *        The base directory to start iterating. May not be <code>null</code>.
   * @param aFileFilters
   *        The file filter to be used. May neither be <code>null</code> nor
   *        empty.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static IIterableIterator <File> create (@Nonnull final File fBaseDir,
                                                 @Nonnull @Nonempty final IFileFilter... aFileFilters)
  {
    return new FilterIterator <File> (new FileSystemRecursiveIterator (fBaseDir),
                                      new FilterListAll <File> (aFileFilters));
  }
}
