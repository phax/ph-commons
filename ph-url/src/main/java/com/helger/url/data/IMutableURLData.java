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
package com.helger.url.data;

import java.nio.charset.Charset;

import com.helger.annotation.style.ReturnsImmutableObject;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.collection.commons.ICommonsList;
import com.helger.url.param.URLParameter;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Mutable version of the {@link IURLData} interface.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The real implementation type
 * @since 12.0.0 RC2
 */
public interface IMutableURLData <IMPLTYPE extends IMutableURLData <IMPLTYPE>> extends IURLData
{
  @Nonnull
  IMPLTYPE setPath (@Nonnull String sPath);

  /**
   * @return The mutable list of all query string parameters. May not be <code>null</code>.
   */
  @Nonnull
  @ReturnsImmutableObject
  ICommonsList <URLParameter> params ();

  @Nonnull
  @ReturnsMutableObject
  default ICommonsList <URLParameter> getAllParams ()
  {
    return params ().getClone ();
  }

  @Nonnull
  IMPLTYPE setParams (@Nullable ICommonsList <URLParameter> aParams);

  @Nonnull
  IMPLTYPE setAnchor (@Nullable String sAnchor);

  @Nonnull
  IMPLTYPE setCharset (@Nullable Charset aCharset);
}
