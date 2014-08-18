/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.io.streams.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.streams.StreamUtils;

/**
 * Test class for {@link ClassPathHelper}
 * 
 * @author Philip Helger
 */
public final class ClassPathHelperTest
{
  @Test
  public void testGetAllClassPathEntries ()
  {
    final List <String> l = ClassPathHelper.getAllClassPathEntries ();
    assertNotNull (l);
    for (final String sItem : l)
      assertNotNull (sItem);
  }

  /**
   * Test for method printClassPathEntries
   * 
   * @throws UnsupportedEncodingException
   *         never
   */
  @Test
  public void testPrintClassPathEntries () throws UnsupportedEncodingException
  {
    // Use default separator
    NonBlockingByteArrayOutputStream baos = new NonBlockingByteArrayOutputStream ();
    ClassPathHelper.printClassPathEntries (new PrintStream (baos, false, CCharset.CHARSET_ISO_8859_1));
    assertTrue (baos.getAsString (CCharset.CHARSET_ISO_8859_1_OBJ).length () > 0);
    assertTrue (baos.getAsString (CCharset.CHARSET_ISO_8859_1_OBJ).indexOf ("\n") > 0);
    StreamUtils.close (baos);

    // Use special separator
    baos = new NonBlockingByteArrayOutputStream ();
    ClassPathHelper.printClassPathEntries (new PrintStream (baos, false, CCharset.CHARSET_ISO_8859_1), "$$$");
    assertTrue (baos.getAsString (CCharset.CHARSET_ISO_8859_1_OBJ).length () > 0);
    assertTrue (baos.getAsString (CCharset.CHARSET_ISO_8859_1_OBJ).indexOf ("$$$") > 0);
    assertTrue (baos.getAsString (CCharset.CHARSET_UTF_8_OBJ).indexOf ("$$$") > 0);
    StreamUtils.close (baos);
  }
}
