package baanhem.quizmakerforteacher;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import helper.PHPUrl;

public class DangKyActivity extends Activity
{
    EditText edtDKTaikhoan, edtDKMatkhau, edtDKHo, edtDKTen;
    RadioButton rbGiangvien, rbSinhvien;
    Button btnDKDangky;

    JSONObject jobj = null;
    String json = "";
    InputStream is = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        overridePendingTransition(R.anim.right_in_start, R.anim.right_in_end);
        setContentView(R.layout.activity_dangky);

        edtDKTaikhoan = (EditText)findViewById(R.id.edtDKTaikhoan);
        edtDKMatkhau = (EditText)findViewById(R.id.edtDKMatkhau);
        edtDKMatkhau.setTypeface(Typeface.DEFAULT);
        edtDKHo = (EditText)findViewById(R.id.edtDKHo);
        edtDKTen = (EditText)findViewById(R.id.edtDKTen);

        rbGiangvien = (RadioButton)findViewById(R.id.rbGiangvien);
        rbSinhvien = (RadioButton)findViewById(R.id.rbSinhvien);

        btnDKDangky = (Button)findViewById(R.id.btnDKDangky);

        btnDKDangky.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String ChucVu = "";
                if (rbGiangvien.isChecked())
                {
                    ChucVu = 0 + "";
                }
                else
                {
                    ChucVu = 1 + "";
                }
                DangKy task = new DangKy(edtDKTaikhoan.getText().toString(), edtDKMatkhau.getText().toString(), edtDKHo.getText().toString(), edtDKTen.getText().toString(), ChucVu);
                task.execute();
                Toast.makeText(DangKyActivity.this, "Đăng ký Thành công.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public class DangKy extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog progress;
        String Username, Password, Ho, Ten, ChucVu;

        public DangKy(String Username, String Password, String Ho, String Ten, String ChucVu)
        {
            this.Username = Username;
            this.Password = Password;
            this.Ho = Ho;
            this.Ten = Ten;
            this.ChucVu = ChucVu;
        }
        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(DangKyActivity.this);
            progress.setMessage("Đang Đăng ký Tài khoản");
            progress.setIndeterminate(false);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            JSONObject json_object = getJsonFromUrl(PHPUrl.DangKy, "", Username, Password, Ho, Ten, ChucVu);
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            progress.dismiss();
            super.onPostExecute(result);
        }
    }

    public JSONObject getJsonFromUrl(String url, String CMD, String Username, String Password, String Ho, String Ten, String ChucVu)
    {
        try
        {
            // Khởi tạo
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            //Gửi thông tin lệnh lên Server
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("CMD", CMD));
            nameValuePairs.add(new BasicNameValuePair("Username", Username));
            nameValuePairs.add(new BasicNameValuePair("Password", Password));
            nameValuePairs.add(new BasicNameValuePair("Ho", Ho));
            nameValuePairs.add(new BasicNameValuePair("Ten", Ten));
            nameValuePairs.add(new BasicNameValuePair("ChucVu", ChucVu));
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