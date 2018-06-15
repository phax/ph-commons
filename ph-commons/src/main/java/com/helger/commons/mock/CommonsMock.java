/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.mock;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.lang.ClassHierarchyCache;
import com.helger.commons.lang.GenericReflection;
import com.helger.commons.traits.IGetterDirectTrait;

/**
 * Mock objects by invoking their constructors with arbitrary objects. It
 * separates into static mocking rules and "per instance" mocking rules. Static
 * mocking rules apply to all instances of this class whereas "per instance"
 * mocking rules apply only to this instance.
 *
 * @author Philip Helger
 */
public final class CommonsMock
{
  /**
   * This class represents a parameter description for a single mockable type.
   * It consists of a parameter name (purely informational), parameter class
   * (required) and a generic supplier.
   *
   * @author Philip Helger
   */
  @Immutable
  public static final class Param
  {
    private final String m_sParamName;
    private final Class <?> m_aParamClass;
    private final Supplier <?> m_aDefaultValueSupplier;

    /**
     * Constructor for a mock parameter
     *
     * @param sParamName
     *        Name of the parameter - informational only. May neither be
     *        <code>null</code> nor empty.
     * @param aParamClass
     *        The class of the parameter. May neither be <code>null</code> nor
     *        empty.
     * @param aDefaultValueSupplier
     *        The default value supplier in case the caller did not provide an
     *        argument. May not be <code>null</code>.
     */
    public <T> Param (@Nonnull @Nonempty final String sParamName,
                      @Nonnull final Class <T> aParamClass,
                      @Nonnull final Supplier <T> aDefaultValueSupplier)
    {
      m_sParamName = ValueEnforcer.notEmpty (sParamName, "ParamName");
      m_aParamClass = ValueEnforcer.notNull (aParamClass, "ParamClass");
      m_aDefaultValueSupplier = ValueEnforcer.notNull (aDefaultValueSupplier, "DefaultValueSupplier");
    }

    @Nonnull
    @Nonempty
    public String getParamName ()
    {
      return m_sParamName;
    }

    @Nonnull
    public Class <?> getParamClass ()
    {
      return m_aParamClass;
    }

    @Nonnull
    public IGetterDirectTrait getDefaultValue ()
    {
      final Object aDefaultValue = m_aDefaultValueSupplier.get ();
      return () -> aDefaultValue;
    }

    @Override
    public String toString ()
    {
      return ClassHelper.getClassLocalName (m_aParamClass) + ":" + m_sParamName;
    }

    @Nonnull
    public static Param createConstant (@Nonnull @Nonempty final String sParamName, final boolean bDefault)
    {
      return createConstant (sParamName, boolean.class, Boolean.valueOf (bDefault));
    }

    /**
     * Create a {@link Param} with a constant default value.
     *
     * @param sParamName
     *        Parameter name. May neither be <code>null</code> nor empty.
     * @param aParamClass
     *        The parameter class. May not be <code>null</code>.
     * @param aDefault
     *        The constant default value to be used. May be <code>null</code>.
     * @return The {@link Param} object and never <code>null</code>.
     */
    @Nonnull
    public static <T> Param createConstant (@Nonnull @Nonempty final String sParamName,
                                            @Nonnull final Class <T> aParamClass,
                                            @Nullable final T aDefault)
    {
      return new Param (sParamName, aParamClass, () -> aDefault);
    }
  }

  private static final class MockSupplier
  {
    private final Class <?> m_aDstClass;
    private final Param [] m_aParams;
    private final Function <IGetterDirectTrait [], ?> m_aFct;

    private MockSupplier (@Nonnull final Class <?> aDstClass,
                          @Nullable final Param [] aParams,
                          @Nonnull final Function <IGetterDirectTrait [], ?> aFct)
    {
      m_aDstClass = aDstClass;
      m_aParams = aParams;
      m_aFct = aFct;
    }

