package tc

object CoolingFanDemo {

  def main(args: Array[String]): Unit = {
    art.Art.run(
      CoolingFanClientArch.ad,
      CoolingFanClientArch.services,
      art.scheduling.legacy.Legacy(CoolingFanClientArch.ad.components)
    )
  }

}
