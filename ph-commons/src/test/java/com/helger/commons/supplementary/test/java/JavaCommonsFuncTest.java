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
package com.helger.commons.supplementary.test.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Dummy class
 *
 * @author Philip Helger
 */
public final class JavaCommonsFuncTest
{
  class A
  {
    public A ()
    {}
  }

  class B extends A
  {
    public B ()
    {}
  }

  @Test
  public void testAssignability ()
  {
    class C extends B
    {
      public C ()
      {}
    }

    new A ();
    new B ();
    new C ();

    // member class
    assertFalse (A.class.isLocalClass ());
    assertTrue (A.class.isMemberClass ());

    // local class
    assertTrue (C.class.isLocalClass ());
    assertFalse (C.class.isMemberClass ());

    // check assignFrom
    assertTrue (A.class.isAssignableFrom (B.class));
    assertFalse (B.class.isAssignableFrom (A.class));
  }

  @SuppressWarnings ("boxing")
  @Test
  public void testAutoboxing ()
  {
    // assumes a cache for -128 - +127
    final Integer i1 = Integer.valueOf (9999);
    final Integer i2 = Integer.valueOf (9999);
    assertTrue (i1 == 9999);
    assertTrue (9999 == i1);
    assertTrue (i1 == i2.intValue ());
    assertTrue (i1 != i2);
  }

  @Test
  public void testParseBoolean ()
  {
    assertFalse (Boolean.parseBoolean ("1"));
    assertTrue (Boolean.parseBoolean ("TRUE"));
    assertTrue (Boolean.parseBoolean ("true"));
    assertFalse (Boolean.parseBoolean ("0"));
    assertFalse (Boolean.parseBoolean ("FALSE"));
    assertFalse (Boolean.parseBoolean ("false"));
  }

  @Test
  public void testHexString ()
  {
    assertEquals ("1", Integer.toHexString (1));
    assertEquals ("a", Integer.toHexString (10));
    assertEquals ("f", Integer.toHexString (15));
    assertEquals ("10", Integer.toHexString (16));
  }
}
