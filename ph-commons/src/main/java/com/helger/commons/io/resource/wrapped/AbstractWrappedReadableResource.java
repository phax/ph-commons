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
package com.helger.commons.io.resource.wrapped;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.string.ToStringGenerator;

/**
 * A wrapper around a readable resource for {@link InputStream} manipulation .
 *
 * @author Philip Helger
 */
public abstract class AbstractWrappedReadableResource implements IWrappedReadableResource
{
  private final IReadableResource m_aBaseResource;

  public AbstractWrappedReadableResource (@Nonnull final IReadableResource aBaseResource)
  {
    m_aBaseResource = ValueEnforcer.notNull (aBaseResource, "BaseResource");
  }

  @Nonnull
  public IReadableResource getWrappedReadableResource ()
  {
    return m_aBaseResource;
  }

  @Nonnull
  public String getResourceID ()
  {
    return m_aBaseResource.getResourceID ();
  }

  @Nonnull
  public String getPath ()
  {
    return m_aBaseResource.getPath ();
  }

  public boolean exists ()
  {
    return m_aBaseResource.exists ();
  }

  @Nullable
  public URL getAsURL ()
  {
    return m_aBaseResource.getAsURL ();
  }

  @Nullable
  public File getAsFile ()
  {
    return m_aBaseResource.getAsFile ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("BaseResource", m_aBaseResource).getToString ();
  }
}
