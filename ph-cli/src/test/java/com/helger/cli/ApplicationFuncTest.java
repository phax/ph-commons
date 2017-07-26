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
import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;

import org.junit.Test;

import com.helger.cli.ex.CommandLineParseException;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.stream.NonBlockingStringWriter;

/**
 * This is a collection of tests that test real world applications command
 * lines.
 * <p>
 * The following applications are tested:
 * <ul>
 * <li>ls</li>
 * <li>Ant</li>
 * <li>Groovy</li>
 * <li>man</li>
 * </ul>
 * </p>
 */
public final class ApplicationFuncTest
{
  @Test
  public void testLs () throws Exception
  {
    // create the command line parser
    final ICommandLineParser parser = new CommandLineParser ();
    final Options options = new Options ();
    options.addOption (Option.builder ("a").longOpt ("all").desc ("do not hide entries starting with .").build ());
    options.addOption (Option.builder ("A").longOpt ("almost-all").desc ("do not list implied . and ..").build ());
    options.addOption (Option.builder ("b")
                             .longOpt ("escape")
                             .desc ("print octal escapes for nongraphic characters")
                             .build ());
    options.addOption (Option.builder ()
                             .longOpt ("block-size")
                             .desc ("use SIZE-byte blocks")
                             .oneArg ()
                             .argName ("SIZE")
                             .build ());
    options.addOption (Option.builder ("B")
                             .longOpt ("ignore-backups")
                             .desc ("do not list implied entried ending with ~")
                             .build ());
    options.addOption (Option.builder ("c")
                             .desc ("with -lt: sort by, and show, ctime (time of last modification of file status information) with -l:show ctime and sort by name otherwise: sort by ctime")
                             .build ());
    options.addOption (Option.builder ("C").desc ("list entries by columns").build ());

    final String [] args = new String [] { "--block-size=10" };

    final CommandLine line = parser.parse (options, args);
    assertTrue (line.hasOption ("block-size"));
    assertEquals (line.getOptionValue ("block-size"), "10");
  }

  /**
   * Ant test
   *
   * @throws CommandLineParseException
   *         on error
   */
  @Test
  public void testAnt () throws CommandLineParseException
  {
    // use the GNU parser
    final ICommandLineParser parser = new CommandLineParser ();
    final Options options = new Options ();
    options.addOption (Option.builder ("help").desc ("print this message").build ());
    options.addOption (Option.builder ("projecthelp").desc ("print project help information").build ());
    options.addOption (Option.builder ("version").desc ("print the version information and exit").build ());
    options.addOption (Option.builder ("quiet").desc ("be extra quiet").build ());
    options.addOption (Option.builder ("verbose").desc ("be extra verbose").build ());
    options.addOption (Option.builder ("debug").desc ("print debug information").build ());
    options.addOption (Option.builder ("logfile").oneArg ().desc ("use given file for log").build ());
    options.addOption (Option.builder ("logger").oneArg ().desc ("the class which is to perform the logging").build ());
    options.addOption (Option.builder ("listener")
                             .oneArg ()
                             .desc ("add an instance of a class as a project listener")
                             .build ());
    options.addOption (Option.builder ("buildfile").oneArg ().desc ("use given buildfile").build ());
    options.addOption (Option.builder ('D')
                             .desc ("use value for given property")
                             .unlimitedArgs ()
                             .valueSeparator ()
                             .build ());
    // ).required().desc ().desc( true .build());
    options.addOption (Option.builder ("find")
                             .oneArg ()
                             .desc ("search for buildfile towards the root of the filesystem and use it")
                             .build ());

    final String [] args = new String [] { "-buildfile",
                                           "mybuild.xml",
                                           "-Dproperty=value",
                                           "-Dproperty1=value1",
                                           "-projecthelp" };

    final CommandLine line = parser.parse (options, args);

    // check multiple values
    final ICommonsList <String> opts = line.getAllOptionValues ("D");
    assertEquals ("property", opts.get (0));
    assertEquals ("value", opts.get (1));
    assertEquals ("property1", opts.get (2));
    assertEquals ("value1", opts.get (3));

    // check single value
    assertEquals (line.getOptionValue ("buildfile"), "mybuild.xml");

    // check option
    assertTrue (line.hasOption ("projecthelp"));
  }

