package com.aver.smsdemo;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SmsDemoActivity extends Activity {

    private static final int GET_CONTACT_LIST = 1;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 2;

    private EditText smsDemoEdit;
    private Button smsDemoBtn;
    private String str;

    String strName = "";
    String strPhone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_demo);

        smsDemoEdit = findViewById(R.id.smsDemoEdit);
        smsDemoBtn = findViewById(R.id.smsDemoBtn);

        smsDemoBtn.setOnClickListener(sendBtnLink);
    }

    private Button.OnClickListener sendBtnLink = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {
                sendSMS();
            }
        }
    };

    private void sendSMS() {
        Uri uri = Uri.parse("content://contacts/people");
        Intent intent = new Intent(Intent.ACTION_PICK, uri);
        str = smsDemoEdit.getText().toString();
        if (isEmpty(str)) {
            Toast.makeText(SmsDemoActivity.this, getString(R.string.magEmpty), Toast.LENGTH_LONG).show();
            return;
        }
        startActivityForResult(intent, GET_CONTACT_LIST);
    }

    private boolean isEmpty(final String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GET_CONTACT_LIST:
                Uri uri = null;
                try {
                    uri = data.getData();
                } catch (Exception e) {
                    Toast.makeText(SmsDemoActivity.this, getString(R.string.getDataError), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                if (uri != null) {
                    try {
                        Cursor c = getContentResolver().query(uri, null, null, null, null);
                        c.moveToFirst();

                        int contactId = c.getInt(c.getColumnIndex(ContactsContract.Contacts._ID));
                        Cursor curContacts = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
                        if (curContacts.getCount() > 0) {
                            curContacts.moveToFirst();
                            strName = curContacts.getString(curContacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            strPhone = curContacts.getString(curContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }

                        String strDestAddress = strPhone;
                        SmsManager smsManager = SmsManager.getDefault();

                        PendingIntent mPI = PendingIntent.getBroadcast(SmsDemoActivity.this, 0, new Intent(), 0);
                        smsManager.sendTextMessage(strDestAddress, null, str, mPI, null);
                        Toast.makeText(SmsDemoActivity.this, getString(R.string.msgPrefix) + strName, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(SmsDemoActivity.this, getString(R.string.getCursorError), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS();
            } else {
                Toast.makeText(SmsDemoActivity.this, getString(R.string.permissionError), Toast.LENGTH_LONG).show();
            }
        }
    }
}
