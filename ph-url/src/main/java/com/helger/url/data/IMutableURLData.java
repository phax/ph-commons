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
package com.helger.url.data;

import java.nio.charset.Charset;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.style.ReturnsImmutableObject;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.collection.commons.ICommonsList;
import com.helger.url.param.URLParameter;

/**
 * Mutable version of the {@link IURLData} interface.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        The real implementation type
 * @since 12.0.0
 */
public interface IMutableURLData <IMPLTYPE extends IMutableURLData <IMPLTYPE>> extends IURLData
{
  @NonNull
  IMPLTYPE setPath (@NonNull String sPath);

  /**
   * @return The mutable list of all query string parameters. May not be <code>null</code>.
   */
  @NonNull
  @ReturnsImmutableObject
  ICommonsList <URLParameter> params ();

  @NonNull
  @ReturnsMutableObject
  default ICommonsList <URLParameter> getAllParams ()
  {
    return params ().getClone ();
  }

  @NonNull
  IMPLTYPE setParams (@Nullable ICommonsList <URLParameter> aParams);

  @NonNull
  IMPLTYPE setAnchor (@Nullable String sAnchor);

  @NonNull
  IMPLTYPE setCharset (@Nullable Charset aCharset);
}
