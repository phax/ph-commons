/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.settings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.state.EChange;

/**
 * Writable settings interface.
 *
 * @author philip
 */
public interface IMutableSettings extends ISettings
{
  /**
   * {@inheritDoc}
   */
  @Nullable
  IMutableSettings getSettingsValue (@Nullable String sFieldName);

  /**
   * Restore a value from serialization. Does not trigger an event!
   *
   * @param sFieldName
   *        The name of the field.
   * @param aNewValue
   *        The value to be set. May be <code>null</code> .
   */
  void restoreValue (@Nonnull @Nonempty String sFieldName, @Nonnull Object aNewValue);

  /**
   * Copy all settings from another settings object. Existing values are not
   * erased, only additional values are added and existing values may be
   * changed.
   *
   * @param aOtherSettings
   *        The settings from which we want to copy stuff. May not be
   *        <code>null</code>.
   * @return {@link EChange}. Is changed if at least one value has been changed.
   */
  @Nonnull
  EChange setValues (@Nonnull ISettings aOtherSettings);

  /**
   * Remove the value identified by the given field name. If the value was
   * successfully removed a change event is triggered, where the old value is
   * passed in and the new value is <code>null</code>.
   *
   * @param sFieldName
   *        The field name to be removed. May be <code>null</code>.
   * @return {@link EChange}.
   */
  @Nonnull
  EChange removeValue (@Nullable String sFieldName);

  /**
   * Remove all values from this settings object.
   *
   * @return {@link EChange#CHANGED} if at least one value was removed.
   */
  @Nonnull
  EChange clear ();

  /**
   * Set the value of a settings field.
   *
   * @param sFieldName
   *        The name of the field to be set. May neither be <code>null</code>
   *        nor empty.
   * @param aNewValue
   *        The new value to be set. If the value is <code>null</code> the value
   *        is removed.
   * @return {@link EChange#CHANGED} if the value changed,
   *         {@link EChange#UNCHANGED} if no change was performed.
   */
  @Nonnull
  EChange setValue (@Nonnull @Nonempty String sFieldName, @Nullable Object aNewValue);

  /**
   * Set the value of a settings field.
   *
   * @param sFieldName
   *        The name of the field to be set. May neither be <code>null</code>
   *        nor empty.
   * @param bNewValue
   *        The new value to be set.
   * @return {@link EChange#CHANGED} if the value changed,
   *         {@link EChange#UNCHANGED} if no change was performed.
   */
  @Nonnull
  default EChange setValue (@Nonnull @Nonempty final String sFieldName, final boolean bNewValue)
  {
    return setValue (sFieldName, Boolean.valueOf (bNewValue));
  }

  /**
   * Set the value of a settings field.
   *
   * @param sFieldName
   *        The name of the field to be set. May neither be <code>null</code>
   *        nor empty.
   * @param nNewValue
   *        The new value to be set.
   * @return {@link EChange#CHANGED} if the value changed,
   *         {@link EChange#UNCHANGED} if no change was performed.
   */
  @Nonnull
  default EChange setValue (@Nonnull @Nonempty final String sFieldName, final int nNewValue)
  {
    return setValue (sFieldName, Integer.valueOf (nNewValue));
  }

  /**
   * Set the value of a settings field.
   *
   * @param sFieldName
   *        The name of the field to be set. May neither be <code>null</code>
   *        nor empty.
   * @param nNewValue
   *        The new value to be set.
   * @return {@link EChange#CHANGED} if the value changed,
   *         {@link EChange#UNCHANGED} if no change was performed.
   */
  @Nonnull
  default EChange setValue (@Nonnull @Nonempty final String sFieldName, final long nNewValue)
  {
    return setValue (sFieldName, Long.valueOf (nNewValue));
  }

  /**
   * Set the value of a settings field.
   *
   * @param sFieldName
   *        The name of the field to be set. May neither be <code>null</code>
   *        nor empty.
   * @param fNewValue
   *        The new value to be set.
   * @return {@link EChange#CHANGED} if the value changed,
   *         {@link EChange#UNCHANGED} if no change was performed.
   */
  @Nonnull
  default EChange setValue (@Nonnull @Nonempty final String sFieldName, final float fNewValue)
  {
    return setValue (sFieldName, Float.valueOf (fNewValue));
  }

  /**
   * Set the value of a settings field.
   *
   * @param sFieldName
   *        The name of the field to be set. May neither be <code>null</code>
   *        nor empty.
   * @param dNewValue
   *        The new value to be set.
   * @return {@link EChange#CHANGED} if the value changed,
   *         {@link EChange#UNCHANGED} if no change was performed.
   */
  @Nonnull
  default EChange setValue (@Nonnull @Nonempty final String sFieldName, final double dNewValue)
  {
    return setValue (sFieldName, Double.valueOf (dNewValue));
  }
}
