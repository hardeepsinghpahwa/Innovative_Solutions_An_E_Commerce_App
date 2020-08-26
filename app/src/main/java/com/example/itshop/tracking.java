package com.example.itshop;

public class tracking implements Comparable{
    String track;
    Long timestamp;

    public tracking(String track, Long timestamp) {
        this.track = track;
        this.timestamp = timestamp;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public tracking() {
    }

    @Override
    public int compareTo(Object o) {
        Long com=((tracking)o).getTimestamp();
        return (int) (this.timestamp-com);
    }
}
