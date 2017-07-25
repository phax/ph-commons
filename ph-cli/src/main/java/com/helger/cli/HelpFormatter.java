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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.compare.IComparator;
import com.helger.commons.io.stream.NonBlockingBufferedReader;
import com.helger.commons.io.stream.NonBlockingStringReader;
import com.helger.commons.string.StringHelper;

/**
 * A formatter of help messages for command line options.
 * <p>
 * Example:
 * </p>
 *
 * <pre>
 * Options options = new Options ();
 * options.addOption (OptionBuilder.withLongOpt ("file")
 *                                 .withDescription ("The file to be processed")
 *                                 .hasArg ()
 *                                 .withArgName ("FILE")
 *                                 .isRequired ()
 *                                 .create ('f'));
 * options.addOption (OptionBuilder.withLongOpt ("version")
 *                                 .withDescription ("Print the version of the application")
 *                                 .create ('v'));
 * options.addOption (OptionBuilder.withLongOpt ("help").create ('h'));
 *
 * String header = "Do something useful with an input file\n\n";
 * String footer = "\nPlease report issues at http://example.com/issues";
 *
 * HelpFormatter formatter = new HelpFormatter ();
 * formatter.printHelp ("myapp", header, options, footer, true);
 * </pre>
 *
 * This produces the following output:
 *
 * <pre>
 * usage: myapp -f &lt;FILE&gt; [-h] [-v]
 * Do something useful with an input file
 *
 *  -f,--file &lt;FILE&gt;   The file to be processed
 *  -h,--help
 *  -v,--version       Print the version of the application
 *
 * Please report issues at http://example.com/issues
 * </pre>
 */
public class HelpFormatter
{
  /** default number of characters per line */
  public static final int DEFAULT_WIDTH = 74;

  /** default padding to the left of each line */
  public static final int DEFAULT_LEFT_PAD = 1;

  /** number of space characters to be prefixed to each description line */
  public static final int DEFAULT_DESC_PAD = 3;

  /** the string to display at the beginning of the usage statement */
  public static final String DEFAULT_SYNTAX_PREFIX = "usage: ";

  /** default prefix for shortOpts */
  public static final String DEFAULT_OPT_PREFIX = CommandLineParser.PREFIX_SHORT_OPT;

  /** default prefix for long Option */
  public static final String DEFAULT_LONG_OPT_PREFIX = CommandLineParser.PREFIX_LONG_OPT;

  /**
   * default separator displayed between a long Option and its value
   *
   * @since 1.3
   **/
  public static final String DEFAULT_LONG_OPT_SEPARATOR = " ";

  /** default name for an argument */
  public static final String DEFAULT_ARG_NAME = "arg";

  /**
   * number of characters per line
   */
  private int m_nDefaultWidth = DEFAULT_WIDTH;

  /**
   * amount of padding to the left of each line
   */
  private int m_nDefaultLeftPad = DEFAULT_LEFT_PAD;

  /**
   * the number of characters of padding to be prefixed to each description line
   */
  private int m_nDefaultDescPad = DEFAULT_DESC_PAD;

  /**
   * the string to display at the beginning of the usage statement
   */
  private String m_sDefaultSyntaxPrefix = DEFAULT_SYNTAX_PREFIX;

  /**
   * the new line string
   */
  private String m_sDefaultNewLine = System.getProperty ("line.separator");

  /**
   * the shortOpt prefix
   */
  private String m_sOptPrefix = DEFAULT_OPT_PREFIX;

  /**
   * the long Opt prefix
   */
  private String m_sLongOptPrefix = DEFAULT_LONG_OPT_PREFIX;

  /**
   * the name of the argument
   */
  private String m_sArgName = DEFAULT_ARG_NAME;

  /**
   * Comparator used to sort the options when they output in help text Defaults
   * to case-insensitive alphabetical sorting by option key
   */
  private IComparator <Option> m_aOptionComparator = (o1, o2) -> o1.getKey ().compareToIgnoreCase (o2.getKey ());

  /** The separator displayed between the long option and its value. */
  private String m_sLongOptSeparator = DEFAULT_LONG_OPT_SEPARATOR;

  /**
   * Sets the 'width'.
   *
   * @param nWidth
   *        the new value of 'width'
   */
  public void setWidth (final int nWidth)
  {
    m_nDefaultWidth = nWidth;
  }

