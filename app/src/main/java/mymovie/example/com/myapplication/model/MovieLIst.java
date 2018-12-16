package mymovie.example.com.myapplication.model;

import java.util.ArrayList;
import java.util.List;

public class MovieLIst {
    public String lastBuildDate;
    public String total;
    public String start;
    public String display;
    public List<MovieContent> items = new ArrayList<>();

    public MovieLIst() {
    }

    public MovieLIst(String lastBuildDate, String total, String start, String display, List<MovieContent> items) {
        this.lastBuildDate = lastBuildDate;
        this.total = total;
        this.start = start;
        this.display = display;
        this.items = items;
    }
    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public String getTotal() {
        return total;
    }

    public String getStart() {
        return start;
    }

    public String getDisplay() {
        return display;
    }

    public List<MovieContent> getItems() {
        return items;
    }
    @Override
    public String toString() {
        return "MovieLIst{" +
                "lastBuildDate='" + lastBuildDate + '\'' +
                ", total='" + total + '\'' +
                ", start='" + start + '\'' +
                ", display='" + display + '\'' +
                ", items=" + items +
                '}';
    }



    public static class MovieContent{
        public String title;
        public String link;
        public String image;
        public String pubDate;
        public String director;
        public String actor;
        public String userRating;

        public MovieContent() {
        }

        public MovieContent(String title, String link, String image, String pubDate, String director, String actor, String userRating) {
            this.title = title;
            this.link = link;
            this.image = image;
            this.pubDate = pubDate;
            this.director = director;
            this.actor = actor;
            this.userRating = userRating;
        }



        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public String getImageUrl() {
            return image;
        }

        public String getPubDate() {
            return pubDate;
        }

        public String getDirector() {
            return director;
        }

        public String getActor() {
            return actor;
        }

        public String getUserRating() {
            return userRating;
        }

        @Override
        public String toString() {
            return "MovieContent{" +
                    "title='" + title + '\'' +
                    ", link='" + link + '\'' +
                    ", image='" + image + '\'' +
                    ", pubDate='" + pubDate + '\'' +
                    ", director='" + director + '\'' +
                    ", actor='" + actor + '\'' +
                    ", userRating='" + userRating + '\'' +
                    '}';
        }
    }
}
