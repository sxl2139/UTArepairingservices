package edu.uta.utarepairingservices;

// Use the same Activity for all three users. I know the GUI is different. You can programmatically hide and show elements

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import android.widget.ArrayAdapter;
import android.os.StrictMode;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class ViewProfileActivity extends Activity {
    String UserId;

    ArrayAdapter<String> adapter;
    int RoleId;
    String address;
    InputStream is=null;
    String line=null;
    String result=null;
    String data;
    String netId;
    UserInfo user;
    Button btnBook;
    int spID;
    String s;

    String name, gender, email, street, city;
    int contact, houseNo, postal;

    TextView tvNameValue, tvGenderValue, tvContactValue, tvEmailValue, tvHouseNoValue, tvStreetValue, tvPostalCodeValue, tvCityValue, tvViewProfileText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_profile);

        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));

        tvNameValue = (TextView) findViewById(R.id.tvNameValue);
        tvGenderValue = (TextView) findViewById(R.id.tvGenderValue);
        tvContactValue = (TextView) findViewById(R.id.tvContactValue);
        tvEmailValue = (TextView) findViewById(R.id.tvEmailValue);
        tvHouseNoValue = (TextView) findViewById(R.id.tvHouseNoValue);
        tvStreetValue = (TextView) findViewById(R.id.tvStreetValue);
        tvPostalCodeValue = (TextView) findViewById(R.id.tvPostalCodeValue);
        tvCityValue = (TextView) findViewById(R.id.tvCityValue);
        tvViewProfileText = (TextView) findViewById(R.id.tvViewProfileText);
        btnBook = (Button) findViewById(R.id.btnBookAppointment);

        UserInfo ui = new UserInfo();
        s = getIntent().getStringExtra("view");
        if(s.equals("view_sp")) {
            tvViewProfileText.setText("Service Provider Profile");
            address = "http://kedarnadkarny.com/utarepair/view_service_provider_profile.php";
            spID = ui.getSpID();
        }
        else if(s.equals("view_cu")) {
            address = "http://kedarnadkarny.com/utarepair/view_customer_profile.php";
            netId = ui.getUta_net_id();
            btnBook.setVisibility(View.GONE);
            RoleId = Integer.parseInt(UserInfo.getRoleId());
            netId = UserInfo.getUta_net_id();
        }
        else {
            Toast.makeText(getBaseContext(), "SOME ERROR", Toast.LENGTH_LONG).show();
        }

        getData();
        setProfileData();

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(ViewProfileActivity.this, "BOOKED", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ViewProfileActivity.this, BookAppointmentActivity.class));
            }
        });
    }

    private  void getData(){

        try {
            URL url = new URL(address);
            HttpURLConnection con=(HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            OutputStream outputStream = con.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter((new OutputStreamWriter(outputStream,"UTF-8")));
            String data_string = "";
            if(s.equals("view_cu")) {
                data_string = URLEncoder.encode("UserId", "UTF-8") + "=" + URLEncoder.encode(netId, "UTF-8");
            }
            else if(s.equals("view_sp")) {
                data_string = URLEncoder.encode("sp_id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(spID), "UTF-8");
            }


            bufferedWriter.write(data_string);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            is=new BufferedInputStream(con.getInputStream());
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            StringBuilder sb=new StringBuilder();

            while((line=br.readLine())!=null){
                sb.append(line +"\n");

            }
            is.close();
            result=sb.toString();
        }
        catch (Exception e){
            e.printStackTrace();

        }

        //parse json data
        try{
            JSONArray ja=new JSONArray(result);
            JSONObject jo;
            data=new String();

            jo=ja.getJSONObject(0);

            name = jo.getString("firstname") + " " + jo.getString("lastname");
            gender = jo.getString("gender");
            email = jo.getString("email");
            street = jo.getString("street");
            city = jo.getString("city");
            contact = Integer.parseInt(jo.getString("contact"));
            houseNo = Integer.parseInt(jo.getString("house_no"));
            postal = Integer.parseInt(jo.getString("postal_code"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setProfileData() {
        tvNameValue.setText(name);
        tvGenderValue.setText(gender);
        tvContactValue.setText(""+contact);
        tvEmailValue.setText(email);
        tvHouseNoValue.setText(""+houseNo);
        tvStreetValue.setText(street);
        tvPostalCodeValue.setText(""+postal);
        tvCityValue.setText(city);
    }
}