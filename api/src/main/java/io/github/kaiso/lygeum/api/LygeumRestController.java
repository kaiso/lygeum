package io.github.kaiso.lygeum.api;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(path = LygeumRestController.CONTEXT_PATH)
public abstract class LygeumRestController {

	private static final Logger logger = LoggerFactory
			.getLogger(LygeumRestController.class.getPackage().getName() + "lygeum-api");
	public static final String CONTEXT_PATH = "/lygeum/api";

	@PostConstruct
	public void init() {
		logger.debug("Init REST controller {}", getClass().getSimpleName());
	}
}
