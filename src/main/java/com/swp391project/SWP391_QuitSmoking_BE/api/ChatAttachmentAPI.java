package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatAttachment;
import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatMessage;
import com.swp391project.SWP391_QuitSmoking_BE.service.ChatAttachmentService;
import com.swp391project.SWP391_QuitSmoking_BE.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat/attachments")
public class ChatAttachmentAPI {
    @Autowired
    private ChatAttachmentService chatAttachmentService;
    @Autowired
    private ChatMessageService chatMessageService;

    @PostMapping
    public ChatAttachment uploadAttachment(@RequestBody ChatAttachment attachment) {
        return chatAttachmentService.saveAttachment(attachment);
    }

    @GetMapping("/message/{messageId}")
    public List<ChatAttachment> getAttachmentsByMessage(@PathVariable Integer messageId) {
        Optional<ChatMessage> message = chatMessageService.getMessageById(messageId);
        return message.map(chatAttachmentService::getAttachmentsByMessage).orElse(List.of());
    }
}