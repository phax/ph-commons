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
