package com.skillnexus.blog;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BlogApiApplicationTest {
    @Test void applicationClassLoads() {
        assertNotNull(BlogApiApplication.class);
    }
}
