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
package com.helger.xml.xpath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.helger.xml.namespace.MapBasedNamespaceContext;
import com.helger.xml.serialize.read.DOMReader;

public class FuncTestXPath
{
  @Test
  public void testBasicUBL () throws XPathExpressionException
  {
    final String sUBL = "<Invoice xmlns='bla' xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
                        "<ds:SignedInfo>\n" +
                        "<ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>\n" +
                        "<ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/>\n" +
                        "<ds:Reference Id=\"id-doc-signed-data\" URI=\"\">\n" +
                        "<ds:Transforms>\n" +
                        "<ds:Transform Algorithm=\"http://www.w3.org/TR/1999/REC-xpath-19991116\">\n" +
                        "<ds:XPath>not(//ancestor-or-self::ext:UBLExtensions)</ds:XPath>\n" +
                        "</ds:Transform>\n" +
                        "<ds:Transform Algorithm=\"http://www.w3.org/TR/1999/REC-xpath-19991116\">\n" +
                        "<ds:XPath>not(//ancestor-or-self::cac:Signature)</ds:XPath>\n" +
                        "</ds:Transform>\n" +
                        "<ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>\n" +
                        "</ds:Transforms>\n" +
                        "<ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>\n" +
                        "<ds:DigestValue>9Ajw/lNpwRWfqdrLeKFePPZJnBXoT9eKzThl1NRflzY=</ds:DigestValue>\n" +
                        "</ds:Reference>\n" +
                        "<ds:Reference Type=\"http://www.w3.org/2000/09/xmldsig#SignatureProperties\" URI=\"#id-xades-signed-props\">\n" +
                        "<ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>\n" +
                        "<ds:DigestValue>dgpDYlwgaRF542ogPTVcdHAQ1q47QgDfd6m+GeCgwfE=</ds:DigestValue>\n" +
                        "</ds:Reference>\n" +
                        "</ds:SignedInfo>\n" +
                        "<ds:SignatureValue>OzgXCgMuioS9zGu/X0hywz+s3vkZVwkuNlefT8Ni1LjOEJqbUw2YFy9IzqxWrnpi6k4JzeHE40cHPFuXwDFkEi4/1MPCbyOPi/EWvc1rp/8sQoaMWG5e4965xKQH7lfX+WYpqhIWRyEEb4X5teVn/jI7u2AuwWlQSuQRHismaF2YJqtwey1VyxJE805wuWWB/3itlzlMSVbO7B9tLpPIL0f6WqENXFEa8yD4kisY9qb6wehIK9fDg3pfaY4g4xzF/ba8H+6h22QNNok3RfXyuWvna8XuhrGR6gOh/5s6FdPxcb1A9aAaB3OUuk+chNxdptdLyC9EZ6AnoJE8jy5ruA==</ds:SignatureValue>\n" +
                        "</Invoice>";
    final Document doc = DOMReader.readXMLDOM (sUBL);
    assertNotNull (doc);

    final XPathFactory xpFactory = XPathFactory.newInstance ();
    final XPath xp = xpFactory.newXPath ();
    final MapBasedNamespaceContext nsContext = new MapBasedNamespaceContext ();
    nsContext.addMapping ("ds", "http://www.w3.org/2000/09/xmldsig#");
    xp.setNamespaceContext (nsContext);

    final NodeList nl = (NodeList) xp.evaluate ("//ds:Transform", doc, XPathConstants.NODESET);
    assertNotNull (nl);
    assertEquals (3, nl.getLength ());
  }
}
