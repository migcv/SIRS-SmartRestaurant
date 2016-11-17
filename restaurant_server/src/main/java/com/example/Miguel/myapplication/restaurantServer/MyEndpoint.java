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
    GenerateQRCode response = new GenerateQRCode();

    @ApiMethod(name = "sayHi")
    public GenerateQRCode sayHi(@Named("name") String name) {
        return response;
    }

    @ApiMethod(name = "sendQRCode")
    public GenerateQRCode sendQRCode() {
        response.sendToTable();
        return response;
    }

    @ApiMethod(name = "verifyQRCode")
    public Response verifyQRCode(@Named("name") String name) {
        Response res = new Response();
        res.result = response.verifyQR(name);

        return res;
    }


}

