package com.shivam.cloudlet_api.models;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "tokens")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Token {

    @Id
    private String id;

    private String token;

    private String userId;

    @CreatedDate
    private Date createdAt;

    @Indexed(name = "expireAtIndex", expireAfterSeconds = 0)
    private Date expireAt;
}
