package lc.common.util.math;

/**
 * 3D matrix implementation
 *
 * @author AfterLifeLochie
 *
 */
public class Matrix3 {

	/** Identity matrix */
	public static Matrix3 ident = new Matrix3();

	/** Rotation map */
	public static Matrix3[] turnRotations = { rotY(0), rotY(90), rotY(180), rotY(-90) };

	/** Side rotation map */
	public static Matrix3[] sideRotations = {
		/* 0, -Y, DOWN */ident,
		/* 1, +Y, UP */rotX(180),
		/* 2, -Z, NORTH */rotX(90),
		/* 3, +Z, SOUTH */rotX(-90).mul(rotY(180)),
		/* 4, -X, WEST */rotZ(-90).mul(rotY(90)),
		/* 5, +X, EAST */rotZ(90).mul(rotY(-90)) };

	/** Unit matricies */
	public double m[][] = new double[][] { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };

	/**
	 * Rotation over x axis
	 *
	 * @param deg
	 *            The angle in degrees
	 * @return A rotation matrix for the angle specified
	 * */
	public static Matrix3 rotX(double deg) {
		return rot(deg, 1, 2);
	}

	/**
	 * Rotation over y axis
	 *
	 * @param deg
	 *            The angle in degrees
	 * @return A rotation matrix for the angle specified
	 * */
	public static Matrix3 rotY(double deg) {
		return rot(deg, 2, 0);
	}

	/**
	 * Rotation over z axis
	 *
	 * @param deg
	 *            The angle in degrees
	 * @return A rotation matrix for the angle specified
	 * */
	public static Matrix3 rotZ(double deg) {
		return rot(deg, 0, 1);
	}

	static Matrix3 rot(double deg, int i, int j) {
		double a = Math.toRadians(deg);
		double s = Math.sin(a);
		double c = Math.cos(a);
		Matrix3 r = new Matrix3();
		r.m[i][i] = c;
		r.m[i][j] = -s;
		r.m[j][i] = s;
		r.m[j][j] = c;
		return r;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Matrix3{Gamut:(");
		sb.append(Math.toDegrees(Math.atan2(m[2][1], m[2][2]))).append(",");
		sb.append(Math.toDegrees(Math.atan2(-m[2][0], Math.sqrt(Math.pow(m[2][1], 2) + Math.pow(m[2][2], 2))))).append(",");
		sb.append(Math.toDegrees(Math.atan2(m[1][0], m[0][0]))).append("),M(");
		for (int i = 0; i < 3; i++) {
			sb.append(i).append(":[");
			for (int j = 0; j < 3; j++) {
				sb.append(m[i][j]).append(",");
			}
			sb.append("],");
		}
		sb.append(")}");
		return sb.toString();
	}

	/**
	 * Multiply this matrix against another matrix
	 *
	 * @param n
	 *            The other matrix
	 * @return The product matrix
	 */
	public Matrix3 mul(Matrix3 n) {
		Matrix3 r = new Matrix3();
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				r.m[i][j] = m[i][0] * n.m[0][j] + m[i][1] * n.m[1][j] + m[i][2] * n.m[2][j];
		return r;
	}

	// public Matrix3 imul(Matrix3 n) {
	// Matrix3 r = new Matrix3();
	// for (int i = 0; i < 3; i++)
	// for (int j = 0; j < 3; j++)
	// r.m[i][j] = m[0][i] * n.m[j][0] + m[1][i] * n.m[j][1] + m[2][i] *
	// n.m[j][2];
	// return r;
	// }

	/**
	 * Multiply this matrix against a vector.
	 *
	 * @param x
	 *            The x of the vector
	 * @param y
	 *            The y of the vector
	 * @param z
	 *            The z of the vector
	 * @return The product of matrix * vector result
	 */
	public Vector3 mul(double x, double y, double z) {
		return new Vector3(x * m[0][0] + y * m[0][1] + z * m[0][2], x * m[1][0] + y * m[1][1] + z * m[1][2], x
				* m[2][0] + y * m[2][1] + z * m[2][2]);
	}

	/**
	 * Multiply this matrix by inverse against a vector
	 *
	 * @param x
	 *            The x of the vector
	 * @param y
	 *            The y of the vector
	 * @param z
	 *            The z of the vector
	 * @return The product of matrix * vector result
	 */
	public Vector3 imul(double x, double y, double z) {
		// Multiply by inverse, assuming an orthonormal matrix
		return new Vector3(x * m[0][0] + y * m[1][0] + z * m[2][0], x * m[0][1] + y * m[1][1] + z * m[2][1], x
				* m[0][2] + y * m[1][2] + z * m[2][2]);
	}

	/**
	 * Multiply this matrix against a vector
	 *
	 * @param v
	 *            The vector
	 * @return The product of matrix * vector result
	 */
	public Vector3 mul(Vector3 v) {
		return mul(v.x, v.y, v.z);
	}

	/**
	 * Multiply this matrix by inverse against a vector
	 *
	 * @param v
	 *            The vector
	 * @return The product of matrix * vector result
	 */
	public Vector3 imul(Vector3 v) {
		return imul(v.x, v.y, v.z);
	}

}
