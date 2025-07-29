package edu.curtin.game;

public class Item {
    private final String name;
    private String message;

    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
}

