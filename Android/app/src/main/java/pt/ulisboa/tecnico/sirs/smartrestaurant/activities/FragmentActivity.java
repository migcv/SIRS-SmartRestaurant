package pt.ulisboa.tecnico.sirs.smartrestaurant.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import pt.ulisboa.tecnico.sirs.smartrestaurant.R;
import pt.ulisboa.tecnico.sirs.smartrestaurant.core.Customer;
import pt.ulisboa.tecnico.sirs.smartrestaurant.core.NaiveTrustManager;
import pt.ulisboa.tecnico.sirs.smartrestaurant.fragments.*;

public class FragmentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Creating Thread!!");
                Thread cThread = new Thread(new FragmentActivity.ClientThread());
                cThread.start();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getFragmentManager();
        Fragment menuFragment = new MenuFragment();
        fm.popBackStack(BurgersFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fm.beginTransaction().add(R.id.content_frame, menuFragment, "CONVERTER_FRAGMENT").commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private static SSLSocketFactory sslSocketFactory;
    /**
     * Returns a SSL Factory instance that accepts all server certificates.
     * <pre>SSLSocket sock =
     *     (SSLSocket) getSocketFactory.createSocket ( host, 443 ); </pre>
     * @return  An SSL-specific socket factory.
     **/
    public SSLSocketFactory getSocketFactory()
    {
        if ( sslSocketFactory == null ) {
            try {
                TrustManager[] tm = new TrustManager[] { new NaiveTrustManager(this) };
                SSLContext context = SSLContext.getInstance ("SSL");
                context.init( new KeyManager[0], tm, new SecureRandom( ) );

                sslSocketFactory = (SSLSocketFactory) context.getSocketFactory ();

            } catch (KeyManagementException e) {
                //log.error ("No SSL algorithm support: " + e.getMessage(), e);
            } catch (NoSuchAlgorithmException e) {
                //log.error ("Exception when setting up the Naive key management.", e);
            }
        }
        return sslSocketFactory;
    }

    public class ClientThread implements Runnable {

        public void run() {
            try {
                System.out.println("Connecting!!!");
                InetAddress serverAddr = InetAddress.getByName("185.43.210.233"); //MANEL
                //InetAddress serverAddr = InetAddress.getByName("192.168.1.66"); //CASA

                SSLSocketFactory sslSocketFactory = (SSLSocketFactory) getSocketFactory();
                SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverAddr, 10002);

                // Set protocol (we want TLSv1.2)
                String[] protocols = socket.getEnabledProtocols(); // gets available protocols

                //Socket socket = new Socket(serverAddr, 10002);

                System.out.println("Connected!!!");
                connected = true;
                ArrayMap<String, Integer> order = Customer.getOrder().getOrders();
                DataOutputStream oos = null;

                //With the order the customer send the customerID
                String o = Customer.getCustomerID() + " ";

                try {
                    //Send Order
                    for (Map.Entry<String,Integer> entry : order.entrySet()) {
                        String food = entry.getKey();
                        int quantity = entry.getValue();
                        o += food + " " + quantity + " ";
                    }
                    oos = new DataOutputStream(socket.getOutputStream());
                    oos.writeBytes(o);
                    oos.flush();
                } catch (Exception e) {
                    Log.e("ClientActivity", "S: Error", e);
                }
                socket.close();
                System.out.println("Socket closed!!!!");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }
}
