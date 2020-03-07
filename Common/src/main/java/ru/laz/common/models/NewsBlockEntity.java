package ru.laz.common.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "news_blocks")
public class NewsBlockEntity {

    @Id
    @Column(name = "id")
        private int id;
    @Column(name = "date")
        private String date = "";//for unique constaint in sqlite
    @Column(name = "title")
        private String title = "";
    @Column(name = "url")
        private String url = "";
    @Column(name = "body")
        private String body = "";
    @Column(name = "processing")
        private int processing = 0;
    @Column(name = "sent")
        private int sent = 0;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        id = url.hashCode();
    }

    public int getSent() {
        return sent;
    }

    public void setSent(int sent) {
        this.sent = sent;
    }

    public int getProcessing() {
        return processing;
    }

    public void setProcessing(int processing) {
        this.processing = processing;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof NewsBlockEntity)) {
            return false;
        }
        NewsBlockEntity nb = (NewsBlockEntity) o;
        return this.id == nb.getId() && this.url.equals(nb.getUrl()) && this.title.equals(nb.getTitle()) && this.body.equals(nb.getBody() );
    }


    @Override
    public String toString() {
        return "date: " + date + " title: "+title + " url: " + url + " text: " + body;
    }
}

