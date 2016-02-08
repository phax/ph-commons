package com.helger.jaxb.builder;

public final class MockExternalArchiveReaderBuilder extends
                                                    AbstractJAXBReaderBuilder <com.helger.jaxb.mock.external.MockJAXBArchive, MockExternalArchiveReaderBuilder>
{
  public MockExternalArchiveReaderBuilder ()
  {
    super (new ExternalArchiveDocumentType ());
  }
}
