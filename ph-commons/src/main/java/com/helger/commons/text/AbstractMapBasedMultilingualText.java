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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.callback.CallbackList;
import com.helger.commons.callback.IChangeCallback;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.locale.LocaleHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.state.EContinue;

/**
 * A {@link Map} based implementation of {@link IMultilingualText} that does
 * also provide writing methods to the outside and is only to be used as a
 * non-abstract base class.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public abstract class AbstractMapBasedMultilingualText extends AbstractReadOnlyMapBasedMultilingualText
                                                       implements IMutableMultilingualText
{
  /** A list of callback upon change. */
  private final CallbackList <IChangeCallback <IMutableMultilingualText>> m_aChangeNotifyCallbacks = new CallbackList <IChangeCallback <IMutableMultilingualText>> ();

  public AbstractMapBasedMultilingualText ()
  {}

  /**
   * Protected constructor that specifies the underlying {@link Map} to use. Use
   * this constructor to e.g. provide a concurrent {@link HashMap} or similar.
   *
   * @param aMapToUse
   *        The map to use. Must not be <code>null</code> and must be writable.
   */
  protected AbstractMapBasedMultilingualText (@Nonnull final Map <Locale, String> aMapToUse)
  {
    super (aMapToUse);
  }

  @Nonnull
  private EContinue _beforeChange ()
  {
    for (final IChangeCallback <IMutableMultilingualText> aCallback : m_aChangeNotifyCallbacks.getAllCallbacks ())
      if (aCallback.beforeChange (this).isBreak ())
        return EContinue.BREAK;
    return EContinue.CONTINUE;
  }

  private void _afterChange ()
  {
    for (final IChangeCallback <IMutableMultilingualText> aCallback : m_aChangeNotifyCallbacks.getAllCallbacks ())
      aCallback.afterChange (this);
  }

  @Nonnull
  public final EChange addText (@Nonnull final Locale aContentLocale, @Nullable final String sText)
  {
    ValueEnforcer.notNull (aContentLocale, "ContentLocale");

    if (containsLocale (aContentLocale))
      return EChange.UNCHANGED;

    if (_beforeChange ().isBreak ())
      return EChange.UNCHANGED;
    internalAddText (aContentLocale, sText);
    _afterChange ();
    return EChange.CHANGED;
  }

  @Nonnull
  public final EChange setText (@Nonnull final Locale aContentLocale, @Nullable final String sText)
  {
    ValueEnforcer.notNull (aContentLocale, "ContentLocale");

    if (containsLocale (aContentLocale))
    {
      // Text for this locale already contained
      final String sOldText = internalGetText (aContentLocale);

      // Did anything change?
      if (EqualsHelper.equals (sOldText, sText))
        return EChange.UNCHANGED;

      if (_beforeChange ().isBreak ())
        return EChange.UNCHANGED;
      internalSetText (aContentLocale, sText);
      _afterChange ();
      return EChange.CHANGED;
    }

    // New text
    if (_beforeChange ().isBreak ())
      return EChange.UNCHANGED;
    internalAddText (aContentLocale, sText);
    _afterChange ();
    return EChange.CHANGED;
  }

  @Nonnull
  public final EChange removeText (@Nonnull final Locale aContentLocale)
  {
    ValueEnforcer.notNull (aContentLocale, "ContentLocale");

    // Always use locale fallbacks
    for (final Locale aCurrentLocale : LocaleHelper.getCalculatedLocaleListForResolving (aContentLocale))
      if (super.containsLocale (aCurrentLocale))
      {
        if (_beforeChange ().isBreak ())
          return EChange.UNCHANGED;
        internalRemoveText (aCurrentLocale);
        _afterChange ();
        return EChange.CHANGED;
      }
    return EChange.UNCHANGED;
  }

  @Nonnull
  public final EChange clear ()
  {
    if (isEmpty () || _beforeChange ().isBreak ())
      return EChange.UNCHANGED;

    internalClear ();
    _afterChange ();
    return EChange.CHANGED;
  }

  @Nonnull
  public final EChange assignFrom (@Nonnull final IMultilingualText aMLT)
  {
    ValueEnforcer.notNull (aMLT, "MLT");

    if (getAllTexts ().equals (aMLT.getAllTexts ()) || _beforeChange ().isBreak ())
      return EChange.UNCHANGED;

    // Remove all existing texts and assign the new ones
    internalClear ();
    for (final Map.Entry <Locale, String> aEntry : aMLT.getAllTexts ().entrySet ())
      internalAddText (aEntry);
    _afterChange ();
    return EChange.CHANGED;
  }

  @Nonnull
  @ReturnsMutableObject ("design")
  public final CallbackList <IChangeCallback <IMutableMultilingualText>> getChangeNotifyCallbacks ()
  {
    return m_aChangeNotifyCallbacks;
  }
}
