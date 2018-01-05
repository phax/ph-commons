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
package com.helger.commons.location;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.string.StringHelper;

/**
 * Interface indication the position of something within a single resource
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface ILocation extends Serializable
{
  /** Constant for an illegal row or column number */
  int ILLEGAL_NUMBER = CGlobal.ILLEGAL_UINT;

  /**
   * @return The ID of the resource where the error occurred. May be
   *         <code>null</code>.
   */
  @Nullable
  String getResourceID ();

  default boolean hasResourceID ()
  {
    return StringHelper.hasText (getResourceID ());
  }

  /**
   * @return The 1-based line number {@link #ILLEGAL_NUMBER} if no line number
   *         is present.
   */
  int getLineNumber ();

  default boolean hasLineNumber ()
  {
    return getLineNumber () > ILLEGAL_NUMBER;
  }

  /**
   * @return The 1-based column number {@link #ILLEGAL_NUMBER} if no column
   *         number is present.
   */
  int getColumnNumber ();

  default boolean hasColumnNumber ()
  {
    return getColumnNumber () > ILLEGAL_NUMBER;
  }

  /**
   * Simple method to check if resource ID, line number, column number or field
   * name is present.
   *
   * @return <code>true</code> if at least one field is set, <code>false</code>
   *         otherwise.
   */
  default boolean isAnyInformationPresent ()
  {
    return hasResourceID () || hasLineNumber () || hasColumnNumber ();
  }

  /**
   * @return The display text of the resource location.
   */
  @Nonnull
  default String getAsString ()
  {
    String ret = "";

    final String sResourceID = getResourceID ();
    if (StringHelper.hasText (sResourceID))
      ret += sResourceID;

    if (hasLineNumber ())
    {
      if (hasColumnNumber ())
        ret += "(" + getLineNumber () + ":" + getColumnNumber () + ")";
      else
        ret += "(" + getLineNumber () + ":?)";
    }
    else
    {
      if (hasColumnNumber ())
        ret += "(?:" + getColumnNumber () + ")";
      // else: neither nor
    }
    return ret;
  }
}
