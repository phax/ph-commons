/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.base;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.clone.ICloneable;
import com.helger.base.equals.EqualsHelper;

/**
 * This class contains default test methods to test the correctness of implementations of standard
 * methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class BaseTestHelper
{
  @PresentForCodeCoverage
  private static final BaseTestHelper INSTANCE = new BaseTestHelper ();

  private BaseTestHelper ()
  {}

  private static <DATATYPE> void _testEqualsImplementation (@NonNull final DATATYPE aObject)
  {
    assertNotNull ("Passed object may not be null!", aObject);
    assertTrue ("Passed objects are not equal", aObject.equals (aObject));
    assertFalse ("Object may no be equal to String", aObject.equals ("any string"));
    assertFalse ("Object may no be equal to String", "any string".equals (aObject));
    assertFalse ("Object may no be equal to null", aObject.equals (null));
  }

  public static <DATATYPE> void testEqualsImplementationWithEqualContentObject (@NonNull final DATATYPE aObject,
                                                                                @NonNull final DATATYPE aObject2)
  {
    _testEqualsImplementation (aObject);
    _testEqualsImplementation (aObject2);
    assertFalse ("This test may not be used with the same object!", EqualsHelper.identityEqual (aObject, aObject2));
    assertTrue ("Passed objects are not identical!", aObject.equals (aObject2));
    assertTrue ("Passed objects are not identical!", aObject2.equals (aObject));
  }

  public static <DATATYPE> void testEqualsImplementationWithDifferentContentObject (@NonNull final DATATYPE aObject,
                                                                                    @NonNull final DATATYPE aObject2)
  {
    _testEqualsImplementation (aObject);
    _testEqualsImplementation (aObject2);
    assertFalse ("This test may not be used with the same object!", EqualsHelper.identityEqual (aObject, aObject2));
    assertFalse ("Passed objects are identical!", aObject.equals (aObject2));
    assertFalse ("Passed objects are identical!", aObject2.equals (aObject));
  }

  private static <DATATYPE> void _testHashcodeImplementation (@NonNull final DATATYPE aObject)
  {
    assertNotNull ("Passed object may not be null!", aObject);
    assertTrue ("hashCode() invocations must be consistent", aObject.hashCode () == aObject.hashCode ());
    assertFalse ("hashCode() may not be 0", aObject.hashCode () == 0);
  }

  public static <DATATYPE> void testHashcodeImplementationWithEqualContentObject (@NonNull final DATATYPE aObject,
                                                                                  @NonNull final DATATYPE aObject2)
  {
    _testHashcodeImplementation (aObject);
    _testHashcodeImplementation (aObject2);
    assertTrue ("Passed objects are not identical!", aObject.equals (aObject2));
    assertFalse ("This test may not be used with the same object!", aObject == aObject2);
    assertTrue ("hashCode() invocations must be consistent", aObject.hashCode () == aObject2.hashCode ());
    assertTrue ("hashCode() invocations must be consistent", aObject2.hashCode () == aObject.hashCode ());
  }

  public static <DATATYPE> void testHashcodeImplementationWithDifferentContentObject (@NonNull final DATATYPE aObject1,
                                                                                      @NonNull final DATATYPE aObject2)
  {
    _testHashcodeImplementation (aObject1);
    _testHashcodeImplementation (aObject2);
    assertFalse ("Passed objects are identical!", aObject1.equals (aObject2));
    assertFalse ("This test may not be used with the same object!", EqualsHelper.identityEqual (aObject1, aObject2));
    final int nHash1 = aObject1.hashCode ();
    final int nHash2 = aObject2.hashCode ();
    assertFalse ("hashCode() may not be the same for both objects", nHash1 == nHash2);
  }

  /**
   * Test the toString implementation of the passed object. It may not be empty, and consistent.
   *
   * @param aObject
   *        The object to be tested.
   */
  public static void testToStringImplementation (@NonNull final Object aObject)
  {
    assertNotNull ("Passed object may not be null!", aObject);
    assertNotNull ("toString() may not return null!", aObject.toString ());
    assertTrue ("toString() may not return an empty string!", aObject.toString ().length () > 0);
    assertTrue ("toString() invocations must be consistent", aObject.toString ().equals (aObject.toString ()));
  }

  public static <DATATYPE> void testToStringImplementationWithEqualContentObject (@NonNull final DATATYPE aObject,
                                                                                  @NonNull final DATATYPE aObject2)
  {
    testToStringImplementation (aObject);
    testToStringImplementation (aObject2);
    assertTrue ("Passed objects are not identical!", aObject.equals (aObject2));
    assertFalse ("This test may not be used with the same object!", EqualsHelper.identityEqual (aObject, aObject2));
  }

  public static <DATATYPE> void testToStringImplementationWithDifferentContentObject (@NonNull final DATATYPE aObject,
                                                                                      @NonNull final DATATYPE aObject2)
  {
    testToStringImplementation (aObject);
    testToStringImplementation (aObject2);
    assertFalse ("Passed objects are identical!", aObject.equals (aObject2));
    assertFalse ("This test may not be used with the same object!", EqualsHelper.identityEqual (aObject, aObject2));
  }

  /**
   * Check if two different objects (who may not be the same) are equal to each other. Checks
   * toString, equals and hashCode.
   *
   * @param <DATATYPE>
   *        The data type to be used
   * @param aObject1
   *        First object. May not be <code>null</code>.
   * @param aObject2
   *        Second object. May not be <code>null</code>.
   */
  public static <DATATYPE> void testDefaultImplementationWithEqualContentObject (@NonNull final DATATYPE aObject1,
                                                                                 @NonNull final DATATYPE aObject2)
  {
    testEqualsImplementationWithEqualContentObject (aObject1, aObject2);
    testHashcodeImplementationWithEqualContentObject (aObject1, aObject2);
    testToStringImplementationWithEqualContentObject (aObject1, aObject2);
  }

  /**
   * Check if two different objects are different to each other. Checks toString, equals and
   * hashCode.
   *
   * @param <DATATYPE>
   *        The data type to be used
   * @param aObject1
   *        First object. May not be <code>null</code>.
   * @param aObject2
   *        Second object. May not be <code>null</code>.
   */
  public static <DATATYPE> void testDefaultImplementationWithDifferentContentObject (@NonNull final DATATYPE aObject1,
                                                                                     @NonNull final DATATYPE aObject2)
  {
    testEqualsImplementationWithDifferentContentObject (aObject1, aObject2);
    testHashcodeImplementationWithDifferentContentObject (aObject1, aObject2);
    testToStringImplementationWithDifferentContentObject (aObject1, aObject2);
  }

  /**
   * Test if the implementation {@link ICloneable} is OK. It creates a clone and than uses
   * {@link #testDefaultImplementationWithEqualContentObject(Object, Object)} to check for equality.
   *
   * @param aCloneable
   *        The cloneable object to test
   */
  public static void testGetClone (@NonNull final ICloneable <?> aCloneable)
  {
    final Object aClone = aCloneable.getClone ();
    assertNotNull ("Clone returned a null object", aClone);
    assertTrue ("Clone returned a different class than the original one",
                aClone.getClass ().equals (aCloneable.getClass ()));
    testDefaultImplementationWithEqualContentObject (aCloneable, aClone);
  }
}
