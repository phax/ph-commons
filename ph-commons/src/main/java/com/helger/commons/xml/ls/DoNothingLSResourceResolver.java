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
package com.helger.commons.xml.ls;

import javax.annotation.Nullable;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * A {@link LSResourceResolver} implementation that does nothing.
 *
 * @author Philip Helger
 */
public class DoNothingLSResourceResolver implements LSResourceResolver
{
  public DoNothingLSResourceResolver ()
  {}

  @Nullable
  public LSInput resolveResource (@Nullable final String sType,
                                  @Nullable final String sNamespaceURI,
                                  @Nullable final String sPublicId,
                                  @Nullable final String sSystemId,
                                  @Nullable final String sBaseURI)
  {
    return null;
  }
}
