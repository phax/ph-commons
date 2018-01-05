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
package com.helger.json.parser.handler;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.Nonempty;
import com.helger.json.CJson;

/**
 * This {@link IJsonParserHandler} builds the JSON string as a 1:1 copy of the
 * original. It can be used to read and validate the JSON is one step. Use
 * {@link #getJsonString()} after reading to retrieve the whole content.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class StringAssemblyJsonParserHandler implements IJsonParserHandler
{
  private final StringBuilder m_aSB = new StringBuilder ();

  public void onWhitespace (@Nonnull @Nonempty final String sWhitespace)
  {
    m_aSB.append (sWhitespace);
  }

  public void onComment (@Nonnull final String sComment)
  {
    m_aSB.append (CJson.COMMENT_START).append (sComment).append (CJson.COMMENT_END);
  }

  public void onString (@Nonnull final String sString, @Nonnull final String sUnescaped)
  {
    m_aSB.append (sString);
  }

  public void onNumber (@Nonnull final String sNumber, @Nonnull final Number aNumber)
  {
    m_aSB.append (sNumber);
  }

  public void onFalse ()
  {
    m_aSB.append (CJson.KEYWORD_FALSE);
  }

  public void onTrue ()
  {
    m_aSB.append (CJson.KEYWORD_TRUE);
  }

  public void onNull ()
  {
    m_aSB.append (CJson.KEYWORD_NULL);
  }

  public void onArrayStart ()
  {
    m_aSB.append (CJson.ARRAY_START);
  }

  public void onArrayNextElement ()
  {
    m_aSB.append (CJson.ITEM_SEPARATOR);
  }

  public void onArrayEnd ()
  {
    m_aSB.append (CJson.ARRAY_END);
  }

  public void onObjectStart ()
  {
    m_aSB.append (CJson.OBJECT_START);
  }

  public void onObjectName (@Nonnull final String sString, @Nonnull final String sName)
  {
    m_aSB.append (sString);
  }

  public void onObjectColon ()
  {
    m_aSB.append (CJson.NAME_VALUE_SEPARATOR);
  }

  public void onObjectNextElement ()
  {
    m_aSB.append (CJson.ITEM_SEPARATOR);
  }

  public void onObjectEnd ()
  {
    m_aSB.append (CJson.OBJECT_END);
  }

  @Nonnull
  public String getJsonString ()
  {
    return m_aSB.toString ();
  }
}
