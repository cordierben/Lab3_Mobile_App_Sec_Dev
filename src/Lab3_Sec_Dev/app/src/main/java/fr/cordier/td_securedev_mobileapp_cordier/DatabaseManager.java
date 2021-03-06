package fr.cordier.td_securedev_mobileapp_cordier;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;
import android.database.Cursor;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Bank.db";
    private static final int DATABASE_VERSION = 7;
    private static final String AES="AES";
    private static final String pass="734564327";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("DATABASE", "invoked");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String strSql = "CREATE TABLE IF NOT EXISTS Account ("
                + "id Integer not null PRIMARY KEY,"
                + "name VARCHAR(500) not null,"
                + "amount Float not null,"
                + "iban VARCHAR(20) not null,"
                + "currency VARCHAR(1) not null);";

        db.execSQL(strSql);
        Log.i("DATABASE", "onCreate invoked");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String strSql = "DROP TABLE IF EXISTS Account;";
        db.execSQL(strSql);
        this.onCreate(db);
        Log.i("DATABASE", "onUpgrade invoked");
    }

    public void insertAccount(int id, String name,float amount, String iban,String currency){
        name=encrypt(name);
        String amountS=encrypt(String.valueOf(amount));
        iban=encrypt(iban);
        currency=encrypt(currency);
        String strSql = "INSERT INTO Account(id,name,amount,iban,currency) VALUES('"+id+"','"+name+"','"+amountS+"','"+iban+"','"+currency+"');";
        this.getWritableDatabase().execSQL(strSql);
        Log.i("DATABASE","insertAccount invoked");
    }

    public String selectAccount(int id){
        String strSql="SELECT name FROM Account WHERE id='"+id+"';";
        Cursor cursor= this.getReadableDatabase().rawQuery(strSql,null);
        cursor.moveToFirst();
        String name="";
        while(!cursor.isAfterLast()) {
            name=cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        Log.i("Database","selectAccount invoked");
        return name;
    }

    public List<String> selectAllAccount(){
        String strSql="SELECT * FROM Account;";
        Cursor cursor= this.getReadableDatabase().rawQuery(strSql,null);
        cursor.moveToFirst();
        List<String> info=new ArrayList<>();
        while(!cursor.isAfterLast()) {
            int idUser=cursor.getInt(0);
            info.add(String.valueOf(idUser));
            info.add(decrypt(cursor.getString(1)));
            info.add(decrypt(cursor.getString(2)));
            info.add(decrypt(cursor.getString(3)));
            info.add(decrypt(cursor.getString(4)));
            cursor.moveToNext();
        }
        cursor.close();
        Log.i("Database","selectAllAccount invoked");
        return info;
    }

    private String encrypt(String msg){
        SecretKeySpec key=generateKey(pass);
        Cipher c= null;
        try {
            c = Cipher.getInstance(AES);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            c.init(Cipher.ENCRYPT_MODE,key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] encVal= new byte[0];
        try {
            encVal = c.doFinal(msg.getBytes());
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        String value= Base64.encodeToString(encVal,Base64.DEFAULT);
        return value;
    }

    private String decrypt(String msg){
        SecretKeySpec key=generateKey(pass);
        Cipher c= null;
        try {
            c = Cipher.getInstance(AES);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            c.init(Cipher.DECRYPT_MODE,key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] valueB= Base64.decode(msg,Base64.DEFAULT);
        byte[] decValue= new byte[0];
        try {
            decValue = c.doFinal(valueB);
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        String value=new String(decValue);
        return value;
    }

    private SecretKeySpec generateKey(String pwd){
        try {
            final MessageDigest digest=MessageDigest.getInstance("SHA-256");
            byte[] bytes=pwd.getBytes();
            digest.update(bytes,0,bytes.length);
            byte[] key=digest.digest();
            SecretKeySpec secretKeySpec=new SecretKeySpec(key,AES);
            return secretKeySpec;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
