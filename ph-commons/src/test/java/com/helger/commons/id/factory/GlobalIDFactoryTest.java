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
package com.helger.commons.id.factory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.helger.commons.io.file.FileOperations;

/**
 * Test class for class {@link GlobalIDFactory}.
 *
 * @author Philip Helger
 */
public final class GlobalIDFactoryTest
{
  @BeforeClass
  public static void testSet ()
  {
    // int factories
    assertTrue (GlobalIDFactory.setIntIDFactory (new MemoryIntIDFactory (9)).isChanged ());
    assertTrue (GlobalIDFactory.setIntIDFactory (new MemoryIntIDFactory (10)).isChanged ());
    assertFalse (GlobalIDFactory.setIntIDFactory (new MemoryIntIDFactory (10)).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentIntIDFactory (new MemoryIntIDFactory ()).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentIntIDFactory (new MemoryIntIDFactory (10)).isChanged ());
    assertFalse (GlobalIDFactory.setPersistentIntIDFactory (new MemoryIntIDFactory (10)).isChanged ());
    assertTrue (GlobalIDFactory.setIntIDFactory (new MemoryStaticIntIDFactory ()).isChanged ());
    assertFalse (GlobalIDFactory.setIntIDFactory (new MemoryStaticIntIDFactory ()).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentIntIDFactory (new FileIntIDFactory (new File ("idint.dat"))).isChanged ());
    assertTrue (GlobalIDFactory.hasPersistentIntIDFactory ());

    // long factories
    assertTrue (GlobalIDFactory.setLongIDFactory (new MemoryLongIDFactory (9)).isChanged ());
    assertTrue (GlobalIDFactory.setLongIDFactory (new MemoryLongIDFactory (10)).isChanged ());
    assertFalse (GlobalIDFactory.setLongIDFactory (new MemoryLongIDFactory (10)).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentLongIDFactory (new MemoryLongIDFactory ()).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentLongIDFactory (new MemoryLongIDFactory (10)).isChanged ());
    assertFalse (GlobalIDFactory.setPersistentLongIDFactory (new MemoryLongIDFactory (10)).isChanged ());
    assertTrue (GlobalIDFactory.setLongIDFactory (new MemoryStaticLongIDFactory ()).isChanged ());
    assertFalse (GlobalIDFactory.setLongIDFactory (new MemoryStaticLongIDFactory ()).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentLongIDFactory (new FileLongIDFactory (new File ("idlong.dat")))
                               .isChanged ());
    assertTrue (GlobalIDFactory.hasPersistentLongIDFactory ());

    // string factories
    assertTrue (GlobalIDFactory.setStringIDFactory (new StringIDFromGlobalIntIDFactory ("prefix1")).isChanged ());
    assertTrue (GlobalIDFactory.setStringIDFactory (new StringIDFromGlobalIntIDFactory ("prefix2")).isChanged ());
    assertFalse (GlobalIDFactory.setStringIDFactory (new StringIDFromGlobalIntIDFactory ("prefix2")).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentStringIDFactory (new StringIDFromGlobalPersistentIntIDFactory ("prefix1"))
                               .isChanged ());
    assertTrue (GlobalIDFactory.setPersistentStringIDFactory (new StringIDFromGlobalPersistentIntIDFactory ("prefix2"))
                               .isChanged ());
    assertFalse (GlobalIDFactory.setPersistentStringIDFactory (new StringIDFromGlobalPersistentIntIDFactory ("prefix2"))
                                .isChanged ());
    assertTrue (GlobalIDFactory.setStringIDFactory (new StringIDFromGlobalLongIDFactory ("prefix1")).isChanged ());
    assertTrue (GlobalIDFactory.setStringIDFactory (new StringIDFromGlobalLongIDFactory ("prefix2")).isChanged ());
    assertFalse (GlobalIDFactory.setStringIDFactory (new StringIDFromGlobalLongIDFactory ("prefix2")).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentStringIDFactory (new StringIDFromGlobalPersistentLongIDFactory ("prefix1"))
                               .isChanged ());
    assertTrue (GlobalIDFactory.setPersistentStringIDFactory (new StringIDFromGlobalPersistentLongIDFactory ("prefix2"))
                               .isChanged ());
    assertFalse (GlobalIDFactory.setPersistentStringIDFactory (new StringIDFromGlobalPersistentLongIDFactory ("prefix2"))
                                .isChanged ());
  }

  @AfterClass
  public static void reset ()
  {
    assertTrue (GlobalIDFactory.setIntIDFactory (new MemoryIntIDFactory ()).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentIntIDFactory (new MemoryIntIDFactory ()).isChanged ());
    assertTrue (GlobalIDFactory.setLongIDFactory (new MemoryLongIDFactory ()).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentLongIDFactory (new MemoryLongIDFactory ()).isChanged ());
    assertTrue (GlobalIDFactory.setStringIDFactory (new StringIDFromGlobalIntIDFactory ()).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentStringIDFactory (new StringIDFromGlobalPersistentIntIDFactory ())
                               .isChanged ());
    FileOperations.deleteFile (new File ("idint.dat"));
    FileOperations.deleteFile (new File ("idlong.dat"));
  }

  @Test
  public void testGetNewID ()
  {
    // value always > 0?
    assertTrue (GlobalIDFactory.getNewIntID () > 0);

    // 2 invocation return different results?
    assertFalse (GlobalIDFactory.getNewIntID () == GlobalIDFactory.getNewIntID ());

    // 2 invocation return different results?
    assertFalse (GlobalIDFactory.getNewStringID ().equals (GlobalIDFactory.getNewStringID ()));

    GlobalIDFactory.getNewIntID ();
    GlobalIDFactory.getNewPersistentIntID ();
    GlobalIDFactory.getNewStringID ();
    GlobalIDFactory.getNewPersistentStringID ();
  }

  @Test
  public void testNoFactories ()
  {
    assertTrue (GlobalIDFactory.setIntIDFactory (null).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentIntIDFactory (null).isChanged ());
    assertTrue (GlobalIDFactory.setStringIDFactory (null).isChanged ());
    assertTrue (GlobalIDFactory.setPersistentStringIDFactory (null).isChanged ());
    assertFalse (GlobalIDFactory.hasPersistentIntIDFactory ());

    try
    {
      GlobalIDFactory.getNewIntID ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
    try
    {
      GlobalIDFactory.getNewPersistentIntID ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
    try
    {
      GlobalIDFactory.getNewStringID ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
    try
    {
      GlobalIDFactory.getNewPersistentStringID ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
  }
}
