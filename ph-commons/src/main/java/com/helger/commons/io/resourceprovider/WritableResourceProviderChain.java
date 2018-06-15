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
package com.helger.commons.io.resourceprovider;

import java.io.OutputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.EAppend;
import com.helger.commons.io.resource.IWritableResource;
import com.helger.commons.string.ToStringGenerator;

/**
 * A resource provider chain implementation of {@link IWritableResourceProvider}
 * .
 *
 * @author Philip Helger
 */
@Immutable
public class WritableResourceProviderChain extends ReadableResourceProviderChain implements IWritableResourceProvider
{
  protected final ICommonsList <IWritableResourceProvider> m_aWritableResourceProviders = new CommonsArrayList <> ();

  public WritableResourceProviderChain (@Nonnull final IReadableResourceProvider... aResProviders)
  {
    super (aResProviders);

    for (final IReadableResourceProvider aResProvider : aResProviders)
      if (aResProvider instanceof IWritableResourceProvider)
        m_aWritableResourceProviders.add ((IWritableResourceProvider) aResProvider);
    if (m_aWritableResourceProviders.isEmpty ())
      throw new IllegalArgumentException ("No writable resource provider passed - use a ReadableResourceProviderChain");
  }

  public WritableResourceProviderChain (@Nonnull final Iterable <? extends IReadableResourceProvider> aResProviders)
  {
    super (aResProviders);

    for (final IReadableResourceProvider aResProvider : aResProviders)
      if (aResProvider instanceof IWritableResourceProvider)
        m_aWritableResourceProviders.add ((IWritableResourceProvider) aResProvider);
    if (m_aWritableResourceProviders.isEmpty ())
      throw new IllegalArgumentException ("No writable resource provider passed - use a ReadableResourceProviderChain");
  }

  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public ICommonsList <IWritableResourceProvider> getAllContainedWritingResourceProviders ()
  {
    return m_aWritableResourceProviders.getClone ();
  }

  public final boolean supportsWriting (@Nullable final String sName)
  {
    // Check if any provider can handle this resource
    return m_aWritableResourceProviders.containsAny (x -> x.supportsWriting (sName));
  }

  @Nonnull
  @OverrideOnDemand
  public IWritableResource getWritableResource (@Nonnull final String sName)
  {
    // Use the first resource provider that supports the name
    for (final IWritableResourceProvider aResProvider : m_aWritableResourceProviders)
      if (aResProvider.supportsWriting (sName))
        return aResProvider.getWritableResource (sName);
    throw new IllegalArgumentException ("Cannot handle writing '" +
                                        sName +
                                        "' by any of " +
                                        m_aReadingResourceProviders);
  }

  @Override
  @Nullable
  public OutputStream getOutputStream (@Nonnull final String sName, @Nonnull final EAppend eAppend)
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
