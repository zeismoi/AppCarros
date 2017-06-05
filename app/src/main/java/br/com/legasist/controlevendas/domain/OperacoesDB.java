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

public class OperacoesDB extends SQLiteOpenHelper {
    protected static final String TAG = "sql";
    //Nome do banco
    public static final String NOME_BANCO = "controle_vendas";
    private static final int VERSAO_BANCO = 5;

    public OperacoesDB(Context context) {
        //context, nome do banco, factory, versão
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Criando a tabela cliente...");
        db.execSQL("create table if not exists cliente (_id integer primary key autoincrement, nome tex, endereco text, cidade text, " +
                "uf text, celular text, email text, latitude text, longitude text); ");
        Log.d(TAG, "Tabela cliente criada com sucesso.");

        Log.d(TAG, "Criando a tabela fornecedor...");
        db.execSQL("create table if not exists fornecedor (_id integer primary key autoincrement, nome tex, endereco text, cidade text, " +
                "uf text, telefone text, email text, latitude text, longitude text); ");
        Log.d(TAG, "Tabela fornecedor criada com sucesso.");

        Log.d(TAG, "Criando a tabela produto...");
        db.execSQL("create table if not exists produto (_id integer primary key autoincrement, nome text, codigo_barras text, estoque_atual Numeric(10,2), " +
                "estoque_min Numeric(10,2), preco_custo Numeric(10,2), preco_venda Numeric(10,2), id_fornecedor int, FOREIGN KEY (id_fornecedor) REFERENCES fornecedor(_id); ");
        Log.d(TAG, "Tabela produto criada com sucesso.");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Caso mude a versão do banco de dados, podemos executar um SQL aqui
        Log.d(TAG, "Criando a tabela produto...");
        db.execSQL("create table if not exists produto (_id integer primary key autoincrement, nome text, codigo_barras text, estoque_atual Numeric(10,2), " +
                "estoque_min Numeric(10,2), preco_custo Numeric(10,2), preco_venda Numeric(10,2), id_fornecedor int); ");
        Log.d(TAG, "Tabela produto criada com sucesso.");
    }

    //Executa um SQL
    public void execSQL(String sql){
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL(sql);
        }finally {
            db.close();
        }
    }

    //Executa um SQL
    public void execSQL(String sql, Object[] args){
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL(sql, args);
        }finally {
            db.close();
        }
    }
}
