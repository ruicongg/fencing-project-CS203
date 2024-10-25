package org.fencing.demo.AfterEvent;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface AfterEventRepository extends JpaRepository<AfterEvent, Long>{
    
}
    

