package pt.ulisboa.tecnico.sirs.smartrestaurant.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import pt.ulisboa.tecnico.sirs.smartrestaurant.R;

public class Food extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

    }

    public void FoodMenu(View view) {
        Intent dd = new Intent(this, DrinksDeserts.class);
        startActivity(dd);
    }
}