  @Test
  public void testGroovy () throws Exception
  {
    final Options options = new Options ();

    options.addOption (Option.builder ('D')
                             .longOpt ("define")
                             .desc ("define a system property")
                             .oneArg ()
                             .argName ("name=value")
                             .build ());
    options.addOption (Option.builder ('h').desc ("usage information").longOpt ("help").build ());
    options.addOption (Option.builder ('d')
                             .desc ("debug mode will print out full stack traces")
                             .longOpt ("debug")
                             .build ());
    options.addOption (Option.builder ('v').desc ("display the Groovy and JVM versions").longOpt ("version").build ());
    options.addOption (Option.builder ('c')
                             .argName ("charset")
                             .oneArg ()
                             .desc ("specify the encoding of the files")
                             .longOpt ("encoding")
                             .build ());
    options.addOption (Option.builder ('e')
                             .argName ("script")
                             .oneArg ()
                             .desc ("specify a command line script")
                             .build ());
    options.addOption (Option.builder ('i')
                             .argName ("extension")
                             .oneOptionalArg ()
                             .desc ("modify files in place; create backup if extension is given (e.g. \'.bak\')")
                             .build ());
    options.addOption (Option.builder ('n')
                             .desc ("process files line by line using implicit 'line' variable")
                             .build ());
    options.addOption (Option.builder ('p')
                             .desc ("process files line by line and print result (see also -n)")
                             .build ());
    options.addOption (Option.builder ('l')
                             .argName ("port")
                             .oneOptionalArg ()
                             .desc ("listen on a port and process inbound lines")
                             .build ());
    options.addOption (Option.builder ('a')
                             .argName ("splitPattern")
                             .oneOptionalArg ()
                             .desc ("split lines using splitPattern (default '\\s') using implicit 'split' variable")
                             .longOpt ("autosplit")
                             .build ());

    final ICommandLineParser parser = new CommandLineParser ();
    final CommandLine line = parser.parse (options, new String [] { "-e", "println 'hello'" }, true);

    assertTrue (line.hasOption ('e'));
    assertEquals ("println 'hello'", line.getOptionValue ('e'));
  }

