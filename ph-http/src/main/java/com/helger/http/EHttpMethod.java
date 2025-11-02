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
package com.helger.http;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.lang.EnumHelper;
import com.helger.base.name.IHasName;

/**
 * HTTP 1.1 methods.<br>
 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html
 *
 * @author Philip Helger
 */
public enum EHttpMethod implements IHasName
{
  OPTIONS ("OPTIONS"),
  GET ("GET"),
  HEAD ("HEAD"),
  POST ("POST"),
  PUT ("PUT"),
  DELETE ("DELETE"),
  TRACE ("TRACE"),
  CONNECT ("CONNECT"),
  /* Extension as of RFC 5789 - partial PUT */
  PATCH ("PATCH");

  private final String m_sName;

  EHttpMethod (@NonNull @Nonempty final String sName)
  {
    m_sName = sName;
  }

  @NonNull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  public boolean isIdempodent ()
  {
    return this == GET || this == HEAD || this == PUT || this == DELETE || this == OPTIONS || this == TRACE;
  }

  public boolean isContentAllowed ()
  {
    return this != HEAD;
  }

  public boolean isPayloadInBody ()
  {
    return this == POST || this == PUT || this == PATCH;
  }

  @Nullable
  public static EHttpMethod getFromNameOrNull (@Nullable final String sName)
  {
    return EnumHelper.getFromNameOrNull (EHttpMethod.class, sName);
  }

  @Nullable
  public static EHttpMethod getFromNameOrDefault (@Nullable final String sName, @Nullable final EHttpMethod eDefault)
  {
    return EnumHelper.getFromNameOrDefault (EHttpMethod.class, sName, eDefault);
  }
}
