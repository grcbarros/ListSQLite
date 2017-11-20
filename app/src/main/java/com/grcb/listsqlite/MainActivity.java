package com.grcb.listsqlite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private EditText editText;
    private ListView listView;
    private SQLiteDatabase sqLiteDatabase;
    private ArrayList<String> arrayListNotas;
    private ArrayList<Integer> arrayListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Button buttonSalvar = findViewById(R.id.buttonId);
            editText = findViewById(R.id.editTextId);
            editText.callOnClick();
            listView = findViewById(R.id.listViewId);

            sqLiteDatabase = openOrCreateDatabase("notes", MODE_PRIVATE, null);

            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY AUTOINCREMENT, note VARCHAR)");

            listarNotas();

            buttonSalvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    salvarNota(editText.getText().toString());
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    removerNota(position);
                    return false;
                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listarNotas() {
        try {
            Cursor notas = sqLiteDatabase.rawQuery("SELECT * FROM notes", null);
            notas.moveToFirst();

            int idColumnIndex = notas.getColumnIndex("id");
            int noteColumnIndex = notas.getColumnIndex("note");

            arrayListId = new ArrayList<>();
            arrayListNotas = new ArrayList<>();

            while (!notas.isAfterLast()) {
                arrayListNotas.add(notas.getString(noteColumnIndex));
                arrayListId.add(Integer.parseInt(notas.getString(idColumnIndex)));
                notas.moveToNext();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    arrayListNotas
            );

            listView.setAdapter(adapter);
            notas.close();
        } catch (Exception e) {
            exibirMSG(e.getMessage());
            e.printStackTrace();
        }
    }

    private void salvarNota(String note) {
        try {
            if (note.isEmpty()) {
                exibirMSG("Digite algo para salvar!");
            } else {
                sqLiteDatabase.execSQL("INSERT INTO notes (note) VALUES ('" + note + "')");

                editText.setText(null);
                exibirMSG("Nota salva com sucesso!");

                listarNotas();
            }
        } catch (Exception e) {
            exibirMSG(e.getMessage());
            e.printStackTrace();
        }
    }

    private void removerNota(final Integer position) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

            //Titulo do AlertDialog
            //dialog.setTitle("Title");
            //Mensagem do AlertDialog
            dialog.setMessage("Deseja excluir esta nota?");

        //Opcionais:
            //autoriza o cancelamento do AlertDialog clicando fora do AlertDialog
            dialog.setCancelable(false);
            //Seta o icone do AlertDialog
            dialog.setIcon(android.R.drawable.ic_delete);
        //

            dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    exibirMSG("Exclusão cancelada!");
                }
            });

            dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Integer idToRemove = arrayListId.get(position);
                    sqLiteDatabase.execSQL("DELETE FROM notes WHERE id=" + idToRemove);
                    exibirMSG("Nota excluída com sucesso!");
                    listarNotas();
                }
            });

            dialog.create();
            dialog.show();

        } catch (Exception e) {
            exibirMSG(e.getMessage());
            e.printStackTrace();
        }
    }

    private void exibirMSG(String msg) {
        try {
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            exibirMSG(e.getMessage());
            e.printStackTrace();
        }
    }


}
