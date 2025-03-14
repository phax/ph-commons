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
package com.helger.xml.microdom;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IMicroCDATA} interface.
 *
 * @author Philip Helger
 */
public final class MicroCDATA extends AbstractMicroNode implements IMicroCDATA
{
  private final MicroDataAware m_aData;

  public MicroCDATA (@Nonnull final char [] aChars, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    m_aData = new MicroDataAware (aChars, nOfs, nLen);
  }

  public MicroCDATA (@Nullable final CharSequence sText)
  {
    m_aData = new MicroDataAware (sText);
  }

  private MicroCDATA (@Nonnull final MicroDataAware aData)
  {
    m_aData = aData.getClone ();
  }

  @Nonnull
  public EMicroNodeType getType ()
  {
    return EMicroNodeType.CDATA;
  }

  @Nonnull
  @Nonempty
  public String getNodeName ()
  {
    return "#cdata-section";
  }

  @Override
  @Nonnull
  public String getNodeValue ()
  {
    return getData ().toString ();
  }

  @Nonnull
  public CharSequence getData ()
  {
    return m_aData.getData ();
  }

  public void appendData (@Nullable final CharSequence sData)
  {
    m_aData.appendData (sData);
  }

  public void appendData (@Nonnull final char [] aChars, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    m_aData.appendData (aChars, nOfs, nLen);
  }

  public void appendData (final char cChar)
  {
    m_aData.appendData (cChar);
  }

  public void prependData (@Nullable final CharSequence sData)
  {
    m_aData.prependData (sData);
  }

  public void prependData (@Nonnull final char [] aChars, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    m_aData.prependData (aChars, nOfs, nLen);
  }

  public void prependData (final char cChar)
  {
    m_aData.prependData (cChar);
  }

  public void setData (@Nullable final CharSequence sData)
  {
    m_aData.setData (sData);
  }

  @Nonnull
  public IMicroCDATA getClone ()
  {
    return new MicroCDATA (m_aData);
  }

  public boolean isEqualContent (@Nullable final IMicroNode o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MicroCDATA rhs = (MicroCDATA) o;
    return m_aData.equals (rhs.m_aData);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("text", getData ()).getToString ();
  }
}
