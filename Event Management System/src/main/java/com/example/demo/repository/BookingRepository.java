package com.example.demo.repository;

import com.example.demo.model.Booking;
import com.example.demo.model.Event;
import com.example.demo.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByEvent(Event event);

    boolean existsByUserAndEvent(User user, Event event);
}