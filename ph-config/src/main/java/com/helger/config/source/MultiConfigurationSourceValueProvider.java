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
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
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

  @FunctionalInterface
  public interface ICVPWithPriorityCallback
  {
    void onConfigurationSource (@Nonnull IConfigurationValueProvider aCVP, int nPriority);
  }

  public static final boolean DEFAULT_USE_ONLY_INTIIALIZED_CONFIG_SOURCES = true;

  private final ICommonsList <CS> m_aSources = new CommonsArrayList <> ();
  private boolean m_bUseOnlyInitializedConfigSources = DEFAULT_USE_ONLY_INTIIALIZED_CONFIG_SOURCES;

  /**
   * Default constructor without any configuration source.
   */
  public MultiConfigurationSourceValueProvider ()
  {}

  /**
   * Constructor with a list of existing configuration sources.
   *
   * @param aSources
   *        The list of existing sources to be added. The order will be
   *        maintained. May be <code>null</code> but may not contain
   *        <code>null</code> values.
   * @see #addConfigurationSource(IConfigurationSource)
   */
  public MultiConfigurationSourceValueProvider (@Nullable final List <? extends IConfigurationSource> aSources)
  {
    if (aSources != null)
      for (final IConfigurationSource aSource : aSources)
        addConfigurationSource (aSource);
  }

  /**
   * Constructor with an array of existing configuration sources.
   *
   * @param aSources
   *        The array of existing sources to be added. The order will be
   *        maintained. May be <code>null</code> but may not contain
   *        <code>null</code> values.
   * @see #addConfigurationSource(IConfigurationSource)
   */
  public MultiConfigurationSourceValueProvider (@Nullable final IConfigurationSource... aSources)
  {
    if (aSources != null)
      for (final IConfigurationSource aSource : aSources)
        addConfigurationSource (aSource);
  }

  public final boolean isUseOnlyInitializedConfigSources ()
  {
    return m_bUseOnlyInitializedConfigSources;
  }

  @Nonnull
  public final MultiConfigurationSourceValueProvider setUseOnlyInitializedConfigSources (final boolean bUseOnlyInitializedConfigSources)
  {
    m_bUseOnlyInitializedConfigSources = bUseOnlyInitializedConfigSources;
    return this;
  }

  /**
   * Add a configuration source.
   *
   * @param aSource
   *        The source to be added. May not be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public final MultiConfigurationSourceValueProvider addConfigurationSource (@Nonnull final IConfigurationSource aSource)
  {
    ValueEnforcer.notNull (aSource, "ConfigSource");

    if (m_bUseOnlyInitializedConfigSources && !aSource.isInitializedAndUsable ())
    {
      // Don't add
      return this;
    }

    return addConfigurationSource (aSource, aSource.getPriority ());
  }

  /**
   * Add a configuration value provider and a priority. This method sorts the
   * list of sources
   *
   * @param aCVP
   *        The configuration value provider to be added. May be
   *        <code>null</code>.
   * @param nPriority
   *        The priority to be used.
   * @return this for chaining
   */
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

  /**
   * @return The number of contained configuration sources. Always &ge; 0.
   */
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

  /**
   * Try to find the configuration value provider that delivers the provided
   * key.
   *
   * @param sKey
   *        The key to search. May not be <code>null</code>.
   * @return <code>null</code> if the key cannot be resolved.
   */
  @Nullable
  public IConfigurationValueProvider getConfigurationValueProvider (@Nonnull @Nonempty final String sKey)
  {
    for (final CS aSource : m_aSources)
    {
      if (aSource.m_aCVP.getConfigurationValue (sKey) != null)
      {
        // Use the first one that is not null
        return aSource.m_aCVP;
      }
    }
    return null;
  }

  /**
   * Scan through all added configuration sources and invoke the callback.
   *
   * @param aCallback
   *        The callback to be invoked. May not be <code>null</code>.
   */
  public void forEachConfigurationValueProvider (@Nonnull final ICVPWithPriorityCallback aCallback)
  {
    ValueEnforcer.notNull (aCallback, "aCallback");

    for (final CS aSource : m_aSources)
      aCallback.onConfigurationSource (aSource.m_aCVP, aSource.m_nPrio);
  }

  /**
   * Return a deep clone of this {@link MultiConfigurationSourceValueProvider}.
   * If the contained {@link IConfigurationValueProvider} implements
   * {@link ICloneable} it is cloned as well.
   */
  @Nonnull
  @ReturnsMutableCopy
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
    {
      // Avoid returning an empty object
      return null;
    }

    return ret;
  }
}
