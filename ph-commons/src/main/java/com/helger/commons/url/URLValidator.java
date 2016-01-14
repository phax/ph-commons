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
package com.helger.commons.url;

import java.util.Locale;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.regex.RegExCache;
import com.helger.commons.string.StringHelper;

/**
 * Check if a URL is valid.
 *
 * @author Philip Helger
 */
@Immutable
public final class URLValidator
{
  private static final Pattern PATTERN = RegExCache.getPattern ("(?:https?://(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?)(?:/(?:(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*~'(),]|(?:%[a-fA-F\\d]{2}))|[;:@&=])*)(?:/(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*~'(),]|(?:%[a-fA-F\\d]{2}))|[;:@&=])*))*)(?:\\?(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*~'(),]|(?:%[a-fA-F\\d]{2}))|[;:@&=])*))?)?)|" +
                                                               "(?:ftps?://(?:(?:(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[;?&=])*)(?::(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[;?&=])*))?@)?(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?))(?:/(?:(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[?:@&=])*)(?:/(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[?:@&=])*))*)(?:;type=[AIDaid])?)?)|" +
                                                               "(?:news:(?:(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[;/?:&=])+@(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3})))|(?:[a-zA-Z](?:[a-zA-Z\\d]|[_.+-])*)|\\*))|" +
                                                               "(?:nntp://(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?)/(?:[a-zA-Z](?:[a-zA-Z\\d]|[_.+-])*)(?:/(?:\\d+))?)|" +
                                                               "(?:telnet://(?:(?:(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[;?&=])*)(?::(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[;?&=])*))?@)?(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?))/?)|" +
                                                               "(?:gopher://(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?)(?:/(?:[a-zA-Z\\d\\$\\-_.+!*'(),;/?:@&=]|(?:%[a-fA-F\\d]{2}))(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),;/?:@&=]|(?:%[a-fA-F\\d]{2}))*)(?:%09(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[;:@&=])*)(?:%09(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),;/?:@&=]|(?:%[a-fA-F\\d]{2}))*))?)?)?)?)|" +
                                                               "(?:wais://(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?)/(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))*)(?:(?:/(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))*)/(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))*))|\\?(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[;:@&=])*))?)|" +
                                                               "(?:mailto:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),;/?:@&=]|(?:%[a-fA-F\\d]{2}))+))|" +
                                                               "(?:file://(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))|localhost)?/(?:(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[?:@&=])*)(?:/(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[?:@&=])*))*))|" +
                                                               "(?:prospero://(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?)/(?:(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[?:@&=])*)(?:/(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[?:@&=])*))*)(?:(?:;(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[?:@&])*)=(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[?:@&])*)))*)|" +
                                                               "(?:ldap://(?:(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?))?/(?:(?:(?:(?:(?:(?:(?:[a-zA-Z\\d]|%(?:3\\d|[46][a-fA-F\\d]|[57][Aa\\d]))|(?:%20))+|(?:OID|oid)\\.(?:(?:\\d+)(?:\\.(?:\\d+))*))(?:(?:%0[Aa])?(?:%20)*)=(?:(?:%0[Aa])?(?:%20)*))?(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))*))(?:(?:(?:%0[Aa])?(?:%20)*)\\+(?:(?:%0[Aa])?(?:%20)*)(?:(?:(?:(?:(?:[a-zA-Z\\d]|%(?:3\\d|[46][a-fA-F\\d]|[57][Aa\\d]))|(?:%20))+|(?:OID|oid)\\.(?:(?:\\d+)(?:\\.(?:\\d+))*))(?:(?:%0[Aa])?(?:%20)*)=(?:(?:%0[Aa])?(?:%20)*))?(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))*)))*)(?:(?:(?:(?:%0[Aa])?(?:%20)*)(?:[;,])(?:(?:%0[Aa])?(?:%20)*))(?:(?:(?:(?:(?:(?:[a-zA-Z\\d]|%(?:3\\d|[46][a-fA-F\\d]|[57][Aa\\d]))|(?:%20))+|(?:OID|oid)\\.(?:(?:\\d+)(?:\\.(?:\\d+))*))(?:(?:%0[Aa])?(?:%20)*)=(?:(?:%0[Aa])?(?:%20)*))?(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))*))(?:(?:(?:%0[Aa])?(?:%20)*)\\+(?:(?:%0[Aa])?(?:%20)*)(?:(?:(?:(?:(?:[a-zA-Z\\d]|%(?:3\\d|[46][a-fA-F\\d]|[57][Aa\\d]))|(?:%20))+|(?:OID|oid)\\.(?:(?:\\d+)(?:\\.(?:\\d+))*))(?:(?:%0[Aa])?(?:%20)*)=(?:(?:%0[Aa])?(?:%20)*))?(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))*)))*))*(?:(?:(?:%0[Aa])?(?:%20)*)(?:[;,])(?:(?:%0[Aa])?(?:%20)*))?)(?:\\?(?:(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))+)(?:,(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))+))*)?)(?:\\?(?:base|one|sub)(?:\\?(?:((?:[a-zA-Z\\d\\$\\-_.+!*'(),;/?:@&=]|(?:%[a-fA-F\\d]{2}))+)))?)?)?)|" +
                                                               "(?:(?:z39\\.50[rs])://(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?)(?:/(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))+)(?:\\+(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))+))*(?:\\?(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))+))?)?(?:;esn=(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))+))?(?:;rs=(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))+)(?:\\+(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))+))*)?))|" +
                                                               "(?:cid:(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[;?:@&=])*))|" +
                                                               "(?:mid:(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[;?:@&=])*)(?:/(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[;?:@&=])*))?)|" +
                                                               "(?:vemmi://(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?)(?:/(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[/?:@&=])*)(?:(?:;(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[/?:@&])*)=(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[/?:@&])*))*))?)|" +
                                                               "(?:imap://(?:(?:(?:(?:(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[&=~])+)(?:(?:;[Aa][Uu][Tt][Hh]=(?:\\*|(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[&=~])+))))?)|(?:(?:;[Aa][Uu][Tt][Hh]=(?:\\*|(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[&=~])+)))(?:(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[&=~])+))?))@)?(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?))/(?:(?:(?:(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[&=~:@/])+)?;[Tt][Yy][Pp][Ee]=(?:[Ll](?:[Ii][Ss][Tt]|[Ss][Uu][Bb])))|(?:(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[&=~:@/])+)(?:\\?(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[&=~:@/])+))?(?:(?:;[Uu][Ii][Dd][Vv][Aa][Ll][Ii][Dd][Ii][Tt][Yy]=(?:[1-9]\\d*)))?)|(?:(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[&=~:@/])+)(?:(?:;[Uu][Ii][Dd][Vv][Aa][Ll][Ii][Dd][Ii][Tt][Yy]=(?:[1-9]\\d*)))?(?:/;[Uu][Ii][Dd]=(?:[1-9]\\d*))(?:(?:/;[Ss][Ee][Cc][Tt][Ii][Oo][Nn]=(?:(?:(?:[a-zA-Z\\d\\$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[&=~:@/])+)))?)))?)|" +
                                                               "(?:nfs:(?:(?://(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?)(?:(?:/(?:(?:(?:(?:(?:[a-zA-Z\\d\\$\\-_.!~*'(),])|(?:%[a-fA-F\\d]{2})|[:@&=+])*)(?:/(?:(?:(?:[a-zA-Z\\d\\$\\-_.!~*'(),])|(?:%[a-fA-F\\d]{2})|[:@&=+])*))*)?)))?)|(?:/(?:(?:(?:(?:(?:[a-zA-Z\\d\\$\\-_.!~*'(),])|(?:%[a-fA-F\\d]{2})|[:@&=+])*)(?:/(?:(?:(?:[a-zA-Z\\d\\$\\-_.!~*'(),])|(?:%[a-fA-F\\d]{2})|[:@&=+])*))*)?))|(?:(?:(?:(?:(?:[a-zA-Z\\d\\$\\-_.!~*'(),])|(?:%[a-fA-F\\d]{2})|[:@&=+])*)(?:/(?:(?:(?:[a-zA-Z\\d\\$\\-_.!~*'(),])|(?:%[a-fA-F\\d]{2})|[:@&=+])*))*)?)))");

  @PresentForCodeCoverage
  private static final URLValidator s_aInstance = new URLValidator ();

  private URLValidator ()
  {}

  /**
   * Get the unified version of a URL. It trims leading and trailing spaces and
   * lower-cases the URL.
   *
   * @param sURL
   *        The URL to unify. May be <code>null</code>.
   * @return The unified URL or <code>null</code> if the input address is
   *         <code>null</code>.
   */
  @Nullable
  public static String getUnifiedURL (@Nullable final String sURL)
  {
    return sURL == null ? null : sURL.trim ().toLowerCase (Locale.US);
  }

  /**
   * Checks if a value is a valid URL.
   *
   * @param sURL
   *        The value validation is being performed on. A <code>null</code> or
   *        empty value is considered invalid.
   * @return <code>true</code> if URL is valid, <code>false</code> otherwise.
   */
  public static boolean isValid (@Nullable final String sURL)
  {
    if (StringHelper.hasNoText (sURL))
      return false;

    final String sUnifiedURL = getUnifiedURL (sURL);
    return PATTERN.matcher (sUnifiedURL).matches ();
  }
}
