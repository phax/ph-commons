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
package com.helger.commons.xml;

import java.util.regex.Pattern;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.regex.RegExCache;

/**
 * XML regular expressions.<br>
 * Source:
 * http://cpansearch.perl.org/src/TJMATHER/XML-RegExp-0.03/lib/XML/RegExp.pm
 *
 * @author Philip Helger
 */
@Immutable
public final class CXMLRegEx
{
  private static final String BASECHAR = "(?:[a-zA-Z]|\u00C3[\u0080-\u0096\u0098-\u00B6\u00B8-\u00BF]|\u00C4[\u0080-\u00B1\u00B4-\u00BE]|\u00C5[\u0081-\u0088\u008A-\u00BE]|\u00C6[\u0080-\u00BF]|\u00C7[\u0080-\u0083\u008D-\u00B0\u00B4\u00B5\u00BA-\u00BF]|\u00C8[\u0080-\u0097]|\u00C9[\u0090-\u00BF]|\u00CA[\u0080-\u00A8\u00BB-\u00BF]|\u00CB[\u0080\u0081]|\u00CE[\u0086\u0088-\u008A\u008C\u008E-\u00A1\u00A3-\u00BF]|\u00CF[\u0080-\u008E\u0090-\u0096\u009A\u009C\u009E\u00A0\u00A2-\u00B3]|\u00D0[\u0081-\u008C\u008E-\u00BF]|\u00D1[\u0080-\u008F\u0091-\u009C\u009E-\u00BF]|\u00D2[\u0080\u0081\u0090-\u00BF]|\u00D3[\u0080-\u0084\u0087\u0088\u008B\u008C\u0090-\u00AB\u00AE-\u00B5\u00B8\u00B9]|\u00D4[\u00B1-\u00BF]|\u00D5[\u0080-\u0096\u0099\u00A1-\u00BF]|\u00D6[\u0080-\u0086]|\u00D7[\u0090-\u00AA\u00B0-\u00B2]|\u00D8[\u00A1-\u00BA]|\u00D9[\u0081-\u008A\u00B1-\u00BF]|\u00DA[\u0080-\u00B7\u00BA-\u00BE]|\u00DB[\u0080-\u008E\u0090-\u0093\u0095\u00A5\u00A6]|\u00E0(?:\u00A4[\u0085-\u00B9\u00BD]|\u00A5[\u0098-\u00A1]|\u00A6[\u0085-\u008C\u008F\u0090\u0093-\u00A8\u00AA-\u00B0\u00B2\u00B6-\u00B9]|\u00A7[\u009C\u009D\u009F-\u00A1\u00B0\u00B1]|\u00A8[\u0085-\u008A\u008F\u0090\u0093-\u00A8\u00AA-\u00B0\u00B2\u00B3\u00B5\u00B6\u00B8\u00B9]|\u00A9[\u0099-\u009C\u009E\u00B2-\u00B4]|\u00AA[\u0085-\u008B\u008D\u008F-\u0091\u0093-\u00A8\u00AA-\u00B0\u00B2\u00B3\u00B5-\u00B9\u00BD]|\u00AB\u00A0|\u00AC[\u0085-\u008C\u008F\u0090\u0093-\u00A8\u00AA-\u00B0\u00B2\u00B3\u00B6-\u00B9\u00BD]|\u00AD[\u009C\u009D\u009F-\u00A1]|\u00AE[\u0085-\u008A\u008E-\u0090\u0092-\u0095\u0099\u009A\u009C\u009E\u009F\u00A3\u00A4\u00A8-\u00AA\u00AE-\u00B5\u00B7-\u00B9]|\u00B0[\u0085-\u008C\u008E-\u0090\u0092-\u00A8\u00AA-\u00B3\u00B5-\u00B9]|\u00B1[\u00A0\u00A1]|\u00B2[\u0085-\u008C\u008E-\u0090\u0092-\u00A8\u00AA-\u00B3\u00B5-\u00B9]|\u00B3[\u009E\u00A0\u00A1]|\u00B4[\u0085-\u008C\u008E-\u0090\u0092-\u00A8\u00AA-\u00B9]|\u00B5[\u00A0\u00A1]|\u00B8[\u0081-\u00AE\u00B0\u00B2\u00B3]|\u00B9[\u0080-\u0085]|\u00BA[\u0081\u0082\u0084\u0087\u0088\u008A\u008D\u0094-\u0097\u0099-\u009F\u00A1-\u00A3\u00A5\u00A7\u00AA\u00AB\u00AD\u00AE\u00B0\u00B2\u00B3\u00BD]|\u00BB[\u0080-\u0084]|\u00BD[\u0080-\u0087\u0089-\u00A9])|\u00E1(?:\u0082[\u00A0-\u00BF]|\u0083[\u0080-\u0085\u0090-\u00B6]|\u0084[\u0080\u0082\u0083\u0085-\u0087\u0089\u008B\u008C\u008E-\u0092\u00BC\u00BE]|\u0085[\u0080\u008C\u008E\u0090\u0094\u0095\u0099\u009F-\u00A1\u00A3\u00A5\u00A7\u00A9\u00AD\u00AE\u00B2\u00B3\u00B5]|\u0086[\u009E\u00A8\u00AB\u00AE\u00AF\u00B7\u00B8\u00BA\u00BC-\u00BF]|\u0087[\u0080-\u0082\u00AB\u00B0\u00B9]|[\u00B8\u00B9][\u0080-\u00BF]|\u00BA[\u0080-\u009B\u00A0-\u00BF]|\u00BB[\u0080-\u00B9]|\u00BC[\u0080-\u0095\u0098-\u009D\u00A0-\u00BF]|\u00BD[\u0080-\u0085\u0088-\u008D\u0090-\u0097\u0099\u009B\u009D\u009F-\u00BD]|\u00BE[\u0080-\u00B4\u00B6-\u00BC\u00BE]|\u00BF[\u0082-\u0084\u0086-\u008C\u0090-\u0093\u0096-\u009B\u00A0-\u00AC\u00B2-\u00B4\u00B6-\u00BC])|\u00E2(?:\u0084[\u00A6\u00AA\u00AB\u00AE]|\u0086[\u0080-\u0082])|\u00E3(?:\u0081[\u0081-\u00BF]|\u0082[\u0080-\u0094\u00A1-\u00BF]|\u0083[\u0080-\u00BA]|\u0084[\u0085-\u00AC])|\u00EA(?:[\u00B0-\u00BF][\u0080-\u00BF])|\u00EB(?:[\u0080-\u00BF][\u0080-\u00BF])|\u00EC(?:[\u0080-\u00BF][\u0080-\u00BF])|\u00ED(?:[\u0080-\u009D][\u0080-\u00BF]|\u009E[\u0080-\u00A3]))";
  private static final String IDEOGRAPHIC = "(?:\u00E3\u0080[\u0087\u00A1-\u00A9]|\u00E4(?:[\u00B8-\u00BF][\u0080-\u00BF])|\u00E5(?:[\u0080-\u00BF][\u0080-\u00BF])|\u00E6(?:[\u0080-\u00BF][\u0080-\u00BF])|\u00E7(?:[\u0080-\u00BF][\u0080-\u00BF])|\u00E8(?:[\u0080-\u00BF][\u0080-\u00BF])|\u00E9(?:[\u0080-\u00BD][\u0080-\u00BF]|\u00BE[\u0080-\u00A5]))";
  private static final String DIGIT = "(?:[0-9]|\u00D9[\u00A0-\u00A9]|\u00DB[\u00B0-\u00B9]|\u00E0(?:\u00A5[\u00A6-\u00AF]|\u00A7[\u00A6-\u00AF]|\u00A9[\u00A6-\u00AF]|\u00AB[\u00A6-\u00AF]|\u00AD[\u00A6-\u00AF]|\u00AF[\u00A7-\u00AF]|\u00B1[\u00A6-\u00AF]|\u00B3[\u00A6-\u00AF]|\u00B5[\u00A6-\u00AF]|\u00B9[\u0090-\u0099]|\u00BB[\u0090-\u0099]|\u00BC[\u00A0-\u00A9]))";
  private static final String EXTENDER = "(?:\u00C2\u00B7|\u00CB[\u0090\u0091]|\u00CE\u0087|\u00D9\u0080|\u00E0(?:\u00B9\u0086|\u00BB\u0086)|\u00E3(?:\u0080[\u0085\u00B1-\u00B5]|\u0082[\u009D\u009E]|\u0083[\u00BC-\u00BE]))";
  private static final String COMBININGCHAR = "(?:\u00CC[\u0080-\u00BF]|\u00CD[\u0080-\u0085\u00A0\u00A1]|\u00D2[\u0083-\u0086]|\u00D6[\u0091-\u00A1\u00A3-\u00B9\u00BB-\u00BD\u00BF]|\u00D7[\u0081\u0082\u0084]|\u00D9[\u008B-\u0092\u00B0]|\u00DB[\u0096-\u00A4\u00A7\u00A8\u00AA-\u00AD]|\u00E0(?:\u00A4[\u0081-\u0083\u00BC\u00BE\u00BF]|\u00A5[\u0080-\u008D\u0091-\u0094\u00A2\u00A3]|\u00A6[\u0081-\u0083\u00BC\u00BE\u00BF]|\u00A7[\u0080-\u0084\u0087\u0088\u008B-\u008D\u0097\u00A2\u00A3]|\u00A8[\u0082\u00BC\u00BE\u00BF]|\u00A9[\u0080-\u0082\u0087\u0088\u008B-\u008D\u00B0\u00B1]|\u00AA[\u0081-\u0083\u00BC\u00BE\u00BF]|\u00AB[\u0080-\u0085\u0087-\u0089\u008B-\u008D]|\u00AC[\u0081-\u0083\u00BC\u00BE\u00BF]|\u00AD[\u0080-\u0083\u0087\u0088\u008B-\u008D\u0096\u0097]|\u00AE[\u0082\u0083\u00BE\u00BF]|\u00AF[\u0080-\u0082\u0086-\u0088\u008A-\u008D\u0097]|\u00B0[\u0081-\u0083\u00BE\u00BF]|\u00B1[\u0080-\u0084\u0086-\u0088\u008A-\u008D\u0095\u0096]|\u00B2[\u0082\u0083\u00BE\u00BF]|\u00B3[\u0080-\u0084\u0086-\u0088\u008A-\u008D\u0095\u0096]|\u00B4[\u0082\u0083\u00BE\u00BF]|\u00B5[\u0080-\u0083\u0086-\u0088\u008A-\u008D\u0097]|\u00B8[\u00B1\u00B4-\u00BA]|\u00B9[\u0087-\u008E]|\u00BA[\u00B1\u00B4-\u00B9\u00BB\u00BC]|\u00BB[\u0088-\u008D]|\u00BC[\u0098\u0099\u00B5\u00B7\u00B9\u00BE\u00BF]|\u00BD[\u00B1-\u00BF]|\u00BE[\u0080-\u0084\u0086-\u008B\u0090-\u0095\u0097\u0099-\u00AD\u00B1-\u00B7\u00B9])|\u00E2\u0083[\u0090-\u009C\u00A1]|\u00E3(?:\u0080[\u00AA-\u00AF]|\u0082[\u0099\u009A]))";
  private static final String LETTER = "(?:" + BASECHAR + "|" + IDEOGRAPHIC + ")";
  private static final String NAMECHAR = "(?:[-._:]|" +
                                         LETTER +
                                         "|" +
                                         DIGIT +
                                         "|" +
                                         COMBININGCHAR +
                                         "|" +
                                         EXTENDER +
                                         ")";

