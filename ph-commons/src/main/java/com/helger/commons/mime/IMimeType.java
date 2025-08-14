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
package com.helger.commons.mime;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.MustImplementComparable;
import com.helger.annotation.style.MustImplementEqualsAndHashcode;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.lang.ICloneable;
import com.helger.collection.commons.ICommonsList;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Interface for the structured representation of a single MIME type.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
@MustImplementComparable
public interface IMimeType extends ICloneable <IMimeType>
{
  /**
   * @return The content type. Never <code>null</code>.
   */
  @Nonnull
  EMimeContentType getContentType ();

  /**
   * @return The content sub type. Never <code>null</code>.
   */
  @Nonnull
  String getContentSubType ();

  /**
   * Get the MIME type including all parameters as a single string. By default
   * the {@link CMimeType#DEFAULT_QUOTING} quoting algorithm is used.
   *
   * @return The combined string to be used as text representation:
   *         <code><em>contentType</em> '/' <em>subType</em> ( ';'
   *         <em>parameterName</em> '=' <em>parameterValue</em> )*</code>
   * @see #getAsString(EMimeQuoting)
   * @see #getAsStringWithoutParameters()
   */
  @Nonnull
  default String getAsString ()
  {
    return getAsString (CMimeType.DEFAULT_QUOTING);
  }

  /**
   * Get the MIME type including all parameters as a single string. The
   * specified quoting algorithm is used to quote parameter values (if
   * necessary).
   *
   * @param eQuotingAlgorithm
   *        Quoting algorithm to be used
   * @return The combined string to be used as text representation:
   *         <code><em>contentType</em> '/' <em>subType</em> ( ';'
   *         <em>parameterName</em> '=' <em>parameterValue</em> )*</code>
   * @see #getAsStringWithoutParameters()
   * @see #getParametersAsString(EMimeQuoting)
   */
  @Nonnull
  @Nonempty
  String getAsString (@Nonnull EMimeQuoting eQuotingAlgorithm);

  /**
   * @return The combined string to be used as text representation but without
   *         the parameters: <code><em>contentType</em> '/'
   *         <em>subType</em></code>
   * @see #getAsString()
   */
  @Nonnull
  @Nonempty
  String getAsStringWithoutParameters ();

  /**
   * Get all MIME type parameters as a single string but without the leading
   * content and sub type. The specified quoting algorithm is used to quote
   * parameter values.
   *
   * @param eQuotingAlgorithm
   *        Quoting algorithm to be used
   * @return The combined string to be used as text representation:
   *         <code>(';' <em>parameterName</em> '=' <em>parameterValue</em>
   *         )*</code> . If no parameters are present, an empty String is
   *         returned!
   * @see #getAsString(EMimeQuoting)
   * @see #getAsStringWithoutParameters()
   */
  @Nonnull
  String getParametersAsString (@Nonnull EMimeQuoting eQuotingAlgorithm);

  /**
   * @return <code>true</code> if at least one parameter is present,
   *         <code>false</code> if no parameter is present.
   */
  boolean hasAnyParameters ();

  /**
   * @return The number of parameters. Alway &ge; 0.
   */
  @Nonnegative
  int getParameterCount ();

  /**
   * @return All present parameters. May not be <code>null</code> but empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <MimeTypeParameter> getAllParameters ();

  /**
   * Get the parameter at the specified index.
   *
   * @param nIndex
   *        The index to use. Should be &ge; 0.
   * @return <code>null</code> if the provided index is illegal.
   */
  @Nullable
  MimeTypeParameter getParameterAtIndex (@Nonnegative int nIndex);

  /**
   * Check if a parameter with the specified name is present. The names are
   * matched case sensitive!
   *
   * @param sParamName
   *        The parameter name to search. May be <code>null</code>.
   * @return <code>true</code> if such a parameter exists.
   */
  default boolean hasParameterWithName (@Nullable final String sParamName)
  {
    return getParameterWithName (sParamName) != null;
  }

  /**
   * Get the parameter with the specified name. The names are matched case
   * sensitive!
   *
   * @param sParamName
   *        The parameter name to search. May be <code>null</code>.
   * @return <code>null</code> if no such parameter exists.
   */
  @Nullable
  MimeTypeParameter getParameterWithName (@Nullable String sParamName);

  /**
   * Get the value of the parameter with the specified name. The names are
   * matched case sensitive!
   *
   * @param sParamName
   *        The parameter name to search. May be <code>null</code>.
   * @return <code>null</code> if no such parameter exists.
   */
  @Nullable
  String getParameterValueWithName (@Nullable String sParamName);

  /**
   * @return A copy of this MIME type but only the content type and the sub
   *         type. This method must even deliver a copy if no parameter are
   *         present! May not return <code>null</code>.
   */
  @Nonnull
  IMimeType getCopyWithoutParameters ();
}
