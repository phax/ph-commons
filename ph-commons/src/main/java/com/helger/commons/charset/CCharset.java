/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.commons.charset;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;

/**
 * Charset constants.
 *
 * @author Philip Helger
 */
@Immutable
public final class CCharset
{
  /**
   * The default charset object. Currently this is UTF-8.
   */
  @Nonnull
  public static final Charset DEFAULT_CHARSET_OBJ = StandardCharsets.UTF_8;

  /**
   * The charset used by the ServiceLoader to read the service files.
   */
  @Nonnull
  public static final Charset CHARSET_SERVICE_LOADER_OBJ = StandardCharsets.UTF_8;

  @PresentForCodeCoverage
  private static final CCharset s_aInstance = new CCharset ();

  private CCharset ()
  {}
}
