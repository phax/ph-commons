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
package com.helger.commons.exception;

import javax.annotation.Nullable;

/**
 * This class should be used when an error occurs in the initialization phase
 * (e.g. in static blocks of classes).
 *
 * @author Philip Helger
 */
public class InitializationException extends RuntimeException
{
  public InitializationException ()
  {
    super ();
  }

  public InitializationException (@Nullable final String sMsg)
  {
    super (sMsg);
  }

  public InitializationException (@Nullable final Throwable t)
  {
    super (t);
  }

  public InitializationException (@Nullable final String sMsg, @Nullable final Throwable t)
  {
    super (sMsg, t);
  }
}
