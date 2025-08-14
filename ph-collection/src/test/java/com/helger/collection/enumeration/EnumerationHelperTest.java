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
package com.helger.collection.enumeration;

import static com.helger.collection.enumeration.EnumerationHelper.getEnumeration;
import static com.helger.collection.enumeration.EnumerationHelper.isEmpty;
import static com.helger.collection.helper.CollectionHelperExt.newList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.helger.collection.base.EmptyEnumeration;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.iterator.IteratorHelper;

/**
 * Test class for class {@link IteratorHelper}.
 *
 * @author Philip Helger
 */
public final class EnumerationHelperTest
{
  @Test
  public void testIsEmpty_Enumeration ()
  {
    assertTrue (isEmpty ((Enumeration <?>) null));
    assertTrue (isEmpty (getEnumeration (new CommonsArrayList <> ())));
    assertTrue (isEmpty (new EmptyEnumeration <> ()));
    assertFalse (isEmpty (getEnumeration (newList ("any"))));
  }

  @Test
  public void testGetEnumeratorFromIterator ()
  {
    final List <String> aList = newList ("d", "c", "b", "a");
    Enumeration <String> aEnum = getEnumeration (aList.iterator ());
    assertTrue (aEnum.hasMoreElements ());
    assertEquals ("d", aEnum.nextElement ());
    assertTrue (aEnum.hasMoreElements ());
    assertEquals ("c", aEnum.nextElement ());
    assertTrue (aEnum.hasMoreElements ());
    assertEquals ("b", aEnum.nextElement ());
    assertTrue (aEnum.hasMoreElements ());
    assertEquals ("a", aEnum.nextElement ());
    assertFalse (aEnum.hasMoreElements ());
    assertFalse (aEnum.hasMoreElements ());

    aEnum = getEnumeration ((Iterator <String>) null);
    assertFalse (aEnum.hasMoreElements ());
  }
}
