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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import com.helger.commons.annotation.ReturnsImmutableObject;
import com.helger.commons.name.IHasName;
import com.helger.commons.system.EOperatingSystem;

/**
 * Test class for class {@link ClassHelper}.
 *
 * @author Philip Helger
 */
public final class ClassHelperTest
{
  static final class DummyDefaultVisibility
  {
    DummyDefaultVisibility ()
    {}
  }

  protected static final class DummyProtectedVisibility
  {
    protected DummyProtectedVisibility ()
    {}
  }

  private static final class DummyPrivateVisibility
  {
    private DummyPrivateVisibility ()
    {}
  }

  public abstract static class DummyAbstract
  {}

  @Test
  public void testGetClassLoader ()
  {
    assertNotNull (ClassLoaderHelper.getDefaultClassLoader ());
  }

  @Test
  public void testIsPublicClass ()
  {
    // null is not
    assertFalse (ClassHelper.isPublicClass (null));

    // annotation is not
    assertFalse (ClassHelper.isPublicClass (Override.class));

    // interface is not
    assertFalse (ClassHelper.isPublicClass (IHasName.class));

    // visibilities
    assertFalse (ClassHelper.isPublicClass (DummyDefaultVisibility.class));
    assertFalse (ClassHelper.isPublicClass (DummyProtectedVisibility.class));
    assertFalse (ClassHelper.isPublicClass (DummyPrivateVisibility.class));

    // abstract class
    assertFalse (ClassHelper.isPublicClass (DummyAbstract.class));

    // valid:
    assertTrue (ClassHelper.isPublicClass (String.class));
    assertTrue (ClassHelper.isPublicClass (new String [0].getClass ()));
    assertTrue (ClassHelper.isPublicClass (new DummyAbstract [0].getClass ()));
    assertTrue (ClassHelper.isPublicClass (getClass ()));
    assertTrue (ClassHelper.isPublicClass (ClassHelper.class));
    assertTrue (ClassHelper.isPublicClass (EOperatingSystem.class));
  }

  @Test
  public void testIsInstancableClass ()
  {
    // null is not
    assertFalse (ClassHelper.isInstancableClass (null));

    // annotation is not
    assertFalse (ClassHelper.isInstancableClass (Override.class));

    // interface is not
    assertFalse (ClassHelper.isInstancableClass (IHasName.class));

    // visibilities
    assertFalse (ClassHelper.isInstancableClass (DummyDefaultVisibility.class));
    assertFalse (ClassHelper.isInstancableClass (DummyProtectedVisibility.class));
    assertFalse (ClassHelper.isInstancableClass (DummyPrivateVisibility.class));

    // abstract class
    assertFalse (ClassHelper.isInstancableClass (DummyAbstract.class));

    // valid:
    assertTrue (ClassHelper.isInstancableClass (String.class));
    assertTrue (ClassHelper.isInstancableClass (getClass ()));

    // No default constructor present (all static)
    assertFalse (ClassHelper.isInstancableClass (ClassHelper.class));

    // Not valid: array
    assertFalse (ClassHelper.isInstancableClass (new String [0].getClass ()));
    assertFalse (ClassHelper.isInstancableClass (new DummyAbstract [0].getClass ()));

    // Not valid: enum
    assertFalse (ClassHelper.isInstancableClass (EOperatingSystem.class));
  }

  @Test
  public void testIsPublic ()
  {
    assertFalse (ClassHelper.isPublic (null));
    assertTrue (ClassHelper.isPublic (Override.class));
    assertTrue (ClassHelper.isPublic (ReturnsImmutableObject.class));
    assertTrue (ClassHelper.isPublic (IHasName.class));
    assertTrue (ClassHelper.isPublic (getClass ()));
    assertTrue (ClassHelper.isPublic (new String [0].getClass ()));
    assertTrue (ClassHelper.isPublic (new DummyAbstract [0].getClass ()));
    assertTrue (ClassHelper.isPublic (EOperatingSystem.class));
    assertFalse (ClassHelper.isPublic (DummyDefaultVisibility.class));
    assertFalse (ClassHelper.isPublic (DummyProtectedVisibility.class));
    assertFalse (ClassHelper.isPublic (DummyPrivateVisibility.class));
    assertTrue (ClassHelper.isPublic (DummyAbstract.class));
  }

