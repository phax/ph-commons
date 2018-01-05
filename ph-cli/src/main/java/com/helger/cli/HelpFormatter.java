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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Iterator;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.compare.IComparator;
import com.helger.commons.io.stream.NonBlockingBufferedReader;
import com.helger.commons.io.stream.NonBlockingStringReader;
import com.helger.commons.string.StringHelper;
import com.helger.commons.system.ENewLineMode;

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
  public static final String DEFAULT_OPT_PREFIX = CmdLineParser.PREFIX_SHORT_OPT;

  /** default prefix for long Option */
  public static final String DEFAULT_LONG_OPT_PREFIX = CmdLineParser.PREFIX_LONG_OPT;

  /**
   * default separator displayed between a long Option and its value
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
  private String m_sDefaultNewLine = ENewLineMode.DEFAULT.getText ();

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
   * Returns the 'optPrefix'. The default is {@link #DEFAULT_OPT_PREFIX}.
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
   * Returns the 'longOptPrefix'. The default is
   * {@link #DEFAULT_LONG_OPT_PREFIX}.
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
   */
  public void setLongOptSeparator (@Nonnull final String sLongOptSeparator)
  {
    ValueEnforcer.notNull (sLongOptSeparator, "LongOptSeparator");
    m_sLongOptSeparator = sLongOptSeparator;
  }

  /**
   * Returns the separator displayed between a long option and its value.
   * Defaults to {@link #DEFAULT_LONG_OPT_SEPARATOR}.
   *
   * @return the separator
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
   */
  public void setOptionComparator (@Nullable final IComparator <Option> aComparator)
  {
    m_aOptionComparator = aComparator;
  }

  /**
   * Print the help for <code>options</code> with the specified command line
   * syntax. This method prints help information to System.out.
   *
   * @param sCmdLineSyntax
   *        the syntax for this application
   * @param aOptions
   *        the Options instance
   */
  public void printHelp (@Nonnull @Nonempty final String sCmdLineSyntax, @Nonnull final Options aOptions)
  {
    printHelp (getWidth (), sCmdLineSyntax, null, aOptions, null, false);
  }

  /**
   * Print the help for <code>options</code> with the specified command line
   * syntax. This method prints help information to System.out.
   *
   * @param sCmdLineSyntax
   *        the syntax for this application
   * @param aOptions
   *        the Options instance
   * @param bAutoUsage
   *        whether to print an automatically generated usage statement
   */
  public void printHelp (@Nonnull @Nonempty final String sCmdLineSyntax,
                         @Nonnull final Options aOptions,
                         final boolean bAutoUsage)
  {
    printHelp (getWidth (), sCmdLineSyntax, null, aOptions, null, bAutoUsage);
  }

  /**
   * Print the help for <code>options</code> with the specified command line
   * syntax. This method prints help information to System.out.
   *
   * @param sCmdLineSyntax
   *        the syntax for this application
   * @param sHeader
   *        the banner to display at the beginning of the help
   * @param aOptions
   *        the Options instance
   * @param sFooter
   *        the banner to display at the end of the help
   */
  public void printHelp (@Nonnull @Nonempty final String sCmdLineSyntax,
                         @Nullable final String sHeader,
                         @Nonnull final Options aOptions,
                         @Nullable final String sFooter)
  {
    printHelp (sCmdLineSyntax, sHeader, aOptions, sFooter, false);
  }

  /**
   * Print the help for <code>options</code> with the specified command line
   * syntax. This method prints help information to System.out.
   *
   * @param sCmdLineSyntax
   *        the syntax for this application
   * @param sHeader
   *        the banner to display at the beginning of the help
   * @param aOptions
   *        the Options instance
   * @param sFooter
   *        the banner to display at the end of the help
   * @param bAutoUsage
   *        whether to print an automatically generated usage statement
   */
  public void printHelp (@Nonnull @Nonempty final String sCmdLineSyntax,
                         @Nullable final String sHeader,
                         @Nonnull final Options aOptions,
                         @Nullable final String sFooter,
                         final boolean bAutoUsage)
  {
    printHelp (getWidth (), sCmdLineSyntax, sHeader, aOptions, sFooter, bAutoUsage);
  }

  /**
   * Print the help for <code>options</code> with the specified command line
   * syntax. This method prints help information to System.out.
   *
   * @param nWidth
   *        the number of characters to be displayed on each line
   * @param sCmdLineSyntax
   *        the syntax for this application
   * @param sHeader
   *        the banner to display at the beginning of the help
   * @param aOptions
   *        the Options instance
   * @param sFooter
   *        the banner to display at the end of the help
   */
  public void printHelp (final int nWidth,
                         @Nonnull @Nonempty final String sCmdLineSyntax,
                         @Nullable final String sHeader,
                         @Nonnull final Options aOptions,
                         @Nullable final String sFooter)
  {
    printHelp (nWidth, sCmdLineSyntax, sHeader, aOptions, sFooter, false);
  }

  /**
   * Print the help for <code>options</code> with the specified command line
   * syntax. This method prints help information to System.out.
   *
   * @param nWidth
   *        the number of characters to be displayed on each line
   * @param sCmdLineSyntax
   *        the syntax for this application
   * @param sHeader
   *        the banner to display at the beginning of the help
   * @param aOptions
   *        the Options instance
   * @param sFooter
   *        the banner to display at the end of the help
   * @param bAutoUsage
   *        whether to print an automatically generated usage statement
   */
  public void printHelp (final int nWidth,
                         @Nonnull @Nonempty final String sCmdLineSyntax,
                         @Nullable final String sHeader,
                         @Nonnull final Options aOptions,
                         @Nullable final String sFooter,
                         final boolean bAutoUsage)
  {
    final PrintWriter aPW = new PrintWriter (System.out);
    printHelp (aPW,
               nWidth,
               sCmdLineSyntax,
               sHeader,
               aOptions,
               getLeftPadding (),
               getDescPadding (),
               sFooter,
               bAutoUsage);
    aPW.flush ();
  }

  /**
   * Print the help for <code>options</code> with the specified command line
   * syntax.
   *
   * @param aPW
   *        the writer to which the help will be written
   * @param nWidth
   *        the number of characters to be displayed on each line
   * @param sCmdLineSyntax
   *        the syntax for this application
   * @param sHeader
   *        the banner to display at the beginning of the help
   * @param aOptions
   *        the Options instance
   * @param nLeftPad
   *        the number of characters of padding to be prefixed to each line
   * @param nDescPad
   *        the number of characters of padding to be prefixed to each
   *        description line
   * @param sFooter
   *        the banner to display at the end of the help
   * @throws IllegalStateException
   *         if there is no room to print a line
   */
  public void printHelp (@Nonnull final PrintWriter aPW,
                         final int nWidth,
                         @Nonnull @Nonempty final String sCmdLineSyntax,
                         @Nullable final String sHeader,
                         @Nonnull final Options aOptions,
                         final int nLeftPad,
                         final int nDescPad,
                         @Nullable final String sFooter)
  {
    printHelp (aPW, nWidth, sCmdLineSyntax, sHeader, aOptions, nLeftPad, nDescPad, sFooter, false);
  }

  /**
   * Print the help for <code>options</code> with the specified command line
   * syntax.
   *
   * @param aPW
   *        the writer to which the help will be written
   * @param nWidth
   *        the number of characters to be displayed on each line
   * @param sCmdLineSyntax
   *        the syntax for this application
   * @param sHeader
   *        the banner to display at the beginning of the help
   * @param aOptions
   *        the Options instance
   * @param nLeftPad
   *        the number of characters of padding to be prefixed to each line
   * @param nDescPad
   *        the number of characters of padding to be prefixed to each
   *        description line
   * @param sFooter
   *        the banner to display at the end of the help
   * @param bAutoUsage
   *        whether to print an automatically generated usage statement
   * @throws IllegalStateException
   *         if there is no room to print a line
   */
  public void printHelp (@Nonnull final PrintWriter aPW,
                         final int nWidth,
                         @Nonnull @Nonempty final String sCmdLineSyntax,
                         @Nullable final String sHeader,
                         @Nonnull final Options aOptions,
                         final int nLeftPad,
                         final int nDescPad,
                         @Nullable final String sFooter,
                         final boolean bAutoUsage)
  {
    ValueEnforcer.notEmpty (sCmdLineSyntax, "sCmdLineSyntax");

    if (bAutoUsage)
      printUsage (aPW, nWidth, sCmdLineSyntax, aOptions);
    else
      printUsage (aPW, nWidth, sCmdLineSyntax);

    if (sHeader != null && sHeader.trim ().length () > 0)
      printWrapped (aPW, nWidth, sHeader);

    printOptions (aPW, nWidth, aOptions, nLeftPad, nDescPad);

    if (sFooter != null && sFooter.trim ().length () > 0)
      printWrapped (aPW, nWidth, sFooter);
  }

  /**
   * Prints the usage statement for the specified application.
   *
   * @param aPW
   *        The PrintWriter to print the usage statement
   * @param nWidth
   *        The number of characters to display per line
   * @param sAppName
   *        The application name
   * @param aOptions
   *        The command line Options
   */
  public void printUsage (@Nonnull final PrintWriter aPW,
                          final int nWidth,
                          final String sAppName,
                          final Options aOptions)
  {
    // initialise the string buffer
    final StringBuilder aSB = new StringBuilder (getSyntaxPrefix ()).append (sAppName).append (' ');

    // create a list for processed option groups
    final ICommonsSet <OptionGroup> aProcessedGroups = new CommonsHashSet <> ();

    final ICommonsList <Option> aOptList = aOptions.getAllResolvedOptions ();
    if (m_aOptionComparator != null)
      aOptList.sort (m_aOptionComparator);

    // iterate over the options
    for (final Iterator <Option> aIt = aOptList.iterator (); aIt.hasNext ();)
    {
      // get the next Option
      final Option aOption = aIt.next ();

      // check if the option is part of an OptionGroup
      final OptionGroup group = aOptions.getOptionGroup (aOption);

      // if the option is part of a group
      if (group != null)
      {
        // and if the group has not already been processed
        if (aProcessedGroups.add (group))
        {
          // add the usage clause
          _appendOptionGroup (aSB, group);
        }

        // otherwise the option was displayed in the group
        // previously so ignore it.
      }
      else
      {
        // if the Option is not part of an OptionGroup
        _appendOption (aSB, aOption, aOption.isRequired ());
      }

      if (aIt.hasNext ())
        aSB.append (' ');
    }

    // call printWrapped
    printWrapped (aPW, nWidth, aSB.toString ().indexOf (' ') + 1, aSB.toString ());
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
    if (m_aOptionComparator != null)
      optList.sort (m_aOptionComparator);

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

    if (aOption.hasShortOpt ())
      aSB.append (getOptPrefix ()).append (aOption.getShortOpt ());
    else
      aSB.append (getLongOptPrefix ()).append (aOption.getLongOpt ());

    // if the Option has a value and a non blank argname
    if (aOption.canHaveArgs ())
    {
      aSB.append (aOption.hasShortOpt () ? " " : getLongOptSeparator ());
      aSB.append ('<').append (aOption.hasArgName () ? aOption.getArgName () : getArgName ()).append ('>');
    }

    // if the Option is not a required option
    if (!bRequired)
      aSB.append (']');
  }

  /**
   * Print the sCmdLineSyntax to the specified writer, using the specified
   * width.
   *
   * @param aPW
   *        The printWriter to write the help to
   * @param nWidth
   *        The number of characters per line for the usage statement.
   * @param sCmdLineSyntax
   *        The usage statement.
   */
  public void printUsage (@Nonnull final PrintWriter aPW, final int nWidth, final String sCmdLineSyntax)
  {
    final int nArgPos = sCmdLineSyntax.indexOf (' ') + 1;

    printWrapped (aPW, nWidth, getSyntaxPrefix ().length () + nArgPos, getSyntaxPrefix () + sCmdLineSyntax);
  }

  /**
   * Print the help for the specified Options to the specified writer, using the
   * specified width, left padding and description padding.
   *
   * @param aPW
   *        The printWriter to write the help to
   * @param nWidth
   *        The number of characters to display per line
   * @param aOptions
   *        The command line Options
   * @param nLeftPad
   *        the number of characters of padding to be prefixed to each line
   * @param nDescPad
   *        the number of characters of padding to be prefixed to each
   *        description line
   */
  public void printOptions (@Nonnull final PrintWriter aPW,
                            final int nWidth,
                            @Nonnull final Options aOptions,
                            final int nLeftPad,
                            final int nDescPad)
  {
    final StringBuilder aSB = new StringBuilder ();
    renderOptions (aSB, nWidth, aOptions, nLeftPad, nDescPad);

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
  protected StringBuilder renderOptions (@Nonnull final StringBuilder ret,
                                         final int nWidth,
                                         @Nonnull final Options aOptions,
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

    if (m_aOptionComparator != null)
      aOptList.sort (m_aOptionComparator);

    for (final Option aOption : aOptList)
    {
      final StringBuilder aSB = new StringBuilder ();

      if (aOption.hasShortOpt ())
      {
        aSB.append (sLeftPad).append (getOptPrefix ()).append (aOption.getShortOpt ());

        if (aOption.hasLongOpt ())
          aSB.append (',').append (getLongOptPrefix ()).append (aOption.getLongOpt ());
      }
      else
      {
        aSB.append (sLeftPad).append ("   ").append (getLongOptPrefix ()).append (aOption.getLongOpt ());
      }

      if (aOption.canHaveArgs ())
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
