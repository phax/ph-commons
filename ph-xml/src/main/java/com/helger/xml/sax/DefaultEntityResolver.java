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
package com.helger.xml.sax;

import java.io.IOException;
import java.net.URL;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.io.resource.IReadableResource;
import com.helger.io.resourceresolver.DefaultResourceResolver;
import com.helger.xml.ls.SimpleLSResourceResolver;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A simple version of {@link EntityResolver} using
 * {@link SimpleLSResourceResolver} with a base URL.
 *
 * @author Philip Helger
 * @since 8.6.5
 */
public class DefaultEntityResolver implements EntityResolver
{
  private final String m_sBaseURI;

  /**
   * Constructor with a URL.
   *
   * @param aBaseURL
   *        The base URL. May not be <code>null</code>.
   */
  public DefaultEntityResolver (@Nonnull final URL aBaseURL)
  {
    this (aBaseURL.toExternalForm ());
  }

  /**
   * Constructor with a base URI.
   *
   * @param sBaseURI
   *        The base URI. May not be <code>null</code>.
   */
  public DefaultEntityResolver (@Nonnull final String sBaseURI)
  {
    m_sBaseURI = ValueEnforcer.notNull (sBaseURI, "BaseURI");
  }

  /**
   * @return The base URI from the constructor. Never <code>null</code>.
   * @since 9.2.0
   */
  @Nonnull
  public final String getBaseURI ()
  {
    return m_sBaseURI;
  }

  @Nullable
  public InputSource resolveEntity (@Nullable final String sPublicID, @Nullable final String sSystemID) throws SAXException, IOException
  {
    final IReadableResource aResolvedRes = DefaultResourceResolver.getResolvedResource (sSystemID, m_sBaseURI);
    return InputSourceFactory.create (aResolvedRes);
  }

  /**
   * Factory method with a resource.
   *
   * @param aBaseResource
   *        The base resource. May not be <code>null</code>.
   * @return <code>null</code> if the resource does not exist
   */
  @Nullable
  public static DefaultEntityResolver createOnDemand (@Nonnull final IReadableResource aBaseResource)
  {
    final URL aURL = aBaseResource.getAsURL ();
    return aURL == null ? null : new DefaultEntityResolver (aURL);
  }
}
