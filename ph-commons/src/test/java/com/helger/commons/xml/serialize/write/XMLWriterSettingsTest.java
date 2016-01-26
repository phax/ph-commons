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
package com.helger.commons.xml.serialize.write;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.Charset;

import org.junit.Test;

import com.helger.commons.charset.CCharset;
import com.helger.commons.charset.CharsetManager;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.system.ENewLineMode;
import com.helger.commons.xml.namespace.MapBasedNamespaceContext;

/**
 * Test class for class {@link XMLWriterSettings}.
 *
 * @author Philip Helger
 */
public final class XMLWriterSettingsTest
{
  private static final boolean [] BOOLS = new boolean [] { true, false };

  @Test
  public void testDefault ()
  {
    IXMLWriterSettings mws = XMLWriterSettings.DEFAULT_XML_SETTINGS;
    assertEquals (EXMLSerializeXMLDeclaration.EMIT, mws.getSerializeXMLDeclaration ());
    assertEquals (EXMLSerializeDocType.EMIT, mws.getSerializeDocType ());
    assertEquals (EXMLSerializeComments.EMIT, mws.getSerializeComments ());
    assertEquals (XMLWriterSettings.DEFAULT_XML_CHARSET, mws.getCharset ());
    assertEquals (EXMLSerializeIndent.INDENT_AND_ALIGN, mws.getIndent ());
    assertEquals (CCharset.CHARSET_UTF_8_OBJ, mws.getCharsetObj ());
    assertTrue (mws.isSpaceOnSelfClosedElement ());
    assertTrue (mws.isUseDoubleQuotesForAttributes ());
    assertEquals (ENewLineMode.DEFAULT, mws.getNewLineMode ());
    assertEquals (ENewLineMode.DEFAULT.getText (), mws.getNewLineString ());
    assertEquals ("  ", mws.getIndentationString ());
    assertTrue (mws.isEmitNamespaces ());
    assertFalse (mws.isPutNamespaceContextPrefixesInRoot ());

    mws = new XMLWriterSettings ();
    assertEquals (EXMLSerializeXMLDeclaration.EMIT, mws.getSerializeXMLDeclaration ());
    assertEquals (EXMLSerializeDocType.EMIT, mws.getSerializeDocType ());
    assertEquals (EXMLSerializeComments.EMIT, mws.getSerializeComments ());
    assertEquals (XMLWriterSettings.DEFAULT_XML_CHARSET, mws.getCharset ());
    assertEquals (EXMLSerializeIndent.INDENT_AND_ALIGN, mws.getIndent ());
    assertEquals (CCharset.CHARSET_UTF_8_OBJ, mws.getCharsetObj ());
    assertTrue (mws.isSpaceOnSelfClosedElement ());
    assertTrue (mws.isUseDoubleQuotesForAttributes ());
    assertTrue (mws.isEmitNamespaces ());
    assertFalse (mws.isPutNamespaceContextPrefixesInRoot ());

    CommonsTestHelper.testDefaultImplementationWithEqualContentObject (mws, new XMLWriterSettings ());
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setSerializeXMLDeclaration (EXMLSerializeXMLDeclaration.IGNORE));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setSerializeDocType (EXMLSerializeDocType.IGNORE));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setSerializeComments (EXMLSerializeComments.IGNORE));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setCharset (CCharset.CHARSET_US_ASCII_OBJ));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setNamespaceContext (new MapBasedNamespaceContext ().addMapping ("prefix",
                                                                                                                                                                     "uri")));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setSpaceOnSelfClosedElement (false));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setUseDoubleQuotesForAttributes (false));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setNewLineMode (ENewLineMode.DEFAULT == ENewLineMode.WINDOWS ? ENewLineMode.UNIX
                                                                                                                                                                 : ENewLineMode.WINDOWS));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setIndentationString ("\t"));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setEmitNamespaces (false));
    CommonsTestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setPutNamespaceContextPrefixesInRoot (true));
  }

  @Test
  public void testPermutations ()
  {
    // Now try all permutations
    final XMLWriterSettings aXWS = new XMLWriterSettings ();
    for (final EXMLSerializeXMLDeclaration eXMLDecl : EXMLSerializeXMLDeclaration.values ())
    {
      aXWS.setSerializeXMLDeclaration (eXMLDecl);
      assertEquals (eXMLDecl, aXWS.getSerializeXMLDeclaration ());
      for (final EXMLSerializeDocType eDocType : EXMLSerializeDocType.values ())
      {
        aXWS.setSerializeDocType (eDocType);
        assertEquals (eDocType, aXWS.getSerializeDocType ());
        for (final EXMLSerializeComments eComments : EXMLSerializeComments.values ())
        {
          aXWS.setSerializeComments (eComments);
          assertEquals (eComments, aXWS.getSerializeComments ());
          for (final EXMLSerializeIndent eIndent : EXMLSerializeIndent.values ())
          {
            aXWS.setIndent (eIndent);
            assertEquals (eIndent, aXWS.getIndent ());
            for (final EXMLIncorrectCharacterHandling eIncorrectCharHandling : EXMLIncorrectCharacterHandling.values ())
            {
              aXWS.setIncorrectCharacterHandling (eIncorrectCharHandling);
              assertEquals (eIncorrectCharHandling, aXWS.getIncorrectCharacterHandling ());
              for (final Charset aCS : CharsetManager.getAllCharsets ().values ())
              {
                aXWS.setCharset (aCS);
                assertEquals (aCS, aXWS.getCharsetObj ());
                assertEquals (aCS.name (), aXWS.getCharset ());
                for (final boolean bUseDoubleQuotesForAttributes : BOOLS)
                {
                  aXWS.setUseDoubleQuotesForAttributes (bUseDoubleQuotesForAttributes);
                  assertTrue (bUseDoubleQuotesForAttributes == aXWS.isUseDoubleQuotesForAttributes ());
                  for (final boolean bSpaceOnSelfClosedElement : BOOLS)
                  {
                    aXWS.setSpaceOnSelfClosedElement (bSpaceOnSelfClosedElement);
                    assertTrue (bSpaceOnSelfClosedElement == aXWS.isSpaceOnSelfClosedElement ());
                    for (final ENewLineMode eNewlineMode : ENewLineMode.values ())
                    {
                      aXWS.setNewLineMode (eNewlineMode);
                      assertEquals (eNewlineMode, aXWS.getNewLineMode ());
                      assertTrue (StringHelper.hasText (aXWS.getNewLineString ()));
                      for (final String sIndentation : new String [] { "\t", "  " })
                      {
                        aXWS.setIndentationString (sIndentation);
                        assertEquals (sIndentation, aXWS.getIndentationString ());
                        for (final boolean bEmitNamespaces : BOOLS)
                        {
                          aXWS.setEmitNamespaces (bEmitNamespaces);
                          assertTrue (bEmitNamespaces == aXWS.isEmitNamespaces ());
                          for (final boolean bPutNamespaceContextPrefixesInRoot : BOOLS)
                          {
                            aXWS.setPutNamespaceContextPrefixesInRoot (bPutNamespaceContextPrefixesInRoot);
                            assertTrue (bPutNamespaceContextPrefixesInRoot == aXWS.isPutNamespaceContextPrefixesInRoot ());
                            final XMLWriterSettings aXWS2 = new XMLWriterSettings ().setSerializeXMLDeclaration (eXMLDecl)
                                                                                    .setSerializeDocType (eDocType)
                                                                                    .setSerializeComments (eComments)
                                                                                    .setIndent (eIndent)
                                                                                    .setIncorrectCharacterHandling (eIncorrectCharHandling)
                                                                                    .setCharset (aCS)
                                                                                    .setUseDoubleQuotesForAttributes (bUseDoubleQuotesForAttributes)
                                                                                    .setSpaceOnSelfClosedElement (bSpaceOnSelfClosedElement)
                                                                                    .setNewLineMode (eNewlineMode)
                                                                                    .setIndentationString (sIndentation)
                                                                                    .setEmitNamespaces (bEmitNamespaces)
                                                                                    .setPutNamespaceContextPrefixesInRoot (bPutNamespaceContextPrefixesInRoot);
                            CommonsTestHelper.testEqualsImplementationWithEqualContentObject (aXWS, aXWS2);
                            CommonsTestHelper.testHashcodeImplementationWithEqualContentObject (aXWS, aXWS2);
                            // Main time is spent in the "toString" calls - so
                            // don't test it in the loop
                          }
                          assertTrue (bEmitNamespaces == aXWS.isEmitNamespaces ());
                        }
                        assertEquals (sIndentation, aXWS.getIndentationString ());
                      }
                      assertEquals (eNewlineMode, aXWS.getNewLineMode ());
                    }
                    assertTrue (bSpaceOnSelfClosedElement == aXWS.isSpaceOnSelfClosedElement ());
                  }
                  assertTrue (bUseDoubleQuotesForAttributes == aXWS.isUseDoubleQuotesForAttributes ());
                }
                assertEquals (aCS, aXWS.getCharsetObj ());
                assertEquals (aCS.name (), aXWS.getCharset ());
              }
              assertEquals (eIncorrectCharHandling, aXWS.getIncorrectCharacterHandling ());
            }
            assertEquals (eIndent, aXWS.getIndent ());
          }
          assertEquals (eComments, aXWS.getSerializeComments ());
        }
        assertEquals (eDocType, aXWS.getSerializeDocType ());
      }
      assertEquals (eXMLDecl, aXWS.getSerializeXMLDeclaration ());
    }
  }

  @Test
  public void testNullParams ()
  {
    try
    {
      new XMLWriterSettings ().setCharset ((Charset) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new XMLWriterSettings ().setIndent (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new XMLWriterSettings ().setSerializeXMLDeclaration (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new XMLWriterSettings ().setSerializeDocType (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      new XMLWriterSettings ().setSerializeComments (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
