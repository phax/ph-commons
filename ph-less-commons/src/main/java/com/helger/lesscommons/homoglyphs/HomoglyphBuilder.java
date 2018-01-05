/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.lesscommons.homoglyphs;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.MissingResourceException;

import javax.annotation.Nonnull;
import javax.annotation.WillClose;
import javax.annotation.concurrent.Immutable;

import com.helger.collection.map.IntSet;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.stream.NonBlockingBufferedReader;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;

/**
 * Helper class providing methods that supply populated Homoglyph objects.
 *
 * @author Rob Dawson
 * @author Philip Helger
 */
@Immutable
public final class HomoglyphBuilder
{
  public static final String CHAR_CODES_FILE = "homoglyph/char_codes.txt";

  private HomoglyphBuilder ()
  {}

  /**
   * Parses the bundled char_codes.txt file, and uses it to construct a
   * populated Homoglyph object.
   *
   * @return a Homoglyph object populated using the contents of the
   *         char_codes.txt file
   * @throws MissingResourceException
   *         if the char_codes.txt file is missing
   * @throws IOException
   *         if the char_codes.txt exists but cannot be read
   */
  @Nonnull
  public static Homoglyph build () throws IOException
  {
    return build (new ClassPathResource (CHAR_CODES_FILE));
  }

  /**
   * Parses the specified resource and uses it to construct a populated
   * Homoglyph object.
   *
   * @param aRes
   *        the path to a file containing a list of homoglyphs (see the bundled
   *        char_codes.txt file for an example of the required format)
   * @return a Homoglyph object populated using the contents of the specified
   *         file
   * @throws IOException
   *         if the specified file cannot be read
   */
  @Nonnull
  public static Homoglyph build (@Nonnull final IReadableResource aRes) throws IOException
  {
    ValueEnforcer.notNull (aRes, "Resource");
    return build (aRes.getReader (StandardCharsets.ISO_8859_1));
  }

  /**
   * Consumes the supplied Reader and uses it to construct a populated Homoglyph
   * object.
   *
   * @param aReader
   *        a Reader object that provides access to homoglyph data (see the
   *        bundled char_codes.txt file for an example of the required format)
   * @return a Homoglyph object populated using the data returned by the Reader
   *         object
   * @throws IOException
   *         if the specified Reader cannot be read
   */
  @Nonnull
  public static Homoglyph build (@Nonnull @WillClose final Reader aReader) throws IOException
  {
    ValueEnforcer.notNull (aReader, "reader");
    try (final NonBlockingBufferedReader aBR = new NonBlockingBufferedReader (aReader))
    {
      final ICommonsList <IntSet> aList = new CommonsArrayList <> ();
      String sLine;
      while ((sLine = aBR.readLine ()) != null)
      {
        sLine = sLine.trim ();
        if (sLine.startsWith ("#") || sLine.length () == 0)
          continue;

        final IntSet aSet = new IntSet (sLine.length () / 3);
        for (final String sCharCode : StringHelper.getExploded (',', sLine))
        {
          final int nVal = StringParser.parseInt (sCharCode, 16, -1);
          if (nVal >= 0)
            aSet.add (nVal);
        }
        aList.add (aSet);
      }

      return new Homoglyph (aList);
    }
  }
}
