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
package com.helger.commons.errorlist;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.error.IError;
import com.helger.commons.error.SingleError;
import com.helger.commons.error.list.ErrorList;

/**
 * Handles form field specific and form global error messages centrally.
 *
 * @author Philip Helger
 * @deprecated Use {@link ErrorList} instead
 */
@NotThreadSafe
@Deprecated
public class FormErrors extends ErrorList
{
  public FormErrors ()
  {}

  /**
   * Add a form-global item
   *
   * @param aFormError
   *        The form error object to add. May not be <code>null</code>.
   */
  @Deprecated
  public void addGlobalItem (@Nonnull final IError aFormError)
  {}

  /**
   * Add a form-global information
   *
   * @param sText
   *        The text to use. May neither be <code>null</code> nor empty.
   */
  @Deprecated
  public void addGlobalInfo (@Nonnull @Nonempty final String sText)
  {}

  /**
   * Add a form-global warning
   *
   * @param sText
   *        The text to use. May neither be <code>null</code> nor empty.
   */
  @Deprecated
  public void addGlobalWarning (@Nonnull @Nonempty final String sText)
  {}

  /**
   * Add a form-global error
   *
   * @param sText
   *        The text to use. May neither be <code>null</code> nor empty.
   */
  @Deprecated
  public void addGlobalError (@Nonnull @Nonempty final String sText)
  {}

  /**
   * Add a form field specific item
   *
   * @param aFormFieldError
   *        The form field error object to add. May not be <code>null</code>.
   */
  @Deprecated
  public void addFieldItem (@Nonnull final IError aFormFieldError)
  {
    add (aFormFieldError);
  }

  /**
   * Add a field specific information message.
   *
   * @param sFieldName
   *        The field name for which the message is to be recorded. May neither
   *        be <code>null</code> nor empty.
   * @param sText
   *        The text to use. May neither be <code>null</code> nor empty.
   */
  public void addFieldInfo (@Nonnull @Nonempty final String sFieldName, @Nonnull @Nonempty final String sText)
  {
    add (SingleError.builderInfo ().setErrorFieldName (sFieldName).setErrorText (sText).build ());
  }

  /**
   * Add a field specific warning message.
   *
   * @param sFieldName
   *        The field name for which the message is to be recorded. May neither
   *        be <code>null</code> nor empty.
   * @param sText
   *        The text to use. May neither be <code>null</code> nor empty.
   */
  public void addFieldWarning (@Nonnull @Nonempty final String sFieldName, @Nonnull @Nonempty final String sText)
  {
    add (SingleError.builderWarn ().setErrorFieldName (sFieldName).setErrorText (sText).build ());
  }

  /**
   * Add a field specific error message.
   *
   * @param sFieldName
   *        The field name for which the message is to be recorded. May neither
   *        be <code>null</code> nor empty.
   * @param sText
   *        The text to use. May neither be <code>null</code> nor empty.
   */
  public void addFieldError (@Nonnull @Nonempty final String sFieldName, @Nonnull @Nonempty final String sText)
  {
    add (SingleError.builderError ().setErrorFieldName (sFieldName).setErrorText (sText).build ());
  }

  /**
   * Add a field specific error message for multiple fields.
   *
   * @param aFieldNames
   *        The field names for which the message is to be recorded. May neither
   *        be <code>null</code> nor empty.
   * @param sText
   *        The text to use. May neither be <code>null</code> nor empty.
   */
  public void addFieldError (@Nonnull @Nonempty final String [] aFieldNames, @Nonnull @Nonempty final String sText)
  {
    ValueEnforcer.notEmptyNoNullValue (aFieldNames, "FieldNames");
    for (final String sFieldName : aFieldNames)
      addFieldError (sFieldName, sText);
  }

  /**
   * @return <code>true</code> if form-global errors or warnings are present.
   */
  @Deprecated
  public boolean hasGlobalErrorsOrWarnings ()
  {
    return false;
  }

  /**
   * @return <code>true</code> if form-field errors or warnings are present.
   */
  @Deprecated
  public boolean hasFormFieldErrorsOrWarnings ()
  {
    return containsAtLeastOneWarningOrError ();
  }

  /**
   * @return The number of global items. Always &ge; 0.
   */
  @Nonnegative
  @Deprecated
  public int getGlobalItemCount ()
  {
    return 0;
  }

  /**
   * @return The number of form-field-specific items. Always &ge; 0.
   */
  @Nonnegative
  @Deprecated
  public int getFieldItemCount ()
  {
    return getSize ();
  }

  /**
   * Get the total number of items for both form-global and form-field-specific
   * items
   *
   * @return The total item count. Always &ge; 0.
   */
  @Nonnegative
  @Deprecated
  public int getItemCount ()
  {
    return getSize ();
  }

  /**
   * @return A non-<code>null</code> list of form global errors.
   */
  @Nonnull
  @ReturnsMutableCopy
  @Deprecated
  public ICommonsList <IError> getAllGlobalItems ()
  {
    return new CommonsArrayList<> ();
  }

  /**
   * @return A non-<code>null</code> list of all form global error texts.
   */
  @Nonnull
  @ReturnsMutableCopy
  @Deprecated
  public ICommonsList <String> getAllGlobalItemTexts ()
  {
    return new CommonsArrayList<> ();
  }
}
