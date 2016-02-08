package com.helger.jaxb.builder;

public class ExternalArchiveDocumentType extends JAXBDocumentType
{
  public ExternalArchiveDocumentType ()
  {
    super (com.helger.jaxb.mock.external.MockJAXBArchive.class, null, null);
  }
}
