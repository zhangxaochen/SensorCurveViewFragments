package com.example.sensorcurveviewfragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MyCurveView extends ImageView implements SensorEventListener {
	SensorManager _sm = null;
	int[] _type = null;

	Canvas _canvas = new Canvas();
	float _maxX, _lastX;
	int _width, _height;
	int _speed = 2;

	int[] _colors = new int[6];
	Paint _paint = new Paint();

	{
		_colors[0] = Color.argb(255, 255, 0, 0);
		_colors[1] = Color.argb(255, 0, 200, 0);
		_colors[2] = Color.argb(255, 0, 0, 255);
	}

	public MyCurveView(Context ctx) {
		super(ctx);
	}

	public MyCurveView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Bitmap bm = Bitmap.createBitmap(w, h, Config.RGB_565);
		this.setImageBitmap(bm);
		_canvas.setBitmap(bm);

		_lastX = _maxX = _width = w;
		_height = h;

	}

	/**
	 * 
	 * @param rate
	 *            采样频率
	 */
	public void start(int rate) {
		_sm = (SensorManager) getContext().getSystemService(
				Context.SENSOR_SERVICE);
		if (_sm == null) {
			System.out.println("MyCurveView.start(): _sm==null");
			return;
		}
		// ---下面registerListener
		for(int i:_type){
			_sm.registerListener(this, _sm.getDefaultSensor(i), rate);
		}
	} // start

	/**
	 * rate设定默认 40fps
	 */
	public void start() {
		start(1000 * 1000 / 40);
	}

	public void stop() {
		if (_sm == null) {
			try {
				throw new Exception();
			} catch (Exception e) {
				System.out.println("MyCurveView.stop(): _sm==null");
			}
		}
		_sm.unregisterListener(this);
	} // stop

}
