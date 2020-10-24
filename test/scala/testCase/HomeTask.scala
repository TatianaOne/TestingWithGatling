package testCase

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class HomeTask extends Simulation {

	val httpProtocol = http
		.baseURL("http://demo.nopcommerce.com")
    .disableFollowRedirect


	val headers_0 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_1 = Map("Pragma" -> "no-cache")

	val headers_2 = Map(
		"Accept" -> "application/json, text/javascript, */*; q=0.01",
		"X-Requested-With" -> "XMLHttpRequest")

	val headers_3 = Map(
		"Origin" -> "http://demo.nopcommerce.com",
		"X-Requested-With" -> "XMLHttpRequest")

    val uri1 = "http://demo.nopcommerce.com"

	val scn = scenario("HomeTask")
		// open homepage
		.exec(http("open home page")
			.get("/")
      .check(regex("<title>(.*)</title>").is("nopCommerce demo store"))
			.headers(headers_0))
			.pause(1, 30)

		// search for random product (with autocomplete requests)
		.exec(http("search for random product")
			.get("/catalog/searchtermautocomplete?term=book")
			.headers(headers_2))
		.pause(1)
		.exec(http("enter the word")
			.get("/search?q=book")
			.headers(headers_0)
    .check(status.is(200)))
		.pause(1, 30)

		// open random product page from search results
		.exec(http("open random product page")
			.get("/samsung-series-9-np900x4c-premium-ultrabook")
			.headers(headers_0)
      .check(status.is(200)))
		.pause(1, 30)

		// add to cart
		.exec(http("add to cart")
			.post(uri1 + "/samsung-series-9-np900x4c-premium-ultrabook")
      .formParam("quantity", "1")
			.headers(headers_3))
		.pause(1, 70)

		// go to shopping cart
		.exec(http("go to shopping cart")
			.get("/cart")
			.headers(headers_1))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
    .assertions(global.requestsPerSec.greaterThan(10))
}