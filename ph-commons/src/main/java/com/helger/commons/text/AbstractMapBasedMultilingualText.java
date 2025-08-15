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
package com.helger.commons.text;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.helger.annotation.OverridingMethodsMustInvokeSuper;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.callback.CallbackList;
import com.helger.base.callback.IChangeCallback;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.base.state.EContinue;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.commons.locale.LocaleHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A {@link Map} based implementation of {@link IMultilingualText} that does
 * also provide writing methods to the outside and is only to be used as a
 * non-abstract base class.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public abstract class AbstractMapBasedMultilingualText extends AbstractReadOnlyMapBasedMultilingualText implements
                                                       IMutableMultilingualText
{
  /** A list of callback upon change. */
  private final transient CallbackList <IChangeCallback <IMutableMultilingualText>> m_aChangeNotifyCallbacks = new CallbackList <> ();

  protected AbstractMapBasedMultilingualText ()
  {}

  /**
   * Protected constructor that specifies the underlying {@link Map} to use. Use
   * this constructor to e.g. provide a concurrent {@link HashMap} or similar.
   *
   * @param aMapToUse
   *        The map to use. Must not be <code>null</code> and must be writable.
   */
  protected AbstractMapBasedMultilingualText (@Nonnull final ICommonsOrderedMap <Locale, String> aMapToUse)
  {
    super (aMapToUse);
  }

  @Nonnull
  private EContinue _beforeChange ()
  {
    return m_aChangeNotifyCallbacks.forEachBreakable (x -> x.beforeChange (this));
  }

  private void _afterChange ()
  {
    m_aChangeNotifyCallbacks.forEach (x -> x.afterChange (this));
  }

  @Nonnull
  public final EChange addText (@Nonnull final Locale aContentLocale, @Nullable final String sText)
  {
    ValueEnforcer.notNull (aContentLocale, "ContentLocale");

    if (texts ().containsKey (aContentLocale))
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

    if (texts ().containsKey (aContentLocale))
    {
      // Text for this locale already contained
      final String sOldText = internalGetText (aContentLocale);

      // Did anything change?
      if (EqualsHelper.equals (sOldText, sText))
        return EChange.UNCHANGED;

      if (_beforeChange ().isBreak ())
        return EChange.UNCHANGED;
      internalSetText (aContentLocale, sText);
    }
    else
    {
      // New text
      if (_beforeChange ().isBreak ())
        return EChange.UNCHANGED;
      internalAddText (aContentLocale, sText);
    }
    _afterChange ();
    return EChange.CHANGED;
  }

  @Nonnull
  public final EChange removeText (@Nonnull final Locale aContentLocale)
  {
    ValueEnforcer.notNull (aContentLocale, "ContentLocale");

    // Always use locale fallbacks
    for (final Locale aCurrentLocale : LocaleHelper.getCalculatedLocaleListForResolving (aContentLocale))
      if (texts ().containsKey (aCurrentLocale))
      {
        if (_beforeChange ().isBreak ())
          return EChange.UNCHANGED;
        texts ().remove (aCurrentLocale);
        _afterChange ();
        return EChange.CHANGED;
      }
    return EChange.UNCHANGED;
  }

  @Nonnull
  public final EChange removeAll ()
  {
    if (texts ().isEmpty () || _beforeChange ().isBreak ())
      return EChange.UNCHANGED;

    texts ().clear ();
    _afterChange ();
    return EChange.CHANGED;
  }

  @Nonnull
  public final EChange assignFrom (@Nonnull final IMultilingualText aMLT)
  {
    ValueEnforcer.notNull (aMLT, "MLT");

    if (texts ().equals (aMLT.texts ()) || _beforeChange ().isBreak ())
      return EChange.UNCHANGED;

    // Remove all existing texts and assign the new ones
    texts ().clear ();
    for (final Map.Entry <Locale, String> aEntry : aMLT.texts ().entrySet ())
      internalAddText (aEntry);
    _afterChange ();
    return EChange.CHANGED;
  }

  @Nonnull
  @ReturnsMutableObject ("design")
  public final CallbackList <IChangeCallback <IMutableMultilingualText>> changeNotifyCallbacks ()
  {
    return m_aChangeNotifyCallbacks;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    return super.equals (o);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .appendIf ("ChangeNotifyCallbacks", m_aChangeNotifyCallbacks, CallbackList::isNotEmpty)
                            .getToString ();
  }
}
