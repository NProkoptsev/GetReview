package dmd.project.grabber;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class DateTransformer {

    private static String[] rusMonths = new String[]{
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"};
    private static String[] engMonths = new String[]{
            "january", "february", "march", "april", "may", "june",
            "july", "august", "september", "october", "november", "december"};

    public static Date transform(final String date) throws Exception {
        String dateStr = date;
        for (int i = 0; i < rusMonths.length; i++) {
            dateStr = dateStr.toLowerCase().replaceAll(rusMonths[i], engMonths[i]);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);

        Date result = sdf.parse(dateStr);
        return result;
    }
}
