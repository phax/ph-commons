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
package com.helger.json.parser.handler;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.Immutable;

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
  public void onWhitespace (@NonNull @Nonempty final String sWhitespace)
  {}

  public void onComment (@NonNull final String sComment)
  {}

  public void onString (@NonNull final String sString, @NonNull final String sUnescaped)
  {}

  public void onNumber (@NonNull final String sNumber, @NonNull final Number aNumber)
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

  public void onObjectName (@NonNull final String sString, @NonNull final String sName)
  {}

  public void onObjectColon ()
  {}

  public void onObjectNextElement ()
  {}

  public void onObjectEnd ()
  {}
}
