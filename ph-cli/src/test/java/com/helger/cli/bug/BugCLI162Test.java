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

import java.io.PrintWriter;
import java.sql.ParameterMetaData;
import java.sql.Types;

import org.junit.Before;
import org.junit.Test;

import com.helger.cli.HelpFormatter;
import com.helger.cli.Option;
import com.helger.cli.OptionGroup;
import com.helger.cli.Options;
import com.helger.commons.io.stream.NonBlockingStringWriter;

public final class BugCLI162Test
{
  /** Constant for the line separator. */
  private static final String CR = System.getProperty ("line.separator");

  private HelpFormatter m_aFormatter;
  private NonBlockingStringWriter m_aSW;

  @Before
  public void setUp () throws Exception
  {
    m_aFormatter = new HelpFormatter ();
    m_aSW = new NonBlockingStringWriter ();
  }

  @Test
  public void testInfiniteLoop ()
  {
    final Options options = new Options ();
    options.addOption (Option.builder ("h").longOpt ("help").desc ("This is a looooong description").build ());
    // used to hang & crash
    m_aFormatter.printHelp (new PrintWriter (m_aSW),
                            20,
                            "app",
                            null,
                            options,
                            HelpFormatter.DEFAULT_LEFT_PAD,
                            HelpFormatter.DEFAULT_DESC_PAD,
                            null);

    final String expected = "usage: app" +
                            CR +
                            " -h,--help   This is" +
                            CR +
                            "             a" +
                            CR +
                            "             looooon" +
                            CR +
                            "             g" +
                            CR +
                            "             descrip" +
                            CR +
                            "             tion" +
                            CR;
    assertEquals (expected, m_aSW.getAsString ());
  }

