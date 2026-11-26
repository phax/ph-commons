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

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

import org.jspecify.annotations.NonNull;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * A filtering {@link XMLStreamReader} that reports selected elements without their content. The
 * underlying reader still reads the complete document, so every well-formedness error is reported
 * as usual - only the events inside the selected elements are not passed on.
 * <p>
 * The main use case is reading the header part of a large envelope document (e.g. via JAXB) without
 * building the large payload in memory. Combine it with
 * {@link com.helger.xml.XMLFactory#createNonCoalescingXMLInputFactory()}, otherwise a large text
 * node is collected in memory by the underlying reader before it is skipped.
 * </p>
 *
 * @author Philip Helger
 * @since 12.9.0
 */
public class ShallowXMLStreamReader extends StreamReaderDelegate
{
  private final IShallowElementSelector m_aSelector;
  private final ICommonsList <QName> m_aElementPath = new CommonsArrayList <> ();
  private final List <QName> m_aElementPathView = Collections.unmodifiableList (m_aElementPath);
  private boolean m_bSkipContent = false;

  /**
   * Constructor
   *
   * @param aReader
   *        The underlying reader. May not be <code>null</code>.
   * @param aSelector
   *        The selector that decides what is not reported. May not be <code>null</code>.
   */
  public ShallowXMLStreamReader (@NonNull final XMLStreamReader aReader,
                                 @NonNull final IShallowElementSelector aSelector)
  {
    super (ValueEnforcer.notNull (aReader, "Reader"));
    ValueEnforcer.notNull (aSelector, "Selector");
    m_aSelector = aSelector;
  }

  /**
   * Read the underlying reader up to the end element matching the current start element.
   */
  private void _skipToMatchingEndElement () throws XMLStreamException
  {
    int nNestingLevel = 0;
    while (true)
    {
      final int nEventType = super.next ();
      if (nEventType == START_ELEMENT)
        nNestingLevel++;
      else
        if (nEventType == END_ELEMENT)
        {
          if (nNestingLevel == 0)
            return;
          nNestingLevel--;
        }
        else
          if (nEventType == END_DOCUMENT)
            throw new XMLStreamException ("Unexpected end of document", getLocation ());
    }
  }

  private static boolean _isText (final int nEventType)
  {
    return nEventType == CHARACTERS || nEventType == CDATA || nEventType == SPACE || nEventType == ENTITY_REFERENCE;
  }

  @Override
  public int next () throws XMLStreamException
  {
    if (m_bSkipContent)
    {
      // The previous event was the start of a shallow element
      m_bSkipContent = false;
      _skipToMatchingEndElement ();
      m_aElementPath.removeLastOrNull ();
      return END_ELEMENT;
    }

    while (true)
    {
      final int nEventType = super.next ();
      if (nEventType == START_ELEMENT)
      {
        final QName aName = getName ();
        if (m_aSelector.isShallowElement (m_aElementPathView, aName))
          m_bSkipContent = true;
        m_aElementPath.add (aName);
        return nEventType;
      }
      if (nEventType == END_ELEMENT)
      {
        m_aElementPath.removeLastOrNull ();
        return nEventType;
      }
      if (_isText (nEventType) && m_aElementPath.isNotEmpty () && m_aSelector.isSkipText (m_aElementPathView))
        continue;
      return nEventType;
    }
  }

  @Override
  public int nextTag () throws XMLStreamException
  {
    int nEventType = next ();
    while ((nEventType == CHARACTERS && isWhiteSpace ()) ||
           (nEventType == CDATA && isWhiteSpace ()) ||
           nEventType == SPACE ||
           nEventType == COMMENT ||
           nEventType == PROCESSING_INSTRUCTION)
      nEventType = next ();
    if (nEventType != START_ELEMENT && nEventType != END_ELEMENT)
      throw new XMLStreamException ("Expected a start or end element but found event type " + nEventType,
                                    getLocation ());
    return nEventType;
  }

  @Override
  public String getElementText () throws XMLStreamException
  {
    if (getEventType () != START_ELEMENT)
      throw new XMLStreamException ("The current event is not a start element", getLocation ());

    final StringBuilder aSB = new StringBuilder ();
    int nEventType = next ();
    while (nEventType != END_ELEMENT)
    {
      if (_isText (nEventType))
        aSB.append (getText ());
      else
        if (nEventType == START_ELEMENT)
          throw new XMLStreamException ("Element text must not contain child elements", getLocation ());
        else
          if (nEventType == END_DOCUMENT)
            throw new XMLStreamException ("Unexpected end of document", getLocation ());
      nEventType = next ();
    }
    return aSB.toString ();
  }
}
