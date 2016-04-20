package com.helger.commons.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsHashMap;
import com.helger.commons.collection.ext.ICommonsMap;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resource.URLResource;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.system.SystemProperties;
import com.helger.commons.url.ISimpleURL;

/**
 * Helper class to ease the use of {@link Properties} class.
 *
 * @author Philip Helger
 */
@Immutable
public final class PropertiesHelper
{
  private PropertiesHelper ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsMap <String, String> getAsStringMap (@Nonnull final Properties aProps)
  {
    ValueEnforcer.notNull (aProps, "Props");

    final ICommonsMap <String, String> ret = new CommonsHashMap <> ();
    for (final Map.Entry <Object, Object> aEntry : aProps.entrySet ())
      ret.put ((String) aEntry.getKey (), (String) aEntry.getValue ());
    return ret;
  }

  @Nullable
  public static ICommonsMap <String, String> loadProperties (@Nonnull final ISimpleURL aURL)
  {
    ValueEnforcer.notNull (aURL, "URL");

    try
    {
      return loadProperties (new URLResource (aURL));
    }
    catch (final MalformedURLException ex)
    {
      return null;
    }
  }

  @Nullable
  public static ICommonsMap <String, String> loadProperties (@Nonnull final File aFile)
  {
    ValueEnforcer.notNull (aFile, "File");

    return loadProperties (new FileSystemResource (aFile));
  }

  @Nullable
  public static ICommonsMap <String, String> loadProperties (@Nonnull final IReadableResource aRes)
  {
    ValueEnforcer.notNull (aRes, "Resource");

    final InputStream aIS = aRes.getInputStream ();
    if (aIS == null)
      return null;
    return loadProperties (aIS);
  }

  @Nullable
  public static ICommonsMap <String, String> loadProperties (@Nonnull @WillClose final InputStream aIS)
  {
    ValueEnforcer.notNull (aIS, "InputStream");

    final InputStream aBufferedIS = StreamHelper.getBuffered (aIS);
    try
    {
      final Properties aProps = new Properties ();
      aProps.load (aBufferedIS);
      return getAsStringMap (aProps);
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

  /**
   * Copy of Oracle internal PropertyExpander.expand method
   * 
   * @param sValue
   *        Source value. May be <code>null</code>.
   * @return <code>null</code> if source is <code>null</code>.
   */
  @Nullable
  public static String expandSystemProperties (@Nullable final String sValue)
  {
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

    scanner: while (nIndex < nMax)
    {
      if (nIndex > nCurPos)
      {
        // copy in anything before the special stuff
        aSB.append (sValue.substring (nCurPos, nIndex));
        nCurPos = nIndex;
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
          break scanner;
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
          break scanner;
        }

        final String sPropName = sValue.substring (nIndex + 2, pe);
        if (sPropName.equals ("/"))
        {
          aSB.append (File.separatorChar);
        }
        else
        {
          final String sPropVal = SystemProperties.getPropertyValue (sPropName);
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
        break scanner;
      }
    }
    return aSB.toString ();
  }
}
