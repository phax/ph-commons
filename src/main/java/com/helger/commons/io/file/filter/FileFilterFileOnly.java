/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.io.file.FileUtils;
import com.helger.commons.string.ToStringGenerator;

/**
 * A file filter that accepts only files.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class FileFilterFileOnly extends AbstractFileFilter
{
  private static final FileFilterFileOnly s_aInstance = new FileFilterFileOnly ();

  private FileFilterFileOnly ()
  {}

  @Nonnull
  public static FileFilterFileOnly getInstance ()
  {
    return s_aInstance;
  }

  public boolean matchesFilter (@Nullable final File aFile)
  {
    return aFile != null && FileUtils.existsFile (aFile);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    return true;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).toString ();
  }
}
