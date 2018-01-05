/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * This exception is thrown upon JSON parse error.
 *
 * @author Philip Helger
 */
public class JsonParseException extends Exception
{
  private final IJsonParsePosition m_aTokenStart;
  private final IJsonParsePosition m_aErrorPos;
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
    super ("Json parse error " +
           aErrorPos.getAsString () +
           " for token starting at " +
           aTokenStart.getAsString () +
           ": " +
           sMessage);
    m_aTokenStart = aTokenStart;
    m_aErrorPos = aErrorPos;
    m_sMessage = sMessage;
  }

  /**
   * @return The line number where the token started or -1 if position tracking
   *         is disabled.
   */
  @Nonnegative
  public int getTokenStartLine ()
  {
    return m_aTokenStart == null ? -1 : m_aTokenStart.getLineNumber ();
  }

  /**
   * @return The column number where the token started or -1 if position
   *         tracking is disabled.
   */
  @Nonnegative
  public int getTokenStartColumn ()
  {
    return m_aTokenStart == null ? -1 : m_aTokenStart.getColumnNumber ();
  }

  /**
   * @return The line number where the error occurred or -1 if position tracking
   *         is disabled.
   */
  @Nonnegative
  public int getErrorLine ()
  {
    return m_aErrorPos == null ? -1 : m_aErrorPos.getLineNumber ();
  }

  /**
   * @return The column number where the error occurred or -1 if position
   *         tracking is disabled.
   */
  @Nonnegative
  public int getErrorColumn ()
  {
    return m_aErrorPos == null ? -1 : m_aErrorPos.getColumnNumber ();
  }

  /**
   * @return The main error message without the automatically added prefix.
   */
  @Nonnull
  public String getPureMessage ()
  {
    return m_sMessage;
  }
}
