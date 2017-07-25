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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import com.helger.cli.CommandLine;
import com.helger.cli.CommandLineParser;
import com.helger.cli.HelpFormatter;
import com.helger.cli.ICommandLineParser;
import com.helger.cli.Option;
import com.helger.cli.OptionGroup;
import com.helger.cli.Options;
import com.helger.cli.ex.CommandLineParseException;
import com.helger.cli.ex.MissingArgumentException;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.lang.NonBlockingProperties;

public final class BugsTest
{
  @Test
  public void test11457 () throws Exception
  {
    final Options options = new Options ();
    options.addOption (Option.builder ().longOpt ("verbose").build ());
    final String [] args = new String [] { "--verbose" };

    final ICommandLineParser parser = new CommandLineParser ();

    final CommandLine cmd = parser.parse (options, args);
    assertTrue (cmd.hasOption ("verbose"));
  }

  @Test
  public void test11458 () throws Exception
  {
    final Options options = new Options ();
    options.addOption (Option.builder ('D').valueSeparator ('=').unlimitedArgs ().build ());
    options.addOption (Option.builder ('p').valueSeparator (':').unlimitedArgs ().build ());
    final String [] args = new String [] { "-DJAVA_HOME=/opt/java", "-pfile1:file2:file3" };

    final ICommandLineParser parser = new CommandLineParser ();

    final CommandLine cmd = parser.parse (options, args);

    ICommonsList <String> values = cmd.getAllOptionValues ('D');

    assertEquals (values.get (0), "JAVA_HOME");
    assertEquals (values.get (1), "/opt/java");

    values = cmd.getAllOptionValues ('p');

    assertEquals (values.get (0), "file1");
    assertEquals (values.get (1), "file2");
    assertEquals (values.get (2), "file3");

    for (final Option opt : cmd)
    {
      switch (opt.getKey ().charAt (0))
      {
        case 'D':
          assertEquals (opt.getValue (0), "JAVA_HOME");
          assertEquals (opt.getValue (1), "/opt/java");
          break;
        case 'p':
          assertEquals (opt.getValue (0), "file1");
          assertEquals (opt.getValue (1), "file2");
          assertEquals (opt.getValue (2), "file3");
          break;
        default:
          fail ("-D option not found");
      }
    }
  }

  @Test
  public void test11680 () throws Exception
  {
    final Options options = new Options ();
    options.addOption (Option.builder ("f").oneArg ().desc ("foobar").build ());
    options.addOption (Option.builder ("m").oneArg ().desc ("missing").build ());
    final String [] args = new String [] { "-f", "foo" };

    final ICommandLineParser parser = new CommandLineParser ();

    final CommandLine cmd = parser.parse (options, args);

    cmd.getOptionValue ("f", "default f");
    cmd.getOptionValue ("m", "default m");
  }

  @Test
  public void test11456 () throws Exception
  {
    // Posix
    Options options = new Options ();
    options.addOption (Option.builder ('a').oneOptionalArg ().build ());
    options.addOption (Option.builder ('b').oneArg ().build ());
    String [] args = new String [] { "-a", "-bvalue" };

    ICommandLineParser parser = new CommandLineParser ();

    CommandLine cmd = parser.parse (options, args);
    assertEquals (cmd.getOptionValue ('b'), "value");

    // GNU
    options = new Options ();
    options.addOption (Option.builder ('a').oneOptionalArg ().build ());
    options.addOption (Option.builder ('b').oneArg ().build ());
    args = new String [] { "-a", "-b", "value" };

    parser = new CommandLineParser ();

    cmd = parser.parse (options, args);
    assertEquals (cmd.getOptionValue ('b'), "value");
  }

  @Test
  public void test12210 () throws Exception
  {
    // create the main options object which will handle the first parameter
    final Options mainOptions = new Options ();
    // There can be 2 main exclusive options: -exec|-rep

    // Therefore, place them in an option group

    String [] argv = new String [] { "-exec", "-exec_opt1", "-exec_opt2" };

    final OptionGroup grp = new OptionGroup ();
    grp.addOption (Option.builder ("exec").desc ("description for this option").build ());
    grp.addOption (Option.builder ("rep").desc ("description for this option").build ());

    mainOptions.addOptionGroup (grp);

    // for the exec option, there are 2 options...
    final Options execOptions = new Options ();
    execOptions.addOption (Option.builder ("exec_opt1").desc (" desc").build ());
    execOptions.addOption (Option.builder ("exec_opt2").desc (" desc").build ());

    // similarly, for rep there are 2 options...
    final Options repOptions = new Options ();
    repOptions.addOption (Option.builder ("repopto").desc ("desc").build ());
    repOptions.addOption (Option.builder ("repoptt").desc ("desc").build ());

    // create the parser
    final CommandLineParser parser = new CommandLineParser ();

    // finally, parse the arguments:

    // first parse the main options to see what the user has specified
    // We set stopAtNonOption to true so it does not touch the remaining
    // options
    CommandLine cmd = parser.parse (mainOptions, argv, true);
    // get the remaining options...
    argv = cmd.getArgs ();

    if (cmd.hasOption ("exec"))
    {
      cmd = parser.parse (execOptions, argv, false);
      // process the exec_op1 and exec_opt2...
      assertTrue (cmd.hasOption ("exec_opt1"));
      assertTrue (cmd.hasOption ("exec_opt2"));
    }
    else
      if (cmd.hasOption ("rep"))
      {
        cmd = parser.parse (repOptions, argv, false);
        // process the rep_op1 and rep_opt2...
      }
      else
      {
        fail ("exec option not found");
      }
  }

