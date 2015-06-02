/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
import java.io.FilenameFilter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.io.file.FileHelper;

/**
 * A special file filter that uses and external filename filter to determine the
 * validity. This filter only works for files.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class FileFilterFileFromFilenameFilter extends AbstractFileFilter
{
  public FileFilterFileFromFilenameFilter (@Nonnull final FilenameFilter aFilenameFilter)
  {
    super (new FileFilterFromFilenameFilter (aFilenameFilter));
  }

  @Override
  public boolean matchesThisFilter (@Nullable final File aFile)
  {
    return aFile != null && FileHelper.existsFile (aFile);
  }
}
