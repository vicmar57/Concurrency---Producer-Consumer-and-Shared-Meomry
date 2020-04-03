import scala.collection.mutable
import scala.util.Random

object MultiProdCons extends App{

  class Consumer (id: Int, buff: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
        val random = new Random()

        while (true) {
          buff.synchronized {
            while (buff.isEmpty) {
              println(s"[consumer $id] Buffer empty, waiting for items...")
              buff.wait()
            }

            //notify others that are waiting on the buffer
            val item = buff.dequeue()
            println(s"[consumer $id] consumed " + item)

            buff.notify()
          }

          Thread.sleep(random.nextInt(250))
        }
      }
  }

  class Producer (id: Int, buff: mutable.Queue[Int], capacity: Int) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      var ind = 0

      while (true) {
        buff.synchronized {
          while (buff.length == capacity) {
            println(s"[producer $id] Buffer full, waiting...")
            buff.wait()
          }

          buff.enqueue(ind)
          println(s"[producer $id] produced " + ind)

          //notify others that are waiting on the buffer
          buff.notify()
          ind += 1
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }

  def multiProdConsLargeBuffer(prodNum: Int, consNum: Int): Unit ={
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    (1 to prodNum).foreach(ind => new Producer(ind, buffer, capacity).start())
    (1 to consNum).foreach(ind => new Consumer(ind, buffer).start())
  }

  multiProdConsLargeBuffer(6, 3)
}
