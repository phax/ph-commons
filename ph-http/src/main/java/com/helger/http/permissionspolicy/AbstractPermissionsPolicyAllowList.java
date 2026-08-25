/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.http.permissionspolicy;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringImplode;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.base.trait.IGenericImplTrait;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsOrderedSet;
import com.helger.url.ISimpleURL;

/**
 * An allow list to be used in a Permissions Policy directive ({@link PermissionsPolicyDirective}).
 * It's just a convenient way to build a Permissions Policy directive value.<br>
 * An allow list is either the wildcard <code>*</code> (the feature is allowed in all browsing
 * contexts) or a - possibly empty - list of source expressions in parentheses. The empty list
 * <code>()</code> disables the feature everywhere and is therefore the state of a newly created
 * allow list.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        Implementation type
 * @since 12.4.0
 */
@NotThreadSafe
public abstract class AbstractPermissionsPolicyAllowList <IMPLTYPE extends AbstractPermissionsPolicyAllowList <IMPLTYPE>>
                                                         implements
                                                         IGenericImplTrait <IMPLTYPE>
{
  public static final String KEYWORD_ALL = "*";
  public static final String KEYWORD_SELF = "self";
  public static final String KEYWORD_SRC = "src";
  public static final String ORIGIN_PREFIX = "\"";
  public static final String ORIGIN_SUFFIX = "\"";
  public static final String LIST_PREFIX = "(";
  public static final String LIST_SUFFIX = ")";

  private boolean m_bAll = false;
  private final ICommonsOrderedSet <String> m_aList = new CommonsLinkedHashSet <> ();

  /**
   * Default constructor creating an empty allow list, meaning that the respective feature is
   * disabled in all browsing contexts.
   */
  public AbstractPermissionsPolicyAllowList ()
  {}

  private void _checkNotAll ()
  {
    ValueEnforcer.isFalse (m_bAll,
                           "The wildcard '*' may only be used alone, so no further source expression may be added");
  }

  /**
   * Check if the provided string is a valid origin for usage in an allow list. The origin must not
   * contain any character that would break the header syntax.
   *
   * @param sOrigin
   *        The origin to check. May be <code>null</code>.
   * @return <code>true</code> if the origin is valid, <code>false</code> otherwise.
   */
  public static boolean isValidOrigin (@Nullable final String sOrigin)
  {
    if (StringHelper.isEmpty (sOrigin))
    {
      // Empty origin is not allowed
      return false;
    }

    final char [] aChars = sOrigin.toCharArray ();
    for (final char c : aChars)
      if (c <= 0x20 || c == '"' || c == '(' || c == ')' || c == ',' || c == ';')
        return false;

    return true;
  }

  /**
   * @return <code>true</code> if this allow list is the wildcard <code>*</code>, <code>false</code>
   *         otherwise.
   */
  public boolean isAll ()
  {
    return m_bAll;
  }

  /**
   * @return The number of source expressions in this list. Always &ge; 0. Always 0 if
   *         {@link #isAll()} is <code>true</code>.
   */
  @Nonnegative
  public int getExpressionCount ()
  {
    return m_aList.size ();
  }

  /**
   * Use the wildcard <code>*</code>, meaning that the feature is allowed in this document and in
   * all nested browsing contexts, independent of their origin. The wildcard may only be used alone,
   * so no other source expression may have been added before.
   *
   * @return this for chaining
   */
  @NonNull
  public IMPLTYPE setAll ()
  {
    ValueEnforcer.isTrue (m_aList::isEmpty,
                          "The wildcard '*' may only be used alone, but other source expressions were already added");
    m_bAll = true;
    return thisAsT ();
  }

  /**
   * The source expression <code>self</code> allows the feature in this document and in all
   * same-origin nested browsing contexts.
   *
   * @return this for chaining
   */
  @NonNull
  public IMPLTYPE addKeywordSelf ()
  {
    _checkNotAll ();
    m_aList.add (KEYWORD_SELF);
    return thisAsT ();
  }

  /**
   * The source expression <code>src</code> allows the feature in a nested browsing context, if its
   * origin matches the URL of the <code>src</code> attribute of the respective
   * <code>&lt;iframe&gt;</code> element. This source expression only makes sense inside an
   * <code>&lt;iframe allow="..."&gt;</code> attribute and is not applicable to the
   * <code>Permissions-Policy</code> HTTP header.
   *
   * @return this for chaining
   */
  @NonNull
  public IMPLTYPE addKeywordSrc ()
  {
    _checkNotAll ();
    m_aList.add (KEYWORD_SRC);
    return thisAsT ();
  }

  /**
   * Add a specific origin. The surrounding quote characters are added automatically.
   *
   * @param aOrigin
   *        The origin to add. May not be <code>null</code>. Only the origin part (scheme, host and
   *        optional port) of the provided URL should be present.
   * @return this for chaining
   */
  @NonNull
  public IMPLTYPE addOrigin (@NonNull final ISimpleURL aOrigin)
  {
    ValueEnforcer.notNull (aOrigin, "Origin");
    return addOrigin (aOrigin.getAsString ());
  }

  /**
   * Add a specific origin. The surrounding quote characters are added automatically.
   *
   * @param sOrigin
   *        The origin to add, e.g. <code>https://example.com</code> or
   *        <code>https://*.example.com</code>. May neither be <code>null</code> nor empty and must
   *        be a valid origin.
   * @return this for chaining
   */
  @NonNull
  public IMPLTYPE addOrigin (@NonNull @Nonempty final String sOrigin)
  {
    ValueEnforcer.isTrue (isValidOrigin (sOrigin), () -> "The origin '" + sOrigin + "' is invalid!");
    _checkNotAll ();
    m_aList.add (ORIGIN_PREFIX + sOrigin + ORIGIN_SUFFIX);
    return thisAsT ();
  }

  /**
   * @return The whole allow list as a single string, either <code>*</code> or the source
   *         expressions separated by a blank char and surrounded by parentheses. Neither
   *         <code>null</code> nor empty.
   */
  @NonNull
  @Nonempty
  public String getAsString ()
  {
    if (m_bAll)
      return KEYWORD_ALL;
    return LIST_PREFIX + StringImplode.getImploded (' ', m_aList) + LIST_SUFFIX;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final AbstractPermissionsPolicyAllowList <?> rhs = (AbstractPermissionsPolicyAllowList <?>) o;
    return m_bAll == rhs.m_bAll && m_aList.equals (rhs.m_aList);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_bAll).append (m_aList).getHashCode ();
  }

  @Override
  @NonNull
  public String toString ()
  {
    return new ToStringGenerator (this).append ("All", m_bAll).append ("List", m_aList).getToString ();
  }
}
