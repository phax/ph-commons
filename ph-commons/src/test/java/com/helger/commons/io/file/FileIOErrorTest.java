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
package com.helger.commons.io.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import com.helger.commons.exception.mock.MockException;
import com.helger.commons.exception.mock.MockIOException;
import com.helger.commons.mock.CommonsTestHelper;

/**
 * Test class for class {@link FileIOError}.
 *
 * @author Philip Helger
 */
public final class FileIOErrorTest
{
  private static final File f1 = new File ("source");
  private static final File f2 = new File ("dest");

  @Test
  public void testCtor1 ()
  {
    final FileIOError e = new FileIOError (EFileIOOperation.COPY_FILE, EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
    assertSame (EFileIOOperation.COPY_FILE, e.getOperation ());
    assertSame (EFileIOErrorCode.SOURCE_DOES_NOT_EXIST, e.getErrorCode ());
    assertNull (e.getFile1 ());
    assertNull (e.getFile2 ());
    assertNull (e.getException ());

    try
    {
      new FileIOError (null, EFileIOErrorCode.OPERATION_FAILED);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new FileIOError (EFileIOOperation.CREATE_DIR, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testCtor2 ()
  {
    final FileIOError e = new FileIOError (EFileIOOperation.COPY_FILE, EFileIOErrorCode.SOURCE_DOES_NOT_EXIST, f1);
    assertSame (EFileIOOperation.COPY_FILE, e.getOperation ());
    assertSame (EFileIOErrorCode.SOURCE_DOES_NOT_EXIST, e.getErrorCode ());
    assertEquals (f1, e.getFile1 ());
    assertNull (e.getFile2 ());
    assertNull (e.getException ());

    try
    {
      new FileIOError (null, EFileIOErrorCode.OPERATION_FAILED, f1);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new FileIOError (EFileIOOperation.CREATE_DIR, null, f1);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new FileIOError (EFileIOOperation.CREATE_DIR, EFileIOErrorCode.OPERATION_FAILED, (File) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testCtor3 ()
  {
    final FileIOError e = new FileIOError (EFileIOOperation.COPY_FILE, EFileIOErrorCode.SOURCE_DOES_NOT_EXIST, f1, f2);
    assertSame (EFileIOOperation.COPY_FILE, e.getOperation ());
    assertSame (EFileIOErrorCode.SOURCE_DOES_NOT_EXIST, e.getErrorCode ());
    assertEquals (f1, e.getFile1 ());
    assertEquals (f2, e.getFile2 ());
    assertNull (e.getException ());

    try
    {
      new FileIOError (null, EFileIOErrorCode.OPERATION_FAILED, f1, f2);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new FileIOError (EFileIOOperation.CREATE_DIR, null, f1, f2);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new FileIOError (EFileIOOperation.CREATE_DIR, EFileIOErrorCode.OPERATION_FAILED, (File) null, f2);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new FileIOError (EFileIOOperation.CREATE_DIR, EFileIOErrorCode.OPERATION_FAILED, f1, (File) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testCtor4 ()
  {
    final FileIOError e = new FileIOError (EFileIOOperation.COPY_FILE,
                                           EFileIOErrorCode.SOURCE_DOES_NOT_EXIST,
                                           new MockException ());
    assertSame (EFileIOOperation.COPY_FILE, e.getOperation ());
    assertSame (EFileIOErrorCode.SOURCE_DOES_NOT_EXIST, e.getErrorCode ());
    assertNull (e.getFile1 ());
    assertNull (e.getFile2 ());
    assertTrue (e.getException () instanceof MockException);

    try
    {
      new FileIOError (null, EFileIOErrorCode.OPERATION_FAILED, new MockException ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new FileIOError (EFileIOOperation.CREATE_DIR, null, new MockException ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new FileIOError (EFileIOOperation.CREATE_DIR, EFileIOErrorCode.OPERATION_FAILED, (Exception) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testStdMethods ()
  {
    // ctor 1
    final FileIOError e1 = new FileIOError (EFileIOOperation.COPY_FILE, EFileIOErrorCode.SOURCE_DOES_NOT_EXIST);
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (e1,
                                                                       new FileIOError (EFileIOOperation.COPY_FILE,
                                                                                        EFileIOErrorCode.SOURCE_DOES_NOT_EXIST));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (e1,
                                                                           new FileIOError (EFileIOOperation.COPY_DIR_RECURSIVE,
                                                                                            EFileIOErrorCode.SOURCE_DOES_NOT_EXIST));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (e1,
                                                                           new FileIOError (EFileIOOperation.COPY_FILE,
                                                                                            EFileIOErrorCode.SOURCE_EQUALS_TARGET));

    // ctor 2
    final FileIOError e2 = new FileIOError (EFileIOOperation.COPY_FILE, EFileIOErrorCode.SOURCE_DOES_NOT_EXIST, f1);
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (e2,
                                                                       new FileIOError (EFileIOOperation.COPY_FILE,
                                                                                        EFileIOErrorCode.SOURCE_DOES_NOT_EXIST,
                                                                                        f1));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (e2,
                                                                           new FileIOError (EFileIOOperation.COPY_DIR_RECURSIVE,
                                                                                            EFileIOErrorCode.SOURCE_DOES_NOT_EXIST,
                                                                                            f1));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (e2,
                                                                           new FileIOError (EFileIOOperation.COPY_FILE,
                                                                                            EFileIOErrorCode.SOURCE_EQUALS_TARGET,
                                                                                            f1));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (e2,
                                                                           new FileIOError (EFileIOOperation.COPY_FILE,
                                                                                            EFileIOErrorCode.SOURCE_DOES_NOT_EXIST,
                                                                                            f2));

    // ctor 3
    final FileIOError e3 = new FileIOError (EFileIOOperation.COPY_FILE, EFileIOErrorCode.SOURCE_DOES_NOT_EXIST, f1, f2);
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (e3,
                                                                       new FileIOError (EFileIOOperation.COPY_FILE,
                                                                                        EFileIOErrorCode.SOURCE_DOES_NOT_EXIST,
                                                                                        f1,
                                                                                        f2));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (e3,
                                                                           new FileIOError (EFileIOOperation.COPY_DIR_RECURSIVE,
                                                                                            EFileIOErrorCode.SOURCE_DOES_NOT_EXIST,
                                                                                            f1,
                                                                                            f2));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (e3,
                                                                           new FileIOError (EFileIOOperation.COPY_FILE,
                                                                                            EFileIOErrorCode.SOURCE_EQUALS_TARGET,
                                                                                            f1,
                                                                                            f2));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (e3,
                                                                           new FileIOError (EFileIOOperation.COPY_FILE,
                                                                                            EFileIOErrorCode.SOURCE_DOES_NOT_EXIST,
                                                                                            f2,
                                                                                            f2));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (e3,
                                                                           new FileIOError (EFileIOOperation.COPY_FILE,
                                                                                            EFileIOErrorCode.SOURCE_DOES_NOT_EXIST,
                                                                                            f1,
                                                                                            f1));

    // ctor 4
    final Exception e = new MockException ();
    final FileIOError e4 = new FileIOError (EFileIOOperation.COPY_FILE, EFileIOErrorCode.SOURCE_DOES_NOT_EXIST, e);
    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (e4,
                                                                       new FileIOError (EFileIOOperation.COPY_FILE,
                                                                                        EFileIOErrorCode.SOURCE_DOES_NOT_EXIST,
                                                                                        e));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (e4,
                                                                           new FileIOError (EFileIOOperation.COPY_DIR_RECURSIVE,
                                                                                            EFileIOErrorCode.SOURCE_DOES_NOT_EXIST,
                                                                                            e));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (e4,
                                                                           new FileIOError (EFileIOOperation.COPY_FILE,
                                                                                            EFileIOErrorCode.SOURCE_EQUALS_TARGET,
                                                                                            e));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (e4,
                                                                           new FileIOError (EFileIOOperation.COPY_FILE,
                                                                                            EFileIOErrorCode.SOURCE_DOES_NOT_EXIST,
                                                                                            new MockIOException ()));
  }
}
