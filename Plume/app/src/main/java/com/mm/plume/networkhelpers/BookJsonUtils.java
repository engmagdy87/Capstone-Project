package com.mm.plume.networkhelpers;

import android.util.Log;

import com.mm.plume.MainActivity;
import com.mm.plume.javaclasses.BookInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by MM on 12/24/2017.
 */

public class BookJsonUtils {
    public static ArrayList<BookInfo> getSimpleBookStringsFromJson(String bookJsonStr)
            throws JSONException {


        JSONObject bookJson = new JSONObject(bookJsonStr);
        ArrayList<BookInfo> booksInfo;
        if (bookJson.getInt("totalItems") == 0) {
            booksInfo = new ArrayList<BookInfo>(0);
        } else {
            JSONArray bookArray = bookJson.getJSONArray("items");

            booksInfo = new ArrayList<BookInfo>(bookArray.length());
            for (int i = 0; i < bookArray.length(); i++) {
                BookInfo bookInfo = new BookInfo();

                JSONObject volumeInfoArray = bookArray.getJSONObject(i).getJSONObject("volumeInfo");
                if (!volumeInfoArray.isNull("industryIdentifiers")) {
                    if (volumeInfoArray.getJSONArray("industryIdentifiers").length() == 2) {
                        bookInfo.setId(String.valueOf(bookArray.getJSONObject(i).getString("id")));
                        bookInfo.setTitle(String.valueOf(volumeInfoArray.getString("title")));

                        String[] authors;
                        if (volumeInfoArray.isNull("authors")) {
                            authors = new String[1];
                            authors[0] = "N/A";
                            bookInfo.setAuthors(authors);
                        } else {

                            JSONArray authorsJsonArray = volumeInfoArray.getJSONArray("authors");
                            authors = new String[authorsJsonArray.length()];
                            for (int j = 0; j < authorsJsonArray.length(); j++) {
                                authors[j] = authorsJsonArray.getString(j);
                            }
                            bookInfo.setAuthors(authors);
                        }
                        if (volumeInfoArray.isNull("industryIdentifiers")) {
                            bookInfo.setIsbn("N/A");
                        } else {
                            if (volumeInfoArray.getJSONArray("industryIdentifiers").getJSONObject(0).getString("type").equals("ISBN_13")) {
                                bookInfo.setIsbn(volumeInfoArray.getJSONArray("industryIdentifiers").getJSONObject(0).getString("identifier"));
                            } else {
                                bookInfo.setIsbn(volumeInfoArray.getJSONArray("industryIdentifiers").getJSONObject(1).getString("identifier"));
                            }

                        }
                        if (volumeInfoArray.isNull("publisher"))
                            bookInfo.setPublisher("N/A");
                        else
                            bookInfo.setPublisher(String.valueOf(volumeInfoArray.getString("publisher")));

                        if (volumeInfoArray.isNull("publishedDate"))
                            bookInfo.setPublishedDate("N/A");
                        else
                            bookInfo.setPublishedDate(String.valueOf(volumeInfoArray.getString("publishedDate")));

                        if (volumeInfoArray.isNull("description"))
                            bookInfo.setDescription("N/A");
                        else
                            bookInfo.setDescription(String.valueOf(volumeInfoArray.getString("description")));

                        String[] categories;
                        if (volumeInfoArray.isNull("categories")) {
                            categories = new String[1];
                            categories[0] = "N/A";
                            bookInfo.setCategories(categories);
                        } else {
                            JSONArray categoriesJsonArray = volumeInfoArray.getJSONArray("categories");
                            categories = new String[categoriesJsonArray.length()];
                            for (int k = 0; k < categoriesJsonArray.length(); k++) {
                                categories[k] = categoriesJsonArray.getString(k);
                            }
                            bookInfo.setCategories(categories);
                        }

                        if (volumeInfoArray.isNull("imageLinks")) {
                            bookInfo.setThumbnail("N/A");
                        } else {
                            bookInfo.setThumbnail(String.valueOf(volumeInfoArray.getJSONObject("imageLinks").getString("thumbnail")));
                        }
                        if (volumeInfoArray.isNull("previewLink")) {
                            bookInfo.setShareLink("N/A");
                        } else {
                            bookInfo.setShareLink(String.valueOf(volumeInfoArray.getString("previewLink")));
                        }
                        booksInfo.add(bookInfo);
                    }
                }
            }
        }
        return booksInfo;
    }
}
