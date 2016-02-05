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
package com.helger.jaxb.builder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.traits.IGenericImplTrait;
import com.helger.jaxb.builder.IJAXBDocumentType;

/**
 * Abstract builder class for reading, writing and validating JAXB documents.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The implementation class implementing this abstract class.
 */
@NotThreadSafe
public abstract class AbstractJAXBBuilder <IMPLTYPE extends AbstractJAXBBuilder <IMPLTYPE>>
                                         implements IGenericImplTrait <IMPLTYPE>
{
  protected final IJAXBDocumentType m_aDocType;
  protected ClassLoader m_aClassLoader;

  public AbstractJAXBBuilder (@Nonnull final IJAXBDocumentType aDocType)
  {
    m_aDocType = ValueEnforcer.notNull (aDocType, "DocType");
  }

  /**
   * @return The document type as passed in the constructor. Never
   *         <code>null</code>.
   */
  @Nonnull
  public IJAXBDocumentType getJAXBDocumentType ()
  {
    return m_aDocType;
  }

  /**
   * @return The special class loader to be used. <code>null</code> by default.
   */
  @Nullable
  public ClassLoader getClassLoader ()
  {
    return m_aClassLoader;
  }

  /**
   * Set the class loader to be used. This is optional.
   *
   * @param aClassLoader
   *        The class loader to be used. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  public IMPLTYPE setClassLoader (@Nullable final ClassLoader aClassLoader)
  {
    m_aClassLoader = aClassLoader;
    return thisAsT ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("DocType", m_aDocType)
                                       .append ("ClassLoader", m_aClassLoader)
                                       .toString ();
  }
}
