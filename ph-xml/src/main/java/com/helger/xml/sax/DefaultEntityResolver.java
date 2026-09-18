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
package com.helger.xml.sax;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsSet;
import com.helger.io.resource.IReadableResource;
import com.helger.io.resourceresolver.DefaultResourceResolver;
import com.helger.xml.XMLResourceSchemeHelper;
import com.helger.xml.ls.SimpleLSResourceResolver;

/**
 * A simple version of {@link EntityResolver} using {@link SimpleLSResourceResolver} with a base
 * URL.
 * <p>
 * Like {@link SimpleLSResourceResolver} and
 * {@link com.helger.xml.transform.DefaultTransformURIResolver} this resolver blocks all remote URL
 * schemes by default, to prevent Server Side Request Forgery (SSRF) via an external entity. Use
 * {@link #setAllowedRemoteSchemes(String...)} to explicitly allow specific remote schemes. Note
 * that the default parser configuration in {@link com.helger.xml.XMLFactory} disallows DOCTYPE
 * declarations and external entities anyway, so this resolver is not invoked for external entities
 * under the default (secure) settings.
 *
 * @author Philip Helger
 * @since 8.6.5
 */
public class DefaultEntityResolver implements EntityResolver
{
  private static final Logger LOGGER = LoggerFactory.getLogger (DefaultEntityResolver.class);

  private final String m_sBaseURI;
  // Remote schemes that are explicitly allowed for resolution. Empty by default
  // to prevent SSRF via external entities.
  private final ICommonsSet <String> m_aAllowedRemoteSchemes = new CommonsHashSet <> ();

  /**
   * Constructor with a URL.
   *
   * @param aBaseURL
   *        The base URL. May not be <code>null</code>.
   */
  public DefaultEntityResolver (@NonNull final URL aBaseURL)
  {
    this (aBaseURL.toExternalForm ());
  }

  /**
   * Constructor with a base URI.
   *
   * @param sBaseURI
   *        The base URI. May not be <code>null</code>.
   */
  public DefaultEntityResolver (@NonNull final String sBaseURI)
  {
    m_sBaseURI = ValueEnforcer.notNull (sBaseURI, "BaseURI");
  }

  /**
   * @return The base URI from the constructor. Never <code>null</code>.
   * @since 9.2.0
   */
  @NonNull
  public final String getBaseURI ()
  {
    return m_sBaseURI;
  }

  /**
   * @return A mutable copy of the set of remote URL schemes (all lower case, e.g. "http") that are
   *         allowed to be resolved. Empty by default, meaning that only local (class path or
   *         <code>file</code> based) resources may be resolved. Never <code>null</code>.
   * @since 12.4.1
   */
  @NonNull
  @ReturnsMutableCopy
  public final ICommonsSet <String> getAllAllowedRemoteSchemes ()
  {
    return m_aAllowedRemoteSchemes.getClone ();
  }

  /**
   * Set the remote URL schemes that are allowed to be resolved. By default no remote scheme is
   * allowed, to prevent Server Side Request Forgery (SSRF) via an external entity. Local resources
   * (class path or <code>file</code> based) are always resolved regardless of this setting.
   *
   * @param aAllowedRemoteSchemes
   *        The remote schemes to allow (e.g. "http", "https"). May be <code>null</code> or empty to
   *        deny all remote schemes.
   * @return this for chaining
   * @since 12.4.1
   */
  @NonNull
  public final DefaultEntityResolver setAllowedRemoteSchemes (@Nullable final String... aAllowedRemoteSchemes)
  {
    m_aAllowedRemoteSchemes.clear ();
    if (aAllowedRemoteSchemes != null)
      for (final String sScheme : aAllowedRemoteSchemes)
        if (StringHelper.isNotEmpty (sScheme))
          m_aAllowedRemoteSchemes.add (sScheme.toLowerCase (Locale.ROOT));
    return this;
  }

  /** {@inheritDoc} */
  @Nullable
  public InputSource resolveEntity (@Nullable final String sPublicID,
                                    @Nullable final String sSystemID) throws SAXException, IOException
  {
    final IReadableResource aResolvedRes = DefaultResourceResolver.getResolvedResource (sSystemID, m_sBaseURI);
    if (!XMLResourceSchemeHelper.isResourceAccessAllowed (aResolvedRes, m_aAllowedRemoteSchemes))
    {
      LOGGER.warn ("Blocked resolution of entity '" +
                   sSystemID +
                   "' (base '" +
                   m_sBaseURI +
                   "') because its URL scheme is not in the list of allowed remote schemes " +
                   m_aAllowedRemoteSchemes);
      /*
       * Deliberately not "null": a parser treats "null" as "not handled" and opens the system ID
       * itself. An empty document ends the resolution instead.
       */
      return InputSourceFactory.create ("");
    }
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
  public static DefaultEntityResolver createOnDemand (@NonNull final IReadableResource aBaseResource)
  {
    final URL aURL = aBaseResource.getAsURL ();
    return aURL == null ? null : new DefaultEntityResolver (aURL);
  }
}
