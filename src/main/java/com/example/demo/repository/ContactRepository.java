package com.example.demo.repository;

import com.example.demo.model.Contact;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/*
@Repository stereotype annotation is used to add a bean of this class
type to the Spring context and indicate that given Bean is used to perform
DB related operations and
* */
@Repository
public interface ContactRepository extends PagingAndSortingRepository<Contact,Integer> {

    List<Contact> findByStatus(String status);

    //@Query("SELECT c FROM Contact c WHERE c.status=:status") JPQL Query
    //@Query(value="SELECT * FROM test_contact_msg c WHERE c.status=:status",nativeQuery = true)  //Native SQL Query
    Page<Contact> findByStatus(String status, Pageable pageable);


    @Transactional
    @Modifying
    @Query("UPDATE Contact c SET c.status =?1 WHERE c.contactId =?2")
    int updateStatusById(String status,int id);

    Page<Contact> findOpenMsgs(@Param("status") String status,Pageable pageable);

    @Transactional
    @Modifying
    int updateMsgStatus(String status,int id);

}
