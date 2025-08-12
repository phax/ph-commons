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
package com.helger.commons.io.resourceprovider;

import java.io.InputStream;
import java.util.function.Predicate;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.string.ToStringGenerator;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A readable resource provider that chains multiple
 * {@link IReadableResourceProvider}.
 *
 * @author Philip Helger
 */
@Immutable
public class ReadableResourceProviderChain implements IReadableResourceProvider
{
  private final ICommonsList <IReadableResourceProvider> m_aReadingResourceProviders;

  public ReadableResourceProviderChain (@Nonnull final IReadableResourceProvider... aResProviders)
  {
    ValueEnforcer.notEmptyNoNullValue (aResProviders, "ResourceProviders");

    m_aReadingResourceProviders = new CommonsArrayList <> (aResProviders);
  }

  public ReadableResourceProviderChain (@Nonnull final Iterable <? extends IReadableResourceProvider> aResProviders)
  {
    ValueEnforcer.notEmptyNoNullValue (aResProviders, "ResourceProviders");

    m_aReadingResourceProviders = new CommonsArrayList <> (aResProviders);
  }

  @Nonnull
  @ReturnsMutableObject
  protected final ICommonsList <IReadableResourceProvider> readingResourceProviders ()
  {
    return m_aReadingResourceProviders;
  }

  @Nonnull
  @ReturnsMutableCopy
  public final ICommonsList <IReadableResourceProvider> getAllContainedReadingResourceProviders ()
  {
    return m_aReadingResourceProviders.getClone ();
  }

  public final boolean supportsReading (@Nullable final String sName)
  {
    // Check if any provider can handle this resource
    return m_aReadingResourceProviders.containsAny (x -> x.supportsReading (sName));
  }

  @Nonnull
  @OverrideOnDemand
  public IReadableResource getReadableResource (@Nonnull final String sName)
  {
    // Use the first resource provider that supports the name
    for (final IReadableResourceProvider aResProvider : m_aReadingResourceProviders)
      if (aResProvider.supportsReading (sName))
        return aResProvider.getReadableResource (sName);
    throw new IllegalArgumentException ("Cannot handle reading '" + sName + "' by any of " + m_aReadingResourceProviders);
  }

  @Nullable
  public IReadableResource getReadableResourceIf (@Nonnull final String sName,
                                                  @Nonnull final Predicate <? super IReadableResource> aReturnFilter)
  {
    // Use the first resource provider that supports the name
    for (final IReadableResourceProvider aResProvider : m_aReadingResourceProviders)
      if (aResProvider.supportsReading (sName))
      {
        final IReadableResource aRes = aResProvider.getReadableResource (sName);
        if (aReturnFilter.test (aRes))
          return aRes;
      }
    return null;
  }

  @Override
  @Nullable
  public InputStream getInputStream (@Nonnull final String sName)
  {
    // Use the first resource provider that supports the name and returns a
    // non-null resource provider
    for (final IReadableResourceProvider aResProvider : m_aReadingResourceProviders)
    {
      final InputStream aIS = aResProvider.getInputStream (sName);
      if (aIS != null)
        return aIS;
    }
    return null;
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
    return new ToStringGenerator (this).append ("readableResProviders", m_aReadingResourceProviders).getToString ();
  }
}
