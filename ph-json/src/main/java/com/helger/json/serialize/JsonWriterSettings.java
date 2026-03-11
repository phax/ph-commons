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
package com.helger.json.serialize;

import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHex;
import com.helger.base.system.ENewLineMode;
import com.helger.base.tostring.ToStringGenerator;

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
  /** Default settings space optimized */
  public static final IJsonWriterSettings DEFAULT_SETTINGS = new JsonWriterSettings ();
  /**
   * Default settings using indentation.
   *
   * @since 9.4.7
   */
  public static final IJsonWriterSettings DEFAULT_SETTINGS_FORMATTED = new JsonWriterSettings ().setIndentEnabled (true);

  private boolean m_bIndentEnabled = DEFAULT_INDENT_ENABLED;
  private String m_sIndentString = DEFAULT_INDENT_STRING;
  private String m_sNewlineString = DEFAULT_NEWLINE_STRING;
  private boolean m_bWriteNewlineAtEnd = DEFAULT_WRITE_NEWLINE_AT_END;
  private boolean m_bQuoteNames = DEFAULT_QUOTE_NAMES;

  /**
   * Default constructor using the default settings.
   */
  public JsonWriterSettings ()
  {}

  /**
   * Copy constructor.
   *
   * @param aOther
   *        The settings to copy from. May not be <code>null</code>.
   */
  public JsonWriterSettings (@NonNull final IJsonWriterSettings aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");
    setIndentEnabled (aOther.isIdentEnabled ());
    setIndentString (aOther.getIndentString ());
    setNewlineString (aOther.getNewlineString ());
    setWriteNewlineAtEnd (aOther.isWriteNewlineAtEnd ());
    setQuoteNames (aOther.isQuoteNames ());
  }

  /**
   * @return <code>true</code> if indentation is enabled, <code>false</code> if not. Default is
   *         {@link #DEFAULT_INDENT_ENABLED}.
   */
  public boolean isIdentEnabled ()
  {
    return m_bIndentEnabled;
  }

  /**
   * Enable or disable indentation of the output.
   *
   * @param bIndentEnabled
   *        <code>true</code> to enable indentation, <code>false</code> to disable it.
   * @return this for chaining
   */
  @NonNull
  public final JsonWriterSettings setIndentEnabled (final boolean bIndentEnabled)
  {
    m_bIndentEnabled = bIndentEnabled;
    return this;
  }

  /**
   * @return The string to be used for a single indentation level. Never <code>null</code> nor
   *         empty. Default is {@link #DEFAULT_INDENT_STRING}.
   */
  @NonNull
  @Nonempty
  public String getIndentString ()
  {
    return m_sIndentString;
  }

  /**
   * Set the string to be used for a single indentation level.
   *
   * @param sIndentString
   *        The indent string. May neither be <code>null</code> nor empty.
   * @return this for chaining
   */
  @NonNull
  public final JsonWriterSettings setIndentString (@NonNull @Nonempty final String sIndentString)
  {
    m_sIndentString = ValueEnforcer.notEmpty (sIndentString, "IndentString");
    return this;
  }

  /**
   * @return The newline string to be used. Never <code>null</code> nor empty. Default is
   *         {@link #DEFAULT_NEWLINE_STRING}.
   */
  @NonNull
  @Nonempty
  public String getNewlineString ()
  {
    return m_sNewlineString;
  }

  /**
   * Set the newline string to be used.
   *
   * @param sNewlineString
   *        The newline string. May neither be <code>null</code> nor empty.
   * @return this for chaining
   */
  @NonNull
  public final JsonWriterSettings setNewlineString (@NonNull @Nonempty final String sNewlineString)
  {
    m_sNewlineString = ValueEnforcer.notEmpty (sNewlineString, "NewlineString");
    return this;
  }

  /**
   * @return <code>true</code> if a newline should be written at the end, <code>false</code> if not.
   *         Default is {@link #DEFAULT_WRITE_NEWLINE_AT_END}.
   */
  public boolean isWriteNewlineAtEnd ()
  {
    return m_bWriteNewlineAtEnd;
  }

  /**
   * Enable or disable writing a newline at the end of the output.
   *
   * @param bWriteNewlineAtEnd
   *        <code>true</code> to write a newline at the end, <code>false</code> if not.
   * @return this for chaining
   */
  @NonNull
  public final JsonWriterSettings setWriteNewlineAtEnd (final boolean bWriteNewlineAtEnd)
  {
    m_bWriteNewlineAtEnd = bWriteNewlineAtEnd;
    return this;
  }

  /**
   * @return <code>true</code> if object names should be quoted, <code>false</code> if not. Default
   *         is {@link #DEFAULT_QUOTE_NAMES}.
   */
  public boolean isQuoteNames ()
  {
    return m_bQuoteNames;
  }

  /**
   * Enable or disable quoting of object names.
   *
   * @param bQuoteNames
   *        <code>true</code> to quote names, <code>false</code> if not.
   * @return this for chaining
   */
  @NonNull
  public final JsonWriterSettings setQuoteNames (final boolean bQuoteNames)
  {
    m_bQuoteNames = bQuoteNames;
    return this;
  }

  /**
   * @return A clone of this settings object. Never <code>null</code>.
   */
  @NonNull
  public JsonWriterSettings getClone ()
  {
    return new JsonWriterSettings (this);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("IndentEnabled", m_bIndentEnabled)
                                       .append ("IndentString",
                                                StringHex.getHexEncoded (m_sIndentString, StandardCharsets.ISO_8859_1))
                                       .append ("NewlineString",
                                                StringHex.getHexEncoded (m_sNewlineString, StandardCharsets.ISO_8859_1))
                                       .append ("WriteNewlineAtEnd", m_bWriteNewlineAtEnd)
                                       .append ("QuoteNames", m_bQuoteNames)
                                       .getToString ();
  }
}
