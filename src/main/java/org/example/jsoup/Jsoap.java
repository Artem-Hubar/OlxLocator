package org.example.jsoup;

import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

;

public class Jsoap {
    public void scrapeTopic() throws IOException {
        File html = new File(Paths.get("laptops.html").toUri());
        Document doc = Jsoup.parse(html);
        synchronized (this){
            try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("newBase.csv", true), StandardCharsets.UTF_8);
                 CSVWriter writer = (CSVWriter) new CSVWriterBuilder(osw).withSeparator(';').build()) {
                String[] data1 = {"id", "title","href", "timeAdded"};
                writer.writeNext(data1);

            } catch (Exception e) {
                System.out.println(e);
            }
        }


        Elements paragraphs = doc.getElementsByClass("css-1sw7q4x");
        if (!paragraphs.isEmpty()) {
            for (Element laptop : paragraphs) {
                if (laptop.getElementsByClass("css-1jh69qu").text().isEmpty() && laptop.getElementsByClass("css-1jh69qu").text().length() == 0) {
                    writeLaptopToCsv(laptop);
                }
            }

        } else {
            System.out.println("No content found.");
        }
    }


    private void writeLaptopToCsv(Element laptop) {
        if (laptop.id().length() != 0 && !isAdded(laptop.id())) {
            String titleLaptop = laptop.getElementsByClass("css-u2ayx9").text();
            String hrefLaptop = "https://www.olx.ua/" + laptop.getElementsByClass("css-rc5s2u").attr("href");
            String timeAddedLaptop = laptop.getElementsByAttributeValue("data-testid", "location-date").text();
            synchronized (this){
                try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("newBase.csv", true), StandardCharsets.UTF_8);
                     CSVWriter writer = (CSVWriter) new CSVWriterBuilder(osw).withSeparator(';').build()) {
                    String[] data1 = {laptop.id(), titleLaptop, hrefLaptop, timeAddedLaptop};
                    writer.writeNext(data1);

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }


    }

    private synchronized boolean isAdded(String id) {
        boolean isAdded = false;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("base.csv"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(";");
                String column = columns[0].substring(1, columns[0].length() - 1);
                if (column.equals(id) && column.length() != 0 && id.length() != 0) {
                    isAdded = true;
                    System.out.println(columns[0] + " " + id + ": " + columns[0].substring(1, (columns[0].length()) - 1).equals(id));
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        if (!isAdded){
            System.out.println(id +": false");
        }
        return isAdded;
    }
}
