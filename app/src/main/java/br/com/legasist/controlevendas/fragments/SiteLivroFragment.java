package br.com.legasist.controlevendas.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import br.com.legasist.controlevendas.fragments.dialog.AboutDialog;

public class SiteLivroFragment extends BaseFragment {

    protected SwipeRefreshLayout swipeLayout;

    private static final String URL_SOBRE = "http://www.livroandroid.com.br/sobre.htm";
    private WebView webView;
    private ProgressBar progress;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(br.com.legasist.controlevendas.R.layout.fragment_site_livro, container, false);
        webView = (WebView) view.findViewById(br.com.legasist.controlevendas.R.id.webview);
        progress = (ProgressBar) view.findViewById(br.com.legasist.controlevendas.R.id.progress);
        setWebViewClient(webView);
        //Carrega a página
        webView.loadUrl(URL_SOBRE);

        //Swipe to Refresh
        swipeLayout = (SwipeRefreshLayout) view.findViewById(br.com.legasist.controlevendas.R.id.swipeToRefresh);
        swipeLayout.setOnRefreshListener(OnRefreshListener());
        //cores da animação
        swipeLayout.setColorSchemeResources(br.com.legasist.controlevendas.R.color.refresh_progress_1, br.com.legasist.controlevendas.R.color.refresh_progress_2, br.com.legasist.controlevendas.R.color.refresh_progress_3);


        return view;
    }

    private SwipeRefreshLayout.OnRefreshListener OnRefreshListener(){
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload(); //atualiza a página
            }
        };
    }

    private void setWebViewClient(final WebView webView){
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView webViewl, String URL, Bitmap favicon){
                super.onPageStarted(webView, URL, favicon);
                //liga o progress
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView webView1, String URL){
                //Desliga o progress
                progress.setVisibility(View.INVISIBLE);
                //Termina a animação do Swipe to Refresch
                swipeLayout.setRefreshing(false);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                Log.d("livroandroid", "webview url: " + url);
                if(url != null && url.endsWith("sobre.htm")){
                    AboutDialog.showAbout(getFragmentManager());
                    //Retorna true para informar que interceptamos o evento
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

}
