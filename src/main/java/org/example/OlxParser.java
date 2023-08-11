package org.example;

import org.example.selenium.SeleniumIMP;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;


public class OlxParser extends Thread{
    ReentrantLock mutex;
    JPanel frame;

    public OlxParser(ReentrantLock mutex, JPanel frame) {
        this.mutex = mutex;
        this.frame = frame;

    }

    @Override
    public void run() {
        try {
            new SeleniumIMP(mutex, frame).scrapeTopicSelenium();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
