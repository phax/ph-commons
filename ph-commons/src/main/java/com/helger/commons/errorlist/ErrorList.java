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

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.RegEx;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.error.EErrorLevel;
import com.helger.commons.error.IErrorLevel;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.state.IClearable;
import com.helger.commons.string.ToStringGenerator;

/**
 * Handles a list of form global errors.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class ErrorList implements IErrorList, IClearable, ICloneable <ErrorList>
{
  private final ICommonsList <IError> m_aItems = new CommonsArrayList<> ();

  public ErrorList ()
  {}

  public ErrorList (@Nullable final IErrorList aErrorList)
  {
    addAll (aErrorList);
  }

  public ErrorList (@Nullable final ErrorList aErrorList)
  {
    if (aErrorList != null)
      m_aItems.addAll (aErrorList.m_aItems);
  }

  public ErrorList (@Nullable final Iterable <? extends IError> aErrorList)
  {
    addAll (aErrorList);
  }

  public ErrorList (@Nullable final IError... aErrorList)
  {
    addAll (aErrorList);
  }

  public final void addAll (@Nullable final IErrorList aErrorList)
  {
    if (aErrorList != null)
      for (final IError aFormError : aErrorList.getAllItems ())
        add (aFormError);
  }

  public final void addAll (@Nullable final Iterable <? extends IError> aErrorList)
  {
    if (aErrorList != null)
      for (final IError aFormError : aErrorList)
        add (aFormError);
  }

  public final void addAll (@Nullable final IError... aErrorList)
  {
    if (aErrorList != null)
      for (final IError aFormError : aErrorList)
        add (aFormError);
  }

  /**
   * Add a new item.
   *
   * @param aItem
   *        The item to be added. May not be <code>null</code>.
   */
  public final void add (@Nonnull final IError aItem)
  {
    ValueEnforcer.notNull (aItem, "Item");
    m_aItems.add (aItem);
  }

  public void addSuccess (@Nonnull @Nonempty final String sText)
  {
    add (SingleError.createSuccess (sText));
  }

  public void addSuccess (@Nullable final String sFieldName, @Nonnull @Nonempty final String sText)
  {
    add (SingleError.createSuccess (sFieldName, sText));
  }

  public void addSuccess (@Nullable final String sID,
                          @Nullable final String sFieldName,
                          @Nonnull @Nonempty final String sText)
  {
    add (SingleError.createSuccess (sID, sFieldName, sText));
  }

  public void addInfo (@Nonnull @Nonempty final String sText)
  {
    add (SingleError.createInfo (sText));
  }

  public void addInfo (@Nullable final String sFieldName, @Nonnull @Nonempty final String sText)
  {
    add (SingleError.createInfo (sFieldName, sText));
  }

  public void addInfo (@Nullable final String sID,
                       @Nullable final String sFieldName,
                       @Nonnull @Nonempty final String sText)
  {
    add (SingleError.createInfo (sID, sFieldName, sText));
  }

  public void addWarning (@Nonnull @Nonempty final String sText)
  {
    add (SingleError.createWarning (sText));
  }

  public void addWarning (@Nullable final String sFieldName, @Nonnull @Nonempty final String sText)
  {
    add (SingleError.createWarning (sFieldName, sText));
  }

  public void addWarning (@Nullable final String sID,
                          @Nullable final String sFieldName,
                          @Nonnull @Nonempty final String sText)
  {
    add (SingleError.createWarning (sID, sFieldName, sText));
  }

  public void addError (@Nonnull @Nonempty final String sText)
  {
    add (SingleError.createError (sText));
  }

  public void addError (@Nullable final String sFieldName, @Nonnull @Nonempty final String sText)
  {
    add (SingleError.createError (sFieldName, sText));
  }

  public void addError (@Nullable final String sID,
                        @Nullable final String sFieldName,
                        @Nonnull @Nonempty final String sText)
  {
    add (SingleError.createError (sID, sFieldName, sText));
  }

  public boolean isEmpty ()
  {
    return m_aItems.isEmpty ();
  }

  @Nonnegative
  public int getItemCount ()
  {
    return m_aItems.size ();
  }

  public boolean hasErrorsOrWarnings ()
  {
    return m_aItems.containsAny (e -> e.getErrorLevel ().isMoreOrEqualSevereThan (EErrorLevel.WARN));
  }

  public boolean containsOnlySuccess ()
  {
    return m_aItems.containsOnly (e -> e.isSuccess ());
  }

  public boolean containsAtLeastOneSuccess ()
  {
    return m_aItems.containsAny (e -> e.isSuccess ());
  }

  public boolean containsNoSuccess ()
  {
    return m_aItems.containsNone (e -> e.isSuccess ());
  }

  @Nonnegative
  public int getSuccessCount ()
  {
    return m_aItems.getCount (e -> e.isSuccess ());
  }

  public boolean containsOnlyFailure ()
  {
    return m_aItems.containsOnly (e -> e.isFailure ());
  }

  public boolean containsAtLeastOneFailure ()
  {
    return m_aItems.containsAny (e -> e.isFailure ());
  }

  public boolean containsNoFailure ()
  {
    return m_aItems.containsNone (e -> e.isFailure ());
  }

  @Nonnegative
  public int getFailureCount ()
  {
    return m_aItems.getCount (e -> e.isFailure ());
  }

  public boolean containsOnlyError ()
  {
    return m_aItems.containsOnly (e -> e.isError ());
  }

  public boolean containsAtLeastOneError ()
  {
    return m_aItems.containsAny (e -> e.isError ());
  }

  public boolean containsNoError ()
  {
    return m_aItems.containsNone (e -> e.isError ());
  }

  @Nonnegative
  public int getErrorCount ()
  {
    return m_aItems.getCount (e -> e.isError ());
  }

  @Nonnull
  public IErrorLevel getMostSevereErrorLevel ()
  {
    IErrorLevel ret = EErrorLevel.SUCCESS;
    for (final IError aError : m_aItems)
    {
      final IErrorLevel eCur = aError.getErrorLevel ();
      if (eCur.isMoreSevereThan (ret))
        ret = eCur;
    }
    return ret;
  }

  public void forEachItem (@Nonnull final Consumer <? super IError> aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");
    m_aItems.forEach (aConsumer);
  }

  /**
   * @return A non-<code>null</code> list of all contained texts, independent of
   *         the level.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllItemTexts ()
  {
    return m_aItems.getAllMapped (IError::getErrorText);
  }

  /**
   * @return A copy of all contained items. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IError> getAllItems ()
  {
    return m_aItems.getClone ();
  }

  /**
   * @return An {@link Iterator} over all contained items. Never
   *         <code>null</code>.
   */
  @Nonnull
  public Iterator <IError> iterator ()
  {
    return m_aItems.iterator ();
  }

  // --- field specific elements ---

  @Nonnull
  @ReturnsMutableCopy
  public ErrorList getListWithoutField ()
  {
    final ErrorList ret = new ErrorList ();
    m_aItems.findAll (e -> e.hasNoErrorFieldName (), ret::add);
    return ret;
  }

  public boolean hasNoEntryForField (@Nullable final String sSearchFieldName)
  {
    return m_aItems.containsNone (e -> e.hasErrorFieldName (sSearchFieldName));
  }

  public boolean hasEntryForField (@Nullable final String sSearchFieldName)
  {
    return m_aItems.containsAny (e -> e.hasErrorFieldName (sSearchFieldName));
  }

  public boolean hasEntryForField (@Nullable final String sSearchFieldName, @Nullable final IErrorLevel aErrorLevel)
  {
    if (aErrorLevel != null)
      for (final IError aError : m_aItems)
        if (aError.getErrorLevel ().equals (aErrorLevel) && aError.hasErrorFieldName (sSearchFieldName))
          return true;
    return false;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ErrorList getListOfField (@Nullable final String sSearchFieldName)
  {
    final ErrorList ret = new ErrorList ();
    m_aItems.findAll (e -> e.hasErrorFieldName (sSearchFieldName), ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ErrorList getListOfFields (@Nullable final String... aSearchFieldNames)
  {
    final ErrorList ret = new ErrorList ();
    if (ArrayHelper.isNotEmpty (aSearchFieldNames))
      m_aItems.findAll (e -> ArrayHelper.contains (aSearchFieldNames, e.getErrorFieldName ()), ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ErrorList getListOfFieldsStartingWith (@Nullable final String... aSearchFieldNames)
  {
    final ErrorList ret = new ErrorList ();
    if (ArrayHelper.isNotEmpty (aSearchFieldNames))
      m_aItems.findAll (e -> e.hasErrorFieldName (), e -> {
        final String sErrorFieldName = e.getErrorFieldName ();
        for (final String sSearchField : aSearchFieldNames)
          if (sErrorFieldName.startsWith (sSearchField))
          {
            ret.add (e);
            break;
          }
      });
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ErrorList getListOfFieldsRegExp (@Nonnull @Nonempty @RegEx final String sRegExp)
  {
    ValueEnforcer.notEmpty (sRegExp, "RegExp");

    final ErrorList ret = new ErrorList ();
    m_aItems.findAll (e -> e.hasErrorFieldName () && RegExHelper.stringMatchesPattern (sRegExp, e.getErrorFieldName ()),
                      ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllItemTextsOfField (@Nullable final String sSearchFieldName)
  {
    return m_aItems.getAllMapped (e -> e.hasErrorFieldName (sSearchFieldName), e -> e.getErrorText ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllItemTextsOfFields (@Nullable final String... aSearchFieldNames)
  {
    final ICommonsList <String> ret = new CommonsArrayList<> ();
    if (ArrayHelper.isNotEmpty (aSearchFieldNames))
      m_aItems.findAll (e -> ArrayHelper.contains (aSearchFieldNames, e.getErrorFieldName ()),
                        e -> ret.add (e.getErrorText ()));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllItemTextsOfFieldsStartingWith (@Nullable final String... aSearchFieldNames)
  {
    final ICommonsList <String> ret = new CommonsArrayList<> ();
    if (ArrayHelper.isNotEmpty (aSearchFieldNames))
      m_aItems.findAll (e -> e.hasErrorFieldName (), e -> {
        final String sErrorFieldName = e.getErrorFieldName ();
        for (final String sSearchField : aSearchFieldNames)
          if (sErrorFieldName.startsWith (sSearchField))
          {
            ret.add (e.getErrorText ());
            break;
          }
      });
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllItemTextsOfFieldsRegExp (@Nonnull @Nonempty @RegEx final String sRegEx)
  {
    ValueEnforcer.notEmpty (sRegEx, "RegEx");

    return m_aItems.getAllMapped (e -> e.hasErrorFieldName () &&
                                       RegExHelper.stringMatchesPattern (sRegEx, e.getErrorFieldName ()),
                                  e -> e.getErrorText ());
  }

  @Nonnull
  public EChange remove (@Nullable final IError aError)
  {
    if (aError == null)
      return EChange.UNCHANGED;
    return m_aItems.removeObject (aError);
  }

  @Nonnull
  public EChange removeAll (@Nullable final IError... aErrors)
  {
    EChange ret = EChange.UNCHANGED;
    if (aErrors != null)
      for (final IError aError : aErrors)
        ret = ret.or (remove (aError));
    return ret;
  }

  @Nonnull
  public EChange removeAll (@Nullable final Iterable <? extends IError> aErrors)
  {
    EChange ret = EChange.UNCHANGED;
    if (aErrors != null)
      for (final IError aError : aErrors)
        ret = ret.or (remove (aError));
    return ret;
  }

  @Nonnull
  public EChange removeIf (@Nonnull final Predicate <? super IError> aFilter)
  {
    return EChange.valueOf (m_aItems.removeIf (aFilter));
  }

  @Nonnull
  public EChange clear ()
  {
    return m_aItems.removeAll ();
  }

  @Nonnull
  public ErrorList getClone ()
  {
    return new ErrorList (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ErrorList rhs = (ErrorList) o;
    return m_aItems.equals (rhs.m_aItems);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aItems).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("items", m_aItems).toString ();
  }
}
