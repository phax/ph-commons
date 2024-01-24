/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Test class for class {@link CGlobal}.
 *
 * @author Philip Helger
 */
public final class CGlobalTest
{
  @Test
  public void testConstants ()
  {
    assertEquals (CGlobal.BYTES_PER_KILOBYTE, 1024L);
    assertEquals (CGlobal.BYTES_PER_MEGABYTE, 1024L * 1024);
    assertEquals (CGlobal.BYTES_PER_GIGABYTE, 1024L * 1024 * 1024);
    assertEquals (CGlobal.BYTES_PER_TERABYTE, 1024L * 1024 * 1024 * 1024);
    assertEquals (CGlobal.BYTES_PER_PETABYTE, 1024L * 1024 * 1024 * 1024 * 1024);
  }

  @Test
  public void testDefaults ()
  {
    assertNotNull (CGlobal.DEFAULT_BOOLEAN_OBJ);
    assertNotNull (CGlobal.DEFAULT_BYTE_OBJ);
    assertNotNull (CGlobal.DEFAULT_CHAR_OBJ);
    assertNotNull (CGlobal.DEFAULT_DOUBLE_OBJ);
    assertNotNull (CGlobal.DEFAULT_FLOAT_OBJ);
    assertNotNull (CGlobal.DEFAULT_INT_OBJ);
    assertNotNull (CGlobal.DEFAULT_LONG_OBJ);
    assertNotNull (CGlobal.DEFAULT_SHORT_OBJ);
  }
}
