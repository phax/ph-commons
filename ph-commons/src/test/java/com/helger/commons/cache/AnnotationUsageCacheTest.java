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
package com.helger.commons.cache;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.style.UseDirectEqualsAndHashCode;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.commons.collection.map.LRUMap;
import com.helger.commons.collection.map.LRUSet;
import com.helger.commons.collection.map.LoggingLRUMap;
import com.helger.commons.concurrent.ExecutorServiceHelper;
import com.helger.commons.timing.StopWatch;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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
                                                                       Nonnull.class,
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
}
