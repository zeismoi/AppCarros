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

public class ClienteService {
    private static final boolean LOG_ON = false;
    private static final String TAG = "ClienteService";
    private static final String URL = "http://www.livroandroid.com.br/livro/carros/carros_{tipo}.json";

    public static List<Cliente> getClientes(Context context, int tipo, boolean refresh) throws IOException {
        //busca os clientes no banco de dados (somente se refresh = false)
        List<Cliente> clientes = !refresh ? getClientesFromBanco(context) : null;
        if(clientes != null && clientes.size()>0){
            //retorna os clientes encontrados do banco
            return clientes;
        }
        //se não encontrar, busca no WebService
        clientes = getClientesFromWebService(context, tipo);
        return clientes;

        /*String tipoString = getTipo(tipo);
        String url = URL.replace("{tipo}", tipoString);
        //Faz a requisição HTTP no servidor e retorna a String com o conteúdo
        HttpHelper http = new HttpHelper();
        String json = http.doGet(url);
        List<Carro> carros = parserJSON(context, json);
        salvarArquivoNaMemoriaInterna(context, url, json);*/

    }

    private static List<Cliente> getClientesFromWebService(Context context, int tipo) throws IOException {
        String tipoString = getTipo(tipo);
        String url = URL.replace("{tipo}", tipoString);
        Log.d(TAG, "URL: " + url);
        //Faz a requisição HTTP no servidor e retorna a String com o conteúdo
        HttpHelper http = new HttpHelper();
        String json = http.doGet(url);
        List<Cliente> clientes = parserJSON(context, json);
        //Depois de buscar, salva os clientes
        salvarClientes(context, clientes);
        return clientes;
    }

    //salva os clientes no banco de dados
    private static void salvarClientes(Context context, List<Cliente> clientes) {
        ClienteDB db = new ClienteDB(context);
        try{
            //Deleta os clientes pelo tipo para limpar o banco
           // String tipoString = getTipo(tipo);
            db.deleteTodosClientes();
            //salva todos os clientes
            for (Cliente c : clientes){
                //c.tipo = tipoString;
                Log.d(TAG, "Salvando o cliente: " + c.nome);
                db.save(c);
            }
        }finally {
            db.close();
        }

    }

    private static List<Cliente> getClientesFromBanco(Context context) throws IOException {
        ClienteDB db = new ClienteDB(context);
        try {
            //String tipoString = getTipo(tipo);
            List<Cliente> clientes = db.findAll();
            Log.d(TAG, "Retornando " + clientes.size() + " clientes do banco");
            return clientes;
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

    //Faz o parser do JSON e cria a lista de clientes
    private static List<Cliente> parserJSON(Context context, String json) throws IOException {
        List<Cliente> clientes = new ArrayList<Cliente>();
        try{
            JSONObject root = new JSONObject(json);
            JSONObject obj = root.getJSONObject("clientes");
            JSONArray jsonClientes = obj.getJSONArray("cliente");
            //insere cada cliente na lista
            for (int i=0; i < jsonClientes.length(); i++){
                JSONObject jsonCliente = jsonClientes.getJSONObject(i);
                Cliente c = new Cliente();
                //Lê as informações de cada cliente
                c.nome = jsonCliente.optString("nome");
                c.endereco = jsonCliente.optString("endereco");
                c.cidade = jsonCliente.optString("cidade");
                c.uf = jsonCliente.optString("uf");
                c.celular = jsonCliente.optString("celular");
                c.email = jsonCliente.optString("email");
                c.latitude = jsonCliente.optString("latitude");
                c.longitude = jsonCliente.optString("longitude");
                if(LOG_ON){
                    Log.d(TAG, "Cliente" + c.nome);
                }
                clientes.add(c);
            }
            if (LOG_ON){
                Log.d(TAG, clientes.size() + " encontrados");
            }

        } catch (JSONException e) {
            throw  new IOException(e.getMessage(), e);
        }
        return clientes;
    }
}