  /**
   * Returns the 'width'.
   *
   * @return the 'width'
   */
  public int getWidth ()
  {
    return m_nDefaultWidth;
  }

  /**
   * Sets the 'leftPadding'.
   *
   * @param nPadding
   *        the new value of 'leftPadding'
   */
  public void setLeftPadding (final int nPadding)
  {
    m_nDefaultLeftPad = nPadding;
  }

  /**
   * Returns the 'leftPadding'.
   *
   * @return the 'leftPadding'
   */
  public int getLeftPadding ()
  {
    return m_nDefaultLeftPad;
  }

  /**
   * Sets the 'descPadding'.
   *
   * @param nPadding
   *        the new value of 'descPadding'
   */
  public void setDescPadding (final int nPadding)
  {
    m_nDefaultDescPad = nPadding;
  }

  /**
   * Returns the 'descPadding'.
   *
   * @return the 'descPadding'
   */
  public int getDescPadding ()
  {
    return m_nDefaultDescPad;
  }

  /**
   * Sets the 'syntaxPrefix'.
   *
   * @param sPrefix
   *        the new value of 'syntaxPrefix'
   */
  public void setSyntaxPrefix (@Nonnull final String sPrefix)
  {
    ValueEnforcer.notNull (sPrefix, "Prefix");
    m_sDefaultSyntaxPrefix = sPrefix;
  }

  /**
   * Returns the 'syntaxPrefix'.
   *
   * @return the 'syntaxPrefix'
   */
  @Nonnull
  public String getSyntaxPrefix ()
  {
    return m_sDefaultSyntaxPrefix;
  }

  /**
   * Sets the 'newLine'.
   *
   * @param sNewLine
   *        the new value of 'newLine'
   */
  public void setNewLine (@Nonnull final String sNewLine)
  {
    ValueEnforcer.notNull (sNewLine, "NewLine");
    m_sDefaultNewLine = sNewLine;
  }

  /**
   * Returns the 'newLine'.
   *
   * @return the 'newLine'
   */
  @Nonnull
  public String getNewLine ()
  {
    return m_sDefaultNewLine;
  }

  /**
   * Sets the 'optPrefix'.
   *
   * @param sPrefix
   *        the new value of 'optPrefix'
   */
  public void setOptPrefix (@Nonnull final String sPrefix)
  {
    ValueEnforcer.notNull (sPrefix, "Prefix");
    m_sOptPrefix = sPrefix;
  }

  /**
   * Returns the 'optPrefix'.
   *
   * @return the 'optPrefix'
   */
  @Nonnull
  public String getOptPrefix ()
  {
    return m_sOptPrefix;
  }

  /**
   * Sets the 'longOptPrefix'.
   *
   * @param sPrefix
   *        the new value of 'longOptPrefix'
   */
  public void setLongOptPrefix (@Nonnull final String sPrefix)
  {
    ValueEnforcer.notNull (sPrefix, "Prefix");
    m_sLongOptPrefix = sPrefix;
  }

  /**
   * Returns the 'longOptPrefix'.
   *
   * @return the 'longOptPrefix'
   */
  @Nonnull
  public String getLongOptPrefix ()
  {
    return m_sLongOptPrefix;
  }

  /**
   * Set the separator displayed between a long option and its value. Ensure
   * that the separator specified is supported by the parser used, typically ' '
   * or '='.
   *
   * @param sLongOptSeparator
   *        the separator, typically ' ' or '='.
   * @since 1.3
   */
  public void setLongOptSeparator (@Nonnull final String sLongOptSeparator)
  {
    ValueEnforcer.notNull (sLongOptSeparator, "LongOptSeparator");
    m_sLongOptSeparator = sLongOptSeparator;
  }

  /**
   * Returns the separator displayed between a long option and its value.
   *
   * @return the separator
   * @since 1.3
   */
  @Nonnull
  public String getLongOptSeparator ()
  {
    return m_sLongOptSeparator;
  }

  /**
   * Sets the 'argName'.
   *
   * @param sName
   *        the new value of 'argName'
   */
  public void setArgName (@Nonnull final String sName)
  {
    ValueEnforcer.notNull (sName, "Name");
    m_sArgName = sName;
  }

