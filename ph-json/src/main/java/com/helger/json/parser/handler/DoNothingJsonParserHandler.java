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
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.Nonempty;

/**
 * This implementation of {@link IJsonParserHandler} does nothing. It can e.g.
 * be used to verify the validity of a JSON string without building the full
 * tree in memory.
 *
 * @author Philip Helger
 */
@Immutable
public class DoNothingJsonParserHandler implements IJsonParserHandler
{
  public void onWhitespace (@Nonnull @Nonempty final String sWhitespace)
  {}

  public void onComment (@Nonnull final String sComment)
  {}

  public void onString (@Nonnull final String sString, @Nonnull final String sUnescaped)
  {}

  public void onNumber (@Nonnull final String sNumber, @Nonnull final Number aNumber)
  {}

  public void onFalse ()
  {}

  public void onTrue ()
  {}

  public void onNull ()
  {}

  public void onArrayStart ()
  {}

  public void onArrayNextElement ()
  {}

  public void onArrayEnd ()
  {}

  public void onObjectStart ()
  {}

  public void onObjectName (@Nonnull final String sString, @Nonnull final String sName)
  {}

  public void onObjectColon ()
  {}

  public void onObjectNextElement ()
  {}

  public void onObjectEnd ()
  {}
}
