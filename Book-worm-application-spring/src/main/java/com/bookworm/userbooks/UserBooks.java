package com.bookworm.userbooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.time.LocalDate;

@Table(value = "book_by_user_and_bookid")
public class UserBooks {

    @PrimaryKey
    private UserBooksPrimaryKey userBooksPrimaryKey;

    @Column("started_date")
    @CassandraType(type = CassandraType.Name.DATE)
    private LocalDate startedDate;

    @Column("completed_date")
    @CassandraType(type = CassandraType.Name.DATE)
    private LocalDate completedDate;

    @Column("reading_status")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String readingStatus;

    @Column("rating")
    @CassandraType(type = CassandraType.Name.INT)
    private int rating;

    public UserBooksPrimaryKey getUserBooksPrimaryKey() {
        return userBooksPrimaryKey;
    }

    public void setUserBooksPrimaryKey(UserBooksPrimaryKey userBooksPrimaryKey) {
        this.userBooksPrimaryKey = userBooksPrimaryKey;
    }

    public LocalDate getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(LocalDate startedDate) {
        this.startedDate = startedDate;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public String getReadingStatus() {
        return readingStatus;
    }

    public void setReadingStatus(String readingStatus) {
        this.readingStatus = readingStatus;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
