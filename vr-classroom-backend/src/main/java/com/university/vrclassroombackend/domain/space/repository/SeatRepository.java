package com.university.vrclassroombackend.domain.space.repository;

import com.university.vrclassroombackend.domain.space.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Integer> {
    List<Seat> findByClassroomId(Integer classroomId);
    List<Seat> findByStatus(Integer status);
}
