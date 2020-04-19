package webservice.gatling.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._


class DateTimeWebServiceLoadTestSimulation extends Simulation {
  val baseUrl = System.getenv("gatling-date-time-base-url")
  if (baseUrl == null) {
    throw new NullPointerException("System property gatling-date-time-base-url must be set")
  }
  val httpProtocol = http
    .baseUrl(baseUrl)

  val scn = scenario("LoadTest")
    .exec(http("request_date_time")
      .get("/date-time"))

  setUp(scn.inject(constantUsersPerSec(1200) during (1 minutes))).throttle(
    reachRps(1200) in (30 seconds),
    holdFor(1 minute)
  ).protocols(httpProtocol)
}
