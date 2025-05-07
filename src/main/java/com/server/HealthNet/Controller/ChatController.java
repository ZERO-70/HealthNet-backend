package com.server.HealthNet.Controller;

import com.server.HealthNet.Model.Chat;
import com.server.HealthNet.Model.Role;
import com.server.HealthNet.Model.UserAuthentication;
import com.server.HealthNet.Service.ChatService;
import com.server.HealthNet.Service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserAuthenticationService userAuthService;

    // Admin: get all chats
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Chat> getAllChats() {
        return chatService.getAllChats();
    }

    // Admin or owner: get chat by id
    @GetMapping("/{id}")
    public ResponseEntity<Chat> getChatById(@PathVariable Long id) {
        Chat chat = chatService.getChatById(id);
        if (chat == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication user = userAuthService.getUserByUsername(username);
        if (user.getRole().equals(Role.ADMIN) || user.getPersonId().equals(chat.getPersonId())) {
            return new ResponseEntity<>(chat, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    // Create chat: any authenticated person (patient, doctor, staff)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> createChat(@RequestBody Chat chat) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication user = userAuthService.getUserByUsername(username);
        chat.setPersonId(user.getPersonId());
        int res = chatService.createChat(chat);
        return res > 0
                ? new ResponseEntity<>("Chat created", HttpStatus.OK)
                : new ResponseEntity<>("Failed to create chat", HttpStatus.BAD_REQUEST);
    }

    // Update chat: admin or owner
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateChat(@RequestBody Chat chat) {
        Chat existing = chatService.getChatById(chat.getMessageId());
        if (existing == null) {
            return new ResponseEntity<>("Chat not found", HttpStatus.NOT_FOUND);
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication user = userAuthService.getUserByUsername(username);
        if (!user.getRole().equals(Role.ADMIN) && !user.getPersonId().equals(existing.getPersonId())) {
            return new ResponseEntity<>("Not authorized", HttpStatus.FORBIDDEN);
        }
        int res = chatService.updateChat(chat);
        return res > 0
                ? new ResponseEntity<>("Chat updated", HttpStatus.OK)
                : new ResponseEntity<>("Update failed", HttpStatus.BAD_REQUEST);
    }

    // Delete chat: admin or owner
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteChat(@PathVariable Long id) {
        Chat existing = chatService.getChatById(id);
        if (existing == null) {
            return new ResponseEntity<>("Chat not found", HttpStatus.NOT_FOUND);
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication user = userAuthService.getUserByUsername(username);
        if (!user.getRole().equals(Role.ADMIN) && !user.getPersonId().equals(existing.getPersonId())) {
            return new ResponseEntity<>("Not authorized", HttpStatus.FORBIDDEN);
        }
        int res = chatService.deleteChat(id);
        return res > 0
                ? new ResponseEntity<>("Chat deleted", HttpStatus.OK)
                : new ResponseEntity<>("Deletion failed", HttpStatus.BAD_REQUEST);
    }

    // Get chats for current user
    @GetMapping("/getmine")
    @PreAuthorize("isAuthenticated()")
    public List<Chat> getMyChats() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication user = userAuthService.getUserByUsername(username);
        return chatService.getChatsByPersonId(user.getPersonId());
    }
}