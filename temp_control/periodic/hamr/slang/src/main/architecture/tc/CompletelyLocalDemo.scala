package tc

object CompletelyLocalDemo {

  def main(args: Array[String]): Unit = {
    art.Art.run(
      CompletelyLocalArch.ad,
      CompletelyLocalArch.services,
      art.scheduling.legacy.Legacy(CompletelyLocalArch.ad.components)
    )
  }

}
