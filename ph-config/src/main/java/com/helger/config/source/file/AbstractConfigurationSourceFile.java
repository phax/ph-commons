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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
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
public abstract class AbstractConfigurationSourceFile extends AbstractConfigurationSource
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AbstractConfigurationSourceFile.class);
  public static final EConfigSourceType SOURCE_TYPE = EConfigSourceType.FILE;

  private final File m_aFile;

  protected AbstractConfigurationSourceFile (final int nPriority, @Nonnull final File aFile)
  {
    super (SOURCE_TYPE, nPriority);
    ValueEnforcer.notNull (aFile, "File");
    m_aFile = aFile;
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

  /**
   * @return The file as passed in the constructor. Never <code>null</code>.
   */
  @Nonnull
  public final File getFile ()
  {
    return m_aFile;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final AbstractConfigurationSourceFile rhs = (AbstractConfigurationSourceFile) o;
    return EqualsHelper.equals (m_aFile, rhs.m_aFile);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aFile).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("File", m_aFile).getToString ();
  }
}
