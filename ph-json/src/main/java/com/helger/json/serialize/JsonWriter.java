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
package com.helger.json.serialize;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.WillClose;
import com.helger.annotation.WillNotClose;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.base.io.stream.NonClosingOutputStream;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.string.StringHelper;
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

  public JsonWriter (@NonNull final IJsonWriterSettings aSettings)
  {
    ValueEnforcer.notNull (aSettings, "Settings");
    m_aSettings = aSettings.getClone ();
  }

  /**
   * @return A clone of the JSON writer settings to be used. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public IJsonWriterSettings getSettings ()
  {
    return m_aSettings.getClone ();
  }

  private void _writeToWriter (@NonNull final IJson aJson,
                               @NonNull @WillNotClose final Writer aWriter,
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

        boolean bFirstChild = true;
        for (final IJson aChild : (IJsonArray) aJson)
        {
          if (bFirstChild)
            bFirstChild = false;
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

        boolean bFirstChild = true;
        for (final Map.Entry <String, IJson> aEntry : (IJsonObject) aJson)
        {
          if (bFirstChild)
            bFirstChild = false;
          else
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

  public void writeToWriter (@NonNull final IJson aJson, @NonNull @WillNotClose final Writer aWriter) throws IOException
  {
    ValueEnforcer.notNull (aJson, "Json");
    ValueEnforcer.notNull (aWriter, "Writer");

    _writeToWriter (aJson, aWriter, 0);

    if (m_aSettings.isWriteNewlineAtEnd ())
      aWriter.write (m_aSettings.getNewlineString ());

    aWriter.flush ();
  }

  public void writeToWriterAndClose (@NonNull final IJson aJson, @NonNull @WillClose final Writer aWriter)
                                                                                                           throws IOException
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

  @NonNull
  public String writeAsString (@NonNull final IJson aJson)
  {
    ValueEnforcer.notNull (aJson, "Json");

    try (final NonBlockingStringWriter aWriter = new NonBlockingStringWriter (1024))
    {
      writeToWriter (aJson, aWriter);
      return aWriter.getAsString ();
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("NonBlockingStringWriter should never throw IOException!", ex);
    }
  }

  /**
   * Write the JSON to an OutputStream using the provided Charset, and leave the OutputStream open.
   *
   * @param aJson
   *        The JSON to be written. May not be <code>null</code>.
   * @param aOS
   *        The OutputStream to write to. May not be <code>null</code>.
   * @param aCharset
   *        The character set to be used. May not be <code>null</code>.
   * @throws IOException
   *         On IO error
   * @since 9.4.0
   */
  public void writeToStream (@NonNull final IJson aJson,
                             @NonNull @WillNotClose final OutputStream aOS,
                             @NonNull final Charset aCharset) throws IOException
  {
    ValueEnforcer.notNull (aJson, "Json");
    ValueEnforcer.notNull (aOS, "OutputStream");
    ValueEnforcer.notNull (aCharset, "Charset");

    // Ensure OutputStream stays open
    try (final Writer aWriter = new OutputStreamWriter (new NonClosingOutputStream (aOS), aCharset))
    {
      writeToWriter (aJson, aWriter);
    }
  }

  /**
   * Write the JSON to an OutputStream using the provided Charset, and close the OutputStream
   * afterwards.
   *
   * @param aJson
   *        The JSON to be written. May not be <code>null</code>.
   * @param aOS
   *        The OutputStream to write to. May not be <code>null</code>.
   * @param aCharset
   *        The character set to be used. May not be <code>null</code>.
   * @throws IOException
   *         On IO error
   * @since 9.4.0
   */
  public void writeToStreamAndClose (@NonNull final IJson aJson,
                                     @NonNull @WillClose final OutputStream aOS,
                                     @NonNull final Charset aCharset) throws IOException
  {
    ValueEnforcer.notNull (aJson, "Json");
    ValueEnforcer.notNull (aOS, "OutputStream");
    ValueEnforcer.notNull (aCharset, "Charset");

    // Ensure OutputStream gets closed as well
    try (final Writer aWriter = new OutputStreamWriter (aOS, aCharset))
    {
      writeToWriter (aJson, aWriter);
    }
  }

  /**
   * Write the JSON to an byte array using the provided Charset.
   *
   * @param aJson
   *        The JSON to be written. May not be <code>null</code>.
   * @param aCharset
   *        The character set to be used. May not be <code>null</code>.
   * @return The created byte array and never <code>null</code>.
   * @since 9.4.0
   */
  @NonNull
  public byte [] writeAsByteArray (@NonNull final IJson aJson, @NonNull final Charset aCharset)
  {
    ValueEnforcer.notNull (aJson, "Json");

    try (final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream (1024))
    {
      writeToStream (aJson, aBAOS, aCharset);
      return aBAOS.getBufferOrCopy ();
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("NonBlockingByteArrayOutputStream should never throw IOException!", ex);
    }
  }
}
