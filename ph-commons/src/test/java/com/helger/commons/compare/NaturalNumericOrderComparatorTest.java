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
package com.helger.commons.compare;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

/**
 * Test class for class {@link NaturalNumericOrderComparator}.
 *
 * @author Philip Helger
 */
public final class NaturalNumericOrderComparatorTest
{
  @Test
  public void testCaseSensitive ()
  {
    final ICommonsList <String> aData = new CommonsArrayList <> ("1a", "3a", "2a", "2A");
    aData.sort (new NaturalNumericOrderComparator (String::compareTo));
    // "A" before "a"
    assertEquals (new CommonsArrayList <> ("1a", "2A", "2a", "3a"), aData);
  }

  @Test
  public void testCaseInsensitive ()
  {
    final ICommonsList <String> aData = new CommonsArrayList <> ("2a", "02A", "1a");
    aData.sort (new NaturalNumericOrderComparator (String::compareToIgnoreCase));
    assertEquals (new CommonsArrayList <> ("1a", "02A", "2a"), aData);
  }
}
