package baanhem.quizmakerforteacher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import helper.PHPUrl;
import item.AccountItem;

public class MainActivity extends Activity
{
    Button btnDangnhap;
    TextView tvDangKy;
    EditText edtTaikhoan, edtMatkhau;
    JSONObject jobj = null;
    String json = "";
    JSONArray JArr = null;
    ArrayList<AccountItem> itemList;
    InputStream is = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_main);

        tvDangKy = (TextView)findViewById(R.id.tvDangKy);
        btnDangnhap = (Button)findViewById(R.id.btnDangnhap);

        edtTaikhoan = (EditText)findViewById(R.id.edtTaikhoan);
        edtTaikhoan.setTypeface(Typeface.DEFAULT);
        edtMatkhau = (EditText)findViewById(R.id.edtMatkhau);
        edtMatkhau.setTypeface(Typeface.DEFAULT);

        GetAccount task = new GetAccount();
        task.execute();

        edtMatkhau.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    Boolean kiemtradangnhap = false;
                    int vitri = 0;
                    String taikhoan = edtTaikhoan.getText().toString();
                    String matkhau = edtMatkhau.getText().toString();
                    for (int i = 0; i < itemList.size(); i++)
                    {
                        if(taikhoan.equals(itemList.get(i).Username) && matkhau.equals(itemList.get(i).Password))
                        {
                            kiemtradangnhap = true;
                            vitri = i;
                            Toast.makeText(MainActivity.this, "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                            break;
                        }
                    }

                    if (kiemtradangnhap == false)
                    {
                        Toast.makeText(MainActivity.this, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        if (itemList.get(vitri).ChucVu == 0)
                        {
                            Intent i = new Intent(MainActivity.this, QuanLyGVActivity.class);
                            Bundle b = new Bundle();
                            b.putString("Username", itemList.get(vitri).Username);
                            b.putString("Ho", itemList.get(vitri).Ho);
                            b.putString("Ten", itemList.get(vitri).Ten);
                            i.putExtras(b);
                            startActivity(i);
                        }
                        else if (itemList.get(vitri).ChucVu == 1)
                        {
                            Intent i = new Intent(MainActivity.this, QuanLySVActivity.class);
                            Bundle b = new Bundle();
                            b.putString("Username", itemList.get(vitri).Username);
                            b.putString("Ho", itemList.get(vitri).Ho);
                            b.putString("Ten", itemList.get(vitri).Ten);
                            i.putExtras(b);
                            startActivity(i);
                        }
                    }
                    return true;
                }
                else
                    return false;
            }
        });

        tvDangKy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(MainActivity.this, DangKyActivity.class);
                startActivity(i);
            }
        });

        btnDangnhap.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Boolean kiemtradangnhap = false;
                int vitri = 0;
                String taikhoan = edtTaikhoan.getText().toString();
                String matkhau = edtMatkhau.getText().toString();
                for (int i = 0; i < itemList.size(); i++)
                {
                    if(taikhoan.equals(itemList.get(i).Username) && matkhau.equals(itemList.get(i).Password))
                    {
                        kiemtradangnhap = true;
                        vitri = i;
                        break;
                    }
                }

                if (kiemtradangnhap == false)
                {
                    Toast.makeText(MainActivity.this, "Đăng nhập Thất bại", Toast.LENGTH_LONG).show();
                }
                else
                {
                    if (itemList.get(vitri).ChucVu == 0)
                    {
                        Intent i = new Intent(MainActivity.this, QuanLyGVActivity.class);
                        Bundle b = new Bundle();
                        b.putString("Username", itemList.get(vitri).Username);
                        b.putString("Ho", itemList.get(vitri).Ho);
                        b.putString("Ten", itemList.get(vitri).Ten);
                        i.putExtras(b);
                        startActivity(i);
                    }
                    else if (itemList.get(vitri).ChucVu == 1)
                    {
                        Intent i = new Intent(MainActivity.this, QuanLySVActivity.class);
                        Bundle b = new Bundle();
                        b.putString("Username", itemList.get(vitri).Username);
                        b.putString("Ho", itemList.get(vitri).Ho);
                        b.putString("Ten", itemList.get(vitri).Ten);
                        i.putExtras(b);
                        startActivity(i);
                    }
                }
            }
        });
    }

    public class GetAccount extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            itemList = new ArrayList<>();
            JSONObject json_object = getJsonFromUrl(PHPUrl.getAllAccount, "");
            try
            {
                JArr = new JSONArray();
                JArr = json_object.getJSONArray("All_Account");
                for(int i=0; i < JArr.length(); i++)
                {
                    JSONObject one_account = JArr.getJSONObject(i);

                    AccountItem oc = new AccountItem();
                    oc.Username = one_account.getString("Username");
                    oc.Password = one_account.getString("Password");
                    oc.Ho = one_account.getString("Ho");
                    oc.Ten = one_account.getString("Ten");
                    oc.ChucVu = one_account.getInt("ChucVu");

                    itemList.add(oc);
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