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

import java.net.MalformedURLException;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.io.resource.IReadableResource;
import com.helger.io.resource.URLResource;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * The URL resource provider.
 *
 * @author Philip Helger
 */
@Immutable
public final class URLResourceProvider implements IReadableResourceProvider
{
  public boolean supportsReading (@Nullable final String sName)
  {
    return URLResource.isExplicitURLResource (sName);
  }

  public IReadableResource getReadableResource (@Nonnull final String sURL)
  {
    ValueEnforcer.notNull (sURL, "URL");

    try
    {
      return new URLResource (sURL);
    }
    catch (final MalformedURLException ex)
    {
      throw new IllegalArgumentException ("Passed name '" + sURL + "' is not a URL!", ex);
    }
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    return true;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).getToString ();
  }
}
