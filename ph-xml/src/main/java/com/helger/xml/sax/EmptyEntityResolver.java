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
package com.helger.xml.sax;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import jakarta.annotation.Nonnull;

/**
 * Special {@link EntityResolver} implementation that always delivers an empty
 * document.
 *
 * @author Philip Helger
 */
public class EmptyEntityResolver implements EntityResolver
{
  @Nonnull
  public InputSource resolveEntity (final String sPublicId, final String sSystemId)
  {
    // Create an empty document
    return InputSourceFactory.create ("");
  }
}
