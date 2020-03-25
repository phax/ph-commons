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
import com.helger.commons.lang.ICloneable;

/**
 * An implementation of {@link IConfigurationValueProvider} that supports
 * multiple sources, ordered by priority, descending.
 *
 * @author Philip Helger
 */
public class MultiConfigurationSourceValueProvider implements
                                                   IConfigurationValueProvider,
                                                   ICloneable <MultiConfigurationSourceValueProvider>
{
  private static final class CS
  {
    private final IConfigurationValueProvider m_aCVP;
    private final int m_nPrio;

    public CS (@Nonnull final IConfigurationValueProvider aCVP, final int nPrio)
    {
      m_aCVP = aCVP;
      m_nPrio = nPrio;
    }
  }

  private final ICommonsList <CS> m_aSources = new CommonsArrayList <> ();

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
    return addConfigurationSource (aSource, aSource.getPriority ());
  }

  @Nonnull
  public final MultiConfigurationSourceValueProvider addConfigurationSource (@Nullable final IConfigurationValueProvider aCVP,
                                                                             final int nPriority)
  {
    if (aCVP != null)
    {
      m_aSources.add (new CS (aCVP, nPriority));
      // Ensure entry with highest priority comes first
      m_aSources.sort ( (x, y) -> y.m_nPrio - x.m_nPrio);
    }
    return this;
  }

  @Nonnegative
  public final int getConfigurationSourceCount ()
  {
    return m_aSources.size ();
  }

  @Nullable
  public String getConfigurationValue (@Nonnull @Nonempty final String sKey)
  {
    String ret = null;
    for (final CS aSource : m_aSources)
    {
      ret = aSource.m_aCVP.getConfigurationValue (sKey);
      if (ret != null)
      {
        // Use the first one that is not null
        break;
      }
    }
    return ret;
  }

  @Nonnull
  public MultiConfigurationSourceValueProvider getClone ()
  {
    final MultiConfigurationSourceValueProvider ret = new MultiConfigurationSourceValueProvider ();
    for (final CS aSource : m_aSources)
    {
      if (aSource.m_aCVP instanceof ICloneable <?>)
      {
        final IConfigurationValueProvider aCVPClone = (IConfigurationValueProvider) ((ICloneable <?>) aSource.m_aCVP).getClone ();
        ret.m_aSources.add (new CS (aCVPClone, aSource.m_nPrio));
      }
      else
        ret.m_aSources.add (aSource);
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
   * @return May be <code>null</code> if no resource was found.
   */
  @Nullable
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
    if (ret.getConfigurationSourceCount () == 0)
      return null;

    return ret;
  }
}