    /**
     * This method is responsible for invoking the provided
     * factory/supplier/function with the provided parameters.
     *
     * @param aProvidedParams
     *        The parameter array. May be <code>null</code> or empty.
     * @return The mocked value. May be <code>null</code> but that would be a
     *         relatively rare case.
     */
    @Nullable
    public Object getMockedValue (@Nullable final Object [] aProvidedParams)
    {
      IGetterDirectTrait [] aEffectiveParams = null;
      if (m_aParams != null && m_aParams.length > 0)
      {
        // Parameters are present - convert all to IConvertibleTrait
        final int nRequiredParams = m_aParams.length;
        final int nProvidedParams = ArrayHelper.getSize (aProvidedParams);

        aEffectiveParams = new IGetterDirectTrait [nRequiredParams];
        for (int i = 0; i < nRequiredParams; ++i)
        {
          if (i < nProvidedParams && aProvidedParams[i] != null)
          {
            // Param provided and not null -> use provided
            final Object aVal = aProvidedParams[i];
            aEffectiveParams[i] = () -> aVal;
          }
          else
          {
            // Not provided or null -> use default
            aEffectiveParams[i] = m_aParams[i].getDefaultValue ();
          }
        }
      }
      return m_aFct.apply (aEffectiveParams);
    }

    /**
     * Create a mock supplier for a constant value.
     *
     * @param aConstant
     *        The constant value to be returned. May not be <code>null</code>.
     * @return Never <code>null</code>.
     */
    @Nonnull
    public static MockSupplier createConstant (@Nonnull final Object aConstant)
    {
      ValueEnforcer.notNull (aConstant, "Constant");
      return new MockSupplier (aConstant.getClass (), null, aParam -> aConstant);
    }

    /**
     * Create a mock supplier for a factory without parameters.
     *
     * @param aDstClass
     *        The destination class to be mocked. May not be <code>null</code>.
     * @param aSupplier
     *        The supplier/factory without parameters to be used. May not be
     *        <code>null</code>.
     * @return Never <code>null</code>.
     * @param <T>
     *        The type to be mocked
     */
    @Nonnull
    public static <T> MockSupplier createNoParams (@Nonnull final Class <T> aDstClass,
                                                   @Nonnull final Supplier <T> aSupplier)
    {
      ValueEnforcer.notNull (aDstClass, "DstClass");
      ValueEnforcer.notNull (aSupplier, "Supplier");
      return new MockSupplier (aDstClass, null, aParam -> aSupplier.get ());
    }

    /**
     * Create a mock supplier with parameters.
     *
     * @param aDstClass
     *        The destination class to be mocked. May not be <code>null</code>.
     * @param aParams
     *        The parameter declarations to be used. May not be
     *        <code>null</code>.
     * @param aSupplier
     *        The generic function to be invoked. Must take an array of
     *        {@link IGetterDirectTrait} and return an instance of the passed
     *        class.
     * @return Never <code>null</code>.
     * @param <T>
     *        The type to be mocked
     */
    @Nonnull
    public static <T> MockSupplier create (@Nonnull final Class <T> aDstClass,
                                           @Nonnull final Param [] aParams,
                                           @Nonnull final Function <IGetterDirectTrait [], T> aSupplier)
    {
      ValueEnforcer.notNull (aDstClass, "DstClass");
      ValueEnforcer.notNull (aParams, "Params");
      ValueEnforcer.notNull (aSupplier, "Supplier");
      return new MockSupplier (aDstClass, aParams, aSupplier);
    }
  }

  private static final Map <Class <?>, MockSupplier> s_aStaticSupplier = new WeakHashMap <> ();
  private final Map <Class <?>, MockSupplier> m_aPerInstanceSupplier = new WeakHashMap <> ();

