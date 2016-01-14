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
package com.helger.commons.io.file.filter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

import javax.annotation.Nullable;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.filter.IFilter;

/**
 * Abstract interface that collects {@link FileFilter}, {@link FilenameFilter}
 * and {@link IFilter}.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
@FunctionalInterface
public interface IFileFilter extends FileFilter, FilenameFilter, IFilter <File>
{
  default boolean accept (@Nullable final File aFile)
  {
    return test (aFile);
  }

  default boolean accept (@Nullable final File aDir, @Nullable final String sName)
  {
    if (sName == null)
      return false;

    final File aFileToCheck = aDir != null ? new File (aDir, sName) : new File (sName);
    return test (aFileToCheck);
  }
}
