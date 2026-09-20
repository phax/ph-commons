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
package com.helger.xml.util.mime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsOrderedSet;
import com.helger.mime.CMimeType;
import com.helger.mime.parse.MimeTypeParserException;
import com.helger.unittest.support.TestHelper;
import com.helger.xml.util.mime.MimeTypeInfo.ExtensionWithSource;
import com.helger.xml.util.mime.MimeTypeInfo.MimeTypeWithSource;

/**
 * Test class for class {@link MimeTypeInfo}.
 *
 * @author Philip Helger
 */
public final class MimeTypeInfoTest
{
  private static ICommonsOrderedSet <MimeTypeWithSource> _mimeTypes ()
  {
    return new CommonsLinkedHashSet <> (new MimeTypeWithSource (CMimeType.TEXT_PLAIN, "src1"),
                                        new MimeTypeWithSource (CMimeType.TEXT_HTML));
  }

  private static ICommonsOrderedSet <ExtensionWithSource> _extensions ()
  {
    return new CommonsLinkedHashSet <> (new ExtensionWithSource ("txt", "src1"), new ExtensionWithSource ("text"));
  }

  private static MimeTypeInfo _create ()
  {
    return new MimeTypeInfo (_mimeTypes (),
                             "any comment",
                             new CommonsLinkedHashSet <> ("text/*"),
                             new CommonsLinkedHashSet <> ("*.txt"),
                             _extensions (),
                             "the source");
  }

