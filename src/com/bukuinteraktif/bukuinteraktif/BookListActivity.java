package com.bukuinteraktif.bukuinteraktif;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bukuinteraktif.bukuinteraktif.util.GlobalVariable;

public class BookListActivity extends Activity implements OnClickListener{

	private String tag ="BOOK LIST" ;
	GridView grid;
	BookItemAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Window window = this.getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		window.requestFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.book_list_layout);
		grid = (GridView) findViewById(R.id.gridView1);
		adapter = new BookItemAdapter(this);
		grid.setAdapter(adapter);
		generateGallery();
	}
	
	public class BookItemAdapter extends BaseAdapter 
	{
		private Context context;
		ArrayList<BookEntity> bookList = new ArrayList<BookEntity>();


		public BookItemAdapter(Context c) 
		{
			context = c;        
		}

		public int getCount() {
			return bookList.size();
		}

		public Object getItem(int position) {
			return position;
		}            

		public long getItemId(int position) {
			return position;
		}

		//---returns an ImageView view---
		public View getView(int position, View convertView, ViewGroup parent) {
			BookEntity bookItem = bookList.get(position);

			ImageView icon;
			TextView title;
			TextView desc;
			TextView page;
			if(convertView == null){
				convertView = getLayoutInflater().inflate(R.layout.book_list_item, null);
				convertView.setOnClickListener(BookListActivity.this);
			}
			icon = (ImageView) convertView.findViewById(R.id.bookIcon);
			title = (TextView) convertView.findViewById(R.id.bookTitle);
			desc = (TextView) convertView.findViewById(R.id.bookDesc);
			page = (TextView) convertView.findViewById(R.id.bookPage);
			
			File file = Environment.getExternalStorageDirectory();
			File folder = new File(file, GlobalVariable.ROOT_FOLDER+"/"+bookItem.title);
			Log.w(tag, "FOLDER = "+folder.getAbsolutePath() + "/icon.jpg");
			icon.setImageBitmap(BitmapFactory.decodeFile(folder.getAbsolutePath() + "/icon.jpg"));
			title.setText(bookItem.title);
			page.setText("Total Page: "+bookItem.pageNumber);
			convertView.setTag(bookItem);
			return convertView;
		}
	}

	public void generateGallery(){
		BookEntity bookItem = null;
		File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+GlobalVariable.ROOT_FOLDER);
		String[] allFileList = dir.list();
		java.util.Arrays.sort(allFileList, java.text.Collator.getInstance());
		File folder;
		FileFilter directoryFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".jpg");
			}
		};
		for (int i = 0; i < allFileList.length; i++) {
			Log.w(tag, "GENERATE FILE : "+dir.getAbsolutePath()+"/"+allFileList[i]);
			bookItem = new BookEntity(allFileList[i], 0);
			folder = new File(dir.getAbsolutePath(),allFileList[i]);
			bookItem.pageNumber = folder.listFiles(directoryFilter).length-1;
			adapter.bookList.add(bookItem);
		}
		if(adapter.bookList.size()>0){
			GlobalVariable.allBookList = adapter.bookList;
		}
		adapter.notifyDataSetChanged();
		grid.invalidate();
	}

	@Override
	public void onClick(View v) {
		if(v.getTag() instanceof BookEntity){
			GlobalVariable.selectedBook = (BookEntity) v.getTag(); 
			startActivity(new Intent(this, MainActivity.class));
		}
	}

}
