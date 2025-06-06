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
package com.helger.jaxb.adapter.config;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.hashcode.IHashCodeImplementationRegistrarSPI;
import com.helger.commons.hashcode.IHashCodeImplementationRegistry;

import jakarta.xml.bind.JAXBElement;

/**
 * Implementation of {@link IHashCodeImplementationRegistrarSPI} for
 * {@link JAXBElement}.
 *
 * @author Philip Helger
 */
@IsSPIImplementation
public final class JAXBHashCodeImplementationRegistrarSPI implements IHashCodeImplementationRegistrarSPI
{
  public void registerHashCodeImplementations (@Nonnull final IHashCodeImplementationRegistry aRegistry)
  {
    // JAXBElement does not implement hashCode!
    aRegistry.registerHashCodeImplementation (JAXBElement.class,
                                              aObj -> new HashCodeGenerator (aObj.getClass ()).append (aObj.getDeclaredType ())
                                                                                              .append (aObj.getName ())
                                                                                              .append (aObj.getScope ())
                                                                                              .append (aObj.isNil ())
                                                                                              .append (aObj.getValue ())
                                                                                              .getHashCode ());
  }
}
