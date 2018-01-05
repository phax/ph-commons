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
package com.helger.commons.supplementary.test;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MainReadWriteLockReentrance
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (MainReadWriteLockReentrance.class);

  private MainReadWriteLockReentrance ()
  {}

  public static void main (final String [] args)
  {
    final ReadWriteLock aRWLock = new ReentrantReadWriteLock ();

    aRWLock.readLock ().lock ();
    try
    {
      s_aLogger.info ("in readLock");
    }
    finally
    {
      aRWLock.readLock ().unlock ();
    }

    aRWLock.writeLock ().lock ();
    try
    {
      s_aLogger.info ("in writeLock");
    }
    finally
    {
      aRWLock.writeLock ().unlock ();
    }

    aRWLock.readLock ().lock ();
    try
    {
      aRWLock.readLock ().lock ();
      try
      {
        s_aLogger.info ("in double readLock");
      }
      finally
      {
        aRWLock.readLock ().unlock ();
      }
    }
    finally
    {
      aRWLock.readLock ().unlock ();
    }

    aRWLock.writeLock ().lock ();
    try
    {
      aRWLock.writeLock ().lock ();
      try
      {
        s_aLogger.info ("in double writeLock");
      }
      finally
      {
        aRWLock.writeLock ().unlock ();
      }
    }
    finally
    {
      aRWLock.writeLock ().unlock ();
    }

    if (false)
    {
      // This is the only case that does not work
      aRWLock.readLock ().lock ();
      try
      {
        aRWLock.writeLock ().lock ();
        try
        {
          s_aLogger.info ("in readLock and writeLock");
        }
        finally
        {
          aRWLock.writeLock ().unlock ();
        }
      }
      finally
      {
        aRWLock.readLock ().unlock ();
      }
    }

    aRWLock.writeLock ().lock ();
    try
    {
      aRWLock.readLock ().lock ();
      try
      {
        s_aLogger.info ("in writeLock and readLock");
      }
      finally
      {
        aRWLock.readLock ().unlock ();
      }
    }
    finally
    {
      aRWLock.writeLock ().unlock ();
    }

    s_aLogger.info ("-done-");
  }
}
