package com.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class TabMap_RouteOverlay extends Overlay {

	private GeoPoint gp1;
	private GeoPoint gp2;
	Context mcontext;

	public TabMap_RouteOverlay(GeoPoint gp1, GeoPoint gp2, Context context) {
		mcontext = context;
		this.gp1 = gp1;
		this.gp2 = gp2;
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		// TODO Auto-generated method stub

		Projection projection = mapView.getProjection();
		if (shadow == false) {

			/** set paint for point **/
			Paint paintPoint = new Paint();
			paintPoint.setDither(true);
			paintPoint.setAntiAlias(true);
			paintPoint.setAntiAlias(true);
			paintPoint.setStyle(Paint.Style.FILL_AND_STROKE);
			paintPoint.setStrokeJoin(Paint.Join.ROUND);
			paintPoint.setStrokeCap(Paint.Cap.ROUND);
			paintPoint.setStrokeWidth(14);
			paintPoint.setColor(Color.rgb(39, 64, 139));
			paintPoint.setAlpha(255);

			/** set paint for line **/
			Paint paintPath = new Paint();
			paintPath.setAntiAlias(true);
			paintPath.setDither(true);
			paintPath.setFakeBoldText(true);
			paintPath.setStrokeWidth(10);
			paintPath.setStyle(Paint.Style.FILL_AND_STROKE);
			paintPath.setStrokeJoin(Paint.Join.ROUND);
			paintPath.setStrokeCap(Paint.Cap.ROUND);
			paintPath.setColor(Color.rgb(255, 48, 48));
			paintPath.setAlpha(80);

			Point from = new Point();
			Point to = new Point();
			projection.toPixels(gp1, from);
			projection.toPixels(gp2, to);
			Path path = new Path();
			path.moveTo(from.x, from.y);
			path.lineTo(to.x, to.y);
			Bitmap bmp = BitmapFactory.decodeResource(mcontext.getResources(), R.drawable.map_point);
			canvas.drawBitmap(bmp, to.x - 10, to.y - 10, null);
			canvas.drawLine(from.x, from.y, to.x, to.y, paintPath);
		}
		return super.draw(canvas, mapView, shadow, when);

	}
}