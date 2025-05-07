package com.server.HealthNet.Service;

import com.server.HealthNet.Model.Chat;
import com.server.HealthNet.Repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    public Chat getChatById(Long id) {
        return chatRepository.findById(id);
    }

    public List<Chat> getChatsByPersonId(Long personId) {
        return chatRepository.findByPersonId(personId);
    }

    public int createChat(Chat chat) {
        return chatRepository.save(chat);
    }

    public int updateChat(Chat chat) {
        return chatRepository.update(chat);
    }

    public int deleteChat(Long id) {
        return chatRepository.deleteById(id);
    }
}