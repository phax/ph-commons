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
package com.helger.commons.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.Singleton;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.lru.LRUMap;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.dimension.SizeInt;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.statistics.IMutableStatisticsHandlerCache;
import com.helger.commons.statistics.StatisticsManager;

/**
 * This service class is used to cache information about images. It is used to
 * set the HTML attributes width and height for images. It has an internal cache
 * to avoid querying the data every time.
 *
 * @author Philip Helger
 */
@ThreadSafe
@Singleton
public final class ImageDataManager
{
  private static final class SingletonHolder
  {
    static final ImageDataManager s_aInstance = new ImageDataManager (1000);
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (ImageDataManager.class);
  private static final IMutableStatisticsHandlerCache s_aStatsHdl = StatisticsManager.getCacheHandler (ImageDataManager.class);
  private static boolean s_bDefaultInstantiated = false;

  private final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  // The main cache
  @GuardedBy ("m_aRWLock")
  private final Map <IReadableResource, SizeInt> m_aImageData;
  // A cache for all non-existing resources to avoid checking them over and over
  // again
  @GuardedBy ("m_aRWLock")
  private final Set <IReadableResource> m_aNonExistingResources = new HashSet <IReadableResource> ();

  private ImageDataManager (@Nonnegative final int nMaxCacheSize)
  {
    m_aImageData = new LRUMap <IReadableResource, SizeInt> (nMaxCacheSize);
  }

  public static boolean isInstantiated ()
  {
    return s_bDefaultInstantiated;
  }

  @Nonnull
  public static ImageDataManager getInstance ()
  {
    final ImageDataManager ret = SingletonHolder.s_aInstance;
    s_bDefaultInstantiated = true;
    return ret;
  }

  @Nullable
  private static SizeInt _readImageData (@Nonnull final IHasInputStream aRes)
  {
    SizeInt aData = null;
    InputStream aIS = null;
    try
    {
      aIS = aRes.getInputStream ();
      if (aIS != null)
      {
        // Returned image may be null in case it is not a valid image!
        // Happens e.g. if a file with the extension ".png" is not a PNG image
        final BufferedImage aImage = ImageIO.read (aIS);
        if (aImage != null)
          aData = new SizeInt (aImage.getWidth (), aImage.getHeight ());
        else
          s_aLogger.warn ("Does not seem to be an image resource: " + aRes);
      }
      else
        s_aLogger.warn ("Failed to resolve image resource: " + aRes);
    }
    catch (final UnsatisfiedLinkError ex)
    {
      // Happened on Ubuntu where library libmawt.so is not present
      s_aLogger.error ("Seems like no AWT binding is present", ex);
    }
    catch (final NoClassDefFoundError ex)
    {
      // Happened on Ubuntu where library libmawt.so is not present in a
      // follow-up call after the java.lang.UnsatisfiedLinkError error
      // occurred
      s_aLogger.error ("Seems like no AWT binding is present", ex);
    }
    catch (final IIOException ex)
    {
      s_aLogger.error ("Failed to interprete image data from resource " + aRes + ": " + ex.getMessage ());
    }
    catch (final IOException ex)
    {
      s_aLogger.error ("Failed to read image data from resource " + aRes, ex);
    }
    catch (final AccessControlException ex)
    {
      // can be thrown by f.exist!
      s_aLogger.error ("Whatsoever on " + aRes, ex);
    }
    catch (final IllegalArgumentException ex)
    {
      // can be thrown by the BMP reader :)
      s_aLogger.error ("Failed to read image data from resource " + aRes + ": " + ex.getMessage ());
    }
    finally
    {
      // ImageIO.read doesn't close the stream!
      StreamHelper.close (aIS);
    }
    return aData;
  }

  @Nullable
  public SizeInt getImageSize (@Nullable final IReadableResource aRes)
  {
    if (aRes == null)
      return null;

    /*
     * Use containsKey here instead of "get () != null" in case an image is
     * queried over and over but is not existing. The implementation inserts
     * null values for all elements that are invalid
     */
    m_aRWLock.readLock ().lock ();
    try
    {
      // Valid image data?
      final SizeInt aData = m_aImageData.get (aRes);
      if (aData != null)
      {
        s_aStatsHdl.cacheHit ();
        return aData;
      }

      // Known non-existing image data?
      if (m_aNonExistingResources.contains (aRes))
      {
        s_aStatsHdl.cacheHit ();
        return null;
      }
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }

    // Main read data outside of lock!
    final SizeInt aData = _readImageData (aRes);

    return m_aRWLock.writeLocked ( () -> {
      // In case the image is invalid (why-so-ever), remember a null value
      if (aData == null)
        m_aNonExistingResources.add (aRes);
      else
        m_aImageData.put (aRes, aData);
      s_aStatsHdl.cacheMiss ();
      return aData;
    });
  }

  /**
   * Remove a single resource from the cache.
   *
   * @param aRes
   *        The resource to be removed. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public EChange clearCachedSize (@Nullable final IReadableResource aRes)
  {
    if (aRes == null)
      return EChange.UNCHANGED;

    return m_aRWLock.writeLocked ( () -> {
      // Existing resource?
      if (m_aImageData.remove (aRes) != null)
        return EChange.CHANGED;

      // Non-existing resource?
      if (m_aNonExistingResources.remove (aRes))
        return EChange.CHANGED;

      return EChange.UNCHANGED;
    });
  }

  /**
   * Remove all cached elements
   *
   * @return {@link EChange} - never null
   */
  @Nonnull
  public EChange clearCache ()
  {
    return m_aRWLock.writeLocked ( () -> {
      if (m_aImageData.isEmpty () && m_aNonExistingResources.isEmpty ())
        return EChange.UNCHANGED;

      m_aImageData.clear ();
      m_aNonExistingResources.clear ();

      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Cache was cleared: " + ImageDataManager.class.getName ());
      return EChange.CHANGED;
    });
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <IReadableResource, SizeInt> getAllCachedSizes ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newMap (m_aImageData));
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <IReadableResource> getAllNotExistingResources ()
  {
    return m_aRWLock.readLocked ( () -> CollectionHelper.newSet (m_aNonExistingResources));
  }
}
