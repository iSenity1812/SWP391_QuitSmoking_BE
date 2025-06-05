package com.swp391project.SWP391_QuitSmoking_BE.interfaces;

import com.swp391project.SWP391_QuitSmoking_BE.enums.DurationType;

public interface IDurationAware {
    Integer getDuration();
    DurationType getDurationType();
}
