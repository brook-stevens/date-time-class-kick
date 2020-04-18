package webservice.gatling.simulation
 
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

 
class WebServiceCallSimulation extends Simulation {
 val httpProtocol = http
   .baseUrl("http://localhost:8080")
//   .acceptEncodingHeader("gzip, deflate")

 val scn = scenario("LoadTest")
   .exec(http("request_date_time")
     .get("/date-time"))

 setUp(scn.inject(constantUsersPerSec(1200) during (1 minutes))).throttle(
  reachRps(1200) in (30 seconds),
  holdFor(1 minute)
 ).protocols(httpProtocol)
}
