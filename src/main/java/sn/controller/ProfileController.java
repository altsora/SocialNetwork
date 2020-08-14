package sn.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class ProfileController {

    @GetMapping("/me")
    public ResponseEntity<Object> getCurrentUser() {
        //todo
        return null;
    }

    @PutMapping("/me")
    public ResponseEntity<Object> editCurrentUser() {
        //todo
        return null;
    }

    @DeleteMapping("/me")
    public ResponseEntity<Object> deleteCurrentUser() {
        //todo
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById() {
        //todo
        return null;
    }

    @GetMapping("/{id}/wall")
    public ResponseEntity<Object> getWallEntriesByUserId() {
        //todo
        return null;
    }

    @PostMapping("/{id}/wall")
    public ResponseEntity<Object> setWallEntriesByUserId() {
        //todo
        return null;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findUser() {
        //todo
        return null;
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<Object> blockUserById() {
        //todo
        return null;
    }

    @DeleteMapping("/block/{id}")
    public ResponseEntity<Object> unblockUserById() {
        //todo
        return null;
    }
}
