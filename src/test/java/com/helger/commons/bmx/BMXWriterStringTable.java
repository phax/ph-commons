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
package com.helger.commons.bmx;

import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.string.ToStringGenerator;

final class BMXWriterStringTable
{
  private final DataOutput m_aDO;
  private final Map <String, Integer> m_aStrings;
  private int m_nLastUsedIndex = CBMXIO.INDEX_NULL_STRING;
  private final boolean m_bReUseStrings;

  public BMXWriterStringTable (@Nonnull final DataOutput aDO, final boolean bReUseStrings)
  {
    m_aDO = aDO;
    m_bReUseStrings = bReUseStrings;
    m_aStrings = bReUseStrings ? new HashMap <String, Integer> (1000) : null;
  }

  private void _onNewString (@Nonnull final String sString) throws IOException
  {
    m_aDO.writeByte (CBMXIO.NODETYPE_STRING);
    m_aDO.writeInt (sString.length ());
    m_aDO.writeChars (sString);
  }

  public int addString (@Nullable final CharSequence aString) throws IOException
  {
    if (aString == null)
      return CBMXIO.INDEX_NULL_STRING;

    return addString (aString.toString ());
  }

  @Nonnegative
  public int addString (@Nullable final String sString) throws IOException
  {
    if (sString == null)
      return CBMXIO.INDEX_NULL_STRING;

    if (m_bReUseStrings)
    {
      final Integer aIndex = m_aStrings.get (sString);
      if (aIndex != null)
        return aIndex.intValue ();

      // Real increment
      final int nIndex = ++m_nLastUsedIndex;
      m_aStrings.put (sString, Integer.valueOf (nIndex));
      _onNewString (sString);
      return nIndex;
    }

    // Write String where it occurs!
    _onNewString (sString);
    return ++m_nLastUsedIndex;
  }

  @Nonnegative
  public int getStringCount ()
  {
    return m_aStrings.size ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("strings", m_aStrings).toString ();
  }
}
