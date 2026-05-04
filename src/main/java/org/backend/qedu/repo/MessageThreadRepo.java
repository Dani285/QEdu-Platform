package org.backend.qedu.repo;

import org.backend.qedu.entities.MessageThread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageThreadRepo extends JpaRepository<MessageThread, Long> {

    List<MessageThread> findAllByOrderByCreatedAtDesc();
}
