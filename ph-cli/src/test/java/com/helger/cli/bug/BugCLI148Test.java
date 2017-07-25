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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.helger.cli.CommandLine;
import com.helger.cli.CommandLineParser;
import com.helger.cli.ICommandLineParser;
import com.helger.cli.Option;
import com.helger.cli.Options;

/**
 * http://issues.apache.org/jira/browse/CLI-148
 */
public final class BugCLI148Test
{
  private Options m_aOptions;

  @Before
  public void setUp () throws Exception
  {
    m_aOptions = new Options ();
    m_aOptions.addOption (Option.builder ('t').oneArg ().build ());
    m_aOptions.addOption (Option.builder ('s').oneArg ().build ());
  }

  @Test
  public void testWorkaround1 () throws Exception
  {
    final ICommandLineParser parser = new CommandLineParser ();
    final String [] args = new String [] { "-t-something" };

    final CommandLine commandLine = parser.parse (m_aOptions, args);
    assertEquals ("-something", commandLine.getOptionValue ('t'));
  }

  @Test
  public void testWorkaround2 () throws Exception
  {
    final ICommandLineParser parser = new CommandLineParser ();
    final String [] args = new String [] { "-t", "\"-something\"" };

    final CommandLine commandLine = parser.parse (m_aOptions, args);
    assertEquals ("-something", commandLine.getOptionValue ('t'));
  }
}
