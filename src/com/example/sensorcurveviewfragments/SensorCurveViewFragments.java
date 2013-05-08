package com.example.sensorcurveviewfragments;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class SensorCurveViewFragments extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_curve_view_fragments);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_sensor_curve_view_fragments,
				menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		/**
		 * 只在一开始初始化被调用。所以里面的 new 不会重复产生新对象
		 */
		@Override
		public Fragment getItem(int position) {
			System.out.println("in getItem()---");
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
//			 Fragment fragment = new DummySectionFragment();
//			 Bundle args = new Bundle();
//			 args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position +
//			 1);
//			 fragment.setArguments(args);
//			 return fragment;
			switch (position) {
			case 0:
				return new LPFFragment();
			case 1:
				return new RotationMatrixFragment();
			case 2:
				return new AccToVelocityFragment();
			default:
				return new Fragment();
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
//				return getString(R.string.title_section1).toUpperCase();
				return "LPF";
			case 1:
//				return getString(R.string.title_section2).toUpperCase();
				return "RM";
			case 2:
				return "AV";
			default:
				return null;
			}
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
			textView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return textView;
		}
	}

}

class LPFFragment extends Fragment {
	LowPassFilterView _lpfView;
	View _lpfLayout;

	private void initWidgets() {
		_lpfView = (LowPassFilterView) _lpfLayout
				.findViewById(R.id.lowPassFilterView);
		CheckBox noFilterCheckBox = (CheckBox) _lpfLayout
				.findViewById(R.id.checkBoxNoFilter);
		CheckBox lpfCheckBox = (CheckBox) _lpfLayout
				.findViewById(R.id.checkBoxLPFiltered);
		noFilterCheckBox.setChecked(true);
		lpfCheckBox.setChecked(true);
		noFilterCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						_lpfView.setNoFilterCurveEnabled(isChecked);
					}
				});
		lpfCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				_lpfView.setLPFCurveEnabled(isChecked);
			}
		});
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// return super.onCreateView(inflater, container, savedInstanceState);
		_lpfLayout = inflater.inflate(R.layout.fragment_low_pass_filter,
				container, false);
		initWidgets();
		
		return _lpfLayout;
	}

	@Override
	public void onPause() {
		super.onPause();
		_lpfView.stop();
	}

	@Override
	public void onResume() {
		super.onResume();
		_lpfView.start();
	}

} //LPFFragment

class RotationMatrixFragment extends Fragment{
	RotationMatrixView _rmView;
	View _rmLayout;
//	SensorManager _sm;
	
	private void initWidgets() {
		//-----------------------------------
		_rmView=(RotationMatrixView) _rmLayout.findViewById(R.id.rotationMatrixView);
		CheckBox bfCheckBox=(CheckBox) _rmLayout.findViewById(R.id.checkBoxBodyFrame);
		CheckBox wfCheckBox=(CheckBox) _rmLayout.findViewById(R.id.checkBoxWorldFrame);
		bfCheckBox.setChecked(true);
		wfCheckBox.setChecked(true);
		bfCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_rmView.setBodyFrameCurveEnabled(isChecked);
			}
		});
		wfCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_rmView.setWorldFrameCurveEnabled(isChecked);
			}
		});
	}	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//		return super.onCreateView(inflater, container, savedInstanceState);
		_rmLayout=inflater.inflate(R.layout.fragment_rotation_matrix, container, false);
		initWidgets();
		
//		_sm=(SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		
		return _rmLayout;
	}
	@Override
	public void onPause() {
		super.onPause();
		_rmView.stop();
	}

	@Override
	public void onResume() {
		super.onResume();
//		int rate=1000*1000/40;
//		_sm.registerListener(_rmView, _sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), rate);
//		_sm.registerListener(_rmView, _sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), rate);
		_rmView.start();
	}
	
} //RotationMatrixFragment


class AccToVelocityFragment extends Fragment{
	View _layout;
	AccToVelocityView _avView;
	
	void initWidgets(){
		_avView=(AccToVelocityView) _layout.findViewById(R.id.accToVelocityView);
		CheckBox checkBoxAcc=(CheckBox) _layout.findViewById(R.id.checkBoxLinearAcc);
		CheckBox checkBoxVel=(CheckBox) _layout.findViewById(R.id.checkBoxVelocity);
		checkBoxAcc.setChecked(true);
		checkBoxVel.setChecked(true);
		checkBoxAcc.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_avView.setAccCurveEnabled(isChecked);
			}
		});
		checkBoxVel.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_avView.setVelocityCurveEnabled(isChecked);
			}
		});
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//		return super.onCreateView(inflater, container, savedInstanceState);
		_layout=inflater.inflate(R.layout.fragment_acc_to_velocity, container, false);
		initWidgets();
		return _layout;
	}

	@Override
	public void onPause() {
		super.onPause();
		_avView.stop();
	}

	@Override
	public void onResume() {
		super.onResume();
		_avView.start();
	}
} //AccToVelocityFragment