/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PropertyKey;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.locale.LocaleHelper;
import com.helger.commons.statistics.IMutableStatisticsHandlerKeyedCounter;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.text.resourcebundle.ResourceBundleHelper;

/**
 * Text resolving class that performs the fallback handling for locales other
 * than German and English. Used only from within the
 * {@link DefaultTextResolver} static class.
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

  private static final Logger s_aLogger = LoggerFactory.getLogger (EnumTextResolverWithPropertiesOverrideAndFallback.class);
  private static final IMutableStatisticsHandlerKeyedCounter s_aStatsFailed = StatisticsManager.getKeyedCounterHandler (EnumTextResolverWithPropertiesOverrideAndFallback.class.getName () +
                                                                                                                        "$failed");

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private final Set <String> m_aUsedOverrideBundles = new HashSet <String> ();
  @GuardedBy ("m_aRWLock")
  private final Set <String> m_aUsedFallbackBundles = new HashSet <String> ();
  @GuardedBy ("m_aRWLock")
  private boolean m_bUseResourceBundleCache = DEFAULT_USE_RESOURCE_BUNDLE_CACHE;
  @GuardedBy ("m_aRWLock")
  private final Map <String, ResourceBundle> m_aResourceBundleCache = new HashMap <String, ResourceBundle> ();

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
   * @return <code>true</code> if the internal {@link ResourceBundle} cache
   *         should be used. The default value is
   *         {@link #DEFAULT_USE_RESOURCE_BUNDLE_CACHE}.
   */
  public boolean isUseResourceBundleCache ()
  {
    return m_aRWLock.readLocked ( () -> m_bUseResourceBundleCache);
  }

  /**
   * Get the cached {@link ResourceBundle}. It is assumed, that the locale name
   * is contained within the bundle name!!
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
    ResourceBundle ret = m_aRWLock.readLocked ( () -> {
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
      ret = m_aRWLock.writeLocked ( () -> {
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

    s_aStatsFailed.increment (PREFIX_FALLBACK + aContentLocale.toString () + ':' + sID);
    if (GlobalDebug.isDebugMode ())
    {
      s_aLogger.warn ("getFallbackString (" + sID + "; " + aContentLocale.toString () + ") failed!");

      // Return consistent results
      if (false)
        return "[fallback-" + sID.substring (sID.lastIndexOf ('.') + 1) + "-" + aContentLocale.toString () + "]";
    }
    return null;
  }

  /**
   * @return A set with all resource keys used in overriding. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllUsedOverrideBundleNames ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newSet (m_aUsedOverrideBundles));
  }

  /**
   * @return A set with all resource keys used as fallback. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllUsedFallbackBundleNames ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newSet (m_aUsedFallbackBundles));
  }

  public void clearCache ()
  {
    m_aRWLock.writeLocked ( () -> {
      ResourceBundleHelper.clearCache ();
      m_aUsedOverrideBundles.clear ();
      m_aUsedFallbackBundles.clear ();
      m_aResourceBundleCache.clear ();
    });

    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Cache was cleared: " + getClass ().getName ());
  }
}