  /**
   * author Slawek Zachcial
   */
  @Test
  public void testMan ()
  {
    final String cmdLine = "man [-c|-f|-k|-w|-tZT device] [-adlhu7V] [-Mpath] [-Ppager] [-Slist] " +
                           "[-msystem] [-pstring] [-Llocale] [-eextension] [section] page ...";
    final Options options = new Options ();
    options.addOption (Option.builder ("a").longOpt ("all").desc ("find all matching manual pages.").build ());
    options.addOption (Option.builder ("d").longOpt ("debug").desc ("emit debugging messages.").build ());
    options.addOption (Option.builder ("e")
                             .longOpt ("extension")
                             .desc ("limit search to extension type 'extension'.")
                             .build ());
    options.addOption (Option.builder ("f").longOpt ("whatis").desc ("equivalent to whatis.").build ());
    options.addOption (Option.builder ("k").longOpt ("apropos").desc ("equivalent to apropos.").build ());
    options.addOption (Option.builder ("w")
                             .longOpt ("location")
                             .desc ("print physical location of man page(s).")
                             .build ());
    options.addOption (Option.builder ("l")
                             .longOpt ("local-file")
                             .desc ("interpret 'page' argument(s) as local filename(s)")
                             .build ());
    options.addOption (Option.builder ("u").longOpt ("update").desc ("force a cache consistency check.").build ());
    // FIXME - should generate -r,--prompt string
    options.addOption (Option.builder ("r")
                             .longOpt ("prompt")
                             .oneArg ()
                             .desc ("provide 'less' pager with prompt.")
                             .build ());
    options.addOption (Option.builder ("c")
                             .longOpt ("catman")
                             .desc ("used by catman to reformat out of date cat pages.")
                             .build ());
    options.addOption (Option.builder ("7")
                             .longOpt ("ascii")
                             .desc ("display ASCII translation or certain latin1 chars.")
                             .build ());
    options.addOption (Option.builder ("t").longOpt ("troff").desc ("use troff format pages.").build ());
    // FIXME - should generate -T,--troff-device device
    options.addOption (Option.builder ("T")
                             .longOpt ("troff-device")
                             .oneArg ()
                             .desc ("use groff with selected device.")
                             .build ());
    options.addOption (Option.builder ("Z").longOpt ("ditroff").desc ("use groff with selected device.").build ());
    options.addOption (Option.builder ("D")
                             .longOpt ("default")
                             .desc ("reset all options to their default values.")
                             .build ());
    // FIXME - should generate -M,--manpath path
    options.addOption (Option.builder ("M")
                             .longOpt ("manpath")
                             .oneArg ()
                             .desc ("set search path for manual pages to 'path'.")
                             .build ());
    // FIXME - should generate -P,--pager pager
    options.addOption (Option.builder ("P")
                             .longOpt ("pager")
                             .oneArg ()
                             .desc ("use program 'pager' to display output.")
                             .build ());
    // FIXME - should generate -S,--sections list
    options.addOption (Option.builder ("S")
                             .longOpt ("sections")
                             .oneArg ()
                             .desc ("use colon separated section list.")
                             .build ());
    // FIXME - should generate -m,--systems system
    options.addOption (Option.builder ("m")
                             .longOpt ("systems")
                             .oneArg ()
                             .desc ("search for man pages from other unix system(s).")
                             .build ());
    // FIXME - should generate -L,--locale locale
    options.addOption (Option.builder ("L")
                             .longOpt ("locale")
                             .oneArg ()
                             .desc ("define the locale for this particular man search.")
                             .build ());
    // FIXME - should generate -p,--preprocessor string
    options.addOption (Option.builder ("p")
                             .longOpt ("preprocessor")
                             .oneArg ()
                             .desc ("string indicates which preprocessor to run.\n" +
                                    " e - [n]eqn  p - pic     t - tbl\n" +
                                    " g - grap    r - refer   v - vgrind")
                             .build ());
    options.addOption (Option.builder ("V").longOpt ("version").desc ("show version.").build ());
    options.addOption (Option.builder ("h").longOpt ("help").desc ("show this usage message.").build ());

    final HelpFormatter hf = new HelpFormatter ();
    final String EOL = System.getProperty ("line.separator");
    final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
    hf.printHelp (new PrintWriter (aSW),
                  60,
                  cmdLine,
                  null,
                  options,
                  HelpFormatter.DEFAULT_LEFT_PAD,
                  HelpFormatter.DEFAULT_DESC_PAD,
                  null,
                  false);
    assertEquals ("usage: man [-c|-f|-k|-w|-tZT device] [-adlhu7V] [-Mpath]" +
                  EOL +
                  "           [-Ppager] [-Slist] [-msystem] [-pstring]" +
                  EOL +
                  "           [-Llocale] [-eextension] [section] page ..." +
                  EOL +
                  " -7,--ascii                display ASCII translation or" +
                  EOL +
                  "                           certain latin1 chars." +
                  EOL +
                  " -a,--all                  find all matching manual pages." +
                  EOL +
                  " -c,--catman               used by catman to reformat out of" +
                  EOL +
                  "                           date cat pages." +
                  EOL +
                  " -d,--debug                emit debugging messages." +
                  EOL +
                  " -D,--default              reset all options to their" +
                  EOL +
                  "                           default values." +
                  EOL +
                  " -e,--extension            limit search to extension type" +
                  EOL +
                  "                           'extension'." +
                  EOL +
                  " -f,--whatis               equivalent to whatis." +
                  EOL +
                  " -h,--help                 show this usage message." +
                  EOL +
                  " -k,--apropos              equivalent to apropos." +
                  EOL +
                  " -l,--local-file           interpret 'page' argument(s) as" +
                  EOL +
                  "                           local filename(s)" +
                  EOL +
                  " -L,--locale <arg>         define the locale for this" +
                  EOL +
                  "                           particular man search." +
                  EOL +
                  " -M,--manpath <arg>        set search path for manual pages" +
                  EOL +
                  "                           to 'path'." +
                  EOL +
                  " -m,--systems <arg>        search for man pages from other" +
                  EOL +
                  "                           unix system(s)." +
                  EOL +
                  " -P,--pager <arg>          use program 'pager' to display" +
                  EOL +
                  "                           output." +
                  EOL +
                  " -p,--preprocessor <arg>   string indicates which" +
                  EOL +
                  "                           preprocessor to run." +
                  EOL +
                  "                           e - [n]eqn  p - pic     t - tbl" +
                  EOL +
                  "                           g - grap    r - refer   v -" +
                  EOL +
                  "                           vgrind" +
                  EOL +
                  " -r,--prompt <arg>         provide 'less' pager with prompt." +
                  EOL +
                  " -S,--sections <arg>       use colon separated section list." +
                  EOL +
                  " -t,--troff                use troff format pages." +
                  EOL +
                  " -T,--troff-device <arg>   use groff with selected device." +
                  EOL +
                  " -u,--update               force a cache consistency check." +
                  EOL +
                  " -V,--version              show version." +
                  EOL +
                  " -w,--location             print physical location of man" +
                  EOL +
                  "                           page(s)." +
                  EOL +
                  " -Z,--ditroff              use groff with selected device." +
                  EOL,
                  aSW.getAsString ());
  }

