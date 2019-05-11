package com.ht.wfp3.api.statement.acceptance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import com.ht.wfp3.api.statement.MergingGroup;
import com.ht.wfp3.api.statement.StatementFactory;

public class MergingGroupAcceptanceTests {

  private static final Object MERGING_GROUP_KEYWORD = "mg";

  private StatementFactory statementFactory;

  private void assertValidMergingGroup(Integer expectedMergingGroupNumber,
      BigDecimal expectedMergingResolution, boolean expectedIsEnabled, MergingGroup mergingGroup) {
    assertNotNull(mergingGroup);
    assertEquals(MERGING_GROUP_KEYWORD, mergingGroup.getKeyword());
    assertEquals(expectedMergingGroupNumber, mergingGroup.getMergingGroupNumber());
    assertEquals(expectedMergingResolution, mergingGroup.getMergingResolution());
    assertEquals(expectedIsEnabled, mergingGroup.isEnabled());
  }

  @Before
  public void setup() {
    statementFactory = StatementFactory.createStatementFactory();
  }

  @Test(expected = NullPointerException.class)
  public void MergingGroup_createMergingGroupWithNullGroupNumber_nullPointerExceptionIsThrown() {
    statementFactory.createMergingGroup(null, BigDecimal.valueOf(1.1d));
  }

  @Test(expected = NullPointerException.class)
  public void MergingGroup_createMergingGroupWithNullResolution_nullPointerExceptionIsThrown() {
    statementFactory.createMergingGroup(Integer.valueOf(23), null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void MergingGroup_createMergingGroupWithGroupNumberLessThanZero_illegalArgumentExceptionIsThrown() {
    statementFactory.createMergingGroup(Integer.valueOf(-1), BigDecimal.valueOf(3.3d));
  }

  @Test
  public void MergingGroup_createMergingGroupWithGroupNumberEqualToZero_mergingGroupIsCreated() {
    Integer mergingGroupNumber = Integer.valueOf(0);
    BigDecimal mergingGroupResolution = BigDecimal.valueOf(1.1d);
    boolean expectedIsEnabled = false;

    MergingGroup mergingGroup =
        statementFactory.createMergingGroup(mergingGroupNumber, mergingGroupResolution);

    assertValidMergingGroup(mergingGroupNumber, mergingGroupResolution, expectedIsEnabled,
        mergingGroup);
  }

  @Test
  public void MergingGroup_createMergingGroupWithGroupNumberGreaterThanZero_mergingGroupIsCreated() {
    Integer mergingGroupNumber = Integer.valueOf(1);
    BigDecimal mergingGroupResolution = BigDecimal.valueOf(1.1d);
    boolean expectedIsEnabled = true;

    MergingGroup mergingGroup =
        statementFactory.createMergingGroup(mergingGroupNumber, mergingGroupResolution);

    assertValidMergingGroup(mergingGroupNumber, mergingGroupResolution, expectedIsEnabled,
        mergingGroup);
  }

  @Test(expected = IllegalArgumentException.class)
  public void MergingGroup_createMergingGroupWithResolutionLessThanOrEqualToZero_illegalArgumentExceptionIsThrown() {
    statementFactory.createMergingGroup(Integer.valueOf(53), BigDecimal.valueOf(-00000046d));
  }

  @Test
  public void MergingGroup_createMergingGroupWithResolutionGreaterThanZero_mergingGroupIsCreated() {
    Integer mergingGroupNumber = Integer.valueOf(11111);
    BigDecimal mergingGroupResolution = BigDecimal.valueOf(0.000000001d);
    boolean expectedIsEnabled = true;

    MergingGroup mergingGroup =
        statementFactory.createMergingGroup(mergingGroupNumber, mergingGroupResolution);

    assertValidMergingGroup(mergingGroupNumber, mergingGroupResolution, expectedIsEnabled,
        mergingGroup);
  }

  @Test(expected = NullPointerException.class)
  public void MergingGroup_copyMergingGroupWithNullParameter_nullPointerExceptionIsThrown() {
    statementFactory.copyMergingGroup(null);
  }

  @Test
  public void MergingGroup_copyMergingGroup_mergingGroupIsCopied() {
    Integer mergingGroupNumber = Integer.valueOf(1);
    BigDecimal mergingGroupResolution = BigDecimal.valueOf(1.1d);
    boolean expectedIsEnabled = true;
    MergingGroup originalMergingGroup =
        statementFactory.createMergingGroup(mergingGroupNumber, mergingGroupResolution);

    MergingGroup copiedMergingGroup = statementFactory.copyMergingGroup(originalMergingGroup);

    assertValidMergingGroup(mergingGroupNumber, mergingGroupResolution, expectedIsEnabled,
        copiedMergingGroup);
  }

  // TODO equals, hashCode, compareTo
  // TODO copy malicious mutable statement.

  @Test
  public void test() {
    fail("Not yet implemented");
  }

}
