package org.qedu;

import org.backend.qedu.QEduApplication;
import org.springframework.boot.SpringApplication;

public class TestQEduApplication {

    public static void main(String[] args) {
        SpringApplication.from(QEduApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
