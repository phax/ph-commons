/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.unittest.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.clone.ICloneable;
import com.helger.base.concurrent.ExecutorServiceHelper;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.iface.IThrowingRunnable;
import com.helger.base.lang.IExplicitlyCloneable;
import com.helger.base.rt.StackTraceHelper;
import com.helger.base.serialize.SerializationHelper;
import com.helger.base.string.StringImplode;

/**
 * This class contains default test methods to test the correctness of implementations of standard
 * methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class TestHelper
{
  @PresentForCodeCoverage
  private static final TestHelper INSTANCE = new TestHelper ();

  private TestHelper ()
  {}

  private static <DATATYPE> void _testEqualsImplementation (@NonNull final DATATYPE aObject)
  {
    assertNotNull ("Passed object may not be null!", aObject);
    assertEquals ("Passed objects are not equal", aObject, aObject);
    assertNotEquals ("Object may no be equal to String", aObject, "any string");
    assertNotEquals ("Object may no be equal to String", "any string", aObject);
    assertNotNull ("Object may no be equal to null", aObject);
  }

  public static <DATATYPE> void testEqualsImplementationWithEqualContentObject (@NonNull final DATATYPE aObject,
                                                                                @NonNull final DATATYPE aObject2)
  {
    _testEqualsImplementation (aObject);
    _testEqualsImplementation (aObject2);
    assertFalse ("This test may not be used with the same object!", EqualsHelper.identityEqual (aObject, aObject2));
    assertEquals ("Passed objects are not identical!", aObject, aObject2);
    assertEquals ("Passed objects are not identical!", aObject2, aObject);
  }

  public static <DATATYPE> void testEqualsImplementationWithDifferentContentObject (@NonNull final DATATYPE aObject,
                                                                                    @NonNull final DATATYPE aObject2)
  {
    _testEqualsImplementation (aObject);
    _testEqualsImplementation (aObject2);
    assertFalse ("This test may not be used with the same object!", EqualsHelper.identityEqual (aObject, aObject2));
    assertNotEquals ("Passed objects are identical!", aObject, aObject2);
    assertNotEquals ("Passed objects are identical!", aObject2, aObject);
  }

  private static <DATATYPE> void _testHashCodeImplementation (@NonNull final DATATYPE aObject)
  {
    assertNotNull ("Passed object may not be null!", aObject);
    assertEquals ("hashCode() invocations must be consistent", aObject.hashCode (), aObject.hashCode ());
    assertNotEquals ("hashCode() may not be 0", aObject.hashCode (), 0);
  }

  public static <DATATYPE> void testHashCodeImplementationWithEqualContentObject (@NonNull final DATATYPE aObject,
                                                                                  @NonNull final DATATYPE aObject2)
  {
    _testHashCodeImplementation (aObject);
    _testHashCodeImplementation (aObject2);
    assertEquals ("Passed objects are not identical!", aObject, aObject2);
    assertFalse ("This test may not be used with the same object!", EqualsHelper.identityEqual (aObject, aObject2));
    assertEquals ("hashCode() invocations must be consistent", aObject.hashCode (), aObject2.hashCode ());
    assertEquals ("hashCode() invocations must be consistent", aObject2.hashCode (), aObject.hashCode ());
  }

  public static <DATATYPE> void testHashCodeImplementationWithDifferentContentObject (@NonNull final DATATYPE aObject1,
                                                                                      @NonNull final DATATYPE aObject2)
  {
    _testHashCodeImplementation (aObject1);
    _testHashCodeImplementation (aObject2);
    assertNotEquals ("Passed objects are identical!", aObject1, aObject2);
    assertFalse ("This test may not be used with the same object!", EqualsHelper.identityEqual (aObject1, aObject2));
    final int nHash1 = aObject1.hashCode ();
    final int nHash2 = aObject2.hashCode ();
    assertNotEquals ("hashCode() may not be the same for both objects", nHash1, nHash2);
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
    assertEquals ("toString() invocations must be consistent", aObject.toString (), aObject.toString ());
  }

  public static <DATATYPE> void testToStringImplementationWithEqualContentObject (@NonNull final DATATYPE aObject,
                                                                                  @NonNull final DATATYPE aObject2)
  {
    testToStringImplementation (aObject);
    testToStringImplementation (aObject2);
    assertEquals ("Passed objects are not identical!", aObject, aObject2);
    assertFalse ("This test may not be used with the same object!", EqualsHelper.identityEqual (aObject, aObject2));
  }

  public static <DATATYPE> void testToStringImplementationWithDifferentContentObject (@NonNull final DATATYPE aObject,
                                                                                      @NonNull final DATATYPE aObject2)
  {
    testToStringImplementation (aObject);
    testToStringImplementation (aObject2);
    assertNotEquals ("Passed objects are identical!", aObject, aObject2);
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
    testHashCodeImplementationWithEqualContentObject (aObject1, aObject2);
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
    testHashCodeImplementationWithDifferentContentObject (aObject1, aObject2);
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
    assertEquals ("Clone returned a different class than the original one", aClone.getClass (), aCloneable.getClass ());
    testDefaultImplementationWithEqualContentObject (aCloneable, aClone);
  }

  /**
   * Test if the implementation {@link IExplicitlyCloneable} is OK. It creates a clone and than uses
   * {@link #testDefaultImplementationWithEqualContentObject(Object, Object)} to check for equality.
   *
   * @param aCloneable
   *        The cloneable object to test
   * @since 9.4.5
   */
  public static void testClone (@NonNull final IExplicitlyCloneable aCloneable)
  {
    try
    {
      final Object aClone = aCloneable.clone ();
      assertNotNull ("Clone returned a null object", aClone);
      assertEquals ("Clone returned a different class than the original one",
                    aClone.getClass (),
                    aCloneable.getClass ());
      testDefaultImplementationWithEqualContentObject (aCloneable, aClone);
    }
    catch (final CloneNotSupportedException ex)
    {
      throw new IllegalStateException ("Clone not supported", ex);
    }
  }

  /**
   * Test the serializability of objects. First writes the object to a byte array stream, and then
   * tries to rebuild it from there. After reading it performs an equals check using
   * {@link #testDefaultImplementationWithEqualContentObject(Object, Object)}
   *
   * @param <DATATYPE>
   *        The type of object to be serialized.
   * @param aSerializable
   *        The object to be written and read
   * @return The newly read object
   */
  @NonNull
  public static <DATATYPE extends Serializable> DATATYPE testDefaultSerialization (@NonNull final DATATYPE aSerializable)
  {
    // Serialize to byte array
    final byte [] aBytes = SerializationHelper.getSerializedByteArray (aSerializable);

    // Read new object from byte array
    final DATATYPE aReadObject = SerializationHelper.getDeserializedObject (aBytes);

    // Now check them for equality
    testDefaultImplementationWithEqualContentObject (aSerializable, aReadObject);
    return aReadObject;
  }

  /**
   * Run something in parallel
   *
   * @param nCalls
   *        The number of invocations of the passed runnable. Must be &ge; 0.
   * @param aRunnable
   *        The runnable to execute. May not be <code>null</code>.
   */
  public static void testInParallel (@Nonnegative final int nCalls,
                                     @NonNull final IThrowingRunnable <? extends Exception> aRunnable)
  {
    ValueEnforcer.isGE0 (nCalls, "Calls");
    ValueEnforcer.notNull (aRunnable, "Runnable");

    // More than 20s thread would be overkill!
    final ExecutorService aES = Executors.newFixedThreadPool (20);
    final List <String> aErrors = new Vector <> ();
    for (int i = 0; i < nCalls; ++i)
    {
      aES.submit ( () -> {
        try
        {
          aRunnable.run ();
        }
        catch (final Exception ex)
        {
          // Remember thread stack
          aErrors.add (ex.getMessage () + "\n" + StackTraceHelper.getStackAsString (ex));
        }
      });
    }
    ExecutorServiceHelper.shutdownAndWaitUntilAllTasksAreFinished (aES);

    // No errors should have occurred
    if (!aErrors.isEmpty ())
      fail (StringImplode.getImploded (aErrors));
  }
}
