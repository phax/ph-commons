/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class for class {@link FilterInverted}
 * 
 * @author Philip Helger
 */
public final class FilterInvertedTest
{
  @Test
  public void testAll ()
  {
    try
    {
      new FilterInverted <Object> (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    IFilter <String> aFilter = new FilterInverted <String> (FilterNotNull.<String> getInstance ());
    assertNotNull (aFilter);
    assertTrue (aFilter.matchesFilter (null));
    assertFalse (aFilter.matchesFilter (""));
    assertFalse (aFilter.matchesFilter ("bla bla bla"));

    aFilter = new FilterInverted <String> (FilterNull.<String> getInstance ());
    assertNotNull (aFilter);
    assertFalse (aFilter.matchesFilter (null));
    assertTrue (aFilter.matchesFilter (""));
    assertTrue (aFilter.matchesFilter ("bla bla bla"));
  }
}