  @Test
  public void testIsInterface ()
  {
    assertFalse (ClassHelper.isInterface (null));
    assertTrue (ClassHelper.isInterface (Override.class));
    assertTrue (ClassHelper.isInterface (ReturnsImmutableObject.class));
    assertTrue (ClassHelper.isInterface (IHasName.class));
    assertFalse (ClassHelper.isInterface (getClass ()));
    assertFalse (ClassHelper.isInterface (new String [0].getClass ()));
    assertFalse (ClassHelper.isInterface (new DummyAbstract [0].getClass ()));
    assertFalse (ClassHelper.isInterface (EOperatingSystem.class));
    assertFalse (ClassHelper.isInterface (DummyDefaultVisibility.class));
    assertFalse (ClassHelper.isInterface (DummyProtectedVisibility.class));
    assertFalse (ClassHelper.isInterface (DummyPrivateVisibility.class));
    assertFalse (ClassHelper.isInterface (DummyAbstract.class));
  }

  @Test
  public void testIsAnnotation ()
  {
    assertFalse (ClassHelper.isAnnotationClass (null));
    assertTrue (ClassHelper.isAnnotationClass (Override.class));
    assertTrue (ClassHelper.isAnnotationClass (ReturnsImmutableObject.class));
    assertFalse (ClassHelper.isAnnotationClass (IHasName.class));
    assertFalse (ClassHelper.isAnnotationClass (getClass ()));
    assertFalse (ClassHelper.isAnnotationClass (new String [0].getClass ()));
    assertFalse (ClassHelper.isAnnotationClass (new DummyAbstract [0].getClass ()));
    assertFalse (ClassHelper.isAnnotationClass (EOperatingSystem.class));
    assertFalse (ClassHelper.isAnnotationClass (DummyDefaultVisibility.class));
    assertFalse (ClassHelper.isAnnotationClass (DummyProtectedVisibility.class));
    assertFalse (ClassHelper.isAnnotationClass (DummyPrivateVisibility.class));
    assertFalse (ClassHelper.isAnnotationClass (DummyAbstract.class));
  }

  @Test
  public void testIsEnum ()
  {
    assertFalse (ClassHelper.isEnumClass (null));
    assertFalse (ClassHelper.isEnumClass (Override.class));
    assertFalse (ClassHelper.isEnumClass (ReturnsImmutableObject.class));
    assertFalse (ClassHelper.isEnumClass (IHasName.class));
    assertFalse (ClassHelper.isEnumClass (getClass ()));
    assertFalse (ClassHelper.isEnumClass (new String [0].getClass ()));
    assertFalse (ClassHelper.isEnumClass (new DummyAbstract [0].getClass ()));
    assertTrue (ClassHelper.isEnumClass (EOperatingSystem.class));
    assertFalse (ClassHelper.isEnumClass (DummyDefaultVisibility.class));
    assertFalse (ClassHelper.isEnumClass (DummyProtectedVisibility.class));
    assertFalse (ClassHelper.isEnumClass (DummyPrivateVisibility.class));
    assertFalse (ClassHelper.isEnumClass (DummyAbstract.class));
  }

  @Test
  public void testIsAbstract ()
  {
    assertFalse (ClassHelper.isAbstractClass (null));
    assertTrue (ClassHelper.isAbstractClass (Override.class));
    assertTrue (ClassHelper.isAbstractClass (ReturnsImmutableObject.class));
    assertTrue (ClassHelper.isAbstractClass (IHasName.class));
    assertFalse (ClassHelper.isAbstractClass (getClass ()));
    assertFalse (ClassHelper.isAbstractClass (new String [0].getClass ()));
    assertFalse (ClassHelper.isAbstractClass (new DummyAbstract [0].getClass ()));
    assertFalse (ClassHelper.isAbstractClass (EOperatingSystem.class));
    assertFalse (ClassHelper.isAbstractClass (DummyDefaultVisibility.class));
    assertFalse (ClassHelper.isAbstractClass (DummyProtectedVisibility.class));
    assertFalse (ClassHelper.isAbstractClass (DummyPrivateVisibility.class));
    assertTrue (ClassHelper.isAbstractClass (DummyAbstract.class));
  }

