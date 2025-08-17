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
package com.helger.base.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.Immutable;
import com.helger.base.builder.IBuilder;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.functional.Predicates;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@Immutable
public final class StringImplode
{
  private StringImplode ()
  {}

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
    private List <String> m_aSource;
    private String m_sSeparator;
    private int m_nOffset = 0;
    private int m_nLength = -1;
    private Predicate <String> m_aFilter;

    public ImploderBuilder ()
    {}

    @Nullable
    private static String _toString (@Nullable final Object o)
    {
      return o == null ? null : o.toString ();
    }

    @Nonnull
    public ImploderBuilder source (@Nullable final Collection <?> a)
    {
      return source (a, ImploderBuilder::_toString);
    }

    @Nonnull
    public <T> ImploderBuilder source (@Nullable final Collection <T> a,
                                       @Nonnull final Function <? super T, String> aMapper)
    {
      ValueEnforcer.notNull (aMapper, "Mapper");
      if (a == null)
        m_aSource = null;
      else
      {
        m_aSource = new ArrayList <> (a.size ());
        for (final T aItem : a)
          m_aSource.add (aMapper.apply (aItem));
      }
      return this;
    }

    @Nonnull
    public ImploderBuilder source (@Nullable final String... a)
    {
      m_aSource = a == null ? null : Arrays.asList (a);
      return this;
    }

    @Nonnull
    @SafeVarargs
    public final <T> ImploderBuilder source (@Nullable final T... a)
    {
      return source (a, ImploderBuilder::_toString);
    }

    @Nonnull
    public <T> ImploderBuilder source (@Nullable final T [] a, @Nonnull final Function <? super T, String> aMapper)
    {
      ValueEnforcer.notNull (aMapper, "Mapper");
      if (a == null)
        m_aSource = null;
      else
      {
        m_aSource = new ArrayList <> (a.length);
        for (final T aItem : a)
          m_aSource.add (aMapper.apply (aItem));
      }
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
      return filter (StringHelper::isNotEmpty);
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
      final List <String> aSource = m_aSource;
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
  public static String getImploded (@Nullable final Collection <?> aElements)
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
  public static <ELEMENTTYPE> String getImplodedMapped (@Nullable final Collection <? extends ELEMENTTYPE> aElements,
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
  public static String getImploded (@Nonnull final String sSep, @Nullable final Collection <?> aElements)
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
  public static String getImploded (final char cSep, @Nullable final Collection <?> aElements)
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
                                                        @Nullable final Collection <? extends ELEMENTTYPE> aElements,
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
                                                        @Nullable final Collection <? extends ELEMENTTYPE> aElements,
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
   * container without a separator string. This the very generic version of getConcatenatedOnDemand
   * for an arbitrary number of elements.
   *
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImplodedNonEmpty (@Nullable final Collection <String> aElements)
  {
    return imploder ().source (aElements).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty elements of the passed
   * container without a separator string. This the very generic version of getConcatenatedOnDemand
   * for an arbitrary number of elements.
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
  public static <ELEMENTTYPE> String getImplodedMappedNonEmpty (@Nullable final Collection <? extends ELEMENTTYPE> aElements,
                                                                @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty elements of the passed
   * container without a separator string. This the very generic version of getConcatenatedOnDemand
   * for an arbitrary number of elements.
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
   * getConcatenatedOnDemand for an arbitrary number of elements.
   *
   * @param sSep
   *        The separator to use. May not be <code>null</code>.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImplodedNonEmpty (@Nonnull final String sSep, @Nullable final Collection <String> aElements)
  {
    return imploder ().source (aElements).separator (sSep).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty elements of the passed
   * container, separated by the specified separator char. This the very generic version of
   * getConcatenatedOnDemand for an arbitrary number of elements.
   *
   * @param cSep
   *        The separator to use.
   * @param aElements
   *        The container to convert. May be <code>null</code> or empty.
   * @return The concatenated string.
   */
  @Nonnull
  public static String getImplodedNonEmpty (final char cSep, @Nullable final Collection <String> aElements)
  {
    return imploder ().source (aElements).separator (cSep).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty elements of the passed
   * container, separated by the specified separator string. This the very generic version of
   * getConcatenatedOnDemand for an arbitrary number of elements.
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
                                                                @Nullable final Collection <? extends ELEMENTTYPE> aElements,
                                                                @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).separator (sSep).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all non-<code>null</code> and non empty elements of the passed
   * container, separated by the specified separator char. This the very generic version of
   * getConcatenatedOnDemand for an arbitrary number of elements.
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
                                                                @Nullable final Collection <? extends ELEMENTTYPE> aElements,
                                                                @Nonnull final Function <? super ELEMENTTYPE, String> aMapper)
  {
    return imploder ().source (aElements, aMapper).separator (cSep).filterNonEmpty ().build ();
  }

  /**
   * Get a concatenated String from all elements of the passed array, separated by the specified
   * separator string. This the very generic version of getConcatenatedOnDemand for an arbitrary
   * number of elements.
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
   * separator char. This the very generic version of getConcatenatedOnDemand for an arbitrary
   * number of elements.
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
   * separator string. This the very generic version of getConcatenatedOnDemand for an arbitrary
   * number of elements.
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
   * separator char. This the very generic version of getConcatenatedOnDemand for an arbitrary
   * number of elements.
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
   * separator string. This the very generic version of getConcatenatedOnDemand for an arbitrary
   * number of elements.
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
   * separator char. This the very generic version of getConcatenatedOnDemand for an arbitrary
   * number of elements.
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
   * separator string. This the very generic version of getConcatenatedOnDemand for an arbitrary
   * number of elements.
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
   * separator char. This the very generic version of getConcatenatedOnDemand for an arbitrary
   * number of elements.
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
    private Map <String, String> m_aSource;
    private String m_sSeparatorOuter;
    private String m_sSeparatorInner;
    private Predicate <String> m_aFilterKey;
    private Predicate <String> m_aFilterValue;

    public ImploderBuilderMap ()
    {}

    @Nullable
    private static String _toString (@Nullable final Object o)
    {
      return o == null ? null : o.toString ();
    }

    @Nonnull
    public ImploderBuilderMap source (@Nullable final Map <?, ?> a)
    {
      return source (a, ImploderBuilderMap::_toString, ImploderBuilderMap::_toString);
    }

    @Nonnull
    public <K, V> ImploderBuilderMap source (@Nullable final Map <K, V> a,
                                             @Nonnull final Function <? super K, String> aKeyMapper,
                                             @Nonnull final Function <? super V, String> aValueMapper)
    {
      ValueEnforcer.notNull (aKeyMapper, "KeyMapper");
      ValueEnforcer.notNull (aValueMapper, "ValueMapper");
      if (a == null)
        m_aSource = null;
      else
      {
        m_aSource = new LinkedHashMap <> (a.size ());
        for (final Map.Entry <K, V> e : a.entrySet ())
          m_aSource.put (aKeyMapper.apply (e.getKey ()), aValueMapper.apply (e.getValue ()));
      }
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
      return filterKey (StringHelper::isNotEmpty);
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
      return filterValue (StringHelper::isNotEmpty);
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
      final Map <String, String> aSource = m_aSource;
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

}
