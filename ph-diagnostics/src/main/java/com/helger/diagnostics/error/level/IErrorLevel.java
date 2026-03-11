/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.diagnostics.error.level;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.MustImplementEqualsAndHashcode;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.id.IHasID;
import com.helger.base.state.IErrorIndicator;
import com.helger.base.state.ISuccessIndicator;
import com.helger.diagnostics.severity.ISeverityComparable;

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
   * @return The numeric level of this error level. Must be &ge; 0. The higher the numeric level,
   *         the higher the priority of the error level. So e.g. ERROR has a higher/larger/greater
   *         numerical level than WARNING.
   */
  @Nonnegative
  int getNumericLevel ();

  /** {@inheritDoc} */
  default boolean isSuccess ()
  {
    return isEQ (EErrorLevel.SUCCESS);
  }

  /** {@inheritDoc} */
  @Override
  default boolean isFailure ()
  {
    return isGT (EErrorLevel.SUCCESS);
  }

  /** {@inheritDoc} */
  default boolean isError ()
  {
    return isGE (EErrorLevel.ERROR);
  }

  /** {@inheritDoc} */
  @Override
  default boolean isNoError ()
  {
    return isLT (EErrorLevel.ERROR);
  }

  /** {@inheritDoc} */
  default int compareTo (@NonNull final IErrorLevel aErrorLevel)
  {
    return Integer.compare (getNumericLevel (), aErrorLevel.getNumericLevel ());
  }

  /** {@inheritDoc} */
  default boolean isEQ (@NonNull final IErrorLevel aErrorLevel)
  {
    return getNumericLevel () == aErrorLevel.getNumericLevel ();
  }

  /** {@inheritDoc} */
  @Override
  default boolean isNE (@NonNull final IErrorLevel aErrorLevel)
  {
    return getNumericLevel () != aErrorLevel.getNumericLevel ();
  }

  /** {@inheritDoc} */
  default boolean isLT (@NonNull final IErrorLevel aErrorLevel)
  {
    return getNumericLevel () < aErrorLevel.getNumericLevel ();
  }

  /** {@inheritDoc} */
  default boolean isLE (@NonNull final IErrorLevel aErrorLevel)
  {
    return getNumericLevel () <= aErrorLevel.getNumericLevel ();
  }

  /** {@inheritDoc} */
  default boolean isGT (@NonNull final IErrorLevel aErrorLevel)
  {
    return getNumericLevel () > aErrorLevel.getNumericLevel ();
  }

  /** {@inheritDoc} */
  default boolean isGE (@NonNull final IErrorLevel aErrorLevel)
  {
    return getNumericLevel () >= aErrorLevel.getNumericLevel ();
  }

  /**
   * @return <code>true</code> if this error level is the highest possible level,
   *         <code>false</code> otherwise.
   */
  default boolean isHighest ()
  {
    return isEQ (EErrorLevel.HIGHEST);
  }

  /**
   * Get the more severe of two error levels.
   *
   * @param aLevel1
   *        The first error level. May be <code>null</code>.
   * @param aLevel2
   *        The second error level. May be <code>null</code>.
   * @return The more severe error level, or <code>null</code> if both are <code>null</code>.
   */
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
