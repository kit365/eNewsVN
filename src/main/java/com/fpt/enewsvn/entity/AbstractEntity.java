package com.fpt.enewsvn.entity;

import com.fpt.enewsvn.Enum.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class AbstractEntity {

    @Column(name = "Created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    Date createdAt;

    @Column(name = "Update_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    Date updatedAt;


    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    Status status = Status.ACTIVE;

    @Column(name = "Deleted")
    boolean deleted;


    //    DeletedAt DATETIME,
//    @Column(name = "delete_at")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date deleteAt;


}
