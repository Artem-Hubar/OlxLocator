package org.example.selenium;

import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.concurrent.locks.ReentrantLock;


public class SeleniumIMP {
    ReentrantLock mutex;
    JPanel frame;

    public SeleniumIMP(ReentrantLock mutex, JPanel frame) {
        this.mutex = mutex;
        this.frame = frame;
    }

    public void scrapeTopicSelenium() throws IOException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        WebDriver driver = new ChromeDriver(options);
        String url = "https://www.olx.ua/elektronika/noutbuki-i-aksesuary/noutbuki/?currency=UAH&search%5Border%5D=created_at:desc&search%5Bfilter_float_price:from%5D=10000&search%5Bfilter_float_price:to%5D=20000";
        driver.get(url);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(11000));
        try {
            mutex.lock();
            Files.write(Paths.get("laptops.html"), driver.getPageSource().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        } finally {

            mutex.unlock();
        }
        driver.close();
        scrapeTopic();

    }

    public void scrapeTopic() throws IOException {
        File html = new File(Paths.get("laptops.html").toUri());
        Document doc = Jsoup.parse(html);
        try {

            mutex.lock();

            try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("newBase.csv", true), StandardCharsets.UTF_8);
                 CSVWriter writer = (CSVWriter) new CSVWriterBuilder(osw).withSeparator(';').build()) {
                String[] data1 = {"id", "title", "href", "timeAdded"};
                writer.writeNext(data1);

            } catch (Exception e) {
                frame.add(new JLabel((Icon) e));
            }
        } finally {

            mutex.unlock();
        }


        Elements paragraphs = doc.getElementsByClass("css-1sw7q4x");
        if (!paragraphs.isEmpty()) {
            for (Element laptop : paragraphs) {
                if (laptop.getElementsByClass("css-1jh69qu").text().isEmpty() && laptop.getElementsByClass("css-1jh69qu").text().length() == 0) {
                    writeLaptopToCsv(laptop);
                }
            }

        } else {
            frame.add(new JLabel("No content found."));
        }

    }


    private void writeLaptopToCsv(Element laptop) {
        if (laptop.id().length() != 0 && !isAdded(laptop.id())) {
            String titleLaptop = laptop.getElementsByClass("css-u2ayx9").text();
            String hrefLaptop = "https://www.olx.ua/" + laptop.getElementsByClass("css-rc5s2u").attr("href");
            String timeAddedLaptop = laptop.getElementsByAttributeValue("data-testid", "location-date").text();
            try {

                mutex.lock();
                try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("newBase.csv", true), StandardCharsets.UTF_8);
                     CSVWriter writer = (CSVWriter) new CSVWriterBuilder(osw).withSeparator(';').build()) {
                    String[] data1 = {laptop.id(), titleLaptop, hrefLaptop, timeAddedLaptop};
                    writer.writeNext(data1);

                } catch (Exception e) {
                    frame.add(new JLabel((Icon) e));
                }
            } finally {

                mutex.unlock();
            }
        }


    }

    private synchronized boolean isAdded(String id) {
        boolean isAdded = false;
        try {

            mutex.lock();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("oldBase.csv"), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] columns = line.split(";");
                    String column = columns[0].substring(1, columns[0].length() - 1);
                    if (column.equals(id) && column.length() != 0 && id.length() != 0) {
                        isAdded = true;
                    }
                }
            } catch (Exception e) {
                frame.add(new JLabel((Icon) e));
            }
        } finally {
            mutex.unlock();

        }
        return isAdded;
    }
}
