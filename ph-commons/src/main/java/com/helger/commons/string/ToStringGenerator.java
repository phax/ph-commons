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
package com.helger.commons.string;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * This is a utility class for easier <code>toString</code> method creations. It
 * assumes that the <code>toString</code> method is only used for the
 * representation of the internal state and not for creating human readable
 * formats.
 * <p>
 * A real world example for a final class derived from {@link Object} or a base
 * class looks like this:
 * </p>
 *
 * <pre>
 * &#064;Override
 * public String toString ()
 * {
 *   return new ToStringGenerator (this).append (&quot;member1&quot;, member1).append (&quot;member2&quot;, member2).toString ();
 * }
 * </pre>
 * <p>
 * For a derived class, the typical code looks like this, assuming that the base
 * class also used the {@link ToStringGenerator}:
 * </p>
 *
 * <pre>
 * &#064;Override
 * public String toString ()
 * {
 *   return ToStringGenerator.getDerived (super.toString ())
 *                           .append (&quot;member3&quot;, member3)
 *                           .append (&quot;member4&quot;, member4)
 *                           .toString ();
 * }
 * </pre>
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class ToStringGenerator
{
  /** String to be emitted for <code>null</code> values */
  public static final String CONSTANT_NULL = "null";
  /** String to be emitted for <code>this</code> values */
  public static final String CONSTANT_THIS = "this";
  /** String to be emited for password values */
  public static final String CONSTANT_PASSWORD = "****";
  private static final int FIRST_FIELD = 1;
  private static final int APPENDED_CLOSING_BRACKET = 2;

  private final StringBuilder m_aSB = new StringBuilder ("[");
  private final Object m_aSrc;
  private int m_nIndex = 0;

  public ToStringGenerator (@Nullable final Object aSrc)
  {
    if (aSrc != null)
    {
      final String sClassName = aSrc.getClass ().getName ();
      final int nIndex = sClassName.lastIndexOf ('.');
      m_aSB.append (nIndex == -1 ? sClassName : sClassName.substring (nIndex + 1))
           .append ("@0x")
           .append (StringHelper.getHexStringLeadingZero (System.identityHashCode (aSrc), 8));
    }
    m_aSrc = aSrc;
  }

  private void _beforeAddField ()
  {
    if ((m_nIndex & FIRST_FIELD) == 0)
    {
      m_nIndex |= FIRST_FIELD;

      // Only if a valid source object was provided
      if (m_aSB.length () > 1)
        m_aSB.append (": ");
    }
    else
      m_aSB.append ("; ");
  }

  @Nonnull
  private ToStringGenerator _appendSuper (final String sSuper)
  {
    _beforeAddField ();
    m_aSB.append (sSuper);
    return this;
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, final boolean aValue)
  {
    _beforeAddField ();
    m_aSB.append (sField).append ('=').append (aValue);
    return this;
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, @Nullable final boolean [] aValue)
  {
    _beforeAddField ();
    m_aSB.append (sField).append ('=').append (Arrays.toString (aValue));
    return this;
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, final byte aValue)
  {
    _beforeAddField ();
    m_aSB.append (sField).append ('=').append (aValue);
    return this;
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, @Nullable final byte [] aValue)
  {
    _beforeAddField ();
    m_aSB.append (sField).append ('=').append (Arrays.toString (aValue));
    return this;
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, final char aValue)
  {
    _beforeAddField ();
    m_aSB.append (sField).append ('=').append (aValue);
    return this;
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, @Nullable final char [] aValue)
  {
    _beforeAddField ();
    m_aSB.append (sField).append ('=').append (Arrays.toString (aValue));
    return this;
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, final double aValue)
  {
    _beforeAddField ();
    m_aSB.append (sField).append ('=').append (aValue);
    return this;
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, @Nullable final double [] aValue)
  {
    _beforeAddField ();
    m_aSB.append (sField).append ('=').append (Arrays.toString (aValue));
    return this;
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, final float aValue)
  {
    _beforeAddField ();
    m_aSB.append (sField).append ('=').append (aValue);
    return this;
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, @Nullable final float [] aValue)
  {
    _beforeAddField ();
    m_aSB.append (sField).append ('=').append (Arrays.toString (aValue));
    return this;
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, final int aValue)
  {
    _beforeAddField ();
    m_aSB.append (sField).append ('=').append (aValue);
    return this;
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, @Nullable final int [] aValue)
  {
    _beforeAddField ();
    m_aSB.append (sField).append ('=').append (Arrays.toString (aValue));
    return this;
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, final long aValue)
  {
    _beforeAddField ();
    m_aSB.append (sField).append ('=').append (aValue);
    return this;
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, @Nullable final long [] aValue)
  {
    _beforeAddField ();
    m_aSB.append (sField).append ('=').append (Arrays.toString (aValue));
    return this;
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, final short aValue)
  {
    _beforeAddField ();
    m_aSB.append (sField).append ('=').append (aValue);
    return this;
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, @Nullable final short [] aValue)
  {
    _beforeAddField ();
    m_aSB.append (sField).append ('=').append (Arrays.toString (aValue));
    return this;
  }

  @Nonnull
  public ToStringGenerator appendPassword (@Nonnull final String sField)
  {
    return append (sField, CONSTANT_PASSWORD);
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, @Nullable final Enum <?> aValue)
  {
    return append (sField, String.valueOf (aValue));
  }

  @Nonnull
  private String _getObjectValue (@Nullable final Object aValue)
  {
    return aValue == null ? CONSTANT_NULL : aValue == m_aSrc ? CONSTANT_THIS : aValue.toString ();
  }

  @Nonnull
  private ToStringGenerator _appendArray (@Nonnull final String sField, @Nonnull final Object aValue)
  {
    // Passed value is an array
    final Class <?> aCompType = aValue.getClass ().getComponentType ();
    if (aCompType.isPrimitive ())
    {
      // Assuming byte[] happens most often
      if (aCompType.equals (byte.class))
        return append (sField, (byte []) aValue);
      if (aCompType.equals (boolean.class))
        return append (sField, (boolean []) aValue);
      if (aCompType.equals (char.class))
        return append (sField, (char []) aValue);
      if (aCompType.equals (double.class))
        return append (sField, (double []) aValue);
      if (aCompType.equals (float.class))
        return append (sField, (float []) aValue);
      if (aCompType.equals (int.class))
        return append (sField, (int []) aValue);
      if (aCompType.equals (long.class))
        return append (sField, (long []) aValue);
      if (aCompType.equals (short.class))
        return append (sField, (short []) aValue);
    }
    return append (sField, (Object []) aValue);
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, @Nullable final Object aValue)
  {
    if (aValue != null && aValue.getClass ().isArray ())
      return _appendArray (sField, aValue);

    _beforeAddField ();
    // Avoid endless loop with base object
    m_aSB.append (sField).append ('=').append (_getObjectValue (aValue));
    return this;
  }

  @Nonnull
  public ToStringGenerator append (@Nonnull final String sField, @Nullable final Object [] aValue)
  {
    _beforeAddField ();
    m_aSB.append (sField).append ('=');
    if (aValue == null)
      m_aSB.append (CONSTANT_NULL);
    else
    {
      final int nMax = aValue.length - 1;
      if (nMax == -1)
        m_aSB.append ("[]");
      else
      {
        m_aSB.append ('[');
        for (int i = 0;; i++)
        {
          // Avoid endless loop with base object
          m_aSB.append (_getObjectValue (aValue[i]));
          if (i == nMax)
            break;
          m_aSB.append (", ");
        }
        m_aSB.append (']');
      }
    }

    return this;
  }

  @Nonnull
  public ToStringGenerator appendIfNotNull (@Nonnull final String sField, @Nullable final boolean [] aValue)
  {
    return aValue == null ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotNull (@Nonnull final String sField, @Nullable final byte [] aValue)
  {
    return aValue == null ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotNull (@Nonnull final String sField, @Nullable final char [] aValue)
  {
    return aValue == null ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotNull (@Nonnull final String sField, @Nullable final double [] aValue)
  {
    return aValue == null ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotNull (@Nonnull final String sField, @Nullable final float [] aValue)
  {
    return aValue == null ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotNull (@Nonnull final String sField, @Nullable final int [] aValue)
  {
    return aValue == null ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotNull (@Nonnull final String sField, @Nullable final long [] aValue)
  {
    return aValue == null ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotNull (@Nonnull final String sField, @Nullable final short [] aValue)
  {
    return aValue == null ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotNull (@Nonnull final String sField, @Nullable final Object aValue)
  {
    return aValue == null ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotNull (@Nonnull final String sField, @Nullable final Object [] aValue)
  {
    return aValue == null ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotEmpty (@Nonnull final String sField, @Nullable final boolean [] aValue)
  {
    return aValue == null || aValue.length == 0 ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotEmpty (@Nonnull final String sField, @Nullable final byte [] aValue)
  {
    return aValue == null || aValue.length == 0 ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotEmpty (@Nonnull final String sField, @Nullable final char [] aValue)
  {
    return aValue == null || aValue.length == 0 ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotEmpty (@Nonnull final String sField, @Nullable final double [] aValue)
  {
    return aValue == null || aValue.length == 0 ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotEmpty (@Nonnull final String sField, @Nullable final float [] aValue)
  {
    return aValue == null || aValue.length == 0 ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotEmpty (@Nonnull final String sField, @Nullable final int [] aValue)
  {
    return aValue == null || aValue.length == 0 ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotEmpty (@Nonnull final String sField, @Nullable final long [] aValue)
  {
    return aValue == null || aValue.length == 0 ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotEmpty (@Nonnull final String sField, @Nullable final short [] aValue)
  {
    return aValue == null || aValue.length == 0 ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotEmpty (@Nonnull final String sField, @Nullable final Object [] aValue)
  {
    return aValue == null || aValue.length == 0 ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotEmpty (@Nonnull final String sField, @Nullable final Collection <?> aValue)
  {
    return aValue == null || aValue.isEmpty () ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotEmpty (@Nonnull final String sField, @Nullable final Map <?, ?> aValue)
  {
    return aValue == null || aValue.isEmpty () ? this : append (sField, aValue);
  }

  @Nonnull
  public ToStringGenerator appendIfNotEmpty (@Nonnull final String sField, @Nullable final String sValue)
  {
    return sValue == null || sValue.length () == 0 ? this : append (sField, sValue);
  }

  @Override
  @Nonnull
  public String toString ()
  {
    if ((m_nIndex & APPENDED_CLOSING_BRACKET) == 0)
    {
      m_nIndex |= APPENDED_CLOSING_BRACKET;
      m_aSB.append (']');
    }
    return m_aSB.toString ();
  }

  /**
   * Create a {@link ToStringGenerator} for derived classes where the base class
   * also uses the {@link ToStringGenerator}. This avoids that the implementing
   * class name is emitted more than once.
   *
   * @param sSuperToString
   *        Always pass in <code>super.toString ()</code>
   * @return Never <code>null</code>
   */
  @Nonnull
  public static ToStringGenerator getDerived (@Nonnull final String sSuperToString)
  {
    // We don't need the object is "super.toString" is involved, because in
    // super.toString the object is already emitted!
    return new ToStringGenerator (null)._appendSuper (sSuperToString);
  }
}
