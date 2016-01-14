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

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.io.file.FilenameHelper;

/**
 * A file filter that matches if the direct parent directory is public, meaning
 * it does not start with "." (hidden directory on Unix systems)
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class FileFilterParentDirectoryPublic extends AbstractFileFilter
{
  @Override
  public boolean directTest (@Nullable final File aFile)
  {
    final File aParentFile = aFile != null ? aFile.getAbsoluteFile ().getParentFile () : null;
    return aParentFile != null && !FilenameHelper.isHiddenFilename (aParentFile);
  }
}
