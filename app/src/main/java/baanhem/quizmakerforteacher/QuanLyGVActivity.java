package baanhem.quizmakerforteacher;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

@SuppressWarnings("deprecation")
public class QuanLyGVActivity extends Activity
{
    TextView tvHelloGV, tvKetQuaDiem;
    EditText edtMaDeGV;
    Button btnXemDiem;
    Menu menu;

    String UsernameGV, ketqua = "";
    String json = "";
    JSONArray JArr = null;
    InputStream is = null;
    JSONObject jobj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.right_in_start, R.anim.right_in_end);
        setContentView(R.layout.activity_quanly_gv);
        setTitle("\tGiảng Viên");

        tvHelloGV = (TextView)findViewById(R.id.tvHelloGV);
        tvKetQuaDiem = (TextView)findViewById(R.id.tvKetQuaDiem);
        edtMaDeGV = (EditText)findViewById(R.id.edtMaDeGV);
        btnXemDiem = (Button)findViewById(R.id.btnXemDiem);

        Bundle b = getIntent().getExtras();
        String Ho = b.getString("Ho");
        String Ten = b.getString("Ten");
        UsernameGV = b.getString("Username");

        tvHelloGV.setText("Xin chào " + Ho + " " + Ten);

        btnXemDiem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                tvKetQuaDiem.setText("");
                GetData task = new GetData(UsernameGV, edtMaDeGV.getText().toString());
                task.execute();
            }
        });
    }

    public class GetData extends AsyncTask<Void, Void, Void>
    {
        String UsernameGV, MaDe;
        ProgressDialog progress;

        public GetData(String UsernameGV, String MaDe)
        {
            this.UsernameGV = UsernameGV;
            this.MaDe = MaDe;
        }

        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(QuanLyGVActivity.this);
            progress.setMessage("Đang lấy Dữ liệu Điểm");
            progress.setIndeterminate(false);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            JSONObject json_objectMon = getJsonFromUrl(PHPUrl.getdiem, MaDe, UsernameGV);
            try
            {
                JArr = new JSONArray();
                JArr = json_objectMon.getJSONArray("Danh_sach_diem");
                for(int i=0; i < JArr.length(); i++)
                {
                    JSONObject diem = JArr.getJSONObject(i);
                    ketqua = ketqua + "Tên Sinh viên: " + diem.getString("Ho") + " " + diem.getString("Ten") + "\nNgày làm: " + diem.getString("NgayLam") + "\nĐiểm: " + diem.getDouble("Diem") + "đ\n\n";
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
            progress.dismiss();
            if (ketqua.equals(""))
            {
                tvKetQuaDiem.setText("Không có dữ liệu điểm cho Mã Đề này!");
            }
            else
            {
                tvKetQuaDiem.setText(ketqua);
            }
            ketqua = "";
            super.onPostExecute(result);
        }
    }

    public JSONObject getJsonFromUrl(String url, String CMD, String UsernameGV)
    {
        try
        {
            // Khởi tạo
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            //Gửi thông tin lệnh lên Server
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("CMD", CMD));
            nameValuePairs.add(new BasicNameValuePair("UsernameGV", UsernameGV));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_account);
        item.setIcon(R.drawable.ic_account);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_account:
                Toast.makeText(this, "Chức năng Quản lý Account cho Giảng viên chưa được phát triển!", Toast.LENGTH_LONG).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
