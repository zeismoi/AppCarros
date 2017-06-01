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
 * Created by ovs on 01/06/2017.
 */

public class ProdutoDB extends OperacoesDB {


    public ProdutoDB(Context context) {
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
        Log.d(TAG, "Criando a tabela produto...");
        db.execSQL("create table if not exists produto (_id integer primary key autoincrement, nome text, codigo_barras text, estoque_atual Numeric(10,2), " +
                "estoque_min Numeric(10,2), preco_custo Numeric(10,2), preco_venda Numeric(10,2), id_fornecedor int); ");
        Log.d(TAG, "Tabela produto criada com sucesso.");
    }

    //insere um novo produto, ou atualiza se existe
    public long save(Produto produto){
        long id = produto.id;
        SQLiteDatabase db = getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put("nome", produto.nome);
            values.put("codigo_barras", produto.codigoBarras);
            values.put("estoque_atual", produto.estoqueAtual);
            values.put("estoque_min", produto.estoqueMin);
            values.put("preco_custo", produto.precoCusto);
            values.put("preco_venda", produto.precoVenda);
    //        values.put("id_fornecedor", produto.fornecedor);
            if(id != 0){
                String _id = String.valueOf(produto.id);
                String[] whereArgs = new String[]{_id};
                //update produto set values = ...where _id=?
                int count = db.update("produto", values, "_id=?", whereArgs);
                return count;
            }else{
                //insert into produtos values(...)
                id = db.insert("produto", "", values);
                return id;
            }
        }finally {
            db.close();
        }
    }

    //deleta o produto
    public int delete (Produto produto){
        SQLiteDatabase db = getWritableDatabase();
        try {
            //delete from produto where _id=?
            int count = db.delete("produto", "_id=?", new String[]{String.valueOf(produto.id)});
            Log.i(TAG, "Deletou [" + count + "] registro.");
            return count;
        }finally {
            db.close();
        }
    }

    //Deleta os produtos
    public int deleteTodosProdutos(){
        SQLiteDatabase db = getWritableDatabase();
        try{
            //delete from produto
            int count = db.delete("produto", null, null);
            Log.i(TAG, "Deletou [" + count + "] registros.");
            return count;
        }finally {
            db.close();
        }
    }

    //consulta a lista com todos os produtos
    public List<Produto> findAll(){
        SQLiteDatabase db = getWritableDatabase();
        try {
            //select * from produto
            Cursor c = db.query("produto", null, null, null, null, null, "nome", null);
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
    //Lê o cursor e cria a lista de produtos
    private List<Produto> toList(Cursor c) {
        List<Produto> produtos = new ArrayList<Produto>();
        if(c.moveToFirst()){
            do{
                Produto produto = new Produto();
                produtos.add(produto);
                //recupera os atributos de produto
                produto.id = c.getLong(c.getColumnIndex("_id"));
                produto.nome = c.getString(c.getColumnIndex("nome"));
                produto.codigoBarras = c.getString(c.getColumnIndex("codigo_barras"));
                produto.estoqueAtual = Double.parseDouble(c.getString(c.getColumnIndex("estoque_atual")));
                produto.estoqueMin = Double.parseDouble(c.getString(c.getColumnIndex("estoque_min")));
                produto.precoCusto = Double.parseDouble(c.getString(c.getColumnIndex("preco_custo")));
                produto.precoVenda = Double.parseDouble(c.getString(c.getColumnIndex("preco_venda")));
            }while (c.moveToNext());
        }
        return produtos;
    }


}
