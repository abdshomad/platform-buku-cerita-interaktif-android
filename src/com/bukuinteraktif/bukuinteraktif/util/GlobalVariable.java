package com.bukuinteraktif.bukuinteraktif.util;

import java.util.ArrayList;

import com.bukuinteraktif.bukuinteraktif.BookEntity;

public class GlobalVariable {
	static public String ROOT_FOLDER = "Koleksi Buku";
	static public ArrayList<BookEntity> allBookList = null;
	static public BookEntity selectedBook;
	
	public static String zeroString(int id) {
		String string = "000" + id;
		string = string.substring(string.length() - 3);
		return string;
	}
}
