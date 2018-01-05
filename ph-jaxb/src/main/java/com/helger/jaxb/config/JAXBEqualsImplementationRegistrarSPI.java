/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
  public void registerEqualsImplementations (@Nonnull final IEqualsImplementationRegistry aRegistry)
  {
    // JAXBElement does not implement equals!
    aRegistry.registerEqualsImplementation (JAXBElement.class,
                                            (aObj1,
                                             aObj2) -> EqualsImplementationRegistry.areEqual (aObj1.getDeclaredType (),
                                                                                              aObj2.getDeclaredType ()) &&
                                                       EqualsImplementationRegistry.areEqual (aObj1.getName (),
                                                                                              aObj2.getName ()) &&
                                                       EqualsImplementationRegistry.areEqual (aObj1.getScope (),
                                                                                              aObj2.getScope ()) &&
                                                       EqualsHelper.equals (aObj1.isNil (), aObj2.isNil ()) &&
                                                       EqualsImplementationRegistry.areEqual (aObj1.getValue (),
                                                                                              aObj2.getValue ()));
  }
}
