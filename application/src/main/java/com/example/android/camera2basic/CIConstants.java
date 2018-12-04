package com.example.android.camera2basic;

public class CIConstants extends Object {

	private CIConstants() {
		super();
	}
	
	public static final String PREF_IMAGE1 = "img1";
	public static final String PREF_IMAGE2 = "img2";
	public static final String PREF_SAMPLE_RATE1 = "sample1";
	public static final String PREF_SAMPLE_RATE2 = "sample2";
	public static String RESULT_COMPARESIMPLE = "";
	public static String RESULT_COMPAREVECTORDIFF = "";
	//max difference value for a cell 10x10 pixels - sqrt (255*255 + 255*255 + 255*255)
	public static final double MAX_DIFF_VALUE = 441.67295593;
}
