/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.cache.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.convert.UnidirectionalConverterMapGet;

/**
 * Test class for class {@link CacheWithConversionAndMaxSize}.
 *
 * @author Philip Helger
 */
public final class CacheWithConversionAndMaxSizeTest
{
  @Test
  public void testAll ()
  {
    final Map <String, Integer> aMap = CollectionHelper.newMap (new String [] { "In", "In2" },
                                                               new Integer [] { Integer.valueOf (1),
                                                                               Integer.valueOf (2) });
    final CacheWithConversionAndMaxSize <String, Integer> aCache = new CacheWithConversionAndMaxSize <String, Integer> ("test",
                                                                                                                                    1);
    assertEquals ("test", aCache.getName ());
    assertEquals (1, aCache.getMaxSize ());
    // Get from map
    assertEquals (Integer.valueOf (1),
                  aCache.getFromCache ("In", new UnidirectionalConverterMapGet <String, Integer> (aMap)));
    // Use cached value
    assertEquals (Integer.valueOf (1),
                  aCache.getFromCache ("In", new UnidirectionalConverterMapGet <String, Integer> (aMap)));
    // Use cached value
    assertEquals (Integer.valueOf (1), aCache.getFromCache ("In"));
    // No such cached value
    assertNull (aCache.getFromCache ("Gibts Ned"));
    try
    {
      // Cannot convert the passed key!
      aCache.getFromCache ("Gibts Ned", new UnidirectionalConverterMapGet <String, Integer> (aMap));
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
    // No such cached value
    assertNull (aCache.getFromCache ("Gibts Ned"));

    // Overwrite the item with a new one, therefore kicking the old one

    // Get from map
    assertEquals (Integer.valueOf (2),
                  aCache.getFromCache ("In2", new UnidirectionalConverterMapGet <String, Integer> (aMap)));
    // Use cached value
    assertEquals (Integer.valueOf (2),
                  aCache.getFromCache ("In2", new UnidirectionalConverterMapGet <String, Integer> (aMap)));
    // Use cached value
    assertEquals (Integer.valueOf (2), aCache.getFromCache ("In2"));
    // No longer in the cache
    assertNull (aCache.getFromCache ("In"));

    try
    {
      new CacheWithConversionAndMaxSize <String, Integer> ("test", 0);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}
  }
}
