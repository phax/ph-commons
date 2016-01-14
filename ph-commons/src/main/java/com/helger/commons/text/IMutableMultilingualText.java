/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.text;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.callback.CallbackList;
import com.helger.commons.callback.IChangeCallback;
import com.helger.commons.state.EChange;
import com.helger.commons.state.IClearable;

/**
 * Interface for a writable version of a multilingual text
 *
 * @author Philip Helger
 */
public interface IMutableMultilingualText extends IMultilingualText, IClearable
{
  /**
   * Add a text in the specified locale. If a text with the same locale is
   * already present, <code>false</code> is returned.
   *
   * @param aContentLocale
   *        The locale in which the text should be set. May not be
   *        <code>null</code>.
   * @param sText
   *        The text to be set. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if the text was added,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  EChange addText (@Nonnull Locale aContentLocale, @Nullable String sText);

  /**
   * Set a text in the specified locale. If a text with the same locale is
   * already present, the old value is overwritten.
   *
   * @param aContentLocale
   *        The locale in which the text should be set. May not be
   *        <code>null</code> .
   * @param sText
   *        The text to be set. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if the text was set,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  EChange setText (@Nonnull Locale aContentLocale, @Nullable String sText);

  /**
   * Remove the text with the specified locale.
   *
   * @param aContentLocale
   *        The locale to be removed. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the text was remove,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  EChange removeText (@Nonnull Locale aContentLocale);

  /**
   * Assign all fields from the passed object. All existing texts are removed!
   *
   * @param aMLT
   *        The object to read the content from. May not be <code>null</code>.
   * @return {@link EChange#CHANGED} if the assignment changed anything,
   *         {@link EChange#UNCHANGED} otherwise.
   */
  @Nonnull
  EChange assignFrom (@Nonnull IMultilingualText aMLT);

  /**
   * @return The change notify callbacks. Never <code>null</code>.
   */
  @Nonnull
  CallbackList <IChangeCallback <IMutableMultilingualText>> getChangeNotifyCallbacks ();
}
