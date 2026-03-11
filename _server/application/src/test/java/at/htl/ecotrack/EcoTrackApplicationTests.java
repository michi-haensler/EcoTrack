package at.htl.ecotrack;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestJwtConfig.class)
class EcoTrackApplicationTests {

    @Test
    void contextLoads() {
    }
}
