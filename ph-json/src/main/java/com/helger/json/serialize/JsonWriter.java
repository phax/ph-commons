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

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.WillClose;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.io.stream.NonBlockingStringWriter;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.string.StringHelper;
import com.helger.json.CJson;
import com.helger.json.IJson;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonCollection;
import com.helger.json.IJsonObject;
import com.helger.json.IJsonValue;
import com.helger.json.convert.JsonEscapeHelper;
import com.helger.json.valueserializer.JsonValueSerializerEscaped;

/**
 * Convert {@link IJson} objects to a String.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class JsonWriter
{
  private final IJsonWriterSettings m_aSettings;

  public JsonWriter ()
  {
    this (JsonWriterSettings.DEFAULT_SETTINGS);
  }

  public JsonWriter (@Nonnull final IJsonWriterSettings aSettings)
  {
    ValueEnforcer.notNull (aSettings, "Settings");
    m_aSettings = aSettings.getClone ();
  }

  /**
   * @return A clone of the JSON writer settings to be used. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public IJsonWriterSettings getSettings ()
  {
    return m_aSettings.getClone ();
  }

  private void _writeToWriter (@Nonnull final IJson aJson,
                               @Nonnull @WillNotClose final Writer aWriter,
                               final int nIndentLevel) throws IOException
  {
    ValueEnforcer.notNull (aJson, "Json");

    if (aJson.isValue ())
    {
      // Simple value
      ((IJsonValue) aJson).appendAsJsonString (aWriter);
    }
    else
    {
      // Complex (array or object)
      final boolean bIsIndentEnabled = m_aSettings.isIdentEnabled () && ((IJsonCollection) aJson).isNotEmpty ();
      final String sSingleIndent = m_aSettings.getIndentString ();
      final String sIndentString = bIsIndentEnabled ? StringHelper.getRepeated (sSingleIndent, nIndentLevel) : "";
      final String sNestedIndentString = bIsIndentEnabled ? sIndentString + sSingleIndent : "";
      final String sNewlineString = bIsIndentEnabled ? m_aSettings.getNewlineString () : "";
      final boolean bQuoteNames = m_aSettings.isQuoteNames ();

      if (aJson.isArray ())
      {
        aWriter.write (CJson.ARRAY_START);

        if (bIsIndentEnabled)
          aWriter.write (sNewlineString);

        boolean bFirst = true;
        for (final IJson aChild : (IJsonArray) aJson)
        {
          if (bFirst)
            bFirst = false;
          else
          {
            aWriter.write (CJson.ITEM_SEPARATOR);

            if (bIsIndentEnabled)
              aWriter.write (sNewlineString);
          }

          if (bIsIndentEnabled)
            aWriter.write (sNestedIndentString);

          _writeToWriter (aChild, aWriter, nIndentLevel + 1);
        }

        // Newline after the last entry
        if (bIsIndentEnabled)
        {
          aWriter.write (m_aSettings.getNewlineString ());
          aWriter.write (sIndentString);
        }

        aWriter.write (CJson.ARRAY_END);
      }
      else
      {
        // Must be an object
        aWriter.write (CJson.OBJECT_START);

        if (bIsIndentEnabled)
          aWriter.write (sNewlineString);

        int nIndex = 0;
        for (final Map.Entry <String, IJson> aEntry : (IJsonObject) aJson)
        {
          if (nIndex++ > 0)
          {
            aWriter.write (CJson.ITEM_SEPARATOR);

            if (bIsIndentEnabled)
              aWriter.write (sNewlineString);
          }

          if (bIsIndentEnabled)
            aWriter.write (sNestedIndentString);

          // Object name
          if (bQuoteNames)
            JsonValueSerializerEscaped.appendEscapedJsonString (aEntry.getKey (), aWriter);
          else
            JsonEscapeHelper.jsonEscapeToWriter (aEntry.getKey (), aWriter);

          // Name value separator
          aWriter.write (CJson.NAME_VALUE_SEPARATOR);

          // Object value
          _writeToWriter (aEntry.getValue (), aWriter, nIndentLevel + 1);
        }

        // Newline after the last entry
        if (bIsIndentEnabled)
        {
          aWriter.write (sNewlineString);
          aWriter.write (sIndentString);
        }

        aWriter.write (CJson.OBJECT_END);
      }
    }
  }

  public void writeToWriter (@Nonnull final IJson aJson, @Nonnull @WillNotClose final Writer aWriter) throws IOException
  {
    ValueEnforcer.notNull (aJson, "Json");
    ValueEnforcer.notNull (aWriter, "Writer");

    _writeToWriter (aJson, aWriter, 0);

    if (m_aSettings.isWriteNewlineAtEnd ())
      aWriter.write (m_aSettings.getNewlineString ());
  }

  public void writeToWriterAndClose (@Nonnull final IJson aJson,
                                     @Nonnull @WillClose final Writer aWriter) throws IOException
  {
    ValueEnforcer.notNull (aJson, "Json");
    ValueEnforcer.notNull (aWriter, "Writer");

    try
    {
      writeToWriter (aJson, aWriter);
    }
    finally
    {
      StreamHelper.close (aWriter);
    }
  }

  @Nonnull
  public String writeAsString (@Nonnull final IJson aJson)
  {
    ValueEnforcer.notNull (aJson, "Json");

    try
    {
      final NonBlockingStringWriter aWriter = new NonBlockingStringWriter (1024);
      writeToWriterAndClose (aJson, aWriter);
      return aWriter.getAsString ();
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("NonBlockingStringWriter should never throw IOException!", ex);
    }
  }
}
