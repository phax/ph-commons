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
package com.helger.commons.supplementary.test.java;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;

import javax.annotation.WillClose;

import org.junit.Test;

import com.helger.commons.annotation.DevelopersNote;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.lang.ClassLoaderHelper;
import com.helger.commons.system.EJavaVersion;
import com.helger.commons.system.JavaVersionHelper;

/**
 * Test the difference between loading from a ClassLoader and a Class. The
 * results are:
 * <ul>
 * <li>When using a ClassLoader NO leading slash may be present</li>
 * <li>When using a Class the leading slash MUST be present</li>
 * </ul>
 *
 * @author Philip Helger
 */
public final class JavaClassLoaderFuncTest
{
  private static void _assertNotNull (@WillClose final InputStream aIS)
  {
    try
    {
      assertNotNull (aIS);
    }
    finally
    {
      StreamHelper.close (aIS);
    }
  }

  private static void _assertNull (@WillClose final InputStream aIS)
  {
    try
    {
      assertNull (aIS);
    }
    finally
    {
      StreamHelper.close (aIS);
    }
  }

  @Test
  public void testGetResourceThisProject ()
  {
    final String sWithoutSlash = "classldr/test1.txt";
    final String sWithSlash = "/" + sWithoutSlash;

    // Context class loader
    assertNotNull (ClassLoaderHelper.getContextClassLoader ().getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getContextClassLoader ().getResource (sWithSlash));

    _assertNotNull (ClassLoaderHelper.getContextClassLoader ().getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getContextClassLoader ().getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getContextClassLoader (), sWithoutSlash));
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getContextClassLoader (), sWithSlash));

    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getContextClassLoader (), sWithoutSlash));
    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getContextClassLoader (), sWithSlash));

    // Current class
    assertNull (JavaClassLoaderFuncTest.class.getResource (sWithoutSlash));
    assertNotNull (JavaClassLoaderFuncTest.class.getResource (sWithSlash));

    _assertNull (JavaClassLoaderFuncTest.class.getResourceAsStream (sWithoutSlash));
    _assertNotNull (JavaClassLoaderFuncTest.class.getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassHelper.getResource (JavaClassLoaderFuncTest.class, sWithoutSlash));
    assertNotNull (ClassHelper.getResource (JavaClassLoaderFuncTest.class, sWithSlash));

    _assertNotNull (ClassHelper.getResourceAsStream (JavaClassLoaderFuncTest.class, sWithoutSlash));
    _assertNotNull (ClassHelper.getResourceAsStream (JavaClassLoaderFuncTest.class, sWithSlash));

    // Current class class loader
    assertNotNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class).getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class).getResource (sWithSlash));

    _assertNotNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class)
                                     .getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class)
                                  .getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                  sWithoutSlash));
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                  sWithSlash));

    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                           sWithoutSlash));
    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                           sWithSlash));

    // System class loader
    assertNotNull (ClassLoaderHelper.getSystemClassLoader ().getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getSystemClassLoader ().getResource (sWithSlash));

    _assertNotNull (ClassLoaderHelper.getSystemClassLoader ().getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getSystemClassLoader ().getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getSystemClassLoader (), sWithoutSlash));
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getSystemClassLoader (), sWithSlash));

    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getSystemClassLoader (), sWithoutSlash));
    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getSystemClassLoader (), sWithSlash));
  }

  @Test
  public void testGetDirectoryThisProject ()
  {
    final String sWithoutSlash = "classldr/";
    final String sWithSlash = "/" + sWithoutSlash;

    // Context class loader
    assertNotNull (ClassLoaderHelper.getContextClassLoader ().getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getContextClassLoader ().getResource (sWithSlash));

    _assertNotNull (ClassLoaderHelper.getContextClassLoader ().getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getContextClassLoader ().getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getContextClassLoader (), sWithoutSlash));
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getContextClassLoader (), sWithSlash));

    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getContextClassLoader (), sWithoutSlash));
    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getContextClassLoader (), sWithSlash));

    // Current class
    assertNull (JavaClassLoaderFuncTest.class.getResource (sWithoutSlash));
    assertNotNull (JavaClassLoaderFuncTest.class.getResource (sWithSlash));

    _assertNull (JavaClassLoaderFuncTest.class.getResourceAsStream (sWithoutSlash));
    _assertNotNull (JavaClassLoaderFuncTest.class.getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassHelper.getResource (JavaClassLoaderFuncTest.class, sWithoutSlash));
    assertNotNull (ClassHelper.getResource (JavaClassLoaderFuncTest.class, sWithSlash));

    _assertNotNull (ClassHelper.getResourceAsStream (JavaClassLoaderFuncTest.class, sWithoutSlash));
    _assertNotNull (ClassHelper.getResourceAsStream (JavaClassLoaderFuncTest.class, sWithSlash));

    // Current class class loader
    assertNotNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class).getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class).getResource (sWithSlash));

    _assertNotNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class)
                                     .getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class)
                                  .getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                  sWithoutSlash));
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                  sWithSlash));

    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                           sWithoutSlash));
    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                           sWithSlash));

    // System class loader
    assertNotNull (ClassLoaderHelper.getSystemClassLoader ().getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getSystemClassLoader ().getResource (sWithSlash));

    _assertNotNull (ClassLoaderHelper.getSystemClassLoader ().getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getSystemClassLoader ().getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getSystemClassLoader (), sWithoutSlash));
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getSystemClassLoader (), sWithSlash));

    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getSystemClassLoader (), sWithoutSlash));
    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getSystemClassLoader (), sWithSlash));
  }

  @Test
  public void testGetResourceLinkedProject ()
  {
    final String sWithoutSlash = "org/junit/Assert.class";
    final String sWithSlash = "/" + sWithoutSlash;

    // Context class loader
    assertNotNull (ClassLoaderHelper.getContextClassLoader ().getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getContextClassLoader ().getResource (sWithSlash));

    _assertNotNull (ClassLoaderHelper.getContextClassLoader ().getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getContextClassLoader ().getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getContextClassLoader (), sWithoutSlash));
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getContextClassLoader (), sWithSlash));

    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getContextClassLoader (), sWithoutSlash));
    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getContextClassLoader (), sWithSlash));

    // Current class
    assertNull (JavaClassLoaderFuncTest.class.getResource (sWithoutSlash));
    assertNotNull (JavaClassLoaderFuncTest.class.getResource (sWithSlash));

    _assertNull (JavaClassLoaderFuncTest.class.getResourceAsStream (sWithoutSlash));
    _assertNotNull (JavaClassLoaderFuncTest.class.getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassHelper.getResource (JavaClassLoaderFuncTest.class, sWithoutSlash));
    assertNotNull (ClassHelper.getResource (JavaClassLoaderFuncTest.class, sWithSlash));

    _assertNotNull (ClassHelper.getResourceAsStream (JavaClassLoaderFuncTest.class, sWithoutSlash));
    _assertNotNull (ClassHelper.getResourceAsStream (JavaClassLoaderFuncTest.class, sWithSlash));

    // Current class class loader
    assertNotNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class).getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class).getResource (sWithSlash));

    _assertNotNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class)
                                     .getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class)
                                  .getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                  sWithoutSlash));
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                  sWithSlash));

    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                           sWithoutSlash));
    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                           sWithSlash));

    // System class loader
    assertNotNull (ClassLoaderHelper.getSystemClassLoader ().getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getSystemClassLoader ().getResource (sWithSlash));

    _assertNotNull (ClassLoaderHelper.getSystemClassLoader ().getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getSystemClassLoader ().getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getSystemClassLoader (), sWithoutSlash));
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getSystemClassLoader (), sWithSlash));

    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getSystemClassLoader (), sWithoutSlash));
    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getSystemClassLoader (), sWithSlash));
  }

  @Test
  public void testGetDirectoryLinkedProject_TrailingSlash ()
  {
    final String sWithoutSlash = "org/junit/";
    final String sWithSlash = "/" + sWithoutSlash;

    // Context class loader
    assertNotNull (ClassLoaderHelper.getContextClassLoader ().getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getContextClassLoader ().getResource (sWithSlash));

    _assertNotNull (ClassLoaderHelper.getContextClassLoader ().getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getContextClassLoader ().getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getContextClassLoader (), sWithoutSlash));
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getContextClassLoader (), sWithSlash));

    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getContextClassLoader (), sWithoutSlash));
    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getContextClassLoader (), sWithSlash));

    // Current class
    assertNull (JavaClassLoaderFuncTest.class.getResource (sWithoutSlash));
    assertNotNull (JavaClassLoaderFuncTest.class.getResource (sWithSlash));

    _assertNull (JavaClassLoaderFuncTest.class.getResourceAsStream (sWithoutSlash));
    _assertNotNull (JavaClassLoaderFuncTest.class.getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassHelper.getResource (JavaClassLoaderFuncTest.class, sWithoutSlash));
    assertNotNull (ClassHelper.getResource (JavaClassLoaderFuncTest.class, sWithSlash));

    _assertNotNull (ClassHelper.getResourceAsStream (JavaClassLoaderFuncTest.class, sWithoutSlash));
    _assertNotNull (ClassHelper.getResourceAsStream (JavaClassLoaderFuncTest.class, sWithSlash));

    // Current class class loader
    assertNotNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class).getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class).getResource (sWithSlash));

    _assertNotNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class)
                                     .getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class)
                                  .getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                  sWithoutSlash));
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                  sWithSlash));

    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                           sWithoutSlash));
    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                           sWithSlash));

    // System class loader
    assertNotNull (ClassLoaderHelper.getSystemClassLoader ().getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getSystemClassLoader ().getResource (sWithSlash));

    _assertNotNull (ClassLoaderHelper.getSystemClassLoader ().getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getSystemClassLoader ().getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getSystemClassLoader (), sWithoutSlash));
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getSystemClassLoader (), sWithSlash));

    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getSystemClassLoader (), sWithoutSlash));
    _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getSystemClassLoader (), sWithSlash));
  }

  @Test
  public void testGetDirectoryLinkedProject_NoTrailingSlash ()
  {
    final String sWithoutSlash = "org/junit";
    final String sWithSlash = "/" + sWithoutSlash;

    // Context class loader
    assertNotNull (ClassLoaderHelper.getContextClassLoader ().getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getContextClassLoader ().getResource (sWithSlash));

    _assertNotNull (ClassLoaderHelper.getContextClassLoader ().getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getContextClassLoader ().getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getContextClassLoader (), sWithoutSlash));
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getContextClassLoader (), sWithSlash));

    // -- different here --
    // This is "not null" since JDK 1.8.0_144
    // http://www.oracle.com/technetwork/java/javase/8u144-relnotes-3838694.html
    final boolean isAtLeast1_8_0_144 = JavaVersionHelper.isAtLeast (8, 144);
    if (isAtLeast1_8_0_144)
    {
      _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getContextClassLoader (),
                                                             sWithoutSlash));
      _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getContextClassLoader (), sWithSlash));
    }
    else
    {
      _assertNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getContextClassLoader (), sWithoutSlash));
      _assertNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getContextClassLoader (), sWithSlash));
    }

    // Current class
    assertNull (JavaClassLoaderFuncTest.class.getResource (sWithoutSlash));
    assertNotNull (JavaClassLoaderFuncTest.class.getResource (sWithSlash));

    _assertNull (JavaClassLoaderFuncTest.class.getResourceAsStream (sWithoutSlash));
    _assertNotNull (JavaClassLoaderFuncTest.class.getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassHelper.getResource (JavaClassLoaderFuncTest.class, sWithoutSlash));
    assertNotNull (ClassHelper.getResource (JavaClassLoaderFuncTest.class, sWithSlash));

    // -- different here --
    if (isAtLeast1_8_0_144)
    {
      _assertNotNull (ClassHelper.getResourceAsStream (JavaClassLoaderFuncTest.class, sWithoutSlash));
      _assertNotNull (ClassHelper.getResourceAsStream (JavaClassLoaderFuncTest.class, sWithSlash));
    }
    else
    {
      _assertNull (ClassHelper.getResourceAsStream (JavaClassLoaderFuncTest.class, sWithoutSlash));
      _assertNull (ClassHelper.getResourceAsStream (JavaClassLoaderFuncTest.class, sWithSlash));
    }

    // Current class class loader
    assertNotNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class).getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class).getResource (sWithSlash));

    _assertNotNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class)
                                     .getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class)
                                  .getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                  sWithoutSlash));
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                  sWithSlash));

    // -- different here --
    if (isAtLeast1_8_0_144)
    {
      _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                             sWithoutSlash));
      _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                             sWithSlash));
    }
    else
    {
      _assertNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                          sWithoutSlash));
      _assertNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                          sWithSlash));
    }

    // System class loader
    assertNotNull (ClassLoaderHelper.getSystemClassLoader ().getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getSystemClassLoader ().getResource (sWithSlash));

    _assertNotNull (ClassLoaderHelper.getSystemClassLoader ().getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getSystemClassLoader ().getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getSystemClassLoader (), sWithoutSlash));
    assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getSystemClassLoader (), sWithSlash));

    // -- different here --
    if (isAtLeast1_8_0_144)
    {
      _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getSystemClassLoader (), sWithoutSlash));
      _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getSystemClassLoader (), sWithSlash));
    }
    else
    {
      _assertNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getSystemClassLoader (), sWithoutSlash));
      _assertNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getSystemClassLoader (), sWithSlash));
    }
  }

  @Test
  public void testGetResourceRuntime ()
  {
    // This test does not work with JDK 1.9 or higher because of the new module
    // system
    if (EJavaVersion.getCurrentVersion ().isOlderOrEqualsThan (EJavaVersion.JDK_1_8))
    {
      final String sWithoutSlash = "java/lang/String.class";
      final String sWithSlash = "/" + sWithoutSlash;

      // Context class loader
      assertNotNull (ClassLoaderHelper.getContextClassLoader ().getResource (sWithoutSlash));
      assertNull (ClassLoaderHelper.getContextClassLoader ().getResource (sWithSlash));

      _assertNotNull (ClassLoaderHelper.getContextClassLoader ().getResourceAsStream (sWithoutSlash));
      _assertNull (ClassLoaderHelper.getContextClassLoader ().getResourceAsStream (sWithSlash));

      // This is the work around to be used
      assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getContextClassLoader (), sWithoutSlash));
      assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getContextClassLoader (), sWithSlash));

      _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getContextClassLoader (),
                                                             sWithoutSlash));
      _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getContextClassLoader (), sWithSlash));

      // Current class
      assertNull (JavaClassLoaderFuncTest.class.getResource (sWithoutSlash));
      assertNotNull (JavaClassLoaderFuncTest.class.getResource (sWithSlash));

      _assertNull (JavaClassLoaderFuncTest.class.getResourceAsStream (sWithoutSlash));
      _assertNotNull (JavaClassLoaderFuncTest.class.getResourceAsStream (sWithSlash));

      // This is the work around to be used
      assertNotNull (ClassHelper.getResource (JavaClassLoaderFuncTest.class, sWithoutSlash));
      assertNotNull (ClassHelper.getResource (JavaClassLoaderFuncTest.class, sWithSlash));

      _assertNotNull (ClassHelper.getResourceAsStream (JavaClassLoaderFuncTest.class, sWithoutSlash));
      _assertNotNull (ClassHelper.getResourceAsStream (JavaClassLoaderFuncTest.class, sWithSlash));

      // Current class class loader
      assertNotNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class).getResource (sWithoutSlash));
      assertNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class).getResource (sWithSlash));

      _assertNotNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class)
                                       .getResourceAsStream (sWithoutSlash));
      _assertNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class)
                                    .getResourceAsStream (sWithSlash));

      // This is the work around to be used
      assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                    sWithoutSlash));
      assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                    sWithSlash));

      _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                             sWithoutSlash));
      _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                             sWithSlash));

      // System class loader
      assertNotNull (ClassLoaderHelper.getSystemClassLoader ().getResource (sWithoutSlash));
      assertNull (ClassLoaderHelper.getSystemClassLoader ().getResource (sWithSlash));

      _assertNotNull (ClassLoaderHelper.getSystemClassLoader ().getResourceAsStream (sWithoutSlash));
      _assertNull (ClassLoaderHelper.getSystemClassLoader ().getResourceAsStream (sWithSlash));

      // This is the work around to be used
      assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getSystemClassLoader (), sWithoutSlash));
      assertNotNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getSystemClassLoader (), sWithSlash));

      _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getSystemClassLoader (), sWithoutSlash));
      _assertNotNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getSystemClassLoader (), sWithSlash));
    }
  }

  @Test
  @DevelopersNote ("Very special case!")
  public void testGetDirectoryRuntime ()
  {
    final String sWithoutSlash = "java/lang";
    final String sWithSlash = "/" + sWithoutSlash;

    // Context class loader
    assertNull (ClassLoaderHelper.getContextClassLoader ().getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getContextClassLoader ().getResource (sWithSlash));

    _assertNull (ClassLoaderHelper.getContextClassLoader ().getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getContextClassLoader ().getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getContextClassLoader (), sWithoutSlash));
    assertNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getContextClassLoader (), sWithSlash));

    _assertNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getContextClassLoader (), sWithoutSlash));
    _assertNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getContextClassLoader (), sWithSlash));

    // Current class
    assertNull (JavaClassLoaderFuncTest.class.getResource (sWithoutSlash));
    assertNull (JavaClassLoaderFuncTest.class.getResource (sWithSlash));

    _assertNull (JavaClassLoaderFuncTest.class.getResourceAsStream (sWithoutSlash));
    _assertNull (JavaClassLoaderFuncTest.class.getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNull (ClassHelper.getResource (JavaClassLoaderFuncTest.class, sWithoutSlash));
    assertNull (ClassHelper.getResource (JavaClassLoaderFuncTest.class, sWithSlash));

    _assertNull (ClassHelper.getResourceAsStream (JavaClassLoaderFuncTest.class, sWithoutSlash));
    _assertNull (ClassHelper.getResourceAsStream (JavaClassLoaderFuncTest.class, sWithSlash));

    // Current class class loader
    assertNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class).getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class).getResource (sWithSlash));

    _assertNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class)
                                  .getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class)
                                  .getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                               sWithoutSlash));
    assertNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                               sWithSlash));

    _assertNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                        sWithoutSlash));
    _assertNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getClassClassLoader (JavaClassLoaderFuncTest.class),
                                                        sWithSlash));

    // System class loader
    assertNull (ClassLoaderHelper.getSystemClassLoader ().getResource (sWithoutSlash));
    assertNull (ClassLoaderHelper.getSystemClassLoader ().getResource (sWithSlash));

    _assertNull (ClassLoaderHelper.getSystemClassLoader ().getResourceAsStream (sWithoutSlash));
    _assertNull (ClassLoaderHelper.getSystemClassLoader ().getResourceAsStream (sWithSlash));

    // This is the work around to be used
    assertNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getSystemClassLoader (), sWithoutSlash));
    assertNull (ClassLoaderHelper.getResource (ClassLoaderHelper.getSystemClassLoader (), sWithSlash));

    _assertNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getSystemClassLoader (), sWithoutSlash));
    _assertNull (ClassLoaderHelper.getResourceAsStream (ClassLoaderHelper.getSystemClassLoader (), sWithSlash));
  }
}
