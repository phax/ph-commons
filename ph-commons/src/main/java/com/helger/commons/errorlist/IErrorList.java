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

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.collection.multimap.IMultiMapListBased;
import com.helger.commons.collection.multimap.MultiLinkedHashMapArrayListBased;
import com.helger.commons.error.list.IErrorBaseList;

/**
 * A simple read only form error list interface. For a field specific list look
 * at {@link IErrorList}.
 *
 * @author Philip Helger
 */
@Deprecated
public interface IErrorList extends IErrorBaseList <IError>, IFieldErrorList
{
  /**
   * @return The number of contained items. Always &ge; 0.
   */
  @Nonnegative
  @Deprecated
  default int getItemCount ()
  {
    return getSize ();
  }

  /**
   * @return An immutable list of all contained entries. Never <code>null</code>
   *         .
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <IError> getAllItems ();

  /**
   * @return The error texts of all contained {@link IError} objects. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <String> getAllItemTexts ();

  /**
   * @return An error list with all entries that don't have a field assigned.
   */
  @Nonnull
  @ReturnsMutableCopy
  IErrorList getListWithoutField ();

  @Nonnull
  @ReturnsMutableCopy
  default IMultiMapListBased <String, IError> getGroupedByID ()
  {
    final IMultiMapListBased <String, IError> ret = new MultiLinkedHashMapArrayListBased<> ();
    forEach (x -> ret.putSingle (x.getErrorID (), x));
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  default IMultiMapListBased <String, IError> getGroupedByFieldName ()
  {
    final IMultiMapListBased <String, IError> ret = new MultiLinkedHashMapArrayListBased<> ();
    forEach (x -> ret.putSingle (x.getErrorFieldName (), x));
    return ret;
  }
}
