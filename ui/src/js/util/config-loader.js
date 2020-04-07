(function () {
  window.__ENV = {};
  const configRequest = new XMLHttpRequest();
  configRequest.open('GET', './lygeum-configs.json', false);
  configRequest.onreadystatechange = function () {
    if (this.readyState === XMLHttpRequest.DONE && this.status !== 404) {
      extactConfig(configRequest);
    } else {
      configRequest.open('GET', './lygeum/api/system/web/config', false);
      configRequest.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE && this.status !== 404) {
          extactConfig(configRequest);
        }
      };
      configRequest.send();
    }
  };
  configRequest.send();
})();

function extactConfig(configRequest) {
  let config = null;
  config = JSON.parse(configRequest.responseText);
  if (config === undefined || config == null) {
    config = {};
  }
  if (!config.hasOwnProperty('server.url')) {
    config['server.url'] = `${window.location.protocol}//${window.location.host}/lygeum/`;
  } else {
    config['server.url'] = config['server.url'] + '/lygeum/';
  }

  for (const key of Object.keys(config)) {
    let value = config[key];
    // Convert if number
    if (!isNaN(value)) {
      value = Number(value);
    }
    // Convert if boolean value
    if (value === 'true' || value === 'false') {
      value = value === 'true';
    }
    // Add property to __ENV
    window.__ENV[key] = value;
  }
  ;
}