  /**
   * Returns the 'argName'.
   *
   * @return the 'argName'
   */
  @Nonnull
  public String getArgName ()
  {
    return m_sArgName;
  }

  /**
   * Comparator used to sort the options when they output in help text. Defaults
   * to case-insensitive alphabetical sorting by option key.
   *
   * @return the {@link Comparator} currently in use to sort the options
   * @since 1.2
   */
  @Nullable
  public IComparator <Option> getOptionComparator ()
  {
    return m_aOptionComparator;
  }

  /**
   * Set the comparator used to sort the options when they output in help text.
   * Passing in a null comparator will keep the options in the order they were
   * declared.
   *
   * @param aComparator
   *        the {@link Comparator} to use for sorting the options
   * @since 1.2
   */
  public void setOptionComparator (@Nullable final IComparator <Option> aComparator)
  {
    m_aOptionComparator = aComparator;
  }

  /**
   * Print the help for <code>options</code> with the specified command line
   * syntax. This method prints help information to System.out.
   *
   * @param cmdLineSyntax
   *        the syntax for this application
   * @param options
   *        the Options instance
   */
  public void printHelp (final String cmdLineSyntax, final Options options)
  {
    printHelp (getWidth (), cmdLineSyntax, null, options, null, false);
  }

  /**
   * Print the help for <code>options</code> with the specified command line
   * syntax. This method prints help information to System.out.
   *
   * @param cmdLineSyntax
   *        the syntax for this application
   * @param options
   *        the Options instance
   * @param autoUsage
   *        whether to print an automatically generated usage statement
   */
  public void printHelp (final String cmdLineSyntax, final Options options, final boolean autoUsage)
  {
    printHelp (getWidth (), cmdLineSyntax, null, options, null, autoUsage);
  }

  /**
   * Print the help for <code>options</code> with the specified command line
   * syntax. This method prints help information to System.out.
   *
   * @param cmdLineSyntax
   *        the syntax for this application
   * @param header
   *        the banner to display at the beginning of the help
   * @param options
   *        the Options instance
   * @param footer
   *        the banner to display at the end of the help
   */
  public void printHelp (final String cmdLineSyntax, final String header, final Options options, final String footer)
  {
    printHelp (cmdLineSyntax, header, options, footer, false);
  }

  /**
   * Print the help for <code>options</code> with the specified command line
   * syntax. This method prints help information to System.out.
   *
   * @param cmdLineSyntax
   *        the syntax for this application
   * @param header
   *        the banner to display at the beginning of the help
   * @param options
   *        the Options instance
   * @param footer
   *        the banner to display at the end of the help
   * @param autoUsage
   *        whether to print an automatically generated usage statement
   */
  public void printHelp (final String cmdLineSyntax,
                         final String header,
                         final Options options,
                         final String footer,
                         final boolean autoUsage)
  {
    printHelp (getWidth (), cmdLineSyntax, header, options, footer, autoUsage);
  }

  /**
   * Print the help for <code>options</code> with the specified command line
   * syntax. This method prints help information to System.out.
   *
   * @param width
   *        the number of characters to be displayed on each line
   * @param cmdLineSyntax
   *        the syntax for this application
   * @param header
   *        the banner to display at the beginning of the help
   * @param options
   *        the Options instance
   * @param footer
   *        the banner to display at the end of the help
   */
  public void printHelp (final int width,
                         final String cmdLineSyntax,
                         final String header,
                         final Options options,
                         final String footer)
  {
    printHelp (width, cmdLineSyntax, header, options, footer, false);
  }

  /**
   * Print the help for <code>options</code> with the specified command line
   * syntax. This method prints help information to System.out.
   *
   * @param width
   *        the number of characters to be displayed on each line
   * @param cmdLineSyntax
   *        the syntax for this application
   * @param header
   *        the banner to display at the beginning of the help
   * @param options
   *        the Options instance
   * @param footer
   *        the banner to display at the end of the help
   * @param autoUsage
   *        whether to print an automatically generated usage statement
   */
  public void printHelp (final int width,
                         final String cmdLineSyntax,
                         final String header,
                         final Options options,
                         final String footer,
                         final boolean autoUsage)
  {
    final PrintWriter pw = new PrintWriter (System.out);

    printHelp (pw, width, cmdLineSyntax, header, options, getLeftPadding (), getDescPadding (), footer, autoUsage);
    pw.flush ();
  }

