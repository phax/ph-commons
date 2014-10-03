/**
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
package com.helger.commons.jaxb;

import static org.junit.Assert.assertTrue;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.io.IReadableResource;
import com.helger.commons.io.streams.NonBlockingByteArrayOutputStream;
import com.helger.commons.jaxb.utils.AbstractJAXBMarshaller;
import com.helger.commons.mutable.MutableBoolean;

public class JAXBMarshallerTest
{
  private static final class MyMarshaller extends AbstractJAXBMarshaller <MockJAXBArchive>
  {
    public MyMarshaller ()
    {
      super (MockJAXBArchive.class, (IReadableResource) null);
    }

    @Override
    protected JAXBElement <MockJAXBArchive> wrapObject (final MockJAXBArchive aObject)
    {
      return new JAXBElement <MockJAXBArchive> (new QName ("any"), MockJAXBArchive.class, aObject);
    }
  }

  @Test
  public void testCloseOnWriteToOutputStream ()
  {
    final MyMarshaller m = new MyMarshaller ();
    final MutableBoolean aClosed = new MutableBoolean (false);
    final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ()
    {
      @Override
      public void close ()
      {
        super.close ();
        aClosed.set (true);
      }
    };
    {
      final MockJAXBArchive aArc = new MockJAXBArchive ();
      aArc.setVersion ("1.23");
      m.write (aArc, aOS);
    }
    System.out.println (aOS.getAsString (CCharset.CHARSET_UTF_8_OBJ));
    assertTrue ("Not closed!", aClosed.booleanValue ());
  }
}
