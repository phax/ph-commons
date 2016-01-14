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

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.error.IHasErrorLevels;

/**
 * A simple read only form error list interface. For a field specific list look
 * at {@link IErrorList}.
 *
 * @author Philip Helger
 */
public interface IErrorList extends Iterable <IError>, IHasErrorLevels, IFieldErrorList, Serializable
{
  /**
   * @return <code>true</code> if this list has no items, <code>false</code> if
   *         at least one item is contained
   */
  boolean isEmpty ();

  /**
   * @return The number of contained items. Always &ge; 0.
   */
  @Nonnegative
  int getItemCount ();

  /**
   * @return <code>true</code> if at least 1 item of level warning or at least 1
   *         item of level error is contained.
   */
  boolean hasErrorsOrWarnings ();

  /**
   * @return An immutable list of all contained entries. Never <code>null</code>
   *         .
   */
  @Nonnull
  @ReturnsMutableCopy
  List <IError> getAllItems ();

  /**
   * @return The error texts of all contained {@link IError} objects. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <String> getAllItemTexts ();

  /**
   * @return An error list with all entries that don't have a field assigned.
   */
  @Nonnull
  @ReturnsMutableCopy
  IErrorList getListWithoutField ();
}
