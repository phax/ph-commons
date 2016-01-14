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
package com.helger.commons.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;

/**
 * Test class for class {@link GenericReflection}.
 *
 * @author Philip Helger
 */
public final class GenericReflectionTest
{
  @Test
  public void testUncechkedCast ()
  {
    final List <String> l = CollectionHelper.newList ("a", "b");
    final Object o = l;
    final List <String> l2 = GenericReflection.<Object, List <String>> uncheckedCast (o);
    assertSame (l, l2);
  }

  @Test
  public void testForName () throws ClassNotFoundException
  {
    assertSame (GenericReflectionTest.class,
                GenericReflection.<GenericReflectionTest> getClassFromName (GenericReflectionTest.class.getName ()));
    try
    {
      GenericReflection.getClassFromName ("There ain't no such class");
      fail ();
    }
    catch (final ClassNotFoundException ex)
    {}
  }

  @Test
  public void testSafeForName ()
  {
    assertSame (GenericReflectionTest.class,
                GenericReflection.<GenericReflectionTest> getClassFromNameSafe (GenericReflectionTest.class.getName ()));
    assertNull (GenericReflection.getClassFromNameSafe ("There ain't no such class"));
  }

  @Test
  public void testInvokeMethod () throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
  {
    final MockGenericInvoke gi = new MockGenericInvoke ();
    assertEquals (0, gi.getNoArgs ());
    assertEquals (0, gi.getTwoArgs ());
    GenericReflection.invokeMethod (gi, "noArgs");
    assertEquals (1, gi.getNoArgs ());
    assertEquals (0, gi.getTwoArgs ());
    try
    {
      GenericReflection.invokeMethod (gi, "noArgs", Integer.valueOf (5));
      fail ();
    }
    catch (final NoSuchMethodException ex)
    {}

    GenericReflection.invokeMethod (gi,
                                    "twoArgs",
                                    new Class <?> [] { String.class, int.class },
                                    new Object [] { "String", Integer.valueOf (5) });
    assertEquals (1, gi.getNoArgs ());
    assertEquals (1, gi.getTwoArgs ());

    try
    {
      GenericReflection.invokeMethod (gi, "twoArgs", Integer.valueOf (5));
      fail ();
    }
    catch (final NoSuchMethodException ex)
    {}
  }

  @Test
  public void testInvokeStaticMethod () throws NoSuchMethodException,
                                        IllegalAccessException,
                                        InvocationTargetException,
                                        ClassNotFoundException
  {
    assertEquals (0, MockGenericInvoke.getStaticNoArgs ());
    assertEquals (0, MockGenericInvoke.getStaticTwoArgs ());
    GenericReflection.invokeStaticMethod (MockGenericInvoke.class.getName (), "staticNoArgs");
    assertEquals (1, MockGenericInvoke.getStaticNoArgs ());
    assertEquals (0, MockGenericInvoke.getStaticTwoArgs ());
    try
    {
      GenericReflection.invokeStaticMethod (MockGenericInvoke.class.getName (), "staticNoArgs", Integer.valueOf (5));
      fail ();
    }
    catch (final NoSuchMethodException ex)
    {}

    GenericReflection.invokeStaticMethod (MockGenericInvoke.class.getName (),
                                          "staticTwoArgs",
                                          new Class <?> [] { String.class, int.class },
                                          new Object [] { "String", Integer.valueOf (5) });
    assertEquals (1, MockGenericInvoke.getStaticNoArgs ());
    assertEquals (1, MockGenericInvoke.getStaticTwoArgs ());

    try
    {
      GenericReflection.invokeStaticMethod (MockGenericInvoke.class.getName (), "staticTwoArgs", Integer.valueOf (5));
      fail ();
    }
    catch (final NoSuchMethodException ex)
    {}
  }

  @Test
  public void testNewInstance () throws IllegalAccessException,
                                 NoSuchMethodException,
                                 InvocationTargetException,
                                 InstantiationException
  {
    final String s = GenericReflection.newInstance ("Hallo Welt");
    assertNotNull (s);
    assertEquals (0, s.length ());

    assertNull (GenericReflection.newInstance ((Class <?>) null));
    assertNotNull (GenericReflection.newInstance (String.class));
    assertNull (GenericReflection.newInstance (MockNoPublicCtor.class));

    assertNull (GenericReflection.newInstance (null, Object.class));
    assertNull (GenericReflection.newInstance ("", Object.class));
    assertNull (GenericReflection.newInstance (String.class.getName (), null));
    assertNotNull (GenericReflection.newInstance (String.class.getName (), Object.class));
    assertNotNull (GenericReflection.newInstance (StringBuilder.class.getName (), CharSequence.class));
    assertNull (GenericReflection.newInstance (MockNoPublicCtor.class.getName (), Object.class));

    final ClassLoader cl = ClassLoaderHelper.getDefaultClassLoader ();
    assertNull (GenericReflection.newInstance (null, Object.class, cl));
    assertNull (GenericReflection.newInstance ("", Object.class, cl));
    assertNull (GenericReflection.newInstance (String.class.getName (), null, cl));
    assertNotNull (GenericReflection.newInstance (String.class.getName (), Object.class, cl));
    assertNotNull (GenericReflection.newInstance (StringBuilder.class.getName (), CharSequence.class, cl));
    assertNull (GenericReflection.newInstance (MockNoPublicCtor.class.getName (), Object.class, cl));
  }
}
