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

  private CSVIterator m_aIterator;
  private CSVReader m_aMockReader;

  @Before
  public void setUp () throws IOException
  {
    m_aMockReader = new CSVReader (new NonBlockingStringReader (StringHelper.getImploded (',', STRINGS)));
    m_aIterator = new CSVIterator (m_aMockReader);
  }

  @Test (expected = UnsupportedOperationException.class)
  public void removethrowsUnsupportedOperationException ()
  {
    m_aIterator.remove ();
  }

  @Test
  public void initialReadReturnsStrings ()
  {
    assertEquals (STRINGS, m_aIterator.next ());
  }

  @Test
  public void hasNextWorks ()
  {
    // initial read from constructor
    assertTrue (m_aIterator.hasNext ());
    assertNotNull (m_aIterator.next ());
    assertFalse (m_aIterator.hasNext ());
  }
}
