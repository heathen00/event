package com.ht.wfp3.api.statement;

import com.ht.wfp3.api.statement.VertexReference.Type;

class VertexReferenceGroupBuilderImp implements VertexReferenceGroupBuilder {
  private StatementFactory statementFactory;
  private Integer geometricVertexReferenceNumber;
  private Integer textureVertexReferenceNumber;
  private Integer normalVertexReferenceNumber;
  private Integer parameterVertexReferenceNumber;

  VertexReferenceGroupBuilderImp() {
    super();
    statementFactory = StatementFactory.createStatementFactory();
    clear();
  }

  @Override
  public VertexReferenceGroupBuilder clear() {
    geometricVertexReferenceNumber = null;
    textureVertexReferenceNumber = null;
    normalVertexReferenceNumber = null;
    parameterVertexReferenceNumber = null;
    return this;
  }

  @Override
  public VertexReferenceGroupBuilder geoVertexRef(Integer geometricVertexReferenceNumber) {
    this.geometricVertexReferenceNumber = geometricVertexReferenceNumber;
    return this;
  }

  @Override
  public VertexReferenceGroupBuilder texVertexRef(Integer textureVertexReferenceNumber) {
    this.textureVertexReferenceNumber = textureVertexReferenceNumber;
    return this;
  }

  @Override
  public VertexReferenceGroupBuilder normalVertexRef(Integer normalVertexReferenceNumber) {
    this.normalVertexReferenceNumber = normalVertexReferenceNumber;
    return this;
  }

  @Override
  public VertexReferenceGroupBuilder paramVertexRef(Integer parameterVertexReferenceNumber) {
    this.parameterVertexReferenceNumber = parameterVertexReferenceNumber;
    return this;
  }

  @Override
  public VertexReferenceGroup build() {
    VertexReference geometricVertexReference = (geometricVertexReferenceNumber != null
        ? statementFactory.createVertexReference(Type.GEOMETRIC, geometricVertexReferenceNumber,
            true)
        : statementFactory.createVertexReference(Type.GEOMETRIC,
            VertexReferenceImp.INDEX_NOT_SET_VALUE, false));
    VertexReference textureVertexReference = (textureVertexReferenceNumber != null
        ? statementFactory.createVertexReference(Type.TEXTURE, textureVertexReferenceNumber, true)
        : statementFactory.createVertexReference(Type.TEXTURE,
            VertexReferenceImp.INDEX_NOT_SET_VALUE, false));
    VertexReference normalVertexReference = (normalVertexReferenceNumber != null
        ? statementFactory.createVertexReference(Type.NORMAL, normalVertexReferenceNumber, true)
        : statementFactory.createVertexReference(Type.NORMAL,
            VertexReferenceImp.INDEX_NOT_SET_VALUE, false));
    VertexReference parameterVertexReference = (parameterVertexReferenceNumber != null
        ? statementFactory.createVertexReference(Type.PARAMETER, parameterVertexReferenceNumber,
            true)
        : statementFactory.createVertexReference(Type.PARAMETER,
            VertexReferenceImp.INDEX_NOT_SET_VALUE, false));
    return new VertexReferenceGroupImp(geometricVertexReference, textureVertexReference,
        normalVertexReference, parameterVertexReference);
  }

}
