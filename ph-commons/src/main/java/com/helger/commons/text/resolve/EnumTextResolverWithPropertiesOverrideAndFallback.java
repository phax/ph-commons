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
package com.helger.commons.text.resolve;

import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.annotation.PropertyKey;
import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.debug.GlobalDebug;
import com.helger.base.statistics.IMutableStatisticsHandlerKeyedCounter;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.locale.LocaleHelper;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.text.resourcebundle.ResourceBundleHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Text resolving class that performs the fallback handling for locales other than German and
 * English. Used only from within the {@link DefaultTextResolver} static class.
 *
 * @author Philip Helger
 */
@ThreadSafe
public class EnumTextResolverWithPropertiesOverrideAndFallback extends AbstractEnumTextResolverWithOverrideAndFallback
{
  /** Default classpath prefix for override resources */
  public static final String PREFIX_OVERRIDE = "properties/override-";
  /** Default classpath prefix for fallback resources */
  public static final String PREFIX_FALLBACK = "properties/";
  /** By default the resource bundle cache is used */
  public static final boolean DEFAULT_USE_RESOURCE_BUNDLE_CACHE = true;

  private static final Logger LOGGER = LoggerFactory.getLogger (EnumTextResolverWithPropertiesOverrideAndFallback.class);
  private static final IMutableStatisticsHandlerKeyedCounter STATS_FAILED = StatisticsManager.getKeyedCounterHandler (EnumTextResolverWithPropertiesOverrideAndFallback.class.getName () +
                                                                                                                      "$failed");

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private final ICommonsSet <String> m_aUsedOverrideBundles = new CommonsHashSet <> ();
  @GuardedBy ("m_aRWLock")
  private final ICommonsSet <String> m_aUsedFallbackBundles = new CommonsHashSet <> ();
  @GuardedBy ("m_aRWLock")
  private boolean m_bUseResourceBundleCache = DEFAULT_USE_RESOURCE_BUNDLE_CACHE;
  @GuardedBy ("m_aRWLock")
  private final ICommonsMap <String, ResourceBundle> m_aResourceBundleCache = new CommonsHashMap <> ();

  public EnumTextResolverWithPropertiesOverrideAndFallback ()
  {}

  /**
   * Change whether the internal resource bundle cache should be used.
   *
   * @param bUseResourceBundleCache
   *        The new value. Pass <code>true</code> to enable it.
   */
  public void setUseResourceBundleCache (final boolean bUseResourceBundleCache)
  {
    m_aRWLock.writeLocked ( () -> m_bUseResourceBundleCache = bUseResourceBundleCache);
  }

  /**
   * @return <code>true</code> if the internal {@link ResourceBundle} cache should be used. The
   *         default value is {@link #DEFAULT_USE_RESOURCE_BUNDLE_CACHE}.
   */
  public boolean isUseResourceBundleCache ()
  {
    return m_aRWLock.readLockedBoolean ( () -> m_bUseResourceBundleCache);
  }

  /**
   * Get the cached {@link ResourceBundle}. It is assumed, that the locale name is contained within
   * the bundle name!!
   *
   * @param sBundleName
   *        Name of the bundle. May not be <code>null</code>.
   * @param aLocale
   *        Locale to use. May not be <code>null</code>.
   * @return <code>null</code> if no such bundle exists
   */
  @Nullable
  private ResourceBundle _getResourceBundle (@Nonnull @Nonempty final String sBundleName, @Nonnull final Locale aLocale)
  {
    ResourceBundle ret = m_aRWLock.readLockedGet ( () -> {
      if (!m_bUseResourceBundleCache)
      {
        // Do not use the cache!
        return ResourceBundleHelper.getResourceBundle (sBundleName, aLocale);
      }
      // Existing cache value? May be null!
      if (m_aResourceBundleCache.containsKey (sBundleName))
        return m_aResourceBundleCache.get (sBundleName);
      return null;
    });
    if (ret == null)
    {
      ret = m_aRWLock.writeLockedGet ( () -> {
        // Re-check in write lock
        if (m_aResourceBundleCache.containsKey (sBundleName))
          return m_aResourceBundleCache.get (sBundleName);

        // Resolve the resource bundle
        final ResourceBundle aWLRB = ResourceBundleHelper.getResourceBundle (sBundleName, aLocale);
        m_aResourceBundleCache.put (sBundleName, aWLRB);
        return aWLRB;
      });
    }
    return ret;
  }

  @Override
  @Nullable
  protected String internalGetOverrideString (@Nonnull @PropertyKey final String sID,
                                              @Nonnull final Locale aContentLocale)
  {
    // Try all possible locales of the passed locale
    for (final Locale aLocale : LocaleHelper.getCalculatedLocaleListForResolving (aContentLocale))
    {
      // Explicitly use a bundle name containing the locale in the base name to
      // avoid strange fallback behaviour to the default locale
      final String sBundleName = PREFIX_OVERRIDE + aLocale.toString ();
      final String ret = ResourceBundleHelper.getString (_getResourceBundle (sBundleName, aLocale), sID);
      if (ret != null)
      {
        // Match!
        m_aRWLock.writeLocked ( () -> m_aUsedOverrideBundles.add (sBundleName));
        return ret;
      }
    }
    return null;
  }

  @Override
  @Nullable
  protected String internalGetFallbackString (@Nonnull @PropertyKey final String sID,
                                              @Nonnull final Locale aContentLocale)
  {
    // Try all possible locales of the passed locale
    for (final Locale aLocale : LocaleHelper.getCalculatedLocaleListForResolving (aContentLocale))
    {
      // Explicitly use a bundle name containing the locale in the base name to
      // avoid strange fallback behaviour to the default locale
      final String sBundleName = PREFIX_FALLBACK + aLocale.toString ();
      final String ret = ResourceBundleHelper.getString (_getResourceBundle (sBundleName, aLocale), sID);
      if (ret != null)
      {
        m_aRWLock.writeLocked ( () -> m_aUsedFallbackBundles.add (sBundleName));
        return ret;
      }
    }
    STATS_FAILED.increment (PREFIX_FALLBACK + aContentLocale.toString () + ':' + sID);
    if (GlobalDebug.isDebugMode ())
    {
      LOGGER.warn ("getFallbackString (" + sID + "; " + aContentLocale.toString () + ") failed!");

      // Return consistent results
      if (false)
        return "[fallback-" + sID.substring (sID.lastIndexOf ('.') + 1) + "-" + aContentLocale.toString () + "]";
    }
    return null;
  }

  /**
   * @return A set with all resource keys used in overriding. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <String> getAllUsedOverrideBundleNames ()
  {
    return m_aRWLock.readLockedGet (m_aUsedOverrideBundles::getClone);
  }

  /**
   * @return A set with all resource keys used as fallback. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <String> getAllUsedFallbackBundleNames ()
  {
    return m_aRWLock.readLockedGet (m_aUsedFallbackBundles::getClone);
  }

  public void clearCache ()
  {
    m_aRWLock.writeLocked ( () -> {
      ResourceBundleHelper.clearCache ();
      m_aUsedOverrideBundles.clear ();
      m_aUsedFallbackBundles.clear ();
      m_aResourceBundleCache.clear ();
    });

    if (LOGGER.isDebugEnabled ())
      LOGGER.debug ("Cache was cleared: " + getClass ().getName ());
  }
}
