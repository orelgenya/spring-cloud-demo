package com.github.orelgenya;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrainRestController {

    @GetMapping("/echo")
    @PreAuthorize("hasRole('TELLER')")
    public String echo(String msg) {
        return msg;
    }

    @GetMapping("/data")
    public SensetiveData data() {
        return new SensetiveData();
    }

    @GetMapping("/authn")
    public Authentication authn(Authentication authn) {
        return authn;
    }

    @Data
    class SensetiveData implements X {

        int a = 1;

        int b = 2;

        int c = 3;

        int d = 4;

        public int getA() {
            return a;
        }

        @PreAuthorize("hasAnyRole('USER', 'TELLER', 'SUPERVISOR')")
        public int getB() {
            return b;
        }

        @PreAuthorize("hasAnyRole('TELLER', 'SUPERVISOR')")
        public int getC() {
            return c;
        }
    }

    interface X {

        int getA();

        @PreAuthorize("hasAnyRole('USER', 'TELLER', 'SUPERVISOR')")
        int getB();

        @PreAuthorize("hasAnyRole('TELLER', 'SUPERVISOR')")
        int getC();

        @Value("#{@authentication.authorities.contains('ROLE_SUPERVISOR') ? target.d : null}")
        int getD();
    }
}
