package org.practischool;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {
    final static String FILENAME = "data.json";

    final private static Moshi moshi = new Moshi.Builder().build();
    final static JsonAdapter<Book> adapter = moshi.adapter(Book.class);
    static Book book = new Book();

    static boolean fetchBookFromDouban(final String isbn) {
        try {
            URL url = new URL("https://isbn.qiaohaoforever.cn/" + isbn);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            // read the http response
            while (true) {
                inputLine = in.readLine();
                if (inputLine == null) {
                    break;
                }
                response.append(inputLine);
            }
            in.close();

            book = adapter.fromJson(response.toString());
            return true;
        } catch (IOException e) {
            book = null;
            return false;
        }
    }

    static String getAuthor() {
        return book.author;
    }

    static String getPrice() {
        return book.price;
    }

    static String getISBN() {
        return book.isbn;
    }

    static String getPublisher() {
        return book.publisher;
    }

    static String getTitle() {
        return book.title;
    }
}

class Book {
    String author;
    String publisher;
    String price;
    String isbn;
    String imageURL;
    double rate;
    String title;

    @Json(name = "alt")
    String url;
}

