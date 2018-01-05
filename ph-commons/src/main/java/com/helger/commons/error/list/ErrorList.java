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
package com.helger.commons.error.list;

import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.error.IError;

/**
 * Default implementation of {@link IErrorList}.
 *
 * @author Philip Helger
 * @since 8.5.0
 */
@NotThreadSafe
public class ErrorList extends CommonsArrayList <IError> implements IErrorList
{
  /**
   * Default constructor.
   */
  public ErrorList ()
  {}

  /**
   * Constructor taking a list of iterable objects.
   *
   * @param aList
   *        The list to be added. May be <code>null</code>.
   */
  public ErrorList (@Nullable final Iterable <? extends IError> aList)
  {
    super (aList);
  }

  /**
   * Constructor taking a list of iterable objects.
   *
   * @param aList
   *        The array to be added. May be <code>null</code>.
   */
  public ErrorList (@Nullable final IError... aList)
  {
    super (aList);
  }

  /**
   * Copy constructor.
   *
   * @param aErrorList
   *        The error list to copy from. May be <code>null</code>.
   */
  public ErrorList (@Nonnull final ErrorList aErrorList)
  {
    super (aErrorList);
  }

  @Nonnull
  public ErrorList getSubList (@Nullable final Predicate <? super IError> aFilter)
  {
    if (aFilter == null)
      return getClone ();

    final ErrorList ret = new ErrorList ();
    findAll (aFilter, ret::add);
    return ret;
  }

  @Override
  @Nonnull
  public ErrorList getClone ()
  {
    return new ErrorList (this);
  }
}
