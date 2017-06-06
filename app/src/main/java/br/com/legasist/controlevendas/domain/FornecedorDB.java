package br.com.legasist.controlevendas.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.FloatProperty;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ovs on 05/06/2017.
 */

public class FornecedorDB extends OperacoesDB {


    public FornecedorDB(Context context) {
        super(context);
        //context, nome do banco, factory, versão
     //   super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Caso mude a versão do banco de dados, podemos executar um SQL aqui
        Log.d(TAG, "Criando a tabela fornecedor...");
        db.execSQL("create table if not exists fornecedor (_id integer primary key autoincrement, nome tex, endereco text, cidade text, " +
                "uf text, telefone text, email text, latitude text, longitude text); ");
        Log.d(TAG, "Tabela fornecedor criada com sucesso.");
    }

    //insere um novo fornecedor, ou atualiza se existe
    public long save(Fornecedor fornecedor){
        long id = fornecedor.id_fornecedor;
        SQLiteDatabase db = getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("nome", fornecedor.nome);
            values.put("endereco", fornecedor.endereco);
            values.put("cidade", fornecedor.cidade);
            values.put("uf", fornecedor.uf);
            values.put("telefone", fornecedor.telefone);
            values.put("email", fornecedor.email);
            values.put("latitude", fornecedor.latitude);
            values.put("longitude", fornecedor.longitude);
    //        values.put("id_fornecedor", produto.fornecedor);
            if(id != 0){
                String _id = String.valueOf(fornecedor.id_fornecedor);
                String[] whereArgs = new String[]{_id};
                //update fornecedor set values = ...where _id=?
                int count = db.update("fornecedor", values, "_id=?", whereArgs);
                return count;
            }else{
                //insert into fornecedor values(...)
                id = db.insert("fornecedor", "", values);
                return id;
            }
        }finally {
            db.close();
        }
    }

    //deleta o produto
    public int delete (Fornecedor fornecedor){
        SQLiteDatabase db = getWritableDatabase();
        try {
            //delete from fornecedor where _id=?
            int count = db.delete("fornecedor", "_id=?", new String[]{String.valueOf(fornecedor.id_fornecedor)});
            Log.i(TAG, "Deletou [" + count + "] registro.");
            return count;
        }finally {
            db.close();
        }
    }

    //Deleta os fornecedores
    public int deleteTodosFornecedores(){
        SQLiteDatabase db = getWritableDatabase();
        try{
            //delete from produto
            int count = db.delete("fornecedor", null, null);
            Log.i(TAG, "Deletou [" + count + "] registros.");
            return count;
        }finally {
            db.close();
        }
    }

    //consulta a lista com todos os fornecedores
    public List<Fornecedor> findAll(){
        SQLiteDatabase db = getWritableDatabase();
        try {
            //select * from produto
            Cursor c = db.query("fornecedor", null, null, null, null, null, "nome", null);
            return toList(c);
        }finally {
            db.close();
        }
    }

    //Consulta o carro pelo tipo
    //controlevendas
    /*public List<Carro> findAllByTipo(String tipo){
        SQLiteDatabase db = getWritableDatabase();
        try{
            //select * from carro where tipo = ?
            Cursor c = db.query("carro", null, "tipo = '" + tipo + "'", null, null, null, null);
            return toList(c);
        }finally {
            db.close();
        }

    }
*/
    //Lê o cursor e cria a lista de fornecedores
    private List<Fornecedor> toList(Cursor c) {
        List<Fornecedor> fornecedores = new ArrayList<Fornecedor>();
        if(c.moveToFirst()){
            do{
                Fornecedor fornecedor = new Fornecedor();
                fornecedores.add(fornecedor);
                //recupera os atributos de fornecedor
                fornecedor.id_fornecedor = c.getLong(c.getColumnIndex("_id"));
                fornecedor.nome = c.getString(c.getColumnIndex("nome"));
                fornecedor.endereco = c.getString(c.getColumnIndex("endereco"));
                fornecedor.cidade = c.getString(c.getColumnIndex("cidade"));
                fornecedor.uf = c.getString(c.getColumnIndex("uf"));
                fornecedor.telefone = c.getString(c.getColumnIndex("telefone"));
                fornecedor.email = c.getString(c.getColumnIndex("email"));
                fornecedor.latitude = Double.parseDouble(c.getString(c.getColumnIndex("latitude")));
                fornecedor.longitude = Double.parseDouble(c.getString(c.getColumnIndex("longitude")));
            }while (c.moveToNext());
        }
        return fornecedores;
    }


}
