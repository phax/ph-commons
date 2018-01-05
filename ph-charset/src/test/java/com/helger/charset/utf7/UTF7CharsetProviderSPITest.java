/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.charset.utf7;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Locale;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsSet;

public final class UTF7CharsetProviderSPITest
{
  private final UTF7CharsetProviderSPI tested = new UTF7CharsetProviderSPI ();

  @Test
  public void testModifiedUTF7 ()
  {
    final Charset charset = tested.charsetForName ("x-modified-UTF-7");
    assertNotNull ("charset not found", charset);
    assertEquals (UTF7CharsetModified.class, charset.getClass ());
    assertEquals (charset, tested.charsetForName ("x-imap-modified-utf-7"));
    assertEquals (charset, tested.charsetForName ("x-imap4-modified-utf7"));
    assertEquals (charset, tested.charsetForName ("x-imap4-modified-utf-7"));
    assertEquals (charset, tested.charsetForName ("x-RFC3501"));
    assertEquals (charset, tested.charsetForName ("x-RFC-3501"));
  }

  @Test
  public void testUTF7 ()
  {
    final Charset charset = tested.charsetForName ("UTF-7");
    assertNotNull ("charset not found", charset);
    assertEquals (UTF7Charset.class, charset.getClass ());
    assertEquals (charset, tested.charsetForName ("utf-7"));
    assertEquals (charset, tested.charsetForName ("UNICODE-1-1-UTF-7"));
    assertEquals (charset, tested.charsetForName ("csUnicode11UTF7"));
    assertEquals (charset, tested.charsetForName ("x-RFC2152"));
    assertEquals (charset, tested.charsetForName ("x-RFC-2152"));
  }

  @Test
  public void testUTF7optional ()
  {
    final Charset charset = tested.charsetForName ("X-UTF-7-OPTIONAL");
    assertNotNull ("charset not found", charset);
    assertEquals (UTF7Charset.class, charset.getClass ());
    assertEquals (charset, tested.charsetForName ("x-utf-7-optional"));
    assertEquals (charset, tested.charsetForName ("x-RFC2152-optional"));
    assertEquals (charset, tested.charsetForName ("x-RFC-2152-optional"));
  }

  @Test
  public void testNotHere ()
  {
    assertNull (tested.charsetForName ("X-DOES-NOT-EXIST"));
  }

  @Test
  public void testIterator ()
  {
    final Iterator <Charset> iterator = tested.charsets ();
    final ICommonsSet <Charset> found = new CommonsHashSet<> ();
    while (iterator.hasNext ())
      found.add (iterator.next ());
    assertEquals (3, found.size ());
    final Charset charset1 = tested.charsetForName ("x-IMAP4-modified-UTF7");
    final Charset charset2 = tested.charsetForName ("UTF-7");
    final Charset charset3 = tested.charsetForName ("X-UTF-7-OPTIONAL");
    assertTrue (found.contains (charset1));
    assertTrue (found.contains (charset2));
    assertTrue (found.contains (charset3));
  }

  @Test
  public void testTurkish ()
  {
    Locale.setDefault (new Locale ("tr", "TR"));
    assertEquals (tested.charsetForName ("UTF-7"), tested.charsetForName ("unicode-1-1-utf-7"));
  }
}
