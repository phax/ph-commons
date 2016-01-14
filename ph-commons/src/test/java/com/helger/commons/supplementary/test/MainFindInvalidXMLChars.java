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
package com.helger.commons.supplementary.test;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.commons.io.stream.NonBlockingStringWriter;
import com.helger.commons.xml.EXMLVersion;
import com.helger.commons.xml.XMLFactory;
import com.helger.commons.xml.serialize.read.DOMReader;
import com.helger.commons.xml.transform.XMLTransformerFactory;

public class MainFindInvalidXMLChars
{
  private static String _getFormatted (final List <Integer> x)
  {
    if (x.isEmpty ())
      return "false";
    final int nRadix = 16;
    if (x.size () == 1)
      return "c == 0x" + Integer.toString (x.get (0).intValue (), nRadix);
    final StringBuilder ret = new StringBuilder ();
    int nIndex = 0;
    int nFirst = -1;
    int nLast = -1;
    do
    {
      final int nValue = x.get (nIndex).intValue ();
      if (nFirst < 0)
      {
        // First item
        nFirst = nLast = nValue;
      }
      else
        if (nValue == nLast + 1)
        {
          nLast = nValue;
        }
        else
        {
          if (ret.length () > 0)
            ret.append (" || ");
          if (nFirst == nLast)
            ret.append ("(c == 0x" + Integer.toString (nFirst, nRadix) + ")");
          else
            ret.append ("(c >= 0x" +
                        Integer.toString (nFirst, nRadix) +
                        " && c <= 0x" +
                        Integer.toString (nLast, nRadix) +
                        ")");
          nFirst = nLast = nValue;
        }
      ++nIndex;
    } while (nIndex < x.size ());
    if (nLast > nFirst)
    {
      if (ret.length () > 0)
        ret.append (" || ");
      ret.append ("(c >= 0x" +
                  Integer.toString (nFirst, nRadix) +
                  " && c <= 0x" +
                  Integer.toString (nLast, nRadix) +
                  ")");
    }
    return ret.toString ();
  }

