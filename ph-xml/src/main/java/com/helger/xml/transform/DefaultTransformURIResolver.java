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
package com.helger.xml.transform;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resourceresolver.DefaultResourceResolver;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.xml.ls.SimpleLSResourceResolver;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Implementation of the {@link javax.xml.transform.URIResolver} interface using
 * {@link SimpleLSResourceResolver} to resolve resources.
 *
 * @author Philip Helger
 */
public class DefaultTransformURIResolver extends AbstractTransformURIResolver
{
  private String m_sDefaultBase;

  public DefaultTransformURIResolver ()
  {
    super ();
  }

  public DefaultTransformURIResolver (@Nullable final URIResolver aWrappedURIResolver)
  {
    super (aWrappedURIResolver);
  }

  /**
   * @return The default base to be used, if none is present (if
   *         <code>null</code> or "") in the resolve request. Is
   *         <code>null</code> by default.
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
   *        The default base to be used. May be <code>null</code> or empty to
   *        indicate that no special default base is needed.
   * @return this for chaining
   * @since 9.1.5
   */
  @Nonnull
  public final DefaultTransformURIResolver setDefaultBase (@Nullable final String sDefaultBase)
  {
    m_sDefaultBase = sDefaultBase;
    return this;
  }

  @Override
  @Nullable
  protected Source internalResolve (final String sHref, final String sBase) throws TransformerException
  {
    final String sRealBase;
    if (StringHelper.hasText (sBase))
    {
      // A base was provided - use it
      sRealBase = sBase;
    }
    else
      if (StringHelper.hasText (m_sDefaultBase))
      {
        // No base provided but a default base present - use default base
        sRealBase = m_sDefaultBase;
      }
      else
      {
        // Neither nor - leave as is
        sRealBase = sBase;
      }

    try
    {
      final IReadableResource aRes = DefaultResourceResolver.getResolvedResource (sHref, sRealBase);
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
