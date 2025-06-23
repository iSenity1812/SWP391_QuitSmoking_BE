package com.swp391project.SWP391_QuitSmoking_BE;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = Swp391QuitSmokingBeApplication.class)
@Import(TestMailConfig.class)
class Swp391QuitSmokingBeApplicationTests {

	@Test
	void contextLoads() {
	}

}