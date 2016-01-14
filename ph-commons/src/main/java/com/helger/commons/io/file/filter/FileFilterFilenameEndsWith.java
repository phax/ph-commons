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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.string.ToStringGenerator;

/**
 * A filename filter that checks whether a file has the specified extension. The
 * implementation is done via {@link String#endsWith(String)} so it is case
 * sensitive.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class FileFilterFilenameEndsWith extends AbstractFileFilter
{
  private final String m_sSuffix;

  /**
   * @param sSuffix
   *        The suffix to use. May neither be <code>null</code> nor empty.
   */
  public FileFilterFilenameEndsWith (@Nonnull @Nonempty final String sSuffix)
  {
    m_sSuffix = ValueEnforcer.notEmpty (sSuffix, "Suffix");
  }

  @Nonnull
  @Nonempty
  public String getSuffix ()
  {
    return m_sSuffix;
  }

  @Override
  public boolean directTest (@Nullable final File aFile)
  {
    if (aFile != null)
    {
      final String sSecureFilename = FilenameHelper.getSecureFilename (aFile.getName ());
      if (sSecureFilename != null)
        return sSecureFilename.endsWith (m_sSuffix);
    }
    return false;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final FileFilterFilenameEndsWith rhs = (FileFilterFilenameEndsWith) o;
    return m_sSuffix.equals (rhs.m_sSuffix);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_sSuffix).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("suffix", m_sSuffix).toString ();
  }
}
