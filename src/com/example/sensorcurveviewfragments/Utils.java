package com.example.sensorcurveviewfragments;

public class Utils {
	public static float[] multiplyMV3(float[] mat, float[] vector) {
		try {
			if (!(mat.length == 9 && vector.length == 3))
				throw new Exception();
		} catch (Exception e) {
			System.out.println("!(mat.length==9&&vector.length==3)");
		}
		float[] res = new float[3];
		for (int i = 0; i < 3; i++) {
			int idx = 3 * i;
			res[i] = mat[idx] * vector[0] + mat[idx + 1] * vector[1]
					+ mat[idx + 2] * vector[2];
		}
		return res;
	}

}
