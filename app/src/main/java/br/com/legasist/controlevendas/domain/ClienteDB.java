package br.com.legasist.controlevendas.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ovs on 18/05/2017.
 */

public class ClienteDB extends OperacoesDB {

    public ClienteDB(Context context) {
        //context, nome do banco, factory, versão
        //super(context, NOME_BANCO, null, VERSAO_BANCO);
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Caso mude a versão do banco de dados, podemos executar um SQL aqui
    }

    //insere um novo cliente, ou atualiza se existe
    public long save(Cliente cliente){
        long id = cliente.id;
        SQLiteDatabase db = getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("nome", cliente.nome);
            values.put("endereco", cliente.endereco);
            values.put("cidade", cliente.cidade);
            values.put("uf", cliente.uf);
            values.put("celular", cliente.celular);
            values.put("email", cliente.email);
            values.put("latitude", cliente.latitude);
            values.put("longitude", cliente.longitude);
            if(id != 0){
                String _id = String.valueOf(cliente.id);
                String[] whereArgs = new String[]{_id};
                //update cliente set values = ...where _id=?
                int count = db.update("cliente", values, "_id=?", whereArgs);
                return count;
            }else{
                //insert into cliente values(...)
                id = db.insert("cliente", "", values);
                return id;
            }
        }finally {
            db.close();
        }
    }

    //deleta o cliente
    public int delete (Cliente cliente){
        SQLiteDatabase db = getWritableDatabase();
        try {
            //delete from cliente where _id=?
            int count = db.delete("cliente", "_id=?", new String[]{String.valueOf(cliente.id)});
            Log.i(TAG, "Deletou [" + count + "] registro.");
            return count;
        }finally {
            db.close();
        }
    }

    //Deleta os clientes do tipo fornecido
    //controlevendas
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
    }*/

    //Deleta os clientes
    public int deleteTodosClientes(){
        SQLiteDatabase db = getWritableDatabase();
        try{
            //delete from cliente
            int count = db.delete("cliente", null, null);
            Log.i(TAG, "Deletou [" + count + "] registros.");
            return count;
        }finally {
            db.close();
        }
    }

    //consulta a lista com todos os clientes
    public List<Cliente> findAll(){
        SQLiteDatabase db = getWritableDatabase();
        try {
            //select * from cliente
            Cursor c = db.query("cliente", null, null, null, null, null, "nome", null);
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
    //Lê o cursor e cria a lista de clientes
    private List<Cliente> toList(Cursor c) {
        List<Cliente> clientes = new ArrayList<Cliente>();
        if(c.moveToFirst()){
            do{
                Cliente cliente = new Cliente();
                clientes.add(cliente);
                //recupera os atributos de cliente
                cliente.id = c.getLong(c.getColumnIndex("_id"));
                cliente.nome = c.getString(c.getColumnIndex("nome"));
                cliente.endereco = c.getString(c.getColumnIndex("endereco"));
                cliente.cidade = c.getString(c.getColumnIndex("cidade"));
                cliente.uf = c.getString(c.getColumnIndex("uf"));
                cliente.celular = c.getString(c.getColumnIndex("celular"));
                cliente.email = c.getString(c.getColumnIndex("email"));
                cliente.latitude = c.getDouble(c.getColumnIndex("latitude"));
                cliente.longitude = c.getDouble(c.getColumnIndex("longitude"));
            }while (c.moveToNext());
        }
        return clientes;
    }


}