  @Test
  public void test13425 () throws Exception
  {
    final Options options = new Options ();
    final Option oldpass = Option.builder ('o')
                                 .longOpt ("old-password")
                                 .desc ("Use this option to specify the old password")
                                 .oneArg ()
                                 .build ();
    final Option newpass = Option.builder ('n')
                                 .longOpt ("new-password")
                                 .desc ("Use this option to specify the new password")
                                 .oneArg ()
                                 .build ();

    final String [] args = { "-o", "-n", "newpassword" };

    options.addOption (oldpass);
    options.addOption (newpass);

    final CommandLineParser parser = new CommandLineParser ();

    try
    {
      parser.parse (options, args);
      fail ("MissingArgumentException not caught.");
    }
    catch (final MissingArgumentException expected)
    {}
  }

  @Test
  public void test13666 () throws Exception
  {
    final Options options = new Options ();
    final Option dir = Option.builder ('d').desc ("dir").oneArg ().build ();
    options.addOption (dir);

    final PrintStream oldSystemOut = System.out;
    try
    {
      final ByteArrayOutputStream bytes = new ByteArrayOutputStream ();
      final PrintStream print = new PrintStream (bytes);

      // capture this platform's eol symbol
      print.println ();
      final String eol = bytes.toString ();
      bytes.reset ();

      System.setOut (new PrintStream (bytes));

      final HelpFormatter formatter = new HelpFormatter ();
      formatter.printHelp ("dir", options);

      assertEquals ("usage: dir" + eol + " -d <arg>   dir" + eol, bytes.toString ());
    }
    finally
    {
      System.setOut (oldSystemOut);
    }
  }

  @Test
  public void test13935 () throws Exception
  {
    final OptionGroup directions = new OptionGroup ();

    final Option left = Option.builder ("l").longOpt ("left").desc ("go left").build ();
    final Option right = Option.builder ("r").longOpt ("right").desc ("go right").build ();
    final Option straight = Option.builder ("s").longOpt ("straight").desc ("go straight").build ();
    final Option forward = Option.builder ("f").longOpt ("forward").desc ("go forward").required ().build ();

    directions.addOption (left);
    directions.addOption (right);
    directions.setRequired (true);

    final Options opts = new Options ();
    opts.addOptionGroup (directions);
    opts.addOption (straight);

    final ICommandLineParser parser = new CommandLineParser ();

    String [] args = new String [] {};
    try
    {
      parser.parse (opts, args);
      fail ("Expected ParseException");
    }
    catch (final CommandLineParseException expected)
    {}

    args = new String [] { "-s" };
    try
    {
      parser.parse (opts, args);
      fail ("Expected ParseException");
    }
    catch (final CommandLineParseException expected)
    {}

    args = new String [] { "-s", "-l" };
    CommandLine line = parser.parse (opts, args);
    assertNotNull (line);

    opts.addOption (forward);
    args = new String [] { "-s", "-l", "-f" };
    line = parser.parse (opts, args);
    assertNotNull (line);
  }

  @Test
  public void test14786 () throws Exception
  {
    final Option o = Option.builder ("test").required ().desc ("test").build ();
    final Options opts = new Options ();
    opts.addOption (o);
    opts.addOption (o);

    final ICommandLineParser parser = new CommandLineParser ();

    final String [] args = new String [] { "-test" };

    final CommandLine line = parser.parse (opts, args);
    assertTrue (line.hasOption ("test"));
  }

  @Test
  public void test15046 () throws Exception
  {
    final ICommandLineParser parser = new CommandLineParser ();
    final String [] CLI_ARGS = new String [] { "-z", "c" };

    final Options options = new Options ();
    options.addOption (Option.builder ("z").longOpt ("timezone").oneArg ().desc ("affected option").build ());

    parser.parse (options, CLI_ARGS);

    // now add conflicting option
    options.addOption (Option.builder ("c").longOpt ("conflict").oneArg ().desc ("conflict option").build ());
    final CommandLine line = parser.parse (options, CLI_ARGS);
    assertEquals (line.getOptionValue ('z'), "c");
    assertTrue (!line.hasOption ("c"));
  }

  @Test
  public void test15648 () throws Exception
  {
    final ICommandLineParser parser = new CommandLineParser ();
    final String [] args = new String [] { "-m", "\"Two Words\"" };
    final Option m = Option.builder ("m").unlimitedArgs ().build ();
    final Options options = new Options ();
    options.addOption (m);
    final CommandLine line = parser.parse (options, args);
    assertEquals ("Two Words", line.getOptionValue ("m"));
  }

  @Test
  public void test31148 () throws CommandLineParseException
  {
    final Option multiArgOption = Option.builder ("o").desc ("option with multiple args").oneArg ().build ();

    final Options options = new Options ();
    options.addOption (multiArgOption);

    final CommandLineParser parser = new CommandLineParser ();
    final String [] args = new String [] {};
    final NonBlockingProperties props = new NonBlockingProperties ();
    props.setProperty ("o", "ovalue");
    final CommandLine cl = parser.parse (options, args, props);

    assertTrue (cl.hasOption ('o'));
    assertEquals ("ovalue", cl.getOptionValue ('o'));
  }

}
