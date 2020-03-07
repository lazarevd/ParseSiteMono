package ru.laz.telegram;

public class TelegramDTO {
    int chat_id;
    String text;
    String parse_mode = "HTML";

    public TelegramDTO(int chat_id, String text) {
        this.chat_id = chat_id;
        this.text = text;
    }

    public TelegramDTO() {}

    public int getChat_id() {
        return chat_id;
    }

    public void setChat_id(int chat_id) {
        this.chat_id = chat_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setParse_mode(String parse_mode) {
        this.parse_mode = parse_mode;
    }

    public String getParse_mode() {
        return parse_mode;
    }
}
