/*
package br.com.legasist.controlevendas.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.legasist.controlevendas.R;

*/
/**
 * Created by ovs on 06/06/2017.
 *//*


public class OperacoesDB extends SQLiteOpenHelper {
    protected static final String TAG = "sql";
    //Nome do banco
    public static final String NOME_BANCO = "controle_vendas";
    public static final int VERSAO_BANCO = 31;


    public OperacoesDB(Context context) {
        //context, nome do banco, factory, versão
        super(context, NOME_BANCO, null, VERSAO_BANCO);
        //super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Criando a tabela categoria...");
        db.execSQL("create table if not exists categoria (_id integer primary key autoincrement, categoria text); ");
        Log.d(TAG, "Tabela categoria criada com sucesso.");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Caso mude a versão do banco de dados, podemos executar um SQL aqui
        Log.d(TAG, "Criando a tabela categoria...");
        db.execSQL("drop table categoria ; ");
        db.execSQL("create table if not exists categoria (_id integer primary key autoincrement, categoria text); ");
        Log.d(TAG, "Tabela categoria criada com sucesso.");
    }

    //insere uma nova categoria, ou atualiza se existe
    public long save(Categoria categoria){
        long id = categoria.id;
        SQLiteDatabase db = getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("categoria", categoria.categoria);
            if(id != 0){
                String _id = String.valueOf(categoria.id);
                String[] whereArgs = new String[]{_id};
                //update categoria set values = ...where _id=?
                int count = db.update("categoria", values, "_id=?", whereArgs);
                return count;
            }else{
                //insert into categoria values(...)
                id = db.insert("categoria", "", values);
                return id;
            }
        }finally {
            db.close();
        }
    }

    //deleta a categoria
    public int delete (Categoria categoria){
        SQLiteDatabase db = getWritableDatabase();
        try {
            //delete from categoria where _id=?
            int count = db.delete("categoria", "_id=?", new String[]{String.valueOf(categoria.id)});
            Log.i(TAG, "Deletou [" + count + "] registro.");
            return count;
        }finally {
            db.close();
        }
    }

    //Deleta os clientes do tipo fornecido
    //controlevendas
    *//*
*/
/*public int deleteCarrosByTipo(String tipo){
        SQLiteDatabase db = getWritableDatabase();
        try{
            //delete from carro where tipo=?
            int count = db.delete("carro", "tipo=?", new String[]{tipo});
            Log.i(TAG, "Deletou [" + count + "] registros.");
            return count;
        }finally {
            db.close();
        }
    }*//*
*/
/*

    //Deleta as categorias
    public int deleteTodasCategorias(){
        SQLiteDatabase db = getWritableDatabase();
        try{
            //delete from categoria
            int count = db.delete("categoria", null, null);
            Log.i(TAG, "Deletou [" + count + "] registros.");
            return count;
        }finally {
            db.close();
        }
    }

    //consulta a lista com todas as categorias
    public List<Categoria> findAll(){
        SQLiteDatabase db = getWritableDatabase();
        try {
            //select * from categoria
            Cursor c = db.query("categoria", null, null, null, null, null, "categoria", null);
            return toList(c);
        }finally {
            db.close();
        }
    }


    //Consulta a categoria pelo id
    public Categoria findById(Integer id){
        SQLiteDatabase db = getWritableDatabase();
        try{
            //select * from categoria where id = ?
            Cursor c = db.query("categoria", null, "_id = '" + id + "'", null, null, null, null);
            return toList(c).get(0);
        }finally {
            db.close();
        }

    }

    //Lê o cursor e cria a lista de categorias
    private List<Categoria> toList(Cursor c) {
        List<Categoria> categorias = new ArrayList<Categoria>();
        if(c.moveToFirst()){
            do{
                Categoria categoria = new Categoria();
                categorias.add(categoria);
                //recupera os atributos de categoria
                categoria.id = c.getLong(c.getColumnIndex("_id"));
                categoria.categoria = c.getString(c.getColumnIndex("categoria"));
            }while (c.moveToNext());
        }
        return categorias;
    }
*//*


}
*/