  @Test
  public void testPrintHelpLongLines ()
  {
    // Constants used for options
    final String OPT = "-";
    final String OPT_COLUMN_NAMES = "l";
    final String OPT_CONNECTION = "c";
    final String OPT_DESCRIPTION = "e";
    final String OPT_DRIVER = "d";
    final String OPT_DRIVER_INFO = "n";
    final String OPT_FILE_BINDING = "b";
    final String OPT_FILE_JDBC = "j";
    final String OPT_FILE_SFMD = "f";
    final String OPT_HELP = "h";
    final String OPT_HELP_ = "help";
    final String OPT_INTERACTIVE = "i";
    final String OPT_JDBC_TO_SFMD = "2";
    final String OPT_JDBC_TO_SFMD_L = "jdbc2sfmd";
    final String OPT_METADATA = "m";
    final String OPT_PARAM_MODES_INT = "o";
    final String OPT_PARAM_MODES_NAME = "O";
    final String OPT_PARAM_NAMES = "a";
    final String OPT_PARAM_TYPES_INT = "y";
    final String OPT_PARAM_TYPES_NAME = "Y";
    final String OPT_PASSWORD = "p";
    final String OPT_PASSWORD_L = "password";
    final String OPT_SQL = "s";
    final String OPT_SQL_L = "sql";
    final String OPT_STACK_TRACE = "t";
    final String OPT_TIMING = "g";
    final String OPT_TRIM_L = "trim";
    final String OPT_USER = "u";
    final String OPT_WRITE_TO_FILE = "w";
    final String _PMODE_IN = "IN";
    final String _PMODE_INOUT = "INOUT";
    final String _PMODE_OUT = "OUT";
    final String _PMODE_UNK = "Unknown";
    final String PMODES = _PMODE_IN + ", " + _PMODE_INOUT + ", " + _PMODE_OUT + ", " + _PMODE_UNK;

    // Options build
    Options commandLineOptions;
    commandLineOptions = new Options ();
    commandLineOptions.addOption (Option.builder (OPT_HELP)
                                        .longOpt (OPT_HELP_)
                                        .desc ("Prints help and quits")
                                        .build ());
    commandLineOptions.addOption (Option.builder (OPT_DRIVER)
                                        .longOpt ("driver")
                                        .oneArg ()
                                        .desc ("JDBC driver class name")
                                        .build ());
    commandLineOptions.addOption (Option.builder (OPT_DRIVER_INFO)
                                        .longOpt ("info")
                                        .desc ("Prints driver information and properties. If " +
                                               OPT +
                                               OPT_CONNECTION +
                                               " is not specified, all drivers on the classpath are displayed.")
                                        .build ());
    commandLineOptions.addOption (Option.builder (OPT_CONNECTION)
                                        .longOpt ("url")
                                        .oneArg ()
                                        .desc ("Connection URL")
                                        .build ());
    commandLineOptions.addOption (Option.builder (OPT_USER)
                                        .longOpt ("user")
                                        .oneArg ()
                                        .desc ("A database user name")
                                        .build ());
    commandLineOptions.addOption (Option.builder (OPT_PASSWORD)
                                        .longOpt (OPT_PASSWORD_L)
                                        .oneArg ()
                                        .desc ("The database password for the user specified with the " +
                                               OPT +
                                               OPT_USER +
                                               " option. You can obfuscate the password with org.mortbay.jetty.security.Password, see http://docs.codehaus.org/display/JETTY/Securing+Passwords")
                                        .build ());
    commandLineOptions.addOption (Option.builder (OPT_SQL)
                                        .longOpt (OPT_SQL_L)
                                        .oneArg ()
                                        .desc ("Runs SQL or {call stored_procedure(?, ?)} or {?=call function(?, ?)}")
                                        .build ());
    commandLineOptions.addOption (Option.builder (OPT_FILE_SFMD)
                                        .longOpt ("sfmd")
                                        .oneArg ()
                                        .desc ("Writes a SFMD file for the given SQL")
                                        .build ());
    commandLineOptions.addOption (Option.builder (OPT_FILE_BINDING)
                                        .longOpt ("jdbc")
                                        .oneArg ()
                                        .desc ("Writes a JDBC binding node file for the given SQL")
                                        .build ());
    commandLineOptions.addOption (Option.builder (OPT_FILE_JDBC)
                                        .longOpt ("node")
                                        .oneArg ()
                                        .desc ("Writes a JDBC node file for the given SQL (internal debugging)")
                                        .build ());
    commandLineOptions.addOption (Option.builder (OPT_WRITE_TO_FILE)
                                        .longOpt ("outfile")
                                        .oneArg ()
                                        .desc ("Writes the SQL output to the given file")
                                        .build ());
    commandLineOptions.addOption (Option.builder (OPT_DESCRIPTION)
                                        .longOpt ("description")
                                        .oneArg ()
                                        .desc ("SFMD description. A default description is used if omited. Example: " +
                                               OPT +
                                               OPT_DESCRIPTION +
                                               " \"Runs such and such\"")
                                        .build ());
    commandLineOptions.addOption (Option.builder (OPT_INTERACTIVE)
                                        .longOpt ("interactive")
                                        .desc ("Runs in interactive mode, reading and writing from the console, 'go' or '/' sends a statement")
                                        .build ());
    commandLineOptions.addOption (Option.builder (OPT_TIMING)
                                        .longOpt ("printTiming")
                                        .desc ("Prints timing information")
                                        .build ());
    commandLineOptions.addOption (Option.builder (OPT_METADATA)
                                        .longOpt ("printMetaData")
                                        .desc ("Prints metadata information")
                                        .build ());
    commandLineOptions.addOption (Option.builder (OPT_STACK_TRACE)
                                        .longOpt ("printStack")
                                        .desc ("Prints stack traces on errors")
                                        .build ());
    Option option = Option.builder (OPT_COLUMN_NAMES)
                          .longOpt ("columnNames")
                          .oneArg ()
                          .desc ("Column XML names; default names column labels. Example: " +
                                 OPT +
                                 OPT_COLUMN_NAMES +
                                 " \"cname1 cname2\"")
                          .build ();
    commandLineOptions.addOption (option);
    option = Option.builder (OPT_PARAM_NAMES)
                   .longOpt ("paramNames")
                   .oneArg ()
                   .desc ("Parameter XML names; default names are param1, param2, etc. Example: " +
                          OPT +
                          OPT_PARAM_NAMES +
                          " \"pname1 pname2\"")
                   .build ();
    commandLineOptions.addOption (option);
    //
    final OptionGroup pOutTypesOptionGroup = new OptionGroup ();
    final String pOutTypesOptionGroupDoc = OPT +
                                           OPT_PARAM_TYPES_INT +
                                           " and " +
                                           OPT +
                                           OPT_PARAM_TYPES_NAME +
                                           " are mutually exclusive.";
    final String typesClassName = Types.class.getName ();
    option = Option.builder (OPT_PARAM_TYPES_INT)
                   .longOpt ("paramTypes")
                   .oneArg ()
                   .desc ("Parameter types from " +
                          typesClassName +
                          ". " +
                          pOutTypesOptionGroupDoc +
                          " Example: " +
                          OPT +
                          OPT_PARAM_TYPES_INT +
                          " \"-10 12\"")
                   .build ();
    commandLineOptions.addOption (option);
    option = Option.builder (OPT_PARAM_TYPES_NAME)
                   .longOpt ("paramTypeNames")
                   .oneArg ()
                   .desc ("Parameter " +
                          typesClassName +
                          " names. " +
                          pOutTypesOptionGroupDoc +
                          " Example: " +
                          OPT +
                          OPT_PARAM_TYPES_NAME +
                          " \"CURSOR VARCHAR\"")
                   .build ();
    commandLineOptions.addOption (option);
    commandLineOptions.addOptionGroup (pOutTypesOptionGroup);
    //
    final OptionGroup modesOptionGroup = new OptionGroup ();
    final String modesOptionGroupDoc = OPT +
                                       OPT_PARAM_MODES_INT +
                                       " and " +
                                       OPT +
                                       OPT_PARAM_MODES_NAME +
                                       " are mutually exclusive.";
    option = Option.builder (OPT_PARAM_MODES_INT)
                   .longOpt ("paramModes")
                   .oneArg ()
                   .desc ("Parameters modes (" +
                          ParameterMetaData.parameterModeIn +
                          "=IN, " +
                          ParameterMetaData.parameterModeInOut +
                          "=INOUT, " +
                          ParameterMetaData.parameterModeOut +
                          "=OUT, " +
                          ParameterMetaData.parameterModeUnknown +
                          "=Unknown" +
                          "). " +
                          modesOptionGroupDoc +
                          " Example for 2 parameters, OUT and IN: " +
                          OPT +
                          OPT_PARAM_MODES_INT +
                          " \"" +
                          ParameterMetaData.parameterModeOut +
                          " " +
                          ParameterMetaData.parameterModeIn +
                          "\"")
                   .build ();
    modesOptionGroup.addOption (option);
    option = Option.builder (OPT_PARAM_MODES_NAME)
                   .longOpt ("paramModeNames")
                   .oneArg ()
                   .desc ("Parameters mode names (" +
                          PMODES +
                          "). " +
                          modesOptionGroupDoc +
                          " Example for 2 parameters, OUT and IN: " +
                          OPT +
                          OPT_PARAM_MODES_NAME +
                          " \"" +
                          _PMODE_OUT +
                          " " +
                          _PMODE_IN +
                          "\"")
                   .build ();
    modesOptionGroup.addOption (option);
    commandLineOptions.addOptionGroup (modesOptionGroup);
    option = Option.builder ()
                   .longOpt (OPT_TRIM_L)
                   .oneOptionalArg ()
                   .desc ("Trims leading and trailing spaces from all column values. Column XML names can be optionally specified to set which columns to trim.")
                   .build ();
    commandLineOptions.addOption (option);
    option = Option.builder (OPT_JDBC_TO_SFMD)
                   .longOpt (OPT_JDBC_TO_SFMD_L)
                   .numberOfArgs (2)
                   .desc ("Converts the JDBC file in the first argument to an SMFD file specified in the second argument.")
                   .build ();
    commandLineOptions.addOption (option);

    m_aFormatter.printHelp (new PrintWriter (m_aSW),
                            HelpFormatter.DEFAULT_WIDTH,
                            this.getClass ().getName (),
                            null,
                            commandLineOptions,
                            HelpFormatter.DEFAULT_LEFT_PAD,
                            HelpFormatter.DEFAULT_DESC_PAD,
                            null);
    final String expected = "usage: com.helger.cli.bug.BugCLI162Test" +
                            CR +
                            " -2,--jdbc2sfmd <arg>        Converts the JDBC file in the first argument" +
                            CR +
                            "                             to an SMFD file specified in the second" +
                            CR +
                            "                             argument." +
                            CR +
                            " -a,--paramNames <arg>       Parameter XML names; default names are" +
                            CR +
                            "                             param1, param2, etc. Example: -a \"pname1" +
                            CR +
                            "                             pname2\"" +
                            CR +
                            " -b,--jdbc <arg>             Writes a JDBC binding node file for the given" +
                            CR +
                            "                             SQL" +
                            CR +
                            " -c,--url <arg>              Connection URL" +
                            CR +
                            " -d,--driver <arg>           JDBC driver class name" +
                            CR +
                            " -e,--description <arg>      SFMD description. A default description is" +
                            CR +
                            "                             used if omited. Example: -e \"Runs such and" +
                            CR +
                            "                             such\"" +
                            CR +
                            " -f,--sfmd <arg>             Writes a SFMD file for the given SQL" +
                            CR +
                            " -g,--printTiming            Prints timing information" +
                            CR +
                            " -h,--help                   Prints help and quits" +
                            CR +
                            " -i,--interactive            Runs in interactive mode, reading and writing" +
                            CR +
                            "                             from the console, 'go' or '/' sends a" +
                            CR +
                            "                             statement" +
                            CR +
                            " -j,--node <arg>             Writes a JDBC node file for the given SQL" +
                            CR +
                            "                             (internal debugging)" +
                            CR +
                            " -l,--columnNames <arg>      Column XML names; default names column" +
                            CR +
                            "                             labels. Example: -l \"cname1 cname2\"" +
                            CR +
                            " -m,--printMetaData          Prints metadata information" +
                            CR +
                            " -n,--info                   Prints driver information and properties. If" +
                            CR +
                            "                             -c is not specified, all drivers on the" +
                            CR +
                            "                             classpath are displayed." +
                            CR +
                            " -o,--paramModes <arg>       Parameters modes (1=IN, 2=INOUT, 4=OUT," +
                            CR +
                            "                             0=Unknown). -o and -O are mutually exclusive." +
                            CR +
                            "                             Example for 2 parameters, OUT and IN: -o \"4" +
                            CR +
                            "                             1\"" +
                            CR +
                            " -O,--paramModeNames <arg>   Parameters mode names (IN, INOUT, OUT," +
                            CR +
                            "                             Unknown). -o and -O are mutually exclusive." +
                            CR +
                            "                             Example for 2 parameters, OUT and IN: -O \"OUT" +
                            CR +
                            "                             IN\"" +
                            CR +
                            " -p,--password <arg>         The database password for the user specified" +
                            CR +
                            "                             with the -u option. You can obfuscate the" +
                            CR +
                            "                             password with" +
                            CR +
                            "                             org.mortbay.jetty.security.Password, see" +
                            CR +
                            "                             http://docs.codehaus.org/display/JETTY/Securi" +
                            CR +
                            "                             ng+Passwords" +
                            CR +
                            " -s,--sql <arg>              Runs SQL or {call stored_procedure(?, ?)} or" +
                            CR +
                            "                             {?=call function(?, ?)}" +
                            CR +
                            " -t,--printStack             Prints stack traces on errors" +
                            CR +
                            "    --trim <arg>             Trims leading and trailing spaces from all" +
                            CR +
                            "                             column values. Column XML names can be" +
                            CR +
                            "                             optionally specified to set which columns to" +
                            CR +
                            "                             trim." +
                            CR +
                            " -u,--user <arg>             A database user name" +
                            CR +
                            " -w,--outfile <arg>          Writes the SQL output to the given file" +
                            CR +
                            " -y,--paramTypes <arg>       Parameter types from java.sql.Types. -y and" +
                            CR +
                            "                             -Y are mutually exclusive. Example: -y \"-10" +
                            CR +
                            "                             12\"" +
                            CR +
                            " -Y,--paramTypeNames <arg>   Parameter java.sql.Types names. -y and -Y are" +
                            CR +
                            "                             mutually exclusive. Example: -Y \"CURSOR" +
                            CR +
                            "                             VARCHAR\"" +
                            CR;
    assertEquals (expected, m_aSW.getAsString ());
  }

