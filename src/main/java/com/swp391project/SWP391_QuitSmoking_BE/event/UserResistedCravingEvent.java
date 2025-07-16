package com.swp391project.SWP391_QuitSmoking_BE.event;

import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserResistedCravingEvent extends ApplicationEvent {
    private final User user;

    public UserResistedCravingEvent(User user) {
        super(user);
        this.user = user;
    }
}
