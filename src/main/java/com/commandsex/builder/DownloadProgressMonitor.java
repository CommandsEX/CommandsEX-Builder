package com.commandsex.builder;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class DownloadProgressMonitor {

    private JPanel jPanel;
    private JLabel currentlyDownloading;
    private JProgressBar progressBar;
    private String name;

    public DownloadProgressMonitor(String name, URL url) {
        this.name = name;
        currentlyDownloading.setText("Currently Downloading: " + name);

        JFrame frame = new JFrame("Downloading...");
        frame.setContentPane(jPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(new Dimension(300, 100));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        final Download download = new Download(url);

        boolean running = true;

        while (running){
            switch (download.getStatus()){
                case Download.DOWNLOADING: {
                    progressBar.setValue(((Float) download.getProgress()).intValue());
                    break;
                }

                case Download.COMPLETE: {
                    progressBar.setValue(100);
                    FeatureSelection.jar = download.getDownloadedFile();
                    running = false;
                    Utils.closeWindow(frame);
                    break;
                }

                case Download.ERROR: {
                    JOptionPane.showMessageDialog(jPanel, "Error while downloading " + download.getUrl(), "Download Failed", JOptionPane.ERROR_MESSAGE);
                    running = false;
                    System.exit(1);
                    break;
                }
            }
        }
    }
}
