package by.qstore.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("by.qstore.common.entity")
public class QStoreAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(QStoreAdminApplication.class, args);
    }

}
