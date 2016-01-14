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
package com.helger.commons.microdom.serialize;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import com.helger.commons.io.streamprovider.StringInputStreamProvider;
import com.helger.commons.microdom.IMicroNode;
import com.helger.commons.xml.serialize.write.IXMLWriterSettings;
import com.helger.commons.xml.serialize.write.XMLWriterSettings;

/**
 * A special input stream provider that takes an existing {@link IMicroNode} and
 * converts it to a byte array.
 *
 * @author Philip Helger
 */
public class MicroDOMInputStreamProvider extends StringInputStreamProvider
{
  /**
   * Constructor for MicroNodes using the default charset.
   *
   * @param aNode
   *        The node to be streamed. May not be <code>null</code>.
   * @see XMLWriterSettings#DEFAULT_XML_CHARSET
   */
  public MicroDOMInputStreamProvider (@Nonnull final IMicroNode aNode)
  {
    this (aNode, XMLWriterSettings.DEFAULT_XML_SETTINGS);
  }

  /**
   * Constructor for MicroNodes.
   *
   * @param aNode
   *        The node to be streamed. May not be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   */
  public MicroDOMInputStreamProvider (@Nonnull final IMicroNode aNode, @Nonnull final Charset aCharset)
  {
    this (aNode, new XMLWriterSettings ().setCharset (aCharset));
  }

  /**
   * Constructor for micro nodes.
   *
   * @param aNode
   *        The node to be streamed. May not be <code>null</code>.
   * @param aSettings
   *        The settings to use. May not be <code>null</code>.
   */
  public MicroDOMInputStreamProvider (@Nonnull final IMicroNode aNode, @Nonnull final IXMLWriterSettings aSettings)
  {
    super (MicroWriter.getNodeAsString (aNode, aSettings), aSettings.getCharsetObj ());
  }
}
