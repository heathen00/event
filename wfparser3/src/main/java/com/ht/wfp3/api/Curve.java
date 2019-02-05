package com.ht.wfp3.api;

import java.util.List;

/**
 * A 3D free-form curve.
 * 
 * curv u0 u1 v1 v2 . . .
 * 
 * Element statement for free-form geometry.
 * 
 * Specifies a curve, its parameter range, and its control vertices. Although curves cannot be
 * shaded or rendered, they are used by other Advanced Visualizer programs.
 * 
 * u0 is the starting parameter value for the curve. This is a floating point number.
 * 
 * u1 is the ending parameter value for the curve. This is a floating point number.
 * 
 * v is the vertex reference number for a control point. You can specify multiple control points. A
 * minimum of two control points are required for a curve.
 * 
 * For a non-rational curve, the control points must be 3D. For a rational curve, the control points
 * are 3D or 4D. The fourth coordinate (weight) defaults to 1.0 if omitted.
 * 
 * @author nickl
 *
 */
public interface Curve extends Node, Commentable {

  String getStartingParameterValue();

  String getEndingParameterValue();

  void appendControlPointVertexReferenceGroup(VertexReferenceGroup referenceNumbers);

  List<VertexReferenceGroup> getControlPointVertexReferenceGroup();

}