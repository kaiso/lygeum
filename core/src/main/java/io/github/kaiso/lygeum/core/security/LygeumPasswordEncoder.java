package io.github.kaiso.lygeum.core.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component("passwordEncoder")
public class LygeumPasswordEncoder extends BCryptPasswordEncoder {

}
