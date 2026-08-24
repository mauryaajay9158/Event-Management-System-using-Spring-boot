package com.example.demo.controller;

import com.example.demo.model.Booking;
import com.example.demo.model.Event;
import com.example.demo.model.User;

import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    public HomeController(UserRepository userRepository,
                          EventRepository eventRepository,
                          BookingRepository bookingRepository) {

        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
    }


    // ================= USER LOGIN =================

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }


    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

       


        // USER LOGIN
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent() &&
                user.get().getPassword().equals(password)) {

            session.setAttribute("user", user.get());

            return "redirect:/events";
        }


        model.addAttribute("error",
                "Invalid username or password");

        return "login";
    }


    // ================= USER REGISTRATION =================

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }


    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           Model model) {

        if (!password.equals(confirmPassword)) {

            model.addAttribute("error",
                    "Passwords do not match");

            return "register";
        }


        if (userRepository.findByUsername(username).isPresent()) {

            model.addAttribute("error",
                    "Username already exists");

            return "register";
        }


        User user = new User(
                username,
                email,
                password
        );

        userRepository.save(user);

        return "redirect:/login";
    }


    // ================= USER EVENTS =================

    @GetMapping("/events")
    public String events(Model model,
                          HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        List<Event> events = eventRepository.findAll();

        model.addAttribute("events", events);

        return "events";
    }


    // ================= BOOK EVENT =================

    @PostMapping("/book/{id}")
    public String bookEvent(@PathVariable Long id,
                            HttpSession session,
                            Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }


        Event event = eventRepository
                .findById(id)
                .orElse(null);


        if (event == null) {
            return "redirect:/events";
        }


        // Prevent duplicate booking
        if (bookingRepository.existsByUserAndEvent(user, event)) {

            model.addAttribute("events",
                    eventRepository.findAll());

            model.addAttribute("message",
                    "You have already booked this event.");

            return "events";
        }


        // Save booking
        Booking booking = new Booking(user, event);

        bookingRepository.save(booking);


        model.addAttribute("events",
                eventRepository.findAll());

        model.addAttribute("message",
                "Event booked successfully!");

        return "events";
    }


    // ================= ADMIN PANEL =================

    @GetMapping("/admin")
    public String admin(Model model,
                        HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/login";
        }

        List<Event> events = eventRepository.findAll();

        model.addAttribute("events", events);

        return "admin";
    }


    // ================= CREATE EVENT =================

    @GetMapping("/admin/create")
    public String createEventPage(HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/login";
        }

        return "create-event";
    }


    @PostMapping("/admin/create")
    public String createEvent(@RequestParam String name,
                              @RequestParam LocalDate date,
                              @RequestParam String location,
                              @RequestParam String details,
                              HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/login";
        }


        Event event = new Event(
                name,
                date,
                location,
                details
        );


        // Save event to database
        eventRepository.save(event);

        return "redirect:/admin";
    }


    // ================= VIEW BOOKED USERS =================

    @GetMapping("/admin/event/{id}")
    public String viewUsers(@PathVariable Long id,
                            Model model,
                            HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/login";
        }


        Event event = eventRepository
                .findById(id)
                .orElse(null);


        if (event == null) {
            return "redirect:/admin";
        }


        List<Booking> bookings =
                bookingRepository.findByEvent(event);


        model.addAttribute("event", event);

        model.addAttribute("bookings", bookings);


        return "users";
    }


    // ================= LOGOUT =================

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }

    @GetMapping("/admin/login")
public String adminLoginPage() {

    return "admin-login";
}


@PostMapping("/admin/login")
public String adminLogin(@RequestParam String username,
                         @RequestParam String password,
                         HttpSession session,
                         Model model) {

    if (username.equals("Ajay") &&
        password.equals("123456789")) {

        session.setAttribute("admin", true);

        return "redirect:/admin";
    }

    model.addAttribute("error",
            "Invalid admin username or password");

    return "admin-login";
}







}