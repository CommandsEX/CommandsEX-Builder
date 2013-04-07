package com.commandsex.builder;

public class BuilderItem extends CheckBoxListEntry {

    String file;

    public BuilderItem(String text, String file) {
        super(text, false);
        this.file = file;
    }

    public String getCorrespondingFile(){
        return file;
    }
}
