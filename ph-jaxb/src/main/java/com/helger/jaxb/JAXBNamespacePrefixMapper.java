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
package com.helger.jaxb;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.NamespaceContext;

import org.glassfish.jaxb.runtime.marshaller.NamespacePrefixMapper;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * A special namespace prefix mapper for the Jakarta XML Binding
 * implementation.<br>
 * Important note: this class works only, if the
 * <code>com.sun.xml.bind:jaxb-impl</code> artifact is in your classpath,
 * because this class extends
 * <code>org.glassfish.jaxb.runtime.marshaller.NamespacePrefixMapper</code>
 * which is not available in the other JAXB implementations.
 *
 * @author Philip Helger
 */
public class JAXBNamespacePrefixMapper extends NamespacePrefixMapper
{
  private final NamespaceContext m_aNC;

  public JAXBNamespacePrefixMapper (@Nonnull final NamespaceContext aNC)
  {
    m_aNC = ValueEnforcer.notNull (aNC, "NamespaceContext");
  }

  @Override
  @Nullable
  public String getPreferredPrefix (@Nonnull final String sNamespaceUri,
                                    @Nullable final String sSuggestion,
                                    final boolean bRequirePrefix)
  {
    final String sPrefix = m_aNC.getPrefix (sNamespaceUri);
    if (sPrefix != null)
      return sPrefix;

    // Use suggestion
    return sSuggestion;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("NamespaceContext", m_aNC).getToString ();
  }
}
