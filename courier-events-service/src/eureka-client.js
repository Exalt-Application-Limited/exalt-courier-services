const Eureka = require("eureka-js-client").Eureka;

const eurekaHost = process.env.EUREKA_HOST || "localhost";
const eurekaPort = process.env.EUREKA_PORT || 8761;
const hostName = process.env.HOSTNAME || "localhost";
const ipAddr = process.env.IP_ADDR || "127.0.0.1";
const serviceName = process.env.SERVICE_NAME || "courier-events-service";

exports.eurekaClient = new Eureka({
  instance: {
    app: serviceName,
    hostName: hostName,
    ipAddr: ipAddr,
    port: {
      $: process.env.PORT || 3000,
      "@enabled": true
    },
    vipAddress: serviceName,
    dataCenterInfo: {
      "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
      name: "MyOwn"
    },
    registerWithEureka: true,
    fetchRegistry: true
  },
  eureka: {
    host: eurekaHost,
    port: eurekaPort,
    servicePath: "/eureka/apps/"
  }
});
