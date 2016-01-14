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
package com.helger.commons.supplementary.test.benchmark;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.StringHelper;

public final class StringTrieFuncTest <DATATYPE>
{
  private static class Node <DATATYPE>
  {
    // character
    private final char m_cChar;
    // left, middle, and right subtries
    private Node <DATATYPE> m_aLeft, m_aMid, m_aRight;
    // value associated with string
    private DATATYPE m_aValue;

    public Node (final char c)
    {
      m_cChar = c;
    }
  }

  // size
  private int m_nSize;
  // root of TST
  private Node <DATATYPE> m_aRoot;

  @Nonnegative
  public int size ()
  {
    return m_nSize;
  }

  public boolean contains (@Nonnull @Nonempty final String sKey)
  {
    return get (sKey) != null;
  }

  @Nullable
  public DATATYPE get (@Nonnull @Nonempty final String sKey)
  {
    if (StringHelper.hasNoText (sKey))
      throw new IllegalArgumentException ("key must have length >= 1");

    final char [] aKey = sKey.toCharArray ();
    Node <DATATYPE> aCurNode = m_aRoot;
    int nIndex = 0;
    char c = aKey[nIndex];
    nIndex++;
    while (aCurNode != null)
    {
      final char cNodeChar = aCurNode.m_cChar;
      if (c < cNodeChar)
        aCurNode = aCurNode.m_aLeft;
      else
        if (c > cNodeChar)
          aCurNode = aCurNode.m_aRight;
        else
          if (nIndex < aKey.length)
          {
            aCurNode = aCurNode.m_aMid;
            c = aKey[nIndex];
            nIndex++;
          }
          else
            return aCurNode.m_aValue;
    }
    return null;
  }

  // return subtrie corresponding to given key
  @Nullable
  private Node <DATATYPE> _get (@Nullable final Node <DATATYPE> aNode,
                                @Nonnull @Nonempty final char [] aKey,
                                @Nonnegative final int nIndex)
  {
    if (aNode != null)
    {
      final char c = aKey[nIndex];
      if (c < aNode.m_cChar)
        return _get (aNode.m_aLeft, aKey, nIndex);
      if (c > aNode.m_cChar)
        return _get (aNode.m_aRight, aKey, nIndex);
      if (nIndex < aKey.length - 1)
        return _get (aNode.m_aMid, aKey, nIndex + 1);
    }
    return aNode;
  }

  @Nullable
  private Node <DATATYPE> _putRest (final char [] aChars, final int nIndex, final DATATYPE aValue)
  {
    if (nIndex >= aChars.length)
      throw new IllegalArgumentException ();

    Node <DATATYPE> ret = null;
    Node <DATATYPE> aLastNode = null;

    for (int i = nIndex; i < aChars.length; ++i)
    {
      final char c = aChars[i];
      final Node <DATATYPE> aNode = new Node <DATATYPE> (c);
      if (aLastNode != null)
        aLastNode.m_aMid = aNode;
      if (ret == null)
        ret = aNode;

      // Last char -> new entry -> new overall entry
      if (i == aChars.length - 1)
      {
        aNode.m_aValue = aValue;
        m_nSize++;
      }
      aLastNode = aNode;
    }
    return ret;
  }

  public void put (@Nonnull @Nonempty final String sKey, @Nullable final DATATYPE aValue)
  {
    if (StringHelper.hasNoText (sKey))
      throw new IllegalArgumentException ("key must have length >= 1");

    final char [] aChars = sKey.toCharArray ();
    if (m_aRoot == null)
      m_aRoot = _putRest (aChars, 0, aValue);
    else
    {
      Node <DATATYPE> aCurNode = m_aRoot;
      Node <DATATYPE> aNextNode;
      int nIndex = 0;
      while (aCurNode != null)
      {
        final char c = aChars[nIndex];
        final char cNodeChar = aCurNode.m_cChar;
        if (c < cNodeChar)
        {
          aNextNode = aCurNode.m_aLeft;
          if (aNextNode == null)
          {
            aCurNode.m_aLeft = _putRest (aChars, nIndex, aValue);
            break;
          }
          aCurNode = aNextNode;
        }
        else
          if (c > cNodeChar)
          {
            aNextNode = aCurNode.m_aRight;
            if (aNextNode == null)
            {
              aCurNode.m_aRight = _putRest (aChars, nIndex, aValue);
              break;
            }
            aCurNode = aNextNode;
          }
          else
            if (nIndex < aChars.length - 1)
            {
              nIndex++;
              aNextNode = aCurNode.m_aMid;
              if (aNextNode == null)
              {
                aCurNode.m_aMid = _putRest (aChars, nIndex, aValue);
                break;
              }
              aCurNode = aNextNode;
            }
            else
            {
              // End of key - update value
              aCurNode.m_aValue = aValue;
              break;
            }
      }
    }
  }

