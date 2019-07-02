package piro13.osucatdroid3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.regex.Matcher;

public class SearchResultsActivity extends AppCompatActivity {
    public static final String EXTRA_ACCOUNT = "com.example.kaveri.android10.EXTRA_ACCOUNT";
    private WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        setTitle(R.string.searching);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ACCOUNT)) {
            final Context myApp = this;
            browser = (WebView) findViewById(R.id.search_results_webview);

            class MyJavaScriptInterface {
                @JavascriptInterface
                @SuppressWarnings("unused")
                public void processHTML(String html) {
                    String substr= html.substring(html.indexOf("<main class="),html.indexOf("</main>"));
                    Intent data = new Intent();
                    data.putStringArrayListExtra("list", getArray(substr));
                    setResult(RESULT_OK, data);
                    finish();
                }
            }

            /* JavaScript must be enabled if you want it to work, obviously */
            WebSettings webSettings = browser.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);
            webSettings.setSupportZoom(true);
            browser.setWebChromeClient(new WebChromeClient());
            browser.setWebViewClient(new WebViewClient());

            /* Register a new JavaScript interface called HTMLOUT */
            browser.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

            /* WebViewClient must be set BEFORE calling loadUrl! */
            browser.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    /* This call inject JavaScript into the page which just finished loading. */
                    browser.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                }
            });

            /* load a web page */
            browser.loadUrl(intent.getStringExtra(EXTRA_ACCOUNT));
            //browser.loadUrl("https://bloodcat.com/osu/?q=monstrata&c=b&s=1&m=&g=&l=");

        }
    }

    private static ArrayList<String> getArray(String str) {
        String regexString = Pattern.quote("href=\"s/") + "(.*?)" + Pattern.quote("\">");
        Pattern pattern = Pattern.compile(regexString);
        Matcher matcher = pattern.matcher(str);
        ArrayList<String> beatmapList = new ArrayList<>();
        while (matcher.find()) {
            beatmapList.add(matcher.group(1));
        }
        return beatmapList;
    }
}
