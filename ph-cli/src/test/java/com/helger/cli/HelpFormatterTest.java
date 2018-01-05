/**
 * Original copyright by Apache Software Foundation
 * Copyright (C) 2017-2018 Philip Helger (www.helger.com)
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
import static org.junit.Assert.fail;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.stream.NonBlockingStringWriter;

/**
 * Test case for the {@link HelpFormatter} class.
 */
public final class HelpFormatterTest
{
  private static final String EOL = System.getProperty ("line.separator");

  @Test
  public void testFindWrapPos () throws Exception
  {
    String text = "This is a test.";
    // text width should be max 8; the wrap position is 7
    assertEquals ("wrap position", 7, HelpFormatter.findWrapPos (text, 8, 0));

    // starting from 8 must give -1 - the wrap pos is after end
    assertEquals ("wrap position 2", -1, HelpFormatter.findWrapPos (text, 8, 8));

    // words longer than the width are cut
    text = "aaaa aa";
    assertEquals ("wrap position 3", 3, HelpFormatter.findWrapPos (text, 3, 0));

    // last word length is equal to the width
    text = "aaaaaa aaaaaa";
    assertEquals ("wrap position 4", 6, HelpFormatter.findWrapPos (text, 6, 0));
    assertEquals ("wrap position 4", -1, HelpFormatter.findWrapPos (text, 6, 7));

    text = "aaaaaa\n aaaaaa";
    assertEquals ("wrap position 5", 7, HelpFormatter.findWrapPos (text, 6, 0));

    text = "aaaaaa\t aaaaaa";
    assertEquals ("wrap position 6", 7, HelpFormatter.findWrapPos (text, 6, 0));
  }

  @Test
  public void testRenderWrappedTextWordCut ()
  {
    final int width = 7;
    final int padding = 0;
    final String text = "Thisisatest.";
    final String expected = "Thisisa" + EOL + "test.";

    final StringBuilder sb = new StringBuilder ();
    new HelpFormatter ().renderWrappedText (sb, width, padding, text);
    assertEquals ("cut and wrap", expected, sb.toString ());
  }

  @Test
  public void testRenderWrappedTextSingleLine ()
  {
    // single line text
    final int width = 12;
    final int padding = 0;
    final String text = "This is a test.";
    final String expected = "This is a" + EOL + "test.";

    final StringBuilder sb = new StringBuilder ();
    new HelpFormatter ().renderWrappedText (sb, width, padding, text);
    assertEquals ("single line text", expected, sb.toString ());
  }

  @Test
  public void testRenderWrappedTextSingleLinePadded ()
  {
    // single line padded text
    final int width = 12;
    final int padding = 4;
    final String text = "This is a test.";
    final String expected = "This is a" + EOL + "    test.";

    final StringBuilder sb = new StringBuilder ();
    new HelpFormatter ().renderWrappedText (sb, width, padding, text);
    assertEquals ("single line padded text", expected, sb.toString ());
  }

  @Test
  public void testRenderWrappedTextSingleLinePadded2 ()
  {
    // single line padded text 2
    final int width = 53;
    final int padding = 24;
    final String text = "  -p,--period <PERIOD>  PERIOD is time duration of form " +
                        "DATE[-DATE] where DATE has form YYYY[MM[DD]]";
    final String expected = "  -p,--period <PERIOD>  PERIOD is time duration of" +
                            EOL +
                            "                        form DATE[-DATE] where DATE" +
                            EOL +
                            "                        has form YYYY[MM[DD]]";

    final StringBuilder sb = new StringBuilder ();
    new HelpFormatter ().renderWrappedText (sb, width, padding, text);
    assertEquals ("single line padded text 2", expected, sb.toString ());
  }

  @Test
  public void testRenderWrappedTextMultiLine ()
  {
    // multi line text
    final int width = 16;
    final int padding = 0;
    final String expected = "aaaa aaaa aaaa" + EOL + "aaaaaa" + EOL + "aaaaa";

    final StringBuilder sb = new StringBuilder ();
    new HelpFormatter ().renderWrappedText (sb, width, padding, expected);
    assertEquals ("multi line text", expected, sb.toString ());
  }

