package ro.pub.cs.systems.eim.practicaltest02v1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class PracticalTest02v1MainActivity extends AppCompatActivity {
    private static final String TAG = "AutocompleteApp";
    private EditText etQuery;
    private Button btnSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practical_test02v1_main);
        etQuery = findViewById(R.id.etQuery);
        btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(v -> {
            String query = etQuery.getText().toString();
            if (!query.isEmpty()) {
                fetchSuggestions(query);
            }
        });

        Button openSecondActivityButton = findViewById(R.id.btnOpenSecondActivity);

        // Setăm un click listener pentru buton
        openSecondActivityButton.setOnClickListener(v -> {
            // Creăm un intent pentru a deschide a doua activitate
            Intent intent = new Intent(PracticalTest02v1MainActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void fetchSuggestions(String query) {
        new Thread(() -> {
            String url = "https://www.google.com/complete/search?client=chrome&q=" + query;

            try {
                java.net.URL urlObj = new java.net.URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) urlObj.openConnection();
                urlConnection.setRequestMethod("GET");

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

//                    Log.d(TAG, "Response: " + response.toString());
                    extractThirdSuggestion(response.toString());
                } else {
                    Log.e(TAG, "Failed to fetch data. HTTP Code: " + responseCode);
                }
            } catch (IOException e) {
                Log.e(TAG, "Error during HTTP request", e);
            }
        }).start();
    }
    private void extractThirdSuggestion(String response) {
        try {
            int startIndex = response.indexOf('[');
            int endIndex = response.indexOf(']', startIndex);

            if (startIndex != -1 && endIndex != -1) {
                String suggestionsPart = response.substring(startIndex + 1, endIndex);

                String[] suggestions = suggestionsPart.split(",");

                if (suggestions.length >= 3) {

                    String thirdSuggestion = suggestions[2].trim().replace("\"", "");

                    Log.d(TAG, "A treia sugestie: " + thirdSuggestion);
                } else {
                    Log.e(TAG, "Nu există suficiente sugestii în răspuns.");
                }
            } else {
                Log.e(TAG, "Nu s-a găsit lista de sugestii în răspuns.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing response", e);
        }
    }
}