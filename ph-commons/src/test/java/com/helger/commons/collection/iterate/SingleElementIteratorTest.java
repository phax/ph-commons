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
package com.helger.commons.collection.iterate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link SingleElementIterator}
 *
 * @author Philip Helger
 */
public final class SingleElementIteratorTest
{
  @Test
  public void testAll ()
  {
    final SingleElementIterator <String> eit = new SingleElementIterator <String> ("any");
    assertTrue (eit.hasNext ());

    assertEquals ("any", eit.next ());
    try
    {
      eit.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}
    assertFalse (eit.hasNext ());

    try
    {
      eit.remove ();
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {}

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new SingleElementIterator <String> ("any"),
                                                                       new SingleElementIterator <String> ("any"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new SingleElementIterator <String> ("any"),
                                                                           new SingleElementIterator <String> ("any2"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new SingleElementIterator <String> ("any"),
                                                                           new SingleElementIterator <Integer> (Integer.valueOf (1)));
  }
}
