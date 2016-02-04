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
package com.helger.jaxb;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.charset.CCharset;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.mutable.MutableBoolean;
import com.helger.jaxb.mock.internal.MockJAXBCollection;

public final class JAXBMarshallerFuncTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (JAXBMarshallerFuncTest.class);

  @Test
  public void testCloseExternalOnWriteToOutputStream ()
  {
    final MockMarshallerExternal m = new MockMarshallerExternal ();
    final MutableBoolean aClosed = new MutableBoolean (false);
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ()
    {
      @Override
      public void close ()
      {
        super.close ();
        aClosed.set (true);
      }
    };
    {
      final com.helger.jaxb.mock.external.MockJAXBArchive aArc = new com.helger.jaxb.mock.external.MockJAXBArchive ();
      aArc.setVersion ("1.23");
      m.write (aArc, aBAOS);
    }
    s_aLogger.info (aBAOS.getAsString (CCharset.CHARSET_UTF_8_OBJ));
    // Must be closed!
    assertTrue ("Not closed!", aClosed.booleanValue ());
  }

  @Test
  public void testCloseInternalOnWriteToOutputStream ()
  {
    final MockMarshallerInternal m = new MockMarshallerInternal ();
    final MutableBoolean aClosed = new MutableBoolean (false);
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ()
    {
      @Override
      public void close ()
      {
        super.close ();
        aClosed.set (true);
      }
    };
    {
      final com.helger.jaxb.mock.internal.MockJAXBArchive aArc = new com.helger.jaxb.mock.internal.MockJAXBArchive ();
      aArc.setVersion ("1.24");
      m.write (aArc, aBAOS);
    }
    s_aLogger.info (aBAOS.getAsString (CCharset.CHARSET_UTF_8_OBJ));
    // Must be closed!
    assertTrue ("Not closed!", aClosed.booleanValue ());
  }

  @Test
  public void testGetAsBytes ()
  {
    final MockMarshallerInternal m = new MockMarshallerInternal ();
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    byte [] aDirectBytes;
    {
      final com.helger.jaxb.mock.internal.MockJAXBArchive aArc = new com.helger.jaxb.mock.internal.MockJAXBArchive ();
      aArc.setVersion ("1.24");
      for (int i = 0; i < 100; ++i)
      {
        final MockJAXBCollection aCollection = new MockJAXBCollection ();
        aCollection.setDescription ("Internal bla foo");
        aCollection.setID (i);
        aArc.getCollection ().add (aCollection);
      }
      m.write (aArc, aBAOS);
      aDirectBytes = m.getAsBytes (aArc);
    }
    assertArrayEquals (aBAOS.toByteArray (), aDirectBytes);
  }
}
