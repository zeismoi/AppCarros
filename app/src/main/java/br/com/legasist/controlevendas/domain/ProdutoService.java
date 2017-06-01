package br.com.legasist.controlevendas.domain;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import livroandroid.lib.utils.FileUtils;
import livroandroid.lib.utils.HttpHelper;
import livroandroid.lib.utils.IOUtils;

/**
 * Created by ovs on 01/06/2017.
 */

public class ProdutoService {
    private static final boolean LOG_ON = false;
    private static final String TAG = "ProdutoService";
    private static final String URL = "http://www.livroandroid.com.br/livro/carros/carros_{tipo}.json";

    public static List<Produto> getProdutos(Context context, int tipo, boolean refresh) throws IOException {
        //busca os produtos no banco de dados (somente se refresh = false)
        List<Produto> produtos = !refresh ? getProdutosFromBanco(context) : null;
        if(produtos != null && produtos.size()>0){
            //retorna os produtos encontrados do banco
            return produtos;
        }
        //se não encontrar, busca no WebService
        produtos = getProdutosFromWebService(context, tipo);
        return produtos;

        /*String tipoString = getTipo(tipo);
        String url = URL.replace("{tipo}", tipoString);
        //Faz a requisição HTTP no servidor e retorna a String com o conteúdo
        HttpHelper http = new HttpHelper();
        String json = http.doGet(url);
        List<Carro> carros = parserJSON(context, json);
        salvarArquivoNaMemoriaInterna(context, url, json);*/

    }

    private static List<Produto> getProdutosFromWebService(Context context, int tipo) throws IOException {
        String tipoString = getTipo(tipo);
        String url = URL.replace("{tipo}", tipoString);
        Log.d(TAG, "URL: " + url);
        //Faz a requisição HTTP no servidor e retorna a String com o conteúdo
        HttpHelper http = new HttpHelper();
        String json = http.doGet(url);
        List<Produto> produtos = parserJSON(context, json);
        //Depois de buscar, salva os produtos
        salvarProdutos(context, produtos);
        return produtos;
    }

    //salva os produtos no banco de dados
    private static void salvarProdutos(Context context, List<Produto> produtos) {
        ProdutoDB db = new ProdutoDB(context);
        try{
            //Deleta os produtos pelo tipo para limpar o banco
           // String tipoString = getTipo(tipo);
            db.deleteTodosProdutos();
            //salva todos os produtos
            for (Produto p : produtos){
                //c.tipo = tipoString;
                Log.d(TAG, "Salvando o produto: " + p.nome);
                db.save(p);
            }
        }finally {
            db.close();
        }

    }

    private static List<Produto> getProdutosFromBanco(Context context) throws IOException {
        ProdutoDB db = new ProdutoDB(context);
        try {
            //String tipoString = getTipo(tipo);
            List<Produto> produtos = db.findAll();
            Log.d(TAG, "Retornando " + produtos.size() + " produtos do banco");
            return produtos;
        }finally {
            db.close();
        }
    }

    private static void salvarArquivoNaMemoriaInterna(Context context, String url, String json) {
        String fileName = url.substring(url.lastIndexOf("/")+1);
        File file = FileUtils.getFile(context, fileName);
        IOUtils.writeString(file,json);
        Log.d(TAG, "Arquivo salvo: " + file);
    }

    //Converte a constante para String, para criar a URL do wen service
    private static String getTipo(int tipo) {
        if (tipo == br.com.legasist.controlevendas.R.string.classicos){
            return "classicos";
        }else if (tipo == br.com.legasist.controlevendas.R.string.esportivos){
            return "esportivos";
        }
        return "luxo";
    }

    //FAZ A LEITURA DO ARQUIVO QUE ESTÁ NA PASTA /res/raw
    private static String readFile(Context context, int tipo) throws IOException {
        if(tipo == br.com.legasist.controlevendas.R.string.classicos){
            return FileUtils.readRawFileString(context, br.com.legasist.controlevendas.R.raw.carros_classicos, "UTF-8");
        }else if(tipo == br.com.legasist.controlevendas.R.string.esportivos){
            return FileUtils.readRawFileString(context, br.com.legasist.controlevendas.R.raw.carros_esportivos, "UTF-8");
        }
        return FileUtils.readRawFileString(context, br.com.legasist.controlevendas.R.raw.carros_luxo, "UTF-8");
    }

    //controlevendas
    //Faz o parser do XML e cria a lista de carros
    /*private static List<Carro> parserXML(Context context, String xml){
        List<Carro> carros = new ArrayList<Carro>();
        Element root = XMLUtils.getRoot(xml, "UTF-8");
        //Lê todas as tags<carro>
        List<Node> nodeCarros = XMLUtils.getChildren(root, "carro") ;
        //insere cada carro na lista
        for (Node node :nodeCarros){
            Carro c = new Carro();
            //Lê as informações de cada carro
            c.nome = XMLUtils.getText(node, "nome");
            c.desc = XMLUtils.getText(node, "desc");
            c.urlFoto = XMLUtils.getText(node, "url_foto");
            c.urlInfo = XMLUtils.getText(node, "url_info");
            c.urlVideo = XMLUtils.getText(node, "url_video");
            c.latitude = XMLUtils.getText(node, "latitude");
            c.longitude = XMLUtils.getText(node, "longitude");

            if(LOG_ON){
                Log.d(TAG, "Carro" + c.nome + " > " + c.urlFoto);
            }
            carros.add(c);
        }
        if (LOG_ON){
            Log.d(TAG, carros.size() + " encontrados");
        }
        return carros;
    }*/

    //Faz o parser do JSON e cria a lista de produtos
    private static List<Produto > parserJSON(Context context, String json) throws IOException {
        List<Produto> produtos = new ArrayList<Produto>();
        /*try{
            JSONObject root = new JSONObject(json);
            JSONObject obj = root.getJSONObject("produtos");
            JSONArray jsonProdutos = obj.getJSONArray("produto");
            //insere cada produto na lista
            for (int i=0; i < jsonProdutos.length(); i++){
                JSONObject jsonProduto = jsonProdutos.getJSONObject(i);
                Produto p = new Produto();
                //Lê as informações de cada produto
                p.nome = jsonProduto.optString("nome");
                p.codigoBarras = jsonProduto.optString("codigoBarras");
                p.estoqueAtual = jsonProduto.optDouble("estoqueAtual");
                p.estoqueMin = jsonProduto.optDouble("estoqueMin");
                p.precoCusto = jsonProduto.optDouble("precoCusto");
                p.precoVenda = jsonProduto.optDouble("precoVenda");
                if(LOG_ON){
                    Log.d(TAG, "Produto" + p.nome);
                }
                produtos.add(p);
            }
            if (LOG_ON){
                Log.d(TAG, produtos.size() + " encontrados");
            }

        } catch (JSONException e) {
            throw  new IOException(e.getMessage(), e);
        }*/
        return produtos;
    }
}
