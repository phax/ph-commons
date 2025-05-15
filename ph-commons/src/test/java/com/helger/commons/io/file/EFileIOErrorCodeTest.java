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
package com.helger.commons.io.file;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

/**
 * Test class for class {@link EFileIOErrorCode}.
 *
 * @author Philip Helger
 */
public final class EFileIOErrorCodeTest
{
  @Test
  public void testAll ()
  {
    for (final EFileIOErrorCode e : EFileIOErrorCode.values ())
    {
      assertSame (e, EFileIOErrorCode.valueOf (e.name ()));
      assertSame (e, e.getAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE, new File (".")).getErrorCode ());
      assertSame (e,
                  e.getAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE, new File ("."), new File (".")).getErrorCode ());
      assertTrue (e.getID () >= 0);
      assertSame (e, EFileIOErrorCode.getFromIDOrNull (e.getID ()));
      if (e.equals (EFileIOErrorCode.NO_ERROR))
        assertTrue (e.isSuccess ());
      else
        assertTrue (e.isFailure ());
    }
    assertSame (EFileIOErrorCode.SECURITY_ERROR,
                EFileIOErrorCode.getSecurityAsIOError (EFileIOOperation.COPY_DIR_RECURSIVE, new SecurityException ())
                                .getErrorCode ());
    try
    {
      EFileIOErrorCode.SECURITY_ERROR.getAsIOError (EFileIOOperation.CREATE_DIR, (File) null, (File) null);
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
    assertNull (EFileIOErrorCode.getFromIDOrNull (-1));
    assertNull (EFileIOErrorCode.getFromIDOrNull (Integer.MAX_VALUE));
  }
}
