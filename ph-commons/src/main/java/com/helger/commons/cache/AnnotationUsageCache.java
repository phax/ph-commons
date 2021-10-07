/*
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.commons.cache;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.state.ETriState;
import com.helger.commons.string.ToStringGenerator;

/**
 * A simple cache for the usage of a certain annotation class at other classes.
 * <br>
 * Note: cannot use {@link com.helger.commons.cache.Cache} because it would need
 * a <code>Class&lt;?&gt;</code> as a key and this would be a hard wired
 * reference.<br>
 * Since 10.1.3 the class is no longer read-write-locked, as the performance
 * overhead is too big for reading only, compared to the penalty of double
 * annotation determination.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class AnnotationUsageCache
{
  private final Class <? extends Annotation> m_aAnnotationClass;
  private final ICommonsMap <String, ETriState> m_aMap = new CommonsHashMap <> ();

  /**
   * Constructor
   *
   * @param aAnnotationClass
   *        The annotation class to store the existence of. It must have the
   *        {@link RetentionPolicy#RUNTIME} to be usable within this class!
   */
  public AnnotationUsageCache (@Nonnull final Class <? extends Annotation> aAnnotationClass)
  {
    ValueEnforcer.notNull (aAnnotationClass, "AnnotationClass");

    // Check retention policy
    final Retention aRetention = aAnnotationClass.getAnnotation (Retention.class);
    final RetentionPolicy eRetentionPolicy = aRetention == null ? RetentionPolicy.CLASS : aRetention.value ();
    if (eRetentionPolicy != RetentionPolicy.RUNTIME)
      throw new IllegalArgumentException ("RetentionPolicy must be of type RUNTIME to be used within this cache. The current value ist " +
                                          eRetentionPolicy);

    // Save to members
    m_aAnnotationClass = aAnnotationClass;
  }

  /**
   * @return The annotation class passed in the constructor. Never
   *         <code>null</code>.
   */
  @Nonnull
  public final Class <? extends Annotation> getAnnotationClass ()
  {
    return m_aAnnotationClass;
  }

  public boolean hasAnnotation (@Nonnull final Object aObject)
  {
    ValueEnforcer.notNull (aObject, "Object");

    return hasAnnotation (aObject.getClass ());
  }

  public boolean hasAnnotation (@Nonnull final Class <?> aClass)
  {
    ValueEnforcer.notNull (aClass, "Class");

    final String sClassName = aClass.getName ();

    ETriState eHas = m_aMap.get (sClassName);
    if (eHas == null)
    {
      eHas = ETriState.valueOf (aClass.getAnnotation (m_aAnnotationClass) != null);
      m_aMap.put (sClassName, eHas);
    }
    return eHas.isTrue ();
  }

  public void setAnnotation (@Nonnull final Class <?> aClass, final boolean bHasAnnotation)
  {
    ValueEnforcer.notNull (aClass, "Class");

    final String sClassName = aClass.getName ();

    m_aMap.put (sClassName, ETriState.valueOf (bHasAnnotation));
  }

  public void clearCache ()
  {
    m_aMap.clear ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("AnnotationClass", m_aAnnotationClass).append ("Map", m_aMap).getToString ();
  }
}
