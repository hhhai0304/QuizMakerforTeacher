package baanhem.quizmakerforteacher;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import helper.DatabaseHelper;
import helper.PHPUrl;
import item.AccountItem;
import item.MonhocItem;

public class SplashScreenActivity extends Activity
{
    JSONObject jobj = null;
    String json = "";
    JSONArray JArr = null;
    InputStream is = null;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_splashscreen);

        db = new DatabaseHelper(SplashScreenActivity.this);
        db.xoaGiangvien();
        db.xoaMonhoc();

        GetData task = new GetData();
        task.execute();
    }

    public class GetData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            JSONObject json_object = getJsonFromUrl(PHPUrl.getAllAccount, "");
            try
            {
                JArr = new JSONArray();
                JArr = json_object.getJSONArray("All_Account");
                for(int i=0; i < JArr.length(); i++)
                {
                    JSONObject gv = JArr.getJSONObject(i);
                    if (gv.getInt("ChucVu") == 0)
                    {
                        AccountItem ai = new AccountItem();
                        ai.Username = gv.getString("Username");
                        ai.Ho = gv.getString("Ho");
                        ai.Ten = gv.getString("Ten");

                        db.themGiangVien(ai);
                    }
                }
            }
            catch(Exception e)
            {
                Log.i("Lỗi Kết nối", e.toString());
            }

            JSONObject json_objectMon = getJsonFromUrl(PHPUrl.getAllMon, "");
            try
            {
                JArr = new JSONArray();
                JArr = json_objectMon.getJSONArray("All_Mon");
                for(int i=0; i < JArr.length(); i++)
                {
                    JSONObject gv = JArr.getJSONObject(i);
                    MonhocItem mi = new MonhocItem();
                    mi.MaMon = gv.getString("MaMon");
                    mi.TenMon = gv.getString("TenMon");

                    db.themMon(mi);
                }
            }
            catch(Exception e)
            {
                Log.i("Lỗi Kết nối", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    public JSONObject getJsonFromUrl(String url, String CMD)
    {
        try
        {
            // Khởi tạo
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            //Gửi thông tin lệnh lên Server
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("CMD", CMD));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


            // Thực thi, lấy nội dung về
            HttpResponse http_reaponse = client.execute(httppost);
            HttpEntity http_entity = http_reaponse.getEntity();

            is = http_entity.getContent();

            // Đọc dữ liệu
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"),8 );
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line=reader.readLine())!=null)
            {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString(); // Đọc StringBuilder vào chuỗi
            Pattern p = Pattern.compile("^.*?\\((.*?)\\);$", Pattern.DOTALL);
            Matcher m = p.matcher(json);
            if (m.matches())
            {
                json = m.group(1);
                @SuppressWarnings("unused")
                JSONObject jo = new JSONObject(json);
                jobj = new JSONObject(json); // Đưa chuỗi vào đối tượng JSon
            }
            else
            {
                jobj = new JSONObject(json); // Đưa chuỗi vào đối tượng JSon
            }

        }
        catch(Exception e)
        {
            Log.i("Lỗi Json",e.toString());
        }
        return jobj;
    }
}