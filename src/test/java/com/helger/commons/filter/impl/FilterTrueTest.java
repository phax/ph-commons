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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.filter.IFilter;
import com.helger.commons.filter.impl.FilterTrue;

/**
 * Test class for class {@link FilterTrue}
 *
 * @author Philip Helger
 */
public final class FilterTrueTest
{
  @Test
  public void testAll ()
  {
    final IFilter <String> aFilter = new FilterTrue <String> ();
    assertNotNull (aFilter);
    assertTrue (aFilter.matchesFilter (null));
    assertTrue (aFilter.matchesFilter (""));
    assertTrue (aFilter.matchesFilter ("bla bla bla"));
  }
}
