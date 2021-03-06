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
import com.chrisney.enigma.EnigmaUtils;


public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = EnigmaUtils.enigmatization(new byte[]{-7, -74, 62, 100, -49, 104, 14, -15, 38, 61, -87, -9, -42, -11, 34, 108});
    private static final int DATABASE_VERSION = 7;
    private static final String AES=EnigmaUtils.enigmatization(new byte[]{102, 125, 96, -103, -128, -63, -109, 80, -39, -63, -124, 126, 65, 16, -19, -40});
    private static final String pass=EnigmaUtils.enigmatization(new byte[]{-41, 25, -121, -66, 94, 18, 10, 121, 92, 63, 104, 112, -32, -10, 101, -97});

    public static final String DNGN__EDXI = "ys1a5vHuJOlCDG!9SWw36Ad?PXcC9g";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i(EnigmaUtils.enigmatization(new byte[]{-85, 66, -100, -73, -93, 43, 36, 97, -49, 124, 49, 30, 127, -30, -5, 50}), EnigmaUtils.enigmatization(new byte[]{15, 7, 12, 45, -46, 120, -29, 75, 8, -24, -58, 16, -52, 120, 100, -125}));
        if (DNGN__EDXI.isEmpty()) DNGN__EDXI.getClass().toString();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String strSql = EnigmaUtils.enigmatization(new byte[]{117, 38, 64, 29, 50, 109, -28, -33, -37, -20, -74, 96, -70, 10, 115, -38, -18, -50, 127, -16, 74, -10, 68, 30, 54, 103, 41, -56, -103, 91, -8, 84, 126, 62, 63, 127, 116, -90, 31, 104, 69, 90, -101, -35, -12, 103, -22, -110})
                + EnigmaUtils.enigmatization(new byte[]{83, -67, 2, 95, 39, -98, 3, -98, 67, 103, -94, -95, -8, -31, 16, -60, -15, -57, -17, -33, 96, 126, -127, 85, -125, -73, 87, -124, 2, -18, 31, -78, 85, -68, -42, -104, -126, 82, -82, 39, -40, -60, 72, 105, 64, -87, 64, 14})
                + EnigmaUtils.enigmatization(new byte[]{96, -123, 39, -127, 5, -41, -121, 23, 23, 127, 75, -111, -53, -16, -31, 67, -78, 90, -126, -105, 75, 95, 79, 7, -53, 47, -46, -124, 101, -69, -84, -21})
                + EnigmaUtils.enigmatization(new byte[]{60, -90, -83, -84, -85, 57, 3, -64, 72, 19, 77, 46, 24, 111, -39, 33, 110, -10, 12, -80, 104, -116, -20, 121, 7, 78, -82, -105, -56, -122, 7, 59})
                + EnigmaUtils.enigmatization(new byte[]{24, 76, -28, 33, 88, -116, 3, 113, -36, -78, 83, -98, 14, -40, 56, -13, 56, 36, -1, -109, 62, -29, -72, 35, -42, 28, -87, -112, 125, -95, 78, -1})
                + EnigmaUtils.enigmatization(new byte[]{-47, 21, 44, -80, -58, -58, 35, -24, 125, 18, 105, -79, 65, -43, -49, -82, 115, -46, 90, -18, 36, -11, 117, 57, 114, 117, -55, -67, -27, 33, 111, -41});

        db.execSQL(strSql);
        Log.i(EnigmaUtils.enigmatization(new byte[]{-85, 66, -100, -73, -93, 43, 36, 97, -49, 124, 49, 30, 127, -30, -5, 50}), EnigmaUtils.enigmatization(new byte[]{-109, -114, 117, -126, -77, -33, 11, -6, 15, 108, -108, -82, 42, 70, 62, -124, -93, 122, -78, 50, -40, -56, 110, 105, 93, 40, -103, 124, 21, -91, 124, -102}));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String strSql = EnigmaUtils.enigmatization(new byte[]{118, -57, 33, -46, 28, 115, -21, 100, -94, -127, 113, 111, 96, -76, -103, 38, 31, 45, 78, -102, -84, 95, 59, 98, 1, 32, -4, -107, 63, 65, 1, -96});
        db.execSQL(strSql);
        this.onCreate(db);
        Log.i(EnigmaUtils.enigmatization(new byte[]{-85, 66, -100, -73, -93, 43, 36, 97, -49, 124, 49, 30, 127, -30, -5, 50}), EnigmaUtils.enigmatization(new byte[]{-24, 61, 4, -53, -114, 29, -49, 62, -123, -56, 101, 92, 119, -38, 17, -64, 0, -18, 21, -54, 77, 17, 66, 48, -26, 62, 106, -108, -94, 11, -36, -17}));
    }

    public void insertAccount(int id, String name,float amount, String iban,String currency){
        name=encrypt(name);
        String amountS=encrypt(String.valueOf(amount));
        iban=encrypt(iban);
        currency=encrypt(currency);
        String strSql = EnigmaUtils.enigmatization(new byte[]{53, 21, -18, 76, 1, -37, 41, 108, -101, -22, -30, 29, 123, 6, -96, -77, -58, 93, 33, 108, -122, -27, 38, 41, -71, 85, -116, 18, 102, -40, -93, 8, 34, 84, -4, 8, -11, 81, 12, -114, 7, 57, 107, 48, -16, 24, -24, -111, 28, 32, -109, -91, 95, 119, 117, -18, 1, 124, 108, 61, 84, 108, 33, 29})+id+EnigmaUtils.enigmatization(new byte[]{-43, 89, 5, 27, 46, -102, 49, -68, 75, 55, -77, -99, -120, -57, 92, 87})+name+EnigmaUtils.enigmatization(new byte[]{-43, 89, 5, 27, 46, -102, 49, -68, 75, 55, -77, -99, -120, -57, 92, 87})+amountS+EnigmaUtils.enigmatization(new byte[]{-43, 89, 5, 27, 46, -102, 49, -68, 75, 55, -77, -99, -120, -57, 92, 87})+iban+EnigmaUtils.enigmatization(new byte[]{-43, 89, 5, 27, 46, -102, 49, -68, 75, 55, -77, -99, -120, -57, 92, 87})+currency+EnigmaUtils.enigmatization(new byte[]{47, -62, -37, 78, -87, 23, -4, 41, 117, -5, -98, -44, -13, -5, 95, -10});
        this.getWritableDatabase().execSQL(strSql);
        Log.i(EnigmaUtils.enigmatization(new byte[]{-85, 66, -100, -73, -93, 43, 36, 97, -49, 124, 49, 30, 127, -30, -5, 50}),EnigmaUtils.enigmatization(new byte[]{55, 99, -19, -114, 52, 18, 4, 51, -27, -6, -79, 38, -15, -56, 126, -38, 49, -87, 18, -116, -30, 89, 65, -123, 21, 3, -48, 46, -9, 57, -13, 82}));
    }

    public String selectAccount(int id){
        String strSql=EnigmaUtils.enigmatization(new byte[]{-118, 31, 57, -60, -55, 21, -89, 18, -63, -14, -26, 106, -19, -67, -40, 124, -7, 7, 51, -69, 6, -128, 112, -56, 17, -48, 100, -102, -12, 19, 116, 24, -49, 58, 92, 125, -27, 101, -100, 43, 95, -105, 32, -50, -91, 71, -78, 88})+id+EnigmaUtils.enigmatization(new byte[]{104, 8, -83, 124, 32, -73, -114, 2, -63, -116, -16, -101, -106, -103, -11, 65});
        Cursor cursor= this.getReadableDatabase().rawQuery(strSql,null);
        cursor.moveToFirst();
        String name=EnigmaUtils.enigmatization(new byte[]{71, 46, -79, 78, 60, 40, -69, -27, 17, 65, -111, -100, -47, -24, -29, 104});
        while(!cursor.isAfterLast()) {
            name=cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        Log.i(EnigmaUtils.enigmatization(new byte[]{82, 115, 26, -46, -19, -125, 56, -86, 98, 73, 64, -117, -69, -22, -98, -62}),EnigmaUtils.enigmatization(new byte[]{-95, -93, -44, -66, -90, -4, 22, 24, -54, -127, 1, 10, 30, 61, -25, -26, 70, 68, -110, -50, 29, 38, 35, 106, -58, 29, -99, -23, -110, 88, 34, -46}));
        return name;
    }

    public List<String> selectAllAccount(){
        String strSql=EnigmaUtils.enigmatization(new byte[]{37, -101, 4, 34, 66, 31, -33, -126, 13, -101, -68, -7, -77, 27, -60, 95, 97, -63, -22, -40, -97, 119, -128, -112, -26, 48, -67, -4, 111, 38, 74, 51});
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
        Log.i(EnigmaUtils.enigmatization(new byte[]{82, 115, 26, -46, -19, -125, 56, -86, 98, 73, 64, -117, -69, -22, -98, -62}),EnigmaUtils.enigmatization(new byte[]{11, -10, 14, -124, 19, -83, -38, -120, -16, -99, 97, -46, -6, 29, -87, -109, 32, 25, -70, 68, -120, 51, -55, 118, 23, 37, 46, 27, 46, 118, -28, 23}));
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
            final MessageDigest digest=MessageDigest.getInstance(EnigmaUtils.enigmatization(new byte[]{-80, 80, 54, 73, 12, 17, -32, -17, -81, 62, 121, -52, -45, -14, 93, -49}));
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
