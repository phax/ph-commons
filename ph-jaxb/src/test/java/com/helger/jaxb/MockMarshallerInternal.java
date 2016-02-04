package com.helger.jaxb;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import com.helger.commons.xml.namespace.MapBasedNamespaceContext;

final class MockMarshallerInternal extends AbstractJAXBMarshaller <com.helger.jaxb.mock.internal.MockJAXBArchive>
{
  public MockMarshallerInternal ()
  {
    // No XSD available
    super (com.helger.jaxb.mock.internal.MockJAXBArchive.class,
           null,
           o -> new JAXBElement <com.helger.jaxb.mock.internal.MockJAXBArchive> (new QName ("urn:test:internal", "any"),
                                                                                 com.helger.jaxb.mock.internal.MockJAXBArchive.class,
                                                                                 o));
  }

  @Override
  protected void customizeMarshaller (@Nonnull final Marshaller aMarshaller)
  {
    JAXBMarshallerHelper.setFormattedOutput (aMarshaller, true);
    JAXBMarshallerHelper.setSunNamespacePrefixMapper (aMarshaller,
                                                      new MapBasedNamespaceContext ().addMapping ("def",
                                                                                                  "urn:test:internal"));
  }
}
