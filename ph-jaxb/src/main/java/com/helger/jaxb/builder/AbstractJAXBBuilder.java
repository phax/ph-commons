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
package com.helger.jaxb.builder;

import java.lang.ref.WeakReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.DevelopersNote;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.lang.IHasClassLoader;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.traits.IGenericImplTrait;
import com.helger.jaxb.JAXBContextCache;

/**
 * Abstract builder class for reading, writing and validating JAXB documents.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The implementation class implementing this abstract class.
 */
@NotThreadSafe
public abstract class AbstractJAXBBuilder <IMPLTYPE extends AbstractJAXBBuilder <IMPLTYPE>> implements
                                          IGenericImplTrait <IMPLTYPE>,
                                          IHasClassLoader
{
  protected final IJAXBDocumentType m_aDocType;
  private WeakReference <ClassLoader> m_aClassLoader;
  private boolean m_bUseJAXBContextCache = JAXBBuilderDefaultSettings.isDefaultUseContextCache ();

  public AbstractJAXBBuilder (@Nonnull final IJAXBDocumentType aDocType)
  {
    m_aDocType = ValueEnforcer.notNull (aDocType, "DocType");
    // By default this class loader of the type to be marshalled should be used
    // This is important for OSGI application containers and ANT tasks
    m_aClassLoader = new WeakReference <> (aDocType.getImplementationClass ().getClassLoader ());
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
  public final ClassLoader getClassLoader ()
  {
    return m_aClassLoader.get ();
  }

  /**
   * Set the class loader to be used. This is optional. Since v9.0.0 a class
   * loader is set by default, so this method is most likely not needed anymore!
   *
   * @param aClassLoader
   *        The class loader to be used. May be <code>null</code>.
   * @return this
   */
  @Nonnull
  @Deprecated
  @DevelopersNote ("Deprecated since v9.0.0")
  public final IMPLTYPE setClassLoader (@Nullable final ClassLoader aClassLoader)
  {
    m_aClassLoader = new WeakReference <> (aClassLoader);
    return thisAsT ();
  }

  /**
   * @return <code>true</code> if the {@link JAXBContextCache} is used,
   *         <code>false</code> if not. Default is <code>true</code>.
   */
  public boolean isUseJAXBContextCache ()
  {
    return m_bUseJAXBContextCache;
  }

  /**
   * Set usage of the {@link JAXBContextCache}. For performance reasons it's
   * recommended to use the cache.
   *
   * @param bUseJAXBContextCache
   *        <code>true</code> to use the cache, <code>false</code> to create a
   *        new {@link JAXBContext} every time.
   * @return this
   */
  @Nonnull
  public final IMPLTYPE setUseJAXBContextCache (final boolean bUseJAXBContextCache)
  {
    m_bUseJAXBContextCache = bUseJAXBContextCache;
    return thisAsT ();
  }

  /**
   * @return The XML schema to be used for validating instances. May be
   *         <code>null</code> if no XSDs are present.
   */
  @Nullable
  protected final Schema getSchema ()
  {
    return m_aDocType.getSchema (getClassLoader ());
  }

  @Nonnull
  @OverrideOnDemand
  protected JAXBContext getJAXBContext () throws JAXBException
  {
    if (m_bUseJAXBContextCache)
    {
      // Since creating the JAXB context is quite cost intensive this is done
      // only once!
      return JAXBContextCache.getInstance ().getFromCache (m_aDocType.getImplementationClass (), getClassLoader ());
    }

    // Create a new JAXBContext - inefficient
    return JAXBContext.newInstance (m_aDocType.getImplementationClass ().getPackage ().getName (), getClassLoader ());
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("DocType", m_aDocType)
                                       .appendIfNotNull ("ClassLoader", m_aClassLoader)
                                       .append ("UseJAXBContextCache", m_bUseJAXBContextCache)
                                       .getToString ();
  }
}
