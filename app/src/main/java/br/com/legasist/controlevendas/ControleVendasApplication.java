package br.com.legasist.controlevendas;

import android.app.Application;
import android.util.Log;

import com.squareup.otto.Bus;

/**
 * Created by ovs on 03/05/2017.
 */

public class ControleVendasApplication extends Application {
    private static final String TAG = "VendasApplication";
    private static ControleVendasApplication instance = null;
    private Bus bus = new Bus();
    public static ControleVendasApplication getInstance(){
        return instance; //Singleton
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "ControleVendasApplication.onCreate()");
        //Salva a instância para termos acesso como Singleton
        instance = this;
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
        Log.d(TAG, "ControleVendasApplication.onTerminate()");
    }

    public Bus getBus(){
        return bus;
    }
}
