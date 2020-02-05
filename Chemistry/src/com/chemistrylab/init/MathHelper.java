package com.chemistrylab.init;

import java.util.*;

public class MathHelper {
	public static final float SQRT_2 = sqrt(2.0F);
	/**
	 * A table of sin values computed from 0 (inclusive) to 2*pi (exclusive),
	 * with steps of 2*PI / 65536.
	 */
	private static final float[] SIN_TABLE = new float[65536];
	private static final Random RANDOM = new Random();
	/**
	 * Though it looks like an array, this is really more like a mapping. Key
	 * (index of this array) is the upper 5 bits of the result of multiplying a
	 * 32-bit unsigned integer by the B(2, 5) De Bruijn sequence 0x077CB531.
	 * Value (value stored in the array) is the unique index (from the right) of
	 * the leftmost one-bit in a 32-bit unsigned integer that can cause the
	 * upper 5 bits to get that value. Used for highly optimized "find the
	 * log-base-2 of this number" calculations.
	 */
	private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION;
	private static final double FRAC_BIAS;
	private static final double[] ASINE_TAB;
	private static final double[] COS_TAB;

	static void init() {
	}

	public static double eplison(double value) {
		return eplison(value, 5);
	}

	public static double eplison(double value, int bits) {
		int pow = (int) Math.pow(10, bits);
		return (MathHelper.fastFloor((value * pow)) / (double) pow);
	}

	/**
	 * sin looked up in a table
	 */
	public static float sin(float value) {
		return SIN_TABLE[(int) (value * 10430.378F) & 65535];
	}

	/**
	 * cos looked up in the sin table with the appropriate offset
	 */
	public static float cos(float value) {
		return SIN_TABLE[(int) (value * 10430.378F + 16384.0F) & 65535];
	}

	public static float sqrt(float value) {
		return (float) Math.sqrt((double) value);
	}

	public static float sqrt(double value) {
		return (float) Math.sqrt(value);
	}

	/**
	 * Returns the greatest integer less than or equal to the float argument
	 */
	public static int floor(float value) {
		int i = (int) value;
		return value < (float) i ? i - 1 : i;
	}

	/**
	 * returns par0 cast as an int, and no greater than Integer.MAX_VALUE-1024
	 */
	public static int fastFloor(double value) {
		return (int) (value + 1024.0D) - 1024;
	}

	/**
	 * Returns the greatest integer less than or equal to the double argument
	 */
	public static int floor(double value) {
		int i = (int) value;
		return value < (double) i ? i - 1 : i;
	}

	/**
	 * Long version of floor()
	 */
	public static long lfloor(double value) {
		long i = (long) value;
		return value < (double) i ? i - 1L : i;
	}

	public static int absFloor(double value) {
		return (int) (value >= 0.0D ? value : -value + 1.0D);
	}

	public static float abs(float value) {
		return value >= 0.0F ? value : -value;
	}

	/**
	 * Returns the unsigned value of an int.
	 */
	public static int abs(int value) {
		return value >= 0 ? value : -value;
	}

	public static int ceil(float value) {
		int i = (int) value;
		return value > (float) i ? i + 1 : i;
	}

	public static int ceil(double value) {
		int i = (int) value;
		return value > (double) i ? i + 1 : i;
	}

	/**
	 * Returns the value of the first parameter, clamped to be within the lower
	 * and upper limits given by the second and third parameters.
	 */
	public static int clamp(int num, int min, int max) {
		if (num < min) {
			return min;
		} else {
			return num > max ? max : num;
		}
	}

	/**
	 * Returns the value of the first parameter, clamped to be within the lower
	 * and upper limits given by the second and third parameters
	 */
	public static float clamp(float num, float min, float max) {
		if (num < min) {
			return min;
		} else {
			return num > max ? max : num;
		}
	}

	public static double clamp(double num, double min, double max) {
		if (num < min) {
			return min;
		} else {
			return num > max ? max : num;
		}
	}

