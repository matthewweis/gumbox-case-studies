package tc

object iTempSensorClientDemo {

  def main(args: Array[String]): Unit = {
    art.Art.run(
      TempSensorClientArch.ad,
      TempSensorClientArch.services,
      art.scheduling.legacy.Legacy(TempSensorClientArch.ad.components)
    )
  }

}
