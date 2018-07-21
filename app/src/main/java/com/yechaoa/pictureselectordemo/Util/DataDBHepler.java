package com.yechaoa.pictureselectordemo.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yechaoa.pictureselectordemo.Modle.ListData;
import com.yechaoa.pictureselectordemo.Modle.SelectOid;
import com.yechaoa.pictureselectordemo.Modle.SidSelectData;
import com.yechaoa.pictureselectordemo.Modle.UserData;
import com.yechaoa.pictureselectordemo.Modle.UserpassData;

import java.util.ArrayList;

/**
 * Created by moonshine on 2018/2/5.
 */

public class DataDBHepler extends SQLiteOpenHelper {

    public final static String dbname="data.db";
    public final static int dbversion = 4;

    public DataDBHepler(Context context) {
        super(context,dbname,null, dbversion);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists Sid_table(id INTEGER primary key autoincrement,sid varcher(256))");
        db.execSQL("create table if not exists User_table(id INTEGER primary key autoincrement,user varcher(256),name varcher(125),idnum verchar(125),birthday varcher(125),phone varcher(125))");
        db.execSQL("create table if not exists Oid_table(id INTEGER primary key autoincrement,oid varchar(256))");
        db.execSQL("create table if not exists Password_table(id INTEGER primary key autoincrement,username varcher(125),password varcher(125))");
        db.execSQL("create table if not exists Equipment_table(id INTEGER primary key autoincrement,itemcode varcher(125),itemname varcher(125),itemdetail varcher(256),unitcode varcher(125),itemmembers varcher(125),longitude varcher(125),latitude varcher(125))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }


    public boolean isIdorSid() {

        SQLiteDatabase db = this.getWritableDatabase();
        String SQL = "SELECT * FROM Sid_table";
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(SQL,null);
        if (cursor.getCount()>0) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 查询用户名和密码
     * @return
     */

    public boolean isIdoruserpass() {
        SQLiteDatabase db = this.getWritableDatabase();
        String SQL = "SELECT * FROM Password_table";
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(SQL,null);
        if (cursor.getCount()>0) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 查询数据库里是否有Oid
     * @return
     */

    public boolean isIdorOid() {

        SQLiteDatabase db = this.getWritableDatabase();
        String SQL = "SELECT * FROM Oid_table";
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(SQL,null);

        if (cursor.getCount()>0) {
            return true;
        }else {
            return false;
        }
    }

    /**
     *
     * 查询是否有数据
     */
    public boolean isItemorData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String SQL = "SELECT * FROM Equipment_table";
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(SQL,null);
        if (cursor.getCount()>0) {
            return true;
        }else {
            return false;
        }
    }

    public boolean isIdorUser() {

        SQLiteDatabase db = this.getWritableDatabase();
        String SQL = "SELECT * FROM User_table";
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(SQL,null);

        if (cursor.getCount()>0) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 查询数据库里有没有 这个sid
     * @return
     */
    public ArrayList<SidSelectData> FindSidData() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList SidList = new ArrayList();
        Cursor cursor = db.rawQuery("select * from Sid_table", null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            SidSelectData info =new SidSelectData(cursor.getString(0),cursor.getString(1));
            SidList.add(info);
        }
        cursor.close();
        db.close();
        return SidList;
    }



    /**
     *
     * 查询userpassword
     *
     */
    public ArrayList<UserpassData> FinduserpassData() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList SidList = new ArrayList();
        Cursor cursor = db.rawQuery("select * from Password_table", null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            UserpassData info =new UserpassData(cursor.getString(0),cursor.getString(1),cursor.getString(2));

            SidList.add(info);
        }
        cursor.close();
        db.close();
        return SidList;
    }
    /**
     *
     * 查询Oid
     */
    public ArrayList<SelectOid> FindOidData() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList SidList = new ArrayList();
        Cursor cursor = db.rawQuery("select * from Oid_table", null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            SelectOid info =new SelectOid(cursor.getString(0),cursor.getString(1));

            SidList.add(info);
        }
        cursor.close();
        db.close();
        return SidList;
    }
    /**
     *
     * //查询User
     */
    public ArrayList<UserData> FindUserData() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList UserList = new ArrayList();
        Cursor cursor = db.rawQuery("select * from User_table", null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            UserData info =new UserData(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));
            UserList.add(info);
        }
        cursor.close();
        db.close();
        return UserList;
    }

    public ArrayList<ListData>FindItemData(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList itemdataList = new ArrayList();Cursor cursor = db.rawQuery("select * from Equipment_table", null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            ListData info = new ListData(cursor.getString(0),cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6),cursor.getString(7));
            itemdataList.add(info);
        }
        cursor.close();
        db.close();
        return   itemdataList;
    }

    //更新数据
    public void  update(String sid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Sid_table SET Sid = '" + sid +"' WHERE id= '1'");
    }
    //更新Oid
    public void  updateoid(String oid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Oid_table SET Oid = '" + oid +"' WHERE id= '1'");
    }

    //更新User
    public void  updateUser(String User, String name, String idnum, String birthday, String phone){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE User_table SET User = '" + User +"', name='"+name+"',idnum = '"+idnum+"' ,birthday = '"+birthday+"',phone='"+phone+"'  WHERE id= '1'");
    }
    //更新username，passsword
    public void  updateUserpass(String user, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Password_table SET username='"+user+"',password = '"+password+"'  WHERE id= '1'");
    }
    //更新itemdata的数据
    public  void updateItemdata(String id,String itemcode ,String itemname ,String itemdetail,String unitcode ,String itemmembers ,String longitude,String latitude){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Equipment_table SET itemcode='"+itemcode+"',itemname = '"+itemname+"',itemdetail ='"+itemdetail+"',unitcode='"+unitcode+"',itemmembers='"+itemmembers+"',longitude='"+longitude+"',latitude='"+latitude+"' WHERE id= '1'");

    }
    /**
     * 插入sid
     * @param id
     * @param sid
     */
    public void add(String id, String sid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("insert into Sid_table(id,sid) values(?,?)",
                new Object[]{id,sid});
    }
    /**
     * 插入oid
     *
     */
    public void addoid(String id, String oid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("insert into Oid_table(id,Oid) values(?,?)",
                new Object[]{id,oid});
    }

    /**
     * 插入用户名User
     * @param id
     * @param user
     */
    public void addUser(String id, String user, String name, String idnum, String birthday, String phone){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("insert into User_table(id,user,name,idnum,birthday,phone) values(?,?,?,?,?,?)",
                new Object[]{id,user});
    }
    //更新username，passsword
    public void  addUserpass(String id, String user, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("insert into Password_table(id,username,password) values(?,?,?)");
    }
    //插入item设备数据
    public void  addItemData(String id,String itemcode ,String itemname ,String itemdetail,String unitcode ,String itemmembers ,String longitude,String latitude ){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("insert into Equipment_table(id,itemcode,itemname,itemdetail,unitcode,itemmembers,longitude,latitude) values(?,?,?,?,?,?,?,?)");
    }

    /**+
     * 删除Sid_table里面的数据
     * @return
     */
    public void delete(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Sid_table where id=?",new Object[]{id});
    }
}