  @Test
  public void testIsArray ()
  {
    assertFalse (ClassHelper.isArrayClass (null));
    assertFalse (ClassHelper.isArrayClass (Override.class));
    assertFalse (ClassHelper.isArrayClass (ReturnsImmutableObject.class));
    assertFalse (ClassHelper.isArrayClass (IHasName.class));
    assertFalse (ClassHelper.isArrayClass (getClass ()));
    assertTrue (ClassHelper.isArrayClass (new String [0].getClass ()));
    assertTrue (ClassHelper.isArrayClass (new DummyAbstract [0].getClass ()));
    assertFalse (ClassHelper.isArrayClass (EOperatingSystem.class));
    assertFalse (ClassHelper.isArrayClass (DummyDefaultVisibility.class));
    assertFalse (ClassHelper.isArrayClass (DummyProtectedVisibility.class));
    assertFalse (ClassHelper.isArrayClass (DummyPrivateVisibility.class));
    assertFalse (ClassHelper.isArrayClass (DummyAbstract.class));
  }

  @Test
  public void testPrimitivesAndWrappers ()
  {
    assertTrue (ClassHelper.isPrimitiveType (boolean.class));
    assertTrue (ClassHelper.isPrimitiveType (byte.class));
    assertTrue (ClassHelper.isPrimitiveType (char.class));
    assertTrue (ClassHelper.isPrimitiveType (double.class));
    assertTrue (ClassHelper.isPrimitiveType (float.class));
    assertTrue (ClassHelper.isPrimitiveType (int.class));
    assertTrue (ClassHelper.isPrimitiveType (long.class));
    assertTrue (ClassHelper.isPrimitiveType (short.class));
    assertFalse (ClassHelper.isPrimitiveType (Boolean.class));
    assertFalse (ClassHelper.isPrimitiveType (Byte.class));
    assertFalse (ClassHelper.isPrimitiveType (Character.class));
    assertFalse (ClassHelper.isPrimitiveType (Double.class));
    assertFalse (ClassHelper.isPrimitiveType (Float.class));
    assertFalse (ClassHelper.isPrimitiveType (Integer.class));
    assertFalse (ClassHelper.isPrimitiveType (Long.class));
    assertFalse (ClassHelper.isPrimitiveType (Short.class));
    assertFalse (ClassHelper.isPrimitiveType (String.class));
    assertFalse (ClassHelper.isPrimitiveType (Reader.class));

    assertFalse (ClassHelper.isPrimitiveWrapperType (boolean.class));
    assertFalse (ClassHelper.isPrimitiveWrapperType (byte.class));
    assertFalse (ClassHelper.isPrimitiveWrapperType (char.class));
    assertFalse (ClassHelper.isPrimitiveWrapperType (double.class));
    assertFalse (ClassHelper.isPrimitiveWrapperType (float.class));
    assertFalse (ClassHelper.isPrimitiveWrapperType (int.class));
    assertFalse (ClassHelper.isPrimitiveWrapperType (long.class));
    assertFalse (ClassHelper.isPrimitiveWrapperType (short.class));
    assertTrue (ClassHelper.isPrimitiveWrapperType (Boolean.class));
    assertTrue (ClassHelper.isPrimitiveWrapperType (Byte.class));
    assertTrue (ClassHelper.isPrimitiveWrapperType (Character.class));
    assertTrue (ClassHelper.isPrimitiveWrapperType (Double.class));
    assertTrue (ClassHelper.isPrimitiveWrapperType (Float.class));
    assertTrue (ClassHelper.isPrimitiveWrapperType (Integer.class));
    assertTrue (ClassHelper.isPrimitiveWrapperType (Long.class));
    assertTrue (ClassHelper.isPrimitiveWrapperType (Short.class));
    assertFalse (ClassHelper.isPrimitiveWrapperType (String.class));
    assertFalse (ClassHelper.isPrimitiveWrapperType (Reader.class));

    assertSame (byte.class, ClassHelper.getPrimitiveClass (byte.class));
    assertSame (byte.class, ClassHelper.getPrimitiveClass (Byte.class));
    assertNull (ClassHelper.getPrimitiveClass (String.class));

    assertSame (Byte.class, ClassHelper.getPrimitiveWrapperClass (byte.class));
    assertSame (Byte.class, ClassHelper.getPrimitiveWrapperClass (Byte.class));
    assertNull (ClassHelper.getPrimitiveWrapperClass (String.class));
  }

