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
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.xml.EXMLVersion;
import com.helger.commons.xml.XMLFactory;
import com.helger.commons.xml.serialize.write.EXMLSerializeVersion;
import com.helger.commons.xml.serialize.write.XMLCharHelper;
import com.helger.commons.xml.transform.XMLTransformerFactory;

public class MainFindMaskedXMLChars
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

  private static boolean _containsER (final String s, final int c)
  {
    if (s.indexOf ("&#" + c + ";") >= 0)
      return true;
    if (s.indexOf ("&#x" + Integer.toString (c, 16) + ";") >= 0)
      return true;
    return RegExHelper.stringMatchesPattern (".+&[a-z]+;.+", s);
  }

  /**
   * XML 1.0:
   *
   * <pre>
   * Masked Element Name Start:       false
   * Masked Element Name InBetween:   false
   * Masked Attribute Name Start:     false
   * Masked Attribute Name InBetween: false
   * Masked Attribute Value: (c >= 0x9 && c <= 0xa) || (c == 0xd) || (c == 0x22) || (c == 0x26) || (c == 0x3c)
   * Masked Text Value:      (c == 0xd) || (c == 0x26) || (c == 0x3c) || (c == 0x3e) || (c >= 0x7f && c <= 0x9f)
   * Masked CDATA Value:     false
   * </pre>
   *
   * XML 1.1:
   *
   * <pre>
   * Masked Element Name Start:       false
   * Masked Element Name InBetween:   false
   * Masked Attribute Name Start:     false
   * Masked Attribute Name InBetween: false
   * Masked Attribute Value: (c >= 0x1 && c <= 0x1f) || (c == 0x22) || (c == 0x26) || (c == 0x3c)
   * Masked Text Value:      (c >= 0x1 && c <= 0x8) || (c >= 0xb && c <= 0x1f) || (c == 0x26) || (c == 0x3c) || (c == 0x3e) || (c >= 0x7f && c <= 0x9f)
   * Masked CDATA Value:     (c >= 0x1 && c <= 0x8) || (c >= 0xb && c <= 0xc) || (c >= 0xe && c <= 0x1f)
   * </pre>
   *
   * @param args
   *        main args
   * @throws Exception
   *         in case of err
   */
  public static void main (final String [] args) throws Exception
  {
    final EXMLVersion eXMLVersion = EXMLVersion.XML_11;
    final EXMLSerializeVersion eXMLSerializeVersion = EXMLSerializeVersion.getFromXMLVersionOrThrow (eXMLVersion);
    final int nMax = Character.MAX_VALUE + 1;

    final List <Integer> aMaskedE1 = new ArrayList <Integer> ();
    for (int i = 0; i < nMax; ++i)
      if (!XMLCharHelper.isInvalidXMLNameStartChar (eXMLSerializeVersion, (char) i))
      {
        final Document aDoc = XMLFactory.newDocument (eXMLVersion);
        aDoc.appendChild (aDoc.createElement (Character.toString ((char) i)));
        final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
        XMLTransformerFactory.newTransformer ().transform (new DOMSource (aDoc), new StreamResult (aSW));
        if (_containsER (aSW.getAsString (), i))
          aMaskedE1.add (Integer.valueOf (i));
      }
    final List <Integer> aMaskedE2 = new ArrayList <Integer> ();
    for (int i = 0; i < nMax; ++i)
      if (!XMLCharHelper.isInvalidXMLNameChar (eXMLSerializeVersion, (char) i))
      {
        final Document aDoc = XMLFactory.newDocument (eXMLVersion);
        aDoc.appendChild (aDoc.createElement ("a" + Character.toString ((char) i)));
        final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
        XMLTransformerFactory.newTransformer ().transform (new DOMSource (aDoc), new StreamResult (aSW));
        if (_containsER (aSW.getAsString (), i))
          aMaskedE2.add (Integer.valueOf (i));
      }
    final List <Integer> aMaskedAN1 = new ArrayList <Integer> ();
    for (int i = 0; i < nMax; ++i)
      if (!XMLCharHelper.isInvalidXMLNameStartChar (eXMLSerializeVersion, (char) i))
      {
        final Document aDoc = XMLFactory.newDocument (eXMLVersion);
        final Element aElement = (Element) aDoc.appendChild (aDoc.createElement ("abc"));
        aElement.setAttribute (Character.toString ((char) i), "xyz");
        final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
        XMLTransformerFactory.newTransformer ().transform (new DOMSource (aDoc), new StreamResult (aSW));
        if (_containsER (aSW.getAsString (), i))
          aMaskedAN1.add (Integer.valueOf (i));
      }
    final List <Integer> aMaskedAN2 = new ArrayList <Integer> ();
    for (int i = 0; i < nMax; ++i)
      if (!XMLCharHelper.isInvalidXMLNameChar (eXMLSerializeVersion, (char) i))
      {
        final Document aDoc = XMLFactory.newDocument (eXMLVersion);
        final Element aElement = (Element) aDoc.appendChild (aDoc.createElement ("abc"));
        aElement.setAttribute ("a" + Character.toString ((char) i), "xyz");
        final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
        XMLTransformerFactory.newTransformer ().transform (new DOMSource (aDoc), new StreamResult (aSW));
        if (_containsER (aSW.getAsString (), i))
          aMaskedAN2.add (Integer.valueOf (i));
      }
    final List <Integer> aMaskedAV = new ArrayList <Integer> ();
    for (int i = 0; i < nMax; ++i)
      if (!XMLCharHelper.isInvalidXMLAttributeValueChar (eXMLSerializeVersion, (char) i))
      {
        final Document aDoc = XMLFactory.newDocument (eXMLVersion);
        final Element aElement = (Element) aDoc.appendChild (aDoc.createElement ("abc"));
        aElement.setAttribute ("a", Character.toString ((char) i));
        final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
        XMLTransformerFactory.newTransformer ().transform (new DOMSource (aDoc), new StreamResult (aSW));
        if (_containsER (aSW.getAsString (), i))
          aMaskedAV.add (Integer.valueOf (i));
      }
    final List <Integer> aMaskedTV = new ArrayList <Integer> ();
    for (int i = 0; i < nMax; ++i)
      if (!XMLCharHelper.isInvalidXMLTextChar (eXMLSerializeVersion, (char) i))
      {
        final Document aDoc = XMLFactory.newDocument (eXMLVersion);
        final Element aElement = (Element) aDoc.appendChild (aDoc.createElement ("abc"));
        aElement.appendChild (aDoc.createTextNode (Character.toString ((char) i)));
        final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
        XMLTransformerFactory.newTransformer ().transform (new DOMSource (aDoc), new StreamResult (aSW));
        if (_containsER (aSW.getAsString (), i))
          aMaskedTV.add (Integer.valueOf (i));
      }
    final List <Integer> aMaskedCV = new ArrayList <Integer> ();
    for (int i = 0; i < nMax; ++i)
    {
      final Document aDoc = XMLFactory.newDocument (eXMLVersion);
      final Element aElement = (Element) aDoc.appendChild (aDoc.createElement ("abc"));
      aElement.appendChild (aDoc.createCDATASection (Character.toString ((char) i)));
      final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
      XMLTransformerFactory.newTransformer ().transform (new DOMSource (aDoc), new StreamResult (aSW));
      final String sXML = aSW.getAsString ();
      if (sXML.indexOf ("<[CDATA[") >= 0 && _containsER (sXML, i))
        aMaskedCV.add (Integer.valueOf (i));
    }
    System.out.println ("Masked Element Name Start:       " + _getFormatted (aMaskedE1));
    System.out.println ("Masked Element Name InBetween:   " + _getFormatted (aMaskedE2));
    System.out.println ("Masked Attribute Name Start:     " + _getFormatted (aMaskedAN1));
    System.out.println ("Masked Attribute Name InBetween: " + _getFormatted (aMaskedAN2));
    System.out.println ("Masked Attribute Value: " + _getFormatted (aMaskedAV));
    System.out.println ("Masked Text Value:      " + _getFormatted (aMaskedTV));
    System.out.println ("Masked CDATA Value:     " + _getFormatted (aMaskedCV));
  }
}
