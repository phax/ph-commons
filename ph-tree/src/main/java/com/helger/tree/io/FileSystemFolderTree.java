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
package com.helger.tree.io;

import java.io.File;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.string.StringHelper;
import com.helger.tree.withid.folder.DefaultFolderTree;
import com.helger.tree.withid.folder.DefaultFolderTreeItem;

/**
 * Represents a folder tree with the file system contents. This structure is
 * eagerly filled!
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class FileSystemFolderTree extends DefaultFolderTree <String, File, ICommonsList <File>>
{
  private static void _iterate (@Nonnull final DefaultFolderTreeItem <String, File, ICommonsList <File>> aTreeItem,
                                @Nonnull final File aDir,
                                @Nullable final Predicate <? super File> aDirFilter,
                                @Nullable final Predicate <? super File> aFileFilter)
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
              final DefaultFolderTreeItem <String, File, ICommonsList <File>> aChildItem = aTreeItem.createChildItem (aChild.getName (),
                                                                                                                      new CommonsArrayList <> ());
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
    this (aStartDir, (Predicate <? super File>) null, (Predicate <? super File>) null);
  }

  public FileSystemFolderTree (@Nonnull final String sStartDir,
                               @Nullable final Predicate <? super File> aDirFilter,
                               @Nullable final Predicate <? super File> aFileFilter)
  {
    this (new File (sStartDir), aDirFilter, aFileFilter);
  }

  public FileSystemFolderTree (@Nonnull final File aStartDir,
                               @Nullable final Predicate <? super File> aDirFilter,
                               @Nullable final Predicate <? super File> aFileFilter)
  {
    super (x -> StringHelper.getImplodedNonEmpty ('/', x));
    ValueEnforcer.notNull (aStartDir, "StartDirectory");
    ValueEnforcer.isTrue (aStartDir.isDirectory (), "Start directory is not a directory!");

    final DefaultFolderTreeItem <String, File, ICommonsList <File>> aStart = getRootItem ().createChildItem (aStartDir.getName (),
                                                                                                             new CommonsArrayList <> ());
    _iterate (aStart, aStartDir, aDirFilter, aFileFilter);
  }
}
