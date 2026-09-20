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
package com.helger.base.hashcode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class for class {@link HashCodeGenerator}.
 *
 * @author Philip Helger
 */
public final class HashCodeGeneratorTest
{
  @Test
  public void testAppendPrimitives ()
  {
    final HashCodeGenerator a = new HashCodeGenerator (this);
    assertFalse (a.isClosed ());

    a.append (true)
     .append ((byte) 1)
     .append ('c')
     .append (1.5d)
     .append (2.5f)
     .append (3)
     .append (4L)
     .append ((short) 5);
    a.append ("any");
    a.append ((Object) null);

    final int nHC = a.getHashCode ();
    assertTrue (a.isClosed ());
    // The very same value is returned again
    assertEquals (nHC, a.getHashCode ());

    // The same sequence gives the same hash code
    final HashCodeGenerator b = new HashCodeGenerator (this);
    b.append (true)
     .append ((byte) 1)
     .append ('c')
     .append (1.5d)
     .append (2.5f)
     .append (3)
     .append (4L)
     .append ((short) 5)
     .append ("any")
     .append ((Object) null);
    assertEquals (nHC, b.getHashCode ());
  }

  @Test
  public void testClassBasedCtor ()
  {
    final HashCodeGenerator a = new HashCodeGenerator (HashCodeGeneratorTest.class).append (1);
    final HashCodeGenerator b = new HashCodeGenerator (HashCodeGeneratorTest.class).append (1);
    assertEquals (a.getHashCode (), b.getHashCode ());

    // A different class gives a different hash code
    final HashCodeGenerator c = new HashCodeGenerator (String.class).append (1);
    assertNotEquals (a.getHashCode (), c.getHashCode ());
  }

  @Test
  public void testAppendAfterCloseFails ()
  {
    final HashCodeGenerator a = new HashCodeGenerator (this);
    a.getHashCode ();
    try
    {
      a.append (1);
      fail ();
    }
    catch (final IllegalStateException ex)
    {
      // expected
    }
  }

  @Test
  public void testAppendArrays ()
  {
    final HashCodeGenerator a = new HashCodeGenerator (this);
    a.append (new boolean [] { true })
     .append (new byte [] { 1 })
     .append (new char [] { 'a' })
     .append (new double [] { 1 })
     .append (new float [] { 1 })
     .append (new int [] { 1 })
     .append (new long [] { 1 })
     .append (new short [] { 1 })
     .append (new Object [] { "a" });

    final HashCodeGenerator b = new HashCodeGenerator (this);
    b.append (new boolean [] { true })
     .append (new byte [] { 1 })
     .append (new char [] { 'a' })
     .append (new double [] { 1 })
     .append (new float [] { 1 })
     .append (new int [] { 1 })
     .append (new long [] { 1 })
     .append (new short [] { 1 })
     .append (new Object [] { "a" });

    assertEquals (a.getHashCode (), b.getHashCode ());
  }

  @Test
  public void testAppendNullArrays ()
  {
    final HashCodeGenerator a = new HashCodeGenerator (this);
    a.append ((boolean []) null)
     .append ((byte []) null)
     .append ((char []) null)
     .append ((double []) null)
     .append ((float []) null)
     .append ((int []) null)
     .append ((long []) null)
     .append ((short []) null)
     .append ((Object []) null);

    final HashCodeGenerator b = new HashCodeGenerator (this);
    b.append ((boolean []) null)
     .append ((byte []) null)
     .append ((char []) null)
     .append ((double []) null)
     .append ((float []) null)
     .append ((int []) null)
     .append ((long []) null)
     .append ((short []) null)
     .append ((Object []) null);

    assertEquals (a.getHashCode (), b.getHashCode ());
  }

  @Test
  public void testAppendArraysWithRange ()
  {
    final HashCodeGenerator a = new HashCodeGenerator (this);
    a.append (new boolean [] { true, false }, 0, 1)
     .append (new byte [] { 1, 2 }, 0, 1)
     .append (new char [] { 'a', 'b' }, 0, 1)
     .append (new double [] { 1, 2 }, 0, 1)
     .append (new float [] { 1, 2 }, 0, 1)
     .append (new int [] { 1, 2 }, 0, 1)
     .append (new long [] { 1, 2 }, 0, 1)
     .append (new short [] { 1, 2 }, 0, 1)
     .append (new Object [] { "a", "b" }, 0, 1);

    final HashCodeGenerator b = new HashCodeGenerator (this);
    b.append (new boolean [] { true, false }, 0, 1)
     .append (new byte [] { 1, 2 }, 0, 1)
     .append (new char [] { 'a', 'b' }, 0, 1)
     .append (new double [] { 1, 2 }, 0, 1)
     .append (new float [] { 1, 2 }, 0, 1)
     .append (new int [] { 1, 2 }, 0, 1)
     .append (new long [] { 1, 2 }, 0, 1)
     .append (new short [] { 1, 2 }, 0, 1)
     .append (new Object [] { "a", "b" }, 0, 1);

    assertEquals (a.getHashCode (), b.getHashCode ());
  }

  @Test
  public void testGetDerived ()
  {
    final int nSuper = new HashCodeGenerator (this).append (1).getHashCode ();
    final HashCodeGenerator a = HashCodeGenerator.getDerived (nSuper).append (2);
    final HashCodeGenerator b = HashCodeGenerator.getDerived (nSuper).append (2);
    assertEquals (a.getHashCode (), b.getHashCode ());
  }

  @Test
  public void testStaticGetHashCode ()
  {
    assertEquals (HashCodeGenerator.getHashCode (this, "a", Integer.valueOf (1)),
                  HashCodeGenerator.getHashCode (this, "a", Integer.valueOf (1)));
    assertNotEquals (HashCodeGenerator.getHashCode (this, "a"), HashCodeGenerator.getHashCode (this, "b"));

    // The int based overload needs an explicit cast to be unambiguous
    final int nSuper = 4711;
    assertEquals (HashCodeGenerator.getHashCode (nSuper, new Object [] { "a" }),
                  HashCodeGenerator.getHashCode (nSuper, new Object [] { "a" }));
  }

  @SuppressWarnings ({ "deprecation", "unlikely-arg-type" })
  @Test
  public void testEqualsHashcode ()
  {
    // equals is identity based on purpose - hashCode is the computed value
    final HashCodeGenerator a = new HashCodeGenerator (this).append (1);
    assertEquals (a, a);
    assertFalse (a.equals (null));
    assertFalse (a.equals ("any other type"));
    assertFalse (a.equals (new HashCodeGenerator (this).append (1)));
    assertEquals (a.hashCode (), a.hashCode ());
    assertEquals (a.hashCode (), new HashCodeGenerator (this).append (1).hashCode ());
  }
}
