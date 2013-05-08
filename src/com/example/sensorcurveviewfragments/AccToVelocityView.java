package com.example.sensorcurveviewfragments;

import android.content.Context;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.AttributeSet;

public class AccToVelocityView extends MyCurveView {
	{
		_type = new int[] { Sensor.TYPE_LINEAR_ACCELERATION, Sensor.TYPE_ROTATION_VECTOR };
	}
	// float _dt=0;
	long _lastTimeStamp = 0;

	boolean _accCurveEnabled = true, _velocityCurveEnabled = true,
			_motionCurveEnabled = true;

	float _gap = SensorManager.STANDARD_GRAVITY;
	float[] _accVals = { 0, 0, 0 }, _lastAccVals = { 0, 0, 0 },
			_velocityVals = { 0, 0, 0 }, _lastVelocityVals = { 0, 0, 0 },
			_motionVals = { 0, 0, 0 }, _lastMotionVals = { 0, 0, 0 };

	float[] rotMat=new float[9];
	
	public AccToVelocityView(Context ctx) {
		super(ctx);
	}

	public AccToVelocityView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		int type=event.sensor.getType();
		if(type==Sensor.TYPE_ROTATION_VECTOR){
			System.out.println("type==Sensor.TYPE_ROTATION_VECTOR");
			SensorManager.getRotationMatrixFromVector(rotMat, event.values);
		} else if (type == Sensor.TYPE_LINEAR_ACCELERATION) {
			System.out.println("type == Sensor.TYPE_LINEAR_ACCELERATION");
//			int i=0;
//			for(;i<rotMat.length;i++){
//				if(rotMat[i]!=0)
//					break;
//			}
//			if(i==rotMat.length)	//如果 rotMat 全零
//				return;
				
			

			_lastAccVals = _accVals.clone();
			_lastVelocityVals = _velocityVals.clone();
			_lastMotionVals = _motionVals.clone();

//			_accVals = event.values.clone();
			_accVals=Utils.multiplyMV3(rotMat, event.values);
			
			float dt = 0;
			if (_lastTimeStamp != 0)
				dt = (event.timestamp - _lastTimeStamp) / 1000000000.f;
			// System.out.println("dt: "+dt+", "+event.timestamp+", "+_lastTimeStamp);
			for (int i = 0; i < _accVals.length; i++)
				_velocityVals[i] += dt * _accVals[i];
			_lastTimeStamp = event.timestamp;

			float newX = _lastX + _speed;
			float base = _height / 2;
			float factor = -_height / 4 / _gap;
			_paint.setStrokeWidth(2);
			Paint oldPaint = new Paint(_paint);
			for (int i = 0; i < 3; i++) {
				_paint.setColor(_colors[i]);
				if (_accCurveEnabled) {
					PathEffect effect = new DashPathEffect(
							new float[] { 5, 5 }, 1);
					_paint.setPathEffect(effect);
					_canvas.drawLine(_lastX, base + _lastAccVals[i] * factor,
							newX, base + _accVals[i] * factor, _paint);
				}
				if (_velocityCurveEnabled) {
					_paint.setPathEffect(oldPaint.getPathEffect());
					_canvas.drawLine(_lastX, base + _lastVelocityVals[i]
							* factor, newX, base + _velocityVals[i] * factor,
							_paint);
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
				_canvas.drawLine(0, _height * 3 / 4, _maxX, _height * 3 / 4,
						_paint);
			}
			invalidate();
		}
	} // onSensorChanged

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public void setAccCurveEnabled(boolean enabled) {
		_accCurveEnabled = enabled;
	}

	public void setVelocityCurveEnabled(boolean enabled) {
		_velocityCurveEnabled = enabled;
	}

	public void setMotionCurveEnabled(boolean enabled) {
		_motionCurveEnabled = enabled;
	}

} // AccToVelocityView
