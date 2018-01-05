/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.io.stream.NonBlockingStringReader;
import com.helger.commons.string.StringHelper;

/**
 * Test class for class {@link CSVIterator}.
 *
 * @author OpenCSV
 */
public final class CSVIteratorTest
{
  private static final List <String> STRINGS = CollectionHelper.newList ("test1", "test2");

  private CSVReader m_aMockReader;

  @Before
  public void setUp ()
  {
    m_aMockReader = new CSVReader (new NonBlockingStringReader (StringHelper.getImploded (',', STRINGS)));
  }

  @Test (expected = UnsupportedOperationException.class)
  public void removethrowsUnsupportedOperationException () throws IOException
  {
    final CSVIterator aIterator = new CSVIterator (m_aMockReader);
    aIterator.remove ();
  }

  @Test
  public void initialReadReturnsStrings () throws IOException
  {
    final CSVIterator aIterator = new CSVIterator (m_aMockReader);
    assertEquals (STRINGS, aIterator.next ());
  }

  @Test
  public void hasNextWorks () throws IOException
  {
    // initial read from constructor
    final CSVIterator aIterator = new CSVIterator (m_aMockReader);
    assertTrue (aIterator.hasNext ());
    assertNotNull (aIterator.next ());
    assertFalse (aIterator.hasNext ());
  }
}
