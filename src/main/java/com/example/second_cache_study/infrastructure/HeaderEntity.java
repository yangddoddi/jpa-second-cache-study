package com.example.second_cache_study.infrastructure;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HeaderEntity {
    @Id
    @Column(name = "header_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    private List<NodeEntity> nodes = new ArrayList<>();
}
