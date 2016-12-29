package spray_examples.simple

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import simple.FileData
import spray.http.{HttpEntity, MediaTypes}
import spray.httpx.marshalling.ToResponseMarshallable
import spray.routing.{HttpService, HttpServiceActor, Route, SimpleRoutingApp}
import spray_examples.plain_rest.danielasfregola.quiz.management.RestInterface
import spray_examples.plain_rest.danielasfregola.quiz.management.entities.Question
import spray_examples.plain_rest.danielasfregola.quiz.management.resources.QuestionResource
import spray_examples.plain_rest.danielasfregola.quiz.management.serializers.JsonSupport
import spray_examples.plain_rest.danielasfregola.quiz.management.services.QuestionService
//import spray.httpx.SprayJsonSupport._

import shapeless.list
import spray.http.{HttpEntity, MediaTypes}
import spray_examples.plain_rest.danielasfregola.quiz.management.entities.{Question, QuestionUpdate}
import spray_examples.plain_rest.danielasfregola.quiz.management.routing.MyHttpService
import spray_examples.plain_rest.danielasfregola.quiz.management.services.QuestionService
import spray.routing._
import spray_examples.simple.Page

import scala.util.Properties
import scala.concurrent.ExecutionContext

object JSON extends  JsonSupport

object Server extends SimpleRoutingApp {
//  import spray.httpx.SprayJsonSupport.sprayJsonUnmarshaller
  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher
  val questionService=new QuestionService
  def questionRoutes:Route= pathPrefix("questions") {
    import JSON._
    pathEnd {
      post {
        entity(as[Question]) { question =>
          println(question)
          complete {
            println(question)
            questionService.createQuestion(question)
          }
        }
      }
    } ~path(Segment) { id =>
      get {
        complete(questionService.getQuestion(id))
      } ~
        put {
          entity(as[QuestionUpdate]) { update =>
            complete(questionService.updateQuestion(id, update))
          }
        } ~
        delete {
          complete(204, questionService.deleteQuestion(id))
        }
    }
  }

  def routes: Route =
    get {
      pathSingleSlash {
        complete {
          ToResponseMarshallable.isMarshallable(HttpEntity(
            MediaTypes.`text/html`,

            Page.skeleton.render
          ))
        }
      } ~
      getFromDirectory("./client/target/scala-2.11")
    } ~
    post {
      path("ajax" / "list") {
        entity(as[String]) { e =>
          complete {
            upickle.default.write(list(e))
          }
        }
      }
    } ~
    post {
      path("ajax" / "joco") {
        entity(as[String]) { e =>
          complete {
            println(e)
            "OK"
          }
        }
      }
    } ~ questionRoutes


  def main(args: Array[String]): Unit = {
    implicit val materializer = ActorMaterializer()
    val port = Properties.envOrElse("PORT", "8080").toInt
    startServer("0.0.0.0", port = port)(routes)
  }
  def list(path: String) = {
    val (dir, last) = path.splitAt(path.lastIndexOf("/") + 1)
    val files =
      Option(new java.io.File("./" + dir).listFiles())
        .toSeq.flatten
    for{
      f <- files
      if f.getName.startsWith(last)
    } yield FileData(f.getName, f.length())
  }
}