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

import com.helger.cli.Options.IRequiredOption;
import com.helger.cli.Options.RequiredArg;
import com.helger.cli.Options.RequiredGroup;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.string.StringHelper;

/**
 * Thrown when a required option has not been provided.
 */
public class MissingOptionException extends CommandLineParseException
{
  /** The list of missing options and groups */
  private final ICommonsList <IRequiredOption> m_aMissingOptions;

  /**
   * Constructs a new <code>MissingSelectedException</code> with the specified
   * list of missing options.
   *
   * @param aMissingOptions
   *        the list of missing options and groups
   * @since 1.2
   */
  public MissingOptionException (@Nonnull final Collection <IRequiredOption> aMissingOptions)
  {
    super (_createMessage (aMissingOptions));
    m_aMissingOptions = new CommonsArrayList <> (aMissingOptions);
  }

  /**
   * Returns the list of options or option groups missing in the command line
   * parsed.
   *
   * @return the missing options, consisting of String instances for simple
   *         options, and OptionGroup instances for required option groups.
   * @since 1.2
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IRequiredOption> getAllMissingOptions ()
  {
    return m_aMissingOptions.getClone ();
  }

  /**
   * Build the exception message from the specified list of options.
   *
   * @param aMissingOptions
   *        the list of missing options and groups
   * @since 1.2
   */
  private static String _createMessage (@Nonnull final Collection <IRequiredOption> aMissingOptions)
  {
    final StringBuilder buf = new StringBuilder ("Missing required option");
    if (aMissingOptions.size () != 1)
      buf.append ('s');
    buf.append (": ").append (StringHelper.getImplodedMapped (", ", aMissingOptions, x -> {
      if (x instanceof RequiredArg)
        return ((RequiredArg) x).getName ();
      return ((RequiredGroup) x).getGroup ().getAsString ();
    }));
    return buf.toString ();
  }
}
