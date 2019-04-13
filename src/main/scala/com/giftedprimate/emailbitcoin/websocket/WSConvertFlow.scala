package com.giftedprimate.emailbitcoin.websocket

import akka.NotUsed
import akka.actor.{Actor, ActorContext, ActorRef, ActorSystem}
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl.{Flow, GraphDSL, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, FlowShape, OverflowStrategy}
import com.giftedprimate.emailbitcoin.entities.{
  GetActorFlow,
  JsonParseException,
  WSRequest
}
import io.circe.parser._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

trait WSConvertFlow extends Actor {
  val self: ActorRef
  val context: ActorContext
  def sender: ActorRef

  implicit val system: ActorSystem = context.system
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def convert(WSRequest: WSRequest): Any

  def receiveFlow: Receive = {
    case GetActorFlow() =>
      val s = sender
      s ! flow
    case JsonParseException(msg) => out ! s"json parse error: $msg"
  }

  def websocketReceive: Receive

  override def receive: Receive = receiveFlow orElse websocketReceive

  def msgParser(msg: String): Any = parse(msg) match {
    case Left(error) => JsonParseException(error.getMessage)
    case Right(json) =>
      json.as[WSRequest] match {
        case Left(error)      => JsonParseException(error.getMessage)
        case Right(wSRequest) => convert(wSRequest)
      }
  }

  val (out, publisher) = Source
    .actorRef[String](1000, OverflowStrategy.fail)
    .toMat(Sink.asPublisher(fanout = false))(Keep.both)
    .run()

  val flow: Flow[Message, TextMessage.Strict, NotUsed] =
    Flow.fromGraph(GraphDSL.create() { implicit b =>
      val textMsgFlow = b.add(
        Flow[Message]
          .mapAsync(1) {
            case tm: TextMessage =>
              tm.toStrict(FiniteDuration(3, "seconds")).map(_.text)
            case bm: BinaryMessage =>
              bm.dataStream.runWith(Sink.ignore)
              Future.failed(new Exception("yuck"))
          })

      val pubSrc = b.add(Source.fromPublisher(publisher).map(TextMessage(_)))

      textMsgFlow ~> Sink.foreach[String](self ! msgParser(_))
      FlowShape(textMsgFlow.in, pubSrc.out)
    })
}
