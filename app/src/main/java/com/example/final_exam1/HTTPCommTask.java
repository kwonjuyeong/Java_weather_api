package com.example.final_exam1;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class HTTPCommTask extends AsyncTask<Void, Void, Void> {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected Void doInBackground(Void... voids) {
        try {


            URL url = getUrl(base_date(), base_time());

            StringBuilder sb = getHTTPResult(url);

            Document document =
                    DocumentBuilderFactory.newInstance()
                            .newDocumentBuilder()
                            .parse(new ByteArrayInputStream(sb.toString().getBytes()));

            ArrayList<WeatherData> infos = parseDocument2Info(document);

        }catch (IOException | ParserConfigurationException | SAXException | ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<WeatherData> parseDocument2Info(Document document) throws ParseException {
        ArrayList<WeatherData> infos = new ArrayList<>();

        Element rootNode = document.getDocumentElement();
        Node itemNode = rootNode.getLastChild().getFirstChild();

        NodeList itemNodeList = itemNode.getChildNodes();
        for(int i = 0; i<itemNodeList.getLength();i++){
            NodeList itemNodeChild = itemNodeList.item(i).getChildNodes();

                SimpleDateFormat format = new SimpleDateFormat("yyyy??? MM??? dd??? HH???", Locale.KOREAN);

                double tmp= Integer.parseInt(getNodeValue(itemNodeChild, 1));
                double skycode= Integer.parseInt(getNodeValue(itemNodeChild, 2));;
                double reh= Integer.parseInt(getNodeValue(itemNodeChild, 6));

                Date stdDay = format.parse(getNodeValue(itemNodeChild, 13));

                //?????? ???????????? ?????? ??? constructor ???????????????
                WeatherData info = new WeatherData(tmp, skycode, reh);
                infos.add(info);

        }
        return null;
    }

    private String getNodeValue(NodeList itemNodeChild, int i) {
        return itemNodeChild.item(i).getFirstChild().getNodeValue();
    }

    @NonNull
    private StringBuilder getHTTPResult(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        return sb;

    }

    private URL getUrl(String base_date, String base_time) throws UnsupportedEncodingException, MalformedURLException {

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"); /*URL*/

        String serviceKey = "9zmDQ4S3j8Qi7wsOXFeMe80pj/jHGSFp7iHloQlOvSTS67K1/2pm3k/VBqh+RRzty+V4fJBchhSxsC2b2ZiyUg==";

        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + URLEncoder.encode(serviceKey,"UTF-8")); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*???????????????*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*??? ????????? ?????? ???*/
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("XML", "UTF-8")); /*??????????????????(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(base_date(), "UTF-8")); /*???21??? 12??? 15??? ??????*/
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(base_time(), "UTF-8")); /*05??? ??????(????????????) */
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode("55", "UTF-8")); /*??????????????? X ?????????*/
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode("127", "UTF-8")); /*??????????????? Y ?????????*/
        URL url = new URL(urlBuilder.toString());

        return url;
    }

    private String base_time() {
        return "0500";
    }

    private String base_date() {
        return "20211215";
    }
}