  @Test
  public void testLongLineChunking ()
  {
    final Options options = new Options ();
    options.addOption (Option.builder ("x")
                             .longOpt ("extralongarg")
                             .desc ("This description has ReallyLongValuesThatAreLongerThanTheWidthOfTheColumns " +
                                    "and also other ReallyLongValuesThatAreHugerAndBiggerThanTheWidthOfTheColumnsBob, " +
                                    "yes. ")
                             .build ());

    m_aFormatter.printHelp (new PrintWriter (m_aSW),
                            35,
                            this.getClass ().getName (),
                            "Header",
                            options,
                            0,
                            5,
                            "Footer");
    final String expected = "usage:" +
                            CR +
                            "       com.helger.cli.bug.BugCLI162" +
                            CR +
                            "       Test" +
                            CR +
                            "Header" +
                            CR +
                            "-x,--extralongarg     This" +
                            CR +
                            "                      description" +
                            CR +
                            "                      has" +
                            CR +
                            "                      ReallyLongVal" +
                            CR +
                            "                      uesThatAreLon" +
                            CR +
                            "                      gerThanTheWid" +
                            CR +
                            "                      thOfTheColumn" +
                            CR +
                            "                      s and also" +
                            CR +
                            "                      other" +
                            CR +
                            "                      ReallyLongVal" +
                            CR +
                            "                      uesThatAreHug" +
                            CR +
                            "                      erAndBiggerTh" +
                            CR +
                            "                      anTheWidthOfT" +
                            CR +
                            "                      heColumnsBob," +
                            CR +
                            "                      yes." +
                            CR +
                            "Footer" +
                            CR;
    assertEquals ("Long arguments did not split as expected", expected, m_aSW.getAsString ());
  }

  @Test
  public void testLongLineChunkingIndentIgnored ()
  {
    final Options options = new Options ();
    options.addOption (Option.builder ("x").longOpt ("extralongarg").desc ("This description is Long.").build ());

    m_aFormatter.printHelp (new PrintWriter (m_aSW),
                            22,
                            this.getClass ().getName (),
                            "Header",
                            options,
                            0,
                            5,
                            "Footer");
    final String expected = "usage:" +
                            CR +
                            "       com.helger.cli." +
                            CR +
                            "       bug.BugCLI162Te" +
                            CR +
                            "       st" +
                            CR +
                            "Header" +
                            CR +
                            "-x,--extralongarg" +
                            CR +
                            " This description is" +
                            CR +
                            " Long." +
                            CR +
                            "Footer" +
                            CR;
    assertEquals ("Long arguments did not split as expected", expected, m_aSW.getAsString ());
  }

}