  /**
   * Print the help for <code>options</code> with the specified command line
   * syntax.
   *
   * @param pw
   *        the writer to which the help will be written
   * @param width
   *        the number of characters to be displayed on each line
   * @param cmdLineSyntax
   *        the syntax for this application
   * @param header
   *        the banner to display at the beginning of the help
   * @param options
   *        the Options instance
   * @param leftPad
   *        the number of characters of padding to be prefixed to each line
   * @param descPad
   *        the number of characters of padding to be prefixed to each
   *        description line
   * @param footer
   *        the banner to display at the end of the help
   * @throws IllegalStateException
   *         if there is no room to print a line
   */
  public void printHelp (final PrintWriter pw,
                         final int width,
                         final String cmdLineSyntax,
                         final String header,
                         final Options options,
                         final int leftPad,
                         final int descPad,
                         final String footer)
  {
    printHelp (pw, width, cmdLineSyntax, header, options, leftPad, descPad, footer, false);
  }

  /**
   * Print the help for <code>options</code> with the specified command line
   * syntax.
   *
   * @param aPW
   *        the writer to which the help will be written
   * @param nWidth
   *        the number of characters to be displayed on each line
   * @param cmdLineSyntax
   *        the syntax for this application
   * @param header
   *        the banner to display at the beginning of the help
   * @param options
   *        the Options instance
   * @param leftPad
   *        the number of characters of padding to be prefixed to each line
   * @param descPad
   *        the number of characters of padding to be prefixed to each
   *        description line
   * @param footer
   *        the banner to display at the end of the help
   * @param bAutoUsage
   *        whether to print an automatically generated usage statement
   * @throws IllegalStateException
   *         if there is no room to print a line
   */
  public void printHelp (final PrintWriter aPW,
                         final int nWidth,
                         @Nonnull @Nonempty final String cmdLineSyntax,
                         @Nullable final String header,
                         final Options options,
                         final int leftPad,
                         final int descPad,
                         @Nullable final String footer,
                         final boolean bAutoUsage)
  {
    ValueEnforcer.notEmpty (cmdLineSyntax, "cmdLineSyntax");

    if (bAutoUsage)
      printUsage (aPW, nWidth, cmdLineSyntax, options);
    else
      printUsage (aPW, nWidth, cmdLineSyntax);

    if (header != null && header.trim ().length () > 0)
      printWrapped (aPW, nWidth, header);

    printOptions (aPW, nWidth, options, leftPad, descPad);

    if (footer != null && footer.trim ().length () > 0)
      printWrapped (aPW, nWidth, footer);
  }

  /**
   * Prints the usage statement for the specified application.
   *
   * @param aPW
   *        The PrintWriter to print the usage statement
   * @param width
   *        The number of characters to display per line
   * @param app
   *        The application name
   * @param options
   *        The command line Options
   */
  public void printUsage (@Nonnull final PrintWriter aPW, final int width, final String app, final Options options)
  {
    // initialise the string buffer
    final StringBuilder buff = new StringBuilder (getSyntaxPrefix ()).append (app).append (" ");

    // create a list for processed option groups
    final ICommonsList <OptionGroup> processedGroups = new CommonsArrayList <> ();

    final ICommonsList <Option> optList = new CommonsArrayList <> (options.getAllOptions ());
    if (getOptionComparator () != null)
      optList.sort (getOptionComparator ());

    // iterate over the options
    for (final Iterator <Option> it = optList.iterator (); it.hasNext ();)
    {
      // get the next Option
      final Option option = it.next ();

      // check if the option is part of an OptionGroup
      final OptionGroup group = options.getOptionGroup (option);

      // if the option is part of a group
      if (group != null)
      {
        // and if the group has not already been processed
        if (!processedGroups.contains (group))
        {
          // add the group to the processed list
          processedGroups.add (group);

          // add the usage clause
          _appendOptionGroup (buff, group);
        }

        // otherwise the option was displayed in the group
        // previously so ignore it.
      }
      else
      {
        // if the Option is not part of an OptionGroup
        _appendOption (buff, option, option.isRequired ());
      }

      if (it.hasNext ())
        buff.append (' ');
    }

    // call printWrapped
    printWrapped (aPW, width, buff.toString ().indexOf (' ') + 1, buff.toString ());
  }

