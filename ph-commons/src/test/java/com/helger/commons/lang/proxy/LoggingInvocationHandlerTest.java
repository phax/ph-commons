package com.helger.commons.lang.proxy;

import org.junit.Test;

import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;

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
                                                                           new CommonsArrayList <> ());
    aList.add ("a");
    aList.add ("b");
    aList.subList (0, 1).isEmpty ();
    // String result is not proxied because it is no interface
    aList.remove (0).length ();
  }

  public static interface ITest
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
