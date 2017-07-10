package helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import item.AccountItem;
import item.CauhoiItem;
import item.MonhocItem;

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static String DB_NAME = "b12_16086678_androidnttvn";
    private static final int DATABASE_VERSION = 1;

    private static final String Table_GiangVien = "GiangVien";
    private static final String GiangVien_Username = "Username";
    private static final String GiangVien_Ho = "Ho";
    private static final String GiangVien_Ten = "Ten";

    private static final String Table_Mon = "Mon";
    private static final String Mon_MaMon = "MaMon";
    private static final String Mon_TenMon = "TenMon";

    private static final String Table_CauHoi = "CauHoi";
    private static final String CauHoi_MaCauHoi = "MaCauHoi";
    private static final String CauHoi_NoiDung = "NoiDung";
    private static final String CauHoi_DapAnDung = "DapAnDung";
    private static final String CauHoi_DapAn1 = "DapAn1";
    private static final String CauHoi_DapAn2 = "DapAn2";
    private static final String CauHoi_DapAn3 = "DapAn3";

    private final Context context;

    public DatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //Khởi tạo các bảng trong Database
        String create_giangvien_table = "create table " + Table_GiangVien + "(" + GiangVien_Username
                + " text primary key, " + GiangVien_Ho + " text, " + GiangVien_Ten + " text)";
        String create_mon_table = "create table " + Table_Mon + "(" + Mon_MaMon
                + " text primary key, " + Mon_TenMon + " text)";
        String create_cauhoi_table = "create table " + Table_CauHoi + "(" + CauHoi_MaCauHoi
                + " int primary key, " + CauHoi_NoiDung + " text, " + CauHoi_DapAnDung + " text, "
                + CauHoi_DapAn1 + " text, " + CauHoi_DapAn2 + " text, " + CauHoi_DapAn3 + " text)";

        db.execSQL(create_giangvien_table);
        db.execSQL(create_mon_table);
        db.execSQL(create_cauhoi_table);
        Log.i("Database", "Tạo Database thành công");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("drop table if exists " + Table_GiangVien);
        db.execSQL("drop table if exists " + Table_Mon);
        db.execSQL("drop table if exists " + Table_CauHoi);
        onCreate(db);
    }

    //Thêm một Giảng viên vào bảng GiangVien
    public long themGiangVien(AccountItem gv)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(GiangVien_Username, gv.Username);
        values.put(GiangVien_Ho, gv.Ho);
        values.put(GiangVien_Ten, gv.Ten);

        long insert = db.insert(Table_GiangVien, null, values);

        db.close();
        return insert;
    }

    //Thêm một Môn học vào bảng Mon
    public long themMon(MonhocItem mon)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Mon_MaMon, mon.MaMon);
        values.put(Mon_TenMon, mon.TenMon);

        long insert = db.insert(Table_Mon, null, values);

        db.close();
        return insert;
    }

    //Thêm một Câu hỏi vào bảng CauHoi
    public long themCauhoi(CauhoiItem ch)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CauHoi_MaCauHoi, ch.MaCauHoi);
        values.put(CauHoi_NoiDung, ch.NoiDung);
        values.put(CauHoi_DapAnDung, ch.DapAnDung);
        values.put(CauHoi_DapAn1, ch.DapAn1);
        values.put(CauHoi_DapAn2, ch.DapAn2);
        values.put(CauHoi_DapAn3, ch.DapAn3);

        long insert = db.insert(Table_CauHoi, null, values);

        db.close();
        return insert;
    }

    //Lấy tất cả Giảng viên có trong Database
    public ArrayList<AccountItem> getGiangvien()
    {
        ArrayList<AccountItem> gv = null;

        SQLiteDatabase db = this.getWritableDatabase();

        String[] colunm = {GiangVien_Username, GiangVien_Ho, GiangVien_Ten};
        Cursor cursor = db.query(true, Table_GiangVien, colunm, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst())
        {
            gv = new ArrayList<>();
            do
            {
                String gv_username = cursor.getString(0);
                String gv_ho = cursor.getString(1);
                String gv_ten = cursor.getString(2);
                AccountItem giangvien = new AccountItem(gv_username, gv_ho, gv_ten);
                gv.add(giangvien);
            }	while (cursor.moveToNext());
        }
        db.close();
        return gv;
    }

    //Lấy tất cả Môn học có trong Database
    public ArrayList<MonhocItem> getMonhoc()
    {
        ArrayList<MonhocItem> mon = null;

        SQLiteDatabase db = this.getWritableDatabase();

        String[] colunm = {Mon_MaMon, Mon_TenMon};
        Cursor cursor = db.query(true, Table_Mon, colunm, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst())
        {
            mon = new ArrayList<>();
            do
            {
                String mon_mamon = cursor.getString(0);
                String mon_tenmon = cursor.getString(1);
                MonhocItem monhoc = new MonhocItem(mon_mamon, mon_tenmon);
                mon.add(monhoc);
            }	while (cursor.moveToNext());
        }
        db.close();
        return mon;
    }

    //Lấy tất cả Câu hỏi có trong Database
    public ArrayList<CauhoiItem> getCauhoi()
    {
        ArrayList<CauhoiItem> ch = null;

        SQLiteDatabase db = this.getWritableDatabase();

        String[] colunm = {CauHoi_MaCauHoi, CauHoi_NoiDung, CauHoi_DapAnDung, CauHoi_DapAn1, CauHoi_DapAn2, CauHoi_DapAn3};
        Cursor cursor = db.query(true, Table_CauHoi, colunm, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst())
        {
            ch = new ArrayList<>();
            do
            {
                int cauhoi_macauhoi = cursor.getInt(0);
                String cauhoi_noidung = cursor.getString(1);
                String cauhoi_dapandung = cursor.getString(2);
                String cauhoi_dapan1 = cursor.getString(3);
                String cauhoi_dapan2 = cursor.getString(4);
                String cauhoi_dapan3 = cursor.getString(5);
                CauhoiItem cauhoi = new CauhoiItem(cauhoi_macauhoi, cauhoi_noidung, cauhoi_dapandung, cauhoi_dapan1, cauhoi_dapan2, cauhoi_dapan3);
                ch.add(cauhoi);
            }	while (cursor.moveToNext());
        }
        db.close();
        return ch;
    }

    //Xóa bảng GiangVien
    public int xoaGiangvien()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(Table_GiangVien, null, null);
        db.close();
        return delete;
    }

    //Xóa bảng Mon
    public int xoaMonhoc()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(Table_Mon, null, null);
        db.close();
        return delete;
    }

    //Xóa bảng CauHoi
    public int xoaCauhoi()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int delete = db.delete(Table_CauHoi, null, null);
        db.close();
        return delete;
    }

    //Lấy tổng số câu hỏi
    public int getSoCauhoi()
    {
        int socau = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCount = db.rawQuery("select count(*) from " + Table_CauHoi, null);
        mCount.moveToFirst();
        socau = mCount.getInt(0);
        db.close();
        return socau;
    }
}