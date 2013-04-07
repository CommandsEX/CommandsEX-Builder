package com.commandsex.builder;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static File tempDir = new File("builder-temp");
    public static JFrame frame;
    private JPanel jPanel;

    public enum Version {
        BUKKIT_DEV("Latest BukkitDev Release"),
        JENKINS("Latest Jenkins Development Build"),
        OWN_JAR("Use My Own JAR");

        private String message;

        private Version(String message){
            this.message = message;
        }

        public String getMessage(){
            return message;
        }

        public static Version getFromMessage(String message){
            for (Version version : values()){
                if (version.getMessage().equalsIgnoreCase(message)){
                    return version;
                }
            }

            return null;
        }
    }

    public Main() {
        frame = new JFrame("CommandsEX Builder");

        if (tempDir.exists()){
            if (!tempDir.isDirectory()){
                JOptionPane.showMessageDialog(frame, "The file " + tempDir.getPath() + " is not a directory", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        } else {
            tempDir.mkdir();
        }

        Version version = showVersionDialogue();

        switch (version) {
            case BUKKIT_DEV: {
                try {
                    new DownloadProgressMonitor("Latest BukkitDev Release", new URL("http://api.bukget.org/3/plugins/bukkit/commandsex/latest/download"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                break;
            }

            case JENKINS: {
                new DownloadProgressMonitor("Latest Development Build", Utils.getLatestJenkinsDownload());
                break;
            }

            case OWN_JAR: {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setAcceptAllFileFilterUsed(false);
                jFileChooser.addChoosableFileFilter(new FileFilter() {

                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().toLowerCase().endsWith(".jar");
                    }

                    @Override
                    public String getDescription() {
                        return "CommandsEX JAR Files";
                    }
                });

                int returnVal = jFileChooser.showOpenDialog(frame);

                if (returnVal != JFileChooser.APPROVE_OPTION){
                    System.exit(0);
                }

                FeatureSelection.jar = jFileChooser.getSelectedFile();
            }
        }

        frame.setContentPane(new FeatureSelection().jPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(new Dimension(500, 500));
        frame.setVisible(true);
    }

    private Version showVersionDialogue(){
        String[] choices = new String[Version.values().length];

        for (int i = 0; i < Version.values().length; i++){
            Version version = Version.values()[i];
            choices[i] = version.getMessage();
        }

        Version versionChoice = Version.getFromMessage((String) JOptionPane.showInputDialog(jPanel, "Please choose which version of CommandsEX you would like to use:", "Select a CommandsEX Version", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]));

        if (versionChoice == null){
            /*JOptionPane.showMessageDialog(jPanel, "You must select a CommandsEX version");
            return showVersionDialogue();*/

            System.exit(0);
        }

        return versionChoice;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex){
            ex.printStackTrace();
        }

        new Main();
    }
}
