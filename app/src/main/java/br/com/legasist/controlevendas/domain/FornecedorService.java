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
 * Created by ovs on 05/06/2017.
 */

public class FornecedorService {
    private static final boolean LOG_ON = false;
    private static final String TAG = "FornecedorService";
    private static final String URL = "http://www.livroandroid.com.br/livro/carros/carros_{tipo}.json";

    public static List<Fornecedor> getFornecedores(Context context, int tipo, boolean refresh) throws IOException {
        //busca os fornecedores no banco de dados (somente se refresh = false)
        //List<Cliente> clientes = !refresh ? getFornecedoresFromBanco(context) : null;
        // sempre buscar no banco por enquanto
        List<Fornecedor> fornecedores = !false ? getFornecedoresFromBanco(context) : null;
        if(fornecedores != null && fornecedores.size()>0){
            //retorna os fornecedores encontrados do banco
            return fornecedores;
        }
        //se não encontrar, busca no WebService
        fornecedores = getFornecedoresFromWebService(context, tipo);
        return fornecedores;

        /*String tipoString = getTipo(tipo);
        String url = URL.replace("{tipo}", tipoString);
        //Faz a requisição HTTP no servidor e retorna a String com o conteúdo
        HttpHelper http = new HttpHelper();
        String json = http.doGet(url);
        List<Carro> carros = parserJSON(context, json);
        salvarArquivoNaMemoriaInterna(context, url, json);*/

    }

    private static List<Fornecedor> getFornecedoresFromWebService(Context context, int tipo) throws IOException {
        String tipoString = getTipo(tipo);
        String url = URL.replace("{tipo}", tipoString);
        Log.d(TAG, "URL: " + url);
        //Faz a requisição HTTP no servidor e retorna a String com o conteúdo
        HttpHelper http = new HttpHelper();
        String json = http.doGet(url);
        List<Fornecedor> fornecedores = parserJSON(context, json);
        //Depois de buscar, salva os clientes
        salvarFornecedores(context, fornecedores);
        return fornecedores;
    }

    //salva os fornecedores no banco de dados
    private static void salvarFornecedores(Context context, List<Fornecedor> fornecedores) {
        FornecedorDB db = new FornecedorDB(context);
        try{
            //Deleta os fornecedores pelo tipo para limpar o banco
           // String tipoString = getTipo(tipo);
            db.deleteTodosFornecedores();
            //salva todos os clientes
            for (Fornecedor f : fornecedores){
                //c.tipo = tipoString;
                Log.d(TAG, "Salvando o fornecedor: " + f.nome);
                db.save(f);
            }
        }finally {
            db.close();
        }

    }

    private static List<Fornecedor> getFornecedoresFromBanco(Context context) throws IOException {
        FornecedorDB db = new FornecedorDB(context);
        try {
            //String tipoString = getTipo(tipo);
            List<Fornecedor> fornecedores = db.findAll();
            Log.d(TAG, "Retornando " + fornecedores.size() + " fornecedores do banco");
            return fornecedores;
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

    //controlevendas
    //Converte a constante para String, para criar a URL do wen service
    private static String getTipo(int tipo) {
        if (tipo == br.com.legasist.controlevendas.R.string.classicos){
            return "classicos";
        }else if (tipo == br.com.legasist.controlevendas.R.string.esportivos){
            return "esportivos";
        }
        return "luxo";
    }

    //controlevendas
    //FAZ A LEITURA DO ARQUIVO QUE ESTÁ NA PASTA /res/raw
    /*private static String readFile(Context context, int tipo) throws IOException {
        if(tipo == br.com.legasist.controlevendas.R.string.classicos){
            return FileUtils.readRawFileString(context, br.com.legasist.controlevendas.R.raw.carros_classicos, "UTF-8");
        }else if(tipo == br.com.legasist.controlevendas.R.string.esportivos){
            return FileUtils.readRawFileString(context, br.com.legasist.controlevendas.R.raw.carros_esportivos, "UTF-8");
        }
        return FileUtils.readRawFileString(context, br.com.legasist.controlevendas.R.raw.carros_luxo, "UTF-8");
    }*/

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

    //Faz o parser do JSON e cria a lista de fornecedores
    private static List<Fornecedor> parserJSON(Context context, String json) throws IOException {
        List<Fornecedor> fornecedores = new ArrayList<Fornecedor>();
        try{
            JSONObject root = new JSONObject(json);
            JSONObject obj = root.getJSONObject("fornecedores");
            JSONArray jsonFornecedores = obj.getJSONArray("fornecedor");
            //insere cada fornecedor na lista
            for (int i=0; i < jsonFornecedores.length(); i++){
                JSONObject jsonFornecedor = jsonFornecedores.getJSONObject(i);
                Fornecedor f = new Fornecedor();
                //Lê as informações de cada fornecedor
                f.nome = jsonFornecedor.optString("nome");
                f.endereco = jsonFornecedor.optString("endereco");
                f.cidade = jsonFornecedor.optString("cidade");
                f.uf = jsonFornecedor.optString("uf");
                f.telefone= jsonFornecedor.optString("telefone");
                f.email = jsonFornecedor.optString("email");
                f.latitude = jsonFornecedor.optDouble("latitude");
                f.longitude = jsonFornecedor.optDouble("longitude");
                if(LOG_ON){
                    Log.d(TAG, "Fornecedor" + f.nome);
                }
                fornecedores.add(f);
            }
            if (LOG_ON){
                Log.d(TAG, fornecedores.size() + " encontrados");
            }

        } catch (JSONException e) {
            throw  new IOException(e.getMessage(), e);
        }
        return fornecedores;
    }
}
