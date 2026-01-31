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
package com.helger.http;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.lang.EnumHelper;
import com.helger.base.name.IHasName;

/**
 * HTTP versions
 *
 * @author Philip Helger
 */
public enum EHttpVersion implements IHasName
{
  HTTP_10 ("HTTP/1.0"),
  HTTP_11 ("HTTP/1.1"),
  HTTP_20 ("HTTP/2.0");

  private final String m_sName;

  EHttpVersion (@NonNull @Nonempty final String sName)
  {
    m_sName = sName;
  }

  @NonNull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  public boolean is10 ()
  {
    return this == HTTP_10;
  }

  public boolean isAtLeast11 ()
  {
    return ordinal () >= HTTP_11.ordinal ();
  }

  @Nullable
  public static EHttpVersion getFromNameOrNull (@Nullable final String sName)
  {
    return EnumHelper.getFromNameOrNull (EHttpVersion.class, sName);
  }
}
