/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.http.cache;

import java.util.concurrent.TimeUnit;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.CGlobal;
import com.helger.base.clone.ICloneable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.string.StringImplode;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * This class is used to build the response HTTP header field Cache-Control value in a structured
 * way. This header field is only applicable for HTTP/1.1
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class CacheControlBuilder implements ICloneable <CacheControlBuilder>
{
  private Long m_aMaxAgeSeconds;
  private Long m_aSharedMaxAgeSeconds;
  private boolean m_bPublic = false;
  private boolean m_bPrivate = false;
  private boolean m_bNoCache = false;
  private boolean m_bNoStore = false;
  private boolean m_bNoTransform = false;
  private boolean m_bMustRevalidate = false;
  private boolean m_bProxyRevalidate = false;
  private final ICommonsList <String> m_aExtensions = new CommonsArrayList <> ();

  /**
   * Constructor
   */
  public CacheControlBuilder ()
  {}

  /**
   * Copy constructor
   *
   * @param aBase
   *        The object to copy the settings from. May not be <code>null</code>.
   */
  public CacheControlBuilder (@NonNull final CacheControlBuilder aBase)
  {
    ValueEnforcer.notNull (aBase, "Base");

    m_aMaxAgeSeconds = aBase.m_aMaxAgeSeconds;
    m_aSharedMaxAgeSeconds = aBase.m_aSharedMaxAgeSeconds;
    m_bPublic = aBase.m_bPublic;
    m_bPrivate = aBase.m_bPrivate;
    m_bNoCache = aBase.m_bNoCache;
    m_bNoStore = aBase.m_bNoStore;
    m_bNoTransform = aBase.m_bNoTransform;
    m_bMustRevalidate = aBase.m_bMustRevalidate;
    m_bProxyRevalidate = aBase.m_bProxyRevalidate;
    m_aExtensions.addAll (aBase.m_aExtensions);
  }

  /**
   * Set the maximum age relative to the request time
   *
   * @param eTimeUnit
   *        {@link TimeUnit} to use
   * @param nDuration
   *        The duration in the passed unit
   * @return this
   */
  @NonNull
  public CacheControlBuilder setMaxAge (@NonNull final TimeUnit eTimeUnit, final long nDuration)
  {
    return setMaxAgeSeconds (eTimeUnit.toSeconds (nDuration));
  }

  /**
   * Set the maximum age in days relative to the request time
   *
   * @param nDays
   *        Days to keep it
   * @return this
   */
  @NonNull
  public CacheControlBuilder setMaxAgeDays (@Nonnegative final long nDays)
  {
    return setMaxAgeSeconds (nDays * CGlobal.SECONDS_PER_DAY);
  }

  /**
   * Set the maximum age in hours relative to the request time
   *
   * @param nHours
   *        Hours to keep it
   * @return this
   */
  @NonNull
  public CacheControlBuilder setMaxAgeHours (@Nonnegative final long nHours)
  {
    return setMaxAgeSeconds (nHours * CGlobal.SECONDS_PER_HOUR);
  }

  /**
   * Set the maximum age in minutes relative to the request time
   *
   * @param nMinutes
   *        Minutes to keep it
   * @return this
   */
  @NonNull
  public CacheControlBuilder setMaxAgeMinutes (@Nonnegative final long nMinutes)
  {
    return setMaxAgeSeconds (nMinutes * CGlobal.SECONDS_PER_MINUTE);
  }

  /**
   * Set the maximum age in seconds relative to the request time. Specifies the maximum amount of
   * time that a representation will be considered fresh. Similar to Expires, this directive is
   * relative to the time of the request, rather than absolute. [seconds] is the number of seconds
   * from the time of the request you wish the representation to be fresh for.
   *
   * @param nSeconds
   *        Seconds to keep it
   * @return this
   */
  @NonNull
  public CacheControlBuilder setMaxAgeSeconds (@Nonnegative final long nSeconds)
  {
    ValueEnforcer.isGE0 (nSeconds, "Seconds");
    m_aMaxAgeSeconds = Long.valueOf (nSeconds);
    return this;
  }

  public boolean hasMaxAgeSeconds ()
  {
    return m_aMaxAgeSeconds != null;
  }

  @Nullable
  public Long getMaxAgeSeconds ()
  {
    return m_aMaxAgeSeconds;
  }

  /**
   * Set the maximum age for shared caches relative to the request time. Similar to max-age, except
   * that it only applies to shared (e.g., proxy) caches.
   *
   * @param eTimeUnit
   *        {@link TimeUnit} to use
   * @param nDuration
   *        The duration in the passed unit
   * @return this
   */
  @NonNull
  public CacheControlBuilder setSharedMaxAge (@NonNull final TimeUnit eTimeUnit, final long nDuration)
  {
    return setSharedMaxAgeSeconds (eTimeUnit.toSeconds (nDuration));
  }

  /**
   * Set the maximum age for shared caches in days relative to the request time. Similar to max-age,
   * except that it only applies to shared (e.g., proxy) caches.
   *
   * @param nDays
   *        Days to keep it
   * @return this
   */
  @NonNull
  public CacheControlBuilder setSharedMaxAgeDays (@Nonnegative final long nDays)
  {
    return setSharedMaxAgeSeconds (nDays * CGlobal.SECONDS_PER_DAY);
  }

  /**
   * Set the maximum age for shared caches in hours relative to the request time. Similar to
   * max-age, except that it only applies to shared (e.g., proxy) caches.
   *
   * @param nHours
   *        Hours to keep it
   * @return this
   */
  @NonNull
  public CacheControlBuilder setSharedMaxAgeHours (@Nonnegative final long nHours)
  {
    return setSharedMaxAgeSeconds (nHours * CGlobal.SECONDS_PER_HOUR);
  }

  /**
   * Set the maximum age for shared caches in minutes relative to the request time. Similar to
   * max-age, except that it only applies to shared (e.g., proxy) caches.
   *
   * @param nMinutes
   *        Minutes to keep it
   * @return this
   */
  @NonNull
  public CacheControlBuilder setSharedMaxAgeMinutes (@Nonnegative final long nMinutes)
  {
    return setSharedMaxAgeSeconds (nMinutes * CGlobal.SECONDS_PER_MINUTE);
  }

  /**
   * Set the maximum age for shared caches in seconds relative to the request time. Similar to
   * max-age, except that it only applies to shared (e.g., proxy) caches.
   *
   * @param nSeconds
   *        Seconds to keep it
   * @return this
   */
  @NonNull
  public CacheControlBuilder setSharedMaxAgeSeconds (@Nonnegative final long nSeconds)
  {
    ValueEnforcer.isGE0 (nSeconds, "Seconds");
    m_aSharedMaxAgeSeconds = Long.valueOf (nSeconds);
    return this;
  }

  public boolean hasSharedMaxAgeSeconds ()
  {
    return m_aSharedMaxAgeSeconds != null;
  }

  @Nullable
  public Long getSharedMaxAgeSeconds ()
  {
    return m_aSharedMaxAgeSeconds;
  }

  /**
   * Set the <b>public</b> value. marks authenticated responses as cacheable; normally, if HTTP
   * authentication is required, responses are automatically private.
   *
   * @param bPublic
   *        <code>true</code> to enable public
   * @return this
   */
  @NonNull
  public CacheControlBuilder setPublic (final boolean bPublic)
  {
    m_bPublic = bPublic;
    return this;
  }

  public boolean isPublic ()
  {
    return m_bPublic;
  }

  /**
   * Set the <b>private</b> value. allows caches that are specific to one user (e.g., in a browser)
   * to store the response; shared caches (e.g., in a proxy) may not.
   *
   * @param bPrivate
   *        <code>true</code> to enable private
   * @return this
   */
  @NonNull
  public CacheControlBuilder setPrivate (final boolean bPrivate)
  {
    m_bPrivate = bPrivate;
    return this;
  }

  public boolean isPrivate ()
  {
    return m_bPrivate;
  }

  /**
   * Set the <b>no-cache</b> value. Forces caches to submit the request to the origin server for
   * validation before releasing a cached copy, every time. This is useful to assure that
   * authentication is respected (in combination with public), or to maintain rigid freshness,
   * without sacrificing all of the benefits of caching.
   *
   * @param bNoCache
   *        <code>true</code> to enable no-cache
   * @return this
   */
  @NonNull
  public CacheControlBuilder setNoCache (final boolean bNoCache)
  {
    m_bNoCache = bNoCache;
    return this;
  }

  public boolean isNoCache ()
  {
    return m_bNoCache;
  }

  /**
   * Set the <b>no-store</b> value. Instructs caches not to keep a copy of the representation under
   * any conditions.
   *
   * @param bNoStore
   *        <code>true</code> to enable no-store
   * @return this
   */
  @NonNull
  public CacheControlBuilder setNoStore (final boolean bNoStore)
  {
    m_bNoStore = bNoStore;
    return this;
  }

  public boolean isNoStore ()
  {
    return m_bNoStore;
  }

  /**
   * Set the <b>no-transform</b> value. Implementors of intermediate caches (proxies) have found it
   * useful to convert the media type of certain entity bodies. A non- transparent proxy might, for
   * example, convert between image formats in order to save cache space or to reduce the amount of
   * traffic on a slow link. If a message includes the no-transform directive, an intermediate cache
   * or proxy MUST NOT change those headers that are listed in section 13.5.2 as being subject to
   * the no-transform directive. This implies that the cache or proxy MUST NOT change any aspect of
   * the entity-body that is specified by these headers, including the value of the entity-body
   * itself.
   *
   * @param bNoTransform
   *        <code>true</code> to enable no-transform
   * @return this
   */
  @NonNull
  public CacheControlBuilder setNoTransform (final boolean bNoTransform)
  {
    m_bNoTransform = bNoTransform;
    return this;
  }

  public boolean isNoTransform ()
  {
    return m_bNoTransform;
  }

  /**
   * Set the <b>must-revalidate</b> value. Tells caches that they must obey any freshness
   * information you give them about a representation. HTTP allows caches to serve stale
   * representations under special conditions; by specifying this header, you’re telling the cache
   * that you want it to strictly follow your rules.
   *
   * @param bMustRevalidate
   *        <code>true</code> to enable must-revalidate
   * @return this
   */
  @NonNull
  public CacheControlBuilder setMustRevalidate (final boolean bMustRevalidate)
  {
    m_bMustRevalidate = bMustRevalidate;
    return this;
  }

  public boolean isMustRevalidate ()
  {
    return m_bMustRevalidate;
  }

  /**
   * Set the <b>proxy-revalidate</b> value. Similar to must-revalidate, except that it only applies
   * to proxy caches.
   *
   * @param bProxyRevalidate
   *        <code>true</code> to enable proxy-revalidate
   * @return this
   */
  @NonNull
  public CacheControlBuilder setProxyRevalidate (final boolean bProxyRevalidate)
  {
    m_bProxyRevalidate = bProxyRevalidate;
    return this;
  }

  public boolean isProxyRevalidate ()
  {
    return m_bProxyRevalidate;
  }

  @NonNull
  public CacheControlBuilder addExtension (@NonNull @Nonempty final String sExtension)
  {
    ValueEnforcer.notEmpty (sExtension, "Extension");
    if (sExtension.indexOf (',') >= 0)
      throw new IllegalArgumentException ("Each extension must be added separately: '" + sExtension + "'");
    m_aExtensions.add (sExtension);
    return this;
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllExtensions ()
  {
    return m_aExtensions.getClone ();
  }

  @NonNull
  public String getAsHTTPHeaderValue ()
  {
    final ICommonsList <String> aItems = new CommonsArrayList <> ();
    if (m_aMaxAgeSeconds != null)
      aItems.add ("max-age=" + m_aMaxAgeSeconds.toString ());
    if (m_aSharedMaxAgeSeconds != null)
      aItems.add ("s-maxage=" + m_aSharedMaxAgeSeconds.toString ());
    if (m_bPublic)
      aItems.add ("public");
    if (m_bPrivate)
      aItems.add ("private");
    if (m_bNoCache)
      aItems.add ("no-cache");
    if (m_bNoStore)
      aItems.add ("no-store");
    if (m_bNoTransform)
      aItems.add ("no-transform");
    if (m_bMustRevalidate)
      aItems.add ("must-revalidate");
    if (m_bProxyRevalidate)
      aItems.add ("proxy-revalidate");
    aItems.addAll (m_aExtensions);
    return StringImplode.getImploded (", ", aItems);
  }

  @NonNull
  public CacheControlBuilder getClone ()
  {
    return new CacheControlBuilder (this);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("maxAgeSecs", m_aMaxAgeSeconds)
                                       .appendIfNotNull ("sharedMaxAgeSecs", m_aSharedMaxAgeSeconds)
                                       .append ("public", m_bPublic)
                                       .append ("private", m_bPrivate)
                                       .append ("noCache", m_bNoCache)
                                       .append ("noStore", m_bNoStore)
                                       .append ("noTransform", m_bNoTransform)
                                       .append ("mustRevalidate", m_bMustRevalidate)
                                       .append ("proxyRevalidate", m_bProxyRevalidate)
                                       .append ("extensions", m_aExtensions)
                                       .getToString ();
  }
}