  public static void main (final String [] args) throws Exception
  {
    final EXMLVersion eXMLVersion = EXMLVersion.XML_10;
    final int nMax = Character.MAX_VALUE + 1;
    final List <Integer> aForbiddenE1 = new ArrayList <Integer> ();
    for (int i = 0; i < nMax; ++i)
    {
      final Document aDoc = XMLFactory.newDocument (eXMLVersion);
      try
      {
        aDoc.appendChild (aDoc.createElement (Character.toString ((char) i)));
        final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
        XMLTransformerFactory.newTransformer ().transform (new DOMSource (aDoc), new StreamResult (aSW));
        DOMReader.readXMLDOM (aSW.getAsString ());
      }
      catch (final Exception ex)
      {
        aForbiddenE1.add (Integer.valueOf (i));
      }
    }
    final List <Integer> aForbiddenE2 = new ArrayList <Integer> ();
    for (int i = 0; i < nMax; ++i)
    {
      final Document aDoc = XMLFactory.newDocument (eXMLVersion);
      try
      {
        aDoc.appendChild (aDoc.createElement ("a" + Character.toString ((char) i)));
        final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
        XMLTransformerFactory.newTransformer ().transform (new DOMSource (aDoc), new StreamResult (aSW));
        DOMReader.readXMLDOM (aSW.getAsString ());
      }
      catch (final Exception ex)
      {
        aForbiddenE2.add (Integer.valueOf (i));
      }
    }
    final List <Integer> aForbiddenAN1 = new ArrayList <Integer> ();
    for (int i = 0; i < nMax; ++i)
    {
      final Document aDoc = XMLFactory.newDocument (eXMLVersion);
      try
      {
        final Element aElement = (Element) aDoc.appendChild (aDoc.createElement ("abc"));
        aElement.setAttribute (Character.toString ((char) i), "xyz");
        final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
        XMLTransformerFactory.newTransformer ().transform (new DOMSource (aDoc), new StreamResult (aSW));
        DOMReader.readXMLDOM (aSW.getAsString ());
      }
      catch (final Exception ex)
      {
        aForbiddenAN1.add (Integer.valueOf (i));
      }
    }
    final List <Integer> aForbiddenAN2 = new ArrayList <Integer> ();
    for (int i = 0; i < nMax; ++i)
    {
      final Document aDoc = XMLFactory.newDocument (eXMLVersion);
      try
      {
        final Element aElement = (Element) aDoc.appendChild (aDoc.createElement ("abc"));
        aElement.setAttribute ("a" + Character.toString ((char) i), "xyz");
        final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
        XMLTransformerFactory.newTransformer ().transform (new DOMSource (aDoc), new StreamResult (aSW));
        DOMReader.readXMLDOM (aSW.getAsString ());
      }
      catch (final Exception ex)
      {
        aForbiddenAN2.add (Integer.valueOf (i));
      }
    }
    final List <Integer> aForbiddenAV = new ArrayList <Integer> ();
    for (int i = 0; i < nMax; ++i)
    {
      final Document aDoc = XMLFactory.newDocument (eXMLVersion);
      try
      {
        final Element aElement = (Element) aDoc.appendChild (aDoc.createElement ("abc"));
        aElement.setAttribute ("a", Character.toString ((char) i));
        final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
        XMLTransformerFactory.newTransformer ().transform (new DOMSource (aDoc), new StreamResult (aSW));
        DOMReader.readXMLDOM (aSW.getAsString ());
      }
      catch (final Exception ex)
      {
        aForbiddenAV.add (Integer.valueOf (i));
      }
    }
    final List <Integer> aForbiddenTV = new ArrayList <Integer> ();
    for (int i = 0; i < nMax; ++i)
    {
      final Document aDoc = XMLFactory.newDocument (eXMLVersion);
      try
      {
        final Element aElement = (Element) aDoc.appendChild (aDoc.createElement ("abc"));
        aElement.appendChild (aDoc.createTextNode (Character.toString ((char) i)));
        final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
        XMLTransformerFactory.newTransformer ().transform (new DOMSource (aDoc), new StreamResult (aSW));
        DOMReader.readXMLDOM (aSW.getAsString ());
      }
      catch (final Exception ex)
      {
        aForbiddenTV.add (Integer.valueOf (i));
      }
    }
    final List <Integer> aForbiddenCV = new ArrayList <Integer> ();
    for (int i = 0; i < nMax; ++i)
    {
      final Document aDoc = XMLFactory.newDocument (eXMLVersion);
      try
      {
        final Element aElement = (Element) aDoc.appendChild (aDoc.createElement ("abc"));
        aElement.appendChild (aDoc.createCDATASection (Character.toString ((char) i)));
        final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
        XMLTransformerFactory.newTransformer ().transform (new DOMSource (aDoc), new StreamResult (aSW));
        DOMReader.readXMLDOM (aSW.getAsString ());
      }
      catch (final Exception ex)
      {
        aForbiddenCV.add (Integer.valueOf (i));
      }
    }
    System.out.println ("Forbidden Element Name Start:       " + _getFormatted (aForbiddenE1));
    System.out.println ("Forbidden Element Name InBetween:   " + _getFormatted (aForbiddenE2));
    System.out.println ("Forbidden Attribute Name Start:     " + _getFormatted (aForbiddenAN1));
    System.out.println ("Forbidden Attribute Name InBetween: " + _getFormatted (aForbiddenAN2));
    System.out.println ("Forbidden Attribute Value: " + _getFormatted (aForbiddenAV));
    System.out.println ("Forbidden Text Value:      " + _getFormatted (aForbiddenTV));
    System.out.println ("Forbidden CDATA Value:     " + _getFormatted (aForbiddenCV));
  }
}
