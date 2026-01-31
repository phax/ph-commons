/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.xml.microdom;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.base.clone.ICloneable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Default implementation of the {@link IMicroDataAware} interface.
 *
 * @author Philip Helger
 */
final class MicroDataAware implements IMicroDataAware, ICloneable <MicroDataAware>
{
  private final StringBuilder m_aSB;

  public MicroDataAware (final char @NonNull [] aChars, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    ValueEnforcer.isArrayOfsLen (aChars, nOfs, nLen);
    m_aSB = new StringBuilder (nLen + 16).append (aChars, nOfs, nLen);
  }

  public MicroDataAware (@Nullable final CharSequence aText)
  {
    if (StringHelper.isEmpty (aText))
      m_aSB = new StringBuilder ();
    else
      m_aSB = new StringBuilder (aText);
  }

  @NonNull
  public StringBuilder getData ()
  {
    return m_aSB;
  }

  public void setData (@Nullable final CharSequence aData)
  {
    m_aSB.setLength (0);
    m_aSB.append (aData);
  }

  public void appendData (@Nullable final CharSequence sData)
  {
    m_aSB.append (sData);
  }

  public void appendData (final char @NonNull [] aChars, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    m_aSB.append (aChars, nOfs, nLen);
  }

  public void appendData (final char cChar)
  {
    m_aSB.append (cChar);
  }

  public void prependData (@Nullable final CharSequence sData)
  {
    m_aSB.insert (0, sData);
  }

  public void prependData (final char @NonNull [] aChars, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    m_aSB.insert (0, aChars, nOfs, nLen);
  }

  public void prependData (final char cChar)
  {
    m_aSB.insert (0, cChar);
  }

  @NonNull
  public MicroDataAware getClone ()
  {
    return new MicroDataAware (m_aSB);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MicroDataAware rhs = (MicroDataAware) o;
    return m_aSB.toString ().equals (rhs.m_aSB.toString ());
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aSB.toString ()).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Data", m_aSB).getToString ();
  }
}
