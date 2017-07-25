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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.helger.cli.Options.RequiredArg;
import com.helger.cli.Options.RequiredGroup;
import com.helger.cli.ex.AmbiguousOptionException;
import com.helger.cli.ex.CommandLineParseException;
import com.helger.cli.ex.MissingArgumentException;
import com.helger.cli.ex.MissingOptionException;
import com.helger.cli.ex.UnrecognizedOptionException;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.lang.NonBlockingProperties;

/**
 * Abstract test case testing common parser features.
 */
public abstract class AbstractParserTestCase
{
  protected CommandLineParser parser;
  protected Options m_aOptions;

  @Before
  public void setUp ()
  {
    m_aOptions = new Options ().addOption (Option.builder ("a")
                                                 .longOpt ("enable-a")
                                                 .desc ("turn [a] on or off")
                                                 .build ())
                               .addOption (Option.builder ("b")
                                                 .longOpt ("bfile")
                                                 .oneArg ()
                                                 .desc ("set the value of [b]")
                                                 .build ())
                               .addOption (Option.builder ("c").longOpt ("copt").desc ("turn [c] on or off").build ());
  }

  @Test
  public void testSimpleShort () throws Exception
  {
    final String [] aArgs = new String [] { "-a", "-b", "toast", "foo", "bar" };

    final CommandLine cl = parser.parse (m_aOptions, aArgs);

    assertTrue ("Confirm -a is set", cl.hasOption ("a"));
    assertTrue ("Confirm -b is set", cl.hasOption ("b"));
    assertTrue ("Confirm arg of -b", cl.getOptionValue ("b").equals ("toast"));
    assertTrue ("Confirm size of extra aArgs", cl.getAllArgs ().size () == 2);
  }

  @Test
  public void testSimpleLong () throws Exception
  {
    final String [] aArgs = new String [] { "--enable-a", "--bfile", "toast", "foo", "bar" };

    final CommandLine cl = parser.parse (m_aOptions, aArgs);

    assertTrue ("Confirm -a is set", cl.hasOption ("a"));
    assertTrue ("Confirm -b is set", cl.hasOption ("b"));
    assertTrue ("Confirm arg of -b", cl.getOptionValue ("b").equals ("toast"));
    assertTrue ("Confirm arg of --bfile", cl.getOptionValue ("bfile").equals ("toast"));
    assertTrue ("Confirm size of extra aArgs", cl.getAllArgs ().size () == 2);
  }

  @Test
  public void testMultiple () throws Exception
  {
    final String [] aArgs = new String [] { "-c", "foobar", "-b", "toast" };

    CommandLine cl = parser.parse (m_aOptions, aArgs, true);
    assertTrue ("Confirm -c is set", cl.hasOption ("c"));
    assertTrue ("Confirm  3 extra aArgs: " + cl.getAllArgs ().size (), cl.getAllArgs ().size () == 3);

    cl = parser.parse (m_aOptions, cl.getArgs ());

    assertTrue ("Confirm -c is not set", !cl.hasOption ("c"));
    assertTrue ("Confirm -b is set", cl.hasOption ("b"));
    assertTrue ("Confirm arg of -b", cl.getOptionValue ("b").equals ("toast"));
    assertTrue ("Confirm  1 extra arg: " + cl.getAllArgs ().size (), cl.getAllArgs ().size () == 1);
    assertTrue ("Confirm  value of extra arg: " + cl.getAllArgs ().get (0), cl.getAllArgs ().get (0).equals ("foobar"));
  }

  @Test
  public void testMultipleWithLong () throws Exception
  {
    final String [] aArgs = new String [] { "--copt", "foobar", "--bfile", "toast" };

    CommandLine cl = parser.parse (m_aOptions, aArgs, true);
    assertTrue ("Confirm -c is set", cl.hasOption ("c"));
    assertTrue ("Confirm  3 extra aArgs: " + cl.getAllArgs ().size (), cl.getAllArgs ().size () == 3);

    cl = parser.parse (m_aOptions, cl.getArgs ());

    assertTrue ("Confirm -c is not set", !cl.hasOption ("c"));
    assertTrue ("Confirm -b is set", cl.hasOption ("b"));
    assertTrue ("Confirm arg of -b", cl.getOptionValue ("b").equals ("toast"));
    assertTrue ("Confirm  1 extra arg: " + cl.getAllArgs ().size (), cl.getAllArgs ().size () == 1);
    assertTrue ("Confirm  value of extra arg: " + cl.getAllArgs ().get (0), cl.getAllArgs ().get (0).equals ("foobar"));
  }

