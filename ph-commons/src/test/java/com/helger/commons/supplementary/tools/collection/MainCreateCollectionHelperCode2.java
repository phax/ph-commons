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
package com.helger.commons.supplementary.tools.collection;

import com.helger.commons.collection.PrimitiveCollectionHelper;

/**
 * Create the code for the {@link PrimitiveCollectionHelper} class.
 *
 * @author Philip Helger
 */
public class MainCreateCollectionHelperCode2
{
  public static void main (final String [] args)
  {
    final StringBuilder aSB = new StringBuilder ();
    for (final ECollectionType eCollectionType : ECollectionType.values ())
      if (!eCollectionType.isMap ())
      {
        // Collection<Primitive> newPrimitiveCollection (Primitive...)
        String sType;
        if (eCollectionType == ECollectionType.VECTOR)
          sType = "VectorHelper";
        else
          if (eCollectionType == ECollectionType.STACK)
            sType = "StackHelper";
          else
            if (eCollectionType == ECollectionType.QUEUE)
              sType = "QueueHelper";
            else
              sType = "CollectionHelper";
        final String sNew = sType + ".new" + eCollectionType.m_sSuffix;
        final String sMapped = sNew + "Mapped";
        aSB.append (sNew + "();\n");
        aSB.append (sNew + "(\"a\");\n");
        aSB.append (sNew + "(new String[]{\"a\"});\n");
        aSB.append (sNew + "(new CommonsArrayList<> (\"a\"));\n");
        aSB.append (sNew + "(new IterableIterator<String> (new CommonsArrayList<> (\"a\")));\n");
        aSB.append (sNew + "((Iterable<String>) new CommonsArrayList<> (\"a\"));\n");
        aSB.append (sNew + "(new CommonsArrayList<> (\"a\").iterator ());\n");
        aSB.append (sNew + "(new CommonsArrayList<> (\"a\"), Objects::nonNull);\n");
        aSB.append (sMapped + "(new CommonsArrayList<Object> (\"a\"), Object::toString);\n");
        aSB.append (sMapped + "(new Object[]{\"a\"}, Object::toString);\n");
      }

    System.out.print (aSB.toString ());
  }
}
