package io.github.kaiso.lygeum.api;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.kaiso.lygeum.api.resources.SystemInformation;
import io.github.kaiso.lygeum.core.system.LygeumServerInfo;

@RestController
public class SystemController extends LygeumRestController {

	private LygeumServerInfo serverInfo;
	private ApplicationArguments applicationArguments;

	@Autowired
	private SystemController(LygeumServerInfo serverInfo, ApplicationArguments applicationArguments) {
		super();
		this.serverInfo = serverInfo;
		this.applicationArguments = applicationArguments;
	}

	@RequestMapping(path = "/system/info", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<SystemInformation> getMe() {
		return ResponseEntity.ok(SystemInformation.builder().withJvm(serverInfo.getJvmInformation())
				.withVersion(serverInfo.getImplementationVersion()).withDatabase(serverInfo.getPersistenceInfo())
				.build());
	}

	@RequestMapping(path = "/system/web/config", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Map<String, Object>> config() {
		return ResponseEntity.ok(Collections.singletonMap("server.url", getApplicationArgument("context-path", "")));
	}

	private Optional<String> getApplicationArgument(String key, String defaultValue) {
		if (applicationArguments.containsOption(key)) {
			return Optional.of(applicationArguments.getOptionValues(key).get(0));
		} else {
			return Optional.ofNullable(defaultValue);
		}
	}
}
