package com.bukuinteraktif.bukuinteraktif;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.bukuinteraktif.bukuinteraktif.util.GlobalVariable;

import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener , OnPageChangeListener{

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	private String tag = "bukuinteraktif";

	ViewPager mViewPager;
	//BookEntity selectedBook = new BookEntity("BUKU 1", 3);
	boolean isAudioOn = true;
	ImageView sound;
	MediaPlayer mediaPlayer;
	boolean play = false;
	int currentPage = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Window window = this.getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		window.requestFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter();

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(this);
		sound = (ImageView) findViewById(R.id.imageAudio);
		sound.setOnClickListener(this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		//playAudio(1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// Show 3 total pages.
			return GlobalVariable.selectedBook.pageNumber;
		}

		@Override
		public Object instantiateItem(View collection, int position) {      
			View customView = MainActivity.this.getLayoutInflater().inflate(R.layout.content, null);
			ImageView pageImage = (ImageView) customView.findViewById(R.id.pageImage);
			TextView pageNarasi = (TextView) customView.findViewById(R.id.pageText);
			File file = Environment.getExternalStorageDirectory();
			File folder = new File(file, GlobalVariable.ROOT_FOLDER+"/"+GlobalVariable.selectedBook.title);

			String pagesrc = folder.getAbsolutePath() + "/" + GlobalVariable.zeroString(position+1);
			Log.w(tag, "IMAGE SRC = "+pagesrc);
			pageImage.setImageBitmap(BitmapFactory.decodeFile(pagesrc+".jpg"));

			File f = new File(pagesrc+".txt");
	        StringBuilder text = new StringBuilder();
	        try {
	            BufferedReader br = new BufferedReader(new FileReader(f));
	            String line;
	            while ((line = br.readLine()) != null) {
	                text.append(line);
	                text.append('\n');
	            }
	        }
	        catch (IOException e) {
	        	e.printStackTrace();
	        }
			pageNarasi.setText(text);
			((ViewPager) collection).addView(customView,0);
			return customView;
		}

		@Override
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "Halaman "+(position+1)+"/"+GlobalVariable.selectedBook.pageNumber;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view==((View)object);
		}
	}

	public void toggleSound(){
		Toast.makeText(this, "AUDIO CLICKED", Toast.LENGTH_SHORT).show();
		isAudioOn = !isAudioOn;
		if(isAudioOn){
			sound.setImageResource(R.drawable.ic_lock_ringer_on);
			playAudio(currentPage+1);
		}else{
			sound.setImageResource(R.drawable.ic_lock_ringer_off);
			if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.stop();
		}
	}

	@Override
	public void onClick(View v) {
		if(v.equals(sound)){
			toggleSound();
		}
	}

	public void playAudio(int audioNumber) {
		if (isAudioOn) {
			try {
				String state = Environment.getExternalStorageState();
				if (Environment.MEDIA_MOUNTED.equals(state)) {
					File file = Environment.getExternalStorageDirectory();
					File folder = new File(file, GlobalVariable.ROOT_FOLDER+"/"+GlobalVariable.selectedBook.title);
					if (mediaPlayer != null) {
						if (!mediaPlayer.isPlaying()) {
							mediaPlayer.release();
							//mediaPlayer.reset();
						}
					}

					mediaPlayer = new MediaPlayer();
					Log.w(tag,"PLAY AUDIO  = "+folder.getAbsolutePath() + "/" + GlobalVariable.zeroString(audioNumber)  + ".ogg");
					mediaPlayer.setDataSource(folder.getAbsolutePath() + "/" + GlobalVariable.zeroString(audioNumber)  + ".ogg");
					mediaPlayer.prepare();
					mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						public void onCompletion(MediaPlayer mp) {
							nextPage();
						}
					});
					play = true;
					mediaPlayer.start();					
				}
			} catch (IllegalArgumentException e) {
				play = false;
				e.printStackTrace();
			} catch (IllegalStateException e) {
				play = false;
				e.printStackTrace();
			} catch (IOException e) {
				play = false;
				e.printStackTrace();
			}
		}
	}
	
	public void nextPage(){
		//Toast.makeText(this, "KUDUNA NEXT PAGE", Toast.LENGTH_SHORT).show();
		mViewPager.setCurrentItem(currentPage+1);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		
	}

	@Override
	public void onPageSelected(int position) {
		//Toast.makeText(this, "VIEW PAGER CHANGED CURENT = "+position, Toast.LENGTH_SHORT).show();
		currentPage = position;
		playAudio(currentPage+1);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.stop();
	}
}
