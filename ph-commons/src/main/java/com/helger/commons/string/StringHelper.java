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
package com.helger.commons.string;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Predicate;

import com.helger.annotation.CheckForSigned;
import com.helger.annotation.CheckReturnValue;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.annotation.style.CodingStyleguideUnaware;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.CGlobal;
import com.helger.base.array.ArrayHelper;
import com.helger.base.builder.IBuilder;
import com.helger.base.enforcer.ValueEnforcer;
import com.helger.base.functional.Predicates;
import com.helger.base.math.MathHelper;
import com.helger.base.string.Strings;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.CommonsTreeSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Generic string transformation and helper methods. If you need to modify a string, start looking
 * in this class.
 *
 * @author Philip Helger
 */
@Immutable
public final class StringHelper extends Strings
{
  private static final int [] SIZE_TABLE_INT = { 9,
                                                 99,
                                                 999,
                                                 9999,
                                                 99999,
                                                 999999,
                                                 9999999,
                                                 99999999,
                                                 999999999,
                                                 Integer.MAX_VALUE };
  private static final long [] SIZE_TABLE_LONG = { 9L,
                                                   99L,
                                                   999L,
                                                   9999L,
                                                   99999L,
                                                   999999L,
                                                   9999999L,
                                                   99999999L,
                                                   999999999L,
                                                   9999999999L,
                                                   99999999999L,
                                                   999999999999L,
                                                   9999999999999L,
                                                   99999999999999L,
                                                   999999999999999L,
                                                   9999999999999999L,
                                                   99999999999999999L,
                                                   999999999999999999L,
                                                   Long.MAX_VALUE };
  @PresentForCodeCoverage
  private static final StringHelper INSTANCE = new StringHelper ();

  private StringHelper ()
  {}

  /**
   * Get the number of leading white spaces according to {@link Character#isWhitespace(char)}
   *
   * @param s
   *        The string to be parsed. May be <code>null</code>.
   * @return Always &ge; 0.
   */
  @Nonnegative
  public static int getLeadingWhitespaceCount (@Nullable final String s)
  {
    int ret = 0;
    if (s != null)
    {
      final int nMax = s.length ();
      while (ret < nMax && Character.isWhitespace (s.charAt (ret)))
        ++ret;
    }
    return ret;
  }

  /**
   * Get the number of trailing white spaces according to {@link Character#isWhitespace(char)}
   *
   * @param s
   *        The string to be parsed. May be <code>null</code>.
   * @return Always &ge; 0.
   */
  @Nonnegative
  public static int getTrailingWhitespaceCount (@Nullable final String s)
  {
    int ret = 0;
    if (s != null)
    {
      int nLast = s.length () - 1;
      while (nLast >= 0 && Character.isWhitespace (s.charAt (nLast)))
      {
        ++ret;
        --nLast;
      }
    }
    return ret;
  }

  /**
   * Get the number of specified chars, the passed string starts with.
   *
   * @param s
   *        The string to be parsed. May be <code>null</code>.
   * @param c
   *        The char to be searched.
   * @return Always &ge; 0.
   */
  @Nonnegative
  public static int getLeadingCharCount (@Nullable final String s, final char c)
  {
    int ret = 0;
    if (s != null)
    {
      final int nMax = s.length ();
      while (ret < nMax && s.charAt (ret) == c)
        ++ret;
    }
    return ret;
  }

  /**
   * Get the number of specified chars, the passed string ends with.
   *
   * @param s
   *        The string to be parsed. May be <code>null</code>.
   * @param c
   *        The char to be searched.
   * @return Always &ge; 0.
   */
  @Nonnegative
  public static int getTrailingCharCount (@Nullable final String s, final char c)
  {
    int ret = 0;
    if (s != null)
    {
      int nLast = s.length () - 1;
      while (nLast >= 0 && s.charAt (nLast) == c)
      {
        ++ret;
        --nLast;
      }
    }
    return ret;
  }

  /**
   * A simple builder that allows to implode collections of arguments with a lot of customization.
   * It used by all the "getImploded*" overloads and fulfills the requests of other use cases as
   * well.
   *
   * @author Philip Helger
   * @since 10.0.0
   */
  public static class ImploderBuilder implements IBuilder <String>
  {
    private ICommonsList <String> m_aSource;
    private String m_sSeparator;
    private int m_nOffset = 0;
    private int m_nLength = -1;
    private Predicate <String> m_aFilter;

    public ImploderBuilder ()
    {}

    @Nullable
    private static String _valueOf (@Nullable final Object o)
    {
      return o == null ? null : o.toString ();
    }

    @Nonnull
    public ImploderBuilder source (@Nullable final Iterable <?> a)
    {
      return source (a, ImploderBuilder::_valueOf);
    }

    @Nonnull
    public <T> ImploderBuilder source (@Nullable final Iterable <T> a,
                                       @Nonnull final Function <? super T, String> aMapper)
    {
      ValueEnforcer.notNull (aMapper, "Mapper");
      m_aSource = a == null ? null : new CommonsArrayList <> (a, aMapper);
      return this;
    }

    @Nonnull
    public ImploderBuilder source (@Nullable final String... a)
    {
      m_aSource = a == null ? null : new CommonsArrayList <> (a);
      return this;
    }

    @Nonnull
    @SafeVarargs
    public final <T> ImploderBuilder source (@Nullable final T... a)
    {
      return source (a, ImploderBuilder::_valueOf);
    }

    @Nonnull
    public <T> ImploderBuilder source (@Nullable final T [] a, @Nonnull final Function <? super T, String> aMapper)
    {
      ValueEnforcer.notNull (aMapper, "Mapper");
      m_aSource = a == null ? null : new CommonsArrayList <> (a, aMapper);
      return this;
    }

    @Nonnull
    public ImploderBuilder separator (final char c)
    {
      return separator (Character.toString (c));
    }

    @Nonnull
    public ImploderBuilder separator (@Nullable final String s)
    {
      m_sSeparator = s;
      return this;
    }

    /**
     * Set an offset of the source from which to start. Only values &gt; 0 have an effect.
     *
     * @param n
     *        The offset to use
     * @return this for chaining
     */
    @Nonnull
    public ImploderBuilder offset (final int n)
    {
      m_nOffset = n;
      return this;
    }

    /**
     * Set the number of source items to iterate, depending on the source offset. By default all
     * elements from start to end are used. Only values &gt; 0 have an effect.
     *
     * @param n
     *        The length to use
     * @return this for chaining
     */
    @Nonnull
    public ImploderBuilder length (final int n)
    {
      m_nLength = n;
      return this;
    }

    @Nonnull
    public ImploderBuilder filterNonEmpty ()
    {
      return filter (Strings::isNotEmpty);
    }

    @Nonnull
    public ImploderBuilder filter (@Nullable final Predicate <String> a)
    {
      m_aFilter = a;
      return this;
    }

    @Nonnull
    public String build ()
    {
      final ICommonsList <String> aSource = m_aSource;
      if (aSource == null || aSource.isEmpty () || m_nLength == 0)
        return "";

      final StringBuilder aSB = new StringBuilder ();

      // Avoid constant this access for speed
      final String sSep = m_sSeparator;
      final Predicate <String> aFilter = m_aFilter == null ? Predicates.all () : m_aFilter;

      if (m_nOffset <= 0 && m_nLength < 0)
      {
        // Iterate all
        int nElementsAdded = 0;
        for (final String sElement : aSource)
        {
          if (aFilter.test (sElement))
          {
            if (nElementsAdded > 0 && sSep != null)
              aSB.append (sSep);
            aSB.append (sElement);
            nElementsAdded++;
          }
        }
      }
      else
      {
        // Start as early as possible
        final int nStart = Math.max (0, m_nOffset);

        // End of iteration?
        int nEnd = aSource.size ();
        if (m_nLength > 0)
          nEnd = Math.min (nEnd, nStart + m_nLength);

        int nElementsAdded = 0;
        for (int i = nStart; i < nEnd; ++i)
        {
          final String sElement = aSource.get (i);
          if (aFilter.test (sElement))
          {
            if (nElementsAdded > 0 && sSep != null)
              aSB.append (sSep);
            aSB.append (sElement);
            nElementsAdded++;
          }
        }
      }
      return aSB.toString ();
    }
  }

  /**
   * @return A new {@link ImploderBuilder}.
   * @since 10.0.0
   */
  @Nonnull
  public static ImploderBuilder imploder ()
  {
    return new ImploderBuilder ();
  }

