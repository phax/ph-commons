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
package com.helger.commons.microdom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IMicroEvent} interface.
 *
 * @author Philip Helger
 */
@Immutable
public final class MicroEvent implements IMicroEvent
{
  private final EMicroEvent m_eEventType;
  private final IMicroNode m_aSourceNode;
  private final IMicroNode m_aTargetNode;

  public MicroEvent (@Nonnull final EMicroEvent eEventType,
                     @Nullable final IMicroNode aSourceNode,
                     @Nullable final IMicroNode aTargetNode)
  {
    m_eEventType = ValueEnforcer.notNull (eEventType, "EventType");
    m_aSourceNode = aSourceNode;
    m_aTargetNode = aTargetNode;
  }

  @Nonnull
  public EMicroEvent getEventType ()
  {
    return m_eEventType;
  }

  @Nullable
  public IMicroNode getSourceNode ()
  {
    return m_aSourceNode;
  }

  @Nullable
  public IMicroNode getTargetNode ()
  {
    return m_aTargetNode;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("eventType", m_eEventType)
                                       .appendIfNotNull ("sourceNode", m_aSourceNode)
                                       .appendIfNotNull ("targetNode", m_aTargetNode)
                                       .toString ();
  }
}
