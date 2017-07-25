/**
 * Original copyright by Apache Software Foundation
 * Copyright (C) 2017 Philip Helger (www.helger.com)
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
package com.helger.cli.ex;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.string.StringHelper;

/**
 * Exception thrown when an option can't be identified from a partial name.
 *
 * @since 1.3
 */
public class AmbiguousOptionException extends UnrecognizedOptionException
{
  /** The list of options matching the partial name specified */
  private final ICommonsList <String> m_aMatchingOptions;

  /**
   * Constructs a new AmbiguousOptionException.
   *
   * @param sOption
   *        the partial option name
   * @param matchingOptions
   *        the options matching the name
   */
  public AmbiguousOptionException (final String sOption, @Nonnull final Collection <String> matchingOptions)
  {
    super (_createMessage (sOption, matchingOptions), sOption);
    m_aMatchingOptions = new CommonsArrayList <> (matchingOptions);
  }

  /**
   * Returns the options matching the partial name.
   *
   * @return a collection of options matching the name
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <String> getAllMatchingOptions ()
  {
    return m_aMatchingOptions.getClone ();
  }

  /**
   * Build the exception message from the specified list of options.
   *
   * @param sOption
   * @param matchingOptions
   * @return
   */
  private static String _createMessage (final String sOption, @Nonnull final Iterable <String> matchingOptions)
  {
    return "Ambiguous option: '" +
           sOption +
           "' (could be: " +
           StringHelper.getImplodedMapped (", ", matchingOptions, x -> "'" + x + "'") +
           ")";
  }
}
