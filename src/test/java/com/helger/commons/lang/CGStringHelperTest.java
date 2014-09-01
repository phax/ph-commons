/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Test class of class {@link CGStringHelper}.
 * 
 * @author Philip Helger
 */
public final class CGStringHelperTest
{

  @Test
  public void testGetClassLocalNameClassOfQ ()
  {
    assertEquals ("String", CGStringHelper.getClassLocalName ((Object) ""));
    assertEquals ("CGStringHelper", CGStringHelper.getClassLocalName (CGStringHelper.class));
    assertEquals ("CGStringHelper", CGStringHelper.getClassLocalName (CGStringHelper.class.getName ()));
    assertEquals ("Test", CGStringHelper.getClassLocalName ("Test"));
    assertEquals ("", CGStringHelper.getClassLocalName (""));

    try
    {
      CGStringHelper.getClassLocalName ((Object) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      CGStringHelper.getClassLocalName ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      CGStringHelper.getClassLocalName ((Class <?>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetClassPackageName ()
  {
    assertEquals ("java.lang", CGStringHelper.getClassPackageName (String.class));
    assertEquals ("a.b", CGStringHelper.getClassPackageName ("a.b.c"));
    assertEquals ("a.b", CGStringHelper.getClassPackageName ("a.b.c2"));
    assertEquals ("abra.bbra", CGStringHelper.getClassPackageName ("abra.bbra.c2"));
    assertEquals ("", CGStringHelper.getClassPackageName ("ClassNameOnly"));
    assertEquals ("x", CGStringHelper.getClassPackageName ("x.ClassNameOnly"));
    assertEquals ("java.lang", CGStringHelper.getClassPackageName (String.class));
    try
    {
      CGStringHelper.getClassPackageName ((Class <?>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      CGStringHelper.getClassPackageName ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetSafeClassName ()
  {
    assertEquals ("null", CGStringHelper.getSafeClassName (null));
    assertEquals ("java.lang.String", CGStringHelper.getSafeClassName (String.class));
    assertEquals ("java.lang.String", CGStringHelper.getSafeClassName ("Lol"));
  }

  @Test
  public void testGetDirectoryFromPackage ()
  {
    assertEquals ("java/lang", CGStringHelper.getDirectoryFromPackage (String.class.getPackage ()));
    assertEquals ("", CGStringHelper.getDirectoryFromPackage (""));
    assertEquals ("x", CGStringHelper.getDirectoryFromPackage ("x"));
    assertEquals ("x/y", CGStringHelper.getDirectoryFromPackage ("x.y"));
    assertEquals ("the/little/white/cat", CGStringHelper.getDirectoryFromPackage ("the.little.white.cat"));

    try
    {
      // not allowed
      CGStringHelper.getDirectoryFromPackage ((Package) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // not allowed
      CGStringHelper.getDirectoryFromPackage ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetPathFromClass ()
  {
    assertEquals ("java/lang/String", CGStringHelper.getPathFromClass (String.class));
    assertEquals ("", CGStringHelper.getPathFromClass (""));
    assertEquals ("x", CGStringHelper.getPathFromClass ("x"));
    assertEquals ("x/y", CGStringHelper.getPathFromClass ("x.y"));
    assertEquals ("the/little/white/cat", CGStringHelper.getPathFromClass ("the.little.white.cat"));

    try
    {
      // not allowed
      CGStringHelper.getPathFromClass ((Class <?>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // not allowed
      CGStringHelper.getPathFromClass ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetClassFromPath ()
  {
    assertEquals ("java.lang.String", CGStringHelper.getClassFromPath ("java/lang/String"));
    assertEquals ("", CGStringHelper.getClassFromPath (""));
    assertEquals ("x", CGStringHelper.getClassFromPath ("x"));
    assertEquals ("x.y", CGStringHelper.getClassFromPath ("x/y"));
    assertEquals ("x.y", CGStringHelper.getClassFromPath ("x\\y"));
    assertEquals ("the.little.white.cat", CGStringHelper.getClassFromPath ("the\\little/white\\cat"));

    try
    {
      // not allowed
      CGStringHelper.getClassFromPath ((String) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testGetObjectAddress ()
  {
    assertTrue (CGStringHelper.getObjectAddress (null).startsWith ("0x00000000"));
    assertNotNull (CGStringHelper.getObjectAddress (""));
    assertNotNull (CGStringHelper.getObjectAddress (String.class));
    assertNotNull (CGStringHelper.getObjectAddress (Double.valueOf (4)));
  }
}