  @Test
  public void testUnrecognizedOption () throws Exception
  {
    final String [] aArgs = new String [] { "-a", "-d", "-b", "toast", "foo", "bar" };

    try
    {
      parser.parse (m_aOptions, aArgs);
      fail ("UnrecognizedOptionException wasn't thrown");
    }
    catch (final UnrecognizedOptionException e)
    {
      assertEquals ("-d", e.getOption ());
    }
  }

  @Test
  public void testMissingArg () throws Exception
  {
    final String [] aArgs = new String [] { "-b" };

    boolean caught = false;

    try
    {
      parser.parse (m_aOptions, aArgs);
    }
    catch (final MissingArgumentException e)
    {
      caught = true;
      assertEquals ("option missing an argument", "b", e.getOption ().getOpt ());
    }

    assertTrue ("Confirm MissingArgumentException caught", caught);
  }

  @Test
  public void testDoubleDash1 () throws Exception
  {
    final String [] aArgs = new String [] { "--copt", "--", "-b", "toast" };

    final CommandLine cl = parser.parse (m_aOptions, aArgs);

    assertTrue ("Confirm -c is set", cl.hasOption ("c"));
    assertTrue ("Confirm -b is not set", !cl.hasOption ("b"));
    assertTrue ("Confirm 2 extra aArgs: " + cl.getAllArgs ().size (), cl.getAllArgs ().size () == 2);
  }

  @Test
  public void testDoubleDash2 () throws Exception
  {
    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ("n").oneArg ().build ());
    aOptions.addOption (Option.builder ("m").build ());

