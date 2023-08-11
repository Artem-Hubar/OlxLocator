package org.example.system_tray;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Ap {

    public static void displayTray(Integer count) throws AWTException {
        if (SystemTray.isSupported()) {
            //Obtain only one instance of the SystemTray object
            SystemTray tray = SystemTray.getSystemTray();

            //If the icon is a file
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            //Alternative (if the icon is on the classpath):
            //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

            TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
            PopupMenu popupMenu = new PopupMenu();
            MenuItem openItem = new MenuItem("Open File");
            openItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Действия при нажатии на "Open File"
                    try {
                        Desktop.getDesktop().open(new File("newBase1.csv"));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            popupMenu.add(openItem);
            trayIcon.setPopupMenu(popupMenu);
            //Let the system resize the image if needed
            trayIcon.setImageAutoSize(true);
            //Set tooltip text for the tray icon
            trayIcon.setToolTip("System tray icon demo");
            tray.add(trayIcon);

            trayIcon.displayMessage("Notification", "new laptops: " + count, TrayIcon.MessageType.INFO);
            tray.remove(trayIcon);
        } else {
            System.err.println("System tray not supported!");
        }

    }
}