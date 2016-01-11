/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.compare;

import java.text.Collator;
import java.util.Locale;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.ReturnsMutableCopy;

/**
 * An abstract implementation of a {@link java.util.Comparator} that uses
 * collations for ordering. This is only necessary when comparing strings.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        the type of object to be compared
 */
@NotThreadSafe
public class CollatingPartComparator <DATATYPE> extends PartComparator <DATATYPE, String>
{
  /**
   * Comparator with the specified sort locale.
   *
   * @param aSortLocale
   *        The locale to use. May be <code>null</code>.
   * @param aExtractor
   *        Part extractor function. May not be <code>null</code>.
   */
  public CollatingPartComparator (@Nullable final Locale aSortLocale,
                                  @Nonnull final Function <DATATYPE, String> aExtractor)
  {
    this (new CollatingComparator (aSortLocale), aExtractor);
  }

  /**
   * Constructor with {@link Collator}
   *
   * @param aCollator
   *        The {@link Collator} to use. May not be <code>null</code>.
   * @param aExtractor
   *        Part extractor function. May not be <code>null</code>.
   */
  public CollatingPartComparator (@Nonnull final Collator aCollator,
                                  @Nonnull final Function <DATATYPE, String> aExtractor)
  {
    this (new CollatingComparator (aCollator), aExtractor);
  }

  /**
   * Constructor with {@link CollatingComparator}
   *
   * @param aCollatingComparator
   *        The comparator for comparing the extracted parts. May not be
   *        <code>null</code>.
   * @param aExtractor
   *        Part extractor function. May not be <code>null</code>.
   */
  public CollatingPartComparator (@Nonnull final CollatingComparator aCollatingComparator,
                                  @Nonnull final Function <DATATYPE, String> aExtractor)
  {
    super (aCollatingComparator, aExtractor);
  }

  /**
   * @return A copy of the {@link Collator} as passed or created in the
   *         constructor. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public final Collator getCollator ()
  {
    return ((CollatingComparator) getPartComparator ()).getCollator ();
  }
}
