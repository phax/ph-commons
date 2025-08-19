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
package com.helger.xml.serialize.write;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Ignore;
import org.junit.Test;

import com.helger.base.charset.CharsetHelper;
import com.helger.base.mock.CommonsAssert;
import com.helger.base.string.StringHelper;
import com.helger.base.system.ENewLineMode;
import com.helger.unittest.support.TestHelper;
import com.helger.xml.namespace.MapBasedNamespaceContext;

/**
 * Test class for class {@link XMLWriterSettings}.
 *
 * @author Philip Helger
 */
public final class XMLWriterSettingsTest
{
  private static final boolean [] BOOLS = { true, false };

  @Test
  public void testDefault ()
  {
    IXMLWriterSettings mws = XMLWriterSettings.DEFAULT_XML_SETTINGS;
    assertEquals (EXMLSerializeXMLDeclaration.EMIT, mws.getSerializeXMLDeclaration ());
    assertEquals (EXMLSerializeDocType.EMIT, mws.getSerializeDocType ());
    assertTrue (mws.isNewLineAfterXMLDeclaration ());
    assertEquals (EXMLSerializeComments.EMIT, mws.getSerializeComments ());
    assertEquals (XMLWriterSettings.DEFAULT_XML_CHARSET, mws.getCharset ().name ());
    assertEquals (EXMLSerializeIndent.INDENT_AND_ALIGN, mws.getIndent ());
    assertEquals (StandardCharsets.UTF_8, mws.getCharset ());
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
    assertEquals (XMLWriterSettings.DEFAULT_XML_CHARSET, mws.getCharset ().name ());
    assertEquals (EXMLSerializeIndent.INDENT_AND_ALIGN, mws.getIndent ());
    assertEquals (StandardCharsets.UTF_8, mws.getCharset ());
    assertTrue (mws.isSpaceOnSelfClosedElement ());
    assertTrue (mws.isUseDoubleQuotesForAttributes ());
    assertTrue (mws.isEmitNamespaces ());
    assertFalse (mws.isPutNamespaceContextPrefixesInRoot ());

    TestHelper.testDefaultImplementationWithEqualContentObject (mws, new XMLWriterSettings ());
    TestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setSerializeXMLDeclaration (EXMLSerializeXMLDeclaration.IGNORE));
    TestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setSerializeDocType (EXMLSerializeDocType.IGNORE));
    TestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setNewLineAfterXMLDeclaration (false));
    TestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setSerializeComments (EXMLSerializeComments.IGNORE));
    TestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE));
    TestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setCharset (StandardCharsets.US_ASCII));
    TestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setNamespaceContext (new MapBasedNamespaceContext ().addMapping ("prefix",
                                                                                                                                                                     "uri")));
    TestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setSpaceOnSelfClosedElement (false));
    TestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setUseDoubleQuotesForAttributes (false));
    TestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setNewLineMode (ENewLineMode.DEFAULT ==
                                                                                                                    ENewLineMode.WINDOWS ? ENewLineMode.UNIX
                                                                                                                                         : ENewLineMode.WINDOWS));
    TestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setIndentationString ("\t"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setEmitNamespaces (false));
    TestHelper.testDefaultImplementationWithDifferentContentObject (mws,
                                                                           new XMLWriterSettings ().setPutNamespaceContextPrefixesInRoot (true));
  }

  @Test
  @Ignore ("Tested and works but takes too long")
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
        for (final boolean bNewLineAfterXMLDecl : BOOLS)
        {
          aXWS.setNewLineAfterXMLDeclaration (bNewLineAfterXMLDecl);
          CommonsAssert.assertEquals (bNewLineAfterXMLDecl, aXWS.isNewLineAfterXMLDeclaration ());
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
                for (final Charset aCS : CharsetHelper.getAllCharsets ().values ())
                {
                  aXWS.setCharset (aCS);
                  assertEquals (aCS, aXWS.getCharset ());
                  assertEquals (aCS.name (), aXWS.getCharset ().name ());
                  for (final boolean bUseDoubleQuotesForAttributes : BOOLS)
                  {
                    aXWS.setUseDoubleQuotesForAttributes (bUseDoubleQuotesForAttributes);
                    CommonsAssert.assertEquals (bUseDoubleQuotesForAttributes, aXWS.isUseDoubleQuotesForAttributes ());
                    for (final boolean bSpaceOnSelfClosedElement : BOOLS)
                    {
                      aXWS.setSpaceOnSelfClosedElement (bSpaceOnSelfClosedElement);
                      CommonsAssert.assertEquals (bSpaceOnSelfClosedElement, aXWS.isSpaceOnSelfClosedElement ());
                      for (final ENewLineMode eNewlineMode : ENewLineMode.values ())
                      {
                        aXWS.setNewLineMode (eNewlineMode);
                        assertEquals (eNewlineMode, aXWS.getNewLineMode ());
                        assertTrue (StringHelper.isNotEmpty (aXWS.getNewLineString ()));
                        for (final String sIndentation : new String [] { "\t", "  " })
                        {
                          aXWS.setIndentationString (sIndentation);
                          assertEquals (sIndentation, aXWS.getIndentationString ());
                          for (final boolean bEmitNamespaces : BOOLS)
                          {
                            aXWS.setEmitNamespaces (bEmitNamespaces);
                            CommonsAssert.assertEquals (bEmitNamespaces, aXWS.isEmitNamespaces ());
                            for (final boolean bPutNamespaceContextPrefixesInRoot : BOOLS)
                            {
                              aXWS.setPutNamespaceContextPrefixesInRoot (bPutNamespaceContextPrefixesInRoot);
                              CommonsAssert.assertEquals (bPutNamespaceContextPrefixesInRoot,
                                                          aXWS.isPutNamespaceContextPrefixesInRoot ());
                              final XMLWriterSettings aXWS2 = new XMLWriterSettings ().setSerializeXMLDeclaration (eXMLDecl)
                                                                                      .setSerializeDocType (eDocType)
                                                                                      .setNewLineAfterXMLDeclaration (bNewLineAfterXMLDecl)
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
                              TestHelper.testEqualsImplementationWithEqualContentObject (aXWS, aXWS2);
                              TestHelper.testHashcodeImplementationWithEqualContentObject (aXWS, aXWS2);
                              // Main time is spent in the "toString" calls - so
                              // don't test it in the loop
                            }
                            CommonsAssert.assertEquals (bEmitNamespaces, aXWS.isEmitNamespaces ());
                          }
                          assertEquals (sIndentation, aXWS.getIndentationString ());
                        }
                        assertEquals (eNewlineMode, aXWS.getNewLineMode ());
                      }
                      CommonsAssert.assertEquals (bSpaceOnSelfClosedElement, aXWS.isSpaceOnSelfClosedElement ());
                    }
                    CommonsAssert.assertEquals (bUseDoubleQuotesForAttributes, aXWS.isUseDoubleQuotesForAttributes ());
                  }
                  assertEquals (aCS, aXWS.getCharset ());
                  assertEquals (aCS.name (), aXWS.getCharset ().name ());
                }
                assertEquals (eIncorrectCharHandling, aXWS.getIncorrectCharacterHandling ());
              }
              assertEquals (eIndent, aXWS.getIndent ());
            }
            assertEquals (eComments, aXWS.getSerializeComments ());
          }
          CommonsAssert.assertEquals (bNewLineAfterXMLDecl, aXWS.isNewLineAfterXMLDeclaration ());
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
      new XMLWriterSettings ().setSerializeVersion (null);
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
  }
}
