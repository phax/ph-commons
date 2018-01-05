/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.lang.proxy;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

/**
 * Test class for class {@link LoggingInvocationHandler}.
 *
 * @author Philip Helger
 */
public final class LoggingInvocationHandlerTest
{
  @Test
  public void testBasic ()
  {
    final ICommonsList <String> aList = LoggingInvocationHandler.proxying (ICommonsList.class,
                                                                           new CommonsArrayList<> ());
    aList.add ("a");
    aList.add ("b");
    aList.subList (0, 1).isEmpty ();
    // String result is not proxied because it is no interface
    aList.remove (0).length ();
  }

  public interface ITest
  {
    default int foo ()
    {
      return 1;
    }
  }

  public static class TestImpl implements ITest
  {
    public int foo ()
    {
      return 2;
    }
  }

  public static class TestImpl2 implements ITest
  {}

  @Test
  public void testDefaultMethod ()
  {
    LoggingInvocationHandler.proxying (ITest.class, new TestImpl ()).foo ();
    LoggingInvocationHandler.proxying (ITest.class, new TestImpl2 ()).foo ();
  }
}
