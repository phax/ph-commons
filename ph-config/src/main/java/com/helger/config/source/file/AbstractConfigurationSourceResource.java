/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.config.source.file;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.string.ToStringGenerator;
import com.helger.config.source.AbstractConfigurationSource;
import com.helger.config.source.EConfigSourceType;
import com.helger.config.source.IConfigurationSource;

/**
 * Abstract implementation of {@link IConfigurationSource} for file based
 * configuration sources.
 *
 * @author Philip Helger
 */
@Immutable
public abstract class AbstractConfigurationSourceResource extends AbstractConfigurationSource
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AbstractConfigurationSourceResource.class);
  public static final EConfigSourceType SOURCE_TYPE = EConfigSourceType.RESOURCE;

  private final IReadableResource m_aRes;

  protected AbstractConfigurationSourceResource (final int nPriority, @Nonnull final IReadableResource aRes)
  {
    super (SOURCE_TYPE, nPriority);
    ValueEnforcer.notNull (aRes, "File");
    m_aRes = aRes;

    final File aFile = aRes.getAsFile ();
    if (aFile != null)
    {
      if (aFile.isFile ())
      {
        if (!aFile.canRead ())
          LOGGER.warn ("The configuration file '" + aFile.getAbsolutePath () + "' exists, but is not readable");
      }
      else
      {
        // Non existing files are okay
        if (aFile.isDirectory ())
          LOGGER.warn ("The configuration file '" + aFile.getAbsolutePath () + "' exists, but is a directory");
      }
    }
  }

  /**
   * @return The resource as passed in the constructor. Never <code>null</code>.
   */
  @Nonnull
  public final IReadableResource getResource ()
  {
    return m_aRes;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final AbstractConfigurationSourceResource rhs = (AbstractConfigurationSourceResource) o;
    return EqualsHelper.equals (m_aRes, rhs.m_aRes);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aRes).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("File", m_aRes).getToString ();
  }
}
