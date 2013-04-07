package com.commandsex.builder;

import com.commandsex.api.annotations.Builder;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.reflections.Reflections;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FeatureSelection {
    private JTabbedPane tabbedPane1;
    public JPanel jPanel;
    private CheckBoxList commands;
    private CheckBoxList events;
    private CheckBoxList packages;
    private CheckBoxList langs;
    private JPanel misc;
    private CheckBoxList miscs;
    private JButton buildButton;
    private JButton importButton;
    private JSplitPane splitPane;
    private JButton selectAllButton;
    private JButton deselectAllButton;

    public static File jar = null;

    public FeatureSelection() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex){
            ex.printStackTrace();
        }

        // stop the divider being moved
        splitPane.setEnabled(false);

        try {
            ClasspathHacker.addFile(jar);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(Main.frame, "Unable to add file " + jar.getPath() + " to the system claspath", "Fatal Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        Reflections reflections = new Reflections("com.commandsex");
        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.addAll(reflections.getTypesAnnotatedWith(Builder.class));

        List<JCheckBox> commandItems = new ArrayList<JCheckBox>();
        List<JCheckBox> eventItems = new ArrayList<JCheckBox>();
        List<JCheckBox> packageItems = new ArrayList<JCheckBox>();
        List<JCheckBox> miscItems = new ArrayList<JCheckBox>();

        for (Class<?> clazz : classes){
            Builder builder = clazz.getAnnotation(Builder.class);
            String name = builder.name();
            String description = builder.description();
            boolean core = builder.copy();

            if (builder.show()){
                switch (builder.type().toLowerCase()){
                    default: {
                        JOptionPane.showMessageDialog(Main.frame, clazz.getName() + " has an incorrect type in the Builder annotation", "Error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }

                    case ("command"): {
                        JCheckBox checkBox = new BuilderItem("/" + name + " - " + description, clazz.getName());

                        if (core){
                            checkBox.setEnabled(false);
                            checkBox.setSelected(true);
                        }

                        commandItems.add(checkBox);
                        break;
                    }

                    case ("event"): {
                        JCheckBox checkBox = new BuilderItem(name + " - " + description, clazz.getName());

                        if (core){
                            checkBox.setEnabled(false);
                            checkBox.setSelected(true);
                        }

                        eventItems.add(checkBox);
                        break;
                    }

                    case ("package"): {
                        JCheckBox checkBox = new BuilderItem(name + " - " + description, clazz.getName());

                        if (core){
                            checkBox.setEnabled(false);
                            checkBox.setSelected(true);
                        }

                        packageItems.add(checkBox);
                        break;
                    }

                    case ("misc"): {
                        JCheckBox checkBox = new BuilderItem(name + " - " + description, clazz.getName());

                        if (core){
                            checkBox.setEnabled(false);
                            checkBox.setSelected(true);
                        }

                        miscItems.add(checkBox);
                        break;
                    }
                }
            }
        }

        List<JCheckBox> langItems = new ArrayList<JCheckBox>();

        for (String file : Utils.getJarFileListing(jar, "(^lang/lang_)\\w+((-\\w+)+)?(\\.properties$)")){
            String name = Utils.readFirstLine(ClassLoader.getSystemResourceAsStream(file)).replaceFirst("# Name: ", "");
            JCheckBox checkBox = new BuilderItem(name, file);
            checkBox.setSelected(true);
            langItems.add(checkBox);
        }

        commands.setListData(commandItems.toArray());
        events.setListData(eventItems.toArray());
        packages.setListData(packageItems.toArray());
        miscs.setListData(miscItems.toArray());
        langs.setListData(langItems.toArray());
        selectAllButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectAll();
            }
        });
        deselectAllButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                deselectAll();
            }
        });
        buildButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (langs.getAllItems().size() == 0){
                    JOptionPane.showMessageDialog(Main.frame, "You must select at least one language", "Error", JOptionPane.ERROR_MESSAGE);
                }

                List<String> removeItems = new ArrayList<String>();

                for (JCheckBox checkBox : commands.getAllItems()){
                    if (!checkBox.isSelected()){
                        removeItems.add(((BuilderItem) checkBox).getCorrespondingFile());
                    }
                }

                for (JCheckBox checkBox : events.getAllItems()){
                    if (!checkBox.isSelected()){
                        removeItems.add(((BuilderItem) checkBox).getCorrespondingFile());
                    }
                }

                for (JCheckBox checkBox : packages.getAllItems()){
                    if (!checkBox.isSelected()){
                        removeItems.add(((BuilderItem) checkBox).getCorrespondingFile());
                    }
                }

                for (JCheckBox checkBox : langs.getAllItems()){
                    if (!checkBox.isSelected()){
                        removeItems.add(((BuilderItem) checkBox).getCorrespondingFile());
                    }
                }

                for (JCheckBox checkBox : miscs.getAllItems()){
                    if (!checkBox.isSelected()){
                        removeItems.add(((BuilderItem) checkBox).getCorrespondingFile());
                    }
                }

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.addChoosableFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.getName().endsWith(".jar") || f.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return "CommandsEX JAR File (.jar)";
                    }
                });

                int returnVal = fileChooser.showSaveDialog(Main.frame);

                if (returnVal == JFileChooser.APPROVE_OPTION){
                    File chosenFile = fileChooser.getSelectedFile();
                    if (!chosenFile.getName().endsWith(".jar")){
                        chosenFile = new File(chosenFile.getAbsolutePath() + ".jar");
                    }

                    if (chosenFile.exists()){
                        chosenFile.delete();
                    }

                    if (!chosenFile.exists()){
                        try {
                            chosenFile.createNewFile();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(Main.frame, e1.getMessage(), "Error creating file", JOptionPane.ERROR_MESSAGE);
                            System.exit(1);
                        }
                    }

                    try {
                        byte[] buffer = new byte[2048];
                        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(jar));
                        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(chosenFile));

                        ZipEntry entry;

                        try {
                            while ((entry = zipInputStream.getNextEntry()) != null){
                                String realName = entry.getName();
                                String name = realName.endsWith(".class") ? realName.substring(0, realName.length() - 6) : realName;

                                if (!removeItems.contains(name)){
                                    try {
                                        zipOutputStream.putNextEntry(new ZipEntry(realName));

                                        int len;
                                        while ((len = zipInputStream.read(buffer)) > 0){
                                            zipOutputStream.write(buffer, 0, len);
                                        }
                                    } finally {
                                        zipOutputStream.closeEntry();
                                    }
                                }
                            }
                        } finally {
                            zipInputStream.close();
                            zipOutputStream.close();
                        }

                        JOptionPane.showMessageDialog(Main.frame, "Custom CommandsEX Build completed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        Main.tempDir.delete();
                        System.exit(0);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        JOptionPane.showMessageDialog(Main.frame, throwable.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                }
            }
        });
        importButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.addChoosableFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.getName().endsWith(".jar") || f.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return "CommandsEX JAR File (.jar)";
                    }
                });
                int returnVal = fileChooser.showOpenDialog(Main.frame);

                if (returnVal == JFileChooser.APPROVE_OPTION){
                    File chosenFile = fileChooser.getSelectedFile();
                    deselectAll();

                    try {
                        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(chosenFile));
                        ZipEntry zipEntry;

                        try {
                            while ((zipEntry = zipInputStream.getNextEntry()) != null){
                                try {
                                    String name = (zipEntry.getName().endsWith(".class") ? zipEntry.getName().substring(0, zipEntry.getName().length() - 6) : zipEntry.getName()).replaceAll("/", ".");

                                    for (JCheckBox checkBox : commands.getAllItems()){
                                        BuilderItem builderItem = (BuilderItem) checkBox;
                                        if (name.equals(builderItem.getCorrespondingFile())){
                                            builderItem.setSelected(true);
                                        }
                                    }

                                    for (JCheckBox checkBox : events.getAllItems()){
                                        BuilderItem builderItem = (BuilderItem) checkBox;
                                        if (name.equals(builderItem.getCorrespondingFile())){
                                            builderItem.setSelected(true);
                                        }
                                    }

                                    for (JCheckBox checkBox : packages.getAllItems()){
                                        BuilderItem builderItem = (BuilderItem) checkBox;
                                        if (name.equals(builderItem.getCorrespondingFile())){
                                            builderItem.setSelected(true);
                                        }
                                    }

                                    for (JCheckBox checkBox : langs.getAllItems()){
                                        BuilderItem builderItem = (BuilderItem) checkBox;
                                        if (name.equals(builderItem.getCorrespondingFile())){
                                            builderItem.setSelected(true);
                                        }
                                    }

                                    for (JCheckBox checkBox : miscs.getAllItems()){
                                        BuilderItem builderItem = (BuilderItem) checkBox;
                                        if (name.equals(builderItem.getCorrespondingFile())){
                                            builderItem.setSelected(true);
                                        }
                                    }

                                    Main.frame.repaint();
                                } finally {
                                    zipInputStream.closeEntry();
                                }
                            }
                        } finally {
                            zipInputStream.close();
                        }
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        JOptionPane.showMessageDialog(Main.frame, "Error while importing JAR", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    public void deselectAll(){
        for (JCheckBox checkBox : commands.getAllItems()){
            if (checkBox.isEnabled()){
                checkBox.setSelected(false);
            }
        }

        for (JCheckBox checkBox : events.getAllItems()){
            if (checkBox.isEnabled()){
                checkBox.setSelected(false);
            }
        }

        for (JCheckBox checkBox : packages.getAllItems()){
            if (checkBox.isEnabled()){
                checkBox.setSelected(false);
            }
        }

        for (JCheckBox checkBox : langs.getAllItems()){
            if (checkBox.isEnabled()){
                checkBox.setSelected(false);
            }
        }

        for (JCheckBox checkBox : miscs.getAllItems()){
            if (checkBox.isEnabled()){
                checkBox.setSelected(false);
            }
        }

        Main.frame.repaint();
    }

    public void selectAll(){
        for (JCheckBox checkBox : commands.getAllItems()){
            if (checkBox.isEnabled()){
                checkBox.setSelected(true);
            }
        }

        for (JCheckBox checkBox : events.getAllItems()){
            if (checkBox.isEnabled()){
                checkBox.setSelected(true);
            }
        }

        for (JCheckBox checkBox : packages.getAllItems()){
            if (checkBox.isEnabled()){
                checkBox.setSelected(true);
            }
        }

        for (JCheckBox checkBox : langs.getAllItems()){
            if (checkBox.isEnabled()){
                checkBox.setSelected(true);
            }
        }

        for (JCheckBox checkBox : miscs.getAllItems()){
            if (checkBox.isEnabled()){
                checkBox.setSelected(true);
            }
        }

        Main.frame.repaint();
    }
}
