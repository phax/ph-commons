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
package com.helger.jaxb.adapter;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.CloneHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.xml.bind.JAXBElement;

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

  public static boolean equalsJAXBElements (@Nonnull final JAXBElement <?> aObj1, @Nonnull final JAXBElement <?> aObj2)
  {
    return EqualsHelper.equals (aObj1.getDeclaredType (), aObj2.getDeclaredType ()) &&
           EqualsHelper.equals (aObj1.getName (), aObj2.getName ()) &&
           EqualsHelper.equals (aObj1.getScope (), aObj2.getScope ()) &&
           EqualsHelper.equals (aObj1.isNil (), aObj2.isNil ()) &&
           EqualsHelper.equals (aObj1.getValue (), aObj2.getValue ());
  }

  public static int getHashcode (@Nonnull final JAXBElement <?> aObj)
  {
    return new HashCodeGenerator (aObj.getClass ()).append (aObj.getDeclaredType ())
                                                   .append (aObj.getName ())
                                                   .append (aObj.getScope ())
                                                   .append (aObj.isNil ())
                                                   .append (aObj.getValue ())
                                                   .getHashCode ();
  }

  @Nullable
  public static <DATATYPE> JAXBElement <DATATYPE> getClonedJAXBElement (@Nullable final JAXBElement <DATATYPE> aObj)
  {
    if (aObj == null)
      return null;

    final DATATYPE aClonedValue = CloneHelper.getClonedValue (aObj.getValue ());
    final JAXBElement <DATATYPE> ret = new JAXBElement <> (aObj.getName (),
                                                           aObj.getDeclaredType (),
                                                           aObj.getScope (),
                                                           aClonedValue);
    ret.setNil (aObj.isNil ());
    return ret;
  }
}
