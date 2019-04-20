package com.ht.wfp3.api.statement;

import java.math.BigDecimal;

class CurveApproxCparmTechniqueImp extends CurveApproxImp implements CurveApproxCparmTechnique {
  private static final String TECHNIQUE_KEYWORD = "cparm";

  private final BigDecimal resolution;

  CurveApproxCparmTechniqueImp(BigDecimal resolution) {
    super(TECHNIQUE_KEYWORD);
    this.resolution = resolution;
  }

  CurveApproxCparmTechniqueImp(CurveApproxCparmTechnique curveApproxCparmTechnique) {
    this(curveApproxCparmTechnique.getResolution());
  }

  @Override
  public BigDecimal getResolution() {
    return resolution;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((resolution == null) ? 0 : resolution.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    CurveApproxCparmTechniqueImp other = (CurveApproxCparmTechniqueImp) obj;
    if (resolution == null) {
      if (other.resolution != null)
        return false;
    } else if (!resolution.equals(other.resolution))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "CurveApproxCparmTechniqueImp [resolution=" + resolution + ", super.toString()="
        + super.toString() + "]";
  }
}
