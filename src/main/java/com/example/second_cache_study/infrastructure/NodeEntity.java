package com.example.second_cache_study.infrastructure;

import lombok.*;

import javax.persistence.*;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE;

@Entity
@Getter
@Setter
@Cacheable
@AllArgsConstructor
@org.hibernate.annotations.Cache(usage = READ_WRITE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NodeEntity {
    @Id
    @Column(name = "node_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prev_node_id")
    private NodeEntity prevNode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_node_id")
    private NodeEntity nextNode;

    @ManyToOne
    @JoinColumn(name = "header_id")
    private HeaderEntity header;

    private String name;

    public NodeEntity(String name) {
        this.name = name;
    }
}
