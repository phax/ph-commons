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

import java.io.InputStream;

import javax.xml.stream.XMLResolver;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.CGlobal;
import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;

/**
 * Special {@link javax.xml.stream.XMLResolver} implementation that always delivers an empty
 * document. Contrary to returning <code>null</code> - which makes the StAX implementation open a
 * regular URL connection to the system ID itself - an empty document ends the resolution, so that
 * no external resource can be fetched.
 *
 * @author Philip Helger
 * @since 12.4.1
 */
public class EmptyXMLResolver implements XMLResolver
{
  /**
   * Default constructor.
   */
  public EmptyXMLResolver ()
  {}

  /** {@inheritDoc} */
  @NonNull
  public InputStream resolveEntity (@Nullable final String sPublicID,
                                    @Nullable final String sSystemID,
                                    @Nullable final String sBaseURI,
                                    @Nullable final String sNamespace)
  {
    // Create an empty document
    return new NonBlockingByteArrayInputStream (CGlobal.EMPTY_BYTE_ARRAY);
  }
}
