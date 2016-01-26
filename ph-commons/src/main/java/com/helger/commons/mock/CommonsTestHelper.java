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
package com.helger.commons.mock;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.callback.IThrowingRunnable;
import com.helger.commons.callback.adapter.AdapterRunnableToThrowingRunnable;
import com.helger.commons.charset.CCharset;
import com.helger.commons.concurrent.ManagedExecutorService;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.iterate.FileSystemIterator;
import com.helger.commons.io.stream.NonBlockingBufferedReader;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.io.streamprovider.ByteArrayInputStreamProvider;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.lang.StackTraceHelper;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.convert.MicroTypeConverter;
import com.helger.commons.microdom.serialize.MicroWriter;
import com.helger.commons.string.StringHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This class contains default test methods to test the correctness of
 * implementations of standard methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class CommonsTestHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (CommonsTestHelper.class);

  @PresentForCodeCoverage
  private static final CommonsTestHelper s_aInstance = new CommonsTestHelper ();

  private CommonsTestHelper ()
  {}

  private static void _assertTrue (@Nonnull final String sMsg, final boolean bTrue)
  {
    if (!bTrue)
      throw new IllegalStateException (sMsg);
  }

  private static void _assertFalse (@Nonnull final String sMsg, final boolean bTrue)
  {
    if (bTrue)
      throw new IllegalStateException (sMsg);
  }

  private static void _assertNotNull (@Nonnull final String sMsg, final Object aObj)
  {
    if (aObj == null)
      throw new IllegalStateException (sMsg);
  }

  private static <T> void _assertEquals (@Nonnull final String sMsg, @Nullable final T aObj1, @Nullable final T aObj2)
  {
    if (!EqualsHelper.equals (aObj1, aObj2))
      throw new IllegalStateException (sMsg + "\nOBJ1: " + aObj1 + "\nOBJ2: " + aObj2);
  }

  @SuppressFBWarnings ({ "EC_NULL_ARG" })
  private static <DATATYPE> void _testEqualsImplementation (@Nonnull final DATATYPE aObject)
  {
    _assertNotNull ("Passed object may not be null!", aObject);
    _assertTrue ("Passed objects are not equal", aObject.equals (aObject));
    _assertFalse ("Object may no be equal to String", aObject.equals ("any string"));
    _assertFalse ("Object may no be equal to null", aObject.equals (null));
  }

  public static <DATATYPE> void testEqualsImplementationWithEqualContentObject (@Nonnull final DATATYPE aObject,
                                                                                @Nonnull final DATATYPE aObject2)
  {
    _testEqualsImplementation (aObject);
    _testEqualsImplementation (aObject2);
    _assertFalse ("This test may not be used with the same object!", aObject == aObject2);
    _assertTrue ("Passed objects are not identical!", aObject.equals (aObject2));
    _assertTrue ("Passed objects are not identical!", aObject2.equals (aObject));
  }

  public static <DATATYPE> void testEqualsImplementationWithDifferentContentObject (@Nonnull final DATATYPE aObject,
                                                                                    @Nonnull final DATATYPE aObject2)
  {
    _testEqualsImplementation (aObject);
    _testEqualsImplementation (aObject2);
    _assertFalse ("This test may not be used with the same object!", aObject == aObject2);
    _assertFalse ("Passed objects are identical!", aObject.equals (aObject2));
    _assertFalse ("Passed objects are identical!", aObject2.equals (aObject));
  }

  private static <DATATYPE> void _testHashcodeImplementation (@Nonnull final DATATYPE aObject)
  {
    _assertNotNull ("Passed object may not be null!", aObject);
    _assertTrue ("hashCode() invocations must be consistent", aObject.hashCode () == aObject.hashCode ());
    _assertFalse ("hashCode() may not be 0", aObject.hashCode () == 0);
  }

  public static <DATATYPE> void testHashcodeImplementationWithEqualContentObject (@Nonnull final DATATYPE aObject,
                                                                                  @Nonnull final DATATYPE aObject2)
  {
    _testHashcodeImplementation (aObject);
    _testHashcodeImplementation (aObject2);
    _assertTrue ("Passed objects are not identical!", aObject.equals (aObject2));
    _assertFalse ("This test may not be used with the same object!", aObject == aObject2);
    _assertTrue ("hashCode() invocations must be consistent", aObject.hashCode () == aObject2.hashCode ());
    _assertTrue ("hashCode() invocations must be consistent", aObject2.hashCode () == aObject.hashCode ());
  }

  public static <DATATYPE> void testHashcodeImplementationWithDifferentContentObject (@Nonnull final DATATYPE aObject1,
                                                                                      @Nonnull final DATATYPE aObject2)
  {
    _testHashcodeImplementation (aObject1);
    _testHashcodeImplementation (aObject2);
    _assertFalse ("Passed objects are identical!", aObject1.equals (aObject2));
    _assertFalse ("This test may not be used with the same object!", aObject1 == aObject2);
    final int nHash1 = aObject1.hashCode ();
    final int nHash2 = aObject2.hashCode ();
    _assertFalse ("hashCode() may not be the same for both objects", nHash1 == nHash2);
  }

  /**
   * Test the toString implementation of the passed object. It may not be empty,
   * and consistent.
   *
   * @param aObject
   *        The object to be tested.
   */
  public static void testToStringImplementation (@Nonnull final Object aObject)
  {
    _assertNotNull ("Passed object may not be null!", aObject);
    _assertNotNull ("toString() may not return null!", aObject.toString ());
    _assertTrue ("toString() may not return an empty string!", aObject.toString ().length () > 0);
    _assertTrue ("toString() invocations must be consistent", aObject.toString ().equals (aObject.toString ()));
  }

  public static <DATATYPE> void testToStringImplementationWithEqualContentObject (@Nonnull final DATATYPE aObject,
                                                                                  @Nonnull final DATATYPE aObject2)
  {
    testToStringImplementation (aObject);
    testToStringImplementation (aObject2);
    _assertTrue ("Passed objects are not identical!", aObject.equals (aObject2));
    _assertFalse ("This test may not be used with the same object!", aObject == aObject2);
  }

  public static <DATATYPE> void testToStringImplementationWithDifferentContentObject (@Nonnull final DATATYPE aObject,
                                                                                      @Nonnull final DATATYPE aObject2)
  {
    testToStringImplementation (aObject);
    testToStringImplementation (aObject2);
    _assertFalse ("Passed objects are identical!", aObject.equals (aObject2));
    _assertFalse ("This test may not be used with the same object!", aObject == aObject2);
  }

  /**
   * Check if two different objects (who may not be the same) are equal to each
   * other. Checks toString, equals and hashCode.
   *
   * @param <DATATYPE>
   *        The data type to be used
   * @param aObject1
   *        First object. May not be <code>null</code>.
   * @param aObject2
   *        Second object. May not be <code>null</code>.
   */
  public static <DATATYPE> void testDefaultImplementationWithEqualContentObject (@Nonnull final DATATYPE aObject1,
                                                                                 @Nonnull final DATATYPE aObject2)
  {
    testEqualsImplementationWithEqualContentObject (aObject1, aObject2);
    testHashcodeImplementationWithEqualContentObject (aObject1, aObject2);
    testToStringImplementationWithEqualContentObject (aObject1, aObject2);
  }

  /**
   * Check if two different objects are different to each other. Checks
   * toString, equals and hashCode.
   *
   * @param <DATATYPE>
   *        The data type to be used
   * @param aObject1
   *        First object. May not be <code>null</code>.
   * @param aObject2
   *        Second object. May not be <code>null</code>.
   */
  public static <DATATYPE> void testDefaultImplementationWithDifferentContentObject (@Nonnull final DATATYPE aObject1,
                                                                                     @Nonnull final DATATYPE aObject2)
  {
    testEqualsImplementationWithDifferentContentObject (aObject1, aObject2);
    testHashcodeImplementationWithDifferentContentObject (aObject1, aObject2);
    testToStringImplementationWithDifferentContentObject (aObject1, aObject2);
  }

  /**
   * Test the serializability of objects. First writes the object to a byte
   * array stream, and then tries to rebuild it from there. After reading it
   * performs an equals check using
   * {@link #testDefaultImplementationWithEqualContentObject(Object, Object)}
   *
   * @param <DATATYPE>
   *        The type of object to be serialized.
   * @param aSerializable
   *        The object to be written and read
   * @return The newly read object
   */
  @Nonnull
  public static <DATATYPE extends Serializable> DATATYPE testDefaultSerialization (@Nonnull final DATATYPE aSerializable)
  {
    try
    {
      // Serialize to byte array
      final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
      try (final ObjectOutputStream aOOS = new ObjectOutputStream (aBAOS))
      {
        aOOS.writeObject (aSerializable);
      }

      // Read new object from byte array
      DATATYPE aReadObject;
      try (
          final ObjectInputStream aOIS = new ObjectInputStream (new ByteArrayInputStreamProvider (aBAOS.toByteArray ()).getInputStream ()))
      {
        aReadObject = GenericReflection.<Object, DATATYPE> uncheckedCast (aOIS.readObject ());
      }

      // Now check them for equality
      testDefaultImplementationWithEqualContentObject (aSerializable, aReadObject);
      return aReadObject;
    }
    catch (final Throwable t)
    {
      throw new IllegalStateException ("testDefaultSerialization failed", t);
    }
  }

  /**
   * Test if the implementation {@link ICloneable} is OK. It creates a clone and
   * than uses
   * {@link #testDefaultImplementationWithEqualContentObject(Object, Object)} to
   * check for equality.
   *
   * @param aCloneable
   *        The cloneable object to test
   */
  public static void testGetClone (@Nonnull final ICloneable <?> aCloneable)
  {
    final Object aClone = aCloneable.getClone ();
    _assertNotNull ("Clone returned a null object", aClone);
    _assertTrue ("Clone returned a different class than the original one",
                 aClone.getClass ().equals (aCloneable.getClass ()));
    testDefaultImplementationWithEqualContentObject (aCloneable, aClone);
  }

  /**
   * Test if the {@link MicroTypeConverter} is OK. It converts it to XML and
   * back and than uses
   * {@link #testDefaultImplementationWithEqualContentObject(Object, Object)} to
   * check for equality.
   *
   * @param <T>
   *        The data type to be used and returned
   * @param aObj
   *        The object to test
   * @return The object read after conversion
   */
  public static <T> T testMicroTypeConversion (@Nonnull final T aObj)
  {
    assertNotNull (aObj);

    // Write to XML
    final IMicroElement e = MicroTypeConverter.convertToMicroElement (aObj, "test");
    assertNotNull (e);

    // Read from XML
    final Object aObj2 = MicroTypeConverter.convertToNative (e, aObj.getClass ());
    assertNotNull (aObj2);

    // Write to XML again
    final IMicroElement e2 = MicroTypeConverter.convertToMicroElement (aObj2, "test");
    assertNotNull (e2);

    // Ensure XML representation is identical
    final String sXML1 = MicroWriter.getXMLString (e);
    final String sXML2 = MicroWriter.getXMLString (e2);
    _assertEquals ("XML representation must be identical", sXML1, sXML2);

    // Ensure they are equals
    testDefaultImplementationWithEqualContentObject (aObj, aObj2);

    return GenericReflection.<Object, T> uncheckedCast (aObj2);
  }

  /**
   * Run something in parallel
   *
   * @param nCalls
   *        The number of invocations of the passed runnable. Must be &ge; 0.
   * @param aRunnable
   *        The runnable to execute. May not be <code>null</code>.
   */
  public static void testInParallel (@Nonnegative final int nCalls, @Nonnull final Runnable aRunnable)
  {
    ValueEnforcer.notNull (aRunnable, "Runnable");
    testInParallel (nCalls, new AdapterRunnableToThrowingRunnable <Exception> (aRunnable));
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
                                     @Nonnull final IThrowingRunnable <? extends Throwable> aRunnable)
  {
    ValueEnforcer.isGE0 (nCalls, "Calls");
    ValueEnforcer.notNull (aRunnable, "Runnable");

    // More than 20s thread would be overkill!
    final ExecutorService aES = Executors.newFixedThreadPool (20);
    final List <String> aErrors = new ArrayList <String> ();
    for (int i = 0; i < nCalls; ++i)
    {
      aES.submit ( () -> {
        try
        {
          aRunnable.run ();
        }
        catch (final Throwable t)
        {
          // Remember thread stack
          aErrors.add (t.getMessage () + "\n" + StackTraceHelper.getStackAsString (t));
        }
      });
    }
    ManagedExecutorService.shutdownAndWaitUntilAllTasksAreFinished (aES);

    // No errors should have occurred
    if (!aErrors.isEmpty ())
      throw new IllegalStateException (StringHelper.getImploded (aErrors));
  }

  @Nonnegative
  public static int testIfAllSPIFilesAreValid (@Nonnull final String sBaseDir,
                                               final boolean bContinueOnError) throws Exception
  {
    int nTotalImplementationCount = 0;
    final File aBaseDir = new File (sBaseDir);
    if (aBaseDir.exists () && aBaseDir.isDirectory ())
      for (final File aFile : new FileSystemIterator (sBaseDir))
        if (aFile.isFile ())
        {
          s_aLogger.info ("Checking SPI file " + aFile.getAbsolutePath ());

          // Check if interface exists
          try
          {
            Class.forName (aFile.getName ());
          }
          catch (final Throwable t)
          {
            final String sMsg = "No interface representing " +
                                aFile.getName () +
                                " exists: " +
                                ClassHelper.getClassLocalName (t) +
                                " - " +
                                t.getMessage ();
            s_aLogger.warn (sMsg);
            if (!bContinueOnError)
              throw new Exception (sMsg);
          }

          // Check content
          try (
              final NonBlockingBufferedReader aReader = new NonBlockingBufferedReader (StreamHelper.createReader (FileHelper.getInputStream (aFile),
                                                                                                                  CCharset.CHARSET_SERVICE_LOADER_OBJ)))
          {
            int nCount = 0;
            String sLine;
            while ((sLine = aReader.readLine ()) != null)
            {
              final String sClassName = StringHelper.trim (sLine);
              if (StringHelper.hasText (sClassName))
              {
                // Resolve class
                Class.forName (sLine);
                ++nCount;
                ++nTotalImplementationCount;
              }
            }
            if (nCount == 0)
              s_aLogger.warn ("  Contains no single valid implementation!");
            else
              s_aLogger.info ("  All implementations (" + nCount + ") are valid!");
          }
          catch (final Throwable t)
          {
            // Ensure the path name of the currently checked file is contained
            // in the exception text!
            s_aLogger.warn ("  Error checking content: " + t.getMessage ());
            if (!bContinueOnError)
              throw new Exception ("Error checking SPI file " + aFile.getAbsolutePath (), t);
          }
        }
    return nTotalImplementationCount;
  }

  @Nonnegative
  public static int testIfAllSPIImplementationsAreValid () throws Exception
  {
    return testIfAllSPIImplementationsAreValid (false);
  }

  @Nonnegative
  public static int testIfAllSPIImplementationsAreValid (final boolean bContinueOnError) throws Exception
  {
    int ret = 0;
    ret += testIfAllSPIFilesAreValid ("src/main/resources/META-INF/services", bContinueOnError);
    ret += testIfAllSPIFilesAreValid ("src/test/resources/META-INF/services", bContinueOnError);
    return ret;
  }
}
