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
package com.helger.xml.microdom.convert;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;

/**
 * Default {@link IMicroTypeConverter} implementation for {@link String}
 * objects.
 *
 * @author Philip Helger
 */
@Immutable
public final class StringMicroTypeConverter implements IMicroTypeConverter <String>
{
  private static final StringMicroTypeConverter INSTANCE = new StringMicroTypeConverter ();

  private StringMicroTypeConverter ()
  {}

  @NonNull
  public static StringMicroTypeConverter getInstance ()
  {
    return INSTANCE;
  }

  @NonNull
  public IMicroElement convertToMicroElement (@NonNull final String aObject,
                                              @Nullable final String sNamespaceURI,
                                              @NonNull @Nonempty final String sTagName)
  {
    final IMicroElement e = new MicroElement (sNamespaceURI, sTagName);
    e.addText (aObject);
    return e;
  }

  @NonNull
  public String convertToNative (@NonNull final IMicroElement aElement)
  {
    return aElement.getTextContent ();
  }
}
