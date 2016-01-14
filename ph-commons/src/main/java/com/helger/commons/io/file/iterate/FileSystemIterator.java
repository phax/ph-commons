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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.collection.iterate.FilterIterator;
import com.helger.commons.collection.iterate.IIterableIterator;
import com.helger.commons.collection.iterate.IterableIterator;
import com.helger.commons.filter.FilterListAll;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.filter.IFileFilter;

/**
 * Iterate over the content of a single directory. Iteration is <b>not</b>
 * recursive.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class FileSystemIterator extends IterableIterator <File>
{
  /**
   * Constructor.
   *
   * @param sBaseDir
   *        The base directory to iterate. May not be <code>null</code>.
   */
  public FileSystemIterator (@Nonnull final String sBaseDir)
  {
    this (new File (sBaseDir));
  }

  /**
   * Constructor.
   *
   * @param aBaseDir
   *        The base directory to iterate. May not be <code>null</code>.
   */
  public FileSystemIterator (@Nonnull final File aBaseDir)
  {
    super (FileHelper.getDirectoryContent (aBaseDir));
  }

  /**
   * Create a new non-recursive file system iterator that uses a certain
   * {@link FilenameFilter}.
   *
   * @param sBaseDir
   *        The directory to iterate. May not be <code>null</code>.
   * @param aFileFilter
   *        The filter to use. May not be <code>null</code>.
   * @return The matching iterator.
   */
  @Nonnull
  public static IIterableIterator <File> create (@Nonnull final String sBaseDir, @Nonnull final IFileFilter aFileFilter)
  {
    return new FilterIterator <File> (new FileSystemIterator (sBaseDir), aFileFilter);
  }

  /**
   * Create a new non-recursive file system iterator that uses a certain
   * {@link FilenameFilter}.
   *
   * @param fBaseDir
   *        The directory to iterate. May not be <code>null</code>.
   * @param aFileFilter
   *        The filter to use. May not be <code>null</code>.
   * @return The matching iterator.
   */
  @Nonnull
  public static IIterableIterator <File> create (@Nonnull final File fBaseDir, @Nonnull final IFileFilter aFileFilter)
  {
    return new FilterIterator <File> (new FileSystemIterator (fBaseDir), aFileFilter);
  }

  /**
   * Create a new non-recursive file system iterator that uses multiple
   * {@link FilenameFilter} objects that all need to match.
   *
   * @param sBaseDir
   *        The directory to iterate. May not be <code>null</code>.
   * @param aFileFilters
   *        The filters to use. May not be <code>null</code>.
   * @return The matching iterator.
   */
  @Nonnull
  public static IIterableIterator <File> create (@Nonnull final String sBaseDir,
                                                 @Nonnull final IFileFilter... aFileFilters)
  {
    return new FilterIterator <File> (new FileSystemIterator (sBaseDir), new FilterListAll <File> (aFileFilters));
  }

  /**
   * Create a new non-recursive file system iterator that uses multiple
   * {@link FilenameFilter} objects that all need to match.
   *
   * @param fBaseDir
   *        The directory to iterate. May not be <code>null</code>.
   * @param aFileFilters
   *        The filters to use. May not be <code>null</code>.
   * @return The matching iterator.
   */
  @Nonnull
  public static IIterableIterator <File> create (@Nonnull final File fBaseDir,
                                                 @Nonnull final IFileFilter... aFileFilters)
  {
    return new FilterIterator <File> (new FileSystemIterator (fBaseDir), new FilterListAll <File> (aFileFilters));
  }
}
