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
package com.helger.commons.error.level;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.MustImplementEqualsAndHashcode;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.id.IHasID;
import com.helger.commons.severity.ISeverityComparable;
import com.helger.commons.state.IErrorIndicator;
import com.helger.commons.state.ISuccessIndicator;

/**
 * Interface representing a single error level.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IErrorLevel extends
                             IHasID <String>,
                             ISuccessIndicator,
                             IErrorIndicator,
                             ISeverityComparable <IErrorLevel>
{
  /**
   * @return The numeric level of this error level. Must be &ge; 0. The higher
   *         the numeric level, the higher the priority of the error level. So
   *         e.g. ERROR has a higher/larger/greater numerical level than
   *         WARNING.
   */
  @Nonnegative
  int getNumericLevel ();

  default boolean isSuccess ()
  {
    return isEQ (EErrorLevel.SUCCESS);
  }

  @Override
  default boolean isFailure ()
  {
    return isGT (EErrorLevel.SUCCESS);
  }

  default boolean isError ()
  {
    return isGE (EErrorLevel.ERROR);
  }

  @Override
  default boolean isNoError ()
  {
    return isLT (EErrorLevel.ERROR);
  }

  default int compareTo (@Nonnull final IErrorLevel aErrorLevel)
  {
    return Integer.compare (getNumericLevel (), aErrorLevel.getNumericLevel ());
  }

  default boolean isEQ (@Nonnull final IErrorLevel aErrorLevel)
  {
    return getNumericLevel () == aErrorLevel.getNumericLevel ();
  }

  @Override
  default boolean isNE (@Nonnull final IErrorLevel aErrorLevel)
  {
    return getNumericLevel () != aErrorLevel.getNumericLevel ();
  }

  default boolean isLT (@Nonnull final IErrorLevel aErrorLevel)
  {
    return getNumericLevel () < aErrorLevel.getNumericLevel ();
  }

  default boolean isLE (@Nonnull final IErrorLevel aErrorLevel)
  {
    return getNumericLevel () <= aErrorLevel.getNumericLevel ();
  }

  default boolean isGT (@Nonnull final IErrorLevel aErrorLevel)
  {
    return getNumericLevel () > aErrorLevel.getNumericLevel ();
  }

  default boolean isGE (@Nonnull final IErrorLevel aErrorLevel)
  {
    return getNumericLevel () >= aErrorLevel.getNumericLevel ();
  }

  default boolean isHighest ()
  {
    return isEQ (EErrorLevel.HIGHEST);
  }

  @Nullable
  static IErrorLevel getMostSevere (@Nullable final IErrorLevel aLevel1, @Nullable final IErrorLevel aLevel2)
  {
    if (EqualsHelper.identityEqual (aLevel1, aLevel2))
      return aLevel1;
    if (aLevel1 == null)
      return aLevel2;
    if (aLevel2 == null)
      return aLevel1;
    return aLevel1.isGT (aLevel2) ? aLevel1 : aLevel2;
  }
}
