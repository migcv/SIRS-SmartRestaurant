package pt.ulisboa.tecnico.sirs.smartrestaurant.activities;

import android.app.Activity;
import android.app.Application;

import com.example.miguel.myapplication.restaurantserver.myApi.MyApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Created by dharuqueshil on 16/11/2016.
 */

public class RestaurantEndpoint extends Application {

    private static MyApi myApiService;

    @Override
    public void onCreate() {
        super.onCreate();
        buildCloudEndpoints();
    }

    private void buildCloudEndpoints() {
        MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                .setRootUrl("https://restauranteserver.appspot.com/_ah/api/");

                /*MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });*/
    }

    public static MyApi getApiService() {
        return myApiService;
    }

}