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
package com.helger.commons.collections.iterate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.Test;

/**
 * Test class for class {@link EmptyEnumeration}.
 * 
 * @author Philip Helger
 */
public final class EmptyEnumerationTest
{
  @Test
  public void testAll ()
  {
    final EmptyEnumeration <String> es = EmptyEnumeration.<String> getInstance ();
    assertFalse (es.hasMoreElements ());
    try
    {
      es.nextElement ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}
    final EmptyEnumeration <Integer> ei = EmptyEnumeration.<Integer> getInstance ();
    assertEquals (es, ei);
    assertFalse (es.equals (null));
    assertFalse (es.equals ("any"));
    assertEquals (es.hashCode (), ei.hashCode ());
    assertEquals (es.toString (), ei.toString ());
  }
}
