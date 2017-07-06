package br.com.legasist.controlevendas.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ovs on 18/05/2017.
 */

public class OperacoesDB extends SQLiteOpenHelper{
    protected static final String TAG = "sql";
    //Nome do banco
    public static final String NOME_BANCO = "controle_vendas";
    public static final int VERSAO_BANCO = 39;

    public OperacoesDB(Context context) {
        //context, nome do banco, factory, versão
        super(context, NOME_BANCO, null, VERSAO_BANCO);

    }

    //MÉTODOS COMUNS***********************************************

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Criando a tabela cliente...");
        db.execSQL("create table if not exists cliente (_id integer primary key autoincrement, nome text, endereco text, cidade text, " +
                "uf text, celular text, email text, latitude text, longitude text); ");
        Log.d(TAG, "Tabela cliente criada com sucesso.");

        Log.d(TAG, "Criando a tabela fornecedor...");
        db.execSQL("create table if not exists fornecedor (_id integer primary key autoincrement, nome text, endereco text, cidade text, " +
                "uf text, telefone text, email text, latitude text, longitude text); ");
        Log.d(TAG, "Tabela fornecedor criada com sucesso.");

        Log.d(TAG, "Criando a tabela produto...");
        db.execSQL("create table if not exists produto (_id integer primary key autoincrement, nome text, codigo_barras text, estoque_atual Numeric(10,2), " +
                "estoque_min Numeric(10,2), preco_custo Numeric(10,2), preco_venda Numeric(10,2), categoria text, foto BLOB, id_categoria integer, id_fornecedor integer, " +
                "FOREIGN KEY (id_categoria) REFERENCES categoria(_id), " +
                "FOREIGN KEY (id_fornecedor) REFERENCES fornecedor(_id)); ");
        Log.d(TAG, "Tabela produto criada com sucesso.");

        Log.d(TAG, "Criando a tabela categoria...");
        db.execSQL("create table if not exists categoria (_id integer primary key autoincrement, categoria text); ");
        Log.d(TAG, "Tabela categoria criada com sucesso.");

        Log.d(TAG, "Criando a tabela venda...");
        db.execSQL("create table if not exists venda (_id integer primary key autoincrement, data Numeric, id_cliente integer, valor Numeric, desconto Numeric, total Numeric, " +
                "FOREIGN KEY (id_cliente) REFERENCES cliente(_id)); ");
        Log.d(TAG, "Tabela venda criada com sucesso.");

        Log.d(TAG, "Criando a tabela itensVenda...");
        db.execSQL("create table if not exists itens_venda (_id integer primary key autoincrement, quantidade Numeric, id_venda integer, id_produto integer, " +
                "FOREIGN KEY (id_venda) REFERENCES venda(_id), " +
                "FOREIGN KEY (id_produto) REFERENCES produto(_id)); ");
        Log.d(TAG, "Tabela itens_venda criada com sucesso.");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Caso mude a versão do banco de dados, podemos executar um SQL aqui
        /*db.execSQL("drop table if exists cliente ; ");
        db.execSQL("drop table if exists fornecedor ; ");
        db.execSQL("drop table  if exists produto ; ");
        db.execSQL("drop table  if exists categoria ; ");
        db.execSQL("drop table  if exists itens_venda ; ");
        db.execSQL("drop table  if exists venda ; ");*/

        Log.d(TAG, "Criando a tabela cliente...");
        db.execSQL("create table if not exists cliente (_id integer primary key autoincrement, nome text, endereco text, cidade text, " +
                "uf text, celular text, email text, latitude text, longitude text); ");
        Log.d(TAG, "Tabela cliente criada com sucesso.");

        Log.d(TAG, "Criando a tabela fornecedor...");
        db.execSQL("create table if not exists fornecedor (_id integer primary key autoincrement, nome text, endereco text, cidade text, " +
                "uf text, telefone text, email text, latitude text, longitude text); ");
        Log.d(TAG, "Tabela fornecedor criada com sucesso.");

        Log.d(TAG, "Criando a tabela produto...");
        db.execSQL("create table if not exists produto (_id integer primary key autoincrement, nome text, codigo_barras text, estoque_atual Numeric(10,2), " +
                "estoque_min Numeric(10,2), preco_custo Numeric(10,2), preco_venda Numeric(10,2), foto BLOB, categoria text, id_categoria integer, id_fornecedor integer, " +
                "FOREIGN KEY (id_categoria) REFERENCES categoria(_id), " +
                "FOREIGN KEY (id_fornecedor) REFERENCES fornecedor(_id)); ");
        Log.d(TAG, "Tabela produto criada com sucesso.");

        Log.d(TAG, "Criando a tabela categoria...");
        db.execSQL("create table if not exists categoria (_id integer primary key autoincrement, categoria text); ");
        Log.d(TAG, "Tabela categoria criada com sucesso.");

