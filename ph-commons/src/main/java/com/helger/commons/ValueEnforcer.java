/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.commons;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.equals.EqualsHelper;

/**
 * This class contains "runtime assertions" utility methods. It works like
 * {@link java.util.Objects#requireNonNull(Object)} but offers many more
 * possibilities.
 *
 * @author Philip Helger
 */
@Immutable
public final class ValueEnforcer
{
  private static final AtomicBoolean ENABLED = new AtomicBoolean (true);
  private static final Logger LOGGER = LoggerFactory.getLogger (ValueEnforcer.class);

  private ValueEnforcer ()
  {}

  /**
   * @return <code>true</code> if the assertions are enabled, <code>false</code>
   *         otherwise. By default the checks are enabled.
   */
  public static boolean isEnabled ()
  {
    return ENABLED.get ();
  }

  /**
   * Enable or disable the checks. By default checks are enabled.
   *
   * @param bEnabled
   *        <code>true</code> to enable it, <code>false</code> otherwise.
   */
  public static void setEnabled (final boolean bEnabled)
  {
    ENABLED.set (bEnabled);
    LOGGER.info ("ValueEnforcer checks are now " + (bEnabled ? "enabled" : "disabled"));
  }

  /**
   * Check that the passed value is <code>true</code>.
   *
   * @param bValue
   *        The value to check.
   * @param sMsg
   *        The message to be emitted in case the value is <code>false</code>
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isTrue (final boolean bValue, final String sMsg)
  {
    if (isEnabled ())
      isTrue (bValue, () -> sMsg);
  }

  /**
   * Check that the passed value is <code>true</code>.
   *
   * @param bValue
   *        The value to check.
   * @param aMsg
   *        The message to be emitted in case the value is <code>false</code>
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isTrue (final boolean bValue, @Nonnull final Supplier <? extends String> aMsg)
  {
    if (isEnabled ())
      if (!bValue)
        throw new IllegalArgumentException ("The expression must be true but it is not: " + aMsg.get ());
  }

  /**
   * Check that the passed value is <code>true</code>.
   *
   * @param aValue
   *        The value to check.
   * @param sMsg
   *        The message to be emitted in case the value is <code>false</code>
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isTrue (@Nonnull final BooleanSupplier aValue, final String sMsg)
  {
    if (isEnabled ())
      isTrue (aValue, () -> sMsg);
  }

  /**
   * Check that the passed value is <code>true</code>.
   *
   * @param aValue
   *        The value to check.
   * @param aMsg
   *        The message to be emitted in case the value is <code>false</code>
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isTrue (@Nonnull final BooleanSupplier aValue, @Nonnull final Supplier <? extends String> aMsg)
  {
    if (isEnabled ())
      if (!aValue.getAsBoolean ())
        throw new IllegalArgumentException ("The expression must be true but it is not: " + aMsg.get ());
  }

  /**
   * Check that the passed value is <code>false</code>.
   *
   * @param bValue
   *        The value to check.
   * @param sMsg
   *        The message to be emitted in case the value is <code>true</code>
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isFalse (final boolean bValue, final String sMsg)
  {
    if (isEnabled ())
      isFalse (bValue, () -> sMsg);
  }

  /**
   * Check that the passed value is <code>false</code>.
   *
   * @param bValue
   *        The value to check.
   * @param aMsg
   *        The message to be emitted in case the value is <code>true</code>
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isFalse (final boolean bValue, @Nonnull final Supplier <? extends String> aMsg)
  {
    if (isEnabled ())
      if (bValue)
        throw new IllegalArgumentException ("The expression must be false but it is not: " + aMsg.get ());
  }

  /**
   * Check that the passed value is <code>false</code>.
   *
   * @param aValue
   *        The value to check.
   * @param sMsg
   *        The message to be emitted in case the value is <code>true</code>
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isFalse (@Nonnull final BooleanSupplier aValue, final String sMsg)
  {
    if (isEnabled ())
      isFalse (aValue, () -> sMsg);
  }

  /**
   * Check that the passed value is <code>false</code>.
   *
   * @param aValue
   *        The value to check.
   * @param aMsg
   *        The message to be emitted in case the value is <code>true</code>
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isFalse (@Nonnull final BooleanSupplier aValue, @Nonnull final Supplier <? extends String> aMsg)
  {
    if (isEnabled ())
      if (aValue.getAsBoolean ())
        throw new IllegalArgumentException ("The expression must be false but it is not: " + aMsg.get ());
  }

  /**
   * Check that the passed value is an instance of the passed class.
   *
   * @param aValue
   *        The value to check. May be <code>null</code>.
   * @param aClass
   *        The class of which the passed value must be an instance. May not be
   *        <code>null</code>.
   * @param sMsg
   *        The message to be emitted in case the value is <code>false</code>
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   * @param <T>
   *        Type to check.
   */
  public static <T> void isInstanceOf (@Nullable final T aValue,
                                       @Nonnull final Class <? extends T> aClass,
                                       final String sMsg)
  {
    if (isEnabled ())
      isInstanceOf (aValue, aClass, () -> sMsg);
  }

  /**
   * Check that the passed value is an instance of the passed class.
   *
   * @param aValue
   *        The value to check. May be <code>null</code>.
   * @param aClass
   *        The class of which the passed value must be an instance. May not be
   *        <code>null</code>.
   * @param aMsg
   *        The message to be emitted in case the value is <code>false</code>
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   * @param <T>
   *        Type to check.
   */
  public static <T> void isInstanceOf (@Nullable final T aValue,
                                       @Nonnull final Class <? extends T> aClass,
                                       @Nonnull final Supplier <? extends String> aMsg)
  {
    notNull (aClass, "Class");
    if (isEnabled ())
      if (!aClass.isInstance (aValue))
        throw new IllegalArgumentException (aMsg.get () +
                                            " must be of class " +
                                            aClass.getName () +
                                            " but is of type " +
                                            aValue.getClass ().getName ());
  }

  /**
   * Check that the passed value is not <code>null</code>.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The value to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws NullPointerException
   *         if the passed value is <code>null</code>.
   */
  public static <T> T notNull (final T aValue, final String sName)
  {
    if (isEnabled ())
      if (aValue == null)
        throw new NullPointerException ("The value of '" + sName + "' may not be null!");
    return aValue;
  }

  /**
   * Check that the passed value is not <code>null</code>.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The value to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws NullPointerException
   *         if the passed value is <code>null</code>.
   */
  public static <T> T notNull (final T aValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (aValue == null)
        throw new NullPointerException ("The value of '" + aName.get () + "' may not be null!");
    return aValue;
  }

