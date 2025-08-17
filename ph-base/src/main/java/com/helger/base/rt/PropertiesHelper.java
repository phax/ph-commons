/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.base.rt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.UnaryOperator;

import com.helger.annotation.WillClose;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.stream.StreamHelper;
import com.helger.base.system.SystemProperties;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Helper class to ease the use of {@link Properties} class.
 *
 * @author Philip Helger
 */
@Immutable
public final class PropertiesHelper
{
  // No logger here!

  private PropertiesHelper ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static Map <String, String> getAsStringMap (@Nonnull final Properties aProps)
  {
    ValueEnforcer.notNull (aProps, "Props");

    final Map <String, String> ret = new HashMap <> ();
    for (final Map.Entry <Object, Object> aEntry : aProps.entrySet ())
      ret.put ((String) aEntry.getKey (), (String) aEntry.getValue ());
    return ret;
  }

  @Nullable
  public static NonBlockingProperties loadProperties (@Nonnull @WillClose final InputStream aIS)
  {
    ValueEnforcer.notNull (aIS, "InputStream");

    final InputStream aBufferedIS = StreamHelper.getBuffered (aIS);
    try
    {
      final NonBlockingProperties aProps = new NonBlockingProperties ();
      aProps.load (aBufferedIS);
      return aProps;
    }
    catch (final IOException ex)
    {
      return null;
    }
    finally
    {
      StreamHelper.close (aBufferedIS);
      StreamHelper.close (aIS);
    }
  }

  @Nullable
  public static NonBlockingProperties loadProperties (@Nonnull @WillClose final Reader aReader)
  {
    ValueEnforcer.notNull (aReader, "Reader");

    final Reader aBufferedReader = StreamHelper.getBuffered (aReader);
    try
    {
      final NonBlockingProperties aProps = new NonBlockingProperties ();
      aProps.load (aBufferedReader);
      return aProps;
    }
    catch (final IOException ex)
    {
      return null;
    }
    finally
    {
      StreamHelper.close (aBufferedReader);
      StreamHelper.close (aReader);
    }
  }

  /**
   * Copy of Oracle internal PropertyExpander.expand method
   *
   * @param sValue
   *        Source value. May be <code>null</code>.
   * @return <code>null</code> if source is <code>null</code>.
   * @see #expandProperties(String, UnaryOperator)
   */
  @Nullable
  public static String expandSystemProperties (@Nullable final String sValue)
  {
    return expandProperties (sValue, SystemProperties::getPropertyValue);
  }

  /**
   * Special version of {@link #expandSystemProperties(String)} that takes an arbitrary value
   * provider and is not limited to system properties.
   *
   * @param sValue
   *        Source value. May be <code>null</code>.
   * @param aValueProvider
   *        The value provider to be used. May not be <code>null</code>.
   * @return <code>null</code> if the source value is <code>null</code>.
   * @since 9.1.2
   */
  @Nullable
  public static String expandProperties (@Nullable final String sValue,
                                         @Nonnull final UnaryOperator <String> aValueProvider)
  {
    ValueEnforcer.notNull (aValueProvider, "ValueProvider");

    if (sValue == null)
      return null;

    int nIndex = sValue.indexOf ("${", 0);

    // no special characters
    if (nIndex == -1)
      return sValue;

    final StringBuilder aSB = new StringBuilder (sValue.length ());
    final int nMax = sValue.length ();
    // index of last character we copied
    int nCurPos = 0;

    while (nIndex < nMax)
    {
      if (nIndex > nCurPos)
      {
        // copy in anything before the special stuff
        aSB.append (sValue.substring (nCurPos, nIndex));
      }
      int pe = nIndex + 2;

      // do not expand ${{ ... }}
      if (pe < nMax && sValue.charAt (pe) == '{')
      {
        pe = sValue.indexOf ("}}", pe);
        if (pe == -1 || pe + 2 == nMax)
        {
          // append remaining chars
          aSB.append (sValue.substring (nIndex));
          break;
        }
        // append as normal text
        pe++;
        aSB.append (sValue.substring (nIndex, pe + 1));
      }
      else
      {
        while (pe < nMax && sValue.charAt (pe) != '}')
          pe++;

        if (pe == nMax)
        {
          // no matching '}' found, just add in as normal text
          aSB.append (sValue.substring (nIndex, pe));
          break;
        }

        final String sPropName = sValue.substring (nIndex + 2, pe);
        if (sPropName.equals ("/"))
        {
          aSB.append (File.separatorChar);
        }
        else
        {
          final String sPropVal = aValueProvider.apply (sPropName);
          if (sPropVal == null)
          {
            // Return original value
            return sValue;
          }
          aSB.append (sPropVal);
        }
      }
      nCurPos = pe + 1;
      nIndex = sValue.indexOf ("${", nCurPos);
      if (nIndex == -1)
      {
        // no more to expand. copy in any extra
        if (nCurPos < nMax)
          aSB.append (sValue.substring (nCurPos, nMax));
        // break out of loop
        break;
      }
    }
    return aSB.toString ();
  }
}
