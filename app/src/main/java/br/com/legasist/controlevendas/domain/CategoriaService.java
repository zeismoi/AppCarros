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
 * Created by ovs on 29/05/2017.
 */

public class CategoriaService {
    private static final boolean LOG_ON = false;
    private static final String TAG = "CategoriaService";
    private static final String URL = "http://www.livroandroid.com.br/livro/carros/carros_{tipo}.json";

    public static List<Categoria> getCategorias(Context context, int tipo, boolean refresh) throws IOException {
        //busca as categorias no banco de dados (somente se refresh = false)
        //List<Cliente> clientes = !refresh ? getCategoriasFromBanco(context) : null;
        //sempre buscar no banco por enquanto
        List<Categoria> categorias = !false ? getCategoriasFromBanco(context) : null;
        if(categorias != null && categorias.size()>0){
            //retorna as categorias encontrados do banco
            return categorias;
        }
        //se não encontrar, busca no WebService
        categorias = getCategoriasFromWebService(context, tipo);
        return categorias;

        /*String tipoString = getTipo(tipo);
        String url = URL.replace("{tipo}", tipoString);
        //Faz a requisição HTTP no servidor e retorna a String com o conteúdo
        HttpHelper http = new HttpHelper();
        String json = http.doGet(url);
        List<Carro> carros = parserJSON(context, json);
        salvarArquivoNaMemoriaInterna(context, url, json);*/

    }

    private static List<Categoria> getCategoriasFromWebService(Context context, int tipo) throws IOException {
        String tipoString = getTipo(tipo);
        String url = URL.replace("{tipo}", tipoString);
        Log.d(TAG, "URL: " + url);
        //Faz a requisição HTTP no servidor e retorna a String com o conteúdo
        HttpHelper http = new HttpHelper();
        String json = http.doGet(url);
        List<Categoria> categorias = parserJSON(context, json);
        //Depois de buscar, salva as caetgorias
        salvarCategorias(context, categorias);
        return categorias;
    }

    //salva as categorias no banco de dados
    private static void salvarCategorias(Context context, List<Categoria> categorias) {
        CategoriaDB db = new CategoriaDB(context);
        try{
            //Deleta as categorias pelo tipo para limpar o banco
           // String tipoString = getTipo(tipo);
            db.deleteTodasCategorias();
            //salva todos as categorias
            for (Categoria c : categorias){
                //c.tipo = tipoString;
                Log.d(TAG, "Salvando a categoria: " + c.categoria);
                db.save(c);
            }
        }finally {
            db.close();
        }

    }

    private static List<Categoria> getCategoriasFromBanco(Context context) throws IOException {
        CategoriaDB db = new CategoriaDB(context);
        try {
            //String tipoString = getTipo(tipo);
            List<Categoria> categorias = db.findAll();
            Log.d(TAG, "Retornando " + categorias.size() + " categorias do banco");
            return categorias;
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

    //Faz o parser do JSON e cria a lista de categorias
    private static List<Categoria> parserJSON(Context context, String json) throws IOException {
        List<Categoria> categorias = new ArrayList<Categoria>();
        try{
            JSONObject root = new JSONObject(json);
            JSONObject obj = root.getJSONObject("categorias");
            JSONArray jsonCategorias = obj.getJSONArray("categoria");
            //insere cada categoria na lista
            for (int i=0; i < jsonCategorias.length(); i++){
                JSONObject jsonCategoria = jsonCategorias.getJSONObject(i);
                Categoria c = new Categoria();
                //Lê as informações de cada categoria
                c.categoria = jsonCategoria.optString("categoria");
                if(LOG_ON){
                    Log.d(TAG, "Categoria" + c.categoria);
                }
                categorias.add(c);
            }
            if (LOG_ON){
                Log.d(TAG, categorias.size() + " encontrados");
            }

        } catch (JSONException e) {
            throw  new IOException(e.getMessage(), e);
        }
        return categorias;
    }
}
