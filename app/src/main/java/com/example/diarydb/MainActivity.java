package com.example.diarydb;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    DatePicker dPicker;
    EditText edtDiary;
    Button btnSave;
    int cYear, cMonth, cDay;
    String dateName; // 저장될 날짜 변수
    MyDBHelper myBD;
    SQLiteDatabase sqlDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dPicker=findViewById(R.id.dpicker);
        edtDiary=findViewById(R.id.edtDiary);
        btnSave=findViewById(R.id.btnSave);

        Calendar calendar=Calendar.getInstance();
        cYear=calendar.get(Calendar.YEAR);
        cMonth=calendar.get(Calendar.MONTH);
        cDay=calendar.get(Calendar.DAY_OF_MONTH);
        dateName=cYear+"_"+(cMonth+1)+"_"+cDay;

        myBD=new MyDBHelper(this);

        dPicker.init(cYear, cMonth, cDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateName=year+"_"+(monthOfYear+1)+"_"+dayOfMonth;
                edtDiary.setText(readDiary(dateName));
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlDB = myBD.getWritableDatabase();
                if (btnSave.getText().toString().equals("새로 저장")) {
                    sqlDB.execSQL("INSERT INTO myDiaryTBL VALUES('" + dateName + "','" + edtDiary.getText().toString() + " ');");
                    showToast("일기가 저장되었습니다.");
                    btnSave.setText("수정 하기");
                } else {
                    sqlDB.execSQL("UPDATE myDiaryTBL SET content='" + edtDiary.getText().toString() + "'" +
                            "WHERE diaryDate='" + dateName + "';'");
                    showToast("일기가 수정되었습니다.");
                }
                sqlDB.close();
            }
        });
    }
    void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    //일기를 읽어오는 메소드
    String readDiary(String dateName) {
        String diaryContent = null;
        sqlDB=myBD.getReadableDatabase();
        Cursor cursor; //해당 날짜로 이동
        cursor=sqlDB.rawQuery("SELECT * FROM myDiaryTBL WHERE diaryDate='"+dateName+"'", null);
        if(cursor.moveToFirst()){
            diaryContent=cursor.getString(1);
            btnSave.setText("수정하기");
        } else{
            edtDiary.setHint("일기 없음");
            btnSave.setText("새로 저장");
        }
        cursor.close();
        sqlDB.close();
        return diaryContent;
    }
    //데이터베이스 클래스
    public class MyDBHelper extends SQLiteOpenHelper{
        //DB생성 생성자
        public MyDBHelper(@Nullable Context context) {
            super(context, "myDiaryDB", null, 1);
        }
        // 테이블 생성 메서드
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE myDiaryTBL(diaryDate TEXT PRIMARY KEY, content TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}