  /**
   * Appends the usage clause for an OptionGroup to a StringBuilder. The clause
   * is wrapped in square brackets if the group is required. The display of the
   * options is handled by appendOption
   *
   * @param aSB
   *        the StringBuilder to append to
   * @param aGroup
   *        the group to append
   * @see #_appendOption(StringBuilder,Option,boolean)
   */
  private void _appendOptionGroup (final StringBuilder aSB, final OptionGroup aGroup)
  {
    if (!aGroup.isRequired ())
      aSB.append ('[');

    final ICommonsList <Option> optList = aGroup.getAllOptions ();
    if (getOptionComparator () != null)
    {
      Collections.sort (optList, getOptionComparator ());
    }
    // for each option in the OptionGroup
    final Iterator <Option> it = optList.iterator ();
    while (it.hasNext ())
    {
      // whether the option is required or not is handled at group level
      _appendOption (aSB, it.next (), true);

      if (it.hasNext ())
        aSB.append (" | ");
    }

    if (!aGroup.isRequired ())
      aSB.append (']');
  }

  /**
   * Appends the usage clause for an Option to a StringBuilder.
   *
   * @param aSB
   *        the StringBuilder to append to
   * @param aOption
   *        the Option to append
   * @param bRequired
   *        whether the Option is required or not
   */
  private void _appendOption (@Nonnull final StringBuilder aSB, @Nonnull final Option aOption, final boolean bRequired)
  {
    if (!bRequired)
      aSB.append ('[');

    if (aOption.hasOpt ())
      aSB.append ("-").append (aOption.getOpt ());
    else
      aSB.append ("--").append (aOption.getLongOpt ());

    // if the Option has a value and a non blank argname
    if (aOption.hasArg ())
    {
      aSB.append (aOption.hasOpt () ? " " : getLongOptSeparator ());
      aSB.append ('<').append (aOption.hasArgName () ? aOption.getArgName () : getArgName ()).append ('>');
    }

    // if the Option is not a required option
    if (!bRequired)
      aSB.append (']');
  }

  /**
   * Print the cmdLineSyntax to the specified writer, using the specified width.
   *
   * @param aPW
   *        The printWriter to write the help to
   * @param width
   *        The number of characters per line for the usage statement.
   * @param cmdLineSyntax
   *        The usage statement.
   */
  public void printUsage (@Nonnull final PrintWriter aPW, final int width, final String cmdLineSyntax)
  {
    final int nArgPos = cmdLineSyntax.indexOf (' ') + 1;

    printWrapped (aPW, width, getSyntaxPrefix ().length () + nArgPos, getSyntaxPrefix () + cmdLineSyntax);
  }

  /**
   * Print the help for the specified Options to the specified writer, using the
   * specified width, left padding and description padding.
   *
   * @param aPW
   *        The printWriter to write the help to
   * @param width
   *        The number of characters to display per line
   * @param options
   *        The command line Options
   * @param leftPad
   *        the number of characters of padding to be prefixed to each line
   * @param descPad
   *        the number of characters of padding to be prefixed to each
   *        description line
   */
  public void printOptions (final PrintWriter aPW,
                            final int width,
                            final Options options,
                            final int leftPad,
                            final int descPad)
  {
    final StringBuilder aSB = new StringBuilder ();
    renderOptions (aSB, width, options, leftPad, descPad);

    aPW.println (aSB.toString ());
  }

  /**
   * Print the specified text to the specified PrintWriter.
   *
   * @param aPW
   *        The printWriter to write the help to
   * @param nWidth
   *        The number of characters to display per line
   * @param sText
   *        The text to be written to the PrintWriter
   */
  public void printWrapped (@Nonnull final PrintWriter aPW, final int nWidth, @Nonnull final String sText)
  {
    printWrapped (aPW, nWidth, 0, sText);
  }

  /**
   * Print the specified text to the specified PrintWriter.
   *
   * @param aPW
   *        The printWriter to write the help to
   * @param nWidth
   *        The number of characters to display per line
   * @param nNextLineTabStop
   *        The position on the next line for the first tab.
   * @param sText
   *        The text to be written to the PrintWriter
   */
  public void printWrapped (@Nonnull final PrintWriter aPW,
                            final int nWidth,
                            final int nNextLineTabStop,
                            @Nonnull final String sText)
  {
    final StringBuilder aSB = new StringBuilder (sText.length ());

    _renderWrappedTextBlock (aSB, nWidth, nNextLineTabStop, sText);
    aPW.println (aSB.toString ());
  }

