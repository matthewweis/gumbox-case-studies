// #Sireum

package art

import org.sireum._
import art._
import tc._
import tc.JSON._

object Serde {

  def jsonSerializer(): Serializer = {
    return SerializerFunctionWrapper((d: DataContent) => JSON.from_artDataContent(d, false))
  }

  def jsonDeserializer(): Deserializer = {
    return DeserializerFunctionWrapper((s: String) => JSON.to_artDataContent(s).left)
  }

  def json(): Serde = {
    return SerdeWrapperCombiner(jsonSerializer(), jsonDeserializer())
  }

  def errorSerializer(): Serializer = {
    return SerializerFunctionWrapper((_: DataContent) => {
      eprintln("ERROR: SERIALIZER SHOULD NOT BE INVOKED BY LOCAL QUEUES")
      Os.exit(z"1")
      ""
    })
  }

  def errorDeserializer(): Deserializer = {
    return DeserializerFunctionWrapper((_: String) => {
      eprintln("ERROR: SERIALIZER SHOULD NOT BE INVOKED BY LOCAL QUEUES")
      Os.exit(z"1")
      Empty()
    })
  }

  def error(): Serde = {
    return SerdeWrapperCombiner(errorSerializer(), errorDeserializer())
  }

  /*
   * Serializer / deserializer APIs
   */

  @sig trait Serializer {
    def serialize(data: DataContent): String
  }

  @sig trait Deserializer {
    def deserialize(text: String): DataContent
  }

  @sig trait Serde extends Serializer with Deserializer

  /*
   * SERIALIZER/DESERIALIZER WRAPPERS WHICH CALL FUNCTIONS
   */

  @datatype class SerializerFunctionWrapper(fn: DataContent => String) extends Serializer {
    def serialize(data: DataContent): String = {
      return fn(data)
    }
  }

  @datatype class DeserializerFunctionWrapper(fn: String => DataContent) extends Deserializer {
    def deserialize(text: String): DataContent = {
      return fn(text)
    }
  }

  @datatype class SerdeWrapperCombiner(val s: Serializer, val d: Deserializer) extends Serde with Serializer with Deserializer {
    override def serialize(data: DataContent): String = {
      return s.serialize(data)
    }
    override def deserialize(text: String): DataContent = {
      return d.deserialize(text)
    }
    override def string: String = {
      return s"CombineSerde {\n  ${s.string},\n  ${d.string}\n}"
    }
  }

  /*
   * QUEUE WRAPPERS WHICH CALL SERIALIZERS
   */

  @datatype class DeserializingDequeue(delegate: Dequeue[String], deserializer: Deserializer) extends Dequeue[DataContent] {
    def drain(fn: DataContent => Unit): Unit = {
      // delegate.drain(fn.compose(deserializer.deserialize))
      val composed: String => Unit = (s: String) => fn(deserializer.deserialize(s))
      return delegate.drain(composed)
    }

    def drainWithLimit(fn: DataContent => Unit, limit: Z): Unit = {
      // delegate.drainWithLimit(fn.compose(deserializer.deserialize), limit)
      val composed: String => Unit = (s: String) => fn(deserializer.deserialize(s))
      return delegate.drainWithLimit(composed, limit)
    }

    // note: needed for dispatchStatus in ArtNative
    def isEmpty(): B = {
      return delegate.isEmpty()
    }

    override def peek(): Option[DataContent] = {
      return delegate.peek().map((text: String) => deserializer.deserialize(text))
    }
  }

  @datatype class SerializingDequeue(delegate: Dequeue[DataContent], serializer: Serializer) extends Dequeue[String] {
    def drain(fn: String => Unit): Unit = {
      // delegate.drain(fn.compose(serializer.serialize))
      val composed: DataContent => Unit = (d: DataContent) => fn(serializer.serialize(d))
      return delegate.drain(composed)
    }

    def drainWithLimit(fn: String => Unit, limit: Z): Unit = {
      // return delegate.drainWithLimit(fn.compose(serializer.serialize), limit)
      val composed: DataContent => Unit = (d: DataContent) => fn(serializer.serialize(d))
      return delegate.drainWithLimit(composed, limit)
    }

    // note: needed for dispatchStatus in ArtNative
    def isEmpty(): B = {
      return delegate.isEmpty()
    }

    override def peek(): Option[String] = {
      return delegate.peek().map((data: DataContent) => serializer.serialize(data))
    }
  }

  @datatype class SerializingEnqueue(delegate: Enqueue[String], serializer: Serializer) extends Enqueue[DataContent] {
    def offer(data: DataContent): B = {
      return delegate.offer(serializer.serialize(data))
    }
  }

  @datatype class DeserializingEnqueue(delegate: Enqueue[DataContent], deserializer: Deserializer) extends Enqueue[String] {
    def offer(text: String): B = {
      return delegate.offer(deserializer.deserialize(text))
    }
  }

}
