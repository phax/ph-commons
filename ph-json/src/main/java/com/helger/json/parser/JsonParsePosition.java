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
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.lang.ICloneable;
import com.helger.commons.string.ToStringGenerator;

@NotThreadSafe
public class JsonParsePosition implements ICloneable <JsonParsePosition>, IJsonParsePosition
{
  private int m_nLine;
  private int m_nColumn;

  // status
  private boolean m_bPrevCharIsCR = false;
  private boolean m_bPrevCharIsLF = false;

  public JsonParsePosition ()
  {
    m_nLine = 1;
    m_nColumn = 1;
  }

  public JsonParsePosition (@Nonnull final JsonParsePosition aOther)
  {
    m_nLine = aOther.m_nLine;
    m_nColumn = aOther.m_nColumn;
  }

  protected void updatePosition (final int c, final int nTabSize)
  {
    m_nColumn++;

    if (m_bPrevCharIsLF)
    {
      // Next line
      m_bPrevCharIsLF = false;
      m_nColumn = 1;
      m_nLine++;
    }
    else
      if (m_bPrevCharIsCR)
      {
        m_bPrevCharIsCR = false;
        if (c == '\n')
          m_bPrevCharIsLF = true;
        else
        {
          // Next line
          m_nColumn = 1;
          m_nLine++;
        }
      }

    switch (c)
    {
      case '\r':
        m_bPrevCharIsCR = true;
        break;
      case '\n':
        m_bPrevCharIsLF = true;
        break;
      case '\t':
        // tab character
        m_nColumn--;
        m_nColumn += (nTabSize - (m_nColumn % nTabSize));
        break;
      default:
        break;
    }
  }

  /**
   * @return The current line number. First line has a value of 1.
   */
  @Nonnegative
  public int getLineNumber ()
  {
    return m_nLine;
  }

  /**
   * @return The current column number. First column has a value of 1.
   */
  @Nonnegative
  public int getColumnNumber ()
  {
    return m_nColumn;
  }

  @Nonnull
  public String getAsString ()
  {
    return "[" + m_nLine + ":" + m_nColumn + "]";
  }

  @Nonnull
  public JsonParsePosition getClone ()
  {
    return new JsonParsePosition (this);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("line", m_nLine).append ("column", m_nColumn).getToString ();
  }
}
