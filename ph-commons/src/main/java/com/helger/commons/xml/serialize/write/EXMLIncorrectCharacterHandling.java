/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.serialize.write;

import java.util.Set;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.string.StringHelper;

/**
 * Define what to do, when an invalid character is to be serialized to XML.
 *
 * @author Philip Helger
 */
public enum EXMLIncorrectCharacterHandling
{
  /**
   * Throw an {@link IllegalArgumentException} in case of incorrect XML
   * characters.
   */
  THROW_EXCEPTION (true, false)
  {
    @Override
    public void notifyOnInvalidXMLCharacter (@Nonnull @Nonempty final String sText,
                                             @Nonnull final Set <Character> aInvalidChars) throws IllegalArgumentException
    {
      throw new IllegalArgumentException ("XML content contains invalid character data: '" +
                                          sText +
                                          "'. Invalid chars are: " +
                                          _getAsString (aInvalidChars));
    }
  },

  /**
   * Write the invalid character to the file. This will result in a file that
   * cannot be read with the Java XML parser.<br>
   * This is the fastest option. This is how it was handled in ph-commons &le;
   * 3.3.6. This option will most probably result in unreadable XML files as no
   * replacement takes place!
   */
  WRITE_TO_FILE_NO_LOG (false, false)
  {
    @Override
    public void notifyOnInvalidXMLCharacter (@Nonnull @Nonempty final String sText,
                                             @Nonnull final Set <Character> aInvalidChars)
    {
      // Do nothing
    }
  },

  /**
   * Write the invalid character to the file. This will result in a file that
   * cannot be read with the Java XML parser.<br>
   * This is the second fastest option but will most probably result in
   * unreadable XML files as no replacement takes place!
   */
  WRITE_TO_FILE_LOG_WARNING (true, false)
  {
    @Override
    public void notifyOnInvalidXMLCharacter (@Nonnull @Nonempty final String sText,
                                             @Nonnull final Set <Character> aInvalidChars)
    {
      s_aLogger.warn ("XML content contains invalid character data (no replacement): '" +
                      sText +
                      "'. Invalid chars are: " +
                      _getAsString (aInvalidChars));
    }
  },

  /**
   * Do not write the invalid character to XML and do not log anything. This
   * means silently fixing the problem as the replacement is written.
   */
  DO_NOT_WRITE_NO_LOG (false, true)
  {
    @Override
    public void notifyOnInvalidXMLCharacter (@Nonnull @Nonempty final String sText,
                                             @Nonnull final Set <Character> aInvalidChars)
    {
      // Do nothing
    }
  },

  /**
   * Do not write the invalid character to XML but at least log a warning. Will
   * trigger character replacement.
   */
  DO_NOT_WRITE_LOG_WARNING (true, true)
  {
    @Override
    public void notifyOnInvalidXMLCharacter (@Nonnull @Nonempty final String sText,
                                             @Nonnull final Set <Character> aInvalidChars)
    {
      s_aLogger.warn ("XML content contains invalid character data (will replace): '" +
                      sText +
                      "'. Invalid chars are: " +
                      _getAsString (aInvalidChars));
    }
  };

  /**
   * The default setting as it was in previous versions of ph-commons
   */
  public static final EXMLIncorrectCharacterHandling DEFAULT = EXMLIncorrectCharacterHandling.WRITE_TO_FILE_NO_LOG;
  private static final Logger s_aLogger = LoggerFactory.getLogger (EXMLIncorrectCharacterHandling.class);

  private final boolean m_bIsNotifyRequired;
  private final boolean m_bReplaceWithNothing;

  /**
   * Constructor
   *
   * @param bIsNotifyRequired
   *        Should {@link #notifyOnInvalidXMLCharacter(String, Set)} be invoked
   *        for this type?
   * @param bReplaceWithNothing
   *        Should the invalid character be replaced with nothing? May only be
   *        <code>true</code> if bIsTestRequired is <code>true</code>
   */
  private EXMLIncorrectCharacterHandling (final boolean bIsNotifyRequired, final boolean bReplaceWithNothing)
  {
    m_bIsNotifyRequired = bIsNotifyRequired;
    m_bReplaceWithNothing = bReplaceWithNothing;
  }

  /**
   * @return <code>true</code> if this handling type requires a check for
   *         invalid characters.
   */
  public boolean isTestRequired ()
  {
    return m_bIsNotifyRequired || m_bReplaceWithNothing;
  }

  /**
   * @return <code>true</code> {@link #notifyOnInvalidXMLCharacter(String, Set)}
   *         should be invoked for this type?
   */
  public boolean isNotifyRequired ()
  {
    return m_bIsNotifyRequired;
  }

  /**
   * @return <code>true</code> if all invalid characters should be replaced with
   *         nothing, meaning that they are simply ignored on writing.
   */
  public boolean isReplaceWithNothing ()
  {
    return m_bReplaceWithNothing;
  }

  @Nonnull
  private static String _getAsString (@Nonnull final Set <Character> aInvalidChars)
  {
    if (CollectionHelper.isEmpty (aInvalidChars))
      return "NONE";
    final StringBuilder aSB = new StringBuilder ();
    for (final Character aChar : aInvalidChars)
    {
      if (aSB.length () > 0)
        aSB.append (", ");
      final int nChar = aChar.charValue ();
      aSB.append ("0x").append (StringHelper.getHexStringLeadingZero (nChar, 2));
    }
    return aSB.toString ();
  }

  /**
   * Called in case XML data contains an invalid character
   *
   * @param sText
   *        The XML string where the error occurs.
   * @param aInvalidChars
   *        The invalid characters detected within the text
   */
  public abstract void notifyOnInvalidXMLCharacter (@Nonnull @Nonempty String sText,
                                                    @Nonnull Set <Character> aInvalidChars);
}
