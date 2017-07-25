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
package com.helger.cli.bug;

import org.junit.Test;

import com.helger.cli.CommandLineParser;
import com.helger.cli.Option;
import com.helger.cli.Options;
import com.helger.cli.ex.AmbiguousOptionException;
import com.helger.cli.ex.CommandLineParseException;

public final class BugCLI252Test
{
  @Test
  public void testExactOptionNameMatch () throws CommandLineParseException
  {
    new CommandLineParser ().parse (getOptions (), new String [] { "--prefix" });
  }

  @Test (expected = AmbiguousOptionException.class)
  public void testAmbiquousOptionName () throws CommandLineParseException
  {
    new CommandLineParser ().parse (getOptions (), new String [] { "--pref" });
  }

  private Options getOptions ()
  {
    final Options options = new Options ();
    options.addOption (Option.builder ().longOpt ("prefix").build ());
    options.addOption (Option.builder ().longOpt ("prefixplusplus").build ());
    return options;
  }

}
