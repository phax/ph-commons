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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.collection.iterate.IterableIterator;

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
   * Constructor.
   *
   * @param aBaseDir
   *        The base directory to iterate. May not be <code>null</code>.
   * @param sDirName
   *        The directory name relative to the passed base directory. May not be
   *        <code>null</code>.
   */
  public FileSystemIterator (@Nonnull final File aBaseDir, @Nonnull final String sDirName)
  {
    super (FileHelper.getDirectoryContent (new File (aBaseDir, sDirName)));
  }
}