  /**
   * Render the specified Options and return the rendered Options in a
   * StringBuilder.
   *
   * @param ret
   *        The StringBuilder to place the rendered Options into.
   * @param nWidth
   *        The number of characters to display per line
   * @param aOptions
   *        The command line Options
   * @param nLeftPad
   *        the number of characters of padding to be prefixed to each line
   * @param nDescPad
   *        the number of characters of padding to be prefixed to each
   *        description line
   * @return the StringBuilder with the rendered Options contents.
   */
  protected StringBuilder renderOptions (final StringBuilder ret,
                                         final int nWidth,
                                         final Options aOptions,
                                         final int nLeftPad,
                                         final int nDescPad)
  {
    final String sLeftPad = createPadding (nLeftPad);
    final String sDescPad = createPadding (nDescPad);

    // first create list containing only <lpad>-a,--aaa where
    // -a is opt and --aaa is long opt; in parallel look for
    // the longest opt string this list will be then used to
    // sort options ascending
    int nMaxLen = 0;
    final ICommonsList <StringBuilder> aPrefixList = new CommonsArrayList <> ();
    final ICommonsList <Option> aOptList = aOptions.getAllOptions ();

    if (getOptionComparator () != null)
      aOptList.sort (getOptionComparator ());

    for (final Option aOption : aOptList)
    {
      final StringBuilder aSB = new StringBuilder ();

      if (aOption.hasOpt ())
      {
        aSB.append (sLeftPad).append (getOptPrefix ()).append (aOption.getOpt ());

        if (aOption.hasLongOpt ())
          aSB.append (',').append (getLongOptPrefix ()).append (aOption.getLongOpt ());
      }
      else
      {
        aSB.append (sLeftPad).append ("   ").append (getLongOptPrefix ()).append (aOption.getLongOpt ());
      }

      if (aOption.hasArg ())
      {
        final String argName = aOption.getArgName ();
        if (argName != null && argName.length () == 0)
        {
          // if the option has a blank argname
          aSB.append (' ');
        }
        else
        {
          aSB.append (aOption.hasLongOpt () ? getLongOptSeparator () : " ");
          aSB.append ('<').append (argName != null ? aOption.getArgName () : getArgName ()).append ('>');
        }
      }

      aPrefixList.add (aSB);
      nMaxLen = Math.max (aSB.length (), nMaxLen);
    }

    int x = 0;
    for (final Iterator <Option> it = aOptList.iterator (); it.hasNext ();)
    {
      final Option aOption = it.next ();

      final StringBuilder aSB = new StringBuilder (aPrefixList.get (x++).toString ());
      if (aSB.length () < nMaxLen)
        aSB.append (createPadding (nMaxLen - aSB.length ()));

      aSB.append (sDescPad);

      final int nNextLineTabStop = nMaxLen + nDescPad;

      if (aOption.hasDescription ())
        aSB.append (aOption.getDescription ());

      renderWrappedText (ret, nWidth, nNextLineTabStop, aSB.toString ());

      if (it.hasNext ())
        ret.append (getNewLine ());
    }

    return ret;
  }

  /**
   * Render the specified text and return the rendered Options in a
   * StringBuilder.
   *
   * @param aSB
   *        The StringBuilder to place the rendered text into.
   * @param nWidth
   *        The number of characters to display per line
   * @param nNextLineTabStop
   *        The position on the next line for the first tab.
   * @param sText
   *        The text to be rendered.
   * @return the StringBuilder with the rendered Options contents.
   */
  protected StringBuilder renderWrappedText (final StringBuilder aSB,
                                             final int nWidth,
                                             final int nNextLineTabStop,
                                             final String sText)
  {
    String text = sText;
    int pos = findWrapPos (text, nWidth, 0);
    if (pos == -1)
    {
      aSB.append (rtrim (text));
      return aSB;
    }

    aSB.append (rtrim (text.substring (0, pos))).append (getNewLine ());

    int nextLineTabStop = nNextLineTabStop;
    if (nextLineTabStop >= nWidth)
    {
      // stops infinite loop happening
      nextLineTabStop = 1;
    }

    // all following lines must be padded with nextLineTabStop space characters
    final String padding = createPadding (nextLineTabStop);

    while (true)
    {
      text = padding + text.substring (pos).trim ();
      pos = findWrapPos (text, nWidth, 0);

      if (pos == -1)
      {
        aSB.append (text);
        return aSB;
      }

      if (text.length () > nWidth && pos == nextLineTabStop - 1)
      {
        pos = nWidth;
      }

      aSB.append (rtrim (text.substring (0, pos))).append (getNewLine ());
    }
  }

