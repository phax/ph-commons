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
package com.helger.jaxb.config;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBElement;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.equals.EqualsImplementationRegistry;
import com.helger.commons.equals.IEqualsImplementation;
import com.helger.commons.equals.IEqualsImplementationRegistrarSPI;
import com.helger.commons.equals.IEqualsImplementationRegistry;

/**
 * Implementation of {@link IEqualsImplementationRegistrarSPI} for
 * {@link JAXBElement}.
 *
 * @author Philip Helger
 */
@IsSPIImplementation
public final class JAXBEqualsImplementationRegistrarSPI implements IEqualsImplementationRegistrarSPI
{
  private static final class EqualsImplementationJAXBElement implements IEqualsImplementation
  {
    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      final JAXBElement <?> aRealObj1 = (JAXBElement <?>) aObj1;
      final JAXBElement <?> aRealObj2 = (JAXBElement <?>) aObj2;
      return EqualsImplementationRegistry.areEqual (aRealObj1.getDeclaredType (), aRealObj2.getDeclaredType ()) &&
             EqualsImplementationRegistry.areEqual (aRealObj1.getName (), aRealObj2.getName ()) &&
             EqualsImplementationRegistry.areEqual (aRealObj1.getScope (), aRealObj2.getScope ()) &&
             EqualsHelper.equals (aRealObj1.isNil (), aRealObj2.isNil ()) &&
             EqualsImplementationRegistry.areEqual (aRealObj1.getValue (), aRealObj2.getValue ());
    }
  }

  public void registerEqualsImplementations (@Nonnull final IEqualsImplementationRegistry aRegistry)
  {
    // JAXBElement does not implement equals!
    aRegistry.registerEqualsImplementation (JAXBElement.class, new EqualsImplementationJAXBElement ());
  }
}
