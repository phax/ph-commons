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
package com.helger.commons.collection.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.EmptyStackException;
import java.util.HashSet;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link NonBlockingStack}
 *
 * @author Philip Helger
 */
public final class NonBlockingStackTest
{
  @Test
  public void testCtor ()
  {
    NonBlockingStack <String> st = new NonBlockingStack <> ();
    assertTrue (st.isEmpty ());
    assertFalse (st.iterator ().hasNext ());
    st = new NonBlockingStack <> ("s", "t");
    assertEquals (2, st.size ());
    assertEquals ("s", st.firstElement ());
    assertTrue (st.iterator ().hasNext ());
    st = new NonBlockingStack <> ((String []) null);
    assertTrue (st.isEmpty ());
    assertFalse (st.iterator ().hasNext ());
    st = new NonBlockingStack <> (CollectionHelper.newList ("s", "t"));
    assertEquals (2, st.size ());
    assertEquals ("s", st.firstElement ());
    assertTrue (st.iterator ().hasNext ());
    st = new NonBlockingStack <> (new HashSet <String> ());
    assertTrue (st.isEmpty ());
    assertFalse (st.iterator ().hasNext ());
    st = new NonBlockingStack <> (new NonBlockingStack <> ("s", "t"));
    assertEquals (2, st.size ());
    assertEquals ("s", st.firstElement ());
    assertTrue (st.iterator ().hasNext ());
  }

  @Test
  public void testMethods ()
  {
    final NonBlockingStack <String> aStack = new NonBlockingStack <String> ();
    assertEquals (0, aStack.size ());
    assertTrue (aStack.isEmpty ());
    aStack.push ("a");
    assertEquals (1, aStack.size ());
    assertFalse (aStack.isEmpty ());
    assertFalse (aStack.contains ("b"));
    aStack.push ("b");
    assertTrue (aStack.contains ("b"));
    aStack.pop ();
    assertFalse (aStack.contains ("b"));
    assertEquals (1, aStack.size ());
    assertFalse (aStack.isEmpty ());
    aStack.pop ();
    assertEquals (0, aStack.size ());
    assertTrue (aStack.isEmpty ());
    aStack.clear ();
    aStack.push ("a");
    assertEquals ("a", aStack.firstElement ());
    assertFalse (aStack.isEmpty ());
    aStack.clear ();
    assertTrue (aStack.isEmpty ());
    aStack.push ("a");
    assertEquals ("a", aStack.peek ());
    assertEquals ("a", aStack.top ());
    assertEquals (1, aStack.size ());
    assertEquals ("a", aStack.pop ());
    assertEquals (0, aStack.size ());

    assertEquals ("a", aStack.push ("a"));
    assertEquals (1, aStack.size ());
    assertEquals ("a", aStack.peek ());
    assertEquals ("b", aStack.replaceTopElement ("b"));
    assertEquals (1, aStack.size ());
    assertEquals ("b", aStack.peek ());
    assertEquals ("b", aStack.pop ());
    assertEquals (0, aStack.size ());

    try
    {
      aStack.replaceTopElement ("c");
      fail ();
    }
    catch (final EmptyStackException ex)
    {}
    try
    {
      aStack.peek ();
      fail ();
    }
    catch (final EmptyStackException ex)
    {}
    try
    {
      aStack.top ();
      fail ();
    }
    catch (final EmptyStackException ex)
    {}
    try
    {
      aStack.firstElement ();
      fail ();
    }
    catch (final EmptyStackException ex)
    {}

    aStack.addAll (CollectionHelper.newList ("g", "h"));
    assertEquals (2, aStack.size ());
    assertEquals ("g", aStack.firstElement ());
    assertEquals ("h", aStack.peek ());
    assertEquals ("h", aStack.pop ());
    assertEquals ("g", aStack.pop ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new NonBlockingStack <String> (),
                                                                       new NonBlockingStack <String> ());
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new NonBlockingStack <String> ("a", "b"),
                                                                       new NonBlockingStack <String> ("a", "b"));
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (new NonBlockingStack <String> (CollectionHelper.newList ("a",
                                                                                                                                "b")),
                                                                       new NonBlockingStack <String> (CollectionHelper.newList ("a",
                                                                                                                                "b")));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (new NonBlockingStack <String> (CollectionHelper.newList ("a",
                                                                                                                                    "b")),
                                                                           new NonBlockingStack <String> (CollectionHelper.newList ("a")));
    CommonsTestHelper.testGetClone (new NonBlockingStack <String> ("a", "b"));
    CommonsTestHelper.testGetClone (new NonBlockingStack <String> ());
  }
}
