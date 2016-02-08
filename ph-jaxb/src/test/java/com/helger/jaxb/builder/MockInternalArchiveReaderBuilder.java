package com.helger.jaxb.builder;

public final class MockInternalArchiveReaderBuilder extends
                                                    JAXBReaderBuilder <com.helger.jaxb.mock.internal.MockJAXBArchive, MockInternalArchiveReaderBuilder>
{
  public MockInternalArchiveReaderBuilder ()
  {
    super (new InternalArchiveDocumentType ());
  }
}