  static
  {
    // Create default mappings for primitive types
    s_aStaticSupplier.put (boolean.class, MockSupplier.createConstant (CGlobal.DEFAULT_BOOLEAN_OBJ));
    s_aStaticSupplier.put (Boolean.class, MockSupplier.createConstant (CGlobal.DEFAULT_BOOLEAN_OBJ));
    s_aStaticSupplier.put (byte.class, MockSupplier.createConstant (CGlobal.DEFAULT_BYTE_OBJ));
    s_aStaticSupplier.put (Byte.class, MockSupplier.createConstant (CGlobal.DEFAULT_BYTE_OBJ));
    s_aStaticSupplier.put (char.class, MockSupplier.createConstant (CGlobal.DEFAULT_CHAR_OBJ));
    s_aStaticSupplier.put (Character.class, MockSupplier.createConstant (CGlobal.DEFAULT_CHAR_OBJ));
    s_aStaticSupplier.put (double.class, MockSupplier.createConstant (CGlobal.DEFAULT_DOUBLE_OBJ));
    s_aStaticSupplier.put (Double.class, MockSupplier.createConstant (CGlobal.DEFAULT_DOUBLE_OBJ));
    s_aStaticSupplier.put (float.class, MockSupplier.createConstant (CGlobal.DEFAULT_FLOAT_OBJ));
    s_aStaticSupplier.put (Float.class, MockSupplier.createConstant (CGlobal.DEFAULT_FLOAT_OBJ));
    s_aStaticSupplier.put (int.class, MockSupplier.createConstant (CGlobal.DEFAULT_INT_OBJ));
    s_aStaticSupplier.put (Integer.class, MockSupplier.createConstant (CGlobal.DEFAULT_INT_OBJ));
    s_aStaticSupplier.put (long.class, MockSupplier.createConstant (CGlobal.DEFAULT_LONG_OBJ));
    s_aStaticSupplier.put (Long.class, MockSupplier.createConstant (CGlobal.DEFAULT_LONG_OBJ));
    s_aStaticSupplier.put (short.class, MockSupplier.createConstant (CGlobal.DEFAULT_SHORT_OBJ));
    s_aStaticSupplier.put (Short.class, MockSupplier.createConstant (CGlobal.DEFAULT_SHORT_OBJ));

    // Create some basic simple type mappings
    {
      final Supplier <String> aStringSupplier = new Supplier <String> ()
      {
        private final AtomicInteger m_aCount = new AtomicInteger (0);

        @Nonnull
        @Nonempty
        public String get ()
        {
          return "str" + m_aCount.incrementAndGet ();
        }
      };
      registerStatic (String.class, aStringSupplier);
    }
    registerStatic (LocalDate.class, PDTFactory::getCurrentLocalDate);
    registerStatic (LocalTime.class, PDTFactory::getCurrentLocalTime);
    registerStatic (LocalDateTime.class, PDTFactory::getCurrentLocalDateTime);
    registerStatic (OffsetDateTime.class, PDTFactory::getCurrentOffsetDateTime);
    registerStatic (ZonedDateTime.class, PDTFactory::getCurrentZonedDateTime);
    registerStaticConstant (BigDecimal.ZERO);
    registerStaticConstant (BigInteger.ZERO);
  }

  public CommonsMock ()
  {}

  /**
   * Check if a class can be registered.
   *
   * @param aClass
   *        The class to check
   * @return <code>true</code> for everything except {@link Object}.
   */
  private static final boolean _canRegister (final Class <?> aClass)
  {
    return aClass != Object.class;
  }

  /**
   * Register a constant mock object. That class will always be mocked with the
   * specified instance.
   *
   * @param aObject
   *        The object to be used as a mock result. May not be <code>null</code>
   *        .
   * @param <T>
   *        The type to be mocked
   */
  public static final <T> void registerStaticConstant (@Nonnull final T aObject)
  {
    registerStatic (MockSupplier.createConstant (aObject));
  }

  /**
   * Register a simple supplier (=factory) to be invoked when an instance of the
   * passed class is to be mocked. This method does not give you any possibility
   * to provide parameters and so this works only if mock instance creation is
   * fixed.
   *
   * @param aClass
   *        The class to be mocked. May not be <code>null</code>.
   * @param aSupplier
   *        The supplier/factory to be invoked when to mock this class. May not
   *        be <code>null</code>.
   * @param <T>
   *        The type to be mocked
   */
  public static final <T> void registerStatic (@Nonnull final Class <T> aClass, @Nonnull final Supplier <T> aSupplier)
  {
    registerStatic (MockSupplier.createNoParams (aClass, aSupplier));
  }

  /**
   * Create a mock supplier with parameters.
   *
   * @param aDstClass
   *        The destination class to be mocked. May not be <code>null</code>.
   * @param aParams
   *        The parameter declarations to be used. May not be <code>null</code>.
   * @param aSupplier
   *        The generic function to be invoked. Must take an array of
   *        {@link IGetterDirectTrait} and return an instance of the passed
   *        class.
   * @param <T>
   *        The type to be mocked
   */
  public static final <T> void registerStatic (@Nonnull final Class <T> aDstClass,
                                               @Nonnull final Param [] aParams,
                                               @Nonnull final Function <IGetterDirectTrait [], T> aSupplier)
  {
    registerStatic (MockSupplier.create (aDstClass, aParams, aSupplier));
  }

