package ru.laz.common.models;

public class NewsBlockSendStatusDTO {
    public int id;
    public boolean sent;

    public NewsBlockSendStatusDTO() {
    }

    public NewsBlockSendStatusDTO(int id, boolean sent) {
        this.id = id;
        this.sent = sent;
    }


}
