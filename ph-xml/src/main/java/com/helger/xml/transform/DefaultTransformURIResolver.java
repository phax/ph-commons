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
package com.helger.xml.transform;

import java.util.Locale;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsSet;
import com.helger.io.resource.IReadableResource;
import com.helger.io.resourceresolver.DefaultResourceResolver;
import com.helger.xml.XMLResourceSchemeHelper;
import com.helger.xml.ls.SimpleLSResourceResolver;

/**
 * Implementation of the {@link javax.xml.transform.URIResolver} interface using
 * {@link SimpleLSResourceResolver} to resolve resources.
 *
 * @author Philip Helger
 */
public class DefaultTransformURIResolver extends AbstractTransformURIResolver
{
  private static final Logger LOGGER = LoggerFactory.getLogger (DefaultTransformURIResolver.class);

  private String m_sDefaultBase;
  // Remote schemes that are explicitly allowed for resolution. Empty by default
  // to prevent SSRF via XSLT document()/xsl:import/xsl:include.
  private final ICommonsSet <String> m_aAllowedRemoteSchemes = new CommonsHashSet <> ();

  /**
   * Default constructor.
   */
  public DefaultTransformURIResolver ()
  {}

  /**
   * Constructor with a wrapped URI resolver as fallback.
   *
   * @param aWrappedURIResolver
   *        The wrapped URI resolver. May be <code>null</code>.
   */
  public DefaultTransformURIResolver (@Nullable final URIResolver aWrappedURIResolver)
  {
    super (aWrappedURIResolver);
  }

  /**
   * @return The default base to be used, if none is present (if <code>null</code> or "") in the
   *         resolve request. Is <code>null</code> by default.
   * @since 9.1.5
   */
  @Nullable
  public final String getDefaultBase ()
  {
    return m_sDefaultBase;
  }

  /**
   * Set the default base to be used, if none is present.
   *
   * @param sDefaultBase
   *        The default base to be used. May be <code>null</code> or empty to indicate that no
   *        special default base is needed.
   * @return this for chaining
   * @since 9.1.5
   */
  @NonNull
  public final DefaultTransformURIResolver setDefaultBase (@Nullable final String sDefaultBase)
  {
    m_sDefaultBase = sDefaultBase;
    return this;
  }

  /**
   * @return A mutable copy of the set of remote URL schemes (all lower case, e.g. "http") that are
   *         allowed to be resolved. Empty by default, meaning that only local (class path or
   *         <code>file</code> based) resources may be resolved. Never <code>null</code>.
   * @since 12.3.2
   */
  @NonNull
  @ReturnsMutableCopy
  public final ICommonsSet <String> getAllAllowedRemoteSchemes ()
  {
    return m_aAllowedRemoteSchemes.getClone ();
  }

  /**
   * Set the remote URL schemes that are allowed to be resolved. By default no remote scheme is
   * allowed, to prevent Server Side Request Forgery (SSRF) via XSLT <code>document()</code>,
   * <code>xsl:import</code> or <code>xsl:include</code> referencing remote locations. Local
   * resources (class path or <code>file</code> based) are always resolved regardless of this
   * setting.
   *
   * @param aAllowedRemoteSchemes
   *        The remote schemes to allow (e.g. "http", "https"). May be <code>null</code> or empty to
   *        deny all remote schemes.
   * @return this for chaining
   * @since 12.3.2
   */
  @NonNull
  public final DefaultTransformURIResolver setAllowedRemoteSchemes (@Nullable final String... aAllowedRemoteSchemes)
  {
    m_aAllowedRemoteSchemes.clear ();
    if (aAllowedRemoteSchemes != null)
      for (final String sScheme : aAllowedRemoteSchemes)
        if (StringHelper.isNotEmpty (sScheme))
          m_aAllowedRemoteSchemes.add (sScheme.toLowerCase (Locale.ROOT));
    return this;
  }

  @Override
  @Nullable
  protected Source internalResolve (final String sHref, final String sBase) throws TransformerException
  {
    final String sRealBase;
    if (StringHelper.isNotEmpty (sBase))
    {
      // A base was provided - use it
      sRealBase = sBase;
    }
    else
    {
      final String sStr = m_sDefaultBase;
      if (StringHelper.isNotEmpty (sStr))
      {
        // No base provided but a default base present - use default base
        sRealBase = m_sDefaultBase;
      }
      else
      {
        // Neither nor - leave as is
        sRealBase = sBase;
      }
    }

    try
    {
      final IReadableResource aRes = DefaultResourceResolver.getResolvedResource (sHref, sRealBase);
      // Check the scheme before calling exists() as the latter may already open a
      // remote connection (SSRF)
      if (!XMLResourceSchemeHelper.isResourceAccessAllowed (aRes, m_aAllowedRemoteSchemes))
      {
        LOGGER.warn ("Blocked resolution of resource '" +
                     sHref +
                     "' (base '" +
                     sRealBase +
                     "') because its URL scheme is not in the list of allowed remote schemes " +
                     m_aAllowedRemoteSchemes);
        return null;
      }
      if (aRes.exists ())
        return TransformSourceFactory.create (aRes);
    }
    catch (final RuntimeException ex)
    {
      throw new TransformerException (sHref + "//" + sBase + "//" + sRealBase, ex);
    }

    // Nothing to resolve
    return null;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .appendIfNotNull ("DefaultBase", m_sDefaultBase)
                            .getToString ();
  }
}
