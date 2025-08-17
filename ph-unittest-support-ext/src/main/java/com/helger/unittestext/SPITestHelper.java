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
package com.helger.unittestext;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.style.IsSPIImplementation;
import com.helger.annotation.style.IsSPIInterface;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.nonblocking.NonBlockingBufferedReader;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.lang.clazz.ClassHelper;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsTreeMap;
import com.helger.collection.commons.CommonsTreeSet;
import com.helger.collection.commons.ICommonsSortedMap;
import com.helger.collection.commons.ICommonsSortedSet;
import com.helger.io.file.FileHelper;
import com.helger.io.file.FileSystemIterator;

import jakarta.annotation.Nonnull;

public final class SPITestHelper
{
  /** Project relative path to test SPI directory */
  public static final String TEST_SERVICES = "src/test/resources/META-INF/services";
  /** Project relative path to main SPI directory */
  public static final String MAIN_SERVICES = "src/main/resources/META-INF/services";

  private static final Logger LOGGER = LoggerFactory.getLogger (SPITestHelper.class);

  private SPITestHelper ()
  {}

  public enum EMode
  {
    STRICT,
    IGNORE_ERRORS,
    NO_RESOLVE;

    public boolean isStrict ()
    {
      return this == STRICT;
    }

    public boolean isResolve ()
    {
      return this != NO_RESOLVE;
    }
  }

  /**
   * Test if all SPI configurations and implementations are correctly configured.
   *
   * @param sBaseDir
   *        Base directory. May not be <code>null</code>.
   * @param eMode
   *        Validation mode. May not be <code>null</code>.
   * @return A map from interface to all found implementations in the order of appearance.
   * @throws IOException
   *         In case of read error
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSortedMap <String, ICommonsSortedSet <String>> testIfAllSPIImplementationsAreValid (@Nonnull final String sBaseDir,
                                                                                                            @Nonnull final EMode eMode) throws IOException
  {
    ValueEnforcer.notNull (sBaseDir, "BaseDir");
    ValueEnforcer.notNull (eMode, "Mode");

    final boolean bDoResolve = eMode.isResolve ();
    final ClassLoader aCL = StackWalker.getInstance (StackWalker.Option.RETAIN_CLASS_REFERENCE)
                                       .getCallerClass ()
                                       .getClassLoader ();
    final ICommonsSortedMap <String, ICommonsSortedSet <String>> aAllImplementations = new CommonsTreeMap <> ();
    final File aBaseDir = new File (sBaseDir);
    if (aBaseDir.exists () && aBaseDir.isDirectory ())
      for (final File aFile : new FileSystemIterator (sBaseDir))
        if (aFile.isFile ())
        {
          if (bDoResolve)
            LOGGER.info ("Checking SPI file " + aFile.getAbsolutePath ());
          final String sInterfaceClassName = aFile.getName ();

          // Check if interface exists
          if (bDoResolve)
            try
            {
              final Class <?> aInterfaceClass = aCL.loadClass (sInterfaceClassName);
              if (sInterfaceClassName.startsWith ("com.helger.") &&
                  aInterfaceClass.getAnnotation (IsSPIInterface.class) == null)
                LOGGER.warn (aInterfaceClass + " should have the @IsSPIInterface annotation");
            }
            catch (final Exception ex)
            {
              final String sMsg = "No interface representing " +
                                  sInterfaceClassName +
                                  " exists: " +
                                  ClassHelper.getClassLocalName (ex) +
                                  " - " +
                                  ex.getMessage ();
              LOGGER.warn (sMsg);
              if (eMode.isStrict ())
                throw new IllegalStateException (sMsg);
            }

          // Check content - must be UTF8
          try (final NonBlockingBufferedReader aReader = new NonBlockingBufferedReader (StreamHelper.createReader (FileHelper.getInputStream (aFile),
                                                                                                                   StandardCharsets.UTF_8)))
          {
            int nCount = 0;
            String sLine;
            while ((sLine = aReader.readLine ()) != null)
            {
              final String sImplClassName = StringHelper.trim (sLine);
              if (StringHelper.isNotEmpty (sImplClassName))
              {
                // Resolve class
                if (bDoResolve)
                {
                  try
                  {
                    final Class <?> aImplClass = aCL.loadClass (sImplClassName);
                    if (aImplClass.getAnnotation (IsSPIImplementation.class) == null)
                      LOGGER.warn (aImplClass + " should have the @IsSPIImplementation annotation");
                    ++nCount;
                    aAllImplementations.computeIfAbsent (sInterfaceClassName, x -> new CommonsTreeSet <> ())
                                       .add (sImplClassName);
                  }
                  catch (final Exception ex)
                  {
                    // Ensure the path name of the currently checked file is
                    // contained in the exception text!
                    LOGGER.warn ("  Error checking content: " + ex.getMessage ());
                    if (eMode.isStrict ())
                      throw new IllegalStateException ("Error checking SPI file " + aFile.getAbsolutePath (), ex);
                  }
                }
                else
                {
                  ++nCount;
                  aAllImplementations.computeIfAbsent (sInterfaceClassName, x -> new CommonsTreeSet <> ())
                                     .add (sImplClassName);
                }
              }
            }
            if (bDoResolve)
            {
              if (nCount == 0)
                LOGGER.warn ("  Contains no single valid implementation!");
              else
                LOGGER.info ("  All implementations (" + nCount + ") are valid!");
            }
          }
        }
    return aAllImplementations;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSortedMap <String, ICommonsSortedSet <String>> testIfAllMainSPIImplementationsAreValid (final boolean bContinueOnError) throws IOException
  {
    return testIfAllSPIImplementationsAreValid (MAIN_SERVICES, bContinueOnError ? EMode.IGNORE_ERRORS : EMode.STRICT);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSortedMap <String, ICommonsSortedSet <String>> testIfAllTestSPIImplementationsAreValid (final boolean bContinueOnError) throws IOException
  {
    return testIfAllSPIImplementationsAreValid (TEST_SERVICES, bContinueOnError ? EMode.IGNORE_ERRORS : EMode.STRICT);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSortedMap <String, ICommonsSortedSet <String>> testIfAllSPIImplementationsAreValid (final boolean bContinueOnError) throws IOException
  {
    final ICommonsSortedMap <String, ICommonsSortedSet <String>> aAllImplementations = testIfAllMainSPIImplementationsAreValid (bContinueOnError);
    aAllImplementations.putAll (testIfAllTestSPIImplementationsAreValid (bContinueOnError));
    return aAllImplementations;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSortedMap <String, ICommonsSortedSet <String>> testIfAllSPIImplementationsAreValid () throws IOException
  {
    return testIfAllSPIImplementationsAreValid (false);
  }
}
