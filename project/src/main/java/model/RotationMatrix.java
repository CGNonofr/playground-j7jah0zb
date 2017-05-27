package model;

public class RotationMatrix {
	private double[][] matrix;
	public RotationMatrix(double[][] matrix) {
		this.matrix=matrix;
	}
	
	public static RotationMatrix fromAxisAngle(Vector axis, double ang) {
		axis=axis.normalize();
		double cos=Math.cos(ang);
		double sin=Math.sin(ang);
		return new RotationMatrix(new double[][] {
          new double[]{cos+axis.getX()*axis.getX()*(1-cos), axis.getX()*axis.getY()*(1-cos)-axis.getZ()*sin, axis.getX()*axis.getZ()*(1-cos)+axis.getY()*sin},
          new double[]{axis.getY()*axis.getX()*(1-cos)+axis.getZ()*sin, cos + axis.getY()*axis.getY()*(1-cos), axis.getY()*axis.getZ()*(1-cos)-axis.getX()*sin},
          new double[]{axis.getZ()*axis.getX()*(1-cos)-axis.getY()*sin, axis.getZ()*axis.getY()*(1-cos)+axis.getX()*sin, cos+axis.getZ()*axis.getZ()*(1-cos)}
		});
	}
	
	public Vector apply(Vector v) {
		return new Vector(v.getX()*matrix[0][0]+v.getY()*matrix[0][1]+v.getZ()*matrix[0][2],v.getX()*matrix[1][0]+v.getY()*matrix[1][1]+v.getZ()*matrix[1][2],v.getX()*matrix[2][0]+v.getY()*matrix[2][1]+v.getZ()*matrix[2][2]);
	}
}