  @Test
  public void testGetAllPrimtives ()
  {
    assertTrue (ClassHelper.getAllPrimitiveClasses ().contains (byte.class));
    assertFalse (ClassHelper.getAllPrimitiveClasses ().contains (Byte.class));
    assertFalse (ClassHelper.getAllPrimitiveWrapperClasses ().contains (byte.class));
    assertTrue (ClassHelper.getAllPrimitiveWrapperClasses ().contains (Byte.class));
  }

  @Test
  public void testIsStringClass ()
  {
    assertTrue (ClassHelper.isStringClass (String.class));
    assertTrue (ClassHelper.isStringClass (StringBuilder.class));
    assertFalse (ClassHelper.isStringClass (null));
    assertFalse (ClassHelper.isStringClass (Integer.class));
    assertFalse (ClassHelper.isStringClass (int.class));
  }

  @Test
  public void testIsCharacterClass ()
  {
    assertTrue (ClassHelper.isCharacterClass (Character.class));
    assertTrue (ClassHelper.isCharacterClass (char.class));
    assertFalse (ClassHelper.isCharacterClass (null));
    assertFalse (ClassHelper.isCharacterClass (Integer.class));
    assertFalse (ClassHelper.isCharacterClass (int.class));
  }

  @Test
  public void testIsBooleanClass ()
  {
    assertTrue (ClassHelper.isBooleanClass (Boolean.class));
    assertTrue (ClassHelper.isBooleanClass (boolean.class));
    assertFalse (ClassHelper.isBooleanClass (null));
    assertFalse (ClassHelper.isBooleanClass (Integer.class));
    assertFalse (ClassHelper.isBooleanClass (int.class));
  }

  @Test
  public void testIsFloatingPointClass ()
  {
    assertTrue (ClassHelper.isFloatingPointClass (Double.class));
    assertTrue (ClassHelper.isFloatingPointClass (double.class));
    assertTrue (ClassHelper.isFloatingPointClass (Float.class));
    assertTrue (ClassHelper.isFloatingPointClass (float.class));
    assertTrue (ClassHelper.isFloatingPointClass (BigDecimal.class));
    assertFalse (ClassHelper.isFloatingPointClass (null));
    assertFalse (ClassHelper.isFloatingPointClass (Integer.class));
    assertFalse (ClassHelper.isFloatingPointClass (int.class));
  }

  @Test
  public void testIsIntegerClass ()
  {
    assertTrue (ClassHelper.isIntegerClass (byte.class));
    assertTrue (ClassHelper.isIntegerClass (byte.class));
    assertTrue (ClassHelper.isIntegerClass (int.class));
    assertTrue (ClassHelper.isIntegerClass (Integer.class));
    assertTrue (ClassHelper.isIntegerClass (long.class));
    assertTrue (ClassHelper.isIntegerClass (Long.class));
    assertTrue (ClassHelper.isIntegerClass (short.class));
    assertTrue (ClassHelper.isIntegerClass (Short.class));
    assertTrue (ClassHelper.isIntegerClass (BigInteger.class));
    assertFalse (ClassHelper.isIntegerClass (null));
    assertFalse (ClassHelper.isIntegerClass (String.class));
  }

