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
package com.helger.dao.mock;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.convert.IMicroTypeConverter;

/**
 * Micro type converter for {@link MockDAOItem}.
 *
 * @author Philip Helger
 */
public final class MockDAOItemMicroTypeConverter implements IMicroTypeConverter <MockDAOItem>
{
  private static final String ATTR_ID = "id";
  private static final String ATTR_NAME = "name";

  @Nullable
  public IMicroElement convertToMicroElement (@NonNull final MockDAOItem aValue,
                                              @Nullable final String sNamespaceURI,
                                              @NonNull @Nonempty final String sTagName)
  {
    final IMicroElement eItem = new MicroElement (sNamespaceURI, sTagName);
    eItem.setAttribute (ATTR_ID, aValue.getID ());
    eItem.setAttribute (ATTR_NAME, aValue.getName ());
    return eItem;
  }

  @Nullable
  public MockDAOItem convertToNative (@NonNull final IMicroElement aElement)
  {
    return new MockDAOItem (aElement.getAttributeValue (ATTR_ID), aElement.getAttributeValue (ATTR_NAME));
  }
}
