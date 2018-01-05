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
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.NonBlockingStack;
import com.helger.json.IJson;
import com.helger.json.IJsonCollection;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.json.JsonValue;

/**
 * This {@link IJsonParserHandler} constructs the whole JSON tree while parsing
 * it. The resulting JSON object can be retrieved via {@link #getJson()}. This
 * can be seen as the "DOM" JSON implementation.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class CollectingJsonParserHandler implements IJsonParserHandler
{
  private IJson m_aJson;
  private final NonBlockingStack <IJsonCollection> m_aStack = new NonBlockingStack <> ();
  private final NonBlockingStack <String> m_aObjectName = new NonBlockingStack <> ();

  private void _addToStackPeek (@Nonnull final IJson aValue)
  {
    // Debug only
    if (false)
      if (m_aStack.isEmpty ())
        throw new IllegalStateException ("Internal inconsistency - empty parent stack");
    final IJsonCollection aParent = m_aStack.peek ();

    if (aParent.isArray ())
      aParent.getAsArray ().add (aValue);
    else
    {
      // Debug only
      if (false)
        if (m_aObjectName.isEmpty ())
          throw new IllegalStateException ("Internal inconsistency - empty object name stack");

      aParent.getAsObject ().add (m_aObjectName.pop (), aValue);
    }
  }

  private void _addSimple (@Nonnull final IJson aValue)
  {
    if (m_aJson == null)
      m_aJson = aValue;
    else
      _addToStackPeek (aValue);
  }

  private void _addCollection (@Nonnull final IJsonCollection aValue)
  {
    _addSimple (aValue);

    // Start a new stack level
    m_aStack.push (aValue);
  }

  public void onWhitespace (@Nonnull @Nonempty final String sWhitespace)
  {}

  public void onComment (@Nonnull final String sComment)
  {}

  public void onString (@Nonnull final String sString, @Nonnull final String sUnescaped)
  {
    _addSimple (JsonValue.create (sUnescaped));
  }

  public void onNumber (@Nonnull final String sNumber, @Nonnull final Number aNumber)
  {
    _addSimple (JsonValue.create (aNumber));
  }

  public void onFalse ()
  {
    _addSimple (JsonValue.FALSE);
  }

  public void onTrue ()
  {
    _addSimple (JsonValue.TRUE);
  }

  public void onNull ()
  {
    _addSimple (JsonValue.NULL);
  }

  public void onArrayStart ()
  {
    _addCollection (new JsonArray ());
  }

  public void onArrayNextElement ()
  {}

  public void onArrayEnd ()
  {
    m_aStack.pop ();
  }

  public void onObjectStart ()
  {
    _addCollection (new JsonObject ());
  }

  public void onObjectName (@Nonnull final String sString, @Nonnull final String sName)
  {
    m_aObjectName.push (sName);
  }

  public void onObjectColon ()
  {}

  public void onObjectNextElement ()
  {}

  public void onObjectEnd ()
  {
    m_aStack.pop ();
  }

  @Nullable
  public IJson getJson ()
  {
    return m_aJson;
  }
}
