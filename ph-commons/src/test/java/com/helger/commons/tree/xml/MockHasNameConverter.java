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
package com.helger.commons.tree.xml;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.util.MicroHelper;
import com.helger.commons.name.MockHasName;

public final class MockHasNameConverter implements IConverterTreeXML <MockHasName>
{
  @Nullable
  public String getNamespaceURI ()
  {
    return "bla";
  }

  public void appendDataValue (@Nonnull final IMicroElement eDataElement, @Nullable final MockHasName aAnyName)
  {
    final IMicroElement eName = eDataElement.appendElement (getNamespaceURI (), "name");
    if (aAnyName != null)
      eName.appendText (aAnyName.getName ());
  }

  @Nonnull
  public MockHasName getAsDataValue (final IMicroElement eDataElement)
  {
    return new MockHasName (MicroHelper.getChildTextContent (eDataElement, getNamespaceURI (), "name"));
  }
}