  @Test
  public void testRenderWrappedTextMultiLinePadded ()
  {
    // multi-line padded text
    final int width = 16;
    final int padding = 4;
    final String text = "aaaa aaaa aaaa" + EOL + "aaaaaa" + EOL + "aaaaa";
    final String expected = "aaaa aaaa aaaa" + EOL + "    aaaaaa" + EOL + "    aaaaa";

    final StringBuilder sb = new StringBuilder ();
    new HelpFormatter ().renderWrappedText (sb, width, padding, text);
    assertEquals ("multi-line padded text", expected, sb.toString ());
  }

  @Test
  public void testPrintOptions () throws Exception
  {
    final StringBuilder sb = new StringBuilder ();
    final HelpFormatter hf = new HelpFormatter ();
    final int leftPad = 1;
    final int descPad = 3;
    final String lpad = HelpFormatter.createPadding (leftPad);
    final String dpad = HelpFormatter.createPadding (descPad);
    Options options = null;
    String expected = null;

    options = new Options ().addOption (Option.builder ("a").desc ("aaaa aaaa aaaa aaaa aaaa"));
    expected = lpad + "-a" + dpad + "aaaa aaaa aaaa aaaa aaaa";
    hf.renderOptions (sb, 60, options, leftPad, descPad);
    assertEquals ("simple non-wrapped option", expected, sb.toString ());

    int nextLineTabStop = leftPad + descPad + "-a".length ();
    expected = lpad +
               "-a" +
               dpad +
               "aaaa aaaa aaaa" +
               EOL +
               HelpFormatter.createPadding (nextLineTabStop) +
               "aaaa aaaa";
    sb.setLength (0);
    hf.renderOptions (sb, nextLineTabStop + 17, options, leftPad, descPad);
    assertEquals ("simple wrapped option", expected, sb.toString ());

    options = new Options ().addOption (Option.builder ("a").longOpt ("aaa").desc ("dddd dddd dddd dddd"));
    expected = lpad + "-a,--aaa" + dpad + "dddd dddd dddd dddd";
    sb.setLength (0);
    hf.renderOptions (sb, 60, options, leftPad, descPad);
    assertEquals ("long non-wrapped option", expected, sb.toString ());

    nextLineTabStop = leftPad + descPad + "-a,--aaa".length ();
    expected = lpad +
               "-a,--aaa" +
               dpad +
               "dddd dddd" +
               EOL +
               HelpFormatter.createPadding (nextLineTabStop) +
               "dddd dddd";
    sb.setLength (0);
    hf.renderOptions (sb, 25, options, leftPad, descPad);
    assertEquals ("long wrapped option", expected, sb.toString ());

    options = new Options ().addOption (Option.builder ("a").longOpt ("aaa").desc ("dddd dddd dddd dddd"))
                            .addOption (Option.builder ("b").desc ("feeee eeee eeee eeee"));
    expected = lpad +
               "-a,--aaa" +
               dpad +
               "dddd dddd" +
               EOL +
               HelpFormatter.createPadding (nextLineTabStop) +
               "dddd dddd" +
               EOL +
               lpad +
               "-b      " +
               dpad +
               "feeee eeee" +
               EOL +
               HelpFormatter.createPadding (nextLineTabStop) +
               "eeee eeee";
    sb.setLength (0);
    hf.renderOptions (sb, 25, options, leftPad, descPad);
    assertEquals ("multiple wrapped options", expected, sb.toString ());
  }

  @Test
  public void testPrintHelpWithEmptySyntax ()
  {
    final HelpFormatter formatter = new HelpFormatter ();
    try
    {
      formatter.printHelp (null, new Options ());
      fail ("null command line syntax should be rejected");
    }
    catch (final NullPointerException e)
    {
      // expected
    }

    try
    {
      formatter.printHelp ("", new Options ());
      fail ("empty command line syntax should be rejected");
    }
    catch (final IllegalArgumentException e)
    {
      // expected
    }
  }