  /**
   * Register a mock supplier into the provided map.
   *
   * @param aSupplier
   *        Supplier to be registered. May not be <code>null</code>.
   * @param aTargetMap
   *        Map to register it to. May not be <code>null</code>.
   */
  private static void _register (@Nonnull final MockSupplier aSupplier,
                                 @Nonnull final Map <Class <?>, MockSupplier> aTargetMap)
  {
    ValueEnforcer.notNull (aSupplier, "Supplier");
    ValueEnforcer.notNull (aTargetMap, "TargetMap");

    final Class <?> aClass = aSupplier.m_aDstClass;
    if (aTargetMap.containsKey (aClass))
      throw new IllegalArgumentException ("A static for class " + aClass.getName () + " is already contained!");

    if (false)
    {
      // Register only the class
      aTargetMap.put (aClass, aSupplier);
    }
    else
    {
      // Register the whole class hierarchy list
      for (final Class <?> aRealClass : ClassHierarchyCache.getClassHierarchy (aClass))
        if (_canRegister (aRealClass) && !aTargetMap.containsKey (aRealClass))
          aTargetMap.put (aRealClass, aSupplier);
    }
  }

  /**
   * Register an arbitrary MockSupplier that is available across tests!
   *
   * @param aSupplier
   *        The supplier to be registered. May not be <code>null</code>.
   */
  public static final void registerStatic (@Nonnull final MockSupplier aSupplier)
  {
    // Register globally
    _register (aSupplier, s_aStaticSupplier);
  }

  /**
   * Register a constant mock object. That class will always be mocked with the
   * specified instance.
   *
   * @param aObject
   *        The object to be used as a mock result. May not be <code>null</code>
   *        .
   * @param <T>
   *        The type to be mocked
   */
  public final <T> void registerPerInstanceConstant (@Nonnull final T aObject)
  {
    registerPerInstance (MockSupplier.createConstant (aObject));
  }

  /**
   * Register a simple supplier (=factory) to be invoked when an instance of the
   * passed class is to be mocked. This method does not give you any possibility
   * to provide parameters and so this works only if mock instance creation is
   * fixed.
   *
   * @param aClass
   *        The class to be mocked. May not be <code>null</code>.
   * @param aSupplier
   *        The supplier/factory to be invoked when to mock this class. May not
   *        be <code>null</code>.
   * @param <T>
   *        The type to be mocked
   */
  public final <T> void registerPerInstance (@Nonnull final Class <T> aClass, @Nonnull final Supplier <T> aSupplier)
  {
    registerPerInstance (MockSupplier.createNoParams (aClass, aSupplier));
  }

  /**
   * Create a mock supplier with parameters.
   *
   * @param aDstClass
   *        The destination class to be mocked. May not be <code>null</code>.
   * @param aParams
   *        The parameter declarations to be used. May not be <code>null</code>.
   * @param aSupplier
   *        The generic function to be invoked. Must take an array of
   *        {@link IGetterDirectTrait} and return an instance of the passed
   *        class.
   * @param <T>
   *        The type to be mocked
   */
  public final <T> void registerPerInstance (@Nonnull final Class <T> aDstClass,
                                             @Nonnull final Param [] aParams,
                                             @Nonnull final Function <IGetterDirectTrait [], T> aSupplier)
  {
    registerPerInstance (MockSupplier.create (aDstClass, aParams, aSupplier));
  }

  /**
   * Register an arbitrary MockSupplier.
   *
   * @param aSupplier
   *        The supplier to be registered. May not be <code>null</code>.
   */
  public final void registerPerInstance (@Nonnull final MockSupplier aSupplier)
  {
    // Register per-instance
    _register (aSupplier, m_aPerInstanceSupplier);
  }

