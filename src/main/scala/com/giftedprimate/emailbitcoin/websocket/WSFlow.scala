package com.giftedprimate.emailbitcoin.websocket

import akka.NotUsed
import akka.actor.{ActorContext, ActorRef, ActorSystem}
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl.{Flow, GraphDSL, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, FlowShape, OverflowStrategy}
import com.google.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

// todo -> pass in flow from actor
class WSFlow @Inject()(self: ActorRef, context: ActorContext) {
  implicit val system: ActorSystem = context.system
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val actorRef: ActorRef = self

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
              // consume the stream
              bm.dataStream.runWith(Sink.ignore)
              Future.failed(new Exception("yuck"))
          })

      val pubSrc = b.add(Source.fromPublisher(publisher).map(TextMessage(_)))

      textMsgFlow ~> Sink.foreach[String](self ! _)
      FlowShape(textMsgFlow.in, pubSrc.out)
    })
}