  @Test
  public void testAutomaticUsage () throws Exception
  {
    final HelpFormatter hf = new HelpFormatter ();
    Options options = null;
    String expected = "usage: app [-a]";
    final NonBlockingByteArrayOutputStream out = new NonBlockingByteArrayOutputStream ();
    final PrintWriter pw = new PrintWriter (new OutputStreamWriter (out, StandardCharsets.ISO_8859_1));

    options = new Options ().addOption (Option.builder ("a").desc ("aaaa aaaa aaaa aaaa aaaa"));
    hf.printUsage (pw, 60, "app", options);
    pw.flush ();
    assertEquals ("simple auto usage", expected, out.getAsString (StandardCharsets.ISO_8859_1).trim ());
    out.reset ();

    expected = "usage: app [-a] [-b]";
    options = new Options ().addOption (Option.builder ("a").desc ("aaaa aaaa aaaa aaaa aaaa"))
                            .addOption (Option.builder ("b").desc ("bbb"));
    hf.printUsage (pw, 60, "app", options);
    pw.flush ();
    assertEquals ("simple auto usage", expected, out.getAsString (StandardCharsets.ISO_8859_1).trim ());
    out.reset ();
  }

  // This test ensures the options are properly sorted
  // See https://issues.apache.org/jira/browse/CLI-131
  @Test
  public void testPrintUsage ()
  {
    final Option optionA = Option.builder ("a").desc ("first").build ();
    final Option optionB = Option.builder ("b").desc ("second").build ();
    final Option optionC = Option.builder ("c").desc ("third").build ();
    final Options opts = new Options ();
    opts.addOption (optionA);
    opts.addOption (optionB);
    opts.addOption (optionC);
    final HelpFormatter helpFormatter = new HelpFormatter ();
    final NonBlockingStringWriter out = new NonBlockingStringWriter ();
    try (final PrintWriter printWriter = new PrintWriter (out))
    {
      helpFormatter.printUsage (printWriter, 80, "app", opts);
    }
    assertEquals ("usage: app [-a] [-b] [-c]" + EOL, out.getAsString ());
  }

  // uses the test for CLI-131 to implement CLI-155
  @Test
  public void testPrintSortedUsage ()
  {
    final Options opts = new Options ();
    opts.addOption (Option.builder ("a").desc ("first"));
    opts.addOption (Option.builder ("b").desc ("second"));
    opts.addOption (Option.builder ("c").desc ("third"));

    final HelpFormatter helpFormatter = new HelpFormatter ();
    helpFormatter.setOptionComparator ( (opt1, opt2) -> opt2.getKey ().compareToIgnoreCase (opt1.getKey ()));

    try (final NonBlockingStringWriter out = new NonBlockingStringWriter ())
    {
      helpFormatter.printUsage (new PrintWriter (out), 80, "app", opts);

      assertEquals ("usage: app [-c] [-b] [-a]" + EOL, out.getAsString ());
    }
  }

  @Test
  public void testPrintSortedUsageWithNullComparator ()
  {
    final Options opts = new Options ();
    opts.addOption (Option.builder ("c").desc ("first"));
    opts.addOption (Option.builder ("b").desc ("second"));
    opts.addOption (Option.builder ("a").desc ("third"));

    final HelpFormatter helpFormatter = new HelpFormatter ();
    helpFormatter.setOptionComparator (null);

    try (final NonBlockingStringWriter out = new NonBlockingStringWriter ())
    {
      helpFormatter.printUsage (new PrintWriter (out), 80, "app", opts);

      assertEquals ("usage: app [-c] [-b] [-a]" + EOL, out.getAsString ());
    }
  }

