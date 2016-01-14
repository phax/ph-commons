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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.aggregate.AggregatorStringWithSeparatorIgnoreNull;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.file.filter.IFileFilter;
import com.helger.commons.tree.withid.folder.DefaultFolderTree;
import com.helger.commons.tree.withid.folder.DefaultFolderTreeItem;

/**
 * Represents a folder tree with the file system contents. This structure is
 * eagerly filled!
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class FileSystemFolderTree extends DefaultFolderTree <String, File, List <File>>
{
  private static void _iterate (@Nonnull final DefaultFolderTreeItem <String, File, List <File>> aTreeItem,
                                @Nonnull final File aDir,
                                @Nullable final IFileFilter aDirFilter,
                                @Nullable final IFileFilter aFileFilter)
  {
    if (aDir != null)
      for (final File aChild : FileHelper.getDirectoryContent (aDir))
      {
        if (aChild.isFile ())
        {
          // file
          // Check against the optional filter
          if (aFileFilter == null || aFileFilter.test (aChild))
            aTreeItem.getData ().add (aChild);
        }
        else
          if (aChild.isDirectory () && !FilenameHelper.isSystemInternalDirectory (aChild))
          {
            // directory
            // Check against the optional filter
            if (aDirFilter == null || aDirFilter.test (aChild))
            {
              // create item and recursively descend
              final DefaultFolderTreeItem <String, File, List <File>> aChildItem = aTreeItem.createChildItem (aChild.getName (),
                                                                                                              new ArrayList <File> ());
              _iterate (aChildItem, aChild, aDirFilter, aFileFilter);
            }
          }
      }
  }

  public FileSystemFolderTree (@Nonnull final String sStartDir)
  {
    this (new File (sStartDir));
  }

  public FileSystemFolderTree (@Nonnull final File aStartDir)
  {
    this (aStartDir, (IFileFilter) null, (IFileFilter) null);
  }

  public FileSystemFolderTree (@Nonnull final String sStartDir,
                               @Nullable final IFileFilter aDirFilter,
                               @Nullable final IFileFilter aFileFilter)
  {
    this (new File (sStartDir), aDirFilter, aFileFilter);
  }

  public FileSystemFolderTree (@Nonnull final File aStartDir,
                               @Nullable final IFileFilter aDirFilter,
                               @Nullable final IFileFilter aFileFilter)
  {
    super (new AggregatorStringWithSeparatorIgnoreNull ("/"));
    ValueEnforcer.notNull (aStartDir, "StartDirectory");
    ValueEnforcer.isTrue (aStartDir.isDirectory (), "Start directory is not a directory!");

    final DefaultFolderTreeItem <String, File, List <File>> aStart = getRootItem ().createChildItem (aStartDir.getName (),
                                                                                                     new ArrayList <File> ());
    _iterate (aStart, aStartDir, aDirFilter, aFileFilter);
  }
}
