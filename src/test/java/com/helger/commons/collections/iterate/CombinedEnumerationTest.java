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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Enumeration;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.helger.commons.collections.ContainerHelper;

/**
 * Test class for class {@link CombinedEnumeration}.
 * 
 * @author Philip Helger
 */
public final class CombinedEnumerationTest
{
  @Test
  public void testAll ()
  {
    // both null
    Enumeration <String> es = CombinedEnumeration.create (null, null);
    assertFalse (es.hasMoreElements ());
    try
    {
      es.nextElement ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}

    // both empty
    es = CombinedEnumeration.create (EmptyEnumeration.<String> getInstance (), EmptyEnumeration.<String> getInstance ());
    assertFalse (es.hasMoreElements ());
    try
    {
      es.nextElement ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}

    // one null
    es = CombinedEnumeration.create (ContainerHelper.getEnumeration ("a", "b", "c"), null);
    assertTrue (es.hasMoreElements ());
    assertEquals ("a", es.nextElement ());
    assertTrue (es.hasMoreElements ());
    assertEquals ("b", es.nextElement ());
    assertTrue (es.hasMoreElements ());
    assertEquals ("c", es.nextElement ());
    assertFalse (es.hasMoreElements ());
    try
    {
      es.nextElement ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}

    // one empty
    es = CombinedEnumeration.create (ContainerHelper.getEnumeration ("a", "b", "c"),
                                     EmptyEnumeration.<String> getInstance ());
    assertTrue (es.hasMoreElements ());
    assertEquals ("a", es.nextElement ());
    assertTrue (es.hasMoreElements ());
    assertEquals ("b", es.nextElement ());
    assertTrue (es.hasMoreElements ());
    assertEquals ("c", es.nextElement ());
    assertFalse (es.hasMoreElements ());
    try
    {
      es.nextElement ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}

    // other one null
    es = CombinedEnumeration.create (null, ContainerHelper.getEnumeration ("a", "b", "c"));
    assertTrue (es.hasMoreElements ());
    assertEquals ("a", es.nextElement ());
    assertTrue (es.hasMoreElements ());
    assertEquals ("b", es.nextElement ());
    assertTrue (es.hasMoreElements ());
    assertEquals ("c", es.nextElement ());
    assertFalse (es.hasMoreElements ());
    try
    {
      es.nextElement ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}

    // other one empty
    es = CombinedEnumeration.create (EmptyEnumeration.<String> getInstance (),
                                     ContainerHelper.getEnumeration ("a", "b", "c"));
    assertTrue (es.hasMoreElements ());
    assertEquals ("a", es.nextElement ());
    assertTrue (es.hasMoreElements ());
    assertEquals ("b", es.nextElement ());
    assertTrue (es.hasMoreElements ());
    assertEquals ("c", es.nextElement ());
    assertFalse (es.hasMoreElements ());
    try
    {
      es.nextElement ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}

    // both not null
    es = CombinedEnumeration.create (ContainerHelper.getEnumeration ("a", "b"),
                                     ContainerHelper.getEnumeration ("c", "d"));
    assertTrue (es.hasMoreElements ());
    assertEquals ("a", es.nextElement ());
    assertTrue (es.hasMoreElements ());
    assertEquals ("b", es.nextElement ());
    assertTrue (es.hasMoreElements ());
    assertEquals ("c", es.nextElement ());
    assertTrue (es.hasMoreElements ());
    assertEquals ("d", es.nextElement ());
    assertFalse (es.hasMoreElements ());
    try
    {
      es.nextElement ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}
  }
}
