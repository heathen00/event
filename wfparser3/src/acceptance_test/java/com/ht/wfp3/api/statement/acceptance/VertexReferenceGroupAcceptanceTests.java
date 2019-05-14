package com.ht.wfp3.api.statement.acceptance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import com.ht.wfp3.api.statement.GeoVertexReference;
import com.ht.wfp3.api.statement.NormalVertexReference;
import com.ht.wfp3.api.statement.StatementFactory;
import com.ht.wfp3.api.statement.TexVertexReference;
import com.ht.wfp3.api.statement.VertexReferenceGroup;
import com.ht.wfp3.api.statement.VertexReferenceGroupBuilder;

public class VertexReferenceGroupAcceptanceTests {

  private StatementFactory statementFactory;
  private VertexReferenceGroupBuilder vertexReferenceGroupBuilder;

  private void assertExpectedIsSetValues(boolean expectedIsGeoRefSet, boolean expectedIsTexRefSet,
      boolean expectedIsNormalRefSet, VertexReferenceGroup vertexReferenceGroup) {
    assertEquals(expectedIsGeoRefSet, vertexReferenceGroup.getGeoVertexRef().isSet());
    assertEquals(expectedIsTexRefSet, vertexReferenceGroup.getTexVertexRef().isSet());
    assertEquals(expectedIsNormalRefSet, vertexReferenceGroup.getNormalVertexRef().isSet());
  }

  private void assertValidVertexReferenceGroup(GeoVertexReference geoVertexReference,
      VertexReferenceGroup vertexReferenceGroup) {
    assertNotNull(vertexReferenceGroup);
    assertEquals(geoVertexReference.getVertexIndex(),
        vertexReferenceGroup.getGeoVertexRef().getVertexIndex());
    assertExpectedIsSetValues(true, false, false, vertexReferenceGroup);
  }

  private void assertValidVertexReferenceGroup(GeoVertexReference geoVertexReference,
      TexVertexReference texVertexReference, VertexReferenceGroup vertexReferenceGroup) {
    assertNotNull(vertexReferenceGroup);
    assertEquals(geoVertexReference.getVertexIndex(),
        vertexReferenceGroup.getGeoVertexRef().getVertexIndex());
    assertEquals(texVertexReference.getVertexIndex(),
        vertexReferenceGroup.getTexVertexRef().getVertexIndex());
    assertExpectedIsSetValues(true, true, false, vertexReferenceGroup);

  }


  private void assertValidVertexReferenceGroup(GeoVertexReference geoVertexReference,
      TexVertexReference texVertexReference, NormalVertexReference normalVertexReference,
      VertexReferenceGroup vertexReferenceGroup) {
    assertNotNull(vertexReferenceGroup);
    assertEquals(geoVertexReference.getVertexIndex(),
        vertexReferenceGroup.getGeoVertexRef().getVertexIndex());
    assertEquals(texVertexReference.getVertexIndex(),
        vertexReferenceGroup.getTexVertexRef().getVertexIndex());
    assertEquals(normalVertexReference.getVertexIndex(),
        vertexReferenceGroup.getNormalVertexRef().getVertexIndex());
    assertExpectedIsSetValues(true, true, true, vertexReferenceGroup);
  }


  private void assertValidVertexReferenceGroup(GeoVertexReference geoVertexReference,
      NormalVertexReference normalVertexReference, VertexReferenceGroup vertexReferenceGroup) {
    assertNotNull(vertexReferenceGroup);
    assertEquals(geoVertexReference.getVertexIndex(),
        vertexReferenceGroup.getGeoVertexRef().getVertexIndex());
    assertEquals(normalVertexReference.getVertexIndex(),
        vertexReferenceGroup.getNormalVertexRef().getVertexIndex());
    assertExpectedIsSetValues(true, false, true, vertexReferenceGroup);
  }

  @Before
  public void setup() {
    statementFactory = StatementFactory.createStatementFactory();
    vertexReferenceGroupBuilder = statementFactory.createVertexReferenceGroupBuilder();
  }

  @Test
  public void VertexReferenceGroup_createVertexReferenceGroupBuilder_vertexReferenceGroupBuilderIsCreated() {
    assertNotNull(vertexReferenceGroupBuilder);
  }

  @Test(expected = NullPointerException.class)
  public void VertexReferenceGroup_geoVertexIsNull_nullPointerExceptionIsThrown() {
    vertexReferenceGroupBuilder.geoVertexRef(null);
  }

  @Test(expected = NullPointerException.class)
  public void VertexReferenceGroup_texVertexIsNull_nullPointerExceptionIsThrown() {
    vertexReferenceGroupBuilder.texVertexRef(null);
  }