  /**
   * Real world test with long and short options.
   *
   * @throws Exception
   *         on error
   */
  @Test
  public void testNLT () throws Exception
  {
    final Option help = Option.builder ("h").longOpt ("help").desc ("print this message").build ();
    final Option version = Option.builder ("v").longOpt ("version").desc ("print version information").build ();
    final Option newRun = Option.builder ("n")
                                .longOpt ("new")
                                .desc ("Create NLT cache entries only for new items")
                                .build ();
    final Option trackerRun = Option.builder ("t")
                                    .longOpt ("tracker")
                                    .desc ("Create NLT cache entries only for tracker items")
                                    .build ();

    final Option timeLimit = Option.builder ("l")
                                   .longOpt ("limit")
                                   .oneArg ()
                                   .valueSeparator ()
                                   .desc ("Set time limit for execution, in minutes")
                                   .build ();

    final Option age = Option.builder ("a")
                             .longOpt ("age")
                             .oneArg ()
                             .valueSeparator ()
                             .desc ("Age (in days) of cache item before being recomputed")
                             .build ();

    final Option server = Option.builder ("s")
                                .longOpt ("server")
                                .oneArg ()
                                .valueSeparator ()
                                .desc ("The NLT server address")
                                .build ();

    final Option numResults = Option.builder ("r")
                                    .longOpt ("results")
                                    .oneArg ()
                                    .valueSeparator ()
                                    .desc ("Number of results per item")
                                    .build ();

    final Option configFile = Option.builder ()
                                    .longOpt ("file")
                                    .oneArg ()
                                    .valueSeparator ()
                                    .desc ("Use the specified configuration file")
                                    .build ();

    final Options options = new Options ();
    options.addOption (help);
    options.addOption (version);
    options.addOption (newRun);
    options.addOption (trackerRun);
    options.addOption (timeLimit);
    options.addOption (age);
    options.addOption (server);
    options.addOption (numResults);
    options.addOption (configFile);

    // create the command line parser
    final ICommandLineParser parser = new CommandLineParser ();

    final String [] args = new String [] { "-v", "-l", "10", "-age", "5", "-file", "filename" };

    final CommandLine line = parser.parse (options, args);
    assertTrue (line.hasOption ("v"));
    assertEquals (line.getOptionValue ("l"), "10");
    assertEquals (line.getOptionValue ("limit"), "10");
    assertEquals (line.getOptionValue ("a"), "5");
    assertEquals (line.getOptionValue ("age"), "5");
    assertEquals (line.getOptionValue ("file"), "filename");
  }
}
