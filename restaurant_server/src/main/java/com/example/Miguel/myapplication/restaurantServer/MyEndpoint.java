/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.example.Miguel.myapplication.restaurantServer;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "myApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "restaurantServer.myapplication.Miguel.example.com",
                ownerName = "restaurantServer.myapplication.Miguel.example.com",
                packagePath = ""
        )
)
public class MyEndpoint {

    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "sayHi")
    public GenerateQRCode sayHi(@Named("name") String name) {
        GenerateQRCode response = new GenerateQRCode();

        return response;
    }

    @ApiMethod(name = "sendQRCode")
    public GenerateQRCode sendQRCode() {
        GenerateQRCode response = new GenerateQRCode();
        response.sendToTable();
        return response;
    }

    @ApiMethod(name = "refreshQR")
    public GenerateQRCode refreshQR(@Named("name") int id) {
        GenerateQRCode response = new GenerateQRCode();
        response.sentQR(id);
        return response;
    }

    @ApiMethod(name = "receiveQRCode")
    public GenerateQRCode verifyQRCode(@Named("name") String name) {
        GenerateQRCode response = new GenerateQRCode();
        response.verifyQR(name);
        return response;
    }

}

