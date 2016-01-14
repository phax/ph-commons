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
package com.helger.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.junit.Test;

/**
 * Test class for class {@link ValueEnforcer}.
 *
 * @author Philip Helger
 */
public final class ValueEnforcerTest
{
  @Test (expected = NullPointerException.class)
  public void testNotNull ()
  {
    ValueEnforcer.notNull (null, "null");
  }

  @Test (expected = NullPointerException.class)
  public void testNotEmpty1 ()
  {
    ValueEnforcer.notEmpty ((String) null, "null");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmpty2 ()
  {
    ValueEnforcer.notEmpty ("", "null");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmpty3 ()
  {
    ValueEnforcer.notEmpty (new StringBuilder (), "null");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmpty4 ()
  {
    ValueEnforcer.notEmpty (new String [0], "null");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmpty5 ()
  {
    ValueEnforcer.notEmpty (new char [0], "null");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmpty6 ()
  {
    ValueEnforcer.notEmpty (new short [0], "null");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmpty7 ()
  {
    ValueEnforcer.notEmpty (new ArrayList <String> (), "null");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmpty8 ()
  {
    ValueEnforcer.notEmpty (new Vector <String> (), "null");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testNotEmpty9 ()
  {
    ValueEnforcer.notEmpty (new HashMap <String, Integer> (), "null");
  }
}
