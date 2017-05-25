package br.com.legasist.controlevendas.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ovs on 18/05/2017.
 */

public class PermissionUtils {
    public static boolean validate(Activity activity, int requestCode, String... permissions){
        List<String> list = new ArrayList<String>();
        for (String permission : permissions){
            //valida permissão
            boolean ok = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
            if(!ok){
                list.add(permission);
            }
        }
        if(list.isEmpty()){
            //tudo ok, retorna true
            return true;
        }
        //Lista de permisões a que faltam acesso
        String[] newPermissions = new String[list.size()];
        list.toArray(newPermissions);
        //solicita permissão
        ActivityCompat.requestPermissions(activity, newPermissions, 1);
        return false;
    }
}