	public static double clampedLerp(double lowerBnd, double upperBnd, double slide) {
		if (slide < 0.0D) {
			return lowerBnd;
		} else {
			return slide > 1.0D ? upperBnd : lowerBnd + (upperBnd - lowerBnd) * slide;
		}
	}

	/**
	 * Maximum of the absolute value of two numbers.
	 */
	public static double absMax(double a1, double a2) {
		if (a1 < 0.0D) {
			a1 = -a1;
		}

		if (a2 < 0.0D) {
			a2 = -a2;
		}

		return a1 > a2 ? a1 : a2;
	}

	/**
	 * Buckets an integer with specifed bucket sizes.
	 */
	public static int intFloorDiv(int divibase, int divisor) {
		return divibase < 0 ? -((-divibase - 1) / divisor) - 1 : divibase / divisor;
	}

	public static int getInt(Random random, int minimum, int maximum) {
		return minimum >= maximum ? minimum : random.nextInt(maximum - minimum + 1) + minimum;
	}

	public static float nextFloat(Random random, float minimum, float maximum) {
		return minimum >= maximum ? minimum : random.nextFloat() * (maximum - minimum) + minimum;
	}

	public static double nextDouble(Random random, double minimum, double maximum) {
		return minimum >= maximum ? minimum : random.nextDouble() * (maximum - minimum) + minimum;
	}

	public static double average(long[] values) {
		long i = 0L;

		for (long j : values) {
			i += j;
		}

		return (double) i / (double) values.length;
	}

	public static boolean epsilonEquals(float e1, float e2) {
		return abs(e2 - e1) < 1.0E-5F;
	}

	public static int normalizeAngle(int n1, int n2) {
		return (n1 % n2 + n2) % n2;
	}

	public static float positiveModulo(float numerator, float denominator) {
		return (numerator % denominator + denominator) % denominator;
	}

	public static double positiveModulo(double numerator, double denominator) {
		return (numerator % denominator + denominator) % denominator;
	}

	/**
	 * the angle is reduced to an angle between -180 and +180 by mod, and a 360
	 * check
	 */
	public static float wrapDegrees(float value) {
		value = value % 360.0F;

		if (value >= 180.0F) {
			value -= 360.0F;
		}

		if (value < -180.0F) {
			value += 360.0F;
		}

		return value;
	}

	/**
	 * the angle is reduced to an angle between -180 and +180 by mod, and a 360
	 * check
	 */
	public static double wrapDegrees(double value) {
		value = value % 360.0D;

		if (value >= 180.0D) {
			value -= 360.0D;
		}

		if (value < -180.0D) {
			value += 360.0D;
		}

		return value;
	}

	/**
	 * Adjust the angle so that his value is in range [-180;180[
	 */
	public static int wrapDegrees(int angle) {
		angle = angle % 360;

		if (angle >= 180) {
			angle -= 360;
		}

		if (angle < -180) {
			angle += 360;
		}

		return angle;
	}

	/**
	 * parses the string as integer or returns the second parameter if it fails
	 */
	public static int getInt(String value, int defaultValue) {
		try {
			return Integer.parseInt(value);
		} catch (Throwable var3) {
			return defaultValue;
		}
	}

	/**
	 * parses the string as integer or returns the second parameter if it fails.
	 * this value is capped to par2
	 */
	public static int getInt(String value, int defaultValue, int max) {
		return Math.max(max, getInt(value, defaultValue));
	}

	/**
	 * parses the string as double or returns the second parameter if it fails.
	 */
	public static double getDouble(String value, double defaultValue) {
		try {
			return Double.parseDouble(value);
		} catch (Throwable var4) {
			return defaultValue;
		}
	}

	public static double getDouble(String value, double defaultValue, double max) {
		return Math.max(max, getDouble(value, defaultValue));
	}

