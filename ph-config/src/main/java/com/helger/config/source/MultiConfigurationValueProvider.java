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
package com.helger.config.source;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.classloader.ClassLoaderHelper;
import com.helger.base.clone.ICloneable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsSet;
import com.helger.config.value.ConfiguredValue;
import com.helger.config.value.IConfigurationValueProvider;
import com.helger.config.value.IConfigurationValueProviderWithPriorityCallback;
import com.helger.io.resource.FileSystemResource;

/**
 * An implementation of {@link IConfigurationValueProvider} that supports multiple sources, ordered
 * by priority, descending.
 *
 * @author Philip Helger
 */
public class MultiConfigurationValueProvider implements
                                             IConfigurationValueProvider,
                                             ICloneable <MultiConfigurationValueProvider>
{
  private static final class ConfigValueProviderWithPrio
  {
    private final IConfigurationValueProvider m_aCVP;
    private final int m_nPriority;

    public ConfigValueProviderWithPrio (@NonNull final IConfigurationValueProvider aCVP, final int nPrio)
    {
      m_aCVP = aCVP;
      m_nPriority = nPrio;
    }

    @Override
    public String toString ()
    {
      return new ToStringGenerator (null).append ("ConfigValProvider", m_aCVP)
                                         .append ("Priority", m_nPriority)
                                         .getToString ();
    }
  }

  public static final boolean DEFAULT_USE_ONLY_INTIIALIZED_CONFIG_SOURCES = true;

  private static final Logger LOGGER = LoggerFactory.getLogger (MultiConfigurationValueProvider.class);

  private final ICommonsList <ConfigValueProviderWithPrio> m_aSources = new CommonsArrayList <> ();
  private boolean m_bUseOnlyInitializedConfigSources = DEFAULT_USE_ONLY_INTIIALIZED_CONFIG_SOURCES;

  /**
   * Default constructor without any configuration source.
   */
  public MultiConfigurationValueProvider ()
  {}

  /**
   * Constructor with a list of existing configuration sources.
   *
   * @param aSources
   *        The list of existing sources to be added. The order will be maintained. May be
   *        <code>null</code> but may not contain <code>null</code> values.
   * @see #addConfigurationSource(IConfigurationSource)
   */
  public MultiConfigurationValueProvider (@Nullable final List <? extends IConfigurationSource> aSources)
  {
    if (aSources != null)
      for (final IConfigurationSource aSource : aSources)
        addConfigurationSource (aSource);
  }

  /**
   * Constructor with an array of existing configuration sources.
   *
   * @param aSources
   *        The array of existing sources to be added. The order will be maintained. May be
   *        <code>null</code> but may not contain <code>null</code> values.
   * @see #addConfigurationSource(IConfigurationSource)
   */
  public MultiConfigurationValueProvider (@Nullable final IConfigurationSource... aSources)
  {
    if (aSources != null)
      for (final IConfigurationSource aSource : aSources)
        addConfigurationSource (aSource);
  }

  /**
   * @return <code>true</code> if this multi value provider accepts only configuration sources that
   *         were initialized and are usable. The default is
   *         {@value #DEFAULT_USE_ONLY_INTIIALIZED_CONFIG_SOURCES}.
   * @see IConfigurationSource#isInitializedAndUsable()
   */
  public final boolean isUseOnlyInitializedConfigSources ()
  {
    return m_bUseOnlyInitializedConfigSources;
  }

  /**
   * Enable or disable the usage of only initialized configuration sources.
   *
   * @param bUseOnlyInitializedConfigSources
   *        <code>true</code> to only allow the usage of initialized configuration sources.
   * @return this for chaining
   */
  @NonNull
  public final MultiConfigurationValueProvider setUseOnlyInitializedConfigSources (final boolean bUseOnlyInitializedConfigSources)
  {
    m_bUseOnlyInitializedConfigSources = bUseOnlyInitializedConfigSources;
    return this;
  }

  /**
   * Add a configuration source. The priority of the configuration source is used.
   *
   * @param aSource
   *        The source to be added. May not be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final MultiConfigurationValueProvider addConfigurationSource (@NonNull final IConfigurationSource aSource)
  {
    ValueEnforcer.notNull (aSource, "ConfigSource");
    if (m_bUseOnlyInitializedConfigSources && !aSource.isInitializedAndUsable ())
    {
      // Don't add
      LOGGER.warn ("Not adding the configuration source " + aSource + " because it is not yet initialized");
      return this;
    }
    return addConfigurationSource (aSource, aSource.getPriority ());
  }

  /**
   * Add a configuration value provider and a priority. The passed priority overwrites the priority
   * contained in the value provider. This method sorts the internal list of sources based on the
   * registered priorities.
   *
   * @param aCVP
   *        The configuration value provider to be added. May be <code>null</code>.
   * @param nPriority
   *        The priority to be used.
   * @return this for chaining
   */
  @NonNull
  public final MultiConfigurationValueProvider addConfigurationSource (@Nullable final IConfigurationValueProvider aCVP,
                                                                       final int nPriority)
  {
    if (aCVP != null)
    {
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("Adding configuration source " + aCVP + " with priority " + nPriority);

      m_aSources.add (new ConfigValueProviderWithPrio (aCVP, nPriority));
      // Ensure entry with highest priority comes first
      m_aSources.sort ( (x, y) -> y.m_nPriority - x.m_nPriority);
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
  public ConfiguredValue getConfigurationValue (@NonNull @Nonempty final String sKey)
  {
    if (LOGGER.isTraceEnabled ())
      LOGGER.trace ("Trying to resolve configuration value of key '" +
                    sKey +
                    "' in " +
                    m_aSources.size () +
                    " sources");

    ConfiguredValue ret = null;
    for (final ConfigValueProviderWithPrio aSource : m_aSources)
    {
      ret = aSource.m_aCVP.getConfigurationValue (sKey);
      if (ret != null)
      {
        // Use the first one that is not null
        break;
      }
    }

    if (ret != null)
    {
      if (LOGGER.isTraceEnabled ())
        LOGGER.trace ("Successfully resolved configuration value of key '" +
                      sKey +
                      "' to '" +
                      ret.getValue () +
                      "' from " +
                      ret.getConfigurationSource ().getSourceType () +
                      " with prio " +
                      ret.getConfigurationSource ().getPriority ());
    }
    else
    {
      if (LOGGER.isTraceEnabled ())
        LOGGER.trace ("Failed to resolve configuration value of key '" + sKey + "'");
    }

    return ret;
  }

  /**
   * Scan through all added configuration sources and invoke the callback.
   *
   * @param aCallback
   *        The callback to be invoked. May not be <code>null</code>.
   */
  public void forEachConfigurationValueProvider (@NonNull final IConfigurationValueProviderWithPriorityCallback aCallback)
  {
    ValueEnforcer.notNull (aCallback, "aCallback");

    for (final ConfigValueProviderWithPrio aSource : m_aSources)
      aCallback.onConfigurationValueProvider (aSource.m_aCVP, aSource.m_nPriority);
  }

  /**
   * Return a deep clone of this {@link MultiConfigurationValueProvider}. If the contained
   * {@link IConfigurationValueProvider} implements {@link ICloneable} it is cloned as well.
   */
  @NonNull
  @ReturnsMutableCopy
  public MultiConfigurationValueProvider getClone ()
  {
    final MultiConfigurationValueProvider ret = new MultiConfigurationValueProvider ();
    for (final ConfigValueProviderWithPrio aSource : m_aSources)
    {
      if (aSource.m_aCVP instanceof final ICloneable <?> aCloneable)
      {
        final IConfigurationValueProvider aCVPClone = (IConfigurationValueProvider) aCloneable.getClone ();
        ret.m_aSources.add (new ConfigValueProviderWithPrio (aCVPClone, aSource.m_nPriority));
      }
      else
        ret.m_aSources.add (aSource);
    }
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Sources", m_aSources)
                                       .append ("UseOnlyInitializedConfigSources", m_bUseOnlyInitializedConfigSources)
                                       .getToString ();
  }

  /**
   * Load all classpath elements with the same name.
   *
   * @param aClassLoader
   *        The class loader to be used. May not be <code>null</code>.
   * @param sClassPathElement
   *        The name of the class path element(s) to load. May not be <code>null</code>.
   * @param aLoader
   *        The loader that converts all matching URLs to {@link IConfigurationSource} objects. With
   *        this implementation you can differentiate the type of the content.
   * @return May be <code>null</code> if no resource was found.
   */
  @Nullable
  public static MultiConfigurationValueProvider createForClassPath (@NonNull final ClassLoader aClassLoader,
                                                                    @NonNull final String sClassPathElement,
                                                                    @NonNull final Function <URL, IConfigurationSource> aLoader)
  {
    return createForAllOccurrances (aClassLoader, sClassPathElement, aLoader, false);
  }

  /**
   * Load all classpath elements and files with the same name.
   *
   * @param aClassLoader
   *        The class loader to be used. May not be <code>null</code>.
   * @param sPathName
   *        The name of the class path element(s) to load. May not be <code>null</code>.
   * @param aLoader
   *        The loader that converts all matching URLs to {@link IConfigurationSource} objects. With
   *        this implementation you can differentiate the type of the content.
   * @param bCheckForFile
   *        <code>true</code> to also check for a file with the same name.
   * @return May be <code>null</code> if no resource was found.
   */
  @Nullable
  public static MultiConfigurationValueProvider createForAllOccurrances (@NonNull final ClassLoader aClassLoader,
                                                                         @NonNull final String sPathName,
                                                                         @NonNull final Function <URL, IConfigurationSource> aLoader,
                                                                         final boolean bCheckForFile)
  {
    ValueEnforcer.notNull (aClassLoader, "ClassLoader");
    ValueEnforcer.notNull (sPathName, "ClassPathElement");
    ValueEnforcer.notNull (aLoader, "Loader");

    final ICommonsSet <String> aUsedURLs = new CommonsHashSet <> ();
    final MultiConfigurationValueProvider ret = new MultiConfigurationValueProvider ();
    try
    {
      final Enumeration <URL> aEnum = ClassLoaderHelper.getResources (aClassLoader, sPathName);
      while (aEnum.hasMoreElements ())
      {
        final URL aURL = aEnum.nextElement ();
        if (!aUsedURLs.add (aURL.toExternalForm ()))
        {
          LOGGER.warn ("Ignoring duplicate configuration source URL '" + aURL.toExternalForm () + "'");
          continue;
        }
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("Try to load configuration source from '" + aURL.toExternalForm () + "'");

        final IConfigurationSource aConfigSource = aLoader.apply (aURL);
        if (aConfigSource == null)
          throw new IllegalStateException ("Failed to load configration source '" + aURL.toExternalForm () + "'");

        ret.addConfigurationSource (aConfigSource);
      }
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
    if (bCheckForFile)
    {
      // Check for file system as well
      final FileSystemResource aRes = new FileSystemResource (sPathName);
      if (aRes.exists ())
      {
        final URL aURL = aRes.getAsURL ();
        if (!aUsedURLs.add (aURL.toExternalForm ()))
        {
          LOGGER.warn ("Ignoring duplicate configuration source URL '" + aURL.toExternalForm () + "'");
        }
        else
        {
          if (LOGGER.isDebugEnabled ())
            LOGGER.debug ("Try to load configuration source from '" + aURL.toExternalForm () + "'");

          final IConfigurationSource aConfigSource = aLoader.apply (aURL);
          if (aConfigSource == null)
            throw new IllegalStateException ("Failed to load configration source '" + aURL.toExternalForm () + "'");
          ret.addConfigurationSource (aConfigSource);
        }
      }
    }
    if (ret.getConfigurationSourceCount () == 0)
    {
      // Avoid returning an empty object
      return null;
    }
    return ret;
  }
}
