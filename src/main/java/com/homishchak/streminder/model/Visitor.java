package com.homishchak.streminder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long visitorId;

    private String email;

    private int verificationCode;

    private Long beforeReminderTime;

    @OneToMany(mappedBy = "visitor", fetch = FetchType.EAGER)
    private List<Task> tasks;
}