  /**
   * Check that the passed value is <code>null</code>.
   *
   * @param aValue
   *        The value to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isNull (final Object aValue, final String sName)
  {
    if (isEnabled ())
      isNull (aValue, () -> sName);
  }

  /**
   * Check that the passed value is <code>null</code>.
   *
   * @param aValue
   *        The value to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isNull (final Object aValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (aValue != null)
        throw new IllegalArgumentException ("The value of '" + aName.get () + "' must be null but is " + aValue);
  }

  /**
   * Check that the passed String is neither <code>null</code> nor empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The String to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static <T extends CharSequence> T notEmpty (final T aValue, final String sName)
  {
    if (isEnabled ())
      return notEmpty (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed String is neither <code>null</code> nor empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The String to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static <T extends CharSequence> T notEmpty (final T aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.length () == 0)
        throw new IllegalArgumentException ("The value of the string '" + aName.get () + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static <T> T [] notEmpty (final T [] aValue, final String sName)
  {
    return notEmpty (aValue, () -> sName);
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The Array to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static <T> T [] notEmpty (final T [] aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.length == 0)
        throw new IllegalArgumentException ("The value of the array '" + aName.get () + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static boolean [] notEmpty (final boolean [] aValue, final String sName)
  {
    if (isEnabled ())
      return notEmpty (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static boolean [] notEmpty (final boolean [] aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.length == 0)
        throw new IllegalArgumentException ("The value of the array '" + aName.get () + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static byte [] notEmpty (final byte [] aValue, final String sName)
  {
    if (isEnabled ())
      return notEmpty (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static byte [] notEmpty (final byte [] aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.length == 0)
        throw new IllegalArgumentException ("The value of the array '" + aName.get () + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static char [] notEmpty (final char [] aValue, final String sName)
  {
    if (isEnabled ())
      return notEmpty (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static char [] notEmpty (final char [] aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.length == 0)
        throw new IllegalArgumentException ("The value of the array '" + aName.get () + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static double [] notEmpty (final double [] aValue, final String sName)
  {
    if (isEnabled ())
      return notEmpty (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static double [] notEmpty (final double [] aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.length == 0)
        throw new IllegalArgumentException ("The value of the array '" + aName.get () + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static float [] notEmpty (final float [] aValue, final String sName)
  {
    if (isEnabled ())
      return notEmpty (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static float [] notEmpty (final float [] aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.length == 0)
        throw new IllegalArgumentException ("The value of the array '" + aName.get () + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static int [] notEmpty (final int [] aValue, final String sName)
  {
    if (isEnabled ())
      return notEmpty (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static int [] notEmpty (final int [] aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.length == 0)
        throw new IllegalArgumentException ("The value of the array '" + aName.get () + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static long [] notEmpty (final long [] aValue, final String sName)
  {
    if (isEnabled ())
      return notEmpty (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static long [] notEmpty (final long [] aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.length == 0)
        throw new IllegalArgumentException ("The value of the array '" + aName.get () + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static short [] notEmpty (final short [] aValue, final String sName)
  {
    if (isEnabled ())
      return notEmpty (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  public static short [] notEmpty (final short [] aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.length == 0)
        throw new IllegalArgumentException ("The value of the array '" + aName.get () + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed {@link Collection} is neither <code>null</code> nor
   * empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The String to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @CodingStyleguideUnaware
  public static <T extends Collection <?>> T notEmpty (final T aValue, final String sName)
  {
    if (isEnabled ())
      return notEmpty (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed {@link Collection} is neither <code>null</code> nor
   * empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The String to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @CodingStyleguideUnaware
  public static <T extends Collection <?>> T notEmpty (final T aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.isEmpty ())
        throw new IllegalArgumentException ("The value of the collection '" + aName.get () + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed {@link Iterable} is neither <code>null</code> nor
   * empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The String to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @CodingStyleguideUnaware
  public static <T extends Iterable <?>> T notEmpty (final T aValue, final String sName)
  {
    if (isEnabled ())
      return notEmpty (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed {@link Iterable} is neither <code>null</code> nor
   * empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The String to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @CodingStyleguideUnaware
  public static <T extends Iterable <?>> T notEmpty (final T aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (!aValue.iterator ().hasNext ())
        throw new IllegalArgumentException ("The value of the iterable '" + aName.get () + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Collection is neither <code>null</code> nor empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The String to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @CodingStyleguideUnaware
  public static <T extends Map <?, ?>> T notEmpty (final T aValue, final String sName)
  {
    if (isEnabled ())
      return notEmpty (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed Collection is neither <code>null</code> nor empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The String to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @CodingStyleguideUnaware
  public static <T extends Map <?, ?>> T notEmpty (final T aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.isEmpty ())
        throw new IllegalArgumentException ("The value of the map '" + aName.get () + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array contains no <code>null</code> value. But the
   * whole array can be <code>null</code> or empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The Array to check. May be <code>null</code>.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value. Maybe <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is not empty and a <code>null</code> value is
   *         contained
   */
  @Nullable
  public static <T> T [] noNullValue (final T [] aValue, final String sName)
  {
    if (isEnabled ())
      return noNullValue (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed Array contains no <code>null</code> value. But the
   * whole array can be <code>null</code> or empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The Array to check. May be <code>null</code>.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value. Maybe <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is not empty and a <code>null</code> value is
   *         contained
   */
  @Nullable
  public static <T> T [] noNullValue (final T [] aValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (aValue != null)
      {
        int nIndex = 0;
        for (final T aItem : aValue)
        {
          if (aItem == null)
            throw new IllegalArgumentException ("Item " + nIndex + " of array '" + aName.get () + "' may not be null!");
          ++nIndex;
        }
      }
    return aValue;
  }

  /**
   * Check that the passed iterable contains no <code>null</code> value. But the
   * whole iterable can be <code>null</code> or empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The collection to check. May be <code>null</code>.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value. Maybe <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is not empty and a <code>null</code> value is
   *         contained
   */
  @Nullable
  public static <T extends Iterable <?>> T noNullValue (final T aValue, final String sName)
  {
    if (isEnabled ())
      return noNullValue (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed iterable contains no <code>null</code> value. But the
   * whole iterable can be <code>null</code> or empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The collection to check. May be <code>null</code>.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value. Maybe <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is not empty and a <code>null</code> value is
   *         contained
   */
  @Nullable
  public static <T extends Iterable <?>> T noNullValue (final T aValue,
                                                        @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (aValue != null)
      {
        int nIndex = 0;
        for (final Object aItem : aValue)
        {
          if (aItem == null)
            throw new IllegalArgumentException ("Item " +
                                                nIndex +
                                                " of iterable '" +
                                                aName.get () +
                                                "' may not be null!");
          ++nIndex;
        }
      }
    return aValue;
  }

  /**
   * Check that the passed map is neither <code>null</code> nor empty and that
   * no <code>null</code> key or value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The map to check. May be <code>null</code>.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value. Maybe <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is not empty and a <code>null</code> key or
   *         <code>null</code> value is contained
   */
  @Nullable
  @CodingStyleguideUnaware
  public static <T extends Map <?, ?>> T noNullValue (final T aValue, final String sName)
  {
    if (isEnabled ())
      return noNullValue (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed map is neither <code>null</code> nor empty and that
   * no <code>null</code> key or value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The map to check. May be <code>null</code>.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value. Maybe <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is not empty and a <code>null</code> key or
   *         <code>null</code> value is contained
   */
  @Nullable
  @CodingStyleguideUnaware
  public static <T extends Map <?, ?>> T noNullValue (final T aValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (aValue != null)
      {
        for (final Map.Entry <?, ?> aEntry : aValue.entrySet ())
        {
          if (aEntry.getKey () == null)
            throw new IllegalArgumentException ("A key of map '" + aName.get () + "' may not be null!");
          if (aEntry.getValue () == null)
            throw new IllegalArgumentException ("A value of map '" + aName.get () + "' may not be null!");
        }
      }
    return aValue;
  }

  /**
   * Check that the passed Array is not <code>null</code> and that no
   * <code>null</code> value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is null or a <code>null</code> value is
   *         contained
   */
  public static <T> T [] notNullNoNullValue (final T [] aValue, final String sName)
  {
    if (isEnabled ())
      return notNullNoNullValue (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed Array is not <code>null</code> and that no
   * <code>null</code> value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The Array to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is null or a <code>null</code> value is
   *         contained
   */
  public static <T> T [] notNullNoNullValue (final T [] aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    noNullValue (aValue, aName);
    return aValue;
  }

  /**
   * Check that the passed collection is not <code>null</code> and that no
   * <code>null</code> value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The collection to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is <code>null</code> or a <code>null</code>
   *         value is contained
   */
  public static <T extends Iterable <?>> T notNullNoNullValue (final T aValue, final String sName)
  {
    if (isEnabled ())
      return notNullNoNullValue (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed collection is not <code>null</code> and that no
   * <code>null</code> value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The collection to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is <code>null</code> or a <code>null</code>
   *         value is contained
   */
  public static <T extends Iterable <?>> T notNullNoNullValue (final T aValue,
                                                               @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    noNullValue (aValue, aName);
    return aValue;
  }

  /**
   * Check that the passed map is not <code>null</code> and that no
   * <code>null</code> value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The map to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is <code>null</code> or a <code>null</code>
   *         value is contained
   */
  @CodingStyleguideUnaware
  public static <T extends Map <?, ?>> T notNullNoNullValue (final T aValue, final String sName)
  {
    if (isEnabled ())
      return notNullNoNullValue (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed map is not <code>null</code> and that no
   * <code>null</code> value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The map to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is <code>null</code> or a <code>null</code>
   *         value is contained
   */
  @CodingStyleguideUnaware
  public static <T extends Map <?, ?>> T notNullNoNullValue (final T aValue,
                                                             @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    noNullValue (aValue, aName);
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty and that
   * no <code>null</code> value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty or a <code>null</code> value is
   *         contained
   */
  public static <T> T [] notEmptyNoNullValue (final T [] aValue, final String sName)
  {
    if (isEnabled ())
      return notEmptyNoNullValue (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty and that
   * no <code>null</code> value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The Array to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty or a <code>null</code> value is
   *         contained
   */
  public static <T> T [] notEmptyNoNullValue (final T [] aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notEmpty (aValue, aName);
    noNullValue (aValue, aName);
    return aValue;
  }

  /**
   * Check that the passed collection is neither <code>null</code> nor empty and
   * that no <code>null</code> value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The collection to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty or a <code>null</code> value is
   *         contained
   */
  public static <T extends Iterable <?>> T notEmptyNoNullValue (final T aValue, final String sName)
  {
    if (isEnabled ())
      return notEmptyNoNullValue (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed collection is neither <code>null</code> nor empty and
   * that no <code>null</code> value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The collection to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty or a <code>null</code> value is
   *         contained
   */
  public static <T extends Iterable <?>> T notEmptyNoNullValue (final T aValue,
                                                                @Nonnull final Supplier <? extends String> aName)
  {
    notEmpty (aValue, aName);
    noNullValue (aValue, aName);
    return aValue;
  }

  /**
   * Check that the passed map is neither <code>null</code> nor empty and that
   * no <code>null</code> value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The map to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty or a <code>null</code> value is
   *         contained
   */
  @CodingStyleguideUnaware
  public static <T extends Map <?, ?>> T notEmptyNoNullValue (final T aValue, final String sName)
  {
    if (isEnabled ())
      return notEmptyNoNullValue (aValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed map is neither <code>null</code> nor empty and that
   * no <code>null</code> value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The map to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is empty or a <code>null</code> value is
   *         contained
   */
  @CodingStyleguideUnaware
  public static <T extends Map <?, ?>> T notEmptyNoNullValue (final T aValue,
                                                              @Nonnull final Supplier <? extends String> aName)
  {
    notEmpty (aValue, aName);
    noNullValue (aValue, aName);
    return aValue;
  }

  /**
   * Check that the passed value is not <code>null</code> and not equal to the
   * provided value.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The value to check. May not be <code>null</code>.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @param aUnexpectedValue
   *        The value that may not be equal to aValue. May not be
   *        <code>null</code>.
   * @return The passed value.
   */
  public static <T> T notNullNotEquals (final T aValue, final String sName, @Nonnull final T aUnexpectedValue)
  {
    if (isEnabled ())
      return notNullNotEquals (aValue, () -> sName, aUnexpectedValue);
    return aValue;
  }

  /**
   * Check that the passed value is not <code>null</code> and not equal to the
   * provided value.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The value to check. May not be <code>null</code>.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @param aUnexpectedValue
   *        The value that may not be equal to aValue. May not be
   *        <code>null</code>.
   * @return The passed value.
   */
  public static <T> T notNullNotEquals (final T aValue,
                                        @Nonnull final Supplier <? extends String> aName,
                                        @Nonnull final T aUnexpectedValue)
  {
    notNull (aValue, aName);
    notNull (aUnexpectedValue, "UnexpectedValue");
    if (isEnabled ())
      if (aValue.equals (aUnexpectedValue))
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' may not be equal to " +
                                            aUnexpectedValue +
                                            "!");
    return aValue;
  }

  /**
   * Check that the passed value is not <code>null</code> and equal to the
   * provided expected value.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The value to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @param aExpectedValue
   *        The expected value. May not be <code>null</code>.
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static <T> T notNullAndEquals (final T aValue, final String sName, @Nonnull final T aExpectedValue)
  {
    if (isEnabled ())
      return notNullAndEquals (aValue, () -> sName, aExpectedValue);
    return aValue;
  }

  /**
   * Check that the passed value is not <code>null</code> and equal to the
   * provided expected value.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The value to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @param aExpectedValue
   *        The expected value. May not be <code>null</code>.
   * @return The passed value.
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static <T> T notNullAndEquals (final T aValue,
                                        @Nonnull final Supplier <? extends String> aName,
                                        @Nonnull final T aExpectedValue)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (!aValue.equals (aExpectedValue))
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' does not match the expected value. Passed value: " +
                                            aValue +
                                            " -- Expected value: " +
                                            aExpectedValue);
    return aValue;
  }

  /**
   * Check that the passed value is the same as the provided expected value
   * using <code>==</code> to check comparison.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The value to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @param aExpectedValue
   *        The expected value. May be <code>null</code>.
   * @return The passed value and maybe <code>null</code> if the expected value
   *         is null.
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static <T> T isSame (final T aValue, final String sName, @Nullable final T aExpectedValue)
  {
    if (isEnabled ())
      return isSame (aValue, () -> sName, aExpectedValue);
    return aValue;
  }

  /**
   * Check that the passed value is the same as the provided expected value
   * using <code>==</code> to check comparison.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The value to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @param aExpectedValue
   *        The expected value. May be <code>null</code>.
   * @return The passed value and maybe <code>null</code> if the expected value
   *         is <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static <T> T isSame (final T aValue,
                              @Nonnull final Supplier <? extends String> aName,
                              @Nullable final T aExpectedValue)
  {
    if (isEnabled ())
      if (EqualsHelper.identityDifferent (aValue, aExpectedValue))
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' does not match the expected value. Passed value: " +
                                            aValue +
                                            " -- Expected value: " +
                                            aExpectedValue);
    return aValue;
  }

  /**
   * Check that the passed value is the same as the provided expected value
   * using <code>equals</code> to check comparison.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The value to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @param aExpectedValue
   *        The expected value. May be <code>null</code>.
   * @return The passed value and maybe <code>null</code> if the expected value
   *         is null.
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static <T> T isEqual (final T aValue, @Nullable final T aExpectedValue, final String sName)
  {
    if (isEnabled ())
      return isEqual (aValue, aExpectedValue, () -> sName);
    return aValue;
  }

  /**
   * Check that the passed value is the same as the provided expected value
   * using <code>equals</code> to check comparison.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The value to check.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @param aExpectedValue
   *        The expected value. May be <code>null</code>.
   * @return The passed value and maybe <code>null</code> if the expected value
   *         is null.
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static <T> T isEqual (final T aValue,
                               @Nullable final T aExpectedValue,
                               @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (!EqualsHelper.equals (aValue, aExpectedValue))
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' does not match the expected value. Passed value: " +
                                            aValue +
                                            " -- Expected value: " +
                                            aExpectedValue);
    return aValue;
  }

  /**
   * Check that the passed value is the same as the provided expected value
   * using <code>==</code> to check comparison.
   *
   * @param nValue
   *        The First value.
   * @param nExpectedValue
   *        The expected value.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isEqual (final int nValue, final int nExpectedValue, final String sName)
  {
    if (isEnabled ())
      isEqual (nValue, nExpectedValue, () -> sName);
  }

  /**
   * Check that the passed value is the same as the provided expected value
   * using <code>==</code> to check comparison.
   *
   * @param nValue
   *        The First value.
   * @param nExpectedValue
   *        The expected value.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isEqual (final int nValue,
                              final int nExpectedValue,
                              @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (nValue != nExpectedValue)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' does not match the expected value. Passed value: " +
                                            nValue +
                                            " -- Expected value: " +
                                            nExpectedValue);
  }

  /**
   * Check that the passed value is the same as the provided expected value
   * using <code>==</code> to check comparison.
   *
   * @param nValue
   *        The First value.
   * @param nExpectedValue
   *        The expected value.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isEqual (final long nValue, final long nExpectedValue, final String sName)
  {
    if (isEnabled ())
      isEqual (nValue, nExpectedValue, () -> sName);
  }

  /**
   * Check that the passed value is the same as the provided expected value
   * using <code>==</code> to check comparison.
   *
   * @param nValue
   *        The First value.
   * @param nExpectedValue
   *        The expected value.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isEqual (final long nValue,
                              final long nExpectedValue,
                              @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (nValue != nExpectedValue)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' does not match the expected value. Passed value: " +
                                            nValue +
                                            " -- Expected value: " +
                                            nExpectedValue);
  }

  /**
   * Check that the passed value is the same as the provided expected value
   * using <code>==</code> to check comparison.
   *
   * @param dValue
   *        The First value.
   * @param dExpectedValue
   *        The expected value.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isEqual (final double dValue, final double dExpectedValue, final String sName)
  {
    if (isEnabled ())
      isEqual (dValue, dExpectedValue, () -> sName);
  }

  /**
   * Check that the passed value is the same as the provided expected value
   * using <code>==</code> to check comparison.
   *
   * @param dValue
   *        The First value.
   * @param dExpectedValue
   *        The expected value.
   * @param aName
   *        The name of the value (e.g. the parameter name)
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isEqual (final double dValue,
                              final double dExpectedValue,
                              @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (!EqualsHelper.equals (dValue, dExpectedValue))
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' does not match the expected value. Passed value: " +
                                            dValue +
                                            " -- Expected value: " +
                                            dExpectedValue);
  }

  public static int isGE0 (final int nValue, final String sName)
  {
    if (isEnabled ())
      return isGE0 (nValue, () -> sName);
    return nValue;
  }

  public static int isGE0 (final int nValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (nValue < 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be >= 0! The current value is: " +
                                            nValue);
    return nValue;
  }

  public static long isGE0 (final long nValue, final String sName)
  {
    if (isEnabled ())
      return isGE0 (nValue, () -> sName);
    return nValue;
  }

  public static long isGE0 (final long nValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (nValue < 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be >= 0! The current value is: " +
                                            nValue);
    return nValue;
  }

  public static short isGE0 (final short nValue, final String sName)
  {
    if (isEnabled ())
      return isGE0 (nValue, () -> sName);
    return nValue;
  }

  public static short isGE0 (final short nValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (nValue < 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be >= 0! The current value is: " +
                                            nValue);
    return nValue;
  }

  public static double isGE0 (final double dValue, final String sName)
  {
    if (isEnabled ())
      return isGE0 (dValue, () -> sName);
    return dValue;
  }

  public static double isGE0 (final double dValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (dValue < 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be >= 0! The current value is: " +
                                            dValue);
    return dValue;
  }

  public static float isGE0 (final float fValue, final String sName)
  {
    if (isEnabled ())
      return isGE0 (fValue, () -> sName);
    return fValue;
  }

  public static float isGE0 (final float fValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (fValue < 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be >= 0! The current value is: " +
                                            fValue);
    return fValue;
  }

  public static BigDecimal isGE0 (final BigDecimal aValue, final String sName)
  {
    if (isEnabled ())
      return isGE0 (aValue, () -> sName);
    return aValue;
  }

  public static BigDecimal isGE0 (final BigDecimal aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.compareTo (BigDecimal.ZERO) < 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be >= 0! The current value is: " +
                                            aValue);
    return aValue;
  }

  public static BigInteger isGE0 (final BigInteger aValue, final String sName)
  {
    if (isEnabled ())
      return isGE0 (aValue, () -> sName);
    return aValue;
  }

  public static BigInteger isGE0 (final BigInteger aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.compareTo (BigInteger.ZERO) < 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be >= 0! The current value is: " +
                                            aValue);
    return aValue;
  }

  public static int isGT0 (final int nValue, final String sName)
  {
    if (isEnabled ())
      return isGT0 (nValue, () -> sName);
    return nValue;
  }

  public static int isGT0 (final int nValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (nValue <= 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be > 0! The current value is: " +
                                            nValue);
    return nValue;
  }

  public static long isGT0 (final long nValue, final String sName)
  {
    if (isEnabled ())
      return isGT0 (nValue, () -> sName);
    return nValue;
  }

  public static long isGT0 (final long nValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (nValue <= 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be > 0! The current value is: " +
                                            nValue);
    return nValue;
  }

  public static short isGT0 (final short nValue, final String sName)
  {
    if (isEnabled ())
      return isGT0 (nValue, () -> sName);
    return nValue;
  }

  public static short isGT0 (final short nValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (nValue <= 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be > 0! The current value is: " +
                                            nValue);
    return nValue;
  }

  public static double isGT0 (final double dValue, final String sName)
  {
    if (isEnabled ())
      return isGT0 (dValue, () -> sName);
    return dValue;
  }

  public static double isGT0 (final double dValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (dValue <= 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be > 0! The current value is: " +
                                            dValue);
    return dValue;
  }

  public static float isGT0 (final float fValue, final String sName)
  {
    if (isEnabled ())
      return isGT0 (fValue, () -> sName);
    return fValue;
  }

  public static float isGT0 (final float fValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (fValue <= 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be > 0! The current value is: " +
                                            fValue);
    return fValue;
  }

  public static BigDecimal isGT0 (final BigDecimal aValue, final String sName)
  {
    if (isEnabled ())
      return isGT0 (aValue, () -> sName);
    return aValue;
  }

  public static BigDecimal isGT0 (final BigDecimal aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.compareTo (BigDecimal.ZERO) <= 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be > 0! The current value is: " +
                                            aValue);
    return aValue;
  }

  public static BigInteger isGT0 (final BigInteger aValue, final String sName)
  {
    if (isEnabled ())
      return isGT0 (aValue, () -> sName);
    return aValue;
  }

  public static BigInteger isGT0 (final BigInteger aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.compareTo (BigInteger.ZERO) <= 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be > 0! The current value is: " +
                                            aValue);
    return aValue;
  }

  public static int isNE0 (final int nValue, final String sName)
  {
    if (isEnabled ())
      return isNE0 (nValue, () -> sName);
    return nValue;
  }

  public static int isNE0 (final int nValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (nValue == 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must not be 0! The current value is: " +
                                            nValue);
    return nValue;
  }

  public static long isNE0 (final long nValue, final String sName)
  {
    if (isEnabled ())
      return isNE0 (nValue, () -> sName);
    return nValue;
  }

  public static long isNE0 (final long nValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (nValue == 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must not be 0! The current value is: " +
                                            nValue);
    return nValue;
  }

  public static double isNE0 (final double dValue, final String sName)
  {
    if (isEnabled ())
      return isNE0 (dValue, () -> sName);
    return dValue;
  }

  public static double isNE0 (final double dValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (dValue == 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must not be 0! The current value is: " +
                                            dValue);
    return dValue;
  }

  public static BigDecimal isNE0 (final BigDecimal aValue, final String sName)
  {
    if (isEnabled ())
      return isNE0 (aValue, () -> sName);
    return aValue;
  }

  public static BigDecimal isNE0 (final BigDecimal aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.compareTo (BigDecimal.ZERO) == 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must not be 0! The current value is: " +
                                            aValue);
    return aValue;
  }

  public static BigInteger isNE0 (final BigInteger aValue, final String sName)
  {
    if (isEnabled ())
      return isNE0 (aValue, () -> sName);
    return aValue;
  }

  public static BigInteger isNE0 (final BigInteger aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.compareTo (BigInteger.ZERO) == 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must not be 0! The current value is: " +
                                            aValue);
    return aValue;
  }

  public static int isLE0 (final int nValue, final String sName)
  {
    if (isEnabled ())
      return isLE0 (nValue, () -> sName);
    return nValue;
  }

  public static int isLE0 (final int nValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (nValue > 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be <= 0! The current value is: " +
                                            nValue);
    return nValue;
  }

  public static long isLE0 (final long nValue, final String sName)
  {
    if (isEnabled ())
      return isLE0 (nValue, () -> sName);
    return nValue;
  }

  public static long isLE0 (final long nValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (nValue > 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be <= 0! The current value is: " +
                                            nValue);
    return nValue;
  }

  public static short isLE0 (final short nValue, final String sName)
  {
    if (isEnabled ())
      return isLE0 (nValue, () -> sName);
    return nValue;
  }

  public static short isLE0 (final short nValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (nValue > 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be <= 0! The current value is: " +
                                            nValue);
    return nValue;
  }

  public static double isLE0 (final double dValue, final String sName)
  {
    if (isEnabled ())
      return isLE0 (dValue, () -> sName);
    return dValue;
  }

  public static double isLE0 (final double dValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (dValue > 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be <= 0! The current value is: " +
                                            dValue);
    return dValue;
  }

  public static float isLE0 (final float fValue, final String sName)
  {
    if (isEnabled ())
      return isLE0 (fValue, () -> sName);
    return fValue;
  }

  public static float isLE0 (final float fValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (fValue > 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be <= 0! The current value is: " +
                                            fValue);
    return fValue;
  }

  public static BigDecimal isLE0 (final BigDecimal aValue, final String sName)
  {
    if (isEnabled ())
      return isLE0 (aValue, () -> sName);
    return aValue;
  }

  public static BigDecimal isLE0 (final BigDecimal aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.compareTo (BigDecimal.ZERO) > 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be <= 0! The current value is: " +
                                            aValue);
    return aValue;
  }

  public static BigInteger isLE0 (final BigInteger aValue, final String sName)
  {
    if (isEnabled ())
      return isLE0 (aValue, () -> sName);
    return aValue;
  }

  public static BigInteger isLE0 (final BigInteger aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.compareTo (BigInteger.ZERO) > 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be <= 0! The current value is: " +
                                            aValue);
    return aValue;
  }

  public static int isLT0 (final int nValue, final String sName)
  {
    if (isEnabled ())
      return isLT0 (nValue, () -> sName);
    return nValue;
  }

  public static int isLT0 (final int nValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (nValue >= 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be < 0! The current value is: " +
                                            nValue);
    return nValue;
  }

  public static long isLT0 (final long nValue, final String sName)
  {
    if (isEnabled ())
      return isLT0 (nValue, () -> sName);
    return nValue;
  }

  public static long isLT0 (final long nValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (nValue >= 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be < 0! The current value is: " +
                                            nValue);
    return nValue;
  }

  public static short isLT0 (final short nValue, final String sName)
  {
    if (isEnabled ())
      return isLT0 (nValue, () -> sName);
    return nValue;
  }

  public static short isLT0 (final short nValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (nValue >= 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be < 0! The current value is: " +
                                            nValue);
    return nValue;
  }

  public static double isLT0 (final double dValue, final String sName)
  {
    if (isEnabled ())
      return isLT0 (dValue, () -> sName);
    return dValue;
  }

  public static double isLT0 (final double dValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (dValue >= 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be < 0! The current value is: " +
                                            dValue);
    return dValue;
  }

  public static float isLT0 (final float fValue, final String sName)
  {
    if (isEnabled ())
      return isLT0 (fValue, () -> sName);
    return fValue;
  }

  public static float isLT0 (final float fValue, @Nonnull final Supplier <? extends String> aName)
  {
    if (isEnabled ())
      if (fValue >= 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be < 0! The current value is: " +
                                            fValue);
    return fValue;
  }

  public static BigDecimal isLT0 (final BigDecimal aValue, final String sName)
  {
    if (isEnabled ())
      return isLT0 (aValue, () -> sName);
    return aValue;
  }

  public static BigDecimal isLT0 (final BigDecimal aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.compareTo (BigDecimal.ZERO) >= 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be < 0! The current value is: " +
                                            aValue);
    return aValue;
  }

  public static BigInteger isLT0 (final BigInteger aValue, final String sName)
  {
    if (isEnabled ())
      return isLT0 (aValue, () -> sName);
    return aValue;
  }

  public static BigInteger isLT0 (final BigInteger aValue, @Nonnull final Supplier <? extends String> aName)
  {
    notNull (aValue, aName);
    if (isEnabled ())
      if (aValue.compareTo (BigInteger.ZERO) >= 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be < 0! The current value is: " +
                                            aValue);
    return aValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param sName
   *        Name
   * @param nLowerBoundInclusive
   *        Lower bound
   * @param nUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static int isBetweenInclusive (final int nValue,
                                        final String sName,
                                        final int nLowerBoundInclusive,
                                        final int nUpperBoundInclusive)
  {
    if (isEnabled ())
      return isBetweenInclusive (nValue, () -> sName, nLowerBoundInclusive, nUpperBoundInclusive);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param aName
   *        Name
   * @param nLowerBoundInclusive
   *        Lower bound
   * @param nUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static int isBetweenInclusive (final int nValue,
                                        @Nonnull final Supplier <? extends String> aName,
                                        final int nLowerBoundInclusive,
                                        final int nUpperBoundInclusive)
  {
    if (isEnabled ())
      if (nValue < nLowerBoundInclusive || nValue > nUpperBoundInclusive)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be >= " +
                                            nLowerBoundInclusive +
                                            " and <= " +
                                            nUpperBoundInclusive +
                                            "! The current value is: " +
                                            nValue);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param sName
   *        Name
   * @param nLowerBoundInclusive
   *        Lower bound
   * @param nUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static long isBetweenInclusive (final long nValue,
                                         final String sName,
                                         final long nLowerBoundInclusive,
                                         final long nUpperBoundInclusive)
  {
    if (isEnabled ())
      return isBetweenInclusive (nValue, () -> sName, nLowerBoundInclusive, nUpperBoundInclusive);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param aName
   *        Name
   * @param nLowerBoundInclusive
   *        Lower bound
   * @param nUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static long isBetweenInclusive (final long nValue,
                                         @Nonnull final Supplier <? extends String> aName,
                                         final long nLowerBoundInclusive,
                                         final long nUpperBoundInclusive)
  {
    if (isEnabled ())
      if (nValue < nLowerBoundInclusive || nValue > nUpperBoundInclusive)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be >= " +
                                            nLowerBoundInclusive +
                                            " and <= " +
                                            nUpperBoundInclusive +
                                            "! The current value is: " +
                                            nValue);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param sName
   *        Name
   * @param nLowerBoundInclusive
   *        Lower bound
   * @param nUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static short isBetweenInclusive (final short nValue,
                                          final String sName,
                                          final short nLowerBoundInclusive,
                                          final short nUpperBoundInclusive)
  {
    if (isEnabled ())
      return isBetweenInclusive (nValue, () -> sName, nLowerBoundInclusive, nUpperBoundInclusive);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param aName
   *        Name
   * @param nLowerBoundInclusive
   *        Lower bound
   * @param nUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static short isBetweenInclusive (final short nValue,
                                          @Nonnull final Supplier <? extends String> aName,
                                          final short nLowerBoundInclusive,
                                          final short nUpperBoundInclusive)
  {
    if (isEnabled ())
      if (nValue < nLowerBoundInclusive || nValue > nUpperBoundInclusive)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be >= " +
                                            nLowerBoundInclusive +
                                            " and <= " +
                                            nUpperBoundInclusive +
                                            "! The current value is: " +
                                            nValue);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param dValue
   *        Value
   * @param sName
   *        Name
   * @param dLowerBoundInclusive
   *        Lower bound
   * @param dUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static double isBetweenInclusive (final double dValue,
                                           final String sName,
                                           final double dLowerBoundInclusive,
                                           final double dUpperBoundInclusive)
  {
    if (isEnabled ())
      return isBetweenInclusive (dValue, () -> sName, dLowerBoundInclusive, dUpperBoundInclusive);
    return dValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param dValue
   *        Value
   * @param aName
   *        Name
   * @param dLowerBoundInclusive
   *        Lower bound
   * @param dUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static double isBetweenInclusive (final double dValue,
                                           @Nonnull final Supplier <? extends String> aName,
                                           final double dLowerBoundInclusive,
                                           final double dUpperBoundInclusive)
  {
    if (isEnabled ())
      if (dValue < dLowerBoundInclusive || dValue > dUpperBoundInclusive)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be >= " +
                                            dLowerBoundInclusive +
                                            " and <= " +
                                            dUpperBoundInclusive +
                                            "! The current value is: " +
                                            dValue);
    return dValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param fValue
   *        Value
   * @param sName
   *        Name
   * @param fLowerBoundInclusive
   *        Lower bound
   * @param fUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static float isBetweenInclusive (final float fValue,
                                          final String sName,
                                          final float fLowerBoundInclusive,
                                          final float fUpperBoundInclusive)
  {
    if (isEnabled ())
      return isBetweenInclusive (fValue, () -> sName, fLowerBoundInclusive, fUpperBoundInclusive);
    return fValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param fValue
   *        Value
   * @param aName
   *        Name
   * @param fLowerBoundInclusive
   *        Lower bound
   * @param fUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static float isBetweenInclusive (final float fValue,
                                          @Nonnull final Supplier <? extends String> aName,
                                          final float fLowerBoundInclusive,
                                          final float fUpperBoundInclusive)
  {
    if (isEnabled ())
      if (fValue < fLowerBoundInclusive || fValue > fUpperBoundInclusive)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be >= " +
                                            fLowerBoundInclusive +
                                            " and <= " +
                                            fUpperBoundInclusive +
                                            "! The current value is: " +
                                            fValue);
    return fValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param aValue
   *        Value
   * @param sName
   *        Name
   * @param aLowerBoundInclusive
   *        Lower bound
   * @param aUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static BigDecimal isBetweenInclusive (final BigDecimal aValue,
                                               final String sName,
                                               @Nonnull final BigDecimal aLowerBoundInclusive,
                                               @Nonnull final BigDecimal aUpperBoundInclusive)
  {
    if (isEnabled ())
      return isBetweenInclusive (aValue, () -> sName, aLowerBoundInclusive, aUpperBoundInclusive);
    return aValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param aValue
   *        Value
   * @param aName
   *        Name
   * @param aLowerBoundInclusive
   *        Lower bound
   * @param aUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static BigDecimal isBetweenInclusive (final BigDecimal aValue,
                                               @Nonnull final Supplier <? extends String> aName,
                                               @Nonnull final BigDecimal aLowerBoundInclusive,
                                               @Nonnull final BigDecimal aUpperBoundInclusive)
  {
    notNull (aValue, aName);
    notNull (aLowerBoundInclusive, "LowerBoundInclusive");
    notNull (aUpperBoundInclusive, "UpperBoundInclusive");
    if (isEnabled ())
      if (aValue.compareTo (aLowerBoundInclusive) < 0 || aValue.compareTo (aUpperBoundInclusive) > 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be >= " +
                                            aLowerBoundInclusive +
                                            " and <= " +
                                            aUpperBoundInclusive +
                                            "! The current value is: " +
                                            aValue);
    return aValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param aValue
   *        Value
   * @param sName
   *        Name
   * @param aLowerBoundInclusive
   *        Lower bound
   * @param aUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static BigInteger isBetweenInclusive (final BigInteger aValue,
                                               final String sName,
                                               @Nonnull final BigInteger aLowerBoundInclusive,
                                               @Nonnull final BigInteger aUpperBoundInclusive)
  {
    if (isEnabled ())
      return isBetweenInclusive (aValue, () -> sName, aLowerBoundInclusive, aUpperBoundInclusive);
    return aValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param aValue
   *        Value
   * @param aName
   *        Name
   * @param aLowerBoundInclusive
   *        Lower bound
   * @param aUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static BigInteger isBetweenInclusive (final BigInteger aValue,
                                               @Nonnull final Supplier <? extends String> aName,
                                               @Nonnull final BigInteger aLowerBoundInclusive,
                                               @Nonnull final BigInteger aUpperBoundInclusive)
  {
    notNull (aValue, aName);
    notNull (aLowerBoundInclusive, "LowerBoundInclusive");
    notNull (aUpperBoundInclusive, "UpperBoundInclusive");
    if (isEnabled ())
      if (aValue.compareTo (aLowerBoundInclusive) < 0 || aValue.compareTo (aUpperBoundInclusive) > 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be >= " +
                                            aLowerBoundInclusive +
                                            " and <= " +
                                            aUpperBoundInclusive +
                                            "! The current value is: " +
                                            aValue);
    return aValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param sName
   *        Name
   * @param nLowerBoundExclusive
   *        Lower bound
   * @param nUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static int isBetweenExclusive (final int nValue,
                                        final String sName,
                                        final int nLowerBoundExclusive,
                                        final int nUpperBoundExclusive)
  {
    if (isEnabled ())
      return isBetweenExclusive (nValue, () -> sName, nLowerBoundExclusive, nUpperBoundExclusive);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param aName
   *        Name
   * @param nLowerBoundExclusive
   *        Lower bound
   * @param nUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static int isBetweenExclusive (final int nValue,
                                        @Nonnull final Supplier <? extends String> aName,
                                        final int nLowerBoundExclusive,
                                        final int nUpperBoundExclusive)
  {
    if (isEnabled ())
      if (nValue <= nLowerBoundExclusive || nValue >= nUpperBoundExclusive)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be > " +
                                            nLowerBoundExclusive +
                                            " and < " +
                                            nUpperBoundExclusive +
                                            "! The current value is: " +
                                            nValue);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param sName
   *        Name
   * @param nLowerBoundExclusive
   *        Lower bound
   * @param nUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static long isBetweenExclusive (final long nValue,
                                         final String sName,
                                         final long nLowerBoundExclusive,
                                         final long nUpperBoundExclusive)
  {
    if (isEnabled ())
      return isBetweenExclusive (nValue, () -> sName, nLowerBoundExclusive, nUpperBoundExclusive);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param aName
   *        Name
   * @param nLowerBoundExclusive
   *        Lower bound
   * @param nUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static long isBetweenExclusive (final long nValue,
                                         @Nonnull final Supplier <? extends String> aName,
                                         final long nLowerBoundExclusive,
                                         final long nUpperBoundExclusive)
  {
    if (isEnabled ())
      if (nValue <= nLowerBoundExclusive || nValue >= nUpperBoundExclusive)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be > " +
                                            nLowerBoundExclusive +
                                            " and < " +
                                            nUpperBoundExclusive +
                                            "! The current value is: " +
                                            nValue);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param sName
   *        Name
   * @param nLowerBoundExclusive
   *        Lower bound
   * @param nUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static short isBetweenExclusive (final short nValue,
                                          final String sName,
                                          final short nLowerBoundExclusive,
                                          final short nUpperBoundExclusive)
  {
    if (isEnabled ())
      return isBetweenExclusive (nValue, () -> sName, nLowerBoundExclusive, nUpperBoundExclusive);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param aName
   *        Name
   * @param nLowerBoundExclusive
   *        Lower bound
   * @param nUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static short isBetweenExclusive (final short nValue,
                                          @Nonnull final Supplier <? extends String> aName,
                                          final short nLowerBoundExclusive,
                                          final short nUpperBoundExclusive)
  {
    if (isEnabled ())
      if (nValue <= nLowerBoundExclusive || nValue >= nUpperBoundExclusive)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be > " +
                                            nLowerBoundExclusive +
                                            " and < " +
                                            nUpperBoundExclusive +
                                            "! The current value is: " +
                                            nValue);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param dValue
   *        Value
   * @param sName
   *        Name
   * @param dLowerBoundExclusive
   *        Lower bound
   * @param dUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static double isBetweenExclusive (final double dValue,
                                           final String sName,
                                           final double dLowerBoundExclusive,
                                           final double dUpperBoundExclusive)
  {
    if (isEnabled ())
      return isBetweenExclusive (dValue, () -> sName, dLowerBoundExclusive, dUpperBoundExclusive);
    return dValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param dValue
   *        Value
   * @param aName
   *        Name
   * @param dLowerBoundExclusive
   *        Lower bound
   * @param dUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static double isBetweenExclusive (final double dValue,
                                           @Nonnull final Supplier <? extends String> aName,
                                           final double dLowerBoundExclusive,
                                           final double dUpperBoundExclusive)
  {
    if (isEnabled ())
      if (dValue <= dLowerBoundExclusive || dValue >= dUpperBoundExclusive)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be > " +
                                            dLowerBoundExclusive +
                                            " and < " +
                                            dUpperBoundExclusive +
                                            "! The current value is: " +
                                            dValue);
    return dValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param fValue
   *        Value
   * @param sName
   *        Name
   * @param fLowerBoundExclusive
   *        Lower bound
   * @param fUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static float isBetweenExclusive (final float fValue,
                                          final String sName,
                                          final float fLowerBoundExclusive,
                                          final float fUpperBoundExclusive)
  {
    if (isEnabled ())
      return isBetweenExclusive (fValue, () -> sName, fLowerBoundExclusive, fUpperBoundExclusive);
    return fValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param fValue
   *        Value
   * @param aName
   *        Name
   * @param fLowerBoundExclusive
   *        Lower bound
   * @param fUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static float isBetweenExclusive (final float fValue,
                                          @Nonnull final Supplier <? extends String> aName,
                                          final float fLowerBoundExclusive,
                                          final float fUpperBoundExclusive)
  {
    if (isEnabled ())
      if (fValue <= fLowerBoundExclusive || fValue >= fUpperBoundExclusive)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be > " +
                                            fLowerBoundExclusive +
                                            " and < " +
                                            fUpperBoundExclusive +
                                            "! The current value is: " +
                                            fValue);
    return fValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param aValue
   *        Value
   * @param sName
   *        Name
   * @param aLowerBoundExclusive
   *        Lower bound
   * @param aUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static BigDecimal isBetweenExclusive (final BigDecimal aValue,
                                               final String sName,
                                               @Nonnull final BigDecimal aLowerBoundExclusive,
                                               @Nonnull final BigDecimal aUpperBoundExclusive)
  {
    if (isEnabled ())
      return isBetweenExclusive (aValue, () -> sName, aLowerBoundExclusive, aUpperBoundExclusive);
    return aValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param aValue
   *        Value
   * @param aName
   *        Name
   * @param aLowerBoundExclusive
   *        Lower bound
   * @param aUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static BigDecimal isBetweenExclusive (final BigDecimal aValue,
                                               @Nonnull final Supplier <? extends String> aName,
                                               @Nonnull final BigDecimal aLowerBoundExclusive,
                                               @Nonnull final BigDecimal aUpperBoundExclusive)
  {
    notNull (aValue, aName);
    notNull (aLowerBoundExclusive, "LowerBoundInclusive");
    notNull (aUpperBoundExclusive, "UpperBoundInclusive");
    if (isEnabled ())
      if (aValue.compareTo (aLowerBoundExclusive) <= 0 || aValue.compareTo (aUpperBoundExclusive) >= 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be > " +
                                            aLowerBoundExclusive +
                                            " and < " +
                                            aUpperBoundExclusive +
                                            "! The current value is: " +
                                            aValue);
    return aValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param aValue
   *        Value
   * @param sName
   *        Name
   * @param aLowerBoundExclusive
   *        Lower bound
   * @param aUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static BigInteger isBetweenExclusive (final BigInteger aValue,
                                               final String sName,
                                               @Nonnull final BigInteger aLowerBoundExclusive,
                                               @Nonnull final BigInteger aUpperBoundExclusive)
  {
    if (isEnabled ())
      return isBetweenExclusive (aValue, () -> sName, aLowerBoundExclusive, aUpperBoundExclusive);
    return aValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param aValue
   *        Value
   * @param aName
   *        Name
   * @param aLowerBoundExclusive
   *        Lower bound
   * @param aUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static BigInteger isBetweenExclusive (final BigInteger aValue,
                                               @Nonnull final Supplier <? extends String> aName,
                                               @Nonnull final BigInteger aLowerBoundExclusive,
                                               @Nonnull final BigInteger aUpperBoundExclusive)
  {
    notNull (aValue, aName);
    notNull (aLowerBoundExclusive, "LowerBoundInclusive");
    notNull (aUpperBoundExclusive, "UpperBoundInclusive");
    if (isEnabled ())
      if (aValue.compareTo (aLowerBoundExclusive) <= 0 || aValue.compareTo (aUpperBoundExclusive) >= 0)
        throw new IllegalArgumentException ("The value of '" +
                                            aName.get () +
                                            "' must be > " +
                                            aLowerBoundExclusive +
                                            " and < " +
                                            aUpperBoundExclusive +
                                            "! The current value is: " +
                                            aValue);
    return aValue;
  }

  private static void _isArrayOfsLen (@Nonnegative final int nArrayLen,
                                      @Nonnegative final int nOfs,
                                      @Nonnegative final int nLen)
  {
    isGE0 (nOfs, "Offset");
    isGE0 (nLen, "Length");
    if (isEnabled ())
      if ((nOfs + nLen) > nArrayLen)
        throw new IllegalArgumentException ("Offset (" +
                                            nOfs +
                                            ") + length (" +
                                            nLen +
                                            ") exceeds array length (" +
                                            nArrayLen +
                                            ")");
  }

  public static void isArrayOfsLen (final Object [] aArray, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }

  public static void isArrayOfsLen (final boolean [] aArray, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }

  public static void isArrayOfsLen (final byte [] aArray, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }

  public static void isArrayOfsLen (final char [] aArray, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }

  public static void isArrayOfsLen (final double [] aArray, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }

  public static void isArrayOfsLen (final float [] aArray, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }

  public static void isArrayOfsLen (final int [] aArray, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }

  public static void isArrayOfsLen (final long [] aArray, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }

  public static void isArrayOfsLen (final short [] aArray, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }
}
