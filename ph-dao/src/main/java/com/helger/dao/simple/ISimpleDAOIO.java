/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.dao.simple;

import java.io.File;
import java.io.Serializable;

import javax.annotation.Nonnull;

import com.helger.commons.io.file.FileIOError;
import com.helger.commons.io.file.FileOperationManager;
import com.helger.commons.io.relative.IFileRelativeIO;

/**
 * The DAO file IO API. Used in simple DAOs.
 *
 * @author Philip Helger
 */
public interface ISimpleDAOIO extends Serializable
{
  /**
   * @return The "relative" file IO abstraction. May not be <code>null</code>.
   */
  @Nonnull
  IFileRelativeIO getFileRelativeIO ();

  /**
   * Create a new directory if it does not exist. The direct parent directory
   * already needs to exist.
   *
   * @param aDir
   *        The directory to be created if it does not exist. May not be
   *        <code>null</code>.
   * @return A non-<code>null</code> error code.
   */
  @Nonnull
  default FileIOError createDirRecursiveIfNotExisting (@Nonnull final File aDir)
  {
    return FileOperationManager.INSTANCE.createDirRecursiveIfNotExisting (aDir);
  }
}
