package com.example.sensorcurveviewfragments;

import android.content.Context;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.AttributeSet;

public class LowPassFilterView extends MyCurveView {
	{
		_type = new int[] { Sensor.TYPE_ACCELEROMETER };
	}
	boolean _noFilterCurveEnabled = true, _lpfCurveEnabled = true;

	float _gap = SensorManager.STANDARD_GRAVITY;
	float[] _vals = { 0, 0, 0 }, _lastVals = { 0, 0, 0 }, _lastGravityVals = {
			0, 0, 0 }, _gravityVals = { 0, 0, 0 };

	public LowPassFilterView(Context context) {
		super(context);
	}

	public LowPassFilterView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		// System.out.println("--------------\n"+event.timestamp);//注意！纳秒级
		// System.out.println(SystemClock.uptimeMillis());//boot
		// 开始算起的非睡眠时间，数值上等于 event.timestamp/1000,000
		// System.out.println(SystemClock.currentThreadTimeMillis());//不是timestamp
		// System.out.println(System.currentTimeMillis());//小心！ 此数值会因更改系统时间而变掉

		// alpha=T/(T+dT), dT, the event delivery rate
		final float alpha = 0.8f;
		_lastVals = _vals.clone();
		_lastGravityVals = _gravityVals.clone();
		_vals = event.values.clone();
		for (int i = 0; i < 3; i++) {
			_gravityVals[i] = alpha * _gravityVals[i] + (1 - alpha) * _vals[i];
		}

		float newX = _lastX + _speed;
		float base = _height / 2;
		float factor = -_height / 4 / _gap;

		_paint.setStrokeWidth(2);
		Paint oldPaint = new Paint(_paint);
		for (int i = 0; i < 3; i++) {
			_paint.setColor(_colors[i]);
			if (_noFilterCurveEnabled) {
				PathEffect effect = new DashPathEffect(new float[] { 5, 5 }, 0);
				_paint.setPathEffect(effect);
				_canvas.drawLine(_lastX, base + _lastVals[i] * factor, newX,
						base + _vals[i] * factor, _paint);
			}
			if (_lpfCurveEnabled) {
				// _paint.setColor(_colors[i+3]);
				_paint.setPathEffect(oldPaint.getPathEffect());
				_canvas.drawLine(_lastX, base + _lastGravityVals[i] * factor,
						newX, base + _gravityVals[i] * factor, _paint);
			}
		}
		_paint.set(oldPaint);

		_lastX = newX;

		if (_lastX >= _maxX) {
			// System.out.println("_lastX>=_maxX");
			_lastX = 0;
			_canvas.drawColor(0xffcccccc); // 这里同时起到 清空屏幕的作用
			_canvas.drawLine(0, _height / 4, _maxX, _height / 4, _paint);
			_canvas.drawLine(0, _height / 2, _maxX, _height / 2, _paint);
			_canvas.drawLine(0, _height * 3 / 4, _maxX, _height * 3 / 4, _paint);

			float oldTextSize = _paint.getTextSize();
			_paint.setTextSize(24);
			_canvas.drawText("低通滤波曲线对照（实线为处理后数据）", 11, 55, _paint);
			_paint.setTextSize(oldTextSize);
		}
		invalidate();
	}// onSensorChanged

	public void setNoFilterCurveEnabled(boolean enable) {
		_noFilterCurveEnabled = enable;
	}

	public void setLPFCurveEnabled(boolean enable) {
		_lpfCurveEnabled = enable;
	}

}