  private static final String NAME = "(?:(?:[:_]|" + LETTER + ")" + NAMECHAR + "*)";
  private static final String NMTOKEN = "(?:" + NAMECHAR + "+)";
  private static final String ENTITYREF = "(?:\\&" + NAME + ";)";
  private static final String CHARREF = "(?:\\&#(?:[0-9]+|x[0-9a-fA-F]+);)";
  private static final String REFERENCE = "(?:" + ENTITYREF + "|" + CHARREF + ")";

  // ?? what if it contains entity references?
  private static final String ATTVALUE = "(?:\"(?:[^\\&<]*|" + REFERENCE + ")\"|'(?:[^'&<]|" + REFERENCE + ")*')";

  // Same as NameChar without the ":"
  private static final String NCNAMECHAR = "(?:[-._]|" +
                                           LETTER +
                                           "|" +
                                           DIGIT +
                                           "|" +
                                           COMBININGCHAR +
                                           "|" +
                                           EXTENDER +
                                           ")";

  // Same as Name without the colons
  private static final String NCNAME = "(?:(?:_|" + LETTER + ")" + NCNAMECHAR + "*)";
  private static final String PREFIX = NCNAME;
  private static final String LOCALPATZ = NCNAME;
  private static final String QNAME = "(?:(?:" + PREFIX + ":)?" + LOCALPATZ + ")";

  // doesn't work!!!
  /** Pattern for attribute values */
  public static final Pattern PATTERN_ATTVALUE = RegExCache.getPattern (ATTVALUE);

  /** Pattern for names */
  public static final Pattern PATTERN_NAME = RegExCache.getPattern (NAME);
  /** Pattern for names that is much quicker but is not 100% correct */
  public static final Pattern PATTERN_NAME_QUICK = RegExCache.getPattern ("[a-zA-Z_][a-zA-Z0-9]*");
  /** Pattern for NC names */
  public static final Pattern PATTERN_NCNAME = RegExCache.getPattern (NCNAME);
  /** Pattern for NM tokens */
  public static final Pattern PATTERN_NMTOKEN = RegExCache.getPattern (NMTOKEN);
  /** Pattern for qualified names */
  public static final Pattern PATTERN_QNAME = RegExCache.getPattern (QNAME);

  @PresentForCodeCoverage
  private static final CXMLRegEx s_aInstance = new CXMLRegEx ();

  private CXMLRegEx ()
  {}
}
