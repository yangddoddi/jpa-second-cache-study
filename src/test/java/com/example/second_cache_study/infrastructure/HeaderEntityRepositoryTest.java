package com.example.second_cache_study.infrastructure;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@DataJpaTest
//@AutoConfigureDataJpa
class HeaderEntityRepositoryTest {
    @Autowired
    private HeaderEntityRepository headerEntityRepository;

    @Autowired
    private NodeEntityRepository nodeEntityRepository;

    @Autowired
    private EntityManager entityManager;

    @PersistenceUnit
    EntityManagerFactory entityManagerFactory;

    HeaderEntity headerEntity;

    SessionFactory sessionFactory;

    Statistics statistics;

    @BeforeEach
    void init() {
        headerEntity = new HeaderEntity();
        headerEntityRepository.save(headerEntity);

        NodeEntity nodeEntityA = new NodeEntity();
        NodeEntity nodeEntityB = new NodeEntity();
        NodeEntity nodeEntityC = new NodeEntity();

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

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("정상 저장 여부 테스트")
    public void basicTest() {
        // given
        // when
        HeaderEntity header = headerEntityRepository.findById(headerEntity.getId()).get();

        // then
        assertEquals(3, header.getNodes().size());
    }

    @Test
    @DisplayName("next node 조회 시 추가 쿼리 테스트")
    public void basicTest2() {
        //given
        //when
        HeaderEntity header = headerEntityRepository.findById(headerEntity.getId()).get();
        List<NodeEntity> nodes = header.getNodes();

        NodeEntity firstNode = nodes.stream()
                .filter(e -> e.getPrevNode() == null)
                .findFirst()
                .get();

        NodeEntity secondNode = firstNode.getNextNode();
        NodeEntity thirdNode = secondNode.getNextNode();
    }

    @Test
    @DisplayName("2회 조회 시 추가 쿼리 테스트")
    public void secondCacheTest() {
        // given
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class).getCache().getSessionFactory();
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);

        // when
        HeaderEntity header = headerEntityRepository.findById(headerEntity.getId()).get();
        List<NodeEntity> nodes = header.getNodes();

        NodeEntity firstNode = nodes.stream()
                .filter(node -> node.getPrevNode() == null)
                .findFirst()
                .get();

        NodeEntity secondNode = firstNode.getNextNode();
        NodeEntity thirdNode = secondNode.getNextNode();

        entityManager.detach(header);
        entityManager.detach(firstNode);
        entityManager.detach(secondNode);
        entityManager.detach(thirdNode);

        HeaderEntity header2 = headerEntityRepository.findById(headerEntity.getId()).get();
        List<NodeEntity> nodes2 = header2.getNodes();

        NodeEntity firstNode2 = nodes2.stream()
                .filter(node -> node.getPrevNode() == null)
                .findFirst()
                .get();

        NodeEntity secondNode2 = firstNode2.getNextNode();
        NodeEntity thirdNode2 = secondNode2.getNextNode();

        //then
        long next = sessionFactory.getStatistics().getSecondLevelCacheHitCount();

        System.out.println("second level cache hit count: " + statistics.getSecondLevelCacheHitCount());
        System.out.println("second level cache miss count: " + statistics.getSecondLevelCacheMissCount());
        System.out.println("second level cache put count: " + statistics.getSecondLevelCachePutCount());
    }
}