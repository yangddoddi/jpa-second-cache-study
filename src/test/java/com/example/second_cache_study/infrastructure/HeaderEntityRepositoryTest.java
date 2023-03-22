package com.example.second_cache_study.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@DataJpaTest
@AutoConfigureDataJpa
class HeaderEntityRepositoryTest {
    @Autowired
    private HeaderEntityRepository headerEntityRepository;

    @Autowired
    private NodeEntityRepository nodeEntityRepository;

    @Autowired
    private EntityManager entityManager;

    HeaderEntity headerEntity;

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
}