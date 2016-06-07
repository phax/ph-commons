/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.serialize.read;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import org.w3c.dom.Node;

import com.helger.commons.io.streamprovider.StringInputStreamProvider;
import com.helger.commons.xml.serialize.write.IXMLWriterSettings;
import com.helger.commons.xml.serialize.write.XMLWriter;
import com.helger.commons.xml.serialize.write.XMLWriterSettings;

/**
 * A special input stream provider that takes an existing {@link Node} and
 * converts it to a byte array.
 *
 * @author Philip Helger
 */
public class DOMInputStreamProvider extends StringInputStreamProvider
{
  /**
   * Constructor for W3C nodes using the default XML charset.
   *
   * @param aNode
   *        The node to be streamed. May not be <code>null</code>.
   */
  public DOMInputStreamProvider (@Nonnull final Node aNode)
  {
    this (aNode, XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }

  /**
   * Constructor for W3C nodes.
   *
   * @param aNode
   *        The node to be streamed. May not be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   */
  public DOMInputStreamProvider (@Nonnull final Node aNode, @Nonnull final Charset aCharset)
  {
    this (aNode, new XMLWriterSettings ().setCharset (aCharset));
  }

  /**
   * Constructor for W3C nodes.
   *
   * @param aNode
   *        The node to be streamed. May not be <code>null</code>.
   * @param aSettings
   *        The settings to use. May not be <code>null</code>.
   */
  public DOMInputStreamProvider (@Nonnull final Node aNode, @Nonnull final IXMLWriterSettings aSettings)
  {
    super (XMLWriter.getNodeAsString (aNode, aSettings), aSettings.getCharsetObj ());
  }
}