        Log.d(TAG, "Criando a tabela venda...");
        db.execSQL("create table if not exists venda (_id integer primary key autoincrement, data Numeric, id_cliente integer, valor Numeric, desconto Numeric, total Numeric, " +
                "FOREIGN KEY (id_cliente) REFERENCES cliente(_id)); ");
        Log.d(TAG, "Tabela venda criada com sucesso.");

        Log.d(TAG, "Criando a tabela itensVenda...");
        db.execSQL("create table if not exists itens_venda (_id integer primary key autoincrement, quantidade Numeric, id_venda integer, id_produto integer, " +
                "FOREIGN KEY (id_venda) REFERENCES venda(_id), " +
                "FOREIGN KEY (id_produto) REFERENCES produto(_id)); ");
        Log.d(TAG, "Tabela itens_venda criada com sucesso.");
    }


    //deleta
    public int delete (String tabela, long id){
        SQLiteDatabase db = getWritableDatabase();
        try {
            //delete from cliente where _id=?
            int count = db.delete(tabela, "_id=?", new String[]{String.valueOf(id)});
            Log.i(TAG, "Deletou [" + count + "] registro.");
            return count;
        }finally {
            db.close();
        }
    }


    //Deleta TODOS REGISTROS
    public int deleteTodos(String tabela){
        SQLiteDatabase db = getWritableDatabase();
        try{
            //delete from tabela
            int count = db.delete(tabela, null, null);
            Log.i(TAG, "Deletou [" + count + "] registros.");
            return count;
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

//FIM  *****   MÉTODOS COMUNS***********************************************



//MÉTODOS DE CLIENTES*******************************************

    //insere um novo cliente, ou atualiza se existe
    public long saveCliente(Cliente cliente){
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

    //consulta a lista com todos os clientes
    public List<Cliente> findAllClientes(){
        SQLiteDatabase db = getWritableDatabase();
        try {
            //select * from cliente
            Cursor c = db.query("cliente", null, null, null, null, null, "nome", null);
            return toListClientes(c);
        }finally {
            db.close();
        }
    }

    //Consulta O CLIENTE pelo id
    public Cliente findClienteById(long id){
        SQLiteDatabase db = getWritableDatabase();
        try{
            //select * from cliente where id = ?
            Cursor c = db.query("cliente", null, "_id = '" + id + "'", null, null, null, null);
            return toListClientes(c).get(0);
        }finally {
            db.close();
        }

    }

    //Consulta os clientes pelo nome
    public List<Cliente> findClienteByNome(String nome){
        SQLiteDatabase db = getWritableDatabase();
        try{
            //select * from fornecedor where id = ?
            Cursor c = db.query("cliente", null, "nome like '%" + nome + "%'", null, null, null, "nome");
            return toListClientes(c);
        }finally {
            db.close();
        }
    }

    //Lê o cursor e cria a lista de clientes
    private List<Cliente> toListClientes(Cursor c) {
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

//***FIM   **   MÉTODOS DE CLIENTES*******************************************


//MÉTODOS DE FORNECEDORES*****************************************************

    //insere um novo fornecedor, ou atualiza se existe
    public long saveFornecedor(Fornecedor fornecedor){
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


    //consulta a lista com todos os fornecedores
    public List<Fornecedor> findAllFornecedores(){
        SQLiteDatabase db = getWritableDatabase();
        try {
            //select * from produto
            Cursor c = db.query("fornecedor", null, null, null, null, null, "nome", null);
            return toListFornecedores(c);
        }finally {
            db.close();
        }
    }

    //Consulta os fornecedores pelo nome
    public List<Fornecedor> findFornecedorByNome(String nome){
        SQLiteDatabase db = getWritableDatabase();
        try{
            //select * from fornecedor where id = ?
            Cursor c = db.query("fornecedor", null, "nome like '%" + nome + "%'", null, null, null, "nome");
            return toListFornecedores(c);
        }finally {
            db.close();
        }
    }

    //Consulta o fornecedor pelo id
    public Fornecedor findFornecedorById(long id){
        SQLiteDatabase db = getWritableDatabase();
        try{
            //select * from fornecedor where id = ?
            Cursor c = db.query("fornecedor", null, "_id = '" + id + "'", null, null, null, null);
            return toListFornecedores(c).get(0);
        }finally {
            db.close();
        }

    }


    //Lê o cursor e cria a lista de fornecedores
    private List<Fornecedor> toListFornecedores(Cursor c) {
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

//**FIM ***** MÉTODOS DE FORNECEDORES*******************************************


//MÉTODOS DE PRODUTOS*****************************************************

    //insere um novo produto, ou atualiza se existe
    public long saveProduto(Produto produto){
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
  //          values.put("categoria", produto.categoria);
            values.put("id_categoria", produto.categ);
            values.put("id_fornecedor", produto.fornecedor);
            values.put("foto", produto.foto);
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

    //consulta a lista com todos os produtos
    public List<Produto> findAllProdutos(){
        SQLiteDatabase db = getWritableDatabase();
        try {
            //select * from produto
            Cursor c = db.query("produto", null, null, null, null, null, "nome", null);
            return toListProdutos(c);
        }finally {
            db.close();
        }
    }

    //Consulta os produtos pelo nome
    public List<Produto> findProdutoByNome(String nome){
        SQLiteDatabase db = getWritableDatabase();
        try{
            //select * from produto where nome like ?
            Cursor c = db.query("produto", null, "nome like '%" + nome + "%'", null, null, null, "nome");
            return toListProdutos(c);
        }finally {
            db.close();
        }
    }

    //Lê o cursor e cria a lista de produtos
    private List<Produto> toListProdutos(Cursor c) {
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
                produto.categoria = c.getString(c.getColumnIndex("categoria"));

                if(produto.foto != null) {
                    produto.foto = c.getBlob(c.getColumnIndex("foto"));
                }

                if(c.getString(c.getColumnIndex("id_categoria")) != null) {
                    produto.categ = Long.parseLong(c.getString(c.getColumnIndex("id_categoria")));
                }
                if (c.getString(c.getColumnIndex("id_fornecedor")) != null) {
                    produto.fornecedor = Long.parseLong(c.getString(c.getColumnIndex("id_fornecedor")));
                }
            }while (c.moveToNext());
        }
        return produtos;
    }

//***FIM  **   MÉTODOS DE PRODUTOS*****************************************************


//MÉTODOS DE CATEGORIAS *****************************************************************

    //insere uma nova categoria, ou atualiza se existe
    public long saveCategoria(Categoria categoria){
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

    //consulta a lista com todas as categorias
    public List<Categoria> findAllCategorias(){
        SQLiteDatabase db = getWritableDatabase();
        try {
            //select * from categoria
            Cursor c = db.query("categoria", null, null, null, null, null, "categoria", null);
            return toListCategorias(c);
        }finally {
            db.close();
        }
    }


    //Consulta a categoria pelo id
    public Categoria findCategoriaById(long id){
        SQLiteDatabase db = getWritableDatabase();
        try{
            //select * from categoria where id = ?
            Cursor c = db.query("categoria", null, "_id = '" + id + "'", null, null, null, null);
            return toListCategorias(c).get(0);
        }finally {
            db.close();
        }

    }

    //Lê o cursor e cria a lista de categorias
    private List<Categoria> toListCategorias(Cursor c) {
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

//**FIM  ***  MÉTODOS DE CATEGORIAS *****************************************************


//MÉTODOS DE VENDAS*****************************************************


    //insere uma nova venda, ou atualiza se existe
    public long saveVenda(Venda venda){
        long id = venda.id;
        SQLiteDatabase db = getWritableDatabase();
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = new Date();
            ContentValues values = new ContentValues();
            values.put("data", dateFormat.format(venda.data));
            values.put("valor", venda.valor);
            values.put("desconto", venda.desconto);
            values.put("total", venda.total);
            values.put("id_cliente", venda.cliente);
            if(id != 0){
                String _id = String.valueOf(venda.id);
                String[] whereArgs = new String[]{_id};
                //update venda set values = ...where _id=?
                int count = db.update("venda", values, "_id=?", whereArgs);
                return count;
            }else{
                //insert into venda values(...)
                id = db.insert("venda", "", values);
                return id;
            }
        }finally {
            db.close();
        }
    }

    //consulta a lista com todas as vendas
    public List<Venda> findAllVendas() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            //select * from venda
            Cursor c = db.query("venda", null, null, null, null, null, "data", null);
            return toListVendas(c);
        }finally {
            db.close();
        }
    }

    //Lê o cursor e cria a lista de vendas
    private List<Venda> toListVendas(Cursor c) {
        List<Venda> vendas = new ArrayList<Venda>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        if(c.moveToFirst()){
            do{
                Venda venda = new Venda();
                vendas.add(venda);
                //recupera os atributos de venda
                venda.id = c.getLong(c.getColumnIndex("_id"));
                try {
                    venda.data = dateFormat.parse(c.getString(c.getColumnIndex("data")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                venda.valor = Double.parseDouble(c.getString(c.getColumnIndex("valor")));
                venda.desconto = Double.parseDouble(c.getString(c.getColumnIndex("desconto")));
                venda.total = Double.parseDouble(c.getString(c.getColumnIndex("total")));

                if((c.getString(c.getColumnIndex("id_cliente")) != null) && (c.getLong(c.getColumnIndex("id_cliente")) != 0)) {
                    venda.cliente = Long.parseLong(c.getString(c.getColumnIndex("id_cliente")));
                }
            }while (c.moveToNext());
        }
        return vendas;
    }


//***FIM  **   MÉTODOS DE VENDAS*****************************************************

}
