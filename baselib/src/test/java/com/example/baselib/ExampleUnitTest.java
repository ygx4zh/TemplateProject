package com.example.baselib;

import android.os.Environment;

import com.example.baselib.modules.FileDownloader;
import com.example.baselib.modules.IDownloadObserver;
import com.example.baselib.modules.WebServiceRequester;
import com.example.baselib.utils.XmlUtils;

import org.junit.Test;

import java.util.Locale;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testDownload() throws Exception {
        FileDownloader.create().url("http://xxx")
                .intoDir(Environment.getExternalStorageDirectory().getAbsolutePath())
                .name("xxx")
                .download(new IDownloadObserver() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onProgress() {

                    }

                    @Override
                    public void onEnd() {

                    }

                    @Override
                    public void onThrowable(Throwable t) {

                    }
                });
    }

    @Test
    public void testWebService() throws Exception{

        String REQ_FORMAT =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                        "xmlns:q0=\"http://webservice.demo.com\" " +
                        "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                        "  <soapenv:Body>\n" +
                        "    <q0:AddVisite>\n" +
                        "      <strName>%s</strName>\n" +
                        "      <strKCAPID>%s</strKCAPID>\n" +
                        "      <JCLX>%s</JCLX>\n" +
                        "      <MEMO>%s</MEMO>\n" +
                        "    </q0:AddVisite>\n" +
                        "  </soapenv:Body>\n" +
                        "</soapenv:Envelope>";


        String IN  = "0";


        String OUT = "1";

        String COMMENT = "考勤机";

        String url = "http://8m700.cn:8080/services/VertifySfWebService";
        WebServiceRequester.create()
                .url(url)
                .reqRaw(String.format(Locale.CHINA, REQ_FORMAT,"name", "idNum", IN, COMMENT))
                .subscribeOn(WebServiceRequester.ThreadMode.NEW_THREAD)
                .observeOn(WebServiceRequester.ThreadMode.MAIN)
                .subscribe(new WebServiceRequester.ISubscriber<String, String>() {
                    @Override
                    public void onThrowable(Throwable t) {

                    }

                    @Override
                    public String onRecvResult(String recv) {
                        return XmlUtils.getSpecialTagValue(recv,"ns1:out");
                    }

                    @Override
                    public void onData(String s) {

                    }

                    @Override
                    public void onFinish(boolean success) {

                    }
                });

    }
}