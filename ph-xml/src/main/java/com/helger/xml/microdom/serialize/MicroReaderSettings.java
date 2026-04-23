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
package com.helger.xml.microdom.serialize;

import org.jspecify.annotations.NonNull;

import com.helger.xml.EXMLParserFeature;
import com.helger.xml.serialize.read.SAXReaderSettings;

/**
 * Special {@link SAXReaderSettings} implementing also {@link IMicroReaderSettings}
 *
 * @author Philip Helger
 * @since 12.2.2
 */
public class MicroReaderSettings extends SAXReaderSettings implements IMicroReaderSettings
{
  private boolean m_bSaveNamespaceDeclarations = false;

  public MicroReaderSettings ()
  {}

  public MicroReaderSettings (@NonNull final MicroReaderSettings aOther)
  {
    super (aOther);
    m_bSaveNamespaceDeclarations = aOther.m_bSaveNamespaceDeclarations;
  }

  public boolean isSaveNamespaceDeclarations ()
  {
    return m_bSaveNamespaceDeclarations;
  }

  @NonNull
  public MicroReaderSettings setSaveNamespaceDeclarations (final boolean bSaveNamespaceDeclarations)
  {
    // Enable namespace-prefixes so that xmlns: attributes are reported in
    // the Attributes parameter of startElement. Also enable xmlns-uris so
    // that xmlns attributes get the proper http://www.w3.org/2000/xmlns/
    // namespace URI.
    setFeatureValue (EXMLParserFeature.SAX_NAMESPACE_PREFIXES, bSaveNamespaceDeclarations);
    setFeatureValue (EXMLParserFeature.SAX_XMLNS_URIS, bSaveNamespaceDeclarations);
    m_bSaveNamespaceDeclarations = bSaveNamespaceDeclarations;
    return this;
  }
}
