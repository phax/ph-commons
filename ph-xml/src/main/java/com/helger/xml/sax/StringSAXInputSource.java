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
package com.helger.xml.sax;

import java.io.Reader;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.xml.sax.InputSource;

import com.helger.annotation.Nonnegative;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.base.tostring.ToStringGenerator;

/**
 * Special {@link InputSource} implementation that reads from a predefined String.
 *
 * @author Philip Helger
 */
public class StringSAXInputSource extends InputSource
{
  private final String m_sText;

  public StringSAXInputSource (final char @NonNull [] aInput)
  {
    this (new String (aInput));
  }

  public StringSAXInputSource (final char @NonNull [] aInput, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    this (new String (aInput, nOfs, nLen));
  }

  public StringSAXInputSource (@NonNull final CharSequence aInput)
  {
    this (aInput instanceof final String sInput ? sInput : aInput.toString ());
  }

  public StringSAXInputSource (@NonNull final String sText)
  {
    this (sText, null);
  }

  public StringSAXInputSource (@NonNull final String sText, @Nullable final String sSystemID)
  {
    m_sText = ValueEnforcer.notNull (sText, "Text");
    setSystemId (sSystemID);
  }

  @NonNull
  public final String getText ()
  {
    return m_sText;
  }

  @Override
  public Reader getCharacterStream ()
  {
    return new NonBlockingStringReader (m_sText);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("text", m_sText).append ("systemID", getSystemId ()).getToString ();
  }
}
