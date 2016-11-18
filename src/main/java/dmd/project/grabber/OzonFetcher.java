package dmd.project.grabber;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import dmd.project.objects.Item;


public class OzonFetcher {
    protected static final String DEFAULT_DIR = "GrabberOzon/html/";
    protected static final String DATA_FILE = "OzonFetcher.data";
    protected static final int CONNECTION_TIMEOUT = 5000;
    
    protected OzonFetcher() { };

    /**
     * Fetches and saves html file by the specified url
     * with the same as url name (without not allowed symbols) into
     * the default directory, if url is available.
     * @param url
     * @throws IOException
     * @throws ParseException
     */
    public static void fetch(final String url) throws IOException, ParseException {
        fetch(url, url.replaceAll("/", "").replaceAll(":", "").replaceAll("\\?", ""));
    }
    
    /**
     * Fetches and saves html into a specified path, if url is available.
     * @param url
     * @throws IOException
     * @throws ParseException
     */
    public static void fetch(final String url,
            final String fileName) throws IOException, ParseException {
        System.out.println(fileName);
        Document doc = Jsoup.connect(url)
                .timeout(CONNECTION_TIMEOUT).get();
        BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(DEFAULT_DIR + fileName + ".html"), "UTF-8"));
        wr.write(doc.toString());
        wr.flush();
        wr.close();
    }
    
    
    /**
     * Fetches the specified category and all its items
     * @param categoryId
     */
    public static void fetchCategory(
            final String categoryId) throws IOException, ParseException {
        OzonGrabber grabber = new OzonGrabber(false);
                
        int amountOfPages = 100;
        boolean hasNextPage = true;
        for (int page = 0; page < amountOfPages && hasNextPage; page++) {
            String url = "http://www.ozon.ru/catalog/"
                    + categoryId + "/?page=" + page;
            Document doc = grabber.getDocument(url);
            fetch(url);
            
            Element head = doc.getElementsByTag("head").first();
            Element nextPage = head
                    .getElementsByAttributeValue("rel", "next").first();
            if (nextPage == null) {
                hasNextPage = false;
            }
            
            Elements itemLinks = 
                    doc.getElementsByClass("jsUpdateLink eOneTile_link");
            for (Element itemLink : itemLinks) {
                String[] tokens = itemLink.attr("href").split("/");
                String itemId = tokens[tokens.length - 1];
    
                try {
                    String objLink = "http://www.ozon.ru/context/detail/id/" 
                            + itemId + "/";
                    String reviewsLink = "http://www.ozon.ru/reviews/"
                            + itemId + "/#comments";
                    
                    // Fetch
                    fetch(objLink);
                    fetch(reviewsLink);
                    
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    
    /**
     * The iterator through ids.
     */
    private static int i;

    /**
     * Main program body. By default hardcoded categories will
     * be parsed, unless --byid parameter specified. In this case 
     * it will fetch all items in the row by their id and
     * on legal termination, it will write the last fetched id
     * into the data file.
     * @param main
     */
    public static void main(String[] args) {
        // Fetch by ids
        if (args.length > 0 
                && args[0].toLowerCase().equals("--byid")) {
            int start = 135000000;
            try {
                BufferedReader br = new BufferedReader(
                        new FileReader(DATA_FILE));
                String line = br.readLine();
                start = Integer.valueOf(line);
                br.close();
            } catch (Exception e) {
                // exception is silently ignored, initial start
                // value will be used
            }
            
            // Saves the last fetched id into the data file (it is 
            // specified in DATA_FILE field) before been legally terminated
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    System.out.println("Last parsed id: " + i);
                    try {
                        BufferedWriter wr = new BufferedWriter(
                                new FileWriter(DATA_FILE));
                        wr.write(String.valueOf(i));
                        wr.flush();
                        wr.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            
            String objLink = null;
            String reviewsLink = null;
            
            for (i = start; i <= 999999999; i++) {
                // 131331843 - last id before 135 cluster
                String objId = String.format("%09d", i);
                objLink = "http://www.ozon.ru/context/detail/id/" 
                        + objId + "/";
                reviewsLink = "http://www.ozon.ru/reviews/"
                        + objId + "/#comments";
                try {
                    fetch(objLink);
                    fetch(reviewsLink);
                } catch (Exception e) {
                    System.out.println("404 error: " + objLink);
                }
            }
            
        // Fetch by categories
        } else {
            try {
                if (args.length > 1) {
                    List<String> cats = new ArrayList<>();
                    if (args[0].equals("--cats")) {
                        for (int i = 1; i < args.length; i++) {
                            cats.add(args[i]);
                        }
                    }
                    for (String c : cats) {
                        fetchCategory(c);
                    }
                    
                } else {
                    // By default
                    fetchCategory("1168060"); // Смартфоны
                    fetchCategory("1133763"); // Телевизоры
                    fetchCategory("1141109"); // Художественная литература
                    fetchCategory("1178973"); // Планшеты
                    fetchCategory("1133719"); // USB flash накопителиx
                    fetchCategory("1169395"); // Роботы-пылесосы
                    fetchCategory("1133734"); // Пылесосы
                    fetchCategory("1155471"); // Техника для дома
                    fetchCategory("1135615"); // Мультиварки
                    fetchCategory("1133732"); // Блендеры и миксеры
                    fetchCategory("1133693"); // Клавиатуры и мыши
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }    
}