  @Test
  public void testPrintOptionGroupUsage ()
  {
    final OptionGroup group = new OptionGroup ().addOption (Option.builder ("a"))
                                                .addOption (Option.builder ("b"))
                                                .addOption (Option.builder ("c"));

    final Options options = new Options ();
    options.addOptionGroup (group);

    try (final NonBlockingStringWriter out = new NonBlockingStringWriter ())
    {
      final HelpFormatter formatter = new HelpFormatter ();
      formatter.printUsage (new PrintWriter (out), 80, "app", options);

      assertEquals ("usage: app [-a | -b | -c]" + EOL, out.getAsString ());
    }
  }

  @Test
  public void testPrintRequiredOptionGroupUsage ()
  {
    final OptionGroup group = new OptionGroup ().addOption (Option.builder ("a"))
                                                .addOption (Option.builder ("b"))
                                                .addOption (Option.builder ("c"))
                                                .setRequired (true);

    final Options options = new Options ();
    options.addOptionGroup (group);

    try (final NonBlockingStringWriter out = new NonBlockingStringWriter ())
    {
      final HelpFormatter formatter = new HelpFormatter ();
      formatter.printUsage (new PrintWriter (out), 80, "app", options);

      assertEquals ("usage: app -a | -b | -c" + EOL, out.getAsString ());
    }
  }

  @Test
  public void testPrintOptionWithEmptyArgNameUsage ()
  {
    final Option option = Option.builder ("f").required (true).build ();

    final Options options = new Options ();
    options.addOption (option);

    try (final NonBlockingStringWriter out = new NonBlockingStringWriter ())
    {
      final HelpFormatter formatter = new HelpFormatter ();
      formatter.printUsage (new PrintWriter (out), 80, "app", options);

      assertEquals ("usage: app -f" + EOL, out.getAsString ());
    }
  }

  @Test
  public void testDefaultArgName ()
  {
    final Option option = Option.builder ("f").args (1).required (true).build ();

    final Options options = new Options ();
    options.addOption (option);

    try (final NonBlockingStringWriter out = new NonBlockingStringWriter ())
    {
      final HelpFormatter formatter = new HelpFormatter ();
      formatter.setArgName ("argument");
      formatter.printUsage (new PrintWriter (out), 80, "app", options);

      assertEquals ("usage: app -f <argument>" + EOL, out.getAsString ());
    }
  }

  @Test
  public void testRtrim ()
  {
    assertEquals (null, HelpFormatter.rtrim (null));
    assertEquals ("", HelpFormatter.rtrim (""));
    assertEquals ("  foo", HelpFormatter.rtrim ("  foo  "));
  }

  @Test
  public void testAccessors ()
  {
    final HelpFormatter formatter = new HelpFormatter ();

    formatter.setArgName ("argname");
    assertEquals ("arg name", "argname", formatter.getArgName ());

    formatter.setDescPadding (3);
    assertEquals ("desc padding", 3, formatter.getDescPadding ());

    formatter.setLeftPadding (7);
    assertEquals ("left padding", 7, formatter.getLeftPadding ());

    formatter.setLongOptPrefix ("~~");
    assertEquals ("long opt prefix", "~~", formatter.getLongOptPrefix ());

    formatter.setNewLine ("\n");
    assertEquals ("new line", "\n", formatter.getNewLine ());

    formatter.setOptPrefix ("~");
    assertEquals ("opt prefix", "~", formatter.getOptPrefix ());

    formatter.setSyntaxPrefix ("-> ");
    assertEquals ("syntax prefix", "-> ", formatter.getSyntaxPrefix ());

    formatter.setWidth (80);
    assertEquals ("width", 80, formatter.getWidth ());
  }

  @Test
  public void testHeaderStartingWithLineSeparator ()
  {
    // related to Bugzilla #21215
    final Options options = new Options ();
    final HelpFormatter formatter = new HelpFormatter ();
    final String header = EOL + "Header";
    final String footer = "Footer";
    try (final NonBlockingStringWriter out = new NonBlockingStringWriter ())
    {
      formatter.printHelp (new PrintWriter (out), 80, "foobar", header, options, 2, 2, footer, true);
      assertEquals ("usage: foobar" + EOL + EOL + "Header" + EOL + EOL + "Footer" + EOL, out.getAsString ());
    }
  }

