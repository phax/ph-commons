/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.jaxb;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.JAXBElement;

import com.helger.commons.lang.CloneHelper;

/**
 * Misc utility classes for JAXB
 *
 * @author Philip Helger
 * @since 9.2.0
 */
@Immutable
public final class JAXBHelper
{
  private JAXBHelper ()
  {}

  @Nullable
  public static <DATATYPE> JAXBElement <DATATYPE> getClonedJAXBElement (@Nullable final JAXBElement <DATATYPE> aObj)
  {
    if (aObj == null)
      return null;

    final DATATYPE aClonedValue = CloneHelper.getClonedValue (aObj.getValue ());
    final JAXBElement <DATATYPE> ret = new JAXBElement <> (aObj.getName (), aObj.getDeclaredType (), aObj.getScope (), aClonedValue);
    ret.setNil (aObj.isNil ());
    return ret;
  }
}
