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
package com.helger.config.source;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.function.Function;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.lang.ClassLoaderHelper;

/**
 * An implementation of {@link IConfigurationValueProvider} that supports
 * multiple sources, ordered by priority, descending.
 *
 * @author Philip Helger
 */
public class MultiConfigurationSourceValueProvider implements IConfigurationValueProvider
{
  private final ICommonsList <IConfigurationSource> m_aSources = new CommonsArrayList <> ();

  public MultiConfigurationSourceValueProvider ()
  {}

  public MultiConfigurationSourceValueProvider (@Nullable final Iterable <? extends IConfigurationSource> aSources)
  {
    if (aSources != null)
      for (final IConfigurationSource aSource : aSources)
        addConfigurationSource (aSource);
  }

  public MultiConfigurationSourceValueProvider (@Nullable final IConfigurationSource... aSources)
  {
    if (aSources != null)
      for (final IConfigurationSource aSource : aSources)
        addConfigurationSource (aSource);
  }

  @Nonnull
  public final MultiConfigurationSourceValueProvider addConfigurationSource (@Nonnull final IConfigurationSource aSource)
  {
    ValueEnforcer.notNull (aSource, "ConfigSource");
    m_aSources.add (aSource);
    // Ensure entry with highest priority comes first
    m_aSources.sort ( (x, y) -> y.getPriority () - x.getPriority ());
    return this;
  }

  /**
   * @return A copy of all configuration sources in the order they are executed.
   *         Never <code>null</code> but maybe empty.
   */
  @Nonnull
  public final ICommonsList <IConfigurationSource> getAllSources ()
  {
    return m_aSources.getClone ();
  }

  @Nonnegative
  public final int getSourceCount ()
  {
    return m_aSources.size ();
  }

  @Nullable
  public String getConfigurationValue (@Nonnull @Nonempty final String sKey)
  {
    String ret = null;
    for (final IConfigurationSource aSource : m_aSources)
    {
      ret = aSource.getConfigurationValue (sKey);
      if (ret != null)
      {
        // Use the first one that is not null
        break;
      }
    }
    return ret;
  }

  /**
   * Load all classpath elements with the same name.
   *
   * @param aClassLoader
   *        The class loader to be used. May not be <code>null</code>.
   * @param sClassPathElement
   *        The name of the class path element(s) to load. May not be
   *        <code>null</code>.
   * @param aLoader
   *        The loader that converts all matching URLs to
   *        {@link IConfigurationSource} objects. With this implementation you
   *        can differentiate the type of the content.
   * @return A non-<code>null</code> but may empty
   *         {@link MultiConfigurationSourceValueProvider}.
   */
  @Nonnull
  public static MultiConfigurationSourceValueProvider createForClassPath (@Nonnull final ClassLoader aClassLoader,
                                                                          @Nonnull final String sClassPathElement,
                                                                          @Nonnull final Function <URL, IConfigurationSource> aLoader)
  {
    ValueEnforcer.notNull (aClassLoader, "ClassLoader");
    ValueEnforcer.notNull (sClassPathElement, "ClassPathElement");
    ValueEnforcer.notNull (aLoader, "Loader");

    final MultiConfigurationSourceValueProvider ret = new MultiConfigurationSourceValueProvider ();
    try
    {
      final Enumeration <URL> aEnum = ClassLoaderHelper.getResources (aClassLoader, sClassPathElement);
      while (aEnum.hasMoreElements ())
      {
        final URL aURL = aEnum.nextElement ();

        final IConfigurationSource aConfigSource = aLoader.apply (aURL);
        if (aConfigSource == null)
          throw new IllegalStateException ("Failed to load configration source " + aURL);
        ret.addConfigurationSource (aConfigSource);
      }
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
    return ret;
  }
}
