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
package com.helger.commons.changelog;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * This class contains constants for the use of changelogs.
 *
 * @author Philip Helger
 */
@Immutable
public final class CChangeLog
{
  /** The namespace used in XML */
  public static final String CHANGELOG_NAMESPACE_10 = "http://www.helger.com/xsd/changelog/1.0";
  /** The classpath relative path to the changelog XSD file */
  public static final String CHANGELOG_XSD_10 = "schemas/changelog-1.0.xsd";
  /** The schemaLocation value of the changeLog 1.0 XSD */
  public static final String CHANGELOG_SCHEMALOCATION_10 = CHANGELOG_NAMESPACE_10 + " " + CHANGELOG_XSD_10;

  /** The default file name of the changelog XML */
  public static final String CHANGELOG_XML_FILENAME = "changelog.xml";

  @PresentForCodeCoverage
  private static final CChangeLog s_aInstance = new CChangeLog ();

  private CChangeLog ()
  {}
}
