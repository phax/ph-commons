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
package com.helger.xml.stax;

import java.io.IOException;
import java.io.Reader;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jspecify.annotations.NonNull;

import com.helger.base.enforce.ValueEnforcer;

/**
 * A {@link Reader} that delivers the character data of the current element of an
 * {@link XMLStreamReader} chunk by chunk, without collecting it in memory. Comments and processing
 * instructions are ignored, a child element is an error. The reader ends at the end element
 * matching the start element it was created on, and leaves the {@link XMLStreamReader} positioned
 * there.
 * <p>
 * To keep the memory constant, the {@link XMLStreamReader} should be created by a non-coalescing
 * factory (see {@link com.helger.xml.XMLFactory#createNonCoalescingXMLInputFactory()}).
 * </p>
 * <p>
 * Closing this reader closes the {@link XMLStreamReader}, but - as defined by
 * {@link XMLStreamReader#close()} - not the underlying input source.
 * </p>
 *
 * @author Philip Helger
 * @since 12.9.0
 */
public class StAXTextContentReader extends Reader
{
  private final XMLStreamReader m_aReader;
  // Text of an entity reference, that cannot be read in chunks
  private String m_sPendingText;
  private boolean m_bInTextEvent = false;
  private int m_nTextOffset = 0;
  private boolean m_bEOF = false;

  /**
   * Constructor
   *
   * @param aReader
   *        The reader to read from. Must be positioned on a start element. May not be
   *        <code>null</code>.
   * @throws IllegalStateException
   *         If the reader is not positioned on a start element
   */
  public StAXTextContentReader (@NonNull final XMLStreamReader aReader)
  {
    ValueEnforcer.notNull (aReader, "Reader");
    if (aReader.getEventType () != XMLStreamConstants.START_ELEMENT)
      throw new IllegalStateException ("The XMLStreamReader must be positioned on a start element");
    m_aReader = aReader;
  }

  /**
   * Move to the next event containing text.
   *
   * @return <code>true</code> if a text event was found, <code>false</code> if the end of the
   *         element was reached.
   */
  private boolean _nextTextEvent () throws IOException
  {
    try
    {
      while (true)
      {
        final int nEventType = m_aReader.next ();
        switch (nEventType)
        {
          case XMLStreamConstants.CHARACTERS:
          case XMLStreamConstants.CDATA:
          case XMLStreamConstants.SPACE:
            m_bInTextEvent = true;
            m_nTextOffset = 0;
            return true;
          case XMLStreamConstants.ENTITY_REFERENCE:
            m_sPendingText = m_aReader.getText ();
            m_nTextOffset = 0;
            return true;
          case XMLStreamConstants.COMMENT:
          case XMLStreamConstants.PROCESSING_INSTRUCTION:
            break;
          case XMLStreamConstants.END_ELEMENT:
            return false;
          case XMLStreamConstants.START_ELEMENT:
            throw new IOException ("Unexpected child element '" + m_aReader.getName () + "' in text content");
          case XMLStreamConstants.END_DOCUMENT:
            throw new IOException ("Unexpected end of document in text content");
          default:
            throw new IOException ("Unexpected XML event type " + nEventType + " in text content");
        }
      }
    }
    catch (final XMLStreamException ex)
    {
      throw new IOException ("Failed to read the text content", ex);
    }
  }

  @Override
  public int read (final char @NonNull [] aBuf, final int nOfs, final int nLen) throws IOException
  {
    ValueEnforcer.isArrayOfsLen (aBuf, nOfs, nLen);
    if (nLen == 0)
      return 0;

    while (!m_bEOF)
    {
      if (m_sPendingText != null)
      {
        final int nCount = Math.min (nLen, m_sPendingText.length () - m_nTextOffset);
        m_sPendingText.getChars (m_nTextOffset, m_nTextOffset + nCount, aBuf, nOfs);
        m_nTextOffset += nCount;
        if (m_nTextOffset >= m_sPendingText.length ())
          m_sPendingText = null;
        if (nCount > 0)
          return nCount;
      }
      else
        if (m_bInTextEvent)
        {
          final int nCount;
          try
          {
            nCount = m_aReader.getTextCharacters (m_nTextOffset, aBuf, nOfs, nLen);
          }
          catch (final XMLStreamException ex)
          {
            throw new IOException ("Failed to read the text content", ex);
          }
          if (nCount > 0)
          {
            m_nTextOffset += nCount;
            return nCount;
          }
          // Current text event is exhausted
          m_bInTextEvent = false;
        }
        else
          if (!_nextTextEvent ())
            m_bEOF = true;
    }
    return -1;
  }

  @Override
  public void close () throws IOException
  {
    try
    {
      m_aReader.close ();
    }
    catch (final XMLStreamException ex)
    {
      throw new IOException ("Failed to close the XMLStreamReader", ex);
    }
  }
}