  @Nonnull
  private Object _mock (@Nonnull final Class <?> aClass,
                        @Nullable final Object [] aParams,
                        final int nLevel) throws Exception
  {
    // Check for static supplier
    final MockSupplier aStatic = s_aStaticSupplier.get (aClass);
    if (aStatic != null)
      return aStatic.getMockedValue (aParams);

    // Check for per-instance supplier
    final MockSupplier aInstance = m_aPerInstanceSupplier.get (aClass);
    if (aInstance != null)
      return aInstance.getMockedValue (aParams);

    // Is it an array?
    if (aClass.isArray ())
    {
      final Class <?> aArrayType = aClass.getComponentType ();

      if (aArrayType == boolean.class)
        return ArrayHelper.newBooleanArray ();
      if (aArrayType == byte.class)
        return ArrayHelper.newByteArray ();
      if (aArrayType == char.class)
        return ArrayHelper.newCharArray ();
      if (aArrayType == double.class)
        return ArrayHelper.newDoubleArray ();
      if (aArrayType == float.class)
        return ArrayHelper.newFloatArray ();
      if (aArrayType == int.class)
        return ArrayHelper.newIntArray ();
      if (aArrayType == long.class)
        return ArrayHelper.newLongArray ();
      if (aArrayType == short.class)
        return ArrayHelper.newShortArray ();

      final Object [] ret = ArrayHelper.newArray (aArrayType, 1);
      ret[0] = _mock (aArrayType, null, nLevel + 1);
      return ret;
    }

    // As enums have no constructors use the first enum constant
    if (aClass.isEnum ())
    {
      return aClass.getEnumConstants ()[0];
    }

    // Find constructor
    for (final Constructor <?> c : aClass.getConstructors ())
    {
      try
      {
        // c.setAccessible (true);
        final Object [] aCtorParams = new Object [c.getParameterCount ()];
        int nParam = 0;
        for (final Class <?> aParamClass : c.getParameterTypes ())
        {
          // Avoid infinite recursion
          if (aParamClass == aClass)
            aCtorParams[nParam++] = null;
          else
            aCtorParams[nParam++] = _mock (aParamClass, null, nLevel + 1);
        }
        return c.newInstance (aCtorParams);
      }
      catch (final Exception ex)
      {
        // continue to exception below
      }
    }

    // Ooops
    throw new IllegalStateException ("Class " + aClass.getName () + " has no mockable constructor!");
  }

  /**
   * Create a mock instance of the passed class.
   *
   * @param aClass
   *        The class to be mocked. May not be <code>null</code>.
   * @param aParams
   *        An optional array of parameters to be passed to the mocking
   *        supplier. May be <code>null</code> or empty.
   * @return The mocked object. Never <code>null</code>.
   * @throws IllegalStateException
   *         If an exception occurred during the mock instance creation.
   * @param <T>
   *        The type to be mocked
   */
  @Nonnull
  public <T> T mock (@Nonnull final Class <T> aClass, @Nullable final Object... aParams)
  {
    try
    {
      // Try to dynamically create the respective object
      final T ret = GenericReflection.uncheckedCast (_mock (aClass, aParams, 0));

      // Register for future use :)
      if (!m_aPerInstanceSupplier.containsKey (aClass))
        registerPerInstanceConstant (ret);
      return ret;
    }
    catch (final Exception ex)
    {
      throw new IllegalStateException ("Failed to mock class " + aClass.getName (), ex);
    }
  }

  /**
   * Create a {@link List} of mocked objects.
   *
   * @param nCount
   *        Number of objects to be mocked. Must be &ge; 0.
   * @param aClass
   *        The class to be mocked.
   * @param aParams
   *        An optional array of parameters to be passed to the mocking supplier
   *        for each object to be mocked. May be <code>null</code> or empty.
   * @return The list with <code>nCount</code> entries.
   * @param <T>
   *        The type to be mocked
   */
  @Nonnull
  @ReturnsMutableCopy
  public <T> ICommonsList <T> mockMany (@Nonnegative final int nCount,
                                        @Nonnull final Class <T> aClass,
                                        @Nullable final Object... aParams)
  {
    final ICommonsList <T> ret = new CommonsArrayList <> (nCount);
    for (int i = 0; i < nCount; ++i)
      ret.add (mock (aClass, aParams));
    return ret;
  }

  /**
   * Create a {@link ICommonsSet} of mocked objects.
   *
   * @param nCount
   *        Number of objects to be mocked. Must be &ge; 0.
   * @param aClass
   *        The class to be mocked.
   * @param aParams
   *        An optional array of parameters to be passed to the mocking supplier
   *        for each object to be mocked. May be <code>null</code> or empty.
   * @return The set with <code>nCount</code> entries.
   * @param <T>
   *        The type to be mocked
   */
  @Nonnull
  @ReturnsMutableCopy
  public <T> ICommonsSet <T> mockSet (@Nonnegative final int nCount,
                                      @Nonnull final Class <T> aClass,
                                      @Nullable final Object... aParams)
  {
    final ICommonsSet <T> ret = new CommonsHashSet <> (nCount);
    for (int i = 0; i < nCount; ++i)
      ret.add (mock (aClass, aParams));
    return ret;
  }
}
