package org.example;

import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;
import org.checkerframework.checker.units.qual.A;
import org.example.jsoup.Jsoap;
import org.example.selenium.SeleniumIMP;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Hello world!
 *
 */
public class App 
{

    public static void main( String[] args ) throws IOException, AWTException, InterruptedException {
        JFrame frame = new JFrame("Пример окна");

        JPanel contentPane = new JPanel(); // Основная панель
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS)); // Вертикальное расположение
        JScrollPane scrollPane = new JScrollPane(contentPane); // Панель с полосами прокрутки

        Thread thread = new Parser(contentPane);
        thread.start();


        frame.add(scrollPane); // Добавляем панель с полосами прокрутки на окно

        frame.setSize(300, 200);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static class Parser extends Thread{
        JPanel frame;

        public Parser(JPanel frame) {
            this.frame = frame;
        }

        @Override
        public void run() {
            ReentrantLock mutex = new ReentrantLock();
            new OlxMonitor(mutex, frame).start();
//
            while (true)
            {
                new OlxParser(mutex, frame).start();



                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    frame.add(new JLabel((Icon) e));
                }

            }
        }
    }
}

