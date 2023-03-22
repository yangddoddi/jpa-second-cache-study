package com.example.second_cache_study.infrastructure;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.annotations.CacheConcurrencyStrategy.*;

@Entity
@Getter
@Setter
@Cacheable
@org.hibernate.annotations.Cache(usage = READ_WRITE)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HeaderEntity {
    @Id
    @Column(name = "header_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @org.hibernate.annotations.Cache(usage = READ_WRITE)
    @OneToMany(mappedBy = "header", cascade = CascadeType.PERSIST)
    private List<NodeEntity> nodes = new ArrayList<>();
}
