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
package com.helger.io.url;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringParser;
import com.helger.base.string.StringReplace;
import com.helger.io.resource.ClassPathResource;
import com.helger.io.stream.StreamHelperExt;

import jakarta.annotation.Nullable;

/**
 * URL cleanser.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class URLCleanser
{
  private static char [] s_aCleanURLOld;
  private static char [] [] s_aCleanURLNew;

  @PresentForCodeCoverage
  private static final URLCleanser INSTANCE = new URLCleanser ();

  private URLCleanser ()
  {}

  private static void _initCleanURL ()
  {
    // This one cannot be in the static initializer of the class, because
    // ClassPathResource internally uses
    // URLUtils.getInputStream and this static initialization code of this class
    // can therefore not use ClasspathResource because it would create a
    // recursive dependency!
    // Ever trickier is the when running multiple threads for reading XML (e.g.
    // in the unit test) this code would wait forever in the static initializer
    // because XMLMapHandler internally also acquires an XML reader....
    final Map <String, String> aCleanURLMap = new LinkedHashMap <> ();
    StreamHelperExt.readStreamLines (ClassPathResource.getInputStream ("codelists/cleanurl-data.dat",
                                                                       URLCleanser.class.getClassLoader ()),
                                     StandardCharsets.UTF_8,
                                     sLine -> {
                                       if (sLine.length () > 0 && sLine.charAt (0) == '"')
                                       {
                                         final String [] aParts = StringHelper.getExplodedArray ('=', sLine, 2);
                                         String sKey = StringHelper.trimStartAndEnd (aParts[0], '"');
                                         if (sKey.startsWith ("&#"))
                                         {
                                           // E.g. "&#12345;"
                                           sKey = StringHelper.trimStartAndEnd (sKey, "&#", ";");
                                           sKey = Character.toString ((char) StringParser.parseInt (sKey, -1));
                                         }
                                         final String sValue = StringHelper.trimStartAndEnd (aParts[1], '"');
                                         aCleanURLMap.put (sKey, sValue);
                                       }
                                     });
    // if (XMLMapHandler.readMap (new ClassPathResource
    // ("codelists/cleanurl-data.xml"), aCleanURLMap).isFailure ())
    // throw new InitializationException ("Failed to init CleanURL data!");

    s_aCleanURLOld = new char [aCleanURLMap.size ()];
    s_aCleanURLNew = new char [aCleanURLMap.size ()] [];

    // Convert to char array
    int i = 0;
    for (final Map.Entry <String, String> aEntry : aCleanURLMap.entrySet ())
    {
      final String sKey = aEntry.getKey ();
      if (sKey.length () != 1)
        throw new IllegalStateException ("Clean URL source character has an invalid length: " + sKey.length ());
      s_aCleanURLOld[i] = sKey.charAt (0);
      s_aCleanURLNew[i] = aEntry.getValue ().toCharArray ();
      ++i;
    }
  }

  /**
   * Clean an URL part from nasty Umlauts. This mapping needs extension!
   *
   * @param sURLPart
   *        The original URL part. May be <code>null</code>.
   * @return The cleaned version or <code>null</code> if the input was <code>null</code>.
   */
  @Nullable
  public static String getCleanURLPartWithoutUmlauts (@Nullable final String sURLPart)
  {
    if (s_aCleanURLOld == null)
      _initCleanURL ();
    final char [] ret = StringReplace.replaceMultiple (sURLPart, s_aCleanURLOld, s_aCleanURLNew);
    return new String (ret);
  }
}
