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

import com.helger.commons.annotation.Nonempty;

/**
 * This interface is invoked during JSON parsing to perform different things
 * with the read JSON. See implementations of this class for potential usage.
 *
 * @author Philip Helger
 */
public interface IJsonParserHandler
{
  /**
   * Called when reading whitespace characters. If a comment is mixed within
   * whitespaces this method is called for every whitespaces part.
   *
   * @param sWhitespace
   *        Whitespaces string. Never <code>null</code> and never empty.
   */
  void onWhitespace (@Nonnull @Nonempty String sWhitespace);

  /**
   * Called for JSON comments
   *
   * @param sComment
   *        The comment without the leading "/*" and the trailing "*" + "/"
   */
  void onComment (@Nonnull String sComment);

  /**
   * Called for JSON strings that are NOT object names.
   *
   * @param sString
   *        The original read string including the quotes.
   * @param sUnescaped
   *        The unescaped string excluding the quotes.
   */
  void onString (@Nonnull String sString, @Nonnull String sUnescaped);

  /**
   * Called for JSON numbers
   *
   * @param sNumber
   *        The read string representation of the number. Never
   *        <code>null</code>.
   * @param aNumber
   *        The parsed number. Never <code>null</code>.
   */
  void onNumber (@Nonnull String sNumber, @Nonnull Number aNumber);

  /**
   * Called for the keyword <code>false</code>
   */
  void onFalse ();

  /**
   * Called for the keyword <code>true</code>
   */
  void onTrue ();

  /**
   * Called for the keyword <code>null</code>
   */
  void onNull ();

  /**
   * Called upon array start ('[')
   */
  void onArrayStart ();

  /**
   * Called after an array element when the next is about to start (','). This
   * is only called after {@link #onArrayStart()} was called.
   */
  void onArrayNextElement ();

  /**
   * Called upon array end (']'). This is only called after
   * {@link #onArrayStart()} was called.
   */
  void onArrayEnd ();

  /**
   * Called upon array start ('{')
   */
  void onObjectStart ();

  /**
   * Called for the name part of an object element. This is only called after
   * {@link #onObjectStart()} was called.
   *
   * @param sString
   *        The original read string including the quotes.
   * @param sName
   *        The unescaped name excluding the quotes.
   */
  void onObjectName (@Nonnull String sString, @Nonnull String sName);

  /**
   * Called between the object element name and the object element value (':').
   * This is only called after {@link #onObjectName(String,String)} was called.
   */
  void onObjectColon ();

  /**
   * Called after an object element when the next is about to start (','). This
   * is only called after {@link #onObjectColon()} was called.
   */
  void onObjectNextElement ();

  /**
   * Called upon object end ('}'). This is only called after
   * {@link #onObjectStart()} was called.
   */
  void onObjectEnd ();
}
