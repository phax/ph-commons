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

import com.helger.xml.serialize.read.ISAXReaderSettings;

/**
 * Extended version of {@link ISAXReaderSettings}
 *
 * @author Philip Helger
 * @since 12.2.2
 */
public interface IMicroReaderSettings extends ISAXReaderSettings
{
  /**
   * @return <code>true</code> to enable namespace-prefixes so that "xmlns:" attributes are reported
   *         in the Attributes parameter of startElement. Also enable xmlns-uris so that xmlns
   *         attributes get the proper http://www.w3.org/2000/xmlns/ namespace URI.
   */
  boolean isSaveNamespaceDeclarations ();
}
