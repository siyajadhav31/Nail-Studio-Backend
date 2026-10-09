
package com.siya.repository;

import com.siya.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoginHistoryRepository
        extends JpaRepository<LoginHistory, Long> {

    List<LoginHistory> findAllByOrderByLoginTimeDesc();
}