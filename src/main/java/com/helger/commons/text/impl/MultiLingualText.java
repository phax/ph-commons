/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.text.impl;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.ReturnsMutableObject;
import com.helger.commons.callback.CallbackList;
import com.helger.commons.callback.IChangeNotify;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.locale.LocaleUtils;
import com.helger.commons.state.EChange;
import com.helger.commons.text.IMultiLingualText;
import com.helger.commons.text.IReadonlyMultiLingualText;
import com.helger.commons.text.ISimpleMultiLingualText;

/**
 * This class represents a multilingual text. It is internally represented as a
 * map from {@link Locale} to the language dependent name.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MultiLingualText extends TextProvider implements IMultiLingualText
{
  // Because of the transient field
  private static final long serialVersionUID = 136888667633487L;

  /** Default empty multilingual text - don't modify this object!!! */
  public static final IMultiLingualText EMPTY_MULTILINGUAL_TEXT = new MultiLingualText ();

  /** A list of callback upon change. */
  private final CallbackList <IChangeNotify <IMultiLingualText>> m_aChangeNotifyCallbacks = new CallbackList <IChangeNotify <IMultiLingualText>> ();

  public MultiLingualText ()
  {}

  public MultiLingualText (@Nonnull final Locale aContentLocale, @Nonnull final String sValue)
  {
    internalAddText (aContentLocale, sValue);
  }

  /**
   * Constructor especially for the static TextProvider.createXXX methods
   *
   * @param aSimpleMLT
   *        The simple multi lingual text to use.
   */
  public MultiLingualText (@Nonnull final ISimpleMultiLingualText aSimpleMLT)
  {
    ValueEnforcer.notNull (aSimpleMLT, "SimpleMLT");

    for (final Locale aLocale : aSimpleMLT.getAllLocales ())
      internalAddText (aLocale, aSimpleMLT.getText (aLocale));
  }

  public MultiLingualText (@Nonnull final IReadonlyMultiLingualText aMLT)
  {
    ValueEnforcer.notNull (aMLT, "MLT");

    for (final Map.Entry <Locale, String> aEntry : aMLT.getAllTexts ().entrySet ())
      internalAddText (aEntry.getKey (), aEntry.getValue ());
  }

  private boolean _beforeChange ()
  {
    for (final IChangeNotify <IMultiLingualText> aCallback : m_aChangeNotifyCallbacks.getAllCallbacks ())
      if (aCallback.beforeChange (this).isBreak ())
        return false;
    return true;
  }

  private void _afterChange ()
  {
    for (final IChangeNotify <IMultiLingualText> aCallback : m_aChangeNotifyCallbacks.getAllCallbacks ())
      aCallback.afterChange (this);
  }

  @Nonnull
  public EChange addText (@Nonnull final Locale aContentLocale, @Nullable final String sText)
  {
    ValueEnforcer.notNull (aContentLocale, "ContentLocale");

    if (super.containsLocale (aContentLocale))
      return EChange.UNCHANGED;

    if (!_beforeChange ())
      return EChange.UNCHANGED;
    internalAddText (aContentLocale, sText);
    _afterChange ();
    return EChange.CHANGED;
  }

  @Nonnull
  public EChange setText (@Nonnull final Locale aContentLocale, @Nullable final String sText)
  {
    ValueEnforcer.notNull (aContentLocale, "ContentLocale");

    if (containsLocale (aContentLocale))
    {
      // Text for this locale already contained
      final String sOldText = super.internalGetText (aContentLocale);

      // Did anything change?
      if (EqualsUtils.equals (sOldText, sText))
        return EChange.UNCHANGED;

      if (!_beforeChange ())
        return EChange.UNCHANGED;
      internalSetText (aContentLocale, sText);
      _afterChange ();
      return EChange.CHANGED;
    }

    // New text
    if (!_beforeChange ())
      return EChange.UNCHANGED;
    internalAddText (aContentLocale, sText);
    _afterChange ();
    return EChange.CHANGED;
  }

  @Nonnull
  public EChange removeText (@Nonnull final Locale aContentLocale)
  {
    for (final Locale aCurrentLocale : LocaleUtils.getCalculatedLocaleListForResolving (aContentLocale))
      if (super.containsLocale (aCurrentLocale))
      {
        if (!_beforeChange ())
          return EChange.UNCHANGED;
        internalRemoveText (aCurrentLocale);
        _afterChange ();
        return EChange.CHANGED;
      }
    return EChange.UNCHANGED;
  }

  @Nonnull
  public EChange clear ()
  {
    if (!isEmpty () && _beforeChange ())
    {
      internalClear ();
      _afterChange ();
      return EChange.CHANGED;
    }
    return EChange.UNCHANGED;
  }

  @Nonnull
  public EChange assignFrom (@Nonnull final IReadonlyMultiLingualText aMLT)
  {
    ValueEnforcer.notNull (aMLT, "MLT");

    if (getAllTexts ().equals (aMLT.getAllTexts ()) || !_beforeChange ())
      return EChange.UNCHANGED;

    // Remove all existing texts and assign the new ones
    internalClear ();
    for (final Map.Entry <Locale, String> aEntry : aMLT.getAllTexts ().entrySet ())
      internalAddText (aEntry.getKey (), aEntry.getValue ());
    _afterChange ();
    return EChange.CHANGED;
  }

  @Nonnull
  @ReturnsMutableObject (reason = "design")
  public CallbackList <IChangeNotify <IMultiLingualText>> getChangeNotifyCallbacks ()
  {
    return m_aChangeNotifyCallbacks;
  }

  @Override
  public boolean equals (final Object o)
  {
    return super.equals (o);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode ();
  }

  @Nonnull
  public static IMultiLingualText createFromMap (@Nonnull final Map <String, String> aMap)
  {
    final IMultiLingualText ret = new MultiLingualText ();
    for (final Entry <String, String> aEntry : aMap.entrySet ())
    {
      final String sText = aEntry.getValue ();
      if (sText != null)
        ret.setText (LocaleCache.getLocale (aEntry.getKey ()), sText);
    }
    return ret;
  }

  /**
   * Get a copy of this object with the specified locales. The default locale is
   * copied.
   *
   * @param aMLT
   *        The initial multi lingual text.
   * @param aContentLocales
   *        The list of locales of which the strings are desired. May not be
   *        <code>null</code>.
   * @return The object containing only the texts of the given locales. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static IMultiLingualText getCopyWithLocales (@Nonnull final IReadonlyMultiLingualText aMLT,
                                                      @Nonnull final Collection <Locale> aContentLocales)
  {
    final IMultiLingualText ret = new MultiLingualText ();
    for (final Locale aConrentLocale : aContentLocales)
      if (aMLT.containsLocale (aConrentLocale))
        ret.setText (aConrentLocale, aMLT.getText (aConrentLocale));
    return ret;
  }
}
