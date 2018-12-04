package com.example.android.camera2basic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.util.Log;

public class CIComparator extends Object {

	private Bitmap original;
	private Bitmap other;
	
	private int originalWhole [][];
	private int otherWhole [][];
	
	private int originalRed;
	private int originalGreen;
	private int originalBlue;
	private int otherRed;
	private int otherGreen;
	private int otherBlue;
	
	private int avgOriginalR;
	private int avgOriginalG;
	private int avgOriginalB;
	private int avgOtherR;
	private int avgOtherG;
	private int avgOtherB;
	
	private int resultOfCompare;
	private int resultOfCompareVectorDiff;
	private double vectorDiff;
	
	public CIComparator() {
		super();
	}
	
	public void setImages(String aImage1, String aImage2, int aSampleRate1, int aSampleRate2) {
		//original = BitmapFactory.decodeResource(res, R.drawable.other3);
		//other = BitmapFactory.decodeResource(res, R.drawable.other6);
		Options opts = new Options();
		opts.inSampleSize = aSampleRate1;//8;
		original = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(aImage1, opts), 200, 200, false);
		opts.inSampleSize = aSampleRate2;
		other = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(aImage2, opts), 200, 200, false);
//		original = Bitmap.createScaledBitmap(original, 100, 100, false);
//		other = Bitmap.createScaledBitmap(other, 100, 100, false);

//		Log.i("CIComparator", "imageWidth=" + original.getWidth() + ", imageHeight=" + original.getHeight());
//		Log.i("CIComparator", "Other imageWidth=" + other.getWidth() + ", Other imageHeight=" + other.getHeight());
	}
	
	public int compareSimple() {
		
		int divideBy = 0;
		int rows = original.getHeight() / 10;
		int cols = original.getWidth() / 10;
//		Log.i("CIComparator", "rows=" + rows + ", cols=" + cols);
		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < cols; col++) {
				avgOriginal(row, col);
				avgOther(row, col);
				
				divideBy++;
				
				int resultR = avgOriginalR - avgOtherR;
				int resultG = avgOriginalG - avgOtherG;
				int resultB = avgOriginalB - avgOtherB;
				//threshold 15%
				if((resultR > -40 && resultR < 40) && (resultG > -40 && resultG < 40) && (resultB > - 40 && resultB < 40)) {
					resultOfCompare++;
				}
			}
		}

		//感觉compareSimpleAVG作为整个图片的计算不够合理
		//compareSimpleAVG
//		int avgOR = totalOR / divideBy;
//		int avgOG = totalOG / divideBy;
//		int avgOB = totalOB / divideBy;
//		int avgOdR = totalOdR / divideBy;
//		int avgOdG = totalOdG / divideBy;
//		int avgOdB = totalOdB / divideBy;
//
//		int alikeTotal = (avgOR - avgOdR) + (avgOG - avgOdG) + (avgOB - avgOdB);
//		int percent = (alikeTotal * 100) / 255;
//		percent = 100 - percent;
//		if(percent > 100) { percent = percent - ((percent - 100) * 2);}
		//compareSimpleAVG end
		
		//set textView text with the results
		int result=(resultOfCompare * 100) / (rows * cols);
		CIConstants.RESULT_COMPARESIMPLE = "compareSimple         " + result + "%";
//		CIConstants.RESULT_COMPARESIMPLEAVG = "compareSimpleAVG \t" + percent + "%";
		
		resultOfCompare = 0;
		return result;
	}



	/* Compare 3D vector difference. Smaller difference = more similar */
	public int compareVectorDiffAVG() {
		vectorDiff = 0;
		int rows = original.getHeight() / 10;
		int cols = original.getWidth() / 10;

		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < cols; col++) {
				avgOriginal(row, col);
				avgOther(row, col);
				
				vectorDiff += Math.sqrt((avgOriginalR - avgOtherR) * (avgOriginalR - avgOtherR) + (avgOriginalG - avgOtherG) * (avgOriginalG - avgOtherG) 
						+ (avgOriginalB - avgOtherB) * (avgOriginalB - avgOtherB));
				
				//vectorDiff
				double diff = Math.sqrt((avgOriginalR - avgOtherR) * (avgOriginalR - avgOtherR) + (avgOriginalG - avgOtherG) * (avgOriginalG - avgOtherG)
						+ (avgOriginalB - avgOtherB) * (avgOriginalB - avgOtherB));
				//threshold 16%
				if(diff < (CIConstants.MAX_DIFF_VALUE / 6)) {
					resultOfCompareVectorDiff++;
				}
				//end vectorDiff
			}
		}
//		Log.i("CIComparator", "vectorDiff=" + vectorDiff);
		//the result as a percent of the maximum vectorDiff
//		int result = (int) (vectorDiff * 100 / (CIConstants.MAX_DIFF_VALUE * (rows * cols)));
//		result = 100 - result;
//		CIConstants.RESULT_COMPAREVECTORDIFF_AVG = "compareVectorDiffAVG \t" + String.valueOf(result) + "%";
		int result=(resultOfCompareVectorDiff * 100) / (rows * cols);
		CIConstants.RESULT_COMPAREVECTORDIFF = "compareVectorDiff    " + result + "%";
		resultOfCompareVectorDiff = 0;
		return result;
	}

	
	private void avgOriginal(int aRow, int aCol) {
		//calculate for one square 10x10 pixels
				originalRed = 0;
				originalGreen = 0;
				originalBlue = 0;
				
				for(int y = aRow * 10 ; y < (aRow * 10) + 10; y++) {
					for(int x = aCol * 10; x < (aCol * 10) + 10; x++) {
						int pixel = original.getPixel(x, y);
						originalRed += Color.red(pixel);
						originalGreen += Color.green(pixel);
						originalBlue += Color.blue(pixel);
					}
				}
				int avgR = originalRed / 100;
				int avgG = originalGreen / 100;
				int avgB = originalBlue / 100;
				Log.i("CIComparator", "avgOriginalR=" + avgR + ", avgOriginalG=" + avgG + ", avgOriginalB=" + avgB);
				
				avgOriginalR = avgR;
				avgOriginalG = avgG;
				avgOriginalB = avgB;
	}
	
	private void avgOther(int aRow, int aCol) {
		//calculate for one square 10x10 pixels
				otherRed = 0;
				otherGreen = 0;
				otherBlue = 0;
				
				for(int y = aRow * 10 ; y < (aRow * 10) + 10; y++) {
					for(int x = aCol * 10; x < (aCol * 10) + 10; x++) {
						int pixel = other.getPixel(x, y);
						otherRed += Color.red(pixel);
						otherGreen += Color.green(pixel);
						otherBlue += Color.blue(pixel);
					}
				}
				int avgR = otherRed / 100;
				int avgG = otherGreen / 100;
				int avgB = otherBlue / 100;
//				Log.i("CIComparator", "avgOtherR=" + avgR + ", avgOtherG=" + avgG + ", avgOtherB=" + avgB);
				
				avgOtherR = avgR;
				avgOtherG = avgG;
				avgOtherB = avgB;

	}

	
}
