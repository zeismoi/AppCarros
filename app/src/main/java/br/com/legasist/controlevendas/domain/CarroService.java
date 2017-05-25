package br.com.legasist.controlevendas.domain;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import livroandroid.lib.utils.FileUtils;
import livroandroid.lib.utils.HttpHelper;
import livroandroid.lib.utils.IOUtils;
import livroandroid.lib.utils.XMLUtils;

/**
 * Created by ovs on 09/05/2017.
 */

public class CarroService {
    private static final boolean LOG_ON = false;
    private static final String TAG = "CarroService";
    private static final String URL = "http://www.livroandroid.com.br/livro/carros/carros_{tipo}.json";

    public static List<Carro> getCarros(Context context, int tipo, boolean refresh) throws IOException {
        //busca os carros no banco de dados (somente se refresh = false)
        List<Carro> carros = !refresh ? getCarrosFromBanco(context, tipo) : null;
        if(carros != null && carros.size()>0){
            //retorna os carros encontrados do banco
            return carros;
        }
        //se não encontrar, busca no WebService
        carros = getCarrosFromWebService(context, tipo);
        return carros;

        /*String tipoString = getTipo(tipo);
        String url = URL.replace("{tipo}", tipoString);
        //Faz a requisição HTTP no servidor e retorna a String com o conteúdo
        HttpHelper http = new HttpHelper();
        String json = http.doGet(url);
        List<Carro> carros = parserJSON(context, json);
        salvarArquivoNaMemoriaInterna(context, url, json);*/

    }

    private static List<Carro> getCarrosFromWebService(Context context, int tipo) throws IOException {
        String tipoString = getTipo(tipo);
        String url = URL.replace("{tipo}", tipoString);
        Log.d(TAG, "URL: " + url);
        //Faz a requisição HTTP no servidor e retorna a String com o conteúdo
        HttpHelper http = new HttpHelper();
        String json = http.doGet(url);
        List<Carro> carros = parserJSON(context, json);
        //Depois de buscar, salva os carros
        salvarCarros(context, tipo, carros);
        return carros;
    }

    //salva os carros no banco de dados
    private static void salvarCarros(Context context, int tipo, List<Carro> carros) {
        CarroDB db = new CarroDB(context);
        try{
            //Deleta os carros pelo tipo para limpar o banco
            String tipoString = getTipo(tipo);
            db.deleteCarrosByTipo(tipoString);
            //salva todos os carros
            for (Carro c : carros){
                c.tipo = tipoString;
                Log.d(TAG, "Salvando o carro: " + c.nome);
                db.save(c);
            }
        }finally {
            db.close();
        }

    }

    private static List<Carro> getCarrosFromBanco(Context context, int tipo) throws IOException {
        CarroDB db = new CarroDB(context);
        try {
            String tipoString = getTipo(tipo);
            List<Carro> carros = db.findAllByTipo(tipoString);
            Log.d(TAG, "Retornando " + carros.size() + " carros [" + tipoString + "] do banco");
            return carros;
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

    //Faz o parser do XML e cria a lista de carros
    private static List<Carro> parserXML(Context context, String xml){
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
    }

    //Faz o parser do JSON e cria a lista de carros
    private static List<Carro> parserJSON(Context context, String json) throws IOException {
        List<Carro> carros = new ArrayList<Carro>();
        try{
            JSONObject root = new JSONObject(json);
            JSONObject obj = root.getJSONObject("carros");
            JSONArray jsonCarros = obj.getJSONArray("carro");
            //insere cada carro na lista
            for (int i=0; i < jsonCarros.length(); i++){
                JSONObject jsonCarro = jsonCarros.getJSONObject(i);
                Carro c = new Carro();
                //Lê as informações de cada carro
                c.nome = jsonCarro.optString("nome");
                c.desc = jsonCarro.optString("desc");
                c.urlFoto = jsonCarro.optString("url_foto");
                c.urlInfo = jsonCarro.optString("url_info");
                c.urlVideo = jsonCarro.optString("url_video");
                c.latitude = jsonCarro.optString("latitude");
                c.longitude = jsonCarro.optString("longitude");
                if(LOG_ON){
                    Log.d(TAG, "Carro" + c.nome + " > " + c.urlFoto);
                }
                carros.add(c);
            }
            if (LOG_ON){
                Log.d(TAG, carros.size() + " encontrados");
            }

        } catch (JSONException e) {
            throw  new IOException(e.getMessage(), e);
        }
        return carros;
    }
}
