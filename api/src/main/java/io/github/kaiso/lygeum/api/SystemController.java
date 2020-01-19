package io.github.kaiso.lygeum.api;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private SystemController(LygeumServerInfo serverInfo) {
		super();
		this.serverInfo = serverInfo;
	}

	@RequestMapping(path = "/system/info", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<SystemInformation> getMe() {
		return ResponseEntity.ok(SystemInformation.builder().withJvm(serverInfo.getJvmInformation())
				.withVersion(serverInfo.getImplementationVersion()).withDatabase(serverInfo.getPersistenceInfo())
				.build());
	}

}
