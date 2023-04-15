package com.example.mzrt.repository;

import com.example.mzrt.model.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {

    List<Order> findByUserId(int userId, Sort sort);

    List<Order> findByUserIdAndStrategy(int userId, String strategy, Sort sort);

    @Modifying
    @Query("DELETE FROM mzrt_order WHERE user_id = :usrId")
    int deleteOrdersByUserId(@Param("usrId") int userId);
}
