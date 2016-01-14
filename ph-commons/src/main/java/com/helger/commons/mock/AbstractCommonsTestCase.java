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
package com.helger.commons.mock;

import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.CodingStyleguideUnaware;

/**
 * Base class for all JUnit tests requiring setup and teardown handling.<br>
 * Note: Annotating a public static void no-argument method with \@BeforeClass
 * causes it to be run once before any of the test methods in the class. The
 * \@BeforeClass methods of super classes will be run before those the current
 * class.
 *
 * @author Philip Helger
 */
@Immutable
public abstract class AbstractCommonsTestCase
{
  // For test case classes it is ok to have a protected logger
  @CodingStyleguideUnaware
  protected final Logger m_aLogger = LoggerFactory.getLogger (getClass ());

  /** -1 */
  public static final Integer I_1 = Integer.valueOf (-1);
  /** 0 */
  public static final Integer I0 = Integer.valueOf (0);
  /** 1 */
  public static final Integer I1 = Integer.valueOf (1);
  /** 2 */
  public static final Integer I2 = Integer.valueOf (2);
  /** 3 */
  public static final Integer I3 = Integer.valueOf (3);
  /** 4 */
  public static final Integer I4 = Integer.valueOf (4);
  /** 5 */
  public static final Integer I5 = Integer.valueOf (5);
  /** 6 */
  public static final Integer I6 = Integer.valueOf (6);
  /** 7 */
  public static final Integer I7 = Integer.valueOf (7);

  /** -1 */
  public static final Long L_1 = Long.valueOf (-1);
  /** 0 */
  public static final Long L0 = Long.valueOf (0);
  /** 1 */
  public static final Long L1 = Long.valueOf (1);

  /** de */
  public static final Locale L_DE = new Locale ("de");
  /** de-AT */
  public static final Locale L_DE_AT = new Locale ("de", "AT");
  /** de-DE */
  public static final Locale L_DE_DE = new Locale ("de", "DE");
  /** en */
  public static final Locale L_EN = new Locale ("en");
  /** en-GB */
  public static final Locale L_EN_GB = new Locale ("en", "GB");
  /** en-US */
  public static final Locale L_EN_US = new Locale ("en", "US");
  /** fr */
  public static final Locale L_FR = new Locale ("fr");
  /** fr-FR */
  public static final Locale L_FR_FR = new Locale ("fr", "FR");
}
