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
package com.helger.json.parser;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.Nonnull;
import com.helger.annotation.Nullable;

/**
 * This exception is thrown upon JSON parse error.
 *
 * @author Philip Helger
 */
public class JsonParseException extends Exception
{
  private final transient IJsonParsePosition m_aTokenStart;
  private final transient IJsonParsePosition m_aErrorPos;
  private final String m_sMessage;

  public JsonParseException (@Nonnull final String sMessage)
  {
    super ("Json parse error: " + sMessage);
    m_aTokenStart = null;
    m_aErrorPos = null;
    m_sMessage = sMessage;
  }

  public JsonParseException (@Nonnull final IJsonParsePosition aTokenStart,
                             @Nonnull final IJsonParsePosition aErrorPos,
                             @Nonnull final String sMessage)
  {
    super ("Json parse error " + aErrorPos.getAsString () + " for token starting at " + aTokenStart.getAsString () + ": " + sMessage);
    m_aTokenStart = aTokenStart;
    m_aErrorPos = aErrorPos;
    m_sMessage = sMessage;
  }

  /**
   * @return Token start position. May be <code>null</code>.
   * @since 9.2.1
   */
  @Nullable
  public final IJsonParsePosition getTokenStartPosition ()
  {
    return m_aTokenStart;
  }

  /**
   * @return The line number where the token started or -1 if position tracking
   *         is disabled.
   */
  @Nonnegative
  public final int getTokenStartLine ()
  {
    return m_aTokenStart == null ? -1 : m_aTokenStart.getLineNumber ();
  }

  /**
   * @return The column number where the token started or -1 if position
   *         tracking is disabled.
   */
  @Nonnegative
  public final int getTokenStartColumn ()
  {
    return m_aTokenStart == null ? -1 : m_aTokenStart.getColumnNumber ();
  }

  /**
   * @return Error position. May be <code>null</code>.
   * @since 9.2.1
   */
  @Nullable
  public final IJsonParsePosition getErrorPosition ()
  {
    return m_aErrorPos;
  }

  /**
   * @return The line number where the error occurred or -1 if position tracking
   *         is disabled.
   */
  @Nonnegative
  public final int getErrorLine ()
  {
    return m_aErrorPos == null ? -1 : m_aErrorPos.getLineNumber ();
  }

  /**
   * @return The column number where the error occurred or -1 if position
   *         tracking is disabled.
   */
  @Nonnegative
  public final int getErrorColumn ()
  {
    return m_aErrorPos == null ? -1 : m_aErrorPos.getColumnNumber ();
  }

  /**
   * @return The main error message without the automatically added prefix.
   */
  @Nonnull
  public final String getPureMessage ()
  {
    return m_sMessage;
  }
}