  @Test(expected = NullPointerException.class)
  public void VertexReferenceGroup_normalVertexIsNull_nullPointerExceptionIsThrown() {
    vertexReferenceGroupBuilder.normalVertexRef(null);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void VertexReferenceGroup_clearVertexReferenceGroupAndBuild_unsupportedOperationExceptionIsThrown() {
    vertexReferenceGroupBuilder.clear().build();
  }

  @Test
  public void VertexReferenceGroup_setGeoVertexAndBuild_vertexReferenceGroupIsBuilt() {
    Integer geoVertexIndex = Integer.valueOf(1);
    GeoVertexReference geoVertexReference =
        statementFactory.createGeoVertexReference(geoVertexIndex);

    VertexReferenceGroup vertexReferenceGroup =
        vertexReferenceGroupBuilder.clear().geoVertexRef(geoVertexIndex).build();

    assertValidVertexReferenceGroup(geoVertexReference, vertexReferenceGroup);
  }

  @Test
  public void VertexReferenceGroup_setGeoVertexAndTexVertexAndBuild_vertexReferenceGroupIsBuilt() {
    Integer geoVertexIndex = Integer.valueOf(1);
    Integer texVertexIndex = Integer.valueOf(-99);
    GeoVertexReference geoVertexReference =
        statementFactory.createGeoVertexReference(geoVertexIndex);
    TexVertexReference texVertexReference =
        statementFactory.createTexVertexReference(texVertexIndex);

    VertexReferenceGroup vertexReferenceGroup = vertexReferenceGroupBuilder.clear()
        .geoVertexRef(geoVertexIndex).texVertexRef(texVertexIndex).build();

    assertValidVertexReferenceGroup(geoVertexReference, texVertexReference, vertexReferenceGroup);
  }

  @Test
  public void VertexReferenceGroup_setGeoVertexTexVertexAndNormalVertexAndBuild_vertexReferenceGroupIsBuilt() {
    Integer geoVertexIndex = Integer.valueOf(1);
    Integer texVertexIndex = Integer.valueOf(-99);
    Integer normalVertexIndex = Integer.valueOf(56);
    GeoVertexReference geoVertexReference =
        statementFactory.createGeoVertexReference(geoVertexIndex);
    TexVertexReference texVertexReference =
        statementFactory.createTexVertexReference(texVertexIndex);
    NormalVertexReference normalVertexReference =
        statementFactory.createNormalVertexReference(normalVertexIndex);

    VertexReferenceGroup vertexReferenceGroup =
        vertexReferenceGroupBuilder.clear().geoVertexRef(geoVertexIndex)
            .texVertexRef(texVertexIndex).normalVertexRef(normalVertexIndex).build();

    assertValidVertexReferenceGroup(geoVertexReference, texVertexReference, normalVertexReference,
        vertexReferenceGroup);
  }

  @Test
  public void VertexReferenceGroup_setGeoVertexAndNormalVertexAndBuild_vertexReferenceGroupIsBuilt() {
    Integer geoVertexIndex = Integer.valueOf(1);
    Integer normalVertexIndex = Integer.valueOf(56);
    GeoVertexReference geoVertexReference =
        statementFactory.createGeoVertexReference(geoVertexIndex);
    NormalVertexReference normalVertexReference =
        statementFactory.createNormalVertexReference(normalVertexIndex);

    VertexReferenceGroup vertexReferenceGroup = vertexReferenceGroupBuilder.clear()
        .geoVertexRef(geoVertexIndex).normalVertexRef(normalVertexIndex).build();

    assertValidVertexReferenceGroup(geoVertexReference, normalVertexReference,
        vertexReferenceGroup);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void VertexReferenceGroup_setTexVertexAndBuild_unsupportedOperationExceptionIsThrown() {
    vertexReferenceGroupBuilder.clear().texVertexRef(Integer.valueOf(33)).build();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void VertexReferenceGroup_setNormalVertexAndBuild_unsupportedOperationExceptionIsThrown() {
    vertexReferenceGroupBuilder.clear().normalVertexRef(Integer.valueOf(-101)).build();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void VertexReferenceGroup_setTexVertexAndNormalVertexAndBuild_unsupportedOperationExceptionIsThrown() {
    vertexReferenceGroupBuilder.clear().texVertexRef(Integer.valueOf(22)).normalVertexRef(Integer.valueOf(33)).build();
  }

  // TODO equals, hashCode, compareTo
  // TODO copy malicious mutable statement.

  @Test
  public void test() {
    fail("Not yet implemented");
  }

}