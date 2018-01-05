/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.json.serialize;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.system.ENewLineMode;

/**
 * Default implementation of {@link IJsonWriterSettings}.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class JsonWriterSettings implements IJsonWriterSettings
{
  public static final boolean DEFAULT_INDENT_ENABLED = false;
  public static final String DEFAULT_INDENT_STRING = "  ";
  public static final String DEFAULT_NEWLINE_STRING = ENewLineMode.DEFAULT.getText ();
  public static final boolean DEFAULT_WRITE_NEWLINE_AT_END = false;
  public static final boolean DEFAULT_QUOTE_NAMES = true;

  // Must be after all default values!!
  public static final IJsonWriterSettings DEFAULT_SETTINGS = new JsonWriterSettings ();

  private boolean m_bIndentEnabled = DEFAULT_INDENT_ENABLED;
  private String m_sIndentString = DEFAULT_INDENT_STRING;
  private String m_sNewlineString = DEFAULT_NEWLINE_STRING;
  private boolean m_bWriteNewlineAtEnd = DEFAULT_WRITE_NEWLINE_AT_END;
  private boolean m_bQuoteNames = DEFAULT_QUOTE_NAMES;

  public JsonWriterSettings ()
  {}

  public JsonWriterSettings (@Nonnull final IJsonWriterSettings aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");
    setIndentEnabled (aOther.isIdentEnabled ());
    setIndentString (aOther.getIndentString ());
    setNewlineString (aOther.getNewlineString ());
    setWriteNewlineAtEnd (aOther.isWriteNewlineAtEnd ());
    setQuoteNames (aOther.isQuoteNames ());
  }

  public boolean isIdentEnabled ()
  {
    return m_bIndentEnabled;
  }

  @Nonnull
  public final JsonWriterSettings setIndentEnabled (final boolean bIndentEnabled)
  {
    m_bIndentEnabled = bIndentEnabled;
    return this;
  }

  @Nonnull
  @Nonempty
  public String getIndentString ()
  {
    return m_sIndentString;
  }

  @Nonnull
  public final JsonWriterSettings setIndentString (@Nonnull @Nonempty final String sIndentString)
  {
    m_sIndentString = ValueEnforcer.notEmpty (sIndentString, "IndentString");
    return this;
  }

  @Nonnull
  @Nonempty
  public String getNewlineString ()
  {
    return m_sNewlineString;
  }

  @Nonnull
  public final JsonWriterSettings setNewlineString (@Nonnull @Nonempty final String sNewlineString)
  {
    m_sNewlineString = ValueEnforcer.notEmpty (sNewlineString, "NewlineString");
    return this;
  }

  public boolean isWriteNewlineAtEnd ()
  {
    return m_bWriteNewlineAtEnd;
  }

  @Nonnull
  public final JsonWriterSettings setWriteNewlineAtEnd (final boolean bWriteNewlineAtEnd)
  {
    m_bWriteNewlineAtEnd = bWriteNewlineAtEnd;
    return this;
  }

  public boolean isQuoteNames ()
  {
    return m_bQuoteNames;
  }

  @Nonnull
  public final JsonWriterSettings setQuoteNames (final boolean bQuoteNames)
  {
    m_bQuoteNames = bQuoteNames;
    return this;
  }

  @Nonnull
  public JsonWriterSettings getClone ()
  {
    return new JsonWriterSettings (this);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("IndentEnabled", m_bIndentEnabled)
                                       .append ("IndentString",
                                                StringHelper.getHexEncoded (m_sIndentString,
                                                                            StandardCharsets.ISO_8859_1))
                                       .append ("NewlineString",
                                                StringHelper.getHexEncoded (m_sNewlineString,
                                                                            StandardCharsets.ISO_8859_1))
                                       .append ("WriteNewlineAtEnd", m_bWriteNewlineAtEnd)
                                       .append ("QuoteNames", m_bQuoteNames)
                                       .getToString ();
  }
}
