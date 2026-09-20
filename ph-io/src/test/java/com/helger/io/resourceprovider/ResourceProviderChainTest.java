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
package com.helger.io.resourceprovider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.OutputStream;

import org.junit.After;
import org.junit.Test;

import com.helger.base.io.EAppend;
import com.helger.base.io.stream.StreamHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.io.file.FileOperations;
import com.helger.io.resource.IReadableResource;
import com.helger.io.resource.IWritableResource;
import com.helger.unittest.support.TestHelper;

/**
 * Test class for class {@link ReadableResourceProviderChain} and
 * {@link WritableResourceProviderChain}.
 *
 * @author Philip Helger
 */
public final class ResourceProviderChainTest
{
  private static final String EXISTING = "test1.txt";
  private static final String TARGET_FILE = "target/junittest-providerchain.txt";

  @After
  public void deleteFile ()
  {
    FileOperations.deleteFileIfExisting (new java.io.File (TARGET_FILE));
  }

  @Test
  public void testReadableChain ()
  {
    final ReadableResourceProviderChain aChain = new ReadableResourceProviderChain (new ClassPathResourceProvider (),
                                                                                    new FileSystemResourceProvider ());
    assertEquals (2, aChain.getAllContainedReadingResourceProviders ().size ());
    assertNotNull (aChain.toString ());

    assertTrue (aChain.supportsReading (EXISTING));
    final IReadableResource aRes = aChain.getReadableResource (EXISTING);
    assertNotNull (aRes);
    assertTrue (aRes.exists ());

    assertNotNull (aChain.getInputStream (EXISTING));
  }

  @Test
  public void testReadableChainIterableCtor ()
  {
    final ReadableResourceProviderChain aChain = new ReadableResourceProviderChain (new CommonsArrayList <> (new ClassPathResourceProvider ()));
    assertEquals (1, aChain.getAllContainedReadingResourceProviders ().size ());
    assertTrue (aChain.supportsReading (EXISTING));
  }

  @Test
  public void testGetReadableResourceIf ()
  {
    final ReadableResourceProviderChain aChain = new ReadableResourceProviderChain (new ClassPathResourceProvider ());
    assertNotNull (aChain.getReadableResourceIf (EXISTING, x -> true));
    // The filter rejects everything
    assertNull (aChain.getReadableResourceIf (EXISTING, x -> false));
  }

  @Test
  public void testWritableChain ()
  {
    final WritableResourceProviderChain aChain = new WritableResourceProviderChain (new FileSystemResourceProvider ());
    assertEquals (1, aChain.getAllContainedWritingResourceProviders ().size ());
    assertEquals (1, aChain.getAllContainedReadingResourceProviders ().size ());
    assertNotNull (aChain.toString ());

    assertTrue (aChain.supportsWriting (TARGET_FILE));
    final IWritableResource aRes = aChain.getWritableResource (TARGET_FILE);
    assertNotNull (aRes);

    try (final OutputStream aOS = aChain.getOutputStream (TARGET_FILE, EAppend.TRUNCATE))
    {
      assertNotNull (aOS);
    }
    catch (final java.io.IOException ex)
    {
      fail (ex.getMessage ());
    }
    StreamHelper.close (aChain.getOutputStream (TARGET_FILE, EAppend.APPEND));
  }

  @Test
  public void testWritableChainIterableCtor ()
  {
    final WritableResourceProviderChain aChain = new WritableResourceProviderChain (new CommonsArrayList <> (new FileSystemResourceProvider ()));
    assertEquals (1, aChain.getAllContainedWritingResourceProviders ().size ());
  }

  @Test
  public void testGetWritableResourceIf ()
  {
    final WritableResourceProviderChain aChain = new WritableResourceProviderChain (new FileSystemResourceProvider ());
    assertNotNull (aChain.getWritableResourceIf (TARGET_FILE, x -> true));
    assertNull (aChain.getWritableResourceIf (TARGET_FILE, x -> false));
  }

  @Test
  public void testUnsupportedName ()
  {
    final ReadableResourceProviderChain aRChain = new ReadableResourceProviderChain (new ClassPathResourceProvider ());
    assertFalse (aRChain.supportsReading (null));
    try
    {
      aRChain.getReadableResource (null);
      fail ();
    }
    catch (final IllegalArgumentException | NullPointerException ex)
    {
      // expected
    }

    final WritableResourceProviderChain aWChain = new WritableResourceProviderChain (new FileSystemResourceProvider ());
    assertFalse (aWChain.supportsWriting (null));
    try
    {
      aWChain.getWritableResource (null);
      fail ();
    }
    catch (final IllegalArgumentException | NullPointerException ex)
    {
      // expected
    }
  }

  @Test
  public void testEqualsHashcode ()
  {
    TestHelper.testDefaultImplementationWithEqualContentObject (new ReadableResourceProviderChain (new ClassPathResourceProvider ()),
                                                                new ReadableResourceProviderChain (new ClassPathResourceProvider ()));
    TestHelper.testDefaultImplementationWithDifferentContentObject (new ReadableResourceProviderChain (new ClassPathResourceProvider ()),
                                                                    new ReadableResourceProviderChain (new FileSystemResourceProvider ()));
    TestHelper.testDefaultImplementationWithEqualContentObject (new WritableResourceProviderChain (new FileSystemResourceProvider ()),
                                                                new WritableResourceProviderChain (new FileSystemResourceProvider ()));
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new ReadableResourceProviderChain ((IReadableResourceProvider []) null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      // A writable chain needs at least one writable provider
      new WritableResourceProviderChain (new ClassPathResourceProvider ());
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }
}
