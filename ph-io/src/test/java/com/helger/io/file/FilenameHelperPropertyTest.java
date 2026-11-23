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
package com.helger.io.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.helger.base.string.StringHelper;
import com.helger.base.string.StringReplace;
import com.helger.unittest.property.Generators;
import com.helger.unittest.property.IGenerator;
import com.helger.unittest.property.PropertyChecker;

/**
 * Property based tests for {@link FilenameHelper}: whatever the input, the "secure" methods only
 * return valid file names, and cleaning is idempotent.
 *
 * @author Philip Helger
 */
public final class FilenameHelperPropertyTest
{
  /** Building blocks of hostile file names */
  private static final IGenerator <String> PARTS = Generators.oneOf (Generators.strings ("./\\:*?\"<>|\u0000 \t~-a", 0, 6),
                                                                     Generators.of ("CON",
                                                                                    "con",
                                                                                    "aux.txt",
                                                                                    "NUL",
                                                                                    "COM1",
                                                                                    "lpt9.a",
                                                                                    "..",
                                                                                    ".",
                                                                                    "a.",
                                                                                    "a ",
                                                                                    "~"),
                                                                     Generators.unicodeStrings (0, 4));

  /**
   * Names without path separators - separators are kept on purpose (e.g. "a/b/c" stays "a/b/c"), so
   * only single level names must become valid file names.
   */
  private static final IGenerator <String> NAMES = Generators.lists (PARTS, 0, 4)
                                                             .map (FilenameHelperPropertyTest::_concat)
                                                             .map (sName -> StringReplace.replaceAll (StringReplace.replaceAll (sName, '/', '_'), '\\', '_'));

  private static String _concat (final List <String> aParts)
  {
    final StringBuilder aSB = new StringBuilder ();
    for (final String sPart : aParts)
      aSB.append (sPart);
    return aSB.toString ();
  }

  @Test
  public void testSecureValidFilename ()
  {
    new PropertyChecker ().setTries (2000).forAll (NAMES, sName -> {
      final String sSecure = FilenameHelper.getAsSecureValidFilename (sName);
      if (sSecure != null)
      {
        assertFalse (sName, sSecure.isEmpty ());
        assertTrue ("'" + sName + "' -> '" + sSecure + "'", FilenameHelper.isValidFilename (sSecure));
        assertEquals (sName, sSecure, FilenameHelper.getAsSecureValidFilename (sSecure));
      }
    });
  }

  @Test
  public void testSecureValidASCIIFilename ()
  {
    new PropertyChecker ().setTries (2000).forAll (NAMES, sName -> {
      final String sSecure = FilenameHelper.getAsSecureValidASCIIFilename (sName);
      if (sSecure != null)
      {
        assertFalse (sName, sSecure.isEmpty ());
        assertTrue ("'" + sName + "' -> '" + sSecure + "'", FilenameHelper.isValidFilename (sSecure));
        for (int i = 0; i < sSecure.length (); ++i)
          assertTrue (sSecure, sSecure.charAt (i) < 128);
        assertEquals (sName, sSecure, FilenameHelper.getAsSecureValidASCIIFilename (sSecure));
      }
    });
  }

  @Test
  public void testCleanPath ()
  {
    final IGenerator <String> aPaths = Generators.lists (Generators.of ("a", "b", ".", "..", "", "x.y"), 0, 8)
                                                 .flatMap (aSegments -> Generators.booleans ()
                                                                                  .map (aAbsolute -> (aAbsolute.booleanValue () ? "/"
                                                                                                                                : "") +
                                                                                                     String.join ("/", aSegments)));
    new PropertyChecker ().setTries (2000).forAll (aPaths, sPath -> {
      final String sClean = FilenameHelper.getCleanPath (sPath);
      assertEquals (sPath, sClean, FilenameHelper.getCleanPath (sClean));

      // No "." segments, and ".." segments only at the beginning
      boolean bOnlyDotDotSoFar = true;
      for (final String sSegment : StringHelper.getExplodedArray ('/', sClean))
      {
        assertFalse ("'" + sPath + "' -> '" + sClean + "'", sSegment.equals ("."));
        if (sSegment.equals (".."))
          assertTrue ("'" + sPath + "' -> '" + sClean + "'", bOnlyDotDotSoFar);
        else
          if (!sSegment.isEmpty ())
            bOnlyDotDotSoFar = false;
      }
    });
  }
}
