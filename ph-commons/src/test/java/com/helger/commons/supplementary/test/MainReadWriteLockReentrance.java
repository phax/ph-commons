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
package com.helger.commons.supplementary.test;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MainReadWriteLockReentrance
{
  public static void main (final String [] args)
  {
    final ReadWriteLock aRWLock = new ReentrantReadWriteLock ();

    aRWLock.readLock ().lock ();
    try
    {
      System.out.println ("in readLock");
    }
    finally
    {
      aRWLock.readLock ().unlock ();
    }

    aRWLock.writeLock ().lock ();
    try
    {
      System.out.println ("in writeLock");
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
        System.out.println ("in double readLock");
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
        System.out.println ("in double writeLock");
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
          System.out.println ("in readLock and writeLock");
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
        System.out.println ("in writeLock and readLock");
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

    System.out.println ("-done-");
  }
}
