package com.example.second_cache_study.infrastructure;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.List;

@SpringBootTest
public class SecondLevelCacheTest {
    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    HeaderEntityRepository headerEntityRepository;

    @Autowired
    NodeEntityRepository nodeEntityRepository;

    HeaderEntity headerEntity;

    @BeforeEach
    void init1() {
        transactionTemplate.executeWithoutResult(status -> {
                    headerEntity = new HeaderEntity();
                    headerEntityRepository.save(headerEntity);

                    NodeEntity nodeEntityA = new NodeEntity("node 1");
                    NodeEntity nodeEntityB = new NodeEntity("node 2");
                    NodeEntity nodeEntityC = new NodeEntity("node 3");

                    nodeEntityA.setNextNode(nodeEntityB);
                    nodeEntityB.setNextNode(nodeEntityC);
                    nodeEntityC.setNextNode(null);

                    nodeEntityA.setPrevNode(null);
                    nodeEntityB.setPrevNode(nodeEntityA);
                    nodeEntityC.setPrevNode(nodeEntityB);

                    nodeEntityA.setHeader(headerEntity);
                    nodeEntityB.setHeader(headerEntity);
                    nodeEntityC.setHeader(headerEntity);

                    List<NodeEntity> nodes = List.of(nodeEntityA, nodeEntityB, nodeEntityC);
                    nodeEntityRepository.saveAll(nodes);
                    headerEntity.getNodes().addAll(nodes);
                }
        );
    }

    @Test
    void init() {
        //given
        entityManagerFactory.getCache().evictAll();
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);

        //when
        transactionTemplate.executeWithoutResult(status -> {
            HeaderEntity headerEntity = headerEntityRepository.findById(1L).get();
            headerEntity.getNodes().get(0);
        });

        transactionTemplate.executeWithoutResult(status -> {
            HeaderEntity headerEntity = headerEntityRepository.findById(1L).get();
            List<NodeEntity> nodes = headerEntity.getNodes();
            headerEntity.getNodes()
                    .forEach(e -> System.out.println(e.getName()));
        });

        //then
        System.out.println("hit : " + statistics.getSecondLevelCacheHitCount());
        System.out.println("miss : " + statistics.getSecondLevelCacheMissCount());
        System.out.println("put : " + statistics.getSecondLevelCachePutCount());
    }
}
