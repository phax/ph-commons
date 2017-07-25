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

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.helger.cli.CommandLine;
import com.helger.cli.CommandLineParser;
import com.helger.cli.Option;
import com.helger.cli.Options;
import com.helger.cli.ex.CommandLineParseException;

public final class BugCLI133Test
{
  @Test
  public void testOrder () throws CommandLineParseException
  {
    final Option optionA = Option.builder ("a").desc ("first").build ();
    final Options opts = new Options ();
    opts.addOption (optionA);
    final CommandLineParser posixParser = new CommandLineParser ();
    final CommandLine line = posixParser.parse (opts, null);
    assertFalse (line.hasOption (null));
  }
}
