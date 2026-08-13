/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.xml.serialize.read;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.helger.io.resource.ClassPathResource;
import com.helger.xml.EXMLParserFeature;
import com.helger.xml.XMLFactory;
import com.helger.xml.sax.CollectingSAXErrorHandler;

/**
 * Test class for XInclude handling in {@link DOMReader}.<br>
 * The fixtures under <code>xml/xinclude/</code> mirror the structure of a UBL
 * <code>ext:UBLExtension</code> that embeds an <code>xi:include</code> with an
 * <code>xi:fallback</code>. They are intentionally kept fully local: no fixture references a
 * network resource, and the "XXE" fixture only points at the local <code>test1.txt</code>. The
 * point of these tests is to prove that the {@link XMLFactory} / {@link DOMReaderSettings} defaults
 * neutralize the XInclude + external-entity chain, not to actually perform it.
 *
 * @author Philip Helger
 */
public final class XIncludeDOMReaderTest
{
  private static final String XINCLUDE_NS = "http://www.w3.org/2001/XInclude";

  @Test
  public void testXIncludeDisabledByDefault ()
  {
    // XMLFactory / DOMReaderSettings are not XInclude aware by default
    assertEquals (Boolean.FALSE, Boolean.valueOf (XMLFactory.DEFAULT_DOM_XINCLUDE_AWARE));
    assertEquals (Boolean.FALSE, Boolean.valueOf (new DOMReaderSettings ().isXIncludeAware ()));

    // Read with the default settings
    final Document aDoc = DOMReader.readXMLDOM (new ClassPathResource ("xml/xinclude/main-benign.xml"));
    assertNotNull (aDoc);

    // The xi:include element is left untouched (no inclusion happened) and the
    // target content was NOT pulled in
    assertTrue (aDoc.getElementsByTagNameNS (XINCLUDE_NS, "include").getLength () > 0);
    assertEquals (0, aDoc.getElementsByTagName ("greeting").getLength ());
  }

  @Test
  public void testXIncludeEnabledResolvesLocalTarget ()
  {
    final DOMReaderSettings aDRS = new DOMReaderSettings ().setXIncludeAware (true);
    final Document aDoc = DOMReader.readXMLDOM (new ClassPathResource ("xml/xinclude/main-benign.xml"), aDRS);
    assertNotNull (aDoc);

    // The xi:include element was replaced by the target document element
    assertEquals (0, aDoc.getElementsByTagNameNS (XINCLUDE_NS, "include").getLength ());
    assertEquals (1, aDoc.getElementsByTagName ("greeting").getLength ());
    final Element aGreeting = (Element) aDoc.getElementsByTagName ("greeting").item (0);
    assertEquals ("Hello from the XInclude target", aGreeting.getTextContent ());
  }

  @Test
  public void testXIncludeFallbackIsUsed ()
  {
    final DOMReaderSettings aDRS = new DOMReaderSettings ().setXIncludeAware (true);
    // href points to a non-existing file => the xi:fallback branch is taken
    final Document aDoc = DOMReader.readXMLDOM (new ClassPathResource ("xml/xinclude/main-missing.xml"), aDRS);
    assertNotNull (aDoc);

    assertEquals (0, aDoc.getElementsByTagNameNS (XINCLUDE_NS, "include").getLength ());
    assertEquals (1, aDoc.getElementsByTagName ("fallback").getLength ());
    assertEquals ("Fallback content was used", aDoc.getElementsByTagName ("fallback").item (0).getTextContent ());
  }

  @Test
  public void testXIncludedDocumentWithDoctypeIsRejected ()
  {
    // XInclude is enabled, but the DEFAULT security settings
    // (DISALLOW_DOCTYPE_DECL=true, external entities off) still apply to the
    // included sub-document. The included poc-read-local.xml carries a DOCTYPE
    // with an external general entity - the very shape used for out-of-band
    // exfiltration. It must be refused, so no file is ever read.
    final CollectingSAXErrorHandler aCEH = new CollectingSAXErrorHandler ();
    final DOMReaderSettings aDRS = new DOMReaderSettings ().setXIncludeAware (true).setErrorHandler (aCEH);

    final Document aDoc = DOMReader.readXMLDOM (new ClassPathResource ("xml/xinclude/main-xxe.xml"), aDRS);
    assertNull (aDoc);

    assertTrue ("Expected at least one parse error", aCEH.getErrorList ().isNotEmpty ());
    assertTrue ("Error should mention the disallowed DOCTYPE feature",
                aCEH.getErrorList ()
                    .getFirstOrNull ()
                    .getErrorText (Locale.ROOT)
                    .contains (EXMLParserFeature.DISALLOW_DOCTYPE_DECL.getName ()));
  }
}