    try
    {
      parser.parse (aOptions, new String [] { "-n", "--", "-m" });
      fail ("MissingArgumentException not thrown for option -n");
    }
    catch (final MissingArgumentException e)
    {
      assertNotNull ("option null", e.getOption ());
      assertEquals ("n", e.getOption ().getOpt ());
    }
  }

  @Test
  public void testSingleDash () throws Exception
  {
    final String [] aArgs = new String [] { "--copt", "-b", "-", "-a", "-" };

    final CommandLine cl = parser.parse (m_aOptions, aArgs);

    assertTrue ("Confirm -a is set", cl.hasOption ("a"));
    assertTrue ("Confirm -b is set", cl.hasOption ("b"));
    assertTrue ("Confirm arg of -b", cl.getOptionValue ("b").equals ("-"));
    assertTrue ("Confirm 1 extra arg: " + cl.getAllArgs ().size (), cl.getAllArgs ().size () == 1);
    assertTrue ("Confirm value of extra arg: " + cl.getAllArgs ().get (0), cl.getAllArgs ().get (0).equals ("-"));
  }

  @Test
  public void testStopAtUnexpectedArg () throws Exception
  {
    final String [] aArgs = new String [] { "-c", "foober", "-b", "toast" };

    final CommandLine cl = parser.parse (m_aOptions, aArgs, true);
    assertTrue ("Confirm -c is set", cl.hasOption ("c"));
    assertTrue ("Confirm  3 extra aArgs: " + cl.getAllArgs ().size (), cl.getAllArgs ().size () == 3);
  }

  @Test
  public void testStopAtExpectedArg () throws Exception
  {
    final String [] aArgs = new String [] { "-b", "foo" };

    final CommandLine cl = parser.parse (m_aOptions, aArgs, true);

    assertTrue ("Confirm -b is set", cl.hasOption ('b'));
    assertEquals ("Confirm -b is set", "foo", cl.getOptionValue ('b'));
    assertTrue ("Confirm no extra aArgs: " + cl.getAllArgs ().size (), cl.getAllArgs ().size () == 0);
  }

  @Test
  public void testStopAtNonOptionShort () throws Exception
  {
    final String [] aArgs = new String [] { "-z", "-a", "-btoast" };

    final CommandLine cl = parser.parse (m_aOptions, aArgs, true);
    assertFalse ("Confirm -a is not set", cl.hasOption ("a"));
    assertTrue ("Confirm  3 extra aArgs: " + cl.getAllArgs ().size (), cl.getAllArgs ().size () == 3);
  }

  @Test
  public void testStopAtNonOptionLong () throws Exception
  {
    final String [] aArgs = new String [] { "--zop==1", "-abtoast", "--b=bar" };

    final CommandLine cl = parser.parse (m_aOptions, aArgs, true);

    assertFalse ("Confirm -a is not set", cl.hasOption ("a"));
    assertFalse ("Confirm -b is not set", cl.hasOption ("b"));
    assertTrue ("Confirm  3 extra aArgs: " + cl.getAllArgs ().size (), cl.getAllArgs ().size () == 3);
  }

  @Test
  public void testNegativeArgument () throws Exception
  {
    final String [] aArgs = new String [] { "-b", "-1" };

    final CommandLine cl = parser.parse (m_aOptions, aArgs);
    assertEquals ("-1", cl.getOptionValue ("b"));
  }

  @Test
  public void testNegativeOption () throws Exception
  {
    final String [] aArgs = new String [] { "-b", "-1" };

    m_aOptions.addOption (Option.builder ('1').build ());

    final CommandLine cl = parser.parse (m_aOptions, aArgs);
    assertEquals ("-1", cl.getOptionValue ("b"));
  }

  @Test
  public void testArgumentStartingWithHyphen () throws Exception
  {
    final String [] aArgs = new String [] { "-b", "-foo" };

    final CommandLine cl = parser.parse (m_aOptions, aArgs);
    assertEquals ("-foo", cl.getOptionValue ("b"));
  }

  @Test
  public void testShortWithEqual () throws Exception
  {
    final String [] aArgs = new String [] { "-f=bar" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ("f").longOpt ("foo").oneArg ().build ());

    final CommandLine cl = parser.parse (aOptions, aArgs);

    assertEquals ("bar", cl.getOptionValue ("foo"));
  }

  @Test
  public void testShortWithoutEqual () throws Exception
  {
    final String [] aArgs = new String [] { "-fbar" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ("f").longOpt ("foo").oneArg ().build ());

    final CommandLine cl = parser.parse (aOptions, aArgs);

    assertEquals ("bar", cl.getOptionValue ("foo"));
  }

  @Test
  public void testLongWithEqualDoubleDash () throws Exception
  {
    final String [] aArgs = new String [] { "--foo=bar" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ("f").longOpt ("foo").oneArg ().build ());

    final CommandLine cl = parser.parse (aOptions, aArgs);

    assertEquals ("bar", cl.getOptionValue ("foo"));
  }

  @Test
  public void testLongWithEqualSingleDash () throws Exception
  {
    final String [] aArgs = new String [] { "-foo=bar" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ("f").longOpt ("foo").oneArg ().build ());

    final CommandLine cl = parser.parse (aOptions, aArgs);

    assertEquals ("bar", cl.getOptionValue ("foo"));
  }

  @Test
  public void testLongWithoutEqualSingleDash () throws Exception
  {
    final String [] aArgs = new String [] { "-foobar" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ("f").longOpt ("foo").oneArg ().build ());

    final CommandLine cl = parser.parse (aOptions, aArgs);

    assertEquals ("bar", cl.getOptionValue ("foo"));
  }

  @Test
  public void testAmbiguousLongWithoutEqualSingleDash () throws Exception
  {
    final String [] aArgs = new String [] { "-b", "-foobar" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ("f").longOpt ("foo").oneOptionalArg ().build ());
    aOptions.addOption (Option.builder ("b").longOpt ("bar").oneOptionalArg ().build ());

    final CommandLine cl = parser.parse (aOptions, aArgs);

    assertTrue (cl.hasOption ("b"));
    assertTrue (cl.hasOption ("f"));
    assertEquals ("bar", cl.getOptionValue ("foo"));
  }

  @Test
  public void testLongWithoutEqualDoubleDash () throws Exception
  {
    final String [] aArgs = new String [] { "--foobar" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ("f").longOpt ("foo").oneArg ().build ());

    final CommandLine cl = parser.parse (aOptions, aArgs, true);

    assertFalse (cl.hasOption ("foo")); // foo isn't expected to be recognized
                                        // with a double dash
  }

  @Test
  public void testLongWithUnexpectedArgument1 () throws Exception
  {
    final String [] aArgs = new String [] { "--foo=bar" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ("f").longOpt ("foo").build ());

    try
    {
      parser.parse (aOptions, aArgs);
    }
    catch (final UnrecognizedOptionException e)
    {
      assertEquals ("--foo=bar", e.getOption ());
      return;
    }

    fail ("UnrecognizedOptionException not thrown");
  }

  @Test
  public void testLongWithUnexpectedArgument2 () throws Exception
  {
    final String [] aArgs = new String [] { "-foobar" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ("f").longOpt ("foo").build ());

    try
    {
      parser.parse (aOptions, aArgs);
    }
    catch (final UnrecognizedOptionException e)
    {
      assertEquals ("-foobar", e.getOption ());
      return;
    }

    fail ("UnrecognizedOptionException not thrown");
  }

  @Test
  public void testShortWithUnexpectedArgument () throws Exception
  {
    final String [] aArgs = new String [] { "-f=bar" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ("f").longOpt ("foo").build ());

    try
    {
      parser.parse (aOptions, aArgs);
    }
    catch (final UnrecognizedOptionException e)
    {
      assertEquals ("-f=bar", e.getOption ());
      return;
    }

    fail ("UnrecognizedOptionException not thrown");
  }

  @Test
  public void testPropertiesOption1 () throws Exception
  {
    final String [] aArgs = new String [] { "-Jsource=1.5", "-J", "target", "1.5", "foo" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ("J").valueSeparator ().numberOfArgs (2).build ());

    final CommandLine cl = parser.parse (aOptions, aArgs);

    final ICommonsList <String> values = cl.getAllOptionValues ("J");
    assertNotNull ("null values", values);
    assertEquals ("number of values", 4, values.size ());
    assertEquals ("value 1", "source", values.get (0));
    assertEquals ("value 2", "1.5", values.get (1));
    assertEquals ("value 3", "target", values.get (2));
    assertEquals ("value 4", "1.5", values.get (3));

    final List <?> argsleft = cl.getAllArgs ();
    assertEquals ("Should be 1 arg left", 1, argsleft.size ());
    assertEquals ("Expecting foo", "foo", argsleft.get (0));
  }

  @Test
  public void testPropertiesOption2 () throws Exception
  {
    final String [] aArgs = new String [] { "-Dparam1", "-Dparam2=value2", "-D" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ("D").valueSeparator ().optionalNumberOfArgs (2).build ());

    final CommandLine cl = parser.parse (aOptions, aArgs);

    final NonBlockingProperties props = cl.getOptionProperties ("D");
    assertNotNull ("null properties", props);
    assertEquals ("number of properties in " + props, 2, props.size ());
    assertEquals ("property 1", "true", props.getProperty ("param1"));
    assertEquals ("property 2", "value2", props.getProperty ("param2"));

    final List <?> argsleft = cl.getAllArgs ();
    assertEquals ("Should be no arg left", 0, argsleft.size ());
  }

  @Test
  public void testUnambiguousPartialLongOption1 () throws Exception
  {
    final String [] aArgs = new String [] { "--ver" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ().longOpt ("version").build ());
    aOptions.addOption (Option.builder ().longOpt ("help").build ());

    final CommandLine cl = parser.parse (aOptions, aArgs);

    assertTrue ("Confirm --version is set", cl.hasOption ("version"));
  }

  @Test
  public void testUnambiguousPartialLongOption2 () throws Exception
  {
    final String [] aArgs = new String [] { "-ver" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ().longOpt ("version").build ());
    aOptions.addOption (Option.builder ().longOpt ("help").build ());

    final CommandLine cl = parser.parse (aOptions, aArgs);

    assertTrue ("Confirm --version is set", cl.hasOption ("version"));
  }

  @Test
  public void testUnambiguousPartialLongOption3 () throws Exception
  {
    final String [] aArgs = new String [] { "--ver=1" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ().longOpt ("verbose").oneOptionalArg ().build ());
    aOptions.addOption (Option.builder ().longOpt ("help").build ());

    final CommandLine cl = parser.parse (aOptions, aArgs);

    assertTrue ("Confirm --verbose is set", cl.hasOption ("verbose"));
    assertEquals ("1", cl.getOptionValue ("verbose"));
  }

  @Test
  public void testUnambiguousPartialLongOption4 () throws Exception
  {
    final String [] aArgs = new String [] { "-ver=1" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ().longOpt ("verbose").oneOptionalArg ().build ());
    aOptions.addOption (Option.builder ().longOpt ("help").build ());

    final CommandLine cl = parser.parse (aOptions, aArgs);

    assertTrue ("Confirm --verbose is set", cl.hasOption ("verbose"));
    assertEquals ("1", cl.getOptionValue ("verbose"));
  }

  @Test
  public void testAmbiguousPartialLongOption1 () throws Exception
  {
    final String [] aArgs = new String [] { "--ver" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ().longOpt ("version").build ());
    aOptions.addOption (Option.builder ().longOpt ("verbose").build ());

    boolean caught = false;

    try
    {
      parser.parse (aOptions, aArgs);
    }
    catch (final AmbiguousOptionException e)
    {
      caught = true;
      assertEquals ("Partial option", "--ver", e.getOption ());
      assertNotNull ("Matching aOptions null", e.getAllMatchingOptions ());
      assertEquals ("Matching aOptions size", 2, e.getAllMatchingOptions ().size ());
    }

    assertTrue ("Confirm MissingArgumentException caught", caught);
  }

  @Test
  public void testAmbiguousPartialLongOption2 () throws Exception
  {
    final String [] aArgs = new String [] { "-ver" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ().longOpt ("version").build ());
    aOptions.addOption (Option.builder ().longOpt ("verbose").build ());

    boolean caught = false;

    try
    {
      parser.parse (aOptions, aArgs);
    }
    catch (final AmbiguousOptionException e)
    {
      caught = true;
      assertEquals ("Partial option", "-ver", e.getOption ());
      assertNotNull ("Matching aOptions null", e.getAllMatchingOptions ());
      assertEquals ("Matching aOptions size", 2, e.getAllMatchingOptions ().size ());
    }

    assertTrue ("Confirm MissingArgumentException caught", caught);
  }

  @Test
  public void testAmbiguousPartialLongOption3 () throws Exception
  {
    final String [] aArgs = new String [] { "--ver=1" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ().longOpt ("version").build ());
    aOptions.addOption (Option.builder ().longOpt ("verbose").oneOptionalArg ().build ());

    boolean caught = false;

    try
    {
      parser.parse (aOptions, aArgs);
    }
    catch (final AmbiguousOptionException e)
    {
      caught = true;
      assertEquals ("Partial option", "--ver", e.getOption ());
      assertNotNull ("Matching aOptions null", e.getAllMatchingOptions ());
      assertEquals ("Matching aOptions size", 2, e.getAllMatchingOptions ().size ());
    }

    assertTrue ("Confirm MissingArgumentException caught", caught);
  }

  @Test
  public void testAmbiguousPartialLongOption4 () throws Exception
  {
    final String [] aArgs = new String [] { "-ver=1" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ().longOpt ("version").build ());
    aOptions.addOption (Option.builder ().longOpt ("verbose").oneOptionalArg ().build ());

    boolean caught = false;

    try
    {
      parser.parse (aOptions, aArgs);
    }
    catch (final AmbiguousOptionException e)
    {
      caught = true;
      assertEquals ("Partial option", "-ver", e.getOption ());
      assertNotNull ("Matching aOptions null", e.getAllMatchingOptions ());
      assertEquals ("Matching aOptions size", 2, e.getAllMatchingOptions ().size ());
    }

    assertTrue ("Confirm MissingArgumentException caught", caught);
  }

  @Test
  public void testPartialLongOptionSingleDash () throws Exception
  {
    final String [] aArgs = new String [] { "-ver" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ().longOpt ("version").build ());
    aOptions.addOption (Option.builder ("v").oneArg ().build ());

    final CommandLine cl = parser.parse (aOptions, aArgs);

    assertTrue ("Confirm --version is set", cl.hasOption ("version"));
    assertTrue ("Confirm -v is not set", !cl.hasOption ("v"));
  }

  @Test
  public void testWithRequiredOption () throws Exception
  {
    final String [] aArgs = new String [] { "-b", "file" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ('a').longOpt ("enable-a").build ());
    aOptions.addOption (Option.builder ("b").longOpt ("bfile").oneArg ().required ().build ());

    final CommandLine cl = parser.parse (aOptions, aArgs);

    assertTrue ("Confirm -a is NOT set", !cl.hasOption ("a"));
    assertTrue ("Confirm -b is set", cl.hasOption ("b"));
    assertTrue ("Confirm arg of -b", cl.getOptionValue ("b").equals ("file"));
    assertTrue ("Confirm NO of extra aArgs", cl.getAllArgs ().isEmpty ());
  }

  @Test
  public void testOptionAndRequiredOption () throws Exception
  {
    final String [] aArgs = new String [] { "-a", "-b", "file" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ('a').longOpt ("enable-a").build ());
    aOptions.addOption (Option.builder ('b').longOpt ("bfile").oneArg ().required ().build ());

    final CommandLine cl = parser.parse (aOptions, aArgs);

    assertTrue ("Confirm -a is set", cl.hasOption ("a"));
    assertTrue ("Confirm -b is set", cl.hasOption ("b"));
    assertTrue ("Confirm arg of -b", cl.getOptionValue ("b").equals ("file"));
    assertTrue ("Confirm NO of extra aArgs", cl.getAllArgs ().size () == 0);
  }

  @Test
  public void testMissingRequiredOption ()
  {
    final String [] aArgs = new String [] { "-a" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ('a').longOpt ("enable-a").build ());
    aOptions.addOption (Option.builder ("b").longOpt ("bfile").oneArg ().required ().build ());

    try
    {
      parser.parse (aOptions, aArgs);
      fail ("exception should have been thrown");
    }
    catch (final MissingOptionException e)
    {
      assertEquals ("Incorrect exception message", "Missing required option: b", e.getMessage ());
      assertTrue (e.getAllMissingOptions ().contains (new RequiredArg ("b")));
    }
    catch (final CommandLineParseException e)
    {
      fail ("expected to catch MissingOptionException");
    }
  }

  @Test
  public void testMissingRequiredOptions ()
  {
    final String [] aArgs = new String [] { "-a" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ('a').longOpt ("enable-a").build ());
    aOptions.addOption (Option.builder ("b").longOpt ("bfile").oneArg ().required ().build ());
    aOptions.addOption (Option.builder ("c").longOpt ("cfile").oneArg ().required ().build ());

    try
    {
      parser.parse (aOptions, aArgs);
      fail ("exception should have been thrown");
    }
    catch (final MissingOptionException e)
    {
      assertEquals ("Incorrect exception message", "Missing required options: b, c", e.getMessage ());
      assertTrue (e.getAllMissingOptions ().contains (new RequiredArg ("b")));
      assertTrue (e.getAllMissingOptions ().contains (new RequiredArg ("c")));
    }
    catch (final CommandLineParseException e)
    {
      fail ("expected to catch MissingOptionException");
    }
  }

  @Test
  public void testMissingRequiredGroup () throws Exception
  {
    final OptionGroup group = new OptionGroup ();
    group.addOption (Option.builder ("a").build ());
    group.addOption (Option.builder ("b").build ());
    group.setRequired (true);

    final Options aOptions = new Options ();
    aOptions.addOptionGroup (group);
    aOptions.addOption (Option.builder ("c").required ().build ());

    try
    {
      parser.parse (aOptions, new String [] { "-c" });
      fail ("MissingOptionException not thrown");
    }
    catch (final MissingOptionException e)
    {
      assertEquals (1, e.getAllMissingOptions ().size ());
      assertTrue (e.getAllMissingOptions ().get (0) instanceof RequiredGroup);
    }
    catch (final CommandLineParseException e)
    {
      fail ("Expected to catch MissingOptionException");
    }
  }

  @Test
  public void testOptionGroup () throws Exception
  {
    final OptionGroup group = new OptionGroup ();
    group.addOption (Option.builder ("a").build ());
    group.addOption (Option.builder ("b").build ());

    final Options aOptions = new Options ();
    aOptions.addOptionGroup (group);

    parser.parse (aOptions, new String [] { "-b" });

    assertEquals ("selected option", "b", group.getSelected ());
  }

  @Test
  public void testOptionGroupLong () throws Exception
  {
    final OptionGroup group = new OptionGroup ();
    group.addOption (Option.builder ().longOpt ("foo").build ());
    group.addOption (Option.builder ().longOpt ("bar").build ());

    final Options aOptions = new Options ();
    aOptions.addOptionGroup (group);

    final CommandLine cl = parser.parse (aOptions, new String [] { "--bar" });

    assertTrue (cl.hasOption ("bar"));
    assertEquals ("selected option", "bar", group.getSelected ());
  }

  @Test
  public void testReuseOptionsTwice () throws Exception
  {
    final Options aOpts = new Options ();
    aOpts.addOption (Option.builder ("v").required ().build ());

    // first parsing
    parser.parse (aOpts, new String [] { "-v" });

    try
    {
      // second parsing, with the same Options instance and an invalid command
      // line
      parser.parse (aOpts, new String [0]);
      fail ("MissingOptionException not thrown");
    }
    catch (final MissingOptionException e)
    {
      // expected
    }
  }

  @Test
  public void testBursting () throws Exception
  {
    final String [] aArgs = new String [] { "-acbtoast", "foo", "bar" };

    final CommandLine cl = parser.parse (m_aOptions, aArgs);

    assertTrue ("Confirm -a is set", cl.hasOption ("a"));
    assertTrue ("Confirm -b is set", cl.hasOption ("b"));
    assertTrue ("Confirm -c is set", cl.hasOption ("c"));
    assertTrue ("Confirm arg of -b", cl.getOptionValue ("b").equals ("toast"));
    assertTrue ("Confirm size of extra aArgs", cl.getAllArgs ().size () == 2);
  }

  @Test
  public void testUnrecognizedOptionWithBursting () throws Exception
  {
    final String [] aArgs = new String [] { "-adbtoast", "foo", "bar" };

    try
    {
      parser.parse (m_aOptions, aArgs);
      fail ("UnrecognizedOptionException wasn't thrown");
    }
    catch (final UnrecognizedOptionException e)
    {
      assertEquals ("-adbtoast", e.getOption ());
    }
  }

  @Test
  public void testMissingArgWithBursting () throws Exception
  {
    final String [] aArgs = new String [] { "-acb" };

    boolean caught = false;

    try
    {
      parser.parse (m_aOptions, aArgs);
    }
    catch (final MissingArgumentException e)
    {
      caught = true;
      assertEquals ("option missing an argument", "b", e.getOption ().getOpt ());
    }

    assertTrue ("Confirm MissingArgumentException caught", caught);
  }

  @Test
  public void testStopBursting () throws Exception
  {
    final String [] aArgs = new String [] { "-azc" };

    final CommandLine cl = parser.parse (m_aOptions, aArgs, true);
    assertTrue ("Confirm -a is set", cl.hasOption ("a"));
    assertFalse ("Confirm -c is not set", cl.hasOption ("c"));

    assertTrue ("Confirm  1 extra arg: " + cl.getAllArgs ().size (), cl.getAllArgs ().size () == 1);
    assertTrue (cl.getAllArgs ().contains ("zc"));
  }

  @Test
  public void testStopBursting2 () throws Exception
  {
    final String [] aArgs = new String [] { "-c", "foobar", "-btoast" };

    CommandLine cl = parser.parse (m_aOptions, aArgs, true);
    assertTrue ("Confirm -c is set", cl.hasOption ("c"));
    assertTrue ("Confirm  2 extra aArgs: " + cl.getAllArgs ().size (), cl.getAllArgs ().size () == 2);

    cl = parser.parse (m_aOptions, cl.getArgs ());

    assertTrue ("Confirm -c is not set", !cl.hasOption ("c"));
    assertTrue ("Confirm -b is set", cl.hasOption ("b"));
    assertTrue ("Confirm arg of -b", cl.getOptionValue ("b").equals ("toast"));
    assertTrue ("Confirm  1 extra arg: " + cl.getAllArgs ().size (), cl.getAllArgs ().size () == 1);
    assertTrue ("Confirm  value of extra arg: " + cl.getAllArgs ().get (0), cl.getAllArgs ().get (0).equals ("foobar"));
  }

  @Test
  public void testUnlimitedArgs () throws Exception
  {
    final String [] aArgs = new String [] { "-e", "one", "two", "-f", "alpha" };

    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ("e").unlimitedArgs ().build ());
    aOptions.addOption (Option.builder ("f").unlimitedArgs ().build ());

    final CommandLine cl = parser.parse (aOptions, aArgs);

    assertTrue ("Confirm -e is set", cl.hasOption ("e"));
    assertEquals ("number of arg for -e", 2, cl.getAllOptionValues ("e").size ());
    assertTrue ("Confirm -f is set", cl.hasOption ("f"));
    assertEquals ("number of arg for -f", 1, cl.getAllOptionValues ("f").size ());
  }

  private CommandLine _parse (final CommandLineParser aParser,
                              final Options aOpts,
                              final String [] aArgs,
                              final NonBlockingProperties properties) throws CommandLineParseException
  {
    return aParser.parse (aOpts, aArgs, properties);
  }

  @Test
  public void testPropertyOptionSingularValue () throws Exception
  {
    final Options aOpts = new Options ();
    aOpts.addOption (Option.builder ().optionalNumberOfArgs (2).longOpt ("hide").build ());

    final NonBlockingProperties properties = new NonBlockingProperties ();
    properties.setProperty ("hide", "seek");

    final CommandLine aCmdLine = _parse (parser, aOpts, null, properties);
    assertTrue (aCmdLine.hasOption ("hide"));
    assertEquals ("seek", aCmdLine.getOptionValue ("hide"));
    assertTrue (!aCmdLine.hasOption ("fake"));
  }

  @Test
  public void testPropertyOptionFlags () throws Exception
  {
    final Options aOpts = new Options ();
    aOpts.addOption (Option.builder ('a').desc ("toggle -a").build ());
    aOpts.addOption (Option.builder ('c').longOpt ("c").desc ("toggle -c").build ());
    aOpts.addOption (Option.builder ("e").oneOptionalArg ().build ());

    NonBlockingProperties properties = new NonBlockingProperties ();
    properties.setProperty ("a", "true");
    properties.setProperty ("c", "yes");
    properties.setProperty ("e", "1");

    CommandLine aCmdLine = _parse (parser, aOpts, null, properties);
    assertTrue (aCmdLine.hasOption ("a"));
    assertTrue (aCmdLine.hasOption ("c"));
    assertTrue (aCmdLine.hasOption ("e"));

    properties = new NonBlockingProperties ();
    properties.setProperty ("a", "false");
    properties.setProperty ("c", "no");
    properties.setProperty ("e", "0");

    aCmdLine = _parse (parser, aOpts, null, properties);
    assertTrue (!aCmdLine.hasOption ("a"));
    assertTrue (!aCmdLine.hasOption ("c"));
    assertTrue (aCmdLine.hasOption ("e")); // this option accepts an argument

    properties = new NonBlockingProperties ();
    properties.setProperty ("a", "TRUE");
    properties.setProperty ("c", "nO");
    properties.setProperty ("e", "TrUe");

    aCmdLine = _parse (parser, aOpts, null, properties);
    assertTrue (aCmdLine.hasOption ("a"));
    assertTrue (!aCmdLine.hasOption ("c"));
    assertTrue (aCmdLine.hasOption ("e"));

    properties = new NonBlockingProperties ();
    properties.setProperty ("a", "just a string");
    properties.setProperty ("e", "");

    aCmdLine = _parse (parser, aOpts, null, properties);
    assertTrue (!aCmdLine.hasOption ("a"));
    assertTrue (!aCmdLine.hasOption ("c"));
    assertTrue (aCmdLine.hasOption ("e"));

    properties = new NonBlockingProperties ();
    properties.setProperty ("a", "0");
    properties.setProperty ("c", "1");

    aCmdLine = _parse (parser, aOpts, null, properties);
    assertTrue (!aCmdLine.hasOption ("a"));
    assertTrue (aCmdLine.hasOption ("c"));
  }

  @Test
  public void testPropertyOptionMultipleValues () throws Exception
  {
    final Options aOpts = new Options ();
    aOpts.addOption (Option.builder ("k").unlimitedArgs ().valueSeparator (',').build ());

    final NonBlockingProperties properties = new NonBlockingProperties ();
    properties.setProperty ("k", "one,two");

    final String [] aValues = new String [] { "one", "two" };

    final CommandLine aCmdLine = _parse (parser, aOpts, null, properties);
    assertTrue (aCmdLine.hasOption ("k"));
    assertEquals (new CommonsArrayList <> (aValues), aCmdLine.getAllOptionValues ('k'));
  }

  @Test
  public void testPropertyOverrideValues () throws Exception
  {
    final Options aOpts = new Options ();
    aOpts.addOption (Option.builder ("i").optionalNumberOfArgs (2).build ());
    aOpts.addOption (Option.builder ("j").oneOptionalArg ().build ());

    final String [] aArgs = new String [] { "-j", "found", "-i", "ink" };

    final NonBlockingProperties properties = new NonBlockingProperties ();
    properties.setProperty ("j", "seek");

    final CommandLine aCmdLine = _parse (parser, aOpts, aArgs, properties);
    assertTrue (aCmdLine.hasOption ("j"));
    assertEquals ("found", aCmdLine.getOptionValue ("j"));
    assertTrue (aCmdLine.hasOption ("i"));
    assertEquals ("ink", aCmdLine.getOptionValue ("i"));
    assertFalse (aCmdLine.hasOption ("fake"));
  }

  @Test
  public void testPropertyOptionRequired () throws Exception
  {
    final Options aOpts = new Options ();
    aOpts.addOption (Option.builder ("f").required ().build ());

    final NonBlockingProperties properties = new NonBlockingProperties ();
    properties.setProperty ("f", "true");

    final CommandLine aCmdLine = _parse (parser, aOpts, null, properties);
    assertTrue (aCmdLine.hasOption ("f"));
  }

  @Test
  public void testPropertyOptionUnexpected () throws Exception
  {
    final Options aOpts = new Options ();

    final NonBlockingProperties properties = new NonBlockingProperties ();
    properties.setProperty ("f", "true");

    try
    {
      _parse (parser, aOpts, null, properties);
      fail ("UnrecognizedOptionException expected");
    }
    catch (final UnrecognizedOptionException e)
    {
      // expected
    }
  }

  @Test
  public void testPropertyOptionGroup () throws Exception
  {
    final Options aOpts = new Options ();

    final OptionGroup group1 = new OptionGroup ();
    group1.addOption (Option.builder ("a").build ());
    group1.addOption (Option.builder ("b").build ());
    aOpts.addOptionGroup (group1);

    final OptionGroup group2 = new OptionGroup ();
    group2.addOption (Option.builder ("x").build ());
    group2.addOption (Option.builder ("y").build ());
    aOpts.addOptionGroup (group2);

    final String [] aArgs = new String [] { "-a" };

    final NonBlockingProperties properties = new NonBlockingProperties ();
    properties.put ("b", "true");
    properties.put ("x", "true");

    final CommandLine aCmdLine = _parse (parser, aOpts, aArgs, properties);

    assertTrue (aCmdLine.hasOption ("a"));
    assertFalse (aCmdLine.hasOption ("b"));
    assertTrue (aCmdLine.hasOption ("x"));
    assertFalse (aCmdLine.hasOption ("y"));
  }
}