  @Test
  public void testGetClassLocalNameClassOfQ ()
  {
    assertEquals ("String", ClassHelper.getClassLocalName ((Object) ""));
    assertEquals ("ClassHelper", ClassHelper.getClassLocalName (ClassHelper.class));
    assertEquals ("ClassHelper", ClassHelper.getClassLocalName (ClassHelper.class.getName ()));
    assertEquals ("Test", ClassHelper.getClassLocalName ("Test"));
    assertEquals ("", ClassHelper.getClassLocalName (""));
    assertNull (ClassHelper.getClassLocalName ((Object) null));
    assertNull (ClassHelper.getClassLocalName ((String) null));
    assertNull (ClassHelper.getClassLocalName ((Class <?>) null));
  }

  @Test
  public void testGetClassPackageName ()
  {
    assertEquals ("java.lang", ClassHelper.getClassPackageName (String.class));
    assertEquals ("a.b", ClassHelper.getClassPackageName ("a.b.c"));
    assertEquals ("a.b", ClassHelper.getClassPackageName ("a.b.c2"));
    assertEquals ("abra.bbra", ClassHelper.getClassPackageName ("abra.bbra.c2"));
    assertEquals ("", ClassHelper.getClassPackageName ("ClassNameOnly"));
    assertEquals ("x", ClassHelper.getClassPackageName ("x.ClassNameOnly"));
    assertEquals ("java.lang", ClassHelper.getClassPackageName (String.class));
    assertNull (ClassHelper.getClassPackageName ((Class <?>) null));
    assertNull (ClassHelper.getClassPackageName ((String) null));
  }

  @Test
  public void testGetSafeClassName ()
  {
    assertEquals ("null", ClassHelper.getSafeClassName (null));
    assertEquals ("java.lang.String", ClassHelper.getSafeClassName (String.class));
    assertEquals ("java.lang.String", ClassHelper.getSafeClassName ("Lol"));
  }

  @Test
  public void testGetDirectoryFromPackage ()
  {
    assertEquals ("java/lang", ClassHelper.getDirectoryFromPackage (String.class.getPackage ()));
    assertEquals ("", ClassHelper.getDirectoryFromPackage (""));
    assertEquals ("x", ClassHelper.getDirectoryFromPackage ("x"));
    assertEquals ("x/y", ClassHelper.getDirectoryFromPackage ("x.y"));
    assertEquals ("the/little/white/cat", ClassHelper.getDirectoryFromPackage ("the.little.white.cat"));

    assertNull (ClassHelper.getDirectoryFromPackage ((Package) null));
    assertNull (ClassHelper.getDirectoryFromPackage ((String) null));
  }

  @Test
  public void testGetPathFromClass ()
  {
    assertEquals ("java/lang/String", ClassHelper.getPathFromClass (String.class));
    assertEquals ("", ClassHelper.getPathFromClass (""));
    assertEquals ("x", ClassHelper.getPathFromClass ("x"));
    assertEquals ("x/y", ClassHelper.getPathFromClass ("x.y"));
    assertEquals ("the/little/white/cat", ClassHelper.getPathFromClass ("the.little.white.cat"));
    assertNull (ClassHelper.getPathFromClass ((Class <?>) null));
    assertNull (ClassHelper.getPathFromClass ((String) null));
  }

  @Test
  public void testGetClassFromPath ()
  {
    assertEquals ("java.lang.String", ClassHelper.getClassFromPath ("java/lang/String"));
    assertEquals ("", ClassHelper.getClassFromPath (""));
    assertEquals ("x", ClassHelper.getClassFromPath ("x"));
    assertEquals ("x.y", ClassHelper.getClassFromPath ("x/y"));
    assertEquals ("x.y", ClassHelper.getClassFromPath ("x\\y"));
    assertEquals ("the.little.white.cat", ClassHelper.getClassFromPath ("the\\little/white\\cat"));
    assertNull (ClassHelper.getClassFromPath ((String) null));
  }

  @Test
  public void testGetObjectAddress ()
  {
    assertEquals ("0x00000000", ClassHelper.getObjectAddress (null));
    assertNotNull (ClassHelper.getObjectAddress (""));
    assertNotNull (ClassHelper.getObjectAddress (String.class));
    assertNotNull (ClassHelper.getObjectAddress (Double.valueOf (4)));
  }
}
