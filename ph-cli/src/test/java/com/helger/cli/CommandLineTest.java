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
package com.helger.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.helger.commons.lang.NonBlockingProperties;

public final class CommandLineTest
{
  @Test
  public void testGetOptionProperties () throws Exception
  {
    final String [] args = new String [] { "-Dparam1=value1",
                                           "-Dparam2=value2",
                                           "-Dparam3",
                                           "-Dparam4=value4",
                                           "-D",
                                           "--property",
                                           "foo=bar" };

    final Options options = new Options ();
    options.addOption (Option.builder ('D').valueSeparator ().optionalNumberOfArgs (2).build ());
    options.addOption (Option.builder ().longOpt ("property").valueSeparator ().numberOfArgs (2).build ());

    final ICommandLineParser parser = new CommandLineParser ();
    final CommandLine cl = parser.parse (options, args);

    final NonBlockingProperties props = cl.getOptionProperties ("D");
    assertNotNull ("null properties", props);
    assertEquals ("number of properties in " + props, 4, props.size ());
    assertEquals ("property 1", "value1", props.getProperty ("param1"));
    assertEquals ("property 2", "value2", props.getProperty ("param2"));
    assertEquals ("property 3", "true", props.getProperty ("param3"));
    assertEquals ("property 4", "value4", props.getProperty ("param4"));

    assertEquals ("property with long format", "bar", cl.getOptionProperties ("property").getProperty ("foo"));
  }

  @Test
  public void testGetOptions ()
  {
    final CommandLine cmd = new CommandLine ();
    assertNotNull (cmd.getAllOptions ());
    assertEquals (0, cmd.getAllOptions ().size ());

    cmd.addOption (Option.builder ("a").build ());
    cmd.addOption (Option.builder ("b").build ());
    cmd.addOption (Option.builder ("c").build ());

    assertEquals (3, cmd.getAllOptions ().size ());
  }

  @Test
  public void testGetParsedOptionValue () throws Exception
  {
    final Options options = new Options ();
    options.addOption (Option.builder ("i").oneArg ().build ());
    options.addOption (Option.builder ("f").oneArg ().build ());

    final ICommandLineParser parser = new CommandLineParser ();
    final CommandLine cmd = parser.parse (options, new String [] { "-i", "123", "-f", "foo" });

    assertEquals (123, cmd.values ().getAsInt ("i"));
    assertEquals ("foo", cmd.values ().getAsString ("f"));
  }
}