  @Test
  public void testIndentedHeaderAndFooter ()
  {
    // related to CLI-207
    final Options options = new Options ();
    final HelpFormatter formatter = new HelpFormatter ();
    final String header = "  Header1\n  Header2";
    final String footer = "  Footer1\n  Footer2";
    try (final NonBlockingStringWriter out = new NonBlockingStringWriter ())
    {
      formatter.printHelp (new PrintWriter (out), 80, "foobar", header, options, 2, 2, footer, true);

      assertEquals ("usage: foobar" +
                    EOL +
                    "  Header1" +
                    EOL +
                    "  Header2" +
                    EOL +
                    EOL +
                    "  Footer1" +
                    EOL +
                    "  Footer2" +
                    EOL,
                    out.getAsString ());
    }
  }

  @Test
  public void testOptionWithoutShortFormat ()
  {
    // related to Bugzilla #19383 (CLI-67)
    final Options options = new Options ();
    options.addOption (Option.builder ("a").longOpt ("aaa").desc ("aaaaaaa"));
    options.addOption (Option.builder (null).longOpt ("bbb").desc ("bbbbbbb"));
    options.addOption (Option.builder ("c").desc ("ccccccc"));

    final HelpFormatter formatter = new HelpFormatter ();
    try (final NonBlockingStringWriter out = new NonBlockingStringWriter ())
    {
      formatter.printHelp (new PrintWriter (out), 80, "foobar", "", options, 2, 2, "", true);
      assertEquals ("usage: foobar [-a] [--bbb] [-c]" +
                    EOL +
                    "  -a,--aaa  aaaaaaa" +
                    EOL +
                    "     --bbb  bbbbbbb" +
                    EOL +
                    "  -c        ccccccc" +
                    EOL,
                    out.getAsString ());
    }
  }

  @Test
  public void testOptionWithoutShortFormat2 ()
  {
    // related to Bugzilla #27635 (CLI-26)
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
                                   .args (1)
                                   .valueSeparator ('=')
                                   .desc ("Set time limit for execution, in mintues")
                                   .build ();
    final Option age = Option.builder ("a")
                             .longOpt ("age")
                             .args (1)
                             .valueSeparator ('=')
                             .desc ("Age (in days) of cache item before being recomputed")
                             .build ();
    final Option server = Option.builder ("s")
                                .longOpt ("server")
                                .args (1)
                                .valueSeparator ('=')
                                .desc ("The NLT server address")
                                .build ();
    final Option numResults = Option.builder ("r")
                                    .longOpt ("results")
                                    .args (1)
                                    .valueSeparator ('=')
                                    .desc ("Number of results per item")
                                    .build ();
    final Option configFile = Option.builder (null)
                                    .longOpt ("config")
                                    .args (1)
                                    .valueSeparator ('=')
                                    .desc ("Use the specified configuration file")
                                    .build ();

    final Options mOptions = new Options ();
    mOptions.addOption (help);
    mOptions.addOption (version);
    mOptions.addOption (newRun);
    mOptions.addOption (trackerRun);
    mOptions.addOption (timeLimit);
    mOptions.addOption (age);
    mOptions.addOption (server);
    mOptions.addOption (numResults);
    mOptions.addOption (configFile);

    final HelpFormatter formatter = new HelpFormatter ();
    try (final NonBlockingStringWriter out = new NonBlockingStringWriter ())
    {
      formatter.printHelp (new PrintWriter (out), 80, "commandline", "header", mOptions, 2, 2, "footer", true);
      assertEquals ("usage: commandline [-a <arg>] [--config <arg>] [-h] [-l <arg>] [-n] [-r <arg>]" +
                    EOL +
                    "       [-s <arg>] [-t] [-v]" +
                    EOL +
                    "header" +
                    EOL +
                    "  -a,--age <arg>      Age (in days) of cache item before being recomputed" +
                    EOL +
                    "     --config <arg>   Use the specified configuration file" +
                    EOL +
                    "  -h,--help           print this message" +
                    EOL +
                    "  -l,--limit <arg>    Set time limit for execution, in mintues" +
                    EOL +
                    "  -n,--new            Create NLT cache entries only for new items" +
                    EOL +
                    "  -r,--results <arg>  Number of results per item" +
                    EOL +
                    "  -s,--server <arg>   The NLT server address" +
                    EOL +
                    "  -t,--tracker        Create NLT cache entries only for tracker items" +
                    EOL +
                    "  -v,--version        print version information" +
                    EOL +
                    "footer" +
                    EOL,
                    out.getAsString ());
    }
  }

