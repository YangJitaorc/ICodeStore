package com.example.jtnote.ui.MainPage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jtnote.Constants;
import com.example.jtnote.R;
import com.example.jtnote.bean.NoteItem;
import com.example.jtnote.ui.KeyboardActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MainPageContract.View{
    private static final int KEYBOARD_ACTIVITY_REQUESTCODE = 100;

    private List<NoteItem> noteItemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MainPageContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_text:
//                presenter.textEntryClick();
                KeyboardActivity.start(this, KEYBOARD_ACTIVITY_REQUESTCODE);
                break;
        }
    }

    @Override
    public void notesChange(List<NoteItem> noteList) {
        noteItemList.clear();
        noteItemList.addAll(noteList);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestory();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == KEYBOARD_ACTIVITY_REQUESTCODE && resultCode == RESULT_OK && data != null){
            String inputStr = data.getStringExtra(Constants.KEY_INPUT_CONTENT);
            if(TextUtils.isEmpty(inputStr)) return;
            presenter.newTextContent(inputStr);
        }
    }

    private class NoteContentAdapter extends RecyclerView.Adapter<NoteContentAdapter.NoteContentHolder>{


        @Override
        public NoteContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_note, parent, false);
            return new NoteContentHolder(itemView);
        }

        @Override
        public void onBindViewHolder(NoteContentHolder holder, int position) {
            NoteItem noteItem = noteItemList.get(position);
            holder.textContent.setText(noteItem.getTextContent());
        }

        @Override
        public int getItemCount() {
            return noteItemList.size();
        }

        public class NoteContentHolder extends RecyclerView.ViewHolder{
            private TextView textContent;

            public NoteContentHolder(View itemView) {
                super(itemView);
                textContent = itemView.findViewById(R.id.tv_content);
            }
        }
    }

    private void initView(){
        recyclerView = findViewById(R.id.rc_note_board);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new NoteContentAdapter());

        findViewById(R.id.tv_text).setOnClickListener(this);
    }

    private void initData(){
        presenter = new MainPagePresenter(this);
    }

}