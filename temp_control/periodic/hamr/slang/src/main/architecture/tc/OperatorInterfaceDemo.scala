package tc

object OperatorInterfaceDemo {

  def main(args: Array[String]): Unit = {
    art.Art.run(
      OperatorInterfaceArch.ad,
      OperatorInterfaceArch.services,
      art.scheduling.legacy.Legacy(OperatorInterfaceArch.ad.components)
    )
  }

}
