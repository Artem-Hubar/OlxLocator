package org.example;


import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.locks.ReentrantLock;

import static org.example.system_tray.Ap.displayTray;

public class OlxMonitor extends Thread {

    ReentrantLock mutex;
    JPanel frame;

    public OlxMonitor(ReentrantLock mutex, JPanel frame) {
        this.mutex = mutex;
        this.frame = frame;
    }

    @Override
    public void run() {
        while (true) {
            File file = new File("newBase.csv");
            if (file.exists()) {
                Charset charset1251 = Charset.forName("Cp1251");

                try {
                    mutex.lock();
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("newBase.csv"), StandardCharsets.UTF_8)); OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("oldBase.csv", true), charset1251);
                         CSVWriter writer = (CSVWriter) new CSVWriterBuilder(osw).withSeparator(';').build()) {
                        String line;
                        int i = 0;
                        while ((line = br.readLine()) != null) {
                            String[] columns = line.split(";");
                            String columnFirst = columns[0].substring(1, columns[0].length() - 1);
                            String columnSec = columns[1].substring(1, columns[1].length() - 1);
                            String columnThird = columns[2].substring(1, columns[2].length() - 1);
                            String columnFour = columns[3].substring(1, columns[3].length() - 1);

                            frame.add(new JLabel(columnFirst + " " + columnSec));

                            String[] data1 = {columnFirst, columnSec, columnThird, columnFour};
                            writer.writeNext(data1);
                            i++;

                        }

                        try {

                            Files.copy(Path.of("newBase.csv"), Path.of("newBase1.csv"), StandardCopyOption.REPLACE_EXISTING);
                            displayTray(i);
                        } catch (AWTException e) {
                            frame.add(new JLabel((Icon) e));
                        }
                    } catch (Exception e) {
                        frame.add(new JLabel((Icon) e));
                    }
                } finally {
                    mutex.unlock();
                }


                try {
                    file.delete();
                    sleep(30000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
