/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.callback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.helger.base.state.EChange;
import com.helger.base.state.EContinue;

/**
 * Test class for class {@link CallbackList}.
 *
 * @author Philip Helger
 */
public final class CallbackListTest
{
  /**
   * A callback that only carries a name.
   *
   * @author Philip Helger
   */
  private static final class MockCallback implements ICallback
  {
    private final String m_sName;

    MockCallback (final String sName)
    {
      m_sName = sName;
    }

    String getName ()
    {
      return m_sName;
    }
  }

  @Test
  public void testEmpty ()
  {
    final CallbackList <MockCallback> a = new CallbackList <> ();
    assertTrue (a.isEmpty ());
    assertEquals (0, a.size ());
    assertTrue (a.getAllCallbacks ().isEmpty ());
    assertNotNull (a.toString ());
    assertSame (EChange.UNCHANGED, a.removeAll ());
  }

  @Test
  public void testAddAndRemove ()
  {
    final CallbackList <MockCallback> a = new CallbackList <> ();
    final MockCallback aCB1 = new MockCallback ("1");
    final MockCallback aCB2 = new MockCallback ("2");

    assertSame (EChange.CHANGED, a.add (aCB1));
    assertEquals (1, a.size ());
    assertFalse (a.isEmpty ());
    assertSame (aCB1, a.getCallbackAtIndex (0));

    assertSame (EChange.CHANGED, a.addAll (aCB2));
    assertEquals (2, a.size ());

    assertSame (EChange.CHANGED, a.removeObject (aCB1));
    assertSame (EChange.UNCHANGED, a.removeObject (aCB1));
    assertSame (EChange.UNCHANGED, a.removeObject (null));
    assertEquals (1, a.size ());

    assertSame (EChange.CHANGED, a.removeAll ());
    assertTrue (a.isEmpty ());
  }

  @Test
  public void testAddAllEmpty ()
  {
    final CallbackList <MockCallback> a = new CallbackList <> ();
    assertSame (EChange.UNCHANGED, a.addAll ());
  }

  @Test
  public void testSet ()
  {
    final CallbackList <MockCallback> a = new CallbackList <> ();
    a.add (new MockCallback ("old"));

    final MockCallback aNew = new MockCallback ("new");
    assertSame (EChange.CHANGED, a.set (aNew));
    assertEquals (1, a.size ());
    assertSame (aNew, a.getCallbackAtIndex (0));

    final CallbackList <MockCallback> b = new CallbackList <> ();
    b.add (new MockCallback ("b1"));
    assertSame (EChange.CHANGED, a.set (b));
    assertEquals (1, a.size ());
    assertEquals ("b1", a.getCallbackAtIndex (0).getName ());
  }

  @Test
  public void testCopyCtorAndClone ()
  {
    final CallbackList <MockCallback> a = new CallbackList <> ();
    a.add (new MockCallback ("1"));

    final CallbackList <MockCallback> aCopy = new CallbackList <> (a);
    assertEquals (1, aCopy.size ());

    // A null source gives an empty list
    assertTrue (new CallbackList <MockCallback> (null).isEmpty ());

    final CallbackList <MockCallback> aClone = a.getClone ();
    assertNotSame (a, aClone);
    assertEquals (1, aClone.size ());
  }

  @Test
  public void testGetCallbackAtIndex ()
  {
    final CallbackList <MockCallback> a = new CallbackList <> ();
    a.add (new MockCallback ("1"));

    assertNotNull (a.getCallbackAtIndex (0));
    // An index beyond the end returns null
    assertSame (null, a.getCallbackAtIndex (1));
    // A negative index is rejected
    try
    {
      a.getCallbackAtIndex (-1);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testIterateAndForEach ()
  {
    final CallbackList <MockCallback> a = new CallbackList <> ();
    a.add (new MockCallback ("1"));
    a.add (new MockCallback ("2"));

    final Iterator <MockCallback> it = a.iterator ();
    assertTrue (it.hasNext ());
    assertNotNull (it.next ());

    final List <String> aNames = new ArrayList <> ();
    a.forEach (x -> aNames.add (x.getName ()));
    assertEquals (2, aNames.size ());
  }

  @Test
  public void testForEachBreakable ()
  {
    final CallbackList <MockCallback> a = new CallbackList <> ();
    a.add (new MockCallback ("1"));
    a.add (new MockCallback ("2"));

    final List <String> aNames = new ArrayList <> ();
    assertSame (EContinue.CONTINUE, a.forEachBreakable (x -> {
      aNames.add (x.getName ());
      return EContinue.CONTINUE;
    }));
    assertEquals (2, aNames.size ());

    aNames.clear ();
    assertSame (EContinue.BREAK, a.forEachBreakable (x -> {
      aNames.add (x.getName ());
      return EContinue.BREAK;
    }));
    assertEquals (1, aNames.size ());
  }

  @Test
  public void testInvalidParams ()
  {
    final CallbackList <MockCallback> a = new CallbackList <> ();
    try
    {
      a.add (null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      a.set ((MockCallback) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      a.set ((CallbackList <MockCallback>) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
