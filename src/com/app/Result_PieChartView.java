package com.app;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class Result_PieChartView extends View {
	private static final int WAIT = 0;
	private static final int IS_READY_TO_DRAW = 1;
	private static final int IS_DRAW = 2;
	private static final float START_INC = 30;
	private Paint mBgPaints = new Paint();
	private Paint mLinePaints = new Paint();
	private Paint mText = new Paint();
	private int mOverlayId;
	private int mWidth;
	private int mHeight;
	private int mGapLeft;
	private int mGapRight;
	private int mGapTop;
	private int mGapBottom;
	private int mBgColor;
	private int mState = WAIT;
	private float mStart;
	private float mSweep;
	private int mMaxConnection;
	private List<Result_PieDetailsItem> mDataArray;

	// --------------------------------------------------------------------------------------
	public Result_PieChartView(Context context) {
		super(context);
	}

	// --------------------------------------------------------------------------------------
	public Result_PieChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// --------------------------------------------------------------------------------------
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// ------------------------------------------------------
		if (mState != IS_READY_TO_DRAW)
			return;
		canvas.drawColor(mBgColor);
		// ------------------------------------------------------
		mBgPaints.setAntiAlias(true);
		mBgPaints.setStyle(Paint.Style.FILL);
		mBgPaints.setColor(0x88FF0000);
		mBgPaints.setStrokeWidth(0.5f);
		// ------------------------------------------------------
		mLinePaints.setAntiAlias(true);
		mLinePaints.setStyle(Paint.Style.STROKE);
		mLinePaints.setColor(0xff000000);
		mLinePaints.setStrokeWidth(0.5f);
		// ------------------------------------------------------
		mText.setTextSize(30);
		mText.setAntiAlias(true);
		mText.setColor(Color.BLACK);
		mText.setStyle(Style.FILL);
		// ------------------------------------------------------
		RectF mOvals = new RectF(mGapLeft, mGapTop, mWidth - mGapRight, mHeight - mGapBottom);
		// ------------------------------------------------------
		mStart = START_INC;
		Result_PieDetailsItem Item;

		float lblX;
		float lblY;
		String LblPercent = null;
		float Percent;
		DecimalFormat FloatFormatter = new DecimalFormat("0.## %");
		float CenterOffset = (mWidth / 2); // Pie Center from Top-Left origin
		float Conv = (float) (2 * Math.PI / 360); // Constant for convert Degree
													// to rad.
		float Radius = 2 * (mWidth / 2) / 3; // Radius of the circle will be
												// drawn the legend.
		Rect bounds = new Rect();
		for (int i = 0; i < mDataArray.size(); i++) {
			Item = mDataArray.get(i);
			mBgPaints.setColor(Item.color);
			mSweep = 360 * ((float) Item.count / (float) mMaxConnection);
			canvas.drawArc(mOvals, mStart, mSweep, true, mBgPaints);
			canvas.drawArc(mOvals, mStart, mSweep, true, mLinePaints);
			Percent = (float) Item.count / (float) mMaxConnection;
			LblPercent = FloatFormatter.format(Percent) + " " + Item.label;
			// Get Label width and height in pixels
			mText.getTextBounds(LblPercent, 0, LblPercent.length(), bounds);

			// Calculate final coords for Label
			lblX = (float) (CenterOffset + Radius * Math.cos(Conv * (mStart + mSweep / 2))) - bounds.width() / 2;
			lblY = (float) (CenterOffset + Radius * Math.sin(Conv * (mStart + mSweep / 2))) + bounds.height() / 2;
			// Dwraw Label on Canvas
			canvas.drawText(LblPercent, lblX, lblY, mText);

			mStart += mSweep;
		}

		// ------------------------------------------------------
		Options options = new BitmapFactory.Options();
		options.inScaled = false;
		Bitmap OverlayBitmap = BitmapFactory.decodeResource(getResources(), mOverlayId, options);
		canvas.drawBitmap(OverlayBitmap, 0.0f, 0.0f, null);
		// ------------------------------------------------------
		mState = IS_DRAW;
	}

	// --------------------------------------------------------------------------------------
	public void setGeometry(int width, int height, int GapLeft, int GapRight, int GapTop, int GapBottom, int OverlayId) {
		mWidth = width;
		mHeight = height;
		mGapLeft = GapLeft;
		mGapRight = GapRight;
		mGapTop = GapTop;
		mGapBottom = GapBottom;
		mOverlayId = OverlayId;
	}

	// --------------------------------------------------------------------------------------
	public void setSkinParams(int bgColor) {
		mBgColor = bgColor;
	}

	// --------------------------------------------------------------------------------------
	public void setData(List<Result_PieDetailsItem> data, int MaxConnection) {
		mDataArray = data;
		mMaxConnection = MaxConnection;
		mState = IS_READY_TO_DRAW;
	}

	// --------------------------------------------------------------------------------------
	public void setState(int State) {
		mState = State;
	}

	// --------------------------------------------------------------------------------------
	public int getColorValue(int Index) {
		if (mDataArray == null)
			return 0;
		if (Index < 0) {
			return mDataArray.get(0).color;
		} else if (Index >= mDataArray.size()) {
			return mDataArray.get(mDataArray.size() - 1).color;
		} else {
			return mDataArray.get(mDataArray.size() - 1).color;
		}
	}
}