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
package com.helger.graph.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.string.StringHelper;
import com.helger.graph.IBaseGraph;
import com.helger.graph.IBaseGraphNode;
import com.helger.graph.IBaseGraphRelation;
import com.helger.graph.IDirectedGraph;
import com.helger.graph.IDirectedGraphNode;
import com.helger.graph.IDirectedGraphRelation;
import com.helger.xml.serialize.write.EXMLCharMode;
import com.helger.xml.serialize.write.EXMLIncorrectCharacterHandling;
import com.helger.xml.serialize.write.EXMLSerializeVersion;
import com.helger.xml.serialize.write.XMLMaskHelper;

/**
 * Utility class to export a graph to something else
 *
 * @author Philip Helger
 */
@Immutable
public final class GraphVizHelper
{
  private GraphVizHelper ()
  {}

  @Nonnull
  @Nonempty
  public static String getAttribute (@Nonnull @Nonempty final String sName, @Nonnull final String sValue)
  {
    return new StringBuilder (sName).append ("=<")
                                    .append (XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_10,
                                                                             EXMLCharMode.ELEMENT_NAME,
                                                                             EXMLIncorrectCharacterHandling.DEFAULT,
                                                                             sValue))
                                    .append ('>')
                                    .toString ();
  }

  /**
   * Get the graph in a simple DOT notation suitable for GraphViz
   * (http://www.graphviz.org). The DOT specs can be found at
   * http://www.graphviz.org/content/dot-language<br>
   * The default file encoding for GraphViz 2.28 is UTF-8!
   *
   * @param aGraph
   *        The graph to be converted. May not be <code>null</code>.
   * @param sNodeLabelAttr
   *        The name of the attribute to be used for node labels. May be
   *        <code>null</code> to use the node ID as the label.
   * @param sRelationLabelAttr
   *        The name of the attribute to be used for relation labels. May be
   *        <code>null</code> to use no relation label.
   * @return The string representation to be used as input for DOT.
   * @param <N>
   *        Graph node type
   * @param <R>
   *        Graph relation type
   */
  @Nonnull
  public static <N extends IBaseGraphNode <N, R>, R extends IBaseGraphRelation <N, R>> String getAsGraphVizDot (@Nonnull final IBaseGraph <N, R> aGraph,
                                                                                                                @Nullable final String sNodeLabelAttr,
                                                                                                                @Nullable final String sRelationLabelAttr)
  {
    ValueEnforcer.notNull (aGraph, "Graph");

    final StringBuilder aSB = new StringBuilder ();
    // It's a directed graph
    aSB.append ("graph ").append (aGraph.getID ()).append ("{\n");
    aSB.append ("node[shape=box];");
    aGraph.forEachNode (aGraphNode -> {
      aSB.append (aGraphNode.getID ());
      if (StringHelper.hasText (sNodeLabelAttr))
      {
        final String sLabel = aGraphNode.attrs ().getAsString (sNodeLabelAttr);
        aSB.append ('[').append (getAttribute ("label", sLabel)).append (']');
      }
      aSB.append (';');
    });
    aSB.append ('\n');
    aGraph.forEachRelation (aGraphRelation -> {
      final Iterator <N> it = aGraphRelation.getAllConnectedNodes ().iterator ();
      aSB.append (it.next ().getID ()).append ("--").append (it.next ().getID ());
      if (StringHelper.hasText (sRelationLabelAttr))
      {
        final String sLabel = aGraphRelation.attrs ().getAsString (sRelationLabelAttr);
        aSB.append ('[').append (getAttribute ("label", sLabel)).append (']');
      }
      aSB.append (";\n");
    });
    aSB.append ("overlap=false;\n");
    aSB.append ('}');
    return aSB.toString ();
  }

  /**
   * Get the graph in a simple DOT notation suitable for GraphViz
   * (http://www.graphviz.org). The DOT specs can be found at
   * http://www.graphviz.org/content/dot-language<br>
   * The default file encoding for GraphViz 2.28 is UTF-8!
   *
   * @param aGraph
   *        The graph to be converted. May not be <code>null</code>.
   * @param sNodeLabelAttr
   *        The name of the attribute to be used for node labels. May be
   *        <code>null</code> to use the node ID as the label.
   * @param sRelationLabelAttr
   *        The name of the attribute to be used for relation labels. May be
   *        <code>null</code> to use no relation label.
   * @return The string representation to be used as input for DOT.
   * @param <N>
   *        Graph node type
   * @param <R>
   *        Graph relation type
   */
  @Nonnull
  public static <N extends IDirectedGraphNode <N, R>, R extends IDirectedGraphRelation <N, R>> String getAsGraphVizDot (@Nonnull final IDirectedGraph <N, R> aGraph,
                                                                                                                        @Nullable final String sNodeLabelAttr,
                                                                                                                        @Nullable final String sRelationLabelAttr)
  {
    ValueEnforcer.notNull (aGraph, "Graph");

    final StringBuilder aSB = new StringBuilder ();
    // It's a directed graph
    aSB.append ("digraph ").append (aGraph.getID ()).append ("{\n");
    aSB.append ("node[shape=box];");
    aGraph.forEachNode (aGraphNode -> {
      aSB.append (aGraphNode.getID ());
      if (StringHelper.hasText (sNodeLabelAttr))
      {
        final String sLabel = aGraphNode.attrs ().getAsString (sNodeLabelAttr);
        aSB.append ("[label=<")
           .append (XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_10,
                                                    EXMLCharMode.ELEMENT_NAME,
                                                    EXMLIncorrectCharacterHandling.DEFAULT,
                                                    sLabel))
           .append (">]");
      }
      aSB.append (';');
    });
    aSB.append ('\n');
    aGraph.forEachRelation (aGraphRelation -> {
      aSB.append (aGraphRelation.getFromID ()).append ("->").append (aGraphRelation.getToID ());
      if (StringHelper.hasText (sRelationLabelAttr))
      {
        final String sLabel = aGraphRelation.attrs ().getAsString (sRelationLabelAttr);
        aSB.append ("[label=<")
           .append (XMLMaskHelper.getMaskedXMLText (EXMLSerializeVersion.XML_10,
                                                    EXMLCharMode.ELEMENT_NAME,
                                                    EXMLIncorrectCharacterHandling.DEFAULT,
                                                    sLabel))
           .append (">]");
      }
      aSB.append (";\n");
    });
    aSB.append ("overlap=false;\n");
    aSB.append ('}');
    return aSB.toString ();
  }

  /**
   * Invoked the external process "neato" from the GraphViz package. Attention:
   * this spans a sub-process!
   *
   * @param sFileType
   *        The file type to be generated. E.g. "png" - see neato help for
   *        details. May neither be <code>null</code> nor empty.
   * @param sDOT
   *        The DOT file to be converted to an image. May neither be
   *        <code>null</code> nor empty.
   * @return The byte buffer that keeps the converted image. Never
   *         <code>null</code>.
   * @throws IOException
   *         In case some IO error occurs
   * @throws InterruptedException
   *         If the sub-process did not terminate correctly!
   */
  @Nonnull
  public static NonBlockingByteArrayOutputStream getGraphAsImageWithGraphVizNeato (@Nonnull @Nonempty final String sFileType,
                                                                                   @Nonnull final String sDOT) throws IOException,
                                                                                                               InterruptedException
  {
    ValueEnforcer.notEmpty (sFileType, "FileType");
    ValueEnforcer.notEmpty (sDOT, "DOT");

    final ProcessBuilder aPB = new ProcessBuilder ("neato", "-T" + sFileType).redirectErrorStream (false);
    final Process p = aPB.start ();
    // Set neato stdin
    p.getOutputStream ().write (sDOT.getBytes (StandardCharsets.UTF_8));
    p.getOutputStream ().close ();
    // Read neato stdout
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    StreamHelper.copyInputStreamToOutputStream (p.getInputStream (), aBAOS);
    p.waitFor ();
    return aBAOS;
  }
}
