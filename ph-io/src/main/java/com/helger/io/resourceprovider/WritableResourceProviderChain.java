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
package com.helger.io.resourceprovider;

import java.io.OutputStream;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.io.EAppend;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.io.resource.IWritableResource;

/**
 * A resource provider chain implementation of {@link IWritableResourceProvider} .
 *
 * @author Philip Helger
 */
@Immutable
public class WritableResourceProviderChain extends ReadableResourceProviderChain implements IWritableResourceProvider
{
  protected final ICommonsList <IWritableResourceProvider> m_aWritableResourceProviders = new CommonsArrayList <> ();

  /**
   * Constructor with a varargs array of resource providers.
   *
   * @param aResProviders
   *        The resource providers to use. May neither be <code>null</code> nor
   *        empty. At least one must implement {@link IWritableResourceProvider}.
   */
  public WritableResourceProviderChain (@NonNull final IReadableResourceProvider... aResProviders)
  {
    super (aResProviders);

    for (final IReadableResourceProvider aResProvider : aResProviders)
      if (aResProvider instanceof final IWritableResourceProvider aWRP)
        m_aWritableResourceProviders.add (aWRP);

    if (m_aWritableResourceProviders.isEmpty ())
      throw new IllegalArgumentException ("No writable resource provider passed - use a ReadableResourceProviderChain");
  }

  /**
   * Constructor with an iterable of resource providers.
   *
   * @param aResProviders
   *        The resource providers to use. May neither be <code>null</code> nor
   *        empty. At least one must implement {@link IWritableResourceProvider}.
   */
  public WritableResourceProviderChain (@NonNull final Iterable <? extends IReadableResourceProvider> aResProviders)
  {
    super (aResProviders);

    for (final IReadableResourceProvider aResProvider : aResProviders)
      if (aResProvider instanceof final IWritableResourceProvider aWRP)
        m_aWritableResourceProviders.add (aWRP);

    if (m_aWritableResourceProviders.isEmpty ())
      throw new IllegalArgumentException ("No writable resource provider passed - use a ReadableResourceProviderChain");
  }

  /**
   * @return A copy of all contained writable resource providers. Never
   *         <code>null</code>.
   */
  @NonNull
  @Nonempty
  @ReturnsMutableCopy
  public ICommonsList <IWritableResourceProvider> getAllContainedWritingResourceProviders ()
  {
    return m_aWritableResourceProviders.getClone ();
  }

  /**
   * {@inheritDoc}
   */
  public final boolean supportsWriting (@Nullable final String sName)
  {
    // Check if any provider can handle this resource
    return m_aWritableResourceProviders.containsAny (x -> x.supportsWriting (sName));
  }

  /**
   * {@inheritDoc}
   */
  @NonNull
  @OverrideOnDemand
  public IWritableResource getWritableResource (@NonNull final String sName)
  {
    // Use the first resource provider that supports the name
    for (final IWritableResourceProvider aResProvider : m_aWritableResourceProviders)
      if (aResProvider.supportsWriting (sName))
        return aResProvider.getWritableResource (sName);
    throw new IllegalArgumentException ("Cannot handle writing '" +
                                        sName +
                                        "' by any of " +
                                        m_aWritableResourceProviders);
  }

  /**
   * Get a writable resource matching the name and filter.
   *
   * @param sName
   *        The name of the resource to resolve. May not be <code>null</code>.
   * @param aReturnFilter
   *        The filter to apply on found resources. May not be
   *        <code>null</code>.
   * @return <code>null</code> if no matching resource was found.
   */
  @Nullable
  public IWritableResource getWritableResourceIf (@NonNull final String sName,
                                                  @NonNull final Predicate <? super IWritableResource> aReturnFilter)
  {
    // Use the first resource provider that supports the name
    for (final IWritableResourceProvider aResProvider : m_aWritableResourceProviders)
      if (aResProvider.supportsWriting (sName))
      {
        final IWritableResource aRes = aResProvider.getWritableResource (sName);
        if (aReturnFilter.test (aRes))
          return aRes;
      }
    return null;
  }

  @Override
  @Nullable
  public OutputStream getOutputStream (@NonNull final String sName, @NonNull final EAppend eAppend)
  {
    // Use the first resource provider that supports the name and creates a
    // valid output stream
    for (final IWritableResourceProvider aResProvider : m_aWritableResourceProviders)
    {
      final OutputStream aOS = aResProvider.getOutputStream (sName, eAppend);
      if (aOS != null)
        return aOS;
    }
    return null;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final WritableResourceProviderChain rhs = (WritableResourceProviderChain) o;
    return m_aWritableResourceProviders.equals (rhs.m_aWritableResourceProviders);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aWritableResourceProviders).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("writableResProviders", m_aWritableResourceProviders)
                            .getToString ();
  }
}
