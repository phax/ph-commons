/*
 * Copyright (C) 2026 Philip Helger (www.helger.com)
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
package com.helger.telemetry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.builder.IBuilder;

/**
 * Immutable, typed set of attribute key/value pairs. Used to attach dimensions to metric recordings
 * ({@link ITelemetryCounter}, {@link ITelemetryHistogram} ...) and to span events
 * ({@link ITelemetrySpan#addEvent(String, TelemetryAttributes)}).
 * <p>
 * Build instances via {@link #builder()} and the fluent {@link Builder#put(String, String)}
 * overloads. The value type is preserved (String, long, double, boolean) so that SPI
 * implementations can map each entry to the native type of the underlying observability backend.
 * Null string values are silently dropped (they are treated as "not set").
 * <p>
 * Iterate entries via {@link #forEach(IVisitor)} — this is the only access the SPI gets to the
 * payload, so the visitor dispatches the value to a typed callback without any {@code instanceof}
 * checks at the call site.
 *
 * <pre>
 * TelemetryAttributes aAttrs = TelemetryAttributes.builder ()
 *                                                 .put ("vesid", sVESID)
 *                                                 .put ("valid", bValid)
 *                                                 .put ("error_count", nErrors)
 *                                                 .build ();
 * COUNTER.add (1, aAttrs);
 * </pre>
 *
 * @author Philip Helger
 * @since 12.2.7
 */
@Immutable
public final class TelemetryAttributes
{
  /**
   * Typed visitor for {@link TelemetryAttributes#forEach(IVisitor)}. Dispatches each entry to the
   * callback matching its declared value type.
   */
  public interface IVisitor
  {
    void onString (@NonNull String sKey, @NonNull String sValue);

    void onLong (@NonNull String sKey, long nValue);

    void onDouble (@NonNull String sKey, double dValue);

    void onBoolean (@NonNull String sKey, boolean bValue);
  }

  private static final class Entry
  {
    final String m_sKey;
    // String | Long | Double | Boolean
    final Object m_aValue;

    Entry (@NonNull final String sKey, @NonNull final Object aValue)
    {
      m_sKey = sKey;
      m_aValue = aValue;
    }
  }

  /** Shared empty instance — use whenever no dimensions are needed. */
  @NonNull
  public static final TelemetryAttributes EMPTY = new TelemetryAttributes (Collections.emptyList ());

  private final List <Entry> m_aEntries;

  private TelemetryAttributes (@NonNull final List <Entry> aEntries)
  {
    m_aEntries = aEntries;
  }

  /**
   * @return The number of entries in this attribute set.
   */
  public int size ()
  {
    return m_aEntries.size ();
  }

  /**
   * @return <code>true</code> if no entries are present.
   */
  public boolean isEmpty ()
  {
    return m_aEntries.isEmpty ();
  }

  /**
   * Visit each entry in insertion order, calling the typed method on the visitor matching the
   * declared value type.
   *
   * @param aVisitor
   *        The visitor. Never <code>null</code>.
   */
  public void forEach (@NonNull final IVisitor aVisitor)
  {
    for (final Entry e : m_aEntries)
    {
      final Object v = e.m_aValue;
      if (v instanceof final String s)
        aVisitor.onString (e.m_sKey, s);
      else
        if (v instanceof final Long a)
          aVisitor.onLong (e.m_sKey, a.longValue ());
        else
          if (v instanceof final Double a)
            aVisitor.onDouble (e.m_sKey, a.doubleValue ());
          else
            if (v instanceof final Boolean a)
              aVisitor.onBoolean (e.m_sKey, a.booleanValue ());
            else
              throw new IllegalStateException ("Unexpected telemetry attribute type of " + v);
    }
  }

  /**
   * @return A new {@link Builder} instance. Never <code>null</code>.
   */
  @NonNull
  public static Builder builder ()
  {
    return new Builder ();
  }

  /**
   * Builder for {@link TelemetryAttributes}. Not thread-safe — build on a single thread, then share
   * the resulting immutable {@link TelemetryAttributes}.
   */
  public static final class Builder implements IBuilder <TelemetryAttributes>
  {
    private final List <Entry> m_aEntries = new ArrayList <> ();

    private Builder ()
    {}

    /**
     * Add a string attribute. Passing a <code>null</code> value silently drops the entry, mirroring
     * {@link ITelemetrySpan#setAttribute(String, String)}.
     *
     * @param sKey
     *        The attribute key. Never <code>null</code>.
     * @param sValue
     *        The value. May be <code>null</code> to skip.
     * @return this. Never <code>null</code>.
     */
    @NonNull
    public Builder put (@NonNull final String sKey, @Nullable final String sValue)
    {
      if (sValue != null)
        m_aEntries.add (new Entry (sKey, sValue));
      return this;
    }

    /**
     * Add a long attribute.
     *
     * @param sKey
     *        The attribute key. Never <code>null</code>.
     * @param nValue
     *        The value.
     * @return this. Never <code>null</code>.
     */
    @NonNull
    public Builder put (@NonNull final String sKey, final long nValue)
    {
      m_aEntries.add (new Entry (sKey, Long.valueOf (nValue)));
      return this;
    }

    /**
     * Add a double attribute.
     *
     * @param sKey
     *        The attribute key. Never <code>null</code>.
     * @param dValue
     *        The value.
     * @return this. Never <code>null</code>.
     */
    @NonNull
    public Builder put (@NonNull final String sKey, final double dValue)
    {
      m_aEntries.add (new Entry (sKey, Double.valueOf (dValue)));
      return this;
    }

    /**
     * Add a boolean attribute.
     *
     * @param sKey
     *        The attribute key. Never <code>null</code>.
     * @param bValue
     *        The value.
     * @return this. Never <code>null</code>.
     */
    @NonNull
    public Builder put (@NonNull final String sKey, final boolean bValue)
    {
      m_aEntries.add (new Entry (sKey, Boolean.valueOf (bValue)));
      return this;
    }

    /**
     * @return The immutable {@link TelemetryAttributes} snapshot. Never <code>null</code>.
     */
    @NonNull
    public TelemetryAttributes build ()
    {
      if (m_aEntries.isEmpty ())
        return EMPTY;

      return new TelemetryAttributes (List.copyOf (m_aEntries));
    }
  }
}
