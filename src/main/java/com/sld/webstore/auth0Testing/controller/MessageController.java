package com.sld.webstore.auth0Testing.controller;

import com.sld.webstore.models.Message;
import com.sld.webstore.user.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/public")
    public Message getPublic() {
        return messageService.getPublicMessage();
    }

    @GetMapping("/protected")
    public Message getProtected() {
        return messageService.getProtectedMessage();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('read:admin-messages')")
    public Message getAdmin() {
        return messageService.getAdminMessage();
    }
}
