package com.example.sensorcurveviewfragments;

import android.content.Context;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.AttributeSet;

public class RotationMatrixView extends MyCurveView {
	{
		_type=new int[]{Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_ROTATION_VECTOR};
//		_type=new int[]{Sensor.TYPE_LINEAR_ACCELERATION, Sensor.TYPE_ROTATION_VECTOR};
	}
	boolean _worldFrameCurveEnabled = true, _bodyFrameCurveEnabled = true;

	float _gap = SensorManager.STANDARD_GRAVITY;
	// _worldVals 存储世界坐标系坐标值
	float[] _vals = { 0, 0, 0 }, _worldVals = { 0, 0, 0 }, _lastVals,
			_lastWorldVals;

	float[] _matB = new float[9];

	public RotationMatrixView(Context ctx) {
		super(ctx);
	}

	public RotationMatrixView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// ---------手算版，参考李嘉俊
		float[] vals = event.values;
		float x = vals[0], y = vals[1], z = vals[2];
		int type = event.sensor.getType();
		if (type == Sensor.TYPE_ROTATION_VECTOR) {
			float w = (float) Math.sqrt(1 - x * x - y * y - z * z);
			{
				float[] matA = new float[9];
				matA[0] = 1 - 2 * (y * y + z * z);
				matA[1] = 2 * (x * y - w * z);
				matA[2] = 2 * (x * z + w * y);

				matA[3] = 2 * (x * y + w * z);
				matA[4] = 1 - 2 * (x * x + z * z);
				matA[5] = 2 * (y * z - w * x);

				matA[6] = 2 * (x * z - w * y);
				matA[7] = 2 * (y * z + w * x);
				matA[8] = 1 - 2 * (x * x + y * y);
			}
			// 实验证实了 matA/B 完全一样。也见源码
			SensorManager.getRotationMatrixFromVector(_matB, vals);
		} else if (type == Sensor.TYPE_ACCELEROMETER
				|| type == Sensor.TYPE_LINEAR_ACCELERATION) {
			_lastVals = _vals.clone();
			_lastWorldVals = _worldVals.clone();

			_vals = vals.clone();
			_worldVals = Utils.multiplyMV3(_matB, _vals);

			float newX = _lastX + _speed;
			float base = _height / 2;
			float factor = -_height / 4 / _gap;

			_paint.setStrokeWidth(2);
			Paint oldPaint = new Paint(_paint);
			for (int i = 0; i < 3; i++) {
				_paint.setColor(_colors[i]);

				if (_bodyFrameCurveEnabled) {
					PathEffect effect = new DashPathEffect(
							new float[] { 5, 5 }, 1);
					_paint.setPathEffect(effect);
					_canvas.drawLine(_lastX, base + _lastVals[i] * factor,
							newX, base + _vals[i] * factor, _paint);
				}
				if (_worldFrameCurveEnabled) {
					_paint.setPathEffect(oldPaint.getPathEffect());
					_canvas.drawLine(_lastX, base + _lastWorldVals[i] * factor,
							newX, base + _worldVals[i] * factor, _paint);
				}
			}
			_paint.set(oldPaint);

			_lastX = newX;

			if (_lastX >= _maxX) {
				// System.out.println("_lastX>=_maxX");
				_lastX = 0;
				_canvas.drawColor(0xffccddcc); // 这里同时起到 清空屏幕的作用
				_canvas.drawLine(0, _height / 4, _maxX, _height / 4, _paint);
				_canvas.drawLine(0, _height / 2, _maxX, _height / 2, _paint);
				_canvas.drawLine(0, _height * 3 / 4, _maxX, _height * 3 / 4,
						_paint);

				float oldTextSize = _paint.getTextSize();
				_paint.setTextSize(24);
				_canvas.drawText("两坐标系曲线对照（实线世界坐标系）", 11, 55, _paint);
				_paint.setTextSize(oldTextSize);

			}

			invalidate();
		}

	}// onSensorChanged

	public void setBodyFrameCurveEnabled(boolean enable) {
		_bodyFrameCurveEnabled = enable;
	}

	public void setWorldFrameCurveEnabled(boolean enable) {
		_worldFrameCurveEnabled = enable;
	}

}
