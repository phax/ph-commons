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
package com.helger.commons.xml.transform;

import javax.annotation.Nullable;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import com.helger.commons.string.ToStringGenerator;

/**
 * Abstract base implementation of the {@link URIResolver} interface.
 *
 * @author Philip Helger
 */
public abstract class AbstractTransformURIResolver implements URIResolver
{
  private final URIResolver m_aWrappedURIResolver;

  public AbstractTransformURIResolver ()
  {
    this (null);
  }

  public AbstractTransformURIResolver (@Nullable final URIResolver aWrappedURIResolver)
  {
    m_aWrappedURIResolver = aWrappedURIResolver;
  }

  /**
   * @return The wrapped {@link URIResolver}. May be <code>null</code>.
   */
  @Nullable
  public final URIResolver getWrappedURIResolver ()
  {
    return m_aWrappedURIResolver;
  }

  @Nullable
  protected abstract Source internalResolve (final String sHref, final String sBase) throws TransformerException;

  @Nullable
  public final Source resolve (final String sHref, final String sBase) throws TransformerException
  {
    final Source aSource = internalResolve (sHref, sBase);
    if (aSource != null)
      return aSource;

    // Nothing found -> call wrapped resolver (if any)
    if (m_aWrappedURIResolver != null)
      return m_aWrappedURIResolver.resolve (sHref, sBase);

    // Nothing to resolve
    return null;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("wrappedURIResolver", m_aWrappedURIResolver).toString ();
  }
}
