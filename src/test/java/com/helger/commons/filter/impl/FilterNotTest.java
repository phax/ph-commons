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
package com.helger.commons.filter.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.filter.IFilter;
import com.helger.commons.filter.impl.FilterNot;
import com.helger.commons.filter.impl.FilterNotNull;
import com.helger.commons.filter.impl.FilterNull;

/**
 * Test class for class {@link FilterNot}
 *
 * @author Philip Helger
 */
public final class FilterNotTest
{
  @Test
  public void testAll ()
  {
    try
    {
      new FilterNot <Object> (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    IFilter <String> aFilter = new FilterNot <String> (new FilterNotNull <String> ());
    assertNotNull (aFilter);
    assertTrue (aFilter.matchesFilter (null));
    assertFalse (aFilter.matchesFilter (""));
    assertFalse (aFilter.matchesFilter ("bla bla bla"));

    aFilter = new FilterNot <String> (new FilterNull <String> ());
    assertNotNull (aFilter);
    assertFalse (aFilter.matchesFilter (null));
    assertTrue (aFilter.matchesFilter (""));
    assertTrue (aFilter.matchesFilter ("bla bla bla"));
  }
}