  /**
   * Render the specified text width a maximum width. This method differs from
   * renderWrappedText by not removing leading spaces after a new line.
   *
   * @param aSB
   *        The StringBuilder to place the rendered text into.
   * @param nWidth
   *        The number of characters to display per line
   * @param nNextLineTabStop
   *        The position on the next line for the first tab.
   * @param sText
   *        The text to be rendered.
   * @return The provided {@link StringBuilder}
   */
  @Nonnull
  private StringBuilder _renderWrappedTextBlock (final StringBuilder aSB,
                                                 final int nWidth,
                                                 final int nNextLineTabStop,
                                                 final String sText)
  {
    try (final NonBlockingBufferedReader in = new NonBlockingBufferedReader (new NonBlockingStringReader (sText)))
    {
      String sLine;
      boolean bFirstLine = true;
      while ((sLine = in.readLine ()) != null)
      {
        if (bFirstLine)
          bFirstLine = false;
        else
          aSB.append (getNewLine ());
        renderWrappedText (aSB, nWidth, nNextLineTabStop, sLine);
      }
    }
    catch (final IOException e) // NOPMD
    {
      // cannot happen
    }

    return aSB;
  }

  /**
   * Finds the next text wrap position after <code>startPos</code> for the text
   * in <code>text</code> with the column width <code>width</code>. The wrap
   * point is the last position before startPos+width having a whitespace
   * character (space, \n, \r). If there is no whitespace character before
   * startPos+width, it will return startPos+width.
   *
   * @param sText
   *        The text being searched for the wrap position
   * @param nWidth
   *        width of the wrapped text
   * @param nStartPos
   *        position from which to start the lookup whitespace character
   * @return position on which the text must be wrapped or -1 if the wrap
   *         position is at the end of the text
   */
  @CheckForSigned
  protected static int findWrapPos (final String sText, final int nWidth, final int nStartPos)
  {
    // the line ends before the max wrap pos or a new line char found
    int pos = sText.indexOf ('\n', nStartPos);
    if (pos != -1 && pos <= nWidth)
    {
      return pos + 1;
    }

    pos = sText.indexOf ('\t', nStartPos);
    if (pos != -1 && pos <= nWidth)
    {
      return pos + 1;
    }

    if (nStartPos + nWidth >= sText.length ())
    {
      return -1;
    }

    // look for the last whitespace character before startPos+width
    for (pos = nStartPos + nWidth; pos >= nStartPos; --pos)
    {
      final char c = sText.charAt (pos);
      if (c == ' ' || c == '\n' || c == '\r')
      {
        break;
      }
    }

    // if we found it - just return
    if (pos > nStartPos)
    {
      return pos;
    }

    // if we didn't find one, simply chop at startPos+width
    pos = nStartPos + nWidth;

    return pos == sText.length () ? -1 : pos;
  }

  /**
   * Return a String of padding of length <code>len</code>.
   *
   * @param nLen
   *        The length of the String of padding to create.
   * @return The String of padding
   */
  @Nonnull
  protected static String createPadding (@Nonnegative final int nLen)
  {
    return StringHelper.getRepeated (' ', nLen);
  }

  /**
   * Remove the trailing whitespace from the specified String.
   *
   * @param sStr
   *        The String to remove the trailing padding from.
   * @return The String of without the trailing padding
   */
  @Nullable
  protected static String rtrim (@Nullable final String sStr)
  {
    if (StringHelper.hasNoText (sStr))
      return sStr;

    int pos = sStr.length ();
    while (pos > 0 && Character.isWhitespace (sStr.charAt (pos - 1)))
    {
      --pos;
    }

    return sStr.substring (0, pos);
  }
}