  @Test
  public void testMimeTypeWithSource () throws MimeTypeParserException
  {
    final MimeTypeWithSource aMTS = new MimeTypeWithSource (CMimeType.TEXT_PLAIN, "src1");
    assertEquals (CMimeType.TEXT_PLAIN, aMTS.getMimeType ());
    assertEquals (CMimeType.TEXT_PLAIN.getAsString (), aMTS.getMimeTypeAsString ());
    assertEquals ("src1", aMTS.getSource ());
    assertNotNull (aMTS.toString ());

    // No source
    final MimeTypeWithSource aNoSource = new MimeTypeWithSource (CMimeType.TEXT_PLAIN);
    assertNull (aNoSource.getSource ());

    // From String
    final MimeTypeWithSource aFromString = new MimeTypeWithSource (CMimeType.TEXT_PLAIN.getAsString ());
    assertEquals (CMimeType.TEXT_PLAIN, aFromString.getMimeType ());

    TestHelper.testDefaultImplementationWithEqualContentObject (aMTS,
                                                                new MimeTypeWithSource (CMimeType.TEXT_PLAIN,
                                                                                        "src1"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aMTS,
                                                                    new MimeTypeWithSource (CMimeType.TEXT_HTML,
                                                                                            "src1"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aMTS, aNoSource);
  }

  @Test
  public void testExtensionWithSource ()
  {
    final ExtensionWithSource aEWS = new ExtensionWithSource ("txt", "src1");
    assertEquals ("txt", aEWS.getExtension ());
    assertEquals ("src1", aEWS.getSource ());
    assertNotNull (aEWS.toString ());

    assertTrue (aEWS.matches ("txt"));
    assertTrue (aEWS.matches ("TXT"));
    assertFalse (aEWS.matches ("doc"));

    final ExtensionWithSource aNoSource = new ExtensionWithSource ("txt");
    assertNull (aNoSource.getSource ());

    TestHelper.testDefaultImplementationWithEqualContentObject (aEWS, new ExtensionWithSource ("txt", "src1"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aEWS, new ExtensionWithSource ("doc", "src1"));
    TestHelper.testDefaultImplementationWithDifferentContentObject (aEWS, aNoSource);
  }

  @Test
  public void testMimeTypes ()
  {
    final MimeTypeInfo aInfo = _create ();
    assertEquals (2, aInfo.getAllMimeTypesWithSource ().size ());
    assertEquals (2, aInfo.getAllMimeTypes ().size ());
    assertEquals (2, aInfo.getAllMimeTypeStrings ().size ());

    assertTrue (aInfo.containsMimeType (CMimeType.TEXT_PLAIN));
    assertFalse (aInfo.containsMimeType (CMimeType.APPLICATION_JSON));
    assertFalse (aInfo.containsMimeType ((com.helger.mime.IMimeType) null));

    assertTrue (aInfo.containsMimeType (CMimeType.TEXT_PLAIN.getAsString ()));
    assertFalse (aInfo.containsMimeType ("does/not-exist"));
    assertFalse (aInfo.containsMimeType ((String) null));

    assertNotNull (aInfo.getPrimaryMimeTypeWithSource ());
    assertEquals (CMimeType.TEXT_PLAIN, aInfo.getPrimaryMimeType ());
    assertEquals (CMimeType.TEXT_PLAIN.getAsString (), aInfo.getPrimaryMimeTypeString ());
  }

  @Test
  public void testCommentParentTypesAndGlobs ()
  {
    final MimeTypeInfo aInfo = _create ();
    assertEquals ("any comment", aInfo.getComment ());
    assertTrue (aInfo.hasComment ());

    assertEquals (1, aInfo.getAllParentTypes ().size ());
    assertTrue (aInfo.hasAnyParentType ());

    assertEquals (1, aInfo.getAllGlobs ().size ());
    assertEquals ("*.txt", aInfo.getPrimaryGlob ());
    assertTrue (aInfo.hasAnyGlob ());


    assertEquals ("the source", aInfo.getSource ());
    assertTrue (aInfo.hasSource ());
    assertNotNull (aInfo.toString ());
  }

  @Test
  public void testExtensions ()
  {
    final MimeTypeInfo aInfo = _create ();
    assertEquals (2, aInfo.getAllExtensionsWithSource ().size ());
    assertEquals (2, aInfo.getAllExtensions ().size ());
    assertNotNull (aInfo.getPrimaryExtensionWithSource ());
    assertEquals ("txt", aInfo.getPrimaryExtension ());
    assertTrue (aInfo.hasAnyExtension ());

    assertTrue (aInfo.containsExtension ("txt"));
    assertTrue (aInfo.containsExtension ("TXT"));
    assertFalse (aInfo.containsExtension ("doc"));
    assertFalse (aInfo.containsExtension (null));
  }

  @Test
  public void testMinimalInfo ()
  {
    final MimeTypeInfo aInfo = new MimeTypeInfo (_mimeTypes (),
                                                 null,
                                                 new CommonsLinkedHashSet <> (),
                                                 new CommonsLinkedHashSet <> (),
                                                 new CommonsLinkedHashSet <> (),
                                                 null);
    assertNull (aInfo.getComment ());
    assertFalse (aInfo.hasComment ());
    assertFalse (aInfo.hasAnyParentType ());
    assertFalse (aInfo.hasAnyGlob ());
    assertFalse (aInfo.hasAnyExtension ());
    assertNull (aInfo.getSource ());
    assertFalse (aInfo.hasSource ());
    assertTrue (aInfo.getAllExtensions ().isEmpty ());
    assertTrue (aInfo.getAllGlobs ().isEmpty ());
    assertTrue (aInfo.getAllParentTypes ().isEmpty ());
    assertNull (aInfo.getPrimaryGlob ());
    assertNull (aInfo.getPrimaryExtension ());
    assertNull (aInfo.getPrimaryExtensionWithSource ());
  }

  @Test
  public void testEqualsHashcode ()
  {
    TestHelper.testDefaultImplementationWithEqualContentObject (_create (), _create ());
    // Only the MIME types are relevant for equals and hashCode
    TestHelper.testDefaultImplementationWithDifferentContentObject (_create (),
                                                                    new MimeTypeInfo (new CommonsLinkedHashSet <> (new MimeTypeWithSource (CMimeType.APPLICATION_JSON)),
                                                                                      "any comment",
                                                                                      new CommonsLinkedHashSet <> ("text/*"),
                                                                                      new CommonsLinkedHashSet <> ("*.txt"),
                                                                                      _extensions (),
                                                                                      "the source"));
  }

  @Test
  public void testInvalidParams ()
  {
    try
    {
      new MimeTypeInfo (new CommonsLinkedHashSet <> (),
                        null,
                        new CommonsLinkedHashSet <> (),
                        new CommonsLinkedHashSet <> (),
                        new CommonsLinkedHashSet <> (),
                        null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
    try
    {
      new MimeTypeInfo (_mimeTypes (), null, null, new CommonsLinkedHashSet <> (), new CommonsLinkedHashSet <> (), null);
      fail ();
    }
    catch (final NullPointerException | IllegalArgumentException ex)
    {
      // expected
    }
  }
}
