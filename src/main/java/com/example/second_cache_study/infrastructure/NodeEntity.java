package com.example.second_cache_study.infrastructure;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NodeEntity {
    @Id
    @Column(name = "node_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "prev_node_id")
    private NodeEntity prevNode;

    @OneToOne
    @JoinColumn(name = "next_node_id")
    private NodeEntity nextNode;

    @ManyToOne
    @JoinColumn(name = "header_id")
    private HeaderEntity header;
}