  @Test
  public void testHelpWithLongOptSeparator () throws Exception
  {
    final Options options = new Options ();
    options.addOption (Option.builder ("f").args (1).desc ("the file"));
    options.addOption (Option.builder ("s").longOpt ("size").desc ("the size").args (1).argName ("SIZE"));
    options.addOption (Option.builder (null).longOpt ("age").desc ("the age").args (1));

    final HelpFormatter formatter = new HelpFormatter ();
    assertEquals (HelpFormatter.DEFAULT_LONG_OPT_SEPARATOR, formatter.getLongOptSeparator ());
    formatter.setLongOptSeparator ("=");
    assertEquals ("=", formatter.getLongOptSeparator ());

    try (final NonBlockingStringWriter out = new NonBlockingStringWriter ())
    {
      formatter.printHelp (new PrintWriter (out), 80, "create", "header", options, 2, 2, "footer");
      assertEquals ("usage: create" +
                    EOL +
                    "header" +
                    EOL +
                    "     --age=<arg>    the age" +
                    EOL +
                    "  -f <arg>          the file" +
                    EOL +
                    "  -s,--size=<SIZE>  the size" +
                    EOL +
                    "footer" +
                    EOL,
                    out.getAsString ());
    }
  }

  @Test
  public void testUsageWithLongOptSeparator () throws Exception
  {
    final Options options = new Options ().addOption (Option.builder ("f").args (1).desc ("the file"))
                                          .addOption (Option.builder ("s")
                                                            .longOpt ("size")
                                                            .desc ("the size")
                                                            .args (1)
                                                            .argName ("SIZE"))
                                          .addOption (Option.builder (null).longOpt ("age").desc ("the age").args (1));

    final HelpFormatter formatter = new HelpFormatter ();
    formatter.setLongOptSeparator ("=");

    try (final NonBlockingStringWriter out = new NonBlockingStringWriter ())
    {
      formatter.printUsage (new PrintWriter (out), 80, "create", options);

      assertEquals ("usage: create [--age=<arg>] [-f <arg>] [-s <SIZE>]", out.getAsString ().trim ());
    }
  }

  @Test
  public void testRequiredOption ()
  {
    final Options options = new Options ();
    options.addOption (Option.builder ("a").longOpt ("aaa").desc ("aaaaaaa"));
    options.addOption (Option.builder (null).longOpt ("bbb").desc ("bbbbbbb"));
    options.addOption (Option.builder ("c").desc ("ccccccc").required (true));
    options.addOption (Option.builder ("d").required (true));

    final HelpFormatter formatter = new HelpFormatter ();
    try (final NonBlockingStringWriter out = new NonBlockingStringWriter ())
    {
      formatter.printHelp (new PrintWriter (out), 80, "foobar", "", options, 2, 2, "", true);
      assertEquals ("usage: foobar [-a] [--bbb] -c -d" +
                    EOL +
                    "  -a,--aaa  aaaaaaa" +
                    EOL +
                    "     --bbb  bbbbbbb" +
                    EOL +
                    "  -c        ccccccc" +
                    EOL +
                    "  -d" +
                    EOL,
                    out.getAsString ());
    }
  }
}
