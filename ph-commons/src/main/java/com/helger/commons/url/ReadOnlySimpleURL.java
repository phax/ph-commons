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
package com.helger.commons.url;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Abstraction of the string parts of a URL but much simpler (and faster) than
 * {@link java.net.URL}.
 *
 * @author Philip Helger
 */
public final class ReadOnlySimpleURL extends AbstractSimpleURL
{
  /** Empty URL */
  public static final ReadOnlySimpleURL EMPTY_URL = new ReadOnlySimpleURL (URLData.EMPTY_URL_DATA);

  public ReadOnlySimpleURL (@Nonnull final String sHref)
  {
    super (sHref);
  }

  public ReadOnlySimpleURL (@Nonnull final String sHref, @Nullable final Map <String, String> aParams)
  {
    super (sHref, aParams);
  }

  public ReadOnlySimpleURL (@Nonnull final String sHref,
                            @Nullable final Map <String, String> aParams,
                            @Nullable final String sAnchor)
  {
    super (sHref, aParams, sAnchor);
  }

  public ReadOnlySimpleURL (@Nonnull final IURLData aURL)
  {
    super (aURL);
  }
}
