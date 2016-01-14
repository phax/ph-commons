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

import org.junit.Test;

public final class JavaPrivateMethodFuncTest
{
  private static class Super
  {
    public Super ()
    {}

    private String method ()
    {
      return "Super.method";
    }

    public String wrapper ()
    {
      return method ();
    }
  }

  private static final class Child extends Super
  {
    public Child ()
    {}

    private String method ()
    {
      return "Child.method";
    }

    @Override
    public String wrapper ()
    {
      return method ();
    }
  }

  @Test
  public void testPrivateAccessor ()
  {
    assertEquals ("Super.method", new Super ().wrapper ());
    assertEquals ("Child.method", new Child ().wrapper ());
    assertEquals ("Child.method", ((Super) new Child ()).wrapper ());
  }
}
