/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.commons.supplementary.test;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MainFileOnWindows
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainFileOnWindows.class);

  private MainFileOnWindows ()
  {}

  public static void main (final String [] args)
  {
    final File fBase = new File ("f:\\test\\bla.txt");
    File f = fBase;
    while (f != null)
    {
      LOGGER.info ("'" + f.getName () + "'");
      LOGGER.info ("  '" + f.getPath () + "'");
      f = f.getParentFile ();
    }
  }
}
