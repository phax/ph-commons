/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.Test;

import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.hierarchy.visit.DefaultHierarchyVisitorCallback;
import com.helger.commons.hierarchy.visit.EHierarchyVisitorReturn;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.IFileFilter;
import com.helger.tree.util.TreeVisitor;
import com.helger.tree.withid.folder.DefaultFolderTreeItem;

/**
 * Test class for class {@link FileSystemFolderTree}.
 *
 * @author Philip Helger
 */
public final class FileSystemFolderTreeTest
{
  @Test
  public void testCreate ()
  {
    try
    {
      new FileSystemFolderTree ((File) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      new FileSystemFolderTree ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      new FileSystemFolderTree (new File ("gibts-ned"));
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      new FileSystemFolderTree ("gibts-ned");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    FileSystemFolderTree aTree = new FileSystemFolderTree (new File (".").getAbsoluteFile (),
                                                           null,
                                                           IFileFilter.filenameEndsWith (".java"));
    TreeVisitor.visitTreeItem (aTree.getRootItem (),
                               new DefaultHierarchyVisitorCallback <DefaultFolderTreeItem <String, File, ICommonsList <File>>> ()
                               {
                                 @Override
                                 @Nonnull
                                 public EHierarchyVisitorReturn onItemBeforeChildren (@Nullable final DefaultFolderTreeItem <String, File, ICommonsList <File>> aFolder)
                                 {
                                   if (aFolder != null)
                                     for (final File aFile : aFolder.getData ())
                                       assertTrue (FileHelper.existsFile (aFile));
                                   return EHierarchyVisitorReturn.CONTINUE;
                                 }
                               });

    // Only dir filter
    aTree = new FileSystemFolderTree (new File (".").getAbsoluteFile (), IFileFilter.filenameEndsWith ("src"), null);
    TreeVisitor.visitTreeItem (aTree.getRootItem (), new DefaultHierarchyVisitorCallback <> ());

    // No filter
    aTree = new FileSystemFolderTree (new File (".").getAbsoluteFile ());
    TreeVisitor.visitTreeItem (aTree.getRootItem (), new DefaultHierarchyVisitorCallback <> ());
  }
}
