/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.io.resourceprovider;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.OverrideOnDemand;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.io.IReadableResource;
import com.helger.commons.io.IReadableResourceProvider;
import com.helger.commons.string.ToStringGenerator;

/**
 * A readable resource provider that chains multiple readable resource
 * providers.
 * 
 * @author Philip Helger
 */
@Immutable
public class ReadableResourceProviderChain implements IReadableResourceProvider
{
  protected final List <IReadableResourceProvider> m_aReadingResourceProviders;

  public ReadableResourceProviderChain (@Nonnull final IReadableResourceProvider... aResProviders)
  {
    ValueEnforcer.notEmptyNoNullValue (aResProviders, "ResourceProviders");

    m_aReadingResourceProviders = ContainerHelper.newList (aResProviders);
  }

  public ReadableResourceProviderChain (@Nonnull final Iterable <? extends IReadableResourceProvider> aResProviders)
  {
    ValueEnforcer.notEmptyNoNullValue (aResProviders, "ResourceProviders");

    m_aReadingResourceProviders = ContainerHelper.newList (aResProviders);
  }

  public final boolean supportsReading (@Nullable final String sName)
  {
    // Check if any provider can handle this resource
    for (final IReadableResourceProvider aResProvider : m_aReadingResourceProviders)
      if (aResProvider.supportsReading (sName))
        return true;
    return false;
  }

  @Nonnull
  @OverrideOnDemand
  public IReadableResource getReadableResource (@Nonnull final String sName)
  {
    // Use the first resource provider that supports the name
    for (final IReadableResourceProvider aResProvider : m_aReadingResourceProviders)
      if (aResProvider.supportsReading (sName))
        return aResProvider.getReadableResource (sName);
    throw new IllegalArgumentException ("Cannot handle reading '" + sName + "' by " + m_aReadingResourceProviders);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ReadableResourceProviderChain rhs = (ReadableResourceProviderChain) o;
    return m_aReadingResourceProviders.equals (rhs.m_aReadingResourceProviders);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aReadingResourceProviders).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("resProviders", m_aReadingResourceProviders).toString ();
  }
}
