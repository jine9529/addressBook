package com.example.jejesave;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    myAdapter adapter;
    ListView list;
    List<MyData> data;
    EditText name, fone, nick;
    MyDbHelper helper;
    SQLiteDatabase db;
    String[] projection = {"_id", "name", "fone", "nick"};
    MyData saveData;

    private Button btn;
    private long btnPressTime = 0;
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btnAdd = (Button) findViewById(R.id.add);
        final Button btnUpdate = (Button) findViewById(R.id.upd);
        final Button btnDelete = (Button) findViewById(R.id.del);


        name = findViewById(R.id.name);
        fone = findViewById(R.id.fone);
        nick = findViewById(R.id.nick);
        helper = new MyDbHelper(this);
        list = findViewById(R.id.list);
        data = new ArrayList<>();
        saveData = new MyData("", "", "");

        getAllData();

        adapter = new myAdapter(this, data);

        list.setAdapter(adapter);

        activity = this;
        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(activity);

        fone.addTextChangedListener(new PhoneNumberFormattingTextWatcher("KR"));

        //클릭한 데이터 EditText필드에 데이터 가도록 설정!
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // 한 번 누르면 데이터 필드에 가도록!
                if (System.currentTimeMillis() > btnPressTime + 1000) {
                    btnPressTime = System.currentTimeMillis();
                    ContentValues values = new ContentValues();

                    ((EditText) findViewById(R.id.name)).setText(name.getText().toString());

                    MyData item = (MyData) list.getItemAtPosition(i);
                    name.setText(item.getName());
                    fone.setText(item.getFone());
                    nick.setText(item.getNick());
                    saveData = item;

                }
                //두 번 누르면 창 띄우기
                else if (System.currentTimeMillis() <= btnPressTime + 1000) {


                    final MyData item = (MyData) list.getItemAtPosition(i);

                    alertdialog.setMessage("이름\t\t:\t" + item.getName() + "\n"
                            + "전화번호:\t" + item.getFone() + "\n"
                            + "별명\t\t:\t" + item.getNick());

                    //팝업안에 전화걸기 버튼
                    alertdialog.setPositiveButton("call", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            /*Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("01089089519"));*/
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + item.getFone()));
                            startActivity(intent);
                        }
                    });
                    //팝업안에 문자보내기 버튼
                    alertdialog.setNegativeButton("message", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms: " + item.getFone()));
                            startActivity(intent);
                        }
                    });
                    AlertDialog alert = alertdialog.create();
                    alert.show();

                    return;
                }
            }
        });

        helper.close();

        //EditText에 값이 있을때에만 버튼이 활성화 되도록!
        fone.setEnabled(false);
        nick.setEnabled(false);
        btnAdd.setEnabled(false);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //name입력해야 fone EditText필드 입력가능
                if (charSequence.toString().equals("")) {
                    fone.setEnabled(false);
                    btnDelete.setEnabled(false);
                } else {
                    fone.setEnabled(true);
                    btnDelete.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        fone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //fone입력해야 add버튼 활성화
                if (charSequence.toString().equals("")) {
                    nick.setEnabled(false);
                    btnAdd.setEnabled(false);
                    btnUpdate.setEnabled(false);
                } else {

                    db = helper.getReadableDatabase();
                    Cursor c = db.query("people", projection, null, null, null, null, null);

                    nick.setEnabled(true);
                    btnUpdate.setEnabled(true);
                    btnAdd.setEnabled(true);

                  /*  while (c.moveToNext()) {
                        *//*이름이 리스트에 있어서 찾을 수 있다면 갱신, 삭제만 가능*//*
                        if (fone.getText().toString().equals(c.getString(2))) {
                            Toast.makeText(MainActivity.this, "추가안됨", Toast.LENGTH_SHORT).show();
                            *//*이름이 리스트에 없다면 add 버튼도 가능*//*
                            break;
                        } else {
                            btnAdd.setEnabled(true);
                        }

                    }*/
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    public void onClick(View view) {

        db = helper.getWritableDatabase();
        ContentValues values;

        switch (view.getId()) {
            case R.id.add: {
                if (name.getText().toString().length() > 0 && fone.getText().toString().length() > 0) {
                    values = new ContentValues();
                    values.put("name", name.getText().toString());
                    values.put("fone", fone.getText().toString());
                    values.put("nick", nick.getText().toString());


                    if (name.getText().toString().equals(saveData.getName()) && fone.getText().toString().equals(saveData.getFone())) {
                        Toast.makeText(MainActivity.this, "추가 실패", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        db.insert("people", null, values);
                        Toast.makeText(MainActivity.this, name.getText().toString() + "추가", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }

            case R.id.upd: {
                Button btn = (Button) findViewById(R.id.upd);
                if (name.getText().toString().length() > 0 && fone.getText().toString().length() > 0) {

                    if (fone.getText().toString().equals(saveData.getFone()) && nick.getText().toString().equals(saveData.getNick())) {
                        Toast.makeText(MainActivity.this, "수정사항없음", Toast.LENGTH_SHORT).show();

                    } else {
                        values = new ContentValues();
                        values.put("name", name.getText().toString());
                        values.put("fone", fone.getText().toString());
                        values.put("nick", nick.getText().toString());
                        db.update("people", values, "name = ?", new String[]{name.getText().toString()});

                        Toast.makeText(MainActivity.this, name.getText().toString()+"갱신", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    btn.setEnabled(false);
                }

                btn.setEnabled(true);
                break;
            }

            case R.id.del: {
                adapter = new myAdapter(this, data);
                list.setAdapter(adapter);

                db = helper.getReadableDatabase();
                Cursor c = db.query("people", projection, null, null, null, null, null);

                while (c.moveToNext()) {

                    /*이름이 리스트에 있어서 찾을 수 있는 경우에만 삭제*/
                    if (name.getText().toString().equals(c.getString(1))) {

                        db.delete("people", "name = ?", new String[]{name.getText().toString()});
                        db.delete("people", "fone = ?", new String[]{fone.getText().toString()});
                        db.delete("people", "nick = ?", new String[]{nick.getText().toString()});
                        Toast.makeText(MainActivity.this, name.getText().toString() + "삭제", Toast.LENGTH_SHORT).show();
                        break;
                    }else{
                        Toast.makeText(MainActivity.this, name.getText().toString() + "존재안함", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }

        name.setText("");
        fone.setText("");
        nick.setText("");

        db.close();

        getAllData();

        adapter.notifyDataSetChanged();
        helper.close();

    }


    //화면에 디비에 있는 내용 다 보여주는 method
    void getAllData() {
        db = helper.getReadableDatabase();
        Cursor c = db.query("people", projection, null, null, null, null, null);

        data.clear();
        while (c.moveToNext()) {
            MyData item = new MyData(c.getString(1), c.getString(2), c.getString(3));
            data.add(item);
        }
    }
}


class myAdapter extends BaseAdapter {

    Context context;
    List<MyData> data;

    public myAdapter(Context context, List<MyData> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public MyData getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(context, R.layout.row, null);
        }

        TextView text1 = (TextView) view.findViewById(R.id.text1);
        TextView text2 = (TextView) view.findViewById(R.id.text2);
        TextView text3 = (TextView) view.findViewById(R.id.text3);
        text1.setText(data.get(i).getName());
        text2.setText(data.get(i).getFone());
        text3.setText(data.get(i).getNick());

        return view;
    }
}