  /**
   * Get a concatenated String from all elements of the passed container, without a separator. Even
   * <code>null</code> elements are added.
   *
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImploded (@Nullable final Iterable <?> aElements)
  {
    return imploder ().source (aElements).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed container, without a separator. Even
   * <code>null</code> elements are added.
   *
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        Iterable element type
   * @since 8.5.6
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedMapped (@Nullable final Iterable <? extends ELEMENTTYPE> aElements,
                                                        @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed container, separated by the specified
   * separator string. Even <code>null</code> elements are added.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImploded (@Nonnull final String sSep, @Nullable final Iterable <?> aElements)
  {
    return imploder ().source (aElements).separator (sSep).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed container, separated by the specified
   * separator char. Even <code>null</code> elements are added.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImploded (final char cSep, @Nullable final Iterable <?> aElements)
  {
    return imploder ().source (aElements).separator (cSep).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed container, separated by the specified
   * separator string. Even <code>null</code> elements are added.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The element type of the collection to be imploded
   * @since 8.5.6
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedMapped (@Nonnull final String sSep,
                                                        @Nullable final Iterable <? extends ELEMENTTYPE> aElements,
                                                        @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).separator (sSep).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed container, separated by the specified
   * separator char. Even <code>null</code> elements are added.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The element type of the collection to be imploded
   * @since 8.5.6
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedMapped (final char cSep,
                                                        @Nullable final Iterable <? extends ELEMENTTYPE> aElements,
                                                        @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).separator (cSep).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, without a separator.
   *
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   */
  @Nonnull
  @SafeVarargs
  public static <ELEMENTTYPE> String getImploded (@Nullable final ELEMENTTYPE... aElements)
  {
    return imploder ().source (aElements).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, without a separator.
   *
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImploded (@Nullable final ELEMENTTYPE [] aElements,
                                                  @Nonnegative final int nOfs,
                                                  @Nonnegative final int nLen)
  {
    return imploder ().source (aElements).offset (nOfs).length (nLen).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, without a separator.
   *
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   * @since 8.5.6
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedMapped (@Nullable final ELEMENTTYPE [] aElements,
                                                        @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, without a separator.
   *
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   * @since 8.5.6
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedMapped (@Nullable final ELEMENTTYPE [] aElements,
                                                        @Nonnegative final int nOfs,
                                                        @Nonnegative final int nLen,
                                                        @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).offset (nOfs).length (nLen).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated by the specified
   * separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   */
  @Nonnull
  @SafeVarargs
  public static <ELEMENTTYPE> String getImploded (@Nonnull final String sSep, @Nullable final ELEMENTTYPE... aElements)
  {
    return imploder ().source (aElements).separator (sSep).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated by the specified
   * separator char.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   */
  @Nonnull
  @SafeVarargs
  public static <ELEMENTTYPE> String getImploded (final char cSep, @Nullable final ELEMENTTYPE... aElements)
  {
    return imploder ().source (aElements).separator (cSep).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated by the specified
   * separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImploded (@Nonnull final String sSep,
                                                  @Nullable final ELEMENTTYPE [] aElements,
                                                  @Nonnegative final int nOfs,
                                                  @Nonnegative final int nLen)
  {
    return imploder ().source (aElements).offset (nOfs).length (nLen).separator (sSep).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated by the specified
   * separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   * @since 8.5.6
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedMapped (@Nonnull final String sSep,
                                                        @Nullable final ELEMENTTYPE [] aElements,
                                                        @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).separator (sSep).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated by the specified
   * separator char.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   * @since 8.5.6
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedMapped (final char cSep,
                                                        @Nullable final ELEMENTTYPE [] aElements,
                                                        @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).separator (cSep).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated by the specified
   * separator char.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   * @since 8.5.6
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedMapped (final char cSep,
                                                        @Nullable final ELEMENTTYPE [] aElements,
                                                        @Nonnegative final int nOfs,
                                                        @Nonnegative final int nLen,
                                                        @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).offset (nOfs).length (nLen).separator (cSep).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated by the specified
   * separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   * @since 8.5.6
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedMapped (@Nonnull final String sSep,
                                                        @Nullable final ELEMENTTYPE [] aElements,
                                                        @Nonnegative final int nOfs,
                                                        @Nonnegative final int nLen,
                                                        @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).offset (nOfs).length (nLen).separator (sSep).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated by the specified
   * separator char.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The type of elements to be imploded.
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImploded (final char cSep,
                                                  @Nullable final ELEMENTTYPE [] aElements,
                                                  @Nonnegative final int nOfs,
                                                  @Nonnegative final int nLen)
  {
    return imploder ().source (aElements).offset (nOfs).length (nLen).separator (cSep).build ();
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty elements of the passed
   * container without a separator string. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String)} for an arbitrary number of elements.
   *
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImplodedNonEmpty (@Nullable final Iterable <String> aElements)
  {
    return imploder ().source (aElements).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty elements of the passed
   * container without a separator string. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String)} for an arbitrary number of elements.
   *
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        Iterable element type
   * @since 8.5.6
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedMappedNonEmpty (@Nullable final Iterable <? extends ELEMENTTYPE> aElements,
                                                                @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty elements of the passed
   * container without a separator string. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String)} for an arbitrary number of elements.
   *
   * @param aElements
   *        The array to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        Array component type
   * @since 8.5.6
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedMappedNonEmpty (@Nullable final ELEMENTTYPE [] aElements,
                                                                @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty elements of the passed
   * container, separated by the specified separator string. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary number of elements.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImplodedNonEmpty (@Nonnull final String sSep, @Nullable final Iterable <String> aElements)
  {
    return imploder ().source (aElements).separator (sSep).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty elements of the passed
   * container, separated by the specified separator char. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary number of elements.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImplodedNonEmpty (final char cSep, @Nullable final Iterable <String> aElements)
  {
    return imploder ().source (aElements).separator (cSep).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty elements of the passed
   * container, separated by the specified separator string. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary number of elements.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The element type of the collection to be imploded
   * @since 8.5.6
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedMappedNonEmpty (@Nonnull final String sSep,
                                                                @Nullable final Iterable <? extends ELEMENTTYPE> aElements,
                                                                @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).separator (sSep).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty elements of the passed
   * container, separated by the specified separator char. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary number of elements.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        The element type of the collection to be imploded
   * @since 8.5.6
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedMappedNonEmpty (final char cSep,
                                                                @Nullable final Iterable <? extends ELEMENTTYPE> aElements,
                                                                @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).separator (cSep).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated by the specified
   * separator string. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary number of elements.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImplodedNonEmpty (@Nonnull final String sSep, @Nullable final String... aElements)
  {
    return imploder ().source (aElements).separator (sSep).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated by the specified
   * separator char. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary number of elements.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImplodedNonEmpty (final char cSep, @Nullable final String... aElements)
  {
    return imploder ().source (aElements).separator (cSep).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated by the specified
   * separator string. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary number of elements.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        Array component type
   * @since 8.5.6
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedMappedNonEmpty (@Nonnull final String sSep,
                                                                @Nullable final ELEMENTTYPE [] aElements,
                                                                @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).separator (sSep).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated by the specified
   * separator char. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary number of elements.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        Array component type
   * @since 8.5.6
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedMappedNonEmpty (final char cSep,
                                                                @Nullable final ELEMENTTYPE [] aElements,
                                                                @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).separator (cSep).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated by the specified
   * separator string. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary number of elements.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImplodedNonEmpty (@Nonnull final String sSep,
                                            @Nullable final String [] aElements,
                                            @Nonnegative final int nOfs,
                                            @Nonnegative final int nLen)
  {
    return imploder ().source (aElements).separator (sSep).offset (nOfs).length (nLen).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated by the specified
   * separator char. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary number of elements.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImplodedNonEmpty (final char cSep,
                                            @Nullable final String [] aElements,
                                            @Nonnegative final int nOfs,
                                            @Nonnegative final int nLen)
  {
    return imploder ().source (aElements).separator (cSep).offset (nOfs).length (nLen).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated by the specified
   * separator string. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary number of elements.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        Array component type
   * @since 8.5.6
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedMappedNonEmpty (@Nonnull final String sSep,
                                                                @Nullable final ELEMENTTYPE [] aElements,
                                                                @Nonnegative final int nOfs,
                                                                @Nonnegative final int nLen,
                                                                @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper)
                      .separator (sSep)
                      .offset (nOfs)
                      .length (nLen)
                      .filterNonEmpty ()
                      .build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated by the specified
   * separator char. This the very generic version of
   * {@link #getConcatenatedOnDemand(String, String, String)} for an arbitrary number of elements.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @param nOfs
   *        The offset to start from.
   * @param nLen
   *        The number of elements to implode.
   * @param aMapper
   *        The mapping function to convert from ELEMENTTYPE to String. May not be
   *        <code>null</code>.
   * @return The concatenated string.
   * @param <ELEMENTTYPE>
   *        Array component type
   * @since 8.5.6
   */
  @Nonnull
  public static <ELEMENTTYPE> String getImplodedMappedNonEmpty (final char cSep,
                                                                @Nullable final ELEMENTTYPE [] aElements,
                                                                @Nonnegative final int nOfs,
                                                                @Nonnegative final int nLen,
                                                                @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper)
                      .separator (cSep)
                      .offset (nOfs)
                      .length (nLen)
                      .filterNonEmpty ()
                      .build ();
  }

  /**
   * A simple builder that allows to implode maps of arguments with a lot of customization. It used
   * by all the "getImploded*" overloads and fulfills the requests of other use cases as well.
   *
   * @author Philip Helger
   * @since 10.0.0
   */
  public static class ImploderBuilderMap implements IBuilder <String>
  {
    private ICommonsMap <String, String> m_aSource;
    private String m_sSeparatorOuter;
    private String m_sSeparatorInner;
    private Predicate <String> m_aFilterKey;
    private Predicate <String> m_aFilterValue;

    public ImploderBuilderMap ()
    {}

    @Nullable
    private static String _valueOf (@Nullable final Object o)
    {
      return o == null ? null : o.toString ();
    }

    @Nonnull
    public ImploderBuilderMap source (@Nullable final Map <?, ?> a)
    {
      return source (a, ImploderBuilderMap::_valueOf, ImploderBuilderMap::_valueOf);
    }

    @Nonnull
    public <K, V> ImploderBuilderMap source (@Nullable final Map <K, V> a,
                                             @Nonnull final Function <? super K, String> aKeyMapper,
                                             @Nonnull final Function <? super V, String> aValueMapper)
    {
      ValueEnforcer.notNull (aKeyMapper, "KeyMapper");
      ValueEnforcer.notNull (aValueMapper, "ValueMapper");
      m_aSource = a == null ? null : new CommonsLinkedHashMap <> (a, aKeyMapper, aValueMapper);
      return this;
    }

    @Nonnull
    public ImploderBuilderMap separatorOuter (final char c)
    {
      return separatorOuter (Character.toString (c));
    }

    @Nonnull
    public ImploderBuilderMap separatorOuter (@Nullable final String s)
    {
      m_sSeparatorOuter = s;
      return this;
    }

    @Nonnull
    public ImploderBuilderMap separatorInner (final char c)
    {
      return separatorInner (Character.toString (c));
    }

    @Nonnull
    public ImploderBuilderMap separatorInner (@Nullable final String s)
    {
      m_sSeparatorInner = s;
      return this;
    }

    @Nonnull
    public ImploderBuilderMap filterKeyNonEmpty ()
    {
      return filterKey (Strings::isNotEmpty);
    }

    @Nonnull
    public ImploderBuilderMap filterKey (@Nullable final Predicate <String> a)
    {
      m_aFilterKey = a;
      return this;
    }

    @Nonnull
    public ImploderBuilderMap filterValueNonEmpty ()
    {
      return filterValue (Strings::isNotEmpty);
    }

    @Nonnull
    public ImploderBuilderMap filterValue (@Nullable final Predicate <String> a)
    {
      m_aFilterValue = a;
      return this;
    }

    @Nonnull
    public String build ()
    {
      final ICommonsMap <String, String> aSource = m_aSource;
      if (aSource == null || aSource.isEmpty ())
        return "";

      final StringBuilder aSB = new StringBuilder ();

      // Avoid constant this access for speed
      final String sSepOuter = m_sSeparatorOuter;
      final String sSepInner = m_sSeparatorInner;
      final Predicate <String> aFilterKey = m_aFilterKey == null ? Predicates.all () : m_aFilterKey;
      final Predicate <String> aFilterValue = m_aFilterValue == null ? Predicates.all () : m_aFilterValue;

      // Iterate all
      int nElementsAdded = 0;
      for (final Map.Entry <String, String> aEntry : aSource.entrySet ())
      {
        final String sKey = aEntry.getKey ();
        final String sValue = aEntry.getValue ();
        if (aFilterKey.test (sKey) && aFilterValue.test (sValue))
        {
          if (nElementsAdded > 0 && sSepOuter != null)
            aSB.append (sSepOuter);
          aSB.append (sKey);
          if (sSepInner != null)
            aSB.append (sSepInner);
          aSB.append (sValue);
          nElementsAdded++;
        }
      }
      return aSB.toString ();
    }
  }

  /**
   * @return A new {@link ImploderBuilderMap}.
   * @since 10.0.0
   */
  @Nonnull
  public static ImploderBuilderMap imploderMap ()
  {
    return new ImploderBuilderMap ();
  }

  /**
   * Get a concatenated String from all elements of the passed map, separated by the specified
   * separator strings.
   *
   * @param sSepOuter
   *        The separator to use for separating the map entries. May not be <code>null</code>.
   * @param sSepInner
   *        The separator to use for separating the key from the value. May not be
   *        <code>null</code>.
   * @param aElements
   *        The map to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   * @param <KEYTYPE>
   *        Map key type
   * @param <VALUETYPE>
   *        Map value type
   */
  @Nonnull
  public static <KEYTYPE, VALUETYPE> String getImploded (@Nonnull final String sSepOuter,
                                                         @Nonnull final String sSepInner,
                                                         @Nullable final Map <KEYTYPE, VALUETYPE> aElements)
  {
    return imploderMap ().source (aElements).separatorOuter (sSepOuter).separatorInner (sSepInner).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed map, separated by the specified
   * separator chars.
   *
   * @param cSepOuter
   *        The separator to use for separating the map entries.
   * @param cSepInner
   *        The separator to use for separating the key from the value.
   * @param aElements
   *        The map to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   * @param <KEYTYPE>
   *        Map key type
   * @param <VALUETYPE>
   *        Map value type
   */
  @Nonnull
  public static <KEYTYPE, VALUETYPE> String getImploded (final char cSepOuter,
                                                         final char cSepInner,
                                                         @Nullable final Map <KEYTYPE, VALUETYPE> aElements)
  {
    return imploderMap ().source (aElements).separatorOuter (cSepOuter).separatorInner (cSepInner).build ();
  }

  /**
   * Get a concatenated String from all elements of the passed map, separated by the specified
   * separator strings.
   *
   * @param sSepOuter
   *        The separator to use for separating the map entries. May not be <code>null</code>.
   * @param sSepInner
   *        The separator to use for separating the key from the value. May not be
   *        <code>null</code>.
   * @param aElements
   *        The map to convert. May be <code>null</code> or empty.
   * @param aKeyMapper
   *        The mapping function to convert from KEYTYPE to String. May not be <code>null</code>.
   * @param aValueMapper
   *        The mapping function to convert from VALUETYPE to String. May not be <code>null</code>.
   * @return The concatenated string.
   * @param <KEYTYPE>
   *        Map key type
   * @param <VALUETYPE>
   *        Map value type
   * @since 8.5.6
   */
  @Nonnull
  public static <KEYTYPE, VALUETYPE> String getImplodedMapped (@Nonnull final String sSepOuter,
                                                               @Nonnull final String sSepInner,
                                                               @Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aElements,
                                                               @Nonnull final Function <? super KEYTYPE, String> aKeyMapper,
                                                               @Nonnull final Function <? super VALUETYPE, String> aValueMapper)
  {
    return imploderMap ().source (aElements, aKeyMapper, aValueMapper)
                         .separatorOuter (sSepOuter)
                         .separatorInner (sSepInner)
                         .build ();
  }

  /**
   * Get a concatenated String from all elements of the passed map, separated by the specified
   * separator chars.
   *
   * @param cSepOuter
   *        The separator to use for separating the map entries.
   * @param cSepInner
   *        The separator to use for separating the key from the value.
   * @param aElements
   *        The map to convert. May be <code>null</code> or empty.
   * @param aKeyMapper
   *        The mapping function to convert from KEYTYPE to String. May not be <code>null</code>.
   * @param aValueMapper
   *        The mapping function to convert from VALUETYPE to String. May not be <code>null</code>.
   * @return The concatenated string.
   * @param <KEYTYPE>
   *        Map key type
   * @param <VALUETYPE>
   *        Map value type
   * @since 8.5.6
   */
  @Nonnull
  public static <KEYTYPE, VALUETYPE> String getImplodedMapped (final char cSepOuter,
                                                               final char cSepInner,
                                                               @Nullable final Map <? extends KEYTYPE, ? extends VALUETYPE> aElements,
                                                               @Nonnull final Function <? super KEYTYPE, String> aKeyMapper,
                                                               @Nonnull final Function <? super VALUETYPE, String> aValueMapper)
  {
    return imploderMap ().source (aElements, aKeyMapper, aValueMapper)
                         .separatorOuter (cSepOuter)
                         .separatorInner (cSepInner)
                         .build ();
  }

  /**
   * Take a concatenated String and return the passed String array of all elements in the passed
   * string, using specified separator char.
   *
   * @param cSep
   *        The separator to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le; 0 all items are
   *        used. If max items is 1, than the result string is returned as is. If max items is
   *        larger than the number of elements found, it has no effect.
   * @return The passed collection and never <code>null</code>.
   */
  @Nonnull
  public static String [] getExplodedArray (final char cSep,
                                            @Nullable final String sElements,
                                            @CheckForSigned final int nMaxItems)
  {
    if (nMaxItems == 1)
      return new String [] { sElements };
    if (isEmpty (sElements))
      return CGlobal.EMPTY_STRING_ARRAY;

    final int nMaxResultElements = 1 + getCharCount (sElements, cSep);
    if (nMaxResultElements == 1)
    {
      // Separator not found
      return new String [] { sElements };
    }
    final String [] ret = new String [nMaxItems < 1 ? nMaxResultElements : Math.min (nMaxResultElements, nMaxItems)];

    // Do not use RegExCache.stringReplacePattern because of package
    // dependencies
    // Do not use String.split because it trims empty tokens from the end
    int nStartIndex = 0;
    int nItemsAdded = 0;
    while (true)
    {
      final int nMatchIndex = sElements.indexOf (cSep, nStartIndex);
      if (nMatchIndex < 0)
        break;

      ret[nItemsAdded++] = sElements.substring (nStartIndex, nMatchIndex);
      // 1 == length of separator char
      nStartIndex = nMatchIndex + 1;
      if (nMaxItems > 0 && nItemsAdded == nMaxItems - 1)
      {
        // We have exactly one item the left: the rest of the string
        break;
      }
    }
    ret[nItemsAdded++] = sElements.substring (nStartIndex);
    if (nItemsAdded != ret.length)
      throw new IllegalStateException ("Added " + nItemsAdded + " but expected " + ret.length);
    return ret;
  }

  /**
   * Take a concatenated String and return the passed String array of all elements in the passed
   * string, using specified separator char.
   *
   * @param cSep
   *        The separator to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @return The passed collection and never <code>null</code>.
   */
  @Nonnull
  public static String [] getExplodedArray (final char cSep, @Nullable final String sElements)
  {
    return getExplodedArray (cSep, sElements, -1);
  }

  /**
   * Take a concatenated String and return the passed Collection of all elements in the passed
   * string, using specified separator string.
   *
   * @param <COLLTYPE>
   *        The collection type to be passed and returned
   * @param cSep
   *        The separator to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le; 0 all items are
   *        used. If max items is 1, than the result string is returned as is. If max items is
   *        larger than the number of elements found, it has no effect.
   * @param aCollection
   *        The non-<code>null</code> target collection that should be filled with the exploded
   *        elements
   * @return The passed collection and never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableObject ("The passed parameter")
  @CodingStyleguideUnaware
  public static <COLLTYPE extends Collection <String>> COLLTYPE getExploded (final char cSep,
                                                                             @Nullable final String sElements,
                                                                             final int nMaxItems,
                                                                             @Nonnull final COLLTYPE aCollection)
  {
    explode (cSep, sElements, nMaxItems, aCollection::add);
    return aCollection;
  }

  /**
   * Split the provided string by the provided separator and invoke the consumer for each matched
   * element. The number of returned items is unlimited.
   *
   * @param cSep
   *        The separator to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param aConsumer
   *        The non-<code>null</code> consumer that is invoked for each exploded element
   */
  public static void explode (final char cSep,
                              @Nullable final String sElements,
                              @Nonnull final Consumer <? super String> aConsumer)
  {
    explode (cSep, sElements, -1, aConsumer);
  }

  /**
   * Split the provided string by the provided separator and invoke the consumer for each matched
   * element. The maximum number of elements can be specified.
   *
   * @param cSep
   *        The separator to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le; 0 all items are
   *        used. If max items is 1, than the result string is returned as is. If max items is
   *        larger than the number of elements found, it has no effect.
   * @param aConsumer
   *        The non-<code>null</code> consumer that is invoked for each exploded element
   */
  public static void explode (final char cSep,
                              @Nullable final String sElements,
                              final int nMaxItems,
                              @Nonnull final Consumer <? super String> aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");

    if (nMaxItems == 1)
      aConsumer.accept (sElements);
    else
      if (isNotEmpty (sElements))
      {
        // Do not use RegExPool.stringReplacePattern because of package
        // dependencies
        // Do not use String.split because it trims empty tokens from the end
        int nStartIndex = 0;
        int nMatchIndex;
        int nItemsAdded = 0;
        while ((nMatchIndex = sElements.indexOf (cSep, nStartIndex)) >= 0)
        {
          aConsumer.accept (sElements.substring (nStartIndex, nMatchIndex));
          // 1 == length of separator char
          nStartIndex = nMatchIndex + 1;
          ++nItemsAdded;
          if (nMaxItems > 0 && nItemsAdded == nMaxItems - 1)
          {
            // We have exactly one item the left: the rest of the string
            break;
          }
        }
        aConsumer.accept (sElements.substring (nStartIndex));
      }
  }

  /**
   * Take a concatenated String and return a {@link ICommonsList} of all elements in the passed
   * string, using specified separator string.
   *
   * @param cSep
   *        The separator character to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @return The {@link ICommonsList} represented by the passed string. Never <code>null</code>. If
   *         the passed input string is <code>null</code> or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <String> getExploded (final char cSep, @Nullable final String sElements)
  {
    return getExploded (cSep, sElements, -1);
  }

  /**
   * Take a concatenated String and return a {@link ICommonsList} of all elements in the passed
   * string, using specified separator string.
   *
   * @param cSep
   *        The separator character to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le; 0 all items are
   *        used. If max items is 1, than the result string is returned as is. If max items is
   *        larger than the number of elements found, it has no effect.
   * @return The {@link ICommonsList} represented by the passed string. Never <code>null</code>. If
   *         the passed input string is <code>null</code> or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <String> getExploded (final char cSep,
                                                   @Nullable final String sElements,
                                                   final int nMaxItems)
  {
    return getExploded (cSep,
                        sElements,
                        nMaxItems,
                        nMaxItems >= 1 ? new CommonsArrayList <> (nMaxItems) : new CommonsArrayList <> ());
  }

  /**
   * Take a concatenated String and return the passed Collection of all elements in the passed
   * string, using specified separator string.
   *
   * @param <COLLTYPE>
   *        The collection type to be used and returned
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le; 0 all items are
   *        used. If max items is 1, than the result string is returned as is. If max items is
   *        larger than the number of elements found, it has no effect.
   * @param aCollection
   *        The non-<code>null</code> target collection that should be filled with the exploded
   *        elements
   * @return The passed collection and never <code>null</code>.
   */
  @Nonnull
  @CodingStyleguideUnaware
  public static <COLLTYPE extends Collection <String>> COLLTYPE getExploded (@Nonnull final String sSep,
                                                                             @Nullable final String sElements,
                                                                             final int nMaxItems,
                                                                             @Nonnull final COLLTYPE aCollection)
  {
    explode (sSep, sElements, nMaxItems, aCollection::add);
    return aCollection;
  }

  /**
   * Split the provided string by the provided separator and invoke the consumer for each matched
   * element.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param aConsumer
   *        The non-<code>null</code> consumer that is invoked for each exploded element
   */
  public static void explode (@Nonnull final String sSep,
                              @Nullable final String sElements,
                              @Nonnull final Consumer <? super String> aConsumer)
  {
    explode (sSep, sElements, -1, aConsumer);
  }

  /**
   * Split the provided string by the provided separator and invoke the consumer for each matched
   * element. The maximum number of elements can be specified.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le; 0 all items are
   *        used. If max items is 1, than the result string is returned as is. If max items is
   *        larger than the number of elements found, it has no effect.
   * @param aConsumer
   *        The non-<code>null</code> consumer that is invoked for each exploded element
   */
  public static void explode (@Nonnull final String sSep,
                              @Nullable final String sElements,
                              final int nMaxItems,
                              @Nonnull final Consumer <? super String> aConsumer)
  {
    ValueEnforcer.notNull (sSep, "Separator");
    ValueEnforcer.notNull (aConsumer, "Collection");

    if (sSep.length () == 1)
    {
      // If the separator consists only of a single character, use the
      // char-optimized version for better performance
      // Note: do it before the "hasText (sElements)" check, because the same
      // check is performed in the char version as well
      explode (sSep.charAt (0), sElements, nMaxItems, aConsumer);
    }
    else
    {
      if (nMaxItems == 1)
        aConsumer.accept (sElements);
      else
      {
        if (isNotEmpty (sElements))
        {
          // Do not use RegExPool.stringReplacePattern because of package
          // dependencies
          // Do not use String.split because it trims empty tokens from the end
          int nStartIndex = 0;
          int nItemsAdded = 0;
          while (true)
          {
            final int nMatchIndex = sElements.indexOf (sSep, nStartIndex);
            if (nMatchIndex < 0)
              break;
            aConsumer.accept (sElements.substring (nStartIndex, nMatchIndex));
            nStartIndex = nMatchIndex + sSep.length ();
            ++nItemsAdded;
            if (nMaxItems > 0 && nItemsAdded == nMaxItems - 1)
            {
              // We have exactly one item the left: the rest of the string
              break;
            }
          }
          aConsumer.accept (sElements.substring (nStartIndex));
        }
      }
    }
  }

  /**
   * Take a concatenated String and return a {@link ICommonsList} of all elements in the passed
   * string, using specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @return The {@link ICommonsList} represented by the passed string. Never <code>null</code>. If
   *         the passed input string is <code>null</code> or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <String> getExploded (@Nonnull final String sSep, @Nullable final String sElements)
  {
    return getExploded (sSep, sElements, -1);
  }

  /**
   * Take a concatenated String and return a {@link ICommonsList} of all elements in the passed
   * string, using specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le; 0 all items are
   *        used. If max items is 1, than the result string is returned as is. If max items is
   *        larger than the number of elements found, it has no effect.
   * @return The {@link ICommonsList} represented by the passed string. Never <code>null</code>. If
   *         the passed input string is <code>null</code> or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <String> getExploded (@Nonnull final String sSep,
                                                   @Nullable final String sElements,
                                                   final int nMaxItems)
  {
    return getExploded (sSep,
                        sElements,
                        nMaxItems,
                        nMaxItems >= 1 ? new CommonsArrayList <> (nMaxItems) : new CommonsArrayList <> ());
  }

  /**
   * Take a concatenated String and return a {@link Set} of all elements in the passed string, using
   * specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @return The {@link Set} represented by the passed string. Never <code>null</code>. If the
   *         passed input string is <code>null</code> or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static CommonsHashSet <String> getExplodedToSet (@Nonnull final String sSep, @Nullable final String sElements)
  {
    return getExploded (sSep, sElements, -1, new CommonsHashSet <> ());
  }

  /**
   * Take a concatenated String and return an ordered {@link CommonsLinkedHashSet} of all elements
   * in the passed string, using specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @return The ordered {@link Set} represented by the passed string. Never <code>null</code>. If
   *         the passed input string is <code>null</code> or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static CommonsLinkedHashSet <String> getExplodedToOrderedSet (@Nonnull final String sSep,
                                                                       @Nullable final String sElements)
  {
    return getExploded (sSep, sElements, -1, new CommonsLinkedHashSet <> ());
  }

  /**
   * Take a concatenated String and return a sorted {@link CommonsTreeSet} of all elements in the
   * passed string, using specified separator string.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or empty.
   * @return The sorted {@link Set} represented by the passed string. Never <code>null</code>. If
   *         the passed input string is <code>null</code> or "" an empty list is returned.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static CommonsTreeSet <String> getExplodedToSortedSet (@Nonnull final String sSep,
                                                                @Nullable final String sElements)
  {
    return getExploded (sSep, sElements, -1, new CommonsTreeSet <> ());
  }

  /**
   * Concatenate the strings sFront and sEnd. If either front or back is <code>null</code> or empty
   * only the other element is returned. If both strings are <code>null</code> or empty and empty
   * String is returned.
   *
   * @param sFront
   *        Front string. May be <code>null</code>.
   * @param sEnd
   *        May be <code>null</code>.
   * @return The concatenated string. Never <code>null</code>.
   */
  @Nonnull
  public static String getConcatenatedOnDemand (@Nullable final String sFront, @Nullable final String sEnd)
  {
    if (sFront == null)
      return sEnd == null ? "" : sEnd;
    if (sEnd == null)
      return sFront;
    return sFront + sEnd;
  }

  /**
   * Concatenate the strings sFront and sEnd by the "sSep" string. If either front or back is
   * <code>null</code> or empty, the separator is not applied.
   *
   * @param sFront
   *        Front string. May be <code>null</code>.
   * @param sSep
   *        Separator string. May be <code>null</code>.
   * @param sEnd
   *        May be <code>null</code>.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getConcatenatedOnDemand (@Nullable final String sFront,
                                                @Nullable final String sSep,
                                                @Nullable final String sEnd)
  {
    final StringBuilder aSB = new StringBuilder ();
    if (isNotEmpty (sFront))
    {
      aSB.append (sFront);
      if (isNotEmpty (sSep) && isNotEmpty (sEnd))
        aSB.append (sSep);
    }
    if (isNotEmpty (sEnd))
      aSB.append (sEnd);
    return aSB.toString ();
  }

  /**
   * Concatenate the strings sFront and sEnd by the "cSep" separator. If either front or back is
   * <code>null</code> or empty, the separator is not applied.
   *
   * @param sFront
   *        Front string. May be <code>null</code>.
   * @param cSep
   *        Separator character.
   * @param sEnd
   *        May be <code>null</code>.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getConcatenatedOnDemand (@Nullable final String sFront,
                                                final char cSep,
                                                @Nullable final String sEnd)
  {
    final StringBuilder aSB = new StringBuilder ();
    if (isNotEmpty (sFront))
    {
      aSB.append (sFront);
      if (isNotEmpty (sEnd))
        aSB.append (cSep);
    }
    if (isNotEmpty (sEnd))
      aSB.append (sEnd);
    return aSB.toString ();
  }

  /**
   * Get the provided string quoted or unquoted if it is <code>null</code>.
   *
   * @param sSource
   *        Source string. May be <code>null</code>.
   * @return The String <code>"null"</code> if the source is <code>null</code>,
   *         <code>"'" + sSource + "'"</code> otherwise.
   * @since 9.2.0
   */
  @Nonnull
  public static String getQuoted (@Nullable final String sSource)
  {
    return sSource == null ? "null" : "'" + sSource + "'";
  }

  /**
   * Append the provided string quoted or unquoted if it is <code>null</code>.
   *
   * @param aTarget
   *        The target to write to. May not be <code>null</code>.
   * @param sSource
   *        Source string. May be <code>null</code>.
   * @see #getQuoted(String)
   * @since 9.2.0
   */
  public static void appendQuoted (@Nonnull final StringBuilder aTarget, @Nullable final String sSource)
  {
    if (sSource == null)
      aTarget.append ("null");
    else
      aTarget.append ('\'').append (sSource).append ('\'');
  }

  /**
   * Append the provided string quoted or unquoted if it is <code>null</code>.
   *
   * @param aTarget
   *        The target to write to. May not be <code>null</code>.
   * @param sSource
   *        Source string. May be <code>null</code>.
   * @throws IOException
   *         in case of IO error
   * @see #getQuoted(String)
   * @since 9.2.0
   */
  public static void appendQuoted (@Nonnull final Appendable aTarget, @Nullable final String sSource) throws IOException
  {
    if (sSource == null)
      aTarget.append ("null");
    else
      aTarget.append ('\'').append (sSource).append ('\'');
  }

  public static boolean startsWith (@Nullable final CharSequence aCS, final char c)
  {
    return isNotEmpty (aCS) && aCS.charAt (0) == c;
  }

  public static boolean startsWithAny (@Nullable final CharSequence aCS, @Nullable final char [] aChars)
  {
    if (isNotEmpty (aCS) && aChars != null)
      if (ArrayHelper.contains (aChars, aCS.charAt (0)))
        return true;
    return false;
  }

  public static boolean startsWithIgnoreCase (@Nullable final CharSequence aCS, final char c)
  {
    return isNotEmpty (aCS) && Character.toLowerCase (aCS.charAt (0)) == Character.toLowerCase (c);
  }

  public static boolean startsWith (@Nullable final CharSequence aCS, @Nullable final CharSequence aSearch)
  {
    if (aCS == null || aSearch == null)
      return false;
    final int nSearchLength = aSearch.length ();
    if (nSearchLength == 0)
      return true;
    final int nCSLength = aCS.length ();
    if (nCSLength < nSearchLength)
      return false;
    return aCS.subSequence (0, nSearchLength).equals (aSearch);
  }

  public static boolean startsWith (@Nullable final String sStr, @Nullable final String sSearch)
  {
    if (sStr == null || sSearch == null)
      return false;
    final int nSearchLength = sSearch.length ();
    if (nSearchLength == 0)
      return true;
    final int nStrLength = sStr.length ();
    if (nStrLength < nSearchLength)
      return false;

    if (nSearchLength == 1)
      return sStr.charAt (0) == sSearch.charAt (0);

    return sStr.subSequence (0, nSearchLength).equals (sSearch);
  }

  public static boolean startsWithIgnoreCase (@Nullable final String sStr, @Nullable final String sSearch)
  {
    if (sStr == null || sSearch == null)
      return false;
    final int nSearchLength = sSearch.length ();
    if (nSearchLength == 0)
      return true;
    final int nStrLength = sStr.length ();
    if (nStrLength < nSearchLength)
      return false;
    return sStr.substring (0, nSearchLength).equalsIgnoreCase (sSearch);
  }

  public static boolean endsWith (@Nullable final CharSequence aCS, final char c)
  {
    return isNotEmpty (aCS) && getLastChar (aCS) == c;
  }

  public static boolean endsWithAny (@Nullable final CharSequence aCS, @Nullable final char [] aChars)
  {
    if (isNotEmpty (aCS) && aChars != null)
      if (ArrayHelper.contains (aChars, getLastChar (aCS)))
        return true;
    return false;
  }

  public static boolean endsWith (@Nullable final CharSequence aCS, @Nullable final CharSequence aSearch)
  {
    if (aCS == null || aSearch == null)
      return false;
    final int nSearchLength = aSearch.length ();
    if (nSearchLength == 0)
      return true;
    final int nCSLength = aCS.length ();
    if (nCSLength < nSearchLength)
      return false;

    if (nSearchLength == 1)
      return aCS.charAt (nCSLength - 1) == aSearch.charAt (0);

    return aCS.subSequence (nCSLength - nSearchLength, nCSLength).equals (aSearch);
  }

  public static boolean endsWith (@Nullable final String sStr, @Nullable final String sSearch)
  {
    if (sStr == null || sSearch == null)
      return false;
    final int nSearchLength = sSearch.length ();
    if (nSearchLength == 0)
      return true;
    final int nStrLength = sStr.length ();
    if (nStrLength < nSearchLength)
      return false;

    if (nSearchLength == 1)
      return sStr.charAt (nStrLength - 1) == sSearch.charAt (0);

    return sStr.startsWith (sSearch, nStrLength - nSearchLength);
  }

  public static boolean endsWithIgnoreCase (@Nullable final CharSequence aCS, final char c)
  {
    return isNotEmpty (aCS) && Character.toLowerCase (getLastChar (aCS)) == Character.toLowerCase (c);
  }

  public static boolean endsWithIgnoreCase (@Nullable final String sStr, @Nullable final String sSearch)
  {
    if (sStr == null || sSearch == null)
      return false;
    final int nSearchLength = sSearch.length ();
    if (nSearchLength == 0)
      return true;
    final int nStrLength = sStr.length ();
    if (nStrLength < nSearchLength)
      return false;
    return sStr.substring (nStrLength - nSearchLength, nStrLength).equalsIgnoreCase (sSearch);
  }

  /**
   * Get the first index of sSearch within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @return The first index of sSearch within sText or {@value #STRING_NOT_FOUND} if sSearch was
   *         not found or if any parameter was <code>null</code>.
   * @see String#indexOf(String)
   */
  public static int getIndexOf (@Nullable final String sText, @Nullable final String sSearch)
  {
    return sText != null && sSearch != null && sText.length () >= sSearch.length () ? sText.indexOf (sSearch)
                                                                                    : STRING_NOT_FOUND;
  }

  /**
   * Get the first index of sSearch within sText starting at index nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @return The first index of sSearch within sText or {@value #STRING_NOT_FOUND} if sSearch was
   *         not found or if any parameter was <code>null</code>.
   * @see String#indexOf(String,int)
   */
  public static int getIndexOf (@Nullable final String sText,
                                @Nonnegative final int nFromIndex,
                                @Nullable final String sSearch)
  {
    return sText != null && sSearch != null && (sText.length () - nFromIndex) >= sSearch.length () ? sText.indexOf (
                                                                                                                    sSearch,
                                                                                                                    nFromIndex)
                                                                                                   : STRING_NOT_FOUND;
  }

  /**
   * Get the last index of sSearch within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @return The last index of sSearch within sText or {@value #STRING_NOT_FOUND} if sSearch was not
   *         found or if any parameter was <code>null</code>.
   * @see String#lastIndexOf(String)
   */
  public static int getLastIndexOf (@Nullable final String sText, @Nullable final String sSearch)
  {
    return sText != null && sSearch != null && sText.length () >= sSearch.length () ? sText.lastIndexOf (sSearch)
                                                                                    : STRING_NOT_FOUND;
  }

  /**
   * Get the last index of sSearch within sText starting at index nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @return The last index of sSearch within sText or {@value #STRING_NOT_FOUND} if sSearch was not
   *         found or if any parameter was <code>null</code>.
   * @see String#lastIndexOf(String,int)
   */
  public static int getLastIndexOf (@Nullable final String sText,
                                    @Nonnegative final int nFromIndex,
                                    @Nullable final String sSearch)
  {
    return sText != null && sSearch != null && (sText.length () - nFromIndex) >= sSearch.length () ? sText.lastIndexOf (
                                                                                                                        sSearch,
                                                                                                                        nFromIndex)
                                                                                                   : STRING_NOT_FOUND;
  }

  /**
   * Get the first index of cSearch within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The character to search for. May be <code>null</code>.
   * @return The first index of sSearch within sText or {@value #STRING_NOT_FOUND} if cSearch was
   *         not found or if any parameter was <code>null</code>.
   * @see String#indexOf(int)
   */
  public static int getIndexOf (@Nullable final String sText, final char cSearch)
  {
    return sText != null && sText.length () >= 1 ? sText.indexOf (cSearch) : STRING_NOT_FOUND;
  }

  /**
   * Get the first index of cSearch within sText starting at index nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param cSearch
   *        The character to search for. May be <code>null</code>.
   * @return The first index of sSearch within sText or {@value #STRING_NOT_FOUND} if cSearch was
   *         not found or if any parameter was <code>null</code>.
   * @see String#indexOf(int,int)
   */
  public static int getIndexOf (@Nullable final String sText, @Nonnegative final int nFromIndex, final char cSearch)
  {
    return sText != null && (sText.length () - nFromIndex) >= 1 ? sText.indexOf (cSearch, nFromIndex)
                                                                : STRING_NOT_FOUND;
  }

  /**
   * Get the last index of cSearch within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The character to search for. May be <code>null</code>.
   * @return The last index of sSearch within sText or {@value #STRING_NOT_FOUND} if cSearch was not
   *         found or if any parameter was <code>null</code>.
   * @see String#lastIndexOf(int)
   */
  public static int getLastIndexOf (@Nullable final String sText, final char cSearch)
  {
    return sText != null && sText.length () >= 1 ? sText.lastIndexOf (cSearch) : STRING_NOT_FOUND;
  }

  /**
   * Get the last index of cSearch within sText starting at index nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param cSearch
   *        The character to search for. May be <code>null</code>.
   * @return The last index of sSearch within sText or {@value #STRING_NOT_FOUND} if cSearch was not
   *         found or if any parameter was <code>null</code>.
   * @see String#lastIndexOf(int,int)
   */
  public static int getLastIndexOf (@Nullable final String sText, @Nonnegative final int nFromIndex, final char cSearch)
  {
    return sText != null && (sText.length () - nFromIndex) >= 1 ? sText.lastIndexOf (cSearch, nFromIndex)
                                                                : STRING_NOT_FOUND;
  }

  /**
   * Get the first index of sSearch within sText ignoring case.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The first index of sSearch within sText or {@value #STRING_NOT_FOUND} if sSearch was
   *         not found or if any parameter was <code>null</code>.
   * @see String#indexOf(String)
   */
  public static int getIndexOfIgnoreCase (@Nullable final String sText,
                                          @Nullable final String sSearch,
                                          @Nonnull final Locale aSortLocale)
  {
    return sText != null && sSearch != null && sText.length () >= sSearch.length () ? sText.toLowerCase (aSortLocale)
                                                                                           .indexOf (sSearch.toLowerCase (aSortLocale))
                                                                                    : STRING_NOT_FOUND;
  }

  /**
   * Get the first index of sSearch within sText ignoring case starting at index nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The first index of sSearch within sText or {@value #STRING_NOT_FOUND} if sSearch was
   *         not found or if any parameter was <code>null</code>.
   * @see String#indexOf(String)
   */
  public static int getIndexOfIgnoreCase (@Nullable final String sText,
                                          @Nonnegative final int nFromIndex,
                                          @Nullable final String sSearch,
                                          @Nonnull final Locale aSortLocale)
  {
    return sText != null && sSearch != null && (sText.length () - nFromIndex) >= sSearch.length () ? sText.toLowerCase (
                                                                                                                        aSortLocale)
                                                                                                          .indexOf (sSearch.toLowerCase (aSortLocale),
                                                                                                                    nFromIndex)
                                                                                                   : STRING_NOT_FOUND;
  }

  /**
   * Get the last index of sSearch within sText ignoring case.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The last index of sSearch within sText or {@value #STRING_NOT_FOUND} if sSearch was not
   *         found or if any parameter was <code>null</code>.
   * @see String#lastIndexOf(String)
   */
  public static int getLastIndexOfIgnoreCase (@Nullable final String sText,
                                              @Nullable final String sSearch,
                                              @Nonnull final Locale aSortLocale)
  {
    return sText != null && sSearch != null && sText.length () >= sSearch.length () ? sText.toLowerCase (aSortLocale)
                                                                                           .lastIndexOf (sSearch.toLowerCase (aSortLocale))
                                                                                    : STRING_NOT_FOUND;
  }

  /**
   * Get the last index of sSearch within sText ignoring case starting at index nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The last index of sSearch within sText or {@value #STRING_NOT_FOUND} if sSearch was not
   *         found or if any parameter was <code>null</code>.
   * @see String#lastIndexOf(String)
   */
  public static int getLastIndexOfIgnoreCase (@Nullable final String sText,
                                              @Nonnegative final int nFromIndex,
                                              @Nullable final String sSearch,
                                              @Nonnull final Locale aSortLocale)
  {
    return sText != null && sSearch != null && (sText.length () - nFromIndex) >= sSearch.length () ? sText.toLowerCase (
                                                                                                                        aSortLocale)
                                                                                                          .lastIndexOf (sSearch.toLowerCase (aSortLocale),
                                                                                                                        nFromIndex)
                                                                                                   : STRING_NOT_FOUND;
  }

  /**
   * Get the first index of cSearch within sText ignoring case.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The char to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The first index of sSearch within sText or {@value #STRING_NOT_FOUND} if sSearch was
   *         not found or if any parameter was <code>null</code>.
   * @see String#indexOf(int)
   */
  public static int getIndexOfIgnoreCase (@Nullable final String sText,
                                          final char cSearch,
                                          @Nonnull final Locale aSortLocale)
  {
    return sText != null && sText.length () >= 1 ? sText.toLowerCase (aSortLocale)
                                                        .indexOf (Character.toLowerCase (cSearch)) : STRING_NOT_FOUND;
  }

  /**
   * Get the first index of cSearch within sText ignoring case starting at index nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param cSearch
   *        The char to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The first index of sSearch within sText or {@value #STRING_NOT_FOUND} if sSearch was
   *         not found or if any parameter was <code>null</code>.
   * @see String#indexOf(int)
   */
  public static int getIndexOfIgnoreCase (@Nullable final String sText,
                                          @Nonnegative final int nFromIndex,
                                          final char cSearch,
                                          @Nonnull final Locale aSortLocale)
  {
    return sText != null && (sText.length () - nFromIndex) >= 1 ? sText.toLowerCase (aSortLocale)
                                                                       .indexOf (Character.toLowerCase (cSearch),
                                                                                 nFromIndex) : STRING_NOT_FOUND;
  }

  /**
   * Get the last index of cSearch within sText ignoring case.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The char to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The last index of sSearch within sText or {@value #STRING_NOT_FOUND} if sSearch was not
   *         found or if any parameter was <code>null</code>.
   * @see String#lastIndexOf(int)
   */
  public static int getLastIndexOfIgnoreCase (@Nullable final String sText,
                                              final char cSearch,
                                              @Nonnull final Locale aSortLocale)
  {
    return sText != null && sText.length () >= 1 ? sText.toLowerCase (aSortLocale)
                                                        .lastIndexOf (Character.toLowerCase (cSearch))
                                                 : STRING_NOT_FOUND;
  }

  /**
   * Get the last index of cSearch within sText ignoring case starting at index nFromIndex.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param nFromIndex
   *        The index to start searching in the source string
   * @param cSearch
   *        The char to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return The last index of sSearch within sText or {@value #STRING_NOT_FOUND} if sSearch was not
   *         found or if any parameter was <code>null</code>.
   * @see String#lastIndexOf(int)
   */
  public static int getLastIndexOfIgnoreCase (@Nullable final String sText,
                                              @Nonnegative final int nFromIndex,
                                              final char cSearch,
                                              @Nonnull final Locale aSortLocale)
  {
    return sText != null && (sText.length () - nFromIndex) >= 1 ? sText.toLowerCase (aSortLocale)
                                                                       .lastIndexOf (Character.toLowerCase (cSearch),
                                                                                     nFromIndex) : STRING_NOT_FOUND;
  }

  /**
   * Check if sSearch is contained within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @return <code>true</code> if sSearch is contained in sText, <code>false</code> otherwise.
   * @see String#contains(CharSequence)
   */
  public static boolean contains (@Nullable final String sText, @Nullable final String sSearch)
  {
    return getIndexOf (sText, sSearch) != STRING_NOT_FOUND;
  }

  /**
   * Check if cSearch is contained within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The character to search for. May be <code>null</code>.
   * @return <code>true</code> if cSearch is contained in sText, <code>false</code> otherwise.
   * @see String#contains(CharSequence)
   */
  public static boolean contains (@Nullable final String sText, final char cSearch)
  {
    return getIndexOf (sText, cSearch) != STRING_NOT_FOUND;
  }

  /**
   * Check if sSearch is contained within sText ignoring case.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return <code>true</code> if sSearch is contained in sText, <code>false</code> otherwise.
   * @see String#contains(CharSequence)
   */
  public static boolean containsIgnoreCase (@Nullable final String sText,
                                            @Nullable final String sSearch,
                                            @Nonnull final Locale aSortLocale)
  {
    return getIndexOfIgnoreCase (sText, sSearch, aSortLocale) != STRING_NOT_FOUND;
  }

  /**
   * Check if cSearch is contained within sText ignoring case.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The char to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return <code>true</code> if sSearch is contained in sText, <code>false</code> otherwise.
   * @see String#indexOf(int)
   */
  public static boolean containsIgnoreCase (@Nullable final String sText,
                                            final char cSearch,
                                            @Nonnull final Locale aSortLocale)
  {
    return getIndexOfIgnoreCase (sText, cSearch, aSortLocale) != STRING_NOT_FOUND;
  }

  /**
   * Check if any of the passed searched characters is contained in the input char array.
   *
   * @param aInput
   *        The input char array. May be <code>null</code>.
   * @param aSearchChars
   *        The char array to search. May not be <code>null</code>.
   * @return <code>true</code> if at least any of the search char is contained in the input char
   *         array, <code>false</code> otherwise.
   */
  public static boolean containsAny (@Nullable final char [] aInput, @Nonnull final char [] aSearchChars)
  {
    ValueEnforcer.notNull (aSearchChars, "SearchChars");

    if (aInput != null)
      for (final char cIn : aInput)
        if (ArrayHelper.contains (aSearchChars, cIn))
          return true;
    return false;
  }

  /**
   * Check if any of the passed searched characters in contained in the input string.
   *
   * @param sInput
   *        The input string. May be <code>null</code>.
   * @param aSearchChars
   *        The char array to search. May not be <code>null</code>.
   * @return <code>true</code> if at least any of the search char is contained in the input char
   *         array, <code>false</code> otherwise.
   */
  public static boolean containsAny (@Nullable final String sInput, @Nonnull final char [] aSearchChars)
  {
    return sInput != null && containsAny (sInput.toCharArray (), aSearchChars);
  }

  /**
   * Count the number of occurrences of sSearch within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @return A non-negative number of occurrences.
   */
  @Nonnegative
  public static int getOccurrenceCount (@Nullable final String sText, @Nullable final String sSearch)
  {
    int ret = 0;
    final int nTextLength = getLength (sText);
    final int nSearchLength = getLength (sSearch);
    if (nSearchLength > 0 && nTextLength >= nSearchLength)
    {
      int nLastIndex = 0;
      int nIndex;
      do
      {
        // Start searching from the last result
        nIndex = getIndexOf (sText, nLastIndex, sSearch);
        if (nIndex != STRING_NOT_FOUND)
        {
          // Match found
          ++ret;

          // Identify the next starting position (relative index + number of
          // search strings)
          nLastIndex = nIndex + nSearchLength;
        }
      } while (nIndex != STRING_NOT_FOUND);
    }
    return ret;
  }

  /**
   * Count the number of occurrences of sSearch within sText ignoring case.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param sSearch
   *        The text to search for. May be <code>null</code>.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return A non-negative number of occurrences.
   */
  @Nonnegative
  public static int getOccurrenceCountIgnoreCase (@Nullable final String sText,
                                                  @Nullable final String sSearch,
                                                  @Nonnull final Locale aSortLocale)
  {
    return sText != null && sSearch != null ? getOccurrenceCount (sText.toLowerCase (aSortLocale),
                                                                  sSearch.toLowerCase (aSortLocale)) : 0;
  }

  /**
   * Count the number of occurrences of cSearch within sText.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The character to search for.
   * @return A non-negative number of occurrences.
   */
  @Nonnegative
  public static int getOccurrenceCount (@Nullable final String sText, final char cSearch)
  {
    int ret = 0;
    final int nTextLength = getLength (sText);
    if (nTextLength >= 1)
    {
      int nLastIndex = 0;
      int nIndex;
      do
      {
        // Start searching from the last result
        nIndex = getIndexOf (sText, nLastIndex, cSearch);
        if (nIndex != STRING_NOT_FOUND)
        {
          // Match found
          ++ret;

          // Identify the next starting position (relative index + number of
          // search strings)
          nLastIndex = nIndex + 1;
        }
      } while (nIndex != STRING_NOT_FOUND);
    }
    return ret;
  }

  /**
   * Count the number of occurrences of cSearch within sText ignoring case.
   *
   * @param sText
   *        The text to search in. May be <code>null</code>.
   * @param cSearch
   *        The character to search for.
   * @param aSortLocale
   *        The locale to be used for case unifying.
   * @return A non-negative number of occurrences.
   */
  @Nonnegative
  public static int getOccurrenceCountIgnoreCase (@Nullable final String sText,
                                                  final char cSearch,
                                                  @Nonnull final Locale aSortLocale)
  {
    return sText != null ? getOccurrenceCount (sText.toLowerCase (aSortLocale), Character.toLowerCase (cSearch)) : 0;
  }

  /**
   * Remove any leading whitespaces from the passed string.
   *
   * @param s
   *        the String to be trimmed
   * @return the original String with all leading whitespaces removed
   */
  @Nullable
  @CheckReturnValue
  public static String trimLeadingWhitespaces (@Nullable final String s)
  {
    final int n = getLeadingWhitespaceCount (s);
    return n == 0 ? s : s.substring (n);
  }

  /**
   * Remove any trailing whitespaces from the passed string.
   *
   * @param s
   *        the String to be cut
   * @return the original String with all trailing whitespaces removed
   */
  @Nullable
  @CheckReturnValue
  public static String trimTrailingWhitespaces (@Nullable final String s)
  {
    final int n = getTrailingWhitespaceCount (s);
    return n == 0 ? s : s.substring (0, s.length () - n);
  }

  /**
   * Trim the passed lead from the source value. If the source value does not start with the passed
   * lead, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param sLead
   *        The string to be trimmed of the beginning
   * @return The trimmed string, or the original input string, if the lead was not found
   * @see #trimEnd(String, String)
   * @see #trimStartAndEnd(String, String)
   * @see #trimStartAndEnd(String, String, String)
   */
  @Nullable
  @CheckReturnValue
  public static String trimStart (@Nullable final String sSrc, @Nullable final String sLead)
  {
    return startsWith (sSrc, sLead) ? sSrc.substring (sLead.length ()) : sSrc;
  }

  @Nullable
  @CheckReturnValue
  public static String trimStartRepeatedly (@Nullable final String sSrc, @Nullable final String sLead)
  {
    if (isEmpty (sSrc))
      return sSrc;

    final int nLeadLength = getLength (sLead);
    if (nLeadLength == 0)
      return sSrc;

    String ret = sSrc;
    while (startsWith (ret, sLead))
      ret = ret.substring (nLeadLength);
    return ret;
  }

  /**
   * Trim the passed lead from the source value. If the source value does not start with the passed
   * lead, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param cLead
   *        The char to be trimmed of the beginning
   * @return The trimmed string, or the original input string, if the lead was not found
   * @see #trimEnd(String, String)
   * @see #trimStartAndEnd(String, String)
   * @see #trimStartAndEnd(String, String, String)
   */
  @Nullable
  @CheckReturnValue
  public static String trimStart (@Nullable final String sSrc, final char cLead)
  {
    return startsWith (sSrc, cLead) ? sSrc.substring (1) : sSrc;
  }

  @Nullable
  @CheckReturnValue
  public static String trimStartRepeatedly (@Nullable final String sSrc, final char cLead)
  {
    if (isEmpty (sSrc))
      return sSrc;

    String ret = sSrc;
    while (startsWith (ret, cLead))
      ret = ret.substring (1);
    return ret;
  }

  /**
   * Trim the passed lead from the source value. If the source value does not start with the passed
   * lead, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param nCount
   *        The number of characters to trim at the end.
   * @return The trimmed string, or an empty string if nCount is &ge; the length of the source
   *         string
   */
  @Nullable
  @CheckReturnValue
  public static String trimStart (@Nullable final String sSrc, @Nonnegative final int nCount)
  {
    if (nCount <= 0)
      return sSrc;
    return getLength (sSrc) <= nCount ? "" : sSrc.substring (nCount);
  }

  /**
   * Trim the passed tail from the source value. If the source value does not end with the passed
   * tail, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param sTail
   *        The string to be trimmed of the end
   * @return The trimmed string, or the original input string, if the tail was not found
   * @see #trimStart(String, String)
   * @see #trimStartAndEnd(String, String)
   * @see #trimStartAndEnd(String, String, String)
   */
  @Nullable
  @CheckReturnValue
  public static String trimEnd (@Nullable final String sSrc, @Nullable final String sTail)
  {
    return endsWith (sSrc, sTail) ? sSrc.substring (0, sSrc.length () - sTail.length ()) : sSrc;
  }

  @Nullable
  @CheckReturnValue
  public static String trimEndRepeatedly (@Nullable final String sSrc, @Nullable final String sTail)
  {
    if (isEmpty (sSrc))
      return sSrc;

    final int nTailLength = getLength (sTail);
    if (nTailLength == 0)
      return sSrc;

    String ret = sSrc;
    while (endsWith (ret, sTail))
      ret = ret.substring (0, ret.length () - nTailLength);
    return ret;
  }

  /**
   * Trim the passed tail from the source value. If the source value does not end with the passed
   * tail, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param cTail
   *        The char to be trimmed of the end
   * @return The trimmed string, or the original input string, if the tail was not found
   * @see #trimStart(String, String)
   * @see #trimStartAndEnd(String, String)
   * @see #trimStartAndEnd(String, String, String)
   */
  @Nullable
  @CheckReturnValue
  public static String trimEnd (@Nullable final String sSrc, final char cTail)
  {
    return endsWith (sSrc, cTail) ? sSrc.substring (0, sSrc.length () - 1) : sSrc;
  }

  @Nullable
  @CheckReturnValue
  public static String trimEndRepeatedly (@Nullable final String sSrc, final char cTail)
  {
    if (isEmpty (sSrc))
      return sSrc;

    String ret = sSrc;
    while (endsWith (ret, cTail))
      ret = ret.substring (0, ret.length () - 1);
    return ret;
  }

  /**
   * Trim the passed tail from the source value. If the source value does not end with the passed
   * tail, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param nCount
   *        The number of characters to trim at the end.
   * @return The trimmed string, or an empty string if nCount is &ge; the length of the source
   *         string
   */
  @Nullable
  @CheckReturnValue
  public static String trimEnd (@Nullable final String sSrc, @Nonnegative final int nCount)
  {
    if (nCount <= 0)
      return sSrc;
    return getLength (sSrc) <= nCount ? "" : sSrc.substring (0, sSrc.length () - nCount);
  }

  /**
   * Trim the passed lead and tail from the source value. If the source value does not start with
   * the passed trimmed value, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param sValueToTrim
   *        The string to be trimmed of the beginning and the end
   * @return The trimmed string, or the original input string, if the value to trim was not found
   * @see #trimStart(String, String)
   * @see #trimEnd(String, String)
   * @see #trimStartAndEnd(String, String, String)
   */
  @Nullable
  @CheckReturnValue
  public static String trimStartAndEnd (@Nullable final String sSrc, @Nullable final String sValueToTrim)
  {
    return trimStartAndEnd (sSrc, sValueToTrim, sValueToTrim);
  }

  /**
   * Trim the passed lead and tail from the source value. If the source value does not start with
   * the passed lead and does not end with the passed tail, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param sLead
   *        The string to be trimmed of the beginning
   * @param sTail
   *        The string to be trimmed of the end
   * @return The trimmed string, or the original input string, if the lead and the tail were not
   *         found
   * @see #trimStart(String, String)
   * @see #trimEnd(String, String)
   * @see #trimStartAndEnd(String, String)
   */
  @Nullable
  @CheckReturnValue
  public static String trimStartAndEnd (@Nullable final String sSrc,
                                        @Nullable final String sLead,
                                        @Nullable final String sTail)
  {
    final String sInbetween = trimStart (sSrc, sLead);
    return trimEnd (sInbetween, sTail);
  }

  /**
   * Trim the passed lead and tail from the source value. If the source value does not start with
   * the passed trimmed value, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param cValueToTrim
   *        The char to be trimmed of the beginning and the end
   * @return The trimmed string, or the original input string, if the value to trim was not found
   * @see #trimStart(String, String)
   * @see #trimEnd(String, String)
   * @see #trimStartAndEnd(String, String, String)
   */
  @Nullable
  @CheckReturnValue
  public static String trimStartAndEnd (@Nullable final String sSrc, final char cValueToTrim)
  {
    return trimStartAndEnd (sSrc, cValueToTrim, cValueToTrim);
  }

  /**
   * Trim the passed lead and tail from the source value. If the source value does not start with
   * the passed lead and does not end with the passed tail, nothing happens.
   *
   * @param sSrc
   *        The input source string
   * @param cLead
   *        The char to be trimmed of the beginning
   * @param cTail
   *        The char to be trimmed of the end
   * @return The trimmed string, or the original input string, if the lead and the tail were not
   *         found
   * @see #trimStart(String, char)
   * @see #trimEnd(String, char)
   * @see #trimStartAndEnd(String, char)
   */
  @Nullable
  @CheckReturnValue
  public static String trimStartAndEnd (@Nullable final String sSrc, final char cLead, final char cTail)
  {
    final String sInbetween = trimStart (sSrc, cLead);
    return trimEnd (sInbetween, cTail);
  }

  /**
   * Trim the passed string, if it is not <code>null</code>.
   *
   * @param s
   *        The string to be trimmed. May be <code>null</code>.
   * @return <code>null</code> if the input string was <code>null</code>, the non-<code>null</code>
   *         trimmed string otherwise.
   * @see String#trim()
   */
  @Nullable
  @CheckReturnValue
  public static String trim (@Nullable final String s)
  {
    return isEmpty (s) ? s : s.trim ();
  }

  /**
   * Get the first character of the passed character sequence
   *
   * @param aCS
   *        The source character sequence
   * @return {@link CGlobal#ILLEGAL_CHAR} if the passed sequence was empty
   */
  public static char getFirstChar (@Nullable final CharSequence aCS)
  {
    return isNotEmpty (aCS) ? aCS.charAt (0) : CGlobal.ILLEGAL_CHAR;
  }

  /**
   * Get the first character of the passed array
   *
   * @param aChars
   *        The character array
   * @return {@link CGlobal#ILLEGAL_CHAR} if the passed array was empty
   */
  public static char getFirstChar (@Nullable final char [] aChars)
  {
    return ArrayHelper.getFirst (aChars, CGlobal.ILLEGAL_CHAR);
  }

  /**
   * Get the last character of the passed character sequence
   *
   * @param aCS
   *        The source character sequence
   * @return {@link CGlobal#ILLEGAL_CHAR} if the passed sequence was empty
   */
  public static char getLastChar (@Nullable final CharSequence aCS)
  {
    final int nLength = getLength (aCS);
    return nLength > 0 ? aCS.charAt (nLength - 1) : CGlobal.ILLEGAL_CHAR;
  }

  /**
   * Get the last character of the passed array
   *
   * @param aChars
   *        The character array
   * @return {@link CGlobal#ILLEGAL_CHAR} if the passed array was empty
   */
  public static char getLastChar (@Nullable final char [] aChars)
  {
    return ArrayHelper.getLast (aChars, CGlobal.ILLEGAL_CHAR);
  }

  @Nonnegative
  public static int getCharCount (@Nullable final String s, final char cSearch)
  {
    return s == null ? 0 : getCharCount (s.toCharArray (), cSearch);
  }

  @Nonnegative
  public static int getCharCount (@Nullable final char [] aChars, final char cSearch)
  {
    int ret = 0;
    if (aChars != null)
      for (final char c : aChars)
        if (c == cSearch)
          ++ret;
    return ret;
  }

  @Nonnegative
  public static int getLineCount (@Nullable final String s)
  {
    return getLineCount (s, '\n');
  }

  @Nonnegative
  public static int getLineCount (@Nullable final String s, final char cLineSep)
  {
    return 1 + getCharCount (s, cLineSep);
  }

  /**
   * Get the number of characters the passed value would occupy in a string representation.<br>
   * Copied from java.lang.Integer#StringSize
   *
   * @param nValue
   *        The integer value to check. May be be positive or negative.
   * @return Number of characters required. Alyways &gt; 0.
   */
  @Nonnegative
  public static int getCharacterCount (final int nValue)
  {
    // index is always one character less than the real size; that's why nPrefix
    // is 1 more!
    final int nPrefix = nValue < 0 ? 2 : 1;
    final int nRealValue = MathHelper.abs (nValue);

    for (int nIndex = 0;; nIndex++)
      if (nRealValue <= SIZE_TABLE_INT[nIndex])
        return nPrefix + nIndex;
  }

  /**
   * Get the number of characters the passed value would occupy in a string representation.
   *
   * @param nValue
   *        The long value to check. May be be positive or negative.
   * @return Number of characters required. Always &gt; 0.
   */
  @Nonnegative
  public static int getCharacterCount (final long nValue)
  {
    // index is always one character less than the real size; that's why nPrefix
    // is 1 more!
    final int nPrefix = nValue < 0 ? 2 : 1;
    final long nRealValue = MathHelper.abs (nValue);

    for (int nIndex = 0;; nIndex++)
      if (nRealValue <= SIZE_TABLE_LONG[nIndex])
        return nPrefix + nIndex;
  }

  @Nonnull
  public static String getCutAfterLength (@Nonnull final String sValue, @Nonnegative final int nMaxLength)
  {
    return getCutAfterLength (sValue, nMaxLength, null);
  }

  @Nonnull
  public static String getCutAfterLength (@Nonnull final String sValue,
                                          @Nonnegative final int nMaxLength,
                                          @Nullable final String sNewSuffix)
  {
    ValueEnforcer.notNull (sValue, "Value");
    ValueEnforcer.isGE0 (nMaxLength, "MaxLength");

    if (sValue.length () <= nMaxLength)
      return sValue;
    if (isEmpty (sNewSuffix))
      return sValue.substring (0, nMaxLength);
    return sValue.substring (0, nMaxLength) + sNewSuffix;
  }

  /**
   * Convert the passed object to a string using the {@link Object#toString()} method.
   *
   * @param aObject
   *        The value to be converted. May be <code>null</code>.
   * @return An empty string in case the passed object was <code>null</code>. Never
   *         <code>null</code>.
   * @see Object#toString()
   */
  @Nonnull
  public static String getToString (@Nullable final Object aObject)
  {
    return getToString (aObject, "");
  }

  /**
   * Convert the passed object to a string using the {@link Object#toString()} method or otherwise
   * return the passed default value.
   *
   * @param aObject
   *        The value to be converted. May be <code>null</code>.
   * @param sNullValue
   *        The value to be returned in case the passed object is <code>null</code>. May be
   *        <code>null</code> itself.
   * @return The passed default value in case the passed object was <code>null</code> or the result
   *         of {@link Object#toString()} on the passed object.
   * @see Object#toString()
   */
  @Nullable
  public static String getToString (@Nullable final Object aObject, @Nullable final String sNullValue)
  {
    return aObject == null ? sNullValue : aObject.toString ();
  }

  /**
   * Get the passed string without the first char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @return An empty, non-<code>null</code> string if the passed string has a length &le; 1.
   */
  @Nonnull
  public static String getWithoutLeadingChar (@Nullable final String sStr)
  {
    return getWithoutLeadingChars (sStr, 1);
  }

  /**
   * Get the passed string without the specified number of leading chars.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param nCount
   *        The number of chars to remove.
   * @return An empty, non-<code>null</code> string if the passed string has a length &le;
   *         <code>nCount</code>.
   */
  @Nonnull
  public static String getWithoutLeadingChars (@Nullable final String sStr, @Nonnegative final int nCount)
  {
    ValueEnforcer.isGE0 (nCount, "Count");

    if (nCount == 0)
      return sStr;
    return getLength (sStr) <= nCount ? "" : sStr.substring (nCount);
  }

  /**
   * Get the passed string without the last char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @return An empty, non-<code>null</code> string if the passed string has a length &le; 1.
   */
  @Nonnull
  public static String getWithoutTrailingChar (@Nullable final String sStr)
  {
    return getWithoutTrailingChars (sStr, 1);
  }

  /**
   * Get the passed string without the specified number of trailing chars.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param nCount
   *        The number of chars to remove.
   * @return An empty, non-<code>null</code> string if the passed string has a length &le;
   *         <code>nCount</code>.
   */
  @Nonnull
  public static String getWithoutTrailingChars (@Nullable final String sStr, @Nonnegative final int nCount)
  {
    ValueEnforcer.isGE0 (nCount, "Count");

    if (nCount == 0)
      return sStr;
    final int nLength = getLength (sStr);
    return nLength <= nCount ? "" : sStr.substring (0, nLength - nCount);
  }

  /**
   * Get the passed string where all spaces (white spaces or unicode spaces) have been removed.
   *
   * @param sStr
   *        The source string. May be <code>null</code>
   * @return A non-<code>null</code> string representing the passed string without any spaces
   */
  @Nonnull
  public static String getWithoutAnySpaces (@Nullable final String sStr)
  {
    if (sStr == null)
      return "";

    // Trim first
    final char [] aChars = sStr.trim ().toCharArray ();
    final StringBuilder aResult = new StringBuilder (aChars.length);
    for (final char c : aChars)
      if (!Character.isWhitespace (c) && !Character.isSpaceChar (c))
        aResult.append (c);
    return aResult.toString ();
  }

  @Nullable
  private static String _getUntilFirst (@Nullable final String sStr,
                                        final char cSearch,
                                        final boolean bIncludingSearchChar)
  {
    final int nIndex = getIndexOf (sStr, cSearch);
    if (nIndex == STRING_NOT_FOUND)
      return null;
    return sStr.substring (0, nIndex + (bIncludingSearchChar ? 1 : 0));
  }

  /**
   * Get everything from the string up to and including the first passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search character.
   */
  @Nullable
  public static String getUntilFirstIncl (@Nullable final String sStr, final char cSearch)
  {
    return _getUntilFirst (sStr, cSearch, true);
  }

  /**
   * Get everything from the string up to and excluding first the passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search character.
   */
  @Nullable
  public static String getUntilFirstExcl (@Nullable final String sStr, final char cSearch)
  {
    return _getUntilFirst (sStr, cSearch, false);
  }

  @Nullable
  private static String _getUntilFirst (@Nullable final String sStr,
                                        @Nullable final String sSearch,
                                        final boolean bIncludingSearchChar)
  {
    if (isEmpty (sSearch))
      return "";

    final int nIndex = getIndexOf (sStr, sSearch);
    if (nIndex == STRING_NOT_FOUND)
      return null;
    return sStr.substring (0, nIndex + (bIncludingSearchChar ? sSearch.length () : 0));
  }

  /**
   * Get everything from the string up to and including the first passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search string. If the
   *         search string is empty, the empty string is returned.
   */
  @Nullable
  public static String getUntilFirstIncl (@Nullable final String sStr, @Nullable final String sSearch)
  {
    return _getUntilFirst (sStr, sSearch, true);
  }

  /**
   * Get everything from the string up to and excluding the first passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search string. If the
   *         search string is empty, the empty string is returned.
   */
  @Nullable
  public static String getUntilFirstExcl (@Nullable final String sStr, @Nullable final String sSearch)
  {
    return _getUntilFirst (sStr, sSearch, false);
  }

  @Nullable
  private static String _getUntilLast (@Nullable final String sStr,
                                       final char cSearch,
                                       final boolean bIncludingSearchChar)
  {
    final int nIndex = getLastIndexOf (sStr, cSearch);
    if (nIndex == STRING_NOT_FOUND)
      return null;
    return sStr.substring (0, nIndex + (bIncludingSearchChar ? 1 : 0));
  }

  /**
   * Get everything from the string up to and including the first passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search character.
   */
  @Nullable
  public static String getUntilLastIncl (@Nullable final String sStr, final char cSearch)
  {
    return _getUntilLast (sStr, cSearch, true);
  }

  /**
   * Get everything from the string up to and excluding first the passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search character.
   */
  @Nullable
  public static String getUntilLastExcl (@Nullable final String sStr, final char cSearch)
  {
    return _getUntilLast (sStr, cSearch, false);
  }

  @Nullable
  private static String _getUntilLast (@Nullable final String sStr,
                                       @Nullable final String sSearch,
                                       final boolean bIncludingSearchChar)
  {
    if (isEmpty (sSearch))
      return "";

    final int nIndex = getLastIndexOf (sStr, sSearch);
    if (nIndex == STRING_NOT_FOUND)
      return null;
    return sStr.substring (0, nIndex + (bIncludingSearchChar ? sSearch.length () : 0));
  }

  /**
   * Get everything from the string up to and including the first passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search string. If the
   *         search string is empty, the empty string is returned.
   */
  @Nullable
  public static String getUntilLastIncl (@Nullable final String sStr, @Nullable final String sSearch)
  {
    return _getUntilLast (sStr, sSearch, true);
  }

  /**
   * Get everything from the string up to and excluding the first passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search string. If the
   *         search string is empty, the empty string is returned.
   */
  @Nullable
  public static String getUntilLastExcl (@Nullable final String sStr, @Nullable final String sSearch)
  {
    return _getUntilLast (sStr, sSearch, false);
  }

  @Nullable
  private static String _getFromFirst (@Nullable final String sStr,
                                       final char cSearch,
                                       final boolean bIncludingSearchChar)
  {
    final int nIndex = getIndexOf (sStr, cSearch);
    if (nIndex == STRING_NOT_FOUND)
      return null;
    return sStr.substring (nIndex + (bIncludingSearchChar ? 0 : 1));
  }

  /**
   * Get everything from the string from and including the first passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search character.
   */
  @Nullable
  public static String getFromFirstIncl (@Nullable final String sStr, final char cSearch)
  {
    return _getFromFirst (sStr, cSearch, true);
  }

  /**
   * Get everything from the string from and excluding the first passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search character.
   */
  @Nullable
  public static String getFromFirstExcl (@Nullable final String sStr, final char cSearch)
  {
    return _getFromFirst (sStr, cSearch, false);
  }

  @Nullable
  private static String _getFromFirst (@Nullable final String sStr,
                                       @Nullable final String sSearch,
                                       final boolean bIncludingSearchString)
  {
    if (isEmpty (sSearch))
      return sStr;

    final int nIndex = getIndexOf (sStr, sSearch);
    if (nIndex == STRING_NOT_FOUND)
      return null;
    return sStr.substring (nIndex + (bIncludingSearchString ? 0 : sSearch.length ()));
  }

  /**
   * Get everything from the string from and including the passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search string. If the
   *         search string is empty, the input string is returned unmodified.
   */
  @Nullable
  public static String getFromFirstIncl (@Nullable final String sStr, @Nullable final String sSearch)
  {
    return _getFromFirst (sStr, sSearch, true);
  }

  /**
   * Get everything from the string from and excluding the passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search string. If the
   *         search string is empty, the input string is returned unmodified.
   */
  @Nullable
  public static String getFromFirstExcl (@Nullable final String sStr, @Nullable final String sSearch)
  {
    return _getFromFirst (sStr, sSearch, false);
  }

  @Nullable
  private static String _getFromLast (@Nullable final String sStr,
                                      final char cSearch,
                                      final boolean bIncludingSearchChar)
  {
    final int nIndex = getLastIndexOf (sStr, cSearch);
    if (nIndex == STRING_NOT_FOUND)
      return null;
    return sStr.substring (nIndex + (bIncludingSearchChar ? 0 : 1));
  }

  /**
   * Get everything from the string from and including the first passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search character.
   */
  @Nullable
  public static String getFromLastIncl (@Nullable final String sStr, final char cSearch)
  {
    return _getFromLast (sStr, cSearch, true);
  }

  /**
   * Get everything from the string from and excluding the first passed char.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param cSearch
   *        The character to search.
   * @return <code>null</code> if the passed string does not contain the search character.
   */
  @Nullable
  public static String getFromLastExcl (@Nullable final String sStr, final char cSearch)
  {
    return _getFromLast (sStr, cSearch, false);
  }

  @Nullable
  private static String _getFromLast (@Nullable final String sStr,
                                      @Nullable final String sSearch,
                                      final boolean bIncludingSearchString)
  {
    if (isEmpty (sSearch))
      return sStr;

    final int nIndex = getLastIndexOf (sStr, sSearch);
    if (nIndex == STRING_NOT_FOUND)
      return null;
    return sStr.substring (nIndex + (bIncludingSearchString ? 0 : sSearch.length ()));
  }

  /**
   * Get everything from the string from and including the passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search string. If the
   *         search string is empty, the input string is returned unmodified.
   */
  @Nullable
  public static String getFromLastIncl (@Nullable final String sStr, @Nullable final String sSearch)
  {
    return _getFromLast (sStr, sSearch, true);
  }

  /**
   * Get everything from the string from and excluding the passed string.
   *
   * @param sStr
   *        The source string. May be <code>null</code>.
   * @param sSearch
   *        The string to search. May be <code>null</code>.
   * @return <code>null</code> if the passed string does not contain the search string. If the
   *         search string is empty, the input string is returned unmodified.
   */
  @Nullable
  public static String getFromLastExcl (@Nullable final String sStr, @Nullable final String sSearch)
  {
    return _getFromLast (sStr, sSearch, false);
  }

  /**
   * Get the first token up to (and excluding) the separating character.
   *
   * @param sStr
   *        The string to search. May be <code>null</code>.
   * @param cSearch
   *        The search character.
   * @return The passed string if no such separator token was found.
   */
  @Nullable
  public static String getFirstToken (@Nullable final String sStr, final char cSearch)
  {
    final int nIndex = getIndexOf (sStr, cSearch);
    return nIndex == Strings.STRING_NOT_FOUND ? sStr : sStr.substring (0, nIndex);
  }

  /**
   * Get the first token up to (and excluding) the separating string.
   *
   * @param sStr
   *        The string to search. May be <code>null</code>.
   * @param sSearch
   *        The search string. May be <code>null</code>.
   * @return The passed string if no such separator token was found.
   */
  @Nullable
  public static String getFirstToken (@Nullable final String sStr, @Nullable final String sSearch)
  {
    if (Strings.isEmpty (sSearch))
      return sStr;
    final int nIndex = getIndexOf (sStr, sSearch);
    return nIndex == Strings.STRING_NOT_FOUND ? sStr : sStr.substring (0, nIndex);
  }

  /**
   * Get the last token from (and excluding) the separating character.
   *
   * @param sStr
   *        The string to search. May be <code>null</code>.
   * @param cSearch
   *        The search character.
   * @return The passed string if no such separator token was found.
   */
  @Nullable
  public static String getLastToken (@Nullable final String sStr, final char cSearch)
  {
    final int nIndex = getLastIndexOf (sStr, cSearch);
    return nIndex == Strings.STRING_NOT_FOUND ? sStr : sStr.substring (nIndex + 1);
  }

  /**
   * Get the last token from (and excluding) the separating string.
   *
   * @param sStr
   *        The string to search. May be <code>null</code>.
   * @param sSearch
   *        The search string. May be <code>null</code>.
   * @return The passed string if no such separator token was found.
   */
  @Nullable
  public static String getLastToken (@Nullable final String sStr, @Nullable final String sSearch)
  {
    if (Strings.isEmpty (sSearch))
      return sStr;
    final int nIndex = getLastIndexOf (sStr, sSearch);
    return nIndex == Strings.STRING_NOT_FOUND ? sStr : sStr.substring (nIndex + getLength (sSearch));
  }

  @Nullable
  public static String getReverse (@Nullable final String sStr)
  {
    if (sStr == null)
      return null;

    final char [] aChars = sStr.toCharArray ();
    if (aChars.length <= 1)
      return sStr;

    final char [] ret = new char [aChars.length];
    int nSrc = aChars.length - 1;
    int nDst = 0;
    while (nSrc >= 0)
    {
      ret[nDst] = aChars[nSrc];
      nSrc--;
      nDst++;
    }
    return new String (ret);
  }

  /**
   * Iterate all code points and pass them to the provided consumer. This implementation is
   * approximately 20% quicker than <code>CharSequence.codePoints().forEachOrdered(c)</code>
   *
   * @param sInputString
   *        Input String to use. May be <code>null</code> or empty.
   * @param aConsumer
   *        The consumer to be used. May not be <code>null</code>.
   */
  public static void iterateCodePoints (@Nullable final String sInputString, @Nonnull final IntConsumer aConsumer)
  {
    ValueEnforcer.notNull (aConsumer, "Consumer");

    if (sInputString != null)
    {
      final int nStringLength = sInputString.length ();
      int nOfs = 0;
      while (nOfs < nStringLength)
      {
        final int nCodePoint = sInputString.codePointAt (nOfs);
        nOfs += Character.charCount (nCodePoint);

        aConsumer.accept (nCodePoint);
      }
    }
  }
}
