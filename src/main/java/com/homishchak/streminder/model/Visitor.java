package com.homishchak.streminder.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private int verificationCode;

    private Long beforeReminderTime;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "visitor", fetch = FetchType.LAZY)
    private List<Task> tasks;
}
