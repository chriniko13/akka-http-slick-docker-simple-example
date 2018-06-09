# akka-http-slick-docker-simple-example

Technologies used:
1) Akka Http
2) Slick
3) Spray json
4) Specs2
5) Scalatest


In order to create it as a docker and run it do the following:

===============================
            NOTES
===============================
1) sbt docker:publishLocal

2) docker run -dit -p 5000:5000 --name akka-http-sample akka-http-sample-with-docker:1.0

3) docker stop akka-http-sample
