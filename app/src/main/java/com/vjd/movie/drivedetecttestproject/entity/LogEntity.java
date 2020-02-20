package com.vjd.movie.drivedetecttestproject.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.Objects;

@Entity
public class LogEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String state;

    public Date getDateAdded() {
        return dateAdded;
    }

    public LogEntity(String state, String cconfidence, Date dateAdded) {
        this.state = state;
        this.dateAdded = dateAdded;
        this.cconfidence = cconfidence;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Date dateAdded;

    public LogEntity(String state, String cconfidence) {
        this.state = state;
        this.cconfidence = cconfidence;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCconfidence() {
        return cconfidence;
    }

    public void setCconfidence(String cconfidence) {
        this.cconfidence = cconfidence;
    }

    public String cconfidence;

    public LogEntity() {
    }

    public LogEntity(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogEntity logEntity = (LogEntity) o;
        return id == logEntity.id &&
                Objects.equals(state, logEntity.state) &&
                Objects.equals(dateAdded, logEntity.dateAdded) &&
                Objects.equals(cconfidence, logEntity.cconfidence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, dateAdded, cconfidence);
    }
}
