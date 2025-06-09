package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.ChatMessageDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatMessage;
import com.swp391project.SWP391_QuitSmoking_BE.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDTO messageDTO) {
        ChatMessage savedMessage = chatService.saveMessage(messageDTO);
        ChatMessageDTO responseDTO = new ChatMessageDTO();
        responseDTO.setSenderId(savedMessage.getSenderId());
        responseDTO.setReceiverId(savedMessage.getReceiverId());
        responseDTO.setContent(savedMessage.getContent());
        responseDTO.setSentDate(savedMessage.getSentDate());
        responseDTO.setConversationId(savedMessage.getConversationId());

        messagingTemplate.convertAndSendToUser(
                messageDTO.getReceiverId().toString(),
                "/queue/messages",
                responseDTO
        );
        messagingTemplate.convertAndSendToUser(
                messageDTO.getSenderId().toString(),
                "/queue/messages",
                responseDTO
        );
    }
}