  @Nullable
  public String getLongestPrefixOf (@Nullable final String s)
  {
    if (StringHelper.hasNoText (s))
      return null;

    int nLength = 0;
    Node <DATATYPE> x = m_aRoot;
    int i = 0;
    while (x != null && i < s.length ())
    {
      final char c = s.charAt (i);
      if (c < x.m_cChar)
        x = x.m_aLeft;
      else
        if (c > x.m_cChar)
          x = x.m_aRight;
        else
        {
          i++;
          if (x.m_aValue != null)
            nLength = i;
          x = x.m_aMid;
        }
    }
    return s.substring (0, nLength);
  }

  @Nonnull
  public Collection <String> getAllKeys ()
  {
    final List <String> aList = new ArrayList <String> ();
    _collect (m_aRoot, "", aList);
    return aList;
  }

  // all keys in subtrie rooted at x with given prefix
  private void _collect (@Nullable final Node <DATATYPE> aNode,
                         @Nonnull final String sPrefix,
                         @Nonnull final List <String> aList)
  {
    if (aNode != null)
    {
      _collect (aNode.m_aLeft, sPrefix, aList);
      final String sPrefixC = sPrefix + aNode.m_cChar;
      if (aNode.m_aValue != null)
        aList.add (sPrefixC);
      _collect (aNode.m_aMid, sPrefixC, aList);
      _collect (aNode.m_aRight, sPrefix, aList);
    }
  }

  // all keys starting with given prefix
  @Nonnull
  public Collection <String> prefixMatch (@Nonnull @Nonempty final String sPrefix)
  {
    if (StringHelper.hasNoText (sPrefix))
      throw new IllegalArgumentException ("prefix must have length >= 1");

    final List <String> aList = new ArrayList <String> ();
    final char [] aPrefix = sPrefix.toCharArray ();
    final Node <DATATYPE> aNode = _get (m_aRoot, aPrefix, 0);
    if (aNode != null)
    {
      if (aNode.m_aValue != null)
        aList.add (sPrefix);
      _collect (aNode.m_aMid, sPrefix, aList);
    }
    return aList;
  }

  // return all keys matching given wilcard pattern
  @Nonnull
  public Collection <String> wildcardMatch (@Nonnull final String sPattern)
  {
    final List <String> queue = new ArrayList <String> ();
    collect (m_aRoot, "", 0, sPattern, queue);
    return queue;
  }

  public void collect (@Nullable final Node <DATATYPE> aNode,
                       @Nonnull final String sPrefix,
                       @Nonnegative final int nIndex,
                       @Nonnull final String sPattern,
                       @Nonnull final List <String> aList)
  {
    if (aNode != null)
    {
      final char c = sPattern.charAt (nIndex);
      if (c == '.' || c < aNode.m_cChar)
        collect (aNode.m_aLeft, sPrefix, nIndex, sPattern, aList);
      if (c == '.' || c == aNode.m_cChar)
      {
        if (nIndex == sPattern.length () - 1 && aNode.m_aValue != null)
          aList.add (sPrefix + aNode.m_cChar);
        if (nIndex < sPattern.length () - 1)
          collect (aNode.m_aMid, sPrefix + aNode.m_cChar, nIndex + 1, sPattern, aList);
      }
      if (c == '.' || c > aNode.m_cChar)
        collect (aNode.m_aRight, sPrefix, nIndex, sPattern, aList);
    }
  }
}
