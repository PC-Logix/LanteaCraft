package gcewing.sg.util;

public class Matrix3 {

	public static Matrix3 ident = new Matrix3();

	public static Matrix3[] turnRotations = { rotY(0), rotY(90), rotY(180), rotY(270) };

	public static Matrix3[] sideRotations = {
	/* 0, -Y, DOWN */ident,
	/* 1, +Y, UP */rotX(180),
	/* 2, -Z, NORTH */rotX(90),
	/* 3, +Z, SOUTH */rotX(-90).mul(rotY(180)),
	/* 4, -X, WEST */rotZ(-90).mul(rotY(90)),
	/* 5, +X, EAST */rotZ(90).mul(rotY(-90)) };

	public double m[][] = new double[][] { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };

	public static Matrix3 rotX(double deg) {
		return rot(deg, 1, 2);
	}

	public static Matrix3 rotY(double deg) {
		return rot(deg, 2, 0);
	}

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

	public Vector3 mul(double x, double y, double z) {
		return new Vector3(x * m[0][0] + y * m[0][1] + z * m[0][2], x * m[1][0] + y * m[1][1] + z * m[1][2], x
				* m[2][0] + y * m[2][1] + z * m[2][2]);
	}

	public Vector3 imul(double x, double y, double z) {
		// Multiply by inverse, assuming an orthonormal matrix
		return new Vector3(x * m[0][0] + y * m[1][0] + z * m[2][0], x * m[0][1] + y * m[1][1] + z * m[2][1], x
				* m[0][2] + y * m[1][2] + z * m[2][2]);
	}

	public Vector3 mul(Vector3 v) {
		return mul(v.x, v.y, v.z);
	}

	public Vector3 imul(Vector3 v) {
		return imul(v.x, v.y, v.z);
	}

	public void dump() {
		// for (int i = 0; i < 3; i++)
		// System.out.printf("[%6.3f %6.3f %6.3f]\n", m[i][0], m[i][1],
		// m[i][2]);
	}

}
