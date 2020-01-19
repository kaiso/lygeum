package io.github.kaiso.lygeum.api;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(path = LygeumRestController.CONTEXT_PATH)
public abstract class LygeumRestController {

	public static final String CONTEXT_PATH = "/lygeum/api";

}