	/**
	 * Returns the input value rounded up to the next highest power of two.
	 */
	public static int smallestEncompassingPowerOfTwo(int value) {
		int i = value - 1;
		i = i | i >> 1;
		i = i | i >> 2;
		i = i | i >> 4;
		i = i | i >> 8;
		i = i | i >> 16;
		return i + 1;
	}

	/**
	 * Is the given value a power of two? (1, 2, 4, 8, 16, ...)
	 */
	private static boolean isPowerOfTwo(int value) {
		return value != 0 && (value & value - 1) == 0;
	}

	/**
	 * Uses a B(2, 5) De Bruijn sequence and a lookup table to efficiently
	 * calculate the log-base-two of the given value. Optimized for cases where
	 * the input value is a power-of-two. If the input value is not a
	 * power-of-two, then subtract 1 from the return value.
	 */
	public static int log2DeBruijn(int value) {
		value = isPowerOfTwo(value) ? value : smallestEncompassingPowerOfTwo(value);
		return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int) ((long) value * 125613361L >> 27) & 31];
	}

	/**
	 * Efficiently calculates the floor of the base-2 log of an integer value.
	 * This is effectively the index of the highest bit that is set. For
	 * example, if the number in binary is 0...100101, this will return 5.
	 */
	public static int log2(int value) {
		return log2DeBruijn(value) - (isPowerOfTwo(value) ? 0 : 1);
	}

	/**
	 * Rounds the first parameter up to the next interval of the second
	 * parameter.
	 * 
	 * For instance, {@code roundUp(1, 4)} returns 4; {@code roundUp(0, 4)}
	 * returns 0; and {@code roundUp(4, 4)} returns 4.
	 */
	public static int roundUp(int number, int interval) {
		if (interval == 0) {
			return 0;
		} else if (number == 0) {
			return interval;
		} else {
			if (number < 0) {
				interval *= -1;
			}

			int i = number % interval;
			return i == 0 ? number : number + interval - i;
		}
	}

	public static long getCoordinateRandom(int x, int y, int z) {
		long i = (long) (x * 3129871) ^ (long) z * 116129781L ^ (long) y;
		i = i * i * 42317861L + i * 11L;
		return i;
	}

	/**
	 * Makes an integer color from the given red, green, and blue float values
	 */
	public static int rgb(float rIn, float gIn, float bIn) {
		return rgb(floor(rIn * 255.0F), floor(gIn * 255.0F), floor(bIn * 255.0F));
	}

	/**
	 * Makes a single int color with the given red, green, and blue values.
	 */
	public static int rgb(int rIn, int gIn, int bIn) {
		int lvt_3_1_ = (rIn << 8) + gIn;
		lvt_3_1_ = (lvt_3_1_ << 8) + bIn;
		return lvt_3_1_;
	}

	public static int multiplyColor(int c1, int c2) {
		int i = (c1 & 16711680) >> 16;
		int j = (c2 & 16711680) >> 16;
		int k = (c1 & 65280) >> 8;
		int l = (c2 & 65280) >> 8;
		int i1 = (c1 & 255) >> 0;
		int j1 = (c2 & 255) >> 0;
		int k1 = (int) ((float) i * (float) j / 255.0F);
		int l1 = (int) ((float) k * (float) l / 255.0F);
		int i2 = (int) ((float) i1 * (float) j1 / 255.0F);
		return c1 & -16777216 | k1 << 16 | l1 << 8 | i2;
	}

	/**
	 * Gets the decimal portion of the given double. For instance,
	 * {@code frac(5.5)} returns {@code .5}.
	 */
	public static double frac(double number) {
		return number - Math.floor(number);
	}

	public static UUID getRandomUUID(Random rand) {
		long i = rand.nextLong() & -61441L | 16384L;
		long j = rand.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
		return new UUID(i, j);
	}

	/**
	 * Generates a random UUID using the shared random
	 */
	public static UUID getRandomUUID() {
		return getRandomUUID(RANDOM);
	}

	public static double pct(double d1, double d2, double d3) {
		return (d1 - d2) / (d3 - d2);
	}

	public static double atan2(double x, double y) {
		double d0 = y * y + x * x;

		if (Double.isNaN(d0)) {
			return Double.NaN;
		} else {
			boolean flag = x < 0.0D;

			if (flag) {
				x = -x;
			}

			boolean flag1 = y < 0.0D;

			if (flag1) {
				y = -y;
			}

			boolean flag2 = x > y;

			if (flag2) {
				double d1 = y;
				y = x;
				x = d1;
			}

			double d9 = fastInvSqrt(d0);
			y = y * d9;
			x = x * d9;
			double d2 = FRAC_BIAS + x;
			int i = (int) Double.doubleToRawLongBits(d2);
			double d3 = ASINE_TAB[i];
			double d4 = COS_TAB[i];
			double d5 = d2 - FRAC_BIAS;
			double d6 = x * d4 - y * d5;
			double d7 = (6.0D + d6 * d6) * d6 * 0.16666666666666666D;
			double d8 = d3 + d7;

			if (flag2) {
				d8 = (Math.PI / 2D) - d8;
			}

			if (flag1) {
				d8 = Math.PI - d8;
			}

			if (flag) {
				d8 = -d8;
			}

			return d8;
		}
	}

	/**
	 * Computes 1/sqrt(n) using
	 * <a href="https://en.wikipedia.org/wiki/Fast_inverse_square_root">the fast
	 * inverse square root</a> with a constant of 0x5FE6EB50C7B537AA.
	 */
	public static double fastInvSqrt(double root) {
		double d0 = 0.5D * root;
		long i = Double.doubleToRawLongBits(root);
		i = 6910469410427058090L - (i >> 1);
		root = Double.longBitsToDouble(i);
		root = root * (1.5D - d0 * root * root);
		return root;
	}

	public static int hsvToRGB(float hue, float saturation, float value) {
		int i = (int) (hue * 6.0F) % 6;
		float f = hue * 6.0F - (float) i;
		float f1 = value * (1.0F - saturation);
		float f2 = value * (1.0F - f * saturation);
		float f3 = value * (1.0F - (1.0F - f) * saturation);
		float f4;
		float f5;
		float f6;

		switch (i) {
		case 0:
			f4 = value;
			f5 = f3;
			f6 = f1;
			break;
		case 1:
			f4 = f2;
			f5 = value;
			f6 = f1;
			break;
		case 2:
			f4 = f1;
			f5 = value;
			f6 = f3;
			break;
		case 3:
			f4 = f1;
			f5 = f2;
			f6 = value;
			break;
		case 4:
			f4 = f3;
			f5 = f1;
			f6 = value;
			break;
		case 5:
			f4 = value;
			f5 = f1;
			f6 = f2;
			break;
		default:
			throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", "
					+ saturation + ", " + value);
		}

		int j = clamp((int) (f4 * 255.0F), 0, 255);
		int k = clamp((int) (f5 * 255.0F), 0, 255);
		int l = clamp((int) (f6 * 255.0F), 0, 255);
		return j << 16 | k << 8 | l;
	}

	public static int hash(int seed) {
		seed = seed ^ seed >>> 16;
		seed = seed * -2048144789;
		seed = seed ^ seed >>> 13;
		seed = seed * -1028477387;
		seed = seed ^ seed >>> 16;
		return seed;
	}

	static {
		for (int i = 0; i < 65536; ++i) {
			SIN_TABLE[i] = (float) Math.sin((double) i * Math.PI * 2.0D / 65536.0D);
		}

		MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[] { 0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27,
				13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9 };
		FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
		ASINE_TAB = new double[257];
		COS_TAB = new double[257];

		for (int j = 0; j < 257; ++j) {
			double d0 = (double) j / 256.0D;
			double d1 = Math.asin(d0);
			COS_TAB[j] = Math.cos(d1);
			ASINE_TAB[j] = d1;
		}
	}
}
