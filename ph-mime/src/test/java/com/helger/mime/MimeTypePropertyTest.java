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
package com.helger.mime;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.helger.mime.parse.MimeTypeParser;
import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for {@link MimeType} and {@link MimeTypeParser}: the string form of any MIME
 * type can be parsed back, with every quoting algorithm.
 *
 * @author Philip Helger
 */
public final class MimeTypePropertyTest
{
  private static final IGenerator <String> TOKENS = Generators.strings ("abz09-+.!#$&^_`|~*'%", 1, 8);

  /** Parameter values with special characters of the MIME syntax, whitespace and non-ASCII */
  private static final IGenerator <String> VALUES = Generators.oneOf (Generators.strings ("a=;\" \\\t,()<>@:/[]?{}=", 0, 8),
                                                                      TOKENS,
                                                                      Generators.unicodeStrings (0, 8));

  private static final IGenerator <MimeType> MIME_TYPES = Generators.of (EMimeContentType.values ())
                                                                    .flatMap (eContentType -> TOKENS.flatMap (sSubType -> Generators.lists (Generators.lists (TOKENS,
                                                                                                                                                              1,
                                                                                                                                                              1)
                                                                                                                                            .flatMap (aName -> VALUES.map (sValue -> new MimeTypeParameter (aName.get (0),
                                                                                                                                                                                                            sValue))),
                                                                                                                                  0,
                                                                                                                                  3)
                                                                                                                         .map (aParams -> new MimeType (eContentType,
                                                                                                                                                        sSubType,
                                                                                                                                                        aParams))));

  @Test
  public void testParseAsString ()
  {
    new PropertyChecker ().setTries (1000)
                          .forAll (MIME_TYPES,
                                   Generators.of (EMimeQuoting.values ()),
                                   (aMimeType, eQuoting) -> assertEquals (aMimeType,
                                                                          MimeTypeParser.parseMimeType (aMimeType.getAsString (eQuoting),
                                                                                                        eQuoting)));
  }

  @Test
  public void testParametersSurvive ()
  {
    // A single parameter, compared field by field for a readable failure message
    new PropertyChecker ().setTries (1000).forAll (TOKENS, VALUES, Generators.of (EMimeQuoting.values ()), (sName, sValue, eQuoting) -> {
      final MimeType aMimeType = new MimeType (EMimeContentType.TEXT, "plain", List.of (new MimeTypeParameter (sName, sValue)));
      final String sMimeType = aMimeType.getAsString (eQuoting);
      final MimeType aParsed = MimeTypeParser.parseMimeType (sMimeType, eQuoting);
      assertEquals (sMimeType, 1, aParsed.getParameterCount ());
      assertEquals (sMimeType, sName, aParsed.getParameterAtIndex (0).getAttribute ());
      assertEquals (sMimeType, sValue, aParsed.getParameterAtIndex (0).getValue ());
    });
  }
}
