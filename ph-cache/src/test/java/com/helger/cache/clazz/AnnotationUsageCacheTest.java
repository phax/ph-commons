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
package com.helger.cache.clazz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.style.UseDirectEqualsAndHashCode;
import com.helger.base.concurrent.ExecutorServiceHelper;
import com.helger.base.timing.StopWatch;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.map.LRUMap;
import com.helger.collection.map.LRUSet;
import com.helger.collection.map.LoggingLRUMap;

/**
 * Test class for class {@link AnnotationUsageCache}.
 *
 * @author Philip Helger
 */
public final class AnnotationUsageCacheTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AnnotationUsageCacheTest.class);

  @Test
  public void testConcurrency ()
  {
    final AnnotationUsageCache a = new AnnotationUsageCache (UseDirectEqualsAndHashCode.class);
    // Arbitrary list of classes
    // Contains 3 classes that has this annotation
    final ICommonsList <Class <?>> aClasses = new CommonsArrayList <> (String.class,
                                                                       Byte.class,
                                                                       Character.class,
                                                                       Double.class,
                                                                       Float.class,
                                                                       Integer.class,
                                                                       Long.class,
                                                                       Short.class,
                                                                       StringBuilder.class,
                                                                       Nullable.class,
                                                                       NonNull.class,
                                                                       LRUMap.class,
                                                                       LRUSet.class,
                                                                       LoggingLRUMap.class);
    final StopWatch aSW = StopWatch.createdStarted ();
    LOGGER.info (aClasses.size () + " classes");
    final ExecutorService e = Executors.newCachedThreadPool ();
    final AtomicInteger aCounter = new AtomicInteger (0);
    final int nMaxOuter = 1_000_000;
    final int nMaxInner = 10;
    for (int i = 0; i < nMaxOuter; ++i)
      e.submit ( () -> {
        try
        {
          for (int j = 0; j < nMaxInner; ++j)
            for (final Class <?> c : aClasses)
            {
              if (a.hasAnnotation (c))
                aCounter.incrementAndGet ();
            }
        }
        catch (final Throwable ex)
        {
          LOGGER.error ("Bla", ex);
        }
      });
    ExecutorServiceHelper.shutdownAndWaitUntilAllTasksAreFinished (e);
    LOGGER.info ("done after " + aSW.stopAndGetMillis () + " millis.");
    assertEquals (3 * nMaxOuter * nMaxInner, aCounter.intValue ());
  }

  @Test
  public void testGetAnnotationClass ()
  {
    final AnnotationUsageCache a = new AnnotationUsageCache (UseDirectEqualsAndHashCode.class);
    assertSame (UseDirectEqualsAndHashCode.class, a.getAnnotationClass ());
  }

  @Test
  public void testHasAnnotationByClass ()
  {
    final AnnotationUsageCache a = new AnnotationUsageCache (UseDirectEqualsAndHashCode.class);
    assertTrue (a.hasAnnotation (LRUMap.class));
    assertFalse (a.hasAnnotation (String.class));

    // Second call should be a cache hit
    assertTrue (a.hasAnnotation (LRUMap.class));
    assertFalse (a.hasAnnotation (String.class));
  }

  @Test
  public void testHasAnnotationByObject ()
  {
    final AnnotationUsageCache a = new AnnotationUsageCache (UseDirectEqualsAndHashCode.class);
    assertFalse (a.hasAnnotation ("hello"));
    assertFalse (a.hasAnnotation (Integer.valueOf (1)));
  }

  @Test
  public void testSetAnnotation ()
  {
    final AnnotationUsageCache a = new AnnotationUsageCache (UseDirectEqualsAndHashCode.class);
    // Override the annotation status
    a.setAnnotation (String.class, true);
    assertTrue (a.hasAnnotation (String.class));

    a.setAnnotation (String.class, false);
    assertFalse (a.hasAnnotation (String.class));
  }

  @Test
  public void testClearCache ()
  {
    final AnnotationUsageCache a = new AnnotationUsageCache (UseDirectEqualsAndHashCode.class);
    a.hasAnnotation (String.class);
    a.hasAnnotation (LRUMap.class);
    a.clearCache ();
    // After clear, values should be re-determined
    assertTrue (a.hasAnnotation (LRUMap.class));
    assertFalse (a.hasAnnotation (String.class));
  }

  @Test
  public void testInvalidRetentionPolicy ()
  {
    try
    {
      // SuppressWarnings has CLASS retention, not RUNTIME
      new AnnotationUsageCache (SuppressWarnings.class);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Retention (RetentionPolicy.SOURCE)
  private @interface SourceAnnotation
  {
    // empty
  }

  @Test
  public void testSourceRetentionPolicy ()
  {
    try
    {
      new AnnotationUsageCache (SourceAnnotation.class);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testToString ()
  {
    final AnnotationUsageCache a = new AnnotationUsageCache (UseDirectEqualsAndHashCode.class);
    final String s = a.toString ();
    assertNotNull (s);
    assertTrue (s.contains ("UseDirectEqualsAndHashCode"));
  }